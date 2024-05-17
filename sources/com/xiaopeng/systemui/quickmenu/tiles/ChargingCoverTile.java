package com.xiaopeng.systemui.quickmenu.tiles;

import android.text.TextUtils;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class ChargingCoverTile extends ContentProviderTile {

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface State {
        public static final int FAULT = 4;
        public static final int INIT = -1;
        public static final int OFF = 1;
        public static final int ON = 2;
        public static final int TRAN = 5;
        public static final int UNKNOW = 3;
    }

    public ChargingCoverTile(String tileSpec) {
        super(tileSpec);
        this.mBuriedBtnId = "B009";
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        int state = getCurrentState();
        int nextState = -1;
        if (state == -1) {
            nextState = 1;
        }
        if (state == 1) {
            nextState = 2;
            TextUtils.isEmpty(this.mBuriedBtnId);
        } else if (state == 2) {
            nextState = 1;
            TextUtils.isEmpty(this.mBuriedBtnId);
        } else if (state == 3 || state == 4) {
            nextState = 1;
        } else if (state == 5) {
            return;
        }
        QuickMenuBIHelper.sendBIData(this.mTileKey, nextState, this.mScreenId);
        saveContentProvider(state, nextState);
    }
}
