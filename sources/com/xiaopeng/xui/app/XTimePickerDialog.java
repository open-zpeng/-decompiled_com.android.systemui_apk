package com.xiaopeng.xui.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.app.XDialogInterface;
import com.xiaopeng.xui.widget.timepicker.XTimePicker;
/* loaded from: classes25.dex */
public class XTimePickerDialog extends XDialog implements XTimePicker.OnTimeChangedListener, XDialogInterface.OnClickListener {
    private final XTimePicker mTimePicker;
    private final OnXTimeSetListener mTimeSetListener;

    /* loaded from: classes25.dex */
    public interface OnXTimeSetListener {
        void onTimeSet(XTimePicker xTimePicker, int i, int i2);
    }

    public XTimePickerDialog(@NonNull Context context, @Nullable OnXTimeSetListener listener, int hourOfDay, int minute) {
        this(context, 0, listener, hourOfDay, minute);
    }

    public XTimePickerDialog(@NonNull Context context, @StyleRes int themeResId, @Nullable OnXTimeSetListener listener, int hourOfDay, int minute) {
        super(context, resolveDialogTheme(context, themeResId));
        this.mTimeSetListener = listener;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.x_time_picker, getContentView(), false);
        setCustomView(view, false);
        setPositiveButton(" ", this);
        setNegativeButton(" ", this);
        this.mTimePicker = (XTimePicker) view.findViewById(R.id.x_time_picker);
        this.mTimePicker.setCurrentHour(Integer.valueOf(hourOfDay));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(minute));
        this.mTimePicker.setOnTimeChangedListener(this);
    }

    static int resolveDialogTheme(Context context, int resId) {
        if (resId == 0) {
            return R.style.XDialogView_Large;
        }
        return resId;
    }

    public XTimePicker getXTimePicker() {
        return this.mTimePicker;
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

    public void updateTime(int hourOfDay, int minuteOfHour) {
        this.mTimePicker.setCurrentHour(Integer.valueOf(hourOfDay));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(minuteOfHour));
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.OnTimeChangedListener
    public void onTimeChanged(XTimePicker view, int hourOfDay, int minute) {
    }

    @Override // com.xiaopeng.xui.app.XDialogInterface.OnClickListener
    public void onClick(XDialog dialog, int which) {
        OnXTimeSetListener onXTimeSetListener;
        if (which == -2) {
            super.getDialog().cancel();
        } else if (which == -1 && (onXTimeSetListener = this.mTimeSetListener) != null) {
            XTimePicker xTimePicker = this.mTimePicker;
            onXTimeSetListener.onTimeSet(xTimePicker, xTimePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
        }
    }
}
