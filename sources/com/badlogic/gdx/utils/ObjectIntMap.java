package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;
/* loaded from: classes21.dex */
public class ObjectIntMap<K> implements Iterable<Entry<K>> {
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    private Entries entries1;
    private Entries entries2;
    private int hashShift;
    K[] keyTable;
    private Keys keys1;
    private Keys keys2;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;
    int[] valueTable;
    private Values values1;
    private Values values2;

    public ObjectIntMap() {
        this(51, 0.8f);
    }

    public ObjectIntMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public ObjectIntMap(int initialCapacity, float loadFactor) {
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
        this.keyTable = (K[]) new Object[this.capacity + this.stashCapacity];
        this.valueTable = new int[this.keyTable.length];
    }

    public ObjectIntMap(ObjectIntMap<? extends K> map) {
        this((int) Math.floor(map.capacity * map.loadFactor), map.loadFactor);
        this.stashSize = map.stashSize;
        Object[] objArr = map.keyTable;
        System.arraycopy(objArr, 0, this.keyTable, 0, objArr.length);
        int[] iArr = map.valueTable;
        System.arraycopy(iArr, 0, this.valueTable, 0, iArr.length);
        this.size = map.size;
    }

    public void put(K key, int value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null.");
        }
        K[] keyTable = this.keyTable;
        int hashCode = key.hashCode();
        int index1 = hashCode & this.mask;
        K key1 = keyTable[index1];
        if (key.equals(key1)) {
            this.valueTable[index1] = value;
            return;
        }
        int index2 = hash2(hashCode);
        K key2 = keyTable[index2];
        if (key.equals(key2)) {
            this.valueTable[index2] = value;
            return;
        }
        int index3 = hash3(hashCode);
        K key3 = keyTable[index3];
        if (key.equals(key3)) {
            this.valueTable[index3] = value;
            return;
        }
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (!key.equals(keyTable[i])) {
                i++;
            } else {
                this.valueTable[i] = value;
                return;
            }
        }
        if (key1 == null) {
            keyTable[index1] = key;
            this.valueTable[index1] = value;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
            }
        } else if (key2 == null) {
            keyTable[index2] = key;
            this.valueTable[index2] = value;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
        } else if (key3 == null) {
            keyTable[index3] = key;
            this.valueTable[index3] = value;
            int i4 = this.size;
            this.size = i4 + 1;
            if (i4 >= this.threshold) {
                resize(this.capacity << 1);
            }
        } else {
            push(key, value, index1, key1, index2, key2, index3, key3);
        }
    }

    public void putAll(ObjectIntMap<? extends K> map) {
        Entries<? extends K> it = map.entries().iterator();
        while (it.hasNext()) {
            Entry<K> next = it.next();
            put(next.key, next.value);
        }
    }

    private void putResize(K key, int value) {
        int hashCode = key.hashCode();
        int index1 = hashCode & this.mask;
        K[] kArr = this.keyTable;
        K key1 = kArr[index1];
        if (key1 == null) {
            kArr[index1] = key;
            this.valueTable[index1] = value;
            int i = this.size;
            this.size = i + 1;
            if (i >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index2 = hash2(hashCode);
        K[] kArr2 = this.keyTable;
        K key2 = kArr2[index2];
        if (key2 == null) {
            kArr2[index2] = key;
            this.valueTable[index2] = value;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index3 = hash3(hashCode);
        K[] kArr3 = this.keyTable;
        K key3 = kArr3[index3];
        if (key3 == null) {
            kArr3[index3] = key;
            this.valueTable[index3] = value;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        push(key, value, index1, key1, index2, key2, index3, key3);
    }

    private void push(K insertKey, int insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
        K evictedKey;
        int evictedValue;
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        int mask = this.mask;
        int pushIterations = this.pushIterations;
        K insertKey2 = insertKey;
        int insertValue2 = insertValue;
        int index12 = index1;
        K key12 = key1;
        int index22 = index2;
        K key22 = key2;
        int index32 = index3;
        int i = 0;
        K key32 = key3;
        while (true) {
            int random = MathUtils.random(2);
            if (random == 0) {
                evictedKey = key12;
                int evictedValue2 = valueTable[index12];
                keyTable[index12] = insertKey2;
                valueTable[index12] = insertValue2;
                evictedValue = evictedValue2;
            } else if (random == 1) {
                evictedKey = key22;
                int evictedValue3 = valueTable[index22];
                keyTable[index22] = insertKey2;
                valueTable[index22] = insertValue2;
                evictedValue = evictedValue3;
            } else {
                evictedKey = key32;
                int evictedValue4 = valueTable[index32];
                keyTable[index32] = insertKey2;
                valueTable[index32] = insertValue2;
                evictedValue = evictedValue4;
            }
            int hashCode = evictedKey.hashCode();
            index12 = hashCode & mask;
            key12 = keyTable[index12];
            if (key12 == null) {
                keyTable[index12] = evictedKey;
                valueTable[index12] = evictedValue;
                int mask2 = this.size;
                int index33 = mask2 + 1;
                this.size = index33;
                if (mask2 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            int mask3 = mask;
            index22 = hash2(hashCode);
            key22 = keyTable[index22];
            if (key22 == null) {
                keyTable[index22] = evictedKey;
                valueTable[index22] = evictedValue;
                int i2 = this.size;
                this.size = i2 + 1;
                if (i2 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index32 = hash3(hashCode);
            K key33 = keyTable[index32];
            if (key33 == null) {
                keyTable[index32] = evictedKey;
                valueTable[index32] = evictedValue;
                int i3 = this.size;
                this.size = i3 + 1;
                if (i3 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            K[] keyTable2 = keyTable;
            int[] valueTable2 = valueTable;
            i++;
            if (i != pushIterations) {
                insertKey2 = evictedKey;
                insertValue2 = evictedValue;
                key32 = key33;
                mask = mask3;
                keyTable = keyTable2;
                valueTable = valueTable2;
            } else {
                putStash(evictedKey, evictedValue);
                return;
            }
        }
    }

    private void putStash(K key, int value) {
        int i = this.stashSize;
        if (i == this.stashCapacity) {
            resize(this.capacity << 1);
            putResize(key, value);
            return;
        }
        int index = this.capacity + i;
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        this.stashSize = i + 1;
        this.size++;
    }

    public int get(K key, int defaultValue) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (!key.equals(this.keyTable[index])) {
            index = hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = hash3(hashCode);
                if (!key.equals(this.keyTable[index])) {
                    return getStash(key, defaultValue);
                }
            }
        }
        return this.valueTable[index];
    }

    private int getStash(K key, int defaultValue) {
        K[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key.equals(keyTable[i])) {
                return this.valueTable[i];
            }
            i++;
        }
        return defaultValue;
    }

    public int getAndIncrement(K key, int defaultValue, int increment) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (!key.equals(this.keyTable[index])) {
            index = hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = hash3(hashCode);
                if (!key.equals(this.keyTable[index])) {
                    return getAndIncrementStash(key, defaultValue, increment);
                }
            }
        }
        int[] iArr = this.valueTable;
        int value = iArr[index];
        iArr[index] = value + increment;
        return value;
    }

    private int getAndIncrementStash(K key, int defaultValue, int increment) {
        K[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (!key.equals(keyTable[i])) {
                i++;
            } else {
                int[] iArr = this.valueTable;
                int value = iArr[i];
                iArr[i] = value + increment;
                return value;
            }
        }
        put(key, defaultValue + increment);
        return defaultValue;
    }

    public int remove(K key, int defaultValue) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            int oldValue = this.valueTable[index];
            this.size--;
            return oldValue;
        }
        int index2 = hash2(hashCode);
        if (key.equals(this.keyTable[index2])) {
            this.keyTable[index2] = null;
            int oldValue2 = this.valueTable[index2];
            this.size--;
            return oldValue2;
        }
        int index3 = hash3(hashCode);
        if (key.equals(this.keyTable[index3])) {
            this.keyTable[index3] = null;
            int oldValue3 = this.valueTable[index3];
            this.size--;
            return oldValue3;
        }
        int oldValue4 = removeStash(key, defaultValue);
        return oldValue4;
    }

    int removeStash(K key, int defaultValue) {
        K[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (!key.equals(keyTable[i])) {
                i++;
            } else {
                int oldValue = this.valueTable[i];
                removeStashIndex(i);
                this.size--;
                return oldValue;
            }
        }
        return defaultValue;
    }

    void removeStashIndex(int index) {
        this.stashSize--;
        int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            K[] kArr = this.keyTable;
            kArr[index] = kArr[lastIndex];
            int[] iArr = this.valueTable;
            iArr[index] = iArr[lastIndex];
            kArr[lastIndex] = null;
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
        K[] keyTable = this.keyTable;
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

    public boolean containsValue(int value) {
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                if (keyTable[i2] != null && valueTable[i2] == value) {
                    return true;
                }
                i = i2;
            } else {
                return false;
            }
        }
    }

    public boolean containsKey(K key) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (!key.equals(this.keyTable[index])) {
            int index2 = hash2(hashCode);
            if (!key.equals(this.keyTable[index2])) {
                int index3 = hash3(hashCode);
                if (key.equals(this.keyTable[index3])) {
                    return true;
                }
                return containsKeyStash(key);
            }
            return true;
        }
        return true;
    }

    private boolean containsKeyStash(K key) {
        K[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key.equals(keyTable[i])) {
                return true;
            }
            i++;
        }
        return false;
    }

    public K findKey(int value) {
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                if (keyTable[i2] != null && valueTable[i2] == value) {
                    return keyTable[i2];
                }
                i = i2;
            } else {
                return null;
            }
        }
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
        K[] oldKeyTable = this.keyTable;
        int[] oldValueTable = this.valueTable;
        int i = this.stashCapacity;
        this.keyTable = (K[]) new Object[newSize + i];
        this.valueTable = new int[i + newSize];
        int oldSize = this.size;
        this.size = 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i2 = 0; i2 < oldEndIndex; i2++) {
                K key = oldKeyTable[i2];
                if (key != null) {
                    putResize(key, oldValueTable[i2]);
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
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            K key = keyTable[i];
            if (key != null) {
                int h2 = h + (key.hashCode() * 31);
                int value = valueTable[i];
                h = h2 + value;
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ObjectIntMap) {
            ObjectIntMap<K> other = (ObjectIntMap) obj;
            if (other.size != this.size) {
                return false;
            }
            K[] keyTable = this.keyTable;
            int[] valueTable = this.valueTable;
            int n = this.capacity + this.stashSize;
            for (int i = 0; i < n; i++) {
                K key = keyTable[i];
                if (key != null) {
                    int otherValue = other.get(key, 0);
                    if (otherValue == 0 && !other.containsKey(key)) {
                        return false;
                    }
                    int value = valueTable[i];
                    if (otherValue != value) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public String toString() {
        int i;
        if (this.size == 0) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('{');
        K[] keyTable = this.keyTable;
        int[] valueTable = this.valueTable;
        int i2 = keyTable.length;
        while (true) {
            i = i2 - 1;
            if (i2 > 0) {
                K key = keyTable[i];
                if (key != null) {
                    buffer.append(key);
                    buffer.append('=');
                    buffer.append(valueTable[i]);
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
                K key2 = keyTable[i3];
                if (key2 != null) {
                    buffer.append(", ");
                    buffer.append(key2);
                    buffer.append('=');
                    buffer.append(valueTable[i3]);
                }
                i = i3;
            } else {
                buffer.append('}');
                return buffer.toString();
            }
        }
    }

    @Override // java.lang.Iterable
    public Entries<K> iterator() {
        return entries();
    }

    public Entries<K> entries() {
        if (Collections.allocateIterators) {
            return new Entries<>(this);
        }
        if (this.entries1 == null) {
            this.entries1 = new Entries(this);
            this.entries2 = new Entries(this);
        }
        if (!this.entries1.valid) {
            this.entries1.reset();
            Entries<K> entries = this.entries1;
            entries.valid = true;
            this.entries2.valid = false;
            return entries;
        }
        this.entries2.reset();
        Entries<K> entries2 = this.entries2;
        entries2.valid = true;
        this.entries1.valid = false;
        return entries2;
    }

    public Values values() {
        if (Collections.allocateIterators) {
            return new Values(this);
        }
        if (this.values1 == null) {
            this.values1 = new Values(this);
            this.values2 = new Values(this);
        }
        if (!this.values1.valid) {
            this.values1.reset();
            Values values = this.values1;
            values.valid = true;
            this.values2.valid = false;
            return values;
        }
        this.values2.reset();
        Values values2 = this.values2;
        values2.valid = true;
        this.values1.valid = false;
        return values2;
    }

    public Keys<K> keys() {
        if (Collections.allocateIterators) {
            return new Keys<>(this);
        }
        if (this.keys1 == null) {
            this.keys1 = new Keys(this);
            this.keys2 = new Keys(this);
        }
        if (!this.keys1.valid) {
            this.keys1.reset();
            Keys<K> keys = this.keys1;
            keys.valid = true;
            this.keys2.valid = false;
            return keys;
        }
        this.keys2.reset();
        Keys<K> keys2 = this.keys2;
        keys2.valid = true;
        this.keys1.valid = false;
        return keys2;
    }

    /* loaded from: classes21.dex */
    public static class Entry<K> {
        public K key;
        public int value;

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class MapIterator<K> {
        int currentIndex;
        public boolean hasNext;
        final ObjectIntMap<K> map;
        int nextIndex;
        boolean valid = true;

        public MapIterator(ObjectIntMap<K> map) {
            this.map = map;
            reset();
        }

        public void reset() {
            this.currentIndex = -1;
            this.nextIndex = -1;
            findNextIndex();
        }

        void findNextIndex() {
            this.hasNext = false;
            K[] keyTable = this.map.keyTable;
            int n = this.map.capacity + this.map.stashSize;
            do {
                int i = this.nextIndex + 1;
                this.nextIndex = i;
                if (i >= n) {
                    return;
                }
            } while (keyTable[this.nextIndex] == null);
            this.hasNext = true;
        }

        public void remove() {
            int i = this.currentIndex;
            if (i < 0) {
                throw new IllegalStateException("next must be called before remove.");
            }
            if (i >= this.map.capacity) {
                this.map.removeStashIndex(this.currentIndex);
                this.nextIndex = this.currentIndex - 1;
                findNextIndex();
            } else {
                this.map.keyTable[this.currentIndex] = null;
            }
            this.currentIndex = -1;
            ObjectIntMap<K> objectIntMap = this.map;
            objectIntMap.size--;
        }
    }

    /* loaded from: classes21.dex */
    public static class Entries<K> extends MapIterator<K> implements Iterable<Entry<K>>, Iterator<Entry<K>> {
        private Entry<K> entry;

        @Override // com.badlogic.gdx.utils.ObjectIntMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Entries(ObjectIntMap<K> map) {
            super(map);
            this.entry = new Entry<>();
        }

        @Override // java.util.Iterator
        public Entry<K> next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            K[] keyTable = this.map.keyTable;
            this.entry.key = keyTable[this.nextIndex];
            this.entry.value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return this.entry;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.hasNext;
        }

        @Override // java.lang.Iterable
        public Entries<K> iterator() {
            return this;
        }

        @Override // com.badlogic.gdx.utils.ObjectIntMap.MapIterator, java.util.Iterator
        public void remove() {
            super.remove();
        }
    }

    /* loaded from: classes21.dex */
    public static class Values extends MapIterator<Object> {
        @Override // com.badlogic.gdx.utils.ObjectIntMap.MapIterator, java.util.Iterator
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        @Override // com.badlogic.gdx.utils.ObjectIntMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Values(ObjectIntMap<?> map) {
            super(map);
        }

        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.hasNext;
        }

        public int next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            int value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return value;
        }

        public IntArray toArray() {
            IntArray array = new IntArray(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }

    /* loaded from: classes21.dex */
    public static class Keys<K> extends MapIterator<K> implements Iterable<K>, Iterator<K> {
        @Override // com.badlogic.gdx.utils.ObjectIntMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Keys(ObjectIntMap<K> map) {
            super(map);
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
            K key = this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return key;
        }

        @Override // java.lang.Iterable
        public Keys<K> iterator() {
            return this;
        }

        public Array<K> toArray() {
            Array array = new Array(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }

        public Array<K> toArray(Array<K> array) {
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }

        @Override // com.badlogic.gdx.utils.ObjectIntMap.MapIterator, java.util.Iterator
        public void remove() {
            super.remove();
        }
    }
}
