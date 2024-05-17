package com.android.keyguard;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.IStopUserCallback;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.xiaopeng.systemui.controller.OsdController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.TimeZone;
/* loaded from: classes19.dex */
public class KeyguardStatusView extends GridLayout implements ConfigurationController.ConfigurationListener {
    private static final boolean DEBUG = false;
    private static final int MARQUEE_DELAY_MS = 2000;
    private static final String TAG = "KeyguardStatusView";
    private KeyguardClockSwitch mClockView;
    private float mDarkAmount;
    private Handler mHandler;
    private final IActivityManager mIActivityManager;
    private int mIconTopMargin;
    private int mIconTopMarginWithHeader;
    private KeyguardUpdateMonitorCallback mInfoCallback;
    private KeyguardSliceView mKeyguardSlice;
    private final LockPatternUtils mLockPatternUtils;
    private TextView mLogoutView;
    private View mNotificationIcons;
    private TextView mOwnerInfo;
    private Runnable mPendingMarqueeStart;
    private boolean mPulsing;
    private boolean mShowingHeader;
    private LinearLayout mStatusViewContainer;
    private int mTextColor;

    public KeyguardStatusView(Context context) {
        this(context, null, 0);
    }

    public KeyguardStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyguardStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDarkAmount = 0.0f;
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.keyguard.KeyguardStatusView.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onTimeChanged() {
                KeyguardStatusView.this.refreshTime();
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onTimeZoneChanged(TimeZone timeZone) {
                KeyguardStatusView.this.updateTimeZone(timeZone);
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onKeyguardVisibilityChanged(boolean showing) {
                if (showing) {
                    KeyguardStatusView.this.refreshTime();
                    KeyguardStatusView.this.updateOwnerInfo();
                    KeyguardStatusView.this.updateLogoutView();
                }
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onStartedWakingUp() {
                KeyguardStatusView.this.setEnableMarquee(true);
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onFinishedGoingToSleep(int why) {
                KeyguardStatusView.this.setEnableMarquee(false);
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onUserSwitchComplete(int userId) {
                KeyguardStatusView.this.refreshFormat();
                KeyguardStatusView.this.updateOwnerInfo();
                KeyguardStatusView.this.updateLogoutView();
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onLogoutEnabledChanged() {
                KeyguardStatusView.this.updateLogoutView();
            }
        };
        this.mIActivityManager = ActivityManager.getService();
        this.mLockPatternUtils = new LockPatternUtils(getContext());
        this.mHandler = new Handler(Looper.myLooper());
        onDensityOrFontScaleChanged();
    }

    public boolean hasCustomClock() {
        return this.mClockView.hasCustomClock();
    }

    public void setHasVisibleNotifications(boolean hasVisibleNotifications) {
        this.mClockView.setHasVisibleNotifications(hasVisibleNotifications);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEnableMarquee(boolean enabled) {
        if (enabled) {
            if (this.mPendingMarqueeStart == null) {
                this.mPendingMarqueeStart = new Runnable() { // from class: com.android.keyguard.-$$Lambda$KeyguardStatusView$ps9yj97ShIVR2u2hJB8SKuKk-kQ
                    @Override // java.lang.Runnable
                    public final void run() {
                        KeyguardStatusView.this.lambda$setEnableMarquee$0$KeyguardStatusView();
                    }
                };
                this.mHandler.postDelayed(this.mPendingMarqueeStart, OsdController.TN.DURATION_TIMEOUT_SHORT);
                return;
            }
            return;
        }
        Runnable runnable = this.mPendingMarqueeStart;
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
            this.mPendingMarqueeStart = null;
        }
        setEnableMarqueeImpl(false);
    }

    public /* synthetic */ void lambda$setEnableMarquee$0$KeyguardStatusView() {
        setEnableMarqueeImpl(true);
        this.mPendingMarqueeStart = null;
    }

    private void setEnableMarqueeImpl(boolean enabled) {
        TextView textView = this.mOwnerInfo;
        if (textView != null) {
            textView.setSelected(enabled);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mStatusViewContainer = (LinearLayout) findViewById(R.id.status_view_container);
        this.mLogoutView = (TextView) findViewById(R.id.logout);
        this.mNotificationIcons = findViewById(R.id.clock_notification_icon_container);
        TextView textView = this.mLogoutView;
        if (textView != null) {
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.keyguard.-$$Lambda$KeyguardStatusView$Pryio69yVoRI9F153p5QiMZe-bw
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    KeyguardStatusView.this.onLogoutClicked(view);
                }
            });
        }
        this.mClockView = (KeyguardClockSwitch) findViewById(R.id.keyguard_clock_container);
        this.mClockView.setShowCurrentUserTime(true);
        if (KeyguardClockAccessibilityDelegate.isNeeded(this.mContext)) {
            this.mClockView.setAccessibilityDelegate(new KeyguardClockAccessibilityDelegate(this.mContext));
        }
        this.mOwnerInfo = (TextView) findViewById(R.id.owner_info);
        this.mKeyguardSlice = (KeyguardSliceView) findViewById(R.id.keyguard_status_area);
        this.mTextColor = this.mClockView.getCurrentTextColor();
        this.mKeyguardSlice.setContentChangeListener(new Runnable() { // from class: com.android.keyguard.-$$Lambda$KeyguardStatusView$Xo7rGDTjuOiD9nJpe80IUZ1ddFw
            @Override // java.lang.Runnable
            public final void run() {
                KeyguardStatusView.this.onSliceContentChanged();
            }
        });
        onSliceContentChanged();
        boolean shouldMarquee = KeyguardUpdateMonitor.getInstance(this.mContext).isDeviceInteractive();
        setEnableMarquee(shouldMarquee);
        refreshFormat();
        updateOwnerInfo();
        updateLogoutView();
        updateDark();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSliceContentChanged() {
        boolean hasHeader = this.mKeyguardSlice.hasHeader();
        this.mClockView.setKeyguardShowingHeader(hasHeader);
        if (this.mShowingHeader == hasHeader) {
            return;
        }
        this.mShowingHeader = hasHeader;
        View view = this.mNotificationIcons;
        if (view != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(params.leftMargin, hasHeader ? this.mIconTopMarginWithHeader : this.mIconTopMargin, params.rightMargin, params.bottomMargin);
            this.mNotificationIcons.setLayoutParams(params);
        }
    }

    @Override // android.widget.GridLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutOwnerInfo();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        KeyguardClockSwitch keyguardClockSwitch = this.mClockView;
        if (keyguardClockSwitch != null) {
            keyguardClockSwitch.setTextSize(0, getResources().getDimensionPixelSize(R.dimen.widget_big_font_size));
        }
        TextView textView = this.mOwnerInfo;
        if (textView != null) {
            textView.setTextSize(0, getResources().getDimensionPixelSize(R.dimen.widget_label_font_size));
        }
        loadBottomMargin();
    }

    public void dozeTimeTick() {
        refreshTime();
        this.mKeyguardSlice.refresh();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshTime() {
        this.mClockView.refresh();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTimeZone(TimeZone timeZone) {
        this.mClockView.onTimeZoneChanged(timeZone);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshFormat() {
        Patterns.update(this.mContext);
        this.mClockView.setFormat12Hour(Patterns.clockView12);
        this.mClockView.setFormat24Hour(Patterns.clockView24);
    }

    public int getLogoutButtonHeight() {
        TextView textView = this.mLogoutView;
        if (textView != null && textView.getVisibility() == 0) {
            return this.mLogoutView.getHeight();
        }
        return 0;
    }

    public float getClockTextSize() {
        return this.mClockView.getTextSize();
    }

    public int getClockPreferredY(int totalHeight) {
        return this.mClockView.getPreferredY(totalHeight);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLogoutView() {
        TextView textView = this.mLogoutView;
        if (textView == null) {
            return;
        }
        textView.setVisibility(shouldShowLogout() ? 0 : 8);
        this.mLogoutView.setText(this.mContext.getResources().getString(17040065));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOwnerInfo() {
        if (this.mOwnerInfo == null) {
            return;
        }
        String info = this.mLockPatternUtils.getDeviceOwnerInfo();
        if (info == null) {
            boolean ownerInfoEnabled = this.mLockPatternUtils.isOwnerInfoEnabled(KeyguardUpdateMonitor.getCurrentUser());
            if (ownerInfoEnabled) {
                info = this.mLockPatternUtils.getOwnerInfo(KeyguardUpdateMonitor.getCurrentUser());
            }
        }
        this.mOwnerInfo.setText(info);
        updateDark();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mInfoCallback);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KeyguardUpdateMonitor.getInstance(this.mContext).removeCallback(this.mInfoCallback);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onLocaleListChanged() {
        refreshFormat();
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        Object valueOf;
        pw.println("KeyguardStatusView:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mOwnerInfo: ");
        TextView textView = this.mOwnerInfo;
        if (textView == null) {
            valueOf = "null";
        } else {
            valueOf = Boolean.valueOf(textView.getVisibility() == 0);
        }
        sb.append(valueOf);
        pw.println(sb.toString());
        pw.println("  mPulsing: " + this.mPulsing);
        pw.println("  mDarkAmount: " + this.mDarkAmount);
        pw.println("  mTextColor: " + Integer.toHexString(this.mTextColor));
        if (this.mLogoutView != null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("  logout visible: ");
            sb2.append(this.mLogoutView.getVisibility() == 0);
            pw.println(sb2.toString());
        }
        KeyguardClockSwitch keyguardClockSwitch = this.mClockView;
        if (keyguardClockSwitch != null) {
            keyguardClockSwitch.dump(fd, pw, args);
        }
        KeyguardSliceView keyguardSliceView = this.mKeyguardSlice;
        if (keyguardSliceView != null) {
            keyguardSliceView.dump(fd, pw, args);
        }
    }

    private void loadBottomMargin() {
        this.mIconTopMargin = getResources().getDimensionPixelSize(R.dimen.widget_vertical_padding);
        this.mIconTopMarginWithHeader = getResources().getDimensionPixelSize(R.dimen.widget_vertical_padding_with_header);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes19.dex */
    public static final class Patterns {
        static String cacheKey;
        static String clockView12;
        static String clockView24;

        private Patterns() {
        }

        static void update(Context context) {
            Locale locale = Locale.getDefault();
            Resources res = context.getResources();
            String clockView12Skel = res.getString(R.string.clock_12hr_format);
            String clockView24Skel = res.getString(R.string.clock_24hr_format);
            String key = locale.toString() + clockView12Skel + clockView24Skel;
            if (key.equals(cacheKey)) {
                return;
            }
            clockView12 = DateFormat.getBestDateTimePattern(locale, clockView12Skel);
            if (!clockView12Skel.contains("a")) {
                clockView12 = clockView12.replaceAll("a", "").trim();
            }
            clockView24 = DateFormat.getBestDateTimePattern(locale, clockView24Skel);
            clockView24 = clockView24.replace(AccessibilityUtils.ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR, (char) 60929);
            clockView12 = clockView12.replace(AccessibilityUtils.ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR, (char) 60929);
            cacheKey = key;
        }
    }

    public void setDarkAmount(float darkAmount) {
        if (this.mDarkAmount == darkAmount) {
            return;
        }
        this.mDarkAmount = darkAmount;
        this.mClockView.setDarkAmount(darkAmount);
        updateDark();
    }

    private void updateDark() {
        boolean dark = this.mDarkAmount == 1.0f;
        TextView textView = this.mLogoutView;
        if (textView != null) {
            textView.setAlpha(dark ? 0.0f : 1.0f);
        }
        TextView textView2 = this.mOwnerInfo;
        if (textView2 != null) {
            boolean hasText = !TextUtils.isEmpty(textView2.getText());
            this.mOwnerInfo.setVisibility(hasText ? 0 : 8);
            layoutOwnerInfo();
        }
        int blendedTextColor = ColorUtils.blendARGB(this.mTextColor, -1, this.mDarkAmount);
        this.mKeyguardSlice.setDarkAmount(this.mDarkAmount);
        this.mClockView.setTextColor(blendedTextColor);
    }

    private void layoutOwnerInfo() {
        TextView textView = this.mOwnerInfo;
        if (textView != null && textView.getVisibility() != 8) {
            this.mOwnerInfo.setAlpha(1.0f - this.mDarkAmount);
            float ratio = this.mDarkAmount;
            int collapsed = this.mOwnerInfo.getTop() - this.mOwnerInfo.getPaddingTop();
            int expanded = this.mOwnerInfo.getBottom() + this.mOwnerInfo.getPaddingBottom();
            int toRemove = (int) ((expanded - collapsed) * ratio);
            setBottom(getMeasuredHeight() - toRemove);
            View view = this.mNotificationIcons;
            if (view != null) {
                view.setScrollY(toRemove);
                return;
            }
            return;
        }
        View view2 = this.mNotificationIcons;
        if (view2 != null) {
            view2.setScrollY(0);
        }
    }

    public void setPulsing(boolean pulsing) {
        if (this.mPulsing == pulsing) {
            return;
        }
        this.mPulsing = pulsing;
    }

    private boolean shouldShowLogout() {
        return KeyguardUpdateMonitor.getInstance(this.mContext).isLogoutEnabled() && KeyguardUpdateMonitor.getCurrentUser() != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLogoutClicked(View view) {
        int currentUserId = KeyguardUpdateMonitor.getCurrentUser();
        try {
            this.mIActivityManager.switchUser(0);
            this.mIActivityManager.stopUser(currentUserId, true, (IStopUserCallback) null);
        } catch (RemoteException re) {
            Log.e(TAG, "Failed to logout user", re);
        }
    }
}
