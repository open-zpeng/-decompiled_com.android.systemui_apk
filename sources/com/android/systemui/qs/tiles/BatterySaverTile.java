package com.android.systemui.qs.tiles;

import android.content.Intent;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import androidx.mediarouter.media.MediaRouter;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.SecureSetting;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.policy.BatteryController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class BatterySaverTile extends QSTileImpl<QSTile.BooleanState> implements BatteryController.BatteryStateChangeCallback {
    private final BatteryController mBatteryController;
    private boolean mCharging;
    private QSTile.Icon mIcon;
    private int mLevel;
    private boolean mPluggedIn;
    private boolean mPowerSave;
    private final SecureSetting mSetting;

    @Inject
    public BatterySaverTile(QSHost host, BatteryController batteryController) {
        super(host);
        this.mIcon = QSTileImpl.ResourceIcon.get(17302785);
        this.mBatteryController = batteryController;
        this.mBatteryController.observe(getLifecycle(), (Lifecycle) this);
        this.mSetting = new SecureSetting(this.mContext, this.mHandler, "low_power_warning_acknowledged") { // from class: com.android.systemui.qs.tiles.BatterySaverTile.1
            @Override // com.android.systemui.qs.SecureSetting
            protected void handleValueChanged(int value, boolean observedChange) {
                BatterySaverTile.this.handleRefreshState(null);
            }
        };
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleDestroy() {
        super.handleDestroy();
        this.mSetting.setListening(false);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return MediaRouter.GlobalMediaRouter.CallbackHandler.MSG_ROUTE_PRESENTATION_DISPLAY_CHANGED;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
        this.mSetting.setListening(listening);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return new Intent("android.intent.action.POWER_USAGE_SUMMARY");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        if (getState().state == 0) {
            return;
        }
        this.mBatteryController.setPowerSaveMode(!this.mPowerSave);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.battery_detail_switch_title);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        int i;
        if (this.mPluggedIn) {
            i = 0;
        } else {
            i = this.mPowerSave ? 2 : 1;
        }
        state.state = i;
        state.icon = this.mIcon;
        state.label = this.mContext.getString(R.string.battery_detail_switch_title);
        state.contentDescription = state.label;
        state.value = this.mPowerSave;
        state.expandedAccessibilityClassName = Switch.class.getName();
        state.showRippleEffect = this.mSetting.getValue() == 0;
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback
    public void onBatteryLevelChanged(int level, boolean pluggedIn, boolean charging) {
        this.mLevel = level;
        this.mPluggedIn = pluggedIn;
        this.mCharging = charging;
        refreshState(Integer.valueOf(level));
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback
    public void onPowerSaveChanged(boolean isPowerSave) {
        this.mPowerSave = isPowerSave;
        refreshState(null);
    }
}
