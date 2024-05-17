package com.xiaopeng.aar.client.ipc;

import androidx.annotation.RestrictTo;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class IpcManager {
    private final Ipc mIpc;

    /* loaded from: classes22.dex */
    private static class SingletonHolder {
        private static final IpcManager INSTANCE = new IpcManager();

        private SingletonHolder() {
        }
    }

    public static IpcManager get() {
        return SingletonHolder.INSTANCE;
    }

    public void init() {
    }

    private IpcManager() {
        this.mIpc = IpcImpl.get();
    }

    public Ipc getIpc() {
        return this.mIpc;
    }
}
