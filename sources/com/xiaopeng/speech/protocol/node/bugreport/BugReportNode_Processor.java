package com.xiaopeng.speech.protocol.node.bugreport;

import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.BugReportEvent;
/* loaded from: classes23.dex */
public class BugReportNode_Processor implements ICommandProcessor {
    private BugReportNode mTarget;

    public BugReportNode_Processor(BugReportNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1361908491:
                if (event.equals(BugReportEvent.BUG_REPORT_CANCEL)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1272286452:
                if (event.equals(BugReportEvent.BUG_REPORT_END)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1272269173:
                if (event.equals(BugReportEvent.BUG_REPORT_ENDTTS)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1265421229:
                if (event.equals(BugReportEvent.BUG_REPORT_BEGIN)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1032388717:
                if (event.equals(BugReportEvent.BUG_REPORT_SUBMIT)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -508144235:
                if (event.equals(BugReportEvent.BUG_REPORT_VOLUME)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            this.mTarget.onBugReportBegin(event, data);
        } else if (c == 1) {
            this.mTarget.onBugReportEnd(event, data);
        } else if (c == 2) {
            this.mTarget.onBugReportSubmit(event, data);
        } else if (c == 3) {
            this.mTarget.onBugReportCancel(event, data);
        } else if (c == 4) {
            this.mTarget.onBugReportVolume(event, data);
        } else if (c == 5) {
            this.mTarget.onBugReportEndtts(event, data);
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{BugReportEvent.BUG_REPORT_BEGIN, BugReportEvent.BUG_REPORT_END, BugReportEvent.BUG_REPORT_SUBMIT, BugReportEvent.BUG_REPORT_CANCEL, BugReportEvent.BUG_REPORT_VOLUME, BugReportEvent.BUG_REPORT_ENDTTS};
    }
}
