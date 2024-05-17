package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import com.android.internal.util.LatencyTracker;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public abstract class PanelView extends FrameLayout {
    public static final boolean DEBUG = false;
    private static final int INITIAL_OPENING_PEEK_DURATION = 200;
    private static final int NO_FIXED_DURATION = -1;
    private static final int PEEK_ANIMATION_DURATION = 360;
    public static final String TAG = PanelView.class.getSimpleName();
    private boolean mAnimateAfterExpanding;
    private boolean mAnimatingOnDown;
    PanelBar mBar;
    private Interpolator mBounceInterpolator;
    private boolean mClosing;
    private boolean mCollapsedAndHeadsUpOnDown;
    protected long mDownTime;
    private boolean mExpandLatencyTracking;
    private float mExpandedFraction;
    protected float mExpandedHeight;
    protected boolean mExpanding;
    protected ArrayList<PanelExpansionListener> mExpansionListeners;
    private final FalsingManager mFalsingManager;
    private int mFixedDuration;
    private FlingAnimationUtils mFlingAnimationUtils;
    private FlingAnimationUtils mFlingAnimationUtilsClosing;
    private FlingAnimationUtils mFlingAnimationUtilsDismissing;
    private final Runnable mFlingCollapseRunnable;
    private boolean mGestureWaitForTouchSlop;
    private boolean mHasLayoutedSinceDown;
    protected HeadsUpManagerPhone mHeadsUpManager;
    private ValueAnimator mHeightAnimator;
    protected boolean mHintAnimationRunning;
    private float mHintDistance;
    private boolean mIgnoreXTouchSlop;
    private float mInitialOffsetOnTouch;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private boolean mInstantExpanding;
    private boolean mJustPeeked;
    protected KeyguardBottomAreaView mKeyguardBottomArea;
    protected final KeyguardMonitor mKeyguardMonitor;
    protected boolean mLaunchingNotification;
    private LockscreenGestureLogger mLockscreenGestureLogger;
    private float mMinExpandHeight;
    private boolean mMotionAborted;
    private float mNextCollapseSpeedUpFactor;
    private boolean mNotificationsDragEnabled;
    private boolean mOverExpandedBeforeFling;
    private boolean mPanelClosedOnDown;
    private boolean mPanelUpdateWhenAnimatorEnds;
    private ObjectAnimator mPeekAnimator;
    private float mPeekHeight;
    private boolean mPeekTouching;
    protected final Runnable mPostCollapseRunnable;
    protected StatusBar mStatusBar;
    protected final SysuiStatusBarStateController mStatusBarStateController;
    private boolean mTouchAboveFalsingThreshold;
    private boolean mTouchDisabled;
    protected int mTouchSlop;
    private boolean mTouchSlopExceeded;
    protected boolean mTouchSlopExceededBeforeDown;
    private boolean mTouchStartedInEmptyArea;
    protected boolean mTracking;
    private int mTrackingPointer;
    private int mUnlockFalsingThreshold;
    private boolean mUpdateFlingOnLayout;
    private float mUpdateFlingVelocity;
    private boolean mUpwardsWhenTresholdReached;
    private final VelocityTracker mVelocityTracker;
    private boolean mVibrateOnOpening;
    private final VibratorHelper mVibratorHelper;
    private String mViewName;

    protected abstract boolean fullyExpandedClearAllVisible();

    protected abstract int getClearAllHeight();

    protected abstract int getMaxPanelHeight();

    protected abstract float getOpeningHeight();

    protected abstract float getOverExpansionAmount();

    protected abstract float getOverExpansionPixels();

    protected abstract float getPeekHeight();

    protected abstract boolean isClearAllVisible();

    protected abstract boolean isDozing();

    protected abstract boolean isInContentBounds(float f, float f2);

    protected abstract boolean isPanelVisibleBecauseOfHeadsUp();

    protected abstract boolean isTrackingBlocked();

    protected abstract void onHeightUpdated(float f);

    protected abstract boolean onMiddleClicked();

    public abstract void resetViews(boolean z);

    protected abstract void setOverExpansion(float f, boolean z);

    protected abstract boolean shouldGestureIgnoreXTouchSlop(float f, float f2);

    protected abstract boolean shouldGestureWaitForTouchSlop();

    protected abstract boolean shouldUseDismissingAnimation();

    private final void logf(String fmt, Object... args) {
        String str;
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        if (this.mViewName != null) {
            str = this.mViewName + ": ";
        } else {
            str = "";
        }
        sb.append(str);
        sb.append(String.format(fmt, args));
        Log.v(str2, sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onExpandingFinished() {
        this.mBar.onExpandingFinished();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onExpandingStarted() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyExpandingStarted() {
        if (!this.mExpanding) {
            this.mExpanding = true;
            onExpandingStarted();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void notifyExpandingFinished() {
        endClosing();
        if (this.mExpanding) {
            this.mExpanding = false;
            onExpandingFinished();
        }
    }

    private void runPeekAnimation(long duration, float peekHeight, final boolean collapseWhenFinished) {
        this.mPeekHeight = peekHeight;
        if (this.mHeightAnimator != null) {
            return;
        }
        ObjectAnimator objectAnimator = this.mPeekAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        this.mPeekAnimator = ObjectAnimator.ofFloat(this, "expandedHeight", this.mPeekHeight).setDuration(duration);
        this.mPeekAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        this.mPeekAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.PanelView.1
            private boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PanelView.this.mPeekAnimator = null;
                if (!this.mCancelled && collapseWhenFinished) {
                    PanelView panelView = PanelView.this;
                    panelView.postOnAnimation(panelView.mPostCollapseRunnable);
                }
            }
        });
        notifyExpandingStarted();
        this.mPeekAnimator.start();
        this.mJustPeeked = true;
    }

    public PanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLockscreenGestureLogger = new LockscreenGestureLogger();
        this.mFixedDuration = -1;
        this.mExpansionListeners = new ArrayList<>();
        this.mExpandedFraction = 0.0f;
        this.mExpandedHeight = 0.0f;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mNextCollapseSpeedUpFactor = 1.0f;
        this.mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
        this.mStatusBarStateController = (SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class);
        this.mFlingCollapseRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.PanelView.3
            @Override // java.lang.Runnable
            public void run() {
                PanelView panelView = PanelView.this;
                panelView.fling(0.0f, false, panelView.mNextCollapseSpeedUpFactor, false);
            }
        };
        this.mPostCollapseRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.PanelView.7
            @Override // java.lang.Runnable
            public void run() {
                PanelView.this.collapse(false, 1.0f);
            }
        };
        this.mFlingAnimationUtils = new FlingAnimationUtils(context, 0.6f, 0.6f);
        this.mFlingAnimationUtilsClosing = new FlingAnimationUtils(context, 0.5f, 0.6f);
        this.mFlingAnimationUtilsDismissing = new FlingAnimationUtils(context, 0.5f, 0.2f, 0.6f, 0.84f);
        this.mBounceInterpolator = new BounceInterpolator();
        this.mFalsingManager = (FalsingManager) Dependency.get(FalsingManager.class);
        this.mNotificationsDragEnabled = getResources().getBoolean(R.bool.config_enableNotificationShadeDrag);
        this.mVibratorHelper = (VibratorHelper) Dependency.get(VibratorHelper.class);
        this.mVibrateOnOpening = this.mContext.getResources().getBoolean(R.bool.config_vibrateOnIconAnimation);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void loadDimens() {
        Resources res = getContext().getResources();
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mHintDistance = res.getDimension(R.dimen.hint_move_distance);
        this.mUnlockFalsingThreshold = res.getDimensionPixelSize(R.dimen.unlock_falsing_threshold);
    }

    private void addMovement(MotionEvent event) {
        float deltaX = event.getRawX() - event.getX();
        float deltaY = event.getRawY() - event.getY();
        event.offsetLocation(deltaX, deltaY);
        this.mVelocityTracker.addMovement(event);
        event.offsetLocation(-deltaX, -deltaY);
    }

    public void setTouchAndAnimationDisabled(boolean disabled) {
        this.mTouchDisabled = disabled;
        if (this.mTouchDisabled) {
            cancelHeightAnimator();
            if (this.mTracking) {
                onTrackingStopped(true);
            }
            notifyExpandingFinished();
        }
    }

    public void startExpandLatencyTracking() {
        if (LatencyTracker.isEnabled(this.mContext)) {
            LatencyTracker.getInstance(this.mContext).onActionStart(0);
            this.mExpandLatencyTracking = true;
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int upPointer;
        if (this.mInstantExpanding || ((this.mTouchDisabled && event.getActionMasked() != 3) || (this.mMotionAborted && event.getActionMasked() != 0))) {
            return false;
        }
        if (!this.mNotificationsDragEnabled) {
            if (this.mTracking) {
                onTrackingStopped(true);
            }
            return false;
        } else if (isFullyCollapsed() && event.isFromSource(8194)) {
            if (event.getAction() == 1) {
                expand(true);
            }
            return true;
        } else {
            int pointerIndex = event.findPointerIndex(this.mTrackingPointer);
            if (pointerIndex < 0) {
                pointerIndex = 0;
                this.mTrackingPointer = event.getPointerId(0);
            }
            float x = event.getX(pointerIndex);
            float y = event.getY(pointerIndex);
            if (event.getActionMasked() == 0) {
                this.mGestureWaitForTouchSlop = shouldGestureWaitForTouchSlop();
                this.mIgnoreXTouchSlop = isFullyCollapsed() || shouldGestureIgnoreXTouchSlop(x, y);
            }
            int actionMasked = event.getActionMasked();
            if (actionMasked == 0) {
                startExpandMotion(x, y, false, this.mExpandedHeight);
                this.mJustPeeked = false;
                this.mMinExpandHeight = 0.0f;
                this.mPanelClosedOnDown = isFullyCollapsed();
                this.mHasLayoutedSinceDown = false;
                this.mUpdateFlingOnLayout = false;
                this.mMotionAborted = false;
                this.mPeekTouching = this.mPanelClosedOnDown;
                this.mDownTime = SystemClock.uptimeMillis();
                this.mTouchAboveFalsingThreshold = false;
                this.mCollapsedAndHeadsUpOnDown = isFullyCollapsed() && this.mHeadsUpManager.hasPinnedHeadsUp();
                addMovement(event);
                if (!this.mGestureWaitForTouchSlop || ((this.mHeightAnimator != null && !this.mHintAnimationRunning) || this.mPeekAnimator != null)) {
                    this.mTouchSlopExceeded = ((this.mHeightAnimator == null || this.mHintAnimationRunning) && this.mPeekAnimator == null && !this.mTouchSlopExceededBeforeDown) ? false : true;
                    cancelHeightAnimator();
                    cancelPeek();
                    onTrackingStarted();
                }
                if (isFullyCollapsed() && !this.mHeadsUpManager.hasPinnedHeadsUp() && !this.mStatusBar.isBouncerShowing()) {
                    startOpening(event);
                }
            } else {
                if (actionMasked != 1) {
                    if (actionMasked == 2) {
                        addMovement(event);
                        float h = y - this.mInitialTouchY;
                        if (Math.abs(h) > this.mTouchSlop && (Math.abs(h) > Math.abs(x - this.mInitialTouchX) || this.mIgnoreXTouchSlop)) {
                            this.mTouchSlopExceeded = true;
                            if (this.mGestureWaitForTouchSlop && !this.mTracking && !this.mCollapsedAndHeadsUpOnDown) {
                                if (!this.mJustPeeked && this.mInitialOffsetOnTouch != 0.0f) {
                                    startExpandMotion(x, y, false, this.mExpandedHeight);
                                    h = 0.0f;
                                }
                                cancelHeightAnimator();
                                onTrackingStarted();
                            }
                        }
                        float newHeight = Math.max(0.0f, this.mInitialOffsetOnTouch + h);
                        if (newHeight > this.mPeekHeight) {
                            ObjectAnimator objectAnimator = this.mPeekAnimator;
                            if (objectAnimator != null) {
                                objectAnimator.cancel();
                            }
                            this.mJustPeeked = false;
                        } else if (this.mPeekAnimator == null && this.mJustPeeked) {
                            float f = this.mExpandedHeight;
                            this.mInitialOffsetOnTouch = f;
                            this.mInitialTouchY = y;
                            this.mMinExpandHeight = f;
                            this.mJustPeeked = false;
                        }
                        float newHeight2 = Math.max(newHeight, this.mMinExpandHeight);
                        if ((-h) >= getFalsingThreshold()) {
                            this.mTouchAboveFalsingThreshold = true;
                            this.mUpwardsWhenTresholdReached = isDirectionUpwards(x, y);
                        }
                        if (!this.mJustPeeked && ((!this.mGestureWaitForTouchSlop || this.mTracking) && !isTrackingBlocked())) {
                            setExpandedHeightInternal(newHeight2);
                        }
                    } else if (actionMasked != 3) {
                        if (actionMasked != 5) {
                            if (actionMasked == 6 && this.mTrackingPointer == (upPointer = event.getPointerId(event.getActionIndex()))) {
                                int newIndex = event.getPointerId(0) != upPointer ? 0 : 1;
                                float newY = event.getY(newIndex);
                                float newX = event.getX(newIndex);
                                this.mTrackingPointer = event.getPointerId(newIndex);
                                startExpandMotion(newX, newY, true, this.mExpandedHeight);
                            }
                        } else if (this.mStatusBarStateController.getState() == 1) {
                            this.mMotionAborted = true;
                            endMotionEvent(event, x, y, true);
                            return false;
                        }
                    }
                }
                addMovement(event);
                endMotionEvent(event, x, y, false);
            }
            return !this.mGestureWaitForTouchSlop || this.mTracking;
        }
    }

    private void startOpening(MotionEvent event) {
        runPeekAnimation(200L, getOpeningHeight(), false);
        notifyBarPanelExpansionChanged();
        maybeVibrateOnOpening();
        float width = this.mStatusBar.getDisplayWidth();
        float height = this.mStatusBar.getDisplayHeight();
        int rot = this.mStatusBar.getRotation();
        this.mLockscreenGestureLogger.writeAtFractionalPosition(1328, (int) ((event.getX() / width) * 100.0f), (int) ((event.getY() / height) * 100.0f), rot);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void maybeVibrateOnOpening() {
        if (this.mVibrateOnOpening) {
            this.mVibratorHelper.vibrate(2);
        }
    }

    private boolean isDirectionUpwards(float x, float y) {
        float xDiff = x - this.mInitialTouchX;
        float yDiff = y - this.mInitialTouchY;
        return yDiff < 0.0f && Math.abs(yDiff) >= Math.abs(xDiff);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startExpandingFromPeek() {
        this.mStatusBar.handlePeekToExpandTransistion();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startExpandMotion(float newX, float newY, boolean startTracking, float expandedHeight) {
        this.mInitialOffsetOnTouch = expandedHeight;
        this.mInitialTouchY = newY;
        this.mInitialTouchX = newX;
        if (startTracking) {
            this.mTouchSlopExceeded = true;
            setExpandedHeight(this.mInitialOffsetOnTouch);
            onTrackingStarted();
        }
    }

    private void endMotionEvent(MotionEvent event, float x, float y, boolean forceCancel) {
        this.mTrackingPointer = -1;
        boolean z = true;
        if ((this.mTracking && this.mTouchSlopExceeded) || Math.abs(x - this.mInitialTouchX) > this.mTouchSlop || Math.abs(y - this.mInitialTouchY) > this.mTouchSlop || event.getActionMasked() == 3 || forceCancel) {
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float vel = this.mVelocityTracker.getYVelocity();
            float vectorVel = (float) Math.hypot(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
            boolean expand = flingExpands(vel, vectorVel, x, y) || event.getActionMasked() == 3 || forceCancel;
            DozeLog.traceFling(expand, this.mTouchAboveFalsingThreshold, this.mStatusBar.isFalsingThresholdNeeded(), this.mStatusBar.isWakeUpComingFromTouch());
            if (!expand && this.mStatusBarStateController.getState() == 1) {
                float displayDensity = this.mStatusBar.getDisplayDensity();
                int heightDp = (int) Math.abs((y - this.mInitialTouchY) / displayDensity);
                int velocityDp = (int) Math.abs(vel / displayDensity);
                this.mLockscreenGestureLogger.write(186, heightDp, velocityDp);
            }
            fling(vel, expand, isFalseTouch(x, y));
            onTrackingStopped(expand);
            if (!expand || !this.mPanelClosedOnDown || this.mHasLayoutedSinceDown) {
                z = false;
            }
            this.mUpdateFlingOnLayout = z;
            if (this.mUpdateFlingOnLayout) {
                this.mUpdateFlingVelocity = vel;
            }
        } else if (this.mPanelClosedOnDown && !this.mHeadsUpManager.hasPinnedHeadsUp() && !this.mTracking && !this.mStatusBar.isBouncerShowing() && !this.mKeyguardMonitor.isKeyguardFadingAway()) {
            long timePassed = SystemClock.uptimeMillis() - this.mDownTime;
            if (timePassed < ViewConfiguration.getLongPressTimeout()) {
                runPeekAnimation(360L, getPeekHeight(), true);
            } else {
                postOnAnimation(this.mPostCollapseRunnable);
            }
        } else if (!this.mStatusBar.isBouncerShowing()) {
            boolean expands = onEmptySpaceClick(this.mInitialTouchX);
            onTrackingStopped(expands);
        }
        this.mVelocityTracker.clear();
        this.mPeekTouching = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getCurrentExpandVelocity() {
        this.mVelocityTracker.computeCurrentVelocity(1000);
        return this.mVelocityTracker.getYVelocity();
    }

    private int getFalsingThreshold() {
        float factor = this.mStatusBar.isWakeUpComingFromTouch() ? 1.5f : 1.0f;
        return (int) (this.mUnlockFalsingThreshold * factor);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onTrackingStopped(boolean expand) {
        this.mTracking = false;
        this.mBar.onTrackingStopped(expand);
        notifyBarPanelExpansionChanged();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onTrackingStarted() {
        endClosing();
        this.mTracking = true;
        this.mBar.onTrackingStarted();
        notifyExpandingStarted();
        notifyBarPanelExpansionChanged();
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int upPointer;
        if (this.mInstantExpanding || !this.mNotificationsDragEnabled || this.mTouchDisabled || (this.mMotionAborted && event.getActionMasked() != 0)) {
            return false;
        }
        int pointerIndex = event.findPointerIndex(this.mTrackingPointer);
        if (pointerIndex < 0) {
            pointerIndex = 0;
            this.mTrackingPointer = event.getPointerId(0);
        }
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        boolean scrolledToBottom = isScrolledToBottom();
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            this.mStatusBar.userActivity();
            this.mAnimatingOnDown = this.mHeightAnimator != null;
            this.mMinExpandHeight = 0.0f;
            this.mDownTime = SystemClock.uptimeMillis();
            if ((this.mAnimatingOnDown && this.mClosing && !this.mHintAnimationRunning) || this.mPeekAnimator != null) {
                cancelHeightAnimator();
                cancelPeek();
                this.mTouchSlopExceeded = true;
                return true;
            }
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
            this.mTouchStartedInEmptyArea = !isInContentBounds(x, y);
            this.mTouchSlopExceeded = this.mTouchSlopExceededBeforeDown;
            this.mJustPeeked = false;
            this.mMotionAborted = false;
            this.mPanelClosedOnDown = isFullyCollapsed();
            this.mCollapsedAndHeadsUpOnDown = false;
            this.mHasLayoutedSinceDown = false;
            this.mUpdateFlingOnLayout = false;
            this.mTouchAboveFalsingThreshold = false;
            addMovement(event);
        } else {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    float h = y - this.mInitialTouchY;
                    addMovement(event);
                    if (scrolledToBottom || this.mTouchStartedInEmptyArea || this.mAnimatingOnDown) {
                        float hAbs = Math.abs(h);
                        int i = this.mTouchSlop;
                        if ((h < (-i) || (this.mAnimatingOnDown && hAbs > i)) && hAbs > Math.abs(x - this.mInitialTouchX)) {
                            cancelHeightAnimator();
                            startExpandMotion(x, y, true, this.mExpandedHeight);
                            return true;
                        }
                    }
                } else if (actionMasked != 3) {
                    if (actionMasked != 5) {
                        if (actionMasked == 6 && this.mTrackingPointer == (upPointer = event.getPointerId(event.getActionIndex()))) {
                            int newIndex = event.getPointerId(0) != upPointer ? 0 : 1;
                            this.mTrackingPointer = event.getPointerId(newIndex);
                            this.mInitialTouchX = event.getX(newIndex);
                            this.mInitialTouchY = event.getY(newIndex);
                        }
                    } else if (this.mStatusBarStateController.getState() == 1) {
                        this.mMotionAborted = true;
                        this.mVelocityTracker.clear();
                    }
                }
            }
            this.mVelocityTracker.clear();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cancelHeightAnimator() {
        ValueAnimator valueAnimator = this.mHeightAnimator;
        if (valueAnimator != null) {
            if (valueAnimator.isRunning()) {
                this.mPanelUpdateWhenAnimatorEnds = false;
            }
            this.mHeightAnimator.cancel();
        }
        endClosing();
    }

    private void endClosing() {
        if (this.mClosing) {
            this.mClosing = false;
            onClosingFinished();
        }
    }

    protected boolean isScrolledToBottom() {
        return true;
    }

    protected float getContentHeight() {
        return this.mExpandedHeight;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        loadDimens();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadDimens();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean flingExpands(float vel, float vectorVel, float x, float y) {
        if (this.mFalsingManager.isUnlockingDisabled() || isFalseTouch(x, y)) {
            return true;
        }
        if (Math.abs(vectorVel) < this.mFlingAnimationUtils.getMinVelocityPxPerSecond()) {
            return shouldExpandWhenNotFlinging();
        }
        return vel > 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldExpandWhenNotFlinging() {
        return getExpandedFraction() > 0.5f;
    }

    private boolean isFalseTouch(float x, float y) {
        if (this.mStatusBar.isFalsingThresholdNeeded()) {
            if (this.mFalsingManager.isClassiferEnabled()) {
                return this.mFalsingManager.isFalseTouch();
            }
            if (this.mTouchAboveFalsingThreshold) {
                if (this.mUpwardsWhenTresholdReached) {
                    return false;
                }
                return !isDirectionUpwards(x, y);
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void fling(float vel, boolean expand) {
        fling(vel, expand, 1.0f, false);
    }

    protected void fling(float vel, boolean expand, boolean expandBecauseOfFalsing) {
        fling(vel, expand, 1.0f, expandBecauseOfFalsing);
    }

    protected void fling(float vel, boolean expand, float collapseSpeedUpFactor, boolean expandBecauseOfFalsing) {
        cancelPeek();
        float target = expand ? getMaxPanelHeight() : 0.0f;
        if (!expand) {
            this.mClosing = true;
        }
        flingToHeight(vel, expand, target, collapseSpeedUpFactor, expandBecauseOfFalsing);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void flingToHeight(float vel, boolean expand, float target, float collapseSpeedUpFactor, boolean expandBecauseOfFalsing) {
        final boolean clearAllExpandHack = expand && fullyExpandedClearAllVisible() && this.mExpandedHeight < ((float) (getMaxPanelHeight() - getClearAllHeight())) && !isClearAllVisible();
        if (clearAllExpandHack) {
            target = getMaxPanelHeight() - getClearAllHeight();
        }
        if (target == this.mExpandedHeight || (getOverExpansionAmount() > 0.0f && expand)) {
            notifyExpandingFinished();
            return;
        }
        this.mOverExpandedBeforeFling = getOverExpansionAmount() > 0.0f;
        ValueAnimator animator = createHeightAnimator(target);
        if (expand) {
            if (expandBecauseOfFalsing && vel < 0.0f) {
                vel = 0.0f;
            }
            this.mFlingAnimationUtils.apply(animator, this.mExpandedHeight, target, vel, getHeight());
            if (vel == 0.0f) {
                animator.setDuration(350L);
            }
        } else {
            if (shouldUseDismissingAnimation()) {
                if (vel == 0.0f) {
                    animator.setInterpolator(Interpolators.PANEL_CLOSE_ACCELERATED);
                    long duration = ((this.mExpandedHeight / getHeight()) * 100.0f) + 200.0f;
                    animator.setDuration(duration);
                } else {
                    this.mFlingAnimationUtilsDismissing.apply(animator, this.mExpandedHeight, target, vel, getHeight());
                }
            } else {
                this.mFlingAnimationUtilsClosing.apply(animator, this.mExpandedHeight, target, vel, getHeight());
            }
            if (vel == 0.0f) {
                animator.setDuration(((float) animator.getDuration()) / collapseSpeedUpFactor);
            }
            int i = this.mFixedDuration;
            if (i != -1) {
                animator.setDuration(i);
            }
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.PanelView.2
            private boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (clearAllExpandHack && !this.mCancelled) {
                    PanelView panelView = PanelView.this;
                    panelView.setExpandedHeightInternal(panelView.getMaxPanelHeight());
                }
                PanelView.this.setAnimator(null);
                if (!this.mCancelled) {
                    PanelView.this.notifyExpandingFinished();
                }
                PanelView.this.notifyBarPanelExpansionChanged();
            }
        });
        setAnimator(animator);
        animator.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mViewName = getResources().getResourceName(getId());
    }

    public String getName() {
        return this.mViewName;
    }

    public void setExpandedHeight(float height) {
        setExpandedHeightInternal(getOverExpansionPixels() + height);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mStatusBar.onPanelLaidOut();
        requestPanelHeightUpdate();
        this.mHasLayoutedSinceDown = true;
        if (this.mUpdateFlingOnLayout) {
            abortAnimations();
            fling(this.mUpdateFlingVelocity, true);
            this.mUpdateFlingOnLayout = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void requestPanelHeightUpdate() {
        float currentMaxPanelHeight = getMaxPanelHeight();
        if (isFullyCollapsed() || currentMaxPanelHeight == this.mExpandedHeight || this.mPeekAnimator != null || this.mPeekTouching) {
            return;
        }
        if (this.mTracking && !isTrackingBlocked()) {
            return;
        }
        if (this.mHeightAnimator != null) {
            this.mPanelUpdateWhenAnimatorEnds = true;
        } else {
            setExpandedHeight(currentMaxPanelHeight);
        }
    }

    public void setExpandedHeightInternal(float h) {
        if (this.mExpandLatencyTracking && h != 0.0f) {
            DejankUtils.postAfterTraversal(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$PanelView$NcQsgCLFImw9_GKeELFJ1HpKiII
                @Override // java.lang.Runnable
                public final void run() {
                    PanelView.this.lambda$setExpandedHeightInternal$0$PanelView();
                }
            });
            this.mExpandLatencyTracking = false;
        }
        float fhWithoutOverExpansion = getMaxPanelHeight() - getOverExpansionAmount();
        if (this.mHeightAnimator == null) {
            float overExpansionPixels = Math.max(0.0f, h - fhWithoutOverExpansion);
            if (getOverExpansionPixels() != overExpansionPixels && this.mTracking) {
                setOverExpansion(overExpansionPixels, true);
            }
            this.mExpandedHeight = Math.min(h, fhWithoutOverExpansion) + getOverExpansionAmount();
        } else {
            this.mExpandedHeight = h;
            if (this.mOverExpandedBeforeFling) {
                setOverExpansion(Math.max(0.0f, h - fhWithoutOverExpansion), false);
            }
        }
        float f = this.mExpandedHeight;
        if (f < 1.0f && f != 0.0f && this.mClosing) {
            this.mExpandedHeight = 0.0f;
            ValueAnimator valueAnimator = this.mHeightAnimator;
            if (valueAnimator != null) {
                valueAnimator.end();
            }
        }
        this.mExpandedFraction = Math.min(1.0f, fhWithoutOverExpansion != 0.0f ? this.mExpandedHeight / fhWithoutOverExpansion : 0.0f);
        onHeightUpdated(this.mExpandedHeight);
        notifyBarPanelExpansionChanged();
    }

    public /* synthetic */ void lambda$setExpandedHeightInternal$0$PanelView() {
        LatencyTracker.getInstance(this.mContext).onActionEnd(0);
    }

    public void setExpandedFraction(float frac) {
        setExpandedHeight(getMaxPanelHeight() * frac);
    }

    public float getExpandedHeight() {
        return this.mExpandedHeight;
    }

    public float getExpandedFraction() {
        return this.mExpandedFraction;
    }

    public boolean isFullyExpanded() {
        return this.mExpandedHeight >= ((float) getMaxPanelHeight());
    }

    public boolean isFullyCollapsed() {
        return this.mExpandedFraction <= 0.0f;
    }

    public boolean isCollapsing() {
        return this.mClosing || this.mLaunchingNotification;
    }

    public boolean isTracking() {
        return this.mTracking;
    }

    public void setBar(PanelBar panelBar) {
        this.mBar = panelBar;
    }

    public void collapse(boolean delayed, float speedUpFactor) {
        if (canPanelBeCollapsed()) {
            cancelHeightAnimator();
            notifyExpandingStarted();
            this.mClosing = true;
            if (delayed) {
                this.mNextCollapseSpeedUpFactor = speedUpFactor;
                postDelayed(this.mFlingCollapseRunnable, 120L);
                return;
            }
            fling(0.0f, false, speedUpFactor, false);
        }
    }

    public boolean canPanelBeCollapsed() {
        return (isFullyCollapsed() || this.mTracking || this.mClosing) ? false : true;
    }

    public void cancelPeek() {
        boolean cancelled = false;
        ObjectAnimator objectAnimator = this.mPeekAnimator;
        if (objectAnimator != null) {
            cancelled = true;
            objectAnimator.cancel();
        }
        if (cancelled) {
            notifyBarPanelExpansionChanged();
        }
    }

    public void expand(boolean animate) {
        if (!isFullyCollapsed() && !isCollapsing()) {
            return;
        }
        this.mInstantExpanding = true;
        this.mAnimateAfterExpanding = animate;
        this.mUpdateFlingOnLayout = false;
        abortAnimations();
        cancelPeek();
        if (this.mTracking) {
            onTrackingStopped(true);
        }
        if (this.mExpanding) {
            notifyExpandingFinished();
        }
        notifyBarPanelExpansionChanged();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.systemui.statusbar.phone.PanelView.4
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (!PanelView.this.mInstantExpanding) {
                    PanelView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else if (PanelView.this.mStatusBar.getStatusBarWindow().getHeight() != PanelView.this.mStatusBar.getStatusBarHeight()) {
                    PanelView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (PanelView.this.mAnimateAfterExpanding) {
                        PanelView.this.notifyExpandingStarted();
                        PanelView.this.fling(0.0f, true);
                    } else {
                        PanelView.this.setExpandedFraction(1.0f);
                    }
                    PanelView.this.mInstantExpanding = false;
                }
            }
        });
        requestLayout();
    }

    public void instantCollapse() {
        abortAnimations();
        setExpandedFraction(0.0f);
        if (this.mExpanding) {
            notifyExpandingFinished();
        }
        if (this.mInstantExpanding) {
            this.mInstantExpanding = false;
            notifyBarPanelExpansionChanged();
        }
    }

    private void abortAnimations() {
        cancelPeek();
        cancelHeightAnimator();
        removeCallbacks(this.mPostCollapseRunnable);
        removeCallbacks(this.mFlingCollapseRunnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onClosingFinished() {
        this.mBar.onClosingFinished();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startUnlockHintAnimation() {
        if (this.mHeightAnimator != null || this.mTracking) {
            return;
        }
        cancelPeek();
        notifyExpandingStarted();
        startUnlockHintAnimationPhase1(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$PanelView$tAljekoGx9mlKIleW6Fmi59MCOs
            @Override // java.lang.Runnable
            public final void run() {
                PanelView.this.lambda$startUnlockHintAnimation$1$PanelView();
            }
        });
        onUnlockHintStarted();
        this.mHintAnimationRunning = true;
    }

    public /* synthetic */ void lambda$startUnlockHintAnimation$1$PanelView() {
        notifyExpandingFinished();
        onUnlockHintFinished();
        this.mHintAnimationRunning = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onUnlockHintFinished() {
        this.mStatusBar.onHintFinished();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onUnlockHintStarted() {
        this.mStatusBar.onUnlockHintStarted();
    }

    public boolean isUnlockHintRunning() {
        return this.mHintAnimationRunning;
    }

    private void startUnlockHintAnimationPhase1(final Runnable onAnimationFinished) {
        float target = Math.max(0.0f, getMaxPanelHeight() - this.mHintDistance);
        ValueAnimator animator = createHeightAnimator(target);
        animator.setDuration(250L);
        animator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.PanelView.5
            private boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (this.mCancelled) {
                    PanelView.this.setAnimator(null);
                    onAnimationFinished.run();
                    return;
                }
                PanelView.this.startUnlockHintAnimationPhase2(onAnimationFinished);
            }
        });
        animator.start();
        setAnimator(animator);
        View[] viewsToAnimate = {this.mKeyguardBottomArea.getIndicationArea(), this.mStatusBar.getAmbientIndicationContainer()};
        for (final View v : viewsToAnimate) {
            if (v != null) {
                v.animate().translationY(-this.mHintDistance).setDuration(250L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).withEndAction(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$PanelView$NxwxpLj3ZElyZw-bMmniBqBlhdY
                    @Override // java.lang.Runnable
                    public final void run() {
                        PanelView.this.lambda$startUnlockHintAnimationPhase1$2$PanelView(v);
                    }
                }).start();
            }
        }
    }

    public /* synthetic */ void lambda$startUnlockHintAnimationPhase1$2$PanelView(View v) {
        v.animate().translationY(0.0f).setDuration(450L).setInterpolator(this.mBounceInterpolator).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAnimator(ValueAnimator animator) {
        this.mHeightAnimator = animator;
        if (animator == null && this.mPanelUpdateWhenAnimatorEnds) {
            this.mPanelUpdateWhenAnimatorEnds = false;
            requestPanelHeightUpdate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startUnlockHintAnimationPhase2(final Runnable onAnimationFinished) {
        ValueAnimator animator = createHeightAnimator(getMaxPanelHeight());
        animator.setDuration(450L);
        animator.setInterpolator(this.mBounceInterpolator);
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.PanelView.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PanelView.this.setAnimator(null);
                onAnimationFinished.run();
                PanelView.this.notifyBarPanelExpansionChanged();
            }
        });
        animator.start();
        setAnimator(animator);
    }

    private ValueAnimator createHeightAnimator(float targetHeight) {
        ValueAnimator animator = ValueAnimator.ofFloat(this.mExpandedHeight, targetHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$PanelView$IA-IF_ME3fPevHucfSNm-kDD_eY
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PanelView.this.lambda$createHeightAnimator$3$PanelView(valueAnimator);
            }
        });
        return animator;
    }

    public /* synthetic */ void lambda$createHeightAnimator$3$PanelView(ValueAnimator animation) {
        setExpandedHeightInternal(((Float) animation.getAnimatedValue()).floatValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyBarPanelExpansionChanged() {
        PanelBar panelBar = this.mBar;
        if (panelBar != null) {
            float f = this.mExpandedFraction;
            panelBar.panelExpansionChanged(f, f > 0.0f || this.mPeekAnimator != null || this.mInstantExpanding || isPanelVisibleBecauseOfHeadsUp() || this.mTracking || this.mHeightAnimator != null);
        }
        for (int i = 0; i < this.mExpansionListeners.size(); i++) {
            this.mExpansionListeners.get(i).onPanelExpansionChanged(this.mExpandedFraction, this.mTracking);
        }
    }

    public void addExpansionListener(PanelExpansionListener panelExpansionListener) {
        this.mExpansionListeners.add(panelExpansionListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean onEmptySpaceClick(float x) {
        if (this.mHintAnimationRunning) {
            return true;
        }
        return onMiddleClicked();
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        Object[] objArr = new Object[11];
        objArr[0] = getClass().getSimpleName();
        objArr[1] = Float.valueOf(getExpandedHeight());
        objArr[2] = Integer.valueOf(getMaxPanelHeight());
        objArr[3] = this.mClosing ? "T" : "f";
        objArr[4] = this.mTracking ? "T" : "f";
        objArr[5] = this.mJustPeeked ? "T" : "f";
        ObjectAnimator objectAnimator = this.mPeekAnimator;
        objArr[6] = objectAnimator;
        String str = " (started)";
        objArr[7] = (objectAnimator == null || !objectAnimator.isStarted()) ? "" : " (started)";
        ValueAnimator valueAnimator = this.mHeightAnimator;
        objArr[8] = valueAnimator;
        objArr[9] = (valueAnimator == null || !valueAnimator.isStarted()) ? "" : "";
        objArr[10] = this.mTouchDisabled ? "T" : "f";
        pw.println(String.format("[PanelView(%s): expandedHeight=%f maxPanelHeight=%d closing=%s tracking=%s justPeeked=%s peekAnim=%s%s timeAnim=%s%s touchDisabled=%s]", objArr));
    }

    public void setHeadsUpManager(HeadsUpManagerPhone headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    public void setLaunchingNotification(boolean launchingNotification) {
        this.mLaunchingNotification = launchingNotification;
    }

    public void collapseWithDuration(int animationDuration) {
        this.mFixedDuration = animationDuration;
        collapse(false, 1.0f);
        this.mFixedDuration = -1;
    }
}
