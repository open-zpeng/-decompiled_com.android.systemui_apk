package com.xiaopeng.systemui.controller;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.wifi.WifiManager;
import com.android.settingslib.wifi.WifiStatusTracker;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.NetworkController;
import com.xiaopeng.systemui.controller.SignalController;
import com.xiaopeng.systemui.statusbar.CallbackHandler;
/* loaded from: classes24.dex */
public class WifiController extends SignalController<WifiState> {
    private static final String TAG = "WifiController";
    private final WifiStatusTracker mWifiTracker;

    public WifiController(Context context, CallbackHandler callbackHandler, NetworkController networkController, WifiManager wifiManager) {
        super(TAG, context, 1, callbackHandler, networkController);
        NetworkScoreManager networkScoreManager = (NetworkScoreManager) context.getSystemService(NetworkScoreManager.class);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        this.mWifiTracker = new WifiStatusTracker(this.mContext, wifiManager, networkScoreManager, connectivityManager, new Runnable() { // from class: com.xiaopeng.systemui.controller.-$$Lambda$WifiController$88oCV0GCdXWlvg0PmZR0pROnvoc
            @Override // java.lang.Runnable
            public final void run() {
                WifiController.this.handleStatusUpdated();
            }
        });
        this.mWifiTracker.setListening(true);
        handleStatusUpdated();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.controller.SignalController
    public WifiState cleanState() {
        return new WifiState();
    }

    @Override // com.xiaopeng.systemui.controller.SignalController
    public void notifyListeners(NetworkController.SignalCallback callback) {
        int level = 0;
        if (((WifiState) this.mCurrentState).enabled && ((WifiState) this.mCurrentState).connected) {
            level = ((WifiState) this.mCurrentState).level;
        }
        Logger.d(TAG, "notifyListeners currentState.level=" + level);
        callback.setWifiLevel(level);
    }

    public void handleBroadcast(Intent intent) {
        try {
            this.mWifiTracker.handleBroadcast(intent);
        } catch (Exception e) {
        }
        ((WifiState) this.mCurrentState).enabled = this.mWifiTracker.enabled;
        ((WifiState) this.mCurrentState).connected = this.mWifiTracker.connected;
        ((WifiState) this.mCurrentState).rssi = this.mWifiTracker.rssi;
        ((WifiState) this.mCurrentState).level = this.mWifiTracker.level;
        notifyListenersIfNecessary();
        String action = intent.getAction();
        char c = 65535;
        if (action.hashCode() == -1875733435 && action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            c = 0;
        }
        if (c == 0 && this.mCallbackHandler != null) {
            int wifiState = intent.getIntExtra("wifi_state", 0);
            this.mCallbackHandler.setWifiState(wifiState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStatusUpdated() {
        notifyListenersIfNecessary();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public static class WifiState extends SignalController.State {
        WifiState() {
        }

        @Override // com.xiaopeng.systemui.controller.SignalController.State
        public void copyFrom(SignalController.State s) {
            super.copyFrom(s);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.systemui.controller.SignalController.State
        public void toString(StringBuilder builder) {
            super.toString(builder);
        }

        @Override // com.xiaopeng.systemui.controller.SignalController.State
        public boolean equals(Object o) {
            return false;
        }
    }
}
