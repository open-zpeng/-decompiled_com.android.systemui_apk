package com.xiaopeng.speech.protocol.node.aiassistant;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.AiAssistantEvent;
/* loaded from: classes23.dex */
public class AiAssistantNode_Processor implements ICommandProcessor {
    private AiAssistantNode mTarget;

    public AiAssistantNode_Processor(AiAssistantNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1808972678:
                if (event.equals(AiAssistantEvent.MESSAGE_CLOSE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -874525820:
                if (event.equals(AiAssistantEvent.PLAY_VIDEO)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -473634936:
                if (event.equals(AiAssistantEvent.MESSAGE_OPEN)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 67809134:
                if (event.equals(AiAssistantEvent.XIAOP_DANCE)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 422503758:
                if (event.equals(AiAssistantEvent.GUI_OPEN_VIDEO)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1345769982:
                if (event.equals(AiAssistantEvent.PLAY_VIDEO_TTSEND)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1715275603:
                if (event.equals(AiAssistantEvent.XIAOP_CHANGE_CLOTHE)) {
                    c = 6;
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
                this.mTarget.onPlayVideo(event, data);
                return;
            case 1:
                this.mTarget.onPlayVideoTtsend(event, data);
                return;
            case 2:
                this.mTarget.onMessageOpen(event, data);
                return;
            case 3:
                this.mTarget.onMessageClose(event, data);
                return;
            case 4:
                this.mTarget.onOpenVideo(event, data);
                return;
            case 5:
                this.mTarget.onXiaoPDance(event, data);
                return;
            case 6:
                this.mTarget.onXiaoPChangeClothe(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{AiAssistantEvent.PLAY_VIDEO, AiAssistantEvent.PLAY_VIDEO_TTSEND, AiAssistantEvent.MESSAGE_OPEN, AiAssistantEvent.MESSAGE_CLOSE, AiAssistantEvent.GUI_OPEN_VIDEO, AiAssistantEvent.XIAOP_DANCE, AiAssistantEvent.XIAOP_CHANGE_CLOTHE};
    }
}
