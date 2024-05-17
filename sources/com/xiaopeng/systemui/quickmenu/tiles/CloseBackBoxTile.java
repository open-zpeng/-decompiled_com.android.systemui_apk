package com.xiaopeng.systemui.quickmenu.tiles;

import android.util.Log;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class CloseBackBoxTile extends BackBoxTile {
    public CloseBackBoxTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
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
