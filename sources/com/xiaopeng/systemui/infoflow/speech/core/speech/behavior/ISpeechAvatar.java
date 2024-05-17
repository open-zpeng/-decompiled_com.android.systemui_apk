package com.xiaopeng.systemui.infoflow.speech.core.speech.behavior;

import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
/* loaded from: classes24.dex */
public interface ISpeechAvatar {
    void onAvatarWakerupDisable(String str);

    void onAvatarWakerupEnable(String str);

    void onDialogEnd(DialogEndReason dialogEndReason);

    void onDialogStart(int i);

    void onDialogWait(DMWait dMWait);

    void onSilence();

    void onVadBegin();

    void onVadEnd();

    void onVoiceLocChanged(int i);

    void onWakeupStatusChanged(int i, int i2, String str);
}
