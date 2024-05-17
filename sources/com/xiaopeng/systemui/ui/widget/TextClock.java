package com.xiaopeng.systemui.ui.widget;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.SystemController;
import com.xiaopeng.systemui.infoflow.util.TimeUtils;
import com.xiaopeng.systemui.ui.widget.TextClock;
import com.xiaopeng.systemui.utils.Utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes24.dex */
public class TextClock extends AnimatedTextView implements SystemController.OnTimeFormatChangedListener {
    private static final String TAG = "TextClock";
    private boolean mAttached;
    private Calendar mCalendar;
    private String mClockFormat;
    private String mDateFormat;
    private Handler mHandler;
    private final BroadcastReceiver mIntentReceiver;
    private Locale mLocale;
    private String mTimeFormat;
    private String mWeekFormat;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.ui.widget.TextClock$1  reason: invalid class name */
    /* loaded from: classes24.dex */
    public class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.d(TextClock.TAG, "onReceive action=" + action);
            if (action.equals("android.intent.action.TIMEZONE_CHANGED")) {
                final String tz = intent.getStringExtra("time-zone");
                TextClock.this.getHandler().post(new Runnable() { // from class: com.xiaopeng.systemui.ui.widget.-$$Lambda$TextClock$1$wX9LrnL3gUjn3ir3ApyODvxo4iE
                    @Override // java.lang.Runnable
                    public final void run() {
                        TextClock.AnonymousClass1.this.lambda$onReceive$0$TextClock$1(tz);
                    }
                });
            } else if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                final Locale newLocale = TextClock.this.getResources().getConfiguration().locale;
                TextClock.this.getHandler().post(new Runnable() { // from class: com.xiaopeng.systemui.ui.widget.-$$Lambda$TextClock$1$RiDesvwjWfx79hKcpOy8j39Dci4
                    @Override // java.lang.Runnable
                    public final void run() {
                        TextClock.AnonymousClass1.this.lambda$onReceive$1$TextClock$1(newLocale);
                    }
                });
            }
            TextClock.this.getHandler().post(new Runnable() { // from class: com.xiaopeng.systemui.ui.widget.-$$Lambda$TextClock$1$jXv_01jjouM5t7dxLxdK_ZM7ysQ
                @Override // java.lang.Runnable
                public final void run() {
                    TextClock.AnonymousClass1.this.lambda$onReceive$2$TextClock$1();
                }
            });
        }

        public /* synthetic */ void lambda$onReceive$0$TextClock$1(String tz) {
            TextClock.this.mCalendar = Calendar.getInstance(TimeZone.getTimeZone(tz));
        }

        public /* synthetic */ void lambda$onReceive$1$TextClock$1(Locale newLocale) {
            if (!newLocale.equals(TextClock.this.mLocale)) {
                TextClock.this.mLocale = newLocale;
            }
        }

        public /* synthetic */ void lambda$onReceive$2$TextClock$1() {
            TextClock.this.lambda$onTimeFormatChanged$0$TextClock();
        }
    }

    public TextClock(Context context) {
        this(context, null);
    }

    public TextClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mWeekFormat = "E";
        this.mHandler = new Handler();
        this.mIntentReceiver = new AnonymousClass1();
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TextClock, 0, 0);
        this.mClockFormat = attributes.getString(0);
        attributes.recycle();
        this.mDateFormat = getContext().getString(R.string.clock_date_format);
        SystemController.getInstance(context).addOnTimeFormatChangeListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.AnimatedTextView, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mAttached) {
            this.mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.TIME_TICK");
            filter.addAction("android.intent.action.TIME_SET");
            filter.addAction("android.intent.action.TIMEZONE_CHANGED");
            filter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            getContext().registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, filter, null, (Handler) Dependency.get(Dependency.TIME_TICK_HANDLER));
        }
        this.mCalendar = Calendar.getInstance(TimeZone.getDefault());
        lambda$onTimeFormatChanged$0$TextClock();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.AnimatedTextView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttached) {
            getContext().unregisterReceiver(this.mIntentReceiver);
            this.mAttached = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateClock */
    public final void lambda$onTimeFormatChanged$0$TextClock() {
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        setText(getClockContent());
    }

    private final CharSequence getClockContent() {
        Locale locale;
        String[] formats;
        Context context = getContext();
        String format = TextUtils.isEmpty(this.mClockFormat) ? "D:T" : this.mClockFormat;
        String dateFormat = this.mDateFormat;
        StringBuffer buffer = new StringBuffer("");
        if (Utils.isChineseLanguage()) {
            locale = Locale.getDefault();
        } else {
            locale = Locale.US;
        }
        Date date = this.mCalendar.getTime();
        if (!TextUtils.isEmpty(format) && format.contains(NavigationBarInflaterView.KEY_IMAGE_DELIM) && (formats = format.split(NavigationBarInflaterView.KEY_IMAGE_DELIM)) != null && formats.length > 0) {
            int length = formats.length;
            int i = 0;
            int i2 = 0;
            while (i2 < length) {
                String value = formats[i2];
                char c = value.charAt(i);
                if (c == 'D') {
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, locale);
                    buffer.append(getFormatContent(sdf.format(date)));
                    buffer.append(" ");
                } else if (c == 'E') {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("E", locale);
                    buffer.append(sdf2.format(date));
                    buffer.append(" ");
                } else if (c == 'T') {
                    buffer.append(TimeUtils.getFormatTimeString(context, date));
                    buffer.append(" ");
                }
                i2++;
                i = 0;
            }
        }
        return buffer;
    }

    private final CharSequence getFormatContent(String value) {
        if (!TextUtils.isEmpty(value)) {
            StringBuffer buffer = new StringBuffer("");
            for (int i = 0; i < value.length(); i++) {
                boolean skip = false;
                char c = value.charAt(i);
                if (c == '0') {
                    if (i == 0) {
                        skip = true;
                    } else {
                        char prev = value.charAt(i - 1);
                        if (!Character.isDigit(prev)) {
                            skip = true;
                        }
                    }
                }
                if (!skip) {
                    buffer.append(c);
                }
            }
            return buffer.toString();
        }
        return value;
    }

    @Override // com.xiaopeng.systemui.controller.SystemController.OnTimeFormatChangedListener
    public void onTimeFormatChanged() {
        getHandler().post(new Runnable() { // from class: com.xiaopeng.systemui.ui.widget.-$$Lambda$TextClock$NhR8ZaNuAgt3Tu_uhBSU6LyngdY
            @Override // java.lang.Runnable
            public final void run() {
                TextClock.this.lambda$onTimeFormatChanged$0$TextClock();
            }
        });
    }
}
