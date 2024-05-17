package com.android.systemui.recents;

import android.app.ActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.input.InputManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.policy.ScreenDecorationsUtils;
import com.android.systemui.Dumpable;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.shared.recents.ISystemUiProxy;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarWindowCallback;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.policy.CallbackController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class OverviewProxyService implements CallbackController<OverviewProxyListener>, NavigationModeController.ModeChangedListener, Dumpable {
    private static final String ACTION_QUICKSTEP = "android.intent.action.QUICKSTEP_SERVICE";
    private static final long BACKOFF_MILLIS = 1000;
    private static final long DEFERRED_CALLBACK_MILLIS = 5000;
    private static final long MAX_BACKOFF_MILLIS = 600000;
    public static final String TAG_OPS = "OverviewProxyService";
    private Region mActiveNavBarRegion;
    private boolean mBound;
    private final Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private long mInputFocusTransferStartMillis;
    private float mInputFocusTransferStartY;
    private boolean mInputFocusTransferStarted;
    private boolean mIsEnabled;
    private final NavigationBarController mNavBarController;
    private int mNavBarMode;
    private IOverviewProxy mOverviewProxy;
    private final Intent mQuickStepIntent;
    private final ComponentName mRecentsComponentName;
    private final StatusBarWindowController mStatusBarWinController;
    private boolean mSupportsRoundedCornersOnWindows;
    private int mSysUiStateFlags;
    private float mWindowCornerRadius;
    private final Runnable mConnectionRunnable = new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$2FrwSEVJnaHX9GGsAnD2I96htxU
        @Override // java.lang.Runnable
        public final void run() {
            OverviewProxyService.this.internalConnectToCurrentUser();
        }
    };
    private final List<OverviewProxyListener> mConnectionCallbacks = new ArrayList();
    private int mCurrentBoundedUserId = -1;
    private ISystemUiProxy mSysUiProxy = new AnonymousClass1();
    private final Runnable mDeferredConnectionCallback = new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$53s1j2vSUNo_EjM7u2nSTJl32gM
        @Override // java.lang.Runnable
        public final void run() {
            OverviewProxyService.this.lambda$new$0$OverviewProxyService();
        }
    };
    private final BroadcastReceiver mLauncherStateChangedReceiver = new BroadcastReceiver() { // from class: com.android.systemui.recents.OverviewProxyService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            OverviewProxyService.this.updateEnabledState();
            OverviewProxyService.this.startConnectionToCurrentUser();
        }
    };
    private final ServiceConnection mOverviewServiceConnection = new ServiceConnection() { // from class: com.android.systemui.recents.OverviewProxyService.3
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            OverviewProxyService.this.mConnectionBackoffAttempts = 0;
            OverviewProxyService.this.mHandler.removeCallbacks(OverviewProxyService.this.mDeferredConnectionCallback);
            try {
                service.linkToDeath(OverviewProxyService.this.mOverviewServiceDeathRcpt, 0);
                OverviewProxyService overviewProxyService = OverviewProxyService.this;
                overviewProxyService.mCurrentBoundedUserId = overviewProxyService.mDeviceProvisionedController.getCurrentUser();
                OverviewProxyService.this.mOverviewProxy = IOverviewProxy.Stub.asInterface(service);
                Bundle params = new Bundle();
                params.putBinder(QuickStepContract.KEY_EXTRA_SYSUI_PROXY, OverviewProxyService.this.mSysUiProxy.asBinder());
                params.putFloat(QuickStepContract.KEY_EXTRA_WINDOW_CORNER_RADIUS, OverviewProxyService.this.mWindowCornerRadius);
                params.putBoolean(QuickStepContract.KEY_EXTRA_SUPPORTS_WINDOW_CORNERS, OverviewProxyService.this.mSupportsRoundedCornersOnWindows);
                try {
                    OverviewProxyService.this.mOverviewProxy.onInitialize(params);
                } catch (RemoteException e) {
                    OverviewProxyService.this.mCurrentBoundedUserId = -1;
                    Log.e(OverviewProxyService.TAG_OPS, "Failed to call onInitialize()", e);
                }
                OverviewProxyService.this.dispatchNavButtonBounds();
                OverviewProxyService.this.updateSystemUiStateFlags();
                OverviewProxyService.this.notifyConnectionChanged();
            } catch (RemoteException e2) {
                Log.e(OverviewProxyService.TAG_OPS, "Lost connection to launcher service", e2);
                OverviewProxyService.this.disconnectFromLauncherService();
                OverviewProxyService.this.retryConnectionWithBackoff();
            }
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName name) {
            Log.w(OverviewProxyService.TAG_OPS, "Null binding of '" + name + "', try reconnecting");
            OverviewProxyService.this.mCurrentBoundedUserId = -1;
            OverviewProxyService.this.retryConnectionWithBackoff();
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(ComponentName name) {
            Log.w(OverviewProxyService.TAG_OPS, "Binding died of '" + name + "', try reconnecting");
            OverviewProxyService.this.mCurrentBoundedUserId = -1;
            OverviewProxyService.this.retryConnectionWithBackoff();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            OverviewProxyService.this.mCurrentBoundedUserId = -1;
        }
    };
    private final DeviceProvisionedController.DeviceProvisionedListener mDeviceProvisionedCallback = new DeviceProvisionedController.DeviceProvisionedListener() { // from class: com.android.systemui.recents.OverviewProxyService.4
        @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener
        public void onUserSetupChanged() {
            if (OverviewProxyService.this.mDeviceProvisionedController.isCurrentUserSetup()) {
                OverviewProxyService.this.internalConnectToCurrentUser();
            }
        }

        @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener
        public void onUserSwitched() {
            OverviewProxyService.this.mConnectionBackoffAttempts = 0;
            OverviewProxyService.this.internalConnectToCurrentUser();
        }
    };
    private final StatusBarWindowCallback mStatusBarWindowCallback = new StatusBarWindowCallback() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$b7uhSpdl46tRQQQT8ZW7Bieyg6A
        @Override // com.android.systemui.statusbar.phone.StatusBarWindowCallback
        public final void onStateChanged(boolean z, boolean z2, boolean z3) {
            OverviewProxyService.this.onStatusBarStateChanged(z, z2, z3);
        }
    };
    private final IBinder.DeathRecipient mOverviewServiceDeathRcpt = new IBinder.DeathRecipient() { // from class: com.android.systemui.recents.-$$Lambda$FF1twVzMKp_FAsQO2IsbqUbCb-s
        @Override // android.os.IBinder.DeathRecipient
        public final void binderDied() {
            OverviewProxyService.this.cleanupAfterDeath();
        }
    };
    private final Handler mHandler = new Handler();
    private int mConnectionBackoffAttempts = 0;
    private float mNavBarButtonAlpha = 1.0f;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.recents.OverviewProxyService$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 extends ISystemUiProxy.Stub {
        AnonymousClass1() {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void startScreenPinning(final int taskId) {
            if (!verifyCaller("startScreenPinning")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                OverviewProxyService.this.mHandler.post(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$1$gf7wUE1qIRz6uZ2klN6hqV9xFfM
                    @Override // java.lang.Runnable
                    public final void run() {
                        OverviewProxyService.AnonymousClass1.this.lambda$startScreenPinning$0$OverviewProxyService$1(taskId);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public /* synthetic */ void lambda$startScreenPinning$0$OverviewProxyService$1(int taskId) {
            StatusBar statusBar = (StatusBar) SysUiServiceProvider.getComponent(OverviewProxyService.this.mContext, StatusBar.class);
            if (statusBar != null) {
                statusBar.showScreenPinningRequest(taskId, false);
            }
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void stopScreenPinning() {
            if (!verifyCaller("stopScreenPinning")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                OverviewProxyService.this.mHandler.post(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$1$WjFAUijOf0iWbjyxz5nDkhLz-xA
                    @Override // java.lang.Runnable
                    public final void run() {
                        OverviewProxyService.AnonymousClass1.lambda$stopScreenPinning$1();
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$stopScreenPinning$1() {
            try {
                ActivityTaskManager.getService().stopSystemLockTaskMode();
            } catch (RemoteException e) {
                Log.e(OverviewProxyService.TAG_OPS, "Failed to stop screen pinning");
            }
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onStatusBarMotionEvent(final MotionEvent event) {
            if (!verifyCaller("onStatusBarMotionEvent")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                OverviewProxyService.this.mHandler.post(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$1$2LCvhYvor9KUdkD8Lozm_8CbJlE
                    @Override // java.lang.Runnable
                    public final void run() {
                        OverviewProxyService.AnonymousClass1.this.lambda$onStatusBarMotionEvent$2$OverviewProxyService$1(event);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public /* synthetic */ void lambda$onStatusBarMotionEvent$2$OverviewProxyService$1(MotionEvent event) {
            StatusBar bar = (StatusBar) SysUiServiceProvider.getComponent(OverviewProxyService.this.mContext, StatusBar.class);
            if (bar != null) {
                int action = event.getActionMasked();
                if (action == 0) {
                    OverviewProxyService.this.mInputFocusTransferStarted = true;
                    OverviewProxyService.this.mInputFocusTransferStartY = event.getY();
                    OverviewProxyService.this.mInputFocusTransferStartMillis = event.getEventTime();
                    bar.onInputFocusTransfer(OverviewProxyService.this.mInputFocusTransferStarted, 0.0f);
                }
                if (action == 1 || action == 3) {
                    OverviewProxyService.this.mInputFocusTransferStarted = false;
                    bar.onInputFocusTransfer(OverviewProxyService.this.mInputFocusTransferStarted, (event.getY() - OverviewProxyService.this.mInputFocusTransferStartY) / ((float) (event.getEventTime() - OverviewProxyService.this.mInputFocusTransferStartMillis)));
                }
                event.recycle();
            }
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onSplitScreenInvoked() {
            if (!verifyCaller("onSplitScreenInvoked")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                Divider divider = (Divider) SysUiServiceProvider.getComponent(OverviewProxyService.this.mContext, Divider.class);
                if (divider != null) {
                    divider.onDockedFirstAnimationFrame();
                }
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onOverviewShown(final boolean fromHome) {
            if (!verifyCaller("onOverviewShown")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                OverviewProxyService.this.mHandler.post(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$1$o_Nvl9rNrEnvxnQlEkJ_hCsmmfI
                    @Override // java.lang.Runnable
                    public final void run() {
                        OverviewProxyService.AnonymousClass1.this.lambda$onOverviewShown$3$OverviewProxyService$1(fromHome);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public /* synthetic */ void lambda$onOverviewShown$3$OverviewProxyService$1(boolean fromHome) {
            for (int i = OverviewProxyService.this.mConnectionCallbacks.size() - 1; i >= 0; i--) {
                ((OverviewProxyListener) OverviewProxyService.this.mConnectionCallbacks.get(i)).onOverviewShown(fromHome);
            }
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public Rect getNonMinimizedSplitScreenSecondaryBounds() {
            if (verifyCaller("getNonMinimizedSplitScreenSecondaryBounds")) {
                long token = Binder.clearCallingIdentity();
                try {
                    Divider divider = (Divider) SysUiServiceProvider.getComponent(OverviewProxyService.this.mContext, Divider.class);
                    if (divider != null) {
                        return divider.getView().getNonMinimizedSplitScreenSecondaryBounds();
                    }
                    return null;
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
            return null;
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void setNavBarButtonAlpha(final float alpha, final boolean animate) {
            if (!verifyCaller("setNavBarButtonAlpha")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                OverviewProxyService.this.mNavBarButtonAlpha = alpha;
                OverviewProxyService.this.mHandler.post(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$1$dMl_i-5aLm6UXcbb7W1OvCrMhAM
                    @Override // java.lang.Runnable
                    public final void run() {
                        OverviewProxyService.AnonymousClass1.this.lambda$setNavBarButtonAlpha$4$OverviewProxyService$1(alpha, animate);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public /* synthetic */ void lambda$setNavBarButtonAlpha$4$OverviewProxyService$1(float alpha, boolean animate) {
            OverviewProxyService.this.notifyNavBarButtonAlphaChanged(alpha, animate);
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void setBackButtonAlpha(float alpha, boolean animate) {
            setNavBarButtonAlpha(alpha, animate);
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onAssistantProgress(final float progress) {
            if (!verifyCaller("onAssistantProgress")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                OverviewProxyService.this.mHandler.post(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$1$HkSs0hra3pArt93DbnvuWmlgXvE
                    @Override // java.lang.Runnable
                    public final void run() {
                        OverviewProxyService.AnonymousClass1.this.lambda$onAssistantProgress$5$OverviewProxyService$1(progress);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public /* synthetic */ void lambda$onAssistantProgress$5$OverviewProxyService$1(float progress) {
            OverviewProxyService.this.notifyAssistantProgress(progress);
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onAssistantGestureCompletion(final float velocity) {
            if (!verifyCaller("onAssistantGestureCompletion")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                OverviewProxyService.this.mHandler.post(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$1$tM0ScuR2E6sp4f29_cYFeTNI-IA
                    @Override // java.lang.Runnable
                    public final void run() {
                        OverviewProxyService.AnonymousClass1.this.lambda$onAssistantGestureCompletion$6$OverviewProxyService$1(velocity);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public /* synthetic */ void lambda$onAssistantGestureCompletion$6$OverviewProxyService$1(float velocity) {
            OverviewProxyService.this.notifyAssistantGestureCompletion(velocity);
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void startAssistant(final Bundle bundle) {
            if (!verifyCaller("startAssistant")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                OverviewProxyService.this.mHandler.post(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$1$lzBDCxnJTcDNIZHX5ISOsfuKOL0
                    @Override // java.lang.Runnable
                    public final void run() {
                        OverviewProxyService.AnonymousClass1.this.lambda$startAssistant$7$OverviewProxyService$1(bundle);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public /* synthetic */ void lambda$startAssistant$7$OverviewProxyService$1(Bundle bundle) {
            OverviewProxyService.this.notifyStartAssistant(bundle);
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public Bundle monitorGestureInput(String name, int displayId) {
            if (!verifyCaller("monitorGestureInput")) {
                return null;
            }
            long token = Binder.clearCallingIdentity();
            try {
                Parcelable monitorGestureInput = InputManager.getInstance().monitorGestureInput(name, displayId);
                Bundle result = new Bundle();
                result.putParcelable(QuickStepContract.KEY_EXTRA_INPUT_MONITOR, monitorGestureInput);
                return result;
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void notifyAccessibilityButtonClicked(int displayId) {
            if (!verifyCaller("notifyAccessibilityButtonClicked")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                AccessibilityManager.getInstance(OverviewProxyService.this.mContext).notifyAccessibilityButtonClicked(displayId);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void notifyAccessibilityButtonLongClicked() {
            if (!verifyCaller("notifyAccessibilityButtonLongClicked")) {
                return;
            }
            long token = Binder.clearCallingIdentity();
            try {
                Intent intent = new Intent("com.android.internal.intent.action.CHOOSE_ACCESSIBILITY_BUTTON");
                intent.addFlags(268468224);
                OverviewProxyService.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        private boolean verifyCaller(String reason) {
            int callerId = Binder.getCallingUserHandle().getIdentifier();
            if (callerId != OverviewProxyService.this.mCurrentBoundedUserId) {
                Log.w(OverviewProxyService.TAG_OPS, "Launcher called sysui with invalid user: " + callerId + ", reason: " + reason);
                return false;
            }
            return true;
        }
    }

    public /* synthetic */ void lambda$new$0$OverviewProxyService() {
        Log.w(TAG_OPS, "Binder supposed established connection but actual connection to service timed out, trying again");
        retryConnectionWithBackoff();
    }

    @Inject
    public OverviewProxyService(Context context, DeviceProvisionedController provisionController, NavigationBarController navBarController, NavigationModeController navModeController, StatusBarWindowController statusBarWinController) {
        this.mNavBarMode = 0;
        this.mContext = context;
        this.mNavBarController = navBarController;
        this.mStatusBarWinController = statusBarWinController;
        this.mDeviceProvisionedController = provisionController;
        this.mRecentsComponentName = ComponentName.unflattenFromString(context.getString(17039774));
        this.mQuickStepIntent = new Intent(ACTION_QUICKSTEP).setPackage(this.mRecentsComponentName.getPackageName());
        this.mWindowCornerRadius = ScreenDecorationsUtils.getWindowCornerRadius(this.mContext.getResources());
        this.mSupportsRoundedCornersOnWindows = ScreenDecorationsUtils.supportsRoundedCornersOnWindows(this.mContext.getResources());
        this.mNavBarMode = navModeController.addListener(this);
        updateEnabledState();
        this.mDeviceProvisionedController.addCallback(this.mDeviceProvisionedCallback);
        IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        filter.addDataScheme("package");
        filter.addDataSchemeSpecificPart(this.mRecentsComponentName.getPackageName(), 0);
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        this.mContext.registerReceiver(this.mLauncherStateChangedReceiver, filter);
        statusBarWinController.registerCallback(this.mStatusBarWindowCallback);
    }

    public void notifyBackAction(boolean completed, int downX, int downY, boolean isButton, boolean gestureSwipeLeft) {
        try {
            if (this.mOverviewProxy != null) {
                this.mOverviewProxy.onBackAction(completed, downX, downY, isButton, gestureSwipeLeft);
            }
        } catch (RemoteException e) {
            Log.e(TAG_OPS, "Failed to notify back action", e);
        }
    }

    public void setSystemUiStateFlag(int flag, boolean enabled, int displayId) {
        int newState;
        if (displayId != 0) {
            return;
        }
        int newState2 = this.mSysUiStateFlags;
        if (enabled) {
            newState = newState2 | flag;
        } else {
            newState = newState2 & (~flag);
        }
        if (this.mSysUiStateFlags != newState) {
            this.mSysUiStateFlags = newState;
            notifySystemUiStateChanged(this.mSysUiStateFlags);
            notifySystemUiStateFlags(this.mSysUiStateFlags);
        }
    }

    public int getSystemUiStateFlags() {
        return this.mSysUiStateFlags;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSystemUiStateFlags() {
        NavigationBarFragment navBarFragment = this.mNavBarController.getDefaultNavigationBarFragment();
        NavigationBarView navBarView = this.mNavBarController.getNavigationBarView(this.mContext.getDisplayId());
        this.mSysUiStateFlags = 0;
        if (navBarFragment != null) {
            navBarFragment.updateSystemUiStateFlags(-1);
        }
        if (navBarView != null) {
            navBarView.updatePanelSystemUiStateFlags();
            navBarView.updateDisabledSystemUiStateFlags();
        }
        StatusBarWindowController statusBarWindowController = this.mStatusBarWinController;
        if (statusBarWindowController != null) {
            statusBarWindowController.notifyStateChangedCallbacks();
        }
        notifySystemUiStateFlags(this.mSysUiStateFlags);
    }

    private void notifySystemUiStateFlags(int flags) {
        try {
            if (this.mOverviewProxy != null) {
                this.mOverviewProxy.onSystemUiStateChanged(flags);
            }
        } catch (RemoteException e) {
            Log.e(TAG_OPS, "Failed to notify sysui state change", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStatusBarStateChanged(boolean keyguardShowing, boolean keyguardOccluded, boolean bouncerShowing) {
        int displayId = this.mContext.getDisplayId();
        boolean z = true;
        setSystemUiStateFlag(64, keyguardShowing && !keyguardOccluded, displayId);
        if (!keyguardShowing || !keyguardOccluded) {
            z = false;
        }
        setSystemUiStateFlag(512, z, displayId);
        setSystemUiStateFlag(8, bouncerShowing, displayId);
    }

    public void onActiveNavBarRegionChanges(Region activeRegion) {
        this.mActiveNavBarRegion = activeRegion;
        dispatchNavButtonBounds();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchNavButtonBounds() {
        Region region;
        IOverviewProxy iOverviewProxy = this.mOverviewProxy;
        if (iOverviewProxy != null && (region = this.mActiveNavBarRegion) != null) {
            try {
                iOverviewProxy.onActiveNavBarRegionChanges(region);
            } catch (RemoteException e) {
                Log.e(TAG_OPS, "Failed to call onActiveNavBarRegionChanges()", e);
            }
        }
    }

    public float getBackButtonAlpha() {
        return this.mNavBarButtonAlpha;
    }

    public void cleanupAfterDeath() {
        if (this.mInputFocusTransferStarted) {
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$OverviewProxyService$eCc1ukzT5yAkIaX2QrSVYaZKqYk
                @Override // java.lang.Runnable
                public final void run() {
                    OverviewProxyService.this.lambda$cleanupAfterDeath$1$OverviewProxyService();
                }
            });
        }
        startConnectionToCurrentUser();
    }

    public /* synthetic */ void lambda$cleanupAfterDeath$1$OverviewProxyService() {
        StatusBar bar = (StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
        if (bar != null) {
            this.mInputFocusTransferStarted = false;
            bar.onInputFocusTransfer(false, 0.0f);
        }
    }

    public void startConnectionToCurrentUser() {
        if (this.mHandler.getLooper() != Looper.myLooper()) {
            this.mHandler.post(this.mConnectionRunnable);
        } else {
            internalConnectToCurrentUser();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void internalConnectToCurrentUser() {
        disconnectFromLauncherService();
        if (!this.mDeviceProvisionedController.isCurrentUserSetup() || !isEnabled()) {
            Log.v(TAG_OPS, "Cannot attempt connection, is setup " + this.mDeviceProvisionedController.isCurrentUserSetup() + ", is enabled " + isEnabled());
            return;
        }
        this.mHandler.removeCallbacks(this.mConnectionRunnable);
        Intent launcherServiceIntent = new Intent(ACTION_QUICKSTEP).setPackage(this.mRecentsComponentName.getPackageName());
        try {
            this.mBound = this.mContext.bindServiceAsUser(launcherServiceIntent, this.mOverviewServiceConnection, 33554433, UserHandle.of(this.mDeviceProvisionedController.getCurrentUser()));
        } catch (SecurityException e) {
            Log.e(TAG_OPS, "Unable to bind because of security error", e);
        }
        if (this.mBound) {
            this.mHandler.postDelayed(this.mDeferredConnectionCallback, DEFERRED_CALLBACK_MILLIS);
        } else {
            retryConnectionWithBackoff();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void retryConnectionWithBackoff() {
        if (this.mHandler.hasCallbacks(this.mConnectionRunnable)) {
            return;
        }
        long timeoutMs = Math.min(Math.scalb(1000.0f, this.mConnectionBackoffAttempts), 600000.0f);
        this.mHandler.postDelayed(this.mConnectionRunnable, timeoutMs);
        this.mConnectionBackoffAttempts++;
        Log.w(TAG_OPS, "Failed to connect on attempt " + this.mConnectionBackoffAttempts + " will try again in " + timeoutMs + "ms");
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(OverviewProxyListener listener) {
        this.mConnectionCallbacks.add(listener);
        listener.onConnectionChanged(this.mOverviewProxy != null);
        listener.onNavBarButtonAlphaChanged(this.mNavBarButtonAlpha, false);
        listener.onSystemUiStateChanged(this.mSysUiStateFlags);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(OverviewProxyListener listener) {
        this.mConnectionCallbacks.remove(listener);
    }

    public boolean shouldShowSwipeUpUI() {
        return isEnabled() && !QuickStepContract.isLegacyMode(this.mNavBarMode);
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public IOverviewProxy getProxy() {
        return this.mOverviewProxy;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disconnectFromLauncherService() {
        if (this.mBound) {
            this.mContext.unbindService(this.mOverviewServiceConnection);
            this.mBound = false;
        }
        IOverviewProxy iOverviewProxy = this.mOverviewProxy;
        if (iOverviewProxy != null) {
            iOverviewProxy.asBinder().unlinkToDeath(this.mOverviewServiceDeathRcpt, 0);
            this.mOverviewProxy = null;
            notifyNavBarButtonAlphaChanged(1.0f, false);
            notifyConnectionChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyNavBarButtonAlphaChanged(float alpha, boolean animate) {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; i--) {
            this.mConnectionCallbacks.get(i).onNavBarButtonAlphaChanged(alpha, animate);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyConnectionChanged() {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; i--) {
            this.mConnectionCallbacks.get(i).onConnectionChanged(this.mOverviewProxy != null);
        }
    }

    public void notifyQuickStepStarted() {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; i--) {
            this.mConnectionCallbacks.get(i).onQuickStepStarted();
        }
    }

    public void notifyQuickScrubStarted() {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; i--) {
            this.mConnectionCallbacks.get(i).onQuickScrubStarted();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAssistantProgress(float progress) {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; i--) {
            this.mConnectionCallbacks.get(i).onAssistantProgress(progress);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyAssistantGestureCompletion(float velocity) {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; i--) {
            this.mConnectionCallbacks.get(i).onAssistantGestureCompletion(velocity);
        }
    }

    private void notifySystemUiStateChanged(int sysuiStateFlags) {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; i--) {
            this.mConnectionCallbacks.get(i).onSystemUiStateChanged(sysuiStateFlags);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyStartAssistant(Bundle bundle) {
        for (int i = this.mConnectionCallbacks.size() - 1; i >= 0; i--) {
            this.mConnectionCallbacks.get(i).startAssistant(bundle);
        }
    }

    public void notifyAssistantVisibilityChanged(float visibility) {
        try {
            if (this.mOverviewProxy == null) {
                Log.e(TAG_OPS, "Failed to get overview proxy for assistant visibility.");
            } else {
                this.mOverviewProxy.onAssistantVisibilityChanged(visibility);
            }
        } catch (RemoteException e) {
            Log.e(TAG_OPS, "Failed to call onAssistantVisibilityChanged()", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEnabledState() {
        this.mIsEnabled = this.mContext.getPackageManager().resolveServiceAsUser(this.mQuickStepIntent, 1048576, ActivityManagerWrapper.getInstance().getCurrentUserId()) != null;
    }

    @Override // com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener
    public void onNavigationModeChanged(int mode) {
        this.mNavBarMode = mode;
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("OverviewProxyService state:");
        pw.print("  recentsComponentName=");
        pw.println(this.mRecentsComponentName);
        pw.print("  isConnected=");
        pw.println(this.mOverviewProxy != null);
        pw.print("  isCurrentUserSetup=");
        pw.println(this.mDeviceProvisionedController.isCurrentUserSetup());
        pw.print("  connectionBackoffAttempts=");
        pw.println(this.mConnectionBackoffAttempts);
        pw.print("  quickStepIntent=");
        pw.println(this.mQuickStepIntent);
        pw.print("  quickStepIntentResolved=");
        pw.println(isEnabled());
        pw.print("  mSysUiStateFlags=");
        pw.println(this.mSysUiStateFlags);
        pw.println("    " + QuickStepContract.getSystemUiStateString(this.mSysUiStateFlags));
        pw.print("    backGestureDisabled=");
        pw.println(QuickStepContract.isBackGestureDisabled(this.mSysUiStateFlags));
        pw.print("    assistantGestureDisabled=");
        pw.println(QuickStepContract.isAssistantGestureDisabled(this.mSysUiStateFlags));
        pw.print(" mInputFocusTransferStarted=");
        pw.println(this.mInputFocusTransferStarted);
    }

    /* loaded from: classes21.dex */
    public interface OverviewProxyListener {
        default void onConnectionChanged(boolean isConnected) {
        }

        default void onQuickStepStarted() {
        }

        default void onOverviewShown(boolean fromHome) {
        }

        default void onQuickScrubStarted() {
        }

        default void onNavBarButtonAlphaChanged(float alpha, boolean animate) {
        }

        default void onSystemUiStateChanged(int sysuiStateFlags) {
        }

        default void onAssistantProgress(float progress) {
        }

        default void onAssistantGestureCompletion(float velocity) {
        }

        default void startAssistant(Bundle bundle) {
        }
    }
}
