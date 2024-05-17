package com.android.systemui.util.leak;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/* loaded from: classes21.dex */
public class WeakIdentityHashMap<K, V> {
    private final HashMap<WeakReference<K>, V> mMap = new HashMap<>();
    private final ReferenceQueue<Object> mRefQueue = new ReferenceQueue<>();

    private void cleanUp() {
        while (true) {
            Reference<?> ref = this.mRefQueue.poll();
            if (ref != null) {
                this.mMap.remove(ref);
            } else {
                return;
            }
        }
    }

    public void put(K key, V value) {
        cleanUp();
        this.mMap.put(new CmpWeakReference(key, this.mRefQueue), value);
    }

    public V get(K key) {
        cleanUp();
        return this.mMap.get(new CmpWeakReference(key));
    }

    public Collection<V> values() {
        cleanUp();
        return this.mMap.values();
    }

    public Set<Map.Entry<WeakReference<K>, V>> entrySet() {
        return this.mMap.entrySet();
    }

    public int size() {
        cleanUp();
        return this.mMap.size();
    }

    public boolean isEmpty() {
        cleanUp();
        return this.mMap.isEmpty();
    }

    /* loaded from: classes21.dex */
    private static class CmpWeakReference<K> extends WeakReference<K> {
        private final int mHashCode;

        public CmpWeakReference(K key) {
            super(key);
            this.mHashCode = System.identityHashCode(key);
        }

        public CmpWeakReference(K key, ReferenceQueue<Object> refQueue) {
            super(key, refQueue);
            this.mHashCode = System.identityHashCode(key);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            Object obj = get();
            if (obj != null && (o instanceof CmpWeakReference) && ((CmpWeakReference) o).get() == obj) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return this.mHashCode;
        }
    }
}
