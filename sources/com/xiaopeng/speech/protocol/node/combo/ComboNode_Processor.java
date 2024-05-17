package com.xiaopeng.speech.protocol.node.combo;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.ComboEvent;
/* loaded from: classes23.dex */
public class ComboNode_Processor implements ICommandProcessor {
    private ComboNode mTarget;

    public ComboNode_Processor(ComboNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2090731182:
                if (event.equals(ComboEvent.EXIT_USER_MODE)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -2050544765:
                if (event.equals(ComboEvent.FAST_CLOSE_MODE_INVISIBLE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1861214251:
                if (event.equals(ComboEvent.DATA_MODE_WAIT_TTS)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -1439089727:
                if (event.equals(ComboEvent.MODE_FRIDGE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1170878131:
                if (event.equals(ComboEvent.MODE_BIO_OFF)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -814414287:
                if (event.equals(ComboEvent.MODE_WAIT)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case -780117185:
                if (event.equals(ComboEvent.MODE_VENTILATE_OFF)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -719028020:
                if (event.equals(ComboEvent.MODE_BIO)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -597830030:
                if (event.equals(ComboEvent.MODE_INVISIBLE_OFF)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -260120463:
                if (event.equals(ComboEvent.MODE_INVISIBLE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 174005252:
                if (event.equals(ComboEvent.DATA_MODE_VENTILATE_TTS)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 356292407:
                if (event.equals(ComboEvent.DATA_MODE_INVISIBLE_TTS)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 802818498:
                if (event.equals(ComboEvent.MODE_FRIDGE_OFF)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 1008063422:
                if (event.equals(ComboEvent.MODE_VENTILATE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1059045797:
                if (event.equals(ComboEvent.DATA_MODE_FRIDGE_TTS)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 1114869502:
                if (event.equals(ComboEvent.DATA_MODE_MEDITATION_TTS)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1618652110:
                if (event.equals(ComboEvent.ENTER_USER_MODE)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case 1682644018:
                if (event.equals(ComboEvent.MODE_WAIT_OFF)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 2039944914:
                if (event.equals(ComboEvent.DATA_MODE_BIO_TTS)) {
                    c = '\b';
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
                this.mTarget.onModeBio(event, data);
                return;
            case 1:
                this.mTarget.onModeVentilate(event, data);
                return;
            case 2:
                this.mTarget.onModeInvisible(event, data);
                return;
            case 3:
                this.mTarget.onModeInvisibleOff(event, data);
                return;
            case 4:
                this.mTarget.onModeFridge(event, data);
                return;
            case 5:
                this.mTarget.onDataModeInvisibleTts(event, data);
                return;
            case 6:
                this.mTarget.onDataModeMeditationTts(event, data);
                return;
            case 7:
                this.mTarget.onFastCloseModeInvisible(event, data);
                return;
            case '\b':
                this.mTarget.onDataModeBioTts(event, data);
                return;
            case '\t':
                this.mTarget.onDataModeVentilateTts(event, data);
                return;
            case '\n':
                this.mTarget.onDataModeFridgeTts(event, data);
                return;
            case 11:
                this.mTarget.onModeBioOff(event, data);
                return;
            case '\f':
                this.mTarget.onModeVentilateOff(event, data);
                return;
            case '\r':
                this.mTarget.onModeFridgeOff(event, data);
                return;
            case 14:
                this.mTarget.onDataModeWaitTts(event, data);
                return;
            case 15:
                this.mTarget.onModeWait(event, data);
                return;
            case 16:
                this.mTarget.onModeWaitOff(event, data);
                return;
            case 17:
                this.mTarget.enterUserMode(event, data);
                return;
            case 18:
                this.mTarget.exitUserModel(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{ComboEvent.MODE_BIO, ComboEvent.MODE_VENTILATE, ComboEvent.MODE_INVISIBLE, ComboEvent.MODE_INVISIBLE_OFF, ComboEvent.MODE_FRIDGE, ComboEvent.DATA_MODE_INVISIBLE_TTS, ComboEvent.DATA_MODE_MEDITATION_TTS, ComboEvent.FAST_CLOSE_MODE_INVISIBLE, ComboEvent.DATA_MODE_BIO_TTS, ComboEvent.DATA_MODE_VENTILATE_TTS, ComboEvent.DATA_MODE_FRIDGE_TTS, ComboEvent.MODE_BIO_OFF, ComboEvent.MODE_VENTILATE_OFF, ComboEvent.MODE_FRIDGE_OFF, ComboEvent.DATA_MODE_WAIT_TTS, ComboEvent.MODE_WAIT, ComboEvent.MODE_WAIT_OFF, ComboEvent.ENTER_USER_MODE, ComboEvent.EXIT_USER_MODE};
    }
}
