package com.xiaopeng.systemui.qs.tilemodels;

import android.os.SystemClock;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.brightness.BrightnessManager;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.qs.QuickMenuBIHelper;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes24.dex */
public class ScreenOffTileModel extends XpTileModel implements BrightnessManager.OnScreenChangedCallBack {
    private static final String PASSENGER = "xp_mt_psg";
    public static final String TAG = "ScreenOffTile";

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface BaseState {
        public static final int INIT = -1;
        public static final int OFF = 2;
        public static final int ON = 1;
    }

    public ScreenOffTileModel(String tileSpec) {
        super(tileSpec);
        BrightnessManager.get(SystemUIApplication.getContext()).addOnScreenChangedCallBack(this);
        boolean isScreenON = BrightnessManager.get(ContextUtils.getContext()).isPsnScreenOn();
        this.mCurrentState = convertState(isScreenON ? 1 : 2);
        this.mCurrentLivedata.setValue(Integer.valueOf(this.mCurrentState));
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public void click(int value) {
        long uptimeMillis = SystemClock.uptimeMillis();
        Logger.d("ScreenOffTile", "ScreenOffTile click : " + uptimeMillis + " ," + value);
        QuickMenuBIHelper.sendBIData(this.mTileKey, BrightnessManager.get(ContextUtils.getContext()).isPsnScreenOn() ? 2 : 1, this.mScreenId);
        BrightnessManager.get(SystemUIApplication.getContext()).setPsnScreenOn(!BrightnessManager.get(ContextUtils.getContext()).isPsnScreenOn());
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(1);
    }

    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int getCurrentState() {
        return BrightnessManager.get(ContextUtils.getContext()).isPsnScreenOn() ? 1 : 2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.qs.tilemodels.XpTileModel
    public int convertState(int value) {
        return value - 1;
    }

    @Override // com.xiaopeng.systemui.controller.brightness.BrightnessManager.OnScreenChangedCallBack
    public void onScreenPowerChanged(String whichScreen, boolean status) {
        Logger.d("ScreenOffTile", "ScreenOffTile onScreenPowerChanged : " + whichScreen + " ," + status);
        if (PASSENGER.equals(whichScreen)) {
            boolean isScreenON = BrightnessManager.get(ContextUtils.getContext()).isPsnScreenOn();
            this.mCurrentState = convertState(isScreenON ? 1 : 2);
            this.mCurrentLivedata.setValue(Integer.valueOf(this.mCurrentState));
        }
    }
}
