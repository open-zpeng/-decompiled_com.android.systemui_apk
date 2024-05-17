package com.xiaopeng.speech;

import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes23.dex */
public class QueryManager implements ConnectManager.OnConnectCallback {
    private static final String TAG = "QueryManager";
    private volatile IQueryInjector mIQueryInjector;
    private Map<Class<? extends SpeechQuery>, SpeechQuery> mQueryMap = new HashMap();
    private WorkerHandler mWorkerHandler;

    public void init(WorkerHandler workerHandler) {
        this.mWorkerHandler = workerHandler;
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechProxy) {
        LogUtils.i(TAG, "onConnect " + speechProxy);
        if (speechProxy == null) {
            return;
        }
        try {
            this.mIQueryInjector = speechProxy.getQueryInjector();
            this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.QueryManager.1
                @Override // java.lang.Runnable
                public void run() {
                    QueryManager.this.injectQueryList(true);
                }
            });
        } catch (RemoteException e) {
            LogUtils.e(this, e);
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mIQueryInjector = null;
    }

    public <T extends SpeechQuery> T getQuery(Class<T> queryClass) {
        return (T) lazyInitQuery(queryClass);
    }

    public void inject(final Class<? extends SpeechQuery> queryClass, final IQueryCaller queryCaller) {
        this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.QueryManager.2
            @Override // java.lang.Runnable
            public void run() {
                SpeechQuery speechQuery = QueryManager.this.lazyInitQuery(queryClass);
                speechQuery.setQueryCaller(queryCaller);
            }
        });
    }

    public void unInject(final Class<? extends SpeechQuery> queryClass) {
        this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.QueryManager.3
            @Override // java.lang.Runnable
            public void run() {
                SpeechQuery query = (SpeechQuery) QueryManager.this.mQueryMap.get(queryClass);
                if (query != null) {
                    QueryManager.this.unRegister(query);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SpeechQuery lazyInitQuery(Class<? extends SpeechQuery> queryClass) {
        SpeechQuery query;
        SpeechQuery query2 = this.mQueryMap.get(queryClass);
        if (query2 != null) {
            return query2;
        }
        synchronized (this.mQueryMap) {
            query = this.mQueryMap.get(queryClass);
            if (query == null) {
                try {
                    query = queryClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(String.format("create %s error", queryClass));
                }
                if (query != null) {
                    register(query);
                    query.setWorkerHandler(this.mWorkerHandler);
                }
            }
        }
        return query;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void register(SpeechQuery query) {
        if (this.mQueryMap.containsKey(query.getClass())) {
            LogUtils.e(String.format("query %s had register", query));
            return;
        }
        LogUtils.i(String.format("register query:%s", query));
        this.mQueryMap.put(query.getClass(), query);
        this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.QueryManager.4
            @Override // java.lang.Runnable
            public void run() {
                QueryManager.this.injectQueryList(false);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unRegister(SpeechQuery query) {
        if (this.mQueryMap.containsKey(query.getClass()) && this.mIQueryInjector != null) {
            try {
                this.mIQueryInjector.unRegisterDataSensor(query.getQueryEvents());
                this.mQueryMap.remove(query.getClass());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void injectQueryList(boolean forceSub) {
        if (this.mIQueryInjector == null) {
            LogUtils.e("mIQueryInjector == null");
            return;
        }
        for (Map.Entry<Class<? extends SpeechQuery>, SpeechQuery> entry : this.mQueryMap.entrySet()) {
            SpeechQuery query = entry.getValue();
            if (!query.isSubscribed() || forceSub) {
                LogUtils.i(String.format("do inject query:%s", query));
                if (query.getQueryEvents() == null || query.getQueryEvents().length <= 0) {
                    LogUtils.e("getInjectEvents.length == 0");
                } else {
                    try {
                        if (this.mIQueryInjector != null) {
                            this.mIQueryInjector.registerDataSensor(query.getQueryEvents(), query);
                            query.setSubscribed(true);
                        }
                    } catch (RemoteException e) {
                        LogUtils.e(this, "inject error ", e);
                    }
                }
            }
        }
    }
}
