package com.xiaopeng.speech.protocol.query.speech.radio;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechRadioEvent;
/* loaded from: classes23.dex */
public class SpeechRadioQuery_Processor implements IQueryProcessor {
    private SpeechRadioQuery mTarget;

    public SpeechRadioQuery_Processor(SpeechRadioQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1465934626:
                if (event.equals(SpeechRadioEvent.RADIO_STATE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -971371186:
                if (event.equals(SpeechRadioEvent.RADIO_DSP)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -47471690:
                if (event.equals(SpeechRadioEvent.RADIO_MODE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 928420887:
                if (event.equals(SpeechRadioEvent.RADIO_VOLUME_FOCUS)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1543073577:
                if (event.equals(SpeechRadioEvent.RADIO_FREQUENCY)) {
                    c = 3;
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
                        if (c == 4) {
                            return this.mTarget.getAudioMode(event, data);
                        }
                        return null;
                    }
                    return this.mTarget.getRadioFrequency(event, data);
                }
                return Integer.valueOf(this.mTarget.getRadioVolumeAutoFocus(event, data));
            }
            return this.mTarget.getRadioStatus(event, data);
        }
        return this.mTarget.getAudioDspStatus(event, data);
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechRadioEvent.RADIO_DSP, SpeechRadioEvent.RADIO_STATE, SpeechRadioEvent.RADIO_VOLUME_FOCUS, SpeechRadioEvent.RADIO_FREQUENCY, SpeechRadioEvent.RADIO_MODE};
    }
}
