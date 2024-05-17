package com.xiaopeng.systemui.navigationbar;
/* loaded from: classes24.dex */
public interface INavigationBarView {
    void onActivityChanged(String str, String str2, boolean z, boolean z2, int i);

    void onHvacPanelChanged(boolean z);

    void setAutoDefog(boolean z);

    void setDriverTemperature(float f);

    void setHvacInfo(HvacInfo hvacInfo);

    void setInnerQuality(int i, String str);

    void setPassengerTemperature(float f);

    void setPsnSeatHeatLevel(int i);

    void setPsnSeatVentLevel(int i);

    void setPurgeMode(boolean z, boolean z2, int i);

    void setQuickTemperature(float f);

    void switchHvacDashboard(boolean z, boolean z2);
}
