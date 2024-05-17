package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class OpenRearMirrorTile extends ContentProviderTile {

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface State {
        public static final int INIT = -1;
        public static final int LOADING = 3;
        public static final int OFF = 1;
        public static final int ON = 2;
    }

    public OpenRearMirrorTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        QuickMenuBIHelper.sendBIData(this.mTileKey, 2, this.mScreenId);
        saveContentProvider(-1, 2);
    }
}
