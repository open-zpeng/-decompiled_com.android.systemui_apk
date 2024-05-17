package com.xiaopeng.speech.protocol.query.music;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QuerySingEvent;
/* loaded from: classes23.dex */
public class SingQuery_Processor implements IQueryProcessor {
    private SingQuery mTarget;

    public SingQuery_Processor(SingQuery target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        if (((event.hashCode() == -314142516 && event.equals(QuerySingEvent.GET_SING_STATUS_QUERY)) ? (char) 0 : (char) 65535) == 0) {
            return Integer.valueOf(this.mTarget.getSingStatus(event, data));
        }
        return null;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QuerySingEvent.GET_SING_STATUS_QUERY};
    }
}
