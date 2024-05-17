package com.android.keyguard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.EmergencyButton;
import com.android.systemui.R;
import java.util.Arrays;
/* loaded from: classes19.dex */
public abstract class KeyguardAbsKeyInputView extends LinearLayout implements KeyguardSecurityView, EmergencyButton.EmergencyButtonCallback {
    protected static final int MINIMUM_PASSWORD_LENGTH_BEFORE_REPORT = 3;
    protected KeyguardSecurityCallback mCallback;
    private CountDownTimer mCountdownTimer;
    private boolean mDismissing;
    protected View mEcaView;
    protected boolean mEnableHaptics;
    protected LockPatternUtils mLockPatternUtils;
    protected AsyncTask<?, ?, ?> mPendingLockCheck;
    protected boolean mResumed;
    protected SecurityMessageDisplay mSecurityMessageDisplay;

    protected abstract byte[] getPasswordText();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int getPasswordTextViewId();

    protected abstract int getPromptReasonStringRes(int i);

    protected abstract void resetPasswordText(boolean z, boolean z2);

    protected abstract void resetState();

    protected abstract void setPasswordEntryEnabled(boolean z);

    protected abstract void setPasswordEntryInputEnabled(boolean z);

    public KeyguardAbsKeyInputView(Context context) {
        this(context, null);
    }

    public KeyguardAbsKeyInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCountdownTimer = null;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void setKeyguardCallback(KeyguardSecurityCallback callback) {
        this.mCallback = callback;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void setLockPatternUtils(LockPatternUtils utils) {
        this.mLockPatternUtils = utils;
        this.mEnableHaptics = this.mLockPatternUtils.isTactileFeedbackEnabled();
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void reset() {
        this.mDismissing = false;
        resetPasswordText(false, false);
        long deadline = this.mLockPatternUtils.getLockoutAttemptDeadline(KeyguardUpdateMonitor.getCurrentUser());
        if (shouldLockout(deadline)) {
            handleAttemptLockout(deadline);
        } else {
            resetState();
        }
    }

    protected boolean shouldLockout(long deadline) {
        return deadline != 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mEcaView = findViewById(R.id.keyguard_selector_fade_container);
        EmergencyButton button = (EmergencyButton) findViewById(R.id.emergency_call_button);
        if (button != null) {
            button.setCallback(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSecurityMessageDisplay = KeyguardMessageArea.findSecurityMessageDisplay(this);
    }

    @Override // com.android.keyguard.EmergencyButton.EmergencyButtonCallback
    public void onEmergencyButtonClickedWhenInCall() {
        this.mCallback.reset();
    }

    protected int getWrongPasswordStringId() {
        return R.string.kg_wrong_password;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void verifyPasswordAndUnlock() {
        if (this.mDismissing) {
            return;
        }
        final byte[] entry = getPasswordText();
        setPasswordEntryInputEnabled(false);
        AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
        if (asyncTask != null) {
            asyncTask.cancel(false);
        }
        final int userId = KeyguardUpdateMonitor.getCurrentUser();
        if (entry.length <= 3) {
            setPasswordEntryInputEnabled(true);
            onPasswordChecked(userId, false, 0, false);
            Arrays.fill(entry, (byte) 0);
            return;
        }
        if (LatencyTracker.isEnabled(this.mContext)) {
            LatencyTracker.getInstance(this.mContext).onActionStart(3);
            LatencyTracker.getInstance(this.mContext).onActionStart(4);
        }
        this.mPendingLockCheck = LockPatternChecker.checkPassword(this.mLockPatternUtils, entry, userId, new LockPatternChecker.OnCheckCallback() { // from class: com.android.keyguard.KeyguardAbsKeyInputView.1
            public void onEarlyMatched() {
                if (LatencyTracker.isEnabled(KeyguardAbsKeyInputView.this.mContext)) {
                    LatencyTracker.getInstance(KeyguardAbsKeyInputView.this.mContext).onActionEnd(3);
                }
                KeyguardAbsKeyInputView.this.onPasswordChecked(userId, true, 0, true);
                Arrays.fill(entry, (byte) 0);
            }

            public void onChecked(boolean matched, int timeoutMs) {
                if (LatencyTracker.isEnabled(KeyguardAbsKeyInputView.this.mContext)) {
                    LatencyTracker.getInstance(KeyguardAbsKeyInputView.this.mContext).onActionEnd(4);
                }
                KeyguardAbsKeyInputView.this.setPasswordEntryInputEnabled(true);
                KeyguardAbsKeyInputView keyguardAbsKeyInputView = KeyguardAbsKeyInputView.this;
                keyguardAbsKeyInputView.mPendingLockCheck = null;
                if (!matched) {
                    keyguardAbsKeyInputView.onPasswordChecked(userId, false, timeoutMs, true);
                }
                Arrays.fill(entry, (byte) 0);
            }

            public void onCancelled() {
                if (LatencyTracker.isEnabled(KeyguardAbsKeyInputView.this.mContext)) {
                    LatencyTracker.getInstance(KeyguardAbsKeyInputView.this.mContext).onActionEnd(4);
                }
                Arrays.fill(entry, (byte) 0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPasswordChecked(int userId, boolean matched, int timeoutMs, boolean isValidPassword) {
        boolean dismissKeyguard = KeyguardUpdateMonitor.getCurrentUser() == userId;
        if (matched) {
            this.mCallback.reportUnlockAttempt(userId, true, 0);
            if (dismissKeyguard) {
                this.mDismissing = true;
                this.mCallback.dismiss(true, userId);
            }
        } else {
            if (isValidPassword) {
                this.mCallback.reportUnlockAttempt(userId, false, timeoutMs);
                if (timeoutMs > 0) {
                    long deadline = this.mLockPatternUtils.setLockoutAttemptDeadline(userId, timeoutMs);
                    handleAttemptLockout(deadline);
                }
            }
            if (timeoutMs == 0) {
                this.mSecurityMessageDisplay.setMessage(getWrongPasswordStringId());
            }
        }
        resetPasswordText(true, !matched);
    }

    /* JADX WARN: Type inference failed for: r10v0, types: [com.android.keyguard.KeyguardAbsKeyInputView$2] */
    protected void handleAttemptLockout(long elapsedRealtimeDeadline) {
        setPasswordEntryEnabled(false);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long secondsInFuture = (long) Math.ceil((elapsedRealtimeDeadline - elapsedRealtime) / 1000.0d);
        this.mCountdownTimer = new CountDownTimer(secondsInFuture * 1000, 1000L) { // from class: com.android.keyguard.KeyguardAbsKeyInputView.2
            @Override // android.os.CountDownTimer
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) Math.round(millisUntilFinished / 1000.0d);
                KeyguardAbsKeyInputView.this.mSecurityMessageDisplay.setMessage(KeyguardAbsKeyInputView.this.mContext.getResources().getQuantityString(R.plurals.kg_too_many_failed_attempts_countdown, secondsRemaining, Integer.valueOf(secondsRemaining)));
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
                KeyguardAbsKeyInputView.this.mSecurityMessageDisplay.setMessage("");
                KeyguardAbsKeyInputView.this.resetState();
            }
        }.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onUserInput() {
        KeyguardSecurityCallback keyguardSecurityCallback = this.mCallback;
        if (keyguardSecurityCallback != null) {
            keyguardSecurityCallback.userActivity();
            this.mCallback.onUserInput();
        }
        this.mSecurityMessageDisplay.setMessage("");
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 0) {
            onUserInput();
            return false;
        }
        return false;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public boolean needsInput() {
        return false;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void onPause() {
        this.mResumed = false;
        CountDownTimer countDownTimer = this.mCountdownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            this.mCountdownTimer = null;
        }
        AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
        if (asyncTask != null) {
            asyncTask.cancel(false);
            this.mPendingLockCheck = null;
        }
        reset();
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void onResume(int reason) {
        this.mResumed = true;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public KeyguardSecurityCallback getCallback() {
        return this.mCallback;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showPromptReason(int reason) {
        int promtReasonStringRes;
        if (reason != 0 && (promtReasonStringRes = getPromptReasonStringRes(reason)) != 0) {
            this.mSecurityMessageDisplay.setMessage(promtReasonStringRes);
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showMessage(CharSequence message, ColorStateList colorState) {
        if (colorState != null) {
            this.mSecurityMessageDisplay.setNextMessageColor(colorState);
        }
        this.mSecurityMessageDisplay.setMessage(message);
    }

    public void doHapticKeyClick() {
        if (this.mEnableHaptics) {
            performHapticFeedback(1, 3);
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public boolean startDisappearAnimation(Runnable finishRunnable) {
        return false;
    }
}
