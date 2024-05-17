package com.xiaopeng.systemui.qs.tilemodels;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class PanoramicViewTileModel extends XpTileModel {
    public PanoramicViewTileModel(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        Log.d(this.mTileKey, "360 clicked!");
        Intent intent = new Intent("com.xiaopeng.drivingimageassist.NRA");
        intent.setComponent(new ComponentName("com.xiaopeng.drivingimageassist", "com.xiaopeng.drivingimageassist.NRACtrlReceiver"));
        this.mContext.sendBroadcast(intent);
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(0);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    protected int getCurrentState() {
        return 0;
    }
}
