package com.xiaopeng.systemui.qs.tilemodels;

import android.util.Log;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class OpenBackBoxTileModel extends BackBoxTileModel {
    public OpenBackBoxTileModel(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel, com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        int nextState;
        int state = getCurrentState();
        if (state == -1) {
        }
        if (state != 1) {
            if (state == 3) {
                nextState = 5;
                QuickMenuBIHelper.sendBIData(this.mTileKey, nextState, this.mScreenId);
                saveContentProvider(state, nextState);
            } else if (state != 8 && state != 5 && state != 6) {
                Log.d("quickmenu", "xptile open back box " + state);
                return;
            }
        }
        nextState = 2;
        QuickMenuBIHelper.sendBIData(this.mTileKey, nextState, this.mScreenId);
        saveContentProvider(state, nextState);
    }
}
