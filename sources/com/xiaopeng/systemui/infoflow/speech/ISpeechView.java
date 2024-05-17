package com.xiaopeng.systemui.infoflow.speech;

import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.tts.TtsEchoValue;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
/* loaded from: classes24.dex */
public interface ISpeechView {
    public static final int EXTRA_TYPE_NAVI_POI = 1;
    public static final int EXTRA_TYPE_NAVI_ROUTE = 0;
    public static final int OTHER = 2;

    void hideHint();

    void onBugReportBegin();

    void onBugReportEnd();

    void onDialogEnd(DialogEndReason dialogEndReason);

    void onDialogStart();

    void onPanelVisibilityChanged(boolean z);

    void onSoundAreaStatus(SoundAreaStatus soundAreaStatus);

    void onTtsEcho(TtsEchoValue ttsEchoValue);

    void onWidgetListCancelFocus(int i);

    void onWidgetListExpand();

    void onWidgetListFocus(int i);

    void onWidgetListFold();

    void onWidgetListSelect(int i);

    void onWidgetListStopCountdown();

    void showAsrBackground(boolean z);

    void showHint(String str, String str2);

    void showListWidget(SpeechWidget speechWidget);

    void showVoiceWaveAnim(int i, int i2, int i3);

    void stopVoiceWaveAnim();
}
