package com.xiaopeng.systemui.utils;

import android.content.Context;
import android.text.TextUtils;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.biutil.BiLog;
import com.xiaopeng.biutil.BiLogFactory;
import com.xiaopeng.biutil.BiLogUploader;
import com.xiaopeng.biutil.BiLogUploaderFactory;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.infoflow.util.OrientationUtil;
/* loaded from: classes24.dex */
public class DataLogUtils {
    public static final String AC_CHARGING_ID = "B008";
    public static final String AUTO_HOLD_ID = "B011";
    public static final String BACK_BOX_ID = "B003";
    public static final String BRIGHTNESS_ID = "B004";
    public static final String CHARGING_ID = "B009";
    public static final int DATA_LOG_RESULT_FRONT_DEFROST_OFF = 2;
    public static final int DATA_LOG_RESULT_FRONT_DEFROST_ON = 1;
    public static final int DATA_LOG_RESULT_LOCK = 2;
    public static final int DATA_LOG_RESULT_UNLOCK = 1;
    public static final String DC_CHARGING_ID = "B009";
    public static final String DEPUTY_BRIGHTNESS_ID = "B001";
    public static final String DEPUTY_DRIVER_PAGE_ID = "P00005";
    public static final String DRIVER_MODE_ID = "B002";
    public static final int DROP_DOWN_MENU_OFF = 2;
    public static final int DROP_DOWN_MENU_ON = 1;
    public static final String ENERGY_RECOVERY_ID = "B007";
    public static final String INFO_APP_DATA_ID = "B001";
    public static final String INFO_APP_PAGE_ID = "P00005";
    public static final String INFO_BT_CALLING_DATA_ID = "B005";
    public static final String INFO_BT_DISCONNECT_DATA_ID = "B002";
    public static final String INFO_BT_PAGE_ID = "P00003";
    public static final String INFO_BT_PHONE_DATA_ID = "B003";
    private static final String INFO_MODULE = "infoflow";
    public static final String INFO_MUSIC_FM_DATA_ID = "B004";
    public static final String INFO_MUSIC_LOGIN_DATA_ID = "B003";
    public static final String INFO_MUSIC_MUSIC_DATA_ID = "B001";
    public static final String INFO_MUSIC_READING_DATA_ID = "B002";
    public static final String INFO_NAVI_CRUISE_DATA_ID = "B001";
    public static final String INFO_NAVI_EXPLORE_DATA_ID = "B003";
    public static final String INFO_NAVI_NAVI_DATA_ID = "B002";
    public static final String INFO_NAVI_PAGE_ID = "P00001";
    public static final String INFO_NOTIF_HAVE_DATA_ID = "B002";
    public static final String INFO_NOTIF_NO_DATA_ID = "B001";
    public static final String INFO_NOTIF_PAGE_ID = "P00004";
    public static final String INTELLIGENT_DEODORIZATION_ID = "B013";
    public static final String MAIN_DRIVER_PAGE_ID = "P00004";
    public static final String MEDITATION_MODE_ID = "B014";
    private static final String MODULE = "systemui";
    public static final String MUSIC_NEXT_ID = "B006";
    public static final String MUSIC_PLAY_PAUSE_ID = "B007";
    public static final String MUSIC_PRE_ID = "B005";
    public static final String MUSIC_SEEK_ID = "B008";
    public static final String PERSONAL_CENTER_ID = "B001";
    public static final String QM_BACKBOX_OFF_ID = "B006";
    public static final String QM_BACKMIRROR_OFF_ID = "B004";
    public static final String QM_BACKMIRROR_OPEN_ID = "B005";
    public static final String QM_DOWNHILL_ID = "B007";
    public static final String QM_IHB_ID = "B013";
    public static final String QM_SLEEP_SPACE_ID = "B016";
    public static final String QM_SOUNDADJUST_ID = "B010";
    public static final String QM_SPEECH_SETTING_ID = "B014";
    public static final String QM_WINDOW_OFF_ID = "B002";
    public static final String QM_WINDOW_OPEN_ID = "B011";
    public static final String QM_WINDOW_VENT_ID = "B003";
    public static final String QUICKMENU_PAGE_ID = "P10058";
    public static final String REAR_MIRROR_ID = "B005";
    public static final String SEAT_MEMORY_ID = "B006";
    public static final String SOUND_EFFECT_ID = "B016";
    public static final String SOUND_ID = "B004";
    public static final String STATUSBAR_PAGE_ID = "P10057";
    public static final String STS_CARCONTROL_LOCK_ON_ID = "B001";
    public static final String STS_CARDOOR_LOCK_OFF_ID = "B002";
    public static final String STS_DOWNLOAD_ID = "B004";
    public static final String STS_MICROPHONE_STOP_ID = "B003";
    private static final String TAG = "DataLogUtils";
    public static final String TEMPERATURE_ID = "B003";
    public static final String WIND_ID = "B002";
    public static final String WIPER_SPEED_ID = "B010";
    private static BiLogUploader sBiLogUploader;
    public static final String INFO_MUSIC_PAGE_ID = "P00002";
    public static String SYSTEMUI_PAGE_ID = INFO_MUSIC_PAGE_ID;
    public static String BACK_DEFROST_ID = "B019";
    public static String FRONT_DEFROST_ID = "B018";
    public static final String QM_MOVIE_SPACE_ID = "B017";
    public static String HVAC_ID = QM_MOVIE_SPACE_ID;
    public static String PARKING_ID = "B016";
    public static final String CLEAN_MODE_ID = "B015";
    public static String CAR_CONTROL_ID = CLEAN_MODE_ID;
    public static String CLOCK_ID = "B014";
    public static String WIFI_ID = "B013";
    public static final String RAPID_COOLING_ID = "B012";
    public static String G4_ID = RAPID_COOLING_ID;
    public static String ENERGY_ID = "B011";
    public static String UPGRADE_ID = "B010";
    public static String BLUETOOTH_ID = "B009";
    public static String DRIVER_ID = "B008";
    public static String APPLIST_ID = "B007";
    public static String SETTINGS_ID = "B006";
    public static String CAMERA_ID = "B005";
    public static String PHONE_ID = "B004";
    public static String MUSIC_ID = "B003";
    public static String NAVI_ID = "B002";
    public static String AI_ID = "B001";
    public static String PASSENGER_REGION_SLIDE_ID = null;
    public static String DRIVER_REGION_SLIDE_ID = null;
    public static String HVAC_ARROW_UP_ID = null;
    public static String HVAC_ARROW_DOWN_ID = null;
    public static String HVAC_ARROW_LONG_PRESS_ID = null;
    public static String BACK_TO_HOME_ID = null;
    public static String LOCK_ID = null;
    public static String TRUNK_ID = null;
    public static String PASSENGER_ID = null;
    public static String REVERSE_CAR_ID = null;

    public static void sendInfoDataLog(String pid, String bid) {
        sendDataLog(INFO_MODULE, pid, bid, null, null, null, null);
    }

    public static void sendInfoDataLog(String pid, String bid, String result) {
        sendDataLog(INFO_MODULE, pid, bid, null, result, null, null);
    }

    public static void sendInfoDataLog(String pid, String bid, String type, String result) {
        sendDataLog(INFO_MODULE, pid, bid, type, result, null, null);
    }

    public static void sendInfoDataLog(String pid, String bid, String type, String result, String source) {
        sendDataLog(INFO_MODULE, pid, bid, type, result, null, source);
    }

    public static void sendDataLog(String pid, String bid) {
        sendDataLog(MODULE, pid, bid, null, null, null, null);
    }

    public static void sendDataLog(String pid, String bid, int result) {
        sendDataLog(MODULE, pid, bid, null, String.valueOf(result), null, null);
    }

    public static void sendDataLog(String pid, String bid, int type, int result, float distance) {
        sendDataLog(MODULE, pid, bid, String.valueOf(type), String.valueOf(result), String.valueOf(distance), null);
    }

    public static void sendDataLog(String module, String pid, String bid, String type, String result, String count, String source) {
        if (TextUtils.isEmpty(bid)) {
            return;
        }
        Logger.i(TAG, "sendDataLog() called with: module = [" + module + "pid = [" + pid + "], bid = [" + bid + "], result = [" + result + "], count = [" + count + "], type = [" + type + NavigationBarInflaterView.SIZE_MOD_END);
        BiLog bilog = BiLogFactory.create(module, pid, bid);
        if (type != null) {
            bilog.push(VuiConstants.ELEMENT_TYPE, type);
        }
        if (result != null) {
            bilog.push("result", result);
        }
        if (count != null) {
            bilog.push("count", count);
        }
        if (source != null) {
            bilog.push("source", source);
        }
        if (sBiLogUploader == null) {
            sBiLogUploader = BiLogUploaderFactory.create();
        }
        sBiLogUploader.submit(bilog);
    }

    public static void init(Context context) {
        Logger.d(TAG, "init");
        if (OrientationUtil.isLandscapeScreen(context)) {
            initForE28();
        }
    }

    private static void initForE28() {
        SYSTEMUI_PAGE_ID = "P00006";
        BACK_DEFROST_ID = null;
        NAVI_ID = null;
        CAMERA_ID = null;
        DRIVER_ID = "B027";
        BLUETOOTH_ID = "B026";
        UPGRADE_ID = "B025";
        ENERGY_ID = "B024";
        G4_ID = "B023";
        WIFI_ID = "B022";
        CLOCK_ID = "B021";
        CAR_CONTROL_ID = "B020";
        PARKING_ID = "B019";
        HVAC_ID = "B018";
        PHONE_ID = QM_MOVIE_SPACE_ID;
        MUSIC_ID = "B016";
        FRONT_DEFROST_ID = CLEAN_MODE_ID;
        PASSENGER_REGION_SLIDE_ID = "B013";
        DRIVER_REGION_SLIDE_ID = RAPID_COOLING_ID;
        AI_ID = "B011";
        HVAC_ARROW_DOWN_ID = "B010";
        HVAC_ARROW_UP_ID = "B009";
        HVAC_ARROW_LONG_PRESS_ID = "B008";
        PASSENGER_ID = "B007";
        TRUNK_ID = "B006";
        LOCK_ID = "B005";
        BACK_TO_HOME_ID = "B004";
        APPLIST_ID = "B003";
        REVERSE_CAR_ID = "B002";
        SETTINGS_ID = "B001";
    }
}
