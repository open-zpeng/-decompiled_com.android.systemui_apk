package com.xiaopeng.speech.protocol.node.message;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.MessageEvent;
import com.xiaopeng.speech.protocol.node.message.bean.IndexBean;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class MessageNode extends SpeechNode<MessageListener> {
    public static final String EVENT = "jarvis.message.engine.status";

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MessageEvent.CANCEL)
    public void onCancel(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MessageListener) obj).onCancel();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MessageEvent.PARKING_SELECT)
    public void onParkingSelected(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        IndexBean indexBean = IndexBean.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MessageListener) obj).onParkingSelected(indexBean.getIndex());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MessageEvent.PATH_CHANGE)
    public void onPathChanged(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MessageListener) obj).onPathChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MessageEvent.MESSAGE_AI_TO_SPEECH)
    public void onAIMessage(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null && !TextUtils.isEmpty(data)) {
            int index = Integer.parseInt(data);
            for (Object obj : listenerList) {
                ((MessageListener) obj).onAIMessage(index);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MessageEvent.COMMON_MESSAGE_AI_TO_SPEECH)
    public void onCommonAIMessage(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MessageListener) obj).onCommonAIMessage(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MessageEvent.COMMON_MESSAGE_SUBMIT)
    public void onCommonSubmit(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MessageListener) obj).onCommonSubmit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MessageEvent.COMMON_MESSAGE_CANCEL)
    public void onCommonCancel(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MessageListener) obj).onCommonCancel();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MessageEvent.AI_MESSAGE_DISABLE)
    public void onAIMessageDisable(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MessageListener) obj).onAIMessageDisable();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MessageEvent.AI_MESSAGE_DISABLE_SEVEN_DAYS)
    public void onAIMessageDisableSevenDays(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MessageListener) obj).onAIMessageDisableSevenDays();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = EVENT)
    public void onHotWordEngineOK(String event, String data) {
        boolean isReady = getFromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MessageListener) obj).onHotWordEngineOK(isReady);
            }
        }
    }

    private boolean getFromJson(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            boolean isReady = jsonObject.optBoolean("isReady");
            return isReady;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
