package com.xiaopeng.systemui.infoflow.speech.core.speech.behavior;

import com.xiaopeng.speech.protocol.bean.CardValue;
import com.xiaopeng.speech.protocol.node.carac.bean.ChangeValue;
/* loaded from: classes24.dex */
public interface ICarListener {
    void onIcmBrightnessChanged(int i);

    void onScreenBrightnessChanged(int i);

    void onTempDriverDown(ChangeValue changeValue);

    void onTempDriverUp(ChangeValue changeValue);

    void showCtrlCard(int i, CardValue cardValue);
}
