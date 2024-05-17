package com.android.systemui.qs.tiles;

import android.content.Intent;
import android.widget.Switch;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.SecureSetting;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class ColorInversionTile extends QSTileImpl<QSTile.BooleanState> {
    private final QSTile.Icon mIcon;
    private boolean mListening;
    private final SecureSetting mSetting;

    @Inject
    public ColorInversionTile(QSHost host) {
        super(host);
        this.mIcon = QSTileImpl.ResourceIcon.get(R.drawable.ic_invert_colors);
        this.mSetting = new SecureSetting(this.mContext, this.mHandler, "accessibility_display_inversion_enabled") { // from class: com.android.systemui.qs.tiles.ColorInversionTile.1
            @Override // com.android.systemui.qs.SecureSetting
            protected void handleValueChanged(int value, boolean observedChange) {
                ColorInversionTile.this.handleRefreshState(Integer.valueOf(value));
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleDestroy() {
        super.handleDestroy();
        this.mSetting.setListening(false);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.BooleanState newTileState() {
        return new QSTile.BooleanState();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
        this.mSetting.setListening(listening);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUserSwitch(int newUserId) {
        this.mSetting.setUserId(newUserId);
        handleRefreshState(Integer.valueOf(this.mSetting.getValue()));
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        return new Intent("android.settings.ACCESSIBILITY_SETTINGS");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        this.mSetting.setValue(!((QSTile.BooleanState) this.mState).value ? 1 : 0);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.quick_settings_inversion_label);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        int value = arg instanceof Integer ? ((Integer) arg).intValue() : this.mSetting.getValue();
        boolean enabled = value != 0;
        if (state.slash == null) {
            state.slash = new QSTile.SlashState();
        }
        state.value = enabled;
        state.slash.isSlashed = !state.value;
        state.state = state.value ? 2 : 1;
        state.label = this.mContext.getString(R.string.quick_settings_inversion_label);
        state.icon = this.mIcon;
        state.expandedAccessibilityClassName = Switch.class.getName();
        state.contentDescription = state.label;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 116;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R.string.accessibility_quick_settings_color_inversion_changed_on);
        }
        return this.mContext.getString(R.string.accessibility_quick_settings_color_inversion_changed_off);
    }
}
