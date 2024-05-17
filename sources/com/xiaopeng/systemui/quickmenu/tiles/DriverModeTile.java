package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class DriverModeTile extends ContentProviderTile {

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface State {
        public static final int ECO = 1;
        public static final int ECO_PLUS = 4;
        public static final int NORMAL = 3;
        public static final int SPORT = 2;
    }

    public DriverModeTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        int currentState = getCurrentState();
        int nextState = 0;
        if (value == 0) {
            nextState = 3;
        } else if (value == 1) {
            nextState = 2;
        } else if (value == 2) {
            nextState = 1;
        }
        QuickMenuBIHelper.sendBIData(this.mTileKey, nextState, this.mScreenId);
        saveContentProvider(currentState, nextState);
    }
}
