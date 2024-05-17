package com.android.systemui.qs.tiles;

import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Switch;
import androidx.lifecycle.Lifecycle;
import com.android.systemui.Prefs;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.NetworkController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class DataSaverTile extends QSTileImpl<QSTile.BooleanState> implements DataSaverController.Listener {
    private final DataSaverController mDataSaverController;

    @Inject
    public DataSaverTile(QSHost host, NetworkController networkController) {
        super(host);
        this.mDataSaverController = networkController.getDataSaverController();
        this.mDataSaverController.observe(getLifecycle(), (Lifecycle) this);
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
        return new Intent("android.settings.DATA_SAVER_SETTINGS");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        if (((QSTile.BooleanState) this.mState).value || Prefs.getBoolean(this.mContext, Prefs.Key.QS_DATA_SAVER_DIALOG_SHOWN, false)) {
            toggleDataSaver();
            return;
        }
        SystemUIDialog dialog = new SystemUIDialog(this.mContext);
        dialog.setTitle(17039812);
        dialog.setMessage(17039810);
        dialog.setPositiveButton(17039811, new DialogInterface.OnClickListener() { // from class: com.android.systemui.qs.tiles.-$$Lambda$DataSaverTile$7vpE4nfIgph7ByTloh1_igU2EhI
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                DataSaverTile.this.lambda$handleClick$0$DataSaverTile(dialogInterface, i);
            }
        });
        dialog.setNegativeButton(17039360, null);
        dialog.setShowForAllUsers(true);
        dialog.show();
        Prefs.putBoolean(this.mContext, Prefs.Key.QS_DATA_SAVER_DIALOG_SHOWN, true);
    }

    public /* synthetic */ void lambda$handleClick$0$DataSaverTile(DialogInterface dialogInterface, int which) {
        toggleDataSaver();
    }

    private void toggleDataSaver() {
        ((QSTile.BooleanState) this.mState).value = !this.mDataSaverController.isDataSaverEnabled();
        this.mDataSaverController.setDataSaverEnabled(((QSTile.BooleanState) this.mState).value);
        refreshState(Boolean.valueOf(((QSTile.BooleanState) this.mState).value));
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.data_saver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        state.value = arg instanceof Boolean ? ((Boolean) arg).booleanValue() : this.mDataSaverController.isDataSaverEnabled();
        state.state = state.value ? 2 : 1;
        state.label = this.mContext.getString(R.string.data_saver);
        state.contentDescription = state.label;
        state.icon = QSTileImpl.ResourceIcon.get(state.value ? R.drawable.ic_data_saver : R.drawable.ic_data_saver_off);
        state.expandedAccessibilityClassName = Switch.class.getName();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 284;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R.string.accessibility_quick_settings_data_saver_changed_on);
        }
        return this.mContext.getString(R.string.accessibility_quick_settings_data_saver_changed_off);
    }

    @Override // com.android.systemui.statusbar.policy.DataSaverController.Listener
    public void onDataSaverChanged(boolean isDataSaving) {
        refreshState(Boolean.valueOf(isDataSaving));
    }
}
