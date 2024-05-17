package com.xiaopeng.speech.protocol.node.phone;

import com.xiaopeng.speech.protocol.node.phone.bean.PhoneBean;
/* loaded from: classes23.dex */
public abstract class AbsPhoneListener implements PhoneListener {
    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onOut(String name, String phoneNum, String id) {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onRedialSupport() {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onRedial() {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onCallbackSupport() {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onCallback() {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onOutCustomerservice(String number) {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onOutHelp(String number) {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onInAccept() {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onInReject() {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onQueryContacts(String event, PhoneBean phone) {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onSettingOpen() {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onQueryBluetooth() {
    }

    @Override // com.xiaopeng.speech.protocol.node.phone.PhoneListener
    public void onSyncContactResult(int resultCode) {
    }
}
