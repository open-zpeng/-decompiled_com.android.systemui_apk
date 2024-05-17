package com.xiaopeng.speech.protocol.query.carair;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface ICarAirCaller extends IQueryCaller {
    int getAcPanelStatus();

    int getAirLevel();

    int getCurrWindMode();

    int getDriWindDirectionMode();

    int getHeatMax();

    int getHeatMin();

    int getIntelligentPassengerStatus();

    int getOutSidePmLevelQuality();

    int getOutSidePmQuality();

    int getPsnWindDirectionMode();

    int getSeatBlowMax();

    int getSeatBlowMin();

    double getTempMax();

    double getTempMin();

    int getWindMax();

    int getWindMin();

    int[] getWindsStatus();

    boolean isFastFridge();

    boolean isFreshAir();

    boolean isSupportAutoOff();

    boolean isSupportDecimalValue();

    boolean isSupportDemistFoot();

    int isSupportMirrorHeat();

    int isSupportOutSidePm();

    boolean isSupportPm25();

    boolean isSupportPsnSeatHeat();

    boolean isSupportPurifier();

    boolean isSupportSeatBlow();

    boolean isSupportSeatHeat();

    boolean isSupportTempDual();

    default boolean isSupportRearSeatHeat() {
        return false;
    }

    default int getLeftRearSeatHeatLevel() {
        return 0;
    }

    default int getRightRearSeatHeatLevel() {
        return 0;
    }

    default boolean isAcSupportPerfume() {
        return false;
    }
}
