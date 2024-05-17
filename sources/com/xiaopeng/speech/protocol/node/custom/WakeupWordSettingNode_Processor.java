package com.xiaopeng.speech.protocol.node.custom;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.WakeupTestEvent;
/* loaded from: classes23.dex */
public class WakeupWordSettingNode_Processor implements ICommandProcessor {
    private WakeupWordSettingNode mTarget;

    public WakeupWordSettingNode_Processor(WakeupWordSettingNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode != -1752205390) {
            if (hashCode == -376932620 && event.equals(WakeupTestEvent.WAKEUP_WORD_MANUAL_INPUT)) {
                c = 0;
            }
            c = 65535;
        } else {
            if (event.equals(WakeupTestEvent.WAKEUP_WORD_SETTING_DONE)) {
                c = 1;
            }
            c = 65535;
        }
        if (c == 0) {
            this.mTarget.onManualInput(event, data);
        } else if (c == 1) {
            this.mTarget.onSettingDone(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{WakeupTestEvent.WAKEUP_WORD_MANUAL_INPUT, WakeupTestEvent.WAKEUP_WORD_SETTING_DONE};
    }
}
