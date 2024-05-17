package com.xiaopeng.systemui.quickmenu.tiles;

import com.xiaopeng.systemui.quickmenu.CarSettingsManager;
/* loaded from: classes24.dex */
public class TemperatureTile extends ContentProviderTile {
    static CarSettingsManager mCarSettingsManager = CarSettingsManager.getInstance();

    public TemperatureTile(String tileSpec) {
        super(tileSpec);
    }

    @Override // com.xiaopeng.systemui.quickmenu.tiles.ContentProviderTile, com.xiaopeng.systemui.quickmenu.tiles.XpTile
    public void click(int value) {
        int currentState = getCurrentState();
        int result = getConvertValue(value);
        saveContentProvider(currentState, result);
    }

    public static int getConvertValue(int value) {
        if (value % 10 == 0 || value % 5 == 0) {
            return value;
        }
        if (value % 10 > 5) {
            int result = ((value / 10) * 10) + 5;
            return result;
        }
        int result2 = (value / 10) * 10;
        return result2;
    }

    public static int getMaxTemperature() {
        return mCarSettingsManager.getMaxTemperature();
    }

    public static int getMinTemperature() {
        return mCarSettingsManager.getMinTemperature();
    }
}
