package com.xiaopeng.systemui.qs.tilemodels;

import android.content.ComponentName;
import android.content.Intent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
/* loaded from: classes24.dex */
public class SteeringWheelTileModel extends XpTileModel {
    public static final String ACTION_SHOW_STEER_CONTROL_PANEL = "com.xiaopeng.carcontrol.intent.action.ACTION_SHOW_STEER_CONTROL_PANEL";

    public SteeringWheelTileModel(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(VuiConstants.CARCONTROL, "com.xiaopeng.carcontrol.CarControlService"));
        intent.setAction(ACTION_SHOW_STEER_CONTROL_PANEL);
        this.mContext.startService(intent);
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(this.mScreenId);
        QuickMenuBIHelper.sendBIData(this.mTileKey, this.mCurrentState, this.mScreenId);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    protected int getCurrentState() {
        return 0;
    }
}
