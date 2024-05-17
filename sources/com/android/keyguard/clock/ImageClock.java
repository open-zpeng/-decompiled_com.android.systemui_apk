package com.android.keyguard.clock;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.systemui.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
/* loaded from: classes19.dex */
public class ImageClock extends FrameLayout {
    private String mDescFormat;
    private ImageView mHourHand;
    private ImageView mMinuteHand;
    private final Calendar mTime;
    private TimeZone mTimeZone;

    public ImageClock(Context context) {
        this(context, null);
    }

    public ImageClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTime = Calendar.getInstance(TimeZone.getDefault());
        this.mDescFormat = ((SimpleDateFormat) DateFormat.getTimeFormat(context)).toLocalizedPattern();
    }

    public void onTimeChanged() {
        this.mTime.setTimeInMillis(System.currentTimeMillis());
        float hourAngle = (this.mTime.get(10) * 30.0f) + (this.mTime.get(12) * 0.5f);
        this.mHourHand.setRotation(hourAngle);
        float minuteAngle = this.mTime.get(12) * 6.0f;
        this.mMinuteHand.setRotation(minuteAngle);
        setContentDescription(DateFormat.format(this.mDescFormat, this.mTime));
        invalidate();
    }

    public void onTimeZoneChanged(TimeZone timeZone) {
        this.mTimeZone = timeZone;
        this.mTime.setTimeZone(timeZone);
    }

    public void setClockColors(int dark, int light) {
        this.mHourHand.setColorFilter(dark);
        this.mMinuteHand.setColorFilter(light);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHourHand = (ImageView) findViewById(R.id.hour_hand);
        this.mMinuteHand = (ImageView) findViewById(R.id.minute_hand);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Calendar calendar = this.mTime;
        TimeZone timeZone = this.mTimeZone;
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        calendar.setTimeZone(timeZone);
        onTimeChanged();
    }
}
