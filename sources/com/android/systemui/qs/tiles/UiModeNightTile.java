package com.android.systemui.qs.tiles;

import android.app.UiModeManager;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class UiModeNightTile extends QSTileImpl<QSTile.BooleanState> implements ConfigurationController.ConfigurationListener, BatteryController.BatteryStateChangeCallback {
    private final BatteryController mBatteryController;
    private final QSTile.Icon mIcon;
    private final UiModeManager mUiModeManager;

    @Inject
    public UiModeNightTile(QSHost host, ConfigurationController configurationController, BatteryController batteryController) {
        super(host);
        this.mIcon = QSTileImpl.ResourceIcon.get(17302790);
        this.mBatteryController = batteryController;
        this.mUiModeManager = (UiModeManager) this.mContext.getSystemService(UiModeManager.class);
        configurationController.observe(getLifecycle(), (Lifecycle) this);
        batteryController.observe(getLifecycle(), (Lifecycle) this);
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onUiModeChanged() {
        refreshState();
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback
    public void onPowerSaveChanged(boolean isPowerSave) {
        refreshState();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        if (getState().state == 0) {
            return;
        }
        boolean newState = !((QSTile.BooleanState) this.mState).value;
        this.mUiModeManager.setNightModeActivated(newState);
        refreshState(Boolean.valueOf(newState));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        int i;
        int uiMode = this.mUiModeManager.getNightMode();
        boolean powerSave = this.mBatteryController.isPowerSave();
        boolean isAuto = uiMode == 0;
        boolean nightMode = (this.mContext.getResources().getConfiguration().uiMode & 48) == 32;
        if (powerSave) {
            state.secondaryLabel = this.mContext.getResources().getString(R.string.quick_settings_dark_mode_secondary_label_battery_saver);
        } else if (isAuto) {
            Resources resources = this.mContext.getResources();
            if (nightMode) {
                i = R.string.quick_settings_dark_mode_secondary_label_until_sunrise;
            } else {
                i = R.string.quick_settings_dark_mode_secondary_label_on_at_sunset;
            }
            state.secondaryLabel = resources.getString(i);
        } else {
            state.secondaryLabel = null;
        }
        state.value = nightMode;
        state.label = this.mContext.getString(R.string.quick_settings_ui_mode_night_label);
        state.icon = this.mIcon;
        state.contentDescription = TextUtils.isEmpty(state.secondaryLabel) ? state.label : TextUtils.concat(state.label, ", ", state.secondaryLabel);
        if (powerSave) {
            state.state = 0;
        } else {
            state.state = state.value ? 2 : 1;
        }
        state.showRippleEffect = false;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 1706;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return new Intent("android.settings.DARK_THEME_SETTINGS");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleSetListening(boolean listening) {
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return getState().label;
    }
}
