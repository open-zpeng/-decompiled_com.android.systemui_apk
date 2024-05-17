package com.xiaopeng.systemui.quickmenu.tiles;

import android.content.Intent;
import android.util.Log;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class RearMirrorAngleTile extends ContentProviderTile {
    public RearMirrorAngleTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        Intent intent = new Intent();
        intent.setPackage(VuiConstants.CARCONTROL);
        intent.putExtra("key_show_mirror_panel_inner", true);
        intent.setAction("com.xiaopeng.carcontrol.intent.action.ACTION_SHOW_MIRROR_CONTROL_PANEL");
        intent.setFlags(16777216);
        this.mContext.sendBroadcast(intent);
        QuickMenuBIHelper.sendBIData(this.mTileKey, -1, this.mScreenId);
        Log.d("", "xptile send broadcast mirror panel");
    }
}
