package com.xiaopeng.speech.protocol.query.xpu;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface IXpuCaller extends IQueryCaller {
    default int getAutoPilotStatus() {
        return -1;
    }

    default int getALCStatus() {
        return -1;
    }
}
