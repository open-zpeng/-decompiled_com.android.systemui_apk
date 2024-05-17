package com.xiaopeng.speech.protocol.query.system;

import com.xiaopeng.speech.IQueryCaller;
/* loaded from: classes23.dex */
public interface IControlPanelCaller extends IQueryCaller {
    default int onBlueToothVolumeSet(int type) {
        return 0;
    }

    default int onBlueToothVolumeSetValue(int value) {
        return 0;
    }
}
