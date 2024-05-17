package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;
/* loaded from: classes21.dex */
public class ObjectSet<T> implements Iterable<T> {
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    private int hashShift;
    private ObjectSetIterator iterator1;
    private ObjectSetIterator iterator2;
    T[] keyTable;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;

    public ObjectSet() {
        this(51, 0.8f);
    }

    public ObjectSet(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public ObjectSet(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0.0f) {
            throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor);
        }
        this.loadFactor = loadFactor;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
        }
        int initialCapacity2 = MathUtils.nextPowerOfTwo((int) Math.ceil(initialCapacity / loadFactor));
        if (initialCapacity2 > 1073741824) {
            throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity2);
        }
        this.capacity = initialCapacity2;
        int i = this.capacity;
        this.threshold = (int) (i * loadFactor);
        this.mask = i - 1;
        this.hashShift = 31 - Integer.numberOfTrailingZeros(i);
        this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log(this.capacity))) * 2);
        this.pushIterations = Math.max(Math.min(this.capacity, 8), ((int) Math.sqrt(this.capacity)) / 8);
        this.keyTable = (T[]) new Object[this.capacity + this.stashCapacity];
    }

    public ObjectSet(ObjectSet<? extends T> set) {
        this((int) Math.floor(set.capacity * set.loadFactor), set.loadFactor);
        this.stashSize = set.stashSize;
        Object[] objArr = set.keyTable;
        System.arraycopy(objArr, 0, this.keyTable, 0, objArr.length);
        this.size = set.size;
    }

    public boolean add(T key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null.");
        }
        T[] keyTable = this.keyTable;
        int hashCode = key.hashCode();
        int index1 = hashCode & this.mask;
        T key1 = keyTable[index1];
        if (key.equals(key1)) {
            return false;
        }
        int index2 = hash2(hashCode);
        T key2 = keyTable[index2];
        if (key.equals(key2)) {
            return false;
        }
        int index3 = hash3(hashCode);
        T key3 = keyTable[index3];
        if (key.equals(key3)) {
            return false;
        }
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key.equals(keyTable[i])) {
                return false;
            }
            i++;
        }
        if (key1 == null) {
            keyTable[index1] = key;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return true;
        } else if (key2 == null) {
            keyTable[index2] = key;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return true;
        } else if (key3 == null) {
            keyTable[index3] = key;
            int i4 = this.size;
            this.size = i4 + 1;
            if (i4 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return true;
        } else {
            push(key, index1, key1, index2, key2, index3, key3);
            return true;
        }
    }

    public void addAll(Array<? extends T> array) {
        addAll(array.items, 0, array.size);
    }

    public void addAll(Array<? extends T> array, int offset, int length) {
        if (offset + length > array.size) {
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
        }
        addAll(array.items, offset, length);
    }

    public void addAll(T... array) {
        addAll(array, 0, array.length);
    }

    public void addAll(T[] array, int offset, int length) {
        ensureCapacity(length);
        int i = offset;
        int n = i + length;
        while (i < n) {
            add(array[i]);
            i++;
        }
    }

    public void addAll(ObjectSet<T> set) {
        ensureCapacity(set.size);
        ObjectSetIterator<T> it = set.iterator();
        while (it.hasNext()) {
            T key = it.next();
            add(key);
        }
    }

    private void addResize(T key) {
        int hashCode = key.hashCode();
        int index1 = hashCode & this.mask;
        T[] tArr = this.keyTable;
        T key1 = tArr[index1];
        if (key1 == null) {
            tArr[index1] = key;
            int i = this.size;
            this.size = i + 1;
            if (i >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index2 = hash2(hashCode);
        T[] tArr2 = this.keyTable;
        T key2 = tArr2[index2];
        if (key2 == null) {
            tArr2[index2] = key;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index3 = hash3(hashCode);
        T[] tArr3 = this.keyTable;
        T key3 = tArr3[index3];
        if (key3 == null) {
            tArr3[index3] = key;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        push(key, index1, key1, index2, key2, index3, key3);
    }

    private void push(T insertKey, int index1, T key1, int index2, T key2, int index3, T key3) {
        T evictedKey;
        T[] keyTable = this.keyTable;
        int mask = this.mask;
        int pushIterations = this.pushIterations;
        T insertKey2 = insertKey;
        int index12 = index1;
        T key12 = key1;
        int index22 = index2;
        T key22 = key2;
        int index32 = index3;
        int i = 0;
        T key32 = key3;
        while (true) {
            int random = MathUtils.random(2);
            if (random == 0) {
                evictedKey = key12;
                keyTable[index12] = insertKey2;
            } else if (random == 1) {
                evictedKey = key22;
                keyTable[index22] = insertKey2;
            } else {
                evictedKey = key32;
                keyTable[index32] = insertKey2;
            }
            int hashCode = evictedKey.hashCode();
            index12 = hashCode & mask;
            key12 = keyTable[index12];
            if (key12 == null) {
                keyTable[index12] = evictedKey;
                int i2 = this.size;
                this.size = i2 + 1;
                if (i2 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index22 = hash2(hashCode);
            key22 = keyTable[index22];
            if (key22 == null) {
                keyTable[index22] = evictedKey;
                int i3 = this.size;
                this.size = i3 + 1;
                if (i3 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index32 = hash3(hashCode);
            key32 = keyTable[index32];
            if (key32 == null) {
                keyTable[index32] = evictedKey;
                int i4 = this.size;
                this.size = i4 + 1;
                if (i4 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            i++;
            if (i != pushIterations) {
                insertKey2 = evictedKey;
            } else {
                addStash(evictedKey);
                return;
            }
        }
    }

    private void addStash(T key) {
        int i = this.stashSize;
        if (i == this.stashCapacity) {
            resize(this.capacity << 1);
            addResize(key);
            return;
        }
        int index = this.capacity + i;
        this.keyTable[index] = key;
        this.stashSize = i + 1;
        this.size++;
    }

    public boolean remove(T key) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            this.size--;
            return true;
        }
        int index2 = hash2(hashCode);
        if (key.equals(this.keyTable[index2])) {
            this.keyTable[index2] = null;
            this.size--;
            return true;
        }
        int index3 = hash3(hashCode);
        if (key.equals(this.keyTable[index3])) {
            this.keyTable[index3] = null;
            this.size--;
            return true;
        }
        return removeStash(key);
    }

    boolean removeStash(T key) {
        T[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (!key.equals(keyTable[i])) {
                i++;
            } else {
                removeStashIndex(i);
                this.size--;
                return true;
            }
        }
        return false;
    }

    void removeStashIndex(int index) {
        this.stashSize--;
        int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            T[] tArr = this.keyTable;
            tArr[index] = tArr[lastIndex];
            tArr[lastIndex] = null;
        }
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void shrink(int maximumCapacity) {
        if (maximumCapacity < 0) {
            throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
        }
        if (this.size > maximumCapacity) {
            maximumCapacity = this.size;
        }
        if (this.capacity <= maximumCapacity) {
            return;
        }
        resize(MathUtils.nextPowerOfTwo(maximumCapacity));
    }

    public void clear(int maximumCapacity) {
        if (this.capacity <= maximumCapacity) {
            clear();
            return;
        }
        this.size = 0;
        resize(maximumCapacity);
    }

    public void clear() {
        if (this.size == 0) {
            return;
        }
        T[] keyTable = this.keyTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                keyTable[i2] = null;
                i = i2;
            } else {
                this.size = 0;
                this.stashSize = 0;
                return;
            }
        }
    }

    public boolean contains(T key) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (!key.equals(this.keyTable[index])) {
            int index2 = hash2(hashCode);
            if (!key.equals(this.keyTable[index2])) {
                int index3 = hash3(hashCode);
                return key.equals(this.keyTable[index3]) || getKeyStash(key) != null;
            }
        }
        return true;
    }

    public T get(T key) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        T found = this.keyTable[index];
        if (!key.equals(found)) {
            int index2 = hash2(hashCode);
            found = this.keyTable[index2];
            if (!key.equals(found)) {
                int index3 = hash3(hashCode);
                found = this.keyTable[index3];
                if (!key.equals(found)) {
                    return getKeyStash(key);
                }
            }
        }
        return found;
    }

    private T getKeyStash(T key) {
        T[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key.equals(keyTable[i])) {
                return keyTable[i];
            }
            i++;
        }
        return null;
    }

    public T first() {
        T[] keyTable = this.keyTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            if (keyTable[i] != null) {
                return keyTable[i];
            }
        }
        throw new IllegalStateException("ObjectSet is empty.");
    }

    public void ensureCapacity(int additionalCapacity) {
        if (additionalCapacity < 0) {
            throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
        }
        int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded >= this.threshold) {
            resize(MathUtils.nextPowerOfTwo((int) Math.ceil(sizeNeeded / this.loadFactor)));
        }
    }

    private void resize(int newSize) {
        int oldEndIndex = this.capacity + this.stashSize;
        this.capacity = newSize;
        this.threshold = (int) (newSize * this.loadFactor);
        this.mask = newSize - 1;
        this.hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
        this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log(newSize))) * 2);
        this.pushIterations = Math.max(Math.min(newSize, 8), ((int) Math.sqrt(newSize)) / 8);
        T[] oldKeyTable = this.keyTable;
        this.keyTable = (T[]) new Object[this.stashCapacity + newSize];
        int oldSize = this.size;
        this.size = 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i = 0; i < oldEndIndex; i++) {
                T key = oldKeyTable[i];
                if (key != null) {
                    addResize(key);
                }
            }
        }
    }

    private int hash2(int h) {
        int h2 = h * PRIME2;
        return ((h2 >>> this.hashShift) ^ h2) & this.mask;
    }

    private int hash3(int h) {
        int h2 = h * PRIME3;
        return ((h2 >>> this.hashShift) ^ h2) & this.mask;
    }

    public int hashCode() {
        int h = 0;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            T[] tArr = this.keyTable;
            if (tArr[i] != null) {
                h += tArr[i].hashCode();
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ObjectSet) {
            ObjectSet other = (ObjectSet) obj;
            if (other.size != this.size) {
                return false;
            }
            T[] keyTable = this.keyTable;
            int n = this.capacity + this.stashSize;
            for (int i = 0; i < n; i++) {
                if (keyTable[i] != null && !other.contains(keyTable[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public String toString() {
        return '{' + toString(", ") + '}';
    }

    public String toString(String separator) {
        int i;
        if (this.size == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(32);
        T[] keyTable = this.keyTable;
        int i2 = keyTable.length;
        while (true) {
            i = i2 - 1;
            if (i2 > 0) {
                T key = keyTable[i];
                if (key != null) {
                    buffer.append(key);
                    break;
                }
                i2 = i;
            } else {
                break;
            }
        }
        while (true) {
            int i3 = i - 1;
            if (i > 0) {
                T key2 = keyTable[i3];
                if (key2 != null) {
                    buffer.append(separator);
                    buffer.append(key2);
                }
                i = i3;
            } else {
                return buffer.toString();
            }
        }
    }

    @Override // java.lang.Iterable
    public ObjectSetIterator<T> iterator() {
        if (Collections.allocateIterators) {
            return new ObjectSetIterator<>(this);
        }
        if (this.iterator1 == null) {
            this.iterator1 = new ObjectSetIterator(this);
            this.iterator2 = new ObjectSetIterator(this);
        }
        if (!this.iterator1.valid) {
            this.iterator1.reset();
            ObjectSetIterator<T> objectSetIterator = this.iterator1;
            objectSetIterator.valid = true;
            this.iterator2.valid = false;
            return objectSetIterator;
        }
        this.iterator2.reset();
        ObjectSetIterator<T> objectSetIterator2 = this.iterator2;
        objectSetIterator2.valid = true;
        this.iterator1.valid = false;
        return objectSetIterator2;
    }

    public static <T> ObjectSet<T> with(T... array) {
        ObjectSet set = new ObjectSet();
        set.addAll(array);
        return set;
    }

    /* loaded from: classes21.dex */
    public static class ObjectSetIterator<K> implements Iterable<K>, Iterator<K> {
        int currentIndex;
        public boolean hasNext;
        int nextIndex;
        final ObjectSet<K> set;
        boolean valid = true;

        public ObjectSetIterator(ObjectSet<K> set) {
            this.set = set;
            reset();
        }

        public void reset() {
            this.currentIndex = -1;
            this.nextIndex = -1;
            findNextIndex();
        }

        private void findNextIndex() {
            this.hasNext = false;
            K[] keyTable = this.set.keyTable;
            int n = this.set.capacity + this.set.stashSize;
            do {
                int i = this.nextIndex + 1;
                this.nextIndex = i;
                if (i >= n) {
                    return;
                }
            } while (keyTable[this.nextIndex] == null);
            this.hasNext = true;
        }

        @Override // java.util.Iterator
        public void remove() {
            int i = this.currentIndex;
            if (i < 0) {
                throw new IllegalStateException("next must be called before remove.");
            }
            if (i >= this.set.capacity) {
                this.set.removeStashIndex(this.currentIndex);
                this.nextIndex = this.currentIndex - 1;
                findNextIndex();
            } else {
                this.set.keyTable[this.currentIndex] = null;
            }
            this.currentIndex = -1;
            ObjectSet<K> objectSet = this.set;
            objectSet.size--;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.hasNext;
        }

        @Override // java.util.Iterator
        public K next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            K[] kArr = this.set.keyTable;
            int i = this.nextIndex;
            K key = kArr[i];
            this.currentIndex = i;
            findNextIndex();
            return key;
        }

        @Override // java.lang.Iterable
        public ObjectSetIterator<K> iterator() {
            return this;
        }

        public Array<K> toArray(Array<K> array) {
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }

        public Array<K> toArray() {
            return toArray(new Array<>(true, this.set.size));
        }
    }
}
