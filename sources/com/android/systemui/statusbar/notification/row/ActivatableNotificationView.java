package com.android.systemui.statusbar.notification.row;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.notification.FakeShadowView;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.stack.StackStateAnimator;
import com.android.systemui.statusbar.phone.DoubleTapHelper;
import java.util.Objects;
/* loaded from: classes21.dex */
public abstract class ActivatableNotificationView extends ExpandableOutlineView {
    private static final int ACTIVATE_ANIMATION_LENGTH = 220;
    private static final float ALPHA_ANIMATION_END = 0.0f;
    private static final int BACKGROUND_ANIMATION_LENGTH_MS = 220;
    private static final float HORIZONTAL_ANIMATION_END = 0.2f;
    private static final float HORIZONTAL_ANIMATION_START = 1.0f;
    private static final float HORIZONTAL_COLLAPSED_REST_PARTIAL = 0.05f;
    protected static final int NO_COLOR = 0;
    private static final float VERTICAL_ANIMATION_START = 1.0f;
    private final AccessibilityManager mAccessibilityManager;
    private boolean mActivated;
    private float mAnimationTranslationY;
    private float mAppearAnimationFraction;
    private RectF mAppearAnimationRect;
    private float mAppearAnimationTranslation;
    private ValueAnimator mAppearAnimator;
    private ObjectAnimator mBackgroundAnimator;
    private ValueAnimator mBackgroundColorAnimator;
    private NotificationBackgroundView mBackgroundDimmed;
    protected NotificationBackgroundView mBackgroundNormal;
    private ValueAnimator.AnimatorUpdateListener mBackgroundVisibilityUpdater;
    private float mBgAlpha;
    protected int mBgTint;
    private boolean mBlockNextTouch;
    private Interpolator mCurrentAlphaInterpolator;
    private Interpolator mCurrentAppearInterpolator;
    private int mCurrentBackgroundTint;
    private boolean mDimmed;
    private int mDimmedAlpha;
    private float mDimmedBackgroundFadeInAmount;
    private final DoubleTapHelper mDoubleTapHelper;
    private boolean mDrawingAppearAnimation;
    private FakeShadowView mFakeShadow;
    private final FalsingManager mFalsingManager;
    private boolean mFirstInSection;
    private int mHeadsUpAddStartLocation;
    private float mHeadsUpLocation;
    private boolean mIsAppearing;
    private boolean mIsBelowSpeedBump;
    private boolean mIsHeadsUpAnimation;
    private boolean mLastInSection;
    private boolean mNeedsDimming;
    private float mNormalBackgroundVisibilityAmount;
    private int mNormalColor;
    protected int mNormalRippleColor;
    private OnActivatedListener mOnActivatedListener;
    private float mOverrideAmount;
    private int mOverrideTint;
    private boolean mShadowHidden;
    private final Interpolator mSlowOutFastInInterpolator;
    private final Interpolator mSlowOutLinearInInterpolator;
    private int mStartTint;
    private final Runnable mTapTimeoutRunnable;
    private int mTargetTint;
    private int mTintedRippleColor;
    private static final Interpolator ACTIVATE_INVERSE_INTERPOLATOR = new PathInterpolator(0.6f, 0.0f, 0.5f, 1.0f);
    private static final Interpolator ACTIVATE_INVERSE_ALPHA_INTERPOLATOR = new PathInterpolator(0.0f, 0.0f, 0.5f, 1.0f);

    /* loaded from: classes21.dex */
    public interface OnActivatedListener {
        void onActivated(ActivatableNotificationView activatableNotificationView);

        void onActivationReset(ActivatableNotificationView activatableNotificationView);
    }

    protected abstract View getContentView();

    public ActivatableNotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBgTint = 0;
        this.mBgAlpha = 1.0f;
        this.mAppearAnimationRect = new RectF();
        this.mAppearAnimationFraction = -1.0f;
        this.mDimmedBackgroundFadeInAmount = -1.0f;
        this.mBackgroundVisibilityUpdater = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.row.ActivatableNotificationView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                ActivatableNotificationView activatableNotificationView = ActivatableNotificationView.this;
                activatableNotificationView.setNormalBackgroundVisibilityAmount(activatableNotificationView.mBackgroundNormal.getAlpha());
                ActivatableNotificationView activatableNotificationView2 = ActivatableNotificationView.this;
                activatableNotificationView2.mDimmedBackgroundFadeInAmount = activatableNotificationView2.mBackgroundDimmed.getAlpha();
            }
        };
        this.mTapTimeoutRunnable = new Runnable() { // from class: com.android.systemui.statusbar.notification.row.ActivatableNotificationView.2
            @Override // java.lang.Runnable
            public void run() {
                ActivatableNotificationView.this.makeInactive(true);
            }
        };
        this.mSlowOutFastInInterpolator = new PathInterpolator(0.8f, 0.0f, 0.6f, 1.0f);
        this.mSlowOutLinearInInterpolator = new PathInterpolator(0.8f, 0.0f, 1.0f, 1.0f);
        this.mFalsingManager = (FalsingManager) Dependency.get(FalsingManager.class);
        setClipChildren(false);
        setClipToPadding(false);
        updateColors();
        this.mAccessibilityManager = AccessibilityManager.getInstance(this.mContext);
        DoubleTapHelper.ActivationListener activationListener = new DoubleTapHelper.ActivationListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$ActivatableNotificationView$raBogtwUDgGzree3QaRWS5kgw_g
            @Override // com.android.systemui.statusbar.phone.DoubleTapHelper.ActivationListener
            public final void onActiveChanged(boolean z) {
                ActivatableNotificationView.this.lambda$new$0$ActivatableNotificationView(z);
            }
        };
        DoubleTapHelper.DoubleTapListener doubleTapListener = new DoubleTapHelper.DoubleTapListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$ActivatableNotificationView$fZ3qu4yQQcyUWjUQZMAyhFN3cZI
            @Override // com.android.systemui.statusbar.phone.DoubleTapHelper.DoubleTapListener
            public final boolean onDoubleTap() {
                return ActivatableNotificationView.this.lambda$new$1$ActivatableNotificationView();
            }
        };
        DoubleTapHelper.SlideBackListener slideBackListener = new DoubleTapHelper.SlideBackListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$ELE-e-9GisA3PeCbD7mpobFwmaM
            @Override // com.android.systemui.statusbar.phone.DoubleTapHelper.SlideBackListener
            public final boolean onSlideBack() {
                return ActivatableNotificationView.this.handleSlideBack();
            }
        };
        final FalsingManager falsingManager = this.mFalsingManager;
        Objects.requireNonNull(falsingManager);
        this.mDoubleTapHelper = new DoubleTapHelper(this, activationListener, doubleTapListener, slideBackListener, new DoubleTapHelper.DoubleTapLogListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$PkPBcaaRR8KHImTlnKW995Xmvx8
            @Override // com.android.systemui.statusbar.phone.DoubleTapHelper.DoubleTapLogListener
            public final void onDoubleTapLog(boolean z, float f, float f2) {
                FalsingManager.this.onNotificationDoubleTap(z, f, f2);
            }
        });
        initDimens();
    }

    public /* synthetic */ void lambda$new$0$ActivatableNotificationView(boolean active) {
        if (active) {
            makeActive();
        } else {
            makeInactive(true);
        }
    }

    public /* synthetic */ boolean lambda$new$1$ActivatableNotificationView() {
        return super.performClick();
    }

    private void updateColors() {
        this.mNormalColor = this.mContext.getColor(R.color.notification_material_background_color);
        this.mTintedRippleColor = this.mContext.getColor(R.color.notification_ripple_tinted_color);
        this.mNormalRippleColor = this.mContext.getColor(R.color.notification_ripple_untinted_color);
        this.mDimmedAlpha = Color.alpha(this.mContext.getColor(R.color.notification_material_background_dimmed_color));
    }

    private void initDimens() {
        this.mHeadsUpAddStartLocation = getResources().getDimensionPixelSize(17105313);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView
    public void onDensityOrFontScaleChanged() {
        super.onDensityOrFontScaleChanged();
        initDimens();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateBackgroundColors() {
        updateColors();
        initBackground();
        updateBackgroundTint();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mBackgroundNormal = (NotificationBackgroundView) findViewById(R.id.backgroundNormal);
        this.mFakeShadow = (FakeShadowView) findViewById(R.id.fake_shadow);
        this.mShadowHidden = this.mFakeShadow.getVisibility() != 0;
        this.mBackgroundDimmed = (NotificationBackgroundView) findViewById(R.id.backgroundDimmed);
        initBackground();
        updateBackground();
        updateBackgroundTint();
        updateOutlineAlpha();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initBackground() {
        this.mBackgroundNormal.setCustomBackground(R.drawable.notification_material_bg);
        this.mBackgroundDimmed.setCustomBackground(R.drawable.notification_material_bg_dim);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mNeedsDimming && ev.getActionMasked() == 0 && disallowSingleClick(ev) && !isTouchExplorationEnabled()) {
            if (!this.mActivated) {
                return true;
            }
            if (!this.mDoubleTapHelper.isWithinDoubleTapSlop(ev)) {
                this.mBlockNextTouch = true;
                makeInactive(true);
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isTouchExplorationEnabled() {
        return this.mAccessibilityManager.isTouchExplorationEnabled();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean disallowSingleClick(MotionEvent ev) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean handleSlideBack() {
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mBlockNextTouch) {
            this.mBlockNextTouch = false;
            return false;
        } else if (this.mNeedsDimming && !isTouchExplorationEnabled() && isInteractive()) {
            boolean wasActivated = this.mActivated;
            boolean result = handleTouchEventDimmed(event);
            if (wasActivated && result && event.getAction() == 1) {
                removeCallbacks(this.mTapTimeoutRunnable);
                return result;
            }
            return result;
        } else {
            return super.onTouchEvent(event);
        }
    }

    protected boolean isInteractive() {
        return true;
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float x, float y) {
        if (!this.mDimmed) {
            this.mBackgroundNormal.drawableHotspotChanged(x, y);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mDimmed) {
            this.mBackgroundDimmed.setState(getDrawableState());
        } else {
            this.mBackgroundNormal.setState(getDrawableState());
        }
    }

    public void setRippleAllowed(boolean allowed) {
        this.mBackgroundNormal.setPressedAllowed(allowed);
    }

    private boolean handleTouchEventDimmed(MotionEvent event) {
        if (this.mNeedsDimming && !this.mDimmed) {
            super.onTouchEvent(event);
        }
        return this.mDoubleTapHelper.onTouchEvent(event, getActualHeight());
    }

    @Override // android.view.View
    public boolean performClick() {
        if (!this.mNeedsDimming || isTouchExplorationEnabled()) {
            return super.performClick();
        }
        return false;
    }

    private void makeActive() {
        this.mFalsingManager.onNotificationActive();
        startActivateAnimation(false);
        this.mActivated = true;
        OnActivatedListener onActivatedListener = this.mOnActivatedListener;
        if (onActivatedListener != null) {
            onActivatedListener.onActivated(this);
        }
    }

    private void startActivateAnimation(final boolean reverse) {
        Interpolator interpolator;
        Interpolator alphaInterpolator;
        if (!isAttachedToWindow() || !isDimmable()) {
            return;
        }
        int widthHalf = this.mBackgroundNormal.getWidth() / 2;
        int heightHalf = this.mBackgroundNormal.getActualHeight() / 2;
        float radius = (float) Math.sqrt((widthHalf * widthHalf) + (heightHalf * heightHalf));
        float f = 0.0f;
        Animator animator = reverse ? ViewAnimationUtils.createCircularReveal(this.mBackgroundNormal, widthHalf, heightHalf, radius, 0.0f) : ViewAnimationUtils.createCircularReveal(this.mBackgroundNormal, widthHalf, heightHalf, 0.0f, radius);
        this.mBackgroundNormal.setVisibility(0);
        if (!reverse) {
            interpolator = Interpolators.LINEAR_OUT_SLOW_IN;
            alphaInterpolator = Interpolators.LINEAR_OUT_SLOW_IN;
        } else {
            interpolator = ACTIVATE_INVERSE_INTERPOLATOR;
            alphaInterpolator = ACTIVATE_INVERSE_ALPHA_INTERPOLATOR;
        }
        animator.setInterpolator(interpolator);
        animator.setDuration(220L);
        if (reverse) {
            this.mBackgroundNormal.setAlpha(1.0f);
            animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.row.ActivatableNotificationView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ActivatableNotificationView.this.updateBackground();
                }
            });
            animator.start();
        } else {
            this.mBackgroundNormal.setAlpha(0.4f);
            animator.start();
        }
        ViewPropertyAnimator animate = this.mBackgroundNormal.animate();
        if (!reverse) {
            f = 1.0f;
        }
        animate.alpha(f).setInterpolator(alphaInterpolator).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.row.ActivatableNotificationView.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                if (reverse) {
                    animatedFraction = 1.0f - animatedFraction;
                }
                ActivatableNotificationView.this.setNormalBackgroundVisibilityAmount(animatedFraction);
            }
        }).setDuration(220L);
    }

    public void makeInactive(boolean animate) {
        if (this.mActivated) {
            this.mActivated = false;
            if (this.mDimmed) {
                if (animate) {
                    startActivateAnimation(true);
                } else {
                    updateBackground();
                }
            }
        }
        OnActivatedListener onActivatedListener = this.mOnActivatedListener;
        if (onActivatedListener != null) {
            onActivatedListener.onActivationReset(this);
        }
        removeCallbacks(this.mTapTimeoutRunnable);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setDimmed(boolean dimmed, boolean fade) {
        this.mNeedsDimming = dimmed;
        boolean dimmed2 = dimmed & isDimmable();
        if (this.mDimmed != dimmed2) {
            this.mDimmed = dimmed2;
            resetBackgroundAlpha();
            if (fade) {
                fadeDimmedBackground();
            } else {
                updateBackground();
            }
        }
    }

    public boolean isDimmable() {
        return true;
    }

    private void updateOutlineAlpha() {
        float alpha = 0.7f + ((1.0f - 0.7f) * this.mNormalBackgroundVisibilityAmount);
        setOutlineAlpha(alpha);
    }

    public void setNormalBackgroundVisibilityAmount(float normalBackgroundVisibilityAmount) {
        this.mNormalBackgroundVisibilityAmount = normalBackgroundVisibilityAmount;
        updateOutlineAlpha();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setBelowSpeedBump(boolean below) {
        super.setBelowSpeedBump(below);
        if (below != this.mIsBelowSpeedBump) {
            this.mIsBelowSpeedBump = below;
            updateBackgroundTint();
            onBelowSpeedBumpChanged();
        }
    }

    protected void onBelowSpeedBumpChanged() {
    }

    public boolean isBelowSpeedBump() {
        return this.mIsBelowSpeedBump;
    }

    public void setTintColor(int color) {
        setTintColor(color, false);
    }

    public void setTintColor(int color, boolean animated) {
        if (color != this.mBgTint) {
            this.mBgTint = color;
            updateBackgroundTint(animated);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView, com.android.systemui.statusbar.notification.row.ExpandableView
    public void setDistanceToTopRoundness(float distanceToTopRoundness) {
        super.setDistanceToTopRoundness(distanceToTopRoundness);
        this.mBackgroundNormal.setDistanceToTopRoundness(distanceToTopRoundness);
        this.mBackgroundDimmed.setDistanceToTopRoundness(distanceToTopRoundness);
    }

    public boolean isLastInSection() {
        return this.mLastInSection;
    }

    public boolean isFirstInSection() {
        return this.mFirstInSection;
    }

    public void setLastInSection(boolean lastInSection) {
        if (lastInSection != this.mLastInSection) {
            this.mLastInSection = lastInSection;
            this.mBackgroundNormal.setLastInSection(lastInSection);
            this.mBackgroundDimmed.setLastInSection(lastInSection);
        }
    }

    public void setFirstInSection(boolean firstInSection) {
        if (firstInSection != this.mFirstInSection) {
            this.mFirstInSection = firstInSection;
            this.mBackgroundNormal.setFirstInSection(firstInSection);
            this.mBackgroundDimmed.setFirstInSection(firstInSection);
        }
    }

    public void setOverrideTintColor(int color, float overrideAmount) {
        this.mOverrideTint = color;
        this.mOverrideAmount = overrideAmount;
        int newColor = calculateBgColor();
        setBackgroundTintColor(newColor);
        if (!isDimmable() && this.mNeedsDimming) {
            this.mBackgroundNormal.setDrawableAlpha((int) NotificationUtils.interpolate(255.0f, this.mDimmedAlpha, overrideAmount));
        } else {
            this.mBackgroundNormal.setDrawableAlpha(255);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateBackgroundTint() {
        updateBackgroundTint(false);
    }

    private void updateBackgroundTint(boolean animated) {
        ValueAnimator valueAnimator = this.mBackgroundColorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int rippleColor = getRippleColor();
        this.mBackgroundDimmed.setRippleColor(rippleColor);
        this.mBackgroundNormal.setRippleColor(rippleColor);
        int color = calculateBgColor();
        if (!animated) {
            setBackgroundTintColor(color);
            return;
        }
        int i = this.mCurrentBackgroundTint;
        if (color != i) {
            this.mStartTint = i;
            this.mTargetTint = color;
            this.mBackgroundColorAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mBackgroundColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.row.ActivatableNotificationView.5
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    int newColor = NotificationUtils.interpolateColors(ActivatableNotificationView.this.mStartTint, ActivatableNotificationView.this.mTargetTint, animation.getAnimatedFraction());
                    ActivatableNotificationView.this.setBackgroundTintColor(newColor);
                }
            });
            this.mBackgroundColorAnimator.setDuration(360L);
            this.mBackgroundColorAnimator.setInterpolator(Interpolators.LINEAR);
            this.mBackgroundColorAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.row.ActivatableNotificationView.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ActivatableNotificationView.this.mBackgroundColorAnimator = null;
                }
            });
            this.mBackgroundColorAnimator.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setBackgroundTintColor(int color) {
        if (color != this.mCurrentBackgroundTint) {
            this.mCurrentBackgroundTint = color;
            if (color == this.mNormalColor) {
                color = 0;
            }
            this.mBackgroundDimmed.setTint(color);
            this.mBackgroundNormal.setTint(color);
        }
    }

    private void fadeDimmedBackground() {
        this.mBackgroundDimmed.animate().cancel();
        this.mBackgroundNormal.animate().cancel();
        if (this.mActivated) {
            updateBackground();
            return;
        }
        if (!shouldHideBackground()) {
            if (this.mDimmed) {
                this.mBackgroundDimmed.setVisibility(0);
            } else {
                this.mBackgroundNormal.setVisibility(0);
            }
        }
        float startAlpha = this.mDimmed ? 1.0f : 0.0f;
        float endAlpha = this.mDimmed ? 0.0f : 1.0f;
        int duration = StackStateAnimator.ANIMATION_DURATION_DIMMED_ACTIVATED;
        ObjectAnimator objectAnimator = this.mBackgroundAnimator;
        if (objectAnimator != null) {
            startAlpha = ((Float) objectAnimator.getAnimatedValue()).floatValue();
            duration = (int) this.mBackgroundAnimator.getCurrentPlayTime();
            this.mBackgroundAnimator.removeAllListeners();
            this.mBackgroundAnimator.cancel();
            if (duration <= 0) {
                updateBackground();
                return;
            }
        }
        this.mBackgroundNormal.setAlpha(startAlpha);
        this.mBackgroundAnimator = ObjectAnimator.ofFloat(this.mBackgroundNormal, View.ALPHA, startAlpha, endAlpha);
        this.mBackgroundAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        this.mBackgroundAnimator.setDuration(duration);
        this.mBackgroundAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.row.ActivatableNotificationView.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ActivatableNotificationView.this.updateBackground();
                ActivatableNotificationView.this.mBackgroundAnimator = null;
                ActivatableNotificationView.this.mDimmedBackgroundFadeInAmount = -1.0f;
            }
        });
        this.mBackgroundAnimator.addUpdateListener(this.mBackgroundVisibilityUpdater);
        this.mBackgroundAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateBackgroundAlpha(float transformationAmount) {
        this.mBgAlpha = (isChildInGroup() && this.mDimmed) ? transformationAmount : 1.0f;
        float f = this.mDimmedBackgroundFadeInAmount;
        if (f != -1.0f) {
            this.mBgAlpha *= f;
        }
        this.mBackgroundDimmed.setAlpha(this.mBgAlpha);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetBackgroundAlpha() {
        updateBackgroundAlpha(0.0f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateBackground() {
        cancelFadeAnimations();
        if (shouldHideBackground()) {
            this.mBackgroundDimmed.setVisibility(4);
            this.mBackgroundNormal.setVisibility(this.mActivated ? 0 : 4);
        } else if (this.mDimmed) {
            boolean dontShowDimmed = isGroupExpansionChanging() && isChildInGroup();
            this.mBackgroundDimmed.setVisibility(dontShowDimmed ? 4 : 0);
            NotificationBackgroundView notificationBackgroundView = this.mBackgroundNormal;
            if (this.mActivated || dontShowDimmed) {
                r2 = 0;
            }
            notificationBackgroundView.setVisibility(r2);
        } else {
            this.mBackgroundDimmed.setVisibility(4);
            this.mBackgroundNormal.setVisibility(0);
            this.mBackgroundNormal.setAlpha(1.0f);
            removeCallbacks(this.mTapTimeoutRunnable);
            makeInactive(false);
        }
        setNormalBackgroundVisibilityAmount(this.mBackgroundNormal.getVisibility() != 0 ? 0.0f : 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateBackgroundClipping() {
        this.mBackgroundNormal.setBottomAmountClips(!isChildInGroup());
        this.mBackgroundDimmed.setBottomAmountClips(!isChildInGroup());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldHideBackground() {
        return false;
    }

    private void cancelFadeAnimations() {
        ObjectAnimator objectAnimator = this.mBackgroundAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        this.mBackgroundDimmed.animate().cancel();
        this.mBackgroundNormal.animate().cancel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.ExpandableView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setPivotX(getWidth() / 2);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView, com.android.systemui.statusbar.notification.row.ExpandableView
    public void setActualHeight(int actualHeight, boolean notifyListeners) {
        super.setActualHeight(actualHeight, notifyListeners);
        setPivotY(actualHeight / 2);
        this.mBackgroundNormal.setActualHeight(actualHeight);
        this.mBackgroundDimmed.setActualHeight(actualHeight);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView, com.android.systemui.statusbar.notification.row.ExpandableView
    public void setClipTopAmount(int clipTopAmount) {
        super.setClipTopAmount(clipTopAmount);
        this.mBackgroundNormal.setClipTopAmount(clipTopAmount);
        this.mBackgroundDimmed.setClipTopAmount(clipTopAmount);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView, com.android.systemui.statusbar.notification.row.ExpandableView
    public void setClipBottomAmount(int clipBottomAmount) {
        super.setClipBottomAmount(clipBottomAmount);
        this.mBackgroundNormal.setClipBottomAmount(clipBottomAmount);
        this.mBackgroundDimmed.setClipBottomAmount(clipBottomAmount);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public long performRemoveAnimation(long duration, long delay, float translationDirection, boolean isHeadsUpAnimation, float endLocation, Runnable onFinishedRunnable, AnimatorListenerAdapter animationListener) {
        enableAppearDrawing(true);
        this.mIsHeadsUpAnimation = isHeadsUpAnimation;
        this.mHeadsUpLocation = endLocation;
        if (this.mDrawingAppearAnimation) {
            startAppearAnimation(false, translationDirection, delay, duration, onFinishedRunnable, animationListener);
            return 0L;
        } else if (onFinishedRunnable != null) {
            onFinishedRunnable.run();
            return 0L;
        } else {
            return 0L;
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void performAddAnimation(long delay, long duration, boolean isHeadsUpAppear) {
        enableAppearDrawing(true);
        this.mIsHeadsUpAnimation = isHeadsUpAppear;
        this.mHeadsUpLocation = this.mHeadsUpAddStartLocation;
        if (this.mDrawingAppearAnimation) {
            startAppearAnimation(true, isHeadsUpAppear ? 0.0f : -1.0f, delay, duration, null, null);
        }
    }

    private void startAppearAnimation(final boolean isAppearing, float translationDirection, long delay, long duration, final Runnable onFinishedRunnable, AnimatorListenerAdapter animationListener) {
        float targetValue;
        cancelAppearAnimation();
        this.mAnimationTranslationY = getActualHeight() * translationDirection;
        if (this.mAppearAnimationFraction == -1.0f) {
            if (isAppearing) {
                this.mAppearAnimationFraction = 0.0f;
                this.mAppearAnimationTranslation = this.mAnimationTranslationY;
            } else {
                this.mAppearAnimationFraction = 1.0f;
                this.mAppearAnimationTranslation = 0.0f;
            }
        }
        this.mIsAppearing = isAppearing;
        if (isAppearing) {
            this.mCurrentAppearInterpolator = this.mSlowOutFastInInterpolator;
            this.mCurrentAlphaInterpolator = Interpolators.LINEAR_OUT_SLOW_IN;
            targetValue = 1.0f;
        } else {
            this.mCurrentAppearInterpolator = Interpolators.FAST_OUT_SLOW_IN;
            this.mCurrentAlphaInterpolator = this.mSlowOutLinearInInterpolator;
            targetValue = 0.0f;
        }
        this.mAppearAnimator = ValueAnimator.ofFloat(this.mAppearAnimationFraction, targetValue);
        this.mAppearAnimator.setInterpolator(Interpolators.LINEAR);
        this.mAppearAnimator.setDuration(((float) duration) * Math.abs(this.mAppearAnimationFraction - targetValue));
        this.mAppearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.row.ActivatableNotificationView.8
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                ActivatableNotificationView.this.mAppearAnimationFraction = ((Float) animation.getAnimatedValue()).floatValue();
                ActivatableNotificationView.this.updateAppearAnimationAlpha();
                ActivatableNotificationView.this.updateAppearRect();
                ActivatableNotificationView.this.invalidate();
            }
        });
        if (animationListener != null) {
            this.mAppearAnimator.addListener(animationListener);
        }
        if (delay > 0) {
            updateAppearAnimationAlpha();
            updateAppearRect();
            this.mAppearAnimator.setStartDelay(delay);
        }
        this.mAppearAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.row.ActivatableNotificationView.9
            private boolean mWasCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Runnable runnable = onFinishedRunnable;
                if (runnable != null) {
                    runnable.run();
                }
                if (!this.mWasCancelled) {
                    ActivatableNotificationView.this.enableAppearDrawing(false);
                    ActivatableNotificationView.this.onAppearAnimationFinished(isAppearing);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                this.mWasCancelled = false;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mWasCancelled = true;
            }
        });
        this.mAppearAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onAppearAnimationFinished(boolean wasAppearing) {
    }

    private void cancelAppearAnimation() {
        ValueAnimator valueAnimator = this.mAppearAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mAppearAnimator = null;
        }
    }

    public void cancelAppearDrawing() {
        cancelAppearAnimation();
        enableAppearDrawing(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppearRect() {
        float left;
        float right;
        float top;
        float bottom;
        float inverseFraction = 1.0f - this.mAppearAnimationFraction;
        float translationFraction = this.mCurrentAppearInterpolator.getInterpolation(inverseFraction);
        float translateYTotalAmount = this.mAnimationTranslationY * translationFraction;
        this.mAppearAnimationTranslation = translateYTotalAmount;
        float widthFraction = (inverseFraction - 0.0f) / 0.8f;
        float widthFraction2 = this.mCurrentAppearInterpolator.getInterpolation(Math.min(1.0f, Math.max(0.0f, widthFraction)));
        float startWidthFraction = HORIZONTAL_COLLAPSED_REST_PARTIAL;
        if (this.mIsHeadsUpAnimation && !this.mIsAppearing) {
            startWidthFraction = 0.0f;
        }
        float width = MathUtils.lerp(startWidthFraction, 1.0f, 1.0f - widthFraction2) * getWidth();
        if (this.mIsHeadsUpAnimation) {
            left = MathUtils.lerp(this.mHeadsUpLocation, 0.0f, 1.0f - widthFraction2);
            right = left + width;
        } else {
            left = (getWidth() * 0.5f) - (width / 2.0f);
            right = getWidth() - left;
        }
        float heightFraction = (inverseFraction - 0.0f) / 1.0f;
        float heightFraction2 = this.mCurrentAppearInterpolator.getInterpolation(Math.max(0.0f, heightFraction));
        int actualHeight = getActualHeight();
        float f = this.mAnimationTranslationY;
        if (f > 0.0f) {
            bottom = (actualHeight - ((f * heightFraction2) * 0.1f)) - translateYTotalAmount;
            top = bottom * heightFraction2;
        } else {
            top = (((actualHeight + f) * heightFraction2) * 0.1f) - translateYTotalAmount;
            bottom = (top * heightFraction2) + (actualHeight * (1.0f - heightFraction2));
        }
        this.mAppearAnimationRect.set(left, top, right, bottom);
        float f2 = this.mAppearAnimationTranslation;
        setOutlineRect(left, top + f2, right, f2 + bottom);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppearAnimationAlpha() {
        float contentAlphaProgress = this.mAppearAnimationFraction;
        setContentAlpha(this.mCurrentAlphaInterpolator.getInterpolation(Math.min(1.0f, contentAlphaProgress / 1.0f)));
    }

    private void setContentAlpha(float contentAlpha) {
        int layerType;
        View contentView = getContentView();
        if (contentView.hasOverlappingRendering()) {
            if (contentAlpha == 0.0f || contentAlpha == 1.0f) {
                layerType = 0;
            } else {
                layerType = 2;
            }
            int currentLayerType = contentView.getLayerType();
            if (currentLayerType != layerType) {
                contentView.setLayerType(layerType, null);
            }
        }
        contentView.setAlpha(contentAlpha);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView
    public void applyRoundness() {
        super.applyRoundness();
        applyBackgroundRoundness(getCurrentBackgroundRadiusTop(), getCurrentBackgroundRadiusBottom());
    }

    protected void applyBackgroundRoundness(float topRadius, float bottomRadius) {
        this.mBackgroundDimmed.setRoundness(topRadius, bottomRadius);
        this.mBackgroundNormal.setRoundness(topRadius, bottomRadius);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView
    public void setBackgroundTop(int backgroundTop) {
        this.mBackgroundDimmed.setBackgroundTop(backgroundTop);
        this.mBackgroundNormal.setBackgroundTop(backgroundTop);
    }

    public int calculateBgColor() {
        return calculateBgColor(true, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView
    public boolean childNeedsClipping(View child) {
        if ((child instanceof NotificationBackgroundView) && isClippingNeeded()) {
            return true;
        }
        return super.childNeedsClipping(child);
    }

    private int calculateBgColor(boolean withTint, boolean withOverride) {
        int i;
        if (withOverride && this.mOverrideTint != 0) {
            int defaultTint = calculateBgColor(withTint, false);
            return NotificationUtils.interpolateColors(defaultTint, this.mOverrideTint, this.mOverrideAmount);
        } else if (withTint && (i = this.mBgTint) != 0) {
            return i;
        } else {
            return this.mNormalColor;
        }
    }

    protected int getRippleColor() {
        if (this.mBgTint != 0) {
            return this.mTintedRippleColor;
        }
        return this.mNormalRippleColor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableAppearDrawing(boolean enable) {
        if (enable != this.mDrawingAppearAnimation) {
            this.mDrawingAppearAnimation = enable;
            if (!enable) {
                setContentAlpha(1.0f);
                this.mAppearAnimationFraction = -1.0f;
                setOutlineRect(null);
            }
            invalidate();
        }
    }

    public boolean isDrawingAppearAnimation() {
        return this.mDrawingAppearAnimation;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.mDrawingAppearAnimation) {
            canvas.save();
            canvas.translate(0.0f, this.mAppearAnimationTranslation);
        }
        super.dispatchDraw(canvas);
        if (this.mDrawingAppearAnimation) {
            canvas.restore();
        }
    }

    public void setOnActivatedListener(OnActivatedListener onActivatedListener) {
        this.mOnActivatedListener = onActivatedListener;
    }

    public boolean hasSameBgColor(ActivatableNotificationView otherView) {
        return calculateBgColor() == otherView.calculateBgColor();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setFakeShadowIntensity(float shadowIntensity, float outlineAlpha, int shadowYEnd, int outlineTranslation) {
        boolean hiddenBefore = this.mShadowHidden;
        this.mShadowHidden = shadowIntensity == 0.0f;
        if (!this.mShadowHidden || !hiddenBefore) {
            this.mFakeShadow.setFakeShadowTranslationZ((getTranslationZ() + 0.1f) * shadowIntensity, outlineAlpha, shadowYEnd, outlineTranslation);
        }
    }

    public int getBackgroundColorWithoutTint() {
        return calculateBgColor(false, false);
    }

    public int getCurrentBackgroundTint() {
        return this.mCurrentBackgroundTint;
    }

    public boolean isPinned() {
        return false;
    }

    public boolean isHeadsUpAnimatingAway() {
        return false;
    }

    public boolean isHeadsUp() {
        return false;
    }

    public int getHeadsUpHeightWithoutHeader() {
        return getHeight();
    }
}
