package com.xiaopeng.systemui.qs.tilemodels;

import com.xiaopeng.systemui.carmanager.XuiClientWrapper;
import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class CleanModeTileModel extends XpTileModel {
    public CleanModeTileModel(String tileSpec) {
        super(tileSpec);
        this.mCurrentLivedata.setValue(0);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        XuiClientWrapper.getInstance().startCleanMode(0);
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(0);
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int getCurrentState() {
        return 0;
    }
}
