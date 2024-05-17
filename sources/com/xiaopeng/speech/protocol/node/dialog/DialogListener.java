package com.xiaopeng.speech.protocol.node.dialog;

import com.xiaopeng.speech.INodeListener;
import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.speech.jarvisproto.DialogSoundAreaStatus;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogActiveReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogExitReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.PrepareReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.WakeupReason;
/* loaded from: classes23.dex */
public interface DialogListener extends INodeListener {
    void onDialogContinue();

    void onDialogEnd(DialogEndReason dialogEndReason);

    void onDialogError();

    void onDialogStart(WakeupReason wakeupReason);

    void onDialogWait(DMWait dMWait);

    void onVadBegin();

    void onVadEnd();

    void onWakeupResult();

    default void onDialogPrepare(PrepareReason reason) {
    }

    default void onDialogActive(DialogActiveReason reason) {
    }

    default void onDialogExit(DialogExitReason exitReason) {
    }

    default void onDialogStatusChanged(String status) {
    }

    default void onSoundAreaStatusChanged(SoundAreaStatus status) {
    }

    default void onDialogSoundAreaStatusChanged(DialogSoundAreaStatus status) {
    }
}
