package com.xiaopeng.speech.protocol.query.speech.radar;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechRadarEvent;
/* loaded from: classes23.dex */
public class SpeechRadarQuery_Processor implements IQueryProcessor {
    private SpeechRadarQuery mTarget;

    public SpeechRadarQuery_Processor(SpeechRadarQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2090752681:
                if (event.equals(SpeechRadarEvent.RADAR_FRONT_LV)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -636748384:
                if (event.equals(SpeechRadarEvent.RADAR_TAIL_DATA)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -3443915:
                if (event.equals(SpeechRadarEvent.RADAR_FRONT_FAULT)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 781459712:
                if (event.equals(SpeechRadarEvent.RADAR_TAIL_LV)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 831113271:
                if (event.equals(SpeechRadarEvent.RADAR_FRONT_DATA)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1737485036:
                if (event.equals(SpeechRadarEvent.RADAR_TAIL_FAULT)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c != 0) {
            if (c != 1) {
                if (c != 2) {
                    if (c != 3) {
                        if (c != 4) {
                            if (c == 5) {
                                return this.mTarget.getTailRadarFaultSt(event, data);
                            }
                            return null;
                        }
                        return this.mTarget.getFrontRadarFaultSt(event, data);
                    }
                    return this.mTarget.getTailRadarLevel(event, data);
                }
                return this.mTarget.getFrontRadarLevel(event, data);
            }
            return this.mTarget.getTailRadarData(event, data);
        }
        return this.mTarget.getFrontRadarData(event, data);
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechRadarEvent.RADAR_FRONT_DATA, SpeechRadarEvent.RADAR_TAIL_DATA, SpeechRadarEvent.RADAR_FRONT_LV, SpeechRadarEvent.RADAR_TAIL_LV, SpeechRadarEvent.RADAR_FRONT_FAULT, SpeechRadarEvent.RADAR_TAIL_FAULT};
    }
}
