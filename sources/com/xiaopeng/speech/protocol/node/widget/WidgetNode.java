package com.xiaopeng.speech.protocol.node.widget;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.WidgetEvent;
/* loaded from: classes23.dex */
public class WidgetNode extends SpeechNode<WidgetListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = WidgetEvent.AC_WIDGET_ON)
    public void onAcWidgetOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((WidgetListener) obj).onAcWidgetOn();
            }
        }
    }
}
