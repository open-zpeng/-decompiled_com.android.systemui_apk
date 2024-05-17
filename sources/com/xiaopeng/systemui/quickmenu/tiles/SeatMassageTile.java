package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class SeatMassageTile extends XpTile {
    public SeatMassageTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        PackageHelper.startSeatMassage(ContextUtils.getContext(), 0);
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
