package com.android.systemui.volume;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioSystem;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.slice.core.SliceHints;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.Prefs;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.VolumeDialog;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.volume.CaptionsToggleImageButton;
import com.android.systemui.volume.SystemUIInterpolators;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public class VolumeDialogImpl implements VolumeDialog, ConfigurationController.ConfigurationListener {
    static final int DIALOG_HIDE_ANIMATION_DURATION = 250;
    static final int DIALOG_HOVERING_TIMEOUT_MILLIS = 16000;
    static final int DIALOG_ODI_CAPTIONS_TOOLTIP_TIMEOUT_MILLIS = 5000;
    static final int DIALOG_SAFETYWARNING_TIMEOUT_MILLIS = 5000;
    static final int DIALOG_SHOW_ANIMATION_DURATION = 300;
    static final int DIALOG_TIMEOUT_MILLIS = 3000;
    private static final String TAG = Util.logTag(VolumeDialogImpl.class);
    private static final int UPDATE_ANIMATION_DURATION = 80;
    private static final long USER_ATTEMPT_GRACE_PERIOD = 1000;
    private int mActiveStream;
    private final ActivityManager mActivityManager;
    private ConfigurableTexts mConfigurableTexts;
    private final Context mContext;
    private CustomDialog mDialog;
    private ViewGroup mDialogRowsView;
    private ViewGroup mDialogView;
    private boolean mHasSeenODICaptionsTooltip;
    private final KeyguardManager mKeyguard;
    private CaptionsToggleImageButton mODICaptionsIcon;
    private ViewStub mODICaptionsTooltipViewStub;
    private ViewGroup mODICaptionsView;
    private int mPrevActiveStream;
    private ViewGroup mRinger;
    private ImageButton mRingerIcon;
    private SafetyWarningDialog mSafetyWarning;
    private ImageButton mSettingsIcon;
    private View mSettingsView;
    private boolean mShowA11yStream;
    private boolean mShowing;
    private VolumeDialogController.State mState;
    private Window mWindow;
    private FrameLayout mZenIcon;
    private final H mHandler = new H();
    private final List<VolumeRow> mRows = new ArrayList();
    private final SparseBooleanArray mDynamic = new SparseBooleanArray();
    private final Object mSafetyWarningLock = new Object();
    private final Accessibility mAccessibility = new Accessibility();
    private boolean mAutomute = true;
    private boolean mSilentMode = true;
    private boolean mHovering = false;
    private boolean mConfigChanged = false;
    private View mODICaptionsTooltipView = null;
    private final VolumeDialogController.Callbacks mControllerCallbackH = new VolumeDialogController.Callbacks() { // from class: com.android.systemui.volume.VolumeDialogImpl.3
        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onShowRequested(int reason) {
            VolumeDialogImpl.this.showH(reason);
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onDismissRequested(int reason) {
            VolumeDialogImpl.this.dismissH(reason);
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onScreenOff() {
            VolumeDialogImpl.this.dismissH(4);
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onStateChanged(VolumeDialogController.State state) {
            VolumeDialogImpl.this.onStateChangedH(state);
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onLayoutDirectionChanged(int layoutDirection) {
            VolumeDialogImpl.this.mDialogView.setLayoutDirection(layoutDirection);
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onConfigurationChanged() {
            VolumeDialogImpl.this.mDialog.dismiss();
            VolumeDialogImpl.this.mConfigChanged = true;
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onShowVibrateHint() {
            if (VolumeDialogImpl.this.mSilentMode) {
                VolumeDialogImpl.this.mController.setRingerMode(0, false);
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onShowSilentHint() {
            if (VolumeDialogImpl.this.mSilentMode) {
                VolumeDialogImpl.this.mController.setRingerMode(2, false);
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onShowSafetyWarning(int flags) {
            VolumeDialogImpl.this.showSafetyWarningH(flags);
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onAccessibilityModeChanged(Boolean showA11yStream) {
            VolumeDialogImpl.this.mShowA11yStream = showA11yStream == null ? false : showA11yStream.booleanValue();
            VolumeRow activeRow = VolumeDialogImpl.this.getActiveRow();
            if (VolumeDialogImpl.this.mShowA11yStream || 10 != activeRow.stream) {
                VolumeDialogImpl.this.updateRowsH(activeRow);
            } else {
                VolumeDialogImpl.this.dismissH(7);
            }
        }

        @Override // com.android.systemui.plugins.VolumeDialogController.Callbacks
        public void onCaptionComponentStateChanged(Boolean isComponentEnabled, Boolean fromTooltip) {
            VolumeDialogImpl.this.updateODICaptionsH(isComponentEnabled.booleanValue(), fromTooltip.booleanValue());
        }
    };
    private final VolumeDialogController mController = (VolumeDialogController) Dependency.get(VolumeDialogController.class);
    private final AccessibilityManagerWrapper mAccessibilityMgr = (AccessibilityManagerWrapper) Dependency.get(AccessibilityManagerWrapper.class);
    private final DeviceProvisionedController mDeviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
    private boolean mShowActiveStreamOnly = showActiveStreamOnly();

    public VolumeDialogImpl(Context context) {
        this.mContext = new ContextThemeWrapper(context, R.style.qs_theme);
        this.mKeyguard = (KeyguardManager) this.mContext.getSystemService("keyguard");
        this.mActivityManager = (ActivityManager) this.mContext.getSystemService(SliceHints.HINT_ACTIVITY);
        this.mHasSeenODICaptionsTooltip = Prefs.getBoolean(context, Prefs.Key.HAS_SEEN_ODI_CAPTIONS_TOOLTIP, false);
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onUiModeChanged() {
        this.mContext.getTheme().applyStyle(this.mContext.getThemeResId(), true);
    }

    @Override // com.android.systemui.plugins.VolumeDialog
    public void init(int windowType, VolumeDialog.Callback callback) {
        initDialog();
        this.mAccessibility.init();
        this.mController.addCallback(this.mControllerCallbackH, this.mHandler);
        this.mController.getState();
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
    }

    @Override // com.android.systemui.plugins.VolumeDialog
    public void destroy() {
        this.mController.removeCallback(this.mControllerCallbackH);
        this.mHandler.removeCallbacksAndMessages(null);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).removeCallback(this);
    }

    private void initDialog() {
        ViewStub viewStub;
        this.mDialog = new CustomDialog(this.mContext);
        this.mConfigurableTexts = new ConfigurableTexts(this.mContext);
        this.mHovering = false;
        this.mShowing = false;
        this.mWindow = this.mDialog.getWindow();
        this.mWindow.requestFeature(1);
        this.mWindow.setBackgroundDrawable(new ColorDrawable(0));
        this.mWindow.clearFlags(65538);
        this.mWindow.addFlags(17563944);
        this.mWindow.setType(2020);
        this.mWindow.setWindowAnimations(16973828);
        WindowManager.LayoutParams lp = this.mWindow.getAttributes();
        lp.format = -3;
        lp.setTitle(VolumeDialogImpl.class.getSimpleName());
        lp.windowAnimations = -1;
        lp.gravity = 21;
        this.mWindow.setAttributes(lp);
        this.mWindow.setLayout(-2, -2);
        this.mDialog.setContentView(R.layout.volume_dialog);
        this.mDialogView = (ViewGroup) this.mDialog.findViewById(R.id.volume_dialog);
        this.mDialogView.setAlpha(0.0f);
        this.mDialog.setCanceledOnTouchOutside(true);
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$8BZhTIdOE2rPYfFa5HbcUDCtXeM
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                VolumeDialogImpl.this.lambda$initDialog$1$VolumeDialogImpl(dialogInterface);
            }
        });
        this.mDialogView.setOnHoverListener(new View.OnHoverListener() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$T52d0W13mYvykk6ORgbytqfZsps
            @Override // android.view.View.OnHoverListener
            public final boolean onHover(View view, MotionEvent motionEvent) {
                return VolumeDialogImpl.this.lambda$initDialog$2$VolumeDialogImpl(view, motionEvent);
            }
        });
        this.mDialogRowsView = (ViewGroup) this.mDialog.findViewById(R.id.volume_dialog_rows);
        this.mRinger = (ViewGroup) this.mDialog.findViewById(R.id.ringer);
        ViewGroup viewGroup = this.mRinger;
        if (viewGroup != null) {
            this.mRingerIcon = (ImageButton) viewGroup.findViewById(R.id.ringer_icon);
            this.mZenIcon = (FrameLayout) this.mRinger.findViewById(R.id.dnd_icon);
        }
        this.mODICaptionsView = (ViewGroup) this.mDialog.findViewById(R.id.odi_captions);
        ViewGroup viewGroup2 = this.mODICaptionsView;
        if (viewGroup2 != null) {
            this.mODICaptionsIcon = (CaptionsToggleImageButton) viewGroup2.findViewById(R.id.odi_captions_icon);
        }
        this.mODICaptionsTooltipViewStub = (ViewStub) this.mDialog.findViewById(R.id.odi_captions_tooltip_stub);
        if (this.mHasSeenODICaptionsTooltip && (viewStub = this.mODICaptionsTooltipViewStub) != null) {
            this.mDialogView.removeView(viewStub);
            this.mODICaptionsTooltipViewStub = null;
        }
        this.mSettingsView = this.mDialog.findViewById(R.id.settings_container);
        this.mSettingsIcon = (ImageButton) this.mDialog.findViewById(R.id.settings);
        if (this.mRows.isEmpty()) {
            if (!AudioSystem.isSingleVolume(this.mContext)) {
                addRow(10, R.drawable.ic_volume_accessibility, R.drawable.ic_volume_accessibility, true, false);
            }
            addRow(3, R.drawable.ic_volume_media, R.drawable.ic_volume_media_mute, true, true);
            if (!AudioSystem.isSingleVolume(this.mContext)) {
                addRow(2, R.drawable.ic_volume_ringer, R.drawable.ic_volume_ringer_mute, true, false);
                addRow(4, R.drawable.ic_alarm, R.drawable.ic_volume_alarm_mute, true, false);
                addRow(0, 17302773, 17302773, false, false);
                addRow(6, R.drawable.ic_volume_bt_sco, R.drawable.ic_volume_bt_sco, false, false);
                addRow(1, R.drawable.ic_volume_system, R.drawable.ic_volume_system_mute, false, false);
            }
        } else {
            addExistingRows();
        }
        updateRowsH(getActiveRow());
        initRingerH();
        initSettingsH();
        initODICaptionsH();
    }

    public /* synthetic */ void lambda$initDialog$1$VolumeDialogImpl(DialogInterface dialog) {
        if (!isLandscape()) {
            ViewGroup viewGroup = this.mDialogView;
            viewGroup.setTranslationX(viewGroup.getWidth() / 2.0f);
        }
        this.mDialogView.setAlpha(0.0f);
        this.mDialogView.animate().alpha(1.0f).translationX(0.0f).setDuration(300L).setInterpolator(new SystemUIInterpolators.LogDecelerateInterpolator()).withEndAction(new Runnable() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$vBH_Cy2LsLvfluWDg0W4IzJ1dm8
            @Override // java.lang.Runnable
            public final void run() {
                VolumeDialogImpl.this.lambda$initDialog$0$VolumeDialogImpl();
            }
        }).start();
    }

    public /* synthetic */ void lambda$initDialog$0$VolumeDialogImpl() {
        ImageButton imageButton;
        if (!Prefs.getBoolean(this.mContext, Prefs.Key.TOUCHED_RINGER_TOGGLE, false) && (imageButton = this.mRingerIcon) != null) {
            imageButton.postOnAnimationDelayed(getSinglePressFor(imageButton), 1500L);
        }
    }

    public /* synthetic */ boolean lambda$initDialog$2$VolumeDialogImpl(View v, MotionEvent event) {
        int action = event.getActionMasked();
        this.mHovering = action == 9 || action == 7;
        rescheduleTimeoutH();
        return true;
    }

    protected ViewGroup getDialogView() {
        return this.mDialogView;
    }

    private int getAlphaAttr(int attr) {
        TypedArray ta = this.mContext.obtainStyledAttributes(new int[]{attr});
        float alpha = ta.getFloat(0, 0.0f);
        ta.recycle();
        return (int) (255.0f * alpha);
    }

    private boolean isLandscape() {
        return this.mContext.getResources().getConfiguration().orientation == 2;
    }

    public void setStreamImportant(int stream, boolean important) {
        this.mHandler.obtainMessage(5, stream, important ? 1 : 0).sendToTarget();
    }

    public void setAutomute(boolean automute) {
        if (this.mAutomute == automute) {
            return;
        }
        this.mAutomute = automute;
        this.mHandler.sendEmptyMessage(4);
    }

    public void setSilentMode(boolean silentMode) {
        if (this.mSilentMode == silentMode) {
            return;
        }
        this.mSilentMode = silentMode;
        this.mHandler.sendEmptyMessage(4);
    }

    private void addRow(int stream, int iconRes, int iconMuteRes, boolean important, boolean defaultStream) {
        addRow(stream, iconRes, iconMuteRes, important, defaultStream, false);
    }

    private void addRow(int stream, int iconRes, int iconMuteRes, boolean important, boolean defaultStream, boolean dynamic) {
        if (D.BUG) {
            String str = TAG;
            Slog.d(str, "Adding row for stream " + stream);
        }
        VolumeRow row = new VolumeRow();
        initRow(row, stream, iconRes, iconMuteRes, important, defaultStream);
        this.mDialogRowsView.addView(row.view);
        this.mRows.add(row);
    }

    private void addExistingRows() {
        int N = this.mRows.size();
        for (int i = 0; i < N; i++) {
            VolumeRow row = this.mRows.get(i);
            initRow(row, row.stream, row.iconRes, row.iconMuteRes, row.important, row.defaultStream);
            this.mDialogRowsView.addView(row.view);
            updateVolumeRowH(row);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public VolumeRow getActiveRow() {
        for (VolumeRow row : this.mRows) {
            if (row.stream == this.mActiveStream) {
                return row;
            }
        }
        for (VolumeRow row2 : this.mRows) {
            if (row2.stream == 3) {
                return row2;
            }
        }
        return this.mRows.get(0);
    }

    private VolumeRow findRow(int stream) {
        for (VolumeRow row : this.mRows) {
            if (row.stream == stream) {
                return row;
            }
        }
        return null;
    }

    public void dump(PrintWriter writer) {
        writer.println(VolumeDialogImpl.class.getSimpleName() + " state:");
        writer.print("  mShowing: ");
        writer.println(this.mShowing);
        writer.print("  mActiveStream: ");
        writer.println(this.mActiveStream);
        writer.print("  mDynamic: ");
        writer.println(this.mDynamic);
        writer.print("  mAutomute: ");
        writer.println(this.mAutomute);
        writer.print("  mSilentMode: ");
        writer.println(this.mSilentMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getImpliedLevel(SeekBar seekBar, int progress) {
        int m = seekBar.getMax();
        int n = (m / 100) - 1;
        if (progress == 0) {
            return 0;
        }
        if (progress == m) {
            int level = m / 100;
            return level;
        }
        int level2 = ((int) ((progress / m) * n)) + 1;
        return level2;
    }

    @SuppressLint({"InflateParams"})
    private void initRow(final VolumeRow row, final int stream, int iconRes, int iconMuteRes, boolean important, boolean defaultStream) {
        row.stream = stream;
        row.iconRes = iconRes;
        row.iconMuteRes = iconMuteRes;
        row.important = important;
        row.defaultStream = defaultStream;
        row.view = this.mDialog.getLayoutInflater().inflate(R.layout.volume_dialog_row, (ViewGroup) null);
        row.view.setId(row.stream);
        row.view.setTag(row);
        row.header = (TextView) row.view.findViewById(R.id.volume_row_header);
        row.header.setId(row.stream * 20);
        if (stream == 10) {
            row.header.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        }
        row.dndIcon = (FrameLayout) row.view.findViewById(R.id.dnd_icon);
        row.slider = (SeekBar) row.view.findViewById(R.id.volume_row_slider);
        row.slider.setOnSeekBarChangeListener(new VolumeSeekBarChangeListener(row));
        row.anim = null;
        row.icon = (ImageButton) row.view.findViewById(R.id.volume_row_icon);
        row.icon.setImageResource(iconRes);
        if (row.stream != 10) {
            row.icon.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$I-0sumSTzcnKtt5xn4YVlQQget8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    VolumeDialogImpl.this.lambda$initRow$3$VolumeDialogImpl(row, stream, view);
                }
            });
        } else {
            row.icon.setImportantForAccessibility(2);
        }
    }

    public /* synthetic */ void lambda$initRow$3$VolumeDialogImpl(VolumeRow row, int stream, View v) {
        Events.writeEvent(this.mContext, 7, Integer.valueOf(row.stream), Integer.valueOf(row.iconState));
        this.mController.setActiveStream(row.stream);
        if (row.stream == 2) {
            boolean hasVibrator = this.mController.hasVibrator();
            if (this.mState.ringerModeInternal == 2) {
                if (hasVibrator) {
                    this.mController.setRingerMode(1, false);
                } else {
                    boolean wasZero = row.ss.level == 0;
                    this.mController.setStreamVolume(stream, wasZero ? row.lastAudibleLevel : 0);
                }
            } else {
                this.mController.setRingerMode(2, false);
                if (row.ss.level == 0) {
                    this.mController.setStreamVolume(stream, 1);
                }
            }
        } else {
            this.mController.setStreamVolume(stream, (row.ss.level == row.ss.levelMin ? 1 : 0) != 0 ? row.lastAudibleLevel : row.ss.levelMin);
        }
        row.userAttempt = 0L;
    }

    public void initSettingsH() {
        View view = this.mSettingsView;
        if (view != null) {
            view.setVisibility((this.mDeviceProvisionedController.isCurrentUserSetup() && this.mActivityManager.getLockTaskModeState() == 0) ? 0 : 8);
        }
        ImageButton imageButton = this.mSettingsIcon;
        if (imageButton != null) {
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$7RdQKc1FND8ZrjtxSEsHEKXSyeY
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    VolumeDialogImpl.this.lambda$initSettingsH$4$VolumeDialogImpl(view2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$initSettingsH$4$VolumeDialogImpl(View v) {
        Events.writeEvent(this.mContext, 8, new Object[0]);
        Intent intent = new Intent("android.settings.panel.action.VOLUME");
        dismissH(5);
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).startActivity(intent, true);
    }

    public void initRingerH() {
        ImageButton imageButton = this.mRingerIcon;
        if (imageButton != null) {
            imageButton.setAccessibilityLiveRegion(1);
            this.mRingerIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$leUR0c6hrY1TNx5XUG-xhXI1EHk
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    VolumeDialogImpl.this.lambda$initRingerH$5$VolumeDialogImpl(view);
                }
            });
        }
        updateRingerH();
    }

    public /* synthetic */ void lambda$initRingerH$5$VolumeDialogImpl(View v) {
        int newRingerMode;
        Prefs.putBoolean(this.mContext, Prefs.Key.TOUCHED_RINGER_TOGGLE, true);
        VolumeDialogController.StreamState ss = this.mState.states.get(2);
        if (ss == null) {
            return;
        }
        boolean hasVibrator = this.mController.hasVibrator();
        if (this.mState.ringerModeInternal == 2) {
            if (hasVibrator) {
                newRingerMode = 1;
            } else {
                newRingerMode = 0;
            }
        } else if (this.mState.ringerModeInternal == 1) {
            newRingerMode = 0;
        } else {
            if (ss.level == 0) {
                this.mController.setStreamVolume(2, 1);
            }
            newRingerMode = 2;
        }
        Events.writeEvent(this.mContext, 18, Integer.valueOf(newRingerMode));
        incrementManualToggleCount();
        updateRingerH();
        provideTouchFeedbackH(newRingerMode);
        this.mController.setRingerMode(newRingerMode, false);
        maybeShowToastH(newRingerMode);
    }

    private void initODICaptionsH() {
        CaptionsToggleImageButton captionsToggleImageButton = this.mODICaptionsIcon;
        if (captionsToggleImageButton != null) {
            captionsToggleImageButton.setOnConfirmedTapListener(new CaptionsToggleImageButton.ConfirmedTapListener() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$HIlX6MPuNck4Zm6cfzTdHTUxqn4
                @Override // com.android.systemui.volume.CaptionsToggleImageButton.ConfirmedTapListener
                public final void onConfirmedTap() {
                    VolumeDialogImpl.this.lambda$initODICaptionsH$6$VolumeDialogImpl();
                }
            }, this.mHandler);
        }
        this.mController.getCaptionsComponentState(false);
    }

    public /* synthetic */ void lambda$initODICaptionsH$6$VolumeDialogImpl() {
        onCaptionIconClicked();
        Events.writeEvent(this.mContext, 21, new Object[0]);
    }

    private void checkODICaptionsTooltip(boolean fromDismiss) {
        if (!this.mHasSeenODICaptionsTooltip && !fromDismiss && this.mODICaptionsTooltipViewStub != null) {
            this.mController.getCaptionsComponentState(true);
        } else if (this.mHasSeenODICaptionsTooltip && fromDismiss && this.mODICaptionsTooltipView != null) {
            hideCaptionsTooltip();
        }
    }

    protected void showCaptionsTooltip() {
        ViewStub viewStub;
        if (!this.mHasSeenODICaptionsTooltip && (viewStub = this.mODICaptionsTooltipViewStub) != null) {
            this.mODICaptionsTooltipView = viewStub.inflate();
            this.mODICaptionsTooltipView.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$TUvPGuqHQwDl_-z3hgYr3GMVgOs
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    VolumeDialogImpl.this.lambda$showCaptionsTooltip$7$VolumeDialogImpl(view);
                }
            });
            this.mODICaptionsTooltipViewStub = null;
            rescheduleTimeoutH();
        }
        View view = this.mODICaptionsTooltipView;
        if (view != null) {
            view.setAlpha(0.0f);
            this.mODICaptionsTooltipView.animate().alpha(1.0f).setStartDelay(300L).withEndAction(new Runnable() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$j7bv45Q5uulCvMsn_IeT1Mv2PxI
                @Override // java.lang.Runnable
                public final void run() {
                    VolumeDialogImpl.this.lambda$showCaptionsTooltip$8$VolumeDialogImpl();
                }
            }).start();
        }
    }

    public /* synthetic */ void lambda$showCaptionsTooltip$7$VolumeDialogImpl(View v) {
        hideCaptionsTooltip();
        Events.writeEvent(this.mContext, 22, new Object[0]);
    }

    public /* synthetic */ void lambda$showCaptionsTooltip$8$VolumeDialogImpl() {
        if (D.BUG) {
            Log.d(TAG, "tool:checkODICaptionsTooltip() putBoolean true");
        }
        Prefs.putBoolean(this.mContext, Prefs.Key.HAS_SEEN_ODI_CAPTIONS_TOOLTIP, true);
        this.mHasSeenODICaptionsTooltip = true;
        CaptionsToggleImageButton captionsToggleImageButton = this.mODICaptionsIcon;
        if (captionsToggleImageButton != null) {
            captionsToggleImageButton.postOnAnimation(getSinglePressFor(captionsToggleImageButton));
        }
    }

    private void hideCaptionsTooltip() {
        View view = this.mODICaptionsTooltipView;
        if (view != null && view.getVisibility() == 0) {
            this.mODICaptionsTooltipView.animate().cancel();
            this.mODICaptionsTooltipView.setAlpha(1.0f);
            this.mODICaptionsTooltipView.animate().alpha(0.0f).setStartDelay(0L).setDuration(250L).withEndAction(new Runnable() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$eJIc7NaYfyZjv9kbw4RrRBwcYRI
                @Override // java.lang.Runnable
                public final void run() {
                    VolumeDialogImpl.this.lambda$hideCaptionsTooltip$9$VolumeDialogImpl();
                }
            }).start();
        }
    }

    public /* synthetic */ void lambda$hideCaptionsTooltip$9$VolumeDialogImpl() {
        this.mODICaptionsTooltipView.setVisibility(4);
    }

    protected void tryToRemoveCaptionsTooltip() {
        if (this.mHasSeenODICaptionsTooltip && this.mODICaptionsTooltipView != null) {
            ViewGroup container = (ViewGroup) this.mDialog.findViewById(R.id.volume_dialog_container);
            container.removeView(this.mODICaptionsTooltipView);
            this.mODICaptionsTooltipView = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateODICaptionsH(boolean isServiceComponentEnabled, boolean fromTooltip) {
        ViewGroup viewGroup = this.mODICaptionsView;
        if (viewGroup != null) {
            viewGroup.setVisibility(isServiceComponentEnabled ? 0 : 8);
        }
        if (isServiceComponentEnabled) {
            updateCaptionsIcon();
            if (fromTooltip) {
                showCaptionsTooltip();
            }
        }
    }

    private void updateCaptionsIcon() {
        boolean captionsEnabled = this.mController.areCaptionsEnabled();
        if (this.mODICaptionsIcon.getCaptionsEnabled() != captionsEnabled) {
            this.mHandler.post(this.mODICaptionsIcon.setCaptionsEnabled(captionsEnabled));
        }
        final boolean isOptedOut = this.mController.isCaptionStreamOptedOut();
        if (this.mODICaptionsIcon.getOptedOut() != isOptedOut) {
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$lHJr2h1jrFiBPAxP01FnOgolTSg
                @Override // java.lang.Runnable
                public final void run() {
                    VolumeDialogImpl.this.lambda$updateCaptionsIcon$10$VolumeDialogImpl(isOptedOut);
                }
            });
        }
    }

    public /* synthetic */ void lambda$updateCaptionsIcon$10$VolumeDialogImpl(boolean isOptedOut) {
        this.mODICaptionsIcon.setOptedOut(isOptedOut);
    }

    private void onCaptionIconClicked() {
        boolean isEnabled = this.mController.areCaptionsEnabled();
        this.mController.setCaptionsEnabled(!isEnabled);
        updateCaptionsIcon();
    }

    private void incrementManualToggleCount() {
        ContentResolver cr = this.mContext.getContentResolver();
        int ringerCount = Settings.Secure.getInt(cr, "manual_ringer_toggle_count", 0);
        Settings.Secure.putInt(cr, "manual_ringer_toggle_count", ringerCount + 1);
    }

    private void provideTouchFeedbackH(int newRingerMode) {
        VibrationEffect effect = null;
        if (newRingerMode == 0) {
            effect = VibrationEffect.get(0);
        } else if (newRingerMode == 2) {
            this.mController.scheduleTouchFeedback();
        } else {
            effect = VibrationEffect.get(1);
        }
        if (effect != null) {
            this.mController.vibrate(effect);
        }
    }

    private void maybeShowToastH(int newRingerMode) {
        int seenToastCount = Prefs.getInt(this.mContext, Prefs.Key.SEEN_RINGER_GUIDANCE_COUNT, 0);
        if (seenToastCount > 12) {
            return;
        }
        CharSequence toastText = null;
        if (newRingerMode == 0) {
            toastText = this.mContext.getString(17041212);
        } else if (newRingerMode == 2) {
            VolumeDialogController.StreamState ss = this.mState.states.get(2);
            if (ss != null) {
                toastText = this.mContext.getString(R.string.volume_dialog_ringer_guidance_ring, Utils.formatPercentage(ss.level, ss.levelMax));
            }
        } else {
            toastText = this.mContext.getString(17041213);
        }
        Toast.makeText(this.mContext, toastText, 0).show();
        Prefs.putInt(this.mContext, Prefs.Key.SEEN_RINGER_GUIDANCE_COUNT, seenToastCount + 1);
    }

    public void show(int reason) {
        this.mHandler.obtainMessage(1, reason, 0).sendToTarget();
    }

    public void dismiss(int reason) {
        this.mHandler.obtainMessage(2, reason, 0).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showH(int reason) {
        if (D.BUG) {
            String str = TAG;
            Log.d(str, "showH r=" + Events.SHOW_REASONS[reason]);
        }
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        rescheduleTimeoutH();
        if (this.mConfigChanged) {
            initDialog();
            this.mConfigurableTexts.update();
            this.mConfigChanged = false;
        }
        initSettingsH();
        this.mShowing = true;
        this.mDialog.show();
        Events.writeEvent(this.mContext, 0, Integer.valueOf(reason), Boolean.valueOf(this.mKeyguard.isKeyguardLocked()));
        this.mController.notifyVisible(true);
        this.mController.getCaptionsComponentState(false);
        checkODICaptionsTooltip(false);
    }

    protected void rescheduleTimeoutH() {
        this.mHandler.removeMessages(2);
        int timeout = computeTimeoutH();
        H h = this.mHandler;
        h.sendMessageDelayed(h.obtainMessage(2, 3, 0), timeout);
        if (D.BUG) {
            String str = TAG;
            Log.d(str, "rescheduleTimeout " + timeout + " " + Debug.getCaller());
        }
        this.mController.userActivity();
    }

    private int computeTimeoutH() {
        if (this.mHovering) {
            return this.mAccessibilityMgr.getRecommendedTimeoutMillis(DIALOG_HOVERING_TIMEOUT_MILLIS, 4);
        }
        if (this.mSafetyWarning != null) {
            return this.mAccessibilityMgr.getRecommendedTimeoutMillis(5000, 6);
        }
        if (!this.mHasSeenODICaptionsTooltip && this.mODICaptionsTooltipView != null) {
            return this.mAccessibilityMgr.getRecommendedTimeoutMillis(5000, 6);
        }
        return this.mAccessibilityMgr.getRecommendedTimeoutMillis(3000, 4);
    }

    protected void dismissH(int reason) {
        if (D.BUG) {
            String str = TAG;
            Log.d(str, "mDialog.dismiss() reason: " + Events.DISMISS_REASONS[reason] + " from: " + Debug.getCaller());
        }
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(1);
        this.mDialogView.animate().cancel();
        if (this.mShowing) {
            this.mShowing = false;
            Events.writeEvent(this.mContext, 1, Integer.valueOf(reason));
        }
        this.mDialogView.setTranslationX(0.0f);
        this.mDialogView.setAlpha(1.0f);
        ViewPropertyAnimator animator = this.mDialogView.animate().alpha(0.0f).setDuration(250L).setInterpolator(new SystemUIInterpolators.LogAccelerateInterpolator()).withEndAction(new Runnable() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$DPdXKFGeK-9VznmPgQ7xFJyJSxk
            @Override // java.lang.Runnable
            public final void run() {
                VolumeDialogImpl.this.lambda$dismissH$12$VolumeDialogImpl();
            }
        });
        if (!isLandscape()) {
            animator.translationX(this.mDialogView.getWidth() / 2.0f);
        }
        animator.start();
        checkODICaptionsTooltip(true);
        this.mController.notifyVisible(false);
        synchronized (this.mSafetyWarningLock) {
            if (this.mSafetyWarning != null) {
                if (D.BUG) {
                    Log.d(TAG, "SafetyWarning dismissed");
                }
                this.mSafetyWarning.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$dismissH$12$VolumeDialogImpl() {
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$b6ITsqLv2inrGwKl329FqMV42GA
            @Override // java.lang.Runnable
            public final void run() {
                VolumeDialogImpl.this.lambda$dismissH$11$VolumeDialogImpl();
            }
        }, 50L);
    }

    public /* synthetic */ void lambda$dismissH$11$VolumeDialogImpl() {
        this.mDialog.dismiss();
        tryToRemoveCaptionsTooltip();
    }

    private boolean showActiveStreamOnly() {
        return this.mContext.getPackageManager().hasSystemFeature("android.software.leanback") || this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.television");
    }

    private boolean shouldBeVisibleH(VolumeRow row, VolumeRow activeRow) {
        boolean isActive = row.stream == activeRow.stream;
        if (isActive) {
            return true;
        }
        if (!this.mShowActiveStreamOnly) {
            if (row.stream == 10) {
                return this.mShowA11yStream;
            }
            if (activeRow.stream == 10 && row.stream == this.mPrevActiveStream) {
                return true;
            }
            if (row.defaultStream) {
                return activeRow.stream == 2 || activeRow.stream == 4 || activeRow.stream == 0 || activeRow.stream == 10 || this.mDynamic.get(activeRow.stream);
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRowsH(VolumeRow activeRow) {
        if (D.BUG) {
            Log.d(TAG, "updateRowsH");
        }
        if (!this.mShowing) {
            trimObsoleteH();
        }
        Iterator<VolumeRow> it = this.mRows.iterator();
        while (it.hasNext()) {
            VolumeRow row = it.next();
            boolean isActive = row == activeRow;
            boolean shouldBeVisible = shouldBeVisibleH(row, activeRow);
            Util.setVisOrGone(row.view, shouldBeVisible);
            if (row.view.isShown()) {
                updateVolumeRowTintH(row, isActive);
            }
        }
    }

    protected void updateRingerH() {
        VolumeDialogController.StreamState ss;
        VolumeDialogController.State state = this.mState;
        if (state == null || (ss = state.states.get(2)) == null) {
            return;
        }
        boolean isZenMuted = this.mState.zenMode == 3 || this.mState.zenMode == 2 || (this.mState.zenMode == 1 && this.mState.disallowRinger);
        enableRingerViewsH(!isZenMuted);
        int i = this.mState.ringerModeInternal;
        if (i == 0) {
            this.mRingerIcon.setImageResource(R.drawable.ic_volume_ringer_mute);
            this.mRingerIcon.setTag(2);
            addAccessibilityDescription(this.mRingerIcon, 0, this.mContext.getString(R.string.volume_ringer_hint_unmute));
        } else if (i == 1) {
            this.mRingerIcon.setImageResource(R.drawable.ic_volume_ringer_vibrate);
            addAccessibilityDescription(this.mRingerIcon, 1, this.mContext.getString(R.string.volume_ringer_hint_mute));
            this.mRingerIcon.setTag(3);
        } else {
            boolean muted = (this.mAutomute && ss.level == 0) || ss.muted;
            if (!isZenMuted && muted) {
                this.mRingerIcon.setImageResource(R.drawable.ic_volume_ringer_mute);
                addAccessibilityDescription(this.mRingerIcon, 2, this.mContext.getString(R.string.volume_ringer_hint_unmute));
                this.mRingerIcon.setTag(2);
                return;
            }
            this.mRingerIcon.setImageResource(R.drawable.ic_volume_ringer);
            if (this.mController.hasVibrator()) {
                addAccessibilityDescription(this.mRingerIcon, 2, this.mContext.getString(R.string.volume_ringer_hint_vibrate));
            } else {
                addAccessibilityDescription(this.mRingerIcon, 2, this.mContext.getString(R.string.volume_ringer_hint_mute));
            }
            this.mRingerIcon.setTag(1);
        }
    }

    private void addAccessibilityDescription(View view, int currState, final String hintLabel) {
        int currStateResId;
        if (currState == 0) {
            currStateResId = R.string.volume_ringer_status_silent;
        } else if (currState == 1) {
            currStateResId = R.string.volume_ringer_status_vibrate;
        } else {
            currStateResId = R.string.volume_ringer_status_normal;
        }
        view.setContentDescription(this.mContext.getString(currStateResId));
        view.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.systemui.volume.VolumeDialogImpl.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, hintLabel));
            }
        });
    }

    private void enableVolumeRowViewsH(VolumeRow row, boolean enable) {
        boolean showDndIcon = !enable;
        row.dndIcon.setVisibility(showDndIcon ? 0 : 8);
    }

    private void enableRingerViewsH(boolean enable) {
        ImageButton imageButton = this.mRingerIcon;
        if (imageButton != null) {
            imageButton.setEnabled(enable);
        }
        FrameLayout frameLayout = this.mZenIcon;
        if (frameLayout != null) {
            frameLayout.setVisibility(enable ? 8 : 0);
        }
    }

    private void trimObsoleteH() {
        if (D.BUG) {
            Log.d(TAG, "trimObsoleteH");
        }
        for (int i = this.mRows.size() - 1; i >= 0; i--) {
            VolumeRow row = this.mRows.get(i);
            if (row.ss != null && row.ss.dynamic && !this.mDynamic.get(row.stream)) {
                this.mRows.remove(i);
                this.mDialogRowsView.removeView(row.view);
            }
        }
    }

    protected void onStateChangedH(VolumeDialogController.State state) {
        if (D.BUG) {
            String str = TAG;
            Log.d(str, "onStateChangedH() state: " + state.toString());
        }
        VolumeDialogController.State state2 = this.mState;
        if (state2 != null && state != null && state2.ringerModeInternal != state.ringerModeInternal && state.ringerModeInternal == 1) {
            this.mController.vibrate(VibrationEffect.get(5));
        }
        this.mState = state;
        this.mDynamic.clear();
        for (int i = 0; i < state.states.size(); i++) {
            int stream = state.states.keyAt(i);
            VolumeDialogController.StreamState ss = state.states.valueAt(i);
            if (ss.dynamic) {
                this.mDynamic.put(stream, true);
                if (findRow(stream) == null) {
                    addRow(stream, R.drawable.ic_volume_remote, R.drawable.ic_volume_remote_mute, true, false, true);
                }
            }
        }
        int i2 = this.mActiveStream;
        if (i2 != state.activeStream) {
            this.mPrevActiveStream = this.mActiveStream;
            this.mActiveStream = state.activeStream;
            VolumeRow activeRow = getActiveRow();
            updateRowsH(activeRow);
            if (this.mShowing) {
                rescheduleTimeoutH();
            }
        }
        for (VolumeRow row : this.mRows) {
            updateVolumeRowH(row);
        }
        updateRingerH();
        this.mWindow.setTitle(composeWindowTitle());
    }

    CharSequence composeWindowTitle() {
        return this.mContext.getString(R.string.volume_dialog_title, getStreamLabelH(getActiveRow().ss));
    }

    private void updateVolumeRowH(VolumeRow row) {
        VolumeDialogController.StreamState ss;
        boolean zenMuted;
        int iconRes;
        int i;
        boolean isAlarmStream;
        int vlevel;
        int i2;
        int i3;
        int i4;
        if (D.BUG) {
            String str = TAG;
            Log.i(str, "updateVolumeRowH s=" + row.stream);
        }
        VolumeDialogController.State state = this.mState;
        if (state == null || (ss = state.states.get(row.stream)) == null) {
            return;
        }
        row.ss = ss;
        if (ss.level > 0) {
            row.lastAudibleLevel = ss.level;
        }
        if (ss.level == row.requestedLevel) {
            row.requestedLevel = -1;
        }
        boolean isA11yStream = row.stream == 10;
        boolean isRingStream = row.stream == 2;
        boolean isSystemStream = row.stream == 1;
        boolean isAlarmStream2 = row.stream == 4;
        boolean isMusicStream = row.stream == 3;
        boolean isRingVibrate = isRingStream && this.mState.ringerModeInternal == 1;
        boolean isRingSilent = isRingStream && this.mState.ringerModeInternal == 0;
        boolean isZenPriorityOnly = this.mState.zenMode == 1;
        boolean isZenAlarms = this.mState.zenMode == 3;
        boolean isZenNone = this.mState.zenMode == 2;
        if (isZenAlarms) {
            zenMuted = isRingStream || isSystemStream;
        } else if (isZenNone) {
            zenMuted = isRingStream || isSystemStream || isAlarmStream2 || isMusicStream;
        } else if (isZenPriorityOnly) {
            zenMuted = (isAlarmStream2 && this.mState.disallowAlarms) || (isMusicStream && this.mState.disallowMedia) || ((isRingStream && this.mState.disallowRinger) || (isSystemStream && this.mState.disallowSystem));
        } else {
            zenMuted = false;
        }
        int max = ss.levelMax * 100;
        if (max != row.slider.getMax()) {
            row.slider.setMax(max);
        }
        int min = ss.levelMin * 100;
        if (min != row.slider.getMin()) {
            row.slider.setMin(min);
        }
        Util.setText(row.header, getStreamLabelH(ss));
        row.slider.setContentDescription(row.header.getText());
        this.mConfigurableTexts.add(row.header, ss.name);
        boolean iconEnabled = (this.mAutomute || ss.muteSupported) && !zenMuted;
        row.icon.setEnabled(iconEnabled);
        row.icon.setAlpha(iconEnabled ? 1.0f : 0.5f);
        if (isRingVibrate) {
            iconRes = R.drawable.ic_volume_ringer_vibrate;
        } else {
            iconRes = (isRingSilent || zenMuted) ? row.iconMuteRes : ss.routedToBluetooth ? ss.muted ? R.drawable.ic_volume_media_bt_mute : R.drawable.ic_volume_media_bt : (this.mAutomute && ss.level == 0) ? row.iconMuteRes : ss.muted ? row.iconMuteRes : row.iconRes;
        }
        row.icon.setImageResource(iconRes);
        if (iconRes == R.drawable.ic_volume_ringer_vibrate) {
            i = 3;
        } else if (iconRes == R.drawable.ic_volume_media_bt_mute || iconRes == row.iconMuteRes) {
            i = 2;
        } else if (iconRes == R.drawable.ic_volume_media_bt || iconRes == row.iconRes) {
            i = 1;
        } else {
            i = 0;
        }
        row.iconState = i;
        if (!iconEnabled) {
            isAlarmStream = true;
            row.icon.setContentDescription(getStreamLabelH(ss));
        } else if (isRingStream) {
            if (!isRingVibrate) {
                if (this.mController.hasVibrator()) {
                    ImageButton imageButton = row.icon;
                    Context context = this.mContext;
                    if (this.mShowA11yStream) {
                        i4 = R.string.volume_stream_content_description_vibrate_a11y;
                    } else {
                        i4 = R.string.volume_stream_content_description_vibrate;
                    }
                    imageButton.setContentDescription(context.getString(i4, getStreamLabelH(ss)));
                    isAlarmStream = true;
                } else {
                    ImageButton imageButton2 = row.icon;
                    Context context2 = this.mContext;
                    if (this.mShowA11yStream) {
                        i3 = R.string.volume_stream_content_description_mute_a11y;
                    } else {
                        i3 = R.string.volume_stream_content_description_mute;
                    }
                    imageButton2.setContentDescription(context2.getString(i3, getStreamLabelH(ss)));
                    isAlarmStream = true;
                }
            } else {
                ImageButton imageButton3 = row.icon;
                Context context3 = this.mContext;
                int iconRes2 = R.string.volume_stream_content_description_unmute;
                imageButton3.setContentDescription(context3.getString(iconRes2, getStreamLabelH(ss)));
                isAlarmStream = true;
            }
        } else if (isA11yStream) {
            row.icon.setContentDescription(getStreamLabelH(ss));
            isAlarmStream = true;
        } else if (ss.muted || (this.mAutomute && ss.level == 0)) {
            isAlarmStream = true;
            row.icon.setContentDescription(this.mContext.getString(R.string.volume_stream_content_description_unmute, getStreamLabelH(ss)));
        } else {
            ImageButton imageButton4 = row.icon;
            Context context4 = this.mContext;
            if (this.mShowA11yStream) {
                i2 = R.string.volume_stream_content_description_mute_a11y;
            } else {
                i2 = R.string.volume_stream_content_description_mute;
            }
            imageButton4.setContentDescription(context4.getString(i2, getStreamLabelH(ss)));
            isAlarmStream = true;
        }
        if (!zenMuted) {
            vlevel = 0;
        } else {
            vlevel = 0;
            row.tracking = false;
        }
        enableVolumeRowViewsH(row, !zenMuted ? isAlarmStream : vlevel);
        if (zenMuted) {
            isAlarmStream = vlevel;
        }
        boolean enableSlider = isAlarmStream;
        if (!row.ss.muted || isRingStream || zenMuted) {
            vlevel = row.ss.level;
        }
        updateVolumeRowSliderH(row, enableSlider, vlevel);
    }

    private void updateVolumeRowTintH(VolumeRow row, boolean isActive) {
        ColorStateList tint;
        int alpha;
        if (isActive) {
            row.slider.requestFocus();
        }
        boolean useActiveColoring = isActive && row.slider.isEnabled();
        if (useActiveColoring) {
            tint = Utils.getColorAccent(this.mContext);
        } else {
            tint = Utils.getColorAttr(this.mContext, 16842800);
        }
        if (useActiveColoring) {
            alpha = Color.alpha(tint.getDefaultColor());
        } else {
            alpha = getAlphaAttr(16844115);
        }
        if (tint == row.cachedTint) {
            return;
        }
        row.slider.setProgressTintList(tint);
        row.slider.setThumbTintList(tint);
        row.slider.setProgressBackgroundTintList(tint);
        row.slider.setAlpha(alpha / 255.0f);
        row.icon.setImageTintList(tint);
        row.icon.setImageAlpha(alpha);
        row.cachedTint = tint;
    }

    private void updateVolumeRowSliderH(VolumeRow row, boolean enable, int vlevel) {
        int newProgress;
        row.slider.setEnabled(enable);
        updateVolumeRowTintH(row, row.stream == this.mActiveStream);
        if (row.tracking) {
            return;
        }
        int progress = row.slider.getProgress();
        int level = getImpliedLevel(row.slider, progress);
        boolean rowVisible = row.view.getVisibility() == 0;
        boolean inGracePeriod = SystemClock.uptimeMillis() - row.userAttempt < 1000;
        this.mHandler.removeMessages(3, row);
        if (this.mShowing && rowVisible && inGracePeriod) {
            if (D.BUG) {
                Log.d(TAG, "inGracePeriod");
            }
            H h = this.mHandler;
            h.sendMessageAtTime(h.obtainMessage(3, row), row.userAttempt + 1000);
        } else if ((vlevel != level || !this.mShowing || !rowVisible) && progress != (newProgress = vlevel * 100)) {
            if (!this.mShowing || !rowVisible) {
                if (row.anim != null) {
                    row.anim.cancel();
                }
                row.slider.setProgress(newProgress, true);
            } else if (row.anim != null && row.anim.isRunning() && row.animTargetProgress == newProgress) {
            } else {
                if (row.anim == null) {
                    row.anim = ObjectAnimator.ofInt(row.slider, "progress", progress, newProgress);
                    row.anim.setInterpolator(new DecelerateInterpolator());
                } else {
                    row.anim.cancel();
                    row.anim.setIntValues(progress, newProgress);
                }
                row.animTargetProgress = newProgress;
                row.anim.setDuration(80L);
                row.anim.start();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recheckH(VolumeRow row) {
        if (row == null) {
            if (D.BUG) {
                Log.d(TAG, "recheckH ALL");
            }
            trimObsoleteH();
            for (VolumeRow r : this.mRows) {
                updateVolumeRowH(r);
            }
            return;
        }
        if (D.BUG) {
            String str = TAG;
            Log.d(str, "recheckH " + row.stream);
        }
        updateVolumeRowH(row);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setStreamImportantH(int stream, boolean important) {
        for (VolumeRow row : this.mRows) {
            if (row.stream == stream) {
                row.important = important;
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSafetyWarningH(int flags) {
        if ((flags & 1025) != 0 || this.mShowing) {
            synchronized (this.mSafetyWarningLock) {
                if (this.mSafetyWarning != null) {
                    return;
                }
                this.mSafetyWarning = new SafetyWarningDialog(this.mContext, this.mController.getAudioManager()) { // from class: com.android.systemui.volume.VolumeDialogImpl.2
                    @Override // com.android.systemui.volume.SafetyWarningDialog
                    protected void cleanUp() {
                        synchronized (VolumeDialogImpl.this.mSafetyWarningLock) {
                            VolumeDialogImpl.this.mSafetyWarning = null;
                        }
                        VolumeDialogImpl.this.recheckH(null);
                    }
                };
                this.mSafetyWarning.show();
                recheckH(null);
            }
        }
        rescheduleTimeoutH();
    }

    private String getStreamLabelH(VolumeDialogController.StreamState ss) {
        if (ss == null) {
            return "";
        }
        if (ss.remoteLabel != null) {
            return ss.remoteLabel;
        }
        try {
            return this.mContext.getResources().getString(ss.name);
        } catch (Resources.NotFoundException e) {
            String str = TAG;
            Slog.e(str, "Can't find translation for stream " + ss);
            return "";
        }
    }

    private Runnable getSinglePressFor(final ImageButton button) {
        return new Runnable() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$EL--xLq17J-BDlmCmJk3kWI-8E8
            @Override // java.lang.Runnable
            public final void run() {
                VolumeDialogImpl.this.lambda$getSinglePressFor$13$VolumeDialogImpl(button);
            }
        };
    }

    public /* synthetic */ void lambda$getSinglePressFor$13$VolumeDialogImpl(ImageButton button) {
        if (button != null) {
            button.setPressed(true);
            button.postOnAnimationDelayed(getSingleUnpressFor(button), 200L);
        }
    }

    private Runnable getSingleUnpressFor(final ImageButton button) {
        return new Runnable() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogImpl$A9JxlbuHI6pR-_4OJL5e0cwBcPs
            @Override // java.lang.Runnable
            public final void run() {
                VolumeDialogImpl.lambda$getSingleUnpressFor$14(button);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getSingleUnpressFor$14(ImageButton button) {
        if (button != null) {
            button.setPressed(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class H extends Handler {
        private static final int DISMISS = 2;
        private static final int RECHECK = 3;
        private static final int RECHECK_ALL = 4;
        private static final int RESCHEDULE_TIMEOUT = 6;
        private static final int SET_STREAM_IMPORTANT = 5;
        private static final int SHOW = 1;
        private static final int STATE_CHANGED = 7;

        public H() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    VolumeDialogImpl.this.showH(msg.arg1);
                    return;
                case 2:
                    VolumeDialogImpl.this.dismissH(msg.arg1);
                    return;
                case 3:
                    VolumeDialogImpl.this.recheckH((VolumeRow) msg.obj);
                    return;
                case 4:
                    VolumeDialogImpl.this.recheckH(null);
                    return;
                case 5:
                    VolumeDialogImpl.this.setStreamImportantH(msg.arg1, msg.arg2 != 0);
                    return;
                case 6:
                    VolumeDialogImpl.this.rescheduleTimeoutH();
                    return;
                case 7:
                    VolumeDialogImpl volumeDialogImpl = VolumeDialogImpl.this;
                    volumeDialogImpl.onStateChangedH(volumeDialogImpl.mState);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class CustomDialog extends Dialog implements DialogInterface {
        public CustomDialog(Context context) {
            super(context, R.style.qs_theme);
        }

        @Override // android.app.Dialog, android.view.Window.Callback
        public boolean dispatchTouchEvent(MotionEvent ev) {
            VolumeDialogImpl.this.rescheduleTimeoutH();
            return super.dispatchTouchEvent(ev);
        }

        @Override // android.app.Dialog
        protected void onStart() {
            super.setCanceledOnTouchOutside(true);
            super.onStart();
        }

        @Override // android.app.Dialog
        protected void onStop() {
            super.onStop();
            VolumeDialogImpl.this.mHandler.sendEmptyMessage(4);
        }

        @Override // android.app.Dialog
        public boolean onTouchEvent(MotionEvent event) {
            if (VolumeDialogImpl.this.mShowing && event.getAction() == 4) {
                VolumeDialogImpl.this.dismissH(1);
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class VolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        private final VolumeRow mRow;

        private VolumeSeekBarChangeListener(VolumeRow row) {
            this.mRow = row;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int minProgress;
            if (this.mRow.ss == null) {
                return;
            }
            if (D.BUG) {
                String str = VolumeDialogImpl.TAG;
                Log.d(str, AudioSystem.streamToString(this.mRow.stream) + " onProgressChanged " + progress + " fromUser=" + fromUser);
            }
            if (fromUser) {
                if (this.mRow.ss.levelMin > 0 && progress < (minProgress = this.mRow.ss.levelMin * 100)) {
                    seekBar.setProgress(minProgress);
                    progress = minProgress;
                }
                int userLevel = VolumeDialogImpl.getImpliedLevel(seekBar, progress);
                if (this.mRow.ss.level == userLevel && (!this.mRow.ss.muted || userLevel <= 0)) {
                    return;
                }
                this.mRow.userAttempt = SystemClock.uptimeMillis();
                if (this.mRow.requestedLevel == userLevel) {
                    return;
                }
                VolumeDialogImpl.this.mController.setActiveStream(this.mRow.stream);
                VolumeDialogImpl.this.mController.setStreamVolume(this.mRow.stream, userLevel);
                this.mRow.requestedLevel = userLevel;
                Events.writeEvent(VolumeDialogImpl.this.mContext, 9, Integer.valueOf(this.mRow.stream), Integer.valueOf(userLevel));
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (D.BUG) {
                String str = VolumeDialogImpl.TAG;
                Log.d(str, "onStartTrackingTouch " + this.mRow.stream);
            }
            VolumeDialogImpl.this.mController.setActiveStream(this.mRow.stream);
            this.mRow.tracking = true;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (D.BUG) {
                String str = VolumeDialogImpl.TAG;
                Log.d(str, "onStopTrackingTouch " + this.mRow.stream);
            }
            this.mRow.tracking = false;
            this.mRow.userAttempt = SystemClock.uptimeMillis();
            int userLevel = VolumeDialogImpl.getImpliedLevel(seekBar, seekBar.getProgress());
            Events.writeEvent(VolumeDialogImpl.this.mContext, 16, Integer.valueOf(this.mRow.stream), Integer.valueOf(userLevel));
            if (this.mRow.ss.level != userLevel) {
                VolumeDialogImpl.this.mHandler.sendMessageDelayed(VolumeDialogImpl.this.mHandler.obtainMessage(3, this.mRow), 1000L);
            }
        }
    }

    /* loaded from: classes21.dex */
    private final class Accessibility extends View.AccessibilityDelegate {
        private Accessibility() {
        }

        public void init() {
            VolumeDialogImpl.this.mDialogView.setAccessibilityDelegate(this);
        }

        @Override // android.view.View.AccessibilityDelegate
        public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            event.getText().add(VolumeDialogImpl.this.composeWindowTitle());
            return true;
        }

        @Override // android.view.View.AccessibilityDelegate
        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            VolumeDialogImpl.this.rescheduleTimeoutH();
            return super.onRequestSendAccessibilityEvent(host, child, event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class VolumeRow {
        private ObjectAnimator anim;
        private int animTargetProgress;
        private ColorStateList cachedTint;
        private boolean defaultStream;
        private FrameLayout dndIcon;
        private TextView header;
        private ImageButton icon;
        private int iconMuteRes;
        private int iconRes;
        private int iconState;
        private boolean important;
        private int lastAudibleLevel;
        private int requestedLevel;
        private SeekBar slider;
        private VolumeDialogController.StreamState ss;
        private int stream;
        private boolean tracking;
        private long userAttempt;
        private View view;

        private VolumeRow() {
            this.requestedLevel = -1;
            this.lastAudibleLevel = 1;
        }
    }
}
