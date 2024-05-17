package com.xiaopeng.systemui.infoflow.speech.carcontrol;

import android.content.Context;
import com.xiaopeng.systemui.infoflow.speech.ui.model.CtrlCardContent;
/* loaded from: classes24.dex */
public interface ILogicCtrlView {
    Context getContext();

    void playBgAnimation(int i, float f, float f2);

    void refreshBgView(int i);

    void refreshView(int i);

    void setPresenter(ICtrlCardPresenter iCtrlCardPresenter);

    void setViewStub(int i, CtrlCardContent ctrlCardContent);

    void stopProgressAnim();

    void updateCtrlCardContent(CtrlCardContent ctrlCardContent);

    void updateNumTv(String str);

    void updateOffText(int i);

    void updateProgress(int i);

    void updateRadialValue(int i);

    void updateSeatIcon(int i);
}
