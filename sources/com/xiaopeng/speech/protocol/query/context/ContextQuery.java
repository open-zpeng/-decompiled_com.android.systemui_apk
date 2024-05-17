package com.xiaopeng.speech.protocol.query.context;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.QueryContextEvent;
/* loaded from: classes23.dex */
public class ContextQuery extends SpeechQuery<IContextCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryContextEvent.CONTEXT_WIDGET_LIST_SIZE)
    public int getWidgetListSize(String event, String data) {
        return ((IContextCaller) this.mQueryCaller).getWidgetListSize();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryContextEvent.CONTEXT_WIDGET_PAGE_SIZE)
    public int getWidgetPageSize(String event, String data) {
        return ((IContextCaller) this.mQueryCaller).getWidgetPageSize();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryContextEvent.CONTEXT_WIDGET_CURR_LOCATION)
    public int getWidgetCurrLocation(String event, String data) {
        return ((IContextCaller) this.mQueryCaller).getWidgetCurrLocation();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryContextEvent.CONTEXT_WIDGET_ID)
    public String getWidgetId(String event, String data) {
        return ((IContextCaller) this.mQueryCaller).getWidgetId();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryContextEvent.CONTEXT_WIDGET_TYPE)
    public String getWidgetType(String event, String data) {
        return ((IContextCaller) this.mQueryCaller).getWidgetType();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryContextEvent.CONTEXT_WIDGET_INFO)
    public String getWidgetInfo(String event, String data) {
        return ((IContextCaller) this.mQueryCaller).getWidgetInfo();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryContextEvent.CONTEXT_INFO_LIST_TOP)
    public int getInfoFlowScrollToTop(String event, String data) {
        return ((IContextCaller) this.mQueryCaller).getInfoFlowScrollToTop();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryContextEvent.CONTEXT_INFO_LIST_BOTTOM)
    public int getInfoFlowScrollToBottom(String event, String data) {
        return ((IContextCaller) this.mQueryCaller).getInfoFlowScrollToBottom();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryContextEvent.CONTEXT_INFO_LIST_ONEPAGE)
    public int getInfoFlowOnePage(String event, String data) {
        return ((IContextCaller) this.mQueryCaller).getInfoFlowOnePage();
    }
}
