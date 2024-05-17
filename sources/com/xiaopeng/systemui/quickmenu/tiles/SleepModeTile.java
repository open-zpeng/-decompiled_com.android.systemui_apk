package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.carmanager.XuiClientWrapper;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class SleepModeTile extends XpTile {
    public SleepModeTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
        XuiClientWrapper.getInstance().startSleepMode(0);
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
