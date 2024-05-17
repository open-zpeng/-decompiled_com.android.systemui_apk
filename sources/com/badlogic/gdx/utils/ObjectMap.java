package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;
/* loaded from: classes21.dex */
public class ObjectMap<K, V> implements Iterable<Entry<K, V>> {
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    static final Object dummy = new Object();
    int capacity;
    Entries entries1;
    Entries entries2;
    private int hashShift;
    K[] keyTable;
    Keys keys1;
    Keys keys2;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;
    V[] valueTable;
    Values values1;
    Values values2;

    public ObjectMap() {
        this(51, 0.8f);
    }

    public ObjectMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public ObjectMap(int initialCapacity, float loadFactor) {
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
        this.valueTable = (V[]) new Object[this.keyTable.length];
    }

    public ObjectMap(ObjectMap<? extends K, ? extends V> map) {
        this((int) Math.floor(map.capacity * map.loadFactor), map.loadFactor);
        this.stashSize = map.stashSize;
        Object[] objArr = map.keyTable;
        System.arraycopy(objArr, 0, this.keyTable, 0, objArr.length);
        Object[] objArr2 = map.valueTable;
        System.arraycopy(objArr2, 0, this.valueTable, 0, objArr2.length);
        this.size = map.size;
    }

    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null.");
        }
        K[] keyTable = this.keyTable;
        int hashCode = key.hashCode();
        int index1 = hashCode & this.mask;
        K key1 = keyTable[index1];
        if (key.equals(key1)) {
            V[] vArr = this.valueTable;
            V oldValue = vArr[index1];
            vArr[index1] = value;
            return oldValue;
        }
        int index2 = hash2(hashCode);
        K key2 = keyTable[index2];
        if (key.equals(key2)) {
            V[] vArr2 = this.valueTable;
            V oldValue2 = vArr2[index2];
            vArr2[index2] = value;
            return oldValue2;
        }
        int index3 = hash3(hashCode);
        K key3 = keyTable[index3];
        if (key.equals(key3)) {
            V[] vArr3 = this.valueTable;
            V oldValue3 = vArr3[index3];
            vArr3[index3] = value;
            return oldValue3;
        }
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (!key.equals(keyTable[i])) {
                i++;
            } else {
                V[] vArr4 = this.valueTable;
                V oldValue4 = vArr4[i];
                vArr4[i] = value;
                return oldValue4;
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
            return null;
        } else if (key2 == null) {
            keyTable[index2] = key;
            this.valueTable[index2] = value;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        } else if (key3 == null) {
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

    public void putAll(ObjectMap<? extends K, ? extends V> map) {
        ensureCapacity(map.size);
        Entries<? extends K, ? extends V> it = map.iterator();
        while (it.hasNext()) {
            Entry<K, V> next = it.next();
            put(next.key, next.value);
        }
    }

    private void putResize(K key, V value) {
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

    private void push(K insertKey, V insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
        K evictedKey;
        V evictedValue;
        K[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        int mask = this.mask;
        int pushIterations = this.pushIterations;
        K insertKey2 = insertKey;
        V insertValue2 = insertValue;
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
                V evictedValue2 = valueTable[index12];
                keyTable[index12] = insertKey2;
                valueTable[index12] = insertValue2;
                evictedValue = evictedValue2;
            } else if (random == 1) {
                evictedKey = key22;
                V evictedValue3 = valueTable[index22];
                keyTable[index22] = insertKey2;
                valueTable[index22] = insertValue2;
                evictedValue = evictedValue3;
            } else {
                evictedKey = key32;
                V evictedValue4 = valueTable[index32];
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
            V[] valueTable2 = valueTable;
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

    private void putStash(K key, V value) {
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

    public V get(K key) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (!key.equals(this.keyTable[index])) {
            index = hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = hash3(hashCode);
                if (!key.equals(this.keyTable[index])) {
                    return getStash(key, null);
                }
            }
        }
        return this.valueTable[index];
    }

    public V get(K key, V defaultValue) {
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

    private V getStash(K key, V defaultValue) {
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

    public V remove(K key) {
        int hashCode = key.hashCode();
        int index = this.mask & hashCode;
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            V[] vArr = this.valueTable;
            V oldValue = vArr[index];
            vArr[index] = null;
            this.size--;
            return oldValue;
        }
        int index2 = hash2(hashCode);
        if (key.equals(this.keyTable[index2])) {
            this.keyTable[index2] = null;
            V[] vArr2 = this.valueTable;
            V oldValue2 = vArr2[index2];
            vArr2[index2] = null;
            this.size--;
            return oldValue2;
        }
        int index3 = hash3(hashCode);
        if (key.equals(this.keyTable[index3])) {
            this.keyTable[index3] = null;
            V[] vArr3 = this.valueTable;
            V oldValue3 = vArr3[index3];
            vArr3[index3] = null;
            this.size--;
            return oldValue3;
        }
        return removeStash(key);
    }

    V removeStash(K key) {
        K[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (!key.equals(keyTable[i])) {
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
            this.keyTable[index] = null;
            this.valueTable[index] = null;
            return;
        }
        K[] kArr = this.keyTable;
        kArr[index] = kArr[lastIndex];
        V[] vArr = this.valueTable;
        vArr[index] = vArr[lastIndex];
        kArr[lastIndex] = null;
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
        this.size = 0;
        resize(maximumCapacity);
    }

    public void clear() {
        if (this.size == 0) {
            return;
        }
        K[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                keyTable[i2] = null;
                valueTable[i2] = null;
                i = i2;
            } else {
                this.size = 0;
                this.stashSize = 0;
                return;
            }
        }
    }

    public boolean containsValue(Object value, boolean identity) {
        V[] valueTable = this.valueTable;
        if (value == null) {
            K[] keyTable = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i > 0) {
                    if (keyTable[i2] != null && valueTable[i2] == null) {
                        return true;
                    }
                    i = i2;
                } else {
                    return false;
                }
            }
        } else if (identity) {
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

    public K findKey(Object value, boolean identity) {
        V[] valueTable = this.valueTable;
        if (value == null) {
            K[] keyTable = this.keyTable;
            int i = this.capacity + this.stashSize;
            while (true) {
                int i2 = i - 1;
                if (i > 0) {
                    if (keyTable[i2] != null && valueTable[i2] == null) {
                        return keyTable[i2];
                    }
                    i = i2;
                } else {
                    return null;
                }
            }
        } else if (identity) {
            int i3 = this.capacity + this.stashSize;
            while (true) {
                int i4 = i3 - 1;
                if (i3 > 0) {
                    if (valueTable[i4] == value) {
                        return this.keyTable[i4];
                    }
                    i3 = i4;
                } else {
                    return null;
                }
            }
        } else {
            int i5 = this.capacity + this.stashSize;
            while (true) {
                int i6 = i5 - 1;
                if (i5 > 0) {
                    if (value.equals(valueTable[i6])) {
                        return this.keyTable[i6];
                    }
                    i5 = i6;
                } else {
                    return null;
                }
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
        V[] oldValueTable = this.valueTable;
        int i = this.stashCapacity;
        this.keyTable = (K[]) new Object[newSize + i];
        this.valueTable = (V[]) new Object[i + newSize];
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
        V[] valueTable = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            K key = keyTable[i];
            if (key != null) {
                h += key.hashCode() * 31;
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
        if (obj instanceof ObjectMap) {
            ObjectMap other = (ObjectMap) obj;
            if (other.size != this.size) {
                return false;
            }
            K[] keyTable = this.keyTable;
            V[] valueTable = this.valueTable;
            int n = this.capacity + this.stashSize;
            for (int i = 0; i < n; i++) {
                K key = keyTable[i];
                if (key != null) {
                    V value = valueTable[i];
                    if (value == null) {
                        if (other.get(key, dummy) != null) {
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
        if (obj instanceof IdentityMap) {
            IdentityMap other = (IdentityMap) obj;
            if (other.size != this.size) {
                return false;
            }
            K[] keyTable = this.keyTable;
            V[] valueTable = this.valueTable;
            int n = this.capacity + this.stashSize;
            for (int i = 0; i < n; i++) {
                K key = keyTable[i];
                if (key != null && valueTable[i] != other.get(key, dummy)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public String toString(String separator) {
        return toString(separator, false);
    }

    public String toString() {
        return toString(", ", true);
    }

    private String toString(String separator, boolean braces) {
        int i;
        if (this.size == 0) {
            return braces ? "{}" : "";
        }
        StringBuilder buffer = new StringBuilder(32);
        if (braces) {
            buffer.append('{');
        }
        K[] keyTable = this.keyTable;
        V[] valueTable = this.valueTable;
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
            if (i <= 0) {
                break;
            }
            K key2 = keyTable[i3];
            if (key2 != null) {
                buffer.append(separator);
                buffer.append(key2);
                buffer.append('=');
                buffer.append(valueTable[i3]);
            }
            i = i3;
        }
        if (braces) {
            buffer.append('}');
        }
        return buffer.toString();
    }

    @Override // java.lang.Iterable
    public Entries<K, V> iterator() {
        return entries();
    }

    public Entries<K, V> entries() {
        if (Collections.allocateIterators) {
            return new Entries<>(this);
        }
        if (this.entries1 == null) {
            this.entries1 = new Entries(this);
            this.entries2 = new Entries(this);
        }
        if (!this.entries1.valid) {
            this.entries1.reset();
            Entries<K, V> entries = this.entries1;
            entries.valid = true;
            this.entries2.valid = false;
            return entries;
        }
        this.entries2.reset();
        Entries<K, V> entries2 = this.entries2;
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
    public static class Entry<K, V> {
        public K key;
        public V value;

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static abstract class MapIterator<K, V, I> implements Iterable<I>, Iterator<I> {
        int currentIndex;
        public boolean hasNext;
        final ObjectMap<K, V> map;
        int nextIndex;
        boolean valid = true;

        public MapIterator(ObjectMap<K, V> map) {
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
                this.map.valueTable[this.currentIndex] = null;
            }
            this.currentIndex = -1;
            ObjectMap<K, V> objectMap = this.map;
            objectMap.size--;
        }
    }

    /* loaded from: classes21.dex */
    public static class Entries<K, V> extends MapIterator<K, V, Entry<K, V>> {
        Entry<K, V> entry;

        @Override // com.badlogic.gdx.utils.ObjectMap.MapIterator, java.util.Iterator
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        @Override // com.badlogic.gdx.utils.ObjectMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Entries(ObjectMap<K, V> map) {
            super(map);
            this.entry = new Entry<>();
        }

        @Override // java.util.Iterator
        public Entry<K, V> next() {
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
        public Entries<K, V> iterator() {
            return this;
        }
    }

    /* loaded from: classes21.dex */
    public static class Values<V> extends MapIterator<Object, V, V> {
        @Override // com.badlogic.gdx.utils.ObjectMap.MapIterator, java.util.Iterator
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        @Override // com.badlogic.gdx.utils.ObjectMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Values(ObjectMap<?, V> map) {
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
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            V value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return value;
        }

        @Override // java.lang.Iterable
        public Values<V> iterator() {
            return this;
        }

        public Array<V> toArray() {
            return toArray(new Array<>(true, this.map.size));
        }

        public Array<V> toArray(Array<V> array) {
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }

    /* loaded from: classes21.dex */
    public static class Keys<K> extends MapIterator<K, Object, K> {
        @Override // com.badlogic.gdx.utils.ObjectMap.MapIterator, java.util.Iterator
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        @Override // com.badlogic.gdx.utils.ObjectMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Keys(ObjectMap<K, ?> map) {
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
            return toArray(new Array<>(true, this.map.size));
        }

        public Array<K> toArray(Array<K> array) {
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }
}
