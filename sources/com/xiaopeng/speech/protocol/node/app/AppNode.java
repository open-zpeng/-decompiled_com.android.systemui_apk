package com.xiaopeng.speech.protocol.node.app;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.AppEvent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.infoflow.message.define.CardExtra;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class AppNode extends SpeechNode<AppListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.QUERY)
    public void onQuery(String event, String data) {
        String appName = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            appName = jsonObject.optString("appName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onQuery(event, appName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.APP_OPEN)
    public void onAppOpen(String event, String data) {
        String appName = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            appName = jsonObject.optString("appName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onAppOpen(event, appName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.App_PAGE_OPEN)
    public void onAppPageOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
            String pageUrl = jsonObject.optString("pageUrl");
            if (jsonObject.optBoolean("isDui", false)) {
                pageUrl = URLDecoder.decode(replaceAllstr(pageUrl), "utf-8");
            }
            jsonObject.putOpt("pageUrl", pageUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onAppPageOpen(jsonObject.toString());
            }
        }
    }

    private String replaceAllstr(String pageUrl) {
        if (!TextUtils.isEmpty(pageUrl) && pageUrl.contains("$")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pageUrl.length(); i++) {
                String str = pageUrl.substring(i, i + 1);
                if (str.equals("$")) {
                    sb.append("&");
                } else {
                    sb.append(str);
                }
            }
            return sb.toString();
        }
        return pageUrl;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.KEYEVENT_BACK)
    public void onKeyeventBack(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onKeyeventBack();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.APPSTORE_OPEN)
    public void onAppStoreOpen(String event, String data) {
        String appName = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            appName = jsonObject.optString("appName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onAppStoreOpen(event, appName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.TRIGGER_INTENT)
    public void onTriggerIntent(String event, String data) {
        String skill = "";
        String task = "";
        String intent = "";
        String slots = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            skill = jsonObject.optString("skill");
            task = jsonObject.optString("task");
            intent = jsonObject.optString("intent");
            slots = jsonObject.optString("slots");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onTriggerIntent(skill, task, intent, slots);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.DEBUG_OPEN)
    public void onDebugOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onDebugOpen();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.APP_ACTIVE)
    public void onAppActive(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onAppActive();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.AI_HOMEPAGE_OPEN)
    public void onAiHomepageOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onAiHomepageOpen();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.AI_HOMEPAGE_CLOSE)
    public void onAiHomepageClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onAiHomepageClose();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.APP_LAUNCHER_EXIT)
    public void onAppLauncherExit(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onAppLauncherExit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.START_PAGE)
    public void onStartPage(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        String packageName = "";
        String action = "";
        String extraData = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            packageName = jsonObject.optString(VuiConstants.SCENE_PACKAGE_NAME);
            action = jsonObject.optString("action");
            extraData = jsonObject.optString(CardExtra.KEY_CARD_EXTRA_DATA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onStartPage(packageName, action, extraData);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.GUI_CARSPEECH_DEBUG_OPEN)
    public void onGuiSpeechDebugPage(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onGuiSpeechDebugPage();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AppEvent.APP_OPEN_YOUKU_SEARCH)
    public void onOpenYoukuSearch(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AppListener) obj).onOpenYoukuSearch(data);
            }
        }
    }
}
