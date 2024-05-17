package com.xiaopeng.systemui.controller;

import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.display.IDisplayManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.controller.brightness.BrightnessSettings;
/* loaded from: classes24.dex */
public class BrightnessController {
    private static final int DEF_ICM_BRIGHTNESS = 70;
    private static final int DEF_ICM_BRIGHTNESS_DARK = 20;
    private static final int DEF_ICM_BRIGHTNESS_DAY = 70;
    private static final int DEF_ICM_BRIGHTNESS_NIGHT = 40;
    private static final int DEF_SCREEN_BRIGHTNESS = 179;
    private static final int DEF_SCREEN_BRIGHTNESS_DARK = 51;
    private static final int DEF_SCREEN_BRIGHTNESS_DAY = 179;
    private static final int DEF_SCREEN_BRIGHTNESS_NIGHT = 102;
    public static final int FROM_ICM_MANAGER = 2;
    public static final int FROM_ICM_SETTING = 1;
    private static final int ICM_BRIGHTNESS_MAX = 100;
    private static final int ICM_BRIGHTNESS_MIN = 1;
    public static final String KEY_DARK_MODE_ADJUST_TYPE = "screen_brightness_dark_adj_type";
    public static final String KEY_ICM_BRIGHTNESS = "screen_brightness_2";
    public static final String KEY_ICM_BRIGHTNESS_CALLBACK = "screen_brightness_callback_2";
    public static final String KEY_ICM_BRIGHTNESS_DAY = "screen_brightness_day_2";
    public static final String KEY_ICM_BRIGHTNESS_NIGHT = "screen_brightness_nigh_2";
    public static final String KEY_PSN_SCREEN_BRIGHTNESS = "screen_brightness_1";
    public static final String KEY_PSN_SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode_1";
    public static final String KEY_SCREEN_BRIGHTNESS = "screen_brightness";
    public static final String KEY_SCREEN_BRIGHTNESS_DARK = "screen_brightness_dark_state";
    public static final String KEY_SCREEN_BRIGHTNESS_DAY = "screen_brightness_day_0";
    public static final String KEY_SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode_0";
    public static final String KEY_SCREEN_BRIGHTNESS_NIGHT = "screen_brightness_night_0";
    private static final String PROP_BRIGHTNESS_DARK_ENV_SUPPORT = "persist.sys.xp.brightness.dark.env.support";
    public static final String PROP_BRIGHTNESS_DARK_IGNORE = "persist.sys.xp.brightness.dark.ignore";
    private static final String TAG = "BrightnessController";
    private static final int TYPE_ADJUST_ICM = 1;
    private static final int TYPE_ADJUST_MASK = 0;
    private static final int TYPE_ADJUST_SCREEN = 2;
    public static final int TYPE_ICM = 1;
    public static final int TYPE_SCREEN = 2;
    private BrightnessObserver mBrightnessObserver;
    private CarController.CarServiceAdapter mCarServiceAdapter;
    private final Context mContext;
    private DisplayManager mDisplayManager;
    private static final String PROP_BRIGHTNESS_DARK_IGNORE_SUPPORT = "persist.sys.xp.brightness.dark.ignore.support";
    private static final boolean BRIGHTNESS_DARK_IGNORE_SUPPORT = SystemProperties.getBoolean(PROP_BRIGHTNESS_DARK_IGNORE_SUPPORT, true);
    private static final boolean BRIGHTNESS_DARK_ENV_SUPPORT = SystemProperties.getBoolean("persist.sys.xp.brightness.dark.env.support", true);
    private static boolean sDarkModeSupport = true;
    private static boolean sDarkModeEnabled = false;
    private static boolean sIcmDarkBrightnessChanged = false;
    private static boolean sScreenDarkBrightnessChanged = false;
    private static BrightnessController sBrightnessController = null;
    private int mLastIcmDarkBrightness = 20;
    private int mLastScreenDarkBrightness = 51;
    private int mIcmBrightness = 70;
    private int mIcmBrightnessDay = 70;
    private int mIcmBrightnessNight = 40;
    private int mScreenBrightness = 179;
    private int mScreenBrightnessDay = 179;
    private int mScreenBrightnessNight = 102;
    private boolean mInitCompleted = false;
    private boolean mCarServiceConnected = false;
    private boolean mBrightnessChangedCompleted = false;
    private final Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.controller.BrightnessController.1
    };
    private CarController.CarCallback mCarCallback = new CarController.CarCallback() { // from class: com.xiaopeng.systemui.controller.BrightnessController.3
        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarControlChanged(int type, Object newValue) {
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarServiceChanged(int type, Object newValue) {
            if (type == 1000) {
                BrightnessController.this.mCarServiceConnected = true;
            } else if (type == 3301) {
                try {
                    Integer[] values = (Integer[]) newValue;
                    int direction = values[0].intValue() == 0 ? 1 : -1;
                    int adj = values[1].intValue();
                    int brightness = BrightnessController.this.getTargetIcmBrightness() + (direction * 5 * adj);
                    if (brightness > 100) {
                        brightness = 100;
                    }
                    if (brightness < 0) {
                        brightness = 0;
                    }
                    BrightnessController.this.setIcmBrightness(brightness);
                } catch (Exception e) {
                }
            }
        }
    };
    private BrightnessAnimator mBrightnessAnimator = new BrightnessAnimator(this.mHandler);

    public static BrightnessController getInstance(Context context) {
        if (sBrightnessController == null) {
            synchronized (BrightnessController.class) {
                if (sBrightnessController == null) {
                    sBrightnessController = new BrightnessController(context);
                }
            }
        }
        return sBrightnessController;
    }

    private BrightnessController(Context context) {
        this.mContext = context;
    }

    public void init() {
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        this.mCarServiceAdapter = CarController.getInstance(this.mContext).getCarServiceAdapter();
        this.mBrightnessObserver = new BrightnessObserver(this.mHandler);
        this.mBrightnessObserver.startObserving();
        CarController.getInstance(this.mContext).addCallback(this.mCarCallback);
        initBrightness();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initBrightness() {
        boolean serviceConnected = this.mCarServiceConnected;
        boolean bootCompleted = "1".equals(SystemProperties.get("sys.boot_completed"));
        Logger.i(TAG, "initBrightness serviceConnected=" + serviceConnected + " bootCompleted=" + bootCompleted);
        if (serviceConnected && bootCompleted) {
            initIcmBrightness();
            initScreenBrightness();
            onBrightnessDarkChanged();
            this.mInitCompleted = true;
            return;
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.controller.BrightnessController.2
            @Override // java.lang.Runnable
            public void run() {
                BrightnessController.this.initBrightness();
            }
        }, OsdController.TN.DURATION_TIMEOUT_SHORT);
    }

    private void initIcmBrightness() {
        readIcmBrightness();
        int currentBrightness = this.mCarServiceAdapter.getIcmBrightness();
        int targetBrightness = getTargetIcmBrightness();
        Logger.i(TAG, "initIcmBrightness currentBrightness=" + currentBrightness + " targetBrightness=" + targetBrightness);
        if (currentBrightness != targetBrightness) {
            setIcmBrightness(targetBrightness);
        }
    }

    private void initScreenBrightness() {
        readScreenBrightness();
        int currentBrightness = this.mScreenBrightness;
        int targetBrightness = getTargetScreenBrightness();
        Logger.i(TAG, "initScreenBrightness currentBrightness=" + currentBrightness + " targetBrightness=" + targetBrightness);
        if (targetBrightness != currentBrightness) {
            setScreenBrightness(targetBrightness);
        }
    }

    public void updateBrightness() {
        readIcmBrightness();
        readScreenBrightness();
    }

    public boolean isIcmAnimating() {
        return this.mBrightnessAnimator.type == 1 && this.mBrightnessAnimator.isAnimating();
    }

    public void animateIcmBrightness(int to) {
        boolean forceIcmOnly = SystemController.getInstance(this.mContext).isMeditationMode();
        CarController.CarServiceAdapter carServiceAdapter = this.mCarServiceAdapter;
        int from = carServiceAdapter != null ? carServiceAdapter.getIcmBrightness() : this.mIcmBrightness;
        animateIcmBrightness(from, to, forceIcmOnly);
    }

    public void animateIcmBrightness(int to, boolean forceIcmOnly) {
        CarController.CarServiceAdapter carServiceAdapter = this.mCarServiceAdapter;
        int from = carServiceAdapter != null ? carServiceAdapter.getIcmBrightness() : this.mIcmBrightness;
        animateIcmBrightness(from, to, forceIcmOnly);
    }

    public void animateIcmBrightness(int form, int to, boolean forceIcmOnly) {
        Logger.i(TAG, "animateIcmBrightness form=" + form + " to=" + to + " forceIcmOnly=" + forceIcmOnly);
        this.mBrightnessAnimator.setForceIcmOnly(forceIcmOnly);
        this.mBrightnessAnimator.animateTo(1, form, to);
    }

    public void animateScreenBrightness(int form, int to) {
    }

    public void setIcmBrightness(int brightness) {
        setIcmBrightness(brightness, false);
    }

    public void setIcmBrightness(int brightness, boolean icmOnly) {
        Logger.i(TAG, "setIcmBrightness brightness=" + brightness + " icmOnly=" + icmOnly);
        int value = getIntForUser("screen_brightness_2", 0);
        if (brightness != value && !icmOnly) {
            putIntForUser("screen_brightness_2", brightness);
        }
        if (this.mCarServiceAdapter.isCarServiceReady() && brightness != this.mCarServiceAdapter.getIcmBrightness()) {
            try {
                this.mCarServiceAdapter.setIcmBrightness(brightness);
            } catch (Exception e) {
            }
        }
    }

    public void setIcmBrightnessCallback(int brightness) {
        putIntForUser("screen_brightness_callback_2", brightness);
    }

    public void setScreenBrightness(int brightness) {
        putIntForUser("screen_brightness", brightness);
    }

    public void setPsnScreenBrightness(int brightness) {
        putIntForUser("screen_brightness_1", brightness);
        Logger.d(TAG, "setPsnScreenBrightness : screen_brightness_mode_1:0");
        int autoBrightnessDevice = CarModelsManager.getConfig().autoBrightness();
        if (autoBrightnessDevice == CarModelsManager.getFeature().hasCIU() || autoBrightnessDevice == CarModelsManager.getFeature().hasXPU()) {
            int brightnessMode = Settings.System.getInt(this.mContext.getContentResolver(), "screen_brightness_mode_1", 1);
            if (brightnessMode == 1) {
                putIntForUser("screen_brightness_mode_1", 0);
            }
        }
    }

    public int getIcmBrightness() {
        return this.mIcmBrightness;
    }

    public int getScreenBrightness() {
        return this.mScreenBrightness;
    }

    public int getPsnScreenBrightness() {
        return getIntForUser("screen_brightness_1", 1);
    }

    public int getTargetIcmBrightness() {
        int brightness;
        boolean darkModeActive = isDarkModeActive();
        boolean isNightMode = isNight(this.mContext);
        boolean meditationMode = SystemController.getInstance(this.mContext).isMeditationMode();
        readIcmBrightness();
        int i = this.mIcmBrightness;
        if (meditationMode) {
            brightness = 1;
        } else if (darkModeActive) {
            brightness = sIcmDarkBrightnessChanged ? this.mIcmBrightness : 20;
        } else {
            int brightness2 = isNightMode ? this.mIcmBrightnessNight : this.mIcmBrightnessDay;
            int brightness3 = brightness2 > 1 ? brightness2 : 1;
            brightness = brightness3 < 100 ? brightness3 : 100;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("getTargetIcmBrightness ");
        buffer.append(" isNightMode=" + isNightMode);
        buffer.append(" isMeditationMode=" + meditationMode);
        buffer.append(" darkModeActive=" + darkModeActive);
        buffer.append(" darkChanged=" + sIcmDarkBrightnessChanged);
        buffer.append(" brightness=" + brightness);
        buffer.append(" icmBrightnessNight=" + this.mIcmBrightnessNight);
        buffer.append(" icmBrightnessDay=" + this.mIcmBrightnessDay);
        Logger.i(TAG, buffer.toString());
        return brightness;
    }

    public int getTargetScreenBrightness() {
        int brightness;
        boolean darkModeActive = isDarkModeActive();
        boolean isNightMode = isNight(this.mContext);
        readScreenBrightness();
        int i = this.mScreenBrightness;
        if (darkModeActive) {
            brightness = sScreenDarkBrightnessChanged ? this.mScreenBrightness : 51;
        } else {
            brightness = isNightMode ? this.mScreenBrightnessNight : this.mScreenBrightnessDay;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("getTargetScreenBrightness ");
        buffer.append(" isNightMode=" + isNightMode);
        buffer.append(" darkModeActive=" + darkModeActive);
        buffer.append(" darkChanged=" + sScreenDarkBrightnessChanged);
        buffer.append(" brightness=" + brightness);
        buffer.append(" screenBrightnessNight=" + this.mScreenBrightnessNight);
        buffer.append(" screenBrightnessDay=" + this.mScreenBrightnessDay);
        Logger.i(TAG, buffer.toString());
        return brightness;
    }

    private void readIcmBrightness() {
        this.mIcmBrightness = getIntForUser("screen_brightness_2", 70);
        this.mIcmBrightnessDay = getIntForUser("screen_brightness_day_2", 70);
        this.mIcmBrightnessNight = getIntForUser(KEY_ICM_BRIGHTNESS_NIGHT, 40);
        Logger.i(TAG, "readIcmBrightness brightness=" + this.mIcmBrightness + " day=" + this.mIcmBrightnessDay + " night=" + this.mIcmBrightnessNight);
    }

    private void readScreenBrightness() {
        this.mScreenBrightness = getIntForUser("screen_brightness", 179);
        this.mScreenBrightnessDay = getIntForUser("screen_brightness_day_0", 179);
        this.mScreenBrightnessNight = getIntForUser("screen_brightness_night_0", 102);
        Logger.i(TAG, "readScreenBrightness brightness=" + this.mScreenBrightness + " day=" + this.mScreenBrightnessDay + " night=" + this.mScreenBrightnessNight);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (this.mInitCompleted && ThemeManager.isThemeChanged(newConfig)) {
            Logger.i(TAG, "onConfigurationChanged isNight=" + isNight(this.mContext));
            if (!this.mBrightnessChangedCompleted) {
                onThemeChanged();
            }
            this.mBrightnessChangedCompleted = false;
        }
    }

    public void onThemeChanged() {
        boolean darkModeActive = isDarkModeActive();
        boolean canUpdateBrightness = false;
        boolean darkModeChange = darkModeActive != sDarkModeEnabled;
        boolean meditationMode = SystemController.getInstance(this.mContext).isMeditationMode();
        Logger.i(TAG, "onThemeChanged darkModeActive=" + darkModeActive + " darkModeChange" + darkModeChange + " meditationMode=" + meditationMode);
        if (!darkModeActive || darkModeChange) {
            canUpdateBrightness = true;
        }
        if (canUpdateBrightness) {
            animateIcmBrightness(getTargetIcmBrightness(), meditationMode);
            setScreenBrightness(getTargetScreenBrightness());
            onBrightnessDarkChanged();
        }
        this.mBrightnessChangedCompleted = true;
    }

    public void onBrightnessChanged(boolean selfChange, Uri uri) {
        BrightnessObserver brightnessObserver = this.mBrightnessObserver;
        if (brightnessObserver != null && uri != null) {
            if (brightnessObserver.ICM_BRIGHTNESS.equals(uri)) {
                this.mIcmBrightness = getIntForUser("screen_brightness_2", 70);
                onIcmBrightnessChanged(1);
            } else if (this.mBrightnessObserver.SCREEN_BRIGHTNESS.equals(uri)) {
                this.mScreenBrightness = getIntForUser("screen_brightness", 179);
                onScreenBrightnessChanged();
            } else if (this.mBrightnessObserver.SCREEN_BRIGHTNESS_MODE.equals(uri)) {
                onBrightnessModeChanged();
            }
        }
    }

    public void onIcmBrightnessChanged(int changed) {
        StringBuffer buffer = new StringBuffer("");
        buffer.append("onIcmBrightnessChanged");
        boolean isNightMode = isNight(this.mContext);
        boolean darkModeActive = isDarkModeActive();
        String brightnessKey = isNightMode ? KEY_ICM_BRIGHTNESS_NIGHT : "screen_brightness_day_2";
        boolean isMeditationMode = SystemController.getInstance(this.mContext).isMeditationMode();
        boolean isIcmAnimating = getInstance(this.mContext).isIcmAnimating();
        if (changed == 1) {
            if (!darkModeActive) {
                putIntForUser(brightnessKey, this.mIcmBrightness);
                readIcmBrightness();
            } else {
                sIcmDarkBrightnessChanged = this.mIcmBrightness != this.mLastIcmDarkBrightness;
                if (sIcmDarkBrightnessChanged) {
                    setDarkModeAdjustType(1);
                }
            }
            animateIcmBrightness(getTargetIcmBrightness(), true);
        } else if (changed == 2) {
            if (isIcmAnimating || isMeditationMode) {
                return;
            }
            int brightness = this.mCarServiceAdapter.getIcmBrightness();
            setIcmBrightnessCallback(brightness);
            buffer.append(" callback=" + brightness);
        }
        buffer.append(" changed=" + changed);
        buffer.append(" isNightMode=" + isNightMode);
        buffer.append(" darkModeActive=" + darkModeActive);
        buffer.append(" isIcmAnimating=" + isIcmAnimating);
        buffer.append(" isMeditationMode=" + isMeditationMode);
        buffer.append(" brightness=" + this.mIcmBrightness);
        buffer.append(" day=" + this.mIcmBrightnessDay);
        buffer.append(" night=" + this.mIcmBrightnessNight);
        Logger.i(TAG, buffer.toString());
    }

    public void onScreenBrightnessChanged() {
        boolean isNightMode = isNight(this.mContext);
        boolean darkModeActive = isDarkModeActive();
        String brightnessKey = isNightMode ? "screen_brightness_night_0" : "screen_brightness_day_0";
        if (!darkModeActive) {
            putIntForUser(brightnessKey, this.mScreenBrightness);
            readScreenBrightness();
        } else {
            sScreenDarkBrightnessChanged = this.mScreenBrightness != this.mLastScreenDarkBrightness;
            if (sScreenDarkBrightnessChanged) {
                setDarkModeAdjustType(2);
            }
        }
        Logger.i(TAG, "onScreenBrightnessChanged isNightMode=" + isNightMode + " darkModeActive=" + darkModeActive + " brightness=" + this.mScreenBrightness + " day=" + this.mScreenBrightnessDay + " night=" + this.mScreenBrightnessNight);
    }

    public void onLampChanged() {
        if (BRIGHTNESS_DARK_ENV_SUPPORT) {
            return;
        }
        boolean darkIgnore = shouldIgnoreDarkState();
        Logger.i(TAG, "onLampChanged darkIgnore=" + darkIgnore);
        if (darkIgnore) {
            return;
        }
        onBrightnessDarkChanged();
    }

    public void onEnvironmentChanged(int value) {
        if (BRIGHTNESS_DARK_ENV_SUPPORT) {
            onBrightnessDarkChanged();
        }
    }

    public void onBrightnessModeChanged() {
        onBrightnessDarkChanged();
    }

    public void onBrightnessDarkChanged() {
        boolean darkModeActive = isDarkModeActive();
        boolean darkModeSupport = isDarkModeSupport();
        boolean isNightMode = isNight(this.mContext);
        StringBuffer buffer = new StringBuffer("");
        buffer.append("onBrightnessDarkChanged");
        buffer.append(" darkModeActive=" + darkModeActive);
        buffer.append(" darkModeSupport=" + darkModeSupport);
        buffer.append(" isNightMode=" + isNightMode);
        buffer.append(" initCompleted=" + this.mInitCompleted);
        buffer.append(" icmBrightness=" + this.mIcmBrightness);
        buffer.append(" screenBrightness=" + this.mScreenBrightness);
        Logger.i(TAG, buffer.toString());
        sDarkModeEnabled = darkModeActive;
        sIcmDarkBrightnessChanged = false;
        sScreenDarkBrightnessChanged = false;
        setDarkModeState(darkModeActive);
        setDarkModeAdjustType(0);
        if (this.mInitCompleted && darkModeSupport) {
            if (darkModeActive) {
                if (this.mIcmBrightness >= 20) {
                    animateIcmBrightness(getTargetIcmBrightness());
                }
                if (this.mScreenBrightness >= 51) {
                    setScreenBrightness(getTargetScreenBrightness());
                }
                this.mLastIcmDarkBrightness = getTargetIcmBrightness();
                this.mLastScreenDarkBrightness = getTargetScreenBrightness();
                return;
            }
            animateIcmBrightness(getTargetIcmBrightness());
            setScreenBrightness(getTargetScreenBrightness());
        }
    }

    public boolean isNight(Context context) {
        int mode = getRealDayNightMode(context);
        return mode == 2;
    }

    public int getRealDayNightMode(Context context) {
        UiModeManager um = (UiModeManager) context.getSystemService("uimode");
        return um.getDayNightAutoMode();
    }

    public boolean hasCiuDevice() {
        if (this.mCarServiceConnected) {
            try {
                if (this.mCarServiceAdapter != null) {
                    return this.mCarServiceAdapter.isCiuExist();
                }
                return false;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean isLampActive() {
        boolean isLampActive = false;
        boolean isFarLampOn = false;
        int lampState = 0;
        int lampGroup = 0;
        if (this.mCarServiceConnected) {
            try {
                isFarLampOn = this.mCarServiceAdapter != null ? this.mCarServiceAdapter.isFarLampOn() : false;
                lampState = this.mCarServiceAdapter != null ? this.mCarServiceAdapter.getNearLampState() : 0;
                lampGroup = this.mCarServiceAdapter != null ? this.mCarServiceAdapter.getHeadLampGroup() : 0;
                boolean z = true;
                boolean lampActive = lampState == 1 || lampGroup == 2;
                if (isFarLampOn || !lampActive) {
                    z = false;
                }
                isLampActive = z;
            } catch (Exception e) {
            }
        }
        Logger.i(TAG, "isLampActive lampActive=" + isLampActive + " connected=" + this.mCarServiceConnected + " farLampOn=" + isFarLampOn + " lampState=" + lampState + " lampGroup=" + lampGroup);
        return isLampActive;
    }

    public boolean isEnvrActive() {
        int env = 0;
        boolean isEnvrActive = false;
        if (this.mCarServiceConnected) {
            try {
                boolean z = false;
                env = this.mCarServiceAdapter != null ? this.mCarServiceAdapter.getEnvironmentMode() : 0;
                if (env == 2 || env == 1) {
                    z = true;
                }
                isEnvrActive = z;
            } catch (Exception e) {
            }
        }
        Logger.i(TAG, "isEnvrActive isEnvrActive=" + isEnvrActive + " env=" + env);
        return isEnvrActive;
    }

    public boolean isDarkModeActive() {
        boolean darkActive = BRIGHTNESS_DARK_ENV_SUPPORT ? isEnvrActive() : isLampActive();
        boolean isNightMode = isNight(this.mContext);
        boolean darkModeSupport = isDarkModeSupport();
        boolean brightnessAutomatic = isBrightnessAutomatic();
        boolean ret = darkModeSupport && brightnessAutomatic && !isNightMode && darkActive;
        Logger.i(TAG, "isDarkLightAutoEnabled ret=" + ret + " darkActive=" + darkActive + " isNightMode=" + isNightMode + " darkModeSupport=" + darkModeSupport + " brightnessAutomatic=" + brightnessAutomatic);
        return ret;
    }

    public boolean isDarkModeSupport() {
        if (CarModelsManager.getFeature().checkFunctionType() == 0) {
            boolean ret = !CarModelsManager.getConfig().isXPUSupport();
            return ret;
        }
        boolean ret2 = !hasCiuDevice();
        return ret2;
    }

    public boolean isBrightnessAutomatic() {
        int automatic = getIntForUser(BrightnessSettings.KEY_SCREEN_BRIGHTNESS_MODE, 0);
        return automatic != 0;
    }

    public boolean shouldIgnoreDarkState() {
        if (BRIGHTNESS_DARK_IGNORE_SUPPORT) {
            boolean ignore = SystemProperties.getBoolean(PROP_BRIGHTNESS_DARK_IGNORE, false);
            Logger.i(TAG, "shouldIgnoreDarkState ignore=" + ignore);
            return ignore;
        }
        return false;
    }

    public void setDarkModeState(boolean active) {
        putIntForUser("screen_brightness_dark_state", active ? 1 : 0);
    }

    public void setDarkModeAdjustType(int type) {
        int value = getIntForUser("screen_brightness_dark_adj_type", 0);
        Logger.i(TAG, "setDarkModeAdjustType type=0x" + Integer.toHexString(type) + " value=0x" + Integer.toHexString(value));
        if (type == 0) {
            putIntForUser("screen_brightness_dark_adj_type", type);
        } else if (type == 1) {
            if ((value & 1) != 1) {
                int newValue = value | 1;
                putIntForUser("screen_brightness_dark_adj_type", newValue);
            }
        } else if (type == 2 && (value & 2) != 2) {
            int newValue2 = value | 2;
            putIntForUser("screen_brightness_dark_adj_type", newValue2);
        }
    }

    private int getIntForUser(String key, int defaultValue) {
        return Settings.System.getIntForUser(this.mContext.getContentResolver(), key, defaultValue, -2);
    }

    private void putIntForUser(String key, int value) {
        try {
            boolean ret = Settings.System.putIntForUser(this.mContext.getContentResolver(), key, value, -2);
            Logger.i(TAG, "putIntForUser key=" + key + " value=" + value + " ret=" + ret);
        } catch (Exception e) {
            Logger.i(TAG, "putIntForUser e=" + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public class BrightnessObserver extends ContentObserver {
        public final Uri ICM_BRIGHTNESS;
        public final Uri SCREEN_BRIGHTNESS;
        public final Uri SCREEN_BRIGHTNESS_MODE;

        public BrightnessObserver(Handler handler) {
            super(handler);
            this.ICM_BRIGHTNESS = Settings.System.getUriFor("screen_brightness_2");
            this.SCREEN_BRIGHTNESS = Settings.System.getUriFor("screen_brightness");
            this.SCREEN_BRIGHTNESS_MODE = Settings.System.getUriFor("screen_brightness_mode_0");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            StringBuilder sb = new StringBuilder();
            sb.append("onChange selfChange=");
            sb.append(selfChange);
            sb.append(" uri=");
            sb.append(uri != null ? uri.toString() : "");
            Logger.i(BrightnessController.TAG, sb.toString());
            if (selfChange) {
                return;
            }
            BrightnessController.this.onBrightnessChanged(selfChange, uri);
        }

        public void startObserving() {
            ContentResolver resolver = BrightnessController.this.mContext.getContentResolver();
            resolver.unregisterContentObserver(this);
            resolver.registerContentObserver(this.ICM_BRIGHTNESS, false, this, -1);
            resolver.registerContentObserver(this.SCREEN_BRIGHTNESS, false, this, -1);
            resolver.registerContentObserver(this.SCREEN_BRIGHTNESS_MODE, false, this, -1);
        }

        public void stopObserving() {
            ContentResolver resolver = BrightnessController.this.mContext.getContentResolver();
            resolver.unregisterContentObserver(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public class BrightnessAnimator {
        public static final double FRAME_MILLIS = 0.016d;
        private int animatedFrame;
        private int animatedValue;
        private boolean animating;
        private boolean forceIcmOnly;
        private int fromValue;
        private Handler handler;
        private int toValue;
        private int totalFrame;
        private int type;
        public final int RATE = SystemProperties.getInt("persist.sys.xp.brightness.icm.rate", 40);
        private Runnable animatorRunnable = new Runnable() { // from class: com.xiaopeng.systemui.controller.BrightnessController.BrightnessAnimator.1
            @Override // java.lang.Runnable
            public void run() {
                float percent = BrightnessAnimator.this.totalFrame > 0 ? BrightnessAnimator.this.animatedFrame / BrightnessAnimator.this.totalFrame : 1.0f;
                BrightnessAnimator brightnessAnimator = BrightnessAnimator.this;
                brightnessAnimator.animatedValue = brightnessAnimator.getSmoothBrightness(brightnessAnimator.fromValue, BrightnessAnimator.this.toValue, percent);
                BrightnessAnimator.access$508(BrightnessAnimator.this);
                BrightnessAnimator brightnessAnimator2 = BrightnessAnimator.this;
                brightnessAnimator2.animateBrightness(brightnessAnimator2.type, BrightnessAnimator.this.animatedValue, percent != 1.0f);
                if (BrightnessAnimator.this.toValue != BrightnessAnimator.this.animatedValue) {
                    BrightnessAnimator.this.postAnimator();
                    return;
                }
                BrightnessAnimator.this.setAnimating(false);
                BrightnessAnimator brightnessAnimator3 = BrightnessAnimator.this;
                brightnessAnimator3.animateBrightness(brightnessAnimator3.type, BrightnessAnimator.this.animatedValue, false);
                BrightnessAnimator.this.reset();
            }
        };
        private IDisplayManager display = getDisplay();

        static /* synthetic */ int access$508(BrightnessAnimator x0) {
            int i = x0.animatedFrame;
            x0.animatedFrame = i + 1;
            return i;
        }

        public BrightnessAnimator(Handler handler) {
            this.handler = handler;
        }

        public void set(int type, int from, int to, boolean animating) {
            this.type = type;
            this.toValue = to;
            this.fromValue = from;
            this.animatedValue = from;
            this.animating = animating;
            this.totalFrame = getFrame(from, to);
            this.animatedFrame = 0;
        }

        public void reset() {
            this.type = 0;
            this.toValue = 0;
            this.fromValue = 0;
            this.animatedValue = 0;
            this.animating = false;
            this.totalFrame = 0;
            this.animatedFrame = 0;
            this.forceIcmOnly = false;
        }

        public int getFrame(int from, int to) {
            float delta = Math.abs(to - from);
            double amount = (this.RATE * 0.016d) / 1.0d;
            return (int) Math.ceil(delta / amount);
        }

        public boolean isAnimating() {
            return this.animating;
        }

        public void setAnimating(boolean animate) {
            this.animating = animate;
        }

        public int getSmoothBrightness(int from, int to, float percent) {
            if (this.display == null) {
                this.display = getDisplay();
            }
            if (this.display != null) {
                try {
                    Logger.i(BrightnessController.TAG, "getSmoothBrightness from=" + from + " to=" + to + " percent=" + percent + " brightness=" + to);
                } catch (Exception e) {
                }
                return to;
            }
            return to;
        }

        public IDisplayManager getDisplay() {
            IBinder b = ServiceManager.getService("display");
            if (b != null) {
                return IDisplayManager.Stub.asInterface(b);
            }
            return null;
        }

        public void animateTo(int type, int from, int to) {
            set(type, from, to, true);
            cancelAnimator();
            postAnimator();
        }

        public void setForceIcmOnly(boolean icmOnly) {
            this.forceIcmOnly = icmOnly;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void postAnimator() {
            this.handler.post(this.animatorRunnable);
        }

        private void cancelAnimator() {
            this.handler.removeCallbacks(this.animatorRunnable);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void animateBrightness(int type, int brightness, boolean icmOnly) {
            if (type == 1) {
                BrightnessController.this.setIcmBrightness(brightness, this.forceIcmOnly ? true : icmOnly);
            }
        }
    }
}
