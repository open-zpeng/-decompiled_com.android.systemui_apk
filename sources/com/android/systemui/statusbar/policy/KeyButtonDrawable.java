package com.android.systemui.statusbar.policy;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.FloatProperty;
import android.view.ContextThemeWrapper;
import com.android.settingslib.Utils;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class KeyButtonDrawable extends Drawable {
    public static final FloatProperty<KeyButtonDrawable> KEY_DRAWABLE_ROTATE = new FloatProperty<KeyButtonDrawable>("KeyButtonRotation") { // from class: com.android.systemui.statusbar.policy.KeyButtonDrawable.1
        @Override // android.util.FloatProperty
        public void setValue(KeyButtonDrawable drawable, float degree) {
            drawable.setRotation(degree);
        }

        @Override // android.util.Property
        public Float get(KeyButtonDrawable drawable) {
            return Float.valueOf(drawable.getRotation());
        }
    };
    public static final FloatProperty<KeyButtonDrawable> KEY_DRAWABLE_TRANSLATE_Y = new FloatProperty<KeyButtonDrawable>("KeyButtonTranslateY") { // from class: com.android.systemui.statusbar.policy.KeyButtonDrawable.2
        @Override // android.util.FloatProperty
        public void setValue(KeyButtonDrawable drawable, float y) {
            drawable.setTranslationY(y);
        }

        @Override // android.util.Property
        public Float get(KeyButtonDrawable drawable) {
            return Float.valueOf(drawable.getTranslationY());
        }
    };
    private AnimatedVectorDrawable mAnimatedDrawable;
    private final Paint mIconPaint;
    private final Paint mShadowPaint;
    private final ShadowDrawableState mState;

    public KeyButtonDrawable(Drawable d, int lightColor, int darkColor, boolean horizontalFlip, Color ovalBackgroundColor) {
        this(d, new ShadowDrawableState(lightColor, darkColor, d instanceof AnimatedVectorDrawable, horizontalFlip, ovalBackgroundColor));
    }

    private KeyButtonDrawable(Drawable d, ShadowDrawableState state) {
        this.mIconPaint = new Paint(3);
        this.mShadowPaint = new Paint(3);
        this.mState = state;
        if (d != null) {
            this.mState.mBaseHeight = d.getIntrinsicHeight();
            this.mState.mBaseWidth = d.getIntrinsicWidth();
            this.mState.mChangingConfigurations = d.getChangingConfigurations();
            this.mState.mChildState = d.getConstantState();
        }
        if (canAnimate()) {
            this.mAnimatedDrawable = (AnimatedVectorDrawable) this.mState.mChildState.newDrawable().mutate();
            setDrawableBounds(this.mAnimatedDrawable);
        }
    }

    public void setDarkIntensity(float intensity) {
        this.mState.mDarkIntensity = intensity;
        int color = ((Integer) ArgbEvaluator.getInstance().evaluate(intensity, Integer.valueOf(this.mState.mLightColor), Integer.valueOf(this.mState.mDarkColor))).intValue();
        updateShadowAlpha();
        setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
    }

    public void setRotation(float degrees) {
        if (!canAnimate() && this.mState.mRotateDegrees != degrees) {
            this.mState.mRotateDegrees = degrees;
            invalidateSelf();
        }
    }

    public void setTranslationX(float x) {
        setTranslation(x, this.mState.mTranslationY);
    }

    public void setTranslationY(float y) {
        setTranslation(this.mState.mTranslationX, y);
    }

    public void setTranslation(float x, float y) {
        if (this.mState.mTranslationX != x || this.mState.mTranslationY != y) {
            ShadowDrawableState shadowDrawableState = this.mState;
            shadowDrawableState.mTranslationX = x;
            shadowDrawableState.mTranslationY = y;
            invalidateSelf();
        }
    }

    public void setShadowProperties(int x, int y, int size, int color) {
        if (canAnimate()) {
            return;
        }
        if (this.mState.mShadowOffsetX != x || this.mState.mShadowOffsetY != y || this.mState.mShadowSize != size || this.mState.mShadowColor != color) {
            ShadowDrawableState shadowDrawableState = this.mState;
            shadowDrawableState.mShadowOffsetX = x;
            shadowDrawableState.mShadowOffsetY = y;
            shadowDrawableState.mShadowSize = size;
            shadowDrawableState.mShadowColor = color;
            this.mShadowPaint.setColorFilter(new PorterDuffColorFilter(shadowDrawableState.mShadowColor, PorterDuff.Mode.SRC_ATOP));
            updateShadowAlpha();
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mState.mAlpha = alpha;
        this.mIconPaint.setAlpha(alpha);
        updateShadowAlpha();
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mIconPaint.setColorFilter(colorFilter);
        if (this.mAnimatedDrawable != null) {
            if (hasOvalBg()) {
                this.mAnimatedDrawable.setColorFilter(new PorterDuffColorFilter(this.mState.mLightColor, PorterDuff.Mode.SRC_IN));
            } else {
                this.mAnimatedDrawable.setColorFilter(colorFilter);
            }
        }
        invalidateSelf();
    }

    public float getDarkIntensity() {
        return this.mState.mDarkIntensity;
    }

    public float getRotation() {
        return this.mState.mRotateDegrees;
    }

    public float getTranslationX() {
        return this.mState.mTranslationX;
    }

    public float getTranslationY() {
        return this.mState.mTranslationY;
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mState;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mState.mBaseHeight + ((this.mState.mShadowSize + Math.abs(this.mState.mShadowOffsetY)) * 2);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mState.mBaseWidth + ((this.mState.mShadowSize + Math.abs(this.mState.mShadowOffsetX)) * 2);
    }

    public boolean canAnimate() {
        return this.mState.mSupportsAnimation;
    }

    public void startAnimation() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mAnimatedDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.start();
        }
    }

    public void resetAnimation() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mAnimatedDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.reset();
        }
    }

    public void clearAnimationCallbacks() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mAnimatedDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.clearAnimationCallbacks();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds.isEmpty()) {
            return;
        }
        AnimatedVectorDrawable animatedVectorDrawable = this.mAnimatedDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.draw(canvas);
            return;
        }
        boolean hwBitmapChanged = this.mState.mIsHardwareBitmap != canvas.isHardwareAccelerated();
        if (hwBitmapChanged) {
            this.mState.mIsHardwareBitmap = canvas.isHardwareAccelerated();
        }
        if (this.mState.mLastDrawnIcon == null || hwBitmapChanged) {
            regenerateBitmapIconCache();
        }
        canvas.save();
        canvas.translate(this.mState.mTranslationX, this.mState.mTranslationY);
        canvas.rotate(this.mState.mRotateDegrees, getIntrinsicWidth() / 2, getIntrinsicHeight() / 2);
        if (this.mState.mShadowSize > 0) {
            if (this.mState.mLastDrawnShadow == null || hwBitmapChanged) {
                regenerateBitmapShadowCache();
            }
            float radians = (float) ((this.mState.mRotateDegrees * 3.141592653589793d) / 180.0d);
            float shadowOffsetX = ((float) ((Math.sin(radians) * this.mState.mShadowOffsetY) + (Math.cos(radians) * this.mState.mShadowOffsetX))) - this.mState.mTranslationX;
            float shadowOffsetY = ((float) ((Math.cos(radians) * this.mState.mShadowOffsetY) - (Math.sin(radians) * this.mState.mShadowOffsetX))) - this.mState.mTranslationY;
            canvas.drawBitmap(this.mState.mLastDrawnShadow, shadowOffsetX, shadowOffsetY, this.mShadowPaint);
        }
        canvas.drawBitmap(this.mState.mLastDrawnIcon, (Rect) null, bounds, this.mIconPaint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        return this.mState.canApplyTheme();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDrawableBackgroundColor() {
        return this.mState.mOvalBackgroundColor.toArgb();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasOvalBg() {
        return this.mState.mOvalBackgroundColor != null;
    }

    private void regenerateBitmapIconCache() {
        int width = getIntrinsicWidth();
        int height = getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable d = this.mState.mChildState.newDrawable().mutate();
        setDrawableBounds(d);
        canvas.save();
        if (this.mState.mHorizontalFlip) {
            canvas.scale(-1.0f, 1.0f, width * 0.5f, height * 0.5f);
        }
        d.draw(canvas);
        canvas.restore();
        if (this.mState.mIsHardwareBitmap) {
            bitmap = bitmap.copy(Bitmap.Config.HARDWARE, false);
        }
        this.mState.mLastDrawnIcon = bitmap;
    }

    private void regenerateBitmapShadowCache() {
        if (this.mState.mShadowSize == 0) {
            this.mState.mLastDrawnIcon = null;
            return;
        }
        int width = getIntrinsicWidth();
        int height = getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable d = this.mState.mChildState.newDrawable().mutate();
        setDrawableBounds(d);
        canvas.save();
        if (this.mState.mHorizontalFlip) {
            canvas.scale(-1.0f, 1.0f, width * 0.5f, height * 0.5f);
        }
        d.draw(canvas);
        canvas.restore();
        Paint paint = new Paint(3);
        paint.setMaskFilter(new BlurMaskFilter(this.mState.mShadowSize, BlurMaskFilter.Blur.NORMAL));
        int[] offset = new int[2];
        Bitmap shadow = bitmap.extractAlpha(paint, offset);
        paint.setMaskFilter(null);
        bitmap.eraseColor(0);
        canvas.drawBitmap(shadow, offset[0], offset[1], paint);
        if (this.mState.mIsHardwareBitmap) {
            bitmap = bitmap.copy(Bitmap.Config.HARDWARE, false);
        }
        this.mState.mLastDrawnShadow = bitmap;
    }

    private void updateShadowAlpha() {
        int alpha = Color.alpha(this.mState.mShadowColor);
        this.mShadowPaint.setAlpha(Math.round(alpha * (this.mState.mAlpha / 255.0f) * (1.0f - this.mState.mDarkIntensity)));
    }

    private void setDrawableBounds(Drawable d) {
        int offsetX = this.mState.mShadowSize + Math.abs(this.mState.mShadowOffsetX);
        int offsetY = this.mState.mShadowSize + Math.abs(this.mState.mShadowOffsetY);
        d.setBounds(offsetX, offsetY, getIntrinsicWidth() - offsetX, getIntrinsicHeight() - offsetY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class ShadowDrawableState extends Drawable.ConstantState {
        int mAlpha = 255;
        int mBaseHeight;
        int mBaseWidth;
        int mChangingConfigurations;
        Drawable.ConstantState mChildState;
        final int mDarkColor;
        float mDarkIntensity;
        boolean mHorizontalFlip;
        boolean mIsHardwareBitmap;
        Bitmap mLastDrawnIcon;
        Bitmap mLastDrawnShadow;
        final int mLightColor;
        final Color mOvalBackgroundColor;
        float mRotateDegrees;
        int mShadowColor;
        int mShadowOffsetX;
        int mShadowOffsetY;
        int mShadowSize;
        final boolean mSupportsAnimation;
        float mTranslationX;
        float mTranslationY;

        public ShadowDrawableState(int lightColor, int darkColor, boolean animated, boolean horizontalFlip, Color ovalBackgroundColor) {
            this.mLightColor = lightColor;
            this.mDarkColor = darkColor;
            this.mSupportsAnimation = animated;
            this.mHorizontalFlip = horizontalFlip;
            this.mOvalBackgroundColor = ovalBackgroundColor;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new KeyButtonDrawable(null, this);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            return true;
        }
    }

    public static KeyButtonDrawable create(Context ctx, int icon, boolean hasShadow, Color ovalBackgroundColor) {
        int dualToneDarkTheme = Utils.getThemeAttr(ctx, R.attr.darkIconTheme);
        int dualToneLightTheme = Utils.getThemeAttr(ctx, R.attr.lightIconTheme);
        Context lightContext = new ContextThemeWrapper(ctx, dualToneLightTheme);
        Context darkContext = new ContextThemeWrapper(ctx, dualToneDarkTheme);
        return create(lightContext, darkContext, icon, hasShadow, ovalBackgroundColor);
    }

    public static KeyButtonDrawable create(Context ctx, int icon, boolean hasShadow) {
        return create(ctx, icon, hasShadow, null);
    }

    public static KeyButtonDrawable create(Context lightContext, Context darkContext, int iconResId, boolean hasShadow, Color ovalBackgroundColor) {
        return create(lightContext, Utils.getColorAttrDefaultColor(lightContext, R.attr.singleToneColor), Utils.getColorAttrDefaultColor(darkContext, R.attr.singleToneColor), iconResId, hasShadow, ovalBackgroundColor);
    }

    public static KeyButtonDrawable create(Context context, int lightColor, int darkColor, int iconResId, boolean hasShadow, Color ovalBackgroundColor) {
        Resources res = context.getResources();
        boolean isRtl = res.getConfiguration().getLayoutDirection() == 1;
        Drawable d = context.getDrawable(iconResId);
        KeyButtonDrawable drawable = new KeyButtonDrawable(d, lightColor, darkColor, isRtl && d.isAutoMirrored(), ovalBackgroundColor);
        if (hasShadow) {
            int offsetX = res.getDimensionPixelSize(R.dimen.nav_key_button_shadow_offset_x);
            int offsetY = res.getDimensionPixelSize(R.dimen.nav_key_button_shadow_offset_y);
            int radius = res.getDimensionPixelSize(R.dimen.nav_key_button_shadow_radius);
            int color = context.getColor(R.color.nav_key_button_shadow_color);
            drawable.setShadowProperties(offsetX, offsetY, radius, color);
        }
        return drawable;
    }
}
