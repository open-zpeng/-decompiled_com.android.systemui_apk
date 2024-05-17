package com.xiaopeng.systemui.quickmenu.tiles;

import android.util.Log;
/* loaded from: classes24.dex */
public class DefaultTile extends XpTile {
    private String TAG;

    public DefaultTile(String key) {
        super(key);
        this.TAG = "DefaultTile";
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        String str = this.TAG;
        Log.d(str, "Null key:" + this.mTileKey + " Clicked!");
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void destroy() {
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void create() {
        String str = this.TAG;
        Log.d(str, "Null key:" + this.mTileKey + " Created!");
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        String str = this.TAG;
        Log.d(str, "Null key:" + this.mTileKey + " getCurrentState!");
        return -1;
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    int convertState(int state) {
        return 0;
    }
}
