package com.xiaopeng.speech.protocol.node.tts;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.TtsEvent;
/* loaded from: classes23.dex */
public class TtsNode_Processor implements ICommandProcessor {
    private TtsNode mTarget;

    public TtsNode_Processor(TtsNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1878648165:
                if (event.equals(TtsEvent.SPEECH_TTS_START)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1723602740:
                if (event.equals(TtsEvent.SPEECH_TTS_ECHO)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1163978412:
                if (event.equals(TtsEvent.SPEECH_TTS_END)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -201524934:
                if (event.equals(TtsEvent.TTS_TIMBRE_SETTING)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            this.mTarget.onTtsStart(event, data);
        } else if (c == 1) {
            this.mTarget.onTtsEnd(event, data);
        } else if (c == 2) {
            this.mTarget.onTtsTimbreSetting(event, data);
        } else if (c == 3) {
            this.mTarget.onTtsEcho(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{TtsEvent.SPEECH_TTS_START, TtsEvent.SPEECH_TTS_END, TtsEvent.TTS_TIMBRE_SETTING, TtsEvent.SPEECH_TTS_ECHO};
    }
}
