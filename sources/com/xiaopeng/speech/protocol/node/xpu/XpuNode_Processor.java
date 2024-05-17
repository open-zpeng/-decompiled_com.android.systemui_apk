package com.xiaopeng.speech.protocol.node.xpu;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.XpuEvent;
/* loaded from: classes23.dex */
public class XpuNode_Processor implements ICommandProcessor {
    private XpuNode mTarget;

    public XpuNode_Processor(XpuNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        if (((event.hashCode() == -47958497 && event.equals(XpuEvent.XPU_VOICE_CHANGE_LANE)) ? (char) 0 : (char) 65535) == 0) {
            this.mTarget.laneChange(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{XpuEvent.XPU_VOICE_CHANGE_LANE};
    }
}
