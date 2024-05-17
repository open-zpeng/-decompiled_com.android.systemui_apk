package com.xiaopeng.speech.protocol.node.tts;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.TtsEvent;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class TtsNode extends SpeechNode<TtsListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = TtsEvent.SPEECH_TTS_START)
    public void onTtsStart(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((TtsListener) obj).ttsStart(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = TtsEvent.SPEECH_TTS_END)
    public void onTtsEnd(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((TtsListener) obj).ttsEnd(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = TtsEvent.TTS_TIMBRE_SETTING)
    public void onTtsTimbreSetting(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        int type = 1;
        try {
            JSONObject json = new JSONObject(data);
            type = json.optInt("timbre_type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((TtsListener) obj).onTtsTimbreSetting(type);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = TtsEvent.SPEECH_TTS_ECHO)
    public void onTtsEcho(String event, String data) {
        TtsEchoValue echoValue = TtsEchoValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((TtsListener) obj).ttsEcho(echoValue);
            }
        }
    }
}
