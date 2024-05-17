package com.xiaopeng.speech.protocol.query.ota;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.OtaEvent;
/* loaded from: classes23.dex */
public class OtaQuery_Processor implements IQueryProcessor {
    private OtaQuery mTarget;

    public OtaQuery_Processor(OtaQuery target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        if (((event.hashCode() == 1851114339 && event.equals(OtaEvent.IS_LATEST_OTA_VERSION)) ? (char) 0 : (char) 65535) == 0) {
            return Boolean.valueOf(this.mTarget.isLatestVersion(event, data));
        }
        return null;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{OtaEvent.IS_LATEST_OTA_VERSION};
    }
}
