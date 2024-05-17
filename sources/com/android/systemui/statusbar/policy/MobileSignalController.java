package com.android.systemui.statusbar.policy;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.Utils;
import com.android.settingslib.graph.SignalDrawable;
import com.android.settingslib.net.SignalStrengthUtil;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkControllerImpl;
import com.android.systemui.statusbar.policy.SignalController;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes21.dex */
public class MobileSignalController extends SignalController<MobileState, MobileIconGroup> {
    private static final int MSG_DISPLAY_GRACE = 1;
    private static final int NETWORK_TYPE_LTE_CA_5GE = 21;
    private NetworkControllerImpl.Config mConfig;
    private int mDataNetType;
    private int mDataState;
    private MobileIconGroup mDefaultIcons;
    private final NetworkControllerImpl.SubscriptionDefaults mDefaults;
    private final Handler mDisplayGraceHandler;
    @VisibleForTesting
    boolean mInflateSignalStrengths;
    @VisibleForTesting
    boolean mIsShowingIconGracefully;
    private final String mNetworkNameDefault;
    private final String mNetworkNameSeparator;
    final SparseArray<MobileIconGroup> mNetworkToIconLookup;
    private final ContentObserver mObserver;
    private final TelephonyManager mPhone;
    @VisibleForTesting
    final PhoneStateListener mPhoneStateListener;
    private ServiceState mServiceState;
    private SignalStrength mSignalStrength;
    final SubscriptionInfo mSubscriptionInfo;

    public MobileSignalController(Context context, NetworkControllerImpl.Config config, boolean hasMobileData, TelephonyManager phone, CallbackHandler callbackHandler, NetworkControllerImpl networkController, SubscriptionInfo info, NetworkControllerImpl.SubscriptionDefaults defaults, Looper receiverLooper) {
        super("MobileSignalController(" + info.getSubscriptionId() + NavigationBarInflaterView.KEY_CODE_END, context, 0, callbackHandler, networkController);
        this.mDataNetType = 0;
        this.mDataState = 0;
        this.mInflateSignalStrengths = false;
        this.mIsShowingIconGracefully = false;
        this.mNetworkToIconLookup = new SparseArray<>();
        this.mConfig = config;
        this.mPhone = phone;
        this.mDefaults = defaults;
        this.mSubscriptionInfo = info;
        this.mPhoneStateListener = new MobilePhoneStateListener(receiverLooper);
        this.mNetworkNameSeparator = getStringIfExists(R.string.status_bar_network_name_separator).toString();
        this.mNetworkNameDefault = getStringIfExists(17040257).toString();
        mapIconSets();
        String networkName = info.getCarrierName() != null ? info.getCarrierName().toString() : this.mNetworkNameDefault;
        ((MobileState) this.mCurrentState).networkName = networkName;
        ((MobileState) this.mLastState).networkName = networkName;
        ((MobileState) this.mCurrentState).networkNameData = networkName;
        ((MobileState) this.mLastState).networkNameData = networkName;
        ((MobileState) this.mCurrentState).enabled = hasMobileData;
        ((MobileState) this.mLastState).enabled = hasMobileData;
        MobileIconGroup mobileIconGroup = this.mDefaultIcons;
        ((MobileState) this.mCurrentState).iconGroup = mobileIconGroup;
        ((MobileState) this.mLastState).iconGroup = mobileIconGroup;
        updateDataSim();
        this.mObserver = new ContentObserver(new Handler(receiverLooper)) { // from class: com.android.systemui.statusbar.policy.MobileSignalController.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                MobileSignalController.this.updateTelephony();
            }
        };
        this.mDisplayGraceHandler = new Handler(receiverLooper) { // from class: com.android.systemui.statusbar.policy.MobileSignalController.2
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    MobileSignalController mobileSignalController = MobileSignalController.this;
                    mobileSignalController.mIsShowingIconGracefully = false;
                    mobileSignalController.updateTelephony();
                }
            }
        };
    }

    public void setConfiguration(NetworkControllerImpl.Config config) {
        this.mConfig = config;
        updateInflateSignalStrength();
        mapIconSets();
        updateTelephony();
    }

    public void setAirplaneMode(boolean airplaneMode) {
        ((MobileState) this.mCurrentState).airplaneMode = airplaneMode;
        notifyListenersIfNecessary();
    }

    public void setUserSetupComplete(boolean userSetup) {
        ((MobileState) this.mCurrentState).userSetup = userSetup;
        notifyListenersIfNecessary();
    }

    @Override // com.android.systemui.statusbar.policy.SignalController
    public void updateConnectivity(BitSet connectedTransports, BitSet validatedTransports) {
        boolean isValidated = validatedTransports.get(this.mTransportType);
        ((MobileState) this.mCurrentState).isDefault = connectedTransports.get(this.mTransportType);
        ((MobileState) this.mCurrentState).inetCondition = (isValidated || !((MobileState) this.mCurrentState).isDefault) ? 1 : 0;
        notifyListenersIfNecessary();
    }

    public void setCarrierNetworkChangeMode(boolean carrierNetworkChangeMode) {
        ((MobileState) this.mCurrentState).carrierNetworkChangeMode = carrierNetworkChangeMode;
        updateTelephony();
    }

    public void registerListener() {
        this.mPhone.listen(this.mPhoneStateListener, 4260321);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("mobile_data"), true, this.mObserver);
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.registerContentObserver(Settings.Global.getUriFor("mobile_data" + this.mSubscriptionInfo.getSubscriptionId()), true, this.mObserver);
    }

    public void unregisterListener() {
        this.mPhone.listen(this.mPhoneStateListener, 0);
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
    }

    private void mapIconSets() {
        this.mNetworkToIconLookup.clear();
        this.mNetworkToIconLookup.put(5, TelephonyIcons.THREE_G);
        this.mNetworkToIconLookup.put(6, TelephonyIcons.THREE_G);
        this.mNetworkToIconLookup.put(12, TelephonyIcons.THREE_G);
        this.mNetworkToIconLookup.put(14, TelephonyIcons.THREE_G);
        if (this.mConfig.show4gFor3g) {
            this.mNetworkToIconLookup.put(3, TelephonyIcons.FOUR_G);
        } else {
            this.mNetworkToIconLookup.put(3, TelephonyIcons.THREE_G);
        }
        this.mNetworkToIconLookup.put(17, TelephonyIcons.THREE_G);
        if (!this.mConfig.showAtLeast3G) {
            this.mNetworkToIconLookup.put(0, TelephonyIcons.UNKNOWN);
            this.mNetworkToIconLookup.put(2, TelephonyIcons.E);
            this.mNetworkToIconLookup.put(4, TelephonyIcons.ONE_X);
            this.mNetworkToIconLookup.put(7, TelephonyIcons.ONE_X);
            this.mDefaultIcons = TelephonyIcons.G;
        } else {
            this.mNetworkToIconLookup.put(0, TelephonyIcons.THREE_G);
            this.mNetworkToIconLookup.put(2, TelephonyIcons.THREE_G);
            this.mNetworkToIconLookup.put(4, TelephonyIcons.THREE_G);
            this.mNetworkToIconLookup.put(7, TelephonyIcons.THREE_G);
            this.mDefaultIcons = TelephonyIcons.THREE_G;
        }
        MobileIconGroup hGroup = TelephonyIcons.THREE_G;
        MobileIconGroup hPlusGroup = TelephonyIcons.THREE_G;
        if (this.mConfig.show4gFor3g) {
            hGroup = TelephonyIcons.FOUR_G;
            hPlusGroup = TelephonyIcons.FOUR_G;
        } else if (this.mConfig.hspaDataDistinguishable) {
            hGroup = TelephonyIcons.H;
            hPlusGroup = TelephonyIcons.H_PLUS;
        }
        this.mNetworkToIconLookup.put(8, hGroup);
        this.mNetworkToIconLookup.put(9, hGroup);
        this.mNetworkToIconLookup.put(10, hGroup);
        this.mNetworkToIconLookup.put(15, hPlusGroup);
        if (this.mConfig.show4gForLte) {
            this.mNetworkToIconLookup.put(13, TelephonyIcons.FOUR_G);
            if (this.mConfig.hideLtePlus) {
                this.mNetworkToIconLookup.put(19, TelephonyIcons.FOUR_G);
            } else {
                this.mNetworkToIconLookup.put(19, TelephonyIcons.FOUR_G_PLUS);
            }
        } else {
            this.mNetworkToIconLookup.put(13, TelephonyIcons.LTE);
            if (this.mConfig.hideLtePlus) {
                this.mNetworkToIconLookup.put(19, TelephonyIcons.LTE);
            } else {
                this.mNetworkToIconLookup.put(19, TelephonyIcons.LTE_PLUS);
            }
        }
        this.mNetworkToIconLookup.put(21, TelephonyIcons.LTE_CA_5G_E);
        this.mNetworkToIconLookup.put(18, TelephonyIcons.WFC);
    }

    private void updateInflateSignalStrength() {
        this.mInflateSignalStrengths = SignalStrengthUtil.shouldInflateSignalStrength(this.mContext, this.mSubscriptionInfo.getSubscriptionId());
    }

    private int getNumLevels() {
        if (this.mInflateSignalStrengths) {
            return 6;
        }
        return 5;
    }

    @Override // com.android.systemui.statusbar.policy.SignalController
    public int getCurrentIconId() {
        if (((MobileState) this.mCurrentState).iconGroup == TelephonyIcons.CARRIER_NETWORK_CHANGE) {
            return SignalDrawable.getCarrierChangeState(getNumLevels());
        }
        boolean cutOut = false;
        if (((MobileState) this.mCurrentState).connected) {
            int level = ((MobileState) this.mCurrentState).level;
            if (this.mInflateSignalStrengths) {
                level++;
            }
            boolean dataDisabled = ((MobileState) this.mCurrentState).userSetup && (((MobileState) this.mCurrentState).iconGroup == TelephonyIcons.DATA_DISABLED || (((MobileState) this.mCurrentState).iconGroup == TelephonyIcons.NOT_DEFAULT_DATA && ((MobileState) this.mCurrentState).defaultDataOff));
            boolean noInternet = ((MobileState) this.mCurrentState).inetCondition == 0;
            if (dataDisabled || noInternet) {
                cutOut = true;
            }
            return SignalDrawable.getState(level, getNumLevels(), cutOut);
        } else if (((MobileState) this.mCurrentState).enabled) {
            return SignalDrawable.getEmptyState(getNumLevels());
        } else {
            return 0;
        }
    }

    @Override // com.android.systemui.statusbar.policy.SignalController
    public int getQsCurrentIconId() {
        return getCurrentIconId();
    }

    @Override // com.android.systemui.statusbar.policy.SignalController
    public void notifyListeners(NetworkController.SignalCallback callback) {
        CharSequence dataContentDescription;
        int qsTypeIcon;
        NetworkController.IconState qsIcon;
        CharSequence description;
        MobileIconGroup icons = getIcons();
        String contentDescription = getStringIfExists(getContentDescription()).toString();
        CharSequence dataContentDescriptionHtml = getStringIfExists(icons.mDataContentDescription);
        CharSequence dataContentDescription2 = Html.fromHtml(dataContentDescriptionHtml.toString(), 0).toString();
        if (((MobileState) this.mCurrentState).inetCondition != 0) {
            dataContentDescription = dataContentDescription2;
        } else {
            CharSequence dataContentDescription3 = this.mContext.getString(R.string.data_connection_no_internet);
            dataContentDescription = dataContentDescription3;
        }
        boolean z = true;
        boolean dataDisabled = (((MobileState) this.mCurrentState).iconGroup == TelephonyIcons.DATA_DISABLED || ((MobileState) this.mCurrentState).iconGroup == TelephonyIcons.NOT_DEFAULT_DATA) && ((MobileState) this.mCurrentState).userSetup;
        boolean showDataIcon = ((MobileState) this.mCurrentState).dataConnected || dataDisabled;
        NetworkController.IconState statusIcon = new NetworkController.IconState(((MobileState) this.mCurrentState).enabled && !((MobileState) this.mCurrentState).airplaneMode, getCurrentIconId(), contentDescription);
        if (!((MobileState) this.mCurrentState).dataSim) {
            qsTypeIcon = 0;
            qsIcon = null;
            description = null;
        } else {
            int qsTypeIcon2 = (showDataIcon || this.mConfig.alwaysShowDataRatIcon) ? icons.mQsDataType : 0;
            NetworkController.IconState qsIcon2 = new NetworkController.IconState(((MobileState) this.mCurrentState).enabled && !((MobileState) this.mCurrentState).isEmergency, getQsCurrentIconId(), contentDescription);
            CharSequence description2 = ((MobileState) this.mCurrentState).isEmergency ? null : ((MobileState) this.mCurrentState).networkName;
            qsTypeIcon = qsTypeIcon2;
            qsIcon = qsIcon2;
            description = description2;
        }
        boolean activityIn = ((MobileState) this.mCurrentState).dataConnected && !((MobileState) this.mCurrentState).carrierNetworkChangeMode && ((MobileState) this.mCurrentState).activityIn;
        boolean activityOut = ((MobileState) this.mCurrentState).dataConnected && !((MobileState) this.mCurrentState).carrierNetworkChangeMode && ((MobileState) this.mCurrentState).activityOut;
        if (!((MobileState) this.mCurrentState).isDefault && !dataDisabled) {
            z = false;
        }
        int typeIcon = ((showDataIcon && z) || this.mConfig.alwaysShowDataRatIcon) ? icons.mDataType : 0;
        callback.setMobileDataIndicators(statusIcon, qsIcon, typeIcon, qsTypeIcon, activityIn, activityOut, dataContentDescription, dataContentDescriptionHtml, description, icons.mIsWide, this.mSubscriptionInfo.getSubscriptionId(), ((MobileState) this.mCurrentState).roaming);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.statusbar.policy.SignalController
    public MobileState cleanState() {
        return new MobileState();
    }

    private boolean isCdma() {
        SignalStrength signalStrength = this.mSignalStrength;
        return (signalStrength == null || signalStrength.isGsm()) ? false : true;
    }

    public boolean isEmergencyOnly() {
        ServiceState serviceState = this.mServiceState;
        return serviceState != null && serviceState.isEmergencyOnly();
    }

    private boolean isRoaming() {
        ServiceState serviceState;
        if (isCarrierNetworkChangeActive()) {
            return false;
        }
        if (isCdma() && (serviceState = this.mServiceState) != null) {
            int iconMode = serviceState.getCdmaEriIconMode();
            if (this.mServiceState.getCdmaEriIconIndex() != 1) {
                return iconMode == 0 || iconMode == 1;
            }
            return false;
        }
        ServiceState serviceState2 = this.mServiceState;
        return serviceState2 != null && serviceState2.getRoaming();
    }

    private boolean isCarrierNetworkChangeActive() {
        return ((MobileState) this.mCurrentState).carrierNetworkChangeMode;
    }

    public void handleBroadcast(Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.provider.Telephony.SPN_STRINGS_UPDATED")) {
            updateNetworkName(intent.getBooleanExtra("showSpn", false), intent.getStringExtra("spn"), intent.getStringExtra("spnData"), intent.getBooleanExtra("showPlmn", false), intent.getStringExtra("plmn"));
            notifyListenersIfNecessary();
        } else if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
            updateDataSim();
            notifyListenersIfNecessary();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDataSim() {
        int activeDataSubId = this.mDefaults.getActiveDataSubId();
        if (SubscriptionManager.isValidSubscriptionId(activeDataSubId)) {
            ((MobileState) this.mCurrentState).dataSim = activeDataSubId == this.mSubscriptionInfo.getSubscriptionId();
            return;
        }
        ((MobileState) this.mCurrentState).dataSim = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCarrierSpecificDataIcon() {
        if (this.mConfig.patternOfCarrierSpecificDataIcon == null || this.mConfig.patternOfCarrierSpecificDataIcon.length() == 0) {
            return false;
        }
        Pattern stringPattern = Pattern.compile(this.mConfig.patternOfCarrierSpecificDataIcon);
        String[] operatorNames = {this.mServiceState.getOperatorAlphaLongRaw(), this.mServiceState.getOperatorAlphaShortRaw()};
        for (String opName : operatorNames) {
            if (!TextUtils.isEmpty(opName)) {
                Matcher matcher = stringPattern.matcher(opName);
                if (matcher.find()) {
                    return true;
                }
            }
        }
        return false;
    }

    void updateNetworkName(boolean showSpn, String spn, String dataSpn, boolean showPlmn, String plmn) {
        if (CHATTY) {
            Log.d("CarrierLabel", "updateNetworkName showSpn=" + showSpn + " spn=" + spn + " dataSpn=" + dataSpn + " showPlmn=" + showPlmn + " plmn=" + plmn);
        }
        StringBuilder str = new StringBuilder();
        StringBuilder strData = new StringBuilder();
        if (showPlmn && plmn != null) {
            str.append(plmn);
            strData.append(plmn);
        }
        if (showSpn && spn != null) {
            if (str.length() != 0) {
                str.append(this.mNetworkNameSeparator);
            }
            str.append(spn);
        }
        if (str.length() != 0) {
            ((MobileState) this.mCurrentState).networkName = str.toString();
        } else {
            ((MobileState) this.mCurrentState).networkName = this.mNetworkNameDefault;
        }
        if (showSpn && dataSpn != null) {
            if (strData.length() != 0) {
                strData.append(this.mNetworkNameSeparator);
            }
            strData.append(dataSpn);
        }
        if (strData.length() != 0) {
            ((MobileState) this.mCurrentState).networkNameData = strData.toString();
            return;
        }
        ((MobileState) this.mCurrentState).networkNameData = this.mNetworkNameDefault;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateTelephony() {
        ServiceState serviceState;
        if (DEBUG) {
            Log.d(this.mTag, "updateTelephonySignalStrength: hasService=" + Utils.isInService(this.mServiceState) + " ss=" + this.mSignalStrength);
        }
        checkDefaultData();
        boolean z = true;
        ((MobileState) this.mCurrentState).connected = Utils.isInService(this.mServiceState) && this.mSignalStrength != null;
        if (((MobileState) this.mCurrentState).connected) {
            if (!this.mSignalStrength.isGsm() && this.mConfig.alwaysShowCdmaRssi) {
                ((MobileState) this.mCurrentState).level = this.mSignalStrength.getCdmaLevel();
            } else {
                ((MobileState) this.mCurrentState).level = this.mSignalStrength.getLevel();
            }
        }
        MobileIconGroup nr5GIconGroup = getNr5GIconGroup();
        if (this.mConfig.nrIconDisplayGracePeriodMs > 0) {
            nr5GIconGroup = adjustNr5GIconGroupByDisplayGraceTime(nr5GIconGroup);
        }
        if (nr5GIconGroup != null) {
            ((MobileState) this.mCurrentState).iconGroup = nr5GIconGroup;
        } else if (this.mNetworkToIconLookup.indexOfKey(this.mDataNetType) >= 0) {
            ((MobileState) this.mCurrentState).iconGroup = this.mNetworkToIconLookup.get(this.mDataNetType);
        } else {
            ((MobileState) this.mCurrentState).iconGroup = this.mDefaultIcons;
        }
        MobileState mobileState = (MobileState) this.mCurrentState;
        if (!((MobileState) this.mCurrentState).connected || this.mDataState != 2) {
            z = false;
        }
        mobileState.dataConnected = z;
        ((MobileState) this.mCurrentState).roaming = isRoaming();
        if (isCarrierNetworkChangeActive()) {
            ((MobileState) this.mCurrentState).iconGroup = TelephonyIcons.CARRIER_NETWORK_CHANGE;
        } else if (isDataDisabled() && !this.mConfig.alwaysShowDataRatIcon) {
            if (this.mSubscriptionInfo.getSubscriptionId() != this.mDefaults.getDefaultDataSubId()) {
                ((MobileState) this.mCurrentState).iconGroup = TelephonyIcons.NOT_DEFAULT_DATA;
            } else {
                ((MobileState) this.mCurrentState).iconGroup = TelephonyIcons.DATA_DISABLED;
            }
        }
        if (isEmergencyOnly() != ((MobileState) this.mCurrentState).isEmergency) {
            ((MobileState) this.mCurrentState).isEmergency = isEmergencyOnly();
            this.mNetworkController.recalculateEmergency();
        }
        if (((MobileState) this.mCurrentState).networkName.equals(this.mNetworkNameDefault) && (serviceState = this.mServiceState) != null && !TextUtils.isEmpty(serviceState.getOperatorAlphaShort())) {
            ((MobileState) this.mCurrentState).networkName = this.mServiceState.getOperatorAlphaShort();
        }
        if (((MobileState) this.mCurrentState).networkNameData.equals(this.mNetworkNameDefault) && this.mServiceState != null && ((MobileState) this.mCurrentState).dataSim && !TextUtils.isEmpty(this.mServiceState.getDataOperatorAlphaShort())) {
            ((MobileState) this.mCurrentState).networkNameData = this.mServiceState.getDataOperatorAlphaShort();
        }
        notifyListenersIfNecessary();
    }

    private void checkDefaultData() {
        if (((MobileState) this.mCurrentState).iconGroup != TelephonyIcons.NOT_DEFAULT_DATA) {
            ((MobileState) this.mCurrentState).defaultDataOff = false;
            return;
        }
        ((MobileState) this.mCurrentState).defaultDataOff = this.mNetworkController.isDataControllerDisabled();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onMobileDataChanged() {
        checkDefaultData();
        notifyListenersIfNecessary();
    }

    private MobileIconGroup getNr5GIconGroup() {
        ServiceState serviceState = this.mServiceState;
        if (serviceState == null) {
            return null;
        }
        int nrState = serviceState.getNrState();
        if (nrState == 3) {
            if (this.mServiceState.getNrFrequencyRange() == 4 && this.mConfig.nr5GIconMap.containsKey(1)) {
                return this.mConfig.nr5GIconMap.get(1);
            }
            if (this.mConfig.nr5GIconMap.containsKey(2)) {
                return this.mConfig.nr5GIconMap.get(2);
            }
        } else if (nrState == 2) {
            if (((MobileState) this.mCurrentState).activityDormant) {
                if (this.mConfig.nr5GIconMap.containsKey(3)) {
                    return this.mConfig.nr5GIconMap.get(3);
                }
            } else if (this.mConfig.nr5GIconMap.containsKey(4)) {
                return this.mConfig.nr5GIconMap.get(4);
            }
        } else if (nrState == 1 && this.mConfig.nr5GIconMap.containsKey(5)) {
            return this.mConfig.nr5GIconMap.get(5);
        }
        return null;
    }

    private MobileIconGroup adjustNr5GIconGroupByDisplayGraceTime(MobileIconGroup candidateIconGroup) {
        if (this.mIsShowingIconGracefully && candidateIconGroup == null) {
            return (MobileIconGroup) ((MobileState) this.mCurrentState).iconGroup;
        }
        if (!this.mIsShowingIconGracefully && candidateIconGroup != null && ((MobileState) this.mLastState).iconGroup != candidateIconGroup) {
            Handler handler = this.mDisplayGraceHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1), this.mConfig.nrIconDisplayGracePeriodMs);
            this.mIsShowingIconGracefully = true;
            return candidateIconGroup;
        } else if (!((MobileState) this.mCurrentState).connected || this.mDataState == 0 || candidateIconGroup == null) {
            this.mDisplayGraceHandler.removeMessages(1);
            this.mIsShowingIconGracefully = false;
            return null;
        } else {
            return candidateIconGroup;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDataDisabled() {
        return !this.mPhone.isDataCapable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public void setActivity(int activity) {
        ((MobileState) this.mCurrentState).activityIn = activity == 3 || activity == 1;
        ((MobileState) this.mCurrentState).activityOut = activity == 3 || activity == 2;
        ((MobileState) this.mCurrentState).activityDormant = activity == 4;
        notifyListenersIfNecessary();
    }

    @Override // com.android.systemui.statusbar.policy.SignalController
    public void dump(PrintWriter pw) {
        super.dump(pw);
        pw.println("  mSubscription=" + this.mSubscriptionInfo + ",");
        pw.println("  mServiceState=" + this.mServiceState + ",");
        pw.println("  mSignalStrength=" + this.mSignalStrength + ",");
        pw.println("  mDataState=" + this.mDataState + ",");
        pw.println("  mDataNetType=" + this.mDataNetType + ",");
        pw.println("  mInflateSignalStrengths=" + this.mInflateSignalStrengths + ",");
        pw.println("  isDataDisabled=" + isDataDisabled() + ",");
        pw.println("  mIsShowingIconGracefully=" + this.mIsShowingIconGracefully + ",");
    }

    /* loaded from: classes21.dex */
    class MobilePhoneStateListener extends PhoneStateListener {
        public MobilePhoneStateListener(Looper looper) {
            super(looper);
        }

        @Override // android.telephony.PhoneStateListener
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            String str;
            if (SignalController.DEBUG) {
                String str2 = MobileSignalController.this.mTag;
                StringBuilder sb = new StringBuilder();
                sb.append("onSignalStrengthsChanged signalStrength=");
                sb.append(signalStrength);
                if (signalStrength == null) {
                    str = "";
                } else {
                    str = " level=" + signalStrength.getLevel();
                }
                sb.append(str);
                Log.d(str2, sb.toString());
            }
            MobileSignalController.this.mSignalStrength = signalStrength;
            MobileSignalController.this.updateTelephony();
        }

        @Override // android.telephony.PhoneStateListener
        public void onServiceStateChanged(ServiceState state) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                Log.d(str, "onServiceStateChanged voiceState=" + state.getVoiceRegState() + " dataState=" + state.getDataRegState());
            }
            MobileSignalController.this.mServiceState = state;
            if (MobileSignalController.this.mServiceState != null) {
                updateDataNetType(MobileSignalController.this.mServiceState.getDataNetworkType());
            }
            MobileSignalController.this.updateTelephony();
        }

        @Override // android.telephony.PhoneStateListener
        public void onDataConnectionStateChanged(int state, int networkType) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                Log.d(str, "onDataConnectionStateChanged: state=" + state + " type=" + networkType);
            }
            MobileSignalController.this.mDataState = state;
            updateDataNetType(networkType);
            MobileSignalController.this.updateTelephony();
        }

        private void updateDataNetType(int networkType) {
            MobileSignalController.this.mDataNetType = networkType;
            if (MobileSignalController.this.mDataNetType == 13) {
                if (MobileSignalController.this.isCarrierSpecificDataIcon()) {
                    MobileSignalController.this.mDataNetType = 21;
                } else if (MobileSignalController.this.mServiceState != null && MobileSignalController.this.mServiceState.isUsingCarrierAggregation()) {
                    MobileSignalController.this.mDataNetType = 19;
                }
            }
        }

        @Override // android.telephony.PhoneStateListener
        public void onDataActivity(int direction) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                Log.d(str, "onDataActivity: direction=" + direction);
            }
            MobileSignalController.this.setActivity(direction);
        }

        public void onCarrierNetworkChange(boolean active) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                Log.d(str, "onCarrierNetworkChange: active=" + active);
            }
            ((MobileState) MobileSignalController.this.mCurrentState).carrierNetworkChangeMode = active;
            MobileSignalController.this.updateTelephony();
        }

        @Override // android.telephony.PhoneStateListener
        public void onActiveDataSubscriptionIdChanged(int subId) {
            if (SignalController.DEBUG) {
                String str = MobileSignalController.this.mTag;
                Log.d(str, "onActiveDataSubscriptionIdChanged: subId=" + subId);
            }
            MobileSignalController.this.updateDataSim();
            MobileSignalController.this.updateTelephony();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static class MobileIconGroup extends SignalController.IconGroup {
        final int mDataContentDescription;
        final int mDataType;
        final boolean mIsWide;
        final int mQsDataType;

        public MobileIconGroup(String name, int[][] sbIcons, int[][] qsIcons, int[] contentDesc, int sbNullState, int qsNullState, int sbDiscState, int qsDiscState, int discContentDesc, int dataContentDesc, int dataType, boolean isWide) {
            super(name, sbIcons, qsIcons, contentDesc, sbNullState, qsNullState, sbDiscState, qsDiscState, discContentDesc);
            this.mDataContentDescription = dataContentDesc;
            this.mDataType = dataType;
            this.mIsWide = isWide;
            this.mQsDataType = dataType;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static class MobileState extends SignalController.State {
        boolean airplaneMode;
        boolean carrierNetworkChangeMode;
        boolean dataConnected;
        boolean dataSim;
        boolean defaultDataOff;
        boolean isDefault;
        boolean isEmergency;
        String networkName;
        String networkNameData;
        boolean roaming;
        boolean userSetup;

        MobileState() {
        }

        @Override // com.android.systemui.statusbar.policy.SignalController.State
        public void copyFrom(SignalController.State s) {
            super.copyFrom(s);
            MobileState state = (MobileState) s;
            this.dataSim = state.dataSim;
            this.networkName = state.networkName;
            this.networkNameData = state.networkNameData;
            this.dataConnected = state.dataConnected;
            this.isDefault = state.isDefault;
            this.isEmergency = state.isEmergency;
            this.airplaneMode = state.airplaneMode;
            this.carrierNetworkChangeMode = state.carrierNetworkChangeMode;
            this.userSetup = state.userSetup;
            this.roaming = state.roaming;
            this.defaultDataOff = state.defaultDataOff;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.systemui.statusbar.policy.SignalController.State
        public void toString(StringBuilder builder) {
            super.toString(builder);
            builder.append(',');
            builder.append("dataSim=");
            builder.append(this.dataSim);
            builder.append(',');
            builder.append("networkName=");
            builder.append(this.networkName);
            builder.append(',');
            builder.append("networkNameData=");
            builder.append(this.networkNameData);
            builder.append(',');
            builder.append("dataConnected=");
            builder.append(this.dataConnected);
            builder.append(',');
            builder.append("roaming=");
            builder.append(this.roaming);
            builder.append(',');
            builder.append("isDefault=");
            builder.append(this.isDefault);
            builder.append(',');
            builder.append("isEmergency=");
            builder.append(this.isEmergency);
            builder.append(',');
            builder.append("airplaneMode=");
            builder.append(this.airplaneMode);
            builder.append(',');
            builder.append("carrierNetworkChangeMode=");
            builder.append(this.carrierNetworkChangeMode);
            builder.append(',');
            builder.append("userSetup=");
            builder.append(this.userSetup);
            builder.append(',');
            builder.append("defaultDataOff=");
            builder.append(this.defaultDataOff);
        }

        @Override // com.android.systemui.statusbar.policy.SignalController.State
        public boolean equals(Object o) {
            return super.equals(o) && Objects.equals(((MobileState) o).networkName, this.networkName) && Objects.equals(((MobileState) o).networkNameData, this.networkNameData) && ((MobileState) o).dataSim == this.dataSim && ((MobileState) o).dataConnected == this.dataConnected && ((MobileState) o).isEmergency == this.isEmergency && ((MobileState) o).airplaneMode == this.airplaneMode && ((MobileState) o).carrierNetworkChangeMode == this.carrierNetworkChangeMode && ((MobileState) o).userSetup == this.userSetup && ((MobileState) o).isDefault == this.isDefault && ((MobileState) o).roaming == this.roaming && ((MobileState) o).defaultDataOff == this.defaultDataOff;
        }
    }
}
