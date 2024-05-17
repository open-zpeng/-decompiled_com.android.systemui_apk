package com.xiaopeng.speech.protocol.query.personalCenter;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface IPersonalCenterCaller extends IQueryCaller {
    default int getControlProfileHabitSupport() {
        return -1;
    }

    default int getControlProfileHabitNumSupport(int num) {
        return -1;
    }

    default int getGuiPageOpenState(String pageUrl) {
        return -1;
    }
}
