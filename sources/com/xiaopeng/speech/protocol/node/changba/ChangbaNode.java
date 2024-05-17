package com.xiaopeng.speech.protocol.node.changba;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.ChangbaEvent;
import com.xiaopeng.speech.protocol.node.changba.bean.ChangbaBean;
/* loaded from: classes23.dex */
public class ChangbaNode extends SpeechNode<ChangbaListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChangbaEvent.MUSIC_CHANGBA_SEARCH)
    public void onMusicChangbaSearch(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        ChangbaBean changbaBean = ChangbaBean.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChangbaListener) obj).onMusicChangbaSearch(changbaBean);
            }
        }
    }
}
