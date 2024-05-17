package com.android.keyguard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.IccCardConstants;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.controller.CarController;
/* loaded from: classes19.dex */
public class KeyguardSimPinView extends KeyguardPinBasedInputView {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "KeyguardSimPinView";
    public static final String TAG = "KeyguardSimPinView";
    private CheckSimPin mCheckSimPinThread;
    private int mRemainingAttempts;
    private AlertDialog mRemainingAttemptsDialog;
    private boolean mShowDefaultMessage;
    private ImageView mSimImageView;
    private ProgressDialog mSimUnlockProgressDialog;
    private int mSubId;
    KeyguardUpdateMonitorCallback mUpdateMonitorCallback;

    /* renamed from: com.android.keyguard.KeyguardSimPinView$4  reason: invalid class name */
    /* loaded from: classes19.dex */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$IccCardConstants$State = new int[IccCardConstants.State.values().length];

        static {
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.READY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public KeyguardSimPinView(Context context) {
        this(context, null);
    }

    public KeyguardSimPinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSimUnlockProgressDialog = null;
        this.mShowDefaultMessage = true;
        this.mRemainingAttempts = -1;
        this.mSubId = -1;
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.keyguard.KeyguardSimPinView.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onSimStateChanged(int subId, int slotId, IccCardConstants.State simState) {
                Log.v("KeyguardSimPinView", "onSimStateChanged(subId=" + subId + ",state=" + simState + NavigationBarInflaterView.KEY_CODE_END);
                if (AnonymousClass4.$SwitchMap$com$android$internal$telephony$IccCardConstants$State[simState.ordinal()] == 1) {
                    KeyguardSimPinView.this.mRemainingAttempts = -1;
                    KeyguardSimPinView.this.resetState();
                    return;
                }
                KeyguardSimPinView.this.resetState();
            }
        };
    }

    @Override // com.android.keyguard.KeyguardPinBasedInputView, com.android.keyguard.KeyguardAbsKeyInputView
    public void resetState() {
        super.resetState();
        Log.v("KeyguardSimPinView", "Resetting state");
        handleSubInfoChangeIfNeeded();
        if (this.mShowDefaultMessage) {
            showDefaultMessage();
        }
        boolean isEsimLocked = KeyguardEsimArea.isEsimLocked(this.mContext, this.mSubId);
        KeyguardEsimArea esimButton = (KeyguardEsimArea) findViewById(R.id.keyguard_esim_area);
        esimButton.setVisibility(isEsimLocked ? 0 : 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLockedSimMessage() {
        String msg;
        boolean isEsimLocked = KeyguardEsimArea.isEsimLocked(this.mContext, this.mSubId);
        int count = TelephonyManager.getDefault().getSimCount();
        Resources rez = getResources();
        TypedArray array = this.mContext.obtainStyledAttributes(new int[]{R.attr.wallpaperTextColor});
        int color = array.getColor(0, -1);
        array.recycle();
        if (count < 2) {
            msg = rez.getString(R.string.kg_sim_pin_instructions);
        } else {
            SubscriptionInfo info = KeyguardUpdateMonitor.getInstance(this.mContext).getSubscriptionInfoForSubId(this.mSubId);
            String displayName = info != null ? info.getDisplayName() : "";
            msg = rez.getString(R.string.kg_sim_pin_instructions_multi, displayName);
            if (info != null) {
                color = info.getIconTint();
            }
        }
        if (isEsimLocked) {
            msg = rez.getString(R.string.kg_sim_lock_esim_instructions, msg);
        }
        if (this.mSecurityMessageDisplay != null && getVisibility() == 0) {
            this.mSecurityMessageDisplay.setMessage(msg);
        }
        this.mSimImageView.setImageTintList(ColorStateList.valueOf(color));
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [com.android.keyguard.KeyguardSimPinView$2] */
    private void showDefaultMessage() {
        setLockedSimMessage();
        if (this.mRemainingAttempts >= 0) {
            return;
        }
        new CheckSimPin("", this.mSubId) { // from class: com.android.keyguard.KeyguardSimPinView.2
            @Override // com.android.keyguard.KeyguardSimPinView.CheckSimPin
            void onSimCheckResponse(int result, int attemptsRemaining) {
                Log.d("KeyguardSimPinView", "onSimCheckResponse  dummy One result" + result + " attemptsRemaining=" + attemptsRemaining);
                if (attemptsRemaining >= 0) {
                    KeyguardSimPinView.this.mRemainingAttempts = attemptsRemaining;
                    KeyguardSimPinView.this.setLockedSimMessage();
                }
            }
        }.start();
    }

    private void handleSubInfoChangeIfNeeded() {
        KeyguardUpdateMonitor monitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        int subId = monitor.getNextSubIdForState(IccCardConstants.State.PIN_REQUIRED);
        if (subId != this.mSubId && SubscriptionManager.isValidSubscriptionId(subId)) {
            this.mSubId = subId;
            this.mShowDefaultMessage = true;
            this.mRemainingAttempts = -1;
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetState();
    }

    @Override // com.android.keyguard.KeyguardPinBasedInputView, com.android.keyguard.KeyguardAbsKeyInputView
    protected int getPromptReasonStringRes(int reason) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getPinPasswordErrorMessage(int attemptsRemaining, boolean isDefault) {
        String displayMessage;
        if (attemptsRemaining == 0) {
            displayMessage = getContext().getString(R.string.kg_password_wrong_pin_code_pukked);
        } else if (attemptsRemaining > 0) {
            int msgId = isDefault ? R.plurals.kg_password_default_pin_message : R.plurals.kg_password_wrong_pin_code;
            displayMessage = getContext().getResources().getQuantityString(msgId, attemptsRemaining, Integer.valueOf(attemptsRemaining));
        } else {
            int msgId2 = isDefault ? R.string.kg_sim_pin_instructions : R.string.kg_password_pin_failed;
            displayMessage = getContext().getString(msgId2);
        }
        if (KeyguardEsimArea.isEsimLocked(this.mContext, this.mSubId)) {
            displayMessage = getResources().getString(R.string.kg_sim_lock_esim_instructions, displayMessage);
        }
        Log.d("KeyguardSimPinView", "getPinPasswordErrorMessage: attemptsRemaining=" + attemptsRemaining + " displayMessage=" + displayMessage);
        return displayMessage;
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected boolean shouldLockout(long deadline) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    public int getPasswordTextViewId() {
        return R.id.simPinEntry;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardPinBasedInputView, com.android.keyguard.KeyguardAbsKeyInputView, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        if (this.mEcaView instanceof EmergencyCarrierArea) {
            ((EmergencyCarrierArea) this.mEcaView).setCarrierTextVisible(true);
        }
        this.mSimImageView = (ImageView) findViewById(R.id.keyguard_sim);
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showUsabilityHint() {
    }

    @Override // com.android.keyguard.KeyguardPinBasedInputView, com.android.keyguard.KeyguardAbsKeyInputView, com.android.keyguard.KeyguardSecurityView
    public void onResume(int reason) {
        super.onResume(reason);
        KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mUpdateMonitorCallback);
        resetState();
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView, com.android.keyguard.KeyguardSecurityView
    public void onPause() {
        ProgressDialog progressDialog = this.mSimUnlockProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.mSimUnlockProgressDialog = null;
        }
        KeyguardUpdateMonitor.getInstance(this.mContext).removeCallback(this.mUpdateMonitorCallback);
    }

    /* loaded from: classes19.dex */
    private abstract class CheckSimPin extends Thread {
        private final String mPin;
        private int mSubId;

        abstract void onSimCheckResponse(int i, int i2);

        protected CheckSimPin(String pin, int subId) {
            this.mPin = pin;
            this.mSubId = subId;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                Log.v("KeyguardSimPinView", "call supplyPinReportResultForSubscriber(subid=" + this.mSubId + NavigationBarInflaterView.KEY_CODE_END);
                final int[] result = ITelephony.Stub.asInterface(ServiceManager.checkService("phone")).supplyPinReportResultForSubscriber(this.mSubId, this.mPin);
                Log.v("KeyguardSimPinView", "supplyPinReportResult returned: " + result[0] + " " + result[1]);
                KeyguardSimPinView.this.post(new Runnable() { // from class: com.android.keyguard.KeyguardSimPinView.CheckSimPin.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CheckSimPin checkSimPin = CheckSimPin.this;
                        int[] iArr = result;
                        checkSimPin.onSimCheckResponse(iArr[0], iArr[1]);
                    }
                });
            } catch (RemoteException e) {
                Log.e("KeyguardSimPinView", "RemoteException for supplyPinReportResult:", e);
                KeyguardSimPinView.this.post(new Runnable() { // from class: com.android.keyguard.KeyguardSimPinView.CheckSimPin.2
                    @Override // java.lang.Runnable
                    public void run() {
                        CheckSimPin.this.onSimCheckResponse(2, -1);
                    }
                });
            }
        }
    }

    private Dialog getSimUnlockProgressDialog() {
        if (this.mSimUnlockProgressDialog == null) {
            this.mSimUnlockProgressDialog = new ProgressDialog(this.mContext);
            this.mSimUnlockProgressDialog.setMessage(this.mContext.getString(R.string.kg_sim_unlock_progress_dialog_message));
            this.mSimUnlockProgressDialog.setIndeterminate(true);
            this.mSimUnlockProgressDialog.setCancelable(false);
            this.mSimUnlockProgressDialog.getWindow().setType(CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE);
        }
        return this.mSimUnlockProgressDialog;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Dialog getSimRemainingAttemptsDialog(int remaining) {
        String msg = getPinPasswordErrorMessage(remaining, false);
        AlertDialog alertDialog = this.mRemainingAttemptsDialog;
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setMessage(msg);
            builder.setCancelable(false);
            builder.setNeutralButton(R.string.ok, (DialogInterface.OnClickListener) null);
            this.mRemainingAttemptsDialog = builder.create();
            this.mRemainingAttemptsDialog.getWindow().setType(CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE);
        } else {
            alertDialog.setMessage(msg);
        }
        return this.mRemainingAttemptsDialog;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    public void verifyPasswordAndUnlock() {
        String entry = this.mPasswordEntry.getText();
        if (entry.length() < 4) {
            this.mSecurityMessageDisplay.setMessage(R.string.kg_invalid_sim_pin_hint);
            resetPasswordText(true, true);
            this.mCallback.userActivity();
            return;
        }
        getSimUnlockProgressDialog().show();
        if (this.mCheckSimPinThread == null) {
            this.mCheckSimPinThread = new CheckSimPin(this.mPasswordEntry.getText(), this.mSubId) { // from class: com.android.keyguard.KeyguardSimPinView.3
                @Override // com.android.keyguard.KeyguardSimPinView.CheckSimPin
                void onSimCheckResponse(final int result, final int attemptsRemaining) {
                    KeyguardSimPinView.this.post(new Runnable() { // from class: com.android.keyguard.KeyguardSimPinView.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            KeyguardSimPinView.this.mRemainingAttempts = attemptsRemaining;
                            if (KeyguardSimPinView.this.mSimUnlockProgressDialog != null) {
                                KeyguardSimPinView.this.mSimUnlockProgressDialog.hide();
                            }
                            KeyguardSimPinView.this.resetPasswordText(true, result != 0);
                            if (result == 0) {
                                KeyguardUpdateMonitor.getInstance(KeyguardSimPinView.this.getContext()).reportSimUnlocked(KeyguardSimPinView.this.mSubId);
                                KeyguardSimPinView.this.mRemainingAttempts = -1;
                                KeyguardSimPinView.this.mShowDefaultMessage = true;
                                if (KeyguardSimPinView.this.mCallback != null) {
                                    KeyguardSimPinView.this.mCallback.dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                                }
                            } else {
                                KeyguardSimPinView.this.mShowDefaultMessage = false;
                                if (result == 1) {
                                    if (attemptsRemaining <= 2) {
                                        KeyguardSimPinView.this.getSimRemainingAttemptsDialog(attemptsRemaining).show();
                                    } else {
                                        KeyguardSimPinView.this.mSecurityMessageDisplay.setMessage(KeyguardSimPinView.this.getPinPasswordErrorMessage(attemptsRemaining, false));
                                    }
                                } else {
                                    KeyguardSimPinView.this.mSecurityMessageDisplay.setMessage(KeyguardSimPinView.this.getContext().getString(R.string.kg_password_pin_failed));
                                }
                                Log.d("KeyguardSimPinView", "verifyPasswordAndUnlock  CheckSimPin.onSimCheckResponse: " + result + " attemptsRemaining=" + attemptsRemaining);
                            }
                            KeyguardSimPinView.this.mCallback.userActivity();
                            KeyguardSimPinView.this.mCheckSimPinThread = null;
                        }
                    });
                }
            };
            this.mCheckSimPinThread.start();
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void startAppearAnimation() {
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView, com.android.keyguard.KeyguardSecurityView
    public boolean startDisappearAnimation(Runnable finishRunnable) {
        return false;
    }

    @Override // com.android.keyguard.KeyguardPinBasedInputView, com.android.keyguard.KeyguardSecurityView
    public CharSequence getTitle() {
        return getContext().getString(17040178);
    }
}
