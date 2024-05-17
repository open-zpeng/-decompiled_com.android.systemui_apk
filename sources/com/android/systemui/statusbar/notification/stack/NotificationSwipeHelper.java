package com.android.systemui.statusbar.notification.stack;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.view.MotionEvent;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.SwipeHelper;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class NotificationSwipeHelper extends SwipeHelper implements NotificationSwipeActionHelper {
    @VisibleForTesting
    protected static final long COVER_MENU_DELAY = 4000;
    private static final long SWIPE_MENU_TIMING = 200;
    private static final String TAG = "NotificationSwipeHelper";
    private final NotificationCallback mCallback;
    private NotificationMenuRowPlugin mCurrMenuRow;
    private final Runnable mFalsingCheck;
    private boolean mIsExpanded;
    private View mMenuExposedView;
    private final NotificationMenuRowPlugin.OnMenuEventListener mMenuListener;
    private boolean mPulsing;
    private View mTranslatingParentView;

    /* loaded from: classes21.dex */
    public interface NotificationCallback extends SwipeHelper.Callback {
        void handleChildViewDismissed(View view);

        void onDismiss();

        void onSnooze(StatusBarNotification statusBarNotification, NotificationSwipeActionHelper.SnoozeOption snoozeOption);

        boolean shouldDismissQuickly();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationSwipeHelper(int swipeDirection, NotificationCallback callback, Context context, NotificationMenuRowPlugin.OnMenuEventListener menuListener, FalsingManager falsingManager) {
        super(swipeDirection, callback, context, falsingManager);
        this.mMenuListener = menuListener;
        this.mCallback = callback;
        this.mFalsingCheck = new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.NotificationSwipeHelper.1
            @Override // java.lang.Runnable
            public void run() {
                NotificationSwipeHelper.this.resetExposedMenuView(true, true);
            }
        };
    }

    public View getTranslatingParentView() {
        return this.mTranslatingParentView;
    }

    public void clearTranslatingParentView() {
        setTranslatingParentView(null);
    }

    @VisibleForTesting
    protected void setTranslatingParentView(View view) {
        this.mTranslatingParentView = view;
    }

    public void setExposedMenuView(View view) {
        this.mMenuExposedView = view;
    }

    public void clearExposedMenuView() {
        setExposedMenuView(null);
    }

    public void clearCurrentMenuRow() {
        setCurrentMenuRow(null);
    }

    public View getExposedMenuView() {
        return this.mMenuExposedView;
    }

    public void setCurrentMenuRow(NotificationMenuRowPlugin menuRow) {
        this.mCurrMenuRow = menuRow;
    }

    public NotificationMenuRowPlugin getCurrentMenuRow() {
        return this.mCurrMenuRow;
    }

    @VisibleForTesting
    protected Handler getHandler() {
        return this.mHandler;
    }

    @VisibleForTesting
    protected Runnable getFalsingCheck() {
        return this.mFalsingCheck;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.mIsExpanded = isExpanded;
    }

    @Override // com.android.systemui.SwipeHelper
    protected void onChildSnappedBack(View animView, float targetLeft) {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mCurrMenuRow;
        if (notificationMenuRowPlugin != null && targetLeft == 0.0f) {
            notificationMenuRowPlugin.resetMenu();
            clearCurrentMenuRow();
        }
    }

    @Override // com.android.systemui.SwipeHelper
    public void onDownUpdate(View currView, MotionEvent ev) {
        this.mTranslatingParentView = currView;
        NotificationMenuRowPlugin menuRow = getCurrentMenuRow();
        if (menuRow != null) {
            menuRow.onTouchStart();
        }
        clearCurrentMenuRow();
        getHandler().removeCallbacks(getFalsingCheck());
        resetExposedMenuView(true, false);
        if (currView instanceof ExpandableNotificationRow) {
            initializeRow((ExpandableNotificationRow) currView);
        }
    }

    @VisibleForTesting
    protected void initializeRow(ExpandableNotificationRow row) {
        if (row.getEntry().hasFinishedInitialization()) {
            this.mCurrMenuRow = row.createMenu();
            NotificationMenuRowPlugin notificationMenuRowPlugin = this.mCurrMenuRow;
            if (notificationMenuRowPlugin != null) {
                notificationMenuRowPlugin.setMenuClickListener(this.mMenuListener);
                this.mCurrMenuRow.onTouchStart();
            }
        }
    }

    private boolean swipedEnoughToShowMenu(NotificationMenuRowPlugin menuRow) {
        return !swipedFarEnough() && menuRow.isSwipedEnoughToShowMenu();
    }

    @Override // com.android.systemui.SwipeHelper
    public void onMoveUpdate(View view, MotionEvent ev, float translation, float delta) {
        getHandler().removeCallbacks(getFalsingCheck());
        NotificationMenuRowPlugin menuRow = getCurrentMenuRow();
        if (menuRow != null) {
            menuRow.onTouchMove(delta);
        }
    }

    @Override // com.android.systemui.SwipeHelper
    public boolean handleUpEvent(MotionEvent ev, View animView, float velocity, float translation) {
        NotificationMenuRowPlugin menuRow = getCurrentMenuRow();
        if (menuRow != null) {
            menuRow.onTouchEnd();
            handleMenuRowSwipe(ev, animView, velocity, menuRow);
            return true;
        }
        return false;
    }

    @VisibleForTesting
    protected void handleMenuRowSwipe(MotionEvent ev, View animView, float velocity, NotificationMenuRowPlugin menuRow) {
        if (!menuRow.shouldShowMenu()) {
            if (isDismissGesture(ev)) {
                dismiss(animView, velocity);
                return;
            }
            snapClosed(animView, velocity);
            menuRow.onSnapClosed();
        } else if (menuRow.isSnappedAndOnSameSide()) {
            handleSwipeFromOpenState(ev, animView, velocity, menuRow);
        } else {
            handleSwipeFromClosedState(ev, animView, velocity, menuRow);
        }
    }

    private void handleSwipeFromClosedState(MotionEvent ev, View animView, float velocity, NotificationMenuRowPlugin menuRow) {
        boolean isDismissGesture = isDismissGesture(ev);
        boolean gestureTowardsMenu = menuRow.isTowardsMenu(velocity);
        boolean gestureFastEnough = getEscapeVelocity() <= Math.abs(velocity);
        double timeForGesture = ev.getEventTime() - ev.getDownTime();
        boolean showMenuForSlowOnGoing = !menuRow.canBeDismissed() && timeForGesture >= 200.0d;
        boolean isNonDismissGestureTowardsMenu = gestureTowardsMenu && !isDismissGesture;
        boolean isSlowSwipe = !gestureFastEnough || showMenuForSlowOnGoing;
        boolean slowSwipedFarEnough = swipedEnoughToShowMenu(menuRow) && isSlowSwipe;
        boolean isFastNonDismissGesture = (!gestureFastEnough || gestureTowardsMenu || isDismissGesture) ? false : true;
        boolean isAbleToShowMenu = menuRow.shouldShowGutsOnSnapOpen() || (this.mIsExpanded && !this.mPulsing);
        boolean isMenuRevealingGestureAwayFromMenu = slowSwipedFarEnough || (isFastNonDismissGesture && isAbleToShowMenu);
        int menuSnapTarget = menuRow.getMenuSnapTarget();
        boolean isNonFalseMenuRevealingGesture = !isFalseGesture(ev) && isMenuRevealingGestureAwayFromMenu;
        if ((isNonDismissGestureTowardsMenu || isNonFalseMenuRevealingGesture) && menuSnapTarget != 0) {
            snapOpen(animView, menuSnapTarget, velocity);
            menuRow.onSnapOpen();
        } else if (isDismissGesture(ev) && !gestureTowardsMenu) {
            dismiss(animView, velocity);
            menuRow.onDismiss();
        } else {
            snapClosed(animView, velocity);
            menuRow.onSnapClosed();
        }
    }

    private void handleSwipeFromOpenState(MotionEvent ev, View animView, float velocity, NotificationMenuRowPlugin menuRow) {
        boolean isDismissGesture = isDismissGesture(ev);
        boolean withinSnapMenuThreshold = menuRow.isWithinSnapMenuThreshold();
        if (withinSnapMenuThreshold && !isDismissGesture) {
            menuRow.onSnapOpen();
            snapOpen(animView, menuRow.getMenuSnapTarget(), velocity);
        } else if (isDismissGesture && !menuRow.shouldSnapBack()) {
            dismiss(animView, velocity);
            menuRow.onDismiss();
        } else {
            snapClosed(animView, velocity);
            menuRow.onSnapClosed();
        }
    }

    @Override // com.android.systemui.SwipeHelper
    public void dismissChild(View view, float velocity, boolean useAccelerateInterpolator) {
        superDismissChild(view, velocity, useAccelerateInterpolator);
        if (this.mCallback.shouldDismissQuickly()) {
            this.mCallback.handleChildViewDismissed(view);
        }
        this.mCallback.onDismiss();
        handleMenuCoveredOrDismissed();
    }

    @VisibleForTesting
    protected void superDismissChild(View view, float velocity, boolean useAccelerateInterpolator) {
        super.dismissChild(view, velocity, useAccelerateInterpolator);
    }

    @VisibleForTesting
    protected void superSnapChild(View animView, float targetLeft, float velocity) {
        super.snapChild(animView, targetLeft, velocity);
    }

    @Override // com.android.systemui.SwipeHelper
    public void snapChild(View animView, float targetLeft, float velocity) {
        superSnapChild(animView, targetLeft, velocity);
        this.mCallback.onDragCancelled(animView);
        if (targetLeft == 0.0f) {
            handleMenuCoveredOrDismissed();
        }
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper
    public void snooze(StatusBarNotification sbn, NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
        this.mCallback.onSnooze(sbn, snoozeOption);
    }

    @VisibleForTesting
    protected void handleMenuCoveredOrDismissed() {
        View exposedMenuView = getExposedMenuView();
        if (exposedMenuView != null && exposedMenuView == this.mTranslatingParentView) {
            clearExposedMenuView();
        }
    }

    @VisibleForTesting
    protected Animator superGetViewTranslationAnimator(View v, float target, ValueAnimator.AnimatorUpdateListener listener) {
        return super.getViewTranslationAnimator(v, target, listener);
    }

    @Override // com.android.systemui.SwipeHelper
    public Animator getViewTranslationAnimator(View v, float target, ValueAnimator.AnimatorUpdateListener listener) {
        if (v instanceof ExpandableNotificationRow) {
            return ((ExpandableNotificationRow) v).getTranslateViewAnimator(target, listener);
        }
        return superGetViewTranslationAnimator(v, target, listener);
    }

    @Override // com.android.systemui.SwipeHelper
    public void setTranslation(View v, float translate) {
        if (v instanceof ExpandableNotificationRow) {
            ((ExpandableNotificationRow) v).setTranslation(translate);
        }
    }

    @Override // com.android.systemui.SwipeHelper
    public float getTranslation(View v) {
        if (v instanceof ExpandableNotificationRow) {
            return ((ExpandableNotificationRow) v).getTranslation();
        }
        return 0.0f;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper
    public boolean swipedFastEnough(float translation, float viewSize) {
        return swipedFastEnough();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SwipeHelper
    @VisibleForTesting
    public boolean swipedFastEnough() {
        return super.swipedFastEnough();
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper
    public boolean swipedFarEnough(float translation, float viewSize) {
        return swipedFarEnough();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SwipeHelper
    @VisibleForTesting
    public boolean swipedFarEnough() {
        return super.swipedFarEnough();
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper
    public void dismiss(View animView, float velocity) {
        dismissChild(animView, velocity, !swipedFastEnough());
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper
    public void snapOpen(View animView, int targetLeft, float velocity) {
        snapChild(animView, targetLeft, velocity);
    }

    @VisibleForTesting
    protected void snapClosed(View animView, float velocity) {
        snapChild(animView, 0.0f, velocity);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SwipeHelper
    @VisibleForTesting
    public float getEscapeVelocity() {
        return super.getEscapeVelocity();
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper
    public float getMinDismissVelocity() {
        return getEscapeVelocity();
    }

    public void onMenuShown(View animView) {
        setExposedMenuView(getTranslatingParentView());
        this.mCallback.onDragCancelled(animView);
        Handler handler = getHandler();
        if (this.mCallback.isAntiFalsingNeeded()) {
            handler.removeCallbacks(getFalsingCheck());
            handler.postDelayed(getFalsingCheck(), COVER_MENU_DELAY);
        }
    }

    @VisibleForTesting
    protected boolean shouldResetMenu(boolean force) {
        View view = this.mMenuExposedView;
        if (view != null) {
            if (!force && view == this.mTranslatingParentView) {
                return false;
            }
            return true;
        }
        return false;
    }

    public void resetExposedMenuView(boolean animate, boolean force) {
        if (!shouldResetMenu(force)) {
            return;
        }
        View prevMenuExposedView = getExposedMenuView();
        if (animate) {
            Animator anim = getViewTranslationAnimator(prevMenuExposedView, 0.0f, null);
            if (anim != null) {
                anim.start();
            }
        } else if (prevMenuExposedView instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) prevMenuExposedView;
            if (!row.isRemoved()) {
                row.resetTranslation();
            }
        }
        clearExposedMenuView();
    }

    public static boolean isTouchInView(MotionEvent ev, View view) {
        int height;
        if (view == null) {
            return false;
        }
        if (view instanceof ExpandableView) {
            height = ((ExpandableView) view).getActualHeight();
        } else {
            height = view.getHeight();
        }
        int rx = (int) ev.getRawX();
        int ry = (int) ev.getRawY();
        int[] temp = new int[2];
        view.getLocationOnScreen(temp);
        int x = temp[0];
        int y = temp[1];
        Rect rect = new Rect(x, y, view.getWidth() + x, y + height);
        boolean ret = rect.contains(rx, ry);
        return ret;
    }

    public void setPulsing(boolean pulsing) {
        this.mPulsing = pulsing;
    }
}
