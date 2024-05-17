package com.xiaopeng.speech.protocol.query.charge;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface IChargeCaller extends IQueryCaller {
    int getChargeStatus();

    int getGuiPageOpenState(String str);

    int getLimitsAdjustMax();

    int getLimitsAdjustMin();

    int getRestartStatus();

    int getStartStatus();

    int getStopStatus();

    boolean isChargeReadyPage();

    boolean isHasChargingOrder();

    boolean isSupportLimitsAdjust();

    boolean isSupportOpenPort();

    boolean isSupportSmartMode();

    default boolean hasCarRefrigerator() {
        return false;
    }

    default boolean hasSunRoof() {
        return false;
    }

    default int getTrunkPowerStatus() {
        return -1;
    }

    default int getMinDischargeLimit() {
        return 0;
    }

    default int getMaxDischargeLimit() {
        return 0;
    }

    default int getTrunkPowerStatusForOpen() {
        return -1;
    }

    default boolean isSupportEnduranceMode(int mode) {
        return true;
    }
}
