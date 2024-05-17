package com.xiaopeng.speech;

import android.content.Context;
import com.xiaopeng.speech.overall.OverallManager;
import com.xiaopeng.speech.overall.listener.IXpRecordListener;
/* loaded from: classes23.dex */
public class SpeechEngine {
    public static void subscribeOverall(Context context, String observer, String[] events, String[] querys) {
        if (events == null && querys == null) {
            throw new IllegalArgumentException("no events and querys");
        }
        String[] returnEvents = new String[(events == null ? 0 : events.length) + (querys == null ? 0 : querys.length)];
        if (events != null) {
            System.arraycopy(events, 0, returnEvents, 0, events.length);
        }
        if (querys != null) {
            System.arraycopy(querys, 0, returnEvents, events == null ? 0 : events.length, querys.length);
        }
        OverallManager.instance().subscribe(context, observer, returnEvents);
    }

    public static void unsubscribeOverall(String observer) {
        OverallManager.instance().unsubscribe(observer);
    }

    public static void triggerIntent(String skill, String task, String intent, String slots) {
        OverallManager.instance().triggerIntent(skill, task, intent, slots);
    }

    public void triggerEvent(String event, String data) {
        OverallManager.instance().triggerEvent(event, data);
    }

    public void stopDialog() {
        OverallManager.instance().stopDialog();
    }

    public void sendEvent(String event, String data) {
        OverallManager.instance().sendEvent(event, data);
    }

    public void startDialogFrom(String type) {
        OverallManager.instance().startDialogFrom(type);
    }

    public void feedbackResult(String event, String data) {
        OverallManager.instance().feedbackResult(event, data);
    }

    public void replySupport(String event, boolean isSupport, String text) {
        OverallManager.instance().replySupport(event, isSupport, text);
    }

    public void replySupport(String event, boolean isSupport) {
        replySupport(event, isSupport, "");
    }

    public static void speak(String tts) {
        OverallManager.instance().speak(tts);
    }

    public static void initRecord(Context context, String param, IXpRecordListener listener) {
        OverallManager.instance().initRecord(context, param, listener);
    }

    public static void initRecord(Context context, IXpRecordListener listener) {
        OverallManager.instance().initRecord(context, null, listener);
    }

    public static void startRecord(String param) {
        OverallManager.instance().startRecord(param);
    }

    public static void starRecord() {
        OverallManager.instance().startRecord(null);
    }

    public static void stopRecord() {
        OverallManager.instance().stopRecord();
    }

    public static void destroyRecord(IXpRecordListener listener) {
        OverallManager.instance().destroyRecord(listener);
    }

    public static boolean isSupportRecord() {
        return OverallManager.instance().isSupportRecord();
    }
}
