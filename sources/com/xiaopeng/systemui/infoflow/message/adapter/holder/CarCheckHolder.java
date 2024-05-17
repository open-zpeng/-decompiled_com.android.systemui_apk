package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
/* loaded from: classes24.dex */
public class CarCheckHolder extends BaseCardHolder {
    private static final String TAG = CarCheckHolder.class.getSimpleName();
    private TextView mBtn;
    private View mBtnNotTipClose;
    private ViewGroup mBtnNotTipDown;
    private ViewGroup mBtnNotTipUp;
    private TextView mContentTv;
    private ViewGroup mLyNotTip;
    private TextView mSenseTv;
    private ImageView mSensesImg;
    private TextView mTitleTv;

    public CarCheckHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.-$$Lambda$abaEiAACO0vIi26mN6VaReU-LI8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CarCheckHolder.this.onClick(view);
            }
        });
        this.mSenseTv = (TextView) itemView.findViewById(R.id.tv_senses);
        this.mSensesImg = (ImageView) itemView.findViewById(R.id.iv_ai_senses);
        this.mTitleTv = (TextView) itemView.findViewById(R.id.tv_ai_title_1);
        this.mContentTv = (TextView) itemView.findViewById(R.id.tv_ai_title_2);
        this.mBtn = (TextView) itemView.findViewById(R.id.btn_ai_ok);
        this.mBtnNotTipDown = (ViewGroup) itemView.findViewById(R.id.btn_ai_not_tip_down);
        this.mBtnNotTipUp = (ViewGroup) itemView.findViewById(R.id.btn_ai_not_tip_up);
        this.mLyNotTip = (ViewGroup) itemView.findViewById(R.id.ly_ai_not_tip);
        this.mBtnNotTipClose = itemView.findViewById(R.id.tv_not_tip_close);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 19;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public void bindData(CardEntry cardEntry) {
        super.bindData(cardEntry);
        this.mTitleTv.setText(cardEntry.title);
        this.mContentTv.setText(cardEntry.content);
        this.mSenseTv.setText(this.mContext.getString(R.string.car_check_sense_title));
        this.mSensesImg.setImageResource(R.drawable.ic_push_senses_system);
        this.mBtn.setVisibility(0);
        this.mBtn.setText(R.string.car_checked_call_text);
        this.mBtn.setOnClickListener(this);
        this.mBtnNotTipDown.setVisibility(0);
        this.mBtnNotTipDown.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.CarCheckHolder.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CarCheckHolder.this.mBtnNotTipUp.setVisibility(0);
                CarCheckHolder.this.mBtnNotTipDown.setVisibility(8);
                CarCheckHolder.this.mLyNotTip.setVisibility(0);
            }
        });
        this.mLyNotTip.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.CarCheckHolder.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                CarCheckHolder.this.hideNotTip();
            }
        });
        this.mBtnNotTipClose.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.CarCheckHolder.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                CarCheckHolder.this.hideNotTip();
                CarCheckHolder carCheckHolder = CarCheckHolder.this;
                carCheckHolder.dismissCardSelf(carCheckHolder.mData);
                CarCheckHolder.this.mInfoflowCardPresenter.onCardCloseClicked();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideNotTip() {
        this.mBtnNotTipUp.setVisibility(8);
        this.mBtnNotTipDown.setVisibility(0);
        this.mLyNotTip.setVisibility(8);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        this.mInfoflowCardPresenter.onCardClicked();
        dismissCardSelf(this.mData);
    }
}
