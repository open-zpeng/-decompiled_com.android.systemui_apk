package com.xiaopeng.speech.proxy;

import android.os.IBinder;
import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import com.xiaopeng.speech.coreapi.IHotwordEngine;
/* loaded from: classes23.dex */
public class HotwordEngineProxy extends IHotwordEngine.Stub implements ConnectManager.OnConnectCallback {
    public static final String BY_AUTO_PARKING = "autoParking";
    public static final String BY_BLUETOOTH_PHONE = "bluetoothPhone";
    public static final String BY_PARKING_ALONG_THE_WAY = "parkingAlongTheWay";
    private IHotwordEngine mHotwordEngine;
    private IPCRunner<IHotwordEngine> mIpcRunner = new IPCRunner<>("HotwordEngineProxy");

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            if (speechEngine.getHotwordEngine() != null) {
                this.mHotwordEngine = speechEngine.getHotwordEngine();
                this.mIpcRunner.setProxy(this.mHotwordEngine);
                this.mIpcRunner.fetchAll();
            } else {
                LogUtils.e("HotwordEngineProxy", "mHotwordEngine = null");
            }
        } catch (RemoteException e) {
            LogUtils.e(this, "onConnect exception ", e);
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mIpcRunner.setProxy(null);
        this.mHotwordEngine = null;
    }

    @Override // com.xiaopeng.speech.coreapi.IHotwordEngine
    public void enjectHotwords(String words) {
        LogUtils.i("abandoned function: enjectHotwords.");
    }

    @Override // com.xiaopeng.speech.coreapi.IHotwordEngine
    public void removeHotwords() {
        LogUtils.i("abandoned function: removeHotwords.");
    }

    @Override // com.xiaopeng.speech.coreapi.IHotwordEngine
    public int getHotwordEngineState() {
        IHotwordEngine iHotwordEngine = this.mHotwordEngine;
        if (iHotwordEngine != null) {
            try {
                return iHotwordEngine.getHotwordEngineState();
            } catch (RemoteException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    public void setHandler(WorkerHandler workerHandler) {
        this.mIpcRunner.setWorkerHandler(workerHandler);
    }

    @Override // com.xiaopeng.speech.coreapi.IHotwordEngine
    public void removeHotWords(final String byWho) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IHotwordEngine, Object>() { // from class: com.xiaopeng.speech.proxy.HotwordEngineProxy.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IHotwordEngine proxy) throws RemoteException {
                proxy.removeHotWords(byWho);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IHotwordEngine
    public void injectHotWords(final String words, final String byWho) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IHotwordEngine, Object>() { // from class: com.xiaopeng.speech.proxy.HotwordEngineProxy.2
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IHotwordEngine proxy) throws RemoteException {
                proxy.injectHotWords(words, byWho);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IHotwordEngine
    public void injectHotWordsByBinder(final String words, final String byWho, final IBinder binder) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IHotwordEngine, Object>() { // from class: com.xiaopeng.speech.proxy.HotwordEngineProxy.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IHotwordEngine proxy) throws RemoteException {
                proxy.injectHotWordsByBinder(words, byWho, binder);
                return null;
            }
        });
    }
}
