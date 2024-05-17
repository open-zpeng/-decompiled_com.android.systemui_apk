package com.xiaopeng.systemui.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.statusbar.CallbackHandler;
import java.util.BitSet;
/* loaded from: classes24.dex */
public class NetworkController extends BroadcastReceiver {
    public static final int NETWORK_MOBILE = 1;
    public static final int NETWORK_WIFI = 0;
    public static final int TBOX_STATUS_CONNECTED = 1;
    public static final int TBOX_STATUS_DISCONNECT = 0;
    public static final int TBOX_STATUS_NOT_WORKING = 2;
    private final CallbackHandler mCallbackHandler;
    private final BitSet mConnectedTransports;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final Handler mReceiverHandler;
    private final Runnable mRegisterListeners;
    private final TelephonyManager mTelephonyManager;
    private final BitSet mValidatedTransports;
    private final WifiController mWifiController;
    private final WifiManager mWifiManager;
    static final String TAG = "NetworkController";
    static final boolean DEBUG = Log.isLoggable(TAG, 3);

    public NetworkController(Context context, Looper bgLooper) {
        this(context, (ConnectivityManager) context.getSystemService("connectivity"), (TelephonyManager) context.getSystemService("phone"), (WifiManager) context.getSystemService("wifi"), bgLooper, new CallbackHandler());
        this.mReceiverHandler.post(this.mRegisterListeners);
    }

    @VisibleForTesting
    NetworkController(Context context, ConnectivityManager connectivityManager, TelephonyManager telephonyManager, WifiManager wifiManager, Looper bgLooper, CallbackHandler callbackHandler) {
        this.mConnectedTransports = new BitSet();
        this.mValidatedTransports = new BitSet();
        this.mRegisterListeners = new Runnable() { // from class: com.xiaopeng.systemui.controller.NetworkController.4
            @Override // java.lang.Runnable
            public void run() {
                NetworkController.this.registerListeners();
            }
        };
        this.mContext = context;
        this.mWifiManager = wifiManager;
        this.mCallbackHandler = callbackHandler;
        this.mReceiverHandler = new Handler(bgLooper);
        this.mTelephonyManager = telephonyManager;
        this.mConnectivityManager = connectivityManager;
        this.mWifiController = new WifiController(this.mContext, this.mCallbackHandler, this, this.mWifiManager);
        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() { // from class: com.xiaopeng.systemui.controller.NetworkController.1
            private Network mLastNetwork;
            private NetworkCapabilities mLastNetworkCapabilities;

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                NetworkCapabilities networkCapabilities2 = this.mLastNetworkCapabilities;
                boolean lastValidated = networkCapabilities2 != null && networkCapabilities2.hasCapability(16);
                boolean validated = networkCapabilities.hasCapability(16);
                if (network.equals(this.mLastNetwork) && networkCapabilities.equalsTransportTypes(this.mLastNetworkCapabilities) && validated == lastValidated) {
                    return;
                }
                this.mLastNetwork = network;
                this.mLastNetworkCapabilities = networkCapabilities;
                Logger.d(NetworkController.TAG, "NetworkInfo = " + this.mLastNetworkCapabilities.toString());
                NetworkController.this.updateConnectivity();
            }
        };
        this.mConnectivityManager.registerDefaultNetworkCallback(callback, this.mReceiverHandler);
    }

    /* loaded from: classes24.dex */
    public interface SignalCallback {
        void setWifiConnectionState(boolean z);

        void setWifiState(int i);

        default void setWifiLevel(int level) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerListeners() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.RSSI_CHANGED");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction("android.intent.action.SIM_STATE_CHANGED");
        filter.addAction("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED");
        filter.addAction("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED");
        filter.addAction("android.intent.action.SERVICE_STATE");
        filter.addAction("android.provider.Telephony.SPN_STRINGS_UPDATED");
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.conn.INET_CONDITION_ACTION");
        filter.addAction("android.intent.action.AIRPLANE_MODE");
        filter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        this.mContext.registerReceiver(this, filter, null, this.mReceiverHandler);
    }

    private void unregisterListeners() {
        this.mContext.unregisterReceiver(this);
    }

    public int getWifiLevel() {
        return this.mWifiController.getState().level;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.xiaopeng.systemui.controller.NetworkController$2] */
    public void setWifiEnabled(final boolean enabled) {
        new AsyncTask<Void, Void, Void>() { // from class: com.xiaopeng.systemui.controller.NetworkController.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... args) {
                NetworkController.this.mWifiManager.setWifiEnabled(enabled);
                return null;
            }
        }.execute(new Void[0]);
    }

    public void addCallback(SignalCallback cb) {
        this.mWifiController.notifyListeners(cb);
        this.mCallbackHandler.setListening(cb, true);
    }

    public void removeCallback(SignalCallback cb) {
        this.mCallbackHandler.setListening(cb, false);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        char c;
        String action = intent.getAction();
        boolean z = false;
        switch (action.hashCode()) {
            case -2104353374:
                if (action.equals("android.intent.action.SERVICE_STATE")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1465084191:
                if (action.equals("android.intent.action.ACTION_DEFAULT_VOICE_SUBSCRIPTION_CHANGED")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1172645946:
                if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1138588223:
                if (action.equals("android.telephony.action.CARRIER_CONFIG_CHANGED")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1076576821:
                if (action.equals("android.intent.action.AIRPLANE_MODE")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -343630553:
                if (action.equals("android.net.wifi.STATE_CHANGE")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -229777127:
                if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -25388475:
                if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 623179603:
                if (action.equals("android.net.conn.INET_CONDITION_ACTION")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                Network[] networkInfos = this.mConnectivityManager.getAllNetworks();
                boolean hasTboxNetwork = false;
                if (networkInfos != null && networkInfos.length > 0) {
                    Logger.d(TAG, "networkInfos len = " + networkInfos.length);
                    for (Network network : networkInfos) {
                        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(network);
                        if (networkInfo != null && (networkInfo.getSubtype() == 3 || networkInfo.getSubtype() == 1)) {
                            hasTboxNetwork = true;
                            boolean tboxConnected = networkInfo.isConnected();
                            Logger.d(TAG, "tboxConnected = " + tboxConnected);
                            CarController.getInstance(this.mContext).onTboxConnectionChanged(tboxConnected ? 1 : 2);
                        }
                    }
                }
                if (!hasTboxNetwork) {
                    Logger.d(TAG, "no tbox network : tboxConnected = false");
                    CarController.getInstance(this.mContext).onTboxConnectionChanged(0);
                }
                updateConnectivity();
                return;
            case 1:
                updateConnectivity();
                return;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return;
            case '\b':
                Parcelable parcelableExtra = intent.getParcelableExtra("networkInfo");
                if (parcelableExtra != null) {
                    NetworkInfo networkInfo2 = (NetworkInfo) parcelableExtra;
                    Logger.d(TAG, "networkInfo.isConnected : " + networkInfo2.isConnected() + " isCarRecorderConnected : " + isCarRecorderConnected());
                    CallbackHandler callbackHandler = this.mCallbackHandler;
                    if (networkInfo2.isConnected() && !isCarRecorderConnected()) {
                        z = true;
                    }
                    callbackHandler.setWifiConnectionState(z);
                }
                this.mWifiController.handleBroadcast(intent);
                return;
            default:
                this.mWifiController.handleBroadcast(intent);
                return;
        }
    }

    public boolean isWifiConnected() {
        NetworkInfo activeNetworkInfo = this.mConnectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            int type = activeNetworkInfo.getType();
            int subType = activeNetworkInfo.getSubtype();
            return ((type == 9 && subType == 2) || type == 1) && activeNetworkInfo.isConnected();
        }
        Logger.i(TAG, "getActiveNetworkType : activeNetworkInfo = null");
        return false;
    }

    public boolean isCarRecorderConnected() {
        WifiManager wifiManager = this.mWifiManager;
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            boolean result = CarModelsManager.getConfig().isCarRecorderName(wifiInfo.getSSID());
            Logger.d(TAG, "WiFiInfo : " + wifiInfo + " CarRecorderConnected : " + result);
            return result;
        }
        return false;
    }

    public int getActiveNetworkType() {
        NetworkInfo activeNetworkInfo = this.mConnectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            int type = activeNetworkInfo.getType();
            int subType = activeNetworkInfo.getSubtype();
            if ((type == 9 && subType == 2) || type == 1) {
                return 0;
            }
        } else {
            Logger.i(TAG, "getActiveNetworkType : activeNetworkInfo = null");
        }
        return 1;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        this.mReceiverHandler.post(new Runnable() { // from class: com.xiaopeng.systemui.controller.NetworkController.3
            @Override // java.lang.Runnable
            public void run() {
                NetworkController.this.handleConfigurationChanged();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConfigurationChanged() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConnectivity() {
        this.mConnectedTransports.clear();
        this.mValidatedTransports.clear();
    }

    public int getWifiState() {
        return this.mWifiManager.getWifiState();
    }
}
