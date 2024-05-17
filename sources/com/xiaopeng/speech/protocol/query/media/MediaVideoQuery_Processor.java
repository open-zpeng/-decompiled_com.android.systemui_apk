package com.xiaopeng.speech.protocol.query.media;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QueryMediaEvent;
/* loaded from: classes23.dex */
public class MediaVideoQuery_Processor implements IQueryProcessor {
    private MediaVideoQuery mTarget;

    public MediaVideoQuery_Processor(MediaVideoQuery target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        if (((event.hashCode() == -555849819 && event.equals(QueryMediaEvent.GET_MEDIAZ_VIDEO_INFO_QUERY)) ? (char) 0 : (char) 65535) == 0) {
            return this.mTarget.getMediaVideoInfo(event, data);
        }
        return null;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryMediaEvent.GET_MEDIAZ_VIDEO_INFO_QUERY};
    }
}
