package com.xiaopeng.speech.protocol.query.personalCenter;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.protocol.event.query.QueryPersonalCenterEvent;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class PersonalCenterQuery extends SpeechQuery<IPersonalCenterCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryPersonalCenterEvent.CONTROL_PROFILE_HABIT_SUPPORT)
    public int getControlProfileHabitSupport(String event, String data) {
        return ((IPersonalCenterCaller) this.mQueryCaller).getControlProfileHabitSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryPersonalCenterEvent.CONTROL_PROFILE_HABIT_NUM_SUPPORT)
    public int getControlProfileHabitNumSupport(String event, String data) {
        LogUtils.i("PersonalCenterQuery", "getControlProfileHabitNumSupport data = " + data + " , event = " + event);
        int number = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            number = jsonObject.optInt("index");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((IPersonalCenterCaller) this.mQueryCaller).getControlProfileHabitNumSupport(number);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryPersonalCenterEvent.GET_PAGE_OPEN_STATUS_PERSONAL_CENTER)
    public int getGuiPageOpenState(String event, String data) {
        LogUtils.i("PersonalCenterQuery", "getGuiPageOpenState data = " + data + " , event = " + event);
        return ((IPersonalCenterCaller) this.mQueryCaller).getGuiPageOpenState(data);
    }
}
