package com.xiaopeng.systemui.secondarywindow;
/* loaded from: classes24.dex */
public class CarMediaAppInfo extends LargeAppInfo {
    private int mTabIndex;

    public CarMediaAppInfo() {
        setType(1);
        setSysApp(true);
        setSysAppType(2);
    }

    public int getTabIndex() {
        return this.mTabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.mTabIndex = tabIndex;
    }
}
