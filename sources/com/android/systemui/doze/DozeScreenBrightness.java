package com.android.systemui.doze;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.doze.DozeMachine;
/* loaded from: classes21.dex */
public class DozeScreenBrightness extends BroadcastReceiver implements DozeMachine.Part, SensorEventListener {
    protected static final String ACTION_AOD_BRIGHTNESS = "com.android.systemui.doze.AOD_BRIGHTNESS";
    protected static final String BRIGHTNESS_BUCKET = "brightness_bucket";
    private static final boolean DEBUG_AOD_BRIGHTNESS = SystemProperties.getBoolean("debug.aod_brightness", false);
    private final Context mContext;
    private int mDebugBrightnessBucket;
    private final boolean mDebuggable;
    private int mDefaultDozeBrightness;
    private final DozeHost mDozeHost;
    private final DozeMachine.Service mDozeService;
    private final Handler mHandler;
    private int mLastSensorValue;
    private final Sensor mLightSensor;
    private boolean mPaused;
    private boolean mRegistered;
    private boolean mScreenOff;
    private final SensorManager mSensorManager;
    private final int[] mSensorToBrightness;
    private final int[] mSensorToScrimOpacity;

    @VisibleForTesting
    public DozeScreenBrightness(Context context, DozeMachine.Service service, SensorManager sensorManager, Sensor lightSensor, DozeHost host, Handler handler, int defaultDozeBrightness, int[] sensorToBrightness, int[] sensorToScrimOpacity, boolean debuggable) {
        this.mPaused = false;
        this.mScreenOff = false;
        this.mLastSensorValue = -1;
        this.mDebugBrightnessBucket = -1;
        this.mContext = context;
        this.mDozeService = service;
        this.mSensorManager = sensorManager;
        this.mLightSensor = lightSensor;
        this.mDozeHost = host;
        this.mHandler = handler;
        this.mDebuggable = debuggable;
        this.mDefaultDozeBrightness = defaultDozeBrightness;
        this.mSensorToBrightness = sensorToBrightness;
        this.mSensorToScrimOpacity = sensorToScrimOpacity;
        if (this.mDebuggable) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_AOD_BRIGHTNESS);
            this.mContext.registerReceiverAsUser(this, UserHandle.ALL, filter, null, handler);
        }
    }

    public DozeScreenBrightness(Context context, DozeMachine.Service service, SensorManager sensorManager, Sensor lightSensor, DozeHost host, Handler handler, AlwaysOnDisplayPolicy policy) {
        this(context, service, sensorManager, lightSensor, host, handler, context.getResources().getInteger(17694882), policy.screenBrightnessArray, policy.dimmingScrimArray, DEBUG_AOD_BRIGHTNESS);
    }

    /* renamed from: com.android.systemui.doze.DozeScreenBrightness$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State = new int[DozeMachine.State.values().length];

        static {
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.INITIALIZED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.DOZE_AOD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.DOZE_REQUEST_PULSE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.DOZE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.FINISH.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void transitionTo(DozeMachine.State oldState, DozeMachine.State newState) {
        int i = AnonymousClass1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[newState.ordinal()];
        if (i == 1) {
            resetBrightnessToDefault();
        } else if (i == 2 || i == 3) {
            setLightSensorEnabled(true);
        } else if (i == 4) {
            setLightSensorEnabled(false);
            resetBrightnessToDefault();
        } else if (i == 5) {
            onDestroy();
        }
        if (newState != DozeMachine.State.FINISH) {
            setScreenOff(newState == DozeMachine.State.DOZE);
            setPaused(newState == DozeMachine.State.DOZE_AOD_PAUSED);
        }
    }

    private void onDestroy() {
        setLightSensorEnabled(false);
        if (this.mDebuggable) {
            this.mContext.unregisterReceiver(this);
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent event) {
        Trace.beginSection("DozeScreenBrightness.onSensorChanged" + event.values[0]);
        try {
            if (this.mRegistered) {
                this.mLastSensorValue = (int) event.values[0];
                updateBrightnessAndReady(false);
            }
        } finally {
            Trace.endSection();
        }
    }

    private void updateBrightnessAndReady(boolean force) {
        if (force || this.mRegistered || this.mDebugBrightnessBucket != -1) {
            int i = this.mDebugBrightnessBucket;
            if (i == -1) {
                i = this.mLastSensorValue;
            }
            int sensorValue = i;
            int brightness = computeBrightness(sensorValue);
            boolean brightnessReady = brightness > 0;
            if (brightnessReady) {
                this.mDozeService.setDozeScreenBrightness(clampToUserSetting(brightness));
            }
            int scrimOpacity = -1;
            if (this.mLightSensor == null) {
                scrimOpacity = 0;
            } else if (brightnessReady) {
                scrimOpacity = computeScrimOpacity(sensorValue);
            }
            if (scrimOpacity >= 0) {
                this.mDozeHost.setAodDimmingScrim(scrimOpacity / 255.0f);
            }
        }
    }

    private int computeScrimOpacity(int sensorValue) {
        if (sensorValue >= 0) {
            int[] iArr = this.mSensorToScrimOpacity;
            if (sensorValue >= iArr.length) {
                return -1;
            }
            return iArr[sensorValue];
        }
        return -1;
    }

    private int computeBrightness(int sensorValue) {
        if (sensorValue >= 0) {
            int[] iArr = this.mSensorToBrightness;
            if (sensorValue >= iArr.length) {
                return -1;
            }
            return iArr[sensorValue];
        }
        return -1;
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void resetBrightnessToDefault() {
        this.mDozeService.setDozeScreenBrightness(clampToUserSetting(this.mDefaultDozeBrightness));
        this.mDozeHost.setAodDimmingScrim(0.0f);
    }

    private int clampToUserSetting(int brightness) {
        int userSetting = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness", Integer.MAX_VALUE, -2);
        return Math.min(brightness, userSetting);
    }

    private void setLightSensorEnabled(boolean enabled) {
        Sensor sensor;
        if (enabled && !this.mRegistered && (sensor = this.mLightSensor) != null) {
            this.mRegistered = this.mSensorManager.registerListener(this, sensor, 3, this.mHandler);
            this.mLastSensorValue = -1;
        } else if (!enabled && this.mRegistered) {
            this.mSensorManager.unregisterListener(this);
            this.mRegistered = false;
            this.mLastSensorValue = -1;
        }
    }

    private void setPaused(boolean paused) {
        if (this.mPaused != paused) {
            this.mPaused = paused;
            updateBrightnessAndReady(false);
        }
    }

    private void setScreenOff(boolean screenOff) {
        if (this.mScreenOff != screenOff) {
            this.mScreenOff = screenOff;
            updateBrightnessAndReady(true);
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        this.mDebugBrightnessBucket = intent.getIntExtra(BRIGHTNESS_BUCKET, -1);
        updateBrightnessAndReady(false);
    }
}
