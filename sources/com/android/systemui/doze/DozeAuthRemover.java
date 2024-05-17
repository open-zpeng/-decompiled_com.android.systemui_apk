package com.android.systemui.doze;

import android.content.Context;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.doze.DozeMachine;
/* loaded from: classes21.dex */
public class DozeAuthRemover implements DozeMachine.Part {
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;

    public DozeAuthRemover(Context context) {
        this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(context);
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void transitionTo(DozeMachine.State oldState, DozeMachine.State newState) {
        if (newState == DozeMachine.State.DOZE || newState == DozeMachine.State.DOZE_AOD) {
            int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            if (this.mKeyguardUpdateMonitor.getUserUnlockedWithBiometric(currentUser)) {
                this.mKeyguardUpdateMonitor.clearBiometricRecognized();
            }
        }
    }
}
