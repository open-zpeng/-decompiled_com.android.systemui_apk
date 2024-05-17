package com.android.systemui.assist.ui;

import androidx.annotation.ColorInt;
/* loaded from: classes21.dex */
public final class EdgeLight {
    @ColorInt
    private int mColor;
    private float mLength;
    private float mOffset;

    public static EdgeLight[] copy(EdgeLight[] array) {
        EdgeLight[] copy = new EdgeLight[array.length];
        for (int i = 0; i < array.length; i++) {
            copy[i] = new EdgeLight(array[i]);
        }
        return copy;
    }

    public EdgeLight(@ColorInt int color, float offset, float length) {
        this.mColor = color;
        this.mOffset = offset;
        this.mLength = length;
    }

    public EdgeLight(EdgeLight sourceLight) {
        this.mColor = sourceLight.getColor();
        this.mOffset = sourceLight.getOffset();
        this.mLength = sourceLight.getLength();
    }

    @ColorInt
    public int getColor() {
        return this.mColor;
    }

    public void setColor(@ColorInt int color) {
        this.mColor = color;
    }

    public float getLength() {
        return this.mLength;
    }

    public void setLength(float length) {
        this.mLength = length;
    }

    public float getOffset() {
        return this.mOffset;
    }

    public void setOffset(float offset) {
        this.mOffset = offset;
    }

    public float getCenter() {
        return this.mOffset + (this.mLength / 2.0f);
    }
}
