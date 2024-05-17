package com.xiaopeng.systemui.speech.data;

import androidx.annotation.UiThread;
import com.xiaopeng.speech.jarvisproto.DialogSoundAreaStatus;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.WakeupReason;
@UiThread
/* loaded from: classes24.dex */
public interface ISpeechCallBack {
    @UiThread
    default void onDialogStart(WakeupReason wakeupReason) {
    }

    @UiThread
    default void onDialogEnd(DialogEndReason endReason) {
    }

    @UiThread
    default void onSoundAreaStatusChanged(SoundAreaStatus status) {
    }

    @UiThread
    default void onDialogSoundAreaStatusChanged(DialogSoundAreaStatus status) {
    }

    @UiThread
    default void onInputText(SpeechDataInput data) {
    }

    @UiThread
    default void onTtsEcho(SpeechDataEcho data) {
    }

    @UiThread
    default void onTipsListeningShow(SpeechDataHint dataHint) {
    }
}
