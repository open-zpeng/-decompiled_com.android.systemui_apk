package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.WifiTracker;
import com.android.systemui.statusbar.policy.NetworkController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public class AccessPointControllerImpl implements NetworkController.AccessPointController, WifiTracker.WifiListener {
    private static final String EXTRA_START_CONNECT_SSID = "wifi_start_connect_ssid";
    private final Context mContext;
    private final UserManager mUserManager;
    private final WifiTracker mWifiTracker;
    private static final String TAG = "AccessPointController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final int[] ICONS = WifiIcons.WIFI_FULL_ICONS;
    private final ArrayList<NetworkController.AccessPointController.AccessPointCallback> mCallbacks = new ArrayList<>();
    private final WifiManager.ActionListener mConnectListener = new WifiManager.ActionListener() { // from class: com.android.systemui.statusbar.policy.AccessPointControllerImpl.1
        public void onSuccess() {
            if (AccessPointControllerImpl.DEBUG) {
                Log.d(AccessPointControllerImpl.TAG, "connect success");
            }
        }

        public void onFailure(int reason) {
            if (AccessPointControllerImpl.DEBUG) {
                Log.d(AccessPointControllerImpl.TAG, "connect failure reason=" + reason);
            }
        }
    };
    private int mCurrentUser = ActivityManager.getCurrentUser();

    public AccessPointControllerImpl(Context context) {
        this.mContext = context;
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mWifiTracker = new WifiTracker(context, this, false, true);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.mWifiTracker.onDestroy();
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.AccessPointController
    public boolean canConfigWifi() {
        return !this.mUserManager.hasUserRestriction("no_config_wifi", new UserHandle(this.mCurrentUser));
    }

    public void onUserSwitched(int newUserId) {
        this.mCurrentUser = newUserId;
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.AccessPointController
    public void addAccessPointCallback(NetworkController.AccessPointController.AccessPointCallback callback) {
        if (callback == null || this.mCallbacks.contains(callback)) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "addCallback " + callback);
        }
        this.mCallbacks.add(callback);
        if (this.mCallbacks.size() == 1) {
            this.mWifiTracker.onStart();
        }
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.AccessPointController
    public void removeAccessPointCallback(NetworkController.AccessPointController.AccessPointCallback callback) {
        if (callback == null) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "removeCallback " + callback);
        }
        this.mCallbacks.remove(callback);
        if (this.mCallbacks.isEmpty()) {
            this.mWifiTracker.onStop();
        }
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.AccessPointController
    public void scanForAccessPoints() {
        fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.AccessPointController
    public int getIcon(AccessPoint ap) {
        int level = ap.getLevel();
        return ICONS[level >= 0 ? level : 0];
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.AccessPointController
    public boolean connect(AccessPoint ap) {
        if (ap == null) {
            return false;
        }
        if (DEBUG) {
            Log.d(TAG, "connect networkId=" + ap.getConfig().networkId);
        }
        if (ap.isSaved()) {
            this.mWifiTracker.getManager().connect(ap.getConfig().networkId, this.mConnectListener);
        } else if (ap.getSecurity() != 0) {
            Intent intent = new Intent("android.settings.WIFI_SETTINGS");
            intent.putExtra(EXTRA_START_CONNECT_SSID, ap.getSsidStr());
            intent.addFlags(268435456);
            fireSettingsIntentCallback(intent);
            return true;
        } else {
            ap.generateOpenNetworkConfig();
            this.mWifiTracker.getManager().connect(ap.getConfig(), this.mConnectListener);
        }
        return false;
    }

    private void fireSettingsIntentCallback(Intent intent) {
        Iterator<NetworkController.AccessPointController.AccessPointCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            NetworkController.AccessPointController.AccessPointCallback callback = it.next();
            callback.onSettingsActivityTriggered(intent);
        }
    }

    private void fireAcccessPointsCallback(List<AccessPoint> aps) {
        Iterator<NetworkController.AccessPointController.AccessPointCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            NetworkController.AccessPointController.AccessPointCallback callback = it.next();
            callback.onAccessPointsChanged(aps);
        }
    }

    public void dump(PrintWriter pw) {
        this.mWifiTracker.dump(pw);
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onWifiStateChanged(int state) {
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onConnectedChanged() {
        fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onAccessPointsChanged() {
        fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
    }
}
