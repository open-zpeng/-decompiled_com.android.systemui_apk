package com.android.systemui.statusbar.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.MathUtils;
import android.view.IRemoteAnimationFinishedCallback;
import android.view.IRemoteAnimationRunner;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.view.SyncRtSurfaceTransactionApplier;
import android.view.View;
import com.android.internal.policy.ScreenDecorationsUtils;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import com.android.systemui.statusbar.phone.StatusBarWindowView;
/* loaded from: classes21.dex */
public class ActivityLaunchAnimator {
    public static final long ANIMATION_DELAY_ICON_FADE_IN = 14;
    private static final int ANIMATION_DURATION = 400;
    public static final long ANIMATION_DURATION_FADE_APP = 200;
    public static final long ANIMATION_DURATION_FADE_CONTENT = 67;
    private static final long LAUNCH_TIMEOUT = 500;
    private boolean mAnimationPending;
    private boolean mAnimationRunning;
    private Callback mCallback;
    private boolean mIsLaunchForActivity;
    private final NotificationListContainer mNotificationContainer;
    private final NotificationPanelView mNotificationPanel;
    private final StatusBarWindowView mStatusBarWindow;
    private final Runnable mTimeoutRunnable = new Runnable() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$ActivityLaunchAnimator$l5Gj6YM2XO6z1WFQpGTriWePKVk
        @Override // java.lang.Runnable
        public final void run() {
            ActivityLaunchAnimator.this.lambda$new$0$ActivityLaunchAnimator();
        }
    };
    private final float mWindowCornerRadius;

    /* loaded from: classes21.dex */
    public interface Callback {
        boolean areLaunchAnimationsEnabled();

        void onExpandAnimationFinished(boolean z);

        void onExpandAnimationTimedOut();

        void onLaunchAnimationCancelled();
    }

    public /* synthetic */ void lambda$new$0$ActivityLaunchAnimator() {
        setAnimationPending(false);
        this.mCallback.onExpandAnimationTimedOut();
    }

    public ActivityLaunchAnimator(StatusBarWindowView statusBarWindow, Callback callback, NotificationPanelView notificationPanel, NotificationListContainer container) {
        this.mNotificationPanel = notificationPanel;
        this.mNotificationContainer = container;
        this.mStatusBarWindow = statusBarWindow;
        this.mCallback = callback;
        this.mWindowCornerRadius = ScreenDecorationsUtils.getWindowCornerRadius(statusBarWindow.getResources());
    }

    public RemoteAnimationAdapter getLaunchAnimation(View sourceView, boolean occluded) {
        if (!(sourceView instanceof ExpandableNotificationRow) || !this.mCallback.areLaunchAnimationsEnabled() || occluded) {
            return null;
        }
        AnimationRunner animationRunner = new AnimationRunner((ExpandableNotificationRow) sourceView);
        return new RemoteAnimationAdapter(animationRunner, 400L, 250L);
    }

    public boolean isAnimationPending() {
        return this.mAnimationPending;
    }

    public void setLaunchResult(int launchResult, boolean wasIntentActivity) {
        this.mIsLaunchForActivity = wasIntentActivity;
        setAnimationPending((launchResult == 2 || launchResult == 0) && this.mCallback.areLaunchAnimationsEnabled());
    }

    public boolean isLaunchForActivity() {
        return this.mIsLaunchForActivity;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAnimationPending(boolean pending) {
        this.mAnimationPending = pending;
        this.mStatusBarWindow.setExpandAnimationPending(pending);
        if (pending) {
            this.mStatusBarWindow.postDelayed(this.mTimeoutRunnable, 500L);
        } else {
            this.mStatusBarWindow.removeCallbacks(this.mTimeoutRunnable);
        }
    }

    public boolean isAnimationRunning() {
        return this.mAnimationRunning;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public class AnimationRunner extends IRemoteAnimationRunner.Stub {
        private float mCornerRadius;
        private final float mNotificationCornerRadius;
        private final ExpandableNotificationRow mSourceNotification;
        private final SyncRtSurfaceTransactionApplier mSyncRtTransactionApplier;
        private final Rect mWindowCrop = new Rect();
        private boolean mIsFullScreenLaunch = true;
        private final ExpandAnimationParameters mParams = new ExpandAnimationParameters();

        public AnimationRunner(ExpandableNotificationRow sourceNofitication) {
            this.mSourceNotification = sourceNofitication;
            this.mSyncRtTransactionApplier = new SyncRtSurfaceTransactionApplier(this.mSourceNotification);
            this.mNotificationCornerRadius = Math.max(this.mSourceNotification.getCurrentTopRoundness(), this.mSourceNotification.getCurrentBottomRoundness());
        }

        public void onAnimationStart(final RemoteAnimationTarget[] remoteAnimationTargets, final IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) throws RemoteException {
            this.mSourceNotification.post(new Runnable() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$ActivityLaunchAnimator$AnimationRunner$sNLXzFzCbt6n0LlixbKU_lp1tVA
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityLaunchAnimator.AnimationRunner.this.lambda$onAnimationStart$0$ActivityLaunchAnimator$AnimationRunner(remoteAnimationTargets, iRemoteAnimationFinishedCallback);
                }
            });
        }

        public /* synthetic */ void lambda$onAnimationStart$0$ActivityLaunchAnimator$AnimationRunner(RemoteAnimationTarget[] remoteAnimationTargets, final IRemoteAnimationFinishedCallback iRemoteAnimationFinishedCallback) {
            final RemoteAnimationTarget primary = getPrimaryRemoteAnimationTarget(remoteAnimationTargets);
            if (primary == null) {
                ActivityLaunchAnimator.this.setAnimationPending(false);
                invokeCallback(iRemoteAnimationFinishedCallback);
                ActivityLaunchAnimator.this.mNotificationPanel.collapse(false, 1.0f);
                return;
            }
            boolean z = true;
            setExpandAnimationRunning(true);
            if (primary.position.y != 0 || primary.sourceContainerBounds.height() < ActivityLaunchAnimator.this.mNotificationPanel.getHeight()) {
                z = false;
            }
            this.mIsFullScreenLaunch = z;
            if (!this.mIsFullScreenLaunch) {
                ActivityLaunchAnimator.this.mNotificationPanel.collapseWithDuration(400);
            }
            ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mParams.startPosition = this.mSourceNotification.getLocationOnScreen();
            this.mParams.startTranslationZ = this.mSourceNotification.getTranslationZ();
            this.mParams.startClipTopAmount = this.mSourceNotification.getClipTopAmount();
            if (this.mSourceNotification.isChildInGroup()) {
                int parentClip = this.mSourceNotification.getNotificationParent().getClipTopAmount();
                this.mParams.parentStartClipTopAmount = parentClip;
                if (parentClip != 0) {
                    float childClip = parentClip - this.mSourceNotification.getTranslationY();
                    if (childClip > 0.0f) {
                        this.mParams.startClipTopAmount = (int) Math.ceil(childClip);
                    }
                }
            }
            final int targetWidth = primary.sourceContainerBounds.width();
            final int notificationHeight = this.mSourceNotification.getActualHeight() - this.mSourceNotification.getClipBottomAmount();
            final int notificationWidth = this.mSourceNotification.getWidth();
            anim.setDuration(400L);
            anim.setInterpolator(Interpolators.LINEAR);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.ActivityLaunchAnimator.AnimationRunner.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    AnimationRunner.this.mParams.linearProgress = animation.getAnimatedFraction();
                    float progress = Interpolators.FAST_OUT_SLOW_IN.getInterpolation(AnimationRunner.this.mParams.linearProgress);
                    int newWidth = (int) MathUtils.lerp(notificationWidth, targetWidth, progress);
                    AnimationRunner.this.mParams.left = (int) ((targetWidth - newWidth) / 2.0f);
                    AnimationRunner.this.mParams.right = AnimationRunner.this.mParams.left + newWidth;
                    AnimationRunner.this.mParams.top = (int) MathUtils.lerp(AnimationRunner.this.mParams.startPosition[1], primary.position.y, progress);
                    AnimationRunner.this.mParams.bottom = (int) MathUtils.lerp(AnimationRunner.this.mParams.startPosition[1] + notificationHeight, primary.position.y + primary.sourceContainerBounds.bottom, progress);
                    AnimationRunner animationRunner = AnimationRunner.this;
                    animationRunner.mCornerRadius = MathUtils.lerp(animationRunner.mNotificationCornerRadius, ActivityLaunchAnimator.this.mWindowCornerRadius, progress);
                    AnimationRunner.this.applyParamsToWindow(primary);
                    AnimationRunner animationRunner2 = AnimationRunner.this;
                    animationRunner2.applyParamsToNotification(animationRunner2.mParams);
                    AnimationRunner animationRunner3 = AnimationRunner.this;
                    animationRunner3.applyParamsToNotificationList(animationRunner3.mParams);
                }
            });
            anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.ActivityLaunchAnimator.AnimationRunner.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    AnimationRunner.this.setExpandAnimationRunning(false);
                    AnimationRunner.this.invokeCallback(iRemoteAnimationFinishedCallback);
                }
            });
            anim.start();
            ActivityLaunchAnimator.this.setAnimationPending(false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void invokeCallback(IRemoteAnimationFinishedCallback callback) {
            try {
                callback.onAnimationFinished();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        private RemoteAnimationTarget getPrimaryRemoteAnimationTarget(RemoteAnimationTarget[] remoteAnimationTargets) {
            for (RemoteAnimationTarget app : remoteAnimationTargets) {
                if (app.mode == 0) {
                    return app;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setExpandAnimationRunning(boolean running) {
            ActivityLaunchAnimator.this.mNotificationPanel.setLaunchingNotification(running);
            this.mSourceNotification.setExpandAnimationRunning(running);
            ActivityLaunchAnimator.this.mStatusBarWindow.setExpandAnimationRunning(running);
            ActivityLaunchAnimator.this.mNotificationContainer.setExpandingNotification(running ? this.mSourceNotification : null);
            ActivityLaunchAnimator.this.mAnimationRunning = running;
            if (!running) {
                ActivityLaunchAnimator.this.mCallback.onExpandAnimationFinished(this.mIsFullScreenLaunch);
                applyParamsToNotification(null);
                applyParamsToNotificationList(null);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void applyParamsToNotificationList(ExpandAnimationParameters params) {
            ActivityLaunchAnimator.this.mNotificationContainer.applyExpandAnimationParams(params);
            ActivityLaunchAnimator.this.mNotificationPanel.applyExpandAnimationParams(params);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void applyParamsToNotification(ExpandAnimationParameters params) {
            this.mSourceNotification.applyExpandAnimationParams(params);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void applyParamsToWindow(RemoteAnimationTarget app) {
            Matrix m = new Matrix();
            m.postTranslate(0.0f, this.mParams.top - app.position.y);
            this.mWindowCrop.set(this.mParams.left, 0, this.mParams.right, this.mParams.getHeight());
            SyncRtSurfaceTransactionApplier.SurfaceParams params = new SyncRtSurfaceTransactionApplier.SurfaceParams(app.leash, 1.0f, m, this.mWindowCrop, app.prefixOrderIndex, this.mCornerRadius, true);
            this.mSyncRtTransactionApplier.scheduleApply(new SyncRtSurfaceTransactionApplier.SurfaceParams[]{params});
        }

        public void onAnimationCancelled() throws RemoteException {
            this.mSourceNotification.post(new Runnable() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$ActivityLaunchAnimator$AnimationRunner$M-3NAwVAMqbtd1nWxQdGu3JgCNY
                @Override // java.lang.Runnable
                public final void run() {
                    ActivityLaunchAnimator.AnimationRunner.this.lambda$onAnimationCancelled$1$ActivityLaunchAnimator$AnimationRunner();
                }
            });
        }

        public /* synthetic */ void lambda$onAnimationCancelled$1$ActivityLaunchAnimator$AnimationRunner() {
            ActivityLaunchAnimator.this.setAnimationPending(false);
            ActivityLaunchAnimator.this.mCallback.onLaunchAnimationCancelled();
        }
    }

    /* loaded from: classes21.dex */
    public static class ExpandAnimationParameters {
        int bottom;
        int left;
        float linearProgress;
        int parentStartClipTopAmount;
        int right;
        int startClipTopAmount;
        int[] startPosition;
        float startTranslationZ;
        int top;

        public int getTop() {
            return this.top;
        }

        public int getBottom() {
            return this.bottom;
        }

        public int getWidth() {
            return this.right - this.left;
        }

        public int getHeight() {
            return this.bottom - this.top;
        }

        public int getTopChange() {
            int clipTopAmountCompensation = 0;
            int i = this.startClipTopAmount;
            if (i != 0.0f) {
                clipTopAmountCompensation = (int) MathUtils.lerp(0.0f, i, Interpolators.FAST_OUT_SLOW_IN.getInterpolation(this.linearProgress));
            }
            return Math.min((this.top - this.startPosition[1]) - clipTopAmountCompensation, 0);
        }

        public float getProgress() {
            return this.linearProgress;
        }

        public float getProgress(long delay, long duration) {
            return MathUtils.constrain(((this.linearProgress * 400.0f) - ((float) delay)) / ((float) duration), 0.0f, 1.0f);
        }

        public int getStartClipTopAmount() {
            return this.startClipTopAmount;
        }

        public int getParentStartClipTopAmount() {
            return this.parentStartClipTopAmount;
        }

        public float getStartTranslationZ() {
            return this.startTranslationZ;
        }
    }
}
