package com.xiaopeng.speech.protocol.query.media;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QueryMediaEvent;
/* loaded from: classes23.dex */
public class MediaQuery_Processor implements IQueryProcessor {
    private MediaQuery mTarget;

    public MediaQuery_Processor(MediaQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1975677602:
                if (event.equals(QueryMediaEvent.PLAY_MODE)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -1975656786:
                if (event.equals(QueryMediaEvent.NEXT)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1975591185:
                if (event.equals(QueryMediaEvent.PLAY)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1975585298:
                if (event.equals(QueryMediaEvent.PREV)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1975493699:
                if (event.equals(QueryMediaEvent.STOP)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1570967877:
                if (event.equals(QueryMediaEvent.FORWARD)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -1159670343:
                if (event.equals(QueryMediaEvent.CANCEL_COLLECT)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1125776995:
                if (event.equals(QueryMediaEvent.CLOSE)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1114093157:
                if (event.equals(QueryMediaEvent.PAUSE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -965975055:
                if (event.equals(QueryMediaEvent.MEDIA_MUSIC_PLAY_LIST)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -931837998:
                if (event.equals(QueryMediaEvent.GET_INFO_QUERY)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -116254424:
                if (event.equals(QueryMediaEvent.RESUME)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 183299757:
                if (event.equals(QueryMediaEvent.BACKWARD)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 282775051:
                if (event.equals(QueryMediaEvent.MEDIA_AUDIOBOOK_PLAY_LIST)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 542977935:
                if (event.equals(QueryMediaEvent.CONTROL_COLLECT)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1092119077:
                if (event.equals(QueryMediaEvent.SETTIME)) {
                    c = '\r';
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
                return this.mTarget.getMediaInfo(event, data);
            case 1:
                return Integer.valueOf(this.mTarget.play(event, data));
            case 2:
                return Integer.valueOf(this.mTarget.pause(event, data));
            case 3:
                return Integer.valueOf(this.mTarget.resume(event, data));
            case 4:
                return Integer.valueOf(this.mTarget.stop(event, data));
            case 5:
                return Integer.valueOf(this.mTarget.close(event, data));
            case 6:
                return Integer.valueOf(this.mTarget.prev(event, data));
            case 7:
                return Integer.valueOf(this.mTarget.next(event, data));
            case '\b':
                return Integer.valueOf(this.mTarget.playMode(event, data));
            case '\t':
                return Integer.valueOf(this.mTarget.collect(event, data));
            case '\n':
                return Integer.valueOf(this.mTarget.cancelCollect(event, data));
            case 11:
                return Integer.valueOf(this.mTarget.forward(event, data));
            case '\f':
                return Integer.valueOf(this.mTarget.backward(event, data));
            case '\r':
                return Integer.valueOf(this.mTarget.setTime(event, data));
            case 14:
                return Integer.valueOf(this.mTarget.mediaListPlay(event, data));
            case 15:
                return Integer.valueOf(this.mTarget.mediaAudioBookListPlay(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryMediaEvent.GET_INFO_QUERY, QueryMediaEvent.PLAY, QueryMediaEvent.PAUSE, QueryMediaEvent.RESUME, QueryMediaEvent.STOP, QueryMediaEvent.CLOSE, QueryMediaEvent.PREV, QueryMediaEvent.NEXT, QueryMediaEvent.PLAY_MODE, QueryMediaEvent.CONTROL_COLLECT, QueryMediaEvent.CANCEL_COLLECT, QueryMediaEvent.FORWARD, QueryMediaEvent.BACKWARD, QueryMediaEvent.SETTIME, QueryMediaEvent.MEDIA_MUSIC_PLAY_LIST, QueryMediaEvent.MEDIA_AUDIOBOOK_PLAY_LIST};
    }
}
