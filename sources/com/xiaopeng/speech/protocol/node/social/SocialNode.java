package com.xiaopeng.speech.protocol.node.social;

import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.jarvisproto.BackButtonClick;
import com.xiaopeng.speech.jarvisproto.VoiceButtonClick;
import com.xiaopeng.speech.protocol.event.SocialEvent;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SocialNode extends SpeechNode<SocialListener> {
    public static final String GROUP_MESSAGE_INTENT = "播放群内容";
    public static final String JOIN_GROUP_INTENT = "加入鹏窝";
    private static final String LBS_SOCAIL_TASK = "LBS社交";
    private static final String OFFLINE_SKILL = "命令词";
    public static final String QUERY_SEND_MESSAGE = "发送消息";
    public static final String QUERY_SET_VOICE_BUTTON = "设置方向盘按钮";

    @SpeechAnnotation(event = SocialEvent.SOCIAL_MOTORCADE_OPEN)
    public void onSocialMotorcadeOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SocialListener) obj).onSocialMotorcadeOpen();
            }
        }
    }

    @SpeechAnnotation(event = SocialEvent.SOCIAL_MOTORCADE_CLOSE)
    public void onSocialMotorcadeClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SocialListener) obj).onSocialMotorcadeClose();
            }
        }
    }

    @SpeechAnnotation(event = SocialEvent.SOCIAL_GRAB_MIC)
    public void onSocialGrabMic(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SocialListener) obj).onSocialGrabMic();
            }
        }
    }

    @SpeechAnnotation(event = SocialEvent.SOCIAL_GRAB_MIC_CANCEL)
    public void onSocialGrabMicCancel(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SocialListener) obj).onSocialGrabMicCancel();
            }
        }
    }

    @SpeechAnnotation(event = SocialEvent.SOCIAL_CREATE_TOPIC)
    public void onSocialCreateTopic(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SocialListener) obj).onSocialCreateTopic();
            }
        }
    }

    @SpeechAnnotation(event = SocialEvent.SOCIAL_REPLY_TOPIC)
    public void onSocialReplyTopic(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SocialListener) obj).onSocialReplyTopic();
            }
        }
    }

    @SpeechAnnotation(event = SocialEvent.SOCIAL_QUIT_CHAT)
    public void onSocialQuitChat(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SocialListener) obj).onSocialQuitChat();
            }
        }
    }

    @SpeechAnnotation(event = SocialEvent.SOCIAL_CONFIRM)
    public void onSocialConfirm(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        String intent = "";
        try {
            JSONObject obj = new JSONObject(data);
            intent = obj.optString("intent");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj2 : listenerList) {
                ((SocialListener) obj2).onSocialConfirm(intent);
            }
        }
    }

    @SpeechAnnotation(event = SocialEvent.SOCIAL_CANCEL)
    public void onSocialCancel(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        String intent = "";
        try {
            JSONObject obj = new JSONObject(data);
            intent = obj.optString("intent");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj2 : listenerList) {
                ((SocialListener) obj2).onSocialCancel(intent);
            }
        }
    }

    @SpeechAnnotation(event = VoiceButtonClick.EVENT)
    public void onVoiceButtonClick(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SocialListener) obj).onVoiceButtonClick();
            }
        }
    }

    @SpeechAnnotation(event = BackButtonClick.EVENT)
    public void onBackButtonClick(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SocialListener) obj).onBackButtonClick();
            }
        }
    }

    public void broadcastGroupMessage(String tts) {
        String slots = "";
        try {
            slots = new JSONObject().put("tts", tts).put("intent", GROUP_MESSAGE_INTENT).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SpeechClient.instance().getAgent().triggerIntent(OFFLINE_SKILL, LBS_SOCAIL_TASK, GROUP_MESSAGE_INTENT, slots);
    }

    public void joinGroup(String tts) {
        String slots = "";
        try {
            slots = new JSONObject().put("tts", tts).put("intent", JOIN_GROUP_INTENT).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SpeechClient.instance().getAgent().triggerIntent(OFFLINE_SKILL, LBS_SOCAIL_TASK, JOIN_GROUP_INTENT, slots);
    }

    public void querySendMessage(String tts) {
        String slots = "";
        try {
            slots = new JSONObject().put("tts", tts).put("intent", QUERY_SEND_MESSAGE).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SpeechClient.instance().getAgent().triggerIntent(OFFLINE_SKILL, LBS_SOCAIL_TASK, QUERY_SEND_MESSAGE, slots);
    }

    public void querySetVoiceButton(String tts) {
        String slots = "";
        try {
            slots = new JSONObject().put("tts", tts).put("intent", QUERY_SET_VOICE_BUTTON).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SpeechClient.instance().getAgent().triggerIntent(OFFLINE_SKILL, LBS_SOCAIL_TASK, QUERY_SET_VOICE_BUTTON, slots);
    }

    public void stopDialog() {
        SpeechClient.instance().getWakeupEngine().stopDialog();
    }
}
