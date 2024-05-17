package com.xiaopeng.speech.protocol.node.oobe;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.ErrorEvent;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
/* loaded from: classes23.dex */
public class OOBENode extends SpeechNode<OOBEListener> {
    private static final String TAG = "OOBENode";

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = OOBEEvent.OOBE_RECORD_RESULT)
    public void onRecordResult(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((OOBEListener) obj).onRecordResult(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = OOBEEvent.OOBE_RECORD_INPUT)
    public void onRecordInput(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((OOBEListener) obj).onRecordInput(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = OOBEEvent.OOBE_ADDRESS_SET)
    public void onAddressSet(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((OOBEListener) obj).onAddressSet(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = OOBEEvent.OOBE_SKIP)
    public void onSkip(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((OOBEListener) obj).onSkip();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = OOBEEvent.OOBE_SEARCH_ERROR)
    public void onSearchError(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((OOBEListener) obj).onSearchError();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ErrorEvent.ERROR_ASR_RESULT)
    public void onNetError(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((OOBEListener) obj).onASRError();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = OOBEEvent.OOBE_ASR_ERROR)
    public void onASRError(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((OOBEListener) obj).onASRError();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = OOBEEvent.OOBE_OTHER_ERROR)
    public void onError(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((OOBEListener) obj).onError(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = OOBEEvent.OOBE_VOLUME)
    public void onVolumeChange(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((OOBEListener) obj).onVolumeChange(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = OOBEEvent.OOBE_NETWORK_ERROR)
    public void onNetWorkError(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((OOBEListener) obj).onNetWorkError();
            }
        }
    }
}
