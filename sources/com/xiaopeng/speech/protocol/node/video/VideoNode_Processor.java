package com.xiaopeng.speech.protocol.node.video;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.query.QueryVideoEvent;
/* loaded from: classes23.dex */
public class VideoNode_Processor implements ICommandProcessor {
    private VideoNode mTarget;

    public VideoNode_Processor(VideoNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1348076748:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_END)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -1348063557:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_SET)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1240049296:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_SPEED_DOWN)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1081395201:
                if (event.equals(QueryVideoEvent.VIDEO_SKIP_END)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -1046937853:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_COLLECT)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case -317082156:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_SPEED_SET)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -10441720:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_SETTIME)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 158265037:
                if (event.equals(QueryVideoEvent.VIDEO_SKIP_BEGIN)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case 270519299:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_CLARITY_DOWN)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 405413609:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_SPEED_UP)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 1023744572:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_CLARITY_UP)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 1159553978:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_NEXT)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1159625466:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_PREV)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1227086388:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_BACKWARD)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1588160015:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_PAUSE)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1621438622:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_FORWARD)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1671308513:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_CLARITY_SET)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 1989573573:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_COLLECT_CANCEL)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 2049215284:
                if (event.equals(QueryVideoEvent.VIDEO_PLAY_RESUME)) {
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
                this.mTarget.onVideoPrev(event, data);
                return;
            case 1:
                this.mTarget.onVideoNext(event, data);
                return;
            case 2:
                this.mTarget.onVideoSet(event, data);
                return;
            case 3:
                this.mTarget.onVideoForward(event, data);
                return;
            case 4:
                this.mTarget.onVideoBackward(event, data);
                return;
            case 5:
                this.mTarget.onVideoSettime(event, data);
                return;
            case 6:
                this.mTarget.onVideoPause(event, data);
                return;
            case 7:
                this.mTarget.onVideoResume(event, data);
                return;
            case '\b':
                this.mTarget.onVideoSpeedUp(event, data);
                return;
            case '\t':
                this.mTarget.onVideoSpeedDown(event, data);
                return;
            case '\n':
                this.mTarget.onVideoSpeedSet(event, data);
                return;
            case 11:
                this.mTarget.onVideoEnd(event, data);
                return;
            case '\f':
                this.mTarget.onVideoClarityUP(event, data);
                return;
            case '\r':
                this.mTarget.onVideoClarityDown(event, data);
                return;
            case 14:
                this.mTarget.onVideoClaritySet(event, data);
                return;
            case 15:
                this.mTarget.onVideoCollect(event, data);
                return;
            case 16:
                this.mTarget.onVideoCollectCancel(event, data);
                return;
            case 17:
                this.mTarget.onVideoSkipBegin(event, data);
                return;
            case 18:
                this.mTarget.onVideoSkipEnd(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{QueryVideoEvent.VIDEO_PLAY_PREV, QueryVideoEvent.VIDEO_PLAY_NEXT, QueryVideoEvent.VIDEO_PLAY_SET, QueryVideoEvent.VIDEO_PLAY_FORWARD, QueryVideoEvent.VIDEO_PLAY_BACKWARD, QueryVideoEvent.VIDEO_PLAY_SETTIME, QueryVideoEvent.VIDEO_PLAY_PAUSE, QueryVideoEvent.VIDEO_PLAY_RESUME, QueryVideoEvent.VIDEO_PLAY_SPEED_UP, QueryVideoEvent.VIDEO_PLAY_SPEED_DOWN, QueryVideoEvent.VIDEO_PLAY_SPEED_SET, QueryVideoEvent.VIDEO_PLAY_END, QueryVideoEvent.VIDEO_PLAY_CLARITY_UP, QueryVideoEvent.VIDEO_PLAY_CLARITY_DOWN, QueryVideoEvent.VIDEO_PLAY_CLARITY_SET, QueryVideoEvent.VIDEO_PLAY_COLLECT, QueryVideoEvent.VIDEO_PLAY_COLLECT_CANCEL, QueryVideoEvent.VIDEO_SKIP_BEGIN, QueryVideoEvent.VIDEO_SKIP_END};
    }
}
