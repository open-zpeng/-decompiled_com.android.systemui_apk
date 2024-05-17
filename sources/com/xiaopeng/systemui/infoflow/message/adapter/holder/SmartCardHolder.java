package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
/* loaded from: classes24.dex */
public class SmartCardHolder extends BaseCardHolder {
    private static final String TAG = SmartCardHolder.class.getSimpleName();
    private ImageView mCardBg;
    private TextView mContent;
    private TextView mTitle;
    private TextView mTypeTitle;

    public SmartCardHolder(View itemView) {
        super(itemView);
        this.mTypeTitle = (TextView) itemView.findViewById(R.id.tv_smart_senses);
        this.mTitle = (TextView) itemView.findViewById(R.id.tv_smart_title_1);
        this.mContent = (TextView) itemView.findViewById(R.id.tv_smart_title_2);
        this.mCardBg = (ImageView) itemView.findViewById(R.id.bg_smart_push);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 28;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public void bindData(CardEntry cardEntry) {
        super.bindData(cardEntry);
        updateView();
    }

    private void updateView() {
        this.mTypeTitle.setText(this.mData.title);
        this.mTitle.setText(this.mData.title);
        this.mContent.setText(this.mData.content);
        this.mCardBg.setImageResource(R.mipmap.bg_smart_light);
    }
}
