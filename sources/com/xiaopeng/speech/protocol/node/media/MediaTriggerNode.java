package com.xiaopeng.speech.protocol.node.media;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.actorapi.SupportActor;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.MediaEvent;
import com.xiaopeng.speech.protocol.query.music.bean.PlayInfo;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class MediaTriggerNode extends SpeechNode<MediaTriggerListener> {
    private static final String TAG = "MediaTriggerNode";

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.TRIGGER_MESSAGE_SUBMIT)
    public void onSubmit(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            if (!TextUtils.isEmpty(data)) {
                JSONObject json = new JSONObject(data);
                json.optString(VuiConstants.ELEMENT_VALUE);
                if (listenerList != null) {
                    for (Object obj : listenerList) {
                        ((MediaTriggerListener) obj).onSubmit();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.TRIGGER_MESSAGE_CANCEL)
    public void onCancel(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaTriggerListener) obj).onCancel();
            }
        }
    }

    public void sendTrigger(String tts, String okText, String cancelText) {
        try {
            JSONObject data = new JSONObject();
            data.put("triggerID", PlayInfo.ERROR_CURRENT_NOT_PLAY);
            data.put("tts", tts);
            data.put("wordList", getWordList(okText, cancelText));
            data.put("ifMergeWords", false);
            data.put("repeatNum", 0);
            SpeechClient.instance().getAgent().sendApiRoute("media.message.trigger.send", data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leaveTrigger() {
        try {
            JSONObject json = new JSONObject();
            json.put("triggerID", PlayInfo.ERROR_CURRENT_NOT_PLAY);
            SpeechClient.instance().getAgent().sendApiRoute("media.message.trigger.leave", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getWordList(String ok_text, String cancel_text) {
        JSONObject resultObj = new JSONObject();
        try {
            if (!TextUtils.isEmpty(ok_text)) {
                String[] okArray = ok_text.split("\\|");
                for (String str : okArray) {
                    resultObj.put(str, getSubmitObj());
                }
            }
            if (!TextUtils.isEmpty(cancel_text)) {
                String[] cancelArray = cancel_text.split("\\|");
                for (String str2 : cancelArray) {
                    resultObj.put(str2, getCancelObj());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultObj;
    }

    private JSONObject getSubmitObj() {
        JSONObject json = new JSONObject();
        try {
            json.put("command", MediaEvent.TRIGGER_MESSAGE_SUBMIT);
            json.put(VuiConstants.ELEMENT_TYPE, "all");
            json.put("tts", SupportActor.TTS_DEFAULT_SUPPORT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private JSONObject getCancelObj() {
        JSONObject json = new JSONObject();
        try {
            json.put("command", MediaEvent.TRIGGER_MESSAGE_CANCEL);
            json.put(VuiConstants.ELEMENT_TYPE, "all");
            json.put("tts", SupportActor.TTS_DEFAULT_SUPPORT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
