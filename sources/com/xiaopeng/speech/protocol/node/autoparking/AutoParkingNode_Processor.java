package com.xiaopeng.speech.protocol.node.autoparking;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.AutoParkingEvent;
/* loaded from: classes23.dex */
public class AutoParkingNode_Processor implements ICommandProcessor {
    private AutoParkingNode mTarget;

    public AutoParkingNode_Processor(AutoParkingNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case 60390650:
                if (event.equals(AutoParkingEvent.PARK_CARPORT_COUNT)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 658676530:
                if (event.equals(AutoParkingEvent.EXIT)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 744299561:
                if (event.equals(AutoParkingEvent.MEMORY_PARKING_START)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 897056882:
                if (event.equals(AutoParkingEvent.PARK_START)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1563921671:
                if (event.equals(AutoParkingEvent.ACTIVATE)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            this.mTarget.onActivate(event, data);
        } else if (c == 1) {
            this.mTarget.onExit(event, data);
        } else if (c == 2) {
            this.mTarget.onParkStart(event, data);
        } else if (c == 3) {
            this.mTarget.onParkCarportCount(event, data);
        } else if (c == 4) {
            this.mTarget.onMemoryParkingStart(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{AutoParkingEvent.ACTIVATE, AutoParkingEvent.EXIT, AutoParkingEvent.PARK_START, AutoParkingEvent.PARK_CARPORT_COUNT, AutoParkingEvent.MEMORY_PARKING_START};
    }
}
