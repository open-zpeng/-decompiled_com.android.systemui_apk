package com.xiaopeng.speech.vui.task.queue;

import android.view.View;
import com.xiaopeng.speech.vui.task.base.BaseTask;
import com.xiaopeng.speech.vui.utils.LogUtils;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
/* loaded from: classes.dex */
public class DeDuplicationQueue<K, T extends BaseTask> implements BlockingQueue<T> {
    private static final String TAG = "DeDuplicationQueue";
    protected final PriorityBlockingQueue<T> queue = new PriorityBlockingQueue<>(20, new TaskComparator());
    protected final HashMap<K, T> map = new HashMap<>();
    protected final HashMap<SoftReference<View>, T> viewMap = new HashMap<>();
    protected final HashMap<K, List<BaseTask>> sceneMap = new HashMap<>();
    protected ReentrantLock lock = new ReentrantLock();

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.concurrent.BlockingQueue, java.util.Queue, java.util.Collection
    public /* bridge */ /* synthetic */ boolean add(Object obj) {
        return add((DeDuplicationQueue<K, T>) ((BaseTask) obj));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.concurrent.BlockingQueue, java.util.Queue
    public /* bridge */ /* synthetic */ boolean offer(Object obj) {
        return offer((DeDuplicationQueue<K, T>) ((BaseTask) obj));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.concurrent.BlockingQueue
    public /* bridge */ /* synthetic */ boolean offer(Object obj, long j, TimeUnit timeUnit) throws InterruptedException {
        return offer((DeDuplicationQueue<K, T>) ((BaseTask) obj), j, timeUnit);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.concurrent.BlockingQueue
    public /* bridge */ /* synthetic */ void put(Object obj) throws InterruptedException {
        put((DeDuplicationQueue<K, T>) ((BaseTask) obj));
    }

    @Override // java.util.Collection
    public int size() {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.queue.size();
        } finally {
            lock.unlock();
        }
    }

    @Override // java.util.Collection
    public boolean isEmpty() {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            boolean isEmpty = this.queue.isEmpty();
            return isEmpty;
        } finally {
            lock.unlock();
        }
    }

    @Override // java.util.concurrent.BlockingQueue, java.util.Collection
    public boolean contains(Object o) {
        return false;
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super T> c) {
        return 0;
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super T> c, int maxElements) {
        return 0;
    }

    @Override // java.util.Collection, java.lang.Iterable
    public Iterator<T> iterator() {
        return null;
    }

    @Override // java.util.Collection
    public Object[] toArray() {
        return null;
    }

    @Override // java.util.Collection
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override // java.util.concurrent.BlockingQueue, java.util.Collection
    public boolean remove(Object o) {
        return false;
    }

    @Override // java.util.Collection
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override // java.util.Collection
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override // java.util.Collection
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override // java.util.Collection
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override // java.util.Collection
    public void clear() {
    }

    public boolean add(T e) {
        return false;
    }

    public boolean offer(T e) {
        return false;
    }

    public void put(T t) throws InterruptedException {
    }

    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override // java.util.concurrent.BlockingQueue
    public T take() throws InterruptedException {
        return null;
    }

    @Override // java.util.concurrent.BlockingQueue
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override // java.util.concurrent.BlockingQueue
    public int remainingCapacity() {
        return 0;
    }

    @Override // java.util.Queue
    public T remove() {
        return null;
    }

    @Override // java.util.Queue
    public T poll() {
        return null;
    }

    @Override // java.util.Queue
    public T element() {
        return null;
    }

    @Override // java.util.Queue
    public T peek() {
        return null;
    }

    public synchronized void removeTask(String sceneId) {
        LogUtils.logInfo(TAG, "removeTask:" + sceneId);
        if (sceneId == null) {
            return;
        }
        try {
            if (this.sceneMap != null && this.sceneMap.containsKey(sceneId)) {
                List<BaseTask> taskList = this.sceneMap.get(sceneId);
                for (int i = 0; i < taskList.size(); i++) {
                    BaseTask task = taskList.get(i);
                    this.queue.remove(task);
                    String id = task.wrapper.getVid();
                    if (id != null && this.map.containsKey(id)) {
                        this.map.remove(id);
                    }
                    SoftReference<View> view = task.wrapper.getView();
                    if (view != null && this.map.containsKey(view)) {
                        this.map.remove(view);
                    }
                }
                this.sceneMap.remove(sceneId);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "e:" + e.getMessage());
        }
    }

    public void removeTaskFromSceneMap(BaseTask task) {
        List<BaseTask> taskList;
        int index;
        String sceneId = task.wrapper != null ? task.wrapper.getSceneId() : null;
        HashMap<K, List<BaseTask>> hashMap = this.sceneMap;
        if (hashMap != null && sceneId != null && hashMap.containsKey(sceneId) && (taskList = this.sceneMap.get(sceneId)) != null && (index = taskList.indexOf(task)) != -1) {
            taskList.remove(index);
        }
    }
}
