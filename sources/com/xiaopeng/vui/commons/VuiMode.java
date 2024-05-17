package com.xiaopeng.vui.commons;
/* loaded from: classes24.dex */
public enum VuiMode {
    DISABLED("Disabled"),
    SILENT("Silent"),
    UNDERSTOOD("Understood"),
    NORMAL("Normal");
    
    String name;

    VuiMode(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
