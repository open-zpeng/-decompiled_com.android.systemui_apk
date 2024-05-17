package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Trace;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.util.MathUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.util.function.TriConsumer;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.notification.stack.ViewState;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.util.AlarmTimeout;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class ScrimController implements ViewTreeObserver.OnPreDrawListener, ColorExtractor.OnColorsChangedListener, Dumpable {
    public static final long ANIMATION_DURATION = 220;
    public static final long ANIMATION_DURATION_LONG = 1000;
    public static final float GRADIENT_SCRIM_ALPHA = 0.2f;
    public static final float GRADIENT_SCRIM_ALPHA_BUSY = 0.7f;
    private static final float NOT_INITIALIZED = -1.0f;
    protected static final float SCRIM_BEHIND_ALPHA_KEYGUARD = 0.2f;
    public static final int VISIBILITY_FULLY_OPAQUE = 2;
    public static final int VISIBILITY_FULLY_TRANSPARENT = 0;
    public static final int VISIBILITY_SEMI_TRANSPARENT = 1;
    public static final float WAKE_SENSOR_SCRIM_ALPHA = 0.6f;
    protected boolean mAnimateChange;
    private long mAnimationDelay;
    private boolean mBlankScreen;
    private Runnable mBlankingTransitionRunnable;
    private Callback mCallback;
    private final SysuiColorExtractor mColorExtractor;
    private ColorExtractor.GradientColors mColors;
    private final Context mContext;
    private int mCurrentBehindTint;
    private int mCurrentInFrontTint;
    private boolean mDarkenWhileDragging;
    private boolean mDeferFinishedListener;
    private final DozeParameters mDozeParameters;
    private final Handler mHandler;
    private boolean mKeyguardOccluded;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private boolean mNeedsDrawableColorUpdate;
    private Runnable mOnAnimationFinished;
    private Runnable mPendingFrameCallback;
    private boolean mScreenBlankingCallbackCalled;
    private boolean mScreenOn;
    protected final ScrimView mScrimBehind;
    protected float mScrimBehindAlpha;
    protected float mScrimBehindAlphaResValue;
    protected final ScrimView mScrimInFront;
    private final TriConsumer<ScrimState, Float, ColorExtractor.GradientColors> mScrimStateListener;
    private final Consumer<Integer> mScrimVisibleListener;
    private int mScrimsVisibility;
    private final AlarmTimeout mTimeTicker;
    private boolean mTracking;
    private final UnlockMethodCache mUnlockMethodCache;
    private boolean mUpdatePending;
    private final WakeLock mWakeLock;
    private boolean mWakeLockHeld;
    private boolean mWallpaperSupportsAmbientMode;
    private boolean mWallpaperVisibilityTimedOut;
    static final String TAG = "ScrimController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    static final int TAG_KEY_ANIM = R.id.scrim;
    private static final int TAG_START_ALPHA = R.id.scrim_alpha_start;
    private static final int TAG_END_ALPHA = R.id.scrim_alpha_end;
    private ScrimState mState = ScrimState.UNINITIALIZED;
    protected float mScrimBehindAlphaKeyguard = 0.2f;
    private float mExpansionFraction = 1.0f;
    private boolean mExpansionAffectsAlpha = true;
    protected long mAnimationDuration = -1;
    private final Interpolator mInterpolator = new DecelerateInterpolator();
    private float mCurrentInFrontAlpha = -1.0f;
    private float mCurrentBehindAlpha = -1.0f;
    private final KeyguardVisibilityCallback mKeyguardVisibilityCallback = new KeyguardVisibilityCallback();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface ScrimVisibility {
    }

    public ScrimController(ScrimView scrimBehind, ScrimView scrimInFront, TriConsumer<ScrimState, Float, ColorExtractor.GradientColors> scrimStateListener, Consumer<Integer> scrimVisibleListener, DozeParameters dozeParameters, AlarmManager alarmManager, final KeyguardMonitor keyguardMonitor) {
        this.mScrimBehind = scrimBehind;
        this.mScrimInFront = scrimInFront;
        this.mScrimStateListener = scrimStateListener;
        this.mScrimVisibleListener = scrimVisibleListener;
        this.mContext = scrimBehind.getContext();
        this.mUnlockMethodCache = UnlockMethodCache.getInstance(this.mContext);
        this.mDarkenWhileDragging = !this.mUnlockMethodCache.canSkipBouncer();
        this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        this.mKeyguardUpdateMonitor.registerCallback(this.mKeyguardVisibilityCallback);
        this.mScrimBehindAlphaResValue = this.mContext.getResources().getFloat(R.dimen.scrim_behind_alpha);
        this.mHandler = getHandler();
        this.mTimeTicker = new AlarmTimeout(alarmManager, new AlarmManager.OnAlarmListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$ZxOK9HbkOUnaEI0FKoidLb2saOY
            @Override // android.app.AlarmManager.OnAlarmListener
            public final void onAlarm() {
                ScrimController.this.onHideWallpaperTimeout();
            }
        }, "hide_aod_wallpaper", this.mHandler);
        this.mWakeLock = createWakeLock();
        this.mScrimBehindAlpha = this.mScrimBehindAlphaResValue;
        this.mDozeParameters = dozeParameters;
        keyguardMonitor.addCallback(new KeyguardMonitor.Callback() { // from class: com.android.systemui.statusbar.phone.ScrimController.1
            @Override // com.android.systemui.statusbar.policy.KeyguardMonitor.Callback
            public void onKeyguardFadingAwayChanged() {
                ScrimController.this.setKeyguardFadingAway(keyguardMonitor.isKeyguardFadingAway(), keyguardMonitor.getKeyguardFadingAwayDuration());
            }
        });
        this.mColorExtractor = (SysuiColorExtractor) Dependency.get(SysuiColorExtractor.class);
        this.mColorExtractor.addOnColorsChangedListener(this);
        this.mColors = this.mColorExtractor.getNeutralColors();
        this.mNeedsDrawableColorUpdate = true;
        ScrimState[] states = ScrimState.values();
        for (int i = 0; i < states.length; i++) {
            states[i].init(this.mScrimInFront, this.mScrimBehind, this.mDozeParameters);
            states[i].setScrimBehindAlphaKeyguard(this.mScrimBehindAlphaKeyguard);
        }
        this.mScrimBehind.setDefaultFocusHighlightEnabled(false);
        this.mScrimInFront.setDefaultFocusHighlightEnabled(false);
        updateScrims();
    }

    public void transitionTo(ScrimState state) {
        transitionTo(state, null);
    }

    public void transitionTo(ScrimState state, Callback callback) {
        if (state == this.mState) {
            if (callback != null && this.mCallback != callback) {
                callback.onFinished();
                return;
            }
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "State changed to: " + state);
        }
        if (state == ScrimState.UNINITIALIZED) {
            throw new IllegalArgumentException("Cannot change to UNINITIALIZED.");
        }
        ScrimState oldState = this.mState;
        this.mState = state;
        Trace.traceCounter(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, "scrim_state", this.mState.getIndex());
        Callback callback2 = this.mCallback;
        if (callback2 != null) {
            callback2.onCancelled();
        }
        this.mCallback = callback;
        state.prepare(oldState);
        this.mScreenBlankingCallbackCalled = false;
        this.mAnimationDelay = 0L;
        this.mBlankScreen = state.getBlanksScreen();
        this.mAnimateChange = state.getAnimateChange();
        this.mAnimationDuration = state.getAnimationDuration();
        this.mCurrentInFrontTint = state.getFrontTint();
        this.mCurrentBehindTint = state.getBehindTint();
        this.mCurrentInFrontAlpha = state.getFrontAlpha();
        this.mCurrentBehindAlpha = state.getBehindAlpha();
        if (Float.isNaN(this.mCurrentBehindAlpha) || Float.isNaN(this.mCurrentInFrontAlpha)) {
            throw new IllegalStateException("Scrim opacity is NaN for state: " + state + ", front: " + this.mCurrentInFrontAlpha + ", back: " + this.mCurrentBehindAlpha);
        }
        applyExpansionToAlpha();
        this.mScrimInFront.setFocusable(!state.isLowPowerState());
        this.mScrimBehind.setFocusable(!state.isLowPowerState());
        Runnable runnable = this.mPendingFrameCallback;
        if (runnable != null) {
            this.mScrimBehind.removeCallbacks(runnable);
            this.mPendingFrameCallback = null;
        }
        if (this.mHandler.hasCallbacks(this.mBlankingTransitionRunnable)) {
            this.mHandler.removeCallbacks(this.mBlankingTransitionRunnable);
            this.mBlankingTransitionRunnable = null;
        }
        this.mNeedsDrawableColorUpdate = state != ScrimState.BRIGHTNESS_MIRROR;
        if (this.mState.isLowPowerState()) {
            holdWakeLock();
        }
        this.mWallpaperVisibilityTimedOut = false;
        if (shouldFadeAwayWallpaper()) {
            this.mTimeTicker.schedule(this.mDozeParameters.getWallpaperAodDuration(), 1);
        } else {
            this.mTimeTicker.cancel();
        }
        if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition() && this.mState == ScrimState.UNLOCKED) {
            this.mScrimInFront.postOnAnimationDelayed(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$5DY8P9cXHTvbVZZOVB_VSCJUZk0
                @Override // java.lang.Runnable
                public final void run() {
                    ScrimController.this.scheduleUpdate();
                }
            }, 16L);
            this.mAnimationDelay = 100L;
        } else if ((!this.mDozeParameters.getAlwaysOn() && oldState == ScrimState.AOD) || (this.mState == ScrimState.AOD && !this.mDozeParameters.getDisplayNeedsBlanking())) {
            onPreDraw();
        } else {
            scheduleUpdate();
        }
        dispatchScrimState(this.mScrimBehind.getViewAlpha());
    }

    private boolean shouldFadeAwayWallpaper() {
        Callback callback;
        if (this.mWallpaperSupportsAmbientMode) {
            if (this.mState == ScrimState.AOD && this.mDozeParameters.getAlwaysOn()) {
                return true;
            }
            return this.mState == ScrimState.PULSING && (callback = this.mCallback) != null && callback.shouldTimeoutWallpaper();
        }
        return false;
    }

    public ScrimState getState() {
        return this.mState;
    }

    protected void setScrimBehindValues(float scrimBehindAlphaKeyguard) {
        this.mScrimBehindAlphaKeyguard = scrimBehindAlphaKeyguard;
        ScrimState[] states = ScrimState.values();
        for (ScrimState scrimState : states) {
            scrimState.setScrimBehindAlphaKeyguard(scrimBehindAlphaKeyguard);
        }
        scheduleUpdate();
    }

    public void onTrackingStarted() {
        this.mTracking = true;
        this.mDarkenWhileDragging = true ^ this.mUnlockMethodCache.canSkipBouncer();
    }

    public void onExpandingFinished() {
        this.mTracking = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @VisibleForTesting
    public void onHideWallpaperTimeout() {
        if (this.mState != ScrimState.AOD && this.mState != ScrimState.PULSING) {
            return;
        }
        holdWakeLock();
        this.mWallpaperVisibilityTimedOut = true;
        this.mAnimateChange = true;
        this.mAnimationDuration = this.mDozeParameters.getWallpaperFadeOutDuration();
        scheduleUpdate();
    }

    private void holdWakeLock() {
        if (!this.mWakeLockHeld) {
            WakeLock wakeLock = this.mWakeLock;
            if (wakeLock != null) {
                this.mWakeLockHeld = true;
                wakeLock.acquire(TAG);
                return;
            }
            Log.w(TAG, "Cannot hold wake lock, it has not been set yet");
        }
    }

    public void setPanelExpansion(float fraction) {
        if (Float.isNaN(fraction)) {
            throw new IllegalArgumentException("Fraction should not be NaN");
        }
        if (this.mExpansionFraction != fraction) {
            this.mExpansionFraction = fraction;
            boolean keyguardOrUnlocked = this.mState == ScrimState.UNLOCKED || this.mState == ScrimState.KEYGUARD || this.mState == ScrimState.PULSING;
            if (!keyguardOrUnlocked || !this.mExpansionAffectsAlpha) {
                return;
            }
            applyExpansionToAlpha();
            if (this.mUpdatePending) {
                return;
            }
            setOrAdaptCurrentAnimation(this.mScrimBehind);
            setOrAdaptCurrentAnimation(this.mScrimInFront);
            dispatchScrimState(this.mScrimBehind.getViewAlpha());
            if (this.mWallpaperVisibilityTimedOut) {
                this.mWallpaperVisibilityTimedOut = false;
                this.mTimeTicker.schedule(this.mDozeParameters.getWallpaperAodDuration(), 1);
            }
        }
    }

    private void setOrAdaptCurrentAnimation(View scrim) {
        if (!isAnimating(scrim)) {
            updateScrimColor(scrim, getCurrentScrimAlpha(scrim), getCurrentScrimTint(scrim));
            return;
        }
        ValueAnimator previousAnimator = (ValueAnimator) scrim.getTag(TAG_KEY_ANIM);
        float alpha = getCurrentScrimAlpha(scrim);
        float previousEndValue = ((Float) scrim.getTag(TAG_END_ALPHA)).floatValue();
        float previousStartValue = ((Float) scrim.getTag(TAG_START_ALPHA)).floatValue();
        float relativeDiff = alpha - previousEndValue;
        float newStartValue = previousStartValue + relativeDiff;
        scrim.setTag(TAG_START_ALPHA, Float.valueOf(newStartValue));
        scrim.setTag(TAG_END_ALPHA, Float.valueOf(alpha));
        previousAnimator.setCurrentPlayTime(previousAnimator.getCurrentPlayTime());
    }

    private void applyExpansionToAlpha() {
        if (!this.mExpansionAffectsAlpha) {
            return;
        }
        if (this.mState == ScrimState.UNLOCKED) {
            float behindFraction = getInterpolatedFraction();
            this.mCurrentBehindAlpha = 0.7f * ((float) Math.pow(behindFraction, 0.800000011920929d));
            this.mCurrentInFrontAlpha = 0.0f;
        } else if (this.mState == ScrimState.KEYGUARD || this.mState == ScrimState.PULSING) {
            float interpolatedFract = getInterpolatedFraction();
            float alphaBehind = this.mState.getBehindAlpha();
            if (this.mDarkenWhileDragging) {
                this.mCurrentBehindAlpha = MathUtils.lerp(0.7f, alphaBehind, interpolatedFract);
                this.mCurrentInFrontAlpha = this.mState.getFrontAlpha();
            } else {
                this.mCurrentBehindAlpha = MathUtils.lerp(0.0f, alphaBehind, interpolatedFract);
                this.mCurrentInFrontAlpha = this.mState.getFrontAlpha();
            }
            this.mCurrentBehindTint = ColorUtils.blendARGB(ScrimState.BOUNCER.getBehindTint(), this.mState.getBehindTint(), interpolatedFract);
        }
        if (Float.isNaN(this.mCurrentBehindAlpha) || Float.isNaN(this.mCurrentInFrontAlpha)) {
            throw new IllegalStateException("Scrim opacity is NaN for state: " + this.mState + ", front: " + this.mCurrentInFrontAlpha + ", back: " + this.mCurrentBehindAlpha);
        }
    }

    public void setScrimBehindDrawable(Drawable drawable) {
        this.mScrimBehind.setDrawable(drawable);
    }

    public void setAodFrontScrimAlpha(float alpha) {
        if (((this.mState == ScrimState.AOD && this.mDozeParameters.getAlwaysOn()) || this.mState == ScrimState.PULSING) && this.mCurrentInFrontAlpha != alpha) {
            this.mCurrentInFrontAlpha = alpha;
            updateScrims();
        }
        ScrimState scrimState = this.mState;
        ScrimState.AOD.setAodFrontScrimAlpha(alpha);
        ScrimState scrimState2 = this.mState;
        ScrimState.PULSING.setAodFrontScrimAlpha(alpha);
    }

    public void prepareForGentleWakeUp() {
        if (this.mState == ScrimState.AOD && this.mDozeParameters.getAlwaysOn()) {
            this.mCurrentInFrontAlpha = 1.0f;
            this.mCurrentInFrontTint = -16777216;
            this.mCurrentBehindTint = -16777216;
            this.mAnimateChange = false;
            updateScrims();
            this.mAnimateChange = true;
            this.mAnimationDuration = 1000L;
        }
    }

    public void setWakeLockScreenSensorActive(boolean active) {
        ScrimState[] values;
        for (ScrimState state : ScrimState.values()) {
            state.setWakeLockScreenSensorActive(active);
        }
        if (this.mState == ScrimState.PULSING) {
            float newBehindAlpha = this.mState.getBehindAlpha();
            if (this.mCurrentBehindAlpha != newBehindAlpha) {
                this.mCurrentBehindAlpha = newBehindAlpha;
                if (Float.isNaN(this.mCurrentBehindAlpha)) {
                    throw new IllegalStateException("Scrim opacity is NaN for state: " + this.mState + ", back: " + this.mCurrentBehindAlpha);
                }
                updateScrims();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void scheduleUpdate() {
        if (this.mUpdatePending) {
            return;
        }
        this.mScrimBehind.invalidate();
        this.mScrimBehind.getViewTreeObserver().addOnPreDrawListener(this);
        this.mUpdatePending = true;
    }

    protected void updateScrims() {
        if (this.mNeedsDrawableColorUpdate) {
            this.mNeedsDrawableColorUpdate = false;
            boolean animateScrimInFront = (this.mScrimInFront.getViewAlpha() == 0.0f || this.mBlankScreen) ? false : true;
            boolean animateScrimBehind = (this.mScrimBehind.getViewAlpha() == 0.0f || this.mBlankScreen) ? false : true;
            this.mScrimInFront.setColors(this.mColors, animateScrimInFront);
            this.mScrimBehind.setColors(this.mColors, animateScrimBehind);
            int textColor = this.mColors.supportsDarkText() ? -16777216 : -1;
            int mainColor = this.mColors.getMainColor();
            float minOpacity = ColorUtils.calculateMinimumBackgroundAlpha(textColor, mainColor, 4.5f) / 255.0f;
            this.mScrimBehindAlpha = Math.max(this.mScrimBehindAlphaResValue, minOpacity);
            dispatchScrimState(this.mScrimBehind.getViewAlpha());
        }
        boolean aodWallpaperTimeout = (this.mState == ScrimState.AOD || this.mState == ScrimState.PULSING) && this.mWallpaperVisibilityTimedOut;
        boolean occludedKeyguard = (this.mState == ScrimState.PULSING || this.mState == ScrimState.AOD) && this.mKeyguardOccluded;
        if (aodWallpaperTimeout || occludedKeyguard) {
            this.mCurrentBehindAlpha = 1.0f;
        }
        setScrimInFrontAlpha(this.mCurrentInFrontAlpha);
        setScrimBehindAlpha(this.mCurrentBehindAlpha);
        dispatchScrimsVisible();
    }

    private void dispatchScrimState(float alpha) {
        this.mScrimStateListener.accept(this.mState, Float.valueOf(alpha), this.mScrimInFront.getColors());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchScrimsVisible() {
        int currentScrimVisibility;
        if (this.mScrimInFront.getViewAlpha() == 1.0f || this.mScrimBehind.getViewAlpha() == 1.0f) {
            currentScrimVisibility = 2;
        } else if (this.mScrimInFront.getViewAlpha() == 0.0f && this.mScrimBehind.getViewAlpha() == 0.0f) {
            currentScrimVisibility = 0;
        } else {
            currentScrimVisibility = 1;
        }
        if (this.mScrimsVisibility != currentScrimVisibility) {
            this.mScrimsVisibility = currentScrimVisibility;
            this.mScrimVisibleListener.accept(Integer.valueOf(currentScrimVisibility));
        }
    }

    private float getInterpolatedFraction() {
        float frac = (1.2f * this.mExpansionFraction) - 0.2f;
        if (frac <= 0.0f) {
            return 0.0f;
        }
        return (float) (1.0d - ((1.0d - Math.cos(Math.pow(1.0f - frac, 2.0d) * 3.141590118408203d)) * 0.5d));
    }

    private void setScrimBehindAlpha(float alpha) {
        setScrimAlpha(this.mScrimBehind, alpha);
    }

    private void setScrimInFrontAlpha(float alpha) {
        setScrimAlpha(this.mScrimInFront, alpha);
    }

    private void setScrimAlpha(ScrimView scrim, float alpha) {
        if (alpha == 0.0f) {
            scrim.setClickable(false);
        } else {
            scrim.setClickable(this.mState != ScrimState.AOD);
        }
        updateScrim(scrim, alpha);
    }

    private void updateScrimColor(View scrim, float alpha, int tint) {
        float alpha2 = Math.max(0.0f, Math.min(1.0f, alpha));
        if (scrim instanceof ScrimView) {
            ScrimView scrimView = (ScrimView) scrim;
            Trace.traceCounter(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, scrim == this.mScrimInFront ? "front_scrim_alpha" : "back_scrim_alpha", (int) (255.0f * alpha2));
            Trace.traceCounter(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, scrim == this.mScrimInFront ? "front_scrim_tint" : "back_scrim_tint", Color.alpha(tint));
            scrimView.setTint(tint);
            scrimView.setViewAlpha(alpha2);
        } else {
            scrim.setAlpha(alpha2);
        }
        dispatchScrimsVisible();
    }

    private void startScrimAnimation(final View scrim, float current) {
        ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
        final int initialScrimTint = scrim instanceof ScrimView ? ((ScrimView) scrim).getTint() : 0;
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$ScrimController$RfJJyPt1cPl4hraLjBCUJgqPhOM
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ScrimController.this.lambda$startScrimAnimation$0$ScrimController(scrim, initialScrimTint, valueAnimator);
            }
        });
        anim.setInterpolator(this.mInterpolator);
        anim.setStartDelay(this.mAnimationDelay);
        anim.setDuration(this.mAnimationDuration);
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.ScrimController.2
            private Callback lastCallback;

            {
                this.lastCallback = ScrimController.this.mCallback;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ScrimController.this.onFinished(this.lastCallback);
                scrim.setTag(ScrimController.TAG_KEY_ANIM, null);
                ScrimController.this.dispatchScrimsVisible();
                if (!ScrimController.this.mDeferFinishedListener && ScrimController.this.mOnAnimationFinished != null) {
                    ScrimController.this.mOnAnimationFinished.run();
                    ScrimController.this.mOnAnimationFinished = null;
                }
            }
        });
        scrim.setTag(TAG_START_ALPHA, Float.valueOf(current));
        scrim.setTag(TAG_END_ALPHA, Float.valueOf(getCurrentScrimAlpha(scrim)));
        scrim.setTag(TAG_KEY_ANIM, anim);
        anim.start();
    }

    public /* synthetic */ void lambda$startScrimAnimation$0$ScrimController(View scrim, int initialScrimTint, ValueAnimator animation) {
        float startAlpha = ((Float) scrim.getTag(TAG_START_ALPHA)).floatValue();
        float animAmount = ((Float) animation.getAnimatedValue()).floatValue();
        int finalScrimTint = getCurrentScrimTint(scrim);
        float finalScrimAlpha = getCurrentScrimAlpha(scrim);
        float alpha = MathUtils.lerp(startAlpha, finalScrimAlpha, animAmount);
        float alpha2 = MathUtils.constrain(alpha, 0.0f, 1.0f);
        int tint = ColorUtils.blendARGB(initialScrimTint, finalScrimTint, animAmount);
        updateScrimColor(scrim, alpha2, tint);
        dispatchScrimsVisible();
    }

    private float getCurrentScrimAlpha(View scrim) {
        if (scrim == this.mScrimInFront) {
            return this.mCurrentInFrontAlpha;
        }
        if (scrim == this.mScrimBehind) {
            return this.mCurrentBehindAlpha;
        }
        throw new IllegalArgumentException("Unknown scrim view");
    }

    private int getCurrentScrimTint(View scrim) {
        if (scrim == this.mScrimInFront) {
            return this.mCurrentInFrontTint;
        }
        if (scrim == this.mScrimBehind) {
            return this.mCurrentBehindTint;
        }
        throw new IllegalArgumentException("Unknown scrim view");
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        this.mScrimBehind.getViewTreeObserver().removeOnPreDrawListener(this);
        this.mUpdatePending = false;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onStart();
        }
        updateScrims();
        if (this.mOnAnimationFinished != null && !isAnimating(this.mScrimInFront) && !isAnimating(this.mScrimBehind)) {
            this.mOnAnimationFinished.run();
            this.mOnAnimationFinished = null;
            return true;
        }
        return true;
    }

    private void onFinished() {
        onFinished(this.mCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFinished(Callback callback) {
        if (this.mWakeLockHeld) {
            this.mWakeLock.release(TAG);
            this.mWakeLockHeld = false;
        }
        if (callback != null) {
            callback.onFinished();
            if (callback == this.mCallback) {
                this.mCallback = null;
            }
        }
        if (this.mState == ScrimState.UNLOCKED) {
            this.mCurrentInFrontTint = 0;
            this.mCurrentBehindTint = 0;
        }
    }

    private boolean isAnimating(View scrim) {
        return scrim.getTag(TAG_KEY_ANIM) != null;
    }

    @VisibleForTesting
    void setOnAnimationFinished(Runnable onAnimationFinished) {
        this.mOnAnimationFinished = onAnimationFinished;
    }

    private void updateScrim(ScrimView scrim, float alpha) {
        Callback callback;
        float currentAlpha = scrim.getViewAlpha();
        ValueAnimator previousAnimator = (ValueAnimator) ViewState.getChildTag(scrim, TAG_KEY_ANIM);
        if (previousAnimator != null) {
            if (this.mAnimateChange) {
                this.mDeferFinishedListener = true;
            }
            cancelAnimator(previousAnimator);
            this.mDeferFinishedListener = false;
        }
        if (this.mPendingFrameCallback != null) {
            return;
        }
        if (this.mBlankScreen) {
            blankDisplay();
            return;
        }
        if (!this.mScreenBlankingCallbackCalled && (callback = this.mCallback) != null) {
            callback.onDisplayBlanked();
            this.mScreenBlankingCallbackCalled = true;
        }
        if (scrim == this.mScrimBehind) {
            dispatchScrimState(alpha);
        }
        boolean wantsAlphaUpdate = alpha != currentAlpha;
        boolean wantsTintUpdate = scrim.getTint() != getCurrentScrimTint(scrim);
        if (wantsAlphaUpdate || wantsTintUpdate) {
            if (this.mAnimateChange) {
                startScrimAnimation(scrim, currentAlpha);
                return;
            }
            updateScrimColor(scrim, alpha, getCurrentScrimTint(scrim));
            onFinished();
            return;
        }
        onFinished();
    }

    @VisibleForTesting
    protected void cancelAnimator(ValueAnimator previousAnimator) {
        if (previousAnimator != null) {
            previousAnimator.cancel();
        }
    }

    private void blankDisplay() {
        updateScrimColor(this.mScrimInFront, 1.0f, -16777216);
        this.mPendingFrameCallback = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$ScrimController$kDQtFE2BFSUNax7wJ8UgLaBZyEo
            @Override // java.lang.Runnable
            public final void run() {
                ScrimController.this.lambda$blankDisplay$2$ScrimController();
            }
        };
        doOnTheNextFrame(this.mPendingFrameCallback);
    }

    public /* synthetic */ void lambda$blankDisplay$2$ScrimController() {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onDisplayBlanked();
            this.mScreenBlankingCallbackCalled = true;
        }
        this.mBlankingTransitionRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$ScrimController$xRo1WgiULtMHpaX-VTe04p0hBHI
            @Override // java.lang.Runnable
            public final void run() {
                ScrimController.this.lambda$blankDisplay$1$ScrimController();
            }
        };
        int delay = this.mScreenOn ? 32 : 500;
        if (DEBUG) {
            Log.d(TAG, "Fading out scrims with delay: " + delay);
        }
        this.mHandler.postDelayed(this.mBlankingTransitionRunnable, delay);
    }

    public /* synthetic */ void lambda$blankDisplay$1$ScrimController() {
        this.mBlankingTransitionRunnable = null;
        this.mPendingFrameCallback = null;
        this.mBlankScreen = false;
        updateScrims();
    }

    @VisibleForTesting
    protected void doOnTheNextFrame(Runnable callback) {
        this.mScrimBehind.postOnAnimationDelayed(callback, 32L);
    }

    @VisibleForTesting
    protected Handler getHandler() {
        return new Handler();
    }

    public int getBackgroundColor() {
        int color = this.mColors.getMainColor();
        return Color.argb((int) (this.mScrimBehind.getViewAlpha() * Color.alpha(color)), Color.red(color), Color.green(color), Color.blue(color));
    }

    public void setScrimBehindChangeRunnable(Runnable changeRunnable) {
        this.mScrimBehind.setChangeRunnable(changeRunnable);
    }

    public void setCurrentUser(int currentUser) {
    }

    public void onColorsChanged(ColorExtractor colorExtractor, int which) {
        this.mColors = this.mColorExtractor.getNeutralColors();
        this.mNeedsDrawableColorUpdate = true;
        scheduleUpdate();
    }

    @VisibleForTesting
    protected WakeLock createWakeLock() {
        return new DelayedWakeLock(this.mHandler, WakeLock.createPartial(this.mContext, "Scrims"));
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println(" ScrimController: ");
        pw.print("  state: ");
        pw.println(this.mState);
        pw.print("  frontScrim:");
        pw.print(" viewAlpha=");
        pw.print(this.mScrimInFront.getViewAlpha());
        pw.print(" alpha=");
        pw.print(this.mCurrentInFrontAlpha);
        pw.print(" tint=0x");
        pw.println(Integer.toHexString(this.mScrimInFront.getTint()));
        pw.print("  backScrim:");
        pw.print(" viewAlpha=");
        pw.print(this.mScrimBehind.getViewAlpha());
        pw.print(" alpha=");
        pw.print(this.mCurrentBehindAlpha);
        pw.print(" tint=0x");
        pw.println(Integer.toHexString(this.mScrimBehind.getTint()));
        pw.print("  mTracking=");
        pw.println(this.mTracking);
        pw.print("  mExpansionFraction=");
        pw.println(this.mExpansionFraction);
    }

    public void setWallpaperSupportsAmbientMode(boolean wallpaperSupportsAmbientMode) {
        this.mWallpaperSupportsAmbientMode = wallpaperSupportsAmbientMode;
        ScrimState[] states = ScrimState.values();
        for (ScrimState scrimState : states) {
            scrimState.setWallpaperSupportsAmbientMode(wallpaperSupportsAmbientMode);
        }
    }

    public void onScreenTurnedOn() {
        this.mScreenOn = true;
        if (this.mHandler.hasCallbacks(this.mBlankingTransitionRunnable)) {
            if (DEBUG) {
                Log.d(TAG, "Shorter blanking because screen turned on. All good.");
            }
            this.mHandler.removeCallbacks(this.mBlankingTransitionRunnable);
            this.mBlankingTransitionRunnable.run();
        }
    }

    public void onScreenTurnedOff() {
        this.mScreenOn = false;
    }

    public void setExpansionAffectsAlpha(boolean expansionAffectsAlpha) {
        this.mExpansionAffectsAlpha = expansionAffectsAlpha;
    }

    public void setKeyguardOccluded(boolean keyguardOccluded) {
        this.mKeyguardOccluded = keyguardOccluded;
        updateScrims();
    }

    public void setHasBackdrop(boolean hasBackdrop) {
        ScrimState[] values;
        for (ScrimState state : ScrimState.values()) {
            state.setHasBackdrop(hasBackdrop);
        }
        if (this.mState == ScrimState.AOD || this.mState == ScrimState.PULSING) {
            float newBehindAlpha = this.mState.getBehindAlpha();
            if (Float.isNaN(newBehindAlpha)) {
                throw new IllegalStateException("Scrim opacity is NaN for state: " + this.mState + ", back: " + this.mCurrentBehindAlpha);
            } else if (this.mCurrentBehindAlpha != newBehindAlpha) {
                this.mCurrentBehindAlpha = newBehindAlpha;
                updateScrims();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setKeyguardFadingAway(boolean fadingAway, long duration) {
        ScrimState[] values;
        for (ScrimState state : ScrimState.values()) {
            state.setKeyguardFadingAway(fadingAway, duration);
        }
    }

    public void setLaunchingAffordanceWithPreview(boolean launchingAffordanceWithPreview) {
        ScrimState[] values;
        for (ScrimState state : ScrimState.values()) {
            state.setLaunchingAffordanceWithPreview(launchingAffordanceWithPreview);
        }
    }

    /* loaded from: classes21.dex */
    public interface Callback {
        default void onStart() {
        }

        default void onDisplayBlanked() {
        }

        default void onFinished() {
        }

        default void onCancelled() {
        }

        default boolean shouldTimeoutWallpaper() {
            return false;
        }
    }

    /* loaded from: classes21.dex */
    private class KeyguardVisibilityCallback extends KeyguardUpdateMonitorCallback {
        private KeyguardVisibilityCallback() {
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onKeyguardVisibilityChanged(boolean showing) {
            ScrimController.this.mNeedsDrawableColorUpdate = true;
            ScrimController.this.scheduleUpdate();
        }
    }
}
