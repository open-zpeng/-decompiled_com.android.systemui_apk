package com.android.systemui.keyguard;

import java.util.ArrayList;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class Lifecycle<T> {
    private ArrayList<T> mObservers = new ArrayList<>();

    public void addObserver(T observer) {
        this.mObservers.add(observer);
    }

    public void removeObserver(T observer) {
        this.mObservers.remove(observer);
    }

    public void dispatch(Consumer<T> consumer) {
        for (int i = 0; i < this.mObservers.size(); i++) {
            consumer.accept(this.mObservers.get(i));
        }
    }
}
