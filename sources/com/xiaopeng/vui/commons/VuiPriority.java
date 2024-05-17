package com.xiaopeng.vui.commons;
/* loaded from: classes24.dex */
public enum VuiPriority {
    LEVEL1(0),
    LEVEL2(1),
    LEVEL3(2),
    LEVEL4(3);
    
    int level;

    VuiPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}
