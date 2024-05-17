package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.ICallCardView;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.bean.CallInfo;
import com.xiaopeng.systemui.infoflow.message.presenter.CallCardPresenter;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class CallCardHolder extends BaseCardHolder implements ICallCardView {
    private static final String TAG = CallCardHolder.class.getSimpleName();
    private ImageView mAcceptActionImg;
    private View mActionLayout;
    private ImageView mContactAvatarImg;
    private TextView mContent;
    private ImageView mHangupActionImg;
    private ImageView mHangupActionSingleImg;
    private TextView mSwitchActionTv;
    private TextView mTitle;

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 4;
    }

    public CallCardHolder(View itemView) {
        super(itemView);
        this.mTitle = (TextView) itemView.findViewById(R.id.tv_title);
        this.mContent = (TextView) itemView.findViewById(R.id.tv_des);
        this.mContactAvatarImg = (ImageView) itemView.findViewById(R.id.img_call_avatar);
        this.mAcceptActionImg = (ImageView) itemView.findViewById(R.id.img_call_accept);
        this.mHangupActionImg = (ImageView) itemView.findViewById(R.id.img_call_hangup);
        this.mHangupActionSingleImg = (ImageView) itemView.findViewById(R.id.img_call_hangup_big);
        this.mActionLayout = itemView.findViewById(R.id.ll_incoming_action);
        this.mSwitchActionTv = (TextView) itemView.findViewById(R.id.tv_call_switch);
        this.mAcceptActionImg.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$4QIs9Nu_wMvi07lgfHeJyixju2M
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallCardHolder.this.onClick(view);
            }
        });
        this.mHangupActionImg.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$4QIs9Nu_wMvi07lgfHeJyixju2M
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallCardHolder.this.onClick(view);
            }
        });
        this.mHangupActionSingleImg.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$4QIs9Nu_wMvi07lgfHeJyixju2M
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallCardHolder.this.onClick(view);
            }
        });
        this.mSwitchActionTv.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$4QIs9Nu_wMvi07lgfHeJyixju2M
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallCardHolder.this.onClick(view);
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public void bindData(CardEntry cardEntry) {
        super.bindData(cardEntry);
        this.itemView.setTag("call");
        this.mTitle.setText(cardEntry.title);
        this.mContent.setText(cardEntry.content);
        updateIcon(cardEntry);
    }

    public void updateContent(String content) {
        this.mContent.setText(content);
    }

    private void updateIcon(CardEntry cardEntry) {
        Bitmap avatarBitmap = cardEntry.bigIcon;
        if (avatarBitmap == null) {
            Logger.d(TAG, "avatarBitmap is null");
            this.mContactAvatarImg.setImageResource(R.mipmap.ic_card_contact_default);
            return;
        }
        this.mContactAvatarImg.setImageBitmap(avatarBitmap);
    }

    @Override // com.xiaopeng.systemui.infoflow.ICallCardView
    public void updateActionImg(CallInfo callInfo) {
        if (callInfo != null) {
            int callStatus = callInfo.callStatus;
            int callType = callInfo.callType;
            if (callType == 2) {
                this.mSwitchActionTv.setVisibility(0);
                this.mHangupActionSingleImg.setVisibility(8);
                this.mActionLayout.setVisibility(8);
            } else if (callStatus == 2) {
                this.mActionLayout.setVisibility(0);
                this.mSwitchActionTv.setVisibility(8);
                this.mHangupActionSingleImg.setVisibility(8);
            } else if (callStatus == 3) {
                this.mHangupActionSingleImg.setVisibility(0);
                this.mHangupActionSingleImg.setAlpha(1.0f);
                this.mActionLayout.setVisibility(8);
                this.mSwitchActionTv.setVisibility(8);
            } else {
                this.mHangupActionSingleImg.setVisibility(0);
                this.mHangupActionSingleImg.setAlpha(0.7f);
                this.mActionLayout.setVisibility(8);
                this.mSwitchActionTv.setVisibility(8);
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        CallCardPresenter callCardPresenter = (CallCardPresenter) this.mInfoflowCardPresenter;
        switch (view.getId()) {
            case R.id.img_call_accept /* 2131362429 */:
                callCardPresenter.onCallAcceptClicked();
                return;
            case R.id.img_call_hangup /* 2131362431 */:
            case R.id.img_call_hangup_big /* 2131362432 */:
                callCardPresenter.onCallHangupClicked();
                return;
            case R.id.tv_call_switch /* 2131363282 */:
                callCardPresenter.onCallSwitchClicked();
                return;
            default:
                callCardPresenter.onCallActionClicked();
                return;
        }
    }
}
