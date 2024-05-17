package com.xiaopeng.speech.vui.task;

import com.xiaopeng.speech.vui.task.queue.DeDuplicationQueue;
import com.xiaopeng.speech.vui.task.queue.TaskWrapperQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class ThreadPoolProxy {
    private int mCorePoolSize;
    private ThreadPoolExecutor mExecutor;
    private int mMaximumPoolSize;
    private DeDuplicationQueue queue;

    public ThreadPoolProxy(int corePoolSize, int maximumPoolSize) {
        this.mCorePoolSize = corePoolSize;
        this.mMaximumPoolSize = maximumPoolSize;
    }

    private void initThreadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = this.mExecutor;
        if (threadPoolExecutor == null || threadPoolExecutor.isShutdown() || this.mExecutor.isTerminated()) {
            synchronized (ThreadPoolProxy.class) {
                if (this.mExecutor == null || this.mExecutor.isShutdown() || this.mExecutor.isTerminated()) {
                    TimeUnit unit = TimeUnit.MILLISECONDS;
                    this.queue = new TaskWrapperQueue();
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
                    this.mExecutor = new ThreadPoolExecutor(this.mCorePoolSize, this.mMaximumPoolSize, 3000L, unit, this.queue, threadFactory, handler);
                }
            }
        }
    }

    public void execute(Runnable task) {
        initThreadPoolExecutor();
        this.mExecutor.execute(task);
    }

    public Future submit(Runnable task) {
        initThreadPoolExecutor();
        return this.mExecutor.submit(task);
    }

    public void remove(Runnable task) {
        initThreadPoolExecutor();
    }

    public void removeTask(String sceneId) {
        DeDuplicationQueue deDuplicationQueue = this.queue;
        if (deDuplicationQueue != null) {
            deDuplicationQueue.removeTask(sceneId);
        }
    }
}
