package com.android.systemui.pip.phone;

import android.animation.AnimationHandler;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.RectEvaluator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.animation.Interpolator;
import com.android.internal.graphics.SfVsyncFrameCallbackProvider;
import com.android.internal.os.SomeArgs;
import com.android.internal.policy.PipSnapAlgorithm;
import com.android.systemui.Interpolators;
import com.android.systemui.pip.phone.PipAppOpsListener;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.statusbar.FlingAnimationUtils;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class PipMotionHelper implements Handler.Callback, PipAppOpsListener.Callback {
    private static final boolean DEBUG = false;
    private static final int DEFAULT_MOVE_STACK_DURATION = 225;
    private static final float DISMISS_OFFSCREEN_FRACTION = 0.3f;
    private static final int DRAG_TO_DISMISS_STACK_DURATION = 175;
    private static final int DRAG_TO_TARGET_DISMISS_STACK_DURATION = 375;
    private static final int EXPAND_STACK_TO_FULLSCREEN_DURATION = 300;
    private static final int EXPAND_STACK_TO_MENU_DURATION = 250;
    private static final float MINIMIZE_OFFSCREEN_FRACTION = 0.3f;
    private static final int MINIMIZE_STACK_MAX_DURATION = 200;
    private static final int MSG_OFFSET_ANIMATE = 3;
    private static final int MSG_RESIZE_ANIMATE = 2;
    private static final int MSG_RESIZE_IMMEDIATE = 1;
    private static final RectEvaluator RECT_EVALUATOR = new RectEvaluator(new Rect());
    private static final int SHIFT_DURATION = 300;
    private static final int SHRINK_STACK_FROM_MENU_DURATION = 250;
    private static final int SNAP_STACK_DURATION = 225;
    private static final String TAG = "PipMotionHelper";
    private IActivityManager mActivityManager;
    private IActivityTaskManager mActivityTaskManager;
    private Context mContext;
    private FlingAnimationUtils mFlingAnimationUtils;
    private PipMenuActivityController mMenuController;
    private PipSnapAlgorithm mSnapAlgorithm;
    private final Rect mBounds = new Rect();
    private final Rect mStableInsets = new Rect();
    private ValueAnimator mBoundsAnimator = null;
    private Handler mHandler = new Handler(ForegroundThread.get().getLooper(), this);
    private AnimationHandler mAnimationHandler = new AnimationHandler();

    public PipMotionHelper(Context context, IActivityManager activityManager, IActivityTaskManager activityTaskManager, PipMenuActivityController menuController, PipSnapAlgorithm snapAlgorithm, FlingAnimationUtils flingAnimationUtils) {
        this.mContext = context;
        this.mActivityManager = activityManager;
        this.mActivityTaskManager = activityTaskManager;
        this.mMenuController = menuController;
        this.mSnapAlgorithm = snapAlgorithm;
        this.mFlingAnimationUtils = flingAnimationUtils;
        this.mAnimationHandler.setProvider(new SfVsyncFrameCallbackProvider());
        onConfigurationChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onConfigurationChanged() {
        this.mSnapAlgorithm.onConfigurationChanged();
        WindowManagerWrapper.getInstance().getStableInsets(this.mStableInsets);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void synchronizePinnedStackBounds() {
        cancelAnimations();
        try {
            ActivityManager.StackInfo stackInfo = this.mActivityTaskManager.getStackInfo(2, 0);
            if (stackInfo != null) {
                this.mBounds.set(stackInfo.bounds);
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to get pinned stack bounds");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void movePip(Rect toBounds) {
        cancelAnimations();
        resizePipUnchecked(toBounds);
        this.mBounds.set(toBounds);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void expandPip() {
        expandPip(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void expandPip(final boolean skipAnimation) {
        cancelAnimations();
        this.mMenuController.hideMenuWithoutResize();
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMotionHelper$sKxCzHQTJVfrtc--kVVtTIgcND4
            @Override // java.lang.Runnable
            public final void run() {
                PipMotionHelper.this.lambda$expandPip$0$PipMotionHelper(skipAnimation);
            }
        });
    }

    public /* synthetic */ void lambda$expandPip$0$PipMotionHelper(boolean skipAnimation) {
        try {
            this.mActivityTaskManager.dismissPip(!skipAnimation, 300);
        } catch (RemoteException e) {
            Log.e(TAG, "Error expanding PiP activity", e);
        }
    }

    @Override // com.android.systemui.pip.phone.PipAppOpsListener.Callback
    public void dismissPip() {
        cancelAnimations();
        this.mMenuController.hideMenuWithoutResize();
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMotionHelper$ExBmB11pCWcEFXztVKlantVNH0o
            @Override // java.lang.Runnable
            public final void run() {
                PipMotionHelper.this.lambda$dismissPip$1$PipMotionHelper();
            }
        });
    }

    public /* synthetic */ void lambda$dismissPip$1$PipMotionHelper() {
        try {
            this.mActivityTaskManager.removeStacksInWindowingModes(new int[]{2});
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to remove PiP", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rect getBounds() {
        return this.mBounds;
    }

    Rect getClosestMinimizedBounds(Rect stackBounds, Rect movementBounds) {
        Point displaySize = new Point();
        this.mContext.getDisplay().getRealSize(displaySize);
        Rect toBounds = this.mSnapAlgorithm.findClosestSnapBounds(movementBounds, stackBounds);
        this.mSnapAlgorithm.applyMinimizedOffset(toBounds, movementBounds, displaySize, this.mStableInsets);
        return toBounds;
    }

    boolean shouldMinimizePip() {
        Point displaySize = new Point();
        this.mContext.getDisplay().getRealSize(displaySize);
        if (this.mBounds.left < 0) {
            float offscreenFraction = (-this.mBounds.left) / this.mBounds.width();
            return offscreenFraction >= 0.3f;
        } else if (this.mBounds.right > displaySize.x) {
            float offscreenFraction2 = (this.mBounds.right - displaySize.x) / this.mBounds.width();
            return offscreenFraction2 >= 0.3f;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldDismissPip() {
        Point displaySize = new Point();
        this.mContext.getDisplay().getRealSize(displaySize);
        int y = displaySize.y - this.mStableInsets.bottom;
        if (this.mBounds.bottom > y) {
            float offscreenFraction = (this.mBounds.bottom - y) / this.mBounds.height();
            return offscreenFraction >= 0.3f;
        }
        return false;
    }

    Rect flingToMinimizedState(float velocityY, Rect movementBounds, Point dragStartPosition) {
        cancelAnimations();
        Rect toBounds = this.mSnapAlgorithm.findClosestSnapBounds(new Rect(this.mBounds.left, movementBounds.top, this.mBounds.left, movementBounds.bottom), this.mBounds, 0.0f, velocityY, dragStartPosition);
        if (!this.mBounds.equals(toBounds)) {
            this.mBoundsAnimator = createAnimationToBounds(this.mBounds, toBounds, 0, Interpolators.FAST_OUT_SLOW_IN);
            this.mFlingAnimationUtils.apply(this.mBoundsAnimator, 0.0f, distanceBetweenRectOffsets(this.mBounds, toBounds), velocityY);
            this.mBoundsAnimator.start();
        }
        return toBounds;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rect animateToClosestMinimizedState(Rect movementBounds, ValueAnimator.AnimatorUpdateListener updateListener) {
        cancelAnimations();
        Rect toBounds = getClosestMinimizedBounds(this.mBounds, movementBounds);
        if (!this.mBounds.equals(toBounds)) {
            this.mBoundsAnimator = createAnimationToBounds(this.mBounds, toBounds, 200, Interpolators.LINEAR_OUT_SLOW_IN);
            if (updateListener != null) {
                this.mBoundsAnimator.addUpdateListener(updateListener);
            }
            this.mBoundsAnimator.start();
        }
        return toBounds;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rect flingToSnapTarget(float velocity, float velocityX, float velocityY, Rect movementBounds, ValueAnimator.AnimatorUpdateListener updateListener, Animator.AnimatorListener listener, Point startPosition) {
        cancelAnimations();
        Rect toBounds = this.mSnapAlgorithm.findClosestSnapBounds(movementBounds, this.mBounds, velocityX, velocityY, startPosition);
        if (!this.mBounds.equals(toBounds)) {
            this.mBoundsAnimator = createAnimationToBounds(this.mBounds, toBounds, 0, Interpolators.FAST_OUT_SLOW_IN);
            this.mFlingAnimationUtils.apply(this.mBoundsAnimator, 0.0f, distanceBetweenRectOffsets(this.mBounds, toBounds), velocity);
            if (updateListener != null) {
                this.mBoundsAnimator.addUpdateListener(updateListener);
            }
            if (listener != null) {
                this.mBoundsAnimator.addListener(listener);
            }
            this.mBoundsAnimator.start();
        }
        return toBounds;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rect animateToClosestSnapTarget(Rect movementBounds, ValueAnimator.AnimatorUpdateListener updateListener, Animator.AnimatorListener listener) {
        cancelAnimations();
        Rect toBounds = this.mSnapAlgorithm.findClosestSnapBounds(movementBounds, this.mBounds);
        if (!this.mBounds.equals(toBounds)) {
            this.mBoundsAnimator = createAnimationToBounds(this.mBounds, toBounds, 225, Interpolators.FAST_OUT_SLOW_IN);
            if (updateListener != null) {
                this.mBoundsAnimator.addUpdateListener(updateListener);
            }
            if (listener != null) {
                this.mBoundsAnimator.addListener(listener);
            }
            this.mBoundsAnimator.start();
        }
        return toBounds;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float animateToExpandedState(Rect expandedBounds, Rect movementBounds, Rect expandedMovementBounds) {
        float savedSnapFraction = this.mSnapAlgorithm.getSnapFraction(new Rect(this.mBounds), movementBounds);
        this.mSnapAlgorithm.applySnapFraction(expandedBounds, expandedMovementBounds, savedSnapFraction);
        resizeAndAnimatePipUnchecked(expandedBounds, 250);
        return savedSnapFraction;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void animateToUnexpandedState(Rect normalBounds, float savedSnapFraction, Rect normalMovementBounds, Rect currentMovementBounds, boolean minimized, boolean immediate) {
        if (savedSnapFraction < 0.0f) {
            savedSnapFraction = this.mSnapAlgorithm.getSnapFraction(new Rect(this.mBounds), currentMovementBounds);
        }
        this.mSnapAlgorithm.applySnapFraction(normalBounds, normalMovementBounds, savedSnapFraction);
        if (minimized) {
            normalBounds = getClosestMinimizedBounds(normalBounds, normalMovementBounds);
        }
        if (immediate) {
            movePip(normalBounds);
        } else {
            resizeAndAnimatePipUnchecked(normalBounds, 250);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void animateToOffset(Rect originalBounds, int offset) {
        cancelAnimations();
        adjustAndAnimatePipOffset(originalBounds, offset, 300);
    }

    private void adjustAndAnimatePipOffset(Rect originalBounds, int offset, int duration) {
        if (offset == 0) {
            return;
        }
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = originalBounds;
        args.argi1 = offset;
        args.argi2 = duration;
        Handler handler = this.mHandler;
        handler.sendMessage(handler.obtainMessage(3, args));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Rect animateDismiss(Rect pipBounds, float velocityX, float velocityY, ValueAnimator.AnimatorUpdateListener listener) {
        cancelAnimations();
        float velocity = PointF.length(velocityX, velocityY);
        boolean isFling = velocity > this.mFlingAnimationUtils.getMinVelocityPxPerSecond();
        Point p = getDismissEndPoint(pipBounds, velocityX, velocityY, isFling);
        Rect toBounds = new Rect(pipBounds);
        toBounds.offsetTo(p.x, p.y);
        this.mBoundsAnimator = createAnimationToBounds(this.mBounds, toBounds, DRAG_TO_DISMISS_STACK_DURATION, Interpolators.FAST_OUT_LINEAR_IN);
        this.mBoundsAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.pip.phone.PipMotionHelper.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PipMotionHelper.this.dismissPip();
            }
        });
        if (isFling) {
            this.mFlingAnimationUtils.apply(this.mBoundsAnimator, 0.0f, distanceBetweenRectOffsets(this.mBounds, toBounds), velocity);
        }
        if (listener != null) {
            this.mBoundsAnimator.addUpdateListener(listener);
        }
        this.mBoundsAnimator.start();
        return toBounds;
    }

    void cancelAnimations() {
        ValueAnimator valueAnimator = this.mBoundsAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.mBoundsAnimator = null;
        }
    }

    private ValueAnimator createAnimationToBounds(Rect fromBounds, Rect toBounds, int duration, Interpolator interpolator) {
        ValueAnimator anim = new ValueAnimator() { // from class: com.android.systemui.pip.phone.PipMotionHelper.2
            public AnimationHandler getAnimationHandler() {
                return PipMotionHelper.this.mAnimationHandler;
            }
        };
        anim.setObjectValues(fromBounds, toBounds);
        anim.setEvaluator(RECT_EVALUATOR);
        anim.setDuration(duration);
        anim.setInterpolator(interpolator);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMotionHelper$UijvXdqv_A_f2ZSKr4tqG6uf9mk
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PipMotionHelper.this.lambda$createAnimationToBounds$2$PipMotionHelper(valueAnimator);
            }
        });
        return anim;
    }

    public /* synthetic */ void lambda$createAnimationToBounds$2$PipMotionHelper(ValueAnimator animation) {
        resizePipUnchecked((Rect) animation.getAnimatedValue());
    }

    private void resizePipUnchecked(Rect toBounds) {
        if (!toBounds.equals(this.mBounds)) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = toBounds;
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(1, args));
        }
    }

    private void resizeAndAnimatePipUnchecked(Rect toBounds, int duration) {
        if (!toBounds.equals(this.mBounds)) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = toBounds;
            args.argi1 = duration;
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(2, args));
        }
    }

    private Point getDismissEndPoint(Rect pipBounds, float velX, float velY, boolean isFling) {
        Point displaySize = new Point();
        this.mContext.getDisplay().getRealSize(displaySize);
        float bottomBound = displaySize.y + (pipBounds.height() * 0.1f);
        if (isFling && velX != 0.0f && velY != 0.0f) {
            float slope = velY / velX;
            float yIntercept = pipBounds.top - (pipBounds.left * slope);
            float x = (bottomBound - yIntercept) / slope;
            return new Point((int) x, (int) bottomBound);
        }
        return new Point(pipBounds.left, (int) bottomBound);
    }

    public boolean isGestureToDismissArea(Rect pipBounds, float velX, float velY, boolean isFling) {
        Point endpoint = getDismissEndPoint(pipBounds, velX, velY, isFling);
        endpoint.x += pipBounds.width() / 2;
        endpoint.y += pipBounds.height() / 2;
        Point size = new Point();
        this.mContext.getDisplay().getRealSize(size);
        int left = size.x / 3;
        Rect dismissArea = new Rect(left, size.y - (pipBounds.height() / 2), left * 2, size.y + pipBounds.height());
        return dismissArea.contains(endpoint.x, endpoint.y);
    }

    private float distanceBetweenRectOffsets(Rect r1, Rect r2) {
        return PointF.length(r1.left - r2.left, r1.top - r2.top);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message msg) {
        ActivityManager.StackInfo stackInfo;
        ActivityManager.StackInfo stackInfo2;
        int i = msg.what;
        if (i == 1) {
            Rect toBounds = (Rect) ((SomeArgs) msg.obj).arg1;
            try {
                this.mActivityTaskManager.resizePinnedStack(toBounds, (Rect) null);
                this.mBounds.set(toBounds);
            } catch (RemoteException e) {
                Log.e(TAG, "Could not resize pinned stack to bounds: " + toBounds, e);
            }
            return true;
        } else if (i == 2) {
            SomeArgs args = (SomeArgs) msg.obj;
            Rect toBounds2 = (Rect) args.arg1;
            int duration = args.argi1;
            try {
                stackInfo = this.mActivityTaskManager.getStackInfo(2, 0);
            } catch (RemoteException e2) {
                Log.e(TAG, "Could not animate resize pinned stack to bounds: " + toBounds2, e2);
            }
            if (stackInfo == null) {
                return true;
            }
            this.mActivityTaskManager.resizeStack(stackInfo.stackId, toBounds2, false, true, true, duration);
            this.mBounds.set(toBounds2);
            return true;
        } else if (i != 3) {
            return false;
        } else {
            SomeArgs args2 = (SomeArgs) msg.obj;
            Rect originalBounds = (Rect) args2.arg1;
            int offset = args2.argi1;
            int duration2 = args2.argi2;
            try {
                stackInfo2 = this.mActivityTaskManager.getStackInfo(2, 0);
            } catch (RemoteException e3) {
                Log.e(TAG, "Could not animate offset pinned stack with offset: " + offset, e3);
            }
            if (stackInfo2 == null) {
                return true;
            }
            this.mActivityTaskManager.offsetPinnedStackBounds(stackInfo2.stackId, originalBounds, 0, offset, duration2);
            Rect toBounds3 = new Rect(originalBounds);
            toBounds3.offset(0, offset);
            this.mBounds.set(toBounds3);
            return true;
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        String innerPrefix = prefix + "  ";
        pw.println(prefix + TAG);
        pw.println(innerPrefix + "mBounds=" + this.mBounds);
        pw.println(innerPrefix + "mStableInsets=" + this.mStableInsets);
    }
}
