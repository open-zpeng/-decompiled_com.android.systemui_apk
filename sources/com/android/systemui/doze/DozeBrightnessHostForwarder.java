package com.android.systemui.doze;

import com.android.systemui.doze.DozeMachine;
/* loaded from: classes21.dex */
public class DozeBrightnessHostForwarder extends DozeMachine.Service.Delegate {
    private final DozeHost mHost;

    public DozeBrightnessHostForwarder(DozeMachine.Service wrappedService, DozeHost host) {
        super(wrappedService);
        this.mHost = host;
    }

    @Override // com.android.systemui.doze.DozeMachine.Service.Delegate, com.android.systemui.doze.DozeMachine.Service
    public void setDozeScreenBrightness(int brightness) {
        super.setDozeScreenBrightness(brightness);
        this.mHost.setDozeScreenBrightness(brightness);
    }
}
