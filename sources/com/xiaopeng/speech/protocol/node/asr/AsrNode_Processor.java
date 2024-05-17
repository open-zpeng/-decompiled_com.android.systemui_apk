package com.xiaopeng.speech.protocol.node.asr;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.jarvisproto.AsrEvent;
/* loaded from: classes23.dex */
public class AsrNode_Processor implements ICommandProcessor {
    private AsrNode mTarget;

    public AsrNode_Processor(AsrNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        if (((event.hashCode() == -1570875613 && event.equals(AsrEvent.EVENT)) ? (char) 0 : (char) 65535) == 0) {
            this.mTarget.onAsrEvent(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{AsrEvent.EVENT};
    }
}
