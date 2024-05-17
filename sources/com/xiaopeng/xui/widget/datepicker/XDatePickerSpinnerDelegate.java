package com.xiaopeng.xui.widget.datepicker;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.icu.util.Calendar;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XNumberPicker;
import com.xiaopeng.xui.widget.datepicker.XDatePicker;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
/* loaded from: classes25.dex */
public class XDatePickerSpinnerDelegate extends XDatePicker.AbstractXDatePickerDelegate {
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private static final int DEFAULT_END_YEAR = 2100;
    private static final boolean DEFAULT_SPINNERS_SHOWN = true;
    private static final int DEFAULT_START_YEAR = 1900;
    private final DateFormat mDateFormat;
    private XNumberPicker mDaySpinner;
    private boolean mIsEnabled;
    private Calendar mMaxDate;
    private Calendar mMinDate;
    private XNumberPicker mMonthSpinner;
    private int mNumberOfMonths;
    private String[] mShortMonths;
    private final LinearLayout mSpinners;
    private Calendar mTempDate;
    private XNumberPicker mYearSpinner;

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.AbstractXDatePickerDelegate, com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public /* bridge */ /* synthetic */ void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.AbstractXDatePickerDelegate, com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public /* bridge */ /* synthetic */ void setAutoFillChangeListener(XDatePicker.OnDateChangedListener onDateChangedListener) {
        super.setAutoFillChangeListener(onDateChangedListener);
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.AbstractXDatePickerDelegate, com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public /* bridge */ /* synthetic */ void setOnDateChangedListener(XDatePicker.OnDateChangedListener onDateChangedListener) {
        super.setOnDateChangedListener(onDateChangedListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XDatePickerSpinnerDelegate(XDatePicker delegator, Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(delegator, context);
        this.mDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        this.mIsEnabled = true;
        this.mXDelegator = delegator;
        this.mContext = context;
        setCurrentLocale(Locale.getDefault());
        TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.XDatePicker, defStyleAttr, defStyleRes);
        boolean spinnersShown = attributesArray.getBoolean(R.styleable.XDatePicker_dp_spinnersShown, true);
        int startYear = attributesArray.getInt(R.styleable.XDatePicker_dp_startYear, DEFAULT_START_YEAR);
        int endYear = attributesArray.getInt(R.styleable.XDatePicker_dp_endYear, DEFAULT_END_YEAR);
        String minDate = attributesArray.getString(R.styleable.XDatePicker_dp_minDate);
        String maxDate = attributesArray.getString(R.styleable.XDatePicker_dp_maxDate);
        int layoutResourceId = attributesArray.getResourceId(R.styleable.XDatePicker_dp_xDatePickerLayout, R.layout.x_date_picker_layout);
        attributesArray.recycle();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        View view = inflater.inflate(layoutResourceId, (ViewGroup) this.mXDelegator, true);
        view.setSaveFromParentEnabled(false);
        XNumberPicker.OnValueChangeListener onChangeListener = new XNumberPicker.OnValueChangeListener() { // from class: com.xiaopeng.xui.widget.datepicker.-$$Lambda$XDatePickerSpinnerDelegate$bvC71pUOc2APz4A8E3c-YsjbyLM
            @Override // com.xiaopeng.xui.widget.XNumberPicker.OnValueChangeListener
            public final void onValueChange(XNumberPicker xNumberPicker, int i, int i2) {
                XDatePickerSpinnerDelegate.this.lambda$new$0$XDatePickerSpinnerDelegate(xNumberPicker, i, i2);
            }
        };
        this.mSpinners = (LinearLayout) this.mXDelegator.findViewById(R.id.pickers);
        XNumberPicker spinner1 = (XNumberPicker) this.mXDelegator.findViewById(R.id.picker1);
        XNumberPicker spinner2 = (XNumberPicker) this.mXDelegator.findViewById(R.id.picker2);
        XNumberPicker spinner3 = (XNumberPicker) this.mXDelegator.findViewById(R.id.picker3);
        XNumberPicker[] pickers = {spinner1, spinner2, spinner3};
        reorderSpinners(pickers);
        this.mDaySpinner.setFormatter(XNumberPicker.getTwoDigitFormatter());
        this.mDaySpinner.setOnLongPressUpdateInterval(100L);
        this.mDaySpinner.setOnValueChangedListener(onChangeListener);
        this.mMonthSpinner.setMinValue(0);
        this.mMonthSpinner.setMaxValue(this.mNumberOfMonths - 1);
        this.mMonthSpinner.setDisplayedValues(this.mShortMonths);
        this.mMonthSpinner.setOnLongPressUpdateInterval(200L);
        this.mMonthSpinner.setOnValueChangedListener(onChangeListener);
        this.mYearSpinner.setOnLongPressUpdateInterval(100L);
        this.mYearSpinner.setOnValueChangedListener(onChangeListener);
        setSpinnersShown(spinnersShown);
        this.mTempDate.clear();
        if (!TextUtils.isEmpty(minDate)) {
            if (!parseDate(minDate, this.mTempDate)) {
                this.mTempDate.set(startYear, 0, 1);
            }
        } else {
            this.mTempDate.set(startYear, 0, 1);
        }
        setMinDate(this.mTempDate.getTimeInMillis());
        this.mTempDate.clear();
        if (!TextUtils.isEmpty(maxDate)) {
            if (!parseDate(maxDate, this.mTempDate)) {
                this.mTempDate.set(endYear, 11, 31);
            }
        } else {
            this.mTempDate.set(endYear, 11, 31);
        }
        setMaxDate(this.mTempDate.getTimeInMillis());
        this.mCurrentDate.setTimeInMillis(System.currentTimeMillis());
        init(this.mCurrentDate.get(1), this.mCurrentDate.get(2), this.mCurrentDate.get(5), null);
        if (this.mXDelegator.getImportantForAccessibility() == 0) {
            this.mXDelegator.setImportantForAccessibility(1);
        }
    }

    public /* synthetic */ void lambda$new$0$XDatePickerSpinnerDelegate(XNumberPicker picker, int oldVal, int newVal) {
        this.mTempDate.setTimeInMillis(this.mCurrentDate.getTimeInMillis());
        if (picker == this.mDaySpinner) {
            int maxDayOfMonth = this.mTempDate.getActualMaximum(5);
            if (oldVal == maxDayOfMonth && newVal == 1) {
                this.mTempDate.add(5, 1);
            } else if (oldVal == 1 && newVal == maxDayOfMonth) {
                this.mTempDate.add(5, -1);
            } else {
                this.mTempDate.add(5, newVal - oldVal);
            }
        } else if (picker == this.mMonthSpinner) {
            if (oldVal == 11 && newVal == 0) {
                this.mTempDate.add(2, 1);
            } else if (oldVal == 0 && newVal == 11) {
                this.mTempDate.add(2, -1);
            } else {
                this.mTempDate.add(2, newVal - oldVal);
            }
        } else if (picker == this.mYearSpinner) {
            this.mTempDate.set(1, newVal);
        } else {
            throw new IllegalArgumentException();
        }
        setDate(this.mTempDate.get(1), this.mTempDate.get(2), this.mTempDate.get(5));
        updateSpinners();
        notifyDateChanged();
    }

    private void notifyDateChanged() {
        this.mXDelegator.sendAccessibilityEvent(4);
        if (this.mOnDateChangedListener != null) {
            this.mOnDateChangedListener.onDateChanged(this.mXDelegator, getYear(), getMonth(), getDayOfMonth());
        }
        if (this.mAutoFillChangeListener != null) {
            this.mAutoFillChangeListener.onDateChanged(this.mXDelegator, getYear(), getMonth(), getDayOfMonth());
        }
    }

    private void reorderSpinners(XNumberPicker[] pickers) {
        String pattern = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyyMMMdd");
        char[] order = getDateFormatOrder(pattern);
        for (int i = 0; i < order.length; i++) {
            char c = order[i];
            if (c == 'M') {
                this.mMonthSpinner = pickers[i];
            } else if (c == 'd') {
                this.mDaySpinner = pickers[i];
            } else if (c == 'y') {
                this.mYearSpinner = pickers[i];
            } else {
                throw new IllegalArgumentException(Arrays.toString(order));
            }
        }
    }

    public static char[] getDateFormatOrder(String pattern) {
        char[] result = new char[3];
        int resultIndex = 0;
        boolean sawDay = false;
        boolean sawMonth = false;
        boolean sawYear = false;
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (ch == 'd' || ch == 'L' || ch == 'M' || ch == 'y') {
                if (ch == 'd' && !sawDay) {
                    result[resultIndex] = 'd';
                    sawDay = true;
                    resultIndex++;
                } else if ((ch == 'L' || ch == 'M') && !sawMonth) {
                    result[resultIndex] = 'M';
                    sawMonth = true;
                    resultIndex++;
                } else if (ch == 'y' && !sawYear) {
                    result[resultIndex] = 'y';
                    sawYear = true;
                    resultIndex++;
                }
            }
        }
        return result;
    }

    private void setDate(int year, int month, int dayOfMonth) {
        this.mCurrentDate.set(year, month, dayOfMonth);
        resetAutofilledValue();
        if (this.mCurrentDate.before(this.mMinDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
        } else if (this.mCurrentDate.after(this.mMaxDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
        }
    }

    private boolean parseDate(String date, Calendar outDate) {
        try {
            outDate.setTime(this.mDateFormat.parse(date));
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateSpinners() {
        if (this.mCurrentDate.equals(this.mMinDate)) {
            this.mDaySpinner.setMinValue(this.mCurrentDate.get(5));
            this.mDaySpinner.setMaxValue(this.mCurrentDate.getActualMaximum(5));
            this.mDaySpinner.setWrapSelectorWheel(false);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(this.mCurrentDate.get(2));
            this.mMonthSpinner.setMaxValue(this.mCurrentDate.getActualMaximum(2));
            this.mMonthSpinner.setWrapSelectorWheel(false);
        } else if (this.mCurrentDate.equals(this.mMaxDate)) {
            this.mDaySpinner.setMinValue(this.mCurrentDate.getActualMinimum(5));
            this.mDaySpinner.setMaxValue(this.mCurrentDate.get(5));
            this.mDaySpinner.setWrapSelectorWheel(false);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(this.mCurrentDate.getActualMinimum(2));
            this.mMonthSpinner.setMaxValue(this.mCurrentDate.get(2));
            this.mMonthSpinner.setWrapSelectorWheel(false);
        } else {
            this.mDaySpinner.setMinValue(1);
            this.mDaySpinner.setMaxValue(this.mCurrentDate.getActualMaximum(5));
            this.mDaySpinner.setWrapSelectorWheel(true);
            this.mMonthSpinner.setDisplayedValues(null);
            this.mMonthSpinner.setMinValue(0);
            this.mMonthSpinner.setMaxValue(11);
            this.mMonthSpinner.setWrapSelectorWheel(true);
        }
        String[] dayDisplayedValues = new String[this.mCurrentDate.getActualMaximum(5)];
        for (int i = 0; i < this.mCurrentDate.getActualMaximum(5); i++) {
            dayDisplayedValues[i] = this.mContext.getResources().getString(R.string.x_date_picker_day, Integer.valueOf(i + 1));
        }
        this.mDaySpinner.setDisplayedValues(dayDisplayedValues);
        String[] displayedValues = (String[]) Arrays.copyOfRange(this.mShortMonths, this.mMonthSpinner.getMinValue(), this.mMonthSpinner.getMaxValue() + 1);
        this.mMonthSpinner.setDisplayedValues(displayedValues);
        this.mYearSpinner.setMinValue(this.mMinDate.get(1));
        this.mYearSpinner.setMaxValue(this.mMaxDate.get(1));
        String[] yearDisplayValues = new String[201];
        for (int i2 = 0; i2 < yearDisplayValues.length; i2++) {
            yearDisplayValues[i2] = this.mContext.getResources().getString(R.string.x_date_picker_year, Integer.valueOf(this.mYearSpinner.getMinValue() + i2));
        }
        this.mYearSpinner.setDisplayedValues(yearDisplayValues);
        this.mYearSpinner.setWrapSelectorWheel(false);
        this.mYearSpinner.setValue(this.mCurrentDate.get(1));
        this.mMonthSpinner.setValue(this.mCurrentDate.get(2));
        this.mDaySpinner.setValue(this.mCurrentDate.get(5));
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public void init(int year, int monthOfYear, int dayOfMonth, XDatePicker.OnDateChangedListener onDateChangedListener) {
        setDate(year, monthOfYear, dayOfMonth);
        updateSpinners();
        this.mOnDateChangedListener = onDateChangedListener;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public void updateDate(int year, int month, int dayOfMonth) {
        if (!isNewDate(year, month, dayOfMonth)) {
            return;
        }
        setDate(year, month, dayOfMonth);
        updateSpinners();
        notifyDateChanged();
    }

    private boolean isNewDate(int year, int month, int dayOfMonth) {
        return (this.mCurrentDate.get(1) == year && this.mCurrentDate.get(2) == month && this.mCurrentDate.get(5) == dayOfMonth) ? false : true;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public int getYear() {
        return this.mCurrentDate.get(1);
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public int getMonth() {
        return this.mCurrentDate.get(2);
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public int getDayOfMonth() {
        return this.mCurrentDate.get(5);
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public void setFirstDayOfWeek(int firstDayOfWeek) {
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public int getFirstDayOfWeek() {
        return 0;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public void setMinDate(long minDate) {
        this.mTempDate.setTimeInMillis(minDate);
        if (this.mTempDate.get(1) == this.mMinDate.get(1) && this.mTempDate.get(6) == this.mMinDate.get(6)) {
            return;
        }
        this.mMinDate.setTimeInMillis(minDate);
        if (this.mCurrentDate.before(this.mMinDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
        }
        updateSpinners();
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public Calendar getMinDate() {
        Calendar calendar = this.mMinDate;
        if (calendar != null) {
            return calendar;
        }
        Calendar minDate = Calendar.getInstance();
        minDate.set(DEFAULT_START_YEAR, 1, 1);
        return minDate;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public void setMaxDate(long maxDate) {
        this.mTempDate.setTimeInMillis(maxDate);
        if (this.mTempDate.get(1) == this.mMaxDate.get(1) && this.mTempDate.get(6) == this.mMaxDate.get(6)) {
            return;
        }
        this.mMaxDate.setTimeInMillis(maxDate);
        if (this.mCurrentDate.after(this.mMaxDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
        }
        updateSpinners();
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public Calendar getMaxDate() {
        if (this.mMaxDate != null) {
            return this.mMinDate;
        }
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(DEFAULT_END_YEAR, 11, 30);
        return maxDate;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public void setEnabled(boolean enabled) {
        this.mDaySpinner.setEnabled(enabled);
        this.mMonthSpinner.setEnabled(enabled);
        this.mYearSpinner.setEnabled(enabled);
        this.mIsEnabled = enabled;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public void setSpinnersShown(boolean shown) {
        this.mSpinners.setVisibility(shown ? 0 : 8);
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public boolean getSpinnersShown() {
        return this.mSpinners.isShown();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.AbstractXDatePickerDelegate
    public void setCurrentLocale(Locale locale) {
        super.setCurrentLocale(locale);
        this.mTempDate = getCalendarForLocale(this.mTempDate, locale);
        this.mMinDate = getCalendarForLocale(this.mMinDate, locale);
        this.mMaxDate = getCalendarForLocale(this.mMaxDate, locale);
        this.mCurrentDate = getCalendarForLocale(this.mCurrentDate, locale);
        this.mNumberOfMonths = this.mTempDate.getActualMaximum(2) + 1;
        this.mShortMonths = new DateFormatSymbols().getShortMonths();
    }

    private Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
        if (oldCalendar == null) {
            return Calendar.getInstance(locale);
        }
        long currentTimeMillis = oldCalendar.getTimeInMillis();
        Calendar newCalendar = Calendar.getInstance(locale);
        newCalendar.setTimeInMillis(currentTimeMillis);
        return newCalendar;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public void onConfigurationChanged(Configuration newConfig) {
        setCurrentLocale(newConfig.getLocales().get(0));
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public Parcelable onSaveInstanceState(Parcelable superState) {
        return new XDatePicker.AbstractXDatePickerDelegate.SavedState(superState, getYear(), getMonth(), getDayOfMonth(), getMinDate().getTimeInMillis(), getMaxDate().getTimeInMillis());
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof XDatePicker.AbstractXDatePickerDelegate.SavedState) {
            XDatePicker.AbstractXDatePickerDelegate.SavedState ss = (XDatePicker.AbstractXDatePickerDelegate.SavedState) state;
            setDate(ss.getSelectedYear(), ss.getSelectedMonth(), ss.getSelectedDay());
            updateSpinners();
        }
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.XDatePickerDelegate
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }
}
