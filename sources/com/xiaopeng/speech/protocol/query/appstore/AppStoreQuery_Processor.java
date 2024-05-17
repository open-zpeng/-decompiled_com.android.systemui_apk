package com.xiaopeng.speech.protocol.query.appstore;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QueryAppStoreEvent;
/* loaded from: classes23.dex */
public class AppStoreQuery_Processor implements IQueryProcessor {
    private AppStoreQuery mTarget;

    public AppStoreQuery_Processor(AppStoreQuery target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        if (((event.hashCode() == 305415920 && event.equals(QueryAppStoreEvent.GET_OPEN_STATUS)) ? (char) 0 : (char) 65535) == 0) {
            return Integer.valueOf(this.mTarget.getOpenStatus(event, data));
        }
        return null;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryAppStoreEvent.GET_OPEN_STATUS};
    }
}
