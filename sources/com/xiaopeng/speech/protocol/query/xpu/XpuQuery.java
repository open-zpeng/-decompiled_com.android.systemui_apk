package com.xiaopeng.speech.protocol.query.xpu;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.QueryXpuEvent;
/* loaded from: classes23.dex */
public class XpuQuery extends SpeechQuery<IXpuCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryXpuEvent.XPU_IS_ON_AUTOPILOT)
    public int getAutoPilotStatus(String event, String data) {
        return ((IXpuCaller) this.mQueryCaller).getAutoPilotStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryXpuEvent.XPU_IS_ON_ALC)
    public int getALCStatus(String event, String data) {
        return ((IXpuCaller) this.mQueryCaller).getALCStatus();
    }
}
