package com.android.systemui.util.leak;

import java.util.Collection;
import java.util.WeakHashMap;
/* loaded from: classes21.dex */
public class TrackedObjects {
    private final WeakHashMap<Class<?>, TrackedClass<?>> mTrackedClasses = new WeakHashMap<>();
    private final TrackedCollections mTrackedCollections;

    public TrackedObjects(TrackedCollections trackedCollections) {
        this.mTrackedCollections = trackedCollections;
    }

    public synchronized <T> void track(T object) {
        Class<?> clazz = object.getClass();
        TrackedClass<?> trackedClass = this.mTrackedClasses.get(clazz);
        if (trackedClass == null) {
            trackedClass = new TrackedClass<>();
            this.mTrackedClasses.put(clazz, trackedClass);
        }
        trackedClass.track(object);
        this.mTrackedCollections.track(trackedClass, clazz.getName());
    }

    public static boolean isTrackedObject(Collection<?> collection) {
        return collection instanceof TrackedClass;
    }

    /* loaded from: classes21.dex */
    private static class TrackedClass<T> extends AbstractCollection<T> {
        final WeakIdentityHashMap<T, Void> instances;

        private TrackedClass() {
            this.instances = new WeakIdentityHashMap<>();
        }

        void track(T object) {
            this.instances.put(object, null);
        }

        @Override // com.android.systemui.util.leak.AbstractCollection, java.util.Collection
        public int size() {
            return this.instances.size();
        }

        @Override // com.android.systemui.util.leak.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return this.instances.isEmpty();
        }
    }
}
