package com.xiaopeng.speech.protocol.query.speech.combo;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.ComboQueryEvent;
/* loaded from: classes23.dex */
public class ComboQuery_Processor implements IQueryProcessor {
    private ComboQuery mTarget;

    public ComboQuery_Processor(ComboQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2089284112:
                if (event.equals(ComboQueryEvent.COMBO_ENTER_USERMODE)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1068777799:
                if (event.equals(ComboQueryEvent.COMBO_GET_CURRENT_USERMODE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -949369482:
                if (event.equals(ComboQueryEvent.COMBO_EXIT_USERMODE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1765601312:
                if (event.equals(ComboQueryEvent.COMBO_CHECK_ENTER_USERMODE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c != 0) {
            if (c != 1) {
                if (c != 2) {
                    if (c == 3) {
                        return this.mTarget.getCurrentUserMode(event, data);
                    }
                    return null;
                }
                return this.mTarget.checkEnterUserMode(event, data);
            }
            return this.mTarget.exitMode(event, data);
        }
        return this.mTarget.enterMode(event, data);
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{ComboQueryEvent.COMBO_ENTER_USERMODE, ComboQueryEvent.COMBO_EXIT_USERMODE, ComboQueryEvent.COMBO_CHECK_ENTER_USERMODE, ComboQueryEvent.COMBO_GET_CURRENT_USERMODE};
    }
}
