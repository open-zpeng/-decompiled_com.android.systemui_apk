package com.xiaopeng.systemui.infoflow.speech.core.speech.behavior;

import com.xiaopeng.speech.common.util.SimpleCallbackList;
/* loaded from: classes24.dex */
public class SpeechWidgetManager {
    private SimpleCallbackList<ISpeechWidget> mWidgetSimpleCallbackList = new SimpleCallbackList<>();

    public void addCallback(ISpeechWidget behavior) {
        this.mWidgetSimpleCallbackList.addCallback(behavior);
    }

    public void removeCallback(ISpeechWidget behavior) {
        this.mWidgetSimpleCallbackList.removeCallback(behavior);
    }

    public void onAcWidget() {
        Object[] iSpeechWidgets = this.mWidgetSimpleCallbackList.collectCallbacks();
        if (iSpeechWidgets != null) {
            for (Object obj : iSpeechWidgets) {
                ((ISpeechWidget) obj).onAcWidgetOn();
            }
        }
    }
}
