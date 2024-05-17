package com.xiaopeng.speech.protocol.node.media;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.MediaEvent;
/* loaded from: classes23.dex */
public class MediaNode_Processor implements ICommandProcessor {
    private MediaNode mTarget;

    public MediaNode_Processor(MediaNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1657429332:
                if (event.equals(MediaEvent.FORWARD)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -1563074367:
                if (event.equals(MediaEvent.PLAY_LOOP_ALL)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1524827811:
                if (event.equals(MediaEvent.NEXT)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1524762210:
                if (event.equals(MediaEvent.PLAY)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1524756323:
                if (event.equals(MediaEvent.PREV)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1524664724:
                if (event.equals(MediaEvent.STOP)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -665165651:
                if (event.equals(MediaEvent.PLAY_LOOP_RANDOM)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -661306345:
                if (event.equals(MediaEvent.RESUME)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -23296820:
                if (event.equals(MediaEvent.PAUSE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 226337192:
                if (event.equals(MediaEvent.CANCEL_COLLECT)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 826237568:
                if (event.equals(MediaEvent.CONTROL_COLLECT)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 999672104:
                if (event.equals(MediaEvent.PLAY_LOOP_SINGLE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1005657622:
                if (event.equals(MediaEvent.SETTIME)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 1797961948:
                if (event.equals(MediaEvent.BACKWARD)) {
                    c = '\f';
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
                this.mTarget.onPlay(event, data);
                return;
            case 1:
                this.mTarget.onPlayLoopSingle(event, data);
                return;
            case 2:
                this.mTarget.onPlayLoopAll(event, data);
                return;
            case 3:
                this.mTarget.onPlayLoopRandom(event, data);
                return;
            case 4:
                this.mTarget.onPause(event, data);
                return;
            case 5:
                this.mTarget.onResume(event, data);
                return;
            case 6:
                this.mTarget.onPrev(event, data);
                return;
            case 7:
                this.mTarget.onNext(event, data);
                return;
            case '\b':
                this.mTarget.onStop(event, data);
                return;
            case '\t':
                this.mTarget.onControlCollect(event, data);
                return;
            case '\n':
                this.mTarget.onCancelCollect(event, data);
                return;
            case 11:
                this.mTarget.onForward(event, data);
                return;
            case '\f':
                this.mTarget.onBackward(event, data);
                return;
            case '\r':
                this.mTarget.onSettime(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{MediaEvent.PLAY, MediaEvent.PLAY_LOOP_SINGLE, MediaEvent.PLAY_LOOP_ALL, MediaEvent.PLAY_LOOP_RANDOM, MediaEvent.PAUSE, MediaEvent.RESUME, MediaEvent.PREV, MediaEvent.NEXT, MediaEvent.STOP, MediaEvent.CONTROL_COLLECT, MediaEvent.CANCEL_COLLECT, MediaEvent.FORWARD, MediaEvent.BACKWARD, MediaEvent.SETTIME};
    }
}
