package com.xiaopeng.speech.protocol.node.bugreport;

import com.xiaopeng.speech.INodeListener;
/* loaded from: classes23.dex */
public interface BugReportListener extends INodeListener {
    void onBugReportBegin();

    void onBugReportEnd(BugReportEndValue bugReportEndValue);

    void onBugReportEndtts();

    void onBugReportVolume(String str);

    default void onBugReportCancel() {
    }

    default void onBugReportSubmit() {
    }
}
