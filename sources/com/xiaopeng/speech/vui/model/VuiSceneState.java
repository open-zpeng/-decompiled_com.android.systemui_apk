package com.xiaopeng.speech.vui.model;
/* loaded from: classes.dex */
public enum VuiSceneState {
    INIT(0),
    ACTIVE(1),
    UNACTIVE(2),
    IDLE(3);
    
    private int state;

    VuiSceneState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }
}
