package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.MotionEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class AutoHideController implements CommandQueue.Callbacks {
    private static final long AUTOHIDE_TIMEOUT_MS = 2250;
    private static final String TAG = "AutoHideController";
    private boolean mAutoHideSuspended;
    private final CommandQueue mCommandQueue;
    @VisibleForTesting
    int mDisplayId;
    private final Handler mHandler;
    private AutoHideElement mNavigationBar;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private StatusBar mStatusBar;
    @VisibleForTesting
    int mSystemUiVisibility;
    private final IWindowManager mWindowManagerService;
    private int mLastDispatchedSystemUiVisibility = -1;
    private final Runnable mAutoHide = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$AutoHideController$sJYAhc6qJ_sO_ZdtlpSd2BPK504
        @Override // java.lang.Runnable
        public final void run() {
            AutoHideController.this.lambda$new$0$AutoHideController();
        }
    };

    public /* synthetic */ void lambda$new$0$AutoHideController() {
        int requested = this.mSystemUiVisibility & (~getTransientMask());
        if (this.mSystemUiVisibility != requested) {
            notifySystemUiVisibilityChanged(requested);
        }
    }

    @Inject
    public AutoHideController(Context context, @Named("main_handler") Handler handler) {
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class);
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
        this.mHandler = handler;
        this.mRemoteInputManager = (NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class);
        this.mWindowManagerService = (IWindowManager) Dependency.get(IWindowManager.class);
        this.mDisplayId = context.getDisplayId();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void onDisplayRemoved(int displayId) {
        if (displayId == this.mDisplayId) {
            this.mCommandQueue.removeCallback((CommandQueue.Callbacks) this);
        }
    }

    public void setStatusBar(StatusBar statusBar) {
        this.mStatusBar = statusBar;
    }

    public void setNavigationBar(AutoHideElement navigationBar) {
        this.mNavigationBar = navigationBar;
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void setSystemUiVisibility(int displayId, int vis, int fullscreenStackVis, int dockedStackVis, int mask, Rect fullscreenStackBounds, Rect dockedStackBounds, boolean navbarColorManagedByIme) {
        if (displayId != this.mDisplayId) {
            return;
        }
        int oldVal = this.mSystemUiVisibility;
        int newVal = ((~mask) & oldVal) | (vis & mask);
        int diff = newVal ^ oldVal;
        if (diff != 0) {
            this.mSystemUiVisibility = newVal;
            if (hasStatusBar() && (vis & 268435456) != 0) {
                this.mSystemUiVisibility &= -268435457;
            }
            if (hasNavigationBar() && (vis & 536870912) != 0) {
                this.mSystemUiVisibility &= -536870913;
            }
            int i = this.mSystemUiVisibility;
            if (i != newVal) {
                this.mCommandQueue.setSystemUiVisibility(this.mDisplayId, i, fullscreenStackVis, dockedStackVis, mask, fullscreenStackBounds, dockedStackBounds, navbarColorManagedByIme);
            }
            notifySystemUiVisibilityChanged(this.mSystemUiVisibility);
        }
    }

    @VisibleForTesting
    void notifySystemUiVisibilityChanged(int vis) {
        try {
            if (this.mLastDispatchedSystemUiVisibility != vis) {
                this.mWindowManagerService.statusBarVisibilityChanged(this.mDisplayId, vis);
                this.mLastDispatchedSystemUiVisibility = vis;
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Cannot get WindowManager");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resumeSuspendedAutoHide() {
        if (this.mAutoHideSuspended) {
            scheduleAutoHide();
            Runnable checkBarModesRunnable = getCheckBarModesRunnable();
            if (checkBarModesRunnable != null) {
                this.mHandler.postDelayed(checkBarModesRunnable, 500L);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void suspendAutoHide() {
        this.mHandler.removeCallbacks(this.mAutoHide);
        Runnable checkBarModesRunnable = getCheckBarModesRunnable();
        if (checkBarModesRunnable != null) {
            this.mHandler.removeCallbacks(checkBarModesRunnable);
        }
        this.mAutoHideSuspended = (this.mSystemUiVisibility & getTransientMask()) != 0;
    }

    public void touchAutoHide() {
        if ((hasStatusBar() && this.mStatusBar.getStatusBarMode() == 1) || (hasNavigationBar() && this.mNavigationBar.isSemiTransparent())) {
            scheduleAutoHide();
        } else {
            cancelAutoHide();
        }
    }

    private Runnable getCheckBarModesRunnable() {
        if (hasStatusBar()) {
            return new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$AutoHideController$Dw54NegELGCFcbvVgChoOa9gkLA
                @Override // java.lang.Runnable
                public final void run() {
                    AutoHideController.this.lambda$getCheckBarModesRunnable$1$AutoHideController();
                }
            };
        }
        if (hasNavigationBar()) {
            return new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$AutoHideController$FON87SM6b4--2jIBTAjBTcUbKIM
                @Override // java.lang.Runnable
                public final void run() {
                    AutoHideController.this.lambda$getCheckBarModesRunnable$2$AutoHideController();
                }
            };
        }
        return null;
    }

    public /* synthetic */ void lambda$getCheckBarModesRunnable$1$AutoHideController() {
        this.mStatusBar.checkBarModes();
    }

    public /* synthetic */ void lambda$getCheckBarModesRunnable$2$AutoHideController() {
        this.mNavigationBar.synchronizeState();
    }

    public void cancelAutoHide() {
        this.mAutoHideSuspended = false;
        this.mHandler.removeCallbacks(this.mAutoHide);
    }

    private void scheduleAutoHide() {
        cancelAutoHide();
        this.mHandler.postDelayed(this.mAutoHide, AUTOHIDE_TIMEOUT_MS);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkUserAutoHide(MotionEvent event) {
        boolean shouldAutoHide = (this.mSystemUiVisibility & getTransientMask()) != 0 && event.getAction() == 4 && event.getX() == 0.0f && event.getY() == 0.0f;
        if (hasStatusBar()) {
            shouldAutoHide &= true ^ this.mRemoteInputManager.getController().isRemoteInputActive();
        }
        if (shouldAutoHide) {
            userAutoHide();
        }
    }

    public void userAutoHide() {
        cancelAutoHide();
        this.mHandler.postDelayed(this.mAutoHide, 350L);
    }

    private int getTransientMask() {
        int mask = hasStatusBar() ? 0 | 67108864 : 0;
        if (hasNavigationBar()) {
            return mask | 134217728;
        }
        return mask;
    }

    boolean hasNavigationBar() {
        return this.mNavigationBar != null;
    }

    @VisibleForTesting
    boolean hasStatusBar() {
        return this.mStatusBar != null;
    }
}
