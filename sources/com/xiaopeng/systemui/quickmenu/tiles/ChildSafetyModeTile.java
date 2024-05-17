package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class ChildSafetyModeTile extends ContentProviderTile {
    public ChildSafetyModeTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        PackageHelper.startChildSafetyMode(ContextUtils.getContext());
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(0);
    }
}
