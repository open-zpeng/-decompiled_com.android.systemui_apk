package com.xiaopeng.speech.protocol.query.speech.ui;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechQueryEvent;
/* loaded from: classes23.dex */
public class SpeechUiQuery extends SpeechQuery<ISpeechUiCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.IS_SUPERDIALOGUE_WHITELIST)
    public boolean isSuperDialogueWhitelist(String event, String data) {
        return ((ISpeechUiCaller) this.mQueryCaller).isSuperDialogueWhitelist();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.IS_SUPERDIALOGUE_OPENED)
    public boolean isSuperDialogueOpened(String event, String data) {
        return ((ISpeechUiCaller) this.mQueryCaller).isSuperDialogueOpened();
    }
}
