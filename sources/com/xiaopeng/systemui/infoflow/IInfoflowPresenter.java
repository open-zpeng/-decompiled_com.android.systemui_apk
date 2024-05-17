package com.xiaopeng.systemui.infoflow;
/* loaded from: classes24.dex */
public interface IInfoflowPresenter {
    void onCardFocusedChanged(int i);

    void sendScrollEvent(int i);

    void setInfoflowStatus(int i);

    void stopDialog();
}
