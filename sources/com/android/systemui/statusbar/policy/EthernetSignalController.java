package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.SignalController;
import java.util.BitSet;
/* loaded from: classes21.dex */
public class EthernetSignalController extends SignalController<SignalController.State, SignalController.IconGroup> {
    public EthernetSignalController(Context context, CallbackHandler callbackHandler, NetworkControllerImpl networkController) {
        super("EthernetSignalController", context, 3, callbackHandler, networkController);
        T t = this.mCurrentState;
        T t2 = this.mLastState;
        SignalController.IconGroup iconGroup = new SignalController.IconGroup("Ethernet Icons", EthernetIcons.ETHERNET_ICONS, null, AccessibilityContentDescriptions.ETHERNET_CONNECTION_VALUES, 0, 0, 0, 0, AccessibilityContentDescriptions.ETHERNET_CONNECTION_VALUES[0]);
        t2.iconGroup = iconGroup;
        t.iconGroup = iconGroup;
    }

    @Override // com.android.systemui.statusbar.policy.SignalController
    public void updateConnectivity(BitSet connectedTransports, BitSet validatedTransports) {
        this.mCurrentState.connected = connectedTransports.get(this.mTransportType);
        super.updateConnectivity(connectedTransports, validatedTransports);
    }

    @Override // com.android.systemui.statusbar.policy.SignalController
    public void notifyListeners(NetworkController.SignalCallback callback) {
        boolean ethernetVisible = this.mCurrentState.connected;
        String contentDescription = getStringIfExists(getContentDescription()).toString();
        callback.setEthernetIndicators(new NetworkController.IconState(ethernetVisible, getCurrentIconId(), contentDescription));
    }

    @Override // com.android.systemui.statusbar.policy.SignalController
    public SignalController.State cleanState() {
        return new SignalController.State();
    }
}
