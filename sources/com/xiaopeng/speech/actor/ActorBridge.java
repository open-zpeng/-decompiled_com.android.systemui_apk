package com.xiaopeng.speech.actor;

import android.content.Context;
import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.IActorBridge;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.ITransporter;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
/* loaded from: classes23.dex */
public class ActorBridge extends IActorBridge.Stub implements ITransporter.Sender, ConnectManager.OnConnectCallback {
    private Context mContext;
    private IPCRunner<IActorBridge> mIpcRunner = new IPCRunner<>("ActorBridge");
    private WorkerHandler mWorkerHandler;

    public ActorBridge(Context context) {
        this.mContext = context;
    }

    public void setHandler(WorkerHandler workerHandler) {
        this.mWorkerHandler = workerHandler;
        this.mIpcRunner.setWorkerHandler(workerHandler);
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            this.mIpcRunner.setProxy(speechEngine.getActorBridge());
            this.mIpcRunner.fetchAll();
        } catch (RemoteException e) {
            LogUtils.e(this, "onConnect exception ", e);
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mIpcRunner.setProxy(null);
    }

    @Override // com.xiaopeng.speech.IActorBridge
    public void send(final Actor actor) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IActorBridge, Object>() { // from class: com.xiaopeng.speech.actor.ActorBridge.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IActorBridge proxy) throws RemoteException {
                proxy.send(actor);
                return null;
            }
        });
    }
}
