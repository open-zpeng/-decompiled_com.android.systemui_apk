package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;
/* loaded from: classes21.dex */
public class LongMap<V> implements Iterable<Entry<V>> {
    private static final int EMPTY = 0;
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    private Entries entries1;
    private Entries entries2;
    boolean hasZeroValue;
    private int hashShift;
    long[] keyTable;
    private Keys keys1;
    private Keys keys2;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;
    V[] valueTable;
    private Values values1;
    private Values values2;
    V zeroValue;

    public LongMap() {
        this(51, 0.8f);
    }

    public LongMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public LongMap(int initialCapacity, float loadFactor) {
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
        this.hashShift = 63 - Long.numberOfTrailingZeros(i);
        this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log(this.capacity))) * 2);
        this.pushIterations = Math.max(Math.min(this.capacity, 8), ((int) Math.sqrt(this.capacity)) / 8);
        this.keyTable = new long[this.capacity + this.stashCapacity];
        this.valueTable = (V[]) new Object[this.keyTable.length];
    }

    public LongMap(LongMap<? extends V> map) {
        this((int) Math.floor(map.capacity * map.loadFactor), map.loadFactor);
        this.stashSize = map.stashSize;
        long[] jArr = map.keyTable;
        System.arraycopy(jArr, 0, this.keyTable, 0, jArr.length);
        Object[] objArr = map.valueTable;
        System.arraycopy(objArr, 0, this.valueTable, 0, objArr.length);
        this.size = map.size;
        this.zeroValue = map.zeroValue;
        this.hasZeroValue = map.hasZeroValue;
    }

    public V put(long key, V value) {
        if (key == 0) {
            V oldValue = this.zeroValue;
            this.zeroValue = value;
            if (!this.hasZeroValue) {
                this.hasZeroValue = true;
                this.size++;
            }
            return oldValue;
        }
        long[] keyTable = this.keyTable;
        int index1 = (int) (key & this.mask);
        long key1 = keyTable[index1];
        if (key1 == key) {
            V[] vArr = this.valueTable;
            V oldValue2 = vArr[index1];
            vArr[index1] = value;
            return oldValue2;
        }
        int index2 = hash2(key);
        long key2 = keyTable[index2];
        if (key2 == key) {
            V[] vArr2 = this.valueTable;
            V oldValue3 = vArr2[index2];
            vArr2[index2] = value;
            return oldValue3;
        }
        int index3 = hash3(key);
        long key3 = keyTable[index3];
        if (key3 == key) {
            V[] vArr3 = this.valueTable;
            V oldValue4 = vArr3[index3];
            vArr3[index3] = value;
            return oldValue4;
        }
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (keyTable[i] != key) {
                i++;
            } else {
                V[] vArr4 = this.valueTable;
                V oldValue5 = vArr4[i];
                vArr4[i] = value;
                return oldValue5;
            }
        }
        if (key1 == 0) {
            keyTable[index1] = key;
            this.valueTable[index1] = value;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else if (key2 == 0) {
            keyTable[index2] = key;
            this.valueTable[index2] = value;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else if (key3 == 0) {
            keyTable[index3] = key;
            this.valueTable[index3] = value;
            int i4 = this.size;
            this.size = i4 + 1;
            if (i4 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else {
            push(key, value, index1, key1, index2, key2, index3, key3);
            return null;
        }
    }

    public void putAll(LongMap<? extends V> map) {
        Iterator<Entry<? extends V>> it = map.entries().iterator();
        while (it.hasNext()) {
            Entry<? extends V> entry = it.next();
            put(entry.key, entry.value);
        }
    }

    private void putResize(long key, V value) {
        if (key == 0) {
            this.zeroValue = value;
            this.hasZeroValue = true;
            return;
        }
        int index1 = (int) (key & this.mask);
        long[] jArr = this.keyTable;
        long key1 = jArr[index1];
        if (key1 == 0) {
            jArr[index1] = key;
            this.valueTable[index1] = value;
            int i = this.size;
            this.size = i + 1;
            if (i >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index2 = hash2(key);
        long[] jArr2 = this.keyTable;
        long key2 = jArr2[index2];
        if (key2 == 0) {
            jArr2[index2] = key;
            this.valueTable[index2] = value;
            int i2 = this.size;
            this.size = i2 + 1;
            if (i2 >= this.threshold) {
                resize(this.capacity << 1);
                return;
            }
            return;
        }
        int index3 = hash3(key);
        long[] jArr3 = this.keyTable;
        long key3 = jArr3[index3];
        if (key3 == 0) {
            jArr3[index3] = key;
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

    private void push(long insertKey, V insertValue, int index1, long key1, int index2, long key2, int index3, long key3) {
        V evictedValue;
        long[] keyTable;
        long evictedKey;
        long[] keyTable2 = this.keyTable;
        V[] valueTable = this.valueTable;
        int mask = this.mask;
        int pushIterations = this.pushIterations;
        long insertKey2 = insertKey;
        V insertValue2 = insertValue;
        int index12 = index1;
        long key12 = key1;
        int index22 = index2;
        long key22 = key2;
        long key32 = key3;
        int i = 0;
        int index32 = index3;
        while (true) {
            int pushIterations2 = pushIterations;
            int pushIterations3 = MathUtils.random(2);
            if (pushIterations3 == 0) {
                long evictedKey2 = key12;
                evictedValue = valueTable[index12];
                keyTable2[index12] = insertKey2;
                valueTable[index12] = insertValue2;
                keyTable = keyTable2;
                evictedKey = evictedKey2;
            } else if (pushIterations3 == 1) {
                long evictedKey3 = key22;
                evictedValue = valueTable[index22];
                keyTable2[index22] = insertKey2;
                valueTable[index22] = insertValue2;
                keyTable = keyTable2;
                evictedKey = evictedKey3;
            } else {
                long evictedKey4 = key32;
                evictedValue = valueTable[index32];
                keyTable2[index32] = insertKey2;
                valueTable[index32] = insertValue2;
                keyTable = keyTable2;
                evictedKey = evictedKey4;
            }
            long key33 = mask;
            index12 = (int) (key33 & evictedKey);
            key12 = keyTable[index12];
            if (key12 == 0) {
                keyTable[index12] = evictedKey;
                valueTable[index12] = evictedValue;
                int i2 = this.size;
                int mask2 = i2 + 1;
                this.size = mask2;
                if (i2 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            int mask3 = mask;
            index22 = hash2(evictedKey);
            key22 = keyTable[index22];
            if (key22 == 0) {
                keyTable[index22] = evictedKey;
                valueTable[index22] = evictedValue;
                int i3 = this.size;
                this.size = i3 + 1;
                if (i3 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index32 = hash3(evictedKey);
            long key34 = keyTable[index32];
            if (key34 == 0) {
                keyTable[index32] = evictedKey;
                valueTable[index32] = evictedValue;
                int i4 = this.size;
                this.size = i4 + 1;
                if (i4 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            int i5 = i + 1;
            if (i5 != pushIterations2) {
                insertKey2 = evictedKey;
                insertValue2 = evictedValue;
                i = i5;
                pushIterations = pushIterations2;
                keyTable2 = keyTable;
                mask = mask3;
                key32 = key34;
            } else {
                putStash(evictedKey, evictedValue);
                return;
            }
        }
    }

    private void putStash(long key, V value) {
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

    public V get(long key) {
        if (key == 0) {
            if (this.hasZeroValue) {
                return this.zeroValue;
            }
            return null;
        }
        int index = (int) (this.mask & key);
        if (this.keyTable[index] != key) {
            index = hash2(key);
            if (this.keyTable[index] != key) {
                index = hash3(key);
                if (this.keyTable[index] != key) {
                    return getStash(key, null);
                }
            }
        }
        return this.valueTable[index];
    }

    public V get(long key, V defaultValue) {
        if (key == 0) {
            return !this.hasZeroValue ? defaultValue : this.zeroValue;
        }
        int index = (int) (this.mask & key);
        if (this.keyTable[index] != key) {
            index = hash2(key);
            if (this.keyTable[index] != key) {
                index = hash3(key);
                if (this.keyTable[index] != key) {
                    return getStash(key, defaultValue);
                }
            }
        }
        return this.valueTable[index];
    }

    private V getStash(long key, V defaultValue) {
        long[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (keyTable[i] == key) {
                return this.valueTable[i];
            }
            i++;
        }
        return defaultValue;
    }

    public V remove(long key) {
        if (key == 0) {
            if (this.hasZeroValue) {
                V oldValue = this.zeroValue;
                this.zeroValue = null;
                this.hasZeroValue = false;
                this.size--;
                return oldValue;
            }
            return null;
        }
        int index = (int) (this.mask & key);
        long[] jArr = this.keyTable;
        if (jArr[index] == key) {
            jArr[index] = 0;
            V[] vArr = this.valueTable;
            V oldValue2 = vArr[index];
            vArr[index] = null;
            this.size--;
            return oldValue2;
        }
        int index2 = hash2(key);
        long[] jArr2 = this.keyTable;
        if (jArr2[index2] == key) {
            jArr2[index2] = 0;
            V[] vArr2 = this.valueTable;
            V oldValue3 = vArr2[index2];
            vArr2[index2] = null;
            this.size--;
            return oldValue3;
        }
        int index3 = hash3(key);
        long[] jArr3 = this.keyTable;
        if (jArr3[index3] == key) {
            jArr3[index3] = 0;
            V[] vArr3 = this.valueTable;
            V oldValue4 = vArr3[index3];
            vArr3[index3] = null;
            this.size--;
            return oldValue4;
        }
        return removeStash(key);
    }

    V removeStash(long key) {
        long[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (keyTable[i] != key) {
                i++;
            } else {
                V oldValue = this.valueTable[i];
                removeStashIndex(i);
                this.size--;
                return oldValue;
            }
        }
        return null;
    }

    void removeStashIndex(int index) {
        this.stashSize--;
        int lastIndex = this.capacity + this.stashSize;
        if (index >= lastIndex) {
            this.valueTable[index] = null;
            return;
        }
        long[] jArr = this.keyTable;
        jArr[index] = jArr[lastIndex];
        V[] vArr = this.valueTable;
        vArr[index] = vArr[lastIndex];
        vArr[lastIndex] = null;
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
        this.zeroValue = null;
        this.hasZeroValue = false;
        this.size = 0;
        resize(maximumCapacity);
    }

    public void clear() {
        if (this.size == 0) {
            return;
        }
        long[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                keyTable[i2] = 0;
                valueTable[i2] = null;
                i = i2;
            } else {
                this.size = 0;
                this.stashSize = 0;
                this.zeroValue = null;
                this.hasZeroValue = false;
                return;
            }
        }
    }

    public boolean containsValue(Object value, boolean identity) {
        V[] valueTable = this.valueTable;
        if (value == null) {
            if (this.hasZeroValue && this.zeroValue == null) {
                return true;
            }
            long[] keyTable = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i > 0) {
                    if (keyTable[i2] != 0 && valueTable[i2] == null) {
                        return true;
                    }
                    i = i2;
                } else {
                    return false;
                }
            }
        } else if (identity) {
            if (value == this.zeroValue) {
                return true;
            }
            int i3 = this.capacity + this.stashSize;
            while (true) {
                int i4 = i3 - 1;
                if (i3 > 0) {
                    if (valueTable[i4] == value) {
                        return true;
                    }
                    i3 = i4;
                } else {
                    return false;
                }
            }
        } else if (this.hasZeroValue && value.equals(this.zeroValue)) {
            return true;
        } else {
            int i5 = this.capacity + this.stashSize;
            while (true) {
                int i6 = i5 - 1;
                if (i5 > 0) {
                    if (value.equals(valueTable[i6])) {
                        return true;
                    }
                    i5 = i6;
                } else {
                    return false;
                }
            }
        }
    }

    public boolean containsKey(long key) {
        if (key == 0) {
            return this.hasZeroValue;
        }
        int index = (int) (this.mask & key);
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

    private boolean containsKeyStash(long key) {
        long[] keyTable = this.keyTable;
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

    public long findKey(Object value, boolean identity, long notFound) {
        V[] valueTable = this.valueTable;
        if (value == null) {
            if (this.hasZeroValue && this.zeroValue == null) {
                return 0L;
            }
            long[] keyTable = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i <= 0) {
                    break;
                } else if (keyTable[i2] != 0 && valueTable[i2] == null) {
                    return keyTable[i2];
                } else {
                    i = i2;
                }
            }
        } else if (identity) {
            if (value == this.zeroValue) {
                return 0L;
            }
            int i3 = this.capacity + this.stashSize;
            while (true) {
                int i4 = i3 - 1;
                if (i3 <= 0) {
                    break;
                } else if (valueTable[i4] == value) {
                    return this.keyTable[i4];
                } else {
                    i3 = i4;
                }
            }
        } else if (this.hasZeroValue && value.equals(this.zeroValue)) {
            return 0L;
        } else {
            int i5 = this.capacity + this.stashSize;
            while (true) {
                int i6 = i5 - 1;
                if (i5 <= 0) {
                    break;
                } else if (value.equals(valueTable[i6])) {
                    return this.keyTable[i6];
                } else {
                    i5 = i6;
                }
            }
        }
        return notFound;
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
        this.hashShift = 63 - Long.numberOfTrailingZeros(newSize);
        this.stashCapacity = Math.max(3, ((int) Math.ceil(Math.log(newSize))) * 2);
        this.pushIterations = Math.max(Math.min(newSize, 8), ((int) Math.sqrt(newSize)) / 8);
        long[] oldKeyTable = this.keyTable;
        V[] oldValueTable = this.valueTable;
        int i = this.stashCapacity;
        this.keyTable = new long[newSize + i];
        this.valueTable = (V[]) new Object[i + newSize];
        int oldSize = this.size;
        this.size = this.hasZeroValue ? 1 : 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i2 = 0; i2 < oldEndIndex; i2++) {
                long key = oldKeyTable[i2];
                if (key != 0) {
                    putResize(key, oldValueTable[i2]);
                }
            }
        }
    }

    private int hash2(long h) {
        long h2 = h * (-1262997959);
        return (int) (((h2 >>> this.hashShift) ^ h2) & this.mask);
    }

    private int hash3(long h) {
        long h2 = h * (-825114047);
        return (int) (((h2 >>> this.hashShift) ^ h2) & this.mask);
    }

    public int hashCode() {
        V v;
        int h = 0;
        if (this.hasZeroValue && (v = this.zeroValue) != null) {
            h = 0 + v.hashCode();
        }
        long[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            long key = keyTable[i];
            if (key != 0) {
                h += ((int) ((key >>> 32) ^ key)) * 31;
                V value = valueTable[i];
                if (value != null) {
                    h += value.hashCode();
                }
            }
        }
        return h;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof LongMap) {
            LongMap other = (LongMap) obj;
            if (other.size != this.size) {
                return false;
            }
            boolean z = other.hasZeroValue;
            boolean z2 = this.hasZeroValue;
            if (z != z2) {
                return false;
            }
            if (z2) {
                V v = other.zeroValue;
                if (v == null) {
                    if (this.zeroValue != null) {
                        return false;
                    }
                } else if (!v.equals(this.zeroValue)) {
                    return false;
                }
            }
            long[] keyTable = this.keyTable;
            V[] valueTable = this.valueTable;
            int n = this.capacity + this.stashSize;
            for (int i = 0; i < n; i++) {
                long key = keyTable[i];
                if (key != 0) {
                    V value = valueTable[i];
                    if (value == null) {
                        if (other.get(key, ObjectMap.dummy) != null) {
                            return false;
                        }
                    } else if (!value.equals(other.get(key))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean equalsIdentity(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof LongMap) {
            LongMap other = (LongMap) obj;
            if (other.size != this.size) {
                return false;
            }
            boolean z = other.hasZeroValue;
            boolean z2 = this.hasZeroValue;
            if (z != z2) {
                return false;
            }
            if (!z2 || this.zeroValue == other.zeroValue) {
                long[] keyTable = this.keyTable;
                V[] valueTable = this.valueTable;
                int n = this.capacity + this.stashSize;
                for (int i = 0; i < n; i++) {
                    long key = keyTable[i];
                    if (key != 0 && valueTable[i] != other.get(key, ObjectMap.dummy)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public String toString() {
        int i;
        if (this.size == 0) {
            return "[]";
        }
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        long[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        int i2 = keyTable.length;
        while (true) {
            i = i2 - 1;
            if (i2 <= 0) {
                break;
            }
            long key = keyTable[i];
            if (key != 0) {
                buffer.append(key);
                buffer.append('=');
                buffer.append(valueTable[i]);
                break;
            }
            i2 = i;
        }
        while (true) {
            int i3 = i - 1;
            if (i > 0) {
                long key2 = keyTable[i3];
                if (key2 != 0) {
                    buffer.append(", ");
                    buffer.append(key2);
                    buffer.append('=');
                    buffer.append(valueTable[i3]);
                }
                i = i3;
            } else {
                buffer.append(']');
                return buffer.toString();
            }
        }
    }

    @Override // java.lang.Iterable
    public Iterator<Entry<V>> iterator() {
        return entries();
    }

    public Entries<V> entries() {
        if (Collections.allocateIterators) {
            return new Entries<>(this);
        }
        if (this.entries1 == null) {
            this.entries1 = new Entries(this);
            this.entries2 = new Entries(this);
        }
        if (!this.entries1.valid) {
            this.entries1.reset();
            Entries<V> entries = this.entries1;
            entries.valid = true;
            this.entries2.valid = false;
            return entries;
        }
        this.entries2.reset();
        Entries<V> entries2 = this.entries2;
        entries2.valid = true;
        this.entries1.valid = false;
        return entries2;
    }

    public Values<V> values() {
        if (Collections.allocateIterators) {
            return new Values<>(this);
        }
        if (this.values1 == null) {
            this.values1 = new Values(this);
            this.values2 = new Values(this);
        }
        if (!this.values1.valid) {
            this.values1.reset();
            Values<V> values = this.values1;
            values.valid = true;
            this.values2.valid = false;
            return values;
        }
        this.values2.reset();
        Values<V> values2 = this.values2;
        values2.valid = true;
        this.values1.valid = false;
        return values2;
    }

    public Keys keys() {
        if (Collections.allocateIterators) {
            return new Keys(this);
        }
        if (this.keys1 == null) {
            this.keys1 = new Keys(this);
            this.keys2 = new Keys(this);
        }
        if (!this.keys1.valid) {
            this.keys1.reset();
            Keys keys = this.keys1;
            keys.valid = true;
            this.keys2.valid = false;
            return keys;
        }
        this.keys2.reset();
        Keys keys2 = this.keys2;
        keys2.valid = true;
        this.keys1.valid = false;
        return keys2;
    }

    /* loaded from: classes21.dex */
    public static class Entry<V> {
        public long key;
        public V value;

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class MapIterator<V> {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        int currentIndex;
        public boolean hasNext;
        final LongMap<V> map;
        int nextIndex;
        boolean valid = true;

        public MapIterator(LongMap<V> map) {
            this.map = map;
            reset();
        }

        public void reset() {
            this.currentIndex = -2;
            this.nextIndex = -1;
            if (this.map.hasZeroValue) {
                this.hasNext = true;
            } else {
                findNextIndex();
            }
        }

        void findNextIndex() {
            this.hasNext = false;
            long[] keyTable = this.map.keyTable;
            int n = this.map.capacity + this.map.stashSize;
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
            if (this.currentIndex == -1 && this.map.hasZeroValue) {
                LongMap<V> longMap = this.map;
                longMap.zeroValue = null;
                longMap.hasZeroValue = false;
            } else {
                int i = this.currentIndex;
                if (i < 0) {
                    throw new IllegalStateException("next must be called before remove.");
                }
                if (i >= this.map.capacity) {
                    this.map.removeStashIndex(this.currentIndex);
                    this.nextIndex = this.currentIndex - 1;
                    findNextIndex();
                } else {
                    this.map.keyTable[this.currentIndex] = 0;
                    this.map.valueTable[this.currentIndex] = null;
                }
            }
            this.currentIndex = -2;
            LongMap<V> longMap2 = this.map;
            longMap2.size--;
        }
    }

    /* loaded from: classes21.dex */
    public static class Entries<V> extends MapIterator<V> implements Iterable<Entry<V>>, Iterator<Entry<V>> {
        private Entry<V> entry;

        @Override // com.badlogic.gdx.utils.LongMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Entries(LongMap map) {
            super(map);
            this.entry = new Entry<>();
        }

        @Override // java.util.Iterator
        public Entry<V> next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            long[] keyTable = this.map.keyTable;
            if (this.nextIndex == -1) {
                Entry<V> entry = this.entry;
                entry.key = 0L;
                entry.value = this.map.zeroValue;
            } else {
                this.entry.key = keyTable[this.nextIndex];
                this.entry.value = this.map.valueTable[this.nextIndex];
            }
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
        public Iterator<Entry<V>> iterator() {
            return this;
        }

        @Override // com.badlogic.gdx.utils.LongMap.MapIterator, java.util.Iterator
        public void remove() {
            super.remove();
        }
    }

    /* loaded from: classes21.dex */
    public static class Values<V> extends MapIterator<V> implements Iterable<V>, Iterator<V> {
        @Override // com.badlogic.gdx.utils.LongMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Values(LongMap<V> map) {
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
        public V next() {
            V value;
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            if (this.nextIndex == -1) {
                value = this.map.zeroValue;
            } else {
                value = this.map.valueTable[this.nextIndex];
            }
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return value;
        }

        @Override // java.lang.Iterable
        public Iterator<V> iterator() {
            return this;
        }

        public Array<V> toArray() {
            Array array = new Array(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }

        @Override // com.badlogic.gdx.utils.LongMap.MapIterator, java.util.Iterator
        public void remove() {
            super.remove();
        }
    }

    /* loaded from: classes21.dex */
    public static class Keys extends MapIterator {
        @Override // com.badlogic.gdx.utils.LongMap.MapIterator, java.util.Iterator
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        @Override // com.badlogic.gdx.utils.LongMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Keys(LongMap map) {
            super(map);
        }

        public long next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            long key = this.nextIndex == -1 ? 0L : this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return key;
        }

        public LongArray toArray() {
            LongArray array = new LongArray(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }
}
