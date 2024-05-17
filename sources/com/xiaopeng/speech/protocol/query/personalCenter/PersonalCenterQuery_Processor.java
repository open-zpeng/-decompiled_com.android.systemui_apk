package com.xiaopeng.speech.protocol.query.personalCenter;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QueryPersonalCenterEvent;
/* loaded from: classes23.dex */
public class PersonalCenterQuery_Processor implements IQueryProcessor {
    private PersonalCenterQuery mTarget;

    public PersonalCenterQuery_Processor(PersonalCenterQuery target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode == -1082449569) {
            if (event.equals(QueryPersonalCenterEvent.GET_PAGE_OPEN_STATUS_PERSONAL_CENTER)) {
                c = 2;
            }
            c = 65535;
        } else if (hashCode != 982269335) {
            if (hashCode == 1404633087 && event.equals(QueryPersonalCenterEvent.CONTROL_PROFILE_HABIT_SUPPORT)) {
                c = 0;
            }
            c = 65535;
        } else {
            if (event.equals(QueryPersonalCenterEvent.CONTROL_PROFILE_HABIT_NUM_SUPPORT)) {
                c = 1;
            }
            c = 65535;
        }
        if (c != 0) {
            if (c != 1) {
                if (c == 2) {
                    return Integer.valueOf(this.mTarget.getGuiPageOpenState(event, data));
                }
                return null;
            }
            return Integer.valueOf(this.mTarget.getControlProfileHabitNumSupport(event, data));
        }
        return Integer.valueOf(this.mTarget.getControlProfileHabitSupport(event, data));
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryPersonalCenterEvent.CONTROL_PROFILE_HABIT_SUPPORT, QueryPersonalCenterEvent.CONTROL_PROFILE_HABIT_NUM_SUPPORT, QueryPersonalCenterEvent.GET_PAGE_OPEN_STATUS_PERSONAL_CENTER};
    }
}
