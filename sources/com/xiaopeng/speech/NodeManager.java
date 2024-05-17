package com.xiaopeng.speech;

import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import com.xiaopeng.speech.coreapi.ISubscriber;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes23.dex */
public class NodeManager implements ConnectManager.OnConnectCallback {
    private Map<Class<? extends SpeechNode>, SpeechNode> mNodeMap = new HashMap();
    private volatile ISubscriber mSubscriber;
    private WorkerHandler mWorkerHandler;

    public void init(WorkerHandler workerHandler) {
        this.mWorkerHandler = workerHandler;
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechProxy) {
        LogUtils.i("onConnect " + speechProxy);
        if (speechProxy == null) {
            return;
        }
        try {
            this.mSubscriber = speechProxy.getSubscriber();
            this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.NodeManager.1
                @Override // java.lang.Runnable
                public void run() {
                    NodeManager.this.subscribeNodeList(true);
                }
            });
        } catch (RemoteException e) {
            LogUtils.e(this, e);
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mSubscriber = null;
    }

    public <T extends SpeechNode> T getNode(Class<T> nodeClass) {
        return (T) lazyInitNode(nodeClass);
    }

    public void subscribe(final Class<? extends SpeechNode> nodeClass, final INodeListener listener) {
        this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.NodeManager.2
            @Override // java.lang.Runnable
            public void run() {
                SpeechNode node = NodeManager.this.lazyInitNode(nodeClass);
                if (node != null) {
                    node.addListener(listener);
                }
            }
        });
    }

    public void unSubscribe(final Class<? extends SpeechNode> nodeClass) {
        this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.NodeManager.3
            @Override // java.lang.Runnable
            public void run() {
                SpeechNode node = (SpeechNode) NodeManager.this.mNodeMap.get(nodeClass);
                if (node != null) {
                    NodeManager.this.unRegister(node);
                }
            }
        });
    }

    public void unSubscribe(final Class<? extends SpeechNode> nodeClass, final INodeListener listener) {
        this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.NodeManager.4
            @Override // java.lang.Runnable
            public void run() {
                SpeechNode node = (SpeechNode) NodeManager.this.mNodeMap.get(nodeClass);
                if (node == null) {
                    return;
                }
                node.removeListener(listener);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SpeechNode lazyInitNode(Class<? extends SpeechNode> nodeClass) {
        SpeechNode node;
        SpeechNode node2 = this.mNodeMap.get(nodeClass);
        if (node2 != null) {
            return node2;
        }
        synchronized (this.mNodeMap) {
            node = this.mNodeMap.get(nodeClass);
            if (node == null) {
                try {
                    node = nodeClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(String.format("create %s error", nodeClass));
                }
                if (node != null) {
                    register(node);
                    node.setWorkerHandler(this.mWorkerHandler);
                }
            }
        }
        return node;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void register(SpeechNode node) {
        if (this.mNodeMap.containsKey(node.getClass())) {
            LogUtils.e(String.format("node %s had register", node));
            return;
        }
        LogUtils.i(String.format("register node:%s", node));
        this.mNodeMap.put(node.getClass(), node);
        this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.NodeManager.5
            @Override // java.lang.Runnable
            public void run() {
                NodeManager.this.subscribeNodeList(false);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unRegister(SpeechNode node) {
        if (this.mNodeMap.containsKey(node.getClass()) && this.mSubscriber != null) {
            try {
                this.mSubscriber.unSubscribe(node);
                this.mNodeMap.remove(node.getClass());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void subscribeNodeList(boolean forceSub) {
        if (this.mSubscriber == null) {
            LogUtils.e("mSubscriber == null");
            return;
        }
        for (Map.Entry<Class<? extends SpeechNode>, SpeechNode> entry : this.mNodeMap.entrySet()) {
            SpeechNode node = entry.getValue();
            if (!node.isSubscribed() || forceSub) {
                LogUtils.i(String.format("do subscribe node:%s", node));
                if (node.getSubscribeEvents() == null || node.getSubscribeEvents().length <= 0) {
                    LogUtils.e("getSubscribeEvents.length == 0");
                } else {
                    try {
                        if (this.mSubscriber != null) {
                            this.mSubscriber.subscribe(node.getSubscribeEvents(), node);
                            node.setSubscribed(true);
                        }
                    } catch (RemoteException e) {
                        LogUtils.e(this, "subscribe error ", e);
                    }
                }
            }
        }
    }
}
