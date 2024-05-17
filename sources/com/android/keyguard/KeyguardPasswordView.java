package com.android.keyguard;

import android.content.Context;
import android.graphics.Rect;
import android.os.UserHandle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.TextView;
import com.android.internal.widget.TextViewInputDisabler;
import com.android.systemui.R;
import java.util.List;
/* loaded from: classes19.dex */
public class KeyguardPasswordView extends KeyguardAbsKeyInputView implements KeyguardSecurityView, TextView.OnEditorActionListener, TextWatcher {
    private static final int DELAY_MILLIS_TO_REEVALUATE_IME_SWITCH_ICON = 500;
    private final int mDisappearYTranslation;
    private Interpolator mFastOutLinearInInterpolator;
    InputMethodManager mImm;
    private Interpolator mLinearOutSlowInInterpolator;
    private TextView mPasswordEntry;
    private TextViewInputDisabler mPasswordEntryDisabler;
    private final boolean mShowImeAtScreenOn;
    private View mSwitchImeButton;

    public KeyguardPasswordView(Context context) {
        this(context, null);
    }

    public KeyguardPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mShowImeAtScreenOn = context.getResources().getBoolean(R.bool.kg_show_ime_at_screen_on);
        this.mDisappearYTranslation = getResources().getDimensionPixelSize(R.dimen.disappear_y_translation);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
        this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(context, 17563663);
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected void resetState() {
        this.mPasswordEntry.setTextOperationUser(UserHandle.of(KeyguardUpdateMonitor.getCurrentUser()));
        if (this.mSecurityMessageDisplay != null) {
            this.mSecurityMessageDisplay.setMessage("");
        }
        boolean wasDisabled = this.mPasswordEntry.isEnabled();
        setPasswordEntryEnabled(true);
        setPasswordEntryInputEnabled(true);
        if (this.mResumed && this.mPasswordEntry.isVisibleToUser() && wasDisabled) {
            this.mImm.showSoftInput(this.mPasswordEntry, 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    public int getPasswordTextViewId() {
        return R.id.passwordEntry;
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView, com.android.keyguard.KeyguardSecurityView
    public boolean needsInput() {
        return true;
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView, com.android.keyguard.KeyguardSecurityView
    public void onResume(final int reason) {
        super.onResume(reason);
        post(new Runnable() { // from class: com.android.keyguard.KeyguardPasswordView.1
            @Override // java.lang.Runnable
            public void run() {
                if (KeyguardPasswordView.this.isShown() && KeyguardPasswordView.this.mPasswordEntry.isEnabled()) {
                    KeyguardPasswordView.this.mPasswordEntry.requestFocus();
                    if (reason != 1 || KeyguardPasswordView.this.mShowImeAtScreenOn) {
                        KeyguardPasswordView.this.mImm.showSoftInput(KeyguardPasswordView.this.mPasswordEntry, 1);
                    }
                }
            }
        });
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
                        return R.string.kg_prompt_reason_timeout_password;
                    }
                    return R.string.kg_prompt_reason_device_admin;
                }
                return R.string.kg_prompt_reason_timeout_password;
            }
            return R.string.kg_prompt_reason_restart_password;
        }
        return 0;
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView, com.android.keyguard.KeyguardSecurityView
    public void onPause() {
        super.onPause();
        this.mImm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSwitchImeButton() {
        boolean wasVisible = this.mSwitchImeButton.getVisibility() == 0;
        boolean shouldBeVisible = hasMultipleEnabledIMEsOrSubtypes(this.mImm, false);
        if (wasVisible != shouldBeVisible) {
            this.mSwitchImeButton.setVisibility(shouldBeVisible ? 0 : 8);
        }
        if (this.mSwitchImeButton.getVisibility() != 0) {
            ViewGroup.LayoutParams params = this.mPasswordEntry.getLayoutParams();
            if (params instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) params;
                mlp.setMarginStart(0);
                this.mPasswordEntry.setLayoutParams(params);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.keyguard.KeyguardAbsKeyInputView, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mImm = (InputMethodManager) getContext().getSystemService("input_method");
        this.mPasswordEntry = (TextView) findViewById(getPasswordTextViewId());
        this.mPasswordEntry.setTextOperationUser(UserHandle.of(KeyguardUpdateMonitor.getCurrentUser()));
        this.mPasswordEntryDisabler = new TextViewInputDisabler(this.mPasswordEntry);
        this.mPasswordEntry.setKeyListener(TextKeyListener.getInstance());
        this.mPasswordEntry.setInputType(129);
        this.mPasswordEntry.setOnEditorActionListener(this);
        this.mPasswordEntry.addTextChangedListener(this);
        this.mPasswordEntry.setOnClickListener(new View.OnClickListener() { // from class: com.android.keyguard.KeyguardPasswordView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                KeyguardPasswordView.this.mCallback.userActivity();
            }
        });
        this.mPasswordEntry.setSelected(true);
        this.mSwitchImeButton = findViewById(R.id.switch_ime_button);
        this.mSwitchImeButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.keyguard.KeyguardPasswordView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                KeyguardPasswordView.this.mCallback.userActivity();
                KeyguardPasswordView.this.mImm.showInputMethodPickerFromSystem(false, KeyguardPasswordView.this.getContext().getDisplayId());
            }
        });
        View cancelBtn = findViewById(R.id.cancel_button);
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.keyguard.-$$Lambda$KeyguardPasswordView$o6rdkANQuxgpLXMWWI2lzhbd_0k
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    KeyguardPasswordView.this.lambda$onFinishInflate$0$KeyguardPasswordView(view);
                }
            });
        }
        updateSwitchImeButton();
        postDelayed(new Runnable() { // from class: com.android.keyguard.KeyguardPasswordView.4
            @Override // java.lang.Runnable
            public void run() {
                KeyguardPasswordView.this.updateSwitchImeButton();
            }
        }, 500L);
    }

    public /* synthetic */ void lambda$onFinishInflate$0$KeyguardPasswordView(View view) {
        this.mCallback.reset();
        this.mCallback.onCancelClicked();
    }

    @Override // android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return this.mPasswordEntry.requestFocus(direction, previouslyFocusedRect);
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected void resetPasswordText(boolean animate, boolean announce) {
        this.mPasswordEntry.setText("");
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected byte[] getPasswordText() {
        return charSequenceToByteArray(this.mPasswordEntry.getText());
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected void setPasswordEntryEnabled(boolean enabled) {
        this.mPasswordEntry.setEnabled(enabled);
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    protected void setPasswordEntryInputEnabled(boolean enabled) {
        this.mPasswordEntryDisabler.setInputEnabled(enabled);
    }

    private boolean hasMultipleEnabledIMEsOrSubtypes(InputMethodManager imm, boolean shouldIncludeAuxiliarySubtypes) {
        List<InputMethodInfo> enabledImis = imm.getEnabledInputMethodListAsUser(KeyguardUpdateMonitor.getCurrentUser());
        int filteredImisCount = 0;
        for (InputMethodInfo imi : enabledImis) {
            if (filteredImisCount > 1) {
                return true;
            }
            List<InputMethodSubtype> subtypes = imm.getEnabledInputMethodSubtypeList(imi, true);
            if (subtypes.isEmpty()) {
                filteredImisCount++;
            } else {
                int auxCount = 0;
                for (InputMethodSubtype subtype : subtypes) {
                    if (subtype.isAuxiliary()) {
                        auxCount++;
                    }
                }
                int nonAuxCount = subtypes.size() - auxCount;
                if (nonAuxCount > 0 || (shouldIncludeAuxiliarySubtypes && auxCount > 1)) {
                    filteredImisCount++;
                }
            }
        }
        return filteredImisCount > 1 || imm.getEnabledInputMethodSubtypeList(null, false).size() > 1;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showUsabilityHint() {
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView
    public int getWrongPasswordStringId() {
        return R.string.kg_wrong_password;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void startAppearAnimation() {
        setAlpha(0.0f);
        setTranslationY(0.0f);
        animate().alpha(1.0f).withLayer().setDuration(300L).setInterpolator(this.mLinearOutSlowInInterpolator);
    }

    @Override // com.android.keyguard.KeyguardAbsKeyInputView, com.android.keyguard.KeyguardSecurityView
    public boolean startDisappearAnimation(Runnable finishRunnable) {
        animate().alpha(0.0f).translationY(this.mDisappearYTranslation).setInterpolator(this.mFastOutLinearInInterpolator).setDuration(100L).withEndAction(finishRunnable);
        return true;
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (this.mCallback != null) {
            this.mCallback.userActivity();
        }
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            onUserInput();
        }
    }

    @Override // android.widget.TextView.OnEditorActionListener
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean isSoftImeEvent = event == null && (actionId == 0 || actionId == 6 || actionId == 5);
        boolean isKeyboardEnterKey = event != null && KeyEvent.isConfirmKey(event.getKeyCode()) && event.getAction() == 0;
        if (isSoftImeEvent || isKeyboardEnterKey) {
            verifyPasswordAndUnlock();
            return true;
        }
        return false;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public CharSequence getTitle() {
        return getContext().getString(17040174);
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
