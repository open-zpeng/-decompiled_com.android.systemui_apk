package com.xiaopeng.speech.protocol.query.speech.ui;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechQueryEvent;
/* loaded from: classes23.dex */
public class SpeechUiQuery_Processor implements IQueryProcessor {
    private SpeechUiQuery mTarget;

    public SpeechUiQuery_Processor(SpeechUiQuery target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode != -1584266840) {
            if (hashCode == -1417712984 && event.equals(SpeechQueryEvent.IS_SUPERDIALOGUE_OPENED)) {
                c = 1;
            }
            c = 65535;
        } else {
            if (event.equals(SpeechQueryEvent.IS_SUPERDIALOGUE_WHITELIST)) {
                c = 0;
            }
            c = 65535;
        }
        if (c != 0) {
            if (c == 1) {
                return Boolean.valueOf(this.mTarget.isSuperDialogueOpened(event, data));
            }
            return null;
        }
        return Boolean.valueOf(this.mTarget.isSuperDialogueWhitelist(event, data));
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechQueryEvent.IS_SUPERDIALOGUE_WHITELIST, SpeechQueryEvent.IS_SUPERDIALOGUE_OPENED};
    }
}
