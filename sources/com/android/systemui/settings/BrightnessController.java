package com.android.systemui.settings;

import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.display.BrightnessUtils;
import com.android.systemui.Dependency;
import com.android.systemui.settings.ToggleSlider;
import com.xiaopeng.systemui.controller.brightness.BrightnessSettings;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes21.dex */
public class BrightnessController implements ToggleSlider.Listener {
    private static final int MSG_ATTACH_LISTENER = 3;
    private static final int MSG_DETACH_LISTENER = 4;
    private static final int MSG_SET_CHECKED = 2;
    private static final int MSG_UPDATE_SLIDER = 1;
    private static final int MSG_VR_MODE_CHANGED = 5;
    private static final int SLIDER_ANIMATION_DURATION = 3000;
    private static final String TAG = "StatusBar.BrightnessController";
    private volatile boolean mAutomatic;
    private final boolean mAutomaticAvailable;
    private final Handler mBackgroundHandler;
    private final BrightnessObserver mBrightnessObserver;
    private final Context mContext;
    private final ToggleSlider mControl;
    private boolean mControlValueInitialized;
    private final int mDefaultBacklight;
    private final int mDefaultBacklightForVr;
    private final DisplayManager mDisplayManager;
    private boolean mExternalChange;
    private volatile boolean mIsVrModeEnabled;
    private boolean mListening;
    private final int mMaximumBacklight;
    private final int mMaximumBacklightForVr;
    private final int mMinimumBacklight;
    private final int mMinimumBacklightForVr;
    private ValueAnimator mSliderAnimator;
    private final CurrentUserTracker mUserTracker;
    private final IVrManager mVrManager;
    private ArrayList<BrightnessStateChangeCallback> mChangeCallbacks = new ArrayList<>();
    private final Runnable mStartListeningRunnable = new Runnable() { // from class: com.android.systemui.settings.BrightnessController.1
        @Override // java.lang.Runnable
        public void run() {
            BrightnessController.this.mBrightnessObserver.startObserving();
            BrightnessController.this.mUserTracker.startTracking();
            BrightnessController.this.mUpdateModeRunnable.run();
            BrightnessController.this.mUpdateSliderRunnable.run();
            BrightnessController.this.mHandler.sendEmptyMessage(3);
        }
    };
    private final Runnable mStopListeningRunnable = new Runnable() { // from class: com.android.systemui.settings.BrightnessController.2
        @Override // java.lang.Runnable
        public void run() {
            BrightnessController.this.mBrightnessObserver.stopObserving();
            BrightnessController.this.mUserTracker.stopTracking();
            BrightnessController.this.mHandler.sendEmptyMessage(4);
        }
    };
    private final Runnable mUpdateModeRunnable = new Runnable() { // from class: com.android.systemui.settings.BrightnessController.3
        @Override // java.lang.Runnable
        public void run() {
            if (BrightnessController.this.mAutomaticAvailable) {
                int automatic = Settings.System.getIntForUser(BrightnessController.this.mContext.getContentResolver(), BrightnessSettings.KEY_SCREEN_BRIGHTNESS_MODE, 0, -2);
                BrightnessController.this.mAutomatic = automatic != 0;
                return;
            }
            BrightnessController.this.mHandler.obtainMessage(2, 0).sendToTarget();
        }
    };
    private final Runnable mUpdateSliderRunnable = new Runnable() { // from class: com.android.systemui.settings.BrightnessController.4
        @Override // java.lang.Runnable
        public void run() {
            boolean inVrMode = BrightnessController.this.mIsVrModeEnabled;
            int val = inVrMode ? Settings.System.getIntForUser(BrightnessController.this.mContext.getContentResolver(), "screen_brightness_for_vr", BrightnessController.this.mDefaultBacklightForVr, -2) : Settings.System.getIntForUser(BrightnessController.this.mContext.getContentResolver(), "screen_brightness", BrightnessController.this.mDefaultBacklight, -2);
            BrightnessController.this.mHandler.obtainMessage(1, val, inVrMode ? 1 : 0).sendToTarget();
        }
    };
    private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub() { // from class: com.android.systemui.settings.BrightnessController.5
        public void onVrStateChanged(boolean enabled) {
            BrightnessController.this.mHandler.obtainMessage(5, enabled ? 1 : 0, 0).sendToTarget();
        }
    };
    private final Handler mHandler = new Handler() { // from class: com.android.systemui.settings.BrightnessController.6
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            boolean z = true;
            BrightnessController.this.mExternalChange = true;
            try {
                int i = msg.what;
                if (i == 1) {
                    BrightnessController brightnessController = BrightnessController.this;
                    int i2 = msg.arg1;
                    if (msg.arg2 == 0) {
                        z = false;
                    }
                    brightnessController.updateSlider(i2, z);
                } else if (i == 2) {
                    ToggleSlider toggleSlider = BrightnessController.this.mControl;
                    if (msg.arg1 == 0) {
                        z = false;
                    }
                    toggleSlider.setChecked(z);
                } else if (i == 3) {
                    BrightnessController.this.mControl.setOnChangedListener(BrightnessController.this);
                } else if (i == 4) {
                    BrightnessController.this.mControl.setOnChangedListener(null);
                } else if (i == 5) {
                    BrightnessController brightnessController2 = BrightnessController.this;
                    if (msg.arg1 == 0) {
                        z = false;
                    }
                    brightnessController2.updateVrMode(z);
                } else {
                    super.handleMessage(msg);
                }
            } finally {
                BrightnessController.this.mExternalChange = false;
            }
        }
    };

    /* loaded from: classes21.dex */
    public interface BrightnessStateChangeCallback {
        void onBrightnessLevelChanged();
    }

    /* loaded from: classes21.dex */
    private class BrightnessObserver extends ContentObserver {
        private final Uri BRIGHTNESS_FOR_VR_URI;
        private final Uri BRIGHTNESS_MODE_URI;
        private final Uri BRIGHTNESS_URI;

        public BrightnessObserver(Handler handler) {
            super(handler);
            this.BRIGHTNESS_MODE_URI = Settings.System.getUriFor(BrightnessSettings.KEY_SCREEN_BRIGHTNESS_MODE);
            this.BRIGHTNESS_URI = Settings.System.getUriFor("screen_brightness");
            this.BRIGHTNESS_FOR_VR_URI = Settings.System.getUriFor("screen_brightness_for_vr");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (selfChange) {
                return;
            }
            if (this.BRIGHTNESS_MODE_URI.equals(uri)) {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateModeRunnable);
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            } else if (this.BRIGHTNESS_URI.equals(uri)) {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            } else if (this.BRIGHTNESS_FOR_VR_URI.equals(uri)) {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            } else {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateModeRunnable);
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            }
            Iterator it = BrightnessController.this.mChangeCallbacks.iterator();
            while (it.hasNext()) {
                BrightnessStateChangeCallback cb = (BrightnessStateChangeCallback) it.next();
                cb.onBrightnessLevelChanged();
            }
        }

        public void startObserving() {
            ContentResolver cr = BrightnessController.this.mContext.getContentResolver();
            cr.unregisterContentObserver(this);
            cr.registerContentObserver(this.BRIGHTNESS_MODE_URI, false, this, -1);
            cr.registerContentObserver(this.BRIGHTNESS_URI, false, this, -1);
            cr.registerContentObserver(this.BRIGHTNESS_FOR_VR_URI, false, this, -1);
        }

        public void stopObserving() {
            ContentResolver cr = BrightnessController.this.mContext.getContentResolver();
            cr.unregisterContentObserver(this);
        }
    }

    public BrightnessController(Context context, ToggleSlider control) {
        this.mContext = context;
        this.mControl = control;
        this.mControl.setMax(BrightnessUtils.GAMMA_SPACE_MAX);
        this.mBackgroundHandler = new Handler((Looper) Dependency.get(Dependency.BG_LOOPER));
        this.mUserTracker = new CurrentUserTracker(this.mContext) { // from class: com.android.systemui.settings.BrightnessController.7
            @Override // com.android.systemui.settings.CurrentUserTracker
            public void onUserSwitched(int newUserId) {
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateModeRunnable);
                BrightnessController.this.mBackgroundHandler.post(BrightnessController.this.mUpdateSliderRunnable);
            }
        };
        this.mBrightnessObserver = new BrightnessObserver(this.mHandler);
        PowerManager pm = (PowerManager) context.getSystemService(PowerManager.class);
        this.mMinimumBacklight = pm.getMinimumScreenBrightnessSetting();
        this.mMaximumBacklight = pm.getMaximumScreenBrightnessSetting();
        this.mDefaultBacklight = pm.getDefaultScreenBrightnessSetting();
        this.mMinimumBacklightForVr = pm.getMinimumScreenBrightnessForVrSetting();
        this.mMaximumBacklightForVr = pm.getMaximumScreenBrightnessForVrSetting();
        this.mDefaultBacklightForVr = pm.getDefaultScreenBrightnessForVrSetting();
        this.mAutomaticAvailable = context.getResources().getBoolean(17891367);
        this.mDisplayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        this.mVrManager = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
    }

    public void addStateChangedCallback(BrightnessStateChangeCallback cb) {
        this.mChangeCallbacks.add(cb);
    }

    public boolean removeStateChangedCallback(BrightnessStateChangeCallback cb) {
        return this.mChangeCallbacks.remove(cb);
    }

    @Override // com.android.systemui.settings.ToggleSlider.Listener
    public void onInit(ToggleSlider control) {
    }

    public void registerCallbacks() {
        if (this.mListening) {
            return;
        }
        IVrManager iVrManager = this.mVrManager;
        if (iVrManager != null) {
            try {
                iVrManager.registerListener(this.mVrStateCallbacks);
                this.mIsVrModeEnabled = this.mVrManager.getVrModeState();
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to register VR mode state listener: ", e);
            }
        }
        this.mBackgroundHandler.post(this.mStartListeningRunnable);
        this.mListening = true;
    }

    public void unregisterCallbacks() {
        if (!this.mListening) {
            return;
        }
        IVrManager iVrManager = this.mVrManager;
        if (iVrManager != null) {
            try {
                iVrManager.unregisterListener(this.mVrStateCallbacks);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to unregister VR mode state listener: ", e);
            }
        }
        this.mBackgroundHandler.post(this.mStopListeningRunnable);
        this.mListening = false;
        this.mControlValueInitialized = false;
    }

    @Override // com.android.systemui.settings.ToggleSlider.Listener
    public void onChanged(ToggleSlider toggleSlider, boolean tracking, boolean automatic, int value, boolean stopTracking) {
        int metric;
        int min;
        int max;
        final String setting;
        if (this.mExternalChange) {
            return;
        }
        ValueAnimator valueAnimator = this.mSliderAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (this.mIsVrModeEnabled) {
            metric = 498;
            min = this.mMinimumBacklightForVr;
            max = this.mMaximumBacklightForVr;
            setting = "screen_brightness_for_vr";
        } else {
            if (this.mAutomatic) {
                metric = 219;
            } else {
                metric = 218;
            }
            min = this.mMinimumBacklight;
            max = this.mMaximumBacklight;
            setting = "screen_brightness";
        }
        final int val = BrightnessUtils.convertGammaToLinear(value, min, max);
        if (stopTracking) {
            MetricsLogger.action(this.mContext, metric, val);
        }
        setBrightness(val);
        if (!tracking) {
            AsyncTask.execute(new Runnable() { // from class: com.android.systemui.settings.BrightnessController.8
                @Override // java.lang.Runnable
                public void run() {
                    Settings.System.putIntForUser(BrightnessController.this.mContext.getContentResolver(), setting, val, -2);
                }
            });
        }
        Iterator<BrightnessStateChangeCallback> it = this.mChangeCallbacks.iterator();
        while (it.hasNext()) {
            BrightnessStateChangeCallback cb = it.next();
            cb.onBrightnessLevelChanged();
        }
    }

    public void checkRestrictionAndSetEnabled() {
        this.mBackgroundHandler.post(new Runnable() { // from class: com.android.systemui.settings.BrightnessController.9
            @Override // java.lang.Runnable
            public void run() {
                ((ToggleSliderView) BrightnessController.this.mControl).setEnforcedAdmin(RestrictedLockUtilsInternal.checkIfRestrictionEnforced(BrightnessController.this.mContext, "no_config_brightness", BrightnessController.this.mUserTracker.getCurrentUserId()));
            }
        });
    }

    private void setMode(int mode) {
        Settings.System.putIntForUser(this.mContext.getContentResolver(), BrightnessSettings.KEY_SCREEN_BRIGHTNESS_MODE, mode, this.mUserTracker.getCurrentUserId());
    }

    private void setBrightness(int brightness) {
        this.mDisplayManager.setTemporaryBrightness(brightness);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVrMode(boolean isEnabled) {
        if (this.mIsVrModeEnabled != isEnabled) {
            this.mIsVrModeEnabled = isEnabled;
            this.mBackgroundHandler.post(this.mUpdateSliderRunnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSlider(int val, boolean inVrMode) {
        int min;
        int max;
        if (inVrMode) {
            min = this.mMinimumBacklightForVr;
            max = this.mMaximumBacklightForVr;
        } else {
            min = this.mMinimumBacklight;
            max = this.mMaximumBacklight;
        }
        if (val == BrightnessUtils.convertGammaToLinear(this.mControl.getValue(), min, max)) {
            return;
        }
        int sliderVal = BrightnessUtils.convertLinearToGamma(val, min, max);
        animateSliderTo(sliderVal);
    }

    private void animateSliderTo(int target) {
        if (!this.mControlValueInitialized) {
            this.mControl.setValue(target);
            this.mControlValueInitialized = true;
        }
        ValueAnimator valueAnimator = this.mSliderAnimator;
        if (valueAnimator != null && valueAnimator.isStarted()) {
            this.mSliderAnimator.cancel();
        }
        this.mSliderAnimator = ValueAnimator.ofInt(this.mControl.getValue(), target);
        this.mSliderAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.settings.-$$Lambda$BrightnessController$T5g_am3jK-it6CD1eLLpr05aFxc
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                BrightnessController.this.lambda$animateSliderTo$0$BrightnessController(valueAnimator2);
            }
        });
        long animationDuration = (Math.abs(this.mControl.getValue() - target) * 3000) / BrightnessUtils.GAMMA_SPACE_MAX;
        this.mSliderAnimator.setDuration(animationDuration);
        this.mSliderAnimator.start();
    }

    public /* synthetic */ void lambda$animateSliderTo$0$BrightnessController(ValueAnimator animation) {
        this.mExternalChange = true;
        this.mControl.setValue(((Integer) animation.getAnimatedValue()).intValue());
        this.mExternalChange = false;
    }
}
