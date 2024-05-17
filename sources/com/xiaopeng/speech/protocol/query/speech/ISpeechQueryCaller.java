package com.xiaopeng.speech.protocol.query.speech;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface ISpeechQueryCaller extends IQueryCaller {
    String getCarPlatForm();

    int getCurrentMode();

    int getSoundLocation();

    boolean isAccountLogin();

    boolean isAppForeground(String str);

    boolean isEnableGlobalWakeup();

    default int getVuiSceneSwitchStatus() {
        return -1;
    }

    default int getFirstSpeechState() {
        return -1;
    }

    default int getCurrentTtsEngine() {
        return 1;
    }

    default boolean appIsInstalled(String packageName) {
        return false;
    }

    default boolean isUserExpressionOpened() {
        return false;
    }

    default boolean isNapaTop(int displayId) {
        return true;
    }

    default boolean isNaviTop(int display_location) {
        return false;
    }

    default boolean isScreenOn(int display_location) {
        return false;
    }

    default int getCfcVehicleLevel() {
        return -1;
    }

    default int ifSupportFastSpeech() {
        return 0;
    }

    default int ifSupportWaitAwake() {
        return 0;
    }

    default int ifSupportMulti() {
        return 0;
    }

    default boolean isSupportXSport() {
        return false;
    }
}
