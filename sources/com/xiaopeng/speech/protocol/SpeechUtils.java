package com.xiaopeng.speech.protocol;

import android.text.TextUtils;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.speech.INodeListener;
import com.xiaopeng.speech.IQueryCaller;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.actor.Actor;
import com.xiaopeng.speech.actorapi.DataActor;
import com.xiaopeng.speech.actorapi.SupportActor;
import com.xiaopeng.speech.actorapi.ValueActor;
import com.xiaopeng.speech.speechwidget.SupportWidget;
/* loaded from: classes23.dex */
public class SpeechUtils {
    public static void replySupport(String event, boolean isSupport) {
        replySupport(event, isSupport, "");
    }

    public static void replySupport(String event, boolean isSupport, String text) {
        SupportWidget supportWidget = new SupportWidget();
        supportWidget.setSupport(isSupport);
        supportWidget.setTTS(text);
        SpeechClient.instance().getActorBridge().send(new SupportActor(event).setResult(supportWidget));
    }

    public static void replyData(String event, String result) {
        SpeechClient.instance().getActorBridge().send(new DataActor(event).setResult(result).setSupport(false));
    }

    public static void replyData(String event, String result, boolean isSupport) {
        SpeechClient.instance().getActorBridge().send(new DataActor(event).setResult(result).setSupport(isSupport));
    }

    public static void replyValue(String event, Object value) {
        SpeechClient.instance().getActorBridge().send(new ValueActor(event).setValue(value));
    }

    public static String speak(String text) {
        return SpeechClient.instance().getTTSEngine().speak(text);
    }

    public static void subscribe(Class<? extends SpeechNode> nodeClass, INodeListener listener) {
        SpeechClient.instance().getNodeManager().subscribe(nodeClass, listener);
    }

    public static void subscribe(Class<? extends SpeechQuery> queryClass, IQueryCaller caller) {
        SpeechClient.instance().getQueryManager().inject(queryClass, caller);
    }

    public static void unsubscribe(Class<? extends SpeechNode> nodeClass, INodeListener listener) {
        SpeechClient.instance().getNodeManager().unSubscribe(nodeClass, listener);
    }

    public static void unsubscribe(Class<? extends SpeechQuery> queryClass) {
        SpeechClient.instance().getQueryManager().unInject(queryClass);
    }

    public static void sendActor(Actor actor) {
        SpeechClient.instance().sendActor(actor);
    }

    public static void triggerIntent(String skill, String task, String intent, String slots) {
        SpeechClient.instance().getAgent().triggerIntent(skill, task, intent, slots);
    }

    public static <T extends SpeechNode> T getNode(Class<T> nodeClass) {
        return (T) SpeechClient.instance().getNodeManager().getNode(nodeClass);
    }

    public static <T extends SpeechQuery> T getQuery(Class<T> queryClass) {
        return (T) SpeechClient.instance().getQueryManager().getQuery(queryClass);
    }

    public static boolean isJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String str2 = str.trim();
        if (str2.startsWith("{") && str2.endsWith("}")) {
            return true;
        }
        if (!str2.startsWith(NavigationBarInflaterView.SIZE_MOD_START) || !str2.endsWith(NavigationBarInflaterView.SIZE_MOD_END)) {
            return false;
        }
        return true;
    }
}
