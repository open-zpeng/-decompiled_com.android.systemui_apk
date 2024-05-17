package com.xiaopeng.systemui.navigationbar;
/* loaded from: classes24.dex */
public interface INavigationBarPresenter {
    String getHvacInfo();

    int getInnerQuality();

    float getTemperature(int i);

    void onDockHvacClicked();

    void onDockHvacLongClicked();

    void onHvacComboClicked();

    void onHvacSynchronizedClicked();

    void onNavigationBarClicked();

    void onNavigationButtonClicked(int i);

    void onNavigationTouchDown();

    void onNavigationTouchMove();

    void onNavigationTouchUp();

    void onTemperatureDownClicked(int i);

    void onTemperatureProgressChanged(int i, float f, boolean z);

    void onTemperatureUpClicked(int i);

    void setTemperature(int i, float f);
}
