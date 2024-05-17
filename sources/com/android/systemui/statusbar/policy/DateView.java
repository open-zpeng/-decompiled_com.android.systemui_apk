package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.policy.DateView;
import java.util.Date;
import java.util.Locale;
/* loaded from: classes21.dex */
public class DateView extends TextView {
    private static final String TAG = "DateView";
    private final Date mCurrentTime;
    private DateFormat mDateFormat;
    private String mDatePattern;
    private BroadcastReceiver mIntentReceiver;
    private String mLastText;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.policy.DateView$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.TIME_TICK".equals(action) || "android.intent.action.TIME_SET".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action) || "android.intent.action.LOCALE_CHANGED".equals(action)) {
                if ("android.intent.action.LOCALE_CHANGED".equals(action) || "android.intent.action.TIMEZONE_CHANGED".equals(action)) {
                    DateView.this.getHandler().post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$DateView$1$I-3qZI4QmwEIAfQqo2b2oUNiPII
                        @Override // java.lang.Runnable
                        public final void run() {
                            DateView.AnonymousClass1.this.lambda$onReceive$0$DateView$1();
                        }
                    });
                }
                DateView.this.getHandler().post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$DateView$1$v1y3JoGtv68dyea2Bk7AdwrkpMI
                    @Override // java.lang.Runnable
                    public final void run() {
                        DateView.AnonymousClass1.this.lambda$onReceive$1$DateView$1();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onReceive$0$DateView$1() {
            DateView.this.mDateFormat = null;
        }

        public /* synthetic */ void lambda$onReceive$1$DateView$1() {
            DateView.this.updateClock();
        }
    }

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentTime = new Date();
        this.mIntentReceiver = new AnonymousClass1();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DateView, 0, 0);
        try {
            this.mDatePattern = a.getString(R.styleable.DateView_datePattern);
            a.recycle();
            if (this.mDatePattern == null) {
                this.mDatePattern = getContext().getString(R.string.system_ui_date_pattern);
            }
        } catch (Throwable th) {
            a.recycle();
            throw th;
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        filter.addAction("android.intent.action.LOCALE_CHANGED");
        getContext().registerReceiver(this.mIntentReceiver, filter, null, (Handler) Dependency.get(Dependency.TIME_TICK_HANDLER));
        updateClock();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mDateFormat = null;
        getContext().unregisterReceiver(this.mIntentReceiver);
    }

    protected void updateClock() {
        if (this.mDateFormat == null) {
            Locale l = Locale.getDefault();
            DateFormat format = DateFormat.getInstanceForSkeleton(this.mDatePattern, l);
            format.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
            this.mDateFormat = format;
        }
        this.mCurrentTime.setTime(System.currentTimeMillis());
        String text = this.mDateFormat.format(this.mCurrentTime);
        if (!text.equals(this.mLastText)) {
            setText(text);
            this.mLastText = text;
        }
    }

    public void setDatePattern(String pattern) {
        if (TextUtils.equals(pattern, this.mDatePattern)) {
            return;
        }
        this.mDatePattern = pattern;
        this.mDateFormat = null;
        if (isAttachedToWindow()) {
            updateClock();
        }
    }
}
