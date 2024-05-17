package com.xiaopeng.systemui.quickmenu.tiles;

import android.content.Intent;
import android.net.Uri;
import com.xiaopeng.speech.common.util.ResourceUtils;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class SpeechSettingTile extends XpTile {
    public SpeechSettingTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
        Uri uri = Uri.parse("xiaopeng://carspeechservice/speechmain?from=systemui");
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.addFlags(268435456);
        ResourceUtils.getContext().startActivity(intent);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void destroy() {
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void create() {
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        return 0;
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    int convertState(int state) {
        return 0;
    }
}
