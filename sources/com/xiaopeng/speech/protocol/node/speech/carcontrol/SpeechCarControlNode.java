package com.xiaopeng.speech.protocol.node.speech.carcontrol;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.SpeechCarControlCmdEvent;
/* loaded from: classes23.dex */
public class SpeechCarControlNode extends SpeechNode<SpeechCarControlListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechCarControlCmdEvent.CLOSE_CAR_CONTROL_SOC)
    public void onCloseSoc(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechCarControlListener) obj).onCloseDriveMileIncrease();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechCarControlCmdEvent.OPEN_CAR_CONTROL_SOC)
    public void onOpenSoc(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechCarControlListener) obj).onOpenDriveMileIncrease();
            }
        }
    }

    protected void onRiseSpeaker(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechCarControlListener) obj).onOpenLoudspeaker();
            }
        }
    }
}
