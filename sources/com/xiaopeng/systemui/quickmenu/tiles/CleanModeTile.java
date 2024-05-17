package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.carmanager.XuiClientWrapper;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class CleanModeTile extends XpTile {
    public CleanModeTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        XuiClientWrapper.getInstance().startCleanMode(0);
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
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
