package com.xiaopeng.speech.protocol.query.ota;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.OtaEvent;
/* loaded from: classes23.dex */
public class OtaQuery extends SpeechQuery<IOtaCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = OtaEvent.IS_LATEST_OTA_VERSION)
    public boolean isLatestVersion(String event, String data) {
        return ((IOtaCaller) this.mQueryCaller).isLatestVersion();
    }
}
