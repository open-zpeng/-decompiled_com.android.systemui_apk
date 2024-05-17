package com.xiaopeng.speech.protocol.node.dialog;

import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogActiveReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogExitReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.WakeupReason;
/* loaded from: classes23.dex */
public abstract class AbsDialogListener implements DialogListener {
    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogStart(WakeupReason wakeupReason) {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogError() {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogActive(DialogActiveReason activeReason) {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogExit(DialogExitReason exitReason) {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogEnd(DialogEndReason endReason) {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogWait(DMWait reason) {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogContinue() {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onWakeupResult() {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onVadBegin() {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onVadEnd() {
    }

    public void onResourceUpdateded() {
    }

    public void onUploadResult() {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogStatusChanged(String status) {
    }
}
