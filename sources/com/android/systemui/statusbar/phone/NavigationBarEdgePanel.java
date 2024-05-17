package com.android.systemui.statusbar.phone;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.MathUtils;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.VibratorHelper;
/* loaded from: classes21.dex */
public class NavigationBarEdgePanel extends View {
    private static final int ARROW_ANGLE_ADDED_PER_1000_SPEED = 4;
    private static final int ARROW_ANGLE_WHEN_EXTENDED_DEGREES = 56;
    private static final int ARROW_LENGTH_DP = 18;
    private static final int ARROW_MAX_ANGLE_SPEED_OFFSET_DEGREES = 4;
    private static final float ARROW_THICKNESS_DP = 2.5f;
    private static final int BASE_TRANSLATION_DP = 32;
    private static final long COLOR_ANIMATION_DURATION_MS = 120;
    private static final long DISAPPEAR_ARROW_ANIMATION_DURATION_MS = 100;
    private static final long DISAPPEAR_FADE_ANIMATION_DURATION_MS = 80;
    private static final int GESTURE_DURATION_FOR_CLICK_MS = 400;
    private static final int PROTECTION_WIDTH_PX = 2;
    private static final int RUBBER_BAND_AMOUNT = 15;
    private static final int RUBBER_BAND_AMOUNT_APPEAR = 4;
    private final SpringAnimation mAngleAnimation;
    private final SpringForce mAngleAppearForce;
    private final SpringForce mAngleDisappearForce;
    private float mAngleOffset;
    private int mArrowColor;
    private final ValueAnimator mArrowColorAnimator;
    private int mArrowColorDark;
    private int mArrowColorLight;
    private final ValueAnimator mArrowDisappearAnimation;
    private final float mArrowLength;
    private int mArrowPaddingEnd;
    private final Path mArrowPath;
    private int mArrowStartColor;
    private final float mArrowThickness;
    private boolean mArrowsPointLeft;
    private final float mBaseTranslation;
    private float mCurrentAngle;
    private int mCurrentArrowColor;
    private float mCurrentTranslation;
    private final float mDensity;
    private float mDesiredAngle;
    private float mDesiredTranslation;
    private float mDesiredVerticalTranslation;
    private float mDisappearAmount;
    private boolean mDragSlopPassed;
    private boolean mIsDark;
    private boolean mIsLeftPanel;
    private float mMaxTranslation;
    private final float mMinDeltaForSwitch;
    private final Paint mPaint;
    private float mPreviousTouchTranslation;
    private int mProtectionColor;
    private int mProtectionColorDark;
    private int mProtectionColorLight;
    private final Paint mProtectionPaint;
    private final SpringForce mRegularTranslationSpring;
    private int mScreenSize;
    private DynamicAnimation.OnAnimationEndListener mSetGoneEndListener;
    private boolean mShowProtection;
    private float mStartX;
    private float mStartY;
    private final float mSwipeThreshold;
    private float mTotalTouchDelta;
    private final SpringAnimation mTranslationAnimation;
    private boolean mTriggerBack;
    private final SpringForce mTriggerBackSpring;
    private VelocityTracker mVelocityTracker;
    private float mVerticalTranslation;
    private final SpringAnimation mVerticalTranslationAnimation;
    private long mVibrationTime;
    private final VibratorHelper mVibratorHelper;
    private static final Interpolator RUBBER_BAND_INTERPOLATOR = new PathInterpolator(0.2f, 1.0f, 1.0f, 1.0f);
    private static final Interpolator RUBBER_BAND_INTERPOLATOR_APPEAR = new PathInterpolator(0.25f, 1.0f, 1.0f, 1.0f);
    private static final FloatPropertyCompat<NavigationBarEdgePanel> CURRENT_ANGLE = new FloatPropertyCompat<NavigationBarEdgePanel>("currentAngle") { // from class: com.android.systemui.statusbar.phone.NavigationBarEdgePanel.2
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(NavigationBarEdgePanel object, float value) {
            object.setCurrentAngle(value);
        }

        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(NavigationBarEdgePanel object) {
            return object.getCurrentAngle();
        }
    };
    private static final FloatPropertyCompat<NavigationBarEdgePanel> CURRENT_TRANSLATION = new FloatPropertyCompat<NavigationBarEdgePanel>("currentTranslation") { // from class: com.android.systemui.statusbar.phone.NavigationBarEdgePanel.3
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(NavigationBarEdgePanel object, float value) {
            object.setCurrentTranslation(value);
        }

        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(NavigationBarEdgePanel object) {
            return object.getCurrentTranslation();
        }
    };
    private static final FloatPropertyCompat<NavigationBarEdgePanel> CURRENT_VERTICAL_TRANSLATION = new FloatPropertyCompat<NavigationBarEdgePanel>("verticalTranslation") { // from class: com.android.systemui.statusbar.phone.NavigationBarEdgePanel.4
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(NavigationBarEdgePanel object, float value) {
            object.setVerticalTranslation(value);
        }

        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(NavigationBarEdgePanel object) {
            return object.getVerticalTranslation();
        }
    };

    public NavigationBarEdgePanel(Context context) {
        super(context);
        this.mPaint = new Paint();
        this.mArrowPath = new Path();
        this.mIsDark = false;
        this.mShowProtection = false;
        this.mSetGoneEndListener = new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.systemui.statusbar.phone.NavigationBarEdgePanel.1
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                animation.removeEndListener(this);
                if (!canceled) {
                    NavigationBarEdgePanel.this.setVisibility(8);
                }
            }
        };
        this.mVibratorHelper = (VibratorHelper) Dependency.get(VibratorHelper.class);
        this.mDensity = context.getResources().getDisplayMetrics().density;
        this.mBaseTranslation = dp(32.0f);
        this.mArrowLength = dp(18.0f);
        this.mArrowThickness = dp(ARROW_THICKNESS_DP);
        this.mMinDeltaForSwitch = dp(32.0f);
        this.mPaint.setStrokeWidth(this.mArrowThickness);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mArrowColorAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mArrowColorAnimator.setDuration(COLOR_ANIMATION_DURATION_MS);
        this.mArrowColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.phone.NavigationBarEdgePanel.5
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                int newColor = ColorUtils.blendARGB(NavigationBarEdgePanel.this.mArrowStartColor, NavigationBarEdgePanel.this.mArrowColor, animation.getAnimatedFraction());
                NavigationBarEdgePanel.this.setCurrentArrowColor(newColor);
            }
        });
        this.mArrowDisappearAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mArrowDisappearAnimation.setDuration(DISAPPEAR_ARROW_ANIMATION_DURATION_MS);
        this.mArrowDisappearAnimation.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        this.mArrowDisappearAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarEdgePanel$bOecFcR5bBF6RggHYoy3PBO7S7o
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NavigationBarEdgePanel.this.lambda$new$0$NavigationBarEdgePanel(valueAnimator);
            }
        });
        this.mAngleAnimation = new SpringAnimation(this, CURRENT_ANGLE);
        this.mAngleAppearForce = new SpringForce().setStiffness(500.0f).setDampingRatio(0.5f);
        this.mAngleDisappearForce = new SpringForce().setStiffness(1500.0f).setDampingRatio(0.5f).setFinalPosition(90.0f);
        this.mAngleAnimation.setSpring(this.mAngleAppearForce).setMaxValue(90.0f);
        this.mTranslationAnimation = new SpringAnimation(this, CURRENT_TRANSLATION);
        this.mRegularTranslationSpring = new SpringForce().setStiffness(1500.0f).setDampingRatio(0.75f);
        this.mTriggerBackSpring = new SpringForce().setStiffness(450.0f).setDampingRatio(0.75f);
        this.mTranslationAnimation.setSpring(this.mRegularTranslationSpring);
        this.mVerticalTranslationAnimation = new SpringAnimation(this, CURRENT_VERTICAL_TRANSLATION);
        this.mVerticalTranslationAnimation.setSpring(new SpringForce().setStiffness(1500.0f).setDampingRatio(0.75f));
        this.mProtectionPaint = new Paint(this.mPaint);
        this.mProtectionPaint.setStrokeWidth(this.mArrowThickness + 2.0f);
        loadDimens();
        loadColors(context);
        updateArrowDirection();
        this.mSwipeThreshold = context.getResources().getDimension(R.dimen.navigation_edge_action_drag_threshold);
        setVisibility(8);
    }

    public /* synthetic */ void lambda$new$0$NavigationBarEdgePanel(ValueAnimator animation) {
        this.mDisappearAmount = ((Float) animation.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean shouldTriggerBack() {
        return this.mTriggerBack;
    }

    public void setIsDark(boolean isDark, boolean animate) {
        this.mIsDark = isDark;
        updateIsDark(animate);
    }

    public void setShowProtection(boolean showProtection) {
        this.mShowProtection = showProtection;
        invalidate();
    }

    public void setIsLeftPanel(boolean isLeftPanel) {
        this.mIsLeftPanel = isLeftPanel;
    }

    public void adjustRectToBoundingBox(Rect samplingRect) {
        float translation = this.mDesiredTranslation;
        if (!this.mTriggerBack) {
            translation = this.mBaseTranslation;
            if ((this.mIsLeftPanel && this.mArrowsPointLeft) || (!this.mIsLeftPanel && !this.mArrowsPointLeft)) {
                translation -= getStaticArrowWidth();
            }
        }
        float left = translation - (this.mArrowThickness / 2.0f);
        float left2 = this.mIsLeftPanel ? left : samplingRect.width() - left;
        float width = getStaticArrowWidth();
        float height = polarToCartY(56.0f) * this.mArrowLength * 2.0f;
        if (!this.mArrowsPointLeft) {
            left2 -= width;
        }
        float top = ((getHeight() * 0.5f) + this.mDesiredVerticalTranslation) - (height / 2.0f);
        samplingRect.offset((int) left2, (int) top);
        samplingRect.set(samplingRect.left, samplingRect.top, (int) (samplingRect.left + width), (int) (samplingRect.top + height));
    }

    public void handleTouch(MotionEvent event) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            this.mDragSlopPassed = false;
            resetOnDown();
            this.mStartX = event.getX();
            this.mStartY = event.getY();
            setVisibility(0);
            return;
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                handleMoveEvent(event);
                return;
            } else if (actionMasked != 3) {
                return;
            }
        }
        if (this.mTriggerBack) {
            triggerBack();
        } else if (this.mTranslationAnimation.isRunning()) {
            this.mTranslationAnimation.addEndListener(this.mSetGoneEndListener);
        } else {
            setVisibility(8);
        }
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateArrowDirection();
        loadDimens();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float pointerPosition = this.mCurrentTranslation - (this.mArrowThickness / 2.0f);
        canvas.save();
        canvas.translate(this.mIsLeftPanel ? pointerPosition : getWidth() - pointerPosition, (getHeight() * 0.5f) + this.mVerticalTranslation);
        float x = polarToCartX(this.mCurrentAngle) * this.mArrowLength;
        float y = polarToCartY(this.mCurrentAngle) * this.mArrowLength;
        Path arrowPath = calculatePath(x, y);
        if (this.mShowProtection) {
            canvas.drawPath(arrowPath, this.mProtectionPaint);
        }
        canvas.drawPath(arrowPath, this.mPaint);
        canvas.restore();
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mMaxTranslation = getWidth() - this.mArrowPaddingEnd;
    }

    private void loadDimens() {
        this.mArrowPaddingEnd = getContext().getResources().getDimensionPixelSize(R.dimen.navigation_edge_panel_padding);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        this.mScreenSize = Math.min(metrics.widthPixels, metrics.heightPixels);
    }

    private void updateArrowDirection() {
        this.mArrowsPointLeft = getLayoutDirection() == 0;
        invalidate();
    }

    private void loadColors(Context context) {
        int dualToneDarkTheme = Utils.getThemeAttr(context, R.attr.darkIconTheme);
        int dualToneLightTheme = Utils.getThemeAttr(context, R.attr.lightIconTheme);
        Context lightContext = new ContextThemeWrapper(context, dualToneLightTheme);
        Context darkContext = new ContextThemeWrapper(context, dualToneDarkTheme);
        this.mArrowColorLight = Utils.getColorAttrDefaultColor(lightContext, R.attr.singleToneColor);
        this.mArrowColorDark = Utils.getColorAttrDefaultColor(darkContext, R.attr.singleToneColor);
        this.mProtectionColorDark = this.mArrowColorLight;
        this.mProtectionColorLight = this.mArrowColorDark;
        updateIsDark(false);
    }

    private void updateIsDark(boolean animate) {
        this.mProtectionColor = this.mIsDark ? this.mProtectionColorDark : this.mProtectionColorLight;
        this.mProtectionPaint.setColor(this.mProtectionColor);
        this.mArrowColor = this.mIsDark ? this.mArrowColorDark : this.mArrowColorLight;
        this.mArrowColorAnimator.cancel();
        if (!animate) {
            setCurrentArrowColor(this.mArrowColor);
            return;
        }
        this.mArrowStartColor = this.mCurrentArrowColor;
        this.mArrowColorAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentArrowColor(int color) {
        this.mCurrentArrowColor = color;
        this.mPaint.setColor(color);
        invalidate();
    }

    private float getStaticArrowWidth() {
        return polarToCartX(56.0f) * this.mArrowLength;
    }

    private float polarToCartX(float angleInDegrees) {
        return (float) Math.cos(Math.toRadians(angleInDegrees));
    }

    private float polarToCartY(float angleInDegrees) {
        return (float) Math.sin(Math.toRadians(angleInDegrees));
    }

    private Path calculatePath(float x, float y) {
        if (!this.mArrowsPointLeft) {
            x = -x;
        }
        float extent = MathUtils.lerp(1.0f, 0.75f, this.mDisappearAmount);
        float x2 = x * extent;
        float y2 = y * extent;
        this.mArrowPath.reset();
        this.mArrowPath.moveTo(x2, y2);
        this.mArrowPath.lineTo(0.0f, 0.0f);
        this.mArrowPath.lineTo(x2, -y2);
        return this.mArrowPath;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getCurrentAngle() {
        return this.mCurrentAngle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getCurrentTranslation() {
        return this.mCurrentTranslation;
    }

    private void triggerBack() {
        this.mVelocityTracker.computeCurrentVelocity(1000);
        boolean isSlow = Math.abs(this.mVelocityTracker.getXVelocity()) < 500.0f;
        if (isSlow || SystemClock.uptimeMillis() - this.mVibrationTime >= 400) {
            this.mVibratorHelper.vibrate(0);
        }
        float f = this.mAngleOffset;
        if (f > -4.0f) {
            this.mAngleOffset = Math.max(-8.0f, f - 8.0f);
            updateAngle(true);
        }
        final Runnable translationEnd = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarEdgePanel$qL_Cvd7_6Xne4NYpi_Ofi326YV0
            @Override // java.lang.Runnable
            public final void run() {
                NavigationBarEdgePanel.this.lambda$triggerBack$2$NavigationBarEdgePanel();
            }
        };
        if (this.mTranslationAnimation.isRunning()) {
            this.mTranslationAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.android.systemui.statusbar.phone.NavigationBarEdgePanel.6
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    animation.removeEndListener(this);
                    if (!canceled) {
                        translationEnd.run();
                    }
                }
            });
        } else {
            translationEnd.run();
        }
    }

    public /* synthetic */ void lambda$triggerBack$2$NavigationBarEdgePanel() {
        this.mAngleOffset = Math.max(0.0f, this.mAngleOffset + 8.0f);
        updateAngle(true);
        this.mTranslationAnimation.setSpring(this.mTriggerBackSpring);
        setDesiredTranslation(this.mDesiredTranslation - dp(32.0f), true);
        animate().alpha(0.0f).setDuration(DISAPPEAR_FADE_ANIMATION_DURATION_MS).withEndAction(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarEdgePanel$nHEgOL8ws5zs7-Uj7JMc5oUqL9Y
            @Override // java.lang.Runnable
            public final void run() {
                NavigationBarEdgePanel.this.lambda$triggerBack$1$NavigationBarEdgePanel();
            }
        });
        this.mArrowDisappearAnimation.start();
    }

    public /* synthetic */ void lambda$triggerBack$1$NavigationBarEdgePanel() {
        setVisibility(8);
    }

    private void resetOnDown() {
        animate().cancel();
        this.mAngleAnimation.cancel();
        this.mTranslationAnimation.cancel();
        this.mVerticalTranslationAnimation.cancel();
        this.mArrowDisappearAnimation.cancel();
        this.mAngleOffset = 0.0f;
        this.mTranslationAnimation.setSpring(this.mRegularTranslationSpring);
        setTriggerBack(false, false);
        setDesiredTranslation(0.0f, false);
        setCurrentTranslation(0.0f);
        updateAngle(false);
        this.mPreviousTouchTranslation = 0.0f;
        this.mTotalTouchDelta = 0.0f;
        this.mVibrationTime = 0L;
        setDesiredVerticalTransition(0.0f, false);
    }

    private void handleMoveEvent(MotionEvent event) {
        float touchTranslation;
        float x = event.getX();
        float y = event.getY();
        float touchTranslation2 = MathUtils.abs(x - this.mStartX);
        float yOffset = y - this.mStartY;
        float delta = touchTranslation2 - this.mPreviousTouchTranslation;
        if (Math.abs(delta) > 0.0f) {
            if (Math.signum(delta) == Math.signum(this.mTotalTouchDelta)) {
                this.mTotalTouchDelta += delta;
            } else {
                this.mTotalTouchDelta = delta;
            }
        }
        this.mPreviousTouchTranslation = touchTranslation2;
        if (!this.mDragSlopPassed && touchTranslation2 > this.mSwipeThreshold) {
            this.mDragSlopPassed = true;
            this.mVibratorHelper.vibrate(2);
            this.mVibrationTime = SystemClock.uptimeMillis();
            this.mDisappearAmount = 0.0f;
            setAlpha(1.0f);
            setTriggerBack(true, true);
        }
        float f = this.mBaseTranslation;
        if (touchTranslation2 > f) {
            float diff = touchTranslation2 - f;
            float progress = MathUtils.saturate(diff / (this.mScreenSize - f));
            float interpolation = RUBBER_BAND_INTERPOLATOR.getInterpolation(progress);
            float f2 = this.mMaxTranslation;
            float f3 = this.mBaseTranslation;
            float progress2 = interpolation * (f2 - f3);
            touchTranslation = f3 + progress2;
        } else {
            float diff2 = f - touchTranslation2;
            float progress3 = MathUtils.saturate(diff2 / f);
            float interpolation2 = RUBBER_BAND_INTERPOLATOR_APPEAR.getInterpolation(progress3);
            float f4 = this.mBaseTranslation;
            float progress4 = interpolation2 * (f4 / 4.0f);
            touchTranslation = f4 - progress4;
        }
        boolean triggerBack = this.mTriggerBack;
        if (Math.abs(this.mTotalTouchDelta) > this.mMinDeltaForSwitch) {
            triggerBack = this.mTotalTouchDelta > 0.0f;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = this.mVelocityTracker.getXVelocity();
        float yVelocity = this.mVelocityTracker.getYVelocity();
        float velocity = MathUtils.mag(xVelocity, yVelocity);
        this.mAngleOffset = Math.min((velocity / 1000.0f) * 4.0f, 4.0f) * Math.signum(xVelocity);
        if ((this.mIsLeftPanel && this.mArrowsPointLeft) || (!this.mIsLeftPanel && !this.mArrowsPointLeft)) {
            this.mAngleOffset *= -1.0f;
        }
        if (Math.abs(yOffset) > Math.abs(x - this.mStartX) * 2.0f) {
            triggerBack = false;
        }
        setTriggerBack(triggerBack, true);
        if (!this.mTriggerBack) {
            touchTranslation = 0.0f;
        } else if ((this.mIsLeftPanel && this.mArrowsPointLeft) || (!this.mIsLeftPanel && !this.mArrowsPointLeft)) {
            touchTranslation -= getStaticArrowWidth();
        }
        setDesiredTranslation(touchTranslation, true);
        updateAngle(true);
        float maxYOffset = (getHeight() / 2.0f) - this.mArrowLength;
        float progress5 = MathUtils.constrain(Math.abs(yOffset) / (15.0f * maxYOffset), 0.0f, 1.0f);
        float verticalTranslation = RUBBER_BAND_INTERPOLATOR.getInterpolation(progress5) * maxYOffset * Math.signum(yOffset);
        setDesiredVerticalTransition(verticalTranslation, true);
    }

    private void setDesiredVerticalTransition(float verticalTranslation, boolean animated) {
        if (this.mDesiredVerticalTranslation != verticalTranslation) {
            this.mDesiredVerticalTranslation = verticalTranslation;
            if (!animated) {
                setVerticalTranslation(verticalTranslation);
            } else {
                this.mVerticalTranslationAnimation.animateToFinalPosition(verticalTranslation);
            }
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVerticalTranslation(float verticalTranslation) {
        this.mVerticalTranslation = verticalTranslation;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getVerticalTranslation() {
        return this.mVerticalTranslation;
    }

    private void setDesiredTranslation(float desiredTranslation, boolean animated) {
        if (this.mDesiredTranslation != desiredTranslation) {
            this.mDesiredTranslation = desiredTranslation;
            if (!animated) {
                setCurrentTranslation(desiredTranslation);
            } else {
                this.mTranslationAnimation.animateToFinalPosition(desiredTranslation);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentTranslation(float currentTranslation) {
        this.mCurrentTranslation = currentTranslation;
        invalidate();
    }

    private void setTriggerBack(boolean triggerBack, boolean animated) {
        if (this.mTriggerBack != triggerBack) {
            this.mTriggerBack = triggerBack;
            this.mAngleAnimation.cancel();
            updateAngle(animated);
            this.mTranslationAnimation.cancel();
        }
    }

    private void updateAngle(boolean animated) {
        float newAngle = this.mTriggerBack ? this.mAngleOffset + 56.0f : 90.0f;
        if (newAngle != this.mDesiredAngle) {
            if (!animated) {
                setCurrentAngle(newAngle);
            } else {
                this.mAngleAnimation.setSpring(this.mTriggerBack ? this.mAngleAppearForce : this.mAngleDisappearForce);
                this.mAngleAnimation.animateToFinalPosition(newAngle);
            }
            this.mDesiredAngle = newAngle;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentAngle(float currentAngle) {
        this.mCurrentAngle = currentAngle;
        invalidate();
    }

    private float dp(float dp) {
        return this.mDensity * dp;
    }
}
