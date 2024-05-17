package com.xiaopeng.systemui.qs.tilemodels;

import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class SeatHeatVentTileModel extends ContentProviderTileModel {

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface State {
        public static final int INIT = -1;
        public static final int LEVEL0 = 0;
        public static final int LEVEL1 = 1;
        public static final int LEVEL2 = 2;
        public static final int LEVEL3 = 3;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public SeatHeatVentTileModel(String tileSpec) {
        super(tileSpec);
        char c;
        switch (tileSpec.hashCode()) {
            case -1868200807:
                if (tileSpec.equals("driver_seat_heat_adjustment")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1756583756:
                if (tileSpec.equals("driver_seat_vent_adjustment")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 788168150:
                if (tileSpec.equals("psn_seat_heat_adjustment")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 899785201:
                if (tileSpec.equals("psn_seat_vent_adjustment")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0 || c == 1) {
            this.mScreenId = 0;
        } else if (c == 2 || c == 3) {
            this.mScreenId = 1;
        }
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel, com.xiaopeng.systemui.qs.tilemodels.XpTileModel
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
