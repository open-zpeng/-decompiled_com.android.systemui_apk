package com.android.systemui.shared.recents.model;

import android.util.Log;
import android.util.SparseArray;
import com.android.systemui.shared.recents.model.Task;
/* loaded from: classes21.dex */
public abstract class TaskKeyCache<V> {
    protected static final String TAG = "TaskKeyCache";
    protected final SparseArray<Task.TaskKey> mKeys = new SparseArray<>();

    protected abstract void evictAllCache();

    protected abstract V getCacheEntry(int i);

    protected abstract void putCacheEntry(int i, V v);

    protected abstract void removeCacheEntry(int i);

    public final synchronized V get(Task.TaskKey key) {
        return getCacheEntry(key.id);
    }

    public final synchronized V getAndInvalidateIfModified(Task.TaskKey key) {
        Task.TaskKey lastKey = this.mKeys.get(key.id);
        if (lastKey != null && (lastKey.windowingMode != key.windowingMode || lastKey.lastActiveTime != key.lastActiveTime)) {
            remove(key);
            return null;
        }
        return getCacheEntry(key.id);
    }

    public final synchronized void put(Task.TaskKey key, V value) {
        if (key == null || value == null) {
            Log.e(TAG, "Unexpected null key or value: " + key + ", " + value);
            return;
        }
        this.mKeys.put(key.id, key);
        putCacheEntry(key.id, value);
    }

    public final synchronized void remove(Task.TaskKey key) {
        removeCacheEntry(key.id);
        this.mKeys.remove(key.id);
    }

    public final synchronized void evictAll() {
        evictAllCache();
        this.mKeys.clear();
    }
}
