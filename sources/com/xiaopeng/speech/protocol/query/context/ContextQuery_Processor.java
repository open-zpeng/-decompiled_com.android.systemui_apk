package com.xiaopeng.speech.protocol.query.context;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.QueryContextEvent;
/* loaded from: classes23.dex */
public class ContextQuery_Processor implements IQueryProcessor {
    private ContextQuery mTarget;

    public ContextQuery_Processor(ContextQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1787130714:
                if (event.equals(QueryContextEvent.CONTEXT_WIDGET_CURR_LOCATION)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -316439674:
                if (event.equals(QueryContextEvent.CONTEXT_WIDGET_ID)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -238543034:
                if (event.equals(QueryContextEvent.CONTEXT_INFO_LIST_TOP)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -45776075:
                if (event.equals(QueryContextEvent.CONTEXT_WIDGET_PAGE_SIZE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 844164185:
                if (event.equals(QueryContextEvent.CONTEXT_WIDGET_INFO)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 844502757:
                if (event.equals(QueryContextEvent.CONTEXT_WIDGET_TYPE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1220258458:
                if (event.equals(QueryContextEvent.CONTEXT_INFO_LIST_BOTTOM)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 2078305126:
                if (event.equals(QueryContextEvent.CONTEXT_INFO_LIST_ONEPAGE)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 2141234822:
                if (event.equals(QueryContextEvent.CONTEXT_WIDGET_LIST_SIZE)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return Integer.valueOf(this.mTarget.getWidgetListSize(event, data));
            case 1:
                return Integer.valueOf(this.mTarget.getWidgetPageSize(event, data));
            case 2:
                return Integer.valueOf(this.mTarget.getWidgetCurrLocation(event, data));
            case 3:
                return this.mTarget.getWidgetId(event, data);
            case 4:
                return this.mTarget.getWidgetType(event, data);
            case 5:
                return this.mTarget.getWidgetInfo(event, data);
            case 6:
                return Integer.valueOf(this.mTarget.getInfoFlowScrollToTop(event, data));
            case 7:
                return Integer.valueOf(this.mTarget.getInfoFlowScrollToBottom(event, data));
            case '\b':
                return Integer.valueOf(this.mTarget.getInfoFlowOnePage(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryContextEvent.CONTEXT_WIDGET_LIST_SIZE, QueryContextEvent.CONTEXT_WIDGET_PAGE_SIZE, QueryContextEvent.CONTEXT_WIDGET_CURR_LOCATION, QueryContextEvent.CONTEXT_WIDGET_ID, QueryContextEvent.CONTEXT_WIDGET_TYPE, QueryContextEvent.CONTEXT_WIDGET_INFO, QueryContextEvent.CONTEXT_INFO_LIST_TOP, QueryContextEvent.CONTEXT_INFO_LIST_BOTTOM, QueryContextEvent.CONTEXT_INFO_LIST_ONEPAGE};
    }
}
