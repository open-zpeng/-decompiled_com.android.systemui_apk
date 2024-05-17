package com.xiaopeng.speech.protocol.node.speech.carcontrol;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.SpeechCarControlCmdEvent;
/* loaded from: classes23.dex */
public class SpeechCarControlNode_Processor implements ICommandProcessor {
    private SpeechCarControlNode mTarget;

    public SpeechCarControlNode_Processor(SpeechCarControlNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode != -1772666300) {
            if (hashCode == 81725566 && event.equals(SpeechCarControlCmdEvent.OPEN_CAR_CONTROL_SOC)) {
                c = 1;
            }
            c = 65535;
        } else {
            if (event.equals(SpeechCarControlCmdEvent.CLOSE_CAR_CONTROL_SOC)) {
                c = 0;
            }
            c = 65535;
        }
        if (c == 0) {
            this.mTarget.onCloseSoc(event, data);
        } else if (c == 1) {
            this.mTarget.onOpenSoc(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{SpeechCarControlCmdEvent.CLOSE_CAR_CONTROL_SOC, SpeechCarControlCmdEvent.OPEN_CAR_CONTROL_SOC};
    }
}
