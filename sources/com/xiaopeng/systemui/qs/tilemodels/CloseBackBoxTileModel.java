package com.xiaopeng.systemui.qs.tilemodels;

import android.util.Log;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class CloseBackBoxTileModel extends BackBoxTileModel {
    public CloseBackBoxTileModel(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel, com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        int nextState;
        int state = getCurrentState();
        if (state == -1) {
        }
        if (state == 2) {
            nextState = 1;
        } else if (state == 4) {
            nextState = 6;
        } else if (state == 5 || state == 6) {
            nextState = 1;
        } else {
            Log.d("quickmenu", "xptile close back box " + state);
            return;
        }
        QuickMenuBIHelper.sendBIData(this.mTileKey, nextState, this.mScreenId);
        saveContentProvider(state, nextState);
    }
}
