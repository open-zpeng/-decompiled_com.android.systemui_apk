package com.xiaopeng.xui.widget.timepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.icu.util.Calendar;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XNumberPicker;
import com.xiaopeng.xui.widget.timepicker.XTimePicker;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes25.dex */
public class XTimePickerSpinnerDelegate extends XTimePicker.AbstractTimePickerDelegate {
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private final XNumberPicker mHourSpinner;
    private boolean mIsEnabled;
    private final XNumberPicker mMinuteSpinner;
    private final Calendar mTempCalendar;

    public XTimePickerSpinnerDelegate(XTimePicker delegate, Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(delegate, context);
        this.mIsEnabled = true;
        TypedArray a = this.mContext.obtainStyledAttributes(attrs, R.styleable.XTimePicker, defStyleAttr, defStyleRes);
        int layoutResourceId = a.getResourceId(R.styleable.XTimePicker_tp_xTimePickerLayout, R.layout.x_time_picker_layout);
        a.recycle();
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View view = inflater.inflate(layoutResourceId, (ViewGroup) this.mDelegator, true);
        view.setSaveFromParentEnabled(false);
        this.mHourSpinner = (XNumberPicker) delegate.findViewById(R.id.hour);
        this.mHourSpinner.setMinValue(0);
        this.mHourSpinner.setMaxValue(23);
        this.mHourSpinner.setOnLongPressUpdateInterval(100L);
        String[] hourDisplayValues = new String[24];
        for (int i = 0; i < hourDisplayValues.length; i++) {
            hourDisplayValues[i] = context.getResources().getString(R.string.x_time_picker_hour, Integer.valueOf(i));
        }
        this.mHourSpinner.setDisplayedValues(hourDisplayValues);
        this.mHourSpinner.setOnValueChangedListener(new XNumberPicker.OnValueChangeListener() { // from class: com.xiaopeng.xui.widget.timepicker.XTimePickerSpinnerDelegate.1
            @Override // com.xiaopeng.xui.widget.XNumberPicker.OnValueChangeListener
            public void onValueChange(XNumberPicker spinner, int oldVal, int newVal) {
                XTimePickerSpinnerDelegate.this.onTimeChanged();
            }
        });
        this.mMinuteSpinner = (XNumberPicker) this.mDelegator.findViewById(R.id.minute);
        this.mMinuteSpinner.setMinValue(0);
        this.mMinuteSpinner.setMaxValue(59);
        this.mMinuteSpinner.setOnLongPressUpdateInterval(100L);
        this.mMinuteSpinner.setFormatter(XNumberPicker.getTwoDigitFormatter());
        String[] minuteDisplayValues = new String[60];
        int i2 = 0;
        while (i2 < minuteDisplayValues.length) {
            minuteDisplayValues[i2] = context.getResources().getString(R.string.x_time_picker_minute, Integer.valueOf(i2));
            i2++;
            a = a;
        }
        this.mMinuteSpinner.setDisplayedValues(minuteDisplayValues);
        this.mMinuteSpinner.setOnValueChangedListener(new XNumberPicker.OnValueChangeListener() { // from class: com.xiaopeng.xui.widget.timepicker.XTimePickerSpinnerDelegate.2
            @Override // com.xiaopeng.xui.widget.XNumberPicker.OnValueChangeListener
            public void onValueChange(XNumberPicker spinner, int oldVal, int newVal) {
                int minValue = XTimePickerSpinnerDelegate.this.mMinuteSpinner.getMinValue();
                int maxValue = XTimePickerSpinnerDelegate.this.mMinuteSpinner.getMaxValue();
                if (oldVal == maxValue && newVal == minValue) {
                    int newHour = XTimePickerSpinnerDelegate.this.mHourSpinner.getValue() + 1;
                    XTimePickerSpinnerDelegate.this.mHourSpinner.setValue(newHour);
                } else if (oldVal == minValue && newVal == maxValue) {
                    int newHour2 = XTimePickerSpinnerDelegate.this.mHourSpinner.getValue() - 1;
                    XTimePickerSpinnerDelegate.this.mHourSpinner.setValue(newHour2);
                }
                XTimePickerSpinnerDelegate.this.onTimeChanged();
            }
        });
        this.mTempCalendar = Calendar.getInstance(this.mLocale);
        setHour(this.mTempCalendar.get(11));
        setMinute(this.mTempCalendar.get(12));
        if (!isEnabled()) {
            setEnabled(false);
        }
        if (this.mDelegator.getImportantForAccessibility() == 0) {
            this.mDelegator.setImportantForAccessibility(1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeChanged() {
        this.mDelegator.sendAccessibilityEvent(4);
        if (this.mOnTimeChangedListener != null) {
            this.mOnTimeChangedListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
        }
        if (this.mAutoFillChangeListener != null) {
            this.mAutoFillChangeListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
        }
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public void setHour(int hour) {
        setCurrentHour(hour, true);
    }

    private void setCurrentHour(int currentHour, boolean notifyTimeChanged) {
        if (currentHour == getHour()) {
            return;
        }
        resetAutofilledValue();
        this.mHourSpinner.setValue(currentHour);
        if (notifyTimeChanged) {
            onTimeChanged();
        }
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public int getHour() {
        return this.mHourSpinner.getValue();
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public void setMinute(int minute) {
        setCurrentMinute(minute, true);
    }

    private void setCurrentMinute(int minute, boolean notifyTimeChanged) {
        if (minute == getMinute()) {
            return;
        }
        resetAutofilledValue();
        this.mMinuteSpinner.setValue(minute);
        if (notifyTimeChanged) {
            onTimeChanged();
        }
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public int getMinute() {
        return this.mMinuteSpinner.getValue();
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public void setDate(int hour, int minute) {
        setCurrentHour(hour, false);
        setCurrentMinute(minute, false);
        onTimeChanged();
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public void setEnabled(boolean enabled) {
        this.mMinuteSpinner.setEnabled(enabled);
        this.mHourSpinner.setEnabled(enabled);
        this.mIsEnabled = enabled;
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public int getBaseline() {
        return this.mHourSpinner.getBaseline();
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public Parcelable onSaveInstanceState(Parcelable superState) {
        return new XTimePicker.AbstractTimePickerDelegate.SavedState(superState, getHour(), getMinute());
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof XTimePicker.AbstractTimePickerDelegate.SavedState) {
            XTimePicker.AbstractTimePickerDelegate.SavedState ss = (XTimePicker.AbstractTimePickerDelegate.SavedState) state;
            setHour(ss.getHour());
            setMinute(ss.getMinute());
        }
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        int flags = 1 | 128;
        this.mTempCalendar.set(11, getHour());
        this.mTempCalendar.set(12, getMinute());
        String selectedDateUtterance = DateUtils.formatDateTime(this.mContext, this.mTempCalendar.getTimeInMillis(), flags);
        event.getText().add(selectedDateUtterance);
    }
}
