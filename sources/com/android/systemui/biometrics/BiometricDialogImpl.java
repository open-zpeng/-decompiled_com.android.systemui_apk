package com.android.systemui.biometrics;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;
import com.android.internal.os.SomeArgs;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUI;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.statusbar.CommandQueue;
import com.xiaopeng.systemui.controller.OsdController;
/* loaded from: classes21.dex */
public class BiometricDialogImpl extends SystemUI implements CommandQueue.Callbacks {
    private static final boolean DEBUG = true;
    private static final int MSG_BIOMETRIC_AUTHENTICATED = 2;
    private static final int MSG_BIOMETRIC_ERROR = 4;
    private static final int MSG_BIOMETRIC_HELP = 3;
    private static final int MSG_BUTTON_NEGATIVE = 6;
    private static final int MSG_BUTTON_POSITIVE = 8;
    private static final int MSG_HIDE_DIALOG = 5;
    private static final int MSG_SHOW_DIALOG = 1;
    private static final int MSG_TRY_AGAIN_PRESSED = 9;
    private static final int MSG_USER_CANCELED = 7;
    private static final String TAG = "BiometricDialogImpl";
    private BiometricDialogView mCurrentDialog;
    private SomeArgs mCurrentDialogArgs;
    private boolean mDialogShowing;
    private IBiometricServiceReceiverInternal mReceiver;
    private WakefulnessLifecycle mWakefulnessLifecycle;
    private WindowManager mWindowManager;
    private Callback mCallback = new Callback();
    private Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.systemui.biometrics.BiometricDialogImpl.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    BiometricDialogImpl.this.handleShowDialog((SomeArgs) msg.obj, false, null);
                    return;
                case 2:
                    SomeArgs args = (SomeArgs) msg.obj;
                    BiometricDialogImpl.this.handleBiometricAuthenticated(((Boolean) args.arg1).booleanValue(), (String) args.arg2);
                    args.recycle();
                    return;
                case 3:
                    SomeArgs args2 = (SomeArgs) msg.obj;
                    BiometricDialogImpl.this.handleBiometricHelp((String) args2.arg1);
                    args2.recycle();
                    return;
                case 4:
                    BiometricDialogImpl.this.handleBiometricError((String) msg.obj);
                    return;
                case 5:
                    BiometricDialogImpl.this.handleHideDialog(((Boolean) msg.obj).booleanValue());
                    return;
                case 6:
                    BiometricDialogImpl.this.handleButtonNegative();
                    return;
                case 7:
                    BiometricDialogImpl.this.handleUserCanceled();
                    return;
                case 8:
                    BiometricDialogImpl.this.handleButtonPositive();
                    return;
                case 9:
                    BiometricDialogImpl.this.handleTryAgainPressed();
                    return;
                default:
                    Log.w(BiometricDialogImpl.TAG, "Unknown message: " + msg.what);
                    return;
            }
        }
    };
    final WakefulnessLifecycle.Observer mWakefulnessObserver = new WakefulnessLifecycle.Observer() { // from class: com.android.systemui.biometrics.BiometricDialogImpl.2
        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onStartedGoingToSleep() {
            if (BiometricDialogImpl.this.mDialogShowing) {
                Log.d(BiometricDialogImpl.TAG, "User canceled due to screen off");
                BiometricDialogImpl.this.mHandler.obtainMessage(7).sendToTarget();
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class Callback implements DialogViewCallback {
        private Callback() {
        }

        @Override // com.android.systemui.biometrics.DialogViewCallback
        public void onUserCanceled() {
            BiometricDialogImpl.this.mHandler.obtainMessage(7).sendToTarget();
        }

        @Override // com.android.systemui.biometrics.DialogViewCallback
        public void onErrorShown() {
            BiometricDialogImpl.this.mHandler.sendMessageDelayed(BiometricDialogImpl.this.mHandler.obtainMessage(5, false), OsdController.TN.DURATION_TIMEOUT_SHORT);
        }

        @Override // com.android.systemui.biometrics.DialogViewCallback
        public void onNegativePressed() {
            BiometricDialogImpl.this.mHandler.obtainMessage(6).sendToTarget();
        }

        @Override // com.android.systemui.biometrics.DialogViewCallback
        public void onPositivePressed() {
            BiometricDialogImpl.this.mHandler.obtainMessage(8).sendToTarget();
        }

        @Override // com.android.systemui.biometrics.DialogViewCallback
        public void onTryAgainPressed() {
            BiometricDialogImpl.this.mHandler.obtainMessage(9).sendToTarget();
        }
    }

    @Override // com.android.systemui.SystemUI
    public void start() {
        PackageManager pm = this.mContext.getPackageManager();
        if (pm.hasSystemFeature("android.hardware.fingerprint") || pm.hasSystemFeature("android.hardware.biometrics.face") || pm.hasSystemFeature("android.hardware.biometrics.iris")) {
            ((CommandQueue) getComponent(CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
            this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
            this.mWakefulnessLifecycle = (WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class);
            this.mWakefulnessLifecycle.addObserver(this.mWakefulnessObserver);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void showBiometricDialog(Bundle bundle, IBiometricServiceReceiverInternal receiver, int type, boolean requireConfirmation, int userId) {
        Log.d(TAG, "showBiometricDialog, type: " + type + ", requireConfirmation: " + requireConfirmation);
        this.mHandler.removeMessages(4);
        this.mHandler.removeMessages(3);
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(5);
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = bundle;
        args.arg2 = receiver;
        args.argi1 = type;
        args.arg3 = Boolean.valueOf(requireConfirmation);
        args.argi2 = userId;
        this.mHandler.obtainMessage(1, args).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void onBiometricAuthenticated(boolean authenticated, String failureReason) {
        Log.d(TAG, "onBiometricAuthenticated: " + authenticated + " reason: " + failureReason);
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = Boolean.valueOf(authenticated);
        args.arg2 = failureReason;
        this.mHandler.obtainMessage(2, args).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void onBiometricHelp(String message) {
        Log.d(TAG, "onBiometricHelp: " + message);
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = message;
        this.mHandler.obtainMessage(3, args).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void onBiometricError(String error) {
        Log.d(TAG, "onBiometricError: " + error);
        this.mHandler.obtainMessage(4, error).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void hideBiometricDialog() {
        Log.d(TAG, "hideBiometricDialog");
        this.mHandler.obtainMessage(5, false).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleShowDialog(SomeArgs args, boolean skipAnimation, Bundle savedState) {
        BiometricDialogView newDialog;
        this.mCurrentDialogArgs = args;
        int type = args.argi1;
        if (type == 1) {
            newDialog = new FingerprintDialogView(this.mContext, this.mCallback);
        } else if (type == 4) {
            newDialog = new FaceDialogView(this.mContext, this.mCallback);
        } else {
            Log.e(TAG, "Unsupported type: " + type);
            return;
        }
        Log.d(TAG, "handleShowDialog,  savedState: " + savedState + " mCurrentDialog: " + this.mCurrentDialog + " newDialog: " + newDialog + " type: " + type);
        if (savedState != null) {
            newDialog.restoreState(savedState);
        } else {
            BiometricDialogView biometricDialogView = this.mCurrentDialog;
            if (biometricDialogView != null && this.mDialogShowing) {
                biometricDialogView.forceRemove();
            }
        }
        this.mReceiver = (IBiometricServiceReceiverInternal) args.arg2;
        newDialog.setBundle((Bundle) args.arg1);
        newDialog.setRequireConfirmation(((Boolean) args.arg3).booleanValue());
        newDialog.setUserId(args.argi2);
        newDialog.setSkipIntro(skipAnimation);
        this.mCurrentDialog = newDialog;
        WindowManager windowManager = this.mWindowManager;
        BiometricDialogView biometricDialogView2 = this.mCurrentDialog;
        windowManager.addView(biometricDialogView2, biometricDialogView2.getLayoutParams());
        this.mDialogShowing = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBiometricAuthenticated(boolean authenticated, String failureReason) {
        Log.d(TAG, "handleBiometricAuthenticated: " + authenticated);
        if (authenticated) {
            this.mCurrentDialog.announceForAccessibility(this.mContext.getResources().getText(this.mCurrentDialog.getAuthenticatedAccessibilityResourceId()));
            if (this.mCurrentDialog.requiresConfirmation()) {
                this.mCurrentDialog.updateState(3);
                return;
            }
            this.mCurrentDialog.updateState(4);
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.biometrics.-$$Lambda$BiometricDialogImpl$ClyZbr2Bp-ugYn9TuyRxsmSCP_U
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricDialogImpl.this.lambda$handleBiometricAuthenticated$0$BiometricDialogImpl();
                }
            }, this.mCurrentDialog.getDelayAfterAuthenticatedDurationMs());
            return;
        }
        this.mCurrentDialog.onAuthenticationFailed(failureReason);
    }

    public /* synthetic */ void lambda$handleBiometricAuthenticated$0$BiometricDialogImpl() {
        handleHideDialog(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBiometricHelp(String message) {
        Log.d(TAG, "handleBiometricHelp: " + message);
        this.mCurrentDialog.onHelpReceived(message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBiometricError(String error) {
        Log.d(TAG, "handleBiometricError: " + error);
        if (!this.mDialogShowing) {
            Log.d(TAG, "Dialog already dismissed");
        } else {
            this.mCurrentDialog.onErrorReceived(error);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHideDialog(boolean userCanceled) {
        Log.d(TAG, "handleHideDialog, userCanceled: " + userCanceled);
        if (!this.mDialogShowing) {
            Log.w(TAG, "Dialog already dismissed, userCanceled: " + userCanceled);
            return;
        }
        if (userCanceled) {
            try {
                this.mReceiver.onDialogDismissed(3);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException when hiding dialog", e);
            }
        }
        this.mReceiver = null;
        this.mDialogShowing = false;
        this.mCurrentDialog.startDismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleButtonNegative() {
        IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal = this.mReceiver;
        if (iBiometricServiceReceiverInternal == null) {
            Log.e(TAG, "Receiver is null");
            return;
        }
        try {
            iBiometricServiceReceiverInternal.onDialogDismissed(2);
        } catch (RemoteException e) {
            Log.e(TAG, "Remote exception when handling negative button", e);
        }
        handleHideDialog(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleButtonPositive() {
        IBiometricServiceReceiverInternal iBiometricServiceReceiverInternal = this.mReceiver;
        if (iBiometricServiceReceiverInternal == null) {
            Log.e(TAG, "Receiver is null");
            return;
        }
        try {
            iBiometricServiceReceiverInternal.onDialogDismissed(1);
        } catch (RemoteException e) {
            Log.e(TAG, "Remote exception when handling positive button", e);
        }
        handleHideDialog(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUserCanceled() {
        handleHideDialog(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTryAgainPressed() {
        try {
            this.mReceiver.onTryAgainPressed();
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException when handling try again", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean wasShowing = this.mDialogShowing;
        Bundle savedState = new Bundle();
        BiometricDialogView biometricDialogView = this.mCurrentDialog;
        if (biometricDialogView != null) {
            biometricDialogView.onSaveState(savedState);
        }
        if (this.mDialogShowing) {
            this.mCurrentDialog.forceRemove();
            this.mDialogShowing = false;
        }
        if (wasShowing) {
            handleShowDialog(this.mCurrentDialogArgs, true, savedState);
        }
    }
}
