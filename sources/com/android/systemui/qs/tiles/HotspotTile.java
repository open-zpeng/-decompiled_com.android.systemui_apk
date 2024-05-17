package com.android.systemui.qs.tiles;

import android.content.ComponentName;
import android.content.Intent;
import android.widget.Switch;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.HotspotController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class HotspotTile extends QSTileImpl<QSTile.BooleanState> {
    private static final Intent TETHER_SETTINGS = new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.TetherSettings"));
    private final HotspotAndDataSaverCallbacks mCallbacks;
    private final DataSaverController mDataSaverController;
    private final QSTile.Icon mEnabledStatic;
    private final HotspotController mHotspotController;
    private boolean mListening;

    @Inject
    public HotspotTile(QSHost host, HotspotController hotspotController, DataSaverController dataSaverController) {
        super(host);
        this.mEnabledStatic = QSTileImpl.ResourceIcon.get(R.drawable.ic_hotspot);
        this.mCallbacks = new HotspotAndDataSaverCallbacks();
        this.mHotspotController = hotspotController;
        this.mDataSaverController = dataSaverController;
        this.mHotspotController.observe((LifecycleOwner) this, (HotspotTile) this.mCallbacks);
        this.mDataSaverController.observe((LifecycleOwner) this, (HotspotTile) this.mCallbacks);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public boolean isAvailable() {
        return this.mHotspotController.isHotspotSupported();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleDestroy() {
        super.handleDestroy();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
        if (this.mListening == listening) {
            return;
        }
        this.mListening = listening;
        if (listening) {
            refreshState();
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return new Intent(TETHER_SETTINGS);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        boolean isEnabled = ((QSTile.BooleanState) this.mState).value;
        if (!isEnabled && this.mDataSaverController.isDataSaverEnabled()) {
            return;
        }
        refreshState(isEnabled ? null : ARG_SHOW_TRANSIENT_ENABLING);
        this.mHotspotController.setHotspotEnabled(!isEnabled);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.quick_settings_hotspot_label);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        int numConnectedDevices;
        boolean isDataSaverEnabled;
        boolean transientEnabling = arg == ARG_SHOW_TRANSIENT_ENABLING;
        if (state.slash == null) {
            state.slash = new QSTile.SlashState();
        }
        boolean isTransient = transientEnabling || this.mHotspotController.isHotspotTransient();
        checkIfRestrictionEnforcedByAdminOnly(state, "no_config_tethering");
        if (arg instanceof CallbackInfo) {
            CallbackInfo info = (CallbackInfo) arg;
            state.value = transientEnabling || info.isHotspotEnabled;
            numConnectedDevices = info.numConnectedDevices;
            isDataSaverEnabled = info.isDataSaverEnabled;
        } else {
            state.value = transientEnabling || this.mHotspotController.isHotspotEnabled();
            numConnectedDevices = this.mHotspotController.getNumConnectedDevices();
            isDataSaverEnabled = this.mDataSaverController.isDataSaverEnabled();
        }
        state.icon = this.mEnabledStatic;
        state.label = this.mContext.getString(R.string.quick_settings_hotspot_label);
        state.isTransient = isTransient;
        state.slash.isSlashed = (state.value || state.isTransient) ? false : true;
        if (state.isTransient) {
            state.icon = QSTileImpl.ResourceIcon.get(17302431);
        }
        state.expandedAccessibilityClassName = Switch.class.getName();
        state.contentDescription = state.label;
        boolean isTileUnavailable = isDataSaverEnabled;
        boolean isTileActive = state.value || state.isTransient;
        if (isTileUnavailable) {
            state.state = 0;
        } else {
            state.state = isTileActive ? 2 : 1;
        }
        state.secondaryLabel = getSecondaryLabel(isTileActive, isTransient, isDataSaverEnabled, numConnectedDevices);
    }

    private String getSecondaryLabel(boolean isActive, boolean isTransient, boolean isDataSaverEnabled, int numConnectedDevices) {
        if (isTransient) {
            return this.mContext.getString(R.string.quick_settings_hotspot_secondary_label_transient);
        }
        if (isDataSaverEnabled) {
            return this.mContext.getString(R.string.quick_settings_hotspot_secondary_label_data_saver_enabled);
        }
        if (numConnectedDevices > 0 && isActive) {
            return this.mContext.getResources().getQuantityString(R.plurals.quick_settings_hotspot_secondary_label_num_devices, numConnectedDevices, Integer.valueOf(numConnectedDevices));
        }
        return null;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 120;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R.string.accessibility_quick_settings_hotspot_changed_on);
        }
        return this.mContext.getString(R.string.accessibility_quick_settings_hotspot_changed_off);
    }

    /* loaded from: classes21.dex */
    private final class HotspotAndDataSaverCallbacks implements HotspotController.Callback, DataSaverController.Listener {
        CallbackInfo mCallbackInfo;

        private HotspotAndDataSaverCallbacks() {
            this.mCallbackInfo = new CallbackInfo();
        }

        @Override // com.android.systemui.statusbar.policy.DataSaverController.Listener
        public void onDataSaverChanged(boolean isDataSaving) {
            CallbackInfo callbackInfo = this.mCallbackInfo;
            callbackInfo.isDataSaverEnabled = isDataSaving;
            HotspotTile.this.refreshState(callbackInfo);
        }

        @Override // com.android.systemui.statusbar.policy.HotspotController.Callback
        public void onHotspotChanged(boolean enabled, int numDevices) {
            CallbackInfo callbackInfo = this.mCallbackInfo;
            callbackInfo.isHotspotEnabled = enabled;
            callbackInfo.numConnectedDevices = numDevices;
            HotspotTile.this.refreshState(callbackInfo);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public static final class CallbackInfo {
        boolean isDataSaverEnabled;
        boolean isHotspotEnabled;
        int numConnectedDevices;

        protected CallbackInfo() {
        }

        public String toString() {
            return "CallbackInfo[isHotspotEnabled=" + this.isHotspotEnabled + ",numConnectedDevices=" + this.numConnectedDevices + ",isDataSaverEnabled=" + this.isDataSaverEnabled + ']';
        }
    }
}
