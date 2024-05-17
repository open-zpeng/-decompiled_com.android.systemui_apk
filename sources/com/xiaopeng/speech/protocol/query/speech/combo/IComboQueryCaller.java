package com.xiaopeng.speech.protocol.query.speech.combo;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface IComboQueryCaller extends IQueryCaller {
    default String enterUserMode(String modeType) {
        return "normal";
    }

    default String exitUserMode(String modeType) {
        return "normal";
    }

    default String checkEnterUserMode(String modeType) {
        return "normal";
    }

    default String getCurrentUserMode() {
        return "normal";
    }
}
