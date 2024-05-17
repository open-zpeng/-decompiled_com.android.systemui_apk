package com.android.systemui.qs;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.FloatProperty;
/* loaded from: classes21.dex */
public class SlashDrawable extends Drawable {
    private static final float CENTER_X = 10.65f;
    private static final float CENTER_Y = 11.869239f;
    public static final float CORNER_RADIUS = 1.0f;
    private static final float DEFAULT_ROTATION = -45.0f;
    private static final float LEFT = 0.40544835f;
    private static final float RIGHT = 0.4820516f;
    private static final float SCALE = 24.0f;
    private static final float SLASH_HEIGHT = 28.0f;
    private static final float SLASH_WIDTH = 1.8384776f;
    private static final float TOP = -0.088781714f;
    private float mCurrentSlashLength;
    private Drawable mDrawable;
    private float mRotation;
    private boolean mSlashed;
    private ColorStateList mTintList;
    private PorterDuff.Mode mTintMode;
    private final Path mPath = new Path();
    private final Paint mPaint = new Paint(1);
    private final RectF mSlashRect = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
    private boolean mAnimationEnabled = true;
    private final FloatProperty mSlashLengthProp = new FloatProperty<SlashDrawable>("slashLength") { // from class: com.android.systemui.qs.SlashDrawable.1
        @Override // android.util.FloatProperty
        public void setValue(SlashDrawable object, float value) {
            object.mCurrentSlashLength = value;
        }

        @Override // android.util.Property
        public Float get(SlashDrawable object) {
            return Float.valueOf(object.mCurrentSlashLength);
        }
    };

    public SlashDrawable(Drawable d) {
        this.mDrawable = d;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.mDrawable.setBounds(bounds);
    }

    public void setDrawable(Drawable d) {
        this.mDrawable = d;
        this.mDrawable.setCallback(getCallback());
        this.mDrawable.setBounds(getBounds());
        PorterDuff.Mode mode = this.mTintMode;
        if (mode != null) {
            this.mDrawable.setTintMode(mode);
        }
        ColorStateList colorStateList = this.mTintList;
        if (colorStateList != null) {
            this.mDrawable.setTintList(colorStateList);
        }
        invalidateSelf();
    }

    public void setRotation(float rotation) {
        if (this.mRotation == rotation) {
            return;
        }
        this.mRotation = rotation;
        invalidateSelf();
    }

    public void setAnimationEnabled(boolean enabled) {
        this.mAnimationEnabled = enabled;
    }

    public void setSlashed(boolean slashed) {
        if (this.mSlashed == slashed) {
            return;
        }
        this.mSlashed = slashed;
        float end = this.mSlashed ? 1.1666666f : 0.0f;
        float start = this.mSlashed ? 0.0f : 1.1666666f;
        if (this.mAnimationEnabled) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(this, this.mSlashLengthProp, start, end);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.qs.-$$Lambda$SlashDrawable$d6ImpYshN38WeANK1PRMKepeaRo
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    SlashDrawable.this.lambda$setSlashed$0$SlashDrawable(valueAnimator);
                }
            });
            anim.setDuration(350L);
            anim.start();
            return;
        }
        this.mCurrentSlashLength = end;
        invalidateSelf();
    }

    public /* synthetic */ void lambda$setSlashed$0$SlashDrawable(ValueAnimator valueAnimator) {
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        canvas.save();
        Matrix m = new Matrix();
        int width = getBounds().width();
        int height = getBounds().height();
        float radiusX = scale(1.0f, width);
        float radiusY = scale(1.0f, height);
        updateRect(scale(LEFT, width), scale(TOP, height), scale(RIGHT, width), scale(this.mCurrentSlashLength + TOP, height));
        this.mPath.reset();
        this.mPath.addRoundRect(this.mSlashRect, radiusX, radiusY, Path.Direction.CW);
        m.setRotate(this.mRotation + DEFAULT_ROTATION, width / 2, height / 2);
        this.mPath.transform(m);
        canvas.drawPath(this.mPath, this.mPaint);
        m.setRotate((-this.mRotation) - DEFAULT_ROTATION, width / 2, height / 2);
        this.mPath.transform(m);
        m.setTranslate(this.mSlashRect.width(), 0.0f);
        this.mPath.transform(m);
        this.mPath.addRoundRect(this.mSlashRect, width * 1.0f, height * 1.0f, Path.Direction.CW);
        m.setRotate(this.mRotation + DEFAULT_ROTATION, width / 2, height / 2);
        this.mPath.transform(m);
        canvas.clipOutPath(this.mPath);
        this.mDrawable.draw(canvas);
        canvas.restore();
    }

    private float scale(float frac, int width) {
        return width * frac;
    }

    private void updateRect(float left, float top, float right, float bottom) {
        RectF rectF = this.mSlashRect;
        rectF.left = left;
        rectF.top = top;
        rectF.right = right;
        rectF.bottom = bottom;
    }

    @Override // android.graphics.drawable.Drawable
    public void setTint(int tintColor) {
        super.setTint(tintColor);
        this.mDrawable.setTint(tintColor);
        this.mPaint.setColor(tintColor);
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList tint) {
        this.mTintList = tint;
        super.setTintList(tint);
        setDrawableTintList(tint);
        this.mPaint.setColor(tint.getDefaultColor());
        invalidateSelf();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setDrawableTintList(ColorStateList tint) {
        this.mDrawable.setTintList(tint);
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintMode(PorterDuff.Mode tintMode) {
        this.mTintMode = tintMode;
        super.setTintMode(tintMode);
        this.mDrawable.setTintMode(tintMode);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mDrawable.setAlpha(alpha);
        this.mPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mDrawable.setColorFilter(colorFilter);
        this.mPaint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 255;
    }
}
