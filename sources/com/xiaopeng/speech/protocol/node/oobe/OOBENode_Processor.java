package com.xiaopeng.speech.protocol.node.oobe;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.ErrorEvent;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
/* loaded from: classes23.dex */
public class OOBENode_Processor implements ICommandProcessor {
    private OOBENode mTarget;

    public OOBENode_Processor(OOBENode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1653395749:
                if (event.equals(OOBEEvent.OOBE_SKIP)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1548651618:
                if (event.equals(OOBEEvent.OOBE_SEARCH_ERROR)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1397629300:
                if (event.equals(OOBEEvent.OOBE_ADDRESS_SET)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 176523582:
                if (event.equals(OOBEEvent.OOBE_ASR_ERROR)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 177511753:
                if (event.equals(OOBEEvent.OOBE_RECORD_INPUT)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 314263926:
                if (event.equals(OOBEEvent.OOBE_VOLUME)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 780227936:
                if (event.equals(ErrorEvent.ERROR_ASR_RESULT)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 983678060:
                if (event.equals(OOBEEvent.OOBE_NETWORK_ERROR)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1457336958:
                if (event.equals(OOBEEvent.OOBE_RECORD_RESULT)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1505323214:
                if (event.equals(OOBEEvent.OOBE_OTHER_ERROR)) {
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
                this.mTarget.onRecordResult(event, data);
                return;
            case 1:
                this.mTarget.onRecordInput(event, data);
                return;
            case 2:
                this.mTarget.onAddressSet(event, data);
                return;
            case 3:
                this.mTarget.onSkip(event, data);
                return;
            case 4:
                this.mTarget.onSearchError(event, data);
                return;
            case 5:
                this.mTarget.onNetError(event, data);
                return;
            case 6:
                this.mTarget.onASRError(event, data);
                return;
            case 7:
                this.mTarget.onError(event, data);
                return;
            case '\b':
                this.mTarget.onVolumeChange(event, data);
                return;
            case '\t':
                this.mTarget.onNetWorkError(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{OOBEEvent.OOBE_RECORD_RESULT, OOBEEvent.OOBE_RECORD_INPUT, OOBEEvent.OOBE_ADDRESS_SET, OOBEEvent.OOBE_SKIP, OOBEEvent.OOBE_SEARCH_ERROR, ErrorEvent.ERROR_ASR_RESULT, OOBEEvent.OOBE_ASR_ERROR, OOBEEvent.OOBE_OTHER_ERROR, OOBEEvent.OOBE_VOLUME, OOBEEvent.OOBE_NETWORK_ERROR};
    }
}
