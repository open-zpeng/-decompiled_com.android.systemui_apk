package com.xiaopeng.speech.protocol.query.xpu;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.QueryXpuEvent;
/* loaded from: classes23.dex */
public class XpuQuery_Processor implements IQueryProcessor {
    private XpuQuery mTarget;

    public XpuQuery_Processor(XpuQuery target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode != -1809695763) {
            if (hashCode == -603903460 && event.equals(QueryXpuEvent.XPU_IS_ON_ALC)) {
                c = 1;
            }
            c = 65535;
        } else {
            if (event.equals(QueryXpuEvent.XPU_IS_ON_AUTOPILOT)) {
                c = 0;
            }
            c = 65535;
        }
        if (c != 0) {
            if (c == 1) {
                return Integer.valueOf(this.mTarget.getALCStatus(event, data));
            }
            return null;
        }
        return Integer.valueOf(this.mTarget.getAutoPilotStatus(event, data));
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryXpuEvent.XPU_IS_ON_AUTOPILOT, QueryXpuEvent.XPU_IS_ON_ALC};
    }
}
