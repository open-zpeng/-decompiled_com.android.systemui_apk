package com.xiaopeng.speech.protocol.node.custom;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.WakeupTestEvent;
/* loaded from: classes23.dex */
public class WakeupWordSettingNode extends SpeechNode<WakeupWordSettingListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = WakeupTestEvent.WAKEUP_WORD_MANUAL_INPUT)
    public void onManualInput(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((WakeupWordSettingListener) obj).onManualInput(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = WakeupTestEvent.WAKEUP_WORD_SETTING_DONE)
    public void onSettingDone(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((WakeupWordSettingListener) obj).onSettingDone(data);
            }
        }
    }
}
