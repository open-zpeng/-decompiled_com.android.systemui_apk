package com.xiaopeng.speech.protocol.node.meter;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.MeterEvent;
/* loaded from: classes23.dex */
public class MeterNode_Processor implements ICommandProcessor {
    private MeterNode mTarget;

    public MeterNode_Processor(MeterNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode == -1943195853) {
            if (event.equals(MeterEvent.SET_RIGHTT_CARD)) {
                c = 1;
            }
            c = 65535;
        } else if (hashCode != -1464668575) {
            if (hashCode == 432832230 && event.equals(MeterEvent.SET_LEFT_CARD)) {
                c = 0;
            }
            c = 65535;
        } else {
            if (event.equals(MeterEvent.DASHBOARD_LIGHTS_STATUS)) {
                c = 2;
            }
            c = 65535;
        }
        if (c == 0) {
            this.mTarget.setLeftCard(event, data);
        } else if (c == 1) {
            this.mTarget.setRightCard(event, data);
        } else if (c == 2) {
            this.mTarget.onDashboardLightsStatus(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{MeterEvent.SET_LEFT_CARD, MeterEvent.SET_RIGHTT_CARD, MeterEvent.DASHBOARD_LIGHTS_STATUS};
    }
}
