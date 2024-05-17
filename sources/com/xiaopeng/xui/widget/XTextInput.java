package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.StringRes;
import com.xiaopeng.vui.commons.IVuiEngine;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.sound.XSoundEffectManager;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.utils.XInputUtils;
/* loaded from: classes25.dex */
public abstract class XTextInput extends XRelativeLayout implements TextWatcher, View.OnFocusChangeListener {
    protected int mColorError;
    protected int mColorErrorId;
    protected int mColorFocus;
    protected int mColorFocusId;
    protected int mColorNormal;
    protected int mColorNormalId;
    protected EditText mEditText;
    protected boolean mErrorEnable;
    protected TextView mErrorTextView;
    protected boolean mResetEnable;
    protected ImageButton mResetView;
    private String mSceneId;
    protected View mStatusView;
    private IVuiEngine mVuiEngine;

    protected abstract void initView();

    public XTextInput(Context context) {
        this(context, null);
    }

    public XTextInput(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XTextInput(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XTextInput(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XTextInput);
        CharSequence hint = a.getText(R.styleable.XTextInput_input_edit_hint);
        CharSequence text = a.getText(R.styleable.XTextInput_input_edit_text);
        boolean resetEnable = a.getBoolean(R.styleable.XTextInput_input_reset_enabled, false);
        boolean errorEnable = a.getBoolean(R.styleable.XTextInput_input_error_enabled, false);
        int editAppearance = a.hasValue(R.styleable.XTextInput_input_edit_appearance) ? a.getResourceId(R.styleable.XTextInput_input_edit_appearance, -1) : -1;
        int maxLength = a.hasValue(R.styleable.XTextInput_input_edit_max_length) ? a.getInt(R.styleable.XTextInput_input_edit_max_length, -1) : -1;
        int inputType = a.hasValue(R.styleable.XTextInput_android_inputType) ? a.getInt(R.styleable.XTextInput_android_inputType, -1) : -1;
        this.mColorNormalId = a.getResourceId(R.styleable.XTextInput_input_normal_color, -1);
        this.mColorFocusId = a.getResourceId(R.styleable.XTextInput_input_focus_color, -1);
        this.mColorErrorId = a.getResourceId(R.styleable.XTextInput_input_error_color, -1);
        a.recycle();
        initView();
        setResetEnable(resetEnable);
        setErrorEnable(errorEnable);
        if (editAppearance > 0) {
            this.mEditText.setTextAppearance(editAppearance);
        }
        if (maxLength > 0) {
            this.mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }
        if (inputType > 0) {
            this.mEditText.setInputType(inputType);
        }
        this.mEditText.setOnFocusChangeListener(this);
        this.mEditText.addTextChangedListener(this);
        this.mResetView.setSoundEffectsEnabled(false);
        XInputUtils.ignoreHiddenInput(this.mResetView);
        this.mResetView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.xui.widget.XTextInput.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                XTextInput.this.mResetView.setVisibility(8);
                XTextInput.this.mEditText.setText((CharSequence) null);
                XTextInput.this.mEditText.requestFocus();
                XSoundEffectManager.get().play(5);
                if (XTextInput.this.mVuiEngine != null && !TextUtils.isEmpty(XTextInput.this.mSceneId)) {
                    XTextInput.this.mVuiEngine.updateElementAttribute(XTextInput.this.mSceneId, XTextInput.this.mResetView);
                }
            }
        });
        setEditHint(hint);
        setEditContent(text);
        initColor();
        updateEditTextBackground();
    }

    public void setMaxLength(int maxLength) {
        this.mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    public void setInputType(int inputType) {
        this.mEditText.setInputType(inputType);
    }

    public String getText() {
        return this.mEditText.getText().toString();
    }

    public void setEditHint(CharSequence hint) {
        this.mEditText.setHint(hint);
    }

    public void setEditHint(@StringRes int resid) {
        this.mEditText.setHint(resid);
    }

    public void setEditContent(CharSequence text) {
        this.mEditText.setText(text);
    }

    public void setEditContent(@StringRes int resid) {
        this.mEditText.setText(resid);
    }

    public EditText getEditText() {
        return this.mEditText;
    }

    public void setResetEnable(boolean enable) {
        if (this.mResetEnable != enable) {
            this.mResetEnable = enable;
            this.mResetView.setVisibility((!enable || this.mEditText.getText().toString().length() <= 0) ? 8 : 0);
        }
    }

    public boolean isResetEnable() {
        return this.mResetEnable;
    }

    public boolean isErrorEnable() {
        return this.mErrorEnable;
    }

    public void setErrorEnable(boolean enable) {
        if (this.mErrorEnable != enable) {
            this.mErrorEnable = enable;
            this.mErrorTextView.setVisibility(enable ? 0 : 8);
            updateEditTextBackground();
        }
    }

    public void setErrorMsg(CharSequence errorText) {
        if (!isErrorEnable()) {
            if (TextUtils.isEmpty(errorText)) {
                return;
            }
            setErrorEnable(true);
        }
        if (!TextUtils.isEmpty(errorText)) {
            showError(errorText);
        } else {
            hideError();
        }
        updateEditTextBackground();
    }

    private void showError(CharSequence errorText) {
        this.mErrorTextView.setText(errorText);
    }

    private void hideError() {
        this.mErrorTextView.setText((CharSequence) null);
    }

    private void initColor() {
        this.mColorNormal = getResources().getColor(this.mColorNormalId, getContext().getTheme());
        this.mColorFocus = getResources().getColor(this.mColorFocusId, getContext().getTheme());
        this.mColorError = getResources().getColor(this.mColorErrorId, getContext().getTheme());
    }

    void updateEditTextBackground() {
        Drawable drawable = this.mStatusView.getBackground();
        if (drawable == null) {
            return;
        }
        if (isErrorShow()) {
            drawable.mutate().setTint(this.mColorError);
        } else if (this.mEditText.isFocused()) {
            drawable.mutate().setTint(this.mColorFocus);
        } else {
            drawable.mutate().setTint(this.mColorNormal);
        }
    }

    private boolean isErrorShow() {
        return isErrorEnable() && !TextUtils.isEmpty(this.mErrorTextView.getText().toString());
    }

    public void addTextChangedListener(TextWatcher watcher) {
        if (watcher != null) {
            this.mEditText.addTextChangedListener(watcher);
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        setEnabled(enabled, true);
    }

    public void setEnabled(boolean enabled, boolean containChild) {
        super.setEnabled(enabled);
        if (containChild) {
            setChildEnabled(this, enabled);
        }
    }

    private void setChildEnabled(ViewGroup viewGroup, boolean enabled) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                setChildEnabled((ViewGroup) view, enabled);
            }
            view.setEnabled(enabled);
        }
    }

    @Override // android.text.TextWatcher
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override // android.text.TextWatcher
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!this.mResetEnable) {
            return;
        }
        if (s.length() > 0) {
            if (this.mResetView.getVisibility() == 0) {
                return;
            }
            this.mResetView.setVisibility(0);
            if (this.mVuiEngine != null && !TextUtils.isEmpty(this.mSceneId)) {
                this.mVuiEngine.updateElementAttribute(this.mSceneId, this.mResetView);
            }
        } else if (this.mResetView.getVisibility() == 8) {
        } else {
            this.mResetView.setVisibility(8);
            if (this.mVuiEngine != null && !TextUtils.isEmpty(this.mSceneId)) {
                this.mVuiEngine.updateElementAttribute(this.mSceneId, this.mResetView);
            }
        }
    }

    @Override // android.text.TextWatcher
    public void afterTextChanged(Editable s) {
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == this.mEditText) {
            updateEditTextBackground();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (XThemeManager.isThemeChanged(newConfig)) {
            initColor();
            updateEditTextBackground();
        }
    }

    public void setVuiScene(IVuiEngine vuiEngine, String sceneId) {
        this.mVuiEngine = vuiEngine;
        this.mSceneId = sceneId;
    }
}
