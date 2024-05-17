package com.xiaopeng.speech.proxy;

import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import com.xiaopeng.speech.coreapi.IASREngine;
/* loaded from: classes23.dex */
public class ASREngineProxy extends IASREngine.Stub implements ConnectManager.OnConnectCallback {
    private IPCRunner<IASREngine> mIpcRunner = new IPCRunner<>("ASREngineProxy");

    public void setHandler(WorkerHandler workerHandler) {
        this.mIpcRunner.setWorkerHandler(workerHandler);
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            this.mIpcRunner.setProxy(speechEngine.getASREngine());
            this.mIpcRunner.fetchAll();
        } catch (RemoteException e) {
            LogUtils.e(this, "onConnect exception ", e);
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mIpcRunner.setProxy(null);
    }

    @Override // com.xiaopeng.speech.coreapi.IASREngine
    public void setASRInterruptEnabled(final boolean enable) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IASREngine, Object>() { // from class: com.xiaopeng.speech.proxy.ASREngineProxy.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IASREngine proxy) throws RemoteException {
                proxy.setASRInterruptEnabled(enable);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IASREngine
    public boolean isEnableASRInterrupt() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IASREngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.ASREngineProxy.2
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IASREngine proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isEnableASRInterrupt());
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.IASREngine
    public boolean isDefaultEnableASRInterrupt() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IASREngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.ASREngineProxy.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IASREngine proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isDefaultEnableASRInterrupt());
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.IASREngine
    public void setDefaultASRInterruptEnabled(final boolean enable) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IASREngine, Object>() { // from class: com.xiaopeng.speech.proxy.ASREngineProxy.4
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IASREngine proxy) throws RemoteException {
                proxy.setDefaultASRInterruptEnabled(enable);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IASREngine
    public void startListen() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IASREngine, Object>() { // from class: com.xiaopeng.speech.proxy.ASREngineProxy.5
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IASREngine proxy) throws RemoteException {
                proxy.startListen();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IASREngine
    public void stopListen() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IASREngine, Object>() { // from class: com.xiaopeng.speech.proxy.ASREngineProxy.6
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IASREngine proxy) throws RemoteException {
                proxy.stopListen();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IASREngine
    public void cancelListen() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IASREngine, Object>() { // from class: com.xiaopeng.speech.proxy.ASREngineProxy.7
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IASREngine proxy) throws RemoteException {
                proxy.cancelListen();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IASREngine
    public void setVadTimeout(final long millisecond) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IASREngine, Object>() { // from class: com.xiaopeng.speech.proxy.ASREngineProxy.8
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IASREngine proxy) throws RemoteException {
                proxy.setVadTimeout(millisecond);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IASREngine
    public void setVadPauseTime(final long millisecond) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IASREngine, Object>() { // from class: com.xiaopeng.speech.proxy.ASREngineProxy.9
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IASREngine proxy) throws RemoteException {
                proxy.setVadPauseTime(millisecond);
                return null;
            }
        });
    }
}
