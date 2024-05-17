package com.xiaopeng.systemui.qs.tilemodels;

import android.util.Log;
/* loaded from: classes24.dex */
public class DefaultTileModel extends ContentProviderTileModel {
    private String TAG;

    public DefaultTileModel(String key) {
        super(key);
        this.TAG = "DefaultTile";
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel, com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        String str = this.TAG;
        Log.d(str, "Null key:" + this.mTileKey + " Clicked!");
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel, com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int getCurrentState() {
        String str = this.TAG;
        Log.d(str, "Null key:" + this.mTileKey + " getCurrentState!");
        return -1;
    }
}
