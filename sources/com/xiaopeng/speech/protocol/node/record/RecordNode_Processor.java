package com.xiaopeng.speech.protocol.node.record;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.jarvisproto.AsrCloundResult;
import com.xiaopeng.speech.jarvisproto.RecordBegin;
import com.xiaopeng.speech.jarvisproto.RecordEnd;
import com.xiaopeng.speech.jarvisproto.RecordError;
import com.xiaopeng.speech.jarvisproto.RecordMaxLength;
import com.xiaopeng.speech.jarvisproto.SpeechBegin;
import com.xiaopeng.speech.jarvisproto.SpeechEnd;
import com.xiaopeng.speech.jarvisproto.SpeechVolume;
/* loaded from: classes23.dex */
public class RecordNode_Processor implements ICommandProcessor {
    private RecordNode mTarget;

    public RecordNode_Processor(RecordNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2099844195:
                if (event.equals(SpeechVolume.EVENT)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -477025291:
                if (event.equals(RecordBegin.EVENT)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -473856684:
                if (event.equals(RecordError.EVENT)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 223082937:
                if (event.equals(AsrCloundResult.EVENT)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 744773574:
                if (event.equals(SpeechBegin.EVENT)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 747146040:
                if (event.equals(SpeechEnd.EVENT)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 871014183:
                if (event.equals(RecordEnd.EVENT)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1711895190:
                if (event.equals(RecordMaxLength.EVENT)) {
                    c = 7;
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
                this.mTarget.onAsrResult(event, data);
                return;
            case 1:
                this.mTarget.onRecordBegin(event, data);
                return;
            case 2:
                this.mTarget.onRecordEnd(event, data);
                return;
            case 3:
                this.mTarget.onRecordError(event, data);
                return;
            case 4:
                this.mTarget.onSpeechBegin(event, data);
                return;
            case 5:
                this.mTarget.onSpeechEnd(event, data);
                return;
            case 6:
                this.mTarget.onSpeechVolume(event, data);
                return;
            case 7:
                this.mTarget.onRecordMaxLength(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{AsrCloundResult.EVENT, RecordBegin.EVENT, RecordEnd.EVENT, RecordError.EVENT, SpeechBegin.EVENT, SpeechEnd.EVENT, SpeechVolume.EVENT, RecordMaxLength.EVENT};
    }
}
