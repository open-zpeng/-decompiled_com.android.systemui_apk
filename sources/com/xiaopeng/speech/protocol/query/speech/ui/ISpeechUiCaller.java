package com.xiaopeng.speech.protocol.query.speech.ui;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface ISpeechUiCaller extends IQueryCaller {
    default boolean isSuperDialogueWhitelist() {
        return false;
    }

    default boolean isSuperDialogueOpened() {
        return false;
    }
}
