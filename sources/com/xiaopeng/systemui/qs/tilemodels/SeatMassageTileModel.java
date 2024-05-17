package com.xiaopeng.systemui.qs.tilemodels;

import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class SeatMassageTileModel extends XpTileModel {
    public SeatMassageTileModel(String tileSpec) {
        super(tileSpec);
        char c;
        int hashCode = tileSpec.hashCode();
        if (hashCode != -1659133291) {
            if (hashCode == -61899240 && tileSpec.equals("driver_massage_adjustment")) {
                c = 0;
            }
            c = 65535;
        } else {
            if (tileSpec.equals("psn_massage_adjustment")) {
                c = 1;
            }
            c = 65535;
        }
        if (c == 0) {
            setScreenId(0);
        } else if (c == 1) {
            setScreenId(1);
        }
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        ViewFactory.getNapaView().openNapaAppWindow(this.mTileKey);
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(this.mScreenId);
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int getCurrentState() {
        return 0;
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int convertState(int state) {
        return 0;
    }
}
