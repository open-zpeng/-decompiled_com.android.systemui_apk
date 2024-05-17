package com.android.systemui.statusbar.policy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import java.util.ArrayList;
import java.util.HashSet;
/* loaded from: classes21.dex */
public class KeyButtonRipple extends Drawable {
    private static final int ANIMATION_DURATION_FADE = 450;
    private static final int ANIMATION_DURATION_SCALE = 350;
    private static final float GLOW_MAX_ALPHA = 0.2f;
    private static final float GLOW_MAX_ALPHA_DARK = 0.1f;
    private static final float GLOW_MAX_SCALE_FACTOR = 1.35f;
    private CanvasProperty<Float> mBottomProp;
    private boolean mDark;
    private boolean mDelayTouchFeedback;
    private boolean mDrawingHardwareGlow;
    private boolean mLastDark;
    private CanvasProperty<Float> mLeftProp;
    private int mMaxWidth;
    private CanvasProperty<Paint> mPaintProp;
    private boolean mPressed;
    private CanvasProperty<Float> mRightProp;
    private Paint mRipplePaint;
    private CanvasProperty<Float> mRxProp;
    private CanvasProperty<Float> mRyProp;
    private boolean mSupportHardware;
    private final View mTargetView;
    private CanvasProperty<Float> mTopProp;
    private boolean mVisible;
    private float mGlowAlpha = 0.0f;
    private float mGlowScale = 1.0f;
    private final Interpolator mInterpolator = new LogInterpolator();
    private final Handler mHandler = new Handler();
    private final HashSet<Animator> mRunningAnimations = new HashSet<>();
    private final ArrayList<Animator> mTmpArray = new ArrayList<>();
    private Type mType = Type.ROUNDED_RECT;
    private final AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.policy.KeyButtonRipple.1
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            KeyButtonRipple.this.mRunningAnimations.remove(animation);
            if (KeyButtonRipple.this.mRunningAnimations.isEmpty() && !KeyButtonRipple.this.mPressed) {
                KeyButtonRipple.this.mVisible = false;
                KeyButtonRipple.this.mDrawingHardwareGlow = false;
                KeyButtonRipple.this.invalidateSelf();
            }
        }
    };

    /* loaded from: classes21.dex */
    public enum Type {
        OVAL,
        ROUNDED_RECT
    }

    public KeyButtonRipple(Context ctx, View targetView) {
        this.mMaxWidth = ctx.getResources().getDimensionPixelSize(R.dimen.key_button_ripple_max_width);
        this.mTargetView = targetView;
    }

    public void setDarkIntensity(float darkIntensity) {
        this.mDark = darkIntensity >= 0.5f;
    }

    public void setDelayTouchFeedback(boolean delay) {
        this.mDelayTouchFeedback = delay;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    private Paint getRipplePaint() {
        if (this.mRipplePaint == null) {
            this.mRipplePaint = new Paint();
            this.mRipplePaint.setAntiAlias(true);
            this.mRipplePaint.setColor(this.mLastDark ? -16777216 : -1);
        }
        return this.mRipplePaint;
    }

    private void drawSoftware(Canvas canvas) {
        if (this.mGlowAlpha > 0.0f) {
            Paint p = getRipplePaint();
            p.setAlpha((int) (this.mGlowAlpha * 255.0f));
            float w = getBounds().width();
            float h = getBounds().height();
            boolean horizontal = w > h;
            float diameter = getRippleSize() * this.mGlowScale;
            float radius = diameter * 0.5f;
            float cx = w * 0.5f;
            float cy = h * 0.5f;
            float rx = horizontal ? radius : cx;
            float ry = horizontal ? cy : radius;
            float corner = horizontal ? cy : cx;
            if (this.mType == Type.ROUNDED_RECT) {
                canvas.drawRoundRect(cx - rx, cy - ry, cx + rx, cy + ry, corner, corner, p);
                return;
            }
            canvas.save();
            canvas.translate(cx, cy);
            float r = Math.min(rx, ry);
            canvas.drawOval(-r, -r, r, r, p);
            canvas.restore();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.mSupportHardware = canvas.isHardwareAccelerated();
        if (this.mSupportHardware) {
            drawHardware((RecordingCanvas) canvas);
        } else {
            drawSoftware(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    private boolean isHorizontal() {
        return getBounds().width() > getBounds().height();
    }

    private void drawHardware(RecordingCanvas c) {
        if (this.mDrawingHardwareGlow) {
            if (this.mType == Type.ROUNDED_RECT) {
                c.drawRoundRect(this.mLeftProp, this.mTopProp, this.mRightProp, this.mBottomProp, this.mRxProp, this.mRyProp, this.mPaintProp);
                return;
            }
            CanvasProperty<Float> cx = CanvasProperty.createFloat(getBounds().width() / 2);
            CanvasProperty<Float> cy = CanvasProperty.createFloat(getBounds().height() / 2);
            int d = Math.min(getBounds().width(), getBounds().height());
            CanvasProperty<Float> r = CanvasProperty.createFloat((d * 1.0f) / 2.0f);
            c.drawCircle(cx, cy, r, this.mPaintProp);
        }
    }

    public float getGlowAlpha() {
        return this.mGlowAlpha;
    }

    public void setGlowAlpha(float x) {
        this.mGlowAlpha = x;
        invalidateSelf();
    }

    public float getGlowScale() {
        return this.mGlowScale;
    }

    public void setGlowScale(float x) {
        this.mGlowScale = x;
        invalidateSelf();
    }

    private float getMaxGlowAlpha() {
        return this.mLastDark ? 0.1f : 0.2f;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        boolean pressed = false;
        int i = 0;
        while (true) {
            if (i >= state.length) {
                break;
            } else if (state[i] != 16842919) {
                i++;
            } else {
                pressed = true;
                break;
            }
        }
        if (pressed != this.mPressed) {
            setPressed(pressed);
            this.mPressed = pressed;
            return true;
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public void jumpToCurrentState() {
        cancelAnimations();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean hasFocusStateSpecified() {
        return true;
    }

    public void setPressed(boolean pressed) {
        boolean z = this.mDark;
        if (z != this.mLastDark && pressed) {
            this.mRipplePaint = null;
            this.mLastDark = z;
        }
        if (this.mSupportHardware) {
            setPressedHardware(pressed);
        } else {
            setPressedSoftware(pressed);
        }
    }

    public void abortDelayedRipple() {
        this.mHandler.removeCallbacksAndMessages(null);
    }

    private void cancelAnimations() {
        this.mVisible = false;
        this.mTmpArray.addAll(this.mRunningAnimations);
        int size = this.mTmpArray.size();
        for (int i = 0; i < size; i++) {
            Animator a = this.mTmpArray.get(i);
            a.cancel();
        }
        this.mTmpArray.clear();
        this.mRunningAnimations.clear();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    private void setPressedSoftware(boolean pressed) {
        if (pressed) {
            if (this.mDelayTouchFeedback) {
                if (this.mRunningAnimations.isEmpty()) {
                    this.mHandler.removeCallbacksAndMessages(null);
                    this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$KeyButtonRipple$_NjSlP8uc8G3rFUDxQkVsRHA4H4
                        @Override // java.lang.Runnable
                        public final void run() {
                            KeyButtonRipple.this.enterSoftware();
                        }
                    }, ViewConfiguration.getTapTimeout());
                    return;
                } else if (this.mVisible) {
                    enterSoftware();
                    return;
                } else {
                    return;
                }
            }
            enterSoftware();
            return;
        }
        exitSoftware();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enterSoftware() {
        cancelAnimations();
        this.mVisible = true;
        this.mGlowAlpha = getMaxGlowAlpha();
        ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(this, "glowScale", 0.0f, GLOW_MAX_SCALE_FACTOR);
        scaleAnimator.setInterpolator(this.mInterpolator);
        scaleAnimator.setDuration(350L);
        scaleAnimator.addListener(this.mAnimatorListener);
        scaleAnimator.start();
        this.mRunningAnimations.add(scaleAnimator);
        if (this.mDelayTouchFeedback && !this.mPressed) {
            exitSoftware();
        }
    }

    private void exitSoftware() {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, "glowAlpha", this.mGlowAlpha, 0.0f);
        alphaAnimator.setInterpolator(Interpolators.ALPHA_OUT);
        alphaAnimator.setDuration(450L);
        alphaAnimator.addListener(this.mAnimatorListener);
        alphaAnimator.start();
        this.mRunningAnimations.add(alphaAnimator);
    }

    private void setPressedHardware(boolean pressed) {
        if (pressed) {
            if (this.mDelayTouchFeedback) {
                if (this.mRunningAnimations.isEmpty()) {
                    this.mHandler.removeCallbacksAndMessages(null);
                    this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$KeyButtonRipple$Xl4rWJU_4TFxkXeTg6i8PM566MQ
                        @Override // java.lang.Runnable
                        public final void run() {
                            KeyButtonRipple.this.enterHardware();
                        }
                    }, ViewConfiguration.getTapTimeout());
                    return;
                } else if (this.mVisible) {
                    enterHardware();
                    return;
                } else {
                    return;
                }
            }
            enterHardware();
            return;
        }
        exitHardware();
    }

    private void setExtendStart(CanvasProperty<Float> prop) {
        if (isHorizontal()) {
            this.mLeftProp = prop;
        } else {
            this.mTopProp = prop;
        }
    }

    private CanvasProperty<Float> getExtendStart() {
        return isHorizontal() ? this.mLeftProp : this.mTopProp;
    }

    private void setExtendEnd(CanvasProperty<Float> prop) {
        if (isHorizontal()) {
            this.mRightProp = prop;
        } else {
            this.mBottomProp = prop;
        }
    }

    private CanvasProperty<Float> getExtendEnd() {
        return isHorizontal() ? this.mRightProp : this.mBottomProp;
    }

    private int getExtendSize() {
        return isHorizontal() ? getBounds().width() : getBounds().height();
    }

    private int getRippleSize() {
        int size = isHorizontal() ? getBounds().width() : getBounds().height();
        return Math.min(size, this.mMaxWidth);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enterHardware() {
        cancelAnimations();
        this.mVisible = true;
        this.mDrawingHardwareGlow = true;
        setExtendStart(CanvasProperty.createFloat(getExtendSize() / 2));
        Animator renderNodeAnimator = new RenderNodeAnimator(getExtendStart(), (getExtendSize() / 2) - ((getRippleSize() * GLOW_MAX_SCALE_FACTOR) / 2.0f));
        renderNodeAnimator.setDuration(350L);
        renderNodeAnimator.setInterpolator(this.mInterpolator);
        renderNodeAnimator.addListener(this.mAnimatorListener);
        renderNodeAnimator.setTarget(this.mTargetView);
        setExtendEnd(CanvasProperty.createFloat(getExtendSize() / 2));
        Animator renderNodeAnimator2 = new RenderNodeAnimator(getExtendEnd(), (getExtendSize() / 2) + ((getRippleSize() * GLOW_MAX_SCALE_FACTOR) / 2.0f));
        renderNodeAnimator2.setDuration(350L);
        renderNodeAnimator2.setInterpolator(this.mInterpolator);
        renderNodeAnimator2.addListener(this.mAnimatorListener);
        renderNodeAnimator2.setTarget(this.mTargetView);
        if (isHorizontal()) {
            this.mTopProp = CanvasProperty.createFloat(0.0f);
            this.mBottomProp = CanvasProperty.createFloat(getBounds().height());
            this.mRxProp = CanvasProperty.createFloat(getBounds().height() / 2);
            this.mRyProp = CanvasProperty.createFloat(getBounds().height() / 2);
        } else {
            this.mLeftProp = CanvasProperty.createFloat(0.0f);
            this.mRightProp = CanvasProperty.createFloat(getBounds().width());
            this.mRxProp = CanvasProperty.createFloat(getBounds().width() / 2);
            this.mRyProp = CanvasProperty.createFloat(getBounds().width() / 2);
        }
        this.mGlowScale = GLOW_MAX_SCALE_FACTOR;
        this.mGlowAlpha = getMaxGlowAlpha();
        this.mRipplePaint = getRipplePaint();
        this.mRipplePaint.setAlpha((int) (this.mGlowAlpha * 255.0f));
        this.mPaintProp = CanvasProperty.createPaint(this.mRipplePaint);
        renderNodeAnimator.start();
        renderNodeAnimator2.start();
        this.mRunningAnimations.add(renderNodeAnimator);
        this.mRunningAnimations.add(renderNodeAnimator2);
        invalidateSelf();
        if (this.mDelayTouchFeedback && !this.mPressed) {
            exitHardware();
        }
    }

    private void exitHardware() {
        this.mPaintProp = CanvasProperty.createPaint(getRipplePaint());
        Animator renderNodeAnimator = new RenderNodeAnimator(this.mPaintProp, 1, 0.0f);
        renderNodeAnimator.setDuration(450L);
        renderNodeAnimator.setInterpolator(Interpolators.ALPHA_OUT);
        renderNodeAnimator.addListener(this.mAnimatorListener);
        renderNodeAnimator.setTarget(this.mTargetView);
        renderNodeAnimator.start();
        this.mRunningAnimations.add(renderNodeAnimator);
        invalidateSelf();
    }

    /* loaded from: classes21.dex */
    private static final class LogInterpolator implements Interpolator {
        private LogInterpolator() {
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float input) {
            return 1.0f - ((float) Math.pow(400.0d, (-input) * 1.4d));
        }
    }
}
