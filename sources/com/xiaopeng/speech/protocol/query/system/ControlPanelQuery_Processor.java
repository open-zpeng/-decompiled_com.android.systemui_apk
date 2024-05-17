package com.xiaopeng.speech.protocol.query.system;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QuerySystemEvent;
/* loaded from: classes23.dex */
public class ControlPanelQuery_Processor implements IQueryProcessor {
    private ControlPanelQuery mTarget;

    public ControlPanelQuery_Processor(ControlPanelQuery target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode != -856493362) {
            if (hashCode == 1508154961 && event.equals(QuerySystemEvent.BLUETOOTH_VOLUME_SET_VALUE)) {
                c = 1;
            }
            c = 65535;
        } else {
            if (event.equals(QuerySystemEvent.BLUETOOTH_VOLUME_SET)) {
                c = 0;
            }
            c = 65535;
        }
        if (c != 0) {
            if (c == 1) {
                return Integer.valueOf(this.mTarget.onBlueToothVolumeSetValue(event, data));
            }
            return null;
        }
        return Integer.valueOf(this.mTarget.onBlueToothVolumeSet(event, data));
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QuerySystemEvent.BLUETOOTH_VOLUME_SET, QuerySystemEvent.BLUETOOTH_VOLUME_SET_VALUE};
    }
}
