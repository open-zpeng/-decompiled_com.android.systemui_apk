package com.xiaopeng.speech.protocol.node.personalCenter;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.PersonalCenterEvent;
/* loaded from: classes23.dex */
public class PersonalCenterNode_Processor implements ICommandProcessor {
    private PersonalCenterNode mTarget;

    public PersonalCenterNode_Processor(PersonalCenterNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode != 911050310) {
            if (hashCode == 1416411835 && event.equals(PersonalCenterEvent.CONTROL_PROFILE_HABIT_SELECT)) {
                c = 0;
            }
            c = 65535;
        } else {
            if (event.equals(PersonalCenterEvent.CONTROL_PROFILE_HABIT_SELECT_NEXT)) {
                c = 1;
            }
            c = 65535;
        }
        if (c == 0) {
            this.mTarget.onControlProfileHabitSelect(event, data);
        } else if (c == 1) {
            this.mTarget.onControlProfileHabitSelectNext(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{PersonalCenterEvent.CONTROL_PROFILE_HABIT_SELECT, PersonalCenterEvent.CONTROL_PROFILE_HABIT_SELECT_NEXT};
    }
}
