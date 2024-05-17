package com.xiaopeng.systemui.quickmenu.tiles;

import android.util.Log;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class OpenBackBoxTile extends BackBoxTile {
    public OpenBackBoxTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
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
            } else if (state != 5 && state != 6) {
                Log.d("quickmenu", "xptile open back box " + state);
                return;
            }
        }
        nextState = 2;
        QuickMenuBIHelper.sendBIData(this.mTileKey, nextState, this.mScreenId);
        saveContentProvider(state, nextState);
    }
}
