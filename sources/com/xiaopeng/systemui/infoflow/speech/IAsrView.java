package com.xiaopeng.systemui.infoflow.speech;

import android.view.ViewGroup;
/* loaded from: classes24.dex */
public interface IAsrView {
    void clearAnimation();

    void fadeIn(String str);

    void fadeOut();

    void hideVuiRecommendView();

    void setAsrLoc(int i);

    void setAsrStatus(int i);

    void setAsrText(String str);

    void setRecommendData(String str);

    void showAsr(boolean z);

    void showAsrAnimation(int i);

    void showVuiRecommendView(boolean z);

    void updateAsrContainer(ViewGroup viewGroup);
}
