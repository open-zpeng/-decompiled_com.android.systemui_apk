package com.xiaopeng.systemui.infoflow.speech;

import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
/* loaded from: classes24.dex */
public interface SpeechViewContainer {
    public static final int TYPE_ACTION_SPEECH_CARD_HIDE = 3;
    public static final int TYPE_ACTION_SPEECH_CARD_SHOW = 2;

    void enterSpeechMode(int i);

    void exitSpeechMode();

    void notifyAvatarAction(int i);

    void onDialogEnd(DialogEndReason dialogEndReason);

    void onDialogStart();

    void onListeningStatusChanged(boolean z);

    void onTopViewTypeChanged(int i, boolean z);

    void onVadBegin();

    void onVadEnd();

    void showSpeechBackground(boolean z);

    void showVoiceLoc(int i);

    void updateSceneType(int i);
}
