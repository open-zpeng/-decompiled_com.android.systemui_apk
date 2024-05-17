package com.xiaopeng.xui.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.app.XDialogInterface;
import com.xiaopeng.xui.widget.XNumberPicker;
/* loaded from: classes25.dex */
public class XNumberPickerDialog extends XDialog implements XNumberPicker.OnValueChangeListener, XDialogInterface.OnClickListener {
    private final XNumberPicker mNumberPicker;
    private OnXNumberSetListener mNumberSetListener;

    /* loaded from: classes25.dex */
    public interface OnXNumberSetListener {
        void onXNumberSet(XNumberPicker xNumberPicker, CharSequence charSequence);
    }

    public XNumberPickerDialog(@NonNull Context context, String[] values) {
        this(context, 0, values);
    }

    public XNumberPickerDialog(Context context, int themeResId, String[] values) {
        super(context, resolveDialogTheme(context, themeResId));
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.x_number_picker, getContentView(), false);
        setCustomView(view, false);
        setPositiveButton(" ", this);
        setNegativeButton(" ", this);
        this.mNumberPicker = (XNumberPicker) view.findViewById(R.id.x_number_picker);
        if (values != null) {
            this.mNumberPicker.setMinValue(1);
            this.mNumberPicker.setMaxValue(values.length);
            this.mNumberPicker.setDisplayedValues(values);
        }
        this.mNumberPicker.setOnValueChangedListener(this);
    }

    private static int resolveDialogTheme(Context context, int resId) {
        if (resId == 0) {
            return R.style.XDialogView_Large;
        }
        return resId;
    }

    @Override // com.xiaopeng.xui.app.XDialog
    @Deprecated
    public XDialog setNegativeButton(@Nullable CharSequence text, XDialogInterface.OnClickListener listener) {
        return super.setNegativeButton(text, this);
    }

    @Override // com.xiaopeng.xui.app.XDialog
    @Deprecated
    public XDialog setPositiveButton(@Nullable CharSequence text, XDialogInterface.OnClickListener listener) {
        return super.setPositiveButton(text, this);
    }

    public void setNegativeButtonText(String text) {
        setNegativeButton(text, this);
    }

    public void setPositiveButtonText(String text) {
        setPositiveButton(text, this);
    }

    public XNumberPicker getXNumberPicker() {
        return this.mNumberPicker;
    }

    public void setXNumberSetListener(OnXNumberSetListener listener) {
        this.mNumberSetListener = listener;
    }

    @Override // com.xiaopeng.xui.app.XDialogInterface.OnClickListener
    public void onClick(XDialog dialog, int which) {
        OnXNumberSetListener onXNumberSetListener;
        if (which == -2) {
            super.getDialog().cancel();
            this.mNumberPicker.setValue(2);
        } else if (which == -1 && (onXNumberSetListener = this.mNumberSetListener) != null) {
            XNumberPicker xNumberPicker = this.mNumberPicker;
            onXNumberSetListener.onXNumberSet(xNumberPicker, xNumberPicker.getDisplayedValueForCurrentSelection());
        }
    }

    @Override // com.xiaopeng.xui.widget.XNumberPicker.OnValueChangeListener
    public void onValueChange(XNumberPicker picker, int oldVal, int newVal) {
    }
}
