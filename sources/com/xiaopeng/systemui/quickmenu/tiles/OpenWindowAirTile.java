package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class OpenWindowAirTile extends ContentProviderTile {
    public OpenWindowAirTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        QuickMenuBIHelper.sendBIData(this.mTileKey, 2, this.mScreenId);
        saveContentProvider(1, 2);
    }
}
