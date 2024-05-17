package com.xiaopeng.xui.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.app.XDialogInterface;
import com.xiaopeng.xui.widget.datepicker.XDatePicker;
import java.util.Calendar;
/* loaded from: classes25.dex */
public class XDatePickerDialog extends XDialog implements XDatePicker.OnDateChangedListener, XDialogInterface.OnClickListener {
    private final XDatePicker mDatePicker;
    private OnXDateSetListener mDateSetListener;

    /* loaded from: classes25.dex */
    public interface OnXDateSetListener {
        void onDateSet(XDatePicker xDatePicker, int i, int i2, int i3);
    }

    public XDatePickerDialog(@NonNull Context context) {
        this(context, R.style.XDialogView_Large, null, Calendar.getInstance(), -1, -1, -1);
    }

    public XDatePickerDialog(@NonNull Context context, @StyleRes int themeResId) {
        this(context, themeResId, null, Calendar.getInstance(), -1, -1, -1);
    }

    public XDatePickerDialog(@NonNull Context context, @Nullable OnXDateSetListener listener, int year, int month, int dayOfMonth) {
        this(context, R.style.XDialogView_Large, listener, null, year, month, dayOfMonth);
    }

    public XDatePickerDialog(@NonNull Context context, @StyleRes int themeResId, @Nullable OnXDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        this(context, themeResId, listener, null, year, monthOfYear, dayOfMonth);
    }

    private XDatePickerDialog(@NonNull Context context, @StyleRes int themeResId, @Nullable OnXDateSetListener listener, @Nullable Calendar calendar, int year, int monthOfYear, int dayOfMonth) {
        super(context, resolveDialogTheme(context, themeResId));
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.x_date_picker, getContentView(), false);
        setCustomView(view, false);
        setPositiveButton(" ", this);
        setNegativeButton(" ", this);
        if (calendar != null) {
            year = calendar.get(1);
            monthOfYear = calendar.get(2);
            dayOfMonth = calendar.get(5);
        }
        this.mDatePicker = (XDatePicker) view.findViewById(R.id.x_date_picker);
        this.mDatePicker.init(year, monthOfYear, dayOfMonth, this);
        this.mDateSetListener = listener;
    }

    public void setPositiveButtonText(String text) {
        setPositiveButton(text, this);
    }

    @Override // com.xiaopeng.xui.app.XDialog
    @Deprecated
    public XDialog setPositiveButton(@Nullable CharSequence text, XDialogInterface.OnClickListener listener) {
        return super.setPositiveButton(text, this);
    }

    public void setNegativeButtonText(String text) {
        setNegativeButton(text, this);
    }

    @Override // com.xiaopeng.xui.app.XDialog
    @Deprecated
    public XDialog setNegativeButton(@Nullable CharSequence text, XDialogInterface.OnClickListener listener) {
        return super.setNegativeButton(text, this);
    }

    public void updateDate(int year, int month, int dayOfMonth) {
        this.mDatePicker.updateDate(year, month, dayOfMonth);
    }

    public XDatePicker getXDatePicker() {
        return this.mDatePicker;
    }

    @StyleRes
    private static int resolveDialogTheme(@NonNull Context context, @StyleRes int themeResId) {
        if (themeResId == 0) {
            return R.style.XDialogView_Large;
        }
        return themeResId;
    }

    public void setOnXDateSetListener(OnXDateSetListener dateSetListener) {
        this.mDateSetListener = dateSetListener;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.OnDateChangedListener
    public void onDateChanged(XDatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.mDatePicker.init(year, monthOfYear, dayOfMonth, this);
    }

    @Override // com.xiaopeng.xui.app.XDialogInterface.OnClickListener
    public void onClick(XDialog dialog, int which) {
        if (which != -2) {
            if (which == -1 && this.mDateSetListener != null) {
                this.mDatePicker.clearFocus();
                OnXDateSetListener onXDateSetListener = this.mDateSetListener;
                XDatePicker xDatePicker = this.mDatePicker;
                onXDateSetListener.onDateSet(xDatePicker, xDatePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
                return;
            }
            return;
        }
        super.getDialog().cancel();
    }
}
