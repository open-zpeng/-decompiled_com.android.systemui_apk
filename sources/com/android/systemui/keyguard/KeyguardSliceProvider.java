package com.android.systemui.keyguard;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Handler;
import android.os.Trace;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.R;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.NextAlarmControllerImpl;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl;
import com.android.systemui.util.wakelock.SettableWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
import com.xiaopeng.systemui.controller.OsdController;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
/* loaded from: classes21.dex */
public class KeyguardSliceProvider extends SliceProvider implements NextAlarmController.NextAlarmChangeCallback, ZenModeController.Callback, NotificationMediaManager.MediaListener, StatusBarStateController.StateListener {
    @VisibleForTesting
    static final int ALARM_VISIBILITY_HOURS = 12;
    private static final StyleSpan BOLD_STYLE = new StyleSpan(1);
    public static final String KEYGUARD_ACTION_URI = "content://com.android.systemui.keyguard/action";
    public static final String KEYGUARD_DATE_URI = "content://com.android.systemui.keyguard/date";
    public static final String KEYGUARD_DND_URI = "content://com.android.systemui.keyguard/dnd";
    private static final String KEYGUARD_HEADER_URI = "content://com.android.systemui.keyguard/header";
    public static final String KEYGUARD_MEDIA_URI = "content://com.android.systemui.keyguard/media";
    public static final String KEYGUARD_NEXT_ALARM_URI = "content://com.android.systemui.keyguard/alarm";
    public static final String KEYGUARD_SLICE_URI = "content://com.android.systemui.keyguard/main";
    private static KeyguardSliceProvider sInstance;
    @VisibleForTesting
    protected AlarmManager mAlarmManager;
    @VisibleForTesting
    protected ContentResolver mContentResolver;
    private DateFormat mDateFormat;
    private String mDatePattern;
    private DozeParameters mDozeParameters;
    protected boolean mDozing;
    private KeyguardBypassController mKeyguardBypassController;
    private String mLastText;
    private CharSequence mMediaArtist;
    private boolean mMediaIsVisible;
    protected NotificationMediaManager mMediaManager;
    private CharSequence mMediaTitle;
    @VisibleForTesting
    protected SettableWakeLock mMediaWakeLock;
    private String mNextAlarm;
    private NextAlarmController mNextAlarmController;
    private AlarmManager.AlarmClockInfo mNextAlarmInfo;
    private PendingIntent mPendingIntent;
    private boolean mRegistered;
    private int mStatusBarState;
    private StatusBarStateController mStatusBarStateController;
    @VisibleForTesting
    protected ZenModeController mZenModeController;
    private final Date mCurrentTime = new Date();
    private final AlarmManager.OnAlarmListener mUpdateNextAlarm = new AlarmManager.OnAlarmListener() { // from class: com.android.systemui.keyguard.-$$Lambda$KeyguardSliceProvider$IhzByd8TsqFuOrSyuGurVskyPLo
        @Override // android.app.AlarmManager.OnAlarmListener
        public final void onAlarm() {
            KeyguardSliceProvider.this.updateNextAlarm();
        }
    };
    @VisibleForTesting
    final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.systemui.keyguard.KeyguardSliceProvider.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.DATE_CHANGED".equals(action)) {
                synchronized (this) {
                    KeyguardSliceProvider.this.updateClockLocked();
                }
            } else if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
                synchronized (this) {
                    KeyguardSliceProvider.this.cleanDateFormatLocked();
                }
            }
        }
    };
    @VisibleForTesting
    final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.keyguard.KeyguardSliceProvider.2
        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onTimeChanged() {
            synchronized (this) {
                KeyguardSliceProvider.this.updateClockLocked();
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onTimeZoneChanged(TimeZone timeZone) {
            synchronized (this) {
                KeyguardSliceProvider.this.cleanDateFormatLocked();
            }
        }
    };
    private final Handler mHandler = new Handler();
    private final Handler mMediaHandler = new Handler();
    protected final Uri mSliceUri = Uri.parse(KEYGUARD_SLICE_URI);
    protected final Uri mHeaderUri = Uri.parse(KEYGUARD_HEADER_URI);
    protected final Uri mDateUri = Uri.parse(KEYGUARD_DATE_URI);
    protected final Uri mAlarmUri = Uri.parse(KEYGUARD_NEXT_ALARM_URI);
    protected final Uri mDndUri = Uri.parse(KEYGUARD_DND_URI);
    protected final Uri mMediaUri = Uri.parse(KEYGUARD_MEDIA_URI);

    public static KeyguardSliceProvider getAttachedInstance() {
        return sInstance;
    }

    public void initDependencies(NotificationMediaManager mediaManager, StatusBarStateController statusBarStateController, KeyguardBypassController keyguardBypassController, DozeParameters dozeParameters) {
        this.mMediaManager = mediaManager;
        this.mMediaManager.addCallback(this);
        this.mStatusBarStateController = statusBarStateController;
        this.mStatusBarStateController.addCallback(this);
        this.mKeyguardBypassController = keyguardBypassController;
        this.mDozeParameters = dozeParameters;
    }

    @Override // androidx.slice.SliceProvider
    public Slice onBindSlice(Uri sliceUri) {
        Slice slice;
        Trace.beginSection("KeyguardSliceProvider#onBindSlice");
        synchronized (this) {
            ListBuilder builder = new ListBuilder(getContext(), this.mSliceUri, -1L);
            if (needsMediaLocked()) {
                addMediaLocked(builder);
            } else {
                builder.addRow(new ListBuilder.RowBuilder(this.mDateUri).setTitle(this.mLastText));
            }
            addNextAlarmLocked(builder);
            addZenModeLocked(builder);
            addPrimaryActionLocked(builder);
            slice = builder.build();
        }
        Trace.endSection();
        return slice;
    }

    protected boolean needsMediaLocked() {
        KeyguardBypassController keyguardBypassController = this.mKeyguardBypassController;
        boolean keepWhenAwake = keyguardBypassController != null && keyguardBypassController.getBypassEnabled() && this.mDozeParameters.getAlwaysOn();
        boolean keepWhenShade = this.mStatusBarState == 0 && this.mMediaIsVisible;
        return !TextUtils.isEmpty(this.mMediaTitle) && this.mMediaIsVisible && (this.mDozing || keepWhenAwake || keepWhenShade);
    }

    protected void addMediaLocked(ListBuilder listBuilder) {
        if (TextUtils.isEmpty(this.mMediaTitle)) {
            return;
        }
        listBuilder.setHeader(new ListBuilder.HeaderBuilder(this.mHeaderUri).setTitle(this.mMediaTitle));
        if (!TextUtils.isEmpty(this.mMediaArtist)) {
            ListBuilder.RowBuilder albumBuilder = new ListBuilder.RowBuilder(this.mMediaUri);
            albumBuilder.setTitle(this.mMediaArtist);
            NotificationMediaManager notificationMediaManager = this.mMediaManager;
            Icon mediaIcon = notificationMediaManager == null ? null : notificationMediaManager.getMediaIcon();
            IconCompat mediaIconCompat = mediaIcon != null ? IconCompat.createFromIcon(getContext(), mediaIcon) : null;
            if (mediaIconCompat != null) {
                albumBuilder.addEndItem(mediaIconCompat, 0);
            }
            listBuilder.addRow(albumBuilder);
        }
    }

    protected void addPrimaryActionLocked(ListBuilder builder) {
        IconCompat icon = IconCompat.createWithResource(getContext(), R.drawable.ic_access_alarms_big);
        SliceAction action = SliceAction.createDeeplink(this.mPendingIntent, icon, 0, this.mLastText);
        ListBuilder.RowBuilder primaryActionRow = new ListBuilder.RowBuilder(Uri.parse(KEYGUARD_ACTION_URI)).setPrimaryAction(action);
        builder.addRow(primaryActionRow);
    }

    protected void addNextAlarmLocked(ListBuilder builder) {
        if (TextUtils.isEmpty(this.mNextAlarm)) {
            return;
        }
        IconCompat alarmIcon = IconCompat.createWithResource(getContext(), R.drawable.ic_access_alarms_big);
        ListBuilder.RowBuilder alarmRowBuilder = new ListBuilder.RowBuilder(this.mAlarmUri).setTitle(this.mNextAlarm).addEndItem(alarmIcon, 0);
        builder.addRow(alarmRowBuilder);
    }

    protected void addZenModeLocked(ListBuilder builder) {
        if (!isDndOn()) {
            return;
        }
        ListBuilder.RowBuilder dndBuilder = new ListBuilder.RowBuilder(this.mDndUri).setContentDescription(getContext().getResources().getString(R.string.accessibility_quick_settings_dnd)).addEndItem(IconCompat.createWithResource(getContext(), R.drawable.stat_sys_dnd), 0);
        builder.addRow(dndBuilder);
    }

    protected boolean isDndOn() {
        return this.mZenModeController.getZen() != 0;
    }

    @Override // androidx.slice.SliceProvider
    public boolean onCreateSliceProvider() {
        synchronized (this) {
            KeyguardSliceProvider oldInstance = sInstance;
            if (oldInstance != null) {
                oldInstance.onDestroy();
            }
            this.mAlarmManager = (AlarmManager) getContext().getSystemService(AlarmManager.class);
            this.mContentResolver = getContext().getContentResolver();
            this.mNextAlarmController = new NextAlarmControllerImpl(getContext());
            this.mNextAlarmController.addCallback(this);
            this.mZenModeController = new ZenModeControllerImpl(getContext(), this.mHandler);
            this.mZenModeController.addCallback(this);
            this.mDatePattern = getContext().getString(R.string.system_ui_aod_date_pattern);
            this.mPendingIntent = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), KeyguardSliceProvider.class), 0);
            this.mMediaWakeLock = new SettableWakeLock(WakeLock.createPartial(getContext(), "media"), "media");
            sInstance = this;
            registerClockUpdate();
            updateClockLocked();
        }
        return true;
    }

    @VisibleForTesting
    protected void onDestroy() {
        synchronized (this) {
            this.mNextAlarmController.removeCallback(this);
            this.mZenModeController.removeCallback(this);
            this.mMediaWakeLock.setAcquired(false);
            this.mAlarmManager.cancel(this.mUpdateNextAlarm);
            if (this.mRegistered) {
                this.mRegistered = false;
                getKeyguardUpdateMonitor().removeCallback(this.mKeyguardUpdateMonitorCallback);
                getContext().unregisterReceiver(this.mIntentReceiver);
            }
        }
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
    public void onZenChanged(int zen) {
        notifyChange();
    }

    @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
    public void onConfigChanged(ZenModeConfig config) {
        notifyChange();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNextAlarm() {
        synchronized (this) {
            if (withinNHoursLocked(this.mNextAlarmInfo, 12)) {
                String pattern = android.text.format.DateFormat.is24HourFormat(getContext(), ActivityManager.getCurrentUser()) ? "HH:mm" : "h:mm";
                this.mNextAlarm = android.text.format.DateFormat.format(pattern, this.mNextAlarmInfo.getTriggerTime()).toString();
            } else {
                this.mNextAlarm = "";
            }
        }
        notifyChange();
    }

    private boolean withinNHoursLocked(AlarmManager.AlarmClockInfo alarmClockInfo, int hours) {
        if (alarmClockInfo == null) {
            return false;
        }
        long limit = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(hours);
        return this.mNextAlarmInfo.getTriggerTime() <= limit;
    }

    @VisibleForTesting
    protected void registerClockUpdate() {
        synchronized (this) {
            if (this.mRegistered) {
                return;
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.DATE_CHANGED");
            filter.addAction("android.intent.action.LOCALE_CHANGED");
            getContext().registerReceiver(this.mIntentReceiver, filter, null, null);
            getKeyguardUpdateMonitor().registerCallback(this.mKeyguardUpdateMonitorCallback);
            this.mRegistered = true;
        }
    }

    @VisibleForTesting
    boolean isRegistered() {
        boolean z;
        synchronized (this) {
            z = this.mRegistered;
        }
        return z;
    }

    protected void updateClockLocked() {
        String text = getFormattedDateLocked();
        if (!text.equals(this.mLastText)) {
            this.mLastText = text;
            notifyChange();
        }
    }

    protected String getFormattedDateLocked() {
        if (this.mDateFormat == null) {
            Locale l = Locale.getDefault();
            DateFormat format = DateFormat.getInstanceForSkeleton(this.mDatePattern, l);
            format.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
            this.mDateFormat = format;
        }
        this.mCurrentTime.setTime(System.currentTimeMillis());
        return this.mDateFormat.format(this.mCurrentTime);
    }

    @VisibleForTesting
    void cleanDateFormatLocked() {
        this.mDateFormat = null;
    }

    @Override // com.android.systemui.statusbar.policy.NextAlarmController.NextAlarmChangeCallback
    public void onNextAlarmChanged(AlarmManager.AlarmClockInfo nextAlarm) {
        synchronized (this) {
            this.mNextAlarmInfo = nextAlarm;
            this.mAlarmManager.cancel(this.mUpdateNextAlarm);
            long triggerAt = this.mNextAlarmInfo == null ? -1L : this.mNextAlarmInfo.getTriggerTime() - TimeUnit.HOURS.toMillis(12L);
            if (triggerAt > 0) {
                this.mAlarmManager.setExact(1, triggerAt, "lock_screen_next_alarm", this.mUpdateNextAlarm, this.mHandler);
            }
        }
        updateNextAlarm();
    }

    @VisibleForTesting
    protected KeyguardUpdateMonitor getKeyguardUpdateMonitor() {
        return KeyguardUpdateMonitor.getInstance(getContext());
    }

    @Override // com.android.systemui.statusbar.NotificationMediaManager.MediaListener
    public void onMetadataOrStateChanged(final MediaMetadata metadata, final int state) {
        synchronized (this) {
            boolean nextVisible = NotificationMediaManager.isPlayingState(state);
            this.mMediaHandler.removeCallbacksAndMessages(null);
            if (this.mMediaIsVisible && !nextVisible && this.mStatusBarState != 0) {
                this.mMediaWakeLock.setAcquired(true);
                this.mMediaHandler.postDelayed(new Runnable() { // from class: com.android.systemui.keyguard.-$$Lambda$KeyguardSliceProvider$nRbfFxAPvCUbdEsypLUXXuYm6z0
                    @Override // java.lang.Runnable
                    public final void run() {
                        KeyguardSliceProvider.this.lambda$onMetadataOrStateChanged$0$KeyguardSliceProvider(metadata, state);
                    }
                }, OsdController.TN.DURATION_TIMEOUT_SHORT);
            } else {
                this.mMediaWakeLock.setAcquired(false);
                updateMediaStateLocked(metadata, state);
            }
        }
    }

    public /* synthetic */ void lambda$onMetadataOrStateChanged$0$KeyguardSliceProvider(MediaMetadata metadata, int state) {
        synchronized (this) {
            updateMediaStateLocked(metadata, state);
            this.mMediaWakeLock.setAcquired(false);
        }
    }

    private void updateMediaStateLocked(MediaMetadata metadata, int state) {
        boolean nextVisible = NotificationMediaManager.isPlayingState(state);
        CharSequence title = null;
        if (metadata != null) {
            title = metadata.getText("android.media.metadata.TITLE");
            if (TextUtils.isEmpty(title)) {
                title = getContext().getResources().getString(R.string.music_controls_no_title);
            }
        }
        CharSequence artist = metadata == null ? null : metadata.getText("android.media.metadata.ARTIST");
        if (nextVisible == this.mMediaIsVisible && TextUtils.equals(title, this.mMediaTitle) && TextUtils.equals(artist, this.mMediaArtist)) {
            return;
        }
        this.mMediaTitle = title;
        this.mMediaArtist = artist;
        this.mMediaIsVisible = nextVisible;
        notifyChange();
    }

    protected void notifyChange() {
        this.mContentResolver.notifyChange(this.mSliceUri, null);
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        boolean neededMedia;
        synchronized (this) {
            boolean neededMedia2 = needsMediaLocked();
            this.mDozing = isDozing;
            neededMedia = neededMedia2 != needsMediaLocked();
        }
        if (neededMedia) {
            notifyChange();
        }
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
        boolean needsMedia;
        synchronized (this) {
            boolean needsMedia2 = needsMediaLocked();
            this.mStatusBarState = newState;
            needsMedia = needsMedia2 != needsMediaLocked();
        }
        if (needsMedia) {
            notifyChange();
        }
    }
}
