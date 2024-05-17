package com.android.systemui.classifier.brightline;

import android.view.MotionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
/* loaded from: classes21.dex */
public class TimeLimitedMotionEventBuffer implements List<MotionEvent> {
    private long mMaxAgeMs;
    private final LinkedList<MotionEvent> mMotionEvents = new LinkedList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public TimeLimitedMotionEventBuffer(long maxAgeMs) {
        this.mMaxAgeMs = maxAgeMs;
    }

    private void ejectOldEvents() {
        if (this.mMotionEvents.isEmpty()) {
            return;
        }
        Iterator<MotionEvent> iter = listIterator();
        long mostRecentMs = this.mMotionEvents.getLast().getEventTime();
        while (iter.hasNext()) {
            MotionEvent ev = iter.next();
            if (mostRecentMs - ev.getEventTime() > this.mMaxAgeMs) {
                iter.remove();
                ev.recycle();
            }
        }
    }

    @Override // java.util.List
    public void add(int index, MotionEvent element) {
        throw new UnsupportedOperationException();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.List
    public MotionEvent remove(int index) {
        return this.mMotionEvents.remove(index);
    }

    @Override // java.util.List
    public int indexOf(Object o) {
        return this.mMotionEvents.indexOf(o);
    }

    @Override // java.util.List
    public int lastIndexOf(Object o) {
        return this.mMotionEvents.lastIndexOf(o);
    }

    @Override // java.util.List, java.util.Collection
    public int size() {
        return this.mMotionEvents.size();
    }

    @Override // java.util.List, java.util.Collection
    public boolean isEmpty() {
        return this.mMotionEvents.isEmpty();
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object o) {
        return this.mMotionEvents.contains(o);
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<MotionEvent> iterator() {
        return this.mMotionEvents.iterator();
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        return this.mMotionEvents.toArray();
    }

    @Override // java.util.List, java.util.Collection
    public <T> T[] toArray(T[] a) {
        return (T[]) this.mMotionEvents.toArray(a);
    }

    @Override // java.util.List, java.util.Collection
    public boolean add(MotionEvent element) {
        boolean result = this.mMotionEvents.add(element);
        ejectOldEvents();
        return result;
    }

    @Override // java.util.List, java.util.Collection
    public boolean remove(Object o) {
        return this.mMotionEvents.remove(o);
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> c) {
        return this.mMotionEvents.containsAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean addAll(Collection<? extends MotionEvent> collection) {
        boolean result = this.mMotionEvents.addAll(collection);
        ejectOldEvents();
        return result;
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends MotionEvent> elements) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List, java.util.Collection
    public boolean removeAll(Collection<?> c) {
        return this.mMotionEvents.removeAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean retainAll(Collection<?> c) {
        return this.mMotionEvents.retainAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public void clear() {
        this.mMotionEvents.clear();
    }

    @Override // java.util.List, java.util.Collection
    public boolean equals(Object o) {
        return this.mMotionEvents.equals(o);
    }

    @Override // java.util.List, java.util.Collection
    public int hashCode() {
        return this.mMotionEvents.hashCode();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.List
    public MotionEvent get(int index) {
        return this.mMotionEvents.get(index);
    }

    @Override // java.util.List
    public MotionEvent set(int index, MotionEvent element) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List
    public ListIterator<MotionEvent> listIterator() {
        return new Iter(0);
    }

    @Override // java.util.List
    public ListIterator<MotionEvent> listIterator(int index) {
        return new Iter(index);
    }

    @Override // java.util.List
    public List<MotionEvent> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public class Iter implements ListIterator<MotionEvent> {
        private final ListIterator<MotionEvent> mIterator;

        Iter(int index) {
            this.mIterator = TimeLimitedMotionEventBuffer.this.mMotionEvents.listIterator(index);
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public boolean hasNext() {
            return this.mIterator.hasNext();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public MotionEvent next() {
            return this.mIterator.next();
        }

        @Override // java.util.ListIterator
        public boolean hasPrevious() {
            return this.mIterator.hasPrevious();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.ListIterator
        public MotionEvent previous() {
            return this.mIterator.previous();
        }

        @Override // java.util.ListIterator
        public int nextIndex() {
            return this.mIterator.nextIndex();
        }

        @Override // java.util.ListIterator
        public int previousIndex() {
            return this.mIterator.previousIndex();
        }

        @Override // java.util.ListIterator, java.util.Iterator
        public void remove() {
            this.mIterator.remove();
        }

        @Override // java.util.ListIterator
        public void set(MotionEvent motionEvent) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.ListIterator
        public void add(MotionEvent element) {
            throw new UnsupportedOperationException();
        }
    }
}
