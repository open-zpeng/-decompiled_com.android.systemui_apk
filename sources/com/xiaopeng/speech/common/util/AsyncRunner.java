package com.xiaopeng.speech.common.util;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
/* loaded from: classes23.dex */
public abstract class AsyncRunner<IFunc> {
    protected String TAG = "AsyncRunner";
    private Queue<IFunc> mFuncQueue = new ArrayBlockingQueue(100);
    protected WorkerHandler mWorkerHandler;

    protected abstract boolean canRun();

    protected abstract <R> R realRun(IFunc ifunc);

    public AsyncRunner(String name) {
        this.TAG += "-" + name;
    }

    public void setWorkerHandler(WorkerHandler workerHandler) {
        this.mWorkerHandler = workerHandler;
    }

    public void runFunc(IFunc func) {
        runFunc(func, null);
    }

    public synchronized <R> R runFunc(IFunc func, R result) {
        if (canRun()) {
            if (this.mFuncQueue.size() > 0) {
                this.mFuncQueue.offer(func);
                fetchAll();
                LogUtils.i(this.TAG, "can run offer fun");
            } else {
                return (R) realRun(func);
            }
        } else {
            this.mFuncQueue.offer(func);
            LogUtils.i(this.TAG, "offer fun");
        }
        return result;
    }

    public void fetchAll() {
        LogUtils.i(this.TAG, "fetchAll fun:%d", Integer.valueOf(this.mFuncQueue.size()));
        WorkerHandler workerHandler = this.mWorkerHandler;
        if (workerHandler != null) {
            workerHandler.post(new Runnable() { // from class: com.xiaopeng.speech.common.util.AsyncRunner.1
                @Override // java.lang.Runnable
                public void run() {
                    AsyncRunner.this.doFetchAll();
                }
            });
        } else {
            doFetchAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void doFetchAll() {
        while (this.mFuncQueue.size() > 0) {
            IFunc func = this.mFuncQueue.poll();
            if (canRun() && func != null) {
                realRun(func);
            }
        }
    }

    public synchronized void clearAll() {
        LogUtils.i(this.TAG, "clear all fun:%d", Integer.valueOf(this.mFuncQueue.size()));
        this.mFuncQueue.clear();
    }
}
