package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class BackBoxLockTile extends ContentProviderTile {
    public BackBoxLockTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        saveContentProvider(1, 2);
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
    }
}
