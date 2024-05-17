package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.android.systemui.R;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class AppCardHolder extends BaseCardHolder {
    private static final String TAG = AppCardHolder.class.getSimpleName();
    private String activityName;
    private ImageView mAppImg;
    private TextView mContent;
    private TextView mTitle;
    private ImageView mTypeImg;
    private TextView mTypeTitle;
    private String packageName;

    public AppCardHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.mTypeImg = (ImageView) itemView.findViewById(R.id.img_type);
        this.mTypeTitle = (TextView) itemView.findViewById(R.id.tv_type);
        this.mTitle = (TextView) itemView.findViewById(R.id.tv_title);
        this.mContent = (TextView) itemView.findViewById(R.id.tv_des);
        this.mAppImg = (ImageView) itemView.findViewById(R.id.img_app_icon);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public void bindData(CardEntry cardEntry) {
        super.bindData(cardEntry);
        parseDetailInfo();
        updateView();
    }

    private void updateView() {
        this.mTypeImg.setImageResource(R.drawable.ic_card_small_app);
        this.mTypeTitle.setText(this.mData.title);
        this.mTitle.setText(this.mData.title);
        this.mContent.setText(this.mContext.getString(R.string.text_click_open_app));
        Drawable packageDrawable = getAppIconByPackageName(this.packageName);
        this.mAppImg.setImageDrawable(packageDrawable);
    }

    private void parseDetailInfo() {
        String content = this.mData.content;
        String str = TAG;
        Logger.d(str, "parseDetailInfo content--" + content);
        String[] result = content.split("/");
        if (result.length == 1) {
            this.packageName = result[0];
        } else if (result.length == 2) {
            this.packageName = result[0];
            this.activityName = result[1];
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        processCardClicked();
    }

    private void processCardClicked() {
        if (TextUtils.isEmpty(this.packageName) || TextUtils.isEmpty(this.activityName)) {
            return;
        }
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(this.packageName, this.activityName);
        Logger.d(TAG, String.format("package %s & activityName %s", this.packageName, this.activityName));
        intent.setComponent(componentName);
        PackageHelper.startActivity(this.mContext, intent, (Bundle) null);
        dismissCardSelf(this.mData);
    }

    private Drawable getAppIconByPackageName(String pkgName) {
        try {
            Drawable drawable = this.mContext.getPackageManager().getApplicationIcon(pkgName);
            return drawable;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Drawable drawable2 = ContextCompat.getDrawable(this.mContext, R.mipmap.ic_launcher);
            return drawable2;
        }
    }
}
