package com.xiaopeng.speech.protocol.query.speech.radio;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechRadioEvent;
/* loaded from: classes23.dex */
public class SpeechRadioQuery extends SpeechQuery<ISpeechRadioQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadioEvent.RADIO_DSP)
    public String getAudioDspStatus(String event, String data) {
        return ((ISpeechRadioQueryCaller) this.mQueryCaller).getAudioDspStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadioEvent.RADIO_STATE)
    public String getRadioStatus(String event, String data) {
        return ((ISpeechRadioQueryCaller) this.mQueryCaller).getRadioStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadioEvent.RADIO_VOLUME_FOCUS)
    public int getRadioVolumeAutoFocus(String event, String data) {
        return ((ISpeechRadioQueryCaller) this.mQueryCaller).getRadioVolumeAutoFocus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadioEvent.RADIO_FREQUENCY)
    public int[] getRadioFrequency(String event, String data) {
        return ((ISpeechRadioQueryCaller) this.mQueryCaller).getRadioFrequency();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadioEvent.RADIO_MODE)
    public int[] getAudioMode(String event, String data) {
        return ((ISpeechRadioQueryCaller) this.mQueryCaller).getAudioMode();
    }
}
