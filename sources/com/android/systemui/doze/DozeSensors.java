package com.android.systemui.doze;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.hardware.display.AmbientDisplayConfiguration;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.doze.DozeSensors;
import com.android.systemui.plugins.SensorManagerPlugin;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.util.AlarmTimeout;
import com.android.systemui.util.AsyncSensorManager;
import com.android.systemui.util.ProximitySensor;
import com.android.systemui.util.wakelock.WakeLock;
import com.badlogic.gdx.net.HttpStatus;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class DozeSensors {
    private static final boolean DEBUG = DozeService.DEBUG;
    private static final String TAG = "DozeSensors";
    private final AlarmManager mAlarmManager;
    private final Callback mCallback;
    private final AmbientDisplayConfiguration mConfig;
    private final Context mContext;
    private long mDebounceFrom;
    private final DozeParameters mDozeParameters;
    private boolean mListening;
    private boolean mPaused;
    private final TriggerSensor mPickupSensor;
    private final Consumer<Boolean> mProxCallback;
    private final ProxSensor mProxSensor;
    private final ContentResolver mResolver;
    private final SensorManager mSensorManager;
    @VisibleForTesting
    protected TriggerSensor[] mSensors;
    private boolean mSettingRegistered;
    private final WakeLock mWakeLock;
    private final Handler mHandler = new Handler();
    private final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler) { // from class: com.android.systemui.doze.DozeSensors.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri, int userId) {
            TriggerSensor[] triggerSensorArr;
            if (userId != ActivityManager.getCurrentUser()) {
                return;
            }
            for (TriggerSensor s : DozeSensors.this.mSensors) {
                s.updateListening();
            }
        }
    };

    /* loaded from: classes21.dex */
    public interface Callback {
        void onSensorPulse(int i, float f, float f2, float[] fArr);
    }

    public DozeSensors(Context context, AlarmManager alarmManager, SensorManager sensorManager, DozeParameters dozeParameters, AmbientDisplayConfiguration config, WakeLock wakeLock, Callback callback, Consumer<Boolean> proxCallback, AlwaysOnDisplayPolicy policy) {
        this.mContext = context;
        this.mAlarmManager = alarmManager;
        this.mSensorManager = sensorManager;
        this.mDozeParameters = dozeParameters;
        this.mConfig = config;
        this.mWakeLock = wakeLock;
        this.mProxCallback = proxCallback;
        this.mResolver = this.mContext.getContentResolver();
        boolean alwaysOn = this.mConfig.alwaysOnEnabled(-2);
        TriggerSensor[] triggerSensorArr = new TriggerSensor[7];
        triggerSensorArr[0] = new TriggerSensor(this, this.mSensorManager.getDefaultSensor(17), null, dozeParameters.getPulseOnSigMotion(), 2, false, false);
        TriggerSensor triggerSensor = new TriggerSensor(this.mSensorManager.getDefaultSensor(25), "doze_pulse_on_pick_up", true, config.dozePickupSensorAvailable(), 3, false, false, false);
        this.mPickupSensor = triggerSensor;
        triggerSensorArr[1] = triggerSensor;
        triggerSensorArr[2] = new TriggerSensor(this, findSensorWithType(config.doubleTapSensorType()), "doze_pulse_on_double_tap", true, 4, dozeParameters.doubleTapReportsTouchCoordinates(), true);
        triggerSensorArr[3] = new TriggerSensor(this, findSensorWithType(config.tapSensorType()), "doze_tap_gesture", true, 9, false, true);
        triggerSensorArr[4] = new TriggerSensor(this, findSensorWithType(config.longPressSensorType()), "doze_pulse_on_long_press", false, true, 5, true, true);
        triggerSensorArr[5] = new PluginSensor(this, new SensorManagerPlugin.Sensor(2), "doze_wake_display_gesture", this.mConfig.wakeScreenGestureAvailable() && alwaysOn, 7, false, false);
        triggerSensorArr[6] = new PluginSensor(new SensorManagerPlugin.Sensor(1), "doze_wake_screen_gesture", this.mConfig.wakeScreenGestureAvailable(), 8, false, false, this.mConfig.getWakeLockScreenDebounce());
        this.mSensors = triggerSensorArr;
        this.mProxSensor = new ProxSensor(policy);
        this.mCallback = callback;
    }

    public void requestTemporaryDisable() {
        this.mDebounceFrom = SystemClock.uptimeMillis();
    }

    private Sensor findSensorWithType(String type) {
        return findSensorWithType(this.mSensorManager, type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Sensor findSensorWithType(SensorManager sensorManager, String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        List<Sensor> sensorList = sensorManager.getSensorList(-1);
        for (Sensor s : sensorList) {
            if (type.equals(s.getStringType())) {
                return s;
            }
        }
        return null;
    }

    public void setListening(boolean listen) {
        if (this.mListening == listen) {
            return;
        }
        this.mListening = listen;
        updateListening();
    }

    public void setPaused(boolean paused) {
        if (this.mPaused == paused) {
            return;
        }
        this.mPaused = paused;
        updateListening();
    }

    public void updateListening() {
        TriggerSensor[] triggerSensorArr;
        TriggerSensor[] triggerSensorArr2;
        boolean anyListening = false;
        for (TriggerSensor s : this.mSensors) {
            s.setListening(this.mListening);
            if (this.mListening) {
                anyListening = true;
            }
        }
        if (!anyListening) {
            this.mResolver.unregisterContentObserver(this.mSettingsObserver);
        } else if (!this.mSettingRegistered) {
            for (TriggerSensor s2 : this.mSensors) {
                s2.registerSettingsObserver(this.mSettingsObserver);
            }
        }
        this.mSettingRegistered = anyListening;
    }

    public void setTouchscreenSensorsListening(boolean listening) {
        TriggerSensor[] triggerSensorArr;
        for (TriggerSensor sensor : this.mSensors) {
            if (sensor.mRequiresTouchscreen) {
                sensor.setListening(listening);
            }
        }
    }

    public void onUserSwitched() {
        TriggerSensor[] triggerSensorArr;
        for (TriggerSensor s : this.mSensors) {
            s.updateListening();
        }
    }

    public void setProxListening(boolean listen) {
        this.mProxSensor.setRequested(listen);
    }

    public void setDisableSensorsInterferingWithProximity(boolean disable) {
        this.mPickupSensor.setDisabled(disable);
    }

    public void ignoreTouchScreenSensorsSettingInterferingWithDocking(boolean ignore) {
        TriggerSensor[] triggerSensorArr;
        for (TriggerSensor sensor : this.mSensors) {
            if (sensor.mRequiresTouchscreen) {
                sensor.ignoreSetting(ignore);
            }
        }
    }

    public void dump(PrintWriter pw) {
        TriggerSensor[] triggerSensorArr;
        for (TriggerSensor s : this.mSensors) {
            pw.print("  Sensor: ");
            pw.println(s.toString());
        }
        pw.print("  ProxSensor: ");
        pw.println(this.mProxSensor.toString());
    }

    public Boolean isProximityCurrentlyFar() {
        return this.mProxSensor.mCurrentlyFar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class ProxSensor implements SensorEventListener {
        final AlarmTimeout mCooldownTimer;
        Boolean mCurrentlyFar;
        long mLastNear;
        final AlwaysOnDisplayPolicy mPolicy;
        boolean mRegistered;
        boolean mRequested;
        final Sensor mSensor;
        private final float mSensorThreshold;
        final boolean mUsingBrightnessSensor;

        public ProxSensor(AlwaysOnDisplayPolicy policy) {
            this.mPolicy = policy;
            this.mCooldownTimer = new AlarmTimeout(DozeSensors.this.mAlarmManager, new AlarmManager.OnAlarmListener() { // from class: com.android.systemui.doze.-$$Lambda$DozeSensors$ProxSensor$1rrJyrK-R8bANwbetqs61eKIcvs
                @Override // android.app.AlarmManager.OnAlarmListener
                public final void onAlarm() {
                    DozeSensors.ProxSensor.this.updateRegistered();
                }
            }, "prox_cooldown", DozeSensors.this.mHandler);
            Sensor sensor = ProximitySensor.findCustomProxSensor(DozeSensors.this.mContext, DozeSensors.this.mSensorManager);
            this.mUsingBrightnessSensor = sensor != null;
            if (!this.mUsingBrightnessSensor) {
                sensor = DozeSensors.this.mSensorManager.getDefaultSensor(8);
                this.mSensorThreshold = sensor == null ? 0.0f : sensor.getMaximumRange();
            } else {
                this.mSensorThreshold = ProximitySensor.getBrightnessSensorThreshold(DozeSensors.this.mContext.getResources());
            }
            this.mSensor = sensor;
        }

        void setRequested(boolean requested) {
            if (this.mRequested == requested) {
                DozeSensors.this.mHandler.post(new Runnable() { // from class: com.android.systemui.doze.-$$Lambda$DozeSensors$ProxSensor$ocSoA7n0sI8mkM1nacSopw2_2Oc
                    @Override // java.lang.Runnable
                    public final void run() {
                        DozeSensors.ProxSensor.this.lambda$setRequested$0$DozeSensors$ProxSensor();
                    }
                });
                return;
            }
            this.mRequested = requested;
            updateRegistered();
        }

        public /* synthetic */ void lambda$setRequested$0$DozeSensors$ProxSensor() {
            if (this.mCurrentlyFar != null) {
                DozeSensors.this.mProxCallback.accept(this.mCurrentlyFar);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateRegistered() {
            setRegistered(this.mRequested && !this.mCooldownTimer.isScheduled());
        }

        private void setRegistered(boolean register) {
            if (this.mRegistered == register) {
                return;
            }
            if (!register) {
                DozeSensors.this.mSensorManager.unregisterListener(this);
                this.mRegistered = false;
                this.mCurrentlyFar = null;
                return;
            }
            this.mRegistered = DozeSensors.this.mSensorManager.registerListener(this, this.mSensor, 3, DozeSensors.this.mHandler);
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent event) {
            if (DozeSensors.DEBUG) {
                Log.d(DozeSensors.TAG, "onSensorChanged " + event);
            }
            if (this.mUsingBrightnessSensor) {
                this.mCurrentlyFar = Boolean.valueOf(event.values[0] > this.mSensorThreshold);
            } else {
                this.mCurrentlyFar = Boolean.valueOf(event.values[0] >= this.mSensorThreshold);
            }
            DozeSensors.this.mProxCallback.accept(this.mCurrentlyFar);
            long now = SystemClock.elapsedRealtime();
            Boolean bool = this.mCurrentlyFar;
            if (bool != null) {
                if (!bool.booleanValue()) {
                    this.mLastNear = now;
                } else if (this.mCurrentlyFar.booleanValue() && now - this.mLastNear < this.mPolicy.proxCooldownTriggerMs) {
                    this.mCooldownTimer.schedule(this.mPolicy.proxCooldownPeriodMs, 1);
                    updateRegistered();
                }
            }
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public String toString() {
            return String.format("{registered=%s, requested=%s, coolingDown=%s, currentlyFar=%s, sensor=%s}", Boolean.valueOf(this.mRegistered), Boolean.valueOf(this.mRequested), Boolean.valueOf(this.mCooldownTimer.isScheduled()), this.mCurrentlyFar, this.mSensor);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public class TriggerSensor extends TriggerEventListener {
        final boolean mConfigured;
        protected boolean mDisabled;
        protected boolean mIgnoresSetting;
        final int mPulseReason;
        protected boolean mRegistered;
        private final boolean mReportsTouchCoordinates;
        protected boolean mRequested;
        private final boolean mRequiresTouchscreen;
        final Sensor mSensor;
        private final String mSetting;
        private final boolean mSettingDefault;

        public TriggerSensor(DozeSensors this$0, Sensor sensor, String setting, boolean configured, int pulseReason, boolean reportsTouchCoordinates, boolean requiresTouchscreen) {
            this(this$0, sensor, setting, true, configured, pulseReason, reportsTouchCoordinates, requiresTouchscreen);
        }

        public TriggerSensor(DozeSensors this$0, Sensor sensor, String setting, boolean settingDef, boolean configured, int pulseReason, boolean reportsTouchCoordinates, boolean requiresTouchscreen) {
            this(sensor, setting, settingDef, configured, pulseReason, reportsTouchCoordinates, requiresTouchscreen, false);
        }

        private TriggerSensor(Sensor sensor, String setting, boolean settingDef, boolean configured, int pulseReason, boolean reportsTouchCoordinates, boolean requiresTouchscreen, boolean ignoresSetting) {
            this.mSensor = sensor;
            this.mSetting = setting;
            this.mSettingDefault = settingDef;
            this.mConfigured = configured;
            this.mPulseReason = pulseReason;
            this.mReportsTouchCoordinates = reportsTouchCoordinates;
            this.mRequiresTouchscreen = requiresTouchscreen;
            this.mIgnoresSetting = ignoresSetting;
        }

        public void setListening(boolean listen) {
            if (this.mRequested == listen) {
                return;
            }
            this.mRequested = listen;
            updateListening();
        }

        public void setDisabled(boolean disabled) {
            if (this.mDisabled == disabled) {
                return;
            }
            this.mDisabled = disabled;
            updateListening();
        }

        public void ignoreSetting(boolean ignored) {
            if (this.mIgnoresSetting == ignored) {
                return;
            }
            this.mIgnoresSetting = ignored;
            updateListening();
        }

        public void updateListening() {
            if (!this.mConfigured || this.mSensor == null) {
                return;
            }
            if (this.mRequested && !this.mDisabled && ((enabledBySetting() || this.mIgnoresSetting) && !this.mRegistered)) {
                this.mRegistered = DozeSensors.this.mSensorManager.requestTriggerSensor(this, this.mSensor);
                if (DozeSensors.DEBUG) {
                    Log.d(DozeSensors.TAG, "requestTriggerSensor " + this.mRegistered);
                }
            } else if (this.mRegistered) {
                boolean rt = DozeSensors.this.mSensorManager.cancelTriggerSensor(this, this.mSensor);
                if (DozeSensors.DEBUG) {
                    Log.d(DozeSensors.TAG, "cancelTriggerSensor " + rt);
                }
                this.mRegistered = false;
            }
        }

        protected boolean enabledBySetting() {
            if (DozeSensors.this.mConfig.enabled(-2)) {
                return TextUtils.isEmpty(this.mSetting) || Settings.Secure.getIntForUser(DozeSensors.this.mResolver, this.mSetting, this.mSettingDefault ? 1 : 0, -2) != 0;
            }
            return false;
        }

        public String toString() {
            return "{mRegistered=" + this.mRegistered + ", mRequested=" + this.mRequested + ", mDisabled=" + this.mDisabled + ", mConfigured=" + this.mConfigured + ", mIgnoresSetting=" + this.mIgnoresSetting + ", mSensor=" + this.mSensor + "}";
        }

        @Override // android.hardware.TriggerEventListener
        public void onTrigger(final TriggerEvent event) {
            DozeLog.traceSensor(DozeSensors.this.mContext, this.mPulseReason);
            DozeSensors.this.mHandler.post(DozeSensors.this.mWakeLock.wrap(new Runnable() { // from class: com.android.systemui.doze.-$$Lambda$DozeSensors$TriggerSensor$O2XJN2HKJ96bSF_1qNx6jPK-eFk
                @Override // java.lang.Runnable
                public final void run() {
                    DozeSensors.TriggerSensor.this.lambda$onTrigger$0$DozeSensors$TriggerSensor(event);
                }
            }));
        }

        public /* synthetic */ void lambda$onTrigger$0$DozeSensors$TriggerSensor(TriggerEvent event) {
            if (DozeSensors.DEBUG) {
                Log.d(DozeSensors.TAG, "onTrigger: " + triggerEventToString(event));
            }
            Sensor sensor = this.mSensor;
            if (sensor != null && sensor.getType() == 25) {
                int subType = (int) event.values[0];
                MetricsLogger.action(DozeSensors.this.mContext, (int) HttpStatus.SC_LENGTH_REQUIRED, subType);
            }
            this.mRegistered = false;
            float screenX = -1.0f;
            float screenY = -1.0f;
            if (this.mReportsTouchCoordinates && event.values.length >= 2) {
                screenX = event.values[0];
                screenY = event.values[1];
            }
            DozeSensors.this.mCallback.onSensorPulse(this.mPulseReason, screenX, screenY, event.values);
            if (!this.mRegistered) {
                updateListening();
            }
        }

        public void registerSettingsObserver(ContentObserver settingsObserver) {
            if (this.mConfigured && !TextUtils.isEmpty(this.mSetting)) {
                DozeSensors.this.mResolver.registerContentObserver(Settings.Secure.getUriFor(this.mSetting), false, DozeSensors.this.mSettingsObserver, -1);
            }
        }

        protected String triggerEventToString(TriggerEvent event) {
            if (event == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder("SensorEvent[");
            sb.append(event.timestamp);
            sb.append(',');
            StringBuilder sb2 = sb.append(event.sensor.getName());
            if (event.values != null) {
                for (int i = 0; i < event.values.length; i++) {
                    sb2.append(',');
                    sb2.append(event.values[i]);
                }
            }
            sb2.append(']');
            return sb2.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public class PluginSensor extends TriggerSensor implements SensorManagerPlugin.SensorEventListener {
        private long mDebounce;
        final SensorManagerPlugin.Sensor mPluginSensor;

        PluginSensor(DozeSensors this$0, SensorManagerPlugin.Sensor sensor, String setting, boolean configured, int pulseReason, boolean reportsTouchCoordinates, boolean requiresTouchscreen) {
            this(sensor, setting, configured, pulseReason, reportsTouchCoordinates, requiresTouchscreen, 0L);
        }

        PluginSensor(SensorManagerPlugin.Sensor sensor, String setting, boolean configured, int pulseReason, boolean reportsTouchCoordinates, boolean requiresTouchscreen, long debounce) {
            super(DozeSensors.this, null, setting, configured, pulseReason, reportsTouchCoordinates, requiresTouchscreen);
            this.mPluginSensor = sensor;
            this.mDebounce = debounce;
        }

        @Override // com.android.systemui.doze.DozeSensors.TriggerSensor
        public void updateListening() {
            if (this.mConfigured) {
                AsyncSensorManager asyncSensorManager = (AsyncSensorManager) DozeSensors.this.mSensorManager;
                if (this.mRequested && !this.mDisabled && ((enabledBySetting() || this.mIgnoresSetting) && !this.mRegistered)) {
                    asyncSensorManager.registerPluginListener(this.mPluginSensor, this);
                    this.mRegistered = true;
                    if (DozeSensors.DEBUG) {
                        Log.d(DozeSensors.TAG, "registerPluginListener");
                    }
                } else if (this.mRegistered) {
                    asyncSensorManager.unregisterPluginListener(this.mPluginSensor, this);
                    this.mRegistered = false;
                    if (DozeSensors.DEBUG) {
                        Log.d(DozeSensors.TAG, "unregisterPluginListener");
                    }
                }
            }
        }

        @Override // com.android.systemui.doze.DozeSensors.TriggerSensor
        public String toString() {
            return "{mRegistered=" + this.mRegistered + ", mRequested=" + this.mRequested + ", mDisabled=" + this.mDisabled + ", mConfigured=" + this.mConfigured + ", mIgnoresSetting=" + this.mIgnoresSetting + ", mSensor=" + this.mPluginSensor + "}";
        }

        private String triggerEventToString(SensorManagerPlugin.SensorEvent event) {
            if (event == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder("PluginTriggerEvent[");
            sb.append(event.getSensor());
            sb.append(',');
            StringBuilder sb2 = sb.append(event.getVendorType());
            if (event.getValues() != null) {
                for (int i = 0; i < event.getValues().length; i++) {
                    sb2.append(',');
                    sb2.append(event.getValues()[i]);
                }
            }
            sb2.append(']');
            return sb2.toString();
        }

        @Override // com.android.systemui.plugins.SensorManagerPlugin.SensorEventListener
        public void onSensorChanged(final SensorManagerPlugin.SensorEvent event) {
            DozeLog.traceSensor(DozeSensors.this.mContext, this.mPulseReason);
            DozeSensors.this.mHandler.post(DozeSensors.this.mWakeLock.wrap(new Runnable() { // from class: com.android.systemui.doze.-$$Lambda$DozeSensors$PluginSensor$EFDqlQhDL6RwEmmtbTd8M88V_8Y
                @Override // java.lang.Runnable
                public final void run() {
                    DozeSensors.PluginSensor.this.lambda$onSensorChanged$0$DozeSensors$PluginSensor(event);
                }
            }));
        }

        public /* synthetic */ void lambda$onSensorChanged$0$DozeSensors$PluginSensor(SensorManagerPlugin.SensorEvent event) {
            long now = SystemClock.uptimeMillis();
            if (now >= DozeSensors.this.mDebounceFrom + this.mDebounce) {
                if (DozeSensors.DEBUG) {
                    Log.d(DozeSensors.TAG, "onSensorEvent: " + triggerEventToString(event));
                }
                DozeSensors.this.mCallback.onSensorPulse(this.mPulseReason, -1.0f, -1.0f, event.getValues());
                return;
            }
            Log.d(DozeSensors.TAG, "onSensorEvent dropped: " + triggerEventToString(event));
        }
    }
}
