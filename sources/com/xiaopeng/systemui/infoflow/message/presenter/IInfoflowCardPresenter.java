package com.xiaopeng.systemui.infoflow.message.presenter;

import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
/* loaded from: classes24.dex */
public interface IInfoflowCardPresenter {
    void bindData(BaseCardHolder baseCardHolder, CardEntry cardEntry);

    void bindData(CardEntry cardEntry);

    void destroyCard();

    String getCardPackageName();

    void onActionClicked(int i);

    void onCardClicked();

    void onCardCloseClicked();

    void onViewAttachedToWindow();

    void onViewDetachedFromWindow();
}
