package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.xiaopeng.systemui.helper.PackageHelper;
/* loaded from: classes24.dex */
public class CameraCardHolder extends BaseCardHolder {
    private static final String TAG = CameraCardHolder.class.getSimpleName();
    private final String ACTIVITY_CAMERA;
    private final String PACKAGE_CAMERA;

    public CameraCardHolder(View itemView) {
        super(itemView);
        this.PACKAGE_CAMERA = "com.xiaopeng.xmart.camera";
        this.ACTIVITY_CAMERA = "com.xiaopeng.xmart.camera.MainActivity";
        itemView.setOnClickListener(this);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        processCardClicked();
    }

    private void processCardClicked() {
        Log.e("MarkCode", "processCameraCardClicked");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xiaopeng.xmart.camera", "com.xiaopeng.xmart.camera.MainActivity"));
        PackageHelper.startActivity(this.mContext, intent, (Bundle) null);
    }
}
