package com.xiaopeng.speech.protocol.node.custom;

import com.xiaopeng.speech.INodeListener;
/* loaded from: classes23.dex */
public interface WakeupTestListener extends INodeListener {
    void onVADBeginSpeech();

    void onVADEndSpeech();

    void onWakeupFailed(String str);

    void onWakeupSucced(String str);
}
