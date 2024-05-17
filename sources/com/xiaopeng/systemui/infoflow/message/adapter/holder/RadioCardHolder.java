package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.view.View;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
/* loaded from: classes24.dex */
public class RadioCardHolder extends BaseCardHolder {
    private TextView mCurrentFm;
    private TextView mTitle;

    public RadioCardHolder(View itemView) {
        super(itemView);
        this.mTitle = (TextView) itemView.findViewById(R.id.tv_title);
        this.mCurrentFm = (TextView) itemView.findViewById(R.id.tv_current_fm);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public void bindData(CardEntry cardEntry) {
        super.bindData(cardEntry);
        this.mTitle.setText(cardEntry.title);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View v) {
    }
}
