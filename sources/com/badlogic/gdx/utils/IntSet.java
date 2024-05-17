package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.NoSuchElementException;
/* loaded from: classes21.dex */
public class IntSet {
    private static final int EMPTY = 0;
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    boolean hasZeroValue;
    private int hashShift;
    private IntSetIterator iterator1;
    private IntSetIterator iterator2;
    int[] keyTable;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;

    public IntSet() {
        this(51, 0.8f);
    }

    public IntSet(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public IntSet(int initialCapacity, float loadFactor) {
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
        this.keyTable = new int[this.capacity + this.stashCapacity];
    }

    public IntSet(IntSet set) {
        this((int) Math.floor(set.capacity * set.loadFactor), set.loadFactor);
        this.stashSize = set.stashSize;
        int[] iArr = set.keyTable;
        System.arraycopy(iArr, 0, this.keyTable, 0, iArr.length);
        this.size = set.size;
        this.hasZeroValue = set.hasZeroValue;
    }

    public boolean add(int key) {
        int index2;
        int key2;
        int index3;
        int key3;
        if (key == 0) {
            if (this.hasZeroValue) {
                return false;
            }
            this.hasZeroValue = true;
            this.size++;
            return true;
        }
        int[] keyTable = this.keyTable;
        int index1 = key & this.mask;
        int key1 = keyTable[index1];
        if (key1 == key || (key2 = keyTable[(index2 = hash2(key))]) == key || (key3 = keyTable[(index3 = hash3(key))]) == key) {
            return false;
        }
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (keyTable[i] == key) {
                return false;
            }
            i++;
        }
        if (key1 == 0) {
            keyTable[index1] = key;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return true;
        } else if (key2 == 0) {
            keyTable[index2] = key;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return true;
        } else if (key3 == 0) {
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

    public void addAll(IntArray array) {
        addAll(array.items, 0, array.size);
    }

    public void addAll(IntArray array, int offset, int length) {
        if (offset + length > array.size) {
            throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
        }
        addAll(array.items, offset, length);
    }

    public void addAll(int... array) {
        addAll(array, 0, array.length);
    }

    public void addAll(int[] array, int offset, int length) {
        ensureCapacity(length);
        int i = offset;
        int n = i + length;
        while (i < n) {
            add(array[i]);
            i++;
        }
    }

    public void addAll(IntSet set) {
        ensureCapacity(set.size);
        IntSetIterator iterator = set.iterator();
        while (iterator.hasNext) {
            add(iterator.next());
        }
    }

    private void addResize(int key) {
        if (key == 0) {
            this.hasZeroValue = true;
            return;
        }
        int index1 = key & this.mask;
        int[] iArr = this.keyTable;
        int key1 = iArr[index1];
        if (key1 == 0) {
            iArr[index1] = key;
            int i = this.size;
            this.size = i + 1;
            if (i >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index2 = hash2(key);
        int[] iArr2 = this.keyTable;
        int key2 = iArr2[index2];
        if (key2 == 0) {
            iArr2[index2] = key;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index3 = hash3(key);
        int[] iArr3 = this.keyTable;
        int key3 = iArr3[index3];
        if (key3 == 0) {
            iArr3[index3] = key;
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

    private void push(int insertKey, int index1, int key1, int index2, int key2, int index3, int key3) {
        int evictedKey;
        int[] keyTable = this.keyTable;
        int mask = this.mask;
        int i = 0;
        int pushIterations = this.pushIterations;
        while (true) {
            int random = MathUtils.random(2);
            if (random == 0) {
                evictedKey = key1;
                keyTable[index1] = insertKey;
            } else if (random == 1) {
                evictedKey = key2;
                keyTable[index2] = insertKey;
            } else {
                evictedKey = key3;
                keyTable[index3] = insertKey;
            }
            index1 = evictedKey & mask;
            key1 = keyTable[index1];
            if (key1 == 0) {
                keyTable[index1] = evictedKey;
                int i2 = this.size;
                this.size = i2 + 1;
                if (i2 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index2 = hash2(evictedKey);
            key2 = keyTable[index2];
            if (key2 == 0) {
                keyTable[index2] = evictedKey;
                int i3 = this.size;
                this.size = i3 + 1;
                if (i3 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index3 = hash3(evictedKey);
            key3 = keyTable[index3];
            if (key3 == 0) {
                keyTable[index3] = evictedKey;
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
                insertKey = evictedKey;
            } else {
                addStash(evictedKey);
                return;
            }
        }
    }

    private void addStash(int key) {
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

    public boolean remove(int key) {
        if (key == 0) {
            if (!this.hasZeroValue) {
                return false;
            }
            this.hasZeroValue = false;
            this.size--;
            return true;
        }
        int index = this.mask & key;
        int[] iArr = this.keyTable;
        if (iArr[index] == key) {
            iArr[index] = 0;
            this.size--;
            return true;
        }
        int index2 = hash2(key);
        int[] iArr2 = this.keyTable;
        if (iArr2[index2] == key) {
            iArr2[index2] = 0;
            this.size--;
            return true;
        }
        int index3 = hash3(key);
        int[] iArr3 = this.keyTable;
        if (iArr3[index3] == key) {
            iArr3[index3] = 0;
            this.size--;
            return true;
        }
        return removeStash(key);
    }

    boolean removeStash(int key) {
        int[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (keyTable[i] != key) {
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
            int[] iArr = this.keyTable;
            iArr[index] = iArr[lastIndex];
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
        this.hasZeroValue = false;
        this.size = 0;
        resize(maximumCapacity);
    }

    public void clear() {
        if (this.size == 0) {
            return;
        }
        int[] keyTable = this.keyTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                keyTable[i2] = 0;
                i = i2;
            } else {
                this.size = 0;
                this.stashSize = 0;
                this.hasZeroValue = false;
                return;
            }
        }
    }

    public boolean contains(int key) {
        if (key == 0) {
            return this.hasZeroValue;
        }
        int index = this.mask & key;
        if (this.keyTable[index] != key) {
            int index2 = hash2(key);
            if (this.keyTable[index2] != key) {
                int index3 = hash3(key);
                if (this.keyTable[index3] != key) {
                    return containsKeyStash(key);
                }
                return true;
            }
            return true;
        }
        return true;
    }

    private boolean containsKeyStash(int key) {
        int[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (keyTable[i] == key) {
                return true;
            }
            i++;
        }
        return false;
    }

    public int first() {
        if (this.hasZeroValue) {
            return 0;
        }
        int[] keyTable = this.keyTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            if (keyTable[i] != 0) {
                return keyTable[i];
            }
        }
        throw new IllegalStateException("IntSet is empty.");
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
        int[] oldKeyTable = this.keyTable;
        this.keyTable = new int[this.stashCapacity + newSize];
        int oldSize = this.size;
        this.size = this.hasZeroValue ? 1 : 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i = 0; i < oldEndIndex; i++) {
                int key = oldKeyTable[i];
                if (key != 0) {
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
            int[] iArr = this.keyTable;
            if (iArr[i] != 0) {
                h += iArr[i];
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (obj instanceof IntSet) {
            IntSet other = (IntSet) obj;
            if (other.size == this.size && other.hasZeroValue == this.hasZeroValue) {
                int[] keyTable = this.keyTable;
                int n = this.capacity + this.stashSize;
                for (int i = 0; i < n; i++) {
                    if (keyTable[i] != 0 && !other.contains(keyTable[i])) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0032  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0040  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:15:0x002d -> B:16:0x002e). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String toString() {
        /*
            r5 = this;
            int r0 = r5.size
            if (r0 != 0) goto L7
            java.lang.String r0 = "[]"
            return r0
        L7:
            com.badlogic.gdx.utils.StringBuilder r0 = new com.badlogic.gdx.utils.StringBuilder
            r1 = 32
            r0.<init>(r1)
            r1 = 91
            r0.append(r1)
            int[] r1 = r5.keyTable
            int r2 = r1.length
            boolean r3 = r5.hasZeroValue
            if (r3 == 0) goto L20
            java.lang.String r3 = "0"
            r0.append(r3)
            goto L2e
        L20:
            int r3 = r2 + (-1)
            if (r2 <= 0) goto L2d
            r2 = r1[r3]
            if (r2 != 0) goto L2a
            r2 = r3
            goto L20
        L2a:
            r0.append(r2)
        L2d:
            r2 = r3
        L2e:
            int r3 = r2 + (-1)
            if (r2 <= 0) goto L40
            r2 = r1[r3]
            if (r2 != 0) goto L37
            goto L2d
        L37:
            java.lang.String r4 = ", "
            r0.append(r4)
            r0.append(r2)
            goto L2d
        L40:
            r2 = 93
            r0.append(r2)
            java.lang.String r2 = r0.toString()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.IntSet.toString():java.lang.String");
    }

    public IntSetIterator iterator() {
        if (Collections.allocateIterators) {
            return new IntSetIterator(this);
        }
        if (this.iterator1 == null) {
            this.iterator1 = new IntSetIterator(this);
            this.iterator2 = new IntSetIterator(this);
        }
        if (!this.iterator1.valid) {
            this.iterator1.reset();
            IntSetIterator intSetIterator = this.iterator1;
            intSetIterator.valid = true;
            this.iterator2.valid = false;
            return intSetIterator;
        }
        this.iterator2.reset();
        IntSetIterator intSetIterator2 = this.iterator2;
        intSetIterator2.valid = true;
        this.iterator1.valid = false;
        return intSetIterator2;
    }

    public static IntSet with(int... array) {
        IntSet set = new IntSet();
        set.addAll(array);
        return set;
    }

    /* loaded from: classes21.dex */
    public static class IntSetIterator {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        int currentIndex;
        public boolean hasNext;
        int nextIndex;
        final IntSet set;
        boolean valid = true;

        public IntSetIterator(IntSet set) {
            this.set = set;
            reset();
        }

        public void reset() {
            this.currentIndex = -2;
            this.nextIndex = -1;
            if (this.set.hasZeroValue) {
                this.hasNext = true;
            } else {
                findNextIndex();
            }
        }

        void findNextIndex() {
            this.hasNext = false;
            int[] keyTable = this.set.keyTable;
            int n = this.set.capacity + this.set.stashSize;
            do {
                int i = this.nextIndex + 1;
                this.nextIndex = i;
                if (i >= n) {
                    return;
                }
            } while (keyTable[this.nextIndex] == 0);
            this.hasNext = true;
        }

        public void remove() {
            if (this.currentIndex == -1 && this.set.hasZeroValue) {
                this.set.hasZeroValue = false;
            } else {
                int i = this.currentIndex;
                if (i < 0) {
                    throw new IllegalStateException("next must be called before remove.");
                }
                if (i >= this.set.capacity) {
                    this.set.removeStashIndex(this.currentIndex);
                    this.nextIndex = this.currentIndex - 1;
                    findNextIndex();
                } else {
                    this.set.keyTable[this.currentIndex] = 0;
                }
            }
            this.currentIndex = -2;
            IntSet intSet = this.set;
            intSet.size--;
        }

        public int next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            int key = this.nextIndex == -1 ? 0 : this.set.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return key;
        }

        public IntArray toArray() {
            IntArray array = new IntArray(true, this.set.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }
}
