package com.xiaopeng.speech.protocol.node.phone;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.PhoneEvent;
/* loaded from: classes23.dex */
public class PhoneNode_Processor implements ICommandProcessor {
    private PhoneNode mTarget;

    public PhoneNode_Processor(PhoneNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1366657894:
                if (event.equals(PhoneEvent.QUERY_DETAIL_PHONEINFO)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1249983198:
                if (event.equals(PhoneEvent.IN_ACCEPT)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1094036411:
                if (event.equals(PhoneEvent.QUERY_BLUETOOTH)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -1090067804:
                if (event.equals(PhoneEvent.QUERY_SYNC_BLUETOOTH)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -991558775:
                if (event.equals(PhoneEvent.CALLBACK_SUPPORT)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -761232455:
                if (event.equals(PhoneEvent.IN_REJECT)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -666255396:
                if (event.equals(PhoneEvent.QUERY_CONTACTS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -500460440:
                if (event.equals(PhoneEvent.OUT_CUSTOMERSERVICE)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -307844569:
                if (event.equals(PhoneEvent.REDIAL_SUPPORT)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 478397247:
                if (event.equals(PhoneEvent.OUT)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1301635730:
                if (event.equals(PhoneEvent.REDIAL)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 1330499435:
                if (event.equals(PhoneEvent.SELECT)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1640880884:
                if (event.equals(PhoneEvent.CALLBACK)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 1710160883:
                if (event.equals(PhoneEvent.SYNC_CONTACT_RESULT)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 1821930640:
                if (event.equals(PhoneEvent.OUT_HELP)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                this.mTarget.onQuerySyncBluetooth(event, data);
                return;
            case 1:
                this.mTarget.onQueryContacts(event, data);
                return;
            case 2:
                this.mTarget.onQueryDetailPhoneInfo(event, data);
                return;
            case 3:
                this.mTarget.onOut(event, data);
                return;
            case 4:
                this.mTarget.onPhoneSelectOut(event, data);
                return;
            case 5:
                this.mTarget.onInAccept(event, data);
                return;
            case 6:
                this.mTarget.onInReject(event, data);
                return;
            case 7:
                this.mTarget.onRedialSupport(event, data);
                return;
            case '\b':
                this.mTarget.onRedial(event, data);
                return;
            case '\t':
                this.mTarget.onCallbackSupport(event, data);
                return;
            case '\n':
                this.mTarget.onCallback(event, data);
                return;
            case 11:
                this.mTarget.onOutCustomerservice(event, data);
                return;
            case '\f':
                this.mTarget.onOutHelp(event, data);
                return;
            case '\r':
                this.mTarget.onQueryBluetooth(event, data);
                return;
            case 14:
                this.mTarget.onSyncContactResult(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{PhoneEvent.QUERY_SYNC_BLUETOOTH, PhoneEvent.QUERY_CONTACTS, PhoneEvent.QUERY_DETAIL_PHONEINFO, PhoneEvent.OUT, PhoneEvent.SELECT, PhoneEvent.IN_ACCEPT, PhoneEvent.IN_REJECT, PhoneEvent.REDIAL_SUPPORT, PhoneEvent.REDIAL, PhoneEvent.CALLBACK_SUPPORT, PhoneEvent.CALLBACK, PhoneEvent.OUT_CUSTOMERSERVICE, PhoneEvent.OUT_HELP, PhoneEvent.QUERY_BLUETOOTH, PhoneEvent.SYNC_CONTACT_RESULT};
    }
}
