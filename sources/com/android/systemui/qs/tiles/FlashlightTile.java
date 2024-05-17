package com.android.systemui.qs.tiles;

import android.app.ActivityManager;
import android.content.Intent;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.policy.FlashlightController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class FlashlightTile extends QSTileImpl<QSTile.BooleanState> implements FlashlightController.FlashlightListener {
    private final FlashlightController mFlashlightController;
    private final QSTile.Icon mIcon;

    @Inject
    public FlashlightTile(QSHost host, FlashlightController flashlightController) {
        super(host);
        this.mIcon = QSTileImpl.ResourceIcon.get(17302788);
        this.mFlashlightController = flashlightController;
        this.mFlashlightController.observe(getLifecycle(), (Lifecycle) this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleDestroy() {
        super.handleDestroy();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.BooleanState newTileState() {
        QSTile.BooleanState state = new QSTile.BooleanState();
        state.handlesLongClick = false;
        return state;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUserSwitch(int newUserId) {
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return new Intent("android.media.action.STILL_IMAGE_CAMERA");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public boolean isAvailable() {
        return this.mFlashlightController.hasFlashlight();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        if (ActivityManager.isUserAMonkey()) {
            return;
        }
        boolean newState = !((QSTile.BooleanState) this.mState).value;
        refreshState(Boolean.valueOf(newState));
        this.mFlashlightController.setFlashlight(newState);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.quick_settings_flashlight_label);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleLongClick() {
        handleClick();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        if (state.slash == null) {
            state.slash = new QSTile.SlashState();
        }
        state.label = this.mHost.getContext().getString(R.string.quick_settings_flashlight_label);
        if (!this.mFlashlightController.isAvailable()) {
            state.icon = this.mIcon;
            state.slash.isSlashed = true;
            state.contentDescription = this.mContext.getString(R.string.accessibility_quick_settings_flashlight_unavailable);
            state.state = 0;
            return;
        }
        if (arg instanceof Boolean) {
            boolean value = ((Boolean) arg).booleanValue();
            if (value == state.value) {
                return;
            }
            state.value = value;
        } else {
            state.value = this.mFlashlightController.isEnabled();
        }
        state.icon = this.mIcon;
        state.slash.isSlashed = !state.value;
        state.contentDescription = this.mContext.getString(R.string.quick_settings_flashlight_label);
        state.expandedAccessibilityClassName = Switch.class.getName();
        state.state = state.value ? 2 : 1;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 119;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R.string.accessibility_quick_settings_flashlight_changed_on);
        }
        return this.mContext.getString(R.string.accessibility_quick_settings_flashlight_changed_off);
    }

    @Override // com.android.systemui.statusbar.policy.FlashlightController.FlashlightListener
    public void onFlashlightChanged(boolean enabled) {
        refreshState(Boolean.valueOf(enabled));
    }

    @Override // com.android.systemui.statusbar.policy.FlashlightController.FlashlightListener
    public void onFlashlightError() {
        refreshState(false);
    }

    @Override // com.android.systemui.statusbar.policy.FlashlightController.FlashlightListener
    public void onFlashlightAvailabilityChanged(boolean available) {
        refreshState();
    }
}
