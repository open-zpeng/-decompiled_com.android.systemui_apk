package com.xiaopeng.systemui.infoflow.speech.core.speech.behavior;

import com.xiaopeng.speech.jarvisproto.AsrEvent;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
/* loaded from: classes24.dex */
public interface ISpeechContext {
    void onAsrEvent(int i);

    void onAsrEvent(AsrEvent asrEvent);

    void onBugReportBegin();

    void onBugReportEnd();

    void onExitRecommendCard();

    void onInputText(int i, String str, boolean z, boolean z2, boolean z3);

    void onOutputText(String str);

    void onSayWelcome(String str);

    void onShowWidget(SpeechWidget speechWidget);

    void onWidgetCancel(String str, String str2);

    void onWidgetListCancelFocus(int i);

    void onWidgetListExpend();

    void onWidgetListFocus(int i);

    void onWidgetListFold();

    void onWidgetListSelect(int i);

    void onWidgetListStopCountdown();

    void onWidgetRecommend(String str);

    void onWidgetText(String str);
}
