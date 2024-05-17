package com.xiaopeng.speech.protocol.query.speech.ac;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechACEvent;
/* loaded from: classes23.dex */
public class SpeechACQuery extends SpeechQuery<ISpeechACQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_POWER)
    public boolean getHvacPowerMode(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacPowerMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_DRIVER_TEMP)
    public double getHvacTempDriverValue(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacTempDriverValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_PSN_TEMP)
    public double getHvacTempPsnValue(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacTempPsnValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_SYNC_STATE)
    public boolean getHvacTempSyncMode(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacTempSyncMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_AUTO_MODE)
    public boolean getHvacAutoMode(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacAutoMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_AUTO_MODE_LV)
    public int getHvacAutoModeBlowLevel(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacAutoModeBlowLevel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_CIRCULATION_MODE)
    public int getHvacCirculationMode(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacCirculationMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_FRONT_DEFROST)
    public boolean getHvacFrontDefrostMode(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacFrontDefrostMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = "ac.wind.mode")
    public int getHvacWindBlowMode(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacWindBlowMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_WIND_SPEED_LV)
    public int getHvacWindSpeedLevel(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacWindSpeedLevel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_QUALITY_STATE)
    public boolean getHvacQualityPurgeMode(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacQualityPurgeMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_PM25_VALUE)
    public int getHvacQualityInnerPM25Value(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacQualityInnerPM25Value();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_QUALITY_OUTSIDE_STATE)
    public int getHvacQualityOutsideStatus(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacQualityOutsideStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_QUALITY_OUTSIDE_LV)
    public int getHvacQualityOutsideLevel(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacQualityOutsideLevel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_BACK_DEFROST_STATE)
    public boolean getBCMBackDefrostMode(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getBCMBackDefrostMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_BACK_MIRROR_HEAT_STATE)
    public boolean getBCMBackMirrorHeatMode(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getBCMBackMirrorHeatMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_SEAT_HEAT_LV)
    public int getBCMSeatHeatLevel(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getBCMSeatHeatLevel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_SEAT_BLOW_LV)
    public int getBCMSeatBlowLevel(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getBCMSeatBlowLevel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_INNER_TEMP)
    public double getHvacInnerTemp(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacInnerTemp();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_PSN_SEAT_HEAT_LV)
    public int getPsnSeatHeatLv(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getPsnSeatHeatLv();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechACEvent.AC_OUTTER_TEMP)
    public float getHvacOutterTemp(String event, String data) {
        return ((ISpeechACQueryCaller) this.mQueryCaller).getHvacOutterTemp();
    }
}
