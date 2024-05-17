package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.wifi.WifiStatusTracker;
import com.android.systemui.R;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.SignalController;
import java.util.Objects;
/* loaded from: classes21.dex */
public class WifiSignalController extends SignalController<WifiState, SignalController.IconGroup> {
    private final boolean mHasMobileData;
    private final WifiStatusTracker mWifiTracker;

    public WifiSignalController(Context context, boolean hasMobileData, CallbackHandler callbackHandler, NetworkControllerImpl networkController, WifiManager wifiManager) {
        super("WifiSignalController", context, 1, callbackHandler, networkController);
        NetworkScoreManager networkScoreManager = (NetworkScoreManager) context.getSystemService(NetworkScoreManager.class);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        this.mWifiTracker = new WifiStatusTracker(this.mContext, wifiManager, networkScoreManager, connectivityManager, new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$WifiSignalController$AffzGdHvQakHA4bIzi_tW1MVLCY
            @Override // java.lang.Runnable
            public final void run() {
                WifiSignalController.this.handleStatusUpdated();
            }
        });
        this.mWifiTracker.setListening(true);
        this.mHasMobileData = hasMobileData;
        if (wifiManager != null) {
            wifiManager.registerTrafficStateCallback(new WifiTrafficStateCallback(), null);
        }
        SignalController.IconGroup iconGroup = new SignalController.IconGroup("Wi-Fi Icons", WifiIcons.WIFI_SIGNAL_STRENGTH, WifiIcons.QS_WIFI_SIGNAL_STRENGTH, AccessibilityContentDescriptions.WIFI_CONNECTION_STRENGTH, 17302852, 17302852, 17302852, 17302852, AccessibilityContentDescriptions.WIFI_NO_CONNECTION);
        ((WifiState) this.mLastState).iconGroup = iconGroup;
        ((WifiState) this.mCurrentState).iconGroup = iconGroup;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.policy.SignalController
    public WifiState cleanState() {
        return new WifiState();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void refreshLocale() {
        this.mWifiTracker.refreshLocale();
    }

    @Override // com.android.systemui.statusbar.policy.SignalController
    public void notifyListeners(NetworkController.SignalCallback callback) {
        String contentDescription;
        boolean visibleWhenEnabled = this.mContext.getResources().getBoolean(R.bool.config_showWifiIndicatorWhenEnabled);
        boolean wifiVisible = ((WifiState) this.mCurrentState).enabled && (((WifiState) this.mCurrentState).connected || !this.mHasMobileData || visibleWhenEnabled);
        String wifiDesc = wifiVisible ? ((WifiState) this.mCurrentState).ssid : null;
        boolean ssidPresent = wifiVisible && ((WifiState) this.mCurrentState).ssid != null;
        String contentDescription2 = getStringIfExists(getContentDescription()).toString();
        if (((WifiState) this.mCurrentState).inetCondition != 0) {
            contentDescription = contentDescription2;
        } else {
            contentDescription = contentDescription2 + "," + this.mContext.getString(R.string.data_connection_no_internet);
        }
        NetworkController.IconState statusIcon = new NetworkController.IconState(wifiVisible, getCurrentIconId(), contentDescription);
        NetworkController.IconState qsIcon = new NetworkController.IconState(((WifiState) this.mCurrentState).connected, getQsCurrentIconId(), contentDescription);
        callback.setWifiIndicators(((WifiState) this.mCurrentState).enabled, statusIcon, qsIcon, ssidPresent && ((WifiState) this.mCurrentState).activityIn, ssidPresent && ((WifiState) this.mCurrentState).activityOut, wifiDesc, ((WifiState) this.mCurrentState).isTransient, ((WifiState) this.mCurrentState).statusLabel);
    }

    public void handleBroadcast(Intent intent) {
        this.mWifiTracker.handleBroadcast(intent);
        ((WifiState) this.mCurrentState).enabled = this.mWifiTracker.enabled;
        ((WifiState) this.mCurrentState).connected = this.mWifiTracker.connected;
        ((WifiState) this.mCurrentState).ssid = this.mWifiTracker.ssid;
        ((WifiState) this.mCurrentState).rssi = this.mWifiTracker.rssi;
        ((WifiState) this.mCurrentState).level = this.mWifiTracker.level;
        ((WifiState) this.mCurrentState).statusLabel = this.mWifiTracker.statusLabel;
        notifyListenersIfNecessary();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStatusUpdated() {
        ((WifiState) this.mCurrentState).statusLabel = this.mWifiTracker.statusLabel;
        notifyListenersIfNecessary();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public void setActivity(int wifiActivity) {
        boolean z = false;
        ((WifiState) this.mCurrentState).activityIn = wifiActivity == 3 || wifiActivity == 1;
        WifiState wifiState = (WifiState) this.mCurrentState;
        if (wifiActivity == 3 || wifiActivity == 2) {
            z = true;
        }
        wifiState.activityOut = z;
        notifyListenersIfNecessary();
    }

    /* loaded from: classes21.dex */
    private class WifiTrafficStateCallback implements WifiManager.TrafficStateCallback {
        private WifiTrafficStateCallback() {
        }

        public void onStateChanged(int state) {
            WifiSignalController.this.setActivity(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static class WifiState extends SignalController.State {
        boolean isTransient;
        String ssid;
        String statusLabel;

        WifiState() {
        }

        @Override // com.android.systemui.statusbar.policy.SignalController.State
        public void copyFrom(SignalController.State s) {
            super.copyFrom(s);
            WifiState state = (WifiState) s;
            this.ssid = state.ssid;
            this.isTransient = state.isTransient;
            this.statusLabel = state.statusLabel;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.systemui.statusbar.policy.SignalController.State
        public void toString(StringBuilder builder) {
            super.toString(builder);
            builder.append(",ssid=");
            builder.append(this.ssid);
            builder.append(",isTransient=");
            builder.append(this.isTransient);
            builder.append(",statusLabel=");
            builder.append(this.statusLabel);
        }

        @Override // com.android.systemui.statusbar.policy.SignalController.State
        public boolean equals(Object o) {
            if (super.equals(o)) {
                WifiState other = (WifiState) o;
                return Objects.equals(other.ssid, this.ssid) && other.isTransient == this.isTransient && TextUtils.equals(other.statusLabel, this.statusLabel);
            }
            return false;
        }
    }
}
