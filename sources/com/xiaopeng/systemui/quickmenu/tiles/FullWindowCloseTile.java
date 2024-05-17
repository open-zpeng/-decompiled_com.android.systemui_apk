package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class FullWindowCloseTile extends ContentProviderTile {

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface State {
        public static final int HALF = 4;
        public static final int INIT = -1;
        public static final int LOADING = 3;
        public static final int OFF = 1;
        public static final int ON = 2;
    }

    public FullWindowCloseTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        int state = getCurrentState();
        QuickMenuBIHelper.sendBIData(this.mTileKey, 1, this.mScreenId);
        saveContentProvider(state, 1);
    }
}
