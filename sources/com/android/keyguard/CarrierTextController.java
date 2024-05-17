package com.android.keyguard;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.android.internal.telephony.IccCardConstants;
import com.android.keyguard.CarrierTextController;
import com.android.settingslib.WirelessUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import java.util.List;
import java.util.Objects;
/* loaded from: classes19.dex */
public class CarrierTextController {
    private static final boolean DEBUG = false;
    private static final String TAG = "CarrierTextController";
    private CarrierTextCallback mCarrierTextCallback;
    private Context mContext;
    private final boolean mIsEmergencyCallCapable;
    @VisibleForTesting
    protected KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private CharSequence mSeparator;
    private boolean mShowAirplaneMode;
    private boolean mShowMissingSim;
    private boolean[] mSimErrorState;
    private final int mSimSlotsNumber;
    private boolean mTelephonyCapable;
    private WifiManager mWifiManager;
    private final WakefulnessLifecycle.Observer mWakefulnessObserver = new WakefulnessLifecycle.Observer() { // from class: com.android.keyguard.CarrierTextController.1
        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onFinishedWakingUp() {
            CarrierTextController.this.mCarrierTextCallback.finishedWakingUp();
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onStartedGoingToSleep() {
            CarrierTextController.this.mCarrierTextCallback.startedGoingToSleep();
        }
    };
    @VisibleForTesting
    protected final KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.keyguard.CarrierTextController.2
        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onRefreshCarrierInfo() {
            CarrierTextController.this.updateCarrierText();
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onTelephonyCapable(boolean capable) {
            CarrierTextController.this.mTelephonyCapable = capable;
            CarrierTextController.this.updateCarrierText();
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onSimStateChanged(int subId, int slotId, IccCardConstants.State simState) {
            if (slotId >= 0 && slotId < CarrierTextController.this.mSimSlotsNumber) {
                if (CarrierTextController.this.getStatusForIccState(simState) == StatusMode.SimIoError) {
                    CarrierTextController.this.mSimErrorState[slotId] = true;
                    CarrierTextController.this.updateCarrierText();
                    return;
                } else if (CarrierTextController.this.mSimErrorState[slotId]) {
                    CarrierTextController.this.mSimErrorState[slotId] = false;
                    CarrierTextController.this.updateCarrierText();
                    return;
                } else {
                    return;
                }
            }
            Log.d(CarrierTextController.TAG, "onSimStateChanged() - slotId invalid: " + slotId + " mTelephonyCapable: " + Boolean.toString(CarrierTextController.this.mTelephonyCapable));
        }
    };
    private int mActiveMobileDataSubscription = -1;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() { // from class: com.android.keyguard.CarrierTextController.3
        @Override // android.telephony.PhoneStateListener
        public void onActiveDataSubscriptionIdChanged(int subId) {
            CarrierTextController.this.mActiveMobileDataSubscription = subId;
            if (CarrierTextController.this.mKeyguardUpdateMonitor != null) {
                CarrierTextController.this.updateCarrierText();
            }
        }
    };
    private WakefulnessLifecycle mWakefulnessLifecycle = (WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes19.dex */
    public enum StatusMode {
        Normal,
        NetworkLocked,
        SimMissing,
        SimMissingLocked,
        SimPukLocked,
        SimLocked,
        SimPermDisabled,
        SimNotReady,
        SimIoError,
        SimUnknown
    }

    public CarrierTextController(Context context, CharSequence separator, boolean showAirplaneMode, boolean showMissingSim) {
        this.mContext = context;
        this.mIsEmergencyCallCapable = context.getResources().getBoolean(17891575);
        this.mShowAirplaneMode = showAirplaneMode;
        this.mShowMissingSim = showMissingSim;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mSeparator = separator;
        this.mSimSlotsNumber = ((TelephonyManager) context.getSystemService("phone")).getPhoneCount();
        this.mSimErrorState = new boolean[this.mSimSlotsNumber];
    }

    private CharSequence updateCarrierTextWithSimIoError(CharSequence text, CharSequence[] carrierNames, int[] subOrderBySlot, boolean noSims) {
        CharSequence carrierTextForSimIOError = getCarrierTextForSimState(IccCardConstants.State.CARD_IO_ERROR, "");
        int index = 0;
        while (true) {
            boolean[] zArr = this.mSimErrorState;
            if (index < zArr.length) {
                if (zArr[index]) {
                    if (noSims) {
                        return concatenate(carrierTextForSimIOError, getContext().getText(17039907), this.mSeparator);
                    }
                    if (subOrderBySlot[index] != -1) {
                        int subIndex = subOrderBySlot[index];
                        carrierNames[subIndex] = concatenate(carrierTextForSimIOError, carrierNames[subIndex], this.mSeparator);
                    } else {
                        text = concatenate(text, carrierTextForSimIOError, this.mSeparator);
                    }
                }
                index++;
            } else {
                return text;
            }
        }
    }

    public void setListening(CarrierTextCallback callback) {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        if (callback != null) {
            this.mCarrierTextCallback = callback;
            if (ConnectivityManager.from(this.mContext).isNetworkSupported(0)) {
                this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
                this.mKeyguardUpdateMonitor.registerCallback(this.mCallback);
                this.mWakefulnessLifecycle.addObserver(this.mWakefulnessObserver);
                telephonyManager.listen(this.mPhoneStateListener, 4194304);
                return;
            }
            this.mKeyguardUpdateMonitor = null;
            callback.updateCarrierInfo(new CarrierTextCallbackInfo("", null, false, null));
            return;
        }
        this.mCarrierTextCallback = null;
        KeyguardUpdateMonitor keyguardUpdateMonitor = this.mKeyguardUpdateMonitor;
        if (keyguardUpdateMonitor != null) {
            keyguardUpdateMonitor.removeCallback(this.mCallback);
            this.mWakefulnessLifecycle.removeObserver(this.mWakefulnessObserver);
        }
        telephonyManager.listen(this.mPhoneStateListener, 0);
    }

    protected List<SubscriptionInfo> getSubscriptionInfo() {
        return this.mKeyguardUpdateMonitor.getFilteredSubscriptionInfo(false);
    }

    protected void updateCarrierText() {
        boolean airplaneMode;
        boolean allSimsMissing;
        boolean allSimsMissing2 = true;
        boolean anySimReadyAndInService = false;
        CharSequence displayText = null;
        List<SubscriptionInfo> subs = getSubscriptionInfo();
        int numSubs = subs.size();
        int[] subsIds = new int[numSubs];
        int[] subOrderBySlot = new int[this.mSimSlotsNumber];
        for (int i = 0; i < this.mSimSlotsNumber; i++) {
            subOrderBySlot[i] = -1;
        }
        CharSequence[] carrierNames = new CharSequence[numSubs];
        int i2 = 0;
        while (i2 < numSubs) {
            int subId = subs.get(i2).getSubscriptionId();
            carrierNames[i2] = "";
            subsIds[i2] = subId;
            subOrderBySlot[subs.get(i2).getSimSlotIndex()] = i2;
            IccCardConstants.State simState = this.mKeyguardUpdateMonitor.getSimState(subId);
            CharSequence carrierName = subs.get(i2).getCarrierName();
            CharSequence carrierTextForSimState = getCarrierTextForSimState(simState, carrierName);
            if (carrierTextForSimState != null) {
                allSimsMissing2 = false;
                carrierNames[i2] = carrierTextForSimState;
            }
            if (simState != IccCardConstants.State.READY) {
                allSimsMissing = allSimsMissing2;
            } else {
                ServiceState ss = this.mKeyguardUpdateMonitor.mServiceStates.get(Integer.valueOf(subId));
                if (ss == null || ss.getDataRegState() != 0) {
                    allSimsMissing = allSimsMissing2;
                } else {
                    allSimsMissing = allSimsMissing2;
                    if (ss.getRilDataRadioTechnology() != 18 || (this.mWifiManager.isWifiEnabled() && this.mWifiManager.getConnectionInfo() != null && this.mWifiManager.getConnectionInfo().getBSSID() != null)) {
                        anySimReadyAndInService = true;
                    }
                }
            }
            i2++;
            allSimsMissing2 = allSimsMissing;
        }
        if (allSimsMissing2 && !anySimReadyAndInService) {
            if (numSubs != 0) {
                displayText = makeCarrierStringOnEmergencyCapable(getMissingSimMessage(), subs.get(0).getCarrierName());
            } else {
                CharSequence text = getContext().getText(17039907);
                Intent i3 = getContext().registerReceiver(null, new IntentFilter("android.provider.Telephony.SPN_STRINGS_UPDATED"));
                if (i3 != null) {
                    String spn = "";
                    String plmn = "";
                    if (i3.getBooleanExtra("showSpn", false)) {
                        spn = i3.getStringExtra("spn");
                    }
                    if (i3.getBooleanExtra("showPlmn", false)) {
                        plmn = i3.getStringExtra("plmn");
                    }
                    if (Objects.equals(plmn, spn)) {
                        text = plmn;
                    } else {
                        text = concatenate(plmn, spn, this.mSeparator);
                    }
                }
                String spn2 = getMissingSimMessage();
                displayText = makeCarrierStringOnEmergencyCapable(spn2, text);
            }
        }
        if (TextUtils.isEmpty(displayText)) {
            displayText = joinNotEmpty(this.mSeparator, carrierNames);
        }
        CharSequence displayText2 = updateCarrierTextWithSimIoError(displayText, carrierNames, subOrderBySlot, allSimsMissing2);
        if (!anySimReadyAndInService && WirelessUtils.isAirplaneModeOn(this.mContext)) {
            displayText2 = getAirplaneModeMessage();
            airplaneMode = true;
        } else {
            airplaneMode = false;
        }
        CarrierTextCallbackInfo info = new CarrierTextCallbackInfo(displayText2, carrierNames, allSimsMissing2 ? false : true, subsIds, airplaneMode);
        postToCallback(info);
    }

    @VisibleForTesting
    protected void postToCallback(final CarrierTextCallbackInfo info) {
        Handler handler = (Handler) Dependency.get(Dependency.MAIN_HANDLER);
        final CarrierTextCallback callback = this.mCarrierTextCallback;
        if (callback != null) {
            handler.post(new Runnable() { // from class: com.android.keyguard.-$$Lambda$CarrierTextController$Mi-Je6zX1bpo5TwEBp8HSL1qzz0
                @Override // java.lang.Runnable
                public final void run() {
                    CarrierTextController.CarrierTextCallback.this.updateCarrierInfo(info);
                }
            });
        }
    }

    private Context getContext() {
        return this.mContext;
    }

    private String getMissingSimMessage() {
        return (this.mShowMissingSim && this.mTelephonyCapable) ? getContext().getString(R.string.keyguard_missing_sim_message_short) : "";
    }

    private String getAirplaneModeMessage() {
        return this.mShowAirplaneMode ? getContext().getString(R.string.airplane_mode) : "";
    }

    private CharSequence getCarrierTextForSimState(IccCardConstants.State simState, CharSequence text) {
        StatusMode status = getStatusForIccState(simState);
        switch (status) {
            case Normal:
                return text;
            case SimNotReady:
                return "";
            case NetworkLocked:
                CharSequence carrierText = makeCarrierStringOnEmergencyCapable(this.mContext.getText(R.string.keyguard_network_locked_message), text);
                return carrierText;
            case SimMissing:
                return null;
            case SimPermDisabled:
                CharSequence carrierText2 = makeCarrierStringOnEmergencyCapable(getContext().getText(R.string.keyguard_permanent_disabled_sim_message_short), text);
                return carrierText2;
            case SimMissingLocked:
                return null;
            case SimLocked:
                CharSequence carrierText3 = makeCarrierStringOnLocked(getContext().getText(R.string.keyguard_sim_locked_message), text);
                return carrierText3;
            case SimPukLocked:
                CharSequence carrierText4 = makeCarrierStringOnLocked(getContext().getText(R.string.keyguard_sim_puk_locked_message), text);
                return carrierText4;
            case SimIoError:
                CharSequence carrierText5 = makeCarrierStringOnEmergencyCapable(getContext().getText(R.string.keyguard_sim_error_message_short), text);
                return carrierText5;
            case SimUnknown:
                return null;
            default:
                return null;
        }
    }

    private CharSequence makeCarrierStringOnEmergencyCapable(CharSequence simMessage, CharSequence emergencyCallMessage) {
        if (this.mIsEmergencyCallCapable) {
            return concatenate(simMessage, emergencyCallMessage, this.mSeparator);
        }
        return simMessage;
    }

    private CharSequence makeCarrierStringOnLocked(CharSequence simMessage, CharSequence carrierName) {
        boolean simMessageValid = !TextUtils.isEmpty(simMessage);
        boolean carrierNameValid = !TextUtils.isEmpty(carrierName);
        if (simMessageValid && carrierNameValid) {
            return this.mContext.getString(R.string.keyguard_carrier_name_with_sim_locked_template, carrierName, simMessage);
        }
        if (simMessageValid) {
            return simMessage;
        }
        if (carrierNameValid) {
            return carrierName;
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public StatusMode getStatusForIccState(IccCardConstants.State simState) {
        if (simState == null) {
            return StatusMode.Normal;
        }
        boolean missingAndNotProvisioned = !KeyguardUpdateMonitor.getInstance(this.mContext).isDeviceProvisioned() && (simState == IccCardConstants.State.ABSENT || simState == IccCardConstants.State.PERM_DISABLED);
        switch (AnonymousClass4.$SwitchMap$com$android$internal$telephony$IccCardConstants$State[(missingAndNotProvisioned ? IccCardConstants.State.NETWORK_LOCKED : simState).ordinal()]) {
            case 1:
                return StatusMode.SimMissing;
            case 2:
                return StatusMode.SimMissingLocked;
            case 3:
                return StatusMode.SimNotReady;
            case 4:
                return StatusMode.SimLocked;
            case 5:
                return StatusMode.SimPukLocked;
            case 6:
                return StatusMode.Normal;
            case 7:
                return StatusMode.SimPermDisabled;
            case 8:
                return StatusMode.SimUnknown;
            case 9:
                return StatusMode.SimIoError;
            default:
                return StatusMode.SimUnknown;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.keyguard.CarrierTextController$4  reason: invalid class name */
    /* loaded from: classes19.dex */
    public static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$IccCardConstants$State = new int[IccCardConstants.State.values().length];

        static {
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.ABSENT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.NETWORK_LOCKED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.NOT_READY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PIN_REQUIRED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PUK_REQUIRED.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.READY.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.PERM_DISABLED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.UNKNOWN.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$IccCardConstants$State[IccCardConstants.State.CARD_IO_ERROR.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode = new int[StatusMode.values().length];
            try {
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[StatusMode.Normal.ordinal()] = 1;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[StatusMode.SimNotReady.ordinal()] = 2;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[StatusMode.NetworkLocked.ordinal()] = 3;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[StatusMode.SimMissing.ordinal()] = 4;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[StatusMode.SimPermDisabled.ordinal()] = 5;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[StatusMode.SimMissingLocked.ordinal()] = 6;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[StatusMode.SimLocked.ordinal()] = 7;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[StatusMode.SimPukLocked.ordinal()] = 8;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[StatusMode.SimIoError.ordinal()] = 9;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[StatusMode.SimUnknown.ordinal()] = 10;
            } catch (NoSuchFieldError e19) {
            }
        }
    }

    private static CharSequence concatenate(CharSequence plmn, CharSequence spn, CharSequence separator) {
        boolean plmnValid = !TextUtils.isEmpty(plmn);
        boolean spnValid = !TextUtils.isEmpty(spn);
        if (plmnValid && spnValid) {
            StringBuilder sb = new StringBuilder();
            sb.append(plmn);
            sb.append(separator);
            sb.append(spn);
            return sb.toString();
        } else if (plmnValid) {
            return plmn;
        } else {
            if (spnValid) {
                return spn;
            }
            return "";
        }
    }

    private static CharSequence joinNotEmpty(CharSequence separator, CharSequence[] sequences) {
        int length = sequences.length;
        if (length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (!TextUtils.isEmpty(sequences[i])) {
                if (!TextUtils.isEmpty(sb)) {
                    sb.append(separator);
                }
                sb.append(sequences[i]);
            }
        }
        return sb.toString();
    }

    private static List<CharSequence> append(List<CharSequence> list, CharSequence string) {
        if (!TextUtils.isEmpty(string)) {
            list.add(string);
        }
        return list;
    }

    private CharSequence getCarrierHelpTextForSimState(IccCardConstants.State simState, String plmn, String spn) {
        int carrierHelpTextId = 0;
        StatusMode status = getStatusForIccState(simState);
        int i = AnonymousClass4.$SwitchMap$com$android$keyguard$CarrierTextController$StatusMode[status.ordinal()];
        if (i == 3) {
            carrierHelpTextId = R.string.keyguard_instructions_when_pattern_disabled;
        } else if (i == 4) {
            carrierHelpTextId = R.string.keyguard_missing_sim_instructions_long;
        } else if (i == 5) {
            carrierHelpTextId = R.string.keyguard_permanent_disabled_sim_instructions;
        } else if (i == 6) {
            carrierHelpTextId = R.string.keyguard_missing_sim_instructions;
        }
        return this.mContext.getText(carrierHelpTextId);
    }

    /* loaded from: classes19.dex */
    public static final class CarrierTextCallbackInfo {
        public boolean airplaneMode;
        public final boolean anySimReady;
        public final CharSequence carrierText;
        public final CharSequence[] listOfCarriers;
        public final int[] subscriptionIds;

        @VisibleForTesting
        public CarrierTextCallbackInfo(CharSequence carrierText, CharSequence[] listOfCarriers, boolean anySimReady, int[] subscriptionIds) {
            this(carrierText, listOfCarriers, anySimReady, subscriptionIds, false);
        }

        @VisibleForTesting
        public CarrierTextCallbackInfo(CharSequence carrierText, CharSequence[] listOfCarriers, boolean anySimReady, int[] subscriptionIds, boolean airplaneMode) {
            this.carrierText = carrierText;
            this.listOfCarriers = listOfCarriers;
            this.anySimReady = anySimReady;
            this.subscriptionIds = subscriptionIds;
            this.airplaneMode = airplaneMode;
        }
    }

    /* loaded from: classes19.dex */
    public interface CarrierTextCallback {
        default void updateCarrierInfo(CarrierTextCallbackInfo info) {
        }

        default void startedGoingToSleep() {
        }

        default void finishedWakingUp() {
        }
    }
}
