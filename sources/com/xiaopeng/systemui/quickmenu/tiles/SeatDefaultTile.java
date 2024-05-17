package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class SeatDefaultTile extends ContentProviderTile {
    private final String TAG;
    protected int mSeatPropLevel;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface BaseState {
        public static final int INIT = -1;
        public static final int LEVEL0 = 0;
        public static final int LEVEL1 = 1;
        public static final int LEVEL2 = 2;
        public static final int LEVEL3 = 3;
    }

    public SeatDefaultTile(String key) {
        super(key);
        this.TAG = "SeatDefaultTile";
        this.mSeatPropLevel = getCurrentState();
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        int state = getCurrentState();
        int nextState = -1;
        if (state == -1) {
            nextState = 0;
        }
        if (state == 0) {
            nextState = 3;
        } else if (state == 1) {
            nextState = 0;
        } else if (state == 2) {
            nextState = 1;
        } else if (state == 3) {
            nextState = 2;
        }
        QuickMenuBIHelper.sendBIData(this.mTileKey, nextState, this.mScreenId);
        saveContentProvider(state, nextState);
    }
}
