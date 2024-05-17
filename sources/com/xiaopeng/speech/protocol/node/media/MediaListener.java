package com.xiaopeng.speech.protocol.node.media;

import com.xiaopeng.speech.INodeListener;
/* loaded from: classes23.dex */
public interface MediaListener extends INodeListener {
    default void onPlay(String appName) {
    }

    default void onPause() {
    }

    default void onResume() {
    }

    default void onStop() {
    }

    default void onPrev() {
    }

    default void onNext() {
    }

    default void onPlayMode(String mode) {
    }

    default void onCollect() {
    }

    default void onCancelCollect() {
    }

    default void onForward(int second) {
    }

    default void onBackward(int second) {
    }

    default void onSetTime(int second) {
    }
}
