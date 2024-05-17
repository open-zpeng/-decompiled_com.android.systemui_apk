package com.xiaopeng.systemui.qs.tilemodels;

import com.xiaopeng.systemui.carmanager.XuiClientWrapper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class WaitingModeTileModel extends XpTileModel {
    public WaitingModeTileModel(String tileSpec) {
        super(tileSpec);
        this.mCurrentLivedata.setValue(0);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        XuiClientWrapper.getInstance().startWaitingMode(0);
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(0);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    protected int getCurrentState() {
        return 0;
    }
}
