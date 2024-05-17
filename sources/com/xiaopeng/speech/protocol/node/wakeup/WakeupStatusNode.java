package com.xiaopeng.speech.protocol.node.wakeup;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.jarvisproto.WakeupStatus;
/* loaded from: classes23.dex */
public class WakeupStatusNode extends SpeechNode<WakeupStatusListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = WakeupStatus.EVENT)
    public void onWakeupStatusChanged(String event, String data) {
        WakeupStatus wakeupStatus = WakeupStatus.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((WakeupStatusListener) obj).onWakeupStatusChanged(wakeupStatus.mStatus, wakeupStatus.mType, wakeupStatus.mInfo);
            }
        }
    }
}
