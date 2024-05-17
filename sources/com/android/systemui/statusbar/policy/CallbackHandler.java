package com.android.systemui.statusbar.policy;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SubscriptionInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.policy.NetworkController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public class CallbackHandler extends Handler implements NetworkController.EmergencyListener, NetworkController.SignalCallback {
    private static final int MSG_ADD_REMOVE_EMERGENCY = 6;
    private static final int MSG_ADD_REMOVE_SIGNAL = 7;
    private static final int MSG_AIRPLANE_MODE_CHANGED = 4;
    private static final int MSG_EMERGENCE_CHANGED = 0;
    private static final int MSG_ETHERNET_CHANGED = 3;
    private static final int MSG_MOBILE_DATA_ENABLED_CHANGED = 5;
    private static final int MSG_NO_SIM_VISIBLE_CHANGED = 2;
    private static final int MSG_SUBS_CHANGED = 1;
    private final ArrayList<NetworkController.EmergencyListener> mEmergencyListeners;
    private final ArrayList<NetworkController.SignalCallback> mSignalCallbacks;

    public CallbackHandler() {
        super(Looper.getMainLooper());
        this.mEmergencyListeners = new ArrayList<>();
        this.mSignalCallbacks = new ArrayList<>();
    }

    @VisibleForTesting
    CallbackHandler(Looper looper) {
        super(looper);
        this.mEmergencyListeners = new ArrayList<>();
        this.mSignalCallbacks = new ArrayList<>();
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                Iterator<NetworkController.EmergencyListener> it = this.mEmergencyListeners.iterator();
                while (it.hasNext()) {
                    NetworkController.EmergencyListener listener = it.next();
                    listener.setEmergencyCallsOnly(msg.arg1 != 0);
                }
                return;
            case 1:
                Iterator<NetworkController.SignalCallback> it2 = this.mSignalCallbacks.iterator();
                while (it2.hasNext()) {
                    NetworkController.SignalCallback signalCluster = it2.next();
                    signalCluster.setSubs((List) msg.obj);
                }
                return;
            case 2:
                Iterator<NetworkController.SignalCallback> it3 = this.mSignalCallbacks.iterator();
                while (it3.hasNext()) {
                    NetworkController.SignalCallback signalCluster2 = it3.next();
                    signalCluster2.setNoSims(msg.arg1 != 0, msg.arg2 != 0);
                }
                return;
            case 3:
                Iterator<NetworkController.SignalCallback> it4 = this.mSignalCallbacks.iterator();
                while (it4.hasNext()) {
                    NetworkController.SignalCallback signalCluster3 = it4.next();
                    signalCluster3.setEthernetIndicators((NetworkController.IconState) msg.obj);
                }
                return;
            case 4:
                Iterator<NetworkController.SignalCallback> it5 = this.mSignalCallbacks.iterator();
                while (it5.hasNext()) {
                    NetworkController.SignalCallback signalCluster4 = it5.next();
                    signalCluster4.setIsAirplaneMode((NetworkController.IconState) msg.obj);
                }
                return;
            case 5:
                Iterator<NetworkController.SignalCallback> it6 = this.mSignalCallbacks.iterator();
                while (it6.hasNext()) {
                    NetworkController.SignalCallback signalCluster5 = it6.next();
                    signalCluster5.setMobileDataEnabled(msg.arg1 != 0);
                }
                return;
            case 6:
                if (msg.arg1 != 0) {
                    this.mEmergencyListeners.add((NetworkController.EmergencyListener) msg.obj);
                    return;
                } else {
                    this.mEmergencyListeners.remove((NetworkController.EmergencyListener) msg.obj);
                    return;
                }
            case 7:
                if (msg.arg1 != 0) {
                    this.mSignalCallbacks.add((NetworkController.SignalCallback) msg.obj);
                    return;
                } else {
                    this.mSignalCallbacks.remove((NetworkController.SignalCallback) msg.obj);
                    return;
                }
            default:
                return;
        }
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setWifiIndicators(final boolean enabled, final NetworkController.IconState statusIcon, final NetworkController.IconState qsIcon, final boolean activityIn, final boolean activityOut, final String description, final boolean isTransient, final String secondaryLabel) {
        post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$CallbackHandler$BL9Oe1XlhjuRCIkE3XITv_5klDM
            @Override // java.lang.Runnable
            public final void run() {
                CallbackHandler.this.lambda$setWifiIndicators$0$CallbackHandler(enabled, statusIcon, qsIcon, activityIn, activityOut, description, isTransient, secondaryLabel);
            }
        });
    }

    public /* synthetic */ void lambda$setWifiIndicators$0$CallbackHandler(boolean enabled, NetworkController.IconState statusIcon, NetworkController.IconState qsIcon, boolean activityIn, boolean activityOut, String description, boolean isTransient, String secondaryLabel) {
        Iterator<NetworkController.SignalCallback> it = this.mSignalCallbacks.iterator();
        while (it.hasNext()) {
            NetworkController.SignalCallback callback = it.next();
            callback.setWifiIndicators(enabled, statusIcon, qsIcon, activityIn, activityOut, description, isTransient, secondaryLabel);
        }
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setMobileDataIndicators(final NetworkController.IconState statusIcon, final NetworkController.IconState qsIcon, final int statusType, final int qsType, final boolean activityIn, final boolean activityOut, final CharSequence typeContentDescription, final CharSequence typeContentDescriptionHtml, final CharSequence description, final boolean isWide, final int subId, final boolean roaming) {
        post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$CallbackHandler$uMnAccxpYS4aQwu2V03dAeAi978
            @Override // java.lang.Runnable
            public final void run() {
                CallbackHandler.this.lambda$setMobileDataIndicators$1$CallbackHandler(statusIcon, qsIcon, statusType, qsType, activityIn, activityOut, typeContentDescription, typeContentDescriptionHtml, description, isWide, subId, roaming);
            }
        });
    }

    public /* synthetic */ void lambda$setMobileDataIndicators$1$CallbackHandler(NetworkController.IconState statusIcon, NetworkController.IconState qsIcon, int statusType, int qsType, boolean activityIn, boolean activityOut, CharSequence typeContentDescription, CharSequence typeContentDescriptionHtml, CharSequence description, boolean isWide, int subId, boolean roaming) {
        Iterator<NetworkController.SignalCallback> it = this.mSignalCallbacks.iterator();
        while (it.hasNext()) {
            NetworkController.SignalCallback signalCluster = it.next();
            signalCluster.setMobileDataIndicators(statusIcon, qsIcon, statusType, qsType, activityIn, activityOut, typeContentDescription, typeContentDescriptionHtml, description, isWide, subId, roaming);
        }
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setSubs(List<SubscriptionInfo> subs) {
        obtainMessage(1, subs).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setNoSims(boolean show, boolean simDetected) {
        obtainMessage(2, show ? 1 : 0, simDetected ? 1 : 0).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setMobileDataEnabled(boolean enabled) {
        obtainMessage(5, enabled ? 1 : 0, 0).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.EmergencyListener
    public void setEmergencyCallsOnly(boolean emergencyOnly) {
        obtainMessage(0, emergencyOnly ? 1 : 0, 0).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setEthernetIndicators(NetworkController.IconState icon) {
        obtainMessage(3, icon).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setIsAirplaneMode(NetworkController.IconState icon) {
        obtainMessage(4, icon).sendToTarget();
    }

    public void setListening(NetworkController.EmergencyListener listener, boolean listening) {
        obtainMessage(6, listening ? 1 : 0, 0, listener).sendToTarget();
    }

    public void setListening(NetworkController.SignalCallback listener, boolean listening) {
        obtainMessage(7, listening ? 1 : 0, 0, listener).sendToTarget();
    }
}
