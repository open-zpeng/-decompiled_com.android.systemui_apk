package com.xiaopeng.systemui.infoflow.message.helper;

import android.app.Notification;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import androidx.mediarouter.media.SystemMediaRouteProvider;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.bean.AppCardReasonInfo;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class CardHelper {
    private static final String KEY_INCOMING_CALL = "xp.key.phone.flag";
    private static final String RANKER_GROUP_KEY_WORD = "ranker_group";
    private static final String TAG = CardHelper.class.getSimpleName();
    private static boolean mShowSpeciallNotification = SystemProperties.getBoolean("persist.sys.show.special.notify", false);
    public static final String[] BLACK_APP_PACKAGE_LIST = {"com.google.android.car.defaultstoragemonitoringcompanionapp", SystemMediaRouteProvider.PACKAGE_NAME, "android.car.usb.handler", "com.android.systemui", "com.android.car", "com.android.car.carlauncher", "com.android.system", "com.android.packageinstaller", "com.android.managedprovisioning", "com.android.defcontainer", "com.android.provision", "com.android.settings", PackageHelper.PACKAGE_ACCOUNT_CENTER, "com.xiaopeng.appstore", "com.xiaopeng.appinstaller", "com.xiaopeng.btphone", "com.xiaopeng.ntcenter", VuiConstants.CARCONTROL, VuiConstants.MAP_APPNMAE, "com.xiaopeng.autopilot", VuiConstants.MUSIC, "com.xiaopeng.aiassistant", "com.sinovoice.hcicloudinputvehicle"};
    private static final String[] FLASH_APP_ACTIVITY_LIST = {"com.xiaopeng.car.settings.ui.activity.FallbackHome"};
    private static final String[] BLACK_APP_ACTIVITY_LIST = {"com.xiaopeng.carcontrol.view.HvacActivity", "com.xiaopeng.carcontrol.view.MainActivity", "com.xiaopeng.appstore.ui.MainActivity", "com.xiaopeng.car.settings.ui.activity.FloatingActivity", "com.xiaopeng.energycenter.view.MainActivity", "com.xiaopeng.car.settings.ui.activity.FallbackHome"};

    public static boolean shouldAddCardEntry(CardEntry cardEntry, Notification notification) {
        if (cardEntry.type == 8) {
            String extraData = cardEntry.extraData;
            if (!TextUtils.isEmpty(extraData)) {
                AppCardReasonInfo cardReasonInfo = (AppCardReasonInfo) GsonUtil.fromJson(extraData, (Class<Object>) AppCardReasonInfo.class);
                String str = TAG;
                Logger.d(str, "add card entry" + String.format("lastPackageName %s & lastActivityName %s & currentPackageName %s & currentActivityName %s", cardReasonInfo.lastPackageName, cardReasonInfo.lastActivityName, cardReasonInfo.currentPackageName, cardReasonInfo.currentActivityName));
                if (inBlackPackageList(cardReasonInfo.lastPackageName) || inFlashActivityList(cardReasonInfo.lastActivityName) || inBlackActivityList(cardReasonInfo.currentActivityName)) {
                    Logger.d(TAG, "not add ----");
                    return false;
                }
            }
        }
        return cardEntry.type != 1;
    }

    public static boolean isNotInMessageCenterNotification(Notification notification) {
        boolean specialNotification = true;
        if (notification == null) {
            return true;
        }
        if (mShowSpeciallNotification) {
            return false;
        }
        if ((notification.flags & 32) != 32 && (notification.flags & 2) != 2 && (notification.flags & 1024) != 1024 && (notification.flags & 64) != 64) {
            specialNotification = false;
        }
        Logger.d(TAG, "specialNotification:" + specialNotification);
        return specialNotification;
    }

    private static boolean inBlackPackageList(String packageName) {
        String[] strArr;
        for (String pkgName : BLACK_APP_PACKAGE_LIST) {
            if (pkgName.equals(packageName)) {
                Logger.d(TAG, "inBlackPackageList");
                return true;
            }
        }
        return false;
    }

    private static boolean inFlashActivityList(String activityName) {
        String[] strArr;
        for (String actName : FLASH_APP_ACTIVITY_LIST) {
            if (actName.equals(activityName)) {
                Logger.d(TAG, "inFlashActivityList");
                return true;
            }
        }
        return false;
    }

    private static boolean inBlackActivityList(String activityName) {
        String[] strArr;
        for (String actName : BLACK_APP_ACTIVITY_LIST) {
            if (actName.equals(activityName)) {
                Logger.d(TAG, "inBlackActivityList");
                return true;
            }
        }
        return false;
    }

    public static boolean shouldRemoveCardEntry(CardEntry cardEntry) {
        if (cardEntry.type == 8) {
            String extraData = cardEntry.extraData;
            if (!TextUtils.isEmpty(extraData)) {
                AppCardReasonInfo cardReasonInfo = (AppCardReasonInfo) GsonUtil.fromJson(extraData, (Class<Object>) AppCardReasonInfo.class);
                String str = TAG;
                Logger.d(str, "remove card entry" + String.format("lastPackageName %s & lastActivityName %s & currentPackageName %s & currentActivityName %s", cardReasonInfo.lastPackageName, cardReasonInfo.lastActivityName, cardReasonInfo.currentPackageName, cardReasonInfo.currentActivityName));
                if (inBlackPackageList(cardReasonInfo.lastPackageName) || inBlackActivityList(cardReasonInfo.currentActivityName)) {
                    Logger.d(TAG, "not remove ----");
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isCarHasException(CardEntry cardEntry) {
        String extraData = cardEntry.extraData;
        String str = TAG;
        Logger.w(str, "car checked extraData--" + extraData);
        return !TextUtils.isEmpty(extraData) && extraData.contains(SystemUIApplication.getContext().getString(R.string.car_checked_exception_normal));
    }

    public static void saveIncomingCallStatus(boolean incomingCall) {
        String str = TAG;
        Log.i(str, "saveIncomingCallStatus: " + incomingCall);
        SystemProperties.set(KEY_INCOMING_CALL, incomingCall ? "1" : "0");
    }
}
