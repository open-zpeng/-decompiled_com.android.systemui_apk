package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.xiaopeng.vui.commons.IVuiElementBuilder;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.XInputUtils;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import com.xiaopeng.xui.vui.utils.VuiUtils;
/* loaded from: classes25.dex */
public class XTextFields extends XTextInput implements View.OnClickListener, IVuiElementListener {
    private CheckStateChangeListener checkStateChangeListener;
    private Context mContext;
    private ImageButton mPassWordView;
    private boolean mPasswordCheck;
    private boolean mPasswordEnable;

    /* loaded from: classes25.dex */
    public interface CheckStateChangeListener {
        void onCheckStateChanged();
    }

    public XTextFields(Context context) {
        this(context, null);
    }

    public XTextFields(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XTextFields(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public XTextFields(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XTextFields);
        boolean passwordEnable = a.getBoolean(R.styleable.XTextFields_text_fields_password_enabled, false);
        a.recycle();
        setPasswordEnable(passwordEnable);
        this.mContext = context;
    }

    @Override // com.xiaopeng.xui.widget.XTextInput
    protected void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.x_text_fields, this);
        this.mResetView = (ImageButton) findViewById(R.id.x_text_fields_reset);
        this.mPassWordView = (ImageButton) findViewById(R.id.x_text_fields_pass);
        this.mEditText = (EditText) findViewById(R.id.x_text_fields_edit);
        this.mErrorTextView = (TextView) findViewById(R.id.x_text_fields_error);
        this.mStatusView = findViewById(R.id.x_text_fields_line);
        this.mPassWordView.setOnClickListener(this);
        XInputUtils.ignoreHiddenInput(this.mPassWordView);
    }

    public boolean isPasswordEnable() {
        return this.mPasswordEnable;
    }

    public void setPasswordEnable(boolean enable) {
        if (this.mPasswordEnable != enable) {
            this.mPasswordEnable = enable;
            int selection = this.mEditText.getSelectionEnd();
            this.mPassWordView.setVisibility(enable ? 0 : 8);
            if (enable) {
                this.mPasswordCheck = true;
                this.mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                this.mEditText.setTransformationMethod(null);
                this.mPasswordCheck = false;
            }
            this.mPassWordView.setSelected(this.mPasswordCheck);
            this.mEditText.setSelection(selection);
        }
    }

    public void passwordCheckToggleRequested() {
        CheckStateChangeListener checkStateChangeListener;
        if (this.mPasswordEnable) {
            int selection = this.mEditText.getSelectionEnd();
            if (hasPasswordTransformation()) {
                this.mEditText.setTransformationMethod(null);
                this.mPasswordCheck = false;
            } else {
                this.mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.mPasswordCheck = true;
            }
            this.mPassWordView.setSelected(this.mPasswordCheck);
            this.mEditText.setSelection(selection);
            if (Xui.isVuiEnable() && (checkStateChangeListener = this.checkStateChangeListener) != null) {
                checkStateChangeListener.onCheckStateChanged();
            }
        }
    }

    private boolean hasPasswordTransformation() {
        return this.mEditText != null && (this.mEditText.getTransformationMethod() instanceof PasswordTransformationMethod);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.x_text_fields_pass) {
            passwordCheckToggleRequested();
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public VuiElement onBuildVuiElement(String elementId, IVuiElementBuilder builder) {
        if (this.mPassWordView instanceof VuiView) {
            String[] passwordLabels = {this.mContext.getString(R.string.vui_label_hide_password), this.mContext.getString(R.string.vui_label_display_password)};
            int curIndex = !this.mPasswordCheck ? 1 : 0;
            VuiUtils.setStatefulButtonAttr((VuiView) this.mPassWordView, curIndex, passwordLabels);
            return null;
        }
        return null;
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementListener
    public boolean onVuiElementEvent(final View view, VuiEvent vuiEvent) {
        logD("textfields onVuiElementEvent");
        if (view == null) {
            return false;
        }
        if (view.getId() == this.mPassWordView.getId()) {
            String value = (String) vuiEvent.getEventValue(vuiEvent);
            if (!(this.mPasswordCheck && value.equals(this.mContext.getString(R.string.vui_label_hide_password))) && (this.mPasswordCheck || !value.equals(this.mContext.getString(R.string.vui_label_display_password)))) {
                post(new Runnable() { // from class: com.xiaopeng.xui.widget.XTextFields.1
                    @Override // java.lang.Runnable
                    public void run() {
                        VuiFloatingLayerManager.show(view);
                        XTextFields.this.passwordCheckToggleRequested();
                    }
                });
                return true;
            }
            return true;
        }
        if (view.getVisibility() == 0 && view.isEnabled()) {
            post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XTextFields$ZQ-mDqZK3VUmPHHAmlRctH325y0
                @Override // java.lang.Runnable
                public final void run() {
                    VuiFloatingLayerManager.show(view);
                }
            });
        }
        return false;
    }

    public void setCheckStateChangeListener(CheckStateChangeListener checkStateChangeListener) {
        this.checkStateChangeListener = checkStateChangeListener;
    }
}
