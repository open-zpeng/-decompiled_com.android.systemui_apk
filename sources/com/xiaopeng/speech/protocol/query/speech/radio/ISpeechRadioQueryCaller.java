package com.xiaopeng.speech.protocol.query.speech.radio;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface ISpeechRadioQueryCaller extends IQueryCaller {
    String getAudioDspStatus();

    int[] getAudioMode();

    int[] getRadioFrequency();

    String getRadioStatus();

    int getRadioVolumeAutoFocus();
}
