package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import com.android.settingslib.Utils;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes21.dex */
public class BarTransitions {
    public static final int BACKGROUND_DURATION = 200;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_COLORS = false;
    public static final int LIGHTS_IN_DURATION = 250;
    public static final int LIGHTS_OUT_DURATION = 1500;
    public static final int MODE_LIGHTS_OUT = 3;
    public static final int MODE_LIGHTS_OUT_TRANSPARENT = 6;
    public static final int MODE_OPAQUE = 0;
    public static final int MODE_SEMI_TRANSPARENT = 1;
    public static final int MODE_TRANSLUCENT = 2;
    public static final int MODE_TRANSPARENT = 4;
    public static final int MODE_WARNING = 5;
    private boolean mAlwaysOpaque = false;
    protected final BarBackgroundDrawable mBarBackground;
    private int mMode;
    private final String mTag;
    private final View mView;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface TransitionMode {
    }

    public BarTransitions(View view, int gradientResourceId) {
        this.mTag = "BarTransitions." + view.getClass().getSimpleName();
        this.mView = view;
        this.mBarBackground = new BarBackgroundDrawable(this.mView.getContext(), gradientResourceId);
        this.mView.setBackground(this.mBarBackground);
    }

    public void destroy() {
    }

    public int getMode() {
        return this.mMode;
    }

    public void setAutoDim(boolean autoDim) {
    }

    public void setAlwaysOpaque(boolean alwaysOpaque) {
        this.mAlwaysOpaque = alwaysOpaque;
    }

    public boolean isAlwaysOpaque() {
        return this.mAlwaysOpaque;
    }

    public void transitionTo(int mode, boolean animate) {
        if (isAlwaysOpaque() && (mode == 1 || mode == 2 || mode == 4)) {
            mode = 0;
        }
        if (isAlwaysOpaque() && mode == 6) {
            mode = 3;
        }
        if (this.mMode == mode) {
            return;
        }
        int oldMode = this.mMode;
        this.mMode = mode;
        onTransition(oldMode, this.mMode, animate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onTransition(int oldMode, int newMode, boolean animate) {
        applyModeBackground(oldMode, newMode, animate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyModeBackground(int oldMode, int newMode, boolean animate) {
        this.mBarBackground.applyModeBackground(oldMode, newMode, animate);
    }

    public static String modeToString(int mode) {
        if (mode == 0) {
            return "MODE_OPAQUE";
        }
        if (mode == 1) {
            return "MODE_SEMI_TRANSPARENT";
        }
        if (mode == 2) {
            return "MODE_TRANSLUCENT";
        }
        if (mode == 3) {
            return "MODE_LIGHTS_OUT";
        }
        if (mode == 4) {
            return "MODE_TRANSPARENT";
        }
        if (mode == 5) {
            return "MODE_WARNING";
        }
        if (mode == 6) {
            return "MODE_LIGHTS_OUT_TRANSPARENT";
        }
        throw new IllegalArgumentException("Unknown mode " + mode);
    }

    public void finishAnimations() {
        this.mBarBackground.finishAnimation();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isLightsOut(int mode) {
        return mode == 3 || mode == 6;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public static class BarBackgroundDrawable extends Drawable {
        private boolean mAnimating;
        private int mColor;
        private int mColorStart;
        private long mEndTime;
        private Rect mFrame;
        private final Drawable mGradient;
        private int mGradientAlpha;
        private int mGradientAlphaStart;
        private final int mOpaque;
        private final int mSemiTransparent;
        private long mStartTime;
        private PorterDuffColorFilter mTintFilter;
        private final int mTransparent;
        private final int mWarning;
        private int mMode = -1;
        private Paint mPaint = new Paint();

        public BarBackgroundDrawable(Context context, int gradientResourceId) {
            context.getResources();
            this.mOpaque = context.getColor(R.color.system_bar_background_opaque);
            this.mSemiTransparent = context.getColor(17170985);
            this.mTransparent = context.getColor(R.color.system_bar_background_transparent);
            this.mWarning = Utils.getColorAttrDefaultColor(context, 16844099);
            this.mGradient = context.getDrawable(gradientResourceId);
        }

        public void setFrame(Rect frame) {
            this.mFrame = frame;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setTint(int color) {
            PorterDuffColorFilter porterDuffColorFilter = this.mTintFilter;
            PorterDuff.Mode targetMode = porterDuffColorFilter == null ? PorterDuff.Mode.SRC_IN : porterDuffColorFilter.getMode();
            PorterDuffColorFilter porterDuffColorFilter2 = this.mTintFilter;
            if (porterDuffColorFilter2 == null || porterDuffColorFilter2.getColor() != color) {
                this.mTintFilter = new PorterDuffColorFilter(color, targetMode);
            }
            invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable
        public void setTintMode(PorterDuff.Mode tintMode) {
            PorterDuffColorFilter porterDuffColorFilter = this.mTintFilter;
            int targetColor = porterDuffColorFilter == null ? 0 : porterDuffColorFilter.getColor();
            PorterDuffColorFilter porterDuffColorFilter2 = this.mTintFilter;
            if (porterDuffColorFilter2 == null || porterDuffColorFilter2.getMode() != tintMode) {
                this.mTintFilter = new PorterDuffColorFilter(targetColor, tintMode);
            }
            invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            this.mGradient.setBounds(bounds);
        }

        public void applyModeBackground(int oldMode, int newMode, boolean animate) {
            if (this.mMode == newMode) {
                return;
            }
            this.mMode = newMode;
            this.mAnimating = animate;
            if (animate) {
                long now = SystemClock.elapsedRealtime();
                this.mStartTime = now;
                this.mEndTime = 200 + now;
                this.mGradientAlphaStart = this.mGradientAlpha;
                this.mColorStart = this.mColor;
            }
            invalidateSelf();
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }

        public void finishAnimation() {
            if (this.mAnimating) {
                this.mAnimating = false;
                invalidateSelf();
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int targetColor;
            int i = this.mMode;
            if (i == 5) {
                targetColor = this.mWarning;
            } else if (i == 2) {
                targetColor = this.mSemiTransparent;
            } else if (i == 1) {
                targetColor = this.mSemiTransparent;
            } else if (i == 4 || i == 6) {
                targetColor = this.mTransparent;
            } else {
                targetColor = this.mOpaque;
            }
            if (!this.mAnimating) {
                this.mColor = targetColor;
                this.mGradientAlpha = 0;
            } else {
                long now = SystemClock.elapsedRealtime();
                long j = this.mEndTime;
                if (now >= j) {
                    this.mAnimating = false;
                    this.mColor = targetColor;
                    this.mGradientAlpha = 0;
                } else {
                    long j2 = this.mStartTime;
                    float t = ((float) (now - j2)) / ((float) (j - j2));
                    float v = Math.max(0.0f, Math.min(Interpolators.LINEAR.getInterpolation(t), 1.0f));
                    this.mGradientAlpha = (int) ((0 * v) + (this.mGradientAlphaStart * (1.0f - v)));
                    this.mColor = Color.argb((int) ((Color.alpha(targetColor) * v) + (Color.alpha(this.mColorStart) * (1.0f - v))), (int) ((Color.red(targetColor) * v) + (Color.red(this.mColorStart) * (1.0f - v))), (int) ((Color.green(targetColor) * v) + (Color.green(this.mColorStart) * (1.0f - v))), (int) ((Color.blue(targetColor) * v) + (Color.blue(this.mColorStart) * (1.0f - v))));
                }
            }
            int i2 = this.mGradientAlpha;
            if (i2 > 0) {
                this.mGradient.setAlpha(i2);
                this.mGradient.draw(canvas);
            }
            if (Color.alpha(this.mColor) > 0) {
                this.mPaint.setColor(this.mColor);
                PorterDuffColorFilter porterDuffColorFilter = this.mTintFilter;
                if (porterDuffColorFilter != null) {
                    this.mPaint.setColorFilter(porterDuffColorFilter);
                }
                Rect rect = this.mFrame;
                if (rect != null) {
                    canvas.drawRect(rect, this.mPaint);
                } else {
                    canvas.drawPaint(this.mPaint);
                }
            }
            if (this.mAnimating) {
                invalidateSelf();
            }
        }
    }
}
