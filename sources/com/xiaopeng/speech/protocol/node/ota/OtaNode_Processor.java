package com.xiaopeng.speech.protocol.node.ota;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.OtaEvent;
/* loaded from: classes23.dex */
public class OtaNode_Processor implements ICommandProcessor {
    private OtaNode mTarget;

    public OtaNode_Processor(OtaNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode != 478800373) {
            if (hashCode == 1009711693 && event.equals(OtaEvent.OTARESERVATION_PAGE_OPEN)) {
                c = 1;
            }
            c = 65535;
        } else {
            if (event.equals(OtaEvent.OTA_PAGE_OPEN)) {
                c = 0;
            }
            c = 65535;
        }
        if (c == 0) {
            this.mTarget.onOpenOtaPage(event, data);
        } else if (c == 1) {
            this.mTarget.onOpenReservationPage(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{OtaEvent.OTA_PAGE_OPEN, OtaEvent.OTARESERVATION_PAGE_OPEN};
    }
}
