package com.xiaopeng.speech.protocol.node.speech.carcontrol;

import com.xiaopeng.speech.INodeListener;
/* loaded from: classes23.dex */
public interface SpeechCarControlListener extends INodeListener {
    void onCloseDriveMileIncrease();

    void onOpenDriveMileIncrease();

    void onOpenLoudspeaker();
}
