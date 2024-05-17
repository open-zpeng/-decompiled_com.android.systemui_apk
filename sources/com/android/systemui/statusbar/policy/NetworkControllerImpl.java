package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.net.DataUsageController;
import com.android.systemui.DemoMode;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.settings.CurrentUserTracker;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.MobileSignalController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkControllerImpl;
import com.android.systemui.statusbar.policy.WifiSignalController;
import io.reactivex.annotations.SchedulerSupport;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NetworkControllerImpl extends BroadcastReceiver implements NetworkController, DemoMode, DataUsageController.NetworkNameProvider, Dumpable {
    private static final int EMERGENCY_ASSUMED_VOICE_CONTROLLER = 400;
    private static final int EMERGENCY_FIRST_CONTROLLER = 100;
    private static final int EMERGENCY_NO_CONTROLLERS = 0;
    private static final int EMERGENCY_NO_SUB = 300;
    private static final int EMERGENCY_VOICE_CONTROLLER = 200;
    private final AccessPointControllerImpl mAccessPoints;
    private int mActiveMobileDataSubscription;
    private boolean mAirplaneMode;
    private final CallbackHandler mCallbackHandler;
    private Config mConfig;
    private ConfigurationController.ConfigurationListener mConfigurationListener;
    private final BitSet mConnectedTransports;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private List<SubscriptionInfo> mCurrentSubscriptions;
    private int mCurrentUserId;
    private final DataSaverController mDataSaverController;
    private final DataUsageController mDataUsageController;
    private MobileSignalController mDefaultSignalController;
    private boolean mDemoInetCondition;
    private boolean mDemoMode;
    private WifiSignalController.WifiState mDemoWifiState;
    private int mEmergencySource;
    @VisibleForTesting
    final EthernetSignalController mEthernetSignalController;
    private final boolean mHasMobileDataFeature;
    private boolean mHasNoSubs;
    private boolean mInetCondition;
    private boolean mIsEmergency;
    @VisibleForTesting
    ServiceState mLastServiceState;
    @VisibleForTesting
    boolean mListening;
    private Locale mLocale;
    private final Object mLock;
    @VisibleForTesting
    final SparseArray<MobileSignalController> mMobileSignalControllers;
    private final TelephonyManager mPhone;
    private PhoneStateListener mPhoneStateListener;
    private final Handler mReceiverHandler;
    private final Runnable mRegisterListeners;
    private boolean mSimDetected;
    private final SubscriptionDefaults mSubDefaults;
    private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionListener;
    private final SubscriptionManager mSubscriptionManager;
    private boolean mUserSetup;
    private final CurrentUserTracker mUserTracker;
    private final BitSet mValidatedTransports;
    private final WifiManager mWifiManager;
    @VisibleForTesting
    final WifiSignalController mWifiSignalController;
    static final String TAG = "NetworkController";
    static final boolean DEBUG = Log.isLoggable(TAG, 3);
    static final boolean CHATTY = Log.isLoggable("NetworkControllerChat", 3);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.policy.NetworkControllerImpl$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 implements ConfigurationController.ConfigurationListener {
        AnonymousClass1() {
        }

        @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
        public void onConfigChanged(Configuration newConfig) {
            NetworkControllerImpl networkControllerImpl = NetworkControllerImpl.this;
            networkControllerImpl.mConfig = Config.readConfig(networkControllerImpl.mContext);
            NetworkControllerImpl.this.mReceiverHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$NetworkControllerImpl$1$01lGAVJF_U01trPIzR0_SCXgKnc
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkControllerImpl.AnonymousClass1.this.lambda$onConfigChanged$0$NetworkControllerImpl$1();
                }
            });
        }

        public /* synthetic */ void lambda$onConfigChanged$0$NetworkControllerImpl$1() {
            NetworkControllerImpl.this.handleConfigurationChanged();
        }
    }

    @Inject
    public NetworkControllerImpl(Context context, @Named("background_looper") Looper bgLooper, DeviceProvisionedController deviceProvisionedController) {
        this(context, (ConnectivityManager) context.getSystemService("connectivity"), (TelephonyManager) context.getSystemService("phone"), (WifiManager) context.getSystemService("wifi"), SubscriptionManager.from(context), Config.readConfig(context), bgLooper, new CallbackHandler(), new AccessPointControllerImpl(context), new DataUsageController(context), new SubscriptionDefaults(), deviceProvisionedController);
        this.mReceiverHandler.post(this.mRegisterListeners);
    }

    @VisibleForTesting
    NetworkControllerImpl(Context context, ConnectivityManager connectivityManager, TelephonyManager telephonyManager, WifiManager wifiManager, SubscriptionManager subManager, Config config, Looper bgLooper, CallbackHandler callbackHandler, AccessPointControllerImpl accessPointController, DataUsageController dataUsageController, SubscriptionDefaults defaultsHandler, final DeviceProvisionedController deviceProvisionedController) {
        this.mLock = new Object();
        this.mActiveMobileDataSubscription = -1;
        this.mMobileSignalControllers = new SparseArray<>();
        this.mConnectedTransports = new BitSet();
        this.mValidatedTransports = new BitSet();
        this.mAirplaneMode = false;
        this.mLocale = null;
        this.mCurrentSubscriptions = new ArrayList();
        this.mConfigurationListener = new AnonymousClass1();
        this.mRegisterListeners = new Runnable() { // from class: com.android.systemui.statusbar.policy.NetworkControllerImpl.9
            @Override // java.lang.Runnable
            public void run() {
                NetworkControllerImpl.this.registerListeners();
            }
        };
        this.mContext = context;
        this.mConfig = config;
        this.mReceiverHandler = new Handler(bgLooper);
        this.mCallbackHandler = callbackHandler;
        this.mDataSaverController = new DataSaverControllerImpl(context);
        this.mSubscriptionManager = subManager;
        this.mSubDefaults = defaultsHandler;
        this.mConnectivityManager = connectivityManager;
        this.mHasMobileDataFeature = this.mConnectivityManager.isNetworkSupported(0);
        this.mPhone = telephonyManager;
        this.mWifiManager = wifiManager;
        this.mLocale = this.mContext.getResources().getConfiguration().locale;
        this.mAccessPoints = accessPointController;
        this.mDataUsageController = dataUsageController;
        this.mDataUsageController.setNetworkController(this);
        this.mDataUsageController.setCallback(new DataUsageController.Callback() { // from class: com.android.systemui.statusbar.policy.NetworkControllerImpl.2
            @Override // com.android.settingslib.net.DataUsageController.Callback
            public void onMobileDataEnabled(boolean enabled) {
                NetworkControllerImpl.this.mCallbackHandler.setMobileDataEnabled(enabled);
                NetworkControllerImpl.this.notifyControllersMobileDataChanged();
            }
        });
        this.mWifiSignalController = new WifiSignalController(this.mContext, this.mHasMobileDataFeature, this.mCallbackHandler, this, this.mWifiManager);
        this.mEthernetSignalController = new EthernetSignalController(this.mContext, this.mCallbackHandler, this);
        updateAirplaneMode(true);
        this.mUserTracker = new CurrentUserTracker(this.mContext) { // from class: com.android.systemui.statusbar.policy.NetworkControllerImpl.3
            @Override // com.android.systemui.settings.CurrentUserTracker
            public void onUserSwitched(int newUserId) {
                NetworkControllerImpl.this.onUserSwitched(newUserId);
            }
        };
        this.mUserTracker.startTracking();
        deviceProvisionedController.addCallback(new DeviceProvisionedController.DeviceProvisionedListener() { // from class: com.android.systemui.statusbar.policy.NetworkControllerImpl.4
            @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener
            public void onUserSetupChanged() {
                NetworkControllerImpl networkControllerImpl = NetworkControllerImpl.this;
                DeviceProvisionedController deviceProvisionedController2 = deviceProvisionedController;
                networkControllerImpl.setUserSetupComplete(deviceProvisionedController2.isUserSetup(deviceProvisionedController2.getCurrentUser()));
            }
        });
        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() { // from class: com.android.systemui.statusbar.policy.NetworkControllerImpl.5
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
                NetworkControllerImpl.this.updateConnectivity();
            }
        };
        this.mConnectivityManager.registerDefaultNetworkCallback(callback, this.mReceiverHandler);
        this.mPhoneStateListener = new PhoneStateListener(bgLooper) { // from class: com.android.systemui.statusbar.policy.NetworkControllerImpl.6
            @Override // android.telephony.PhoneStateListener
            public void onActiveDataSubscriptionIdChanged(int subId) {
                NetworkControllerImpl.this.mActiveMobileDataSubscription = subId;
                NetworkControllerImpl.this.doUpdateMobileControllers();
            }
        };
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController
    public DataSaverController getDataSaverController() {
        return this.mDataSaverController;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerListeners() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            MobileSignalController mobileSignalController = this.mMobileSignalControllers.valueAt(i);
            mobileSignalController.registerListener();
        }
        if (this.mSubscriptionListener == null) {
            this.mSubscriptionListener = new SubListener(this, null);
        }
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mSubscriptionListener);
        this.mPhone.listen(this.mPhoneStateListener, 4194304);
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
        this.mListening = true;
        updateMobileControllers();
    }

    private void unregisterListeners() {
        this.mListening = false;
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            MobileSignalController mobileSignalController = this.mMobileSignalControllers.valueAt(i);
            mobileSignalController.unregisterListener();
        }
        this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mSubscriptionListener);
        this.mContext.unregisterReceiver(this);
    }

    public int getConnectedWifiLevel() {
        return this.mWifiSignalController.getState().level;
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController
    public NetworkController.AccessPointController getAccessPointController() {
        return this.mAccessPoints;
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController
    public DataUsageController getMobileDataController() {
        return this.mDataUsageController;
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController
    public void addEmergencyListener(NetworkController.EmergencyListener listener) {
        this.mCallbackHandler.setListening(listener, true);
        this.mCallbackHandler.setEmergencyCallsOnly(isEmergencyOnly());
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController
    public void removeEmergencyListener(NetworkController.EmergencyListener listener) {
        this.mCallbackHandler.setListening(listener, false);
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController
    public boolean hasMobileDataFeature() {
        return this.mHasMobileDataFeature;
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController
    public boolean hasVoiceCallingFeature() {
        return this.mPhone.getPhoneType() != 0;
    }

    private MobileSignalController getDataController() {
        int dataSubId = this.mSubDefaults.getActiveDataSubId();
        if (!SubscriptionManager.isValidSubscriptionId(dataSubId)) {
            if (DEBUG) {
                Log.e(TAG, "No data sim selected");
            }
            return this.mDefaultSignalController;
        } else if (this.mMobileSignalControllers.indexOfKey(dataSubId) >= 0) {
            return this.mMobileSignalControllers.get(dataSubId);
        } else {
            if (DEBUG) {
                Log.e(TAG, "Cannot find controller for data sub: " + dataSubId);
            }
            return this.mDefaultSignalController;
        }
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController, com.android.settingslib.net.DataUsageController.NetworkNameProvider
    public String getMobileDataNetworkName() {
        MobileSignalController controller = getDataController();
        return controller != null ? controller.getState().networkNameData : "";
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController
    public int getNumberSubscriptions() {
        return this.mMobileSignalControllers.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDataControllerDisabled() {
        MobileSignalController dataController = getDataController();
        if (dataController == null) {
            return false;
        }
        return dataController.isDataDisabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyControllersMobileDataChanged() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            MobileSignalController mobileSignalController = this.mMobileSignalControllers.valueAt(i);
            mobileSignalController.onMobileDataChanged();
        }
    }

    public boolean isEmergencyOnly() {
        if (this.mMobileSignalControllers.size() == 0) {
            this.mEmergencySource = 0;
            ServiceState serviceState = this.mLastServiceState;
            return serviceState != null && serviceState.isEmergencyOnly();
        }
        int voiceSubId = this.mSubDefaults.getDefaultVoiceSubId();
        if (!SubscriptionManager.isValidSubscriptionId(voiceSubId)) {
            for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
                MobileSignalController mobileSignalController = this.mMobileSignalControllers.valueAt(i);
                if (!mobileSignalController.getState().isEmergency) {
                    this.mEmergencySource = mobileSignalController.mSubscriptionInfo.getSubscriptionId() + 100;
                    if (DEBUG) {
                        Log.d(TAG, "Found emergency " + mobileSignalController.mTag);
                    }
                    return false;
                }
            }
        }
        if (this.mMobileSignalControllers.indexOfKey(voiceSubId) >= 0) {
            this.mEmergencySource = voiceSubId + 200;
            if (DEBUG) {
                Log.d(TAG, "Getting emergency from " + voiceSubId);
            }
            return this.mMobileSignalControllers.get(voiceSubId).getState().isEmergency;
        } else if (this.mMobileSignalControllers.size() == 1) {
            this.mEmergencySource = this.mMobileSignalControllers.keyAt(0) + 400;
            if (DEBUG) {
                Log.d(TAG, "Getting assumed emergency from " + this.mMobileSignalControllers.keyAt(0));
            }
            return this.mMobileSignalControllers.valueAt(0).getState().isEmergency;
        } else {
            if (DEBUG) {
                Log.e(TAG, "Cannot find controller for voice sub: " + voiceSubId);
            }
            this.mEmergencySource = voiceSubId + 300;
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recalculateEmergency() {
        this.mIsEmergency = isEmergencyOnly();
        this.mCallbackHandler.setEmergencyCallsOnly(this.mIsEmergency);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.statusbar.policy.NetworkController, com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(NetworkController.SignalCallback cb) {
        cb.setSubs(this.mCurrentSubscriptions);
        cb.setIsAirplaneMode(new NetworkController.IconState(this.mAirplaneMode, TelephonyIcons.FLIGHT_MODE_ICON, R.string.accessibility_airplane_mode, this.mContext));
        cb.setNoSims(this.mHasNoSubs, this.mSimDetected);
        this.mWifiSignalController.notifyListeners(cb);
        this.mEthernetSignalController.notifyListeners(cb);
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            MobileSignalController mobileSignalController = this.mMobileSignalControllers.valueAt(i);
            mobileSignalController.notifyListeners(cb);
        }
        this.mCallbackHandler.setListening(cb, true);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.statusbar.policy.NetworkController, com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(NetworkController.SignalCallback cb) {
        this.mCallbackHandler.setListening(cb, false);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.android.systemui.statusbar.policy.NetworkControllerImpl$7] */
    @Override // com.android.systemui.statusbar.policy.NetworkController
    public void setWifiEnabled(final boolean enabled) {
        new AsyncTask<Void, Void, Void>() { // from class: com.android.systemui.statusbar.policy.NetworkControllerImpl.7
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... args) {
                NetworkControllerImpl.this.mWifiManager.setWifiEnabled(enabled);
                return null;
            }
        }.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUserSwitched(int newUserId) {
        this.mCurrentUserId = newUserId;
        this.mAccessPoints.onUserSwitched(newUserId);
        updateConnectivity();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        char c;
        if (CHATTY) {
            Log.d(TAG, "onReceive: intent=" + intent);
        }
        String action = intent.getAction();
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
            case 1:
                updateConnectivity();
                return;
            case 2:
                refreshLocale();
                updateAirplaneMode(false);
                return;
            case 3:
                recalculateEmergency();
                return;
            case 4:
                for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
                    MobileSignalController controller = this.mMobileSignalControllers.valueAt(i);
                    controller.handleBroadcast(intent);
                }
                this.mConfig = Config.readConfig(this.mContext);
                this.mReceiverHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$ybM43k5QVX_SxWbQACu1XwL3Knk
                    @Override // java.lang.Runnable
                    public final void run() {
                        NetworkControllerImpl.this.handleConfigurationChanged();
                    }
                });
                return;
            case 5:
                if (!intent.getBooleanExtra("rebroadcastOnUnlock", false)) {
                    updateMobileControllers();
                    return;
                }
                return;
            case 6:
                this.mLastServiceState = ServiceState.newFromBundle(intent.getExtras());
                if (this.mMobileSignalControllers.size() == 0) {
                    recalculateEmergency();
                    return;
                }
                return;
            case 7:
                this.mConfig = Config.readConfig(this.mContext);
                this.mReceiverHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$ybM43k5QVX_SxWbQACu1XwL3Knk
                    @Override // java.lang.Runnable
                    public final void run() {
                        NetworkControllerImpl.this.handleConfigurationChanged();
                    }
                });
                return;
            default:
                int subId = intent.getIntExtra("subscription", -1);
                if (SubscriptionManager.isValidSubscriptionId(subId)) {
                    if (this.mMobileSignalControllers.indexOfKey(subId) >= 0) {
                        this.mMobileSignalControllers.get(subId).handleBroadcast(intent);
                        return;
                    } else {
                        updateMobileControllers();
                        return;
                    }
                }
                this.mWifiSignalController.handleBroadcast(intent);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public void handleConfigurationChanged() {
        updateMobileControllers();
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            MobileSignalController controller = this.mMobileSignalControllers.valueAt(i);
            controller.setConfiguration(this.mConfig);
        }
        refreshLocale();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMobileControllers() {
        if (!this.mListening) {
            return;
        }
        doUpdateMobileControllers();
    }

    private void filterMobileSubscriptionInSameGroup(List<SubscriptionInfo> subscriptions) {
        if (subscriptions.size() == 2) {
            SubscriptionInfo info1 = subscriptions.get(0);
            SubscriptionInfo info2 = subscriptions.get(1);
            if (info1.getGroupUuid() != null && info1.getGroupUuid().equals(info2.getGroupUuid())) {
                if (info1.isOpportunistic() || info2.isOpportunistic()) {
                    boolean alwaysShowPrimary = CarrierConfigManager.getDefaultConfig().getBoolean("always_show_primary_signal_bar_in_opportunistic_network_boolean");
                    if (alwaysShowPrimary) {
                        subscriptions.remove(info1.isOpportunistic() ? info1 : info2);
                    } else {
                        subscriptions.remove(info1.getSubscriptionId() == this.mActiveMobileDataSubscription ? info2 : info1);
                    }
                }
            }
        }
    }

    @VisibleForTesting
    void doUpdateMobileControllers() {
        List<SubscriptionInfo> subscriptions = this.mSubscriptionManager.getActiveSubscriptionInfoList(false);
        if (subscriptions == null) {
            subscriptions = Collections.emptyList();
        }
        filterMobileSubscriptionInSameGroup(subscriptions);
        if (hasCorrectMobileControllers(subscriptions)) {
            updateNoSims();
            return;
        }
        synchronized (this.mLock) {
            setCurrentSubscriptionsLocked(subscriptions);
        }
        updateNoSims();
        recalculateEmergency();
    }

    @VisibleForTesting
    protected void updateNoSims() {
        boolean hasNoSubs = this.mHasMobileDataFeature && this.mMobileSignalControllers.size() == 0;
        boolean simDetected = hasAnySim();
        if (hasNoSubs != this.mHasNoSubs || simDetected != this.mSimDetected) {
            this.mHasNoSubs = hasNoSubs;
            this.mSimDetected = simDetected;
            this.mCallbackHandler.setNoSims(this.mHasNoSubs, this.mSimDetected);
        }
    }

    private boolean hasAnySim() {
        int simCount = this.mPhone.getSimCount();
        for (int i = 0; i < simCount; i++) {
            int state = this.mPhone.getSimState(i);
            if (state != 1 && state != 0) {
                return true;
            }
        }
        return false;
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void setCurrentSubscriptionsLocked(List<SubscriptionInfo> subscriptions) {
        Collections.sort(subscriptions, new Comparator<SubscriptionInfo>() { // from class: com.android.systemui.statusbar.policy.NetworkControllerImpl.8
            @Override // java.util.Comparator
            public int compare(SubscriptionInfo lhs, SubscriptionInfo rhs) {
                if (lhs.getSimSlotIndex() == rhs.getSimSlotIndex()) {
                    return lhs.getSubscriptionId() - rhs.getSubscriptionId();
                }
                return lhs.getSimSlotIndex() - rhs.getSimSlotIndex();
            }
        });
        this.mCurrentSubscriptions = subscriptions;
        SparseArray<MobileSignalController> cachedControllers = new SparseArray<>();
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            cachedControllers.put(this.mMobileSignalControllers.keyAt(i), this.mMobileSignalControllers.valueAt(i));
        }
        this.mMobileSignalControllers.clear();
        int num = subscriptions.size();
        for (int i2 = 0; i2 < num; i2++) {
            int subId = subscriptions.get(i2).getSubscriptionId();
            if (cachedControllers.indexOfKey(subId) >= 0) {
                this.mMobileSignalControllers.put(subId, cachedControllers.get(subId));
                cachedControllers.remove(subId);
            } else {
                MobileSignalController controller = new MobileSignalController(this.mContext, this.mConfig, this.mHasMobileDataFeature, this.mPhone.createForSubscriptionId(subId), this.mCallbackHandler, this, subscriptions.get(i2), this.mSubDefaults, this.mReceiverHandler.getLooper());
                controller.setUserSetupComplete(this.mUserSetup);
                this.mMobileSignalControllers.put(subId, controller);
                if (subscriptions.get(i2).getSimSlotIndex() == 0) {
                    this.mDefaultSignalController = controller;
                }
                if (this.mListening) {
                    controller.registerListener();
                }
            }
        }
        if (this.mListening) {
            for (int i3 = 0; i3 < cachedControllers.size(); i3++) {
                int key = cachedControllers.keyAt(i3);
                if (cachedControllers.get(key) == this.mDefaultSignalController) {
                    this.mDefaultSignalController = null;
                }
                cachedControllers.get(key).unregisterListener();
            }
        }
        this.mCallbackHandler.setSubs(subscriptions);
        notifyAllListeners();
        pushConnectivityToSignals();
        updateAirplaneMode(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setUserSetupComplete(final boolean userSetup) {
        this.mReceiverHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$NetworkControllerImpl$ip_KPuTyKF5u8IR4L3OPJ6WObYU
            @Override // java.lang.Runnable
            public final void run() {
                NetworkControllerImpl.this.lambda$setUserSetupComplete$0$NetworkControllerImpl(userSetup);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: handleSetUserSetupComplete */
    public void lambda$setUserSetupComplete$0$NetworkControllerImpl(boolean userSetup) {
        this.mUserSetup = userSetup;
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            MobileSignalController controller = this.mMobileSignalControllers.valueAt(i);
            controller.setUserSetupComplete(this.mUserSetup);
        }
    }

    @VisibleForTesting
    boolean hasCorrectMobileControllers(List<SubscriptionInfo> allSubscriptions) {
        if (allSubscriptions.size() != this.mMobileSignalControllers.size()) {
            return false;
        }
        for (SubscriptionInfo info : allSubscriptions) {
            if (this.mMobileSignalControllers.indexOfKey(info.getSubscriptionId()) < 0) {
                return false;
            }
        }
        return true;
    }

    private void updateAirplaneMode(boolean force) {
        boolean airplaneMode = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
        if (airplaneMode != this.mAirplaneMode || force) {
            this.mAirplaneMode = airplaneMode;
            for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
                MobileSignalController mobileSignalController = this.mMobileSignalControllers.valueAt(i);
                mobileSignalController.setAirplaneMode(this.mAirplaneMode);
            }
            notifyListeners();
        }
    }

    private void refreshLocale() {
        Locale current = this.mContext.getResources().getConfiguration().locale;
        if (!current.equals(this.mLocale)) {
            this.mLocale = current;
            this.mWifiSignalController.refreshLocale();
            notifyAllListeners();
        }
    }

    private void notifyAllListeners() {
        notifyListeners();
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            MobileSignalController mobileSignalController = this.mMobileSignalControllers.valueAt(i);
            mobileSignalController.notifyListeners();
        }
        this.mWifiSignalController.notifyListeners();
        this.mEthernetSignalController.notifyListeners();
    }

    private void notifyListeners() {
        this.mCallbackHandler.setIsAirplaneMode(new NetworkController.IconState(this.mAirplaneMode, TelephonyIcons.FLIGHT_MODE_ICON, R.string.accessibility_airplane_mode, this.mContext));
        this.mCallbackHandler.setNoSims(this.mHasNoSubs, this.mSimDetected);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConnectivity() {
        NetworkCapabilities[] defaultNetworkCapabilitiesForUser;
        int[] transportTypes;
        this.mConnectedTransports.clear();
        this.mValidatedTransports.clear();
        for (NetworkCapabilities nc : this.mConnectivityManager.getDefaultNetworkCapabilitiesForUser(this.mCurrentUserId)) {
            for (int transportType : nc.getTransportTypes()) {
                this.mConnectedTransports.set(transportType);
                if (nc.hasCapability(16)) {
                    this.mValidatedTransports.set(transportType);
                }
            }
        }
        if (CHATTY) {
            Log.d(TAG, "updateConnectivity: mConnectedTransports=" + this.mConnectedTransports);
            Log.d(TAG, "updateConnectivity: mValidatedTransports=" + this.mValidatedTransports);
        }
        this.mInetCondition = !this.mValidatedTransports.isEmpty();
        pushConnectivityToSignals();
    }

    private void pushConnectivityToSignals() {
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            MobileSignalController mobileSignalController = this.mMobileSignalControllers.valueAt(i);
            mobileSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
        }
        this.mWifiSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
        this.mEthernetSignalController.updateConnectivity(this.mConnectedTransports, this.mValidatedTransports);
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("NetworkController state:");
        pw.println("  - telephony ------");
        pw.print("  hasVoiceCallingFeature()=");
        pw.println(hasVoiceCallingFeature());
        pw.println("  - connectivity ------");
        pw.print("  mConnectedTransports=");
        pw.println(this.mConnectedTransports);
        pw.print("  mValidatedTransports=");
        pw.println(this.mValidatedTransports);
        pw.print("  mInetCondition=");
        pw.println(this.mInetCondition);
        pw.print("  mAirplaneMode=");
        pw.println(this.mAirplaneMode);
        pw.print("  mLocale=");
        pw.println(this.mLocale);
        pw.print("  mLastServiceState=");
        pw.println(this.mLastServiceState);
        pw.print("  mIsEmergency=");
        pw.println(this.mIsEmergency);
        pw.print("  mEmergencySource=");
        pw.println(emergencyToString(this.mEmergencySource));
        pw.println("  - config ------");
        pw.print("  patternOfCarrierSpecificDataIcon=");
        pw.println(this.mConfig.patternOfCarrierSpecificDataIcon);
        pw.print("  nr5GIconMap=");
        pw.println(this.mConfig.nr5GIconMap.toString());
        pw.print("  nrIconDisplayGracePeriodMs=");
        pw.println(this.mConfig.nrIconDisplayGracePeriodMs);
        for (int i = 0; i < this.mMobileSignalControllers.size(); i++) {
            MobileSignalController mobileSignalController = this.mMobileSignalControllers.valueAt(i);
            mobileSignalController.dump(pw);
        }
        this.mWifiSignalController.dump(pw);
        this.mEthernetSignalController.dump(pw);
        this.mAccessPoints.dump(pw);
    }

    private static final String emergencyToString(int emergencySource) {
        if (emergencySource > 300) {
            StringBuilder sb = new StringBuilder();
            sb.append("ASSUMED_VOICE_CONTROLLER(");
            sb.append(emergencySource - 200);
            sb.append(NavigationBarInflaterView.KEY_CODE_END);
            return sb.toString();
        } else if (emergencySource > 300) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("NO_SUB(");
            sb2.append(emergencySource - 300);
            sb2.append(NavigationBarInflaterView.KEY_CODE_END);
            return sb2.toString();
        } else if (emergencySource > 200) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("VOICE_CONTROLLER(");
            sb3.append(emergencySource - 200);
            sb3.append(NavigationBarInflaterView.KEY_CODE_END);
            return sb3.toString();
        } else if (emergencySource > 100) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("FIRST_CONTROLLER(");
            sb4.append(emergencySource - 100);
            sb4.append(NavigationBarInflaterView.KEY_CODE_END);
            return sb4.toString();
        } else if (emergencySource == 0) {
            return "NO_CONTROLLERS";
        } else {
            return "UNKNOWN_SOURCE";
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:223:0x042f  */
    /* JADX WARN: Removed duplicated region for block: B:230:0x0442  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x016a  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x0183  */
    @Override // com.android.systemui.DemoMode
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void dispatchDemoCommand(java.lang.String r26, android.os.Bundle r27) {
        /*
            Method dump skipped, instructions count: 1154
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.policy.NetworkControllerImpl.dispatchDemoCommand(java.lang.String, android.os.Bundle):void");
    }

    private SubscriptionInfo addSignalController(int id, int simSlotIndex) {
        SubscriptionInfo info = new SubscriptionInfo(id, "", simSlotIndex, "", "", 0, 0, "", 0, null, null, null, "", false, null, null);
        MobileSignalController controller = new MobileSignalController(this.mContext, this.mConfig, this.mHasMobileDataFeature, this.mPhone.createForSubscriptionId(info.getSubscriptionId()), this.mCallbackHandler, this, info, this.mSubDefaults, this.mReceiverHandler.getLooper());
        this.mMobileSignalControllers.put(id, controller);
        controller.getState().userSetup = true;
        return info;
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController
    public boolean hasEmergencyCryptKeeperText() {
        return EncryptionHelper.IS_DATA_ENCRYPTED;
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController
    public boolean isRadioOn() {
        return !this.mAirplaneMode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class SubListener extends SubscriptionManager.OnSubscriptionsChangedListener {
        private SubListener() {
        }

        /* synthetic */ SubListener(NetworkControllerImpl x0, AnonymousClass1 x1) {
            this();
        }

        @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
        public void onSubscriptionsChanged() {
            NetworkControllerImpl.this.updateMobileControllers();
        }
    }

    /* loaded from: classes21.dex */
    public static class SubscriptionDefaults {
        public int getDefaultVoiceSubId() {
            return SubscriptionManager.getDefaultVoiceSubscriptionId();
        }

        public int getDefaultDataSubId() {
            return SubscriptionManager.getDefaultDataSubscriptionId();
        }

        public int getActiveDataSubId() {
            return SubscriptionManager.getActiveDataSubscriptionId();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public static class Config {
        static final int NR_CONNECTED = 2;
        static final int NR_CONNECTED_MMWAVE = 1;
        static final int NR_NOT_RESTRICTED_RRC_CON = 4;
        static final int NR_NOT_RESTRICTED_RRC_IDLE = 3;
        static final int NR_RESTRICTED = 5;
        private static final Map<String, Integer> NR_STATUS_STRING_TO_INDEX = new HashMap(5);
        boolean hspaDataDistinguishable;
        public long nrIconDisplayGracePeriodMs;
        Map<Integer, MobileSignalController.MobileIconGroup> nr5GIconMap = new HashMap();
        boolean showAtLeast3G = false;
        boolean show4gFor3g = false;
        boolean alwaysShowCdmaRssi = false;
        boolean show4gForLte = false;
        boolean hideLtePlus = false;
        boolean inflateSignalStrengths = false;
        boolean alwaysShowDataRatIcon = false;
        public String patternOfCarrierSpecificDataIcon = "";

        Config() {
        }

        static {
            NR_STATUS_STRING_TO_INDEX.put("connected_mmwave", 1);
            NR_STATUS_STRING_TO_INDEX.put("connected", 2);
            NR_STATUS_STRING_TO_INDEX.put("not_restricted_rrc_idle", 3);
            NR_STATUS_STRING_TO_INDEX.put("not_restricted_rrc_con", 4);
            NR_STATUS_STRING_TO_INDEX.put("restricted", 5);
        }

        static Config readConfig(Context context) {
            Config config = new Config();
            Resources res = context.getResources();
            config.showAtLeast3G = res.getBoolean(R.bool.config_showMin3G);
            config.alwaysShowCdmaRssi = res.getBoolean(17891358);
            config.hspaDataDistinguishable = res.getBoolean(R.bool.config_hspa_data_distinguishable);
            config.inflateSignalStrengths = res.getBoolean(17891471);
            CarrierConfigManager configMgr = (CarrierConfigManager) context.getSystemService("carrier_config");
            SubscriptionManager.from(context);
            int defaultDataSubId = SubscriptionManager.getDefaultDataSubscriptionId();
            PersistableBundle b = configMgr.getConfigForSubId(defaultDataSubId);
            if (b != null) {
                config.alwaysShowDataRatIcon = b.getBoolean("always_show_data_rat_icon_bool");
                config.show4gForLte = b.getBoolean("show_4g_for_lte_data_icon_bool");
                config.show4gFor3g = b.getBoolean("show_4g_for_3g_data_icon_bool");
                config.hideLtePlus = b.getBoolean("hide_lte_plus_data_icon_bool");
                config.patternOfCarrierSpecificDataIcon = b.getString("show_carrier_data_icon_pattern_string");
                String nr5GIconConfiguration = b.getString("5g_icon_configuration_string");
                if (!TextUtils.isEmpty(nr5GIconConfiguration)) {
                    String[] nr5GIconConfigPairs = nr5GIconConfiguration.trim().split(",");
                    for (String pair : nr5GIconConfigPairs) {
                        add5GIconMapping(pair, config);
                    }
                }
                setDisplayGraceTime(b.getInt("5g_icon_display_grace_period_sec_int"), config);
            }
            return config;
        }

        @VisibleForTesting
        static void add5GIconMapping(String keyValuePair, Config config) {
            String[] kv = keyValuePair.trim().toLowerCase().split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
            if (kv.length != 2) {
                if (NetworkControllerImpl.DEBUG) {
                    Log.e(NetworkControllerImpl.TAG, "Invalid 5G icon configuration, config = " + keyValuePair);
                    return;
                }
                return;
            }
            String key = kv[0];
            String value = kv[1];
            if (!value.equals(SchedulerSupport.NONE) && NR_STATUS_STRING_TO_INDEX.containsKey(key) && TelephonyIcons.ICON_NAME_TO_ICON.containsKey(value)) {
                config.nr5GIconMap.put(NR_STATUS_STRING_TO_INDEX.get(key), TelephonyIcons.ICON_NAME_TO_ICON.get(value));
            }
        }

        @VisibleForTesting
        static void setDisplayGraceTime(int time, Config config) {
            config.nrIconDisplayGracePeriodMs = time * 1000;
        }
    }
}
