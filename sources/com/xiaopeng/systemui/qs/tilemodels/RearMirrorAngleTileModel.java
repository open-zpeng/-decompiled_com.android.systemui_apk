package com.xiaopeng.systemui.qs.tilemodels;

import android.util.Log;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class RearMirrorAngleTileModel extends XpTileModel {
    public RearMirrorAngleTileModel(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        ViewFactory.getNapaView().openNapaAppWindow(this.mTileKey);
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(0);
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
        Log.d(this.mTileKey, "xptile send broadcast mirror panel");
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    protected int getCurrentState() {
        return 0;
    }
}
