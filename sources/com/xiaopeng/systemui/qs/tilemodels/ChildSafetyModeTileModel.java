package com.xiaopeng.systemui.qs.tilemodels;

import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class ChildSafetyModeTileModel extends ContentProviderTileModel {
    public ChildSafetyModeTileModel(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel, com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        PackageHelper.startChildSafetyMode(ContextUtils.getContext());
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(0);
    }
}
