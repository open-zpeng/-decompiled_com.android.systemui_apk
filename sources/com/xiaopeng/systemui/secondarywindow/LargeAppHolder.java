package com.xiaopeng.systemui.secondarywindow;

import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.xui.widget.XImageView;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes24.dex */
public class LargeAppHolder extends BaseAppHolder {
    private XTextView mDesc;
    private XImageView mSmallAppIcon;

    public LargeAppHolder(View itemView) {
        super(itemView);
        this.mSmallAppIcon = (XImageView) itemView.findViewById(R.id.small_app_icon);
        this.mDesc = (XTextView) itemView.findViewById(R.id.tv_desc);
    }

    @Override // com.xiaopeng.systemui.secondarywindow.BaseAppHolder
    public void bindData(BaseAppInfo appInfo) {
        super.bindData(appInfo);
        LargeAppInfo largeAppInfo = (LargeAppInfo) appInfo;
        this.mSmallAppIcon.setImageResource(largeAppInfo.getSmallIconId());
        this.mDesc.setText(largeAppInfo.getDesc());
    }
}
