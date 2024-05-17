package com.badlogic.gdx.utils;

import com.badlogic.gdx.utils.ObjectSet;
import java.util.NoSuchElementException;
/* loaded from: classes21.dex */
public class OrderedSet<T> extends ObjectSet<T> {
    final Array<T> items;
    OrderedSetIterator iterator1;
    OrderedSetIterator iterator2;

    public OrderedSet() {
        this.items = new Array<>();
    }

    public OrderedSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.items = new Array<>(this.capacity);
    }

    public OrderedSet(int initialCapacity) {
        super(initialCapacity);
        this.items = new Array<>(this.capacity);
    }

    public OrderedSet(OrderedSet<? extends T> set) {
        super(set);
        this.items = new Array<>(this.capacity);
        this.items.addAll(set.items);
    }

    @Override // com.badlogic.gdx.utils.ObjectSet
    public boolean add(T key) {
        if (super.add(key)) {
            this.items.add(key);
            return true;
        }
        return false;
    }

    public boolean add(T key, int index) {
        if (!super.add(key)) {
            this.items.removeValue(key, true);
            this.items.insert(index, key);
            return false;
        }
        this.items.insert(index, key);
        return true;
    }

    @Override // com.badlogic.gdx.utils.ObjectSet
    public boolean remove(T key) {
        if (super.remove(key)) {
            this.items.removeValue(key, false);
            return true;
        }
        return false;
    }

    public T removeIndex(int index) {
        T key = this.items.removeIndex(index);
        super.remove(key);
        return key;
    }

    @Override // com.badlogic.gdx.utils.ObjectSet
    public void clear(int maximumCapacity) {
        this.items.clear();
        super.clear(maximumCapacity);
    }

    @Override // com.badlogic.gdx.utils.ObjectSet
    public void clear() {
        this.items.clear();
        super.clear();
    }

    public Array<T> orderedItems() {
        return this.items;
    }

    @Override // com.badlogic.gdx.utils.ObjectSet, java.lang.Iterable
    public OrderedSetIterator<T> iterator() {
        if (Collections.allocateIterators) {
            return new OrderedSetIterator<>(this);
        }
        if (this.iterator1 == null) {
            this.iterator1 = new OrderedSetIterator(this);
            this.iterator2 = new OrderedSetIterator(this);
        }
        if (!this.iterator1.valid) {
            this.iterator1.reset();
            OrderedSetIterator<T> orderedSetIterator = this.iterator1;
            orderedSetIterator.valid = true;
            this.iterator2.valid = false;
            return orderedSetIterator;
        }
        this.iterator2.reset();
        OrderedSetIterator<T> orderedSetIterator2 = this.iterator2;
        orderedSetIterator2.valid = true;
        this.iterator1.valid = false;
        return orderedSetIterator2;
    }

    @Override // com.badlogic.gdx.utils.ObjectSet
    public String toString() {
        if (this.size == 0) {
            return "{}";
        }
        T[] items = this.items.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('{');
        buffer.append(items[0]);
        for (int i = 1; i < this.size; i++) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append('}');
        return buffer.toString();
    }

    @Override // com.badlogic.gdx.utils.ObjectSet
    public String toString(String separator) {
        return this.items.toString(separator);
    }

    /* loaded from: classes21.dex */
    public static class OrderedSetIterator<T> extends ObjectSet.ObjectSetIterator<T> {
        private Array<T> items;

        public OrderedSetIterator(OrderedSet<T> set) {
            super(set);
            this.items = set.items;
        }

        @Override // com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator
        public void reset() {
            this.nextIndex = 0;
            this.hasNext = this.set.size > 0;
        }

        @Override // com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator, java.util.Iterator
        public T next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            T key = this.items.get(this.nextIndex);
            this.nextIndex++;
            this.hasNext = this.nextIndex < this.set.size;
            return key;
        }

        @Override // com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator, java.util.Iterator
        public void remove() {
            if (this.nextIndex < 0) {
                throw new IllegalStateException("next must be called before remove.");
            }
            this.nextIndex--;
            ((OrderedSet) this.set).removeIndex(this.nextIndex);
        }
    }
}
