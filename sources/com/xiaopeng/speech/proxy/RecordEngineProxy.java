package com.xiaopeng.speech.proxy;

import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import com.xiaopeng.speech.coreapi.IRecordEngine;
/* loaded from: classes23.dex */
public class RecordEngineProxy extends IRecordEngine.Stub implements ConnectManager.OnConnectCallback {
    private IPCRunner<IRecordEngine> mIpcRunner = new IPCRunner<>("RecordEngineProxy");

    public void setHandler(WorkerHandler workerHandler) {
        this.mIpcRunner.setWorkerHandler(workerHandler);
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            this.mIpcRunner.setProxy(speechEngine.getRecordEngine());
            this.mIpcRunner.fetchAll();
        } catch (RemoteException e) {
            LogUtils.e(this, "onConnect exception ", e);
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mIpcRunner.setProxy(null);
    }

    @Override // com.xiaopeng.speech.coreapi.IRecordEngine
    public void startRecord(final String dstFilePath, final int maxLengthMs, final int eosTimeout) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IRecordEngine, Object>() { // from class: com.xiaopeng.speech.proxy.RecordEngineProxy.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IRecordEngine proxy) throws RemoteException {
                proxy.startRecord(dstFilePath, maxLengthMs, eosTimeout);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IRecordEngine
    public void stopRecord() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IRecordEngine, Object>() { // from class: com.xiaopeng.speech.proxy.RecordEngineProxy.2
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IRecordEngine proxy) throws RemoteException {
                proxy.stopRecord();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IRecordEngine
    public void asr(final String token, final String filePath) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IRecordEngine, String>() { // from class: com.xiaopeng.speech.proxy.RecordEngineProxy.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IRecordEngine proxy) throws RemoteException {
                proxy.asr(token, filePath);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IRecordEngine
    public boolean pcm2Amr(final String srcFilePath, final String dstFileName, final boolean deleteFileAfterSuc) throws RemoteException {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IRecordEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.RecordEngineProxy.4
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IRecordEngine proxy) throws RemoteException {
                return Boolean.valueOf(proxy.pcm2Amr(srcFilePath, dstFileName, deleteFileAfterSuc));
            }
        }, false)).booleanValue();
    }
}
