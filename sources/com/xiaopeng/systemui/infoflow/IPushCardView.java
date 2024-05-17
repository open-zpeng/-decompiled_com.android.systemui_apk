package com.xiaopeng.systemui.infoflow;

import com.xiaopeng.systemui.infoflow.message.presenter.PushBean;
/* loaded from: classes24.dex */
public interface IPushCardView {
    void hidePushCardNotTip();

    void setCardFocused(int i, boolean z);

    void setPushCardContent(PushBean pushBean);
}
