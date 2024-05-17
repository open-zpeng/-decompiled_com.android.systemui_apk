package com.sequence;

import android.graphics.Bitmap;
/* loaded from: classes21.dex */
public abstract class BaseAnimationSequence {
    private final int mDefaultLoopCount;
    private final int mFrameCount;
    private final int mHeight;
    private final int mWidth;

    public abstract void destroy();

    public abstract int getDefaultFrameDuration();

    public abstract long getFrame(int i, Bitmap bitmap, int i2);

    public abstract boolean isOpaque();

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getFrameCount() {
        return this.mFrameCount;
    }

    public int getDefaultLoopCount() {
        return this.mDefaultLoopCount;
    }

    public BaseAnimationSequence(int width, int height, int frameCount, int defaultLoopCount) {
        this.mWidth = width;
        this.mHeight = height;
        this.mFrameCount = frameCount;
        this.mDefaultLoopCount = defaultLoopCount;
    }
}
