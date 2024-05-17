package com.xiaopeng.speech.protocol.node.userbook;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.CarcontrolEvent;
/* loaded from: classes23.dex */
public class UserBookNode_Processor implements ICommandProcessor {
    private UserBookNode mTarget;

    public UserBookNode_Processor(UserBookNode target) {
        this.mTarget = target;
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        int hashCode = event.hashCode();
        if (hashCode == -1001296170) {
            if (event.equals(CarcontrolEvent.CLOSE_USER_BOOK)) {
                c = 2;
            }
            c = 65535;
        } else if (hashCode != 1414376552) {
            if (hashCode == 1829348592 && event.equals(CarcontrolEvent.CHECK_USER_BOOK)) {
                c = 0;
            }
            c = 65535;
        } else {
            if (event.equals(CarcontrolEvent.OPEN_USER_BOOK)) {
                c = 1;
            }
            c = 65535;
        }
        if (c == 0) {
            this.mTarget.onCheckUserBook(event, data);
        } else if (c == 1) {
            this.mTarget.onOpenUserBook(event, data);
        } else if (c == 2) {
            this.mTarget.onCloseUserBook(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{CarcontrolEvent.CHECK_USER_BOOK, CarcontrolEvent.OPEN_USER_BOOK, CarcontrolEvent.CLOSE_USER_BOOK};
    }
}
