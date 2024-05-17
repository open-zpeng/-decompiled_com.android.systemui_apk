package com.xiaopeng.speech.protocol.node.media;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.MediaEvent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class MediaNode extends SpeechNode<MediaListener> {
    private static final String TAG = "MediaNode";

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.PLAY)
    public void onPlay(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            if (!TextUtils.isEmpty(data)) {
                JSONObject json = new JSONObject(data);
                String appName = json.optString(VuiConstants.ELEMENT_VALUE);
                if (listenerList != null) {
                    for (Object obj : listenerList) {
                        ((MediaListener) obj).onPlay(appName);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.PLAY_LOOP_SINGLE)
    public void onPlayLoopSingle(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaListener) obj).onPlayMode("single");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.PLAY_LOOP_ALL)
    public void onPlayLoopAll(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaListener) obj).onPlayMode("order");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.PLAY_LOOP_RANDOM)
    public void onPlayLoopRandom(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaListener) obj).onPlayMode("random");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.PAUSE)
    public void onPause(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaListener) obj).onPause();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.RESUME)
    public void onResume(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaListener) obj).onResume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.PREV)
    public void onPrev(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaListener) obj).onPrev();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.NEXT)
    public void onNext(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaListener) obj).onNext();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.STOP)
    public void onStop(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaListener) obj).onStop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.CONTROL_COLLECT)
    public void onControlCollect(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaListener) obj).onCollect();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = MediaEvent.CANCEL_COLLECT)
    public void onCancelCollect(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((MediaListener) obj).onCancelCollect();
            }
        }
    }

    @SpeechAnnotation(event = MediaEvent.FORWARD)
    public void onForward(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            if (!TextUtils.isEmpty(data)) {
                JSONObject json = new JSONObject(data);
                int second = json.optInt(VuiConstants.ELEMENT_VALUE);
                if (listenerList != null) {
                    for (Object obj : listenerList) {
                        ((MediaListener) obj).onForward(second);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SpeechAnnotation(event = MediaEvent.BACKWARD)
    public void onBackward(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            if (!TextUtils.isEmpty(data)) {
                JSONObject json = new JSONObject(data);
                int second = json.optInt(VuiConstants.ELEMENT_VALUE);
                if (listenerList != null) {
                    for (Object obj : listenerList) {
                        ((MediaListener) obj).onBackward(second);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SpeechAnnotation(event = MediaEvent.SETTIME)
    public void onSettime(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            if (!TextUtils.isEmpty(data)) {
                JSONObject json = new JSONObject(data);
                int second = json.optInt(VuiConstants.ELEMENT_VALUE);
                if (listenerList != null) {
                    for (Object obj : listenerList) {
                        ((MediaListener) obj).onSetTime(second);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
