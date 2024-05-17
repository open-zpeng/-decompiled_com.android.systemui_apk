package com.xiaopeng.speech.protocol.query.phone;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.QueryPhoneEvent;
/* loaded from: classes23.dex */
public class PhoneQuery extends SpeechQuery<IPhoneQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryPhoneEvent.GET_PAGE_OPEN_STATUS)
    public int getGuiPageOpenState(String event, String data) {
        return ((IPhoneQueryCaller) this.mQueryCaller).getGuiPageOpenState(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryPhoneEvent.GET_BLUETOOTH_STATUS)
    public boolean onQueryBluetooth(String event, String data) {
        return ((IPhoneQueryCaller) this.mQueryCaller).onQueryBluetooth();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryPhoneEvent.GET_CONTACT_SYNC_STATUS)
    public int onQueryContactSyncStatus(String event, String data) {
        return ((IPhoneQueryCaller) this.mQueryCaller).onQueryContactSyncStatus();
    }
}
