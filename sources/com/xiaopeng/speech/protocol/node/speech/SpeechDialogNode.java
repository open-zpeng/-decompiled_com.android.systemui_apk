package com.xiaopeng.speech.protocol.node.speech;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.SpeechUtils;
import com.xiaopeng.speech.protocol.event.SpeechDialogEvent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SpeechDialogNode extends SpeechNode<SpeechDialogListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.ROUTE_CLOSE_SPEECH_WINDOW)
    public void onCloseWindow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onCloseWindow(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.ROUTE_OPEN_SPEECH_WINDOW)
    public void onOpenWindow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onOpenWindow(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.ROUTE_OPEN_SCENE_GUIDE_WINDOW)
    public void onOpenSceneGuideWindow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onOpenSceneGuideWindow(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.ROUTE_CLOSE_SCENE_GUIDE_WINDOW)
    public void onCloseSceneGuideWindow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onCloseSceneGuideWindow(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.SWITCH_SPEECH_CONTINUOUS_ENABLE)
    public void onOpenSpeechSceneSetting(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onOpenSpeechSceneSetting();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.SWITCH_SPEECH_CONTINUOUS_DISABLED)
    public void onCloseSpeechSceneSetting(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onCloseSpeechSceneSetting();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.SYSTEM_LISTENTIME_ACCOMPANY_OPEN)
    public void onOpenSuperDialogue(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onOpenSuperDialogue();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.SYSTEM_LISTENTIME_ACCOMPANY_CLOSE)
    public void onCloseSuperDialogue(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onCloseSuperDialogue();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.REFRESH_CARSPEECHSERVICE_UI)
    public void onRefreshUi(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        int type = -1;
        boolean state = false;
        try {
            if (SpeechUtils.isJson(data)) {
                JSONObject json = new JSONObject(data);
                type = json.optInt(VuiConstants.ELEMENT_TYPE, -1);
                state = json.optBoolean("state", false);
            }
        } catch (Exception e) {
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onRefreshUi(type, state);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.GLOBAL_SPEECH_EXIT)
    public void onGlobalSpeechExit(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onGlobalSpeechExit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.SET_SCREEN_ON)
    public void setScreenOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            int display_location = -1;
            try {
                if (!TextUtils.isEmpty(data)) {
                    JSONObject json = new JSONObject(data);
                    display_location = json.optInt("display_location");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).setScreenOn(display_location);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.SCRIPT_WIDGET)
    public void onScriptWidget(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            String widget = "";
            try {
                if (!TextUtils.isEmpty(data)) {
                    JSONObject json = new JSONObject(data);
                    widget = json.optString("widget");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onScriptWidget(widget);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.SCRIPT_QUIT)
    public void onScriptQuit(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onScriptQuit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.SHOW_CHILDWATCH_LOCATION)
    public void showChildwatchLocation(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).showChildwatchLocation(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.SOUND_AREA_OPEN)
    public void onSoundAreaOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onSoundAreaOpen(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.SOUND_AREA_CLOSE)
    public void onSoundAreaClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).onSoundAreaClose(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.FAST_SPEECH_ON)
    public void openFastSpeech(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).openFastSpeech(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.FAST_SPEECH_OFF)
    public void closeFastSpeech(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).closeFastSpeech(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.MULTI_SPEECH_ON)
    public void openMultiSpeech(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).openMultiSpeech(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.MULTI_SPEECH_OFF)
    public void closeMultiSpeech(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).closeMultiSpeech(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.FULL_TIME_SPEECH_ON)
    public void openFullTimeSpeech(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).openFullTimeSpeech(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.FULL_TIME_SPEECH_OFF)
    public void closeFullTimeSpeech(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).closeFullTimeSpeech(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SpeechDialogEvent.INJECT_TRIGGER_WORDS)
    public void injectTriggerWords(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SpeechDialogListener) obj).injectTriggerWords(data);
            }
        }
    }
}
