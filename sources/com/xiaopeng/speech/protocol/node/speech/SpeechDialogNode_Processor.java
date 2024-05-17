package com.xiaopeng.speech.protocol.node.speech;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.SpeechDialogEvent;
/* loaded from: classes23.dex */
public class SpeechDialogNode_Processor implements ICommandProcessor {
    private SpeechDialogNode mTarget;

    public SpeechDialogNode_Processor(SpeechDialogNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1928182778:
                if (event.equals(SpeechDialogEvent.SET_SCREEN_ON)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1866559770:
                if (event.equals(SpeechDialogEvent.FAST_SPEECH_OFF)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -1817558719:
                if (event.equals(SpeechDialogEvent.REFRESH_CARSPEECHSERVICE_UI)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -1676326235:
                if (event.equals(SpeechDialogEvent.SWITCH_SPEECH_CONTINUOUS_ENABLE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1440570874:
                if (event.equals(SpeechDialogEvent.SWITCH_SPEECH_CONTINUOUS_DISABLED)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1181317093:
                if (event.equals(SpeechDialogEvent.SYSTEM_LISTENTIME_ACCOMPANY_CLOSE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -829008729:
                if (event.equals(SpeechDialogEvent.MULTI_SPEECH_OFF)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -719478873:
                if (event.equals(SpeechDialogEvent.MULTI_SPEECH_ON)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -608339784:
                if (event.equals(SpeechDialogEvent.SCRIPT_WIDGET)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -412443633:
                if (event.equals(SpeechDialogEvent.ROUTE_OPEN_SCENE_GUIDE_WINDOW)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -265464658:
                if (event.equals(SpeechDialogEvent.ROUTE_CLOSE_SCENE_GUIDE_WINDOW)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -176140078:
                if (event.equals(SpeechDialogEvent.ROUTE_CLOSE_SPEECH_WINDOW)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -45507067:
                if (event.equals(SpeechDialogEvent.SOUND_AREA_CLOSE)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 239348679:
                if (event.equals(SpeechDialogEvent.SYSTEM_LISTENTIME_ACCOMPANY_OPEN)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 287100808:
                if (event.equals(SpeechDialogEvent.FULL_TIME_SPEECH_ON)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case 310190310:
                if (event.equals(SpeechDialogEvent.FULL_TIME_SPEECH_OFF)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 771072392:
                if (event.equals(SpeechDialogEvent.FAST_SPEECH_ON)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 1245819037:
                if (event.equals(SpeechDialogEvent.SOUND_AREA_OPEN)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 1664069701:
                if (event.equals(SpeechDialogEvent.INJECT_TRIGGER_WORDS)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case 1765586190:
                if (event.equals(SpeechDialogEvent.SHOW_CHILDWATCH_LOCATION)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 1788977038:
                if (event.equals(SpeechDialogEvent.GLOBAL_SPEECH_EXIT)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1929924035:
                if (event.equals(SpeechDialogEvent.SCRIPT_QUIT)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 1975136465:
                if (event.equals(SpeechDialogEvent.ROUTE_OPEN_SPEECH_WINDOW)) {
                    c = 1;
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
                this.mTarget.onCloseWindow(event, data);
                return;
            case 1:
                this.mTarget.onOpenWindow(event, data);
                return;
            case 2:
                this.mTarget.onOpenSceneGuideWindow(event, data);
                return;
            case 3:
                this.mTarget.onCloseSceneGuideWindow(event, data);
                return;
            case 4:
                this.mTarget.onOpenSpeechSceneSetting(event, data);
                return;
            case 5:
                this.mTarget.onCloseSpeechSceneSetting(event, data);
                return;
            case 6:
                this.mTarget.onOpenSuperDialogue(event, data);
                return;
            case 7:
                this.mTarget.onCloseSuperDialogue(event, data);
                return;
            case '\b':
                this.mTarget.onRefreshUi(event, data);
                return;
            case '\t':
                this.mTarget.onGlobalSpeechExit(event, data);
                return;
            case '\n':
                this.mTarget.setScreenOn(event, data);
                return;
            case 11:
                this.mTarget.onScriptWidget(event, data);
                return;
            case '\f':
                this.mTarget.onScriptQuit(event, data);
                return;
            case '\r':
                this.mTarget.showChildwatchLocation(event, data);
                return;
            case 14:
                this.mTarget.onSoundAreaOpen(event, data);
                return;
            case 15:
                this.mTarget.onSoundAreaClose(event, data);
                return;
            case 16:
                this.mTarget.openFastSpeech(event, data);
                return;
            case 17:
                this.mTarget.closeFastSpeech(event, data);
                return;
            case 18:
                this.mTarget.openMultiSpeech(event, data);
                return;
            case 19:
                this.mTarget.closeMultiSpeech(event, data);
                return;
            case 20:
                this.mTarget.openFullTimeSpeech(event, data);
                return;
            case 21:
                this.mTarget.closeFullTimeSpeech(event, data);
                return;
            case 22:
                this.mTarget.injectTriggerWords(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{SpeechDialogEvent.ROUTE_CLOSE_SPEECH_WINDOW, SpeechDialogEvent.ROUTE_OPEN_SPEECH_WINDOW, SpeechDialogEvent.ROUTE_OPEN_SCENE_GUIDE_WINDOW, SpeechDialogEvent.ROUTE_CLOSE_SCENE_GUIDE_WINDOW, SpeechDialogEvent.SWITCH_SPEECH_CONTINUOUS_ENABLE, SpeechDialogEvent.SWITCH_SPEECH_CONTINUOUS_DISABLED, SpeechDialogEvent.SYSTEM_LISTENTIME_ACCOMPANY_OPEN, SpeechDialogEvent.SYSTEM_LISTENTIME_ACCOMPANY_CLOSE, SpeechDialogEvent.REFRESH_CARSPEECHSERVICE_UI, SpeechDialogEvent.GLOBAL_SPEECH_EXIT, SpeechDialogEvent.SET_SCREEN_ON, SpeechDialogEvent.SCRIPT_WIDGET, SpeechDialogEvent.SCRIPT_QUIT, SpeechDialogEvent.SHOW_CHILDWATCH_LOCATION, SpeechDialogEvent.SOUND_AREA_OPEN, SpeechDialogEvent.SOUND_AREA_CLOSE, SpeechDialogEvent.FAST_SPEECH_ON, SpeechDialogEvent.FAST_SPEECH_OFF, SpeechDialogEvent.MULTI_SPEECH_ON, SpeechDialogEvent.MULTI_SPEECH_OFF, SpeechDialogEvent.FULL_TIME_SPEECH_ON, SpeechDialogEvent.FULL_TIME_SPEECH_OFF, SpeechDialogEvent.INJECT_TRIGGER_WORDS};
    }
}
