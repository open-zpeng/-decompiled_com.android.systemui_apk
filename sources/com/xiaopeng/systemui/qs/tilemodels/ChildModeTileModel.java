package com.xiaopeng.systemui.qs.tilemodels;
/* loaded from: classes24.dex */
public class ChildModeTileModel extends ContentProviderTileModel {
    public ChildModeTileModel(String tileSpec) {
        super(tileSpec);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int convertState(int value) {
        return value - 1;
    }
}
