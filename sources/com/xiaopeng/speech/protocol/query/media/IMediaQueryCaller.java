package com.xiaopeng.speech.protocol.query.media;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface IMediaQueryCaller extends IQueryCaller {
    default String getMediaInfo() {
        return null;
    }

    default int play(String data) {
        return 0;
    }

    default int pause(String data) {
        return 0;
    }

    default int resume(String data) {
        return 0;
    }

    default int stop(String data) {
        return 0;
    }

    default int close(String data) {
        return 0;
    }

    default int prev(String data) {
        return 0;
    }

    default int next(String data) {
        return 0;
    }

    default int playMode(String data) {
        return 0;
    }

    default int collect(String data) {
        return 0;
    }

    default int cancelCollect(String data) {
        return 0;
    }

    default int forward(String data) {
        return 0;
    }

    default int backward(String data) {
        return 0;
    }

    default int setTime(String data) {
        return 0;
    }

    default int mediaListPlay(String data) {
        return 0;
    }

    default int mediaAudioBookListPlay(String data) {
        return 0;
    }
}
