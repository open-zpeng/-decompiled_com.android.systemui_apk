package com.xiaopeng.speech.protocol.node.custom;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.WakeupTestEvent;
/* loaded from: classes23.dex */
public class WakeupTestNode extends SpeechNode<WakeupTestListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = WakeupTestEvent.VAD_BEGIN)
    public void onVADBeginSpeech(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((WakeupTestListener) obj).onVADBeginSpeech();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = WakeupTestEvent.VAD_END)
    public void onVADEndSpeech(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((WakeupTestListener) obj).onVADEndSpeech();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = WakeupTestEvent.WAKEUP_SUCC)
    public void onWakeupSucced(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((WakeupTestListener) obj).onWakeupSucced(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = WakeupTestEvent.WAKEUP_FAILED)
    public void onWakeupFailed(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((WakeupTestListener) obj).onWakeupFailed(data);
            }
        }
    }
}
