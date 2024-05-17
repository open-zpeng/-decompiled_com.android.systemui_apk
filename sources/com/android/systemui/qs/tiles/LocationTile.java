package com.android.systemui.qs.tiles;

import android.content.Intent;
import android.widget.Switch;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.LocationController;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class LocationTile extends QSTileImpl<QSTile.BooleanState> {
    private final ActivityStarter mActivityStarter;
    private final Callback mCallback;
    private final LocationController mController;
    private final QSTile.Icon mIcon;
    private final KeyguardMonitor mKeyguard;

    @Inject
    public LocationTile(QSHost host, LocationController locationController, KeyguardMonitor keyguardMonitor, ActivityStarter activityStarter) {
        super(host);
        this.mIcon = QSTileImpl.ResourceIcon.get(R.drawable.ic_location);
        this.mCallback = new Callback();
        this.mController = locationController;
        this.mKeyguard = keyguardMonitor;
        this.mActivityStarter = activityStarter;
        this.mController.observe((LifecycleOwner) this, (LocationTile) this.mCallback);
        this.mKeyguard.observe((LifecycleOwner) this, (LocationTile) this.mCallback);
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
        return new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        if (this.mKeyguard.isSecure() && this.mKeyguard.isShowing()) {
            this.mActivityStarter.postQSRunnableDismissingKeyguard(new Runnable() { // from class: com.android.systemui.qs.tiles.-$$Lambda$LocationTile$cnlxD4jGztrpcRYGbQTKRSm3Ng0
                @Override // java.lang.Runnable
                public final void run() {
                    LocationTile.this.lambda$handleClick$0$LocationTile();
                }
            });
            return;
        }
        boolean wasEnabled = ((QSTile.BooleanState) this.mState).value;
        this.mController.setLocationEnabled(!wasEnabled);
    }

    public /* synthetic */ void lambda$handleClick$0$LocationTile() {
        boolean wasEnabled = ((QSTile.BooleanState) this.mState).value;
        this.mHost.openPanels();
        this.mController.setLocationEnabled(!wasEnabled);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return this.mContext.getString(R.string.quick_settings_location_label);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleUpdateState(QSTile.BooleanState state, Object arg) {
        if (state.slash == null) {
            state.slash = new QSTile.SlashState();
        }
        boolean locationEnabled = this.mController.isLocationEnabled();
        state.value = locationEnabled;
        checkIfRestrictionEnforcedByAdminOnly(state, "no_share_location");
        if (!state.disabledByPolicy) {
            checkIfRestrictionEnforcedByAdminOnly(state, "no_config_location");
        }
        state.icon = this.mIcon;
        state.slash.isSlashed = !state.value;
        if (locationEnabled) {
            state.label = this.mContext.getString(R.string.quick_settings_location_label);
            state.contentDescription = this.mContext.getString(R.string.accessibility_quick_settings_location_on);
        } else {
            state.label = this.mContext.getString(R.string.quick_settings_location_label);
            state.contentDescription = this.mContext.getString(R.string.accessibility_quick_settings_location_off);
        }
        state.state = state.value ? 2 : 1;
        state.expandedAccessibilityClassName = Switch.class.getName();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 122;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected String composeChangeAnnouncement() {
        if (((QSTile.BooleanState) this.mState).value) {
            return this.mContext.getString(R.string.accessibility_quick_settings_location_changed_on);
        }
        return this.mContext.getString(R.string.accessibility_quick_settings_location_changed_off);
    }

    /* loaded from: classes21.dex */
    private final class Callback implements LocationController.LocationChangeCallback, KeyguardMonitor.Callback {
        private Callback() {
        }

        @Override // com.android.systemui.statusbar.policy.LocationController.LocationChangeCallback
        public void onLocationSettingsChanged(boolean enabled) {
            LocationTile.this.refreshState();
        }

        @Override // com.android.systemui.statusbar.policy.KeyguardMonitor.Callback
        public void onKeyguardShowingChanged() {
            LocationTile.this.refreshState();
        }
    }
}
