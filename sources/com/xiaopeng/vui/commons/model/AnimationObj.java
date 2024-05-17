package com.xiaopeng.vui.commons.model;

import com.xiaopeng.vui.commons.EffectType;
/* loaded from: classes24.dex */
public class AnimationObj {
    private boolean effectOnly = false;
    private String effect = "waterRipple";

    public boolean isEffectOnly() {
        return this.effectOnly;
    }

    public void setEffectOnly(boolean effectOnly) {
        this.effectOnly = effectOnly;
    }

    public EffectType getEffect() {
        if ("waterRipple".equals(this.effect)) {
            return EffectType.WATERRIPPLE;
        }
        return EffectType.WATERRIPPLE;
    }

    public String toString() {
        return "AnimationObj{effectOnly=" + this.effectOnly + ", effect='" + this.effect + "'}";
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }
}
