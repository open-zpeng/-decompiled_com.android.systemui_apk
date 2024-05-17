package com.xiaopeng.speech.protocol;

import android.os.Handler;
import android.os.Looper;
import com.xiaopeng.speech.INodeListener;
import com.xiaopeng.speech.IQueryCaller;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.actor.Actor;
/* loaded from: classes23.dex */
public class SpeechModel {
    protected Handler mMainHandler = new Handler(Looper.getMainLooper());

    public void subscribe(Class<? extends SpeechNode> nodeClass, INodeListener listener) {
        SpeechUtils.subscribe(nodeClass, listener);
    }

    public void unsubscribe(Class<? extends SpeechNode> nodeClass, INodeListener listener) {
        SpeechUtils.unsubscribe(nodeClass, listener);
    }

    public void subscribe(Class<? extends SpeechQuery> queryClass, IQueryCaller queryCaller) {
        SpeechUtils.subscribe(queryClass, queryCaller);
    }

    public void unsubscribe(Class<? extends SpeechQuery> queryClass) {
        SpeechUtils.unsubscribe(queryClass);
    }

    public void sendActor(Actor actor) {
        SpeechUtils.sendActor(actor);
    }

    public String speak(String text) {
        return SpeechUtils.speak(text);
    }

    public void triggerIntent(String skill, String task, String intent, String slots) {
        SpeechUtils.triggerIntent(skill, task, intent, slots);
    }

    public <T extends SpeechNode> T getNode(Class<T> nodeClass) {
        return (T) SpeechUtils.getNode(nodeClass);
    }

    public <T extends SpeechQuery> T getQuery(Class<T> queryClass) {
        return (T) SpeechUtils.getQuery(queryClass);
    }
}
