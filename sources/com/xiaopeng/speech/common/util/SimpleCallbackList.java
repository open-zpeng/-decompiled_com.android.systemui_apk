package com.xiaopeng.speech.common.util;

import java.util.ArrayList;
import java.util.List;
/* loaded from: classes23.dex */
public class SimpleCallbackList<E> {
    private List<E> mList = new ArrayList();

    public void addCallback(E callback) {
        synchronized (this.mList) {
            if (callback != null) {
                if (!this.mList.contains(callback)) {
                    this.mList.add(callback);
                }
            }
        }
    }

    public void removeCallback(E callback) {
        if (callback != null) {
            synchronized (this.mList) {
                this.mList.remove(callback);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v5, types: [java.lang.Object[]] */
    public E[] collectCallbacks() {
        E[] callbacks = null;
        synchronized (this.mList) {
            if (this.mList.size() > 0) {
                callbacks = this.mList.toArray();
            }
        }
        return callbacks;
    }

    public int size() {
        int size;
        synchronized (this.mList) {
            size = this.mList.size();
        }
        return size;
    }
}
