package com.xiaopeng.speech.protocol.node.error;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.ErrorEvent;
/* loaded from: classes23.dex */
public class ErrorNode_Processor implements ICommandProcessor {
    private ErrorNode mTarget;

    public ErrorNode_Processor(ErrorNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        if (((event.hashCode() == 780227936 && event.equals(ErrorEvent.ERROR_ASR_RESULT)) ? (char) 0 : (char) 65535) == 0) {
            this.mTarget.onErrorAsrResult(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{ErrorEvent.ERROR_ASR_RESULT};
    }
}
