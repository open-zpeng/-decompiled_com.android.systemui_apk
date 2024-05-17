package com.xiaopeng.speech.protocol.node.carcontrol;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.MeditationMusicEvent;
/* loaded from: classes23.dex */
public class MeditationMusicNode_Processor implements ICommandProcessor {
    private MeditationMusicNode mTarget;

    public MeditationMusicNode_Processor(MeditationMusicNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2031117940:
                if (event.equals(MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_PAUSE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1520748375:
                if (event.equals(MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_RESUME)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1874087069:
                if (event.equals(MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_NEXT)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1874158557:
                if (event.equals(MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_PREV)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            this.mTarget.onPrev(event, data);
        } else if (c == 1) {
            this.mTarget.onNext(event, data);
        } else if (c == 2) {
            this.mTarget.onPause(event, data);
        } else if (c == 3) {
            this.mTarget.onResume(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_PREV, MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_NEXT, MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_PAUSE, MeditationMusicEvent.MEDITATION_MUSIC_CONTROL_RESUME};
    }
}
