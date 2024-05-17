package com.xiaopeng.systemui.infoflow.speech.ui.adapter;

import android.content.Context;
import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.node.phone.bean.PhoneBean;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechCardView;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
/* loaded from: classes24.dex */
public class PhoneListAdapter extends BaseRecyclerAdapter<PhoneBean> {
    public PhoneListAdapter(Context context) {
        super(context);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public void bindData(BaseRecyclerAdapter<PhoneBean>.BaseViewHolder holder, PhoneBean phoneBean, final int position) {
        AnimatedTextView sort = (AnimatedTextView) holder.getView(R.id.phone_card_sort);
        sort.setText((position + 1) + "");
        AnimatedTextView number = (AnimatedTextView) holder.getView(R.id.phone_card_number);
        number.setText(phoneBean.getNumber());
        AnimatedTextView name = (AnimatedTextView) holder.getView(R.id.phone_card_name);
        name.setText(phoneBean.getName());
        holder.getView(R.id.phone_card_dial_btn).setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.adapter.PhoneListAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PhoneListAdapter.this.sendSelectedEvent(position);
            }
        });
        SpeechCardView focusView = (SpeechCardView) holder.itemView;
        if (focusView != null) {
            focusView.setFocused(false);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter
    public int getItemLayoutId() {
        return R.layout.item_phone_list;
    }
}
