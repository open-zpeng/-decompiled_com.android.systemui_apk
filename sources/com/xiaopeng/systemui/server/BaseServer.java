package com.xiaopeng.systemui.server;

import com.xiaopeng.xuimanager.systemui.SysLogUtils;
/* loaded from: classes24.dex */
abstract class BaseServer {
    protected abstract String logTag();

    void logD(String msg) {
        SysLogUtils.d(logTag(), msg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void logI(String msg) {
        SysLogUtils.i(logTag(), msg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void logW(String msg) {
        SysLogUtils.w(logTag(), msg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void logE(String msg) {
        SysLogUtils.e(logTag(), msg);
    }
}
