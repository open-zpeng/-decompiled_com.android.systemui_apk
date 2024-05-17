package com.xiaopeng.speech.protocol.query.phone;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QueryPhoneEvent;
/* loaded from: classes23.dex */
public class PhoneQuery_Processor implements IQueryProcessor {
    private PhoneQuery mTarget;

    public PhoneQuery_Processor(PhoneQuery target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode == -991919164) {
            if (event.equals(QueryPhoneEvent.GET_PAGE_OPEN_STATUS)) {
                c = 0;
            }
            c = 65535;
        } else if (hashCode != 579161930) {
            if (hashCode == 1113652927 && event.equals(QueryPhoneEvent.GET_CONTACT_SYNC_STATUS)) {
                c = 2;
            }
            c = 65535;
        } else {
            if (event.equals(QueryPhoneEvent.GET_BLUETOOTH_STATUS)) {
                c = 1;
            }
            c = 65535;
        }
        if (c != 0) {
            if (c != 1) {
                if (c == 2) {
                    return Integer.valueOf(this.mTarget.onQueryContactSyncStatus(event, data));
                }
                return null;
            }
            return Boolean.valueOf(this.mTarget.onQueryBluetooth(event, data));
        }
        return Integer.valueOf(this.mTarget.getGuiPageOpenState(event, data));
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryPhoneEvent.GET_PAGE_OPEN_STATUS, QueryPhoneEvent.GET_BLUETOOTH_STATUS, QueryPhoneEvent.GET_CONTACT_SYNC_STATUS};
    }
}
