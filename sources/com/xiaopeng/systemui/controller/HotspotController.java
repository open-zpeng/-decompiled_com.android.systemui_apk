package com.xiaopeng.systemui.controller;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.UserManager;
import com.xiaopeng.systemui.Logger;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class HotspotController implements WifiManager.SoftApCallback {
    private static final boolean DEBUG = true;
    private static final String TAG = "HotspotController";
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private Handler mHandler;
    private int mHotspotState;
    private int mNumConnectedDevices;
    private boolean mWaitingForCallback;
    private final WifiManager mWifiManager;
    private final ArrayList<Callback> mCallbacks = new ArrayList<>();
    private final WifiStateReceiver mWifiStateReceiver = new WifiStateReceiver();

    /* loaded from: classes24.dex */
    public interface Callback {
        void onHotspotChanged(boolean z, int i);
    }

    public HotspotController(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public boolean isHotspotSupported() {
        return this.mConnectivityManager.isTetheringSupported() && this.mConnectivityManager.getTetherableWifiRegexs().length != 0 && UserManager.get(this.mContext).isUserAdmin(ActivityManager.getCurrentUser());
    }

    public void addCallback(Callback callback) {
        synchronized (this.mCallbacks) {
            if (callback != null) {
                if (!this.mCallbacks.contains(callback)) {
                    Logger.d(TAG, "addCallback " + callback);
                    this.mCallbacks.add(callback);
                    updateWifiStateListeners(!this.mCallbacks.isEmpty());
                }
            }
        }
    }

    public void removeCallback(Callback callback) {
        if (callback == null) {
            return;
        }
        Logger.d(TAG, "removeCallback " + callback);
        synchronized (this.mCallbacks) {
            this.mCallbacks.remove(callback);
            updateWifiStateListeners(!this.mCallbacks.isEmpty());
        }
    }

    private void updateWifiStateListeners(boolean shouldListen) {
        this.mWifiStateReceiver.setListening(shouldListen);
        if (shouldListen) {
            this.mWifiManager.registerSoftApCallback(this, this.mHandler);
        } else {
            this.mWifiManager.unregisterSoftApCallback(this);
        }
    }

    public boolean isHotspotEnabled() {
        return this.mHotspotState == 13;
    }

    public boolean isHotspotTransient() {
        return this.mWaitingForCallback || this.mHotspotState == 12;
    }

    public void setHotspotEnabled(boolean enabled) {
        if (!enabled) {
            this.mConnectivityManager.stopTethering(0);
            return;
        }
        OnStartTetheringCallback callback = new OnStartTetheringCallback();
        this.mWaitingForCallback = true;
        Logger.d(TAG, "Starting tethering");
        this.mConnectivityManager.startTethering(0, false, callback);
    }

    public int getNumConnectedDevices() {
        return this.mNumConnectedDevices;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireHotspotChangedCallback(boolean enabled) {
        fireHotspotChangedCallback(enabled, this.mNumConnectedDevices);
    }

    private void fireHotspotChangedCallback(boolean enabled, int numConnectedDevices) {
        synchronized (this.mCallbacks) {
            Iterator<Callback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                Callback callback = it.next();
                callback.onHotspotChanged(enabled, numConnectedDevices);
            }
        }
    }

    public void onStateChanged(int state, int failureReason) {
    }

    public void onNumClientsChanged(int numConnectedDevices) {
        this.mNumConnectedDevices = numConnectedDevices;
        fireHotspotChangedCallback(isHotspotEnabled(), numConnectedDevices);
    }

    private static String stateToString(int hotspotState) {
        switch (hotspotState) {
            case 10:
                return "DISABLING";
            case 11:
                return "DISABLED";
            case 12:
                return "ENABLING";
            case 13:
                return "ENABLED";
            case 14:
                return "FAILED";
            default:
                return null;
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("HotspotController state:");
        pw.print("  mHotspotEnabled=");
        pw.println(stateToString(this.mHotspotState));
    }

    /* loaded from: classes24.dex */
    private final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        private OnStartTetheringCallback() {
        }

        public void onTetheringStarted() {
            Logger.d(HotspotController.TAG, "onTetheringStarted");
            HotspotController.this.mWaitingForCallback = false;
        }

        public void onTetheringFailed() {
            Logger.d(HotspotController.TAG, "onTetheringFailed");
            HotspotController.this.mWaitingForCallback = false;
            HotspotController hotspotController = HotspotController.this;
            hotspotController.fireHotspotChangedCallback(hotspotController.isHotspotEnabled());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public final class WifiStateReceiver extends BroadcastReceiver {
        private boolean mRegistered;

        private WifiStateReceiver() {
        }

        public void setListening(boolean listening) {
            if (listening && !this.mRegistered) {
                Logger.d(HotspotController.TAG, "Registering receiver");
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
                HotspotController.this.mContext.registerReceiver(this, filter);
                this.mRegistered = true;
            } else if (!listening && this.mRegistered) {
                Logger.d(HotspotController.TAG, "Unregistering receiver");
                HotspotController.this.mContext.unregisterReceiver(this);
                this.mRegistered = false;
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra("wifi_state", 14);
            Logger.d(HotspotController.TAG, "onReceive " + state);
            HotspotController.this.mHotspotState = state;
            if (!HotspotController.this.isHotspotEnabled()) {
                HotspotController.this.mNumConnectedDevices = 0;
            }
            HotspotController hotspotController = HotspotController.this;
            hotspotController.fireHotspotChangedCallback(hotspotController.isHotspotEnabled());
        }
    }
}
