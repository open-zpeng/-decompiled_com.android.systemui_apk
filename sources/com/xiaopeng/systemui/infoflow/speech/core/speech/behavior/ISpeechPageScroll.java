package com.xiaopeng.systemui.infoflow.speech.core.speech.behavior;
/* loaded from: classes24.dex */
public interface ISpeechPageScroll {
    int getInfoFlowOnePage();

    int getInfoFlowScrollToBottom();

    int getInfoFlowScrollToTop();

    int getWidgetCurrLocation();

    int getWidgetListSize();

    int getWidgetPageSize();

    void onPageNext();

    void onPagePrev();

    void onPageSetLow();

    void onPageTopping();
}
