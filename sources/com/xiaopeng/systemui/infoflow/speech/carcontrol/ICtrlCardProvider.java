package com.xiaopeng.systemui.infoflow.speech.carcontrol;

import com.xiaopeng.speech.protocol.bean.CardValue;
import com.xiaopeng.systemui.infoflow.speech.ui.model.CtrlCardContent;
/* loaded from: classes24.dex */
public interface ICtrlCardProvider {
    CtrlCardContent getCardContent(int i, CardValue cardValue);

    float getCurrentModeValue();

    void passBack2SpeechValue(int i, float f);

    void register();

    void unRegister();
}
