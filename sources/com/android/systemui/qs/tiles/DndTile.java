package com.android.systemui.qs.tiles;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.notification.EnableZenModeDialog;
import com.android.systemui.Prefs;
import com.android.systemui.R;
import com.android.systemui.SysUIToast;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.volume.ZenModePanel;
import com.xiaopeng.systemui.controller.CarController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class DndTile extends QSTileImpl<QSTile.BooleanState> {
    private static final String ACTION_SET_VISIBLE = "com.android.systemui.dndtile.SET_VISIBLE";
    private static final String EXTRA_VISIBLE = "visible";
    private final ActivityStarter mActivityStarter;
    private final ZenModeController mController;
    private final DndDetailAdapter mDetailAdapter;
    private boolean mListening;
    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefListener;
    private final BroadcastReceiver mReceiver;
    private boolean mReceiverRegistered;
    private boolean mShowingDetail;
    private final ZenModeController.Callback mZenCallback;
    private final ZenModePanel.Callback mZenModePanelCallback;
    private static final Intent ZEN_SETTINGS = new Intent("android.settings.ZEN_MODE_SETTINGS");
    private static final Intent ZEN_PRIORITY_SETTINGS = new Intent("android.settings.ZEN_MODE_PRIORITY_SETTINGS");

    @Inject
    public DndTile(QSHost host, ZenModeController zenModeController, ActivityStarter activityStarter) {
        super(host);
        this.mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.android.systemui.qs.tiles.DndTile.2
            @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (Prefs.Key.DND_TILE_COMBINED_ICON.equals(key) || Prefs.Key.DND_TILE_VISIBLE.equals(key)) {
                    DndTile.this.refreshState();
                }
            }
        };
        this.mZenCallback = new ZenModeController.Callback() { // from class: com.android.systemui.qs.tiles.DndTile.3
            @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
            public void onZenChanged(int zen) {
                DndTile.this.refreshState(Integer.valueOf(zen));
                if (!DndTile.this.isShowingDetail()) {
                    return;
                }
                DndTile.this.mDetailAdapter.updatePanel();
            }

            @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
            public void onConfigChanged(ZenModeConfig config) {
                if (!DndTile.this.isShowingDetail()) {
                    return;
                }
                DndTile.this.mDetailAdapter.updatePanel();
            }
        };
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.qs.tiles.DndTile.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                boolean visible = intent.getBooleanExtra("visible", false);
                DndTile.setVisible(DndTile.this.mContext, visible);
                DndTile.this.refreshState();
            }
        };
        this.mZenModePanelCallback = new ZenModePanel.Callback() { // from class: com.android.systemui.qs.tiles.DndTile.5
            @Override // com.android.systemui.volume.ZenModePanel.Callback
            public void onPrioritySettings() {
                DndTile.this.mActivityStarter.postStartActivityDismissingKeyguard(DndTile.ZEN_PRIORITY_SETTINGS, 0);
            }

            @Override // com.android.systemui.volume.ZenModePanel.Callback
            public void onInteraction() {
            }

            @Override // com.android.systemui.volume.ZenModePanel.Callback
            public void onExpanded(boolean expanded) {
            }
        };
        this.mController = zenModeController;
        this.mActivityStarter = activityStarter;
        this.mDetailAdapter = new DndDetailAdapter();
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter(ACTION_SET_VISIBLE));
        this.mReceiverRegistered = true;
        this.mController.observe(getLifecycle(), (Lifecycle) this.mZenCallback);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleDestroy() {
        super.handleDestroy();
        if (this.mReceiverRegistered) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mReceiverRegistered = false;
        }
    }

    public static void setVisible(Context context, boolean visible) {
        Prefs.putBoolean(context, Prefs.Key.DND_TILE_VISIBLE, visible);
    }

    public static boolean isVisible(Context context) {
        return Prefs.getBoolean(context, Prefs.Key.DND_TILE_VISIBLE, false);
    }

    public static void setCombinedIcon(Context context, boolean combined) {
        Prefs.putBoolean(context, Prefs.Key.DND_TILE_COMBINED_ICON, combined);
    }

    public static boolean isCombinedIcon(Context context) {
        return Prefs.getBoolean(context, Prefs.Key.DND_TILE_COMBINED_ICON, false);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return ZEN_SETTINGS;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        if (((QSTile.BooleanState) this.mState).value) {
            this.mController.setZen(0, null, this.TAG);
        } else {
            showDetail(true);
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void showDetail(boolean show) {
        int zenDuration = Settings.Secure.getInt(this.mContext.getContentResolver(), "zen_duration", 0);
        boolean showOnboarding = (Settings.Secure.getInt(this.mContext.getContentResolver(), "show_zen_upgrade_notification", 0) == 0 || Settings.Secure.getInt(this.mContext.getContentResolver(), "zen_settings_updated", 0) == 1) ? false : true;
        if (showOnboarding) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "show_zen_upgrade_notification", 0);
            this.mController.setZen(1, null, this.TAG);
            Intent intent = new Intent("android.settings.ZEN_MODE_ONBOARDING");
            intent.addFlags(268468224);
            this.mActivityStarter.postStartActivityDismissingKeyguard(intent, 0);
        } else if (zenDuration == -1) {
            this.mUiHandler.post(new Runnable() { // from class: com.android.systemui.qs.tiles.-$$Lambda$DndTile$fMf3Tdb9veQ9DG26bABcK78yOSM
                @Override // java.lang.Runnable
                public final void run() {
                    DndTile.this.lambda$showDetail$1$DndTile();
                }
            });
        } else if (zenDuration == 0) {
            this.mController.setZen(1, null, this.TAG);
        } else {
            Uri conditionId = ZenModeConfig.toTimeCondition(this.mContext, zenDuration, ActivityManager.getCurrentUser(), true).id;
            this.mController.setZen(1, conditionId, this.TAG);
        }
    }

    public /* synthetic */ void lambda$showDetail$1$DndTile() {
        final Dialog mDialog = new EnableZenModeDialog(this.mContext).createDialog();
        mDialog.getWindow().setType(CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE);
        SystemUIDialog.setShowForAllUsers(mDialog, true);
        SystemUIDialog.registerDismissListener(mDialog);
        SystemUIDialog.setWindowOnTop(mDialog);
        this.mUiHandler.post(new Runnable() { // from class: com.android.systemui.qs.tiles.-$$Lambda$DndTile$h-lFpQiq6o9qG86m-y4CxMaeI_o
            @Override // java.lang.Runnable
            public final void run() {
                mDialog.show();
            }
        });
        this.mHost.collapsePanels();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleSecondaryClick() {
        if (this.mController.isVolumeRestricted()) {
            this.mHost.collapsePanels();
            SysUIToast.makeText(this.mContext, this.mContext.getString(17039911), 1).show();
        } else if (!((QSTile.BooleanState) this.mState).value) {
            this.mController.addCallback(new ZenModeController.Callback() { // from class: com.android.systemui.qs.tiles.DndTile.1
                @Override // com.android.systemui.statusbar.policy.ZenModeController.Callback
                public void onZenChanged(int zen) {
                    DndTile.this.mController.removeCallback(this);
                    DndTile.this.showDetail(true);
                }
            });
            this.mController.setZen(1, null, this.TAG);
        } else {
            showDetail(true);
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.quick_settings_dnd_label);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        ZenModeController zenModeController = this.mController;
        if (zenModeController == null) {
            return;
        }
        int zen = arg instanceof Integer ? ((Integer) arg).intValue() : zenModeController.getZen();
        boolean newValue = zen != 0;
        boolean valueChanged = state.value != newValue;
        if (state.slash == null) {
            state.slash = new QSTile.SlashState();
        }
        state.dualTarget = true;
        state.value = newValue;
        state.state = state.value ? 2 : 1;
        state.slash.isSlashed = !state.value;
        state.label = getTileLabel();
        state.secondaryLabel = TextUtils.emptyIfNull(ZenModeConfig.getDescription(this.mContext, zen != 0, this.mController.getConfig(), false));
        state.icon = QSTileImpl.ResourceIcon.get(17302787);
        checkIfRestrictionEnforcedByAdminOnly(state, "no_adjust_volume");
        if (zen == 1) {
            state.contentDescription = this.mContext.getString(R.string.accessibility_quick_settings_dnd) + ", " + ((Object) state.secondaryLabel);
        } else if (zen == 2) {
            state.contentDescription = this.mContext.getString(R.string.accessibility_quick_settings_dnd) + ", " + this.mContext.getString(R.string.accessibility_quick_settings_dnd_none_on) + ", " + ((Object) state.secondaryLabel);
        } else if (zen == 3) {
            state.contentDescription = this.mContext.getString(R.string.accessibility_quick_settings_dnd) + ", " + this.mContext.getString(R.string.accessibility_quick_settings_dnd_alarms_on) + ", " + ((Object) state.secondaryLabel);
        } else {
            state.contentDescription = this.mContext.getString(R.string.accessibility_quick_settings_dnd);
        }
        if (valueChanged) {
            fireToggleStateChanged(state.value);
        }
        state.dualLabelContentDescription = this.mContext.getResources().getString(R.string.accessibility_quick_settings_open_settings, getTileLabel());
        state.expandedAccessibilityClassName = Switch.class.getName();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 118;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R.string.accessibility_quick_settings_dnd_changed_on);
        }
        return this.mContext.getString(R.string.accessibility_quick_settings_dnd_changed_off);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
        if (this.mListening == listening) {
            return;
        }
        this.mListening = listening;
        if (this.mListening) {
            Prefs.registerListener(this.mContext, this.mPrefListener);
        } else {
            Prefs.unregisterListener(this.mContext, this.mPrefListener);
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public boolean isAvailable() {
        return isVisible(this.mContext);
    }

    /* loaded from: classes21.dex */
    private final class DndDetailAdapter implements DetailAdapter, View.OnAttachStateChangeListener {
        private boolean mAuto;
        private ZenModePanel mZenPanel;

        private DndDetailAdapter() {
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public CharSequence getTitle() {
            return DndTile.this.mContext.getString(R.string.quick_settings_dnd_label);
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public Boolean getToggleState() {
            return Boolean.valueOf(((QSTile.BooleanState) DndTile.this.mState).value);
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public Intent getSettingsIntent() {
            return DndTile.ZEN_SETTINGS;
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public void setToggleState(boolean state) {
            MetricsLogger.action(DndTile.this.mContext, (int) Opcodes.IF_ACMPNE, state);
            if (!state) {
                DndTile.this.mController.setZen(0, null, DndTile.this.TAG);
                this.mAuto = false;
                return;
            }
            DndTile.this.mController.setZen(1, null, DndTile.this.TAG);
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public int getMetricsCategory() {
            return 149;
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public View createDetailView(Context context, View convertView, ViewGroup parent) {
            this.mZenPanel = convertView != null ? (ZenModePanel) convertView : (ZenModePanel) LayoutInflater.from(context).inflate(R.layout.zen_mode_panel, parent, false);
            if (convertView == null) {
                this.mZenPanel.init(DndTile.this.mController);
                this.mZenPanel.addOnAttachStateChangeListener(this);
                this.mZenPanel.setCallback(DndTile.this.mZenModePanelCallback);
                this.mZenPanel.setEmptyState(R.drawable.ic_qs_dnd_detail_empty, R.string.dnd_is_off);
            }
            updatePanel();
            return this.mZenPanel;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updatePanel() {
            if (this.mZenPanel == null) {
                return;
            }
            this.mAuto = false;
            if (DndTile.this.mController.getZen() != 0) {
                ZenModeConfig config = DndTile.this.mController.getConfig();
                String summary = "";
                if (config.manualRule != null && config.manualRule.enabler != null) {
                    summary = getOwnerCaption(config.manualRule.enabler);
                }
                for (ZenModeConfig.ZenRule automaticRule : config.automaticRules.values()) {
                    if (automaticRule.isAutomaticActive()) {
                        summary = summary.isEmpty() ? DndTile.this.mContext.getString(R.string.qs_dnd_prompt_auto_rule, automaticRule.name) : DndTile.this.mContext.getString(R.string.qs_dnd_prompt_auto_rule_app);
                    }
                }
                if (summary.isEmpty()) {
                    this.mZenPanel.setState(0);
                    return;
                }
                this.mAuto = true;
                this.mZenPanel.setState(1);
                this.mZenPanel.setAutoText(summary);
                return;
            }
            this.mZenPanel.setState(2);
        }

        private String getOwnerCaption(String owner) {
            CharSequence seq;
            PackageManager pm = DndTile.this.mContext.getPackageManager();
            try {
                ApplicationInfo info = pm.getApplicationInfo(owner, 0);
                if (info != null && (seq = info.loadLabel(pm)) != null) {
                    String str = seq.toString().trim();
                    return DndTile.this.mContext.getString(R.string.qs_dnd_prompt_app, str);
                }
                return "";
            } catch (Throwable e) {
                Slog.w(DndTile.this.TAG, "Error loading owner caption", e);
                return "";
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
            DndTile.this.mShowingDetail = true;
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            DndTile.this.mShowingDetail = false;
            this.mZenPanel = null;
        }
    }
}
