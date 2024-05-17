package com.xiaopeng.speech.protocol.node.speech;

import com.xiaopeng.speech.INodeListener;
/* loaded from: classes23.dex */
public interface SpeechDialogListener extends INodeListener {
    void onCloseSceneGuideWindow(String str);

    void onCloseSpeechSceneSetting();

    void onCloseWindow(String str);

    void onOpenSceneGuideWindow(String str);

    void onOpenSpeechSceneSetting();

    void onOpenWindow(String str);

    default void onOpenSuperDialogue() {
    }

    default void onCloseSuperDialogue() {
    }

    default void onRefreshUi(int type, boolean state) {
    }

    default void onGlobalSpeechExit() {
    }

    default void onScriptWidget(String widget) {
    }

    default void onScriptQuit() {
    }

    default void setScreenOn(int display_location) {
    }

    default void showChildwatchLocation(String data) {
    }

    default void onSoundAreaOpen(String data) {
    }

    default void onSoundAreaClose(String data) {
    }

    default void openFastSpeech(String data) {
    }

    default void closeFastSpeech(String data) {
    }

    default void openMultiSpeech(String data) {
    }

    default void closeMultiSpeech(String data) {
    }

    default void openFullTimeSpeech(String data) {
    }

    default void closeFullTimeSpeech(String data) {
    }

    default void injectTriggerWords(String data) {
    }
}
