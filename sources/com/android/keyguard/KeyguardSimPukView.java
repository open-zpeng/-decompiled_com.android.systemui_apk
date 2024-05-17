package com.android.keyguard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
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
import com.xiaopeng.systemui.controller.CarController;
/* loaded from: classes19.dex */
public class KeyguardSimPukView extends KeyguardPinBasedInputView {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "KeyguardSimPukView";
    public static final String TAG = "KeyguardSimPukView";
    private CheckSimPuk mCheckSimPukThread;
    private String mPinText;
    private String mPukText;
    private int mRemainingAttempts;
    private AlertDialog mRemainingAttemptsDialog;
    private boolean mShowDefaultMessage;
    private ImageView mSimImageView;
    private ProgressDialog mSimUnlockProgressDialog;
    private StateMachine mStateMachine;
    private int mSubId;
    KeyguardUpdateMonitorCallback mUpdateMonitorCallback;

    /* renamed from: com.android.keyguard.KeyguardSimPukView$4  reason: invalid class name */
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

    public KeyguardSimPukView(Context context) {
        this(context, null);
    }

    public KeyguardSimPukView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSimUnlockProgressDialog = null;
        this.mShowDefaultMessage = true;
        this.mRemainingAttempts = -1;
        this.mStateMachine = new StateMachine();
        this.mSubId = -1;
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.keyguard.KeyguardSimPukView.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onSimStateChanged(int subId, int slotId, IccCardConstants.State simState) {
                if (AnonymousClass4.$SwitchMap$com$android$internal$telephony$IccCardConstants$State[simState.ordinal()] == 1) {
                    KeyguardSimPukView.this.mRemainingAttempts = -1;
                    KeyguardSimPukView.this.mShowDefaultMessage = true;
                    if (KeyguardSimPukView.this.mCallback != null) {
                        KeyguardSimPukView.this.mCallback.dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                        return;
                    }
                    return;
                }
                KeyguardSimPukView.this.resetState();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes19.dex */
    public class StateMachine {
        final int CONFIRM_PIN;
        final int DONE;
        final int ENTER_PIN;
        final int ENTER_PUK;
        private int state;

        private StateMachine() {
            this.ENTER_PUK = 0;
            this.ENTER_PIN = 1;
            this.CONFIRM_PIN = 2;
            this.DONE = 3;
            this.state = 0;
        }

        public void next() {
            int msg = 0;
            int i = this.state;
            if (i == 0) {
                if (KeyguardSimPukView.this.checkPuk()) {
                    this.state = 1;
                    msg = R.string.kg_puk_enter_pin_hint;
                } else {
                    msg = R.string.kg_invalid_sim_puk_hint;
                }
            } else if (i == 1) {
                if (KeyguardSimPukView.this.checkPin()) {
                    this.state = 2;
                    msg = R.string.kg_enter_confirm_pin_hint;
                } else {
                    msg = R.string.kg_invalid_sim_pin_hint;
                }
            } else if (i == 2) {
                if (KeyguardSimPukView.this.confirmPin()) {
                    this.state = 3;
                    msg = R.string.keyguard_sim_unlock_progress_dialog_message;
                    KeyguardSimPukView.this.updateSim();
                } else {
                    this.state = 1;
                    msg = R.string.kg_invalid_confirm_pin_hint;
                }
            }
            KeyguardSimPukView.this.resetPasswordText(true, true);
            if (msg != 0) {
                KeyguardSimPukView.this.mSecurityMessageDisplay.setMessage(msg);
            }
        }

        void reset() {
            KeyguardSimPukView.this.mPinText = "";
            KeyguardSimPukView.this.mPukText = "";
            this.state = 0;
            KeyguardSimPukView.this.handleSubInfoChangeIfNeeded();
            if (KeyguardSimPukView.this.mShowDefaultMessage) {
                KeyguardSimPukView.this.showDefaultMessage();
            }
            boolean isEsimLocked = KeyguardEsimArea.isEsimLocked(KeyguardSimPukView.this.mContext, KeyguardSimPukView.this.mSubId);
            KeyguardEsimArea esimButton = (KeyguardEsimArea) KeyguardSimPukView.this.findViewById(R.id.keyguard_esim_area);
            esimButton.setVisibility(isEsimLocked ? 0 : 8);
            KeyguardSimPukView.this.mPasswordEntry.requestFocus();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r1v3, types: [com.android.keyguard.KeyguardSimPukView$2] */
    public void showDefaultMessage() {
        String msg;
        if (this.mRemainingAttempts >= 0) {
            this.mSecurityMessageDisplay.setMessage(getPukPasswordErrorMessage(this.mRemainingAttempts, true));
            return;
        }
        boolean isEsimLocked = KeyguardEsimArea.isEsimLocked(this.mContext, this.mSubId);
        int count = TelephonyManager.getDefault().getSimCount();
        Resources rez = getResources();
        TypedArray array = this.mContext.obtainStyledAttributes(new int[]{R.attr.wallpaperTextColor});
        int color = array.getColor(0, -1);
        array.recycle();
        if (count < 2) {
            msg = rez.getString(R.string.kg_puk_enter_puk_hint);
        } else {
            SubscriptionInfo info = KeyguardUpdateMonitor.getInstance(this.mContext).getSubscriptionInfoForSubId(this.mSubId);
            String displayName = info != null ? info.getDisplayName() : "";
            msg = rez.getString(R.string.kg_puk_enter_puk_hint_multi, displayName);
            if (info != null) {
                color = info.getIconTint();
            }
        }
        if (isEsimLocked) {
            msg = rez.getString(R.string.kg_sim_lock_esim_instructions, msg);
        }
        if (this.mSecurityMessageDisplay != null) {
            this.mSecurityMessageDisplay.setMessage(msg);
        }
        this.mSimImageView.setImageTintList(ColorStateList.valueOf(color));
        new CheckSimPuk("", "", this.mSubId) { // from class: com.android.keyguard.KeyguardSimPukView.2
            @Override // com.android.keyguard.KeyguardSimPukView.CheckSimPuk
            void onSimLockChangedResponse(int result, int attemptsRemaining) {
                Log.d("KeyguardSimPukView", "onSimCheckResponse  dummy One result" + result + " attemptsRemaining=" + attemptsRemaining);
                if (attemptsRemaining >= 0) {
                    KeyguardSimPukView.this.mRemainingAttempts = attemptsRemaining;
                    KeyguardSimPukView.this.mSecurityMessageDisplay.setMessage(KeyguardSimPukView.this.getPukPasswordErrorMessage(attemptsRemaining, true));
                }
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSubInfoChangeIfNeeded() {
        KeyguardUpdateMonitor monitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        int subId = monitor.getNextSubIdForState(IccCardConstants.State.PUK_REQUIRED);
        if (subId != this.mSubId && SubscriptionManager.isValidSubscriptionId(subId)) {
            this.mSubId = subId;
            this.mShowDefaultMessage = true;
            this.mRemainingAttempts = -1;
        }
    }

    @Override // com.android.keyguard.KeyguardPinBasedInputView, com.android.keyguard.KeyguardAbsKeyInputView
    protected int getPromptReasonStringRes(int reason) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getPukPasswordErrorMessage(int attemptsRemaining, boolean isDefault) {
        String displayMessage;
        if (attemptsRemaining == 0) {
            displayMessage = getContext().getString(R.string.kg_password_wrong_puk_code_dead);
        } else if (attemptsRemaining > 0) {
            int msgId = isDefault ? R.plurals.kg_password_default_puk_message : R.plurals.kg_password_wrong_puk_code;
            displayMessage = getContext().getResources().getQuantityString(msgId, attemptsRemaining, Integer.valueOf(attemptsRemaining));
        } else {
            int msgId2 = isDefault ? R.string.kg_puk_enter_puk_hint : R.string.kg_password_puk_failed;
            displayMessage = getContext().getString(msgId2);
        }
        if (KeyguardEsimArea.isEsimLocked(this.mContext, this.mSubId)) {
            return getResources().getString(R.string.kg_sim_lock_esim_instructions, displayMessage);
        }
        return displayMessage;
    }

    @Override // com.android.keyguard.KeyguardPinBasedInputView, com.android.keyguard.KeyguardAbsKeyInputView
    public void resetState() {
        super.resetState();
        this.mStateMachine.reset();
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected boolean shouldLockout(long deadline) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    public int getPasswordTextViewId() {
        return R.id.pukEntry;
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mUpdateMonitorCallback);
        resetState();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KeyguardUpdateMonitor.getInstance(this.mContext).removeCallback(this.mUpdateMonitorCallback);
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showUsabilityHint() {
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView, com.android.keyguard.KeyguardSecurityView
    public void onPause() {
        ProgressDialog progressDialog = this.mSimUnlockProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.mSimUnlockProgressDialog = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes19.dex */
    public abstract class CheckSimPuk extends Thread {
        private final String mPin;
        private final String mPuk;
        private final int mSubId;

        abstract void onSimLockChangedResponse(int i, int i2);

        protected CheckSimPuk(String puk, String pin, int subId) {
            this.mPuk = puk;
            this.mPin = pin;
            this.mSubId = subId;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                final int[] result = ITelephony.Stub.asInterface(ServiceManager.checkService("phone")).supplyPukReportResultForSubscriber(this.mSubId, this.mPuk, this.mPin);
                KeyguardSimPukView.this.post(new Runnable() { // from class: com.android.keyguard.KeyguardSimPukView.CheckSimPuk.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CheckSimPuk checkSimPuk = CheckSimPuk.this;
                        int[] iArr = result;
                        checkSimPuk.onSimLockChangedResponse(iArr[0], iArr[1]);
                    }
                });
            } catch (RemoteException e) {
                Log.e("KeyguardSimPukView", "RemoteException for supplyPukReportResult:", e);
                KeyguardSimPukView.this.post(new Runnable() { // from class: com.android.keyguard.KeyguardSimPukView.CheckSimPuk.2
                    @Override // java.lang.Runnable
                    public void run() {
                        CheckSimPuk.this.onSimLockChangedResponse(2, -1);
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
            if (!(this.mContext instanceof Activity)) {
                this.mSimUnlockProgressDialog.getWindow().setType(CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE);
            }
        }
        return this.mSimUnlockProgressDialog;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Dialog getPukRemainingAttemptsDialog(int remaining) {
        String msg = getPukPasswordErrorMessage(remaining, false);
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

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkPuk() {
        if (this.mPasswordEntry.getText().length() == 8) {
            this.mPukText = this.mPasswordEntry.getText();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkPin() {
        int length = this.mPasswordEntry.getText().length();
        if (length >= 4 && length <= 8) {
            this.mPinText = this.mPasswordEntry.getText();
            return true;
        }
        return false;
    }

    public boolean confirmPin() {
        return this.mPinText.equals(this.mPasswordEntry.getText());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSim() {
        getSimUnlockProgressDialog().show();
        if (this.mCheckSimPukThread == null) {
            this.mCheckSimPukThread = new CheckSimPuk(this.mPukText, this.mPinText, this.mSubId) { // from class: com.android.keyguard.KeyguardSimPukView.3
                @Override // com.android.keyguard.KeyguardSimPukView.CheckSimPuk
                void onSimLockChangedResponse(final int result, final int attemptsRemaining) {
                    KeyguardSimPukView.this.post(new Runnable() { // from class: com.android.keyguard.KeyguardSimPukView.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (KeyguardSimPukView.this.mSimUnlockProgressDialog != null) {
                                KeyguardSimPukView.this.mSimUnlockProgressDialog.hide();
                            }
                            KeyguardSimPukView.this.resetPasswordText(true, result != 0);
                            if (result == 0) {
                                KeyguardUpdateMonitor.getInstance(KeyguardSimPukView.this.getContext()).reportSimUnlocked(KeyguardSimPukView.this.mSubId);
                                KeyguardSimPukView.this.mRemainingAttempts = -1;
                                KeyguardSimPukView.this.mShowDefaultMessage = true;
                                if (KeyguardSimPukView.this.mCallback != null) {
                                    KeyguardSimPukView.this.mCallback.dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                                }
                            } else {
                                KeyguardSimPukView.this.mShowDefaultMessage = false;
                                if (result == 1) {
                                    KeyguardSimPukView.this.mSecurityMessageDisplay.setMessage(KeyguardSimPukView.this.getPukPasswordErrorMessage(attemptsRemaining, false));
                                    if (attemptsRemaining <= 2) {
                                        KeyguardSimPukView.this.getPukRemainingAttemptsDialog(attemptsRemaining).show();
                                    } else {
                                        KeyguardSimPukView.this.mSecurityMessageDisplay.setMessage(KeyguardSimPukView.this.getPukPasswordErrorMessage(attemptsRemaining, false));
                                    }
                                } else {
                                    KeyguardSimPukView.this.mSecurityMessageDisplay.setMessage(KeyguardSimPukView.this.getContext().getString(R.string.kg_password_puk_failed));
                                }
                                KeyguardSimPukView.this.mStateMachine.reset();
                            }
                            KeyguardSimPukView.this.mCheckSimPukThread = null;
                        }
                    });
                }
            };
            this.mCheckSimPukThread.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    public void verifyPasswordAndUnlock() {
        this.mStateMachine.next();
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
        return getContext().getString(17040179);
    }
}
