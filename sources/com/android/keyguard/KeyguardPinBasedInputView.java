package com.android.keyguard;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.android.keyguard.PasswordTextView;
import com.android.systemui.R;
/* loaded from: classes19.dex */
public abstract class KeyguardPinBasedInputView extends KeyguardAbsKeyInputView implements View.OnKeyListener, View.OnTouchListener {
    private View mButton0;
    private View mButton1;
    private View mButton2;
    private View mButton3;
    private View mButton4;
    private View mButton5;
    private View mButton6;
    private View mButton7;
    private View mButton8;
    private View mButton9;
    private View mDeleteButton;
    private View mOkButton;
    protected PasswordTextView mPasswordEntry;

    public KeyguardPinBasedInputView(Context context) {
        this(context, null);
    }

    public KeyguardPinBasedInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return this.mPasswordEntry.requestFocus(direction, previouslyFocusedRect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    public void resetState() {
        setPasswordEntryEnabled(true);
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected void setPasswordEntryEnabled(boolean enabled) {
        this.mPasswordEntry.setEnabled(enabled);
        this.mOkButton.setEnabled(enabled);
        if (enabled && !this.mPasswordEntry.hasFocus()) {
            this.mPasswordEntry.requestFocus();
        }
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected void setPasswordEntryInputEnabled(boolean enabled) {
        this.mPasswordEntry.setEnabled(enabled);
        this.mOkButton.setEnabled(enabled);
        if (enabled && !this.mPasswordEntry.hasFocus()) {
            this.mPasswordEntry.requestFocus();
        }
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode)) {
            performClick(this.mOkButton);
            return true;
        } else if (keyCode == 67) {
            performClick(this.mDeleteButton);
            return true;
        } else if (keyCode >= 7 && keyCode <= 16) {
            int number = keyCode - 7;
            performNumberClick(number);
            return true;
        } else if (keyCode >= 144 && keyCode <= 153) {
            int number2 = keyCode - 144;
            performNumberClick(number2);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected int getPromptReasonStringRes(int reason) {
        if (reason != 0) {
            if (reason != 1) {
                if (reason != 2) {
                    if (reason != 3) {
                        if (reason == 4) {
                            return R.string.kg_prompt_reason_user_request;
                        }
                        return R.string.kg_prompt_reason_timeout_pin;
                    }
                    return R.string.kg_prompt_reason_device_admin;
                }
                return R.string.kg_prompt_reason_timeout_pin;
            }
            return R.string.kg_prompt_reason_restart_pin;
        }
        return 0;
    }

    private void performClick(View view) {
        view.performClick();
    }

    private void performNumberClick(int number) {
        switch (number) {
            case 0:
                performClick(this.mButton0);
                return;
            case 1:
                performClick(this.mButton1);
                return;
            case 2:
                performClick(this.mButton2);
                return;
            case 3:
                performClick(this.mButton3);
                return;
            case 4:
                performClick(this.mButton4);
                return;
            case 5:
                performClick(this.mButton5);
                return;
            case 6:
                performClick(this.mButton6);
                return;
            case 7:
                performClick(this.mButton7);
                return;
            case 8:
                performClick(this.mButton8);
                return;
            case 9:
                performClick(this.mButton9);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    public void resetPasswordText(boolean animate, boolean announce) {
        this.mPasswordEntry.reset(animate, announce);
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected byte[] getPasswordText() {
        return charSequenceToByteArray(this.mPasswordEntry.getText());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView, android.view.View
    public void onFinishInflate() {
        this.mPasswordEntry = (PasswordTextView) findViewById(getPasswordTextViewId());
        this.mPasswordEntry.setOnKeyListener(this);
        this.mPasswordEntry.setSelected(true);
        this.mPasswordEntry.setUserActivityListener(new PasswordTextView.UserActivityListener() { // from class: com.android.keyguard.KeyguardPinBasedInputView.1
            @Override // com.android.keyguard.PasswordTextView.UserActivityListener
            public void onUserActivity() {
                KeyguardPinBasedInputView.this.onUserInput();
            }
        });
        this.mOkButton = findViewById(R.id.key_enter);
        View view = this.mOkButton;
        if (view != null) {
            view.setOnTouchListener(this);
            this.mOkButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.keyguard.KeyguardPinBasedInputView.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    if (KeyguardPinBasedInputView.this.mPasswordEntry.isEnabled()) {
                        KeyguardPinBasedInputView.this.verifyPasswordAndUnlock();
                    }
                }
            });
            this.mOkButton.setOnHoverListener(new LiftToActivateListener(getContext()));
        }
        this.mDeleteButton = findViewById(R.id.delete_button);
        this.mDeleteButton.setVisibility(0);
        this.mDeleteButton.setOnTouchListener(this);
        this.mDeleteButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.keyguard.KeyguardPinBasedInputView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (KeyguardPinBasedInputView.this.mPasswordEntry.isEnabled()) {
                    KeyguardPinBasedInputView.this.mPasswordEntry.deleteLastChar();
                }
            }
        });
        this.mDeleteButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.keyguard.KeyguardPinBasedInputView.4
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                if (KeyguardPinBasedInputView.this.mPasswordEntry.isEnabled()) {
                    KeyguardPinBasedInputView.this.resetPasswordText(true, true);
                }
                KeyguardPinBasedInputView.this.doHapticKeyClick();
                return true;
            }
        });
        this.mButton0 = findViewById(R.id.key0);
        this.mButton1 = findViewById(R.id.key1);
        this.mButton2 = findViewById(R.id.key2);
        this.mButton3 = findViewById(R.id.key3);
        this.mButton4 = findViewById(R.id.key4);
        this.mButton5 = findViewById(R.id.key5);
        this.mButton6 = findViewById(R.id.key6);
        this.mButton7 = findViewById(R.id.key7);
        this.mButton8 = findViewById(R.id.key8);
        this.mButton9 = findViewById(R.id.key9);
        this.mPasswordEntry.requestFocus();
        super.onFinishInflate();
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView, com.android.keyguard.KeyguardSecurityView
    public void onResume(int reason) {
        super.onResume(reason);
        this.mPasswordEntry.requestFocus();
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getActionMasked() == 0) {
            doHapticKeyClick();
            return false;
        }
        return false;
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == 0) {
            return onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public CharSequence getTitle() {
        return getContext().getString(17040177);
    }

    private static byte[] charSequenceToByteArray(CharSequence chars) {
        if (chars == null) {
            return null;
        }
        byte[] bytes = new byte[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            bytes[i] = (byte) chars.charAt(i);
        }
        return bytes;
    }
}
