package com.xiaopeng.speech.protocol.query.aiassistant;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.QueryAiAssistantEvent;
/* loaded from: classes23.dex */
public class AiAssistantQuery_Processor implements IQueryProcessor {
    private AiAssistantQuery mTarget;

    public AiAssistantQuery_Processor(AiAssistantQuery target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode != -1132668627) {
            if (hashCode == -497888784 && event.equals(QueryAiAssistantEvent.GUI_AI_VIDEO_OPEN)) {
                c = 0;
            }
            c = 65535;
        } else {
            if (event.equals(QueryAiAssistantEvent.GET_SKIN_TYPE)) {
                c = 1;
            }
            c = 65535;
        }
        if (c != 0) {
            if (c == 1) {
                return Integer.valueOf(this.mTarget.getAiSkinType(event, data));
            }
            return null;
        }
        return Integer.valueOf(this.mTarget.getAiVideoOpenStatus(event, data));
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryAiAssistantEvent.GUI_AI_VIDEO_OPEN, QueryAiAssistantEvent.GET_SKIN_TYPE};
    }
}
