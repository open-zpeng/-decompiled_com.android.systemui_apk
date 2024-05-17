package com.xiaopeng.speech.protocol.node.record;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.jarvisproto.AsrCloundResult;
import com.xiaopeng.speech.jarvisproto.RecordBegin;
import com.xiaopeng.speech.jarvisproto.RecordEnd;
import com.xiaopeng.speech.jarvisproto.RecordError;
import com.xiaopeng.speech.jarvisproto.RecordMaxLength;
import com.xiaopeng.speech.jarvisproto.SpeechBegin;
import com.xiaopeng.speech.jarvisproto.SpeechEnd;
import com.xiaopeng.speech.jarvisproto.SpeechVolume;
import com.xiaopeng.speech.protocol.node.record.bean.AsrResult;
import com.xiaopeng.speech.protocol.node.record.bean.RecordErrReason;
import com.xiaopeng.speech.protocol.node.record.bean.Volume;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class RecordNode extends SpeechNode<RecordListener> {
    @SpeechAnnotation(event = AsrCloundResult.EVENT)
    public void onAsrResult(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        AsrResult asrResult = AsrResult.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((RecordListener) obj).onAsrResult(asrResult);
            }
        }
    }

    @SpeechAnnotation(event = RecordBegin.EVENT)
    public void onRecordBegin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((RecordListener) obj).onRecordBegin();
            }
        }
    }

    @SpeechAnnotation(event = RecordEnd.EVENT)
    public void onRecordEnd(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean isStopRecord = false;
        try {
            JSONObject object = new JSONObject(data);
            isStopRecord = object.optBoolean("isStopRecord");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((RecordListener) obj).onRecordEnd(isStopRecord);
            }
        }
    }

    @SpeechAnnotation(event = RecordError.EVENT)
    public void onRecordError(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        RecordErrReason recordError = RecordErrReason.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((RecordListener) obj).onRecordError(recordError);
            }
        }
    }

    @SpeechAnnotation(event = SpeechBegin.EVENT)
    public void onSpeechBegin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((RecordListener) obj).onSpeechBegin();
            }
        }
    }

    @SpeechAnnotation(event = SpeechEnd.EVENT)
    public void onSpeechEnd(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((RecordListener) obj).onSpeechEnd();
            }
        }
    }

    @SpeechAnnotation(event = SpeechVolume.EVENT)
    public void onSpeechVolume(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        Volume volume = Volume.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((RecordListener) obj).onSpeechVolume(volume);
            }
        }
    }

    @SpeechAnnotation(event = RecordMaxLength.EVENT)
    public void onRecordMaxLength(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((RecordListener) obj).onRecordMaxLength();
            }
        }
    }
}
