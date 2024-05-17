package com.android.systemui.pip.phone;

import android.animation.ValueAnimator;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.util.Size;
import android.view.IPinnedStackController;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.os.logging.MetricsLoggerWrapper;
import com.android.internal.policy.PipSnapAlgorithm;
import com.android.systemui.R;
import com.android.systemui.pip.phone.PipAccessibilityInteractionConnection;
import com.android.systemui.pip.phone.PipMenuActivityController;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.statusbar.FlingAnimationUtils;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class PipTouchHandler {
    private static final int BOTTOM_OFFSET_BUFFER_DP = 1;
    private static final boolean ENABLE_FLING_DISMISS = false;
    private static final boolean ENABLE_MINIMIZE = false;
    private static final int SHOW_DISMISS_AFFORDANCE_DELAY = 225;
    private static final String TAG = "PipTouchHandler";
    private final AccessibilityManager mAccessibilityManager;
    private final IActivityManager mActivityManager;
    private final IActivityTaskManager mActivityTaskManager;
    private final Context mContext;
    private final PipDismissViewController mDismissViewController;
    private int mDisplayRotation;
    private final boolean mEnableDimissDragToEdge;
    private int mExpandedShortestEdgeSize;
    private final FlingAnimationUtils mFlingAnimationUtils;
    private final PipTouchGesture[] mGestures;
    private int mImeHeight;
    private int mImeOffset;
    private boolean mIsImeShowing;
    private boolean mIsMinimized;
    private boolean mIsShelfShowing;
    private final PipMenuActivityController mMenuController;
    private final PipMotionHelper mMotionHelper;
    private int mMovementBoundsExtraOffsets;
    private boolean mMovementWithinDismiss;
    private boolean mMovementWithinMinimize;
    private IPinnedStackController mPinnedStackController;
    private boolean mSendingHoverAccessibilityEvents;
    private int mShelfHeight;
    private final PipSnapAlgorithm mSnapAlgorithm;
    private final PipTouchState mTouchState;
    private final ViewConfiguration mViewConfig;
    private final PipMenuListener mMenuListener = new PipMenuListener();
    private boolean mShowPipMenuOnAnimationEnd = false;
    private Rect mMovementBounds = new Rect();
    private Rect mInsetBounds = new Rect();
    private Rect mNormalBounds = new Rect();
    private Rect mNormalMovementBounds = new Rect();
    private Rect mExpandedBounds = new Rect();
    private Rect mExpandedMovementBounds = new Rect();
    private int mDeferResizeToNormalBoundsUntilRotation = -1;
    private Handler mHandler = new Handler();
    private Runnable mShowDismissAffordance = new Runnable() { // from class: com.android.systemui.pip.phone.PipTouchHandler.1
        @Override // java.lang.Runnable
        public void run() {
            if (PipTouchHandler.this.mEnableDimissDragToEdge) {
                PipTouchHandler.this.mDismissViewController.showDismissTarget();
            }
        }
    };
    private ValueAnimator.AnimatorUpdateListener mUpdateScrimListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.pip.phone.PipTouchHandler.2
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator animation) {
            PipTouchHandler.this.updateDismissFraction();
        }
    };
    private int mMenuState = 0;
    private float mSavedSnapFraction = -1.0f;
    private final Rect mTmpBounds = new Rect();
    private PipTouchGesture mDefaultMovementGesture = new PipTouchGesture() { // from class: com.android.systemui.pip.phone.PipTouchHandler.3
        private boolean mStartedOnLeft;
        private final Point mStartPosition = new Point();
        private final PointF mDelta = new PointF();

        @Override // com.android.systemui.pip.phone.PipTouchGesture
        public void onDown(PipTouchState touchState) {
            if (touchState.isUserInteracting()) {
                Rect bounds = PipTouchHandler.this.mMotionHelper.getBounds();
                this.mDelta.set(0.0f, 0.0f);
                this.mStartPosition.set(bounds.left, bounds.top);
                this.mStartedOnLeft = bounds.left < PipTouchHandler.this.mMovementBounds.centerX();
                PipTouchHandler.this.mMovementWithinMinimize = true;
                PipTouchHandler.this.mMovementWithinDismiss = touchState.getDownTouchPosition().y >= ((float) PipTouchHandler.this.mMovementBounds.bottom);
                if (PipTouchHandler.this.mMenuState != 0 && !PipTouchHandler.this.mIsMinimized) {
                    PipTouchHandler.this.mMenuController.pokeMenu();
                }
                if (PipTouchHandler.this.mEnableDimissDragToEdge) {
                    PipTouchHandler.this.mDismissViewController.createDismissTarget();
                    PipTouchHandler.this.mHandler.postDelayed(PipTouchHandler.this.mShowDismissAffordance, 225L);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.android.systemui.pip.phone.PipTouchGesture
        public boolean onMove(PipTouchState touchState) {
            if (touchState.isUserInteracting()) {
                if (touchState.startedDragging()) {
                    PipTouchHandler.this.mSavedSnapFraction = -1.0f;
                    if (PipTouchHandler.this.mEnableDimissDragToEdge) {
                        PipTouchHandler.this.mHandler.removeCallbacks(PipTouchHandler.this.mShowDismissAffordance);
                        PipTouchHandler.this.mDismissViewController.showDismissTarget();
                    }
                }
                if (touchState.isDragging()) {
                    PointF lastDelta = touchState.getLastTouchDelta();
                    float lastX = this.mStartPosition.x + this.mDelta.x;
                    float lastY = this.mStartPosition.y + this.mDelta.y;
                    float top = lastDelta.y + lastY;
                    touchState.allowDraggingOffscreen();
                    float left = Math.max(PipTouchHandler.this.mMovementBounds.left, Math.min(PipTouchHandler.this.mMovementBounds.right, lastDelta.x + lastX));
                    float top2 = PipTouchHandler.this.mEnableDimissDragToEdge ? Math.max(PipTouchHandler.this.mMovementBounds.top, top) : Math.max(PipTouchHandler.this.mMovementBounds.top, Math.min(PipTouchHandler.this.mMovementBounds.bottom, top));
                    this.mDelta.x += left - lastX;
                    this.mDelta.y += top2 - lastY;
                    PipTouchHandler.this.mTmpBounds.set(PipTouchHandler.this.mMotionHelper.getBounds());
                    PipTouchHandler.this.mTmpBounds.offsetTo((int) left, (int) top2);
                    PipTouchHandler.this.mMotionHelper.movePip(PipTouchHandler.this.mTmpBounds);
                    if (PipTouchHandler.this.mEnableDimissDragToEdge) {
                        PipTouchHandler.this.updateDismissFraction();
                    }
                    PointF curPos = touchState.getLastTouchPosition();
                    if (PipTouchHandler.this.mMovementWithinMinimize) {
                        PipTouchHandler.this.mMovementWithinMinimize = this.mStartedOnLeft ? curPos.x <= ((float) (PipTouchHandler.this.mMovementBounds.left + PipTouchHandler.this.mTmpBounds.width())) : curPos.x >= ((float) PipTouchHandler.this.mMovementBounds.right);
                    }
                    if (PipTouchHandler.this.mMovementWithinDismiss) {
                        PipTouchHandler.this.mMovementWithinDismiss = curPos.y >= ((float) PipTouchHandler.this.mMovementBounds.bottom);
                    }
                    return true;
                }
                return false;
            }
            return false;
        }

        /* JADX WARN: Code restructure failed: missing block: B:46:0x00d3, code lost:
            r7 = true;
         */
        /* JADX WARN: Removed duplicated region for block: B:50:0x00e0  */
        /* JADX WARN: Removed duplicated region for block: B:53:0x00ee  */
        /* JADX WARN: Removed duplicated region for block: B:54:0x0115  */
        /* JADX WARN: Removed duplicated region for block: B:56:0x011d  */
        /* JADX WARN: Removed duplicated region for block: B:57:0x013b  */
        @Override // com.android.systemui.pip.phone.PipTouchGesture
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onUp(com.android.systemui.pip.phone.PipTouchState r19) {
            /*
                Method dump skipped, instructions count: 455
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.pip.phone.PipTouchHandler.AnonymousClass3.onUp(com.android.systemui.pip.phone.PipTouchState):boolean");
        }
    };

    /* loaded from: classes21.dex */
    private class PipMenuListener implements PipMenuActivityController.Listener {
        private PipMenuListener() {
        }

        @Override // com.android.systemui.pip.phone.PipMenuActivityController.Listener
        public void onPipMenuStateChanged(int menuState, boolean resize) {
            PipTouchHandler.this.setMenuState(menuState, resize);
        }

        @Override // com.android.systemui.pip.phone.PipMenuActivityController.Listener
        public void onPipExpand() {
            if (!PipTouchHandler.this.mIsMinimized) {
                PipTouchHandler.this.mMotionHelper.expandPip();
            }
        }

        @Override // com.android.systemui.pip.phone.PipMenuActivityController.Listener
        public void onPipMinimize() {
            PipTouchHandler.this.setMinimizedStateInternal(true);
            PipTouchHandler.this.mMotionHelper.animateToClosestMinimizedState(PipTouchHandler.this.mMovementBounds, null);
        }

        @Override // com.android.systemui.pip.phone.PipMenuActivityController.Listener
        public void onPipDismiss() {
            MetricsLoggerWrapper.logPictureInPictureDismissByTap(PipTouchHandler.this.mContext, PipUtils.getTopPinnedActivity(PipTouchHandler.this.mContext, PipTouchHandler.this.mActivityManager));
            PipTouchHandler.this.mMotionHelper.dismissPip();
        }

        @Override // com.android.systemui.pip.phone.PipMenuActivityController.Listener
        public void onPipShowMenu() {
            PipTouchHandler.this.mMenuController.showMenu(2, PipTouchHandler.this.mMotionHelper.getBounds(), PipTouchHandler.this.mMovementBounds, true, PipTouchHandler.this.willResizeMenu());
        }
    }

    public PipTouchHandler(Context context, IActivityManager activityManager, IActivityTaskManager activityTaskManager, PipMenuActivityController menuController, InputConsumerController inputConsumerController) {
        this.mContext = context;
        this.mActivityManager = activityManager;
        this.mActivityTaskManager = activityTaskManager;
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        this.mViewConfig = ViewConfiguration.get(context);
        this.mMenuController = menuController;
        this.mMenuController.addListener(this.mMenuListener);
        this.mDismissViewController = new PipDismissViewController(context);
        this.mSnapAlgorithm = new PipSnapAlgorithm(this.mContext);
        this.mFlingAnimationUtils = new FlingAnimationUtils(context, 2.5f);
        this.mGestures = new PipTouchGesture[]{this.mDefaultMovementGesture};
        this.mMotionHelper = new PipMotionHelper(this.mContext, this.mActivityManager, this.mActivityTaskManager, this.mMenuController, this.mSnapAlgorithm, this.mFlingAnimationUtils);
        this.mTouchState = new PipTouchState(this.mViewConfig, this.mHandler, new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipTouchHandler$Uq5M9Md512Sfgd22VAeFpot25E0
            @Override // java.lang.Runnable
            public final void run() {
                PipTouchHandler.this.lambda$new$0$PipTouchHandler();
            }
        });
        Resources res = context.getResources();
        this.mExpandedShortestEdgeSize = res.getDimensionPixelSize(R.dimen.pip_expanded_shortest_edge_size);
        this.mImeOffset = res.getDimensionPixelSize(R.dimen.pip_ime_offset);
        this.mEnableDimissDragToEdge = res.getBoolean(R.bool.config_pipEnableDismissDragToEdge);
        inputConsumerController.setInputListener(new InputConsumerController.InputListener() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipTouchHandler$A78OVgVs8H_2SG6WUxzMSclOdX0
            @Override // com.android.systemui.shared.system.InputConsumerController.InputListener
            public final boolean onInputEvent(InputEvent inputEvent) {
                boolean handleTouchEvent;
                handleTouchEvent = PipTouchHandler.this.handleTouchEvent(inputEvent);
                return handleTouchEvent;
            }
        });
        inputConsumerController.setRegistrationListener(new InputConsumerController.RegistrationListener() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipTouchHandler$NVpciZTELe-GnxXPZeY5rYMmqJQ
            @Override // com.android.systemui.shared.system.InputConsumerController.RegistrationListener
            public final void onRegistrationChanged(boolean z) {
                PipTouchHandler.this.onRegistrationChanged(z);
            }
        });
        onRegistrationChanged(inputConsumerController.isRegistered());
    }

    public /* synthetic */ void lambda$new$0$PipTouchHandler() {
        this.mMenuController.showMenu(2, this.mMotionHelper.getBounds(), this.mMovementBounds, true, willResizeMenu());
    }

    public void setTouchEnabled(boolean enabled) {
        this.mTouchState.setAllowTouches(enabled);
    }

    public void showPictureInPictureMenu() {
        if (!this.mTouchState.isUserInteracting()) {
            this.mMenuController.showMenu(2, this.mMotionHelper.getBounds(), this.mMovementBounds, false, willResizeMenu());
        }
    }

    public void onActivityPinned() {
        cleanUp();
        this.mShowPipMenuOnAnimationEnd = true;
    }

    public void onActivityUnpinned(ComponentName topPipActivity) {
        if (topPipActivity == null) {
            cleanUp();
        }
    }

    public void onPinnedStackAnimationEnded() {
        this.mMotionHelper.synchronizePinnedStackBounds();
        if (this.mShowPipMenuOnAnimationEnd) {
            this.mMenuController.showMenu(1, this.mMotionHelper.getBounds(), this.mMovementBounds, true, false);
            this.mShowPipMenuOnAnimationEnd = false;
        }
    }

    public void onConfigurationChanged() {
        this.mMotionHelper.onConfigurationChanged();
        this.mMotionHelper.synchronizePinnedStackBounds();
    }

    public void onImeVisibilityChanged(boolean imeVisible, int imeHeight) {
        this.mIsImeShowing = imeVisible;
        this.mImeHeight = imeHeight;
    }

    public void onShelfVisibilityChanged(boolean shelfVisible, int shelfHeight) {
        this.mIsShelfShowing = shelfVisible;
        this.mShelfHeight = shelfHeight;
    }

    public void onMovementBoundsChanged(Rect insetBounds, Rect normalBounds, Rect curBounds, boolean fromImeAdjustment, boolean fromShelfAdjustment, int displayRotation) {
        Rect toMovementBounds;
        int toBottom;
        int i = 0;
        int bottomOffset = this.mIsImeShowing ? this.mImeHeight : 0;
        this.mNormalBounds = normalBounds;
        Rect normalMovementBounds = new Rect();
        this.mSnapAlgorithm.getMovementBounds(this.mNormalBounds, insetBounds, normalMovementBounds, bottomOffset);
        float aspectRatio = normalBounds.width() / normalBounds.height();
        Point displaySize = new Point();
        this.mContext.getDisplay().getRealSize(displaySize);
        Size expandedSize = this.mSnapAlgorithm.getSizeForAspectRatio(aspectRatio, this.mExpandedShortestEdgeSize, displaySize.x, displaySize.y);
        this.mExpandedBounds.set(0, 0, expandedSize.getWidth(), expandedSize.getHeight());
        Rect expandedMovementBounds = new Rect();
        this.mSnapAlgorithm.getMovementBounds(this.mExpandedBounds, insetBounds, expandedMovementBounds, bottomOffset);
        int i2 = this.mIsImeShowing ? this.mImeOffset : 0;
        if (!this.mIsImeShowing && this.mIsShelfShowing) {
            i = this.mShelfHeight;
        }
        int extraOffset = Math.max(i2, i);
        if ((fromImeAdjustment || fromShelfAdjustment) && !this.mTouchState.isUserInteracting()) {
            float offsetBufferPx = this.mContext.getResources().getDisplayMetrics().density * 1.0f;
            if (this.mMenuState == 2) {
                toMovementBounds = new Rect(expandedMovementBounds);
            } else {
                toMovementBounds = new Rect(normalMovementBounds);
            }
            int prevBottom = this.mMovementBounds.bottom - this.mMovementBoundsExtraOffsets;
            if (toMovementBounds.bottom < toMovementBounds.top) {
                toBottom = toMovementBounds.bottom;
            } else {
                toBottom = toMovementBounds.bottom - extraOffset;
            }
            if (Math.min(prevBottom, toBottom) - offsetBufferPx <= curBounds.top && curBounds.top <= Math.max(prevBottom, toBottom) + offsetBufferPx) {
                this.mMotionHelper.animateToOffset(curBounds, toBottom - curBounds.top);
            }
        }
        this.mNormalMovementBounds = normalMovementBounds;
        this.mExpandedMovementBounds = expandedMovementBounds;
        this.mDisplayRotation = displayRotation;
        this.mInsetBounds.set(insetBounds);
        updateMovementBounds(this.mMenuState);
        this.mMovementBoundsExtraOffsets = extraOffset;
        if (this.mDeferResizeToNormalBoundsUntilRotation == displayRotation) {
            this.mMotionHelper.animateToUnexpandedState(normalBounds, this.mSavedSnapFraction, this.mNormalMovementBounds, this.mMovementBounds, this.mIsMinimized, true);
            this.mSavedSnapFraction = -1.0f;
            this.mDeferResizeToNormalBoundsUntilRotation = -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRegistrationChanged(boolean isRegistered) {
        PipAccessibilityInteractionConnection pipAccessibilityInteractionConnection;
        AccessibilityManager accessibilityManager = this.mAccessibilityManager;
        if (isRegistered) {
            pipAccessibilityInteractionConnection = new PipAccessibilityInteractionConnection(this.mMotionHelper, new PipAccessibilityInteractionConnection.AccessibilityCallbacks() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipTouchHandler$1nY3kLe318Fm3UtIAbDmSK80h7w
                @Override // com.android.systemui.pip.phone.PipAccessibilityInteractionConnection.AccessibilityCallbacks
                public final void onAccessibilityShowMenu() {
                    PipTouchHandler.this.onAccessibilityShowMenu();
                }
            }, this.mHandler);
        } else {
            pipAccessibilityInteractionConnection = null;
        }
        accessibilityManager.setPictureInPictureActionReplacingConnection(pipAccessibilityInteractionConnection);
        if (!isRegistered && this.mTouchState.isUserInteracting()) {
            cleanUpDismissTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAccessibilityShowMenu() {
        this.mMenuController.showMenu(2, this.mMotionHelper.getBounds(), this.mMovementBounds, true, willResizeMenu());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleTouchEvent(InputEvent inputEvent) {
        PipTouchGesture[] pipTouchGestureArr;
        if ((inputEvent instanceof MotionEvent) && this.mPinnedStackController != null) {
            MotionEvent ev = (MotionEvent) inputEvent;
            this.mTouchState.onTouchEvent(ev);
            int i = 0;
            boolean shouldDeliverToMenu = this.mMenuState != 0;
            int action = ev.getAction();
            if (action == 0) {
                this.mMotionHelper.synchronizePinnedStackBounds();
                PipTouchGesture[] pipTouchGestureArr2 = this.mGestures;
                int length = pipTouchGestureArr2.length;
                while (i < length) {
                    PipTouchGesture gesture = pipTouchGestureArr2[i];
                    gesture.onDown(this.mTouchState);
                    i++;
                }
            } else {
                if (action == 1) {
                    updateMovementBounds(this.mMenuState);
                    for (PipTouchGesture gesture2 : this.mGestures) {
                        if (gesture2.onUp(this.mTouchState)) {
                            break;
                        }
                    }
                } else if (action == 2) {
                    PipTouchGesture[] pipTouchGestureArr3 = this.mGestures;
                    int length2 = pipTouchGestureArr3.length;
                    while (i < length2) {
                        PipTouchGesture gesture3 = pipTouchGestureArr3[i];
                        if (gesture3.onMove(this.mTouchState)) {
                            break;
                        }
                        i++;
                    }
                    shouldDeliverToMenu = !this.mTouchState.isDragging();
                } else if (action != 3) {
                    if (action == 7 || action == 9) {
                        if (this.mAccessibilityManager.isEnabled() && !this.mSendingHoverAccessibilityEvents) {
                            AccessibilityEvent event = AccessibilityEvent.obtain(128);
                            event.setImportantForAccessibility(true);
                            event.setSourceNodeId(AccessibilityNodeInfo.ROOT_NODE_ID);
                            event.setWindowId(-3);
                            this.mAccessibilityManager.sendAccessibilityEvent(event);
                            this.mSendingHoverAccessibilityEvents = true;
                        }
                    } else if (action == 10 && this.mAccessibilityManager.isEnabled() && this.mSendingHoverAccessibilityEvents) {
                        AccessibilityEvent event2 = AccessibilityEvent.obtain(256);
                        event2.setImportantForAccessibility(true);
                        event2.setSourceNodeId(AccessibilityNodeInfo.ROOT_NODE_ID);
                        event2.setWindowId(-3);
                        this.mAccessibilityManager.sendAccessibilityEvent(event2);
                        this.mSendingHoverAccessibilityEvents = false;
                    }
                }
                if (!this.mTouchState.startedDragging() && !this.mTouchState.isDragging()) {
                    i = 1;
                }
                shouldDeliverToMenu = i;
                this.mTouchState.reset();
            }
            if (shouldDeliverToMenu) {
                MotionEvent cloneEvent = MotionEvent.obtain(ev);
                if (this.mTouchState.startedDragging()) {
                    cloneEvent.setAction(3);
                    this.mMenuController.pokeMenu();
                }
                this.mMenuController.handleTouchEvent(cloneEvent);
            }
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDismissFraction() {
        if (this.mMenuController != null && !this.mIsImeShowing) {
            Rect bounds = this.mMotionHelper.getBounds();
            float target = this.mInsetBounds.bottom;
            float fraction = 0.0f;
            if (bounds.bottom > target) {
                float distance = bounds.bottom - target;
                fraction = Math.min(distance / bounds.height(), 1.0f);
            }
            if (Float.compare(fraction, 0.0f) != 0 || this.mMenuController.isMenuActivityVisible()) {
                this.mMenuController.setDismissFraction(fraction);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPinnedStackController(IPinnedStackController controller) {
        this.mPinnedStackController = controller;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMinimizedStateInternal(boolean isMinimized) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMinimizedState(boolean isMinimized, boolean fromController) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMenuState(int menuState, boolean resize) {
        boolean z = false;
        if (menuState == 2 && this.mMenuState != 2) {
            Rect expandedBounds = new Rect(this.mExpandedBounds);
            if (resize) {
                this.mSavedSnapFraction = this.mMotionHelper.animateToExpandedState(expandedBounds, this.mMovementBounds, this.mExpandedMovementBounds);
            }
        } else if (menuState == 0 && this.mMenuState == 2) {
            if (resize) {
                if (this.mDeferResizeToNormalBoundsUntilRotation == -1) {
                    try {
                        int displayRotation = this.mPinnedStackController.getDisplayRotation();
                        if (this.mDisplayRotation != displayRotation) {
                            this.mDeferResizeToNormalBoundsUntilRotation = displayRotation;
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "Could not get display rotation from controller");
                    }
                }
                if (this.mDeferResizeToNormalBoundsUntilRotation == -1) {
                    Rect normalBounds = new Rect(this.mNormalBounds);
                    this.mMotionHelper.animateToUnexpandedState(normalBounds, this.mSavedSnapFraction, this.mNormalMovementBounds, this.mMovementBounds, this.mIsMinimized, false);
                    this.mSavedSnapFraction = -1.0f;
                }
            } else {
                setTouchEnabled(false);
                this.mSavedSnapFraction = -1.0f;
            }
        }
        this.mMenuState = menuState;
        updateMovementBounds(menuState);
        if (menuState != 1) {
            Context context = this.mContext;
            if (menuState == 2) {
                z = true;
            }
            MetricsLoggerWrapper.logPictureInPictureMenuVisible(context, z);
        }
    }

    public PipMotionHelper getMotionHelper() {
        return this.mMotionHelper;
    }

    private void updateMovementBounds(int menuState) {
        Rect rect;
        boolean isMenuExpanded = menuState == 2;
        if (isMenuExpanded) {
            rect = this.mExpandedMovementBounds;
        } else {
            rect = this.mNormalMovementBounds;
        }
        this.mMovementBounds = rect;
        try {
            if (this.mPinnedStackController != null) {
                this.mPinnedStackController.setMinEdgeSize(isMenuExpanded ? this.mExpandedShortestEdgeSize : 0);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Could not set minimized state", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanUpDismissTarget() {
        this.mHandler.removeCallbacks(this.mShowDismissAffordance);
        this.mDismissViewController.destroyDismissTarget();
    }

    private void cleanUp() {
        if (this.mIsMinimized) {
            setMinimizedStateInternal(false);
        }
        cleanUpDismissTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean willResizeMenu() {
        return (this.mExpandedBounds.width() == this.mNormalBounds.width() && this.mExpandedBounds.height() == this.mNormalBounds.height()) ? false : true;
    }

    public void dump(PrintWriter pw, String prefix) {
        String innerPrefix = prefix + "  ";
        pw.println(prefix + TAG);
        pw.println(innerPrefix + "mMovementBounds=" + this.mMovementBounds);
        pw.println(innerPrefix + "mNormalBounds=" + this.mNormalBounds);
        pw.println(innerPrefix + "mNormalMovementBounds=" + this.mNormalMovementBounds);
        pw.println(innerPrefix + "mExpandedBounds=" + this.mExpandedBounds);
        pw.println(innerPrefix + "mExpandedMovementBounds=" + this.mExpandedMovementBounds);
        pw.println(innerPrefix + "mMenuState=" + this.mMenuState);
        pw.println(innerPrefix + "mIsMinimized=" + this.mIsMinimized);
        pw.println(innerPrefix + "mIsImeShowing=" + this.mIsImeShowing);
        pw.println(innerPrefix + "mImeHeight=" + this.mImeHeight);
        pw.println(innerPrefix + "mIsShelfShowing=" + this.mIsShelfShowing);
        pw.println(innerPrefix + "mShelfHeight=" + this.mShelfHeight);
        pw.println(innerPrefix + "mSavedSnapFraction=" + this.mSavedSnapFraction);
        pw.println(innerPrefix + "mEnableDragToEdgeDismiss=" + this.mEnableDimissDragToEdge);
        pw.println(innerPrefix + "mEnableMinimize=false");
        this.mSnapAlgorithm.dump(pw, innerPrefix);
        this.mTouchState.dump(pw, innerPrefix);
        this.mMotionHelper.dump(pw, innerPrefix);
    }
}
