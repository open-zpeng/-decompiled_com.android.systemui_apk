package com.xiaopeng.speech.protocol.query.speech.carstatus;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechCarStatusEvent;
/* loaded from: classes23.dex */
public class SpeechCarStatusQuery_Processor implements IQueryProcessor {
    private SpeechCarStatusQuery mTarget;

    public SpeechCarStatusQuery_Processor(SpeechCarStatusQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1466888283:
                if (event.equals(SpeechCarStatusEvent.STATUS_AC_TEMPDRIVERVALUE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -340187300:
                if (event.equals(SpeechCarStatusEvent.STATUS_AC_QUALITYPURGE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 44068250:
                if (event.equals(SpeechCarStatusEvent.STATUS_AC_CIRCULATIONMODE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 147719594:
                if (event.equals(SpeechCarStatusEvent.STATUS_AC_TEMPPSNVALUE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 513100557:
                if (event.equals(SpeechCarStatusEvent.STATUS_CUR_MODE)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 641487541:
                if (event.equals(SpeechCarStatusEvent.STATUS_AC_POWER)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1438013613:
                if (event.equals(SpeechCarStatusEvent.STATUS_AC_WINDBLOWMODE)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1962152437:
                if (event.equals(SpeechCarStatusEvent.STATUS_AC_WINDSPEEDLEVEL)) {
                    c = 5;
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
                return Integer.valueOf(this.mTarget.getCurrentMode(event, data));
            case 1:
                return Boolean.valueOf(this.mTarget.getAcPowerStatus(event, data));
            case 2:
                return Boolean.valueOf(this.mTarget.getAcQualityPurgeStatus(event, data));
            case 3:
                return Float.valueOf(this.mTarget.getAcTempDriverValue(event, data));
            case 4:
                return Float.valueOf(this.mTarget.getAcTempPsnValue(event, data));
            case 5:
                return Integer.valueOf(this.mTarget.getAcWindSpeedLevel(event, data));
            case 6:
                return Integer.valueOf(this.mTarget.getAcWindBlowMode(event, data));
            case 7:
                return Integer.valueOf(this.mTarget.getAcCirculationMode(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechCarStatusEvent.STATUS_CUR_MODE, SpeechCarStatusEvent.STATUS_AC_POWER, SpeechCarStatusEvent.STATUS_AC_QUALITYPURGE, SpeechCarStatusEvent.STATUS_AC_TEMPDRIVERVALUE, SpeechCarStatusEvent.STATUS_AC_TEMPPSNVALUE, SpeechCarStatusEvent.STATUS_AC_WINDSPEEDLEVEL, SpeechCarStatusEvent.STATUS_AC_WINDBLOWMODE, SpeechCarStatusEvent.STATUS_AC_CIRCULATIONMODE};
    }
}
