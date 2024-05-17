package com.xiaopeng.systemui.infoflow.effect;

import android.graphics.Canvas;
/* loaded from: classes24.dex */
public abstract class AbsEffect implements Effect {
    private int mHeight;
    private int mWidth;
    private boolean mRunning = true;
    private volatile boolean mEnable = false;

    protected abstract void onDraw(Canvas canvas);

    protected static int getColorAlpha(int color) {
        return (color >> 24) & 255;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public boolean isEnable() {
        return this.mEnable;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public void setEnable(boolean enable) {
        this.mEnable = enable;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getWidth() {
        return this.mWidth;
    }

    protected final void setWidth(int var1) {
        this.mWidth = var1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getHeight() {
        return this.mHeight;
    }

    protected final void setHeight(int var1) {
        this.mHeight = var1;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public void performDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        if (this.mWidth != width || this.mHeight != height) {
            this.mWidth = width;
            this.mHeight = height;
            onSizeChange(width, height);
        }
        onDraw(canvas);
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public void update(float value, boolean shouldFilter) {
        if (!shouldFilter) {
            onUpdate(value);
        }
    }

    protected void onUpdate(float value) {
    }

    public boolean isRunning() {
        return this.mRunning;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public void onPause() {
        this.mRunning = false;
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.Effect
    public void onResume() {
        this.mRunning = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSizeChange(int w, int h) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int setColorAlpha(int color, int alpha) {
        return (16777215 & color) | (alpha << 24);
    }
}
