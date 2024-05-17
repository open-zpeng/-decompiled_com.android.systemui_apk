package com.xiaopeng.speech.protocol.node.avatar;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.jarvisproto.AvatarExpressionEvent;
import com.xiaopeng.speech.jarvisproto.AvatarWakeupDisable;
import com.xiaopeng.speech.jarvisproto.AvatarWakeupEnable;
import com.xiaopeng.speech.jarvisproto.DMListening;
import com.xiaopeng.speech.jarvisproto.DMPersonalRun;
import com.xiaopeng.speech.jarvisproto.DMPersonalRunExit;
import com.xiaopeng.speech.jarvisproto.DMRecognized;
import com.xiaopeng.speech.jarvisproto.DMSpeaking;
import com.xiaopeng.speech.jarvisproto.DMUnderstanding;
import com.xiaopeng.speech.protocol.event.ContextEvent;
/* loaded from: classes23.dex */
public class AvatarNode_Processor implements ICommandProcessor {
    private AvatarNode mTarget;

    public AvatarNode_Processor(AvatarNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1543075995:
                if (event.equals(AvatarWakeupDisable.EVENT)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -1187871553:
                if (event.equals(DMListening.EVENT)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -671951780:
                if (event.equals(ContextEvent.WIDGET_CUSTOM)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -511944871:
                if (event.equals(DMPersonalRun.EVENT)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -229200237:
                if (event.equals(ContextEvent.WIDGET_SEARCH)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -206519100:
                if (event.equals(AvatarExpressionEvent.EVENT)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 260029798:
                if (event.equals(AvatarWakeupEnable.EVENT)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 402709913:
                if (event.equals(ContextEvent.WIDGET_MEDIA)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 843973307:
                if (event.equals(ContextEvent.WIDGET_CARD)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 844249161:
                if (event.equals(ContextEvent.WIDGET_LIST)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 844483800:
                if (event.equals(ContextEvent.WIDGET_TEXT)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 1309193744:
                if (event.equals(DMPersonalRunExit.EVENT)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1330018892:
                if (event.equals(DMSpeaking.EVENT)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1386014226:
                if (event.equals(DMRecognized.EVENT)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1566189864:
                if (event.equals(DMUnderstanding.EVENT)) {
                    c = 3;
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
                this.mTarget.onSilence(event, data);
                return;
            case 1:
                this.mTarget.onListening(event, data);
                return;
            case 2:
                this.mTarget.onSpeaking(event, data);
                return;
            case 3:
                this.mTarget.onUnderstanding(event, data);
                return;
            case 4:
                this.mTarget.onPersonalRunning(event, data);
                return;
            case 5:
                this.mTarget.onPersonalExit(event, data);
                return;
            case 6:
                this.mTarget.onAvatarExpression(event, data);
                return;
            case 7:
                this.mTarget.onWidgetCustom(event, data);
                return;
            case '\b':
                this.mTarget.onWidgetText(event, data);
                return;
            case '\t':
                this.mTarget.onWidgetList(event, data);
                return;
            case '\n':
                this.mTarget.onWidgetMedia(event, data);
                return;
            case 11:
                this.mTarget.onWidgetCard(event, data);
                return;
            case '\f':
                this.mTarget.onWidgetSearch(event, data);
                return;
            case '\r':
                this.mTarget.onAvatarWakerupEnable(event, data);
                return;
            case 14:
                this.mTarget.onAvatarWakerupDisable(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{DMRecognized.EVENT, DMListening.EVENT, DMSpeaking.EVENT, DMUnderstanding.EVENT, DMPersonalRun.EVENT, DMPersonalRunExit.EVENT, AvatarExpressionEvent.EVENT, ContextEvent.WIDGET_CUSTOM, ContextEvent.WIDGET_TEXT, ContextEvent.WIDGET_LIST, ContextEvent.WIDGET_MEDIA, ContextEvent.WIDGET_CARD, ContextEvent.WIDGET_SEARCH, AvatarWakeupEnable.EVENT, AvatarWakeupDisable.EVENT};
    }
}
