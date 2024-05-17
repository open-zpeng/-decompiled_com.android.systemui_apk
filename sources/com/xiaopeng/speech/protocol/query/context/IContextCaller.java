package com.xiaopeng.speech.protocol.query.context;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface IContextCaller extends IQueryCaller {
    int getWidgetCurrLocation();

    String getWidgetId();

    String getWidgetInfo();

    int getWidgetListSize();

    int getWidgetPageSize();

    String getWidgetType();

    default int getInfoFlowScrollToTop() {
        return -1;
    }

    default int getInfoFlowScrollToBottom() {
        return -1;
    }

    default int getInfoFlowOnePage() {
        return -1;
    }
}
