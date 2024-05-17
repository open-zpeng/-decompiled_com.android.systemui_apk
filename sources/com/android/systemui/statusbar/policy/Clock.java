package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.CharacterStyle;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import androidx.slice.core.SliceHints;
import com.android.settingslib.Utils;
import com.android.systemui.DemoMode;
import com.android.systemui.Dependency;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.Clock;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import libcore.icu.LocaleData;
/* loaded from: classes21.dex */
public class Clock extends TextView implements DemoMode, TunerService.Tunable, CommandQueue.Callbacks, DarkIconDispatcher.DarkReceiver, ConfigurationController.ConfigurationListener {
    private static final int AM_PM_STYLE_GONE = 2;
    private static final int AM_PM_STYLE_NORMAL = 0;
    private static final int AM_PM_STYLE_SMALL = 1;
    public static final String CLOCK_SECONDS = "clock_seconds";
    private static final String CLOCK_SUPER_PARCELABLE = "clock_super_parcelable";
    private static final String CURRENT_USER_ID = "current_user_id";
    private static final String SHOW_SECONDS = "show_seconds";
    private static final String TAG = "StatusBarClock";
    private static final String VISIBILITY = "visibility";
    private static final String VISIBLE_BY_POLICY = "visible_by_policy";
    private static final String VISIBLE_BY_USER = "visible_by_user";
    private final int mAmPmStyle;
    private boolean mAttached;
    private Calendar mCalendar;
    private SimpleDateFormat mClockFormat;
    private String mClockFormatString;
    private boolean mClockVisibleByPolicy;
    private boolean mClockVisibleByUser;
    private SimpleDateFormat mContentDescriptionFormat;
    private int mCurrentUserId;
    private final CurrentUserTracker mCurrentUserTracker;
    private boolean mDemoMode;
    private final BroadcastReceiver mIntentReceiver;
    private Locale mLocale;
    private int mNonAdaptedColor;
    private final BroadcastReceiver mScreenReceiver;
    private final Runnable mSecondTick;
    private Handler mSecondsHandler;
    private final boolean mShowDark;
    private boolean mShowSeconds;
    private boolean mUseWallpaperTextColor;

    public Clock(Context context) {
        this(context, null);
    }

    public Clock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Clock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mClockVisibleByPolicy = true;
        this.mClockVisibleByUser = true;
        this.mIntentReceiver = new AnonymousClass2();
        this.mScreenReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.policy.Clock.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if ("android.intent.action.SCREEN_OFF".equals(action)) {
                    if (Clock.this.mSecondsHandler != null) {
                        Clock.this.mSecondsHandler.removeCallbacks(Clock.this.mSecondTick);
                    }
                } else if ("android.intent.action.SCREEN_ON".equals(action) && Clock.this.mSecondsHandler != null) {
                    Clock.this.mSecondsHandler.postAtTime(Clock.this.mSecondTick, ((SystemClock.uptimeMillis() / 1000) * 1000) + 1000);
                }
            }
        };
        this.mSecondTick = new Runnable() { // from class: com.android.systemui.statusbar.policy.Clock.4
            @Override // java.lang.Runnable
            public void run() {
                if (Clock.this.mCalendar != null) {
                    Clock.this.updateClock();
                }
                Clock.this.mSecondsHandler.postAtTime(this, ((SystemClock.uptimeMillis() / 1000) * 1000) + 1000);
            }
        };
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Clock, 0, 0);
        try {
            this.mAmPmStyle = a.getInt(R.styleable.Clock_amPmStyle, 2);
            this.mShowDark = a.getBoolean(R.styleable.Clock_showDark, true);
            this.mNonAdaptedColor = getCurrentTextColor();
            a.recycle();
            this.mCurrentUserTracker = new CurrentUserTracker(context) { // from class: com.android.systemui.statusbar.policy.Clock.1
                @Override // com.android.systemui.settings.CurrentUserTracker
                public void onUserSwitched(int newUserId) {
                    Clock.this.mCurrentUserId = newUserId;
                }
            };
        } catch (Throwable th) {
            a.recycle();
            throw th;
        }
    }

    @Override // android.widget.TextView, android.view.View
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CLOCK_SUPER_PARCELABLE, super.onSaveInstanceState());
        bundle.putInt(CURRENT_USER_ID, this.mCurrentUserId);
        bundle.putBoolean(VISIBLE_BY_POLICY, this.mClockVisibleByPolicy);
        bundle.putBoolean(VISIBLE_BY_USER, this.mClockVisibleByUser);
        bundle.putBoolean(SHOW_SECONDS, this.mShowSeconds);
        bundle.putInt(VISIBILITY, getVisibility());
        return bundle;
    }

    @Override // android.widget.TextView, android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        if (state == null || !(state instanceof Bundle)) {
            super.onRestoreInstanceState(state);
            return;
        }
        Bundle bundle = (Bundle) state;
        Parcelable superState = bundle.getParcelable(CLOCK_SUPER_PARCELABLE);
        super.onRestoreInstanceState(superState);
        if (bundle.containsKey(CURRENT_USER_ID)) {
            this.mCurrentUserId = bundle.getInt(CURRENT_USER_ID);
        }
        this.mClockVisibleByPolicy = bundle.getBoolean(VISIBLE_BY_POLICY, true);
        this.mClockVisibleByUser = bundle.getBoolean(VISIBLE_BY_USER, true);
        this.mShowSeconds = bundle.getBoolean(SHOW_SECONDS, false);
        if (bundle.containsKey(VISIBILITY)) {
            super.setVisibility(bundle.getInt(VISIBILITY));
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mAttached) {
            this.mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.TIME_TICK");
            filter.addAction("android.intent.action.TIME_SET");
            filter.addAction("android.intent.action.TIMEZONE_CHANGED");
            filter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            filter.addAction("android.intent.action.USER_SWITCHED");
            getContext().registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, filter, null, (Handler) Dependency.get(Dependency.TIME_TICK_HANDLER));
            ((TunerService) Dependency.get(TunerService.class)).addTunable(this, CLOCK_SECONDS, StatusBarIconController.ICON_BLACKLIST);
            ((CommandQueue) SysUiServiceProvider.getComponent(getContext(), CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
            if (this.mShowDark) {
                ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class)).addDarkReceiver(this);
            }
            this.mCurrentUserTracker.startTracking();
            this.mCurrentUserId = this.mCurrentUserTracker.getCurrentUserId();
        }
        this.mCalendar = Calendar.getInstance(TimeZone.getDefault());
        updateClock();
        updateClockVisibility();
        updateShowSeconds();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttached) {
            getContext().unregisterReceiver(this.mIntentReceiver);
            this.mAttached = false;
            ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
            ((CommandQueue) SysUiServiceProvider.getComponent(getContext(), CommandQueue.class)).removeCallback((CommandQueue.Callbacks) this);
            if (this.mShowDark) {
                ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class)).removeDarkReceiver(this);
            }
            this.mCurrentUserTracker.stopTracking();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.policy.Clock$2  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass2 extends BroadcastReceiver {
        AnonymousClass2() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Handler handler = Clock.this.getHandler();
            if (handler == null) {
                Log.e(Clock.TAG, "Received intent, but handler is null - still attached to window? Window token: " + Clock.this.getWindowToken());
                return;
            }
            if (action.equals("android.intent.action.TIMEZONE_CHANGED")) {
                final String tz = intent.getStringExtra("time-zone");
                handler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$Clock$2$NVwlBsd8V0hLupY9sb0smFA7zNw
                    @Override // java.lang.Runnable
                    public final void run() {
                        Clock.AnonymousClass2.this.lambda$onReceive$0$Clock$2(tz);
                    }
                });
            } else if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                final Locale newLocale = Clock.this.getResources().getConfiguration().locale;
                handler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$Clock$2$BzKxslldgL1SP5a4jbR8GDSq90w
                    @Override // java.lang.Runnable
                    public final void run() {
                        Clock.AnonymousClass2.this.lambda$onReceive$1$Clock$2(newLocale);
                    }
                });
            }
            handler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$Clock$2$mOTwR4Tu5xrxBBIUbNE9701lx-4
                @Override // java.lang.Runnable
                public final void run() {
                    Clock.AnonymousClass2.this.lambda$onReceive$2$Clock$2();
                }
            });
        }

        public /* synthetic */ void lambda$onReceive$0$Clock$2(String tz) {
            Clock.this.mCalendar = Calendar.getInstance(TimeZone.getTimeZone(tz));
            if (Clock.this.mClockFormat != null) {
                Clock.this.mClockFormat.setTimeZone(Clock.this.mCalendar.getTimeZone());
            }
        }

        public /* synthetic */ void lambda$onReceive$1$Clock$2(Locale newLocale) {
            if (!newLocale.equals(Clock.this.mLocale)) {
                Clock.this.mLocale = newLocale;
                Clock.this.mClockFormatString = "";
            }
        }

        public /* synthetic */ void lambda$onReceive$2$Clock$2() {
            Clock.this.updateClock();
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        if (visibility == 0 && !shouldBeVisible()) {
            return;
        }
        super.setVisibility(visibility);
    }

    public void setClockVisibleByUser(boolean visible) {
        this.mClockVisibleByUser = visible;
        updateClockVisibility();
    }

    public void setClockVisibilityByPolicy(boolean visible) {
        this.mClockVisibleByPolicy = visible;
        updateClockVisibility();
    }

    private boolean shouldBeVisible() {
        return this.mClockVisibleByPolicy && this.mClockVisibleByUser;
    }

    private void updateClockVisibility() {
        boolean visible = shouldBeVisible();
        int visibility = visible ? 0 : 8;
        super.setVisibility(visibility);
    }

    final void updateClock() {
        if (this.mDemoMode) {
            return;
        }
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        setText(getSmallTime());
        setContentDescription(this.mContentDescriptionFormat.format(this.mCalendar.getTime()));
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        if (CLOCK_SECONDS.equals(key)) {
            this.mShowSeconds = TunerService.parseIntegerSwitch(newValue, false);
            updateShowSeconds();
            return;
        }
        setClockVisibleByUser(!StatusBarIconController.getIconBlacklist(newValue).contains(DemoMode.COMMAND_CLOCK));
        updateClockVisibility();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void disable(int displayId, int state1, int state2, boolean animate) {
        if (displayId != getDisplay().getDisplayId()) {
            return;
        }
        boolean clockVisibleByPolicy = (8388608 & state1) == 0;
        if (clockVisibleByPolicy != this.mClockVisibleByPolicy) {
            setClockVisibilityByPolicy(clockVisibleByPolicy);
        }
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver
    public void onDarkChanged(Rect area, float darkIntensity, int tint) {
        this.mNonAdaptedColor = DarkIconDispatcher.getTint(area, this, tint);
        if (!this.mUseWallpaperTextColor) {
            setTextColor(this.mNonAdaptedColor);
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        FontSizeUtils.updateFontSize(this, R.dimen.status_bar_clock_size);
        setPaddingRelative(this.mContext.getResources().getDimensionPixelSize(R.dimen.status_bar_clock_starting_padding), 0, this.mContext.getResources().getDimensionPixelSize(R.dimen.status_bar_clock_end_padding), 0);
    }

    public void useWallpaperTextColor(boolean shouldUseWallpaperTextColor) {
        if (shouldUseWallpaperTextColor == this.mUseWallpaperTextColor) {
            return;
        }
        this.mUseWallpaperTextColor = shouldUseWallpaperTextColor;
        if (this.mUseWallpaperTextColor) {
            setTextColor(Utils.getColorAttr(this.mContext, R.attr.wallpaperTextColor));
        } else {
            setTextColor(this.mNonAdaptedColor);
        }
    }

    private void updateShowSeconds() {
        if (this.mShowSeconds) {
            if (this.mSecondsHandler == null && getDisplay() != null) {
                this.mSecondsHandler = new Handler();
                if (getDisplay().getState() == 2) {
                    this.mSecondsHandler.postAtTime(this.mSecondTick, ((SystemClock.uptimeMillis() / 1000) * 1000) + 1000);
                }
                IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_OFF");
                filter.addAction("android.intent.action.SCREEN_ON");
                this.mContext.registerReceiver(this.mScreenReceiver, filter);
            }
        } else if (this.mSecondsHandler != null) {
            this.mContext.unregisterReceiver(this.mScreenReceiver);
            this.mSecondsHandler.removeCallbacks(this.mSecondTick);
            this.mSecondsHandler = null;
            updateClock();
        }
    }

    private final CharSequence getSmallTime() {
        String format;
        SimpleDateFormat sdf;
        Context context = getContext();
        boolean is24 = DateFormat.is24HourFormat(context, this.mCurrentUserId);
        LocaleData d = LocaleData.get(context.getResources().getConfiguration().locale);
        if (this.mShowSeconds) {
            format = is24 ? d.timeFormat_Hms : d.timeFormat_hms;
        } else {
            format = is24 ? d.timeFormat_Hm : d.timeFormat_hm;
        }
        if (!format.equals(this.mClockFormatString)) {
            this.mContentDescriptionFormat = new SimpleDateFormat(format);
            if (this.mAmPmStyle != 0) {
                int a = -1;
                boolean quoted = false;
                int i = 0;
                while (true) {
                    if (i < format.length()) {
                        char c = format.charAt(i);
                        if (c == '\'') {
                            quoted = !quoted;
                        }
                        if (quoted || c != 'a') {
                            i++;
                        } else {
                            a = i;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (a >= 0) {
                    int b = a;
                    while (a > 0 && Character.isWhitespace(format.charAt(a - 1))) {
                        a--;
                    }
                    format = format.substring(0, a) + (char) 61184 + format.substring(a, b) + "a\uef01" + format.substring(b + 1);
                }
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            sdf = simpleDateFormat;
            this.mClockFormat = simpleDateFormat;
            this.mClockFormatString = format;
        } else {
            sdf = this.mClockFormat;
        }
        String result = sdf.format(this.mCalendar.getTime());
        if (this.mAmPmStyle != 0) {
            int magic1 = result.indexOf(61184);
            int magic2 = result.indexOf(61185);
            if (magic1 >= 0 && magic2 > magic1) {
                SpannableStringBuilder formatted = new SpannableStringBuilder(result);
                int i2 = this.mAmPmStyle;
                if (i2 == 2) {
                    formatted.delete(magic1, magic2 + 1);
                } else {
                    if (i2 == 1) {
                        CharacterStyle style = new RelativeSizeSpan(0.7f);
                        formatted.setSpan(style, magic1, magic2, 34);
                    }
                    formatted.delete(magic2, magic2 + 1);
                    formatted.delete(magic1, magic1 + 1);
                }
                return formatted;
            }
        }
        return result;
    }

    @Override // com.android.systemui.DemoMode
    public void dispatchDemoCommand(String command, Bundle args) {
        if (!this.mDemoMode && command.equals("enter")) {
            this.mDemoMode = true;
        } else if (this.mDemoMode && command.equals(DemoMode.COMMAND_EXIT)) {
            this.mDemoMode = false;
            updateClock();
        } else if (this.mDemoMode && command.equals(DemoMode.COMMAND_CLOCK)) {
            String millis = args.getString(SliceHints.SUBTYPE_MILLIS);
            String hhmm = args.getString("hhmm");
            if (millis != null) {
                this.mCalendar.setTimeInMillis(Long.parseLong(millis));
            } else if (hhmm != null && hhmm.length() == 4) {
                int hh = Integer.parseInt(hhmm.substring(0, 2));
                int mm = Integer.parseInt(hhmm.substring(2));
                boolean is24 = DateFormat.is24HourFormat(getContext(), this.mCurrentUserId);
                if (is24) {
                    this.mCalendar.set(11, hh);
                } else {
                    this.mCalendar.set(10, hh);
                }
                this.mCalendar.set(12, mm);
            }
            setText(getSmallTime());
            setContentDescription(this.mContentDescriptionFormat.format(this.mCalendar.getTime()));
        }
    }
}
