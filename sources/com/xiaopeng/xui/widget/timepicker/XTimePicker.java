package com.xiaopeng.xui.widget.timepicker;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.math.MathUtils;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.widget.XFrameLayout;
import java.util.Locale;
/* loaded from: classes25.dex */
public class XTimePicker extends XFrameLayout {
    private static final String LOG_TAG = XTimePicker.class.getSimpleName();
    private final XTimePickerDelegate mDelegate;

    /* loaded from: classes25.dex */
    public interface OnTimeChangedListener {
        void onTimeChanged(XTimePicker xTimePicker, int i, int i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes25.dex */
    public interface XTimePickerDelegate {
        void autofill(AutofillValue autofillValue);

        boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent);

        AutofillValue getAutofillValue();

        int getBaseline();

        int getHour();

        int getMinute();

        boolean isEnabled();

        void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent);

        void onRestoreInstanceState(Parcelable parcelable);

        Parcelable onSaveInstanceState(Parcelable parcelable);

        void setAutoFillChangeListener(OnTimeChangedListener onTimeChangedListener);

        void setDate(@IntRange(from = 0, to = 23) int i, @IntRange(from = 0, to = 59) int i2);

        void setEnabled(boolean z);

        void setHour(@IntRange(from = 0, to = 23) int i);

        void setMinute(@IntRange(from = 0, to = 59) int i);

        void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener);
    }

    public XTimePicker(Context context) {
        this(context, null);
    }

    public XTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.XTimePicker);
    }

    public XTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (getImportantForAutofill() == 0) {
            setImportantForAutofill(1);
        }
        this.mDelegate = new XTimePickerSpinnerDelegate(this, context, attrs, defStyleAttr, defStyleRes);
        this.mDelegate.setAutoFillChangeListener(new OnTimeChangedListener() { // from class: com.xiaopeng.xui.widget.timepicker.XTimePicker.1
            @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.OnTimeChangedListener
            public void onTimeChanged(XTimePicker view, int hourOfDay, int minute) {
                AutofillManager afm = (AutofillManager) XTimePicker.this.getContext().getSystemService(AutofillManager.class);
                if (afm != null) {
                    afm.notifyValueChanged(XTimePicker.this);
                }
            }
        });
    }

    public void setHour(@IntRange(from = 0, to = 23) int hour) {
        this.mDelegate.setHour(MathUtils.clamp(hour, 0, 23));
    }

    public int getHour() {
        return this.mDelegate.getHour();
    }

    public void setMinute(@IntRange(from = 0, to = 59) int minute) {
        this.mDelegate.setMinute(MathUtils.clamp(minute, 0, 59));
    }

    public int getMinute() {
        return this.mDelegate.getMinute();
    }

    public void setCurrentHour(@NonNull Integer currentHour) {
        setHour(currentHour.intValue());
    }

    @NonNull
    public Integer getCurrentHour() {
        return Integer.valueOf(getHour());
    }

    public void setCurrentMinute(@NonNull Integer currentMinute) {
        setMinute(currentMinute.intValue());
    }

    @NonNull
    public Integer getCurrentMinute() {
        return Integer.valueOf(getMinute());
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        this.mDelegate.setOnTimeChangedListener(onTimeChangedListener);
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mDelegate.setEnabled(enabled);
    }

    @Override // android.view.View
    public boolean isEnabled() {
        return this.mDelegate.isEnabled();
    }

    @Override // android.view.View
    public int getBaseline() {
        return this.mDelegate.getBaseline();
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return this.mDelegate.onSaveInstanceState(superState);
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        View.BaseSavedState ss = (View.BaseSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mDelegate.onRestoreInstanceState(ss);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return XTimePicker.class.getName();
    }

    /* loaded from: classes25.dex */
    static abstract class AbstractTimePickerDelegate implements XTimePickerDelegate {
        protected OnTimeChangedListener mAutoFillChangeListener;
        private long mAutofilledValue;
        protected final Context mContext;
        protected final XTimePicker mDelegator;
        protected final Locale mLocale;
        protected OnTimeChangedListener mOnTimeChangedListener;

        public AbstractTimePickerDelegate(@NonNull XTimePicker delegator, @NonNull Context context) {
            this.mDelegator = delegator;
            this.mContext = context;
            this.mLocale = context.getResources().getConfiguration().getLocales().get(0);
        }

        @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
        public void setOnTimeChangedListener(OnTimeChangedListener callback) {
            this.mOnTimeChangedListener = callback;
        }

        @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
        public void setAutoFillChangeListener(OnTimeChangedListener callback) {
            this.mAutoFillChangeListener = callback;
        }

        @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
        public final void autofill(AutofillValue value) {
            if (value == null || !value.isDate()) {
                String str = XTimePicker.LOG_TAG;
                XLogUtils.w(str, value + " could not be autofilled into " + this);
                return;
            }
            long time = value.getDateValue();
            Calendar cal = Calendar.getInstance(this.mLocale);
            cal.setTimeInMillis(time);
            setDate(cal.get(11), cal.get(12));
            this.mAutofilledValue = time;
        }

        @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.XTimePickerDelegate
        public final AutofillValue getAutofillValue() {
            long j = this.mAutofilledValue;
            if (j != 0) {
                return AutofillValue.forDate(j);
            }
            Calendar cal = Calendar.getInstance(this.mLocale);
            cal.set(11, getHour());
            cal.set(12, getMinute());
            return AutofillValue.forDate(cal.getTimeInMillis());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void resetAutofilledValue() {
            this.mAutofilledValue = 0L;
        }

        /* loaded from: classes25.dex */
        protected static class SavedState extends View.BaseSavedState {
            private final int mCurrentItemShowing;
            private final int mHour;
            private final int mMinute;

            public SavedState(Parcelable superState, int hour, int minute) {
                this(superState, hour, minute, 0);
            }

            public SavedState(Parcelable superState, int hour, int minute, int currentItemShowing) {
                super(superState);
                this.mHour = hour;
                this.mMinute = minute;
                this.mCurrentItemShowing = currentItemShowing;
            }

            private SavedState(Parcel in) {
                super(in);
                this.mHour = in.readInt();
                this.mMinute = in.readInt();
                this.mCurrentItemShowing = in.readInt();
            }

            public int getHour() {
                return this.mHour;
            }

            public int getMinute() {
                return this.mMinute;
            }

            @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeInt(this.mHour);
                dest.writeInt(this.mMinute);
                dest.writeInt(this.mCurrentItemShowing);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchProvideAutofillStructure(ViewStructure structure, int flags) {
        structure.setAutofillId(getAutofillId());
        onProvideAutofillStructure(structure, flags);
    }

    @Override // android.view.View
    public void autofill(AutofillValue value) {
        if (isEnabled()) {
            this.mDelegate.autofill(value);
        }
    }

    @Override // android.view.View
    public AutofillValue getAutofillValue() {
        if (isEnabled()) {
            return this.mDelegate.getAutofillValue();
        }
        return null;
    }
}
