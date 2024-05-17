package com.xiaopeng.speech.protocol.node.bugreport;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.BugReportEvent;
/* loaded from: classes23.dex */
public class BugReportNode extends SpeechNode<BugReportListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = BugReportEvent.BUG_REPORT_BEGIN)
    public void onBugReportBegin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((BugReportListener) obj).onBugReportBegin();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = BugReportEvent.BUG_REPORT_END)
    public void onBugReportEnd(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        BugReportEndValue bugReportEndValue = BugReportEndValue.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((BugReportListener) obj).onBugReportEnd(bugReportEndValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = BugReportEvent.BUG_REPORT_SUBMIT)
    public void onBugReportSubmit(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((BugReportListener) obj).onBugReportSubmit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = BugReportEvent.BUG_REPORT_CANCEL)
    public void onBugReportCancel(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((BugReportListener) obj).onBugReportCancel();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = BugReportEvent.BUG_REPORT_VOLUME)
    public void onBugReportVolume(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((BugReportListener) obj).onBugReportVolume(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = BugReportEvent.BUG_REPORT_ENDTTS)
    public void onBugReportEndtts(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((BugReportListener) obj).onBugReportEndtts();
            }
        }
    }
}
