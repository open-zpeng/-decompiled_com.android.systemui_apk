package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.util.ArraySet;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.tuner.TunerService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
/* loaded from: classes21.dex */
public class StatusBarSignalPolicy implements NetworkController.SignalCallback, SecurityController.SecurityControllerCallback, TunerService.Tunable {
    private static final String TAG = "StatusBarSignalPolicy";
    private boolean mActivityEnabled;
    private boolean mBlockAirplane;
    private boolean mBlockEthernet;
    private boolean mBlockMobile;
    private boolean mBlockWifi;
    private final Context mContext;
    private boolean mForceBlockWifi;
    private final StatusBarIconController mIconController;
    private final String mSlotAirplane;
    private final String mSlotEthernet;
    private final String mSlotMobile;
    private final String mSlotVpn;
    private final String mSlotWifi;
    private final Handler mHandler = Handler.getMain();
    private boolean mIsAirplaneMode = false;
    private boolean mWifiVisible = false;
    private ArrayList<MobileIconState> mMobileStates = new ArrayList<>();
    private WifiIconState mWifiIconState = new WifiIconState();
    private final NetworkController mNetworkController = (NetworkController) Dependency.get(NetworkController.class);
    private final SecurityController mSecurityController = (SecurityController) Dependency.get(SecurityController.class);

    public StatusBarSignalPolicy(Context context, StatusBarIconController iconController) {
        this.mContext = context;
        this.mSlotAirplane = this.mContext.getString(17041078);
        this.mSlotMobile = this.mContext.getString(17041095);
        this.mSlotWifi = this.mContext.getString(17041109);
        this.mSlotEthernet = this.mContext.getString(17041088);
        this.mSlotVpn = this.mContext.getString(17041108);
        this.mActivityEnabled = this.mContext.getResources().getBoolean(R.bool.config_showActivity);
        this.mIconController = iconController;
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, StatusBarIconController.ICON_BLACKLIST);
        this.mNetworkController.addCallback((NetworkController.SignalCallback) this);
        this.mSecurityController.addCallback(this);
    }

    public void destroy() {
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
        this.mNetworkController.removeCallback((NetworkController.SignalCallback) this);
        this.mSecurityController.removeCallback(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVpn() {
        boolean vpnVisible = this.mSecurityController.isVpnEnabled();
        int vpnIconId = currentVpnIconId(this.mSecurityController.isVpnBranded());
        this.mIconController.setIcon(this.mSlotVpn, vpnIconId, this.mContext.getResources().getString(R.string.accessibility_vpn_on));
        this.mIconController.setIconVisibility(this.mSlotVpn, vpnVisible);
    }

    private int currentVpnIconId(boolean isBranded) {
        return isBranded ? R.drawable.stat_sys_branded_vpn : R.drawable.stat_sys_vpn_ic;
    }

    @Override // com.android.systemui.statusbar.policy.SecurityController.SecurityControllerCallback
    public void onStateChanged() {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarSignalPolicy$UsBELiDs0GJjQ8hYeagcWJmxhFc
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarSignalPolicy.this.updateVpn();
            }
        });
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        if (!StatusBarIconController.ICON_BLACKLIST.equals(key)) {
            return;
        }
        ArraySet<String> blockList = StatusBarIconController.getIconBlacklist(newValue);
        boolean blockAirplane = blockList.contains(this.mSlotAirplane);
        boolean blockMobile = blockList.contains(this.mSlotMobile);
        boolean blockWifi = blockList.contains(this.mSlotWifi);
        boolean blockEthernet = blockList.contains(this.mSlotEthernet);
        if (blockAirplane != this.mBlockAirplane || blockMobile != this.mBlockMobile || blockEthernet != this.mBlockEthernet || blockWifi != this.mBlockWifi) {
            this.mBlockAirplane = blockAirplane;
            this.mBlockMobile = blockMobile;
            this.mBlockEthernet = blockEthernet;
            this.mBlockWifi = blockWifi || this.mForceBlockWifi;
            this.mNetworkController.removeCallback((NetworkController.SignalCallback) this);
            this.mNetworkController.addCallback((NetworkController.SignalCallback) this);
        }
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setWifiIndicators(boolean enabled, NetworkController.IconState statusIcon, NetworkController.IconState qsIcon, boolean activityIn, boolean activityOut, String description, boolean isTransient, String statusLabel) {
        boolean z = true;
        boolean visible = statusIcon.visible && !this.mBlockWifi;
        boolean in = activityIn && this.mActivityEnabled && visible;
        boolean out = activityOut && this.mActivityEnabled && visible;
        WifiIconState newState = this.mWifiIconState.copy();
        newState.visible = visible;
        newState.resId = statusIcon.icon;
        newState.activityIn = in;
        newState.activityOut = out;
        newState.slot = this.mSlotWifi;
        newState.airplaneSpacerVisible = this.mIsAirplaneMode;
        newState.contentDescription = statusIcon.contentDescription;
        MobileIconState first = getFirstMobileState();
        if (first == null || first.typeId == 0) {
            z = false;
        }
        newState.signalSpacerVisible = z;
        updateWifiIconWithState(newState);
        this.mWifiIconState = newState;
    }

    private void updateShowWifiSignalSpacer(WifiIconState state) {
        MobileIconState first = getFirstMobileState();
        state.signalSpacerVisible = (first == null || first.typeId == 0) ? false : true;
    }

    private void updateWifiIconWithState(WifiIconState state) {
        if (state.visible && state.resId > 0) {
            this.mIconController.setSignalIcon(this.mSlotWifi, state);
            this.mIconController.setIconVisibility(this.mSlotWifi, true);
            return;
        }
        this.mIconController.setIconVisibility(this.mSlotWifi, false);
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setMobileDataIndicators(NetworkController.IconState statusIcon, NetworkController.IconState qsIcon, int statusType, int qsType, boolean activityIn, boolean activityOut, CharSequence typeContentDescription, CharSequence typeContentDescriptionHtml, CharSequence description, boolean isWide, int subId, boolean roaming) {
        MobileIconState state = getState(subId);
        if (state == null) {
            return;
        }
        boolean z = true;
        boolean typeChanged = statusType != state.typeId && (statusType == 0 || state.typeId == 0);
        state.visible = statusIcon.visible && !this.mBlockMobile;
        state.strengthId = statusIcon.icon;
        state.typeId = statusType;
        state.contentDescription = statusIcon.contentDescription;
        state.typeContentDescription = typeContentDescription;
        state.roaming = roaming;
        state.activityIn = activityIn && this.mActivityEnabled;
        if (!activityOut || !this.mActivityEnabled) {
            z = false;
        }
        state.activityOut = z;
        this.mIconController.setMobileIcons(this.mSlotMobile, MobileIconState.copyStates(this.mMobileStates));
        if (typeChanged) {
            WifiIconState wifiCopy = this.mWifiIconState.copy();
            updateShowWifiSignalSpacer(wifiCopy);
            if (!Objects.equals(wifiCopy, this.mWifiIconState)) {
                updateWifiIconWithState(wifiCopy);
                this.mWifiIconState = wifiCopy;
            }
        }
    }

    private MobileIconState getState(int subId) {
        Iterator<MobileIconState> it = this.mMobileStates.iterator();
        while (it.hasNext()) {
            MobileIconState state = it.next();
            if (state.subId == subId) {
                return state;
            }
        }
        Log.e(TAG, "Unexpected subscription " + subId);
        return null;
    }

    private MobileIconState getFirstMobileState() {
        if (this.mMobileStates.size() > 0) {
            return this.mMobileStates.get(0);
        }
        return null;
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setSubs(List<SubscriptionInfo> subs) {
        if (hasCorrectSubs(subs)) {
            return;
        }
        this.mIconController.removeAllIconsForSlot(this.mSlotMobile);
        this.mMobileStates.clear();
        int n = subs.size();
        for (int i = 0; i < n; i++) {
            this.mMobileStates.add(new MobileIconState(subs.get(i).getSubscriptionId()));
        }
    }

    private boolean hasCorrectSubs(List<SubscriptionInfo> subs) {
        int N = subs.size();
        if (N != this.mMobileStates.size()) {
            return false;
        }
        for (int i = 0; i < N; i++) {
            if (this.mMobileStates.get(i).subId != subs.get(i).getSubscriptionId()) {
                return false;
            }
        }
        return true;
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setNoSims(boolean show, boolean simDetected) {
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setEthernetIndicators(NetworkController.IconState state) {
        if (!state.visible || this.mBlockEthernet) {
        }
        int resId = state.icon;
        String description = state.contentDescription;
        if (resId > 0) {
            this.mIconController.setIcon(this.mSlotEthernet, resId, description);
            this.mIconController.setIconVisibility(this.mSlotEthernet, true);
            return;
        }
        this.mIconController.setIconVisibility(this.mSlotEthernet, false);
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setIsAirplaneMode(NetworkController.IconState icon) {
        this.mIsAirplaneMode = icon.visible && !this.mBlockAirplane;
        int resId = icon.icon;
        String description = icon.contentDescription;
        if (this.mIsAirplaneMode && resId > 0) {
            this.mIconController.setIcon(this.mSlotAirplane, resId, description);
            this.mIconController.setIconVisibility(this.mSlotAirplane, true);
            return;
        }
        this.mIconController.setIconVisibility(this.mSlotAirplane, false);
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setMobileDataEnabled(boolean enabled) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static abstract class SignalIconState {
        public boolean activityIn;
        public boolean activityOut;
        public String contentDescription;
        public String slot;
        public boolean visible;

        private SignalIconState() {
        }

        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SignalIconState that = (SignalIconState) o;
            return this.visible == that.visible && this.activityOut == that.activityOut && this.activityIn == that.activityIn && Objects.equals(this.contentDescription, that.contentDescription) && Objects.equals(this.slot, that.slot);
        }

        public int hashCode() {
            return Objects.hash(Boolean.valueOf(this.visible), Boolean.valueOf(this.activityOut), this.slot);
        }

        protected void copyTo(SignalIconState other) {
            other.visible = this.visible;
            other.activityIn = this.activityIn;
            other.activityOut = this.activityOut;
            other.slot = this.slot;
            other.contentDescription = this.contentDescription;
        }
    }

    /* loaded from: classes21.dex */
    public static class WifiIconState extends SignalIconState {
        public boolean airplaneSpacerVisible;
        public int resId;
        public boolean signalSpacerVisible;

        public WifiIconState() {
            super();
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarSignalPolicy.SignalIconState
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass() || !super.equals(o)) {
                return false;
            }
            WifiIconState that = (WifiIconState) o;
            return this.resId == that.resId && this.airplaneSpacerVisible == that.airplaneSpacerVisible && this.signalSpacerVisible == that.signalSpacerVisible;
        }

        public void copyTo(WifiIconState other) {
            super.copyTo((SignalIconState) other);
            other.resId = this.resId;
            other.airplaneSpacerVisible = this.airplaneSpacerVisible;
            other.signalSpacerVisible = this.signalSpacerVisible;
        }

        public WifiIconState copy() {
            WifiIconState newState = new WifiIconState();
            copyTo(newState);
            return newState;
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarSignalPolicy.SignalIconState
        public int hashCode() {
            return Objects.hash(Integer.valueOf(super.hashCode()), Integer.valueOf(this.resId), Boolean.valueOf(this.airplaneSpacerVisible), Boolean.valueOf(this.signalSpacerVisible));
        }

        public String toString() {
            return "WifiIconState(resId=" + this.resId + ", visible=" + this.visible + NavigationBarInflaterView.KEY_CODE_END;
        }
    }

    /* loaded from: classes21.dex */
    public static class MobileIconState extends SignalIconState {
        public boolean needsLeadingPadding;
        public boolean roaming;
        public int strengthId;
        public int subId;
        public CharSequence typeContentDescription;
        public int typeId;

        private MobileIconState(int subId) {
            super();
            this.subId = subId;
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarSignalPolicy.SignalIconState
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass() || !super.equals(o)) {
                return false;
            }
            MobileIconState that = (MobileIconState) o;
            return this.subId == that.subId && this.strengthId == that.strengthId && this.typeId == that.typeId && this.roaming == that.roaming && this.needsLeadingPadding == that.needsLeadingPadding && Objects.equals(this.typeContentDescription, that.typeContentDescription);
        }

        @Override // com.android.systemui.statusbar.phone.StatusBarSignalPolicy.SignalIconState
        public int hashCode() {
            return Objects.hash(Integer.valueOf(super.hashCode()), Integer.valueOf(this.subId), Integer.valueOf(this.strengthId), Integer.valueOf(this.typeId), Boolean.valueOf(this.roaming), Boolean.valueOf(this.needsLeadingPadding), this.typeContentDescription);
        }

        public MobileIconState copy() {
            MobileIconState copy = new MobileIconState(this.subId);
            copyTo(copy);
            return copy;
        }

        public void copyTo(MobileIconState other) {
            super.copyTo((SignalIconState) other);
            other.subId = this.subId;
            other.strengthId = this.strengthId;
            other.typeId = this.typeId;
            other.roaming = this.roaming;
            other.needsLeadingPadding = this.needsLeadingPadding;
            other.typeContentDescription = this.typeContentDescription;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static List<MobileIconState> copyStates(List<MobileIconState> inStates) {
            ArrayList<MobileIconState> outStates = new ArrayList<>();
            for (MobileIconState state : inStates) {
                MobileIconState copy = new MobileIconState(state.subId);
                state.copyTo(copy);
                outStates.add(copy);
            }
            return outStates;
        }

        public String toString() {
            return "MobileIconState(subId=" + this.subId + ", strengthId=" + this.strengthId + ", roaming=" + this.roaming + ", typeId=" + this.typeId + ", visible=" + this.visible + NavigationBarInflaterView.KEY_CODE_END;
        }
    }
}
