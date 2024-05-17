package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.systemui.ExpandHelper;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.notification.row.ExpandableView;
/* loaded from: classes21.dex */
public class DragDownHelper implements Gefingerpoken {
    private static final float RUBBERBAND_FACTOR_EXPANDABLE = 0.5f;
    private static final float RUBBERBAND_FACTOR_STATIC = 0.15f;
    private static final int SPRING_BACK_ANIMATION_LENGTH_MS = 375;
    private ExpandHelper.Callback mCallback;
    private DragDownCallback mDragDownCallback;
    private boolean mDraggedFarEnough;
    private boolean mDraggingDown;
    private FalsingManager mFalsingManager;
    private View mHost;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private float mLastHeight;
    private int mMinDragDistance;
    private ExpandableView mStartingChild;
    private final int[] mTemp2 = new int[2];
    private float mTouchSlop;

    /* loaded from: classes21.dex */
    public interface DragDownCallback {
        boolean isDragDownAnywhereEnabled();

        boolean isDragDownEnabledForView(ExpandableView expandableView);

        boolean isFalsingCheckNeeded();

        void onCrossedThreshold(boolean z);

        void onDragDownReset();

        boolean onDraggedDown(View view, int i);

        void onTouchSlopExceeded();

        void setEmptyDragAmount(float f);
    }

    public DragDownHelper(Context context, View host, ExpandHelper.Callback callback, DragDownCallback dragDownCallback, FalsingManager falsingManager) {
        this.mMinDragDistance = context.getResources().getDimensionPixelSize(R.dimen.keyguard_drag_down_min_distance);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mCallback = callback;
        this.mDragDownCallback = dragDownCallback;
        this.mHost = host;
        this.mFalsingManager = falsingManager;
    }

    @Override // com.android.systemui.Gefingerpoken
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            this.mDraggedFarEnough = false;
            this.mDraggingDown = false;
            this.mStartingChild = null;
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
        } else if (actionMasked == 2) {
            float h = y - this.mInitialTouchY;
            if (h > this.mTouchSlop && h > Math.abs(x - this.mInitialTouchX)) {
                this.mFalsingManager.onNotificatonStartDraggingDown();
                this.mDraggingDown = true;
                captureStartingChild(this.mInitialTouchX, this.mInitialTouchY);
                this.mInitialTouchY = y;
                this.mInitialTouchX = x;
                this.mDragDownCallback.onTouchSlopExceeded();
                return this.mStartingChild != null || this.mDragDownCallback.isDragDownAnywhereEnabled();
            }
        }
        return false;
    }

    @Override // com.android.systemui.Gefingerpoken
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mDraggingDown) {
            event.getX();
            float y = event.getY();
            int actionMasked = event.getActionMasked();
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    float f = this.mInitialTouchY;
                    this.mLastHeight = y - f;
                    captureStartingChild(this.mInitialTouchX, f);
                    ExpandableView expandableView = this.mStartingChild;
                    if (expandableView != null) {
                        handleExpansion(this.mLastHeight, expandableView);
                    } else {
                        this.mDragDownCallback.setEmptyDragAmount(this.mLastHeight);
                    }
                    if (this.mLastHeight > this.mMinDragDistance) {
                        if (!this.mDraggedFarEnough) {
                            this.mDraggedFarEnough = true;
                            this.mDragDownCallback.onCrossedThreshold(true);
                        }
                    } else if (this.mDraggedFarEnough) {
                        this.mDraggedFarEnough = false;
                        this.mDragDownCallback.onCrossedThreshold(false);
                    }
                    return true;
                } else if (actionMasked == 3) {
                    stopDragging();
                    return false;
                }
            } else if (!this.mFalsingManager.isUnlockingDisabled() && !isFalseTouch() && this.mDragDownCallback.onDraggedDown(this.mStartingChild, (int) (y - this.mInitialTouchY))) {
                ExpandableView expandableView2 = this.mStartingChild;
                if (expandableView2 == null) {
                    cancelExpansion();
                } else {
                    this.mCallback.setUserLockedChild(expandableView2, false);
                    this.mStartingChild = null;
                }
                this.mDraggingDown = false;
            } else {
                stopDragging();
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean isFalseTouch() {
        if (this.mDragDownCallback.isFalsingCheckNeeded()) {
            return this.mFalsingManager.isFalseTouch() || !this.mDraggedFarEnough;
        }
        return false;
    }

    private void captureStartingChild(float x, float y) {
        if (this.mStartingChild == null) {
            this.mStartingChild = findView(x, y);
            ExpandableView expandableView = this.mStartingChild;
            if (expandableView != null) {
                if (this.mDragDownCallback.isDragDownEnabledForView(expandableView)) {
                    this.mCallback.setUserLockedChild(this.mStartingChild, true);
                } else {
                    this.mStartingChild = null;
                }
            }
        }
    }

    private void handleExpansion(float heightDelta, ExpandableView child) {
        float rubberbandFactor;
        if (heightDelta < 0.0f) {
            heightDelta = 0.0f;
        }
        boolean expandable = child.isContentExpandable();
        if (expandable) {
            rubberbandFactor = 0.5f;
        } else {
            rubberbandFactor = RUBBERBAND_FACTOR_STATIC;
        }
        float rubberband = heightDelta * rubberbandFactor;
        if (expandable && child.getCollapsedHeight() + rubberband > child.getMaxContentHeight()) {
            float overshoot = (child.getCollapsedHeight() + rubberband) - child.getMaxContentHeight();
            rubberband -= overshoot * 0.85f;
        }
        child.setActualHeight((int) (child.getCollapsedHeight() + rubberband));
    }

    private void cancelExpansion(final ExpandableView child) {
        if (child.getActualHeight() == child.getCollapsedHeight()) {
            this.mCallback.setUserLockedChild(child, false);
            return;
        }
        ObjectAnimator anim = ObjectAnimator.ofInt(child, "actualHeight", child.getActualHeight(), child.getCollapsedHeight());
        anim.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        anim.setDuration(375L);
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.DragDownHelper.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                DragDownHelper.this.mCallback.setUserLockedChild(child, false);
            }
        });
        anim.start();
    }

    private void cancelExpansion() {
        ValueAnimator anim = ValueAnimator.ofFloat(this.mLastHeight, 0.0f);
        anim.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        anim.setDuration(375L);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.-$$Lambda$DragDownHelper$q6x0oNk24uuvhTw3d_iOE5k6pV4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                DragDownHelper.this.lambda$cancelExpansion$0$DragDownHelper(valueAnimator);
            }
        });
        anim.start();
    }

    public /* synthetic */ void lambda$cancelExpansion$0$DragDownHelper(ValueAnimator animation) {
        this.mDragDownCallback.setEmptyDragAmount(((Float) animation.getAnimatedValue()).floatValue());
    }

    private void stopDragging() {
        this.mFalsingManager.onNotificatonStopDraggingDown();
        ExpandableView expandableView = this.mStartingChild;
        if (expandableView != null) {
            cancelExpansion(expandableView);
            this.mStartingChild = null;
        } else {
            cancelExpansion();
        }
        this.mDraggingDown = false;
        this.mDragDownCallback.onDragDownReset();
    }

    private ExpandableView findView(float x, float y) {
        this.mHost.getLocationOnScreen(this.mTemp2);
        int[] iArr = this.mTemp2;
        float y2 = y + iArr[1];
        return this.mCallback.getChildAtRawPosition(x + iArr[0], y2);
    }

    public boolean isDraggingDown() {
        return this.mDraggingDown;
    }

    public boolean isDragDownEnabled() {
        return this.mDragDownCallback.isDragDownEnabledForView(null);
    }
}
