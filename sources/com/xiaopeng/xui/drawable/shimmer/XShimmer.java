package com.xiaopeng.xui.drawable.shimmer;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import com.xiaopeng.xpui.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Deprecated
/* loaded from: classes25.dex */
public class XShimmer {
    private static final int COMPONENT_COUNT = 4;
    public static boolean msGlobalEnable;
    long repeatDelay;
    final float[] positions = new float[4];
    final int[] colors = new int[4];
    int direction = 0;
    @ColorInt
    int highlightColor = 1728053247;
    @ColorInt
    int baseColor = 16777215;
    int shape = 0;
    int fixedWidth = 0;
    int fixedHeight = 0;
    float widthRatio = 1.0f;
    float heightRatio = 1.0f;
    float intensity = 0.0f;
    float dropoff = 0.5f;
    float tilt = 0.0f;
    boolean clipToChildren = true;
    boolean autoStart = true;
    boolean alphaShimmer = true;
    int repeatCount = -1;
    int repeatMode = 1;
    long animationDuration = 1000;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes25.dex */
    public @interface Direction {
        public static final int BOTTOM_TO_TOP = 3;
        public static final int LEFT_TO_RIGHT = 0;
        public static final int RIGHT_TO_LEFT = 2;
        public static final int TOP_TO_BOTTOM = 1;
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes25.dex */
    public @interface Shape {
        public static final int LINEAR = 0;
        public static final int RADIAL = 1;
    }

    XShimmer() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int width(int width) {
        int i = this.fixedWidth;
        return i > 0 ? i : Math.round(this.widthRatio * width);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int height(int height) {
        int i = this.fixedHeight;
        return i > 0 ? i : Math.round(this.heightRatio * height);
    }

    void updateColors() {
        if (this.shape != 1) {
            int[] iArr = this.colors;
            int i = this.baseColor;
            iArr[0] = i;
            int i2 = this.highlightColor;
            iArr[1] = i2;
            iArr[2] = i2;
            iArr[3] = i;
            return;
        }
        int[] iArr2 = this.colors;
        int i3 = this.highlightColor;
        iArr2[0] = i3;
        iArr2[1] = i3;
        int i4 = this.baseColor;
        iArr2[2] = i4;
        iArr2[3] = i4;
    }

    void updatePositions() {
        if (this.shape != 1) {
            this.positions[0] = Math.max(((1.0f - this.intensity) - this.dropoff) / 2.0f, 0.0f);
            this.positions[1] = Math.max((1.0f - this.intensity) / 2.0f, 0.0f);
            this.positions[2] = Math.min((this.intensity + 1.0f) / 2.0f, 1.0f);
            this.positions[3] = Math.min(((this.intensity + 1.0f) + this.dropoff) / 2.0f, 1.0f);
            return;
        }
        float[] fArr = this.positions;
        fArr[0] = 0.0f;
        fArr[1] = Math.min(this.intensity, 1.0f);
        this.positions[2] = Math.min(this.intensity + this.dropoff, 1.0f);
        this.positions[3] = 1.0f;
    }

    /* loaded from: classes25.dex */
    public static abstract class Builder<T extends Builder<T>> {
        final XShimmer mShimmer = new XShimmer();

        protected abstract T getThis();

        public T consumeAttributes(Context context, AttributeSet attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XShimmerDrawable, 0, 0);
            return consumeAttributes(a);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public T consumeAttributes(TypedArray a) {
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_clip_to_children)) {
                setClipToChildren(a.getBoolean(R.styleable.XShimmerDrawable_shimmer_clip_to_children, this.mShimmer.clipToChildren));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_auto_start)) {
                setAutoStart(a.getBoolean(R.styleable.XShimmerDrawable_shimmer_auto_start, this.mShimmer.autoStart));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_base_alpha)) {
                setBaseAlpha(a.getFloat(R.styleable.XShimmerDrawable_shimmer_base_alpha, 0.3f));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_highlight_alpha)) {
                setHighlightAlpha(a.getFloat(R.styleable.XShimmerDrawable_shimmer_highlight_alpha, 1.0f));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_duration)) {
                setDuration(a.getInt(R.styleable.XShimmerDrawable_shimmer_duration, (int) this.mShimmer.animationDuration));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_repeat_count)) {
                setRepeatCount(a.getInt(R.styleable.XShimmerDrawable_shimmer_repeat_count, this.mShimmer.repeatCount));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_repeat_delay)) {
                setRepeatDelay(a.getInt(R.styleable.XShimmerDrawable_shimmer_repeat_delay, (int) this.mShimmer.repeatDelay));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_repeat_mode)) {
                setRepeatMode(a.getInt(R.styleable.XShimmerDrawable_shimmer_repeat_mode, this.mShimmer.repeatMode));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_direction)) {
                int direction = a.getInt(R.styleable.XShimmerDrawable_shimmer_direction, this.mShimmer.direction);
                setDirection(direction);
            }
            int direction2 = R.styleable.XShimmerDrawable_shimmer_shape;
            if (a.hasValue(direction2)) {
                int shape = a.getInt(R.styleable.XShimmerDrawable_shimmer_shape, this.mShimmer.shape);
                setShape(shape);
            }
            int shape2 = R.styleable.XShimmerDrawable_shimmer_dropoff;
            if (a.hasValue(shape2)) {
                setDropoff(a.getFloat(R.styleable.XShimmerDrawable_shimmer_dropoff, this.mShimmer.dropoff));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_fixed_width)) {
                setFixedWidth(a.getDimensionPixelSize(R.styleable.XShimmerDrawable_shimmer_fixed_width, this.mShimmer.fixedWidth));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_fixed_height)) {
                setFixedHeight(a.getDimensionPixelSize(R.styleable.XShimmerDrawable_shimmer_fixed_height, this.mShimmer.fixedHeight));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_intensity)) {
                setIntensity(a.getFloat(R.styleable.XShimmerDrawable_shimmer_intensity, this.mShimmer.intensity));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_width_ratio)) {
                setWidthRatio(a.getFloat(R.styleable.XShimmerDrawable_shimmer_width_ratio, this.mShimmer.widthRatio));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_height_ratio)) {
                setHeightRatio(a.getFloat(R.styleable.XShimmerDrawable_shimmer_height_ratio, this.mShimmer.heightRatio));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_tilt)) {
                setTilt(a.getFloat(R.styleable.XShimmerDrawable_shimmer_tilt, this.mShimmer.tilt));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_global_configuration_enable)) {
                boolean global = a.getBoolean(R.styleable.XShimmerDrawable_shimmer_global_configuration_enable, false);
                if (global) {
                    setAutoStart(XShimmer.msGlobalEnable);
                }
            }
            return getThis();
        }

        public T copyFrom(XShimmer other) {
            setDirection(other.direction);
            setShape(other.shape);
            setFixedWidth(other.fixedWidth);
            setFixedHeight(other.fixedHeight);
            setWidthRatio(other.widthRatio);
            setHeightRatio(other.heightRatio);
            setIntensity(other.intensity);
            setDropoff(other.dropoff);
            setTilt(other.tilt);
            setClipToChildren(other.clipToChildren);
            setAutoStart(other.autoStart);
            setRepeatCount(other.repeatCount);
            setRepeatMode(other.repeatMode);
            setRepeatDelay(other.repeatDelay);
            setDuration(other.animationDuration);
            this.mShimmer.baseColor = other.baseColor;
            this.mShimmer.highlightColor = other.highlightColor;
            return getThis();
        }

        public T setDirection(int direction) {
            this.mShimmer.direction = direction;
            return getThis();
        }

        public T setShape(int shape) {
            this.mShimmer.shape = shape;
            return getThis();
        }

        public T setFixedWidth(int fixedWidth) {
            if (fixedWidth < 0) {
                throw new IllegalArgumentException("Given invalid width: " + fixedWidth);
            }
            this.mShimmer.fixedWidth = fixedWidth;
            return getThis();
        }

        public T setFixedHeight(int fixedHeight) {
            if (fixedHeight < 0) {
                throw new IllegalArgumentException("Given invalid height: " + fixedHeight);
            }
            this.mShimmer.fixedHeight = fixedHeight;
            return getThis();
        }

        public T setWidthRatio(float widthRatio) {
            if (widthRatio < 0.0f) {
                throw new IllegalArgumentException("Given invalid width ratio: " + widthRatio);
            }
            this.mShimmer.widthRatio = widthRatio;
            return getThis();
        }

        public T setHeightRatio(float heightRatio) {
            if (heightRatio < 0.0f) {
                throw new IllegalArgumentException("Given invalid height ratio: " + heightRatio);
            }
            this.mShimmer.heightRatio = heightRatio;
            return getThis();
        }

        public T setIntensity(float intensity) {
            if (intensity < 0.0f) {
                throw new IllegalArgumentException("Given invalid intensity value: " + intensity);
            }
            this.mShimmer.intensity = intensity;
            return getThis();
        }

        public T setDropoff(float dropoff) {
            if (dropoff < 0.0f) {
                throw new IllegalArgumentException("Given invalid dropoff value: " + dropoff);
            }
            this.mShimmer.dropoff = dropoff;
            return getThis();
        }

        public T setTilt(float tilt) {
            this.mShimmer.tilt = tilt;
            return getThis();
        }

        public T setBaseAlpha(@FloatRange(from = 0.0d, to = 1.0d) float alpha) {
            int intAlpha = (int) (clamp(0.0f, 1.0f, alpha) * 255.0f);
            XShimmer xShimmer = this.mShimmer;
            xShimmer.baseColor = (intAlpha << 24) | (xShimmer.baseColor & 16777215);
            return getThis();
        }

        public T setHighlightAlpha(@FloatRange(from = 0.0d, to = 1.0d) float alpha) {
            int intAlpha = (int) (clamp(0.0f, 1.0f, alpha) * 255.0f);
            XShimmer xShimmer = this.mShimmer;
            xShimmer.highlightColor = (intAlpha << 24) | (xShimmer.highlightColor & 16777215);
            return getThis();
        }

        public T setHighlightColor(@ColorInt int color) {
            return getThis();
        }

        public T setBaseColor(@ColorInt int color) {
            return getThis();
        }

        public T setClipToChildren(boolean status) {
            this.mShimmer.clipToChildren = status;
            return getThis();
        }

        public T setAutoStart(boolean status) {
            this.mShimmer.autoStart = status;
            return getThis();
        }

        public T setRepeatCount(int repeatCount) {
            this.mShimmer.repeatCount = repeatCount;
            return getThis();
        }

        public T setRepeatMode(int mode) {
            this.mShimmer.repeatMode = mode;
            return getThis();
        }

        public T setRepeatDelay(long millis) {
            if (millis < 0) {
                throw new IllegalArgumentException("Given a negative repeat delay: " + millis);
            }
            this.mShimmer.repeatDelay = millis;
            return getThis();
        }

        public T setDuration(long millis) {
            if (millis < 0) {
                throw new IllegalArgumentException("Given a negative duration: " + millis);
            }
            this.mShimmer.animationDuration = millis;
            return getThis();
        }

        public XShimmer build() {
            this.mShimmer.updateColors();
            this.mShimmer.updatePositions();
            return this.mShimmer;
        }

        private static float clamp(float min, float max, float value) {
            return Math.min(max, Math.max(min, value));
        }
    }

    /* loaded from: classes25.dex */
    public static class AlphaHighlightBuilder extends Builder<AlphaHighlightBuilder> {
        public AlphaHighlightBuilder() {
            this.mShimmer.alphaShimmer = true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.xiaopeng.xui.drawable.shimmer.XShimmer.Builder
        public AlphaHighlightBuilder getThis() {
            return this;
        }
    }

    /* loaded from: classes25.dex */
    public static class ColorHighlightBuilder extends Builder<ColorHighlightBuilder> {
        public ColorHighlightBuilder() {
            this.mShimmer.alphaShimmer = false;
        }

        @Override // com.xiaopeng.xui.drawable.shimmer.XShimmer.Builder
        public ColorHighlightBuilder setHighlightColor(@ColorInt int color) {
            this.mShimmer.highlightColor = color;
            return getThis();
        }

        @Override // com.xiaopeng.xui.drawable.shimmer.XShimmer.Builder
        public ColorHighlightBuilder setBaseColor(@ColorInt int color) {
            this.mShimmer.baseColor = (this.mShimmer.baseColor & (-16777216)) | (16777215 & color);
            return getThis();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.xiaopeng.xui.drawable.shimmer.XShimmer.Builder
        public ColorHighlightBuilder consumeAttributes(TypedArray a) {
            super.consumeAttributes(a);
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_base_color)) {
                setBaseColor(a.getColor(R.styleable.XShimmerDrawable_shimmer_base_color, this.mShimmer.baseColor));
            }
            if (a.hasValue(R.styleable.XShimmerDrawable_shimmer_highlight_color)) {
                setHighlightColor(a.getColor(R.styleable.XShimmerDrawable_shimmer_highlight_color, this.mShimmer.highlightColor));
            }
            return getThis();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.xui.drawable.shimmer.XShimmer.Builder
        public ColorHighlightBuilder getThis() {
            return this;
        }
    }
}
