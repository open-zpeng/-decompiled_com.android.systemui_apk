package com.xiaopeng.speech.proxy;

import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.IQueryInjector;
import com.xiaopeng.speech.IRemoteDataSensor;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.common.bean.Value;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.WorkerHandler;
/* loaded from: classes23.dex */
public class QueryInjectorProxy extends IQueryInjector.Stub implements ConnectManager.OnConnectCallback {
    private IPCRunner<IQueryInjector> mIpcRunner = new IPCRunner<>("QueryInjectorProxy");
    private WorkerHandler mWorkerHandler;

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            this.mIpcRunner.setProxy(speechEngine.getQueryInjector());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mIpcRunner.setProxy(null);
    }

    public void setHandler(WorkerHandler workerHandler) {
        this.mWorkerHandler = workerHandler;
        this.mIpcRunner.setWorkerHandler(this.mWorkerHandler);
    }

    @Override // com.xiaopeng.speech.IQueryInjector
    public void registerDataSensor(final String[] command, final IRemoteDataSensor query) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IQueryInjector, Object>() { // from class: com.xiaopeng.speech.proxy.QueryInjectorProxy.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IQueryInjector proxy) {
                try {
                    proxy.registerDataSensor(command, query);
                    return null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.IQueryInjector
    public void unRegisterDataSensor(final String[] command) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IQueryInjector, Object>() { // from class: com.xiaopeng.speech.proxy.QueryInjectorProxy.2
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IQueryInjector proxy) {
                try {
                    proxy.unRegisterDataSensor(command);
                    return null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.IQueryInjector
    public Value queryData(final String dataApi, final String data) {
        return (Value) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IQueryInjector, Value>() { // from class: com.xiaopeng.speech.proxy.QueryInjectorProxy.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Value run(IQueryInjector proxy) {
                try {
                    return proxy.queryData(dataApi, data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return Value.VOID;
                }
            }
        }, Value.VOID);
    }

    @Override // com.xiaopeng.speech.IQueryInjector
    public boolean isQueryInject(final String event) {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IQueryInjector, Boolean>() { // from class: com.xiaopeng.speech.proxy.QueryInjectorProxy.4
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IQueryInjector proxy) {
                try {
                    return Boolean.valueOf(proxy.isQueryInject(event));
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.IQueryInjector
    public Value queryApiRouterData(final String dataApi, final String data) {
        return (Value) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IQueryInjector, Value>() { // from class: com.xiaopeng.speech.proxy.QueryInjectorProxy.5
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Value run(IQueryInjector proxy) {
                try {
                    return proxy.queryApiRouterData(dataApi, data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return Value.VOID;
                }
            }
        }, Value.VOID);
    }
}
