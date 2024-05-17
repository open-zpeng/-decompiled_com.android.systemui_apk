package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.KeyChain;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.systemui.R;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.SecurityControllerImpl;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class SecurityControllerImpl extends CurrentUserTracker implements SecurityController {
    private static final int CA_CERT_LOADING_RETRY_TIME_IN_MS = 30000;
    private static final int NO_NETWORK = -1;
    private static final String VPN_BRANDED_META_DATA = "com.android.systemui.IS_BRANDED";
    private final Handler mBgHandler;
    private final BroadcastReceiver mBroadcastReceiver;
    @GuardedBy({"mCallbacks"})
    private final ArrayList<SecurityController.SecurityControllerCallback> mCallbacks;
    private final ConnectivityManager mConnectivityManager;
    private final IConnectivityManager mConnectivityManagerService;
    private final Context mContext;
    private int mCurrentUserId;
    private SparseArray<VpnConfig> mCurrentVpns;
    private final DevicePolicyManager mDevicePolicyManager;
    private ArrayMap<Integer, Boolean> mHasCACerts;
    private final ConnectivityManager.NetworkCallback mNetworkCallback;
    private final PackageManager mPackageManager;
    private final UserManager mUserManager;
    private int mVpnUserId;
    private static final String TAG = "SecurityController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final NetworkRequest REQUEST = new NetworkRequest.Builder().removeCapability(15).removeCapability(13).removeCapability(14).setUids(null).build();

    @Inject
    public SecurityControllerImpl(Context context, @Named("background_handler") Handler bgHandler) {
        this(context, bgHandler, null);
    }

    public SecurityControllerImpl(Context context, Handler bgHandler, SecurityController.SecurityControllerCallback callback) {
        super(context);
        this.mCallbacks = new ArrayList<>();
        this.mCurrentVpns = new SparseArray<>();
        this.mHasCACerts = new ArrayMap<>();
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.systemui.statusbar.policy.SecurityControllerImpl.1
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onAvailable(Network network) {
                if (SecurityControllerImpl.DEBUG) {
                    Log.d(SecurityControllerImpl.TAG, "onAvailable " + network.netId);
                }
                SecurityControllerImpl.this.updateState();
                SecurityControllerImpl.this.fireCallbacks();
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                if (SecurityControllerImpl.DEBUG) {
                    Log.d(SecurityControllerImpl.TAG, "onLost " + network.netId);
                }
                SecurityControllerImpl.this.updateState();
                SecurityControllerImpl.this.fireCallbacks();
            }
        };
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.policy.SecurityControllerImpl.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.security.action.TRUST_STORE_CHANGED".equals(intent.getAction())) {
                    SecurityControllerImpl.this.refreshCACerts();
                }
            }
        };
        this.mContext = context;
        this.mBgHandler = bgHandler;
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mConnectivityManagerService = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService("user");
        addCallback(callback);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.security.action.TRUST_STORE_CHANGED");
        context.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, filter, null, bgHandler);
        this.mConnectivityManager.registerNetworkCallback(REQUEST, this.mNetworkCallback);
        onUserSwitched(ActivityManager.getCurrentUser());
        startTracking();
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("SecurityController state:");
        pw.print("  mCurrentVpns={");
        for (int i = 0; i < this.mCurrentVpns.size(); i++) {
            if (i > 0) {
                pw.print(", ");
            }
            pw.print(this.mCurrentVpns.keyAt(i));
            pw.print('=');
            pw.print(this.mCurrentVpns.valueAt(i).user);
        }
        pw.println("}");
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public boolean isDeviceManaged() {
        return this.mDevicePolicyManager.isDeviceManaged();
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public String getDeviceOwnerName() {
        return this.mDevicePolicyManager.getDeviceOwnerNameOnAnyUser();
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public boolean hasProfileOwner() {
        return this.mDevicePolicyManager.getProfileOwnerAsUser(this.mCurrentUserId) != null;
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public String getProfileOwnerName() {
        int[] profileIdsWithDisabled;
        for (int profileId : this.mUserManager.getProfileIdsWithDisabled(this.mCurrentUserId)) {
            String name = this.mDevicePolicyManager.getProfileOwnerNameAsUser(profileId);
            if (name != null) {
                return name;
            }
        }
        return null;
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public CharSequence getDeviceOwnerOrganizationName() {
        return this.mDevicePolicyManager.getDeviceOwnerOrganizationName();
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public CharSequence getWorkProfileOrganizationName() {
        int profileId = getWorkProfileUserId(this.mCurrentUserId);
        if (profileId == -10000) {
            return null;
        }
        return this.mDevicePolicyManager.getOrganizationNameForUser(profileId);
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public String getPrimaryVpnName() {
        VpnConfig cfg = this.mCurrentVpns.get(this.mVpnUserId);
        if (cfg != null) {
            return getNameForVpnConfig(cfg, new UserHandle(this.mVpnUserId));
        }
        return null;
    }

    private int getWorkProfileUserId(int userId) {
        for (UserInfo userInfo : this.mUserManager.getProfiles(userId)) {
            if (userInfo.isManagedProfile()) {
                return userInfo.id;
            }
        }
        return -10000;
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public boolean hasWorkProfile() {
        return getWorkProfileUserId(this.mCurrentUserId) != -10000;
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public String getWorkProfileVpnName() {
        VpnConfig cfg;
        int profileId = getWorkProfileUserId(this.mVpnUserId);
        if (profileId == -10000 || (cfg = this.mCurrentVpns.get(profileId)) == null) {
            return null;
        }
        return getNameForVpnConfig(cfg, UserHandle.of(profileId));
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public boolean isNetworkLoggingEnabled() {
        return this.mDevicePolicyManager.isNetworkLoggingEnabled(null);
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public boolean isVpnEnabled() {
        int[] profileIdsWithDisabled;
        for (int profileId : this.mUserManager.getProfileIdsWithDisabled(this.mVpnUserId)) {
            if (this.mCurrentVpns.get(profileId) != null) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public boolean isVpnRestricted() {
        UserHandle currentUser = new UserHandle(this.mCurrentUserId);
        return this.mUserManager.getUserInfo(this.mCurrentUserId).isRestricted() || this.mUserManager.hasUserRestriction("no_config_vpn", currentUser);
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public boolean isVpnBranded() {
        String packageName;
        VpnConfig cfg = this.mCurrentVpns.get(this.mVpnUserId);
        if (cfg == null || (packageName = getPackageNameForVpnConfig(cfg)) == null) {
            return false;
        }
        return isVpnPackageBranded(packageName);
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public boolean hasCACertInCurrentUser() {
        Boolean hasCACerts = this.mHasCACerts.get(Integer.valueOf(this.mCurrentUserId));
        return hasCACerts != null && hasCACerts.booleanValue();
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController
    public boolean hasCACertInWorkProfile() {
        Boolean hasCACerts;
        int userId = getWorkProfileUserId(this.mCurrentUserId);
        return (userId == -10000 || (hasCACerts = this.mHasCACerts.get(Integer.valueOf(userId))) == null || !hasCACerts.booleanValue()) ? false : true;
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(SecurityController.SecurityControllerCallback callback) {
        synchronized (this.mCallbacks) {
            if (callback == null) {
                return;
            }
            if (DEBUG) {
                Log.d(TAG, "removeCallback " + callback);
            }
            this.mCallbacks.remove(callback);
        }
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(SecurityController.SecurityControllerCallback callback) {
        synchronized (this.mCallbacks) {
            if (callback != null) {
                if (!this.mCallbacks.contains(callback)) {
                    if (DEBUG) {
                        Log.d(TAG, "addCallback " + callback);
                    }
                    this.mCallbacks.add(callback);
                }
            }
        }
    }

    @Override // com.android.systemui.settings.CurrentUserTracker
    public void onUserSwitched(int newUserId) {
        this.mCurrentUserId = newUserId;
        UserInfo newUserInfo = this.mUserManager.getUserInfo(newUserId);
        if (newUserInfo.isRestricted()) {
            this.mVpnUserId = newUserInfo.restrictedProfileParentId;
        } else {
            this.mVpnUserId = this.mCurrentUserId;
        }
        refreshCACerts();
        fireCallbacks();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshCACerts() {
        new CACertLoader().execute(Integer.valueOf(this.mCurrentUserId));
        int workProfileId = getWorkProfileUserId(this.mCurrentUserId);
        if (workProfileId != -10000) {
            new CACertLoader().execute(Integer.valueOf(workProfileId));
        }
    }

    private String getNameForVpnConfig(VpnConfig cfg, UserHandle user) {
        if (cfg.legacy) {
            return this.mContext.getString(R.string.legacy_vpn_name);
        }
        String vpnPackage = cfg.user;
        try {
            Context userContext = this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, user);
            return VpnConfig.getVpnLabel(userContext, vpnPackage).toString();
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(TAG, "Package " + vpnPackage + " is not present", nnfe);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireCallbacks() {
        synchronized (this.mCallbacks) {
            Iterator<SecurityController.SecurityControllerCallback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                SecurityController.SecurityControllerCallback callback = it.next();
                callback.onStateChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateState() {
        LegacyVpnInfo legacyVpn;
        SparseArray<VpnConfig> vpns = new SparseArray<>();
        try {
            for (UserInfo user : this.mUserManager.getUsers()) {
                VpnConfig cfg = this.mConnectivityManagerService.getVpnConfig(user.id);
                if (cfg != null && (!cfg.legacy || ((legacyVpn = this.mConnectivityManagerService.getLegacyVpnInfo(user.id)) != null && legacyVpn.state == 3))) {
                    vpns.put(user.id, cfg);
                }
            }
            this.mCurrentVpns = vpns;
        } catch (RemoteException rme) {
            Log.e(TAG, "Unable to list active VPNs", rme);
        }
    }

    private String getPackageNameForVpnConfig(VpnConfig cfg) {
        if (cfg.legacy) {
            return null;
        }
        return cfg.user;
    }

    private boolean isVpnPackageBranded(String packageName) {
        try {
            ApplicationInfo info = this.mPackageManager.getApplicationInfo(packageName, 128);
            if (info != null && info.metaData != null && info.isSystemApp()) {
                boolean isBranded = info.metaData.getBoolean(VPN_BRANDED_META_DATA, false);
                return isBranded;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class CACertLoader extends AsyncTask<Integer, Void, Pair<Integer, Boolean>> {
        protected CACertLoader() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Pair<Integer, Boolean> doInBackground(final Integer... userId) {
            try {
                KeyChain.KeyChainConnection conn = KeyChain.bindAsUser(SecurityControllerImpl.this.mContext, UserHandle.of(userId[0].intValue()));
                boolean hasCACerts = !conn.getService().getUserCaAliases().getList().isEmpty();
                Pair<Integer, Boolean> pair = new Pair<>(userId[0], Boolean.valueOf(hasCACerts));
                conn.close();
                return pair;
            } catch (RemoteException | AssertionError | InterruptedException e) {
                Log.i(SecurityControllerImpl.TAG, "failed to get CA certs", e);
                SecurityControllerImpl.this.mBgHandler.postDelayed(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$SecurityControllerImpl$CACertLoader$xO5ELH-ynhsu1kwnRVzV4aHRUJ0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SecurityControllerImpl.CACertLoader.this.lambda$doInBackground$0$SecurityControllerImpl$CACertLoader(userId);
                    }
                }, 30000L);
                return new Pair<>(userId[0], null);
            }
        }

        public /* synthetic */ void lambda$doInBackground$0$SecurityControllerImpl$CACertLoader(Integer[] userId) {
            new CACertLoader().execute(userId[0]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Pair<Integer, Boolean> result) {
            if (SecurityControllerImpl.DEBUG) {
                Log.d(SecurityControllerImpl.TAG, "onPostExecute " + result);
            }
            if (result.second != null) {
                SecurityControllerImpl.this.mHasCACerts.put((Integer) result.first, (Boolean) result.second);
                SecurityControllerImpl.this.fireCallbacks();
            }
        }
    }
}
