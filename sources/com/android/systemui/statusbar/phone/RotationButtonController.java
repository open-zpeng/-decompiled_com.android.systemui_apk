package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.IRotationWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManagerGlobal;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.statusbar.phone.RotationButtonController;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.util.wakelock.WakeLock;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.controller.OsdController;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes21.dex */
public class RotationButtonController {
    private static final int BUTTON_FADE_IN_OUT_DURATION_MS = 100;
    private static final int NAVBAR_HIDDEN_PENDING_ICON_TIMEOUT_MS = 20000;
    private static final int NUM_ACCEPTED_ROTATION_SUGGESTIONS_FOR_INTRODUCTION = 3;
    private AccessibilityManagerWrapper mAccessibilityManagerWrapper;
    private final Context mContext;
    private boolean mHoveringRotationSuggestion;
    private boolean mIsNavigationBarShowing;
    private int mLastRotationSuggestion;
    private boolean mPendingRotationSuggestion;
    private Consumer<Integer> mRotWatcherListener;
    private Animator mRotateHideAnimator;
    private final RotationButton mRotationButton;
    private RotationLockController mRotationLockController;
    private int mStyleRes;
    private TaskStackListenerImpl mTaskStackListener;
    private final MetricsLogger mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
    private final ViewRippler mViewRippler = new ViewRippler(this, null);
    private boolean mListenersRegistered = false;
    private final Runnable mRemoveRotationProposal = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$RotationButtonController$9GntNFTDdKoyCtcSVI_eBCW3dMQ
        @Override // java.lang.Runnable
        public final void run() {
            RotationButtonController.this.lambda$new$0$RotationButtonController();
        }
    };
    private final Runnable mCancelPendingRotationProposal = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$RotationButtonController$rLt402gKIdgNcqykKz16VIeLAMM
        @Override // java.lang.Runnable
        public final void run() {
            RotationButtonController.this.lambda$new$1$RotationButtonController();
        }
    };
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private final IRotationWatcher.Stub mRotationWatcher = new AnonymousClass1();

    public /* synthetic */ void lambda$new$0$RotationButtonController() {
        setRotateSuggestionButtonState(false);
    }

    public /* synthetic */ void lambda$new$1$RotationButtonController() {
        this.mPendingRotationSuggestion = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.RotationButtonController$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 extends IRotationWatcher.Stub {
        AnonymousClass1() {
        }

        public void onRotationChanged(final int rotation) throws RemoteException {
            RotationButtonController.this.mMainThreadHandler.postAtFrontOfQueue(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$RotationButtonController$1$wNXXdlqLeBk1NR5FrlGSJawDu0I
                @Override // java.lang.Runnable
                public final void run() {
                    RotationButtonController.AnonymousClass1.this.lambda$onRotationChanged$0$RotationButtonController$1(rotation);
                }
            });
        }

        public /* synthetic */ void lambda$onRotationChanged$0$RotationButtonController$1(int rotation) {
            if (RotationButtonController.this.mRotationLockController.isRotationLocked()) {
                if (RotationButtonController.this.shouldOverrideUserLockPrefs(rotation)) {
                    RotationButtonController.this.setRotationLockedAtAngle(rotation);
                }
                RotationButtonController.this.setRotateSuggestionButtonState(false, true);
            }
            if (RotationButtonController.this.mRotWatcherListener != null) {
                RotationButtonController.this.mRotWatcherListener.accept(Integer.valueOf(rotation));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasDisable2RotateSuggestionFlag(int disable2Flags) {
        return (disable2Flags & 16) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RotationButtonController(Context context, int style, RotationButton rotationButton) {
        this.mContext = context;
        this.mRotationButton = rotationButton;
        this.mRotationButton.setRotationButtonController(this);
        this.mStyleRes = style;
        this.mIsNavigationBarShowing = true;
        this.mRotationLockController = (RotationLockController) Dependency.get(RotationLockController.class);
        this.mAccessibilityManagerWrapper = (AccessibilityManagerWrapper) Dependency.get(AccessibilityManagerWrapper.class);
        this.mTaskStackListener = new TaskStackListenerImpl(this, null);
        this.mRotationButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$RotationButtonController$nGgIS1iCjy5uWWIfPZ9LUPKtUUc
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RotationButtonController.this.onRotateSuggestionClick(view);
            }
        });
        this.mRotationButton.setOnHoverListener(new View.OnHoverListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$RotationButtonController$ITAepcsPx2pDX6xNt-4OEwYvoRc
            @Override // android.view.View.OnHoverListener
            public final boolean onHover(View view, MotionEvent motionEvent) {
                boolean onRotateSuggestionHover;
                onRotateSuggestionHover = RotationButtonController.this.onRotateSuggestionHover(view, motionEvent);
                return onRotateSuggestionHover;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerListeners() {
        if (this.mListenersRegistered) {
            return;
        }
        this.mListenersRegistered = true;
        try {
            WindowManagerGlobal.getWindowManagerService().watchRotation(this.mRotationWatcher, this.mContext.getDisplay().getDisplayId());
            ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unregisterListeners() {
        if (!this.mListenersRegistered) {
            return;
        }
        this.mListenersRegistered = false;
        try {
            WindowManagerGlobal.getWindowManagerService().removeRotationWatcher(this.mRotationWatcher);
            ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mTaskStackListener);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addRotationCallback(Consumer<Integer> watcher) {
        this.mRotWatcherListener = watcher;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRotationLockedAtAngle(int rotationSuggestion) {
        this.mRotationLockController.setRotationLockedAtAngle(true, rotationSuggestion);
    }

    public boolean isRotationLocked() {
        return this.mRotationLockController.isRotationLocked();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRotateSuggestionButtonState(boolean visible) {
        setRotateSuggestionButtonState(visible, false);
    }

    void setRotateSuggestionButtonState(boolean visible, boolean force) {
        View view;
        KeyButtonDrawable currentDrawable;
        if ((!visible && !this.mRotationButton.isVisible()) || (view = this.mRotationButton.getCurrentView()) == null || (currentDrawable = this.mRotationButton.getImageDrawable()) == null) {
            return;
        }
        this.mPendingRotationSuggestion = false;
        this.mMainThreadHandler.removeCallbacks(this.mCancelPendingRotationProposal);
        if (visible) {
            Animator animator = this.mRotateHideAnimator;
            if (animator != null && animator.isRunning()) {
                this.mRotateHideAnimator.cancel();
            }
            this.mRotateHideAnimator = null;
            view.setAlpha(1.0f);
            if (currentDrawable.canAnimate()) {
                currentDrawable.resetAnimation();
                currentDrawable.startAnimation();
            }
            if (!isRotateSuggestionIntroduced()) {
                this.mViewRippler.start(view);
            }
            this.mRotationButton.show();
            return;
        }
        this.mViewRippler.stop();
        if (force) {
            Animator animator2 = this.mRotateHideAnimator;
            if (animator2 != null && animator2.isRunning()) {
                this.mRotateHideAnimator.pause();
            }
            this.mRotationButton.hide();
            return;
        }
        Animator animator3 = this.mRotateHideAnimator;
        if (animator3 == null || !animator3.isRunning()) {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, ThemeManager.AttributeSet.ALPHA, 0.0f);
            fadeOut.setDuration(100L);
            fadeOut.setInterpolator(Interpolators.LINEAR);
            fadeOut.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.RotationButtonController.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    RotationButtonController.this.mRotationButton.hide();
                }
            });
            this.mRotateHideAnimator = fadeOut;
            fadeOut.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDarkIntensity(float darkIntensity) {
        this.mRotationButton.setDarkIntensity(darkIntensity);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRotationProposal(int rotation, int windowRotation, boolean isValid) {
        int style;
        if (this.mRotationButton.acceptRotationProposal()) {
            if (!isValid) {
                setRotateSuggestionButtonState(false);
            } else if (rotation == windowRotation) {
                this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
                setRotateSuggestionButtonState(false);
            } else {
                this.mLastRotationSuggestion = rotation;
                boolean rotationCCW = isRotationAnimationCCW(windowRotation, rotation);
                if (windowRotation == 0 || windowRotation == 2) {
                    style = rotationCCW ? R.style.RotateButtonCCWStart90 : R.style.RotateButtonCWStart90;
                } else {
                    style = rotationCCW ? R.style.RotateButtonCCWStart0 : R.style.RotateButtonCWStart0;
                }
                this.mStyleRes = style;
                this.mRotationButton.updateIcon();
                if (this.mIsNavigationBarShowing) {
                    showAndLogRotationSuggestion();
                    return;
                }
                this.mPendingRotationSuggestion = true;
                this.mMainThreadHandler.removeCallbacks(this.mCancelPendingRotationProposal);
                this.mMainThreadHandler.postDelayed(this.mCancelPendingRotationProposal, WakeLock.DEFAULT_MAX_TIMEOUT);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDisable2FlagChanged(int state2) {
        boolean rotateSuggestionsDisabled = hasDisable2RotateSuggestionFlag(state2);
        if (rotateSuggestionsDisabled) {
            onRotationSuggestionsDisabled();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onNavigationBarWindowVisibilityChange(boolean showing) {
        if (this.mIsNavigationBarShowing != showing) {
            this.mIsNavigationBarShowing = showing;
            if (showing && this.mPendingRotationSuggestion) {
                showAndLogRotationSuggestion();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getStyleRes() {
        return this.mStyleRes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RotationButton getRotationButton() {
        return this.mRotationButton;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRotateSuggestionClick(View v) {
        this.mMetricsLogger.action(1287);
        incrementNumAcceptedRotationSuggestionsIfNeeded();
        setRotationLockedAtAngle(this.mLastRotationSuggestion);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onRotateSuggestionHover(View v, MotionEvent event) {
        int action = event.getActionMasked();
        this.mHoveringRotationSuggestion = action == 9 || action == 7;
        rescheduleRotationTimeout(true);
        return false;
    }

    private void onRotationSuggestionsDisabled() {
        setRotateSuggestionButtonState(false, true);
        this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
    }

    private void showAndLogRotationSuggestion() {
        setRotateSuggestionButtonState(true);
        rescheduleRotationTimeout(false);
        this.mMetricsLogger.visible(1288);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldOverrideUserLockPrefs(int rotation) {
        return rotation == 0;
    }

    private boolean isRotationAnimationCCW(int from, int to) {
        if (from == 0 && to == 1) {
            return false;
        }
        if (from == 0 && to == 2) {
            return true;
        }
        if (from == 0 && to == 3) {
            return true;
        }
        if (from == 1 && to == 0) {
            return true;
        }
        if (from == 1 && to == 2) {
            return false;
        }
        if (from == 1 && to == 3) {
            return true;
        }
        if (from == 2 && to == 0) {
            return true;
        }
        if (from == 2 && to == 1) {
            return true;
        }
        if (from == 2 && to == 3) {
            return false;
        }
        if (from == 3 && to == 0) {
            return false;
        }
        if (from == 3 && to == 1) {
            return true;
        }
        return from == 3 && to == 2;
    }

    private void rescheduleRotationTimeout(boolean reasonHover) {
        Animator animator;
        if (!reasonHover || (((animator = this.mRotateHideAnimator) == null || !animator.isRunning()) && this.mRotationButton.isVisible())) {
            this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
            this.mMainThreadHandler.postDelayed(this.mRemoveRotationProposal, computeRotationProposalTimeout());
        }
    }

    private int computeRotationProposalTimeout() {
        return this.mAccessibilityManagerWrapper.getRecommendedTimeoutMillis(this.mHoveringRotationSuggestion ? 16000 : 5000, 4);
    }

    private boolean isRotateSuggestionIntroduced() {
        ContentResolver cr = this.mContext.getContentResolver();
        return Settings.Secure.getInt(cr, "num_rotation_suggestions_accepted", 0) >= 3;
    }

    private void incrementNumAcceptedRotationSuggestionsIfNeeded() {
        ContentResolver cr = this.mContext.getContentResolver();
        int numSuggestions = Settings.Secure.getInt(cr, "num_rotation_suggestions_accepted", 0);
        if (numSuggestions < 3) {
            Settings.Secure.putInt(cr, "num_rotation_suggestions_accepted", numSuggestions + 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class TaskStackListenerImpl extends TaskStackChangeListener {
        private TaskStackListenerImpl() {
        }

        /* synthetic */ TaskStackListenerImpl(RotationButtonController x0, AnonymousClass1 x1) {
            this();
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskStackChanged() {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskRemoved(int taskId) {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskMovedToFront(int taskId) {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onActivityRequestedOrientationChanged(final int taskId, int requestedOrientation) {
            Optional.ofNullable(ActivityManagerWrapper.getInstance()).map(new Function() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$Zm3Yj0EQnVWvu_ZksQ-OsrTwJ3k
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((ActivityManagerWrapper) obj).getRunningTask();
                }
            }).ifPresent(new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$RotationButtonController$TaskStackListenerImpl$zCjhcFpUTQGdzdQEgIMUjTrjPZU
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    RotationButtonController.TaskStackListenerImpl.this.lambda$onActivityRequestedOrientationChanged$0$RotationButtonController$TaskStackListenerImpl(taskId, (ActivityManager.RunningTaskInfo) obj);
                }
            });
        }

        public /* synthetic */ void lambda$onActivityRequestedOrientationChanged$0$RotationButtonController$TaskStackListenerImpl(int taskId, ActivityManager.RunningTaskInfo a) {
            if (a.id == taskId) {
                RotationButtonController.this.setRotateSuggestionButtonState(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class ViewRippler {
        private static final int RIPPLE_INTERVAL_MS = 2000;
        private static final int RIPPLE_OFFSET_MS = 50;
        private final Runnable mRipple;
        private View mRoot;

        private ViewRippler() {
            this.mRipple = new Runnable() { // from class: com.android.systemui.statusbar.phone.RotationButtonController.ViewRippler.1
                @Override // java.lang.Runnable
                public void run() {
                    if (ViewRippler.this.mRoot.isAttachedToWindow()) {
                        ViewRippler.this.mRoot.setPressed(true);
                        ViewRippler.this.mRoot.setPressed(false);
                    }
                }
            };
        }

        /* synthetic */ ViewRippler(RotationButtonController x0, AnonymousClass1 x1) {
            this();
        }

        public void start(View root) {
            stop();
            this.mRoot = root;
            this.mRoot.postOnAnimationDelayed(this.mRipple, 50L);
            this.mRoot.postOnAnimationDelayed(this.mRipple, OsdController.TN.DURATION_TIMEOUT_SHORT);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 4000L);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 6000L);
            this.mRoot.postOnAnimationDelayed(this.mRipple, 8000L);
        }

        public void stop() {
            View view = this.mRoot;
            if (view != null) {
                view.removeCallbacks(this.mRipple);
            }
        }
    }
}
