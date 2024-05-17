package com.xiaopeng.aar.server.ipc;

import androidx.annotation.RestrictTo;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class IpcServerManager {
    private IpcServer mIpcServer;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes22.dex */
    public static class SingletonHolder {
        private static final IpcServerManager INSTANCE = new IpcServerManager();

        private SingletonHolder() {
        }
    }

    public static IpcServerManager get() {
        return SingletonHolder.INSTANCE;
    }

    public void init() {
    }

    private IpcServerManager() {
        this.mIpcServer = IpcServerImpl.get();
    }

    public IpcServer getIpc() {
        return this.mIpcServer;
    }
}
