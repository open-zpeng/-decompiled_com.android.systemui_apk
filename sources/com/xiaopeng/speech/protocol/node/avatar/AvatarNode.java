package com.xiaopeng.speech.protocol.node.avatar;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.jarvisproto.AvatarExpressionEvent;
import com.xiaopeng.speech.jarvisproto.AvatarWakeupDisable;
import com.xiaopeng.speech.jarvisproto.AvatarWakeupEnable;
import com.xiaopeng.speech.jarvisproto.DMListening;
import com.xiaopeng.speech.jarvisproto.DMPersonalRun;
import com.xiaopeng.speech.jarvisproto.DMPersonalRunExit;
import com.xiaopeng.speech.jarvisproto.DMRecognized;
import com.xiaopeng.speech.jarvisproto.DMSpeaking;
import com.xiaopeng.speech.jarvisproto.DMUnderstanding;
import com.xiaopeng.speech.protocol.event.ContextEvent;
/* loaded from: classes23.dex */
public class AvatarNode extends SpeechNode<AvatarListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DMRecognized.EVENT)
    public void onSilence(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        DMRecognized dmRecognized = DMRecognized.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onSilence(dmRecognized);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DMListening.EVENT)
    public void onListening(String event, String data) {
        DMListening dmListening = DMListening.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onListening(dmListening);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DMSpeaking.EVENT)
    public void onSpeaking(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onSpeaking();
            }
        }
    }

    protected void onStandby(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onStandby();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DMUnderstanding.EVENT)
    public void onUnderstanding(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onUnderstanding();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DMPersonalRun.EVENT)
    public void onPersonalRunning(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onPersonalRun();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DMPersonalRunExit.EVENT)
    public void onPersonalExit(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onPersonalRunExit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AvatarExpressionEvent.EVENT)
    public void onAvatarExpression(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onAvatarExpression(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_CUSTOM)
    public void onWidgetCustom(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onWidgetCustom(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_TEXT)
    public void onWidgetText(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onWidgetText(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_LIST)
    public void onWidgetList(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onWidgetList(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_MEDIA)
    public void onWidgetMedia(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onWidgetMedia(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_CARD)
    public void onWidgetCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onWidgetCard(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_SEARCH)
    public void onWidgetSearch(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onWidgetSearch(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SpeechAnnotation(event = AvatarWakeupEnable.EVENT)
    public void onAvatarWakerupEnable(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onAvatarWakerupEnable(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SpeechAnnotation(event = AvatarWakeupDisable.EVENT)
    public void onAvatarWakerupDisable(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AvatarListener) obj).onAvatarWakerupDisable(data);
            }
        }
    }
}
