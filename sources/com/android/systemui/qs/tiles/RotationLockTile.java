package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.widget.Switch;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.policy.RotationLockController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class RotationLockTile extends QSTileImpl<QSTile.BooleanState> {
    private final RotationLockController.RotationLockControllerCallback mCallback;
    private final RotationLockController mController;
    private final QSTile.Icon mIcon;

    @Inject
    public RotationLockTile(QSHost host, RotationLockController rotationLockController) {
        super(host);
        this.mIcon = QSTileImpl.ResourceIcon.get(17302784);
        this.mCallback = new RotationLockController.RotationLockControllerCallback() { // from class: com.android.systemui.qs.tiles.RotationLockTile.1
            @Override // com.android.systemui.statusbar.policy.RotationLockController.RotationLockControllerCallback
            public void onRotationLockStateChanged(boolean rotationLocked, boolean affordanceVisible) {
                RotationLockTile.this.refreshState(Boolean.valueOf(rotationLocked));
            }
        };
        this.mController = rotationLockController;
        this.mController.observe((LifecycleOwner) this, (RotationLockTile) this.mCallback);
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
        return new Intent("android.settings.DISPLAY_SETTINGS");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        boolean newState = !((QSTile.BooleanState) this.mState).value;
        this.mController.setRotationLocked(newState ? false : true);
        refreshState(Boolean.valueOf(newState));
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return getState().label;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        boolean rotationLocked = this.mController.isRotationLocked();
        state.value = !rotationLocked;
        state.label = this.mContext.getString(R.string.quick_settings_rotation_unlocked_label);
        state.icon = this.mIcon;
        state.contentDescription = getAccessibilityString(rotationLocked);
        state.expandedAccessibilityClassName = Switch.class.getName();
        state.state = state.value ? 2 : 1;
    }

    public static boolean isCurrentOrientationLockPortrait(RotationLockController controller, Context context) {
        int lockOrientation = controller.getRotationLockOrientation();
        return lockOrientation == 0 ? context.getResources().getConfiguration().orientation != 2 : lockOrientation != 2;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 123;
    }

    private String getAccessibilityString(boolean locked) {
        return this.mContext.getString(R.string.accessibility_quick_settings_rotation);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected String composeChangeAnnouncement() {
        return getAccessibilityString(((QSTile.BooleanState) this.mState).value);
    }
}
