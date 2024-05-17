package com.android.systemui.statusbar.phone;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.ViewClippingUtil;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.HeadsUpStatusBarView;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class HeadsUpAppearanceController implements OnHeadsUpChangedListener, DarkIconDispatcher.DarkReceiver, NotificationWakeUpCoordinator.WakeUpListener {
    public static final int CONTENT_FADE_DELAY = 100;
    public static final int CONTENT_FADE_DURATION = 110;
    private boolean mAnimationsEnabled;
    @VisibleForTesting
    float mAppearFraction;
    private final KeyguardBypassController mBypassController;
    private final View mCenteredIconView;
    private final View mClockView;
    private final CommandQueue mCommandQueue;
    private final DarkIconDispatcher mDarkIconDispatcher;
    @VisibleForTesting
    float mExpandedHeight;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private final HeadsUpStatusBarView mHeadsUpStatusBarView;
    @VisibleForTesting
    boolean mIsExpanded;
    private KeyguardMonitor mKeyguardMonitor;
    private final NotificationIconAreaController mNotificationIconAreaController;
    private final View mOperatorNameView;
    private final NotificationPanelView mPanelView;
    private final ViewClippingUtil.ClippingParameters mParentClippingParams;
    Point mPoint;
    private final BiConsumer<Float, Float> mSetExpandedHeight;
    private final Consumer<ExpandableNotificationRow> mSetTrackingHeadsUp;
    private boolean mShown;
    private final View.OnLayoutChangeListener mStackScrollLayoutChangeListener;
    private final NotificationStackScrollLayout mStackScroller;
    private final StatusBarStateController mStatusBarStateController;
    private ExpandableNotificationRow mTrackedChild;
    private final Runnable mUpdatePanelTranslation;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;

    public /* synthetic */ void lambda$new$0$HeadsUpAppearanceController(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        updatePanelTranslation();
    }

    public HeadsUpAppearanceController(NotificationIconAreaController notificationIconAreaController, HeadsUpManagerPhone headsUpManager, View statusbarView, SysuiStatusBarStateController statusBarStateController, KeyguardBypassController keyguardBypassController, NotificationWakeUpCoordinator wakeUpCoordinator) {
        this(notificationIconAreaController, headsUpManager, statusBarStateController, keyguardBypassController, wakeUpCoordinator, (HeadsUpStatusBarView) statusbarView.findViewById(R.id.heads_up_status_bar_view), (NotificationStackScrollLayout) statusbarView.findViewById(R.id.notification_stack_scroller), (NotificationPanelView) statusbarView.findViewById(R.id.notification_panel), statusbarView.findViewById(R.id.clock), statusbarView.findViewById(R.id.operator_name_frame), statusbarView.findViewById(R.id.centered_icon_area));
    }

    @VisibleForTesting
    public HeadsUpAppearanceController(NotificationIconAreaController notificationIconAreaController, HeadsUpManagerPhone headsUpManager, StatusBarStateController stateController, KeyguardBypassController bypassController, NotificationWakeUpCoordinator wakeUpCoordinator, HeadsUpStatusBarView headsUpStatusBarView, NotificationStackScrollLayout stackScroller, NotificationPanelView panelView, View clockView, View operatorNameView, View centeredIconView) {
        this.mSetTrackingHeadsUp = new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$u27UVgFXO2Fq-gY8QI0m_qAQyl8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                HeadsUpAppearanceController.this.setTrackingHeadsUp((ExpandableNotificationRow) obj);
            }
        };
        this.mUpdatePanelTranslation = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$22QZFjoGlQJQoKOrFe-bHbZltB4
            @Override // java.lang.Runnable
            public final void run() {
                HeadsUpAppearanceController.this.updatePanelTranslation();
            }
        };
        this.mSetExpandedHeight = new BiConsumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$bcWIlLYHGuPtIh99P0bExeXSsMQ
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                HeadsUpAppearanceController.this.setAppearFraction(((Float) obj).floatValue(), ((Float) obj2).floatValue());
            }
        };
        this.mStackScrollLayoutChangeListener = new View.OnLayoutChangeListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$HeadsUpAppearanceController$hwNOwOgXItDjQM7QwL00pigpnrk
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                HeadsUpAppearanceController.this.lambda$new$0$HeadsUpAppearanceController(view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        };
        this.mParentClippingParams = new ViewClippingUtil.ClippingParameters() { // from class: com.android.systemui.statusbar.phone.HeadsUpAppearanceController.1
            public boolean shouldFinish(View view) {
                return view.getId() == R.id.status_bar;
            }
        };
        this.mAnimationsEnabled = true;
        this.mNotificationIconAreaController = notificationIconAreaController;
        this.mHeadsUpManager = headsUpManager;
        this.mHeadsUpManager.addListener(this);
        this.mHeadsUpStatusBarView = headsUpStatusBarView;
        this.mCenteredIconView = centeredIconView;
        headsUpStatusBarView.setOnDrawingRectChangedListener(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$HeadsUpAppearanceController$1d3l5klDiH8maZOdHwrJBKgigPE
            @Override // java.lang.Runnable
            public final void run() {
                HeadsUpAppearanceController.this.lambda$new$1$HeadsUpAppearanceController();
            }
        });
        this.mStackScroller = stackScroller;
        this.mPanelView = panelView;
        panelView.addTrackingHeadsUpListener(this.mSetTrackingHeadsUp);
        panelView.addVerticalTranslationListener(this.mUpdatePanelTranslation);
        panelView.setHeadsUpAppearanceController(this);
        this.mStackScroller.addOnExpandedHeightChangedListener(this.mSetExpandedHeight);
        this.mStackScroller.addOnLayoutChangeListener(this.mStackScrollLayoutChangeListener);
        this.mStackScroller.setHeadsUpAppearanceController(this);
        this.mClockView = clockView;
        this.mOperatorNameView = operatorNameView;
        this.mDarkIconDispatcher = (DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class);
        this.mDarkIconDispatcher.addDarkReceiver(this);
        this.mHeadsUpStatusBarView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.systemui.statusbar.phone.HeadsUpAppearanceController.2
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (HeadsUpAppearanceController.this.shouldBeVisible()) {
                    HeadsUpAppearanceController.this.updateTopEntry();
                    HeadsUpAppearanceController.this.mStackScroller.requestLayout();
                }
                HeadsUpAppearanceController.this.mHeadsUpStatusBarView.removeOnLayoutChangeListener(this);
            }
        });
        this.mBypassController = bypassController;
        this.mStatusBarStateController = stateController;
        this.mWakeUpCoordinator = wakeUpCoordinator;
        wakeUpCoordinator.addListener(this);
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(headsUpStatusBarView.getContext(), CommandQueue.class);
        this.mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
    }

    public /* synthetic */ void lambda$new$1$HeadsUpAppearanceController() {
        updateIsolatedIconLocation(true);
    }

    public void destroy() {
        this.mHeadsUpManager.removeListener(this);
        this.mHeadsUpStatusBarView.setOnDrawingRectChangedListener(null);
        this.mWakeUpCoordinator.removeListener(this);
        this.mPanelView.removeTrackingHeadsUpListener(this.mSetTrackingHeadsUp);
        this.mPanelView.removeVerticalTranslationListener(this.mUpdatePanelTranslation);
        this.mPanelView.setHeadsUpAppearanceController(null);
        this.mStackScroller.removeOnExpandedHeightChangedListener(this.mSetExpandedHeight);
        this.mStackScroller.removeOnLayoutChangeListener(this.mStackScrollLayoutChangeListener);
        this.mDarkIconDispatcher.removeDarkReceiver(this);
    }

    private void updateIsolatedIconLocation(boolean requireStateUpdate) {
        this.mNotificationIconAreaController.setIsolatedIconLocation(this.mHeadsUpStatusBarView.getIconDrawingRect(), requireStateUpdate);
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpPinned(NotificationEntry entry) {
        updateTopEntry();
        lambda$updateHeadsUpHeaders$4$HeadsUpAppearanceController(entry);
    }

    private int getRtlTranslation() {
        if (this.mPoint == null) {
            this.mPoint = new Point();
        }
        int realDisplaySize = 0;
        if (this.mStackScroller.getDisplay() != null) {
            this.mStackScroller.getDisplay().getRealSize(this.mPoint);
            realDisplaySize = this.mPoint.x;
        }
        WindowInsets windowInset = this.mStackScroller.getRootWindowInsets();
        DisplayCutout cutout = windowInset != null ? windowInset.getDisplayCutout() : null;
        int sysWinLeft = windowInset != null ? windowInset.getStableInsetLeft() : 0;
        int sysWinRight = windowInset != null ? windowInset.getStableInsetRight() : 0;
        int cutoutLeft = cutout != null ? cutout.getSafeInsetLeft() : 0;
        int cutoutRight = cutout != null ? cutout.getSafeInsetRight() : 0;
        int leftInset = Math.max(sysWinLeft, cutoutLeft);
        int rightInset = Math.max(sysWinRight, cutoutRight);
        return ((this.mStackScroller.getRight() + leftInset) + rightInset) - realDisplaySize;
    }

    public void updatePanelTranslation() {
        float newTranslation;
        if (this.mStackScroller.isLayoutRtl()) {
            newTranslation = getRtlTranslation();
        } else {
            newTranslation = this.mStackScroller.getLeft();
        }
        this.mHeadsUpStatusBarView.setPanelTranslation(newTranslation + this.mStackScroller.getTranslationX());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTopEntry() {
        NotificationEntry newEntry = null;
        if (shouldBeVisible()) {
            newEntry = this.mHeadsUpManager.getTopEntry();
        }
        NotificationEntry previousEntry = this.mHeadsUpStatusBarView.getShowingEntry();
        this.mHeadsUpStatusBarView.lambda$setEntry$0$HeadsUpStatusBarView(newEntry);
        if (newEntry != previousEntry) {
            boolean animateIsolation = false;
            if (newEntry == null) {
                setShown(false);
                animateIsolation = true ^ this.mIsExpanded;
            } else if (previousEntry == null) {
                setShown(true);
                animateIsolation = true ^ this.mIsExpanded;
            }
            updateIsolatedIconLocation(false);
            this.mNotificationIconAreaController.showIconIsolated(newEntry == null ? null : newEntry.icon, animateIsolation);
        }
    }

    private void setShown(boolean isShown) {
        if (this.mShown != isShown) {
            this.mShown = isShown;
            if (isShown) {
                updateParentClipping(false);
                this.mHeadsUpStatusBarView.setVisibility(0);
                show(this.mHeadsUpStatusBarView);
                hide(this.mClockView, 4);
                if (this.mCenteredIconView.getVisibility() != 8) {
                    hide(this.mCenteredIconView, 4);
                }
                View view = this.mOperatorNameView;
                if (view != null) {
                    hide(view, 4);
                }
            } else {
                show(this.mClockView);
                if (this.mCenteredIconView.getVisibility() != 8) {
                    show(this.mCenteredIconView);
                }
                View view2 = this.mOperatorNameView;
                if (view2 != null) {
                    show(view2);
                }
                hide(this.mHeadsUpStatusBarView, 8, new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$HeadsUpAppearanceController$iMPD_c-MpkAUOLIdQAujzNCdyYQ
                    @Override // java.lang.Runnable
                    public final void run() {
                        HeadsUpAppearanceController.this.lambda$setShown$2$HeadsUpAppearanceController();
                    }
                });
            }
            if (this.mStatusBarStateController.getState() != 0) {
                this.mCommandQueue.recomputeDisableFlags(this.mHeadsUpStatusBarView.getContext().getDisplayId(), false);
            }
        }
    }

    public /* synthetic */ void lambda$setShown$2$HeadsUpAppearanceController() {
        updateParentClipping(true);
    }

    private void updateParentClipping(boolean shouldClip) {
        ViewClippingUtil.setClippingDeactivated(this.mHeadsUpStatusBarView, !shouldClip, this.mParentClippingParams);
    }

    private void hide(View view, int endState) {
        hide(view, endState, null);
    }

    private void hide(final View view, final int endState, final Runnable callback) {
        if (this.mAnimationsEnabled) {
            CrossFadeHelper.fadeOut(view, 110L, 0, new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$HeadsUpAppearanceController$6jWM7O8t5p3KhJ2lcC8glbZxW9w
                @Override // java.lang.Runnable
                public final void run() {
                    HeadsUpAppearanceController.lambda$hide$3(view, endState, callback);
                }
            });
            return;
        }
        view.setVisibility(endState);
        if (callback != null) {
            callback.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$hide$3(View view, int endState, Runnable callback) {
        view.setVisibility(endState);
        if (callback != null) {
            callback.run();
        }
    }

    private void show(View view) {
        if (this.mAnimationsEnabled) {
            CrossFadeHelper.fadeIn(view, 110L, 100);
        } else {
            view.setVisibility(0);
        }
    }

    @VisibleForTesting
    void setAnimationsEnabled(boolean enabled) {
        this.mAnimationsEnabled = enabled;
    }

    @VisibleForTesting
    public boolean isShown() {
        return this.mShown;
    }

    public boolean shouldBeVisible() {
        boolean notificationsShown = !this.mWakeUpCoordinator.getNotificationsFullyHidden();
        boolean canShow = !this.mIsExpanded && notificationsShown;
        if (this.mBypassController.getBypassEnabled() && ((this.mStatusBarStateController.getState() == 1 || this.mKeyguardMonitor.isKeyguardGoingAway()) && notificationsShown)) {
            canShow = true;
        }
        return canShow && this.mHeadsUpManager.hasPinnedHeadsUp();
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpUnPinned(NotificationEntry entry) {
        updateTopEntry();
        lambda$updateHeadsUpHeaders$4$HeadsUpAppearanceController(entry);
    }

    public void setAppearFraction(float expandedHeight, float appearFraction) {
        boolean changed = expandedHeight != this.mExpandedHeight;
        this.mExpandedHeight = expandedHeight;
        this.mAppearFraction = appearFraction;
        boolean isExpanded = expandedHeight > 0.0f;
        if (changed) {
            updateHeadsUpHeaders();
        }
        if (isExpanded != this.mIsExpanded) {
            this.mIsExpanded = isExpanded;
            updateTopEntry();
        }
    }

    public void setTrackingHeadsUp(ExpandableNotificationRow trackedChild) {
        ExpandableNotificationRow previousTracked = this.mTrackedChild;
        this.mTrackedChild = trackedChild;
        if (previousTracked != null) {
            lambda$updateHeadsUpHeaders$4$HeadsUpAppearanceController(previousTracked.getEntry());
        }
    }

    private void updateHeadsUpHeaders() {
        this.mHeadsUpManager.getAllEntries().forEach(new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$HeadsUpAppearanceController$r_oAtsVltL-EqS4w4SiU08R_o1A
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                HeadsUpAppearanceController.this.lambda$updateHeadsUpHeaders$4$HeadsUpAppearanceController((NotificationEntry) obj);
            }
        });
    }

    /* renamed from: updateHeader */
    public void lambda$updateHeadsUpHeaders$4$HeadsUpAppearanceController(NotificationEntry entry) {
        ExpandableNotificationRow row = entry.getRow();
        float headerVisibleAmount = 1.0f;
        if (row.isPinned() || row.isHeadsUpAnimatingAway() || row == this.mTrackedChild || row.showingPulsing()) {
            headerVisibleAmount = this.mAppearFraction;
        }
        row.setHeaderVisibleAmount(headerVisibleAmount);
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver
    public void onDarkChanged(Rect area, float darkIntensity, int tint) {
        this.mHeadsUpStatusBarView.onDarkChanged(area, darkIntensity, tint);
    }

    public void onStateChanged() {
        updateTopEntry();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void readFrom(HeadsUpAppearanceController oldController) {
        if (oldController != null) {
            this.mTrackedChild = oldController.mTrackedChild;
            this.mExpandedHeight = oldController.mExpandedHeight;
            this.mIsExpanded = oldController.mIsExpanded;
            this.mAppearFraction = oldController.mAppearFraction;
        }
    }

    @Override // com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.WakeUpListener
    public void onFullyHiddenChanged(boolean isFullyHidden) {
        updateTopEntry();
    }
}
