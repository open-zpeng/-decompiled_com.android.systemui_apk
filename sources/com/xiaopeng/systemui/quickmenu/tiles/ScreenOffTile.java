package com.xiaopeng.systemui.quickmenu.tiles;

import android.os.SystemClock;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.brightness.BrightnessManager;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.quickmenu.QuickMenuBIHelper;
/* loaded from: classes24.dex */
public class ScreenOffTile extends XpTile implements BrightnessManager.OnScreenChangedCallBack {
    private static final String PASSENGER = "xp_mt_psg";
    public static final String TAG = "ScreenOffTile";

    public ScreenOffTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        long uptimeMillis = SystemClock.uptimeMillis();
        Logger.d("ScreenOffTile", "ScreenOffTile click : " + uptimeMillis + " ," + value);
        QuickMenuBIHelper.sendBIData(this.mTileKey, BrightnessManager.get(ContextUtils.getContext()).isPsnScreenOn() ? 1 : 2, this.mScreenId);
        BrightnessManager.get(SystemUIApplication.getContext()).setPsnScreenOn(!BrightnessManager.get(ContextUtils.getContext()).isPsnScreenOn());
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void destroy() {
        BrightnessManager.get(SystemUIApplication.getContext()).removeOnScreenChangedCallBack(this);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void create() {
        BrightnessManager.get(SystemUIApplication.getContext()).addOnScreenChangedCallBack(this);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public int getCurrentState() {
        return BrightnessManager.get(ContextUtils.getContext()).isPsnScreenOn() ? 2 : 1;
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.XpTile
    int convertState(int state) {
        return state;
    }

    @Override // com.xiaopeng.systemui.controller.brightness.BrightnessManager.OnScreenChangedCallBack
    public void onScreenPowerChanged(String whichScreen, boolean status) {
        Logger.d("ScreenOffTile", "ScreenOffTile onScreenPowerChanged : " + whichScreen + " ," + status);
        if (PASSENGER.equals(whichScreen)) {
            boolean isScreenON = BrightnessManager.get(ContextUtils.getContext()).isPsnScreenOn();
            refreshState(isScreenON ? 2 : 1);
        }
    }
}
