package com.xiaopeng.speech.protocol.node.media;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.MediaEvent;
/* loaded from: classes23.dex */
public class MediaTriggerNode_Processor implements ICommandProcessor {
    private MediaTriggerNode mTarget;

    public MediaTriggerNode_Processor(MediaTriggerNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode != 531930234) {
            if (hashCode == 1008119320 && event.equals(MediaEvent.TRIGGER_MESSAGE_SUBMIT)) {
                c = 0;
            }
            c = 65535;
        } else {
            if (event.equals(MediaEvent.TRIGGER_MESSAGE_CANCEL)) {
                c = 1;
            }
            c = 65535;
        }
        if (c == 0) {
            this.mTarget.onSubmit(event, data);
        } else if (c == 1) {
            this.mTarget.onCancel(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{MediaEvent.TRIGGER_MESSAGE_SUBMIT, MediaEvent.TRIGGER_MESSAGE_CANCEL};
    }
}
