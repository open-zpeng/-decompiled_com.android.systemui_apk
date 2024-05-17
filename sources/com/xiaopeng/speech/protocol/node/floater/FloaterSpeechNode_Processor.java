package com.xiaopeng.speech.protocol.node.floater;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.FloaterSpeechEvent;
/* loaded from: classes23.dex */
public class FloaterSpeechNode_Processor implements ICommandProcessor {
    private FloaterSpeechNode mTarget;

    public FloaterSpeechNode_Processor(FloaterSpeechNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode == -1829252700) {
            if (event.equals(FloaterSpeechEvent.XIAOP_EXPRESSION)) {
                c = 2;
            }
            c = 65535;
        } else if (hashCode != 1493310260) {
            if (hashCode == 1920721242 && event.equals(FloaterSpeechEvent.CLOSE_WINDOW)) {
                c = 0;
            }
            c = 65535;
        } else {
            if (event.equals(FloaterSpeechEvent.SET_ANIM_STATE)) {
                c = 1;
            }
            c = 65535;
        }
        if (c == 0) {
            this.mTarget.onCloseWindow(event, data);
        } else if (c == 1) {
            this.mTarget.onSetAnimState(event, data);
        } else if (c == 2) {
            this.mTarget.onXiaopExpression(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{FloaterSpeechEvent.CLOSE_WINDOW, FloaterSpeechEvent.SET_ANIM_STATE, FloaterSpeechEvent.XIAOP_EXPRESSION};
    }
}
