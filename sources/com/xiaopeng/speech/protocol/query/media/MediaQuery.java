package com.xiaopeng.speech.protocol.query.media;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.QueryMediaEvent;
/* loaded from: classes23.dex */
public class MediaQuery extends SpeechQuery<IMediaQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.GET_INFO_QUERY)
    public String getMediaInfo(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).getMediaInfo();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.PLAY)
    public int play(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).play(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.PAUSE)
    public int pause(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).pause(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.RESUME)
    public int resume(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).resume(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.STOP)
    public int stop(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).stop(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.CLOSE)
    public int close(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).close(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.PREV)
    public int prev(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).prev(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.NEXT)
    public int next(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).next(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.PLAY_MODE)
    public int playMode(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).playMode(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.CONTROL_COLLECT)
    public int collect(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).collect(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.CANCEL_COLLECT)
    public int cancelCollect(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).cancelCollect(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.FORWARD)
    public int forward(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).forward(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.BACKWARD)
    public int backward(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).backward(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.SETTIME)
    public int setTime(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).setTime(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.MEDIA_MUSIC_PLAY_LIST)
    public int mediaListPlay(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).mediaListPlay(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryMediaEvent.MEDIA_AUDIOBOOK_PLAY_LIST)
    public int mediaAudioBookListPlay(String event, String data) {
        return ((IMediaQueryCaller) this.mQueryCaller).mediaListPlay(data);
    }
}
