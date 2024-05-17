package com.xiaopeng.systemui.infoflow.speech.carcontrol;
/* loaded from: classes24.dex */
public class ProgressProperty {
    private int per;
    private int slips;

    public int getPer() {
        return this.per;
    }

    public void setPer(int per) {
        this.per = per;
    }

    public int getSlips() {
        return this.slips;
    }

    public void setSlips(int slips) {
        this.slips = slips;
    }

    public int sum() {
        return this.per * this.slips;
    }
}
