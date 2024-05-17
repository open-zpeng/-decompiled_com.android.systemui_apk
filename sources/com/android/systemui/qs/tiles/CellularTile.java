package com.android.systemui.qs.tiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.telephony.SubscriptionManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.net.DataUsageController;
import com.android.systemui.Prefs;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.SignalTileView;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.NetworkController;
import com.xiaopeng.systemui.controller.CarController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class CellularTile extends QSTileImpl<QSTile.SignalState> {
    private static final String ENABLE_SETTINGS_DATA_PLAN = "enable.settings.data.plan";
    private final ActivityStarter mActivityStarter;
    private final NetworkController mController;
    private final DataUsageController mDataController;
    private final CellularDetailAdapter mDetailAdapter;
    private final CellSignalCallback mSignalCallback;

    @Inject
    public CellularTile(QSHost host, NetworkController networkController, ActivityStarter activityStarter) {
        super(host);
        this.mSignalCallback = new CellSignalCallback();
        this.mController = networkController;
        this.mActivityStarter = activityStarter;
        this.mDataController = this.mController.getMobileDataController();
        this.mDetailAdapter = new CellularDetailAdapter();
        this.mController.observe(getLifecycle(), (Lifecycle) this.mSignalCallback);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.SignalState newTileState() {
        return new QSTile.SignalState();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public QSIconView createTileView(Context context) {
        return new SignalTileView(context);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public DetailAdapter getDetailAdapter() {
        return this.mDetailAdapter;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return getCellularSettingIntent();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        if (getState().state == 0) {
            return;
        }
        if (this.mDataController.isMobileDataEnabled()) {
            maybeShowDisableDialog();
        } else {
            this.mDataController.setMobileDataEnabled(true);
        }
    }

    private void maybeShowDisableDialog() {
        if (Prefs.getBoolean(this.mContext, Prefs.Key.QS_HAS_TURNED_OFF_MOBILE_DATA, false)) {
            this.mDataController.setMobileDataEnabled(false);
            return;
        }
        String carrierName = this.mController.getMobileDataNetworkName();
        if (TextUtils.isEmpty(carrierName)) {
            carrierName = this.mContext.getString(R.string.mobile_data_disable_message_default_carrier);
        }
        AlertDialog dialog = new AlertDialog.Builder(this.mContext).setTitle(R.string.mobile_data_disable_title).setMessage(this.mContext.getString(R.string.mobile_data_disable_message, carrierName)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(17039484, new DialogInterface.OnClickListener() { // from class: com.android.systemui.qs.tiles.-$$Lambda$CellularTile$oLJGrvqAwKFs9wNM4MvnfZ_a1QQ
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CellularTile.this.lambda$maybeShowDisableDialog$0$CellularTile(dialogInterface, i);
            }
        }).create();
        dialog.getWindow().setType(CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE);
        SystemUIDialog.setShowForAllUsers(dialog, true);
        SystemUIDialog.registerDismissListener(dialog);
        SystemUIDialog.setWindowOnTop(dialog);
        dialog.show();
    }

    public /* synthetic */ void lambda$maybeShowDisableDialog$0$CellularTile(DialogInterface d, int w) {
        this.mDataController.setMobileDataEnabled(false);
        Prefs.putBoolean(this.mContext, Prefs.Key.QS_HAS_TURNED_OFF_MOBILE_DATA, true);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleSecondaryClick() {
        if (this.mDataController.isMobileDataSupported()) {
            showDetail(true);
        } else {
            this.mActivityStarter.postStartActivityDismissingKeyguard(getCellularSettingIntent(), 0);
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.quick_settings_cellular_detail_title);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.SignalState state, Object arg) {
        CharSequence contentDescriptionSuffix;
        CallbackInfo cb = (CallbackInfo) arg;
        if (cb == null) {
            cb = this.mSignalCallback.mInfo;
        }
        Resources r = this.mContext.getResources();
        state.label = r.getString(R.string.mobile_data);
        boolean mobileDataEnabled = this.mDataController.isMobileDataSupported() && this.mDataController.isMobileDataEnabled();
        state.value = mobileDataEnabled;
        state.activityIn = mobileDataEnabled && cb.activityIn;
        state.activityOut = mobileDataEnabled && cb.activityOut;
        state.expandedAccessibilityClassName = Switch.class.getName();
        if (cb.noSim) {
            state.icon = QSTileImpl.ResourceIcon.get(R.drawable.ic_qs_no_sim);
        } else {
            state.icon = QSTileImpl.ResourceIcon.get(R.drawable.ic_swap_vert);
        }
        if (cb.noSim) {
            state.state = 0;
            state.secondaryLabel = r.getString(R.string.keyguard_missing_sim_message_short);
        } else if (cb.airplaneModeEnabled) {
            state.state = 0;
            state.secondaryLabel = r.getString(R.string.status_bar_airplane);
        } else if (mobileDataEnabled) {
            state.state = 2;
            state.secondaryLabel = appendMobileDataType(cb.multipleSubs ? cb.dataSubscriptionName : "", getMobileDataContentName(cb));
        } else {
            state.state = 1;
            state.secondaryLabel = r.getString(R.string.cell_data_off);
        }
        if (state.state == 1) {
            contentDescriptionSuffix = r.getString(R.string.cell_data_off_content_description);
        } else {
            contentDescriptionSuffix = state.secondaryLabel;
        }
        state.contentDescription = ((Object) state.label) + ", " + ((Object) contentDescriptionSuffix);
    }

    private CharSequence appendMobileDataType(CharSequence current, CharSequence dataType) {
        if (TextUtils.isEmpty(dataType)) {
            return Html.fromHtml(current.toString(), 0);
        }
        if (TextUtils.isEmpty(current)) {
            return Html.fromHtml(dataType.toString(), 0);
        }
        String concat = this.mContext.getString(R.string.mobile_carrier_text_format, current, dataType);
        return Html.fromHtml(concat, 0);
    }

    private CharSequence getMobileDataContentName(CallbackInfo cb) {
        if (cb.roaming && !TextUtils.isEmpty(cb.dataContentDescription)) {
            String roaming = this.mContext.getString(R.string.data_connection_roaming);
            String dataDescription = cb.dataContentDescription.toString();
            return this.mContext.getString(R.string.mobile_data_text_format, roaming, dataDescription);
        } else if (cb.roaming) {
            return this.mContext.getString(R.string.data_connection_roaming);
        } else {
            return cb.dataContentDescription;
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 115;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public boolean isAvailable() {
        return this.mController.hasMobileDataFeature();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static final class CallbackInfo {
        boolean activityIn;
        boolean activityOut;
        boolean airplaneModeEnabled;
        CharSequence dataContentDescription;
        CharSequence dataSubscriptionName;
        boolean multipleSubs;
        boolean noSim;
        boolean roaming;

        private CallbackInfo() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class CellSignalCallback implements NetworkController.SignalCallback {
        private final CallbackInfo mInfo;

        private CellSignalCallback() {
            this.mInfo = new CallbackInfo();
        }

        @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
        public void setMobileDataIndicators(NetworkController.IconState statusIcon, NetworkController.IconState qsIcon, int statusType, int qsType, boolean activityIn, boolean activityOut, CharSequence typeContentDescription, CharSequence typeContentDescriptionHtml, CharSequence description, boolean isWide, int subId, boolean roaming) {
            if (qsIcon != null) {
                this.mInfo.dataSubscriptionName = CellularTile.this.mController.getMobileDataNetworkName();
                this.mInfo.dataContentDescription = description != null ? typeContentDescriptionHtml : null;
                CallbackInfo callbackInfo = this.mInfo;
                callbackInfo.activityIn = activityIn;
                callbackInfo.activityOut = activityOut;
                callbackInfo.roaming = roaming;
                callbackInfo.multipleSubs = CellularTile.this.mController.getNumberSubscriptions() > 1;
                CellularTile.this.refreshState(this.mInfo);
            }
        }

        @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
        public void setNoSims(boolean show, boolean simDetected) {
            CallbackInfo callbackInfo = this.mInfo;
            callbackInfo.noSim = show;
            CellularTile.this.refreshState(callbackInfo);
        }

        @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
        public void setIsAirplaneMode(NetworkController.IconState icon) {
            this.mInfo.airplaneModeEnabled = icon.visible;
            CellularTile.this.refreshState(this.mInfo);
        }

        @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
        public void setMobileDataEnabled(boolean enabled) {
            CellularTile.this.mDetailAdapter.setMobileDataEnabled(enabled);
        }
    }

    static Intent getCellularSettingIntent() {
        Intent intent = new Intent("android.settings.NETWORK_OPERATOR_SETTINGS");
        int dataSub = SubscriptionManager.getDefaultDataSubscriptionId();
        if (dataSub != -1) {
            intent.putExtra("android.provider.extra.SUB_ID", SubscriptionManager.getDefaultDataSubscriptionId());
        }
        return intent;
    }

    /* loaded from: classes21.dex */
    private final class CellularDetailAdapter implements DetailAdapter {
        private CellularDetailAdapter() {
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public CharSequence getTitle() {
            return CellularTile.this.mContext.getString(R.string.quick_settings_cellular_detail_title);
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public Boolean getToggleState() {
            if (CellularTile.this.mDataController.isMobileDataSupported()) {
                return Boolean.valueOf(CellularTile.this.mDataController.isMobileDataEnabled());
            }
            return null;
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public Intent getSettingsIntent() {
            return CellularTile.getCellularSettingIntent();
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public void setToggleState(boolean state) {
            MetricsLogger.action(CellularTile.this.mContext, (int) Cea708CCParser.Const.CODE_C1_DF3, state);
            CellularTile.this.mDataController.setMobileDataEnabled(state);
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public int getMetricsCategory() {
            return 117;
        }

        @Override // com.android.systemui.plugins.qs.DetailAdapter
        public View createDetailView(Context context, View convertView, ViewGroup parent) {
            View inflate;
            if (convertView == null) {
                inflate = LayoutInflater.from(CellularTile.this.mContext).inflate(R.layout.data_usage, parent, false);
            } else {
                inflate = convertView;
            }
            DataUsageDetailView v = (DataUsageDetailView) inflate;
            DataUsageController.DataUsageInfo info = CellularTile.this.mDataController.getDataUsageInfo();
            if (info == null) {
                return v;
            }
            v.bind(info);
            v.findViewById(R.id.roaming_text).setVisibility(CellularTile.this.mSignalCallback.mInfo.roaming ? 0 : 4);
            return v;
        }

        public void setMobileDataEnabled(boolean enabled) {
            CellularTile.this.fireToggleStateChanged(enabled);
        }
    }
}
