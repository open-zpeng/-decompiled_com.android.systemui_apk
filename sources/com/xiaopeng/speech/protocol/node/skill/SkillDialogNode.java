package com.xiaopeng.speech.protocol.node.skill;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.SkillDialogEvent;
/* loaded from: classes23.dex */
public class SkillDialogNode extends SpeechNode<SkillDialogListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SkillDialogEvent.ROUTE_CLOSE_WINDOW)
    public void onCloseWindow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SkillDialogListener) obj).onCloseWindow(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SkillDialogEvent.ROUTE_OPEN_WINDOW)
    public void onOpenWindow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SkillDialogListener) obj).onOpenWindow(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SkillDialogEvent.AI_FORWARD_SCREEN_EVENT)
    public void onForwardScreenEvent(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SkillDialogListener) obj).onForwardScreenEvent(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SkillDialogEvent.SKILL_REFRESH_DATA)
    public void onRefreshSkillData(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SkillDialogListener) obj).onRefreshSkillData(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SkillDialogEvent.SKILL_SHOW_DOUBLE_GUIDE)
    public void onShowDoubleGuide(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SkillDialogListener) obj).onShowDoubleGuide(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SkillDialogEvent.SKILL_SHOW_KEY_GUIDE)
    public void onShowKeyGuide(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SkillDialogListener) obj).onShowKeyGuide(data);
            }
        }
    }
}
