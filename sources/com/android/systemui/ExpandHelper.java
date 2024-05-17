package com.android.systemui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.policy.ScrollAdapter;
/* loaded from: classes21.dex */
public class ExpandHelper implements Gefingerpoken {
    private static final int BLINDS = 1;
    protected static final boolean DEBUG = false;
    protected static final boolean DEBUG_SCALE = false;
    private static final float EXPAND_DURATION = 0.3f;
    private static final int NONE = 0;
    private static final int PULL = 2;
    private static final int STRETCH = 4;
    private static final float STRETCH_INTERVAL = 2.0f;
    private static final String TAG = "ExpandHelper";
    private static final boolean USE_DRAG = true;
    private static final boolean USE_SPAN = true;
    private Callback mCallback;
    private Context mContext;
    private float mCurrentHeight;
    private View mEventSource;
    private boolean mExpanding;
    private FlingAnimationUtils mFlingAnimationUtils;
    private boolean mHasPopped;
    private float mInitialTouchFocusY;
    private float mInitialTouchSpan;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private int mLargeSize;
    private float mLastFocusY;
    private float mLastMotionY;
    private float mLastSpanY;
    private float mMaximumStretch;
    private float mNaturalHeight;
    private float mOldHeight;
    private boolean mOnlyMovements;
    private float mPullGestureMinXSpan;
    private ExpandableView mResizedView;
    private ScaleGestureDetector mSGD;
    private ScrollAdapter mScrollAdapter;
    private int mSmallSize;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private boolean mWatchingForPull;
    private int mExpansionStyle = 0;
    private boolean mEnabled = true;
    private ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() { // from class: com.android.systemui.ExpandHelper.1
        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (!ExpandHelper.this.mOnlyMovements) {
                ExpandHelper expandHelper = ExpandHelper.this;
                expandHelper.startExpanding(expandHelper.mResizedView, 4);
            }
            return ExpandHelper.this.mExpanding;
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector detector) {
            return true;
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    };
    private ViewScaler mScaler = new ViewScaler();
    private int mGravity = 48;
    private ObjectAnimator mScaleAnimation = ObjectAnimator.ofFloat(this.mScaler, "height", 0.0f);

    /* loaded from: classes21.dex */
    public interface Callback {
        boolean canChildBeExpanded(View view);

        void expansionStateChanged(boolean z);

        ExpandableView getChildAtPosition(float f, float f2);

        ExpandableView getChildAtRawPosition(float f, float f2);

        int getMaxExpandHeight(ExpandableView expandableView);

        void setExpansionCancelled(View view);

        void setUserExpandedChild(View view, boolean z);

        void setUserLockedChild(View view, boolean z);
    }

    @VisibleForTesting
    ObjectAnimator getScaleAnimation() {
        return this.mScaleAnimation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class ViewScaler {
        ExpandableView mView;

        public ViewScaler() {
        }

        public void setView(ExpandableView v) {
            this.mView = v;
        }

        public void setHeight(float h) {
            this.mView.setActualHeight((int) h);
            ExpandHelper.this.mCurrentHeight = h;
        }

        public float getHeight() {
            return this.mView.getActualHeight();
        }

        public int getNaturalHeight() {
            return ExpandHelper.this.mCallback.getMaxExpandHeight(this.mView);
        }
    }

    public ExpandHelper(Context context, Callback callback, int small, int large) {
        this.mSmallSize = small;
        this.mMaximumStretch = this.mSmallSize * STRETCH_INTERVAL;
        this.mLargeSize = large;
        this.mContext = context;
        this.mCallback = callback;
        this.mPullGestureMinXSpan = this.mContext.getResources().getDimension(R.dimen.pull_span_min);
        ViewConfiguration configuration = ViewConfiguration.get(this.mContext);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mSGD = new ScaleGestureDetector(context, this.mScaleGestureListener);
        this.mFlingAnimationUtils = new FlingAnimationUtils(context, EXPAND_DURATION);
    }

    @VisibleForTesting
    void updateExpansion() {
        float span = (this.mSGD.getCurrentSpan() - this.mInitialTouchSpan) * 1.0f;
        float drag = (this.mSGD.getFocusY() - this.mInitialTouchFocusY) * 1.0f * (this.mGravity == 80 ? -1.0f : 1.0f);
        float pull = Math.abs(drag) + Math.abs(span) + 1.0f;
        float hand = ((Math.abs(drag) * drag) / pull) + ((Math.abs(span) * span) / pull);
        float target = this.mOldHeight + hand;
        float newHeight = clamp(target);
        this.mScaler.setHeight(newHeight);
        this.mLastFocusY = this.mSGD.getFocusY();
        this.mLastSpanY = this.mSGD.getCurrentSpan();
    }

    private float clamp(float target) {
        int i = this.mSmallSize;
        float out = target < ((float) i) ? i : target;
        float out2 = this.mNaturalHeight;
        if (out <= out2) {
            out2 = out;
        }
        return out2;
    }

    private ExpandableView findView(float x, float y) {
        View view = this.mEventSource;
        if (view != null) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            ExpandableView v = this.mCallback.getChildAtRawPosition(x + location[0], y + location[1]);
            return v;
        }
        ExpandableView v2 = this.mCallback.getChildAtPosition(x, y);
        return v2;
    }

    private boolean isInside(View v, float x, float y) {
        if (v == null) {
            return false;
        }
        View view = this.mEventSource;
        if (view != null) {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            x += location[0];
            y += location[1];
        }
        int[] location2 = new int[2];
        v.getLocationOnScreen(location2);
        float x2 = x - location2[0];
        float y2 = y - location2[1];
        if (x2 <= 0.0f || y2 <= 0.0f) {
            return false;
        }
        return ((x2 > ((float) v.getWidth()) ? 1 : (x2 == ((float) v.getWidth()) ? 0 : -1)) < 0) & ((y2 > ((float) v.getHeight()) ? 1 : (y2 == ((float) v.getHeight()) ? 0 : -1)) < 0);
    }

    public void setEventSource(View eventSource) {
        this.mEventSource = eventSource;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public void setScrollAdapter(ScrollAdapter adapter) {
        this.mScrollAdapter = adapter;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0056, code lost:
        if (r6 != 3) goto L20;
     */
    @Override // com.android.systemui.Gefingerpoken
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r10) {
        /*
            Method dump skipped, instructions count: 287
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.ExpandHelper.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    private void trackVelocity(MotionEvent event) {
        int action = event.getActionMasked();
        if (action != 0) {
            if (action == 2) {
                if (this.mVelocityTracker == null) {
                    this.mVelocityTracker = VelocityTracker.obtain();
                }
                this.mVelocityTracker.addMovement(event);
                return;
            }
            return;
        }
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
        this.mVelocityTracker.addMovement(event);
    }

    private void maybeRecycleVelocityTracker(MotionEvent event) {
        if (this.mVelocityTracker != null) {
            if (event.getActionMasked() == 3 || event.getActionMasked() == 1) {
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        }
    }

    private float getCurrentVelocity() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.computeCurrentVelocity(1000);
            return this.mVelocityTracker.getYVelocity();
        }
        return 0.0f;
    }

    public void setEnabled(boolean enable) {
        this.mEnabled = enable;
    }

    private boolean isEnabled() {
        return this.mEnabled;
    }

    private boolean isFullyExpanded(ExpandableView underFocus) {
        return underFocus.getIntrinsicHeight() == underFocus.getMaxContentHeight() && (!underFocus.isSummaryWithChildren() || underFocus.areChildrenExpanded());
    }

    @Override // com.android.systemui.Gefingerpoken
    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnabled() || this.mExpanding) {
            trackVelocity(ev);
            int action = ev.getActionMasked();
            this.mSGD.onTouchEvent(ev);
            int x = (int) this.mSGD.getFocusX();
            int y = (int) this.mSGD.getFocusY();
            if (this.mOnlyMovements) {
                this.mLastMotionY = ev.getRawY();
                return false;
            }
            if (action != 0) {
                if (action != 1) {
                    if (action == 2) {
                        if (this.mWatchingForPull) {
                            float yDiff = ev.getRawY() - this.mInitialTouchY;
                            float xDiff = ev.getRawX() - this.mInitialTouchX;
                            if (yDiff > this.mTouchSlop && yDiff > Math.abs(xDiff)) {
                                this.mWatchingForPull = false;
                                ExpandableView expandableView = this.mResizedView;
                                if (expandableView != null && !isFullyExpanded(expandableView) && startExpanding(this.mResizedView, 1)) {
                                    this.mInitialTouchY = ev.getRawY();
                                    this.mLastMotionY = ev.getRawY();
                                    this.mHasPopped = false;
                                }
                            }
                        }
                        if (this.mExpanding && (this.mExpansionStyle & 1) != 0) {
                            float rawHeight = (ev.getRawY() - this.mLastMotionY) + this.mCurrentHeight;
                            float newHeight = clamp(rawHeight);
                            boolean isFinished = false;
                            if (rawHeight > this.mNaturalHeight) {
                                isFinished = true;
                            }
                            if (rawHeight < this.mSmallSize) {
                                isFinished = true;
                            }
                            if (!this.mHasPopped) {
                                View view = this.mEventSource;
                                if (view != null) {
                                    view.performHapticFeedback(1);
                                }
                                this.mHasPopped = true;
                            }
                            this.mScaler.setHeight(newHeight);
                            this.mLastMotionY = ev.getRawY();
                            if (!isFinished) {
                                this.mCallback.expansionStateChanged(true);
                            } else {
                                this.mCallback.expansionStateChanged(false);
                            }
                            return true;
                        } else if (this.mExpanding) {
                            updateExpansion();
                            this.mLastMotionY = ev.getRawY();
                            return true;
                        }
                    } else if (action != 3) {
                        if (action == 5 || action == 6) {
                            this.mInitialTouchY += this.mSGD.getFocusY() - this.mLastFocusY;
                            this.mInitialTouchSpan += this.mSGD.getCurrentSpan() - this.mLastSpanY;
                        }
                    }
                }
                finishExpanding(!isEnabled() || ev.getActionMasked() == 3, getCurrentVelocity());
                clearView();
            } else {
                ScrollAdapter scrollAdapter = this.mScrollAdapter;
                this.mWatchingForPull = scrollAdapter != null && isInside(scrollAdapter.getHostView(), (float) x, (float) y);
                this.mResizedView = findView(x, y);
                this.mInitialTouchX = ev.getRawX();
                this.mInitialTouchY = ev.getRawY();
            }
            this.mLastMotionY = ev.getRawY();
            maybeRecycleVelocityTracker(ev);
            return this.mResizedView != null;
        }
        return false;
    }

    @VisibleForTesting
    boolean startExpanding(ExpandableView v, int expandType) {
        if (!(v instanceof ExpandableNotificationRow)) {
            return false;
        }
        this.mExpansionStyle = expandType;
        if (this.mExpanding && v == this.mResizedView) {
            return true;
        }
        this.mExpanding = true;
        this.mCallback.expansionStateChanged(true);
        this.mCallback.setUserLockedChild(v, true);
        this.mScaler.setView(v);
        this.mOldHeight = this.mScaler.getHeight();
        this.mCurrentHeight = this.mOldHeight;
        boolean canBeExpanded = this.mCallback.canChildBeExpanded(v);
        if (canBeExpanded) {
            this.mNaturalHeight = this.mScaler.getNaturalHeight();
            this.mSmallSize = v.getCollapsedHeight();
        } else {
            this.mNaturalHeight = this.mOldHeight;
        }
        return true;
    }

    @VisibleForTesting
    void finishExpanding(boolean forceAbort, float velocity) {
        finishExpanding(forceAbort, velocity, true);
    }

    private void finishExpanding(boolean forceAbort, float velocity, boolean allowAnimation) {
        boolean nowExpanded;
        boolean nowExpanded2;
        if (this.mExpanding) {
            float currentHeight = this.mScaler.getHeight();
            boolean wasClosed = this.mOldHeight == ((float) this.mSmallSize);
            if (!forceAbort) {
                if (wasClosed) {
                    nowExpanded2 = currentHeight > this.mOldHeight && velocity >= 0.0f;
                } else {
                    nowExpanded2 = currentHeight >= this.mOldHeight || velocity > 0.0f;
                }
                nowExpanded = nowExpanded2 | (this.mNaturalHeight == ((float) this.mSmallSize));
            } else {
                nowExpanded = !wasClosed;
            }
            if (this.mScaleAnimation.isRunning()) {
                this.mScaleAnimation.cancel();
            }
            this.mCallback.expansionStateChanged(false);
            int naturalHeight = this.mScaler.getNaturalHeight();
            float targetHeight = nowExpanded ? naturalHeight : this.mSmallSize;
            if (targetHeight != currentHeight && this.mEnabled && allowAnimation) {
                this.mScaleAnimation.setFloatValues(targetHeight);
                this.mScaleAnimation.setupStartValues();
                final View scaledView = this.mResizedView;
                final boolean expand = nowExpanded;
                this.mScaleAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.ExpandHelper.2
                    public boolean mCancelled;

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (!this.mCancelled) {
                            ExpandHelper.this.mCallback.setUserExpandedChild(scaledView, expand);
                            if (!ExpandHelper.this.mExpanding) {
                                ExpandHelper.this.mScaler.setView(null);
                            }
                        } else {
                            ExpandHelper.this.mCallback.setExpansionCancelled(scaledView);
                        }
                        ExpandHelper.this.mCallback.setUserLockedChild(scaledView, false);
                        ExpandHelper.this.mScaleAnimation.removeListener(this);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        this.mCancelled = true;
                    }
                });
                this.mFlingAnimationUtils.apply(this.mScaleAnimation, currentHeight, targetHeight, nowExpanded == (velocity >= 0.0f) ? velocity : 0.0f);
                this.mScaleAnimation.start();
            } else {
                if (targetHeight != currentHeight) {
                    this.mScaler.setHeight(targetHeight);
                }
                this.mCallback.setUserExpandedChild(this.mResizedView, nowExpanded);
                this.mCallback.setUserLockedChild(this.mResizedView, false);
                this.mScaler.setView(null);
            }
            this.mExpanding = false;
            this.mExpansionStyle = 0;
        }
    }

    private void clearView() {
        this.mResizedView = null;
    }

    public void cancelImmediately() {
        cancel(false);
    }

    public void cancel() {
        cancel(true);
    }

    private void cancel(boolean allowAnimation) {
        finishExpanding(true, 0.0f, allowAnimation);
        clearView();
        this.mSGD = new ScaleGestureDetector(this.mContext, this.mScaleGestureListener);
    }

    public void onlyObserveMovements(boolean onlyMovements) {
        this.mOnlyMovements = onlyMovements;
    }
}
