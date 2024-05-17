package com.xiaopeng.speech.proxy;

import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import com.xiaopeng.speech.coreapi.IEventObserver;
import com.xiaopeng.speech.coreapi.ISubscriber;
/* loaded from: classes23.dex */
public class SubscriberProxy extends ISubscriber.Stub implements ConnectManager.OnConnectCallback {
    private IPCRunner<ISubscriber> mIpcRunner = new IPCRunner<>("SubscriberProxy");

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            this.mIpcRunner.setProxy(speechEngine.getSubscriber());
            this.mIpcRunner.fetchAll();
        } catch (RemoteException e) {
            LogUtils.e(this, "onConnect exception ", e);
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mIpcRunner.setProxy(null);
    }

    @Override // com.xiaopeng.speech.coreapi.ISubscriber
    public void subscribe(final String[] command, final IEventObserver eventObserver) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ISubscriber, Object>() { // from class: com.xiaopeng.speech.proxy.SubscriberProxy.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ISubscriber proxy) throws RemoteException {
                proxy.subscribe(command, eventObserver);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ISubscriber
    public void unSubscribe(final IEventObserver eventObserver) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ISubscriber, Object>() { // from class: com.xiaopeng.speech.proxy.SubscriberProxy.2
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ISubscriber proxy) throws RemoteException {
                proxy.unSubscribe(eventObserver);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ISubscriber
    public boolean isSubscribed(final String event) {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ISubscriber, Boolean>() { // from class: com.xiaopeng.speech.proxy.SubscriberProxy.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(ISubscriber proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isSubscribed(event));
            }
        }, false)).booleanValue();
    }

    public void setHandler(WorkerHandler workerHandler) {
        this.mIpcRunner.setWorkerHandler(workerHandler);
    }
}
