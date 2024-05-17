package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.android.systemui.R;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
/* loaded from: classes24.dex */
public class DefaultCardHolder extends BaseCardHolder {
    private static final String TAG = DefaultCardHolder.class.getSimpleName();
    private ImageView mAppImg;
    private TextView mContent;
    private TextView mTitle;
    private ImageView mTypeImg;
    private TextView mTypeTitle;

    public DefaultCardHolder(View itemView) {
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
        this.mData = cardEntry;
        updateView();
    }

    private void updateView() {
        this.mTypeTitle.setText(this.mData.title);
        this.mTitle.setText(this.mData.title);
        this.mContent.setText(this.mData.content);
        Drawable packageDrawable = getAppIconByPackageName(this.mData.pkgName);
        this.mAppImg.setImageDrawable(packageDrawable);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        processCardClicked();
    }

    private void processCardClicked() {
        if (TextUtils.isEmpty(this.mData.action)) {
            return;
        }
        PackageHelper.startActivity(this.mContext, this.mData.action, null, null, null);
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
