package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.quickmenu.CarSettingsManager;
/* loaded from: classes24.dex */
public class WindTile extends ContentProviderTile {
    public static final int WIND_AUTO = 14;
    static CarSettingsManager mCarSettingsManager = CarSettingsManager.getInstance();

    public WindTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        int currentState = getCurrentState();
        saveContentProvider(currentState, value);
    }

    public static int getMaxWindSpeed() {
        return mCarSettingsManager.getMaxWindSpeed();
    }

    public static int getMinWindSpeed() {
        return mCarSettingsManager.getMinWindSpeed();
    }
}
