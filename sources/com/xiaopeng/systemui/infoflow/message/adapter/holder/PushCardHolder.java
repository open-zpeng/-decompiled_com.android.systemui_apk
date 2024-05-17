package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xiaopeng.systemui.infoflow.IPushCardView;
import com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.IPushCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.PushBean;
import com.xiaopeng.systemui.infoflow.message.view.CardView;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
/* loaded from: classes24.dex */
public class PushCardHolder extends BaseCardHolder implements IPushCardView {
    public static final String PUSH_CARD_KEY = "com.xiaopeng.aiassistant.push_card_entry_key";
    private static final String TAG = "PushCardHolder";
    private ImageView mBgView;
    private TextView mBtnMsg;
    private View mBtnNotTip7Day;
    private View mBtnNotTipAllDay;
    private View mBtnNotTipClose;
    private View mBtnNotTipCloseLine;
    private View mBtnNotTipDayLine;
    private ViewGroup mBtnNotTipDown;
    private ViewGroup mBtnNotTipUp;
    private ImageView mIvSences;
    private ViewGroup mLyNotTip;
    private IPushCardPresenter mPushCardPresenter;
    private TextView mTvContent;
    private TextView mTvTitle;
    private TextView mTvTitleOnly;

    public PushCardHolder(View itemView) {
        super(itemView);
        Log.i(TAG, TAG);
        this.mPushCardPresenter = (IPushCardPresenter) this.mInfoflowCardPresenter;
        this.mBgView = (ImageView) itemView.findViewById(R.id.bg_ai_push);
        this.mIvSences = (ImageView) itemView.findViewById(R.id.iv_ai_senses);
        this.mTvTitle = (TextView) itemView.findViewById(R.id.tv_ai_title_1);
        this.mTvTitleOnly = (TextView) itemView.findViewById(R.id.tv_ai_title_3);
        this.mTvContent = (TextView) itemView.findViewById(R.id.tv_ai_title_2);
        this.mBtnMsg = (TextView) itemView.findViewById(R.id.btn_ai_ok);
        this.mBtnNotTipDown = (ViewGroup) itemView.findViewById(R.id.btn_ai_not_tip_down);
        this.mBtnNotTipUp = (ViewGroup) itemView.findViewById(R.id.btn_ai_not_tip_up);
        this.mLyNotTip = (ViewGroup) itemView.findViewById(R.id.ly_ai_not_tip);
        this.mBtnNotTipClose = itemView.findViewById(R.id.tv_not_tip_close);
        this.mBtnNotTipCloseLine = itemView.findViewById(R.id.v_not_tip_close_line);
        this.mBtnNotTipDayLine = itemView.findViewById(R.id.v_not_tip_day_line);
        this.mBtnNotTip7Day = itemView.findViewById(R.id.tv_not_tip_seven_day);
        this.mBtnNotTipAllDay = itemView.findViewById(R.id.tv_not_tip_all_day);
        this.mBtnNotTipDown.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.PushCardHolder.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PushCardHolder.this.mBtnNotTipUp.setVisibility(0);
                PushCardHolder.this.mBtnNotTipDown.setVisibility(8);
                PushCardHolder.this.mLyNotTip.setVisibility(0);
                PushCardHolder.this.mPushCardPresenter.onNotTipDownClicked();
            }
        });
        this.mBtnNotTipClose.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.PushCardHolder.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PushCardHolder.this.mPushCardPresenter.onNotTipCloseClicked();
            }
        });
        this.mBtnNotTip7Day.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.PushCardHolder.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PushCardHolder.this.mPushCardPresenter.onNotTip7DayClicked();
            }
        });
        this.mBtnNotTipAllDay.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.PushCardHolder.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PushCardHolder.this.mPushCardPresenter.onNotTipAllDayClicked();
            }
        });
        this.mLyNotTip.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.PushCardHolder.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PushCardHolder.this.mPushCardPresenter.onNotTipClicked();
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 17;
    }

    @Override // com.xiaopeng.systemui.infoflow.IPushCardView
    public void hidePushCardNotTip() {
        this.mBtnNotTipUp.setVisibility(8);
        this.mBtnNotTipDown.setVisibility(0);
        this.mLyNotTip.setVisibility(8);
    }

    private void loadBackground(String bgUrl, ImageView bgView) {
        RequestOptions options = new RequestOptions().centerCrop().priority(Priority.HIGH).override(530, 305).diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(this.mContext).asBitmap().load(bgUrl).apply(options).into(bgView);
    }

    @Override // com.xiaopeng.systemui.infoflow.IPushCardView
    public void setPushCardContent(final PushBean pushBean) {
        int ivSencesIc;
        if (pushBean != null) {
            loadBackground(pushBean.bgUrl, this.mBgView);
            if (pushBean.level != 0) {
                ivSencesIc = PushResourcesHelper.getIcBySenses(pushBean.sences + pushBean.level);
            } else {
                ivSencesIc = PushResourcesHelper.getIcBySenses(pushBean.sences);
            }
            if (ivSencesIc != -1) {
                this.mIvSences.setImageResource(ivSencesIc);
            } else {
                PushResourcesHelper.loadDrawable(pushBean.picPath, new PushResourcesHelper.LoadDrawableCallback() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.PushCardHolder.6
                    @Override // com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper.LoadDrawableCallback
                    public void onFinish(Drawable drawable) {
                        if (drawable != null) {
                            PushCardHolder.this.mIvSences.setImageDrawable(drawable);
                        } else {
                            PushResourcesHelper.getImageFromAssetsFile(pushBean.picPath, new PushResourcesHelper.LoadDrawableCallback() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.PushCardHolder.6.1
                                @Override // com.xiaopeng.systemui.infoflow.aissistant.push.PushResourcesHelper.LoadDrawableCallback
                                public void onFinish(Drawable drawable2) {
                                    PushCardHolder.this.mIvSences.setImageDrawable(drawable2);
                                }
                            });
                        }
                    }
                });
            }
            if (TextUtils.isEmpty(pushBean.content)) {
                this.mTvTitleOnly.setVisibility(0);
                this.mTvTitle.setVisibility(8);
                this.mTvContent.setVisibility(8);
                this.mTvTitleOnly.setText(pushBean.title);
            } else {
                this.mTvTitleOnly.setVisibility(8);
                this.mTvTitle.setVisibility(0);
                this.mTvContent.setVisibility(0);
                this.mTvTitle.setText(pushBean.title);
                this.mTvContent.setText(pushBean.content);
            }
            if (!TextUtils.isEmpty(pushBean.btnText)) {
                this.mBtnMsg.setText(pushBean.btnText);
                this.mBtnMsg.setVisibility(0);
                this.mBtnMsg.setOnClickListener(this);
                this.mItemView.setOnClickListener(this);
                this.mItemView.setClickable(true);
            } else {
                this.mBtnMsg.setVisibility(8);
                this.mItemView.setOnClickListener(null);
                this.mItemView.setClickable(false);
            }
            this.mBtnNotTipDown.setVisibility(0);
            if (!pushBean.isSystem) {
                this.mBtnNotTip7Day.setVisibility(0);
                this.mBtnNotTipAllDay.setVisibility(0);
                this.mBtnNotTipCloseLine.setVisibility(0);
                this.mBtnNotTipDayLine.setVisibility(0);
            } else {
                this.mBtnNotTip7Day.setVisibility(8);
                this.mBtnNotTipAllDay.setVisibility(8);
                this.mBtnNotTipCloseLine.setVisibility(8);
                this.mBtnNotTipDayLine.setVisibility(8);
            }
            this.mBtnNotTipUp.setVisibility(8);
            this.mLyNotTip.setVisibility(8);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.IPushCardView
    public void setCardFocused(int cardType, final boolean focused) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.PushCardHolder.7
            @Override // java.lang.Runnable
            public void run() {
                ((CardView) PushCardHolder.this.itemView).setFocused(focused);
            }
        });
    }
}
