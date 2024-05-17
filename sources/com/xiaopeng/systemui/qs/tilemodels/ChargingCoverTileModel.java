package com.xiaopeng.systemui.qs.tilemodels;

import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class ChargingCoverTileModel extends ContentProviderTileModel {

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

    public ChargingCoverTileModel(String tileSpec) {
        super(tileSpec);
        this.mBuriedBtnId = "B009";
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel, com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        int state = getCurrentState();
        int nextState = -1;
        if (state == -1) {
            nextState = 1;
        }
        if (state == 1) {
            nextState = 2;
        } else if (state == 2) {
            nextState = 1;
        } else if (state == 3 || state == 4) {
            nextState = 1;
        } else if (state == 5) {
            return;
        }
        QuickMenuBIHelper.sendBIData(this.mTileKey, nextState, this.mScreenId);
        saveContentProvider(state, nextState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int convertState(int value) {
        if (value == -1) {
            return value;
        }
        return value - 1;
    }
}
