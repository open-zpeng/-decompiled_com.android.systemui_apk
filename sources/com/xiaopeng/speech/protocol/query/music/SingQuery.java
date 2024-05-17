package com.xiaopeng.speech.protocol.query.music;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.QuerySingEvent;
/* loaded from: classes23.dex */
public class SingQuery extends SpeechQuery<ISingQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySingEvent.GET_SING_STATUS_QUERY)
    public int getSingStatus(String event, String data) {
        return ((ISingQueryCaller) this.mQueryCaller).getSingStatus(data);
    }
}
