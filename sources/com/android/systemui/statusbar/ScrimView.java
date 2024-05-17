package com.android.systemui.statusbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.colorextraction.drawable.ScrimDrawable;
/* loaded from: classes21.dex */
public class ScrimView extends View {
    private Runnable mChangeRunnable;
    private PorterDuffColorFilter mColorFilter;
    private final ColorExtractor.GradientColors mColors;
    private Drawable mDrawable;
    private int mTintColor;
    private float mViewAlpha;

    public ScrimView(Context context) {
        this(context, null);
    }

    public ScrimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrimView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ScrimView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mViewAlpha = 1.0f;
        this.mDrawable = new ScrimDrawable();
        this.mDrawable.setCallback(this);
        this.mColors = new ColorExtractor.GradientColors();
        updateColorWithTint(false);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mDrawable.getAlpha() > 0) {
            this.mDrawable.draw(canvas);
        }
    }

    public void setDrawable(Drawable drawable) {
        this.mDrawable = drawable;
        this.mDrawable.setCallback(this);
        this.mDrawable.setBounds(getLeft(), getTop(), getRight(), getBottom());
        this.mDrawable.setAlpha((int) (this.mViewAlpha * 255.0f));
        invalidate();
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        super.invalidateDrawable(drawable);
        if (drawable == this.mDrawable) {
            invalidate();
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            this.mDrawable.setBounds(left, top, right, bottom);
            invalidate();
        }
    }

    public void setColors(ColorExtractor.GradientColors colors) {
        setColors(colors, false);
    }

    public void setColors(ColorExtractor.GradientColors colors, boolean animated) {
        if (colors == null) {
            throw new IllegalArgumentException("Colors cannot be null");
        }
        if (this.mColors.equals(colors)) {
            return;
        }
        this.mColors.set(colors);
        updateColorWithTint(animated);
    }

    @VisibleForTesting
    Drawable getDrawable() {
        return this.mDrawable;
    }

    public ColorExtractor.GradientColors getColors() {
        return this.mColors;
    }

    public void setTint(int color) {
        setTint(color, false);
    }

    public void setTint(int color, boolean animated) {
        if (this.mTintColor == color) {
            return;
        }
        this.mTintColor = color;
        updateColorWithTint(animated);
    }

    private void updateColorWithTint(boolean animated) {
        ScrimDrawable scrimDrawable = this.mDrawable;
        if (scrimDrawable instanceof ScrimDrawable) {
            ScrimDrawable drawable = scrimDrawable;
            float tintAmount = Color.alpha(this.mTintColor) / 255.0f;
            int mainTinted = ColorUtils.blendARGB(this.mColors.getMainColor(), this.mTintColor, tintAmount);
            drawable.setColor(mainTinted, animated);
        } else {
            boolean hasAlpha = Color.alpha(this.mTintColor) != 0;
            if (hasAlpha) {
                PorterDuffColorFilter porterDuffColorFilter = this.mColorFilter;
                PorterDuff.Mode targetMode = porterDuffColorFilter == null ? PorterDuff.Mode.SRC_OVER : porterDuffColorFilter.getMode();
                PorterDuffColorFilter porterDuffColorFilter2 = this.mColorFilter;
                if (porterDuffColorFilter2 == null || porterDuffColorFilter2.getColor() != this.mTintColor) {
                    this.mColorFilter = new PorterDuffColorFilter(this.mTintColor, targetMode);
                }
            } else {
                this.mColorFilter = null;
            }
            this.mDrawable.setColorFilter(this.mColorFilter);
            this.mDrawable.invalidateSelf();
        }
        Runnable runnable = this.mChangeRunnable;
        if (runnable != null) {
            runnable.run();
        }
    }

    public int getTint() {
        return this.mTintColor;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setViewAlpha(float alpha) {
        if (Float.isNaN(alpha)) {
            throw new IllegalArgumentException("alpha cannot be NaN");
        }
        if (alpha != this.mViewAlpha) {
            this.mViewAlpha = alpha;
            this.mDrawable.setAlpha((int) (255.0f * alpha));
            Runnable runnable = this.mChangeRunnable;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public float getViewAlpha() {
        return this.mViewAlpha;
    }

    public void setChangeRunnable(Runnable changeRunnable) {
        this.mChangeRunnable = changeRunnable;
    }

    protected boolean canReceivePointerEvents() {
        return false;
    }
}
