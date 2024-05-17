package com.xiaopeng.systemui.infoflow.checking;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import androidx.slice.core.SliceHints;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.carmanager.impl.VcuController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.common.event.EventCenter;
import com.xiaopeng.systemui.infoflow.dao.InfoFlowConfigDao;
import com.xiaopeng.systemui.infoflow.egg.EasterEggHelper;
import com.xiaopeng.systemui.infoflow.message.event.EasterEggStateEventPackage;
import com.xiaopeng.systemui.infoflow.message.event.EventType;
import com.xiaopeng.systemui.infoflow.message.listener.XNotificationListener;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.utils.SystemUIMediatorUtil;
/* loaded from: classes24.dex */
public class CarCheckHelper {
    private static final int ALLOW_AUTO_SELF_CHECK_OFF = 0;
    private static final int ALLOW_AUTO_SELF_CHECK_ON = 1;
    private static final String KEY_ALLOW_AUTO_SELF_CHECK = "XP_Allow_Self_Check";
    private static final String KEY_LAST_SLEEP_TIME = "last_sleep_time";
    private static final String PRE_FILE_NAME = "base_config";
    private static final String TAG = "CarCheckHelper";
    private static boolean isElapsedTimeTest;
    private static boolean isInSpeechMode;
    private static boolean isOOBERunning;
    private static long CHECKED_INTERVAL = 900000;
    private static String PACKAGE_OOBE = PackageHelper.PACKAGE_OOBE;
    private static String KEY_SETTINGS_SYSTEM_CAR_CHECKED = "info_car_check_enable";
    private static String KEY_SETTINGS_SYSTEM_AI_WELCOME = "info_ai_welcome_enable";
    private static boolean isScreenOn = true;
    private static boolean isHaveChecked = true;
    private static boolean isEasterEggShow = false;
    private static boolean isAllowCarCheck = false;
    private static boolean isUiReady = false;
    private static boolean isNotifyNeedCarCheckMode = false;

    public static void doInfoCarCheck() {
        Log.i(TAG, "doInfoCarCheck-isHaveChecked -" + isHaveChecked + " , isAllowCarCheck: " + isAllowCarCheck);
        if (isHaveChecked) {
            return;
        }
        if (isCarCheck()) {
            if (EasterEggHelper.hasEasterEggShow()) {
                showEastEgg(true);
            } else if (isAllowCarCheck) {
                Log.i(TAG, "doInfoCarCheck-isIsUiReady -" + isIsUiReady());
                if (!isIsUiReady()) {
                    needCarCheckMode();
                    return;
                } else {
                    notifyCarCheck(true);
                    notifyAiWelcome(true);
                }
            } else {
                notifyAiWelcome(true);
            }
            isHaveChecked = true;
            return;
        }
        showEastEgg(false);
        notifyCarCheck(false);
        notifyAiWelcome(false);
    }

    public static void stopCarCheck() {
        Log.i(TAG, "stopCarCheck");
        notifyCarCheck(false);
        showEastEgg(false);
    }

    public static void notifyCarCheck(boolean enable) {
        Logger.w(TAG, "notifyCarCheck" + enable);
        ContentResolver resolver = SystemUIApplication.getContext().getContentResolver();
        Settings.System.putInt(resolver, KEY_SETTINGS_SYSTEM_CAR_CHECKED, enable ? 1 : 0);
        if (!enable) {
            removeCarCheckedCard();
        }
    }

    public static void notifyAiWelcome(boolean enable) {
        Logger.w(TAG, "notifyAiWelcome" + enable);
        ContentResolver resolver = SystemUIApplication.getContext().getContentResolver();
        Settings.System.putInt(resolver, KEY_SETTINGS_SYSTEM_AI_WELCOME, enable ? 1 : 0);
    }

    public static void showEastEgg(boolean show) {
        if (isEasterEggShow != show) {
            isEasterEggShow = show;
            EventCenter.instance().raiseEvent(new EasterEggStateEventPackage(EventType.EASTER_EGG_STATE, null, show));
        }
    }

    private static boolean isInfoCarCheckEnable() {
        if (isAllowCarCheck) {
            boolean enable = InfoFlowConfigDao.getInstance().getConfig().infoCarCheck;
            Logger.d(TAG, "isInfoCarCheckEnable--" + enable);
            return enable;
        }
        return false;
    }

    public static void initAllowCarCheckValue() {
        isAllowCarCheck = Settings.Global.getInt(SystemUIApplication.getContext().getContentResolver(), KEY_ALLOW_AUTO_SELF_CHECK, 1) == 1;
    }

    private static boolean isCarCheck() {
        boolean isGearParking = isGearParking();
        boolean isElapsedTimeSatisfied = isElapsedTimeSatisfied();
        Log.i(TAG, String.format("isCarCheckSupport--%s isScreenOn--%s isInSpeechMode--%s  isOOBERunning--%s isGearParking %s isElapsedTimeSatisfied %s isIsUiReady %s", Boolean.valueOf(CarModelsManager.getFeature().isCarCheckSupport()), Boolean.valueOf(isScreenOn), Boolean.valueOf(isInSpeechMode), Boolean.valueOf(isOOBERunning), Boolean.valueOf(isGearParking), Boolean.valueOf(isElapsedTimeSatisfied), Boolean.valueOf(isIsUiReady())));
        return CarModelsManager.getFeature().isCarCheckSupport() && isScreenOn && !isInSpeechMode && !isOOBERunning && isElapsedTimeSatisfied && isGearParking;
    }

    private static boolean isElapsedTimeSatisfied() {
        long currentTime = SystemClock.elapsedRealtime();
        SharedPreferences sharedPreferences = SystemUIApplication.getContext().getSharedPreferences(PRE_FILE_NAME, 0);
        long lastSleepTime = sharedPreferences.getLong(KEY_LAST_SLEEP_TIME, 0L);
        int carCheckIntervalMinutes = InfoFlowConfigDao.getInstance().getConfig().carCheckInterval;
        Logger.d(TAG, "carCheckIntervalMinutes--" + carCheckIntervalMinutes);
        long carCheckInterval = (long) (carCheckIntervalMinutes * 60 * 1000);
        boolean intervalSatisfied = currentTime - lastSleepTime >= carCheckInterval;
        Log.i(TAG, "currentTime--" + currentTime + "&lastSleepTime--" + lastSleepTime + "&interval--" + ((currentTime - lastSleepTime) / 1000) + "&isElapsedTimeSatisfied--" + intervalSatisfied);
        return isElapsedTimeTest || intervalSatisfied;
    }

    public static void setElapsedTimeSatisfiedTest(boolean elapsedTimeTest) {
        Logger.d(TAG, "setElapsedTimeSatisfiedTest--" + elapsedTimeTest);
        isElapsedTimeTest = elapsedTimeTest;
    }

    private static boolean checkScreenOn() {
        PowerManager powerManager = (PowerManager) SystemUIApplication.getContext().getSystemService("power");
        return powerManager.isInteractive();
    }

    public static void notifypeechMode(boolean enable) {
        isInSpeechMode = enable;
        doInfoCarCheck();
        if (isInSpeechMode) {
            showEastEgg(false);
            removeCarCheckedCard();
        }
    }

    public static void notifyScreenOn(boolean screenOn) {
        isScreenOn = screenOn;
        Log.i(TAG, "notifyScreenOn--" + screenOn);
        if (!screenOn) {
            isHaveChecked = false;
            saveSleepTime();
            notifyCarCheck(false);
            notifyAiWelcome(false);
            isNotifyNeedCarCheckMode = false;
            isUiReady = false;
        } else if (isElapsedTimeSatisfied()) {
            initAllowCarCheckValue();
            doInfoCarCheck();
        } else {
            notifyAiWelcome(false);
            isHaveChecked = true;
        }
    }

    public static void enterOOBE() {
        isOOBERunning = true;
        Logger.d(TAG, "enterOOBE");
        doInfoCarCheck();
    }

    public static void exitOOBE() {
        isOOBERunning = false;
        Logger.d(TAG, "exitOOBE");
        doInfoCarCheck();
    }

    public static void saveSleepTime() {
        long currenTime = SystemClock.elapsedRealtime();
        SharedPreferences sharedPreferences = SystemUIApplication.getContext().getSharedPreferences(PRE_FILE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LAST_SLEEP_TIME, currenTime);
        editor.commit();
    }

    public static void resetSleepTime() {
        Logger.d(TAG, "resetSleepTime");
        SharedPreferences sharedPreferences = SystemUIApplication.getContext().getSharedPreferences(PRE_FILE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LAST_SLEEP_TIME, 0L);
        editor.commit();
    }

    public static boolean isOOBERuning() {
        return isOOBERunning;
    }

    public static boolean isGearParking() {
        boolean result = true;
        VcuController vcuController = (VcuController) CarClientWrapper.getInstance().getController(CarClientWrapper.XP_VCU_SERVICE);
        if (vcuController != null) {
            int value = vcuController.getGearLevel();
            Log.i(TAG, "current gear level -- " + value);
            result = value == 4;
        }
        Log.i(TAG, "isGearParking -- " + result);
        return result;
    }

    public static String getCurrentRunningPackageName() {
        ActivityManager am = (ActivityManager) SystemUIApplication.getContext().getSystemService(SliceHints.HINT_ACTIVITY);
        ComponentName componentName = null;
        if (am != null && am.getRunningTasks(1) != null && am.getRunningTasks(1).get(0) != null) {
            componentName = am.getRunningTasks(1).get(0).topActivity;
        }
        if (componentName == null) {
            return null;
        }
        return componentName.getPackageName();
    }

    public static void notifyUIReady() {
        Log.i(TAG, "notifyUIReady");
        isUiReady = true;
        doInfoCarCheck();
    }

    private static void needCarCheckMode() {
        Log.i(TAG, "needCarCheckMode !!!!!!!!!!!!!!!  isNotifyNeedCarCheckMode :" + isNotifyNeedCarCheckMode);
        if (isNotifyNeedCarCheckMode) {
            return;
        }
        SystemUIMediatorUtil.systemUIMediatorApiRouterCall("needCarCheckMode");
        isNotifyNeedCarCheckMode = true;
    }

    private static boolean isIsUiReady() {
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            return isUiReady;
        }
        return true;
    }

    private static void removeCarCheckedCard() {
        Log.d(TAG, "removeCarCheckedCard");
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.checking.CarCheckHelper.1
            @Override // java.lang.Runnable
            public void run() {
                XNotificationListener.getInstance(SystemUIApplication.getContext()).removeCardEntry(19);
            }
        });
        XNotificationListener.getInstance(SystemUIApplication.getContext()).exitCarCheckMode();
    }
}
