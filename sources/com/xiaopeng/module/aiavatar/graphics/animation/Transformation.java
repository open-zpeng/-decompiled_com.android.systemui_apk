package com.xiaopeng.module.aiavatar.graphics.animation;

import com.badlogic.gdx.math.Matrix4;
/* loaded from: classes23.dex */
public class Transformation {
    protected float mAlpha;
    protected Matrix4 mMatrix;
    protected int mTransformationType;
    public static int TYPE_IDENTITY = 0;
    public static int TYPE_ALPHA = 1;
    public static int TYPE_MATRIX = 2;
    public static int TYPE_BOTH = TYPE_ALPHA | TYPE_MATRIX;
    private static final Matrix4 sTemp = new Matrix4();

    public Transformation() {
        clear();
    }

    public void clear() {
        Matrix4 matrix4 = this.mMatrix;
        if (matrix4 == null) {
            this.mMatrix = new Matrix4();
        } else {
            matrix4.idt();
        }
        this.mAlpha = 1.0f;
        this.mTransformationType = TYPE_BOTH;
    }

    public int getTransformationType() {
        return this.mTransformationType;
    }

    public void setTransformationType(int transformationType) {
        this.mTransformationType = transformationType;
    }

    public void set(Transformation t) {
        this.mAlpha = t.getAlpha();
        this.mMatrix.set(t.getMatrix());
        this.mTransformationType = t.getTransformationType();
    }

    public void compose(Transformation t) {
        this.mAlpha *= t.getAlpha();
        this.mMatrix.mul(t.getMatrix());
    }

    public void postCompose(Transformation t) {
        this.mAlpha *= t.getAlpha();
        sTemp.set(t.getMatrix());
        Matrix4 matrix4 = this.mMatrix;
        matrix4.set(sTemp.mul(matrix4));
    }

    public Matrix4 getMatrix() {
        return this.mMatrix;
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
    }

    public float getAlpha() {
        return this.mAlpha;
    }
}
