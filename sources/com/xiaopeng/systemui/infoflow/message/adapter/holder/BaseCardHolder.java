package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.systemui.R;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.helper.FocusHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter;
import com.xiaopeng.systemui.infoflow.message.view.CardView;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
/* loaded from: classes24.dex */
public abstract class BaseCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ActivityController.OnActivityCallback {
    private static final String TAG = "BaseCardHolder";
    private ImageView mAppOpenIndicator;
    protected OnCardClickedListener mCardClickedListener;
    protected Context mContext;
    protected CardEntry mData;
    protected IInfoflowCardPresenter mInfoflowCardPresenter;
    protected View mItemView;

    /* loaded from: classes24.dex */
    public interface OnCardClickedListener {
        void onCardClicked(CardEntry cardEntry);
    }

    public int getCardType() {
        return -1;
    }

    public BaseCardHolder(View itemView) {
        super(itemView);
        this.mItemView = itemView;
        ensureFocusStatus(itemView);
        this.mContext = itemView.getContext();
        this.mAppOpenIndicator = (ImageView) itemView.findViewById(R.id.img_open_indicator);
        itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder.1
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View view) {
                Logger.d(BaseCardHolder.TAG, "onViewAttachedToWindow : " + view + " " + BaseCardHolder.this.mInfoflowCardPresenter);
                if (BaseCardHolder.this.mInfoflowCardPresenter != null) {
                    BaseCardHolder.this.mInfoflowCardPresenter.onViewAttachedToWindow();
                }
                ActivityController.getInstance(BaseCardHolder.this.mContext).addActivityCallback(BaseCardHolder.this);
                BaseCardHolder.this.checkAppOpened();
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View view) {
                Logger.d(BaseCardHolder.TAG, "onViewDetachedFromWindow : " + view + " " + BaseCardHolder.this.mInfoflowCardPresenter);
                if (BaseCardHolder.this.mInfoflowCardPresenter != null) {
                    BaseCardHolder.this.mInfoflowCardPresenter.onViewDetachedFromWindow();
                }
                ActivityController.getInstance(BaseCardHolder.this.mContext).removeActivityCallback(BaseCardHolder.this);
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$IwReruYirhSGMumYev6__8nb6n0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BaseCardHolder.this.onClick(view);
            }
        });
        this.mInfoflowCardPresenter = PresenterCenter.getInstance().getCardPresenter(getCardType());
    }

    public void setFocused(final boolean focused) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder.2
            @Override // java.lang.Runnable
            public void run() {
                ((CardView) BaseCardHolder.this.itemView).setFocused(focused);
            }
        });
    }

    private void ensureFocusStatus(View view) {
        CardView cardView = null;
        if (view instanceof CardView) {
            cardView = (CardView) view;
            Logger.d(TAG, "reset the focus status");
            cardView.setFocused(false);
        }
        CardEntry cardEntry = this.mData;
        if (cardEntry != null && TextUtils.isEmpty(cardEntry.key) && FocusHelper.getFocusItemKey().equals(this.mData.key) && cardView != null) {
            Logger.d(TAG, "set cardView focus");
            cardView.setFocused(true);
        }
    }

    public void bindData(CardEntry cardEntry) {
        this.mData = cardEntry;
        this.itemView.setTag(R.id.tag_key, Integer.valueOf(cardEntry.type));
        IInfoflowCardPresenter iInfoflowCardPresenter = this.mInfoflowCardPresenter;
        if (iInfoflowCardPresenter != null) {
            iInfoflowCardPresenter.bindData(this, cardEntry);
        }
    }

    public void dismissCardSelf(CardEntry data) {
        OnCardClickedListener onCardClickedListener = this.mCardClickedListener;
        if (onCardClickedListener != null) {
            onCardClickedListener.onCardClicked(data);
        }
    }

    public void setCardClickedListener(OnCardClickedListener listener) {
        this.mCardClickedListener = listener;
    }

    @Override // com.xiaopeng.systemui.controller.ActivityController.OnActivityCallback
    public void onActivityChanged(ActivityController.ComponentInfo ci) {
        if (ci != null && ci.getName() != null && ci.isActivityChange()) {
            String pkgName = ci.getName().getPackageName();
            boolean open = getCardPackageName().equals(pkgName);
            onApplicationOpen(open);
        }
    }

    public void onClick(View view) {
        this.mInfoflowCardPresenter.onCardClicked();
    }

    public String getCardPackageName() {
        IInfoflowCardPresenter iInfoflowCardPresenter = this.mInfoflowCardPresenter;
        if (iInfoflowCardPresenter != null) {
            return iInfoflowCardPresenter.getCardPackageName();
        }
        return "";
    }

    public void onApplicationOpen(boolean open) {
        updateAppOpenIndicator(open);
        enableItemView(open);
    }

    private void updateAppOpenIndicator(boolean appOpen) {
        ImageView imageView = this.mAppOpenIndicator;
        if (imageView != null) {
            imageView.setVisibility(appOpen ? 0 : 4);
        }
    }

    public void enableItemView(boolean appOpen) {
        View view = this.mItemView;
        if (view != null) {
            view.setEnabled(!appOpen);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkAppOpened() {
        boolean open = BaseCardPresenter.isAppForeground(getCardPackageName());
        onApplicationOpen(open);
    }
}
