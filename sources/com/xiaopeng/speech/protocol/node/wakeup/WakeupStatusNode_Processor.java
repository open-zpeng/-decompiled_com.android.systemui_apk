package com.xiaopeng.speech.protocol.node.wakeup;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.jarvisproto.WakeupStatus;
/* loaded from: classes23.dex */
public class WakeupStatusNode_Processor implements ICommandProcessor {
    private WakeupStatusNode mTarget;

    public WakeupStatusNode_Processor(WakeupStatusNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        if (((event.hashCode() == -304962696 && event.equals(WakeupStatus.EVENT)) ? (char) 0 : (char) 65535) == 0) {
            this.mTarget.onWakeupStatusChanged(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{WakeupStatus.EVENT};
    }
}
