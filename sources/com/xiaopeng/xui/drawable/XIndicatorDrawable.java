package com.xiaopeng.xui.drawable;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.theme.XThemeManager;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes25.dex */
public final class XIndicatorDrawable extends Drawable {
    private static final int INDICATOR_TYPE_LINE = 1;
    private static final int INDICATOR_TYPE_RECT = 0;
    private static final String TAG = XIndicatorDrawable.class.getSimpleName();
    private float mAnimIndicatorEnd;
    private float mAnimIndicatorStart;
    private BlurMaskFilter mBlurMaskFilter;
    private ColorStateList mColorStateList;
    private int mDefaultColor;
    @ColorRes
    private int mIndicatorColorRes;
    private int mIndicatorCount;
    private float mIndicatorEnd;
    private float mIndicatorEndAnimSpeed;
    private float mIndicatorEndDistance;
    private float mIndicatorHeight;
    private float mIndicatorPadding;
    private float mIndicatorPaddingBottom;
    private float mIndicatorPercent;
    private float mIndicatorRadius;
    private final RectF mIndicatorRect;
    private float mIndicatorStart;
    private float mIndicatorStartAnimSpeed;
    private float mIndicatorStartDistance;
    private OnAnimationListener mOnAnimationListener;
    private final ValueAnimator mValueAnimator;
    private int mIndicatorType = 0;
    private int mCurrentSelection = -1;
    private boolean mEnable = true;
    private final Paint mPaint = new Paint(1);

    /* loaded from: classes25.dex */
    public interface OnAnimationListener {
        void onEnd();

        void onStart();
    }

    public XIndicatorDrawable() {
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mIndicatorRect = new RectF();
        this.mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mValueAnimator.setDuration(500L);
        this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.drawable.-$$Lambda$XIndicatorDrawable$RF3IRyape-IxJ1hs8nfgdCdiWyU
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                XIndicatorDrawable.this.lambda$new$0$XIndicatorDrawable(valueAnimator);
            }
        });
        this.mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mValueAnimator.addListener(new Animator.AnimatorListener() { // from class: com.xiaopeng.xui.drawable.XIndicatorDrawable.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                if (XIndicatorDrawable.this.mOnAnimationListener != null) {
                    XIndicatorDrawable.this.mOnAnimationListener.onStart();
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (XIndicatorDrawable.this.mOnAnimationListener != null) {
                    XIndicatorDrawable.this.mOnAnimationListener.onEnd();
                }
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                XIndicatorDrawable xIndicatorDrawable = XIndicatorDrawable.this;
                xIndicatorDrawable.mAnimIndicatorStart = xIndicatorDrawable.mIndicatorStart;
                XIndicatorDrawable xIndicatorDrawable2 = XIndicatorDrawable.this;
                xIndicatorDrawable2.mAnimIndicatorEnd = xIndicatorDrawable2.mIndicatorEnd;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public /* synthetic */ void lambda$new$0$XIndicatorDrawable(ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        float progress = this.mIndicatorStartAnimSpeed * value;
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        this.mIndicatorStart = this.mAnimIndicatorStart - (this.mIndicatorStartDistance * (1.0f - progress));
        float progress2 = this.mIndicatorEndAnimSpeed * value;
        if (progress2 > 1.0f) {
            progress2 = 1.0f;
        }
        this.mIndicatorEnd = this.mAnimIndicatorEnd - (this.mIndicatorEndDistance * (1.0f - progress2));
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (this.mIndicatorType == 1) {
            this.mIndicatorRadius = this.mIndicatorRect.height() / 2.0f;
            this.mIndicatorRect.bottom = bounds.height() - this.mIndicatorPaddingBottom;
            RectF rectF = this.mIndicatorRect;
            rectF.top = rectF.bottom - this.mIndicatorHeight;
        } else {
            this.mIndicatorRadius = 4.0f;
            RectF rectF2 = this.mIndicatorRect;
            rectF2.top = this.mIndicatorPadding;
            rectF2.bottom = bounds.height() - this.mIndicatorPadding;
        }
        setSelection(this.mIndicatorCount, this.mCurrentSelection, false);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(@NonNull Canvas canvas) {
        if (isVisible() && this.mCurrentSelection != -1) {
            RectF rectF = this.mIndicatorRect;
            rectF.left = this.mIndicatorStart;
            rectF.right = this.mIndicatorEnd;
            if (this.mIndicatorType == 1) {
                this.mPaint.setMaskFilter(this.mBlurMaskFilter);
            } else {
                this.mPaint.setMaskFilter(null);
            }
            RectF rectF2 = this.mIndicatorRect;
            float f = this.mIndicatorRadius;
            canvas.drawRoundRect(rectF2, f, f, this.mPaint);
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        super.onStateChange(state);
        setPaintColor();
        return true;
    }

    private void setPaintColor() {
        ColorStateList colorStateList;
        Paint paint = this.mPaint;
        if (paint != null && (colorStateList = this.mColorStateList) != null) {
            if (this.mEnable) {
                paint.setColor(colorStateList.getColorForState(getState(), this.mDefaultColor));
            } else {
                paint.setColor(colorStateList.getColorForState(StateSet.WILD_CARD, this.mDefaultColor));
            }
        }
    }

    public void setEnable(boolean enable) {
        if (enable != this.mEnable) {
            this.mEnable = enable;
            setPaintColor();
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        return super.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs) throws IOException, XmlPullParserException {
        inflateAttrs(r, attrs, null);
        super.inflate(r, parser, attrs);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, @Nullable Resources.Theme theme) throws IOException, XmlPullParserException {
        inflateAttrs(r, attrs, theme);
        super.inflate(r, parser, attrs, theme);
    }

    public void setSelection(int indicatorCount, int selection, boolean animation) {
        float padding;
        this.mIndicatorCount = indicatorCount;
        this.mCurrentSelection = selection;
        Rect rect = getBounds();
        if (this.mIndicatorCount > this.mCurrentSelection && rect.width() > 0) {
            if (this.mValueAnimator.isRunning()) {
                this.mValueAnimator.cancel();
            }
            float segmentLength = rect.width();
            if (this.mIndicatorCount != 0) {
                segmentLength = rect.width() / this.mIndicatorCount;
            }
            this.mAnimIndicatorStart = this.mCurrentSelection * segmentLength;
            this.mAnimIndicatorEnd = this.mAnimIndicatorStart + segmentLength;
            if (this.mIndicatorType == 1) {
                padding = ((1.0f - this.mIndicatorPercent) * segmentLength) / 2.0f;
            } else {
                padding = this.mIndicatorPadding;
            }
            this.mAnimIndicatorStart += padding;
            this.mAnimIndicatorEnd -= padding;
            if (this.mIndicatorStart == this.mAnimIndicatorStart && this.mIndicatorEnd == this.mAnimIndicatorEnd) {
                return;
            }
            if (animation) {
                startAnimation();
                return;
            }
            this.mIndicatorStart = this.mAnimIndicatorStart;
            this.mIndicatorEnd = this.mAnimIndicatorEnd;
            invalidateSelf();
        }
    }

    private void startAnimation() {
        if (this.mAnimIndicatorStart <= this.mIndicatorStart && this.mAnimIndicatorEnd <= this.mIndicatorEnd) {
            this.mIndicatorStartAnimSpeed = 2.0f;
            this.mIndicatorEndAnimSpeed = 1.0f;
        } else if (this.mAnimIndicatorStart >= this.mIndicatorStart && this.mAnimIndicatorEnd >= this.mIndicatorEnd) {
            this.mIndicatorStartAnimSpeed = 1.0f;
            this.mIndicatorEndAnimSpeed = 2.0f;
        } else {
            this.mIndicatorStartAnimSpeed = 1.0f;
            this.mIndicatorEndAnimSpeed = 1.0f;
        }
        this.mIndicatorStartDistance = this.mAnimIndicatorStart - this.mIndicatorStart;
        this.mIndicatorEndDistance = this.mAnimIndicatorEnd - this.mIndicatorEnd;
        this.mValueAnimator.start();
    }

    public void inflateAttrs(Resources resources, AttributeSet attrs, Resources.Theme theme) {
        TypedArray typedArray;
        if (resources != null && attrs != null) {
            if (theme != null) {
                typedArray = theme.obtainStyledAttributes(attrs, R.styleable.XSegmented, 0, 0);
            } else {
                typedArray = resources.obtainAttributes(attrs, R.styleable.XSegmented);
            }
            this.mIndicatorColorRes = typedArray.getResourceId(R.styleable.XSegmented_segment_indicator_color, R.color.x_segment_indicator_color);
            this.mIndicatorType = typedArray.getInt(R.styleable.XSegmented_segment_indicator_type, 0);
            onConfigurationChanged(resources, theme);
            if (this.mIndicatorType == 1) {
                this.mIndicatorPercent = typedArray.getFloat(R.styleable.XSegmented_segment_line_width_percent, 1.0f);
                this.mIndicatorHeight = dp(resources, 4);
                this.mIndicatorPaddingBottom = typedArray.getDimension(R.styleable.XSegmented_segment_line_padding_bottom, dp(resources, 6));
                if (this.mIndicatorPercent > 1.0f) {
                    this.mIndicatorPercent = 1.0f;
                }
            } else {
                this.mIndicatorPadding = typedArray.getDimension(R.styleable.XSegmented_segment_indicator_padding, 0.0f);
            }
            typedArray.recycle();
        }
    }

    public void onConfigurationChanged(Resources resources, Resources.Theme theme) {
        this.mColorStateList = resources.getColorStateList(this.mIndicatorColorRes, theme);
        this.mDefaultColor = resources.getColor(R.color.x_segment_indicator_color, theme);
        setPaintColor();
        if (XThemeManager.isNight(resources.getConfiguration()) && this.mIndicatorType == 1) {
            this.mBlurMaskFilter = new BlurMaskFilter(dp(resources, 4), BlurMaskFilter.Blur.SOLID);
        } else {
            this.mBlurMaskFilter = null;
        }
        invalidateSelf();
    }

    private float dp(Resources resources, int value) {
        return TypedValue.applyDimension(1, value, resources.getDisplayMetrics());
    }

    public void setOnAnimationListener(OnAnimationListener onAnimationListener) {
        this.mOnAnimationListener = onAnimationListener;
    }
}
