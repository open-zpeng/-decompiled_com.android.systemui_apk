package com.xiaopeng.systemui.quickmenu.tiles;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class PanoramicViewTile extends XpTile {
    public Context mContext;

    public PanoramicViewTile(String tileSpec) {
        super(tileSpec);
        this.mContext = ContextUtils.getContext();
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        Log.d(this.mTileKey, "360 clicked!");
        Intent intent = new Intent("com.xiaopeng.drivingimageassist.NRA");
        intent.setComponent(new ComponentName("com.xiaopeng.drivingimageassist", "com.xiaopeng.drivingimageassist.NRACtrlReceiver"));
        this.mContext.sendBroadcast(intent);
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(0);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void destroy() {
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void create() {
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        return 0;
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    int convertState(int state) {
        return 0;
    }
}
