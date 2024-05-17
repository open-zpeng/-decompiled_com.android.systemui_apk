package com.xiaopeng.speech.protocol.node.personalCenter;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.protocol.event.PersonalCenterEvent;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class PersonalCenterNode extends SpeechNode<PersonalCenterListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = PersonalCenterEvent.CONTROL_PROFILE_HABIT_SELECT)
    public void onControlProfileHabitSelect(String event, String data) {
        LogUtils.i("PersonalCenterNode", "data = " + data + " , event = " + event);
        int number = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            number = jsonObject.optInt("index");
        } catch (Throwable th) {
            LogUtils.e("CaracNode", "onControlProfileHabitSelect string data error, data = " + data);
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PersonalCenterListener) obj).onControlProfileHabitSelect(number);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = PersonalCenterEvent.CONTROL_PROFILE_HABIT_SELECT_NEXT)
    public void onControlProfileHabitSelectNext(String event, String data) {
        LogUtils.i("PersonalCenterNode", "data = " + data + " , event = " + event);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PersonalCenterListener) obj).onControlProfileHabitSelectNext();
            }
        }
    }
}
