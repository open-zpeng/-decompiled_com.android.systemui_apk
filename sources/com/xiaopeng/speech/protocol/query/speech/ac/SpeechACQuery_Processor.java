package com.xiaopeng.speech.protocol.query.speech.ac;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechACEvent;
/* loaded from: classes23.dex */
public class SpeechACQuery_Processor implements IQueryProcessor {
    private SpeechACQuery mTarget;

    public SpeechACQuery_Processor(SpeechACQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1445200882:
                if (event.equals(SpeechACEvent.AC_QUALITY_OUTSIDE_LV)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -1366962097:
                if (event.equals(SpeechACEvent.AC_PM25_VALUE)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -1250437706:
                if (event.equals(SpeechACEvent.AC_CIRCULATION_MODE)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1220798579:
                if (event.equals(SpeechACEvent.AC_QUALITY_OUTSIDE_STATE)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -1002248266:
                if (event.equals(SpeechACEvent.AC_AUTO_MODE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -611810135:
                if (event.equals(SpeechACEvent.AC_SEAT_BLOW_LV)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -6974638:
                if (event.equals(SpeechACEvent.AC_FRONT_DEFROST)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 204821931:
                if (event.equals(SpeechACEvent.AC_SEAT_HEAT_LV)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 262066763:
                if (event.equals(SpeechACEvent.AC_BACK_DEFROST_STATE)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 634597058:
                if (event.equals(SpeechACEvent.AC_AUTO_MODE_LV)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 641228857:
                if (event.equals(SpeechACEvent.AC_POWER)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 846916078:
                if (event.equals(SpeechACEvent.AC_PSN_SEAT_HEAT_LV)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case 1036740945:
                if (event.equals(SpeechACEvent.AC_PSN_TEMP)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1221934520:
                if (event.equals(SpeechACEvent.AC_INNER_TEMP)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 1268308502:
                if (event.equals(SpeechACEvent.AC_QUALITY_STATE)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 1300351747:
                if (event.equals(SpeechACEvent.AC_OUTTER_TEMP)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case 1399563517:
                if (event.equals("ac.wind.mode")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 1486533834:
                if (event.equals(SpeechACEvent.AC_SYNC_STATE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1626812139:
                if (event.equals(SpeechACEvent.AC_WIND_SPEED_LV)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1982026407:
                if (event.equals(SpeechACEvent.AC_BACK_MIRROR_HEAT_STATE)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 2074304014:
                if (event.equals(SpeechACEvent.AC_DRIVER_TEMP)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return Boolean.valueOf(this.mTarget.getHvacPowerMode(event, data));
            case 1:
                return Double.valueOf(this.mTarget.getHvacTempDriverValue(event, data));
            case 2:
                return Double.valueOf(this.mTarget.getHvacTempPsnValue(event, data));
            case 3:
                return Boolean.valueOf(this.mTarget.getHvacTempSyncMode(event, data));
            case 4:
                return Boolean.valueOf(this.mTarget.getHvacAutoMode(event, data));
            case 5:
                return Integer.valueOf(this.mTarget.getHvacAutoModeBlowLevel(event, data));
            case 6:
                return Integer.valueOf(this.mTarget.getHvacCirculationMode(event, data));
            case 7:
                return Boolean.valueOf(this.mTarget.getHvacFrontDefrostMode(event, data));
            case '\b':
                return Integer.valueOf(this.mTarget.getHvacWindBlowMode(event, data));
            case '\t':
                return Integer.valueOf(this.mTarget.getHvacWindSpeedLevel(event, data));
            case '\n':
                return Boolean.valueOf(this.mTarget.getHvacQualityPurgeMode(event, data));
            case 11:
                return Integer.valueOf(this.mTarget.getHvacQualityInnerPM25Value(event, data));
            case '\f':
                return Integer.valueOf(this.mTarget.getHvacQualityOutsideStatus(event, data));
            case '\r':
                return Integer.valueOf(this.mTarget.getHvacQualityOutsideLevel(event, data));
            case 14:
                return Boolean.valueOf(this.mTarget.getBCMBackDefrostMode(event, data));
            case 15:
                return Boolean.valueOf(this.mTarget.getBCMBackMirrorHeatMode(event, data));
            case 16:
                return Integer.valueOf(this.mTarget.getBCMSeatHeatLevel(event, data));
            case 17:
                return Integer.valueOf(this.mTarget.getBCMSeatBlowLevel(event, data));
            case 18:
                return Double.valueOf(this.mTarget.getHvacInnerTemp(event, data));
            case 19:
                return Integer.valueOf(this.mTarget.getPsnSeatHeatLv(event, data));
            case 20:
                return Float.valueOf(this.mTarget.getHvacOutterTemp(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechACEvent.AC_POWER, SpeechACEvent.AC_DRIVER_TEMP, SpeechACEvent.AC_PSN_TEMP, SpeechACEvent.AC_SYNC_STATE, SpeechACEvent.AC_AUTO_MODE, SpeechACEvent.AC_AUTO_MODE_LV, SpeechACEvent.AC_CIRCULATION_MODE, SpeechACEvent.AC_FRONT_DEFROST, "ac.wind.mode", SpeechACEvent.AC_WIND_SPEED_LV, SpeechACEvent.AC_QUALITY_STATE, SpeechACEvent.AC_PM25_VALUE, SpeechACEvent.AC_QUALITY_OUTSIDE_STATE, SpeechACEvent.AC_QUALITY_OUTSIDE_LV, SpeechACEvent.AC_BACK_DEFROST_STATE, SpeechACEvent.AC_BACK_MIRROR_HEAT_STATE, SpeechACEvent.AC_SEAT_HEAT_LV, SpeechACEvent.AC_SEAT_BLOW_LV, SpeechACEvent.AC_INNER_TEMP, SpeechACEvent.AC_PSN_SEAT_HEAT_LV, SpeechACEvent.AC_OUTTER_TEMP};
    }
}
