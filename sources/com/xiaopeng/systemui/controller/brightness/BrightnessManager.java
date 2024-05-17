package com.xiaopeng.systemui.controller.brightness;

import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.WindowManager;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.controller.brightness.impl.ScreenBrightness;
import com.xiaopeng.util.FeatureOption;
import com.xiaopeng.view.SharedDisplayListener;
import com.xiaopeng.view.WindowManagerFactory;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
/* loaded from: classes24.dex */
public class BrightnessManager {
    private static final String ACTION_SCREEN_STATUS_CHANGE = "com.xiaopeng.broadcast.ACTION_SCREEN_STATUS_CHANGE";
    private static final String ACTION_SCREEN_STATUS_CHANGE_DEVICE_EXTRA = "device";
    private static final String ACTION_SCREEN_STATUS_CHANGE_STATUS_EXTRA = "status";
    private static final int ICM_BRIGHTNESS_AJUST = 0;
    private static final int PANEL_TYPE_CENTER = 2;
    private static final int PANEL_TYPE_CENTER_SECOND = 4;
    private static final int PANEL_TYPE_CMS = 8;
    private static final int PANEL_TYPE_ICM = 1;
    private static final String TAG = "XmartBrightness_Manager";
    private static boolean[] sCustomEnabledArray;
    public static int sRebootMode;
    private Context mContext;
    private boolean[] mDarkBrightnessChangedArray;
    private boolean[] mDarkModeActiveArray;
    private final PowerManager mPowerManager;
    private AbsBrightness[] mScreenBrightnessArray;
    private final Handler mScreenHandler;
    private SettingsObserver mSettingsObserver;
    private WindowManagerFactory mWindowFactory;
    public static final boolean FO_HAS_INDEPENDENT_AUTOMODE = FeatureOption.FO_INDEPENDENT_AUTOMODE_ENABLED;
    public static final boolean FO_IS_RESTORE_USER_BRIGHTNESS = FeatureOption.FO_RESTORE_USER_BRIGHTNESS;
    private static boolean sPerformThemeEvent = false;
    private static boolean sPsgDarkModeActive = false;
    private static boolean sICMDarkModeActive = false;
    private static boolean sIcmDarkBrightnessChanged = false;
    private static boolean sScreenDarkBrightnessChanged = false;
    private static boolean sPassengerScreenBrightnessChanged = false;
    private static BrightnessManager sBrightnessManager = null;
    private static int mLastLightIntensity = -1;
    private static String RESTART_TAG = "sys.xiaopeng.systemui";
    private boolean mInitCompleted = false;
    private int mLastIcmDarkBrightness = 20;
    private int mLastScreenDarkBrightness = 51;
    private int mLastPassengerScreenBrightness = 51;
    private final BroadcastReceiver mScreenReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.controller.brightness.BrightnessManager.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.d(BrightnessManager.TAG, "ScreenWork xpservice receiver action:" + action);
            if ("com.xiaopeng.broadcast.ACTION_SCREEN_STATUS_CHANGE".equals(action)) {
                String whichScreen = intent.getStringExtra(BrightnessManager.ACTION_SCREEN_STATUS_CHANGE_DEVICE_EXTRA);
                boolean status = intent.getBooleanExtra("status", true);
                Logger.i(BrightnessManager.TAG, "ScreenWork receiver whichScreen:" + whichScreen + " status:" + status);
                Iterator it = BrightnessManager.this.mOnScreenChangedCallBacks.iterator();
                while (it.hasNext()) {
                    OnScreenChangedCallBack back = (OnScreenChangedCallBack) it.next();
                    back.onScreenPowerChanged(whichScreen, status);
                }
            }
        }
    };
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final CopyOnWriteArraySet<OnScreenChangedCallBack> mOnScreenChangedCallBacks = new CopyOnWriteArraySet<>();
    private final HandlerThread mScreenHandlerThread = new HandlerThread(TAG);

    /* loaded from: classes24.dex */
    public interface OnScreenChangedCallBack {
        void onScreenPowerChanged(String str, boolean z);
    }

    public static BrightnessManager get(Context context) {
        if (sBrightnessManager == null) {
            synchronized (BrightnessManager.class) {
                if (sBrightnessManager == null) {
                    sBrightnessManager = new BrightnessManager(context);
                }
            }
        }
        return sBrightnessManager;
    }

    private BrightnessManager(Context context) {
        this.mContext = context;
        this.mScreenHandlerThread.start();
        this.mScreenHandler = new Handler(this.mScreenHandlerThread.getLooper());
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
    }

    public void init() {
        sRebootMode = BrightnessSettings.getInt(this.mContext, BrightnessSettings.KEY_SYSTEM_REBOOT_MODE, 1);
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        this.mSettingsObserver.startObserving();
        if (FO_IS_RESTORE_USER_BRIGHTNESS) {
            restoreBrightnessPlatformization();
        }
        this.mScreenBrightnessArray = new AbsBrightness[BrightnessSettings.DISPLAY_MAX];
        this.mDarkModeActiveArray = new boolean[BrightnessSettings.DISPLAY_MAX];
        this.mDarkBrightnessChangedArray = new boolean[BrightnessSettings.DISPLAY_MAX];
        sCustomEnabledArray = new boolean[BrightnessSettings.DISPLAY_MAX];
        for (int i = 0; i < BrightnessSettings.DISPLAY_MAX; i++) {
            this.mScreenBrightnessArray[i] = new ScreenBrightness(i, this.mContext, this.mScreenHandler);
            this.mDarkModeActiveArray[i] = false;
            this.mDarkBrightnessChangedArray[i] = false;
            sCustomEnabledArray[i] = false;
        }
        IntentFilter filter = new IntentFilter("com.xiaopeng.broadcast.ACTION_SCREEN_STATUS_CHANGE");
        this.mContext.registerReceiver(this.mScreenReceiver, filter);
        initBrightness();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initBrightness() {
        boolean serviceConnected = BrightnessCarManager.get(this.mContext).isCarConnected();
        boolean bootCompleted = "1".equals(SystemProperties.get("sys.boot_completed"));
        Logger.i(TAG, "initBrightness serviceConnected=" + serviceConnected + " bootCompleted=" + bootCompleted);
        if (serviceConnected && bootCompleted) {
            this.mInitCompleted = true;
            restoreBrightness();
            this.mPowerManager.setXpScreenIdle("xp_mt_psg", false);
            for (int i = 0; i < BrightnessSettings.DISPLAY_MAX; i++) {
                if (i != 2) {
                    Context context = this.mContext;
                    BrightnessSettings.putInt(context, "screen_brightness_change_" + i, 0);
                    Context context2 = this.mContext;
                    BrightnessSettings.putInt(context2, "screen_window_brightness_" + i, -1);
                }
            }
            this.mWindowFactory = WindowManagerFactory.create(this.mContext);
            WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
            wm.registerSharedListener(new SharedDisplayListenerImpl());
            return;
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.controller.brightness.BrightnessManager.2
            @Override // java.lang.Runnable
            public void run() {
                BrightnessManager.this.initBrightness();
            }
        }, OsdController.TN.DURATION_TIMEOUT_SHORT);
    }

    public int getBrightness(int type) {
        AbsBrightness[] absBrightnessArr = this.mScreenBrightnessArray;
        if (absBrightnessArr[type] == null) {
            return 0;
        }
        int brightness = absBrightnessArr[type].getBrightness();
        return brightness;
    }

    public void addOnScreenChangedCallBack(OnScreenChangedCallBack back) {
        this.mOnScreenChangedCallBacks.add(back);
    }

    public void removeOnScreenChangedCallBack(OnScreenChangedCallBack back) {
        this.mOnScreenChangedCallBacks.remove(back);
    }

    public boolean isPsnScreenOn() {
        if (!CarModelsManager.getFeature().isSecondaryWindowSupport()) {
            return false;
        }
        boolean on = this.mPowerManager.isScreenOn("xp_mt_psg");
        Logger.i(TAG, "isPsnScreenOn : " + on);
        return on;
    }

    public void setPsnScreenOn(boolean on) {
        if (!CarModelsManager.getFeature().isSecondaryWindowSupport()) {
            return;
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        Logger.i(TAG, "setPsnScreenOn : " + uptimeMillis + " ,on " + on);
        if (on) {
            this.mPowerManager.setXpScreenOn("xp_mt_psg", uptimeMillis);
        } else {
            this.mPowerManager.setXpScreenOff("xp_mt_psg", uptimeMillis);
        }
    }

    public void updateMode() {
        for (int i = 0; i < BrightnessSettings.DISPLAY_MAX; i++) {
            this.mScreenBrightnessArray[i].setMode(getMode(i, this.mContext));
            Logger.i(TAG, "updateMode type= " + i + "screenMode=" + getMode(i, this.mContext));
        }
    }

    /* renamed from: updateBrightness */
    public void lambda$handleThemeEvent$0$BrightnessManager() {
        updateMode();
        for (int i = 0; i < BrightnessSettings.DISPLAY_MAX; i++) {
            AbsBrightness[] absBrightnessArr = this.mScreenBrightnessArray;
            absBrightnessArr[i].setBrightness(absBrightnessArr[i].getBrightness(), true);
        }
        updateDarkBrightness();
    }

    public void updateDarkBrightness() {
        boolean isDarkState = false;
        boolean[] isDarkModeArray = new boolean[BrightnessSettings.DISPLAY_MAX];
        boolean[] isDarkChangedArray = new boolean[BrightnessSettings.DISPLAY_MAX];
        int i = 0;
        while (true) {
            if (i >= BrightnessSettings.DISPLAY_MAX) {
                break;
            } else if (isDarkMode(i, this.mContext)) {
                isDarkState = true;
                break;
            } else {
                isDarkState = false;
                i++;
            }
        }
        setDarkModeState(this.mContext, isDarkState);
        int i2 = 0;
        while (true) {
            if (i2 < BrightnessSettings.DISPLAY_MAX) {
                isDarkModeArray[i2] = isDarkMode(i2, this.mContext);
                isDarkChangedArray[i2] = isDarkModeArray[i2] != this.mDarkModeActiveArray[i2];
                if (isDarkChangedArray[i2] && isDarkModeArray[i2]) {
                    this.mDarkBrightnessChangedArray[i2] = false;
                    if (i2 == 0) {
                        int TYPE_SCREEN_MASK = FO_HAS_INDEPENDENT_AUTOMODE ? 5 : 0;
                        setDarkModeAdjustType(TYPE_SCREEN_MASK, this.mContext);
                        this.mLastScreenDarkBrightness = this.mScreenBrightnessArray[i2].getBrightness();
                    } else if (i2 == 1) {
                        setDarkModeAdjustType(6, this.mContext);
                        this.mLastPassengerScreenBrightness = this.mScreenBrightnessArray[i2].getBrightness();
                    } else if (i2 == 2) {
                        setDarkModeAdjustType(7, this.mContext);
                        this.mLastIcmDarkBrightness = this.mScreenBrightnessArray[i2].getBrightness();
                    }
                }
                this.mDarkModeActiveArray[i2] = isDarkModeArray[i2];
                i2++;
            } else {
                StringBuffer buffer = new StringBuffer("");
                buffer.append("updateDarkBrightness");
                buffer.append(" isDarkMode=" + isDarkModeArray[0]);
                buffer.append(" isDarkChanged=" + isDarkChangedArray[0]);
                buffer.append(" darkModeActive=" + this.mDarkModeActiveArray[0]);
                buffer.append(" icmChanged=" + isDarkChangedArray[2]);
                buffer.append(" screenChanged=" + this.mDarkBrightnessChangedArray[0]);
                buffer.append(" passengerChanged=" + this.mDarkBrightnessChangedArray[1]);
                buffer.append(" lastIcmBrightness=" + this.mLastIcmDarkBrightness);
                buffer.append(" lastScreenBrightness=" + this.mLastScreenDarkBrightness);
                buffer.append(" lastPassengerScreenBrightness=" + this.mLastPassengerScreenBrightness);
                Logger.i(TAG, buffer.toString());
                return;
            }
        }
    }

    public void handleThemeEvent(Context context) {
        boolean prepare = isThemePrepare(context);
        boolean working = isThemeWorking(context);
        Logger.i(TAG, "handleThemeEvent prepare=" + prepare + " working=" + working + " perform=" + sPerformThemeEvent + " isDarkSup=" + isDarkSupport(this.mContext) + " isAutoMode=" + isAutoMode(this.mContext));
        if (prepare || (working && !sPerformThemeEvent)) {
            sPerformThemeEvent = true;
            Logger.i(TAG, "handleThemeEvent mLastLightIntensity=" + mLastLightIntensity + "isDarkActive=" + isDarkActive(0, this.mContext) + " isPsgDarkActive=" + isDarkActive(1, this.mContext) + " isICMDarkActive=" + isDarkActive(2, this.mContext));
            for (int i = 0; i < BrightnessSettings.DISPLAY_MAX; i++) {
                boolean isScreenAutoMode = isScreenAutoMode(i, this.mContext);
                Logger.i(TAG, "handleThemeEvent type=" + i + "isScreenAutoMode" + isScreenAutoMode);
                if (!isDarkSupport(this.mContext) && isScreenAutoMode && !sCustomEnabledArray[i] && isValidLightIntensity(mLastLightIntensity)) {
                    updateMode();
                    int autoBrightness = BrightnessSettings.getRealBrightnessByPercent(mLastLightIntensity);
                    if (autoBrightness != this.mScreenBrightnessArray[i].getBrightness()) {
                        if (i == 2) {
                            Logger.d(TAG, "handleThemeEvent change type= " + i + " Brightness" + mLastLightIntensity);
                            this.mScreenBrightnessArray[i].setBrightness(isNightMode(this.mContext) ? mLastLightIntensity : Math.min(mLastLightIntensity + 0, 100), false);
                        } else {
                            Logger.d(TAG, "handleThemeEvent change type= " + i + " Brightness" + mLastLightIntensity);
                            this.mScreenBrightnessArray[i].setBrightness(autoBrightness, false);
                        }
                    }
                }
            }
            this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.controller.brightness.-$$Lambda$BrightnessManager$vPCBrSvdPAyLiCotM9QUq68U3bE
                @Override // java.lang.Runnable
                public final void run() {
                    BrightnessManager.this.lambda$handleThemeEvent$0$BrightnessManager();
                }
            }, 500L);
        }
        if (!working) {
            sPerformThemeEvent = false;
        }
    }

    public synchronized void onIcmReady() {
        int icmMode = getMode(2, this.mContext);
        int to = this.mScreenBrightnessArray[2].getBrightness();
        int from = BrightnessSettings.getInt(this.mContext, "screen_brightness_2", 70);
        Logger.i(TAG, "onIcmReady mode=" + icmMode + " to=" + to + " from=" + from);
        if (to != from) {
            this.mScreenBrightnessArray[2].setBrightness(to, true);
        } else {
            onIcmBrightnessChanged(3);
        }
    }

    public synchronized void onIcmBrightnessChanged(int changeFrom) {
        StringBuffer buffer = new StringBuffer("");
        buffer.append("onIcmBrightnessChanged");
        buffer.append(" changeFrom=" + changeFrom);
        boolean z = true;
        if (changeFrom == 1) {
            int brightness = BrightnessSettings.getInt(this.mContext, "screen_brightness_2", 70);
            boolean isDarkMode = FO_HAS_INDEPENDENT_AUTOMODE ? isDarkMode(2, this.mContext) : isDarkMode(0, this.mContext);
            if (isDarkMode) {
                if (brightness == this.mLastIcmDarkBrightness) {
                    z = false;
                }
                sIcmDarkBrightnessChanged = z;
                if (sIcmDarkBrightnessChanged) {
                    setDarkModeAdjustType(4, this.mContext);
                }
            }
            buffer.append(" brightness=" + brightness);
            buffer.append(" isDarkMode=" + isDarkMode);
            buffer.append(" brightnessChanged=" + sIcmDarkBrightnessChanged);
            this.mScreenBrightnessArray[2].setBrightness(brightness, false);
            BrightnessCarManager.get(this.mContext).setIcmBrightness(brightness);
        } else if (changeFrom == 2) {
            int brightness2 = BrightnessCarManager.get(this.mContext).getIcmBrightness();
            BrightnessSettings.putInt(this.mContext, "screen_brightness_callback_2", brightness2);
            buffer.append(" brightness=" + brightness2);
        } else if (changeFrom == 3) {
            this.mScreenBrightnessArray[2].setBrightness(BrightnessSettings.getInt(this.mContext, "screen_brightness_2", 70), true);
        }
        Logger.i(TAG, buffer.toString());
    }

    public synchronized void onCMSBrightnessChanged() {
        int brightnessWithSource = BrightnessSettings.getInt(this.mContext, BrightnessSettings.KEY_CMS_BRIGHTNESS, 70);
        int brightnessSource = 65280 & brightnessWithSource;
        int brightness = brightnessWithSource & 255;
        Logger.i(TAG, "setCMSBrightness brightnessWithSource= " + brightnessWithSource + " brightnessSource=" + brightnessSource + " brightness=" + brightness);
        if (brightnessSource == 0) {
            BrightnessCarManager.get(this.mContext).setCMSBrightness(brightness);
        }
    }

    public synchronized void onScreenBrightnessChanged(int type) {
        int brightness;
        boolean z = true;
        BrightnessSettings.putInt(this.mContext, "screen_brightness_change_" + type, 1);
        if (type == 0) {
            brightness = BrightnessSettings.getInt(this.mContext, "screen_brightness", 179);
        } else {
            brightness = BrightnessSettings.getInt(this.mContext, "screen_brightness_" + type, 179);
        }
        boolean isDarkMode = isDarkMode(type, this.mContext);
        if (isDarkMode) {
            if (type == 0) {
                sScreenDarkBrightnessChanged = brightness != this.mLastScreenDarkBrightness;
                if (sScreenDarkBrightnessChanged) {
                    setDarkModeAdjustType(1, this.mContext);
                }
            } else if (type == 1) {
                if (brightness == this.mLastPassengerScreenBrightness) {
                    z = false;
                }
                sPassengerScreenBrightnessChanged = z;
                if (sPassengerScreenBrightnessChanged) {
                    setDarkModeAdjustType(2, this.mContext);
                }
            }
        }
        this.mScreenBrightnessArray[type].setBrightness(brightness, false);
        Logger.i(TAG, "onScreenBrightnessChanged type=" + type + " brightness=" + brightness + " isDarkMode=" + isDarkMode + " brightnessChanged=" + sScreenDarkBrightnessChanged + " passengerBrightnessChanged=" + sPassengerScreenBrightnessChanged);
    }

    public synchronized void onDarkBrightnessChanged() {
        boolean isDarkSupport = isDarkSupport(this.mContext);
        Logger.i(TAG, "onDarkBrightnessChanged isDarkSupport=" + isDarkSupport + "isDarkActive" + isDarkActive(0, this.mContext) + " isPsgDarkActive=" + isDarkActive(1, this.mContext) + " isICMDarkActive=" + isDarkActive(2, this.mContext) + "isAutoMode" + isAutoMode(this.mContext));
        if (!isDarkSupport) {
            for (int i = 0; i < BrightnessSettings.DISPLAY_MAX; i++) {
                boolean isAutoMode = isScreenAutoMode(i, this.mContext);
                if (isAutoMode) {
                    onLightIntensityChanged();
                    return;
                }
            }
            return;
        }
        lambda$handleThemeEvent$0$BrightnessManager();
    }

    public synchronized void onAutoModeChanged(boolean isMainAutoMode) {
        if (isMainAutoMode) {
            int autoMode = BrightnessSettings.getInt(this.mContext, BrightnessSettings.KEY_SCREEN_BRIGHTNESS_MODE, 1);
            if (autoMode == 0) {
                while (i < BrightnessSettings.DISPLAY_MAX) {
                    Context context = this.mContext;
                    BrightnessSettings.putInt(context, "screen_brightness_mode_" + i, autoMode);
                    i++;
                }
                Logger.d(TAG, "isMainAutoMode= " + autoMode);
            }
        } else if (!FO_HAS_INDEPENDENT_AUTOMODE) {
            i = isScreenAutoMode(0, this.mContext) ? 1 : 0;
            for (int i = 1; i < BrightnessSettings.DISPLAY_MAX; i++) {
                Context context2 = this.mContext;
                BrightnessSettings.putInt(context2, "screen_brightness_mode_" + i, i);
            }
            BrightnessSettings.putInt(this.mContext, BrightnessSettings.KEY_SCREEN_BRIGHTNESS_MODE, i);
            Logger.i(TAG, "NO_INDEPENDENT_AUTOMODE putInt screen_brightness_mode= " + i);
        } else {
            int autoMode2 = 1;
            while (true) {
                if (i >= BrightnessSettings.DISPLAY_MAX) {
                    break;
                } else if (isScreenAutoMode(i, this.mContext)) {
                    autoMode2 = 1;
                    Logger.d(TAG, "i= " + i + " screen_brightness_mode= 1");
                    break;
                } else {
                    autoMode2 = 0;
                    Logger.d(TAG, "i= " + i + " screen_brightness_mode= 0");
                    i++;
                }
            }
            BrightnessSettings.putInt(this.mContext, BrightnessSettings.KEY_SCREEN_BRIGHTNESS_MODE, autoMode2);
            Logger.i(TAG, "putInt screen_brightness_mode= " + autoMode2);
        }
    }

    public synchronized void onCMSAutoModeChanged(int changeFrom) {
        if (changeFrom == 1) {
            try {
                int isCMSAutoMode = BrightnessSettings.getInt(this.mContext, BrightnessSettings.KEY_CMS_BRIGHTNESS_MODE, 1);
                BrightnessCarManager.get(this.mContext).setCMSAutoMode(isCMSAutoMode);
                Logger.i(TAG, "CHANGED_FROM_SETTINGS cms_brightness_mode= " + isCMSAutoMode);
            } catch (Exception e) {
                Logger.i(TAG, "onCMSAutoModeChanged CHANGED_FROM_SETTINGS Exception");
            }
        } else if (changeFrom == 2) {
            try {
                int isCMSAutoMode2 = BrightnessCarManager.get(this.mContext).getCMSAutoMode();
                BrightnessSettings.putInt(this.mContext, BrightnessSettings.KEY_CMS_BRIGHTNESS_MODE, isCMSAutoMode2);
                Logger.i(TAG, "CHANGED_FROM_HARDWARE cms_brightness_mode= " + isCMSAutoMode2);
            } catch (Exception e2) {
                Logger.i(TAG, "onCMSAutoModeChanged CHANGED_FROM_HARDWARE Exception");
            }
        }
    }

    public synchronized void onThemeChanged() {
        boolean isNight = isNightMode(this.mContext);
        Logger.i(TAG, "onThemeChanged isNight=" + isNight);
        handleThemeEvent(this.mContext);
    }

    boolean isValidLightIntensity(int intensity) {
        return intensity >= 0 && intensity <= 100;
    }

    public synchronized void onLightIntensityChanged() {
        int intensity = BrightnessSettings.getInt(this.mContext, BrightnessSettings.KEY_LIGHT_INTENSITY, -1);
        Logger.i(TAG, "intensity =" + intensity);
        if (!isValidLightIntensity(intensity)) {
            Logger.i(TAG, "Invalid light intensity");
            return;
        }
        mLastLightIntensity = intensity;
        int[] currentIntensityArray = new int[BrightnessSettings.DISPLAY_MAX];
        for (int i = 0; i < BrightnessSettings.DISPLAY_MAX; i++) {
            if (i != 2 && i != 3) {
                currentIntensityArray[i] = BrightnessSettings.getPercentProgressByReal(this.mScreenBrightnessArray[i].getBrightness());
                Logger.d(TAG, "currentIntensityArray type=" + i + " currentIntensity=" + currentIntensityArray[i]);
                Logger.d(TAG, "onLightIntensityChanged type = " + i + " before= " + currentIntensityArray[i] + " now  =" + mLastLightIntensity + " sCustomEnabled " + sCustomEnabledArray[i]);
            }
            currentIntensityArray[i] = this.mScreenBrightnessArray[i].getBrightness();
            Logger.d(TAG, "currentIntensity =" + currentIntensityArray[i] + " type = " + i);
            Logger.d(TAG, "onLightIntensityChanged type = " + i + " before= " + currentIntensityArray[i] + " now  =" + mLastLightIntensity + " sCustomEnabled " + sCustomEnabledArray[i]);
        }
        for (int i2 = 0; i2 < BrightnessSettings.DISPLAY_MAX; i2++) {
            if (!sCustomEnabledArray[i2]) {
                boolean isAutoMode = isScreenAutoMode(i2, this.mContext);
                if (isAutoMode) {
                    Logger.d(TAG, "isAutoMode type=" + i2);
                    if (currentIntensityArray[i2] != mLastLightIntensity) {
                        int autoBrightness = BrightnessSettings.getRealBrightnessByPercent(mLastLightIntensity);
                        if (i2 == 2) {
                            if (1 == getMode(i2, this.mContext)) {
                                autoBrightness = Math.min(mLastLightIntensity + 0, 100);
                            } else {
                                autoBrightness = mLastLightIntensity;
                            }
                        } else if (i2 == 3) {
                            autoBrightness = mLastLightIntensity;
                        }
                        this.mScreenBrightnessArray[i2].setBrightness(autoBrightness, true);
                    }
                }
            }
        }
    }

    private void restoreBrightnessPlatformization() {
        int icmBrightness = BrightnessSettings.getInt(this.mContext, "icm_brightness", -1);
        int icmBrightnessDay = BrightnessSettings.getInt(this.mContext, "icm_brightness_day", -1);
        int icmBrightnessNight = BrightnessSettings.getInt(this.mContext, "icm_brightness_night", -1);
        int newBrightnessMode = BrightnessSettings.getInt(this.mContext, "screen_brightness_mode_0", -1);
        int brightnessMode = BrightnessSettings.getInt(this.mContext, BrightnessSettings.KEY_SCREEN_BRIGHTNESS_MODE, 1);
        if (newBrightnessMode == -1) {
            BrightnessSettings.putInt(this.mContext, "screen_brightness_mode_0", brightnessMode);
            Logger.i(TAG, "restoreBrightnessPlatformization brightnessMode= " + brightnessMode);
        }
        if (icmBrightness != -1) {
            BrightnessSettings.putInt(this.mContext, "screen_brightness_2", icmBrightness);
            BrightnessSettings.putInt(this.mContext, "icm_brightness", -1);
            Logger.i(TAG, "icmBrightness= " + icmBrightness);
        }
        if (icmBrightnessDay != -1) {
            BrightnessSettings.putInt(this.mContext, "screen_brightness_day_2", icmBrightnessDay);
            BrightnessSettings.putInt(this.mContext, "icm_brightness_day", -1);
            Logger.i(TAG, "icmBrightnessDay= " + icmBrightnessDay);
        }
        if (icmBrightnessNight != -1) {
            BrightnessSettings.putInt(this.mContext, BrightnessSettings.KEY_ICM_BRIGHTNESS_NIGHT, icmBrightnessNight);
            BrightnessSettings.putInt(this.mContext, "icm_brightness_night", -1);
            Logger.i(TAG, "icmBrightnessNight= " + icmBrightnessNight);
        }
    }

    private void restoreBrightness() {
        String appFlag = SystemProperties.get(RESTART_TAG, "");
        Logger.i(TAG, "restoreBrightness appFlag " + appFlag);
        if ("".equals(appFlag)) {
            SystemProperties.set(RESTART_TAG, "1");
            ContentResolver resolver = this.mContext.getContentResolver();
            Settings.Secure.putString(resolver, "panel_brightness", "");
            lambda$handleThemeEvent$0$BrightnessManager();
            return;
        }
        onPanelBrightnessChanged();
    }

    public synchronized void onPanelBrightnessChanged() {
        String[] brightnessList = parsePanelBrightnessList();
        for (String realPanelBrightness : brightnessList) {
            setPanelBrightness(realPanelBrightness);
        }
    }

    public synchronized void setPanelBrightness(String realPanelBrightness) {
        String[] panelBrightness = parsePanelBrightness(realPanelBrightness);
        if (panelBrightness == null || panelBrightness.length < 3) {
            Logger.i(TAG, "setPanelBrightness wrong param number");
            return;
        }
        try {
            int panelType = Integer.parseInt(panelBrightness[0]);
            int brightness = Integer.parseInt(panelBrightness[1]);
            boolean enabled = "1".equals(panelBrightness[2]);
            String source = panelBrightness[3];
            Logger.d(TAG, "setPanelBrightness panelType " + panelType + " brightness " + brightness + " enabled " + enabled + " source " + source);
            if (!this.mInitCompleted) {
                Logger.i(TAG, "setPanelBrightness system not ready");
                return;
            }
            if ((panelType & 1) != 0) {
                sCustomEnabledArray[2] = enabled;
                if (enabled) {
                    this.mScreenBrightnessArray[2].setMode(getMode(2, this.mContext));
                    this.mScreenBrightnessArray[2].setBrightness(brightness, true);
                } else {
                    this.mScreenBrightnessArray[2].setMode(getMode(2, this.mContext));
                    this.mScreenBrightnessArray[2].setBrightness(this.mScreenBrightnessArray[2].getBrightness(), true);
                    updateDarkBrightness();
                }
            }
            if ((panelType & 2) != 0) {
                sCustomEnabledArray[0] = enabled;
                if (enabled) {
                    this.mScreenBrightnessArray[0].setMode(getMode(0, this.mContext));
                    this.mScreenBrightnessArray[0].setBrightness(BrightnessSettings.getRealBrightnessByPercent(brightness), true);
                } else {
                    this.mScreenBrightnessArray[0].setMode(getMode(0, this.mContext));
                    this.mScreenBrightnessArray[0].setBrightness(this.mScreenBrightnessArray[0].getBrightness(), true);
                    updateDarkBrightness();
                }
            }
            if ((panelType & 4) != 0) {
                sCustomEnabledArray[1] = enabled;
                if (enabled) {
                    this.mScreenBrightnessArray[1].setMode(getMode(1, this.mContext));
                    this.mScreenBrightnessArray[1].setBrightness(BrightnessSettings.getRealBrightnessByPercent(brightness), true);
                } else {
                    this.mScreenBrightnessArray[1].setMode(getMode(1, this.mContext));
                    this.mScreenBrightnessArray[1].setBrightness(this.mScreenBrightnessArray[1].getBrightness(), true);
                    updateDarkBrightness();
                }
            }
        } catch (Exception e) {
            Logger.i(TAG, "setPanelBrightness wrong param ： " + realPanelBrightness);
        }
    }

    private String[] parsePanelBrightnessList() {
        ContentResolver resolver = this.mContext.getContentResolver();
        String panelBrightnessList = Settings.Secure.getString(resolver, "panel_brightness");
        String[] brightnessList = panelBrightnessList.split("@");
        return brightnessList;
    }

    private String[] parsePanelBrightness(String panelBrightness) {
        String[] brightness = panelBrightness.split("#");
        return brightness;
    }

    public synchronized void onSettingsChanged(boolean selfChange, Uri uri) {
        if (BrightnessSettings.URI_THEME_STATE.equals(uri)) {
            onThemeChanged();
        } else if (BrightnessSettings.URI_ICM_BRIGHTNESS.equals(uri)) {
            onIcmBrightnessChanged(1);
        } else if (BrightnessSettings.URI_SCREEN_BRIGHTNESS.equals(uri)) {
            onScreenBrightnessChanged(0);
        } else if (BrightnessSettings.URI_LIGHT_INTENSITY.equals(uri)) {
            onLightIntensityChanged();
        } else if (BrightnessSettings.URI_PANEL_BRIGHTNESS.equals(uri)) {
            onPanelBrightnessChanged();
        } else if (BrightnessSettings.URI_PASSENGER_BRIGHTNESS.equals(uri)) {
            onScreenBrightnessChanged(1);
        } else if (BrightnessSettings.URI_CMS_BRIGHTNESS.equals(uri)) {
            onCMSBrightnessChanged();
        } else if (BrightnessSettings.URI_SCREEN_BRIGHTNESS_MODE.equals(uri)) {
            onAutoModeChanged(true);
            onDarkBrightnessChanged();
        } else if (BrightnessSettings.URI_SCREEN_BRIGHTNESS_FOR_MODE.equals(uri)) {
            onAutoModeChanged(false);
            onDarkBrightnessChanged();
        } else if (BrightnessSettings.URI_PASSENGER_SCREEN_BRIGHTNESS_MODE.equals(uri)) {
            onAutoModeChanged(false);
            onDarkBrightnessChanged();
        } else if (BrightnessSettings.URI_ICM_BRIGHTNESS_MODE.equals(uri)) {
            onAutoModeChanged(false);
            onDarkBrightnessChanged();
        } else if (BrightnessSettings.URI_CMS_BRIGHTNESS_MODE.equals(uri)) {
            onAutoModeChanged(false);
            onCMSAutoModeChanged(1);
            onDarkBrightnessChanged();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        Logger.i(TAG, "onConfigurationChanged= " + newConfig);
        if (this.mInitCompleted && ThemeManager.isThemeChanged(newConfig)) {
            Logger.i(TAG, "onConfigurationChanged isNight=" + isNightMode(this.mContext));
        }
    }

    public static boolean isDarkMode(int type, Context context) {
        int[] screenMode = new int[BrightnessSettings.DISPLAY_MAX];
        if (type == 0) {
            if (FO_HAS_INDEPENDENT_AUTOMODE) {
                screenMode[type] = getMode(type, context);
                return screenMode[type] == 3;
            }
            screenMode[type + 2] = getMode(type + 2, context);
            screenMode[type] = getMode(type, context);
            return screenMode[type + 2] == 3 && screenMode[type] == 3;
        }
        screenMode[type] = getMode(type, context);
        return screenMode[type] == 3;
    }

    public static boolean isDarkActive(int type, Context context) {
        BrightnessCarManager bcm = BrightnessCarManager.get(context);
        boolean deviceActive = BrightnessSettings.BRIGHTNESS_DARK_ENV_SUPPORT ? bcm.isEnvironmentActive() : bcm.isLampActive();
        boolean isNightMode = isNightMode(context);
        boolean darkSupport = isDarkSupport(context);
        boolean isScreenAutoMode = isScreenAutoMode(type, context);
        boolean ret = darkSupport && isScreenAutoMode && !isNightMode && deviceActive;
        Logger.d(TAG, "isDarkActive ret=" + ret + " type=" + type + " deviceActive=" + deviceActive + " isNightMode=" + isNightMode + " darkSupport=" + darkSupport + " isScreenAutoMode=" + isScreenAutoMode);
        return ret;
    }

    public static boolean isDarkSupport(Context context) {
        return (BrightnessCarManager.get(context).hasCiuDevice() || CarModelsManager.getConfig().isXPUSupport()) ? false : true;
    }

    public static boolean isAutoMode(Context context) {
        return 1 == BrightnessSettings.getInt(context, BrightnessSettings.KEY_SCREEN_BRIGHTNESS_MODE, 0);
    }

    public static boolean isScreenAutoMode(int type, Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("screen_brightness_mode_");
        sb.append(type);
        return 1 == BrightnessSettings.getInt(context, sb.toString(), 0);
    }

    public static boolean isNightMode(Context context) {
        int mode = getTwilightMode(context);
        return mode == 2;
    }

    public static boolean isThemePrepare(Context context) {
        ContentResolver resolver = context.getContentResolver();
        int state = Settings.Secure.getInt(resolver, BrightnessSettings.KEY_THEME_STATE, 0);
        if (1 != state) {
            return false;
        }
        return true;
    }

    public static boolean isThemeWorking(Context context) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        return uim.isThemeWorking();
    }

    public static int getMode(int type, Context context) {
        int mode;
        boolean isNightMode = isNightMode(context);
        boolean isDarkActive = isDarkActive(type, context);
        boolean customMode = sCustomEnabledArray[type];
        if (customMode) {
            mode = 6;
        } else if (isDarkActive) {
            mode = 3;
        } else if (isNightMode) {
            mode = 2;
        } else {
            mode = 1;
        }
        Logger.d(TAG, "getMode type=" + type + " mode=" + mode + " isNightMode=" + isNightMode + " isDarkActive=" + isDarkActive);
        return mode;
    }

    public static int getTwilightMode(Context context) {
        UiModeManager um = (UiModeManager) context.getSystemService("uimode");
        return um.getDayNightAutoMode();
    }

    public static void setDarkModeState(Context context, boolean active) {
        BrightnessSettings.putInt(context, "screen_brightness_dark_state", active ? 1 : 0);
    }

    public static void setDarkModeAdjustType(int type, Context context) {
        int value = BrightnessSettings.getInt(context, "screen_brightness_dark_adj_type", 0);
        if (type == 0) {
            BrightnessSettings.putInt(context, "screen_brightness_dark_adj_type", type);
        } else if (type == 1) {
            if ((value & 1) != 1) {
                int newValue = value | 1;
                BrightnessSettings.putInt(context, "screen_brightness_dark_adj_type", newValue);
            }
        } else if (type == 2) {
            if ((value & 2) != 2) {
                int newValue2 = value | 2;
                BrightnessSettings.putInt(context, "screen_brightness_dark_adj_type", newValue2);
            }
        } else if (type == 4) {
            if ((value & 4) != 4) {
                int newValue3 = value | 4;
                BrightnessSettings.putInt(context, "screen_brightness_dark_adj_type", newValue3);
            }
        } else if (type == 5) {
            int passengerMask = value & (-2);
            BrightnessSettings.putInt(context, "screen_brightness_dark_adj_type", passengerMask);
        } else if (type == 6) {
            int ICMMask = value & (-3);
            BrightnessSettings.putInt(context, "screen_brightness_dark_adj_type", ICMMask);
        } else if (type == 7) {
            int ICMMask2 = value & (-5);
            BrightnessSettings.putInt(context, "screen_brightness_dark_adj_type", ICMMask2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public final class SharedDisplayListenerImpl extends SharedDisplayListener {
        private SharedDisplayListenerImpl() {
        }

        public void onActivityChanged(int screenId, String property) throws RemoteException {
            super.onActivityChanged(screenId, property);
            Logger.d(BrightnessManager.TAG, "onActivityChanged screenId=" + screenId + " property=" + property);
            exitActivityWindowBrightness(screenId);
        }

        public void onPositionChanged(String packageName, int event, int from, int to) throws RemoteException {
            super.onPositionChanged(packageName, event, from, to);
            Logger.d(BrightnessManager.TAG, "onPositionChanged packageName=" + packageName + " event=" + event + " from=" + from + " to=" + to);
            if (4 == event) {
                for (int i = 0; i < BrightnessSettings.DISPLAY_MAX; i++) {
                    if (i != 2) {
                        Context context = BrightnessManager.this.mContext;
                        BrightnessSettings.putInt(context, "screen_brightness_change_" + i, 0);
                    }
                }
                exitPositionChangedWindowBrightness(from);
            }
        }

        private void exitActivityWindowBrightness(int screenId) {
            boolean primary = WindowManager.isPrimaryId(screenId);
            if (primary) {
                BrightnessSettings.putInt(BrightnessManager.this.mContext, BrightnessSettings.KEY_SCREEN_BRIGHTNESS_CHANGE, 0);
                Settings.System.putInt(BrightnessManager.this.mContext.getContentResolver(), BrightnessSettings.KEY_SCREEN_WINDOW_BRIGHTNESS, -1);
                BrightnessManager.this.mPowerManager.exitXpWindowBrightness(0);
                return;
            }
            Context context = BrightnessManager.this.mContext;
            BrightnessSettings.putInt(context, "screen_brightness_change_" + screenId, 0);
            ContentResolver contentResolver = BrightnessManager.this.mContext.getContentResolver();
            Settings.System.putInt(contentResolver, "screen_window_brightness_" + screenId, -1);
            BrightnessManager.this.mPowerManager.exitXpWindowBrightness(screenId);
        }

        private void exitPositionChangedWindowBrightness(int sharedId) {
            boolean primary = WindowManager.isPrimaryId(sharedId);
            if (primary) {
                Settings.System.putInt(BrightnessManager.this.mContext.getContentResolver(), BrightnessSettings.KEY_SCREEN_WINDOW_BRIGHTNESS, -1);
                Logger.d(BrightnessManager.TAG, "IVI putint exitPosition ivi brightness = -1");
                BrightnessManager.this.mPowerManager.exitXpWindowBrightness(0);
                return;
            }
            ContentResolver contentResolver = BrightnessManager.this.mContext.getContentResolver();
            Settings.System.putInt(contentResolver, "screen_window_brightness_" + sharedId, -1);
            Logger.d(BrightnessManager.TAG, "exitPosition brightness = -1 type= " + sharedId);
            BrightnessManager.this.mPowerManager.exitXpWindowBrightness(sharedId);
        }
    }

    /* loaded from: classes24.dex */
    private class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
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
            BrightnessManager.this.onSettingsChanged(selfChange, uri);
        }

        public void startObserving() {
            ContentResolver resolver = BrightnessManager.this.mContext.getContentResolver();
            resolver.unregisterContentObserver(this);
            resolver.registerContentObserver(BrightnessSettings.URI_THEME_STATE, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_PANEL_BRIGHTNESS, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_WAITING_MODE, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_MEDITATION_MODE, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_ICM_BRIGHTNESS, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_SCREEN_BRIGHTNESS, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_PASSENGER_BRIGHTNESS, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_LIGHT_INTENSITY, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_SCREEN_BRIGHTNESS_MODE, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_SCREEN_BRIGHTNESS_FOR_MODE, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_ICM_BRIGHTNESS_MODE, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_PASSENGER_SCREEN_BRIGHTNESS_MODE, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_CMS_BRIGHTNESS_MODE, false, this, -1);
            resolver.registerContentObserver(BrightnessSettings.URI_CMS_BRIGHTNESS, false, this, -1);
        }

        public void stopObserving() {
            ContentResolver resolver = BrightnessManager.this.mContext.getContentResolver();
            resolver.unregisterContentObserver(this);
        }
    }
}
