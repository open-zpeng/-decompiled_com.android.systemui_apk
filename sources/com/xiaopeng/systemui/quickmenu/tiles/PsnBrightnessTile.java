package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.controller.BrightnessController;
/* loaded from: classes24.dex */
public class PsnBrightnessTile extends ContentProviderTile {
    public static final String TAG = "PsnBrightnessTile";

    public PsnBrightnessTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        BrightnessController.getInstance(this.mContext).setPsnScreenBrightness(value);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        return BrightnessController.getInstance(this.mContext).getPsnScreenBrightness();
    }
}
