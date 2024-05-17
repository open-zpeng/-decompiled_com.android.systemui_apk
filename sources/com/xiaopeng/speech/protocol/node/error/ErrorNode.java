package com.xiaopeng.speech.protocol.node.error;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.ErrorEvent;
/* loaded from: classes23.dex */
public class ErrorNode extends SpeechNode<IErrorListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ErrorEvent.ERROR_ASR_RESULT)
    public void onErrorAsrResult(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((IErrorListener) obj).onErrorAsrResult();
            }
        }
    }
}
