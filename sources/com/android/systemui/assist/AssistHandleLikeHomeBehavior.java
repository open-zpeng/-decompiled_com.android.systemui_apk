package com.android.systemui.assist;

import android.content.Context;
import androidx.annotation.Nullable;
import com.android.systemui.assist.AssistHandleBehaviorController;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.OverviewProxyService;
import dagger.Lazy;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
final class AssistHandleLikeHomeBehavior implements AssistHandleBehaviorController.BehaviorController {
    @Nullable
    private AssistHandleCallbacks mAssistHandleCallbacks;
    private boolean mIsAwake;
    private boolean mIsDozing;
    private boolean mIsHomeHandleHiding;
    private final Lazy<OverviewProxyService> mOverviewProxyService;
    private final Lazy<StatusBarStateController> mStatusBarStateController;
    private final Lazy<WakefulnessLifecycle> mWakefulnessLifecycle;
    private final StatusBarStateController.StateListener mStatusBarStateListener = new StatusBarStateController.StateListener() { // from class: com.android.systemui.assist.AssistHandleLikeHomeBehavior.1
        @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
        public void onDozingChanged(boolean isDozing) {
            AssistHandleLikeHomeBehavior.this.handleDozingChanged(isDozing);
        }
    };
    private final WakefulnessLifecycle.Observer mWakefulnessLifecycleObserver = new WakefulnessLifecycle.Observer() { // from class: com.android.systemui.assist.AssistHandleLikeHomeBehavior.2
        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onStartedWakingUp() {
            AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(false);
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onFinishedWakingUp() {
            AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(true);
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onStartedGoingToSleep() {
            AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(false);
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onFinishedGoingToSleep() {
            AssistHandleLikeHomeBehavior.this.handleWakefullnessChanged(false);
        }
    };
    private final OverviewProxyService.OverviewProxyListener mOverviewProxyListener = new OverviewProxyService.OverviewProxyListener() { // from class: com.android.systemui.assist.AssistHandleLikeHomeBehavior.3
        @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
        public void onSystemUiStateChanged(int sysuiStateFlags) {
            AssistHandleLikeHomeBehavior.this.handleSystemUiStateChange(sysuiStateFlags);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    @Inject
    public AssistHandleLikeHomeBehavior(Lazy<StatusBarStateController> statusBarStateController, Lazy<WakefulnessLifecycle> wakefulnessLifecycle, Lazy<OverviewProxyService> overviewProxyService) {
        this.mStatusBarStateController = statusBarStateController;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        this.mOverviewProxyService = overviewProxyService;
    }

    @Override // com.android.systemui.assist.AssistHandleBehaviorController.BehaviorController
    public void onModeActivated(Context context, AssistHandleCallbacks callbacks) {
        this.mAssistHandleCallbacks = callbacks;
        this.mIsDozing = this.mStatusBarStateController.get().isDozing();
        this.mStatusBarStateController.get().addCallback(this.mStatusBarStateListener);
        this.mIsAwake = this.mWakefulnessLifecycle.get().getWakefulness() == 2;
        this.mWakefulnessLifecycle.get().addObserver(this.mWakefulnessLifecycleObserver);
        this.mOverviewProxyService.get().addCallback(this.mOverviewProxyListener);
        callbackForCurrentState();
    }

    @Override // com.android.systemui.assist.AssistHandleBehaviorController.BehaviorController
    public void onModeDeactivated() {
        this.mAssistHandleCallbacks = null;
        this.mStatusBarStateController.get().removeCallback(this.mStatusBarStateListener);
        this.mWakefulnessLifecycle.get().removeObserver(this.mWakefulnessLifecycleObserver);
        this.mOverviewProxyService.get().removeCallback(this.mOverviewProxyListener);
    }

    private static boolean isHomeHandleHiding(int sysuiStateFlags) {
        return (sysuiStateFlags & 2) != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDozingChanged(boolean isDozing) {
        if (this.mIsDozing == isDozing) {
            return;
        }
        this.mIsDozing = isDozing;
        callbackForCurrentState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWakefullnessChanged(boolean isAwake) {
        if (this.mIsAwake == isAwake) {
            return;
        }
        this.mIsAwake = isAwake;
        callbackForCurrentState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSystemUiStateChange(int sysuiStateFlags) {
        boolean isHomeHandleHiding = isHomeHandleHiding(sysuiStateFlags);
        if (this.mIsHomeHandleHiding == isHomeHandleHiding) {
            return;
        }
        this.mIsHomeHandleHiding = isHomeHandleHiding;
        callbackForCurrentState();
    }

    private void callbackForCurrentState() {
        if (this.mAssistHandleCallbacks == null) {
            return;
        }
        if (this.mIsHomeHandleHiding || !isFullyAwake()) {
            this.mAssistHandleCallbacks.hide();
        } else {
            this.mAssistHandleCallbacks.showAndStay();
        }
    }

    private boolean isFullyAwake() {
        return this.mIsAwake && !this.mIsDozing;
    }

    @Override // com.android.systemui.assist.AssistHandleBehaviorController.BehaviorController
    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "Current AssistHandleLikeHomeBehavior State:");
        pw.println(prefix + "   mIsDozing=" + this.mIsDozing);
        pw.println(prefix + "   mIsAwake=" + this.mIsAwake);
        pw.println(prefix + "   mIsHomeHandleHiding=" + this.mIsHomeHandleHiding);
    }
}
