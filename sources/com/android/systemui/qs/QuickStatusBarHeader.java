package com.android.systemui.qs;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.service.notification.ZenModeConfig;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.VisibleForTesting;
import com.android.settingslib.Utils;
import com.android.systemui.BatteryMeterView;
import com.android.systemui.DualToneHandler;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.qs.QSDetail;
import com.android.systemui.qs.TouchAnimator;
import com.android.systemui.statusbar.phone.PhoneStatusBarView;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusIconContainer;
import com.android.systemui.statusbar.policy.Clock;
import com.android.systemui.statusbar.policy.DateView;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.xiaopeng.libtheme.ThemeManager;
import java.util.Locale;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class QuickStatusBarHeader extends RelativeLayout implements View.OnClickListener, NextAlarmController.NextAlarmChangeCallback, ZenModeController.Callback {
    private static final long AUTO_FADE_OUT_DELAY_MS = 6000;
    private static final boolean DEBUG = false;
    private static final int FADE_ANIMATION_DURATION_MS = 300;
    public static final int MAX_TOOLTIP_SHOWN_COUNT = 2;
    private static final String TAG = "QuickStatusBarHeader";
    private static final int TOOLTIP_NOT_YET_SHOWN_COUNT = 0;
    private final ActivityStarter mActivityStarter;
    private final NextAlarmController mAlarmController;
    private BatteryMeterView mBatteryRemainingIcon;
    private QSCarrierGroup mCarrierGroup;
    private Clock mClockView;
    private DateView mDateView;
    private DualToneHandler mDualToneHandler;
    private boolean mExpanded;
    private final Handler mHandler;
    private boolean mHasTopCutout;
    protected QuickQSPanel mHeaderQsPanel;
    private TouchAnimator mHeaderTextContainerAlphaAnimator;
    private View mHeaderTextContainerView;
    protected QSTileHost mHost;
    private StatusBarIconController.TintedIconManager mIconManager;
    private boolean mListening;
    private AlarmManager.AlarmClockInfo mNextAlarm;
    private View mNextAlarmContainer;
    private ImageView mNextAlarmIcon;
    private TextView mNextAlarmTextView;
    private boolean mQsDisabled;
    private QSPanel mQsPanel;
    private View mQuickQsStatusIcons;
    private View mRingerContainer;
    private int mRingerMode;
    private ImageView mRingerModeIcon;
    private TextView mRingerModeTextView;
    private final BroadcastReceiver mRingerReceiver;
    private final StatusBarIconController mStatusBarIconController;
    private TouchAnimator mStatusIconsAlphaAnimator;
    private View mStatusSeparator;
    private View mSystemIconsView;
    private final ZenModeController mZenController;

    @Inject
    public QuickStatusBarHeader(@Named("view_context") Context context, AttributeSet attrs, NextAlarmController nextAlarmController, ZenModeController zenModeController, StatusBarIconController statusBarIconController, ActivityStarter activityStarter) {
        super(context, attrs);
        this.mHandler = new Handler();
        this.mRingerMode = 2;
        this.mRingerReceiver = new BroadcastReceiver() { // from class: com.android.systemui.qs.QuickStatusBarHeader.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                QuickStatusBarHeader.this.mRingerMode = intent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1);
                QuickStatusBarHeader.this.updateStatusText();
            }
        };
        this.mHasTopCutout = false;
        this.mAlarmController = nextAlarmController;
        this.mZenController = zenModeController;
        this.mStatusBarIconController = statusBarIconController;
        this.mActivityStarter = activityStarter;
        this.mDualToneHandler = new DualToneHandler(new ContextThemeWrapper(context, R.style.QSHeaderTheme));
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHeaderQsPanel = (QuickQSPanel) findViewById(R.id.quick_qs_panel);
        this.mSystemIconsView = findViewById(R.id.quick_status_bar_system_icons);
        this.mQuickQsStatusIcons = findViewById(R.id.quick_qs_status_icons);
        StatusIconContainer iconContainer = (StatusIconContainer) findViewById(R.id.statusIcons);
        iconContainer.setShouldRestrictIcons(false);
        this.mIconManager = new StatusBarIconController.TintedIconManager(iconContainer);
        this.mHeaderTextContainerView = findViewById(R.id.header_text_container);
        this.mStatusSeparator = findViewById(R.id.status_separator);
        this.mNextAlarmIcon = (ImageView) findViewById(R.id.next_alarm_icon);
        this.mNextAlarmTextView = (TextView) findViewById(R.id.next_alarm_text);
        this.mNextAlarmContainer = findViewById(R.id.alarm_container);
        this.mNextAlarmContainer.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.qs.-$$Lambda$p8TkVReSUo0LsQ3y-9iKja9mJXE
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QuickStatusBarHeader.this.onClick(view);
            }
        });
        this.mRingerModeIcon = (ImageView) findViewById(R.id.ringer_mode_icon);
        this.mRingerModeTextView = (TextView) findViewById(R.id.ringer_mode_text);
        this.mRingerContainer = findViewById(R.id.ringer_container);
        this.mCarrierGroup = (QSCarrierGroup) findViewById(R.id.carrier_group);
        updateResources();
        Rect tintArea = new Rect(0, 0, 0, 0);
        int colorForeground = Utils.getColorAttrDefaultColor(getContext(), 16842800);
        float intensity = getColorIntensity(colorForeground);
        int fillColor = this.mDualToneHandler.getSingleColor(intensity);
        applyDarkness(R.id.clock, tintArea, 0.0f, -1);
        this.mIconManager.setTint(fillColor);
        this.mNextAlarmIcon.setImageTintList(ColorStateList.valueOf(fillColor));
        this.mRingerModeIcon.setImageTintList(ColorStateList.valueOf(fillColor));
        this.mClockView = (Clock) findViewById(R.id.clock);
        this.mClockView.setOnClickListener(this);
        this.mDateView = (DateView) findViewById(R.id.date);
        this.mBatteryRemainingIcon = (BatteryMeterView) findViewById(R.id.batteryRemainingIcon);
        this.mBatteryRemainingIcon.setIgnoreTunerUpdates(true);
        this.mBatteryRemainingIcon.setPercentShowMode(3);
        this.mRingerModeTextView.setSelected(true);
        this.mNextAlarmTextView.setSelected(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStatusText() {
        int i = 0;
        boolean changed = updateRingerStatus() || updateAlarmStatus();
        if (changed) {
            boolean alarmVisible = this.mNextAlarmTextView.getVisibility() == 0;
            boolean ringerVisible = this.mRingerModeTextView.getVisibility() == 0;
            this.mStatusSeparator.setVisibility((alarmVisible && ringerVisible) ? 8 : 8);
        }
    }

    private boolean updateRingerStatus() {
        boolean isOriginalVisible = this.mRingerModeTextView.getVisibility() == 0;
        CharSequence originalRingerText = this.mRingerModeTextView.getText();
        boolean ringerVisible = false;
        if (!ZenModeConfig.isZenOverridingRinger(this.mZenController.getZen(), this.mZenController.getConsolidatedPolicy())) {
            int i = this.mRingerMode;
            if (i == 1) {
                this.mRingerModeIcon.setImageResource(R.drawable.ic_volume_ringer_vibrate);
                this.mRingerModeTextView.setText(R.string.qs_status_phone_vibrate);
                ringerVisible = true;
            } else if (i == 0) {
                this.mRingerModeIcon.setImageResource(R.drawable.ic_volume_ringer_mute);
                this.mRingerModeTextView.setText(R.string.qs_status_phone_muted);
                ringerVisible = true;
            }
        }
        this.mRingerModeIcon.setVisibility(ringerVisible ? 0 : 8);
        this.mRingerModeTextView.setVisibility(ringerVisible ? 0 : 8);
        this.mRingerContainer.setVisibility(ringerVisible ? 0 : 8);
        return (isOriginalVisible == ringerVisible && Objects.equals(originalRingerText, this.mRingerModeTextView.getText())) ? false : true;
    }

    private boolean updateAlarmStatus() {
        boolean isOriginalVisible = this.mNextAlarmTextView.getVisibility() == 0;
        CharSequence originalAlarmText = this.mNextAlarmTextView.getText();
        boolean alarmVisible = false;
        AlarmManager.AlarmClockInfo alarmClockInfo = this.mNextAlarm;
        if (alarmClockInfo != null) {
            alarmVisible = true;
            this.mNextAlarmTextView.setText(formatNextAlarm(alarmClockInfo));
        }
        this.mNextAlarmIcon.setVisibility(alarmVisible ? 0 : 8);
        this.mNextAlarmTextView.setVisibility(alarmVisible ? 0 : 8);
        this.mNextAlarmContainer.setVisibility(alarmVisible ? 0 : 8);
        return (isOriginalVisible == alarmVisible && Objects.equals(originalAlarmText, this.mNextAlarmTextView.getText())) ? false : true;
    }

    private void applyDarkness(int id, Rect tintArea, float intensity, int color) {
        View v = findViewById(id);
        if (v instanceof DarkIconDispatcher.DarkReceiver) {
            ((DarkIconDispatcher.DarkReceiver) v).onDarkChanged(tintArea, intensity, color);
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateResources();
        boolean shouldUseWallpaperTextColor = newConfig.orientation == 2;
        this.mClockView.useWallpaperTextColor(shouldUseWallpaperTextColor);
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        updateResources();
    }

    private void updateMinimumHeight() {
        int sbHeight = this.mContext.getResources().getDimensionPixelSize(17105438);
        int qqsHeight = this.mContext.getResources().getDimensionPixelSize(R.dimen.qs_quick_header_panel_height);
        setMinimumHeight(sbHeight + qqsHeight);
    }

    private void updateResources() {
        Resources resources = this.mContext.getResources();
        updateMinimumHeight();
        this.mHeaderTextContainerView.getLayoutParams().height = resources.getDimensionPixelSize(R.dimen.qs_header_tooltip_height);
        View view = this.mHeaderTextContainerView;
        view.setLayoutParams(view.getLayoutParams());
        this.mSystemIconsView.getLayoutParams().height = resources.getDimensionPixelSize(17105399);
        View view2 = this.mSystemIconsView;
        view2.setLayoutParams(view2.getLayoutParams());
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        if (this.mQsDisabled) {
            lp.height = resources.getDimensionPixelSize(17105399);
        } else {
            lp.height = Math.max(getMinimumHeight(), resources.getDimensionPixelSize(17105400));
        }
        setLayoutParams(lp);
        updateStatusIconAlphaAnimator();
        updateHeaderTextContainerAlphaAnimator();
    }

    private void updateStatusIconAlphaAnimator() {
        this.mStatusIconsAlphaAnimator = new TouchAnimator.Builder().addFloat(this.mQuickQsStatusIcons, ThemeManager.AttributeSet.ALPHA, 1.0f, 0.0f, 0.0f).build();
    }

    private void updateHeaderTextContainerAlphaAnimator() {
        this.mHeaderTextContainerAlphaAnimator = new TouchAnimator.Builder().addFloat(this.mHeaderTextContainerView, ThemeManager.AttributeSet.ALPHA, 0.0f, 0.0f, 1.0f).build();
    }

    public void setExpanded(boolean expanded) {
        if (this.mExpanded == expanded) {
            return;
        }
        this.mExpanded = expanded;
        this.mHeaderQsPanel.setExpanded(expanded);
        updateEverything();
    }

    public void setExpansion(boolean forceExpanded, float expansionFraction, float panelTranslationY) {
        float keyguardExpansionFraction = forceExpanded ? 1.0f : expansionFraction;
        TouchAnimator touchAnimator = this.mStatusIconsAlphaAnimator;
        if (touchAnimator != null) {
            touchAnimator.setPosition(keyguardExpansionFraction);
        }
        if (!forceExpanded) {
            this.mHeaderTextContainerView.setTranslationY(0.0f);
        } else {
            this.mHeaderTextContainerView.setTranslationY(panelTranslationY);
        }
        TouchAnimator touchAnimator2 = this.mHeaderTextContainerAlphaAnimator;
        if (touchAnimator2 != null) {
            touchAnimator2.setPosition(keyguardExpansionFraction);
            if (keyguardExpansionFraction > 0.0f) {
                this.mHeaderTextContainerView.setVisibility(0);
            } else {
                this.mHeaderTextContainerView.setVisibility(4);
            }
        }
    }

    public void disable(int state1, int state2, boolean animate) {
        boolean disabled = (state2 & 1) != 0;
        if (disabled == this.mQsDisabled) {
            return;
        }
        this.mQsDisabled = disabled;
        this.mHeaderQsPanel.setDisabledByPolicy(disabled);
        this.mHeaderTextContainerView.setVisibility(this.mQsDisabled ? 8 : 0);
        this.mQuickQsStatusIcons.setVisibility(this.mQsDisabled ? 8 : 0);
        updateResources();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mStatusBarIconController.addIconGroup(this.mIconManager);
        requestApplyInsets();
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        DisplayCutout cutout = insets.getDisplayCutout();
        Pair<Integer, Integer> padding = PhoneStatusBarView.cornerCutoutMargins(cutout, getDisplay());
        if (padding == null) {
            this.mSystemIconsView.setPaddingRelative(getResources().getDimensionPixelSize(R.dimen.status_bar_padding_start), getResources().getDimensionPixelSize(R.dimen.status_bar_padding_top), getResources().getDimensionPixelSize(R.dimen.status_bar_padding_end), 0);
        } else {
            this.mSystemIconsView.setPadding(((Integer) padding.first).intValue(), getResources().getDimensionPixelSize(R.dimen.status_bar_padding_top), ((Integer) padding.second).intValue(), 0);
        }
        return super.onApplyWindowInsets(insets);
    }

    @Override // android.view.ViewGroup, android.view.View
    @VisibleForTesting
    public void onDetachedFromWindow() {
        setListening(false);
        this.mStatusBarIconController.removeIconGroup(this.mIconManager);
        super.onDetachedFromWindow();
    }

    public void setListening(boolean listening) {
        if (listening == this.mListening) {
            return;
        }
        this.mHeaderQsPanel.setListening(listening);
        this.mListening = listening;
        this.mCarrierGroup.setListening(this.mListening);
        if (listening) {
            this.mZenController.addCallback(this);
            this.mAlarmController.addCallback(this);
            this.mContext.registerReceiver(this.mRingerReceiver, new IntentFilter("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION"));
            return;
        }
        this.mZenController.removeCallback(this);
        this.mAlarmController.removeCallback(this);
        this.mContext.unregisterReceiver(this.mRingerReceiver);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mClockView) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.intent.action.SHOW_ALARMS"), 0);
            return;
        }
        View view = this.mNextAlarmContainer;
        if (v == view && view.isVisibleToUser()) {
            if (this.mNextAlarm.getShowIntent() != null) {
                this.mActivityStarter.postStartActivityDismissingKeyguard(this.mNextAlarm.getShowIntent());
                return;
            }
            Log.d(TAG, "No PendingIntent for next alarm. Using default intent");
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.intent.action.SHOW_ALARMS"), 0);
            return;
        }
        View view2 = this.mRingerContainer;
        if (v == view2 && view2.isVisibleToUser()) {
            this.mActivityStarter.postStartActivityDismissingKeyguard(new Intent("android.settings.SOUND_SETTINGS"), 0);
        }
    }

    @Override // com.android.systemui.statusbar.policy.NextAlarmController.NextAlarmChangeCallback
    public void onNextAlarmChanged(AlarmManager.AlarmClockInfo nextAlarm) {
        this.mNextAlarm = nextAlarm;
        updateStatusText();
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
    public void onZenChanged(int zen) {
        updateStatusText();
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
    public void onConfigChanged(ZenModeConfig config) {
        updateStatusText();
    }

    public /* synthetic */ void lambda$updateEverything$0$QuickStatusBarHeader() {
        setClickable(!this.mExpanded);
    }

    public void updateEverything() {
        post(new Runnable() { // from class: com.android.systemui.qs.-$$Lambda$QuickStatusBarHeader$AvsHoBxZXMvvH_WD73mLXoXpNWs
            @Override // java.lang.Runnable
            public final void run() {
                QuickStatusBarHeader.this.lambda$updateEverything$0$QuickStatusBarHeader();
            }
        });
    }

    public void setQSPanel(QSPanel qsPanel) {
        this.mQsPanel = qsPanel;
        setupHost(qsPanel.getHost());
    }

    public void setupHost(QSTileHost host) {
        this.mHost = host;
        this.mHeaderQsPanel.setQSPanelAndHeader(this.mQsPanel, this);
        this.mHeaderQsPanel.setHost(host, null);
        Rect tintArea = new Rect(0, 0, 0, 0);
        int colorForeground = Utils.getColorAttrDefaultColor(getContext(), 16842800);
        float intensity = getColorIntensity(colorForeground);
        int fillColor = this.mDualToneHandler.getSingleColor(intensity);
        this.mBatteryRemainingIcon.onDarkChanged(tintArea, intensity, fillColor);
    }

    public void setCallback(QSDetail.Callback qsPanelCallback) {
        this.mHeaderQsPanel.setCallback(qsPanelCallback);
    }

    private String formatNextAlarm(AlarmManager.AlarmClockInfo info) {
        if (info == null) {
            return "";
        }
        String skeleton = DateFormat.is24HourFormat(this.mContext, ActivityManager.getCurrentUser()) ? "EHm" : "Ehma";
        String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
        return DateFormat.format(pattern, info.getTriggerTime()).toString();
    }

    public static float getColorIntensity(int color) {
        return color == -1 ? 0.0f : 1.0f;
    }

    public void setMargins(int sideMargins) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v != this.mSystemIconsView && v != this.mQuickQsStatusIcons && v != this.mHeaderQsPanel && v != this.mHeaderTextContainerView) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
                lp.leftMargin = sideMargins;
                lp.rightMargin = sideMargins;
            }
        }
    }
}
