package com.android.systemui.doze;

import android.app.AlarmManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.AmbientDisplayConfiguration;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.Preconditions;
import com.android.systemui.Dependency;
import com.android.systemui.dock.DockManager;
import com.android.systemui.doze.DozeHost;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.doze.DozeSensors;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.util.Assert;
import com.android.systemui.util.ProximitySensor;
import com.android.systemui.util.wakelock.WakeLock;
import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
/* loaded from: classes21.dex */
public class DozeTriggers implements DozeMachine.Part {
    private static final String PULSE_ACTION = "com.android.systemui.doze.pulse";
    private static final String TAG = "DozeTriggers";
    private final boolean mAllowPulseTriggers;
    private final AmbientDisplayConfiguration mConfig;
    private final Context mContext;
    private final DockManager mDockManager;
    private final DozeHost mDozeHost;
    private final DozeParameters mDozeParameters;
    private final DozeSensors mDozeSensors;
    private final Handler mHandler;
    private final DozeMachine mMachine;
    private long mNotificationPulseTime;
    private boolean mPulsePending;
    private final SensorManager mSensorManager;
    private final UiModeManager mUiModeManager;
    private final WakeLock mWakeLock;
    private static final boolean DEBUG = DozeService.DEBUG;
    private static boolean sWakeDisplaySensorState = true;
    private final TriggerReceiver mBroadcastReceiver = new TriggerReceiver();
    private final DockEventListener mDockEventListener = new DockEventListener();
    private final MetricsLogger mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
    private DozeHost.Callback mHostCallback = new DozeHost.Callback() { // from class: com.android.systemui.doze.DozeTriggers.2
        @Override // com.android.systemui.doze.DozeHost.Callback
        public void onNotificationAlerted(Runnable onPulseSuppressedListener) {
            DozeTriggers.this.onNotification(onPulseSuppressedListener);
        }

        @Override // com.android.systemui.doze.DozeHost.Callback
        public void onPowerSaveChanged(boolean active) {
            if (DozeTriggers.this.mDozeHost.isPowerSaveActive()) {
                DozeTriggers.this.mMachine.requestState(DozeMachine.State.DOZE);
            }
        }
    };

    public DozeTriggers(Context context, DozeMachine machine, DozeHost dozeHost, AlarmManager alarmManager, AmbientDisplayConfiguration config, DozeParameters dozeParameters, SensorManager sensorManager, Handler handler, WakeLock wakeLock, boolean allowPulseTriggers, DockManager dockManager) {
        this.mContext = context;
        this.mMachine = machine;
        this.mDozeHost = dozeHost;
        this.mConfig = config;
        this.mDozeParameters = dozeParameters;
        this.mSensorManager = sensorManager;
        this.mHandler = handler;
        this.mWakeLock = wakeLock;
        this.mAllowPulseTriggers = allowPulseTriggers;
        this.mDozeSensors = new DozeSensors(context, alarmManager, this.mSensorManager, dozeParameters, config, wakeLock, new DozeSensors.Callback() { // from class: com.android.systemui.doze.-$$Lambda$XuSeOmLZ56lHJGoIP26_sIwbcBM
            @Override // com.android.systemui.doze.DozeSensors.Callback
            public final void onSensorPulse(int i, float f, float f2, float[] fArr) {
                DozeTriggers.this.onSensor(i, f, f2, fArr);
            }
        }, new Consumer() { // from class: com.android.systemui.doze.-$$Lambda$DozeTriggers$ulqUMEXi8OgK7771oZ9BOr21BBk
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DozeTriggers.this.onProximityFar(((Boolean) obj).booleanValue());
            }
        }, dozeParameters.getPolicy());
        this.mUiModeManager = (UiModeManager) this.mContext.getSystemService(UiModeManager.class);
        this.mDockManager = dockManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNotification(Runnable onPulseSuppressedListener) {
        if (DozeMachine.DEBUG) {
            Log.d(TAG, "requestNotificationPulse");
        }
        if (!sWakeDisplaySensorState) {
            Log.d(TAG, "Wake display false. Pulse denied.");
            runIfNotNull(onPulseSuppressedListener);
            DozeLog.tracePulseDropped(this.mContext, "wakeDisplaySensor");
            return;
        }
        this.mNotificationPulseTime = SystemClock.elapsedRealtime();
        if (!this.mConfig.pulseOnNotificationEnabled(-2)) {
            runIfNotNull(onPulseSuppressedListener);
            DozeLog.tracePulseDropped(this.mContext, "pulseOnNotificationsDisabled");
            return;
        }
        requestPulse(1, false, onPulseSuppressedListener);
        DozeLog.traceNotificationPulse(this.mContext);
    }

    private static void runIfNotNull(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    private void proximityCheckThenCall(final IntConsumer callback, boolean alreadyPerformedProxCheck, final int reason) {
        Boolean cachedProxFar = this.mDozeSensors.isProximityCurrentlyFar();
        if (alreadyPerformedProxCheck) {
            callback.accept(3);
        } else if (cachedProxFar != null) {
            callback.accept(cachedProxFar.booleanValue() ? 2 : 1);
        } else {
            final long start = SystemClock.uptimeMillis();
            new ProximityCheck() { // from class: com.android.systemui.doze.DozeTriggers.1
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super();
                }

                @Override // com.android.systemui.doze.DozeTriggers.ProximityCheck
                public void onProximityResult(int result) {
                    long end = SystemClock.uptimeMillis();
                    DozeLog.traceProximityResult(DozeTriggers.this.mContext, result == 1, end - start, reason);
                    callback.accept(result);
                }
            }.check();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public void onSensor(final int pulseReason, final float screenX, final float screenY, float[] rawValues) {
        final boolean isDoubleTap = pulseReason == 4;
        final boolean isTap = pulseReason == 9;
        final boolean isPickup = pulseReason == 3;
        boolean isLongPress = pulseReason == 5;
        boolean isWakeDisplay = pulseReason == 7;
        boolean isWakeLockScreen = pulseReason == 8;
        boolean wakeEvent = (rawValues == null || rawValues.length <= 0 || rawValues[0] == 0.0f) ? false : true;
        DozeMachine.State state = null;
        if (isWakeDisplay) {
            if (!this.mMachine.isExecutingTransition()) {
                state = this.mMachine.getState();
            }
            onWakeScreen(wakeEvent, state);
        } else if (isLongPress) {
            requestPulse(pulseReason, true, null);
        } else if (!isWakeLockScreen) {
            proximityCheckThenCall(new IntConsumer() { // from class: com.android.systemui.doze.-$$Lambda$DozeTriggers$KXJDb4lGP0PpY23yKRXX1q0y7kA
                @Override // java.util.function.IntConsumer
                public final void accept(int i) {
                    DozeTriggers.this.lambda$onSensor$0$DozeTriggers(isDoubleTap, isTap, screenX, screenY, pulseReason, isPickup, i);
                }
            }, true, pulseReason);
        } else if (wakeEvent) {
            requestPulse(pulseReason, true, null);
        }
        if (isPickup) {
            long timeSinceNotification = SystemClock.elapsedRealtime() - this.mNotificationPulseTime;
            boolean withinVibrationThreshold = timeSinceNotification < ((long) this.mDozeParameters.getPickupVibrationThreshold());
            DozeLog.tracePickupWakeUp(this.mContext, withinVibrationThreshold);
        }
    }

    public /* synthetic */ void lambda$onSensor$0$DozeTriggers(boolean isDoubleTap, boolean isTap, float screenX, float screenY, int pulseReason, boolean isPickup, int result) {
        if (result == 1) {
            return;
        }
        if (isDoubleTap || isTap) {
            if (screenX != -1.0f && screenY != -1.0f) {
                this.mDozeHost.onSlpiTap(screenX, screenY);
            }
            gentleWakeUp(pulseReason);
        } else if (isPickup) {
            gentleWakeUp(pulseReason);
        } else {
            this.mDozeHost.extendPulse(pulseReason);
        }
    }

    private void gentleWakeUp(int reason) {
        this.mMetricsLogger.write(new LogMaker(223).setType(6).setSubtype(reason));
        if (this.mDozeParameters.getDisplayNeedsBlanking()) {
            this.mDozeHost.setAodDimmingScrim(1.0f);
        }
        this.mMachine.wakeUp();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onProximityFar(boolean far) {
        if (this.mMachine.isExecutingTransition()) {
            Log.w(TAG, "onProximityFar called during transition. Ignoring sensor response.");
            return;
        }
        boolean near = !far;
        DozeMachine.State state = this.mMachine.getState();
        boolean paused = state == DozeMachine.State.DOZE_AOD_PAUSED;
        boolean pausing = state == DozeMachine.State.DOZE_AOD_PAUSING;
        boolean aod = state == DozeMachine.State.DOZE_AOD;
        if (state == DozeMachine.State.DOZE_PULSING || state == DozeMachine.State.DOZE_PULSING_BRIGHT) {
            if (DEBUG) {
                Log.i(TAG, "Prox changed, ignore touch = " + near);
            }
            this.mDozeHost.onIgnoreTouchWhilePulsing(near);
        }
        if (far && (paused || pausing)) {
            if (DEBUG) {
                Log.i(TAG, "Prox FAR, unpausing AOD");
            }
            this.mMachine.requestState(DozeMachine.State.DOZE_AOD);
        } else if (near && aod) {
            if (DEBUG) {
                Log.i(TAG, "Prox NEAR, pausing AOD");
            }
            this.mMachine.requestState(DozeMachine.State.DOZE_AOD_PAUSING);
        }
    }

    private void onWakeScreen(boolean wake, final DozeMachine.State state) {
        DozeLog.traceWakeDisplay(wake);
        sWakeDisplaySensorState = wake;
        if (wake) {
            proximityCheckThenCall(new IntConsumer() { // from class: com.android.systemui.doze.-$$Lambda$DozeTriggers$vUNGpAqR9niD5s7OS6n7KlXtw9c
                @Override // java.util.function.IntConsumer
                public final void accept(int i) {
                    DozeTriggers.this.lambda$onWakeScreen$1$DozeTriggers(state, i);
                }
            }, true, 7);
            return;
        }
        boolean paused = state == DozeMachine.State.DOZE_AOD_PAUSED;
        boolean pausing = state == DozeMachine.State.DOZE_AOD_PAUSING;
        if (!pausing && !paused) {
            this.mMachine.requestState(DozeMachine.State.DOZE);
            this.mMetricsLogger.write(new LogMaker(223).setType(2).setSubtype(7));
        }
    }

    public /* synthetic */ void lambda$onWakeScreen$1$DozeTriggers(DozeMachine.State state, int result) {
        if (result != 1 && state == DozeMachine.State.DOZE) {
            this.mMachine.requestState(DozeMachine.State.DOZE_AOD);
            this.mMetricsLogger.write(new LogMaker(223).setType(1).setSubtype(7));
        }
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void transitionTo(DozeMachine.State oldState, DozeMachine.State newState) {
        switch (newState) {
            case INITIALIZED:
                this.mBroadcastReceiver.register(this.mContext);
                this.mDozeHost.addCallback(this.mHostCallback);
                DockManager dockManager = this.mDockManager;
                if (dockManager != null) {
                    dockManager.addListener(this.mDockEventListener);
                }
                this.mDozeSensors.requestTemporaryDisable();
                checkTriggersAtInit();
                return;
            case DOZE:
            case DOZE_AOD:
                this.mDozeSensors.setProxListening(newState != DozeMachine.State.DOZE);
                this.mDozeSensors.setListening(true);
                this.mDozeSensors.setPaused(false);
                if (newState == DozeMachine.State.DOZE_AOD && !sWakeDisplaySensorState) {
                    onWakeScreen(false, newState);
                    return;
                }
                return;
            case DOZE_AOD_PAUSED:
            case DOZE_AOD_PAUSING:
                this.mDozeSensors.setProxListening(true);
                this.mDozeSensors.setPaused(true);
                return;
            case DOZE_PULSING:
            case DOZE_PULSING_BRIGHT:
                this.mDozeSensors.setTouchscreenSensorsListening(false);
                this.mDozeSensors.setProxListening(true);
                this.mDozeSensors.setPaused(false);
                return;
            case DOZE_PULSE_DONE:
                this.mDozeSensors.requestTemporaryDisable();
                this.mDozeSensors.updateListening();
                return;
            case FINISH:
                this.mBroadcastReceiver.unregister(this.mContext);
                this.mDozeHost.removeCallback(this.mHostCallback);
                DockManager dockManager2 = this.mDockManager;
                if (dockManager2 != null) {
                    dockManager2.removeListener(this.mDockEventListener);
                }
                this.mDozeSensors.setListening(false);
                this.mDozeSensors.setProxListening(false);
                return;
            default:
                return;
        }
    }

    private void checkTriggersAtInit() {
        if (this.mUiModeManager.getCurrentModeType() == 3 || this.mDozeHost.isBlockingDoze() || !this.mDozeHost.isProvisioned()) {
            this.mMachine.requestState(DozeMachine.State.FINISH);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestPulse(final int reason, boolean performedProxCheck, final Runnable onPulseSuppressedListener) {
        Assert.isMainThread();
        this.mDozeHost.extendPulse(reason);
        if (this.mMachine.getState() == DozeMachine.State.DOZE_PULSING && reason == 8) {
            this.mMachine.requestState(DozeMachine.State.DOZE_PULSING_BRIGHT);
        } else if (this.mPulsePending || !this.mAllowPulseTriggers || !canPulse()) {
            if (this.mAllowPulseTriggers) {
                DozeLog.tracePulseDropped(this.mContext, this.mPulsePending, this.mMachine.getState(), this.mDozeHost.isPulsingBlocked());
            }
            runIfNotNull(onPulseSuppressedListener);
        } else {
            boolean z = true;
            this.mPulsePending = true;
            IntConsumer intConsumer = new IntConsumer() { // from class: com.android.systemui.doze.-$$Lambda$DozeTriggers$uVbqdwTqPYfR4HMOhbGgCZULuxk
                @Override // java.util.function.IntConsumer
                public final void accept(int i) {
                    DozeTriggers.this.lambda$requestPulse$2$DozeTriggers(onPulseSuppressedListener, reason, i);
                }
            };
            if (this.mDozeParameters.getProxCheckBeforePulse() && !performedProxCheck) {
                z = false;
            }
            proximityCheckThenCall(intConsumer, z, reason);
            this.mMetricsLogger.write(new LogMaker(223).setType(6).setSubtype(reason));
        }
    }

    public /* synthetic */ void lambda$requestPulse$2$DozeTriggers(Runnable onPulseSuppressedListener, int reason, int result) {
        if (result == 1) {
            DozeLog.tracePulseDropped(this.mContext, "inPocket");
            this.mPulsePending = false;
            runIfNotNull(onPulseSuppressedListener);
            return;
        }
        continuePulseRequest(reason);
    }

    private boolean canPulse() {
        return this.mMachine.getState() == DozeMachine.State.DOZE || this.mMachine.getState() == DozeMachine.State.DOZE_AOD;
    }

    private void continuePulseRequest(int reason) {
        this.mPulsePending = false;
        if (this.mDozeHost.isPulsingBlocked() || !canPulse()) {
            DozeLog.tracePulseDropped(this.mContext, this.mPulsePending, this.mMachine.getState(), this.mDozeHost.isPulsingBlocked());
        } else {
            this.mMachine.requestPulse(reason);
        }
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void dump(PrintWriter pw) {
        pw.print(" notificationPulseTime=");
        pw.println(Formatter.formatShortElapsedTime(this.mContext, this.mNotificationPulseTime));
        pw.print(" pulsePending=");
        pw.println(this.mPulsePending);
        pw.println("DozeSensors:");
        this.mDozeSensors.dump(pw);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public abstract class ProximityCheck implements SensorEventListener, Runnable {
        protected static final int RESULT_FAR = 2;
        protected static final int RESULT_NEAR = 1;
        protected static final int RESULT_NOT_CHECKED = 3;
        protected static final int RESULT_UNKNOWN = 0;
        private static final int TIMEOUT_DELAY_MS = 500;
        private boolean mFinished;
        private float mMaxRange;
        private boolean mRegistered;
        private float mSensorThreshold;
        private boolean mUsingBrightnessSensor;

        protected abstract void onProximityResult(int i);

        private ProximityCheck() {
        }

        public void check() {
            Preconditions.checkState((this.mFinished || this.mRegistered) ? false : true);
            Sensor sensor = ProximitySensor.findCustomProxSensor(DozeTriggers.this.mContext, DozeTriggers.this.mSensorManager);
            this.mUsingBrightnessSensor = sensor != null;
            if (!this.mUsingBrightnessSensor) {
                sensor = DozeTriggers.this.mSensorManager.getDefaultSensor(8);
            } else {
                this.mSensorThreshold = ProximitySensor.getBrightnessSensorThreshold(DozeTriggers.this.mContext.getResources());
            }
            if (sensor != null) {
                DozeTriggers.this.mDozeSensors.setDisableSensorsInterferingWithProximity(true);
                this.mMaxRange = sensor.getMaximumRange();
                DozeTriggers.this.mSensorManager.registerListener(this, sensor, 3, 0, DozeTriggers.this.mHandler);
                DozeTriggers.this.mHandler.postDelayed(this, 500L);
                DozeTriggers.this.mWakeLock.acquire(DozeTriggers.TAG);
                this.mRegistered = true;
                return;
            }
            if (DozeMachine.DEBUG) {
                Log.d(DozeTriggers.TAG, "ProxCheck: No sensor found");
            }
            finishWithResult(0);
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent event) {
            boolean isNear;
            if (event.values.length == 0) {
                if (DozeMachine.DEBUG) {
                    Log.d(DozeTriggers.TAG, "ProxCheck: Event has no values!");
                }
                finishWithResult(0);
                return;
            }
            if (DozeMachine.DEBUG) {
                Log.d(DozeTriggers.TAG, "ProxCheck: Event: value=" + event.values[0] + " max=" + this.mMaxRange);
            }
            if (this.mUsingBrightnessSensor) {
                isNear = event.values[0] <= this.mSensorThreshold;
            } else {
                isNear = event.values[0] < this.mMaxRange;
            }
            finishWithResult(isNear ? 1 : 2);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (DozeMachine.DEBUG) {
                Log.d(DozeTriggers.TAG, "ProxCheck: No event received before timeout");
            }
            finishWithResult(0);
        }

        private void finishWithResult(int result) {
            if (this.mFinished) {
                return;
            }
            boolean wasRegistered = this.mRegistered;
            if (this.mRegistered) {
                DozeTriggers.this.mHandler.removeCallbacks(this);
                DozeTriggers.this.mSensorManager.unregisterListener(this);
                DozeTriggers.this.mDozeSensors.setDisableSensorsInterferingWithProximity(false);
                this.mRegistered = false;
            }
            onProximityResult(result);
            if (wasRegistered) {
                DozeTriggers.this.mWakeLock.release(DozeTriggers.TAG);
            }
            this.mFinished = true;
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    /* loaded from: classes21.dex */
    private class TriggerReceiver extends BroadcastReceiver {
        private boolean mRegistered;

        private TriggerReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (DozeTriggers.PULSE_ACTION.equals(intent.getAction())) {
                if (DozeMachine.DEBUG) {
                    Log.d(DozeTriggers.TAG, "Received pulse intent");
                }
                DozeTriggers.this.requestPulse(0, false, null);
            }
            if (UiModeManager.ACTION_ENTER_CAR_MODE.equals(intent.getAction())) {
                DozeTriggers.this.mMachine.requestState(DozeMachine.State.FINISH);
            }
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                DozeTriggers.this.mDozeSensors.onUserSwitched();
            }
        }

        public void register(Context context) {
            if (this.mRegistered) {
                return;
            }
            IntentFilter filter = new IntentFilter(DozeTriggers.PULSE_ACTION);
            filter.addAction(UiModeManager.ACTION_ENTER_CAR_MODE);
            filter.addAction("android.intent.action.USER_SWITCHED");
            context.registerReceiver(this, filter);
            this.mRegistered = true;
        }

        public void unregister(Context context) {
            if (!this.mRegistered) {
                return;
            }
            context.unregisterReceiver(this);
            this.mRegistered = false;
        }
    }

    /* loaded from: classes21.dex */
    private class DockEventListener implements DockManager.DockEventListener {
        private DockEventListener() {
        }

        @Override // com.android.systemui.dock.DockManager.DockEventListener
        public void onEvent(int event) {
            if (DozeTriggers.DEBUG) {
                Log.d(DozeTriggers.TAG, "dock event = " + event);
            }
            if (event == 0) {
                DozeTriggers.this.mDozeSensors.ignoreTouchScreenSensorsSettingInterferingWithDocking(false);
            } else if (event == 1 || event == 2) {
                DozeTriggers.this.mDozeSensors.ignoreTouchScreenSensorsSettingInterferingWithDocking(true);
            }
        }
    }
}
