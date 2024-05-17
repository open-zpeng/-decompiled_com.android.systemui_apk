package com.xiaopeng.speech.protocol.node.dialog;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.jarvisproto.DMActive;
import com.xiaopeng.speech.jarvisproto.DMExit;
import com.xiaopeng.speech.jarvisproto.DMPrepare;
import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.speech.jarvisproto.DialogSoundAreaStatus;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.event.DialogEvent;
/* loaded from: classes23.dex */
public class DialogNode_Processor implements ICommandProcessor {
    private DialogNode mTarget;

    public DialogNode_Processor(DialogNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1434480195:
                if (event.equals(DialogEvent.DIALOG_STATUS_CHANGED)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -1411139995:
                if (event.equals(DialogEvent.VAD_END)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1293262773:
                if (event.equals(DialogEvent.WAKEUP_RESULT)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -931531412:
                if (event.equals(DialogEvent.DIALOG_CONTINUE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -772037990:
                if (event.equals(DMExit.EVENT)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -771523855:
                if (event.equals(DMWait.EVENT)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -206130037:
                if (event.equals(SoundAreaStatus.EVENT)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -153794717:
                if (event.equals(DialogEvent.DIALOG_ERROR)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 561561751:
                if (event.equals(DialogSoundAreaStatus.EVENT)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 763224587:
                if (event.equals(DMPrepare.EVENT)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 967244162:
                if (event.equals(DMActive.EVENT)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1083473887:
                if (event.equals("jarvis.dm.end")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1101097907:
                if (event.equals(DialogEvent.VAD_BEGIN)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1849428582:
                if (event.equals("jarvis.dm.start")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                this.mTarget.onDialogStart(event, data);
                return;
            case 1:
                this.mTarget.onDialogActive(event, data);
                return;
            case 2:
                this.mTarget.onDialogPrepare(event, data);
                return;
            case 3:
                this.mTarget.onDialogError(event, data);
                return;
            case 4:
                this.mTarget.onDialogExit(event, data);
                return;
            case 5:
                this.mTarget.onDialogEnd(event, data);
                return;
            case 6:
                this.mTarget.onDialogWait(event, data);
                return;
            case 7:
                this.mTarget.onDialogContinue(event, data);
                return;
            case '\b':
                this.mTarget.onWakeupResult(event, data);
                return;
            case '\t':
                this.mTarget.onVadBegin(event, data);
                return;
            case '\n':
                this.mTarget.onVadEnd(event, data);
                return;
            case 11:
                this.mTarget.onDialogStatusChanged(event, data);
                return;
            case '\f':
                this.mTarget.onSoundAreaStatusChanged(event, data);
                return;
            case '\r':
                this.mTarget.onDialogState(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{"jarvis.dm.start", DMActive.EVENT, DMPrepare.EVENT, DialogEvent.DIALOG_ERROR, DMExit.EVENT, "jarvis.dm.end", DMWait.EVENT, DialogEvent.DIALOG_CONTINUE, DialogEvent.WAKEUP_RESULT, DialogEvent.VAD_BEGIN, DialogEvent.VAD_END, DialogEvent.DIALOG_STATUS_CHANGED, SoundAreaStatus.EVENT, DialogSoundAreaStatus.EVENT};
    }
}
