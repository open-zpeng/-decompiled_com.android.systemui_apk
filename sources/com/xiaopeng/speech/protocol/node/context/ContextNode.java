package com.xiaopeng.speech.protocol.node.context;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.jarvisproto.ContextInput;
import com.xiaopeng.speech.jarvisproto.ContextOutput;
import com.xiaopeng.speech.protocol.bean.FeedListUIValue;
import com.xiaopeng.speech.protocol.event.ContextEvent;
import com.xiaopeng.speech.protocol.node.tts.TtsEcho;
import com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class ContextNode extends SpeechNode<ContextListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextInput.EVENT)
    public void onInputText(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onInputText(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextOutput.EVENT)
    public void onOutputText(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onOutputText(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.TIPS_TEXT)
    public void onTipsText(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onTipsText(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_CUSTOM)
    public void onWidgetCustom(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onWidgetCustom(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_TEXT)
    public void onWidgetText(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onWidgetText(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_LIST)
    public void onWidgetList(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onWidgetList(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_LIST_FOCUS)
    public void onWidgetListFocus(String event, String data) {
        Object[] listenerList;
        try {
            FeedListUIValue value = FeedListUIValue.fromJson(new JSONObject(data));
            if (value != null && (listenerList = this.mListenerList.collectCallbacks()) != null) {
                for (Object obj : listenerList) {
                    ((ContextListener) obj).onWidgetListFocus(value.source, value.index);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_MEDIA)
    public void onWidgetMedia(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onWidgetMedia(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_CARD)
    public void onWidgetCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onWidgetCard(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_RECOMMEND)
    public void onWidgetRecommend(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onWidgetRecommend(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_SEARCH)
    public void onWidgetSearch(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onWidgetSearch(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.EXPRESSION)
    public void onExpression(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onExpression(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.SAY_WELCOME)
    public void onSayWelcome(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onSayWelcome(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_LIST_SCROLL)
    public void onWidgetScroll(String event, String data) {
        Object[] listenerList;
        try {
            FeedListUIValue value = FeedListUIValue.fromJson(new JSONObject(data));
            if (value != null && (listenerList = this.mListenerList.collectCallbacks()) != null) {
                for (Object obj : listenerList) {
                    LogUtils.i("onWidgetScroll", "onWidgetScroll data:" + data);
                    ((ContextListener) obj).onWidgetScroll(value.source, value.index);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_LIST_SELECT)
    public void onWidgetListSelect(String event, String data) {
        Object[] listenerList;
        try {
            FeedListUIValue value = FeedListUIValue.fromJson(new JSONObject(data));
            if (value != null && (listenerList = this.mListenerList.collectCallbacks()) != null) {
                for (int i = 0; i < listenerList.length; i++) {
                    LogUtils.i("onWidgetListSelect", "onWidgetListSelect data:" + data);
                    if (TextUtils.isEmpty(value.type)) {
                        ((ContextListener) listenerList[i]).onWidgetListSelect(value.source, value.index);
                    } else {
                        ((ContextListener) listenerList[i]).onWidgetListSelect(value.source, value.index, value.type);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_CANCEL)
    public void onWidgetCancel(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            if (listenerList != null) {
                for (Object obj : listenerList) {
                    String widgetId = null;
                    String cancelWay = ContextModel.CTRL_CARD_CANCEL_WAY_FORCE;
                    if (data != null) {
                        JSONObject obj2 = new JSONObject(data);
                        widgetId = obj2.optString("widgetId");
                        cancelWay = obj2.optString("cancel");
                    }
                    ((ContextListener) obj).onWidgetCancel(widgetId, cancelWay);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.CONTEXT_WIDGET_NEXT_PAGE)
    public void onPageNext(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onPageNext();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.CONTEXT_WIDGET_PREV_PAGE)
    public void onPagePrev(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onPagePrev();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.CONTEXT_WIDGET_TOPPING_PAGE)
    public void onPageTopping(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onPageTopping();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.CONTEXT_WIDGET_LOW_PAGE)
    public void onPageSetLow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onPageSetLow();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_LIST_CANCEL_FOCUS)
    public void onWidgetListCancelFocus(String event, String data) {
        Object[] listenerList;
        try {
            FeedListUIValue value = FeedListUIValue.fromJson(new JSONObject(data));
            if (value != null && (listenerList = this.mListenerList.collectCallbacks()) != null) {
                for (Object obj : listenerList) {
                    ((ContextListener) obj).onWidgetListCancelFocus(value.source, value.index);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.SCRIPT_START)
    public void onScript(String event, String data) {
        String scriptId = "";
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            String domain = "";
            if (data != null) {
                JSONObject obj = new JSONObject(data);
                domain = obj.optString("domain");
                scriptId = obj.optString("scriptId");
            }
            if (listenerList != null && !TextUtils.isEmpty(domain) && !TextUtils.isEmpty(scriptId)) {
                for (Object obj2 : listenerList) {
                    ((ContextListener) obj2).onScript(domain, scriptId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.SCRIPT_STATUS)
    public void onScriptStatus(String event, String data) {
        String scriptId = "";
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            String status = "";
            if (data != null) {
                JSONObject obj = new JSONObject(data);
                status = obj.optString("status");
                scriptId = obj.optString("scriptId");
            }
            if (listenerList != null && !TextUtils.isEmpty(status) && !TextUtils.isEmpty(scriptId)) {
                for (Object obj2 : listenerList) {
                    ((ContextListener) obj2).onScriptStatus(scriptId, status);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "context.exit.recommend.card")
    public void onExitRecommendCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onExitRecommendCard();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_LIST_FOLD)
    public void onWidgetListFold(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            if (listenerList != null && data != null) {
                FeedListUIValue value = FeedListUIValue.fromJson(new JSONObject(data));
                for (Object obj : listenerList) {
                    ((ContextListener) obj).onWidgetListFold(value.source, value.type);
                }
            }
        } catch (JSONException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_LIST_EXPEND)
    public void onWidgetListExpend(String event, String data) {
        try {
            Object[] listenerList = this.mListenerList.collectCallbacks();
            if (listenerList != null && data != null) {
                FeedListUIValue value = FeedListUIValue.fromJson(new JSONObject(data));
                for (Object obj : listenerList) {
                    ((ContextListener) obj).onWidgetListExpend(value.source, value.type);
                }
            }
        } catch (JSONException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.WIDGET_LIST_STOP_COUNTDOWN)
    public void onWidgetListStopCountdown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onWidgetListStopCountdown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.TIPS_LISTENING_SHOW)
    public void onTipsListeningShow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onTipsListeningShow(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ContextEvent.TIPS_LISTENING_STOP)
    public void onTipsListeningStop(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onTipsListeningStop(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = TtsEcho.EVENT_TTS_ECHO)
    public void onTtsEcho(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        TtsEcho ttsEcho = TtsEcho.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ContextListener) obj).onTtsEcho(ttsEcho);
            }
        }
    }

    public void onWidgetCancelByUser(String widgetId) {
        LogUtils.i("onWidgetCancelByUser widgetId: " + widgetId);
        SpeechClient.instance().getAgent().sendUIEvent(ContextEvent.WIDGET_CANCEL_BY_USER, widgetId);
    }
}
