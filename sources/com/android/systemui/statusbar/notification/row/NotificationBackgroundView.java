package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.View;
import com.android.internal.util.ArrayUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
/* loaded from: classes21.dex */
public class NotificationBackgroundView extends View {
    private int mActualHeight;
    private float mActualWidth;
    private Drawable mBackground;
    private int mBackgroundTop;
    private boolean mBottomAmountClips;
    private boolean mBottomIsRounded;
    private int mClipBottomAmount;
    private int mClipTopAmount;
    private float[] mCornerRadii;
    private float mDistanceToTopRoundness;
    private final boolean mDontModifyCorners;
    private int mDrawableAlpha;
    private boolean mExpandAnimationRunning;
    private boolean mFirstInSection;
    private boolean mIsPressedAllowed;
    private boolean mLastInSection;
    private int mTintColor;
    private boolean mTopAmountRounded;

    public NotificationBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCornerRadii = new float[8];
        this.mBottomAmountClips = true;
        this.mDrawableAlpha = 255;
        this.mDontModifyCorners = getResources().getBoolean(R.bool.config_clipNotificationsToOutline);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mClipTopAmount + this.mClipBottomAmount < this.mActualHeight - this.mBackgroundTop || this.mExpandAnimationRunning) {
            canvas.save();
            if (!this.mExpandAnimationRunning) {
                canvas.clipRect(0, this.mClipTopAmount, getWidth(), this.mActualHeight - this.mClipBottomAmount);
            }
            draw(canvas, this.mBackground);
            canvas.restore();
        }
    }

    private void draw(Canvas canvas, Drawable drawable) {
        if (drawable != null) {
            int top = this.mBackgroundTop;
            int bottom = this.mActualHeight;
            if (this.mBottomIsRounded && this.mBottomAmountClips && !this.mExpandAnimationRunning && !this.mLastInSection) {
                bottom -= this.mClipBottomAmount;
            }
            int left = 0;
            int right = getWidth();
            if (this.mExpandAnimationRunning) {
                float f = this.mActualWidth;
                left = (int) ((getWidth() - f) / 2.0f);
                right = (int) (left + f);
            }
            if (this.mTopAmountRounded) {
                int clipTop = (int) (this.mClipTopAmount - this.mDistanceToTopRoundness);
                if (clipTop >= 0 || !this.mFirstInSection) {
                    top += clipTop;
                }
                if (clipTop >= 0 && !this.mLastInSection) {
                    bottom += clipTop;
                }
            }
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mBackground;
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        setState(getDrawableState());
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float x, float y) {
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setHotspot(x, y);
        }
    }

    public void setCustomBackground(Drawable background) {
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setCallback(null);
            unscheduleDrawable(this.mBackground);
        }
        this.mBackground = background;
        this.mBackground.mutate();
        Drawable drawable2 = this.mBackground;
        if (drawable2 != null) {
            drawable2.setCallback(this);
            setTint(this.mTintColor);
        }
        Drawable drawable3 = this.mBackground;
        if (drawable3 instanceof RippleDrawable) {
            ((RippleDrawable) drawable3).setForceSoftware(true);
        }
        updateBackgroundRadii();
        invalidate();
    }

    public void setCustomBackground(int drawableResId) {
        Drawable d = this.mContext.getDrawable(drawableResId);
        setCustomBackground(d);
    }

    public void setTint(int tintColor) {
        if (tintColor != 0) {
            this.mBackground.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            this.mBackground.clearColorFilter();
        }
        this.mTintColor = tintColor;
        invalidate();
    }

    public void setActualHeight(int actualHeight) {
        if (this.mExpandAnimationRunning) {
            return;
        }
        this.mActualHeight = actualHeight;
        invalidate();
    }

    public int getActualHeight() {
        return this.mActualHeight;
    }

    public void setClipTopAmount(int clipTopAmount) {
        this.mClipTopAmount = clipTopAmount;
        invalidate();
    }

    public void setClipBottomAmount(int clipBottomAmount) {
        this.mClipBottomAmount = clipBottomAmount;
        invalidate();
    }

    public void setDistanceToTopRoundness(float distanceToTopRoundness) {
        if (distanceToTopRoundness != this.mDistanceToTopRoundness) {
            this.mTopAmountRounded = distanceToTopRoundness >= 0.0f;
            this.mDistanceToTopRoundness = distanceToTopRoundness;
            invalidate();
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setState(int[] drawableState) {
        Drawable drawable = this.mBackground;
        if (drawable != null && drawable.isStateful()) {
            if (!this.mIsPressedAllowed) {
                drawableState = ArrayUtils.removeInt(drawableState, 16842919);
            }
            this.mBackground.setState(drawableState);
        }
    }

    public void setRippleColor(int color) {
        Drawable drawable = this.mBackground;
        if (drawable instanceof RippleDrawable) {
            RippleDrawable ripple = (RippleDrawable) drawable;
            ripple.setColor(ColorStateList.valueOf(color));
        }
    }

    public void setDrawableAlpha(int drawableAlpha) {
        this.mDrawableAlpha = drawableAlpha;
        if (this.mExpandAnimationRunning) {
            return;
        }
        this.mBackground.setAlpha(drawableAlpha);
    }

    public void setRoundness(float topRoundness, float bottomRoundness) {
        float[] fArr = this.mCornerRadii;
        if (topRoundness == fArr[0] && bottomRoundness == fArr[4]) {
            return;
        }
        this.mBottomIsRounded = bottomRoundness != 0.0f;
        float[] fArr2 = this.mCornerRadii;
        fArr2[0] = topRoundness;
        fArr2[1] = topRoundness;
        fArr2[2] = topRoundness;
        fArr2[3] = topRoundness;
        fArr2[4] = bottomRoundness;
        fArr2[5] = bottomRoundness;
        fArr2[6] = bottomRoundness;
        fArr2[7] = bottomRoundness;
        updateBackgroundRadii();
    }

    public void setBottomAmountClips(boolean clips) {
        if (clips != this.mBottomAmountClips) {
            this.mBottomAmountClips = clips;
            invalidate();
        }
    }

    public void setLastInSection(boolean lastInSection) {
        this.mLastInSection = lastInSection;
        invalidate();
    }

    public void setFirstInSection(boolean firstInSection) {
        this.mFirstInSection = firstInSection;
        invalidate();
    }

    private void updateBackgroundRadii() {
        if (this.mDontModifyCorners) {
            return;
        }
        Drawable drawable = this.mBackground;
        if (drawable instanceof LayerDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) ((LayerDrawable) drawable).getDrawable(0);
            gradientDrawable.setCornerRadii(this.mCornerRadii);
        }
    }

    public void setBackgroundTop(int backgroundTop) {
        this.mBackgroundTop = backgroundTop;
        invalidate();
    }

    public void setExpandAnimationParams(ActivityLaunchAnimator.ExpandAnimationParameters params) {
        this.mActualHeight = params.getHeight();
        this.mActualWidth = params.getWidth();
        float alphaProgress = Interpolators.ALPHA_IN.getInterpolation(params.getProgress(67L, 200L));
        this.mBackground.setAlpha((int) (this.mDrawableAlpha * (1.0f - alphaProgress)));
        invalidate();
    }

    public void setExpandAnimationRunning(boolean running) {
        this.mExpandAnimationRunning = running;
        Drawable drawable = this.mBackground;
        if (drawable instanceof LayerDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) ((LayerDrawable) drawable).getDrawable(0);
            gradientDrawable.setXfermode(running ? new PorterDuffXfermode(PorterDuff.Mode.SRC) : null);
            gradientDrawable.setAntiAlias(!running);
        }
        if (!this.mExpandAnimationRunning) {
            setDrawableAlpha(this.mDrawableAlpha);
        }
        invalidate();
    }

    public void setPressedAllowed(boolean allowed) {
        this.mIsPressedAllowed = allowed;
    }
}
