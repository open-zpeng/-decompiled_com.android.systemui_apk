package com.xiaopeng.speech.protocol.node.video;

import com.xiaopeng.speech.INodeListener;
/* loaded from: classes23.dex */
public interface VideoListener extends INodeListener {
    default void onVideoPrev(String data) {
    }

    default void onVideoNext(String data) {
    }

    default void onVideoSet(String data) {
    }

    default void onVideoForward(String data) {
    }

    default void onVideoBackward(String data) {
    }

    default void onVideoSettime(String data) {
    }

    default void onVideoPause(String data) {
    }

    default void onVideoResume(String data) {
    }

    default void onVideoSpeedUp(String data) {
    }

    default void onVideoSpeedDown(String data) {
    }

    default void onVideoSpeedSet(String data) {
    }

    default void onVideoEnd(String data) {
    }

    default void onVideoClarityUP(String data) {
    }

    default void onVideoClarityDown(String data) {
    }

    default void onVideoClaritySet(String data) {
    }

    default void onVideoCollect(String data) {
    }

    default void onVideoCollectCancel(String data) {
    }

    default void onVideoSkipBegin(String data) {
    }

    default void onVideoSkipEnd(String data) {
    }
}
