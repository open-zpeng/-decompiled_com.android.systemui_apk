package com.xiaopeng.speech.protocol.node.bugreport;
/* loaded from: classes23.dex */
public abstract class AbsBugReportListener implements BugReportListener {
    @Override // com.xiaopeng.speech.protocol.node.bugreport.BugReportListener
    public void onBugReportBegin() {
    }

    @Override // com.xiaopeng.speech.protocol.node.bugreport.BugReportListener
    public void onBugReportEnd(BugReportEndValue endValue) {
    }

    @Override // com.xiaopeng.speech.protocol.node.bugreport.BugReportListener
    public void onBugReportVolume(String data) {
    }

    @Override // com.xiaopeng.speech.protocol.node.bugreport.BugReportListener
    public void onBugReportEndtts() {
    }

    @Override // com.xiaopeng.speech.protocol.node.bugreport.BugReportListener
    public void onBugReportCancel() {
    }

    @Override // com.xiaopeng.speech.protocol.node.bugreport.BugReportListener
    public void onBugReportSubmit() {
    }
}
