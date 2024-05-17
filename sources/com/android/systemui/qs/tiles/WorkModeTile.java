package com.android.systemui.qs.tiles;

import android.content.Intent;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class WorkModeTile extends QSTileImpl<QSTile.BooleanState> implements ManagedProfileController.Callback {
    private final QSTile.Icon mIcon;
    private final ManagedProfileController mProfileController;

    @Inject
    public WorkModeTile(QSHost host, ManagedProfileController managedProfileController) {
        super(host);
        this.mIcon = QSTileImpl.ResourceIcon.get(R.drawable.stat_sys_managed_profile_status);
        this.mProfileController = managedProfileController;
        this.mProfileController.observe(getLifecycle(), (Lifecycle) this);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return new Intent("android.settings.MANAGED_PROFILE_SETTINGS");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleClick() {
        this.mProfileController.setWorkModeEnabled(!((QSTile.BooleanState) this.mState).value);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public boolean isAvailable() {
        return this.mProfileController.hasActiveProfile();
    }

    @Override // com.android.systemui.statusbar.phone.ManagedProfileController.Callback
    public void onManagedProfileChanged() {
        refreshState(Boolean.valueOf(this.mProfileController.isWorkModeEnabled()));
    }

    @Override // com.android.systemui.statusbar.phone.ManagedProfileController.Callback
    public void onManagedProfileRemoved() {
        this.mHost.removeTile(getTileSpec());
        this.mHost.unmarkTileAsAutoAdded(getTileSpec());
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.quick_settings_work_mode_label);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        if (!isAvailable()) {
            onManagedProfileRemoved();
        }
        if (state.slash == null) {
            state.slash = new QSTile.SlashState();
        }
        if (arg instanceof Boolean) {
            state.value = ((Boolean) arg).booleanValue();
        } else {
            state.value = this.mProfileController.isWorkModeEnabled();
        }
        state.icon = this.mIcon;
        if (state.value) {
            state.slash.isSlashed = false;
            state.contentDescription = this.mContext.getString(R.string.accessibility_quick_settings_work_mode_on);
        } else {
            state.slash.isSlashed = true;
            state.contentDescription = this.mContext.getString(R.string.accessibility_quick_settings_work_mode_off);
        }
        state.label = this.mContext.getString(R.string.quick_settings_work_mode_label);
        state.expandedAccessibilityClassName = Switch.class.getName();
        state.state = state.value ? 2 : 1;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 257;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R.string.accessibility_quick_settings_work_mode_changed_on);
        }
        return this.mContext.getString(R.string.accessibility_quick_settings_work_mode_changed_off);
    }
}
