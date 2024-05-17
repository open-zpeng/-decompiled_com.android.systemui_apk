package com.xiaopeng.speech.common.util;

import android.os.RemoteException;
/* loaded from: classes23.dex */
public class IPCRunner<Proxy> extends AsyncRunner<IIPCFunc> {
    private volatile Proxy mProxy;

    /* loaded from: classes23.dex */
    public interface IIPCFunc<Proxy, R> {
        R run(Proxy proxy) throws RemoteException;
    }

    public IPCRunner(String name) {
        super(name);
    }

    public synchronized void setProxy(Proxy proxy) {
        String str = this.TAG;
        LogUtils.i(str, "proxy = " + proxy);
        this.mProxy = proxy;
    }

    @Override // com.xiaopeng.speech.common.util.AsyncRunner
    protected synchronized boolean canRun() {
        return this.mProxy != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.speech.common.util.AsyncRunner
    public synchronized Object realRun(IIPCFunc ipcFunc) {
        try {
        } catch (Throwable e) {
            LogUtils.e(this.TAG, "ipc run catch exception ", e);
            return null;
        }
        return ipcFunc.run(this.mProxy);
    }
}
