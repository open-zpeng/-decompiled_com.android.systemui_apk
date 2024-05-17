package com.xiaopeng.lib.framework.moduleinterface.syncmodule;
/* loaded from: classes23.dex */
public final class SyncSaveAllEvent {
    public long uid;

    public SyncSaveAllEvent(long uid) {
        this.uid = uid;
    }

    public String toString() {
        return "SyncSaveAllEvent { uid:" + this.uid + "; }";
    }
}
