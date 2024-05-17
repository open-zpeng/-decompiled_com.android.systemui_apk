package com.android.systemui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.os.Handler;
import android.util.ArrayMap;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
/* loaded from: classes21.dex */
public class SwipeHelper implements Gefingerpoken {
    private static final boolean CONSTRAIN_SWIPE = true;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_INVALIDATE = false;
    private static final int DEFAULT_ESCAPE_ANIMATION_DURATION = 200;
    private static final boolean DISMISS_IF_SWIPED_FAR_ENOUGH = true;
    private static final boolean FADE_OUT_DURING_SWIPE = true;
    private static final int MAX_DISMISS_VELOCITY = 4000;
    private static final int MAX_ESCAPE_ANIMATION_DURATION = 400;
    static final float MAX_SCROLL_SIZE_FRACTION = 0.3f;
    private static final boolean SLOW_ANIMATIONS = false;
    private static final int SNAP_ANIM_LEN = 150;
    public static final float SWIPED_FAR_ENOUGH_SIZE_FRACTION = 0.6f;
    private static final float SWIPE_ESCAPE_VELOCITY = 500.0f;
    static final float SWIPE_PROGRESS_FADE_END = 0.5f;
    static final String TAG = "com.android.systemui.SwipeHelper";
    public static final int X = 0;
    public static final int Y = 1;
    private final Callback mCallback;
    private boolean mCanCurrViewBeDimissed;
    private final Context mContext;
    private View mCurrView;
    private float mDensityScale;
    private boolean mDisableHwLayers;
    private boolean mDragging;
    private final boolean mFadeDependingOnAmountSwiped;
    private final FalsingManager mFalsingManager;
    private final int mFalsingThreshold;
    private final FlingAnimationUtils mFlingAnimationUtils;
    private float mInitialTouchPos;
    private boolean mLongPressSent;
    private boolean mMenuRowIntercepting;
    private float mPagingTouchSlop;
    private float mPerpendicularInitialTouchPos;
    private boolean mSnappingChild;
    private final int mSwipeDirection;
    private boolean mTouchAboveFalsingThreshold;
    private Runnable mWatchLongPress;
    private float mMinSwipeProgress = 0.0f;
    private float mMaxSwipeProgress = 1.0f;
    private float mTranslation = 0.0f;
    private final int[] mTmpPos = new int[2];
    private final ArrayMap<View, Animator> mDismissPendingMap = new ArrayMap<>();
    protected final Handler mHandler = new Handler();
    private final VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private final long mLongPressTimeout = ViewConfiguration.getLongPressTimeout() * 1.5f;

    public SwipeHelper(int swipeDirection, Callback callback, Context context, FalsingManager falsingManager) {
        this.mContext = context;
        this.mCallback = callback;
        this.mSwipeDirection = swipeDirection;
        this.mPagingTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        Resources res = context.getResources();
        this.mDensityScale = res.getDisplayMetrics().density;
        this.mFalsingThreshold = res.getDimensionPixelSize(R.dimen.swipe_helper_falsing_threshold);
        this.mFadeDependingOnAmountSwiped = res.getBoolean(R.bool.config_fadeDependingOnAmountSwiped);
        this.mFalsingManager = falsingManager;
        this.mFlingAnimationUtils = new FlingAnimationUtils(context, ((float) getMaxEscapeAnimDuration()) / 1000.0f);
    }

    public void setDensityScale(float densityScale) {
        this.mDensityScale = densityScale;
    }

    public void setPagingTouchSlop(float pagingTouchSlop) {
        this.mPagingTouchSlop = pagingTouchSlop;
    }

    public void setDisableHardwareLayers(boolean disableHwLayers) {
        this.mDisableHwLayers = disableHwLayers;
    }

    private float getPos(MotionEvent ev) {
        return this.mSwipeDirection == 0 ? ev.getX() : ev.getY();
    }

    private float getPerpendicularPos(MotionEvent ev) {
        return this.mSwipeDirection == 0 ? ev.getY() : ev.getX();
    }

    protected float getTranslation(View v) {
        return this.mSwipeDirection == 0 ? v.getTranslationX() : v.getTranslationY();
    }

    private float getVelocity(VelocityTracker vt) {
        return this.mSwipeDirection == 0 ? vt.getXVelocity() : vt.getYVelocity();
    }

    protected ObjectAnimator createTranslationAnimation(View v, float newPos) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, this.mSwipeDirection == 0 ? View.TRANSLATION_X : View.TRANSLATION_Y, newPos);
        return anim;
    }

    private float getPerpendicularVelocity(VelocityTracker vt) {
        return this.mSwipeDirection == 0 ? vt.getYVelocity() : vt.getXVelocity();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Animator getViewTranslationAnimator(View v, float target, ValueAnimator.AnimatorUpdateListener listener) {
        ObjectAnimator anim = createTranslationAnimation(v, target);
        if (listener != null) {
            anim.addUpdateListener(listener);
        }
        return anim;
    }

    protected void setTranslation(View v, float translate) {
        if (v == null) {
            return;
        }
        if (this.mSwipeDirection == 0) {
            v.setTranslationX(translate);
        } else {
            v.setTranslationY(translate);
        }
    }

    protected float getSize(View v) {
        return this.mSwipeDirection == 0 ? v.getMeasuredWidth() : v.getMeasuredHeight();
    }

    public void setMinSwipeProgress(float minSwipeProgress) {
        this.mMinSwipeProgress = minSwipeProgress;
    }

    public void setMaxSwipeProgress(float maxSwipeProgress) {
        this.mMaxSwipeProgress = maxSwipeProgress;
    }

    private float getSwipeProgressForOffset(View view, float translation) {
        float viewSize = getSize(view);
        float result = Math.abs(translation / viewSize);
        return Math.min(Math.max(this.mMinSwipeProgress, result), this.mMaxSwipeProgress);
    }

    private float getSwipeAlpha(float progress) {
        if (this.mFadeDependingOnAmountSwiped) {
            return Math.max(1.0f - progress, 0.0f);
        }
        return 1.0f - Math.max(0.0f, Math.min(1.0f, progress / 0.5f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSwipeProgressFromOffset(View animView, boolean dismissable) {
        updateSwipeProgressFromOffset(animView, dismissable, getTranslation(animView));
    }

    private void updateSwipeProgressFromOffset(View animView, boolean dismissable, float translation) {
        float swipeProgress = getSwipeProgressForOffset(animView, translation);
        if (!this.mCallback.updateSwipeProgress(animView, dismissable, swipeProgress) && dismissable) {
            if (!this.mDisableHwLayers) {
                if (swipeProgress != 0.0f && swipeProgress != 1.0f) {
                    animView.setLayerType(2, null);
                } else {
                    animView.setLayerType(0, null);
                }
            }
            animView.setAlpha(getSwipeAlpha(swipeProgress));
        }
        invalidateGlobalRegion(animView);
    }

    public static void invalidateGlobalRegion(View view) {
        invalidateGlobalRegion(view, new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
    }

    public static void invalidateGlobalRegion(View view, RectF childBounds) {
        while (view.getParent() != null && (view.getParent() instanceof View)) {
            view = (View) view.getParent();
            view.getMatrix().mapRect(childBounds);
            view.invalidate((int) Math.floor(childBounds.left), (int) Math.floor(childBounds.top), (int) Math.ceil(childBounds.right), (int) Math.ceil(childBounds.bottom));
        }
    }

    public void cancelLongPress() {
        Runnable runnable = this.mWatchLongPress;
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
            this.mWatchLongPress = null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0024, code lost:
        if (r0 != 3) goto L13;
     */
    @Override // com.android.systemui.Gefingerpoken
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onInterceptTouchEvent(final android.view.MotionEvent r10) {
        /*
            Method dump skipped, instructions count: 258
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.SwipeHelper.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    public void dismissChild(View view, float velocity, boolean useAccelerateInterpolator) {
        dismissChild(view, velocity, null, 0L, useAccelerateInterpolator, 0L, false);
    }

    public void dismissChild(final View animView, float velocity, final Runnable endAction, long delay, boolean useAccelerateInterpolator, long fixedDuration, boolean isDismissAll) {
        float newPos;
        long duration;
        Animator anim;
        final boolean canBeDismissed = this.mCallback.canChildBeDismissed(animView);
        boolean z = false;
        boolean isLayoutRtl = animView.getLayoutDirection() == 1;
        boolean animateUpForMenu = velocity == 0.0f && (getTranslation(animView) == 0.0f || isDismissAll) && this.mSwipeDirection == 1;
        boolean animateLeftForRtl = velocity == 0.0f && (getTranslation(animView) == 0.0f || isDismissAll) && isLayoutRtl;
        if ((Math.abs(velocity) > getEscapeVelocity() && velocity < 0.0f) || (getTranslation(animView) < 0.0f && !isDismissAll)) {
            z = true;
        }
        boolean animateLeft = z;
        if (animateLeft || animateLeftForRtl || animateUpForMenu) {
            float newPos2 = getSize(animView);
            newPos = -newPos2;
        } else {
            newPos = getSize(animView);
        }
        if (fixedDuration == 0) {
            if (velocity != 0.0f) {
                duration = Math.min(400L, (int) ((Math.abs(newPos - getTranslation(animView)) * 1000.0f) / Math.abs(velocity)));
            } else {
                duration = 200;
            }
        } else {
            duration = fixedDuration;
        }
        if (!this.mDisableHwLayers) {
            animView.setLayerType(2, null);
        }
        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.SwipeHelper.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                SwipeHelper.this.onTranslationUpdate(animView, ((Float) animation.getAnimatedValue()).floatValue(), canBeDismissed);
            }
        };
        Animator anim2 = getViewTranslationAnimator(animView, newPos, updateListener);
        if (anim2 == null) {
            return;
        }
        if (useAccelerateInterpolator) {
            anim2.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
            anim2.setDuration(duration);
            anim = anim2;
        } else {
            anim = anim2;
            this.mFlingAnimationUtils.applyDismissing(anim2, getTranslation(animView), newPos, velocity, getSize(animView));
        }
        if (delay > 0) {
            anim.setStartDelay(delay);
        }
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.SwipeHelper.3
            private boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                SwipeHelper.this.updateSwipeProgressFromOffset(animView, canBeDismissed);
                SwipeHelper.this.mDismissPendingMap.remove(animView);
                boolean wasRemoved = false;
                View view = animView;
                if (view instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow row = (ExpandableNotificationRow) view;
                    wasRemoved = row.isRemoved();
                }
                if (!this.mCancelled || wasRemoved) {
                    SwipeHelper.this.mCallback.onChildDismissed(animView);
                }
                Runnable runnable = endAction;
                if (runnable != null) {
                    runnable.run();
                }
                if (!SwipeHelper.this.mDisableHwLayers) {
                    animView.setLayerType(0, null);
                }
            }
        });
        prepareDismissAnimation(animView, anim);
        this.mDismissPendingMap.put(animView, anim);
        anim.start();
    }

    protected void prepareDismissAnimation(View view, Animator anim) {
    }

    public void snapChild(final View animView, final float targetLeft, float velocity) {
        final boolean canBeDismissed = this.mCallback.canChildBeDismissed(animView);
        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.SwipeHelper.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                SwipeHelper.this.onTranslationUpdate(animView, ((Float) animation.getAnimatedValue()).floatValue(), canBeDismissed);
            }
        };
        Animator anim = getViewTranslationAnimator(animView, targetLeft, updateListener);
        if (anim == null) {
            return;
        }
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.SwipeHelper.5
            boolean wasCancelled = false;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.wasCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SwipeHelper.this.mSnappingChild = false;
                if (!this.wasCancelled) {
                    SwipeHelper.this.updateSwipeProgressFromOffset(animView, canBeDismissed);
                    SwipeHelper.this.onChildSnappedBack(animView, targetLeft);
                    SwipeHelper.this.mCallback.onChildSnappedBack(animView, targetLeft);
                }
            }
        });
        prepareSnapBackAnimation(animView, anim);
        this.mSnappingChild = true;
        float maxDistance = Math.abs(targetLeft - getTranslation(animView));
        this.mFlingAnimationUtils.apply(anim, getTranslation(animView), targetLeft, velocity, maxDistance);
        anim.start();
    }

    protected void onChildSnappedBack(View animView, float targetLeft) {
    }

    protected void prepareSnapBackAnimation(View view, Animator anim) {
    }

    public void onDownUpdate(View currView, MotionEvent ev) {
    }

    protected void onMoveUpdate(View view, MotionEvent ev, float totalTranslation, float delta) {
    }

    public void onTranslationUpdate(View animView, float value, boolean canBeDismissed) {
        updateSwipeProgressFromOffset(animView, canBeDismissed, value);
    }

    private void snapChildInstantly(View view) {
        boolean canAnimViewBeDismissed = this.mCallback.canChildBeDismissed(view);
        setTranslation(view, 0.0f);
        updateSwipeProgressFromOffset(view, canAnimViewBeDismissed);
    }

    public void snapChildIfNeeded(View view, boolean animate, float targetLeft) {
        if ((this.mDragging && this.mCurrView == view) || this.mSnappingChild) {
            return;
        }
        boolean needToSnap = false;
        Animator dismissPendingAnim = this.mDismissPendingMap.get(view);
        if (dismissPendingAnim == null) {
            if (getTranslation(view) != 0.0f) {
                needToSnap = true;
            }
        } else {
            needToSnap = true;
            dismissPendingAnim.cancel();
        }
        if (needToSnap) {
            if (animate) {
                snapChild(view, targetLeft, 0.0f);
            } else {
                snapChildInstantly(view);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0036, code lost:
        if (r0 != 4) goto L25;
     */
    @Override // com.android.systemui.Gefingerpoken
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r14) {
        /*
            Method dump skipped, instructions count: 253
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.SwipeHelper.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private int getFalsingThreshold() {
        float factor = this.mCallback.getFalsingThresholdFactor();
        return (int) (this.mFalsingThreshold * factor);
    }

    private float getMaxVelocity() {
        return this.mDensityScale * 4000.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getEscapeVelocity() {
        return getUnscaledEscapeVelocity() * this.mDensityScale;
    }

    protected float getUnscaledEscapeVelocity() {
        return SWIPE_ESCAPE_VELOCITY;
    }

    protected long getMaxEscapeAnimDuration() {
        return 400L;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean swipedFarEnough() {
        float translation = getTranslation(this.mCurrView);
        return Math.abs(translation) > getSize(this.mCurrView) * 0.6f;
    }

    public boolean isDismissGesture(MotionEvent ev) {
        float translation = getTranslation(this.mCurrView);
        if (ev.getActionMasked() != 1 || this.mFalsingManager.isUnlockingDisabled() || isFalseGesture(ev)) {
            return false;
        }
        if (swipedFastEnough() || swipedFarEnough()) {
            return this.mCallback.canChildBeDismissedInDirection(this.mCurrView, (translation > 0.0f ? 1 : (translation == 0.0f ? 0 : -1)) > 0);
        }
        return false;
    }

    public boolean isFalseGesture(MotionEvent ev) {
        boolean falsingDetected = this.mCallback.isAntiFalsingNeeded();
        boolean falsingDetected2 = true;
        if (this.mFalsingManager.isClassiferEnabled()) {
            if (!falsingDetected || !this.mFalsingManager.isFalseTouch()) {
                falsingDetected2 = false;
            }
            return falsingDetected2;
        }
        if (!falsingDetected || this.mTouchAboveFalsingThreshold) {
            falsingDetected2 = false;
        }
        return falsingDetected2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0029, code lost:
        if ((r0 > 0.0f) == (r1 > 0.0f)) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean swipedFastEnough() {
        /*
            r6 = this;
            android.view.VelocityTracker r0 = r6.mVelocityTracker
            float r0 = r6.getVelocity(r0)
            android.view.View r1 = r6.mCurrView
            float r1 = r6.getTranslation(r1)
            float r2 = java.lang.Math.abs(r0)
            float r3 = r6.getEscapeVelocity()
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            r3 = 1
            r4 = 0
            if (r2 <= 0) goto L2c
            r2 = 0
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 <= 0) goto L21
            r5 = r3
            goto L22
        L21:
            r5 = r4
        L22:
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 <= 0) goto L28
            r2 = r3
            goto L29
        L28:
            r2 = r4
        L29:
            if (r5 != r2) goto L2c
            goto L2d
        L2c:
            r3 = r4
        L2d:
            r2 = r3
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.SwipeHelper.swipedFastEnough():boolean");
    }

    protected boolean handleUpEvent(MotionEvent ev, View animView, float velocity, float translation) {
        return false;
    }

    /* loaded from: classes21.dex */
    public interface Callback {
        boolean canChildBeDismissed(View view);

        View getChildAtPosition(MotionEvent motionEvent);

        float getFalsingThresholdFactor();

        boolean isAntiFalsingNeeded();

        void onBeginDrag(View view);

        void onChildDismissed(View view);

        void onChildSnappedBack(View view, float f);

        void onDragCancelled(View view);

        boolean updateSwipeProgress(View view, boolean z, float f);

        default boolean canChildBeDismissedInDirection(View v, boolean isRightOrDown) {
            return canChildBeDismissed(v);
        }

        default int getConstrainSwipeStartPosition() {
            return 0;
        }

        default boolean canChildBeDragged(View animView) {
            return true;
        }
    }
}
