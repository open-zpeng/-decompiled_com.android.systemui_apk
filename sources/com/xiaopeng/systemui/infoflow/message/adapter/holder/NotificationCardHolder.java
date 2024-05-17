package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.INotificationCardView;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.NotificationList;
/* loaded from: classes24.dex */
public class NotificationCardHolder extends BaseCardHolder implements INotificationCardView {
    private static final String TAG = NotificationCardHolder.class.getSimpleName();
    private ImageView mAppIcon;
    private TextView mContent;
    private ImageView mMoreImageView;
    private TextView mSubDes;
    private TextView mTitle;

    public NotificationCardHolder(View itemView) {
        super(itemView);
        itemView.setTag("notification");
        this.mTitle = (TextView) itemView.findViewById(R.id.tv_title);
        this.mContent = (TextView) itemView.findViewById(R.id.tv_des);
        this.mSubDes = (TextView) itemView.findViewById(R.id.tv_sub_des);
        this.mAppIcon = (ImageView) itemView.findViewById(R.id.img_app_icon);
        this.mMoreImageView = (ImageView) itemView.findViewById(R.id.img_more);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public void bindData(CardEntry cardEntry) {
        super.bindData(cardEntry);
        this.mData = cardEntry;
        this.mTitle.setText(cardEntry.title);
        this.mContent.setText(cardEntry.content);
        refreshMoreImageView();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 1;
    }

    private void refreshMoreImageView() {
        int notificationCount = NotificationList.getInstance().getSize();
        if (notificationCount > 1) {
            this.mMoreImageView.setVisibility(0);
        } else {
            this.mMoreImageView.setVisibility(8);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.INotificationCardView
    public void setNotificationCardStatus(boolean hasNotification, String currentTime) {
        if (!hasNotification) {
            this.mAppIcon.setImageResource(R.mipmap.ic_card_notification_empty);
            this.mContent.setText(currentTime);
            return;
        }
        this.mAppIcon.setImageResource(R.mipmap.ic_card_notification);
    }

    @Override // com.xiaopeng.systemui.infoflow.INotificationCardView
    public void setNotificationCardSubDesc(String desc) {
        this.mSubDes.setText(desc);
    }
}
