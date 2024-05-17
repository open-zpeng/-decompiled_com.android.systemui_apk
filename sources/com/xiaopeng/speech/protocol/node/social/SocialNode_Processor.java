package com.xiaopeng.speech.protocol.node.social;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.jarvisproto.BackButtonClick;
import com.xiaopeng.speech.jarvisproto.VoiceButtonClick;
import com.xiaopeng.speech.protocol.event.SocialEvent;
/* loaded from: classes23.dex */
public class SocialNode_Processor implements ICommandProcessor {
    private SocialNode mTarget;

    public SocialNode_Processor(SocialNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1489293258:
                if (event.equals(SocialEvent.SOCIAL_MOTORCADE_OPEN)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1174519421:
                if (event.equals(VoiceButtonClick.EVENT)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1000693031:
                if (event.equals(SocialEvent.SOCIAL_REPLY_TOPIC)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -967605051:
                if (event.equals(SocialEvent.SOCIAL_QUIT_CHAT)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -470207025:
                if (event.equals(SocialEvent.SOCIAL_CREATE_TOPIC)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -411700335:
                if (event.equals(SocialEvent.SOCIAL_GRAB_MIC_CANCEL)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1065357708:
                if (event.equals(SocialEvent.SOCIAL_MOTORCADE_CLOSE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1292442478:
                if (event.equals(SocialEvent.SOCIAL_CONFIRM)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1454210858:
                if (event.equals(BackButtonClick.EVENT)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 1829874700:
                if (event.equals(SocialEvent.SOCIAL_CANCEL)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 2077193399:
                if (event.equals(SocialEvent.SOCIAL_GRAB_MIC)) {
                    c = 2;
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
                this.mTarget.onSocialMotorcadeOpen(event, data);
                return;
            case 1:
                this.mTarget.onSocialMotorcadeClose(event, data);
                return;
            case 2:
                this.mTarget.onSocialGrabMic(event, data);
                return;
            case 3:
                this.mTarget.onSocialGrabMicCancel(event, data);
                return;
            case 4:
                this.mTarget.onSocialCreateTopic(event, data);
                return;
            case 5:
                this.mTarget.onSocialReplyTopic(event, data);
                return;
            case 6:
                this.mTarget.onSocialQuitChat(event, data);
                return;
            case 7:
                this.mTarget.onSocialConfirm(event, data);
                return;
            case '\b':
                this.mTarget.onSocialCancel(event, data);
                return;
            case '\t':
                this.mTarget.onVoiceButtonClick(event, data);
                return;
            case '\n':
                this.mTarget.onBackButtonClick(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{SocialEvent.SOCIAL_MOTORCADE_OPEN, SocialEvent.SOCIAL_MOTORCADE_CLOSE, SocialEvent.SOCIAL_GRAB_MIC, SocialEvent.SOCIAL_GRAB_MIC_CANCEL, SocialEvent.SOCIAL_CREATE_TOPIC, SocialEvent.SOCIAL_REPLY_TOPIC, SocialEvent.SOCIAL_QUIT_CHAT, SocialEvent.SOCIAL_CONFIRM, SocialEvent.SOCIAL_CANCEL, VoiceButtonClick.EVENT, BackButtonClick.EVENT};
    }
}
