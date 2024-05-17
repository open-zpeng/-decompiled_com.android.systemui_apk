package com.xiaopeng.speech.proxy;

import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import com.xiaopeng.speech.coreapi.IVADEngine;
/* loaded from: classes23.dex */
public class VADEngineProxy extends IVADEngine.Stub implements ConnectManager.OnConnectCallback {
    private IPCRunner<IVADEngine> mIpcRunner = new IPCRunner<>("VADEngineProxy");

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            this.mIpcRunner.setProxy(speechEngine.getVadEngine());
            this.mIpcRunner.fetchAll();
        } catch (RemoteException e) {
            LogUtils.e(this, "onConnect exception ", e);
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mIpcRunner.setProxy(null);
    }

    @Override // com.xiaopeng.speech.coreapi.IVADEngine
    public void start(final String from) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IVADEngine, Object>() { // from class: com.xiaopeng.speech.proxy.VADEngineProxy.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IVADEngine proxy) throws RemoteException {
                proxy.start(from);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IVADEngine
    public void stop() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IVADEngine, Object>() { // from class: com.xiaopeng.speech.proxy.VADEngineProxy.2
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IVADEngine proxy) throws RemoteException {
                proxy.stop();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IVADEngine
    public void destory() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IVADEngine, Object>() { // from class: com.xiaopeng.speech.proxy.VADEngineProxy.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IVADEngine proxy) throws RemoteException {
                proxy.destory();
                return null;
            }
        });
    }

    public void setHandler(WorkerHandler workerHandler) {
        this.mIpcRunner.setWorkerHandler(workerHandler);
    }
}
