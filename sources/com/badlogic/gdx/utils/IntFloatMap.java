package com.badlogic.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import java.util.Iterator;
import java.util.NoSuchElementException;
/* loaded from: classes21.dex */
public class IntFloatMap implements Iterable<Entry> {
    private static final int EMPTY = 0;
    private static final int PRIME1 = -1105259343;
    private static final int PRIME2 = -1262997959;
    private static final int PRIME3 = -825114047;
    int capacity;
    private Entries entries1;
    private Entries entries2;
    boolean hasZeroValue;
    private int hashShift;
    int[] keyTable;
    private Keys keys1;
    private Keys keys2;
    private float loadFactor;
    private int mask;
    private int pushIterations;
    public int size;
    private int stashCapacity;
    int stashSize;
    private int threshold;
    float[] valueTable;
    private Values values1;
    private Values values2;
    float zeroValue;

    public IntFloatMap() {
        this(51, 0.8f);
    }

    public IntFloatMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    public IntFloatMap(int initialCapacity, float loadFactor) {
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
        this.valueTable = new float[this.keyTable.length];
    }

    public IntFloatMap(IntFloatMap map) {
        this((int) Math.floor(map.capacity * map.loadFactor), map.loadFactor);
        this.stashSize = map.stashSize;
        int[] iArr = map.keyTable;
        System.arraycopy(iArr, 0, this.keyTable, 0, iArr.length);
        float[] fArr = map.valueTable;
        System.arraycopy(fArr, 0, this.valueTable, 0, fArr.length);
        this.size = map.size;
        this.zeroValue = map.zeroValue;
        this.hasZeroValue = map.hasZeroValue;
    }

    public void put(int key, float value) {
        if (key == 0) {
            this.zeroValue = value;
            if (!this.hasZeroValue) {
                this.hasZeroValue = true;
                this.size++;
                return;
            }
            return;
        }
        int[] keyTable = this.keyTable;
        int index1 = key & this.mask;
        int key1 = keyTable[index1];
        if (key == key1) {
            this.valueTable[index1] = value;
            return;
        }
        int index2 = hash2(key);
        int key2 = keyTable[index2];
        if (key == key2) {
            this.valueTable[index2] = value;
            return;
        }
        int index3 = hash3(key);
        int key3 = keyTable[index3];
        if (key == key3) {
            this.valueTable[index3] = value;
            return;
        }
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key != keyTable[i]) {
                i++;
            } else {
                this.valueTable[i] = value;
                return;
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
        } else if (key2 == 0) {
            keyTable[index2] = key;
            this.valueTable[index2] = value;
            int i3 = this.size;
            this.size = i3 + 1;
            if (i3 >= this.threshold) {
                resize(this.capacity << 1);
            }
        } else if (key3 == 0) {
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

    public void putAll(IntFloatMap map) {
        Iterator<Entry> it = map.entries().iterator();
        while (it.hasNext()) {
            Entry entry = it.next();
            put(entry.key, entry.value);
        }
    }

    private void putResize(int key, float value) {
        if (key == 0) {
            this.zeroValue = value;
            this.hasZeroValue = true;
            return;
        }
        int index1 = key & this.mask;
        int[] iArr = this.keyTable;
        int key1 = iArr[index1];
        if (key1 == 0) {
            iArr[index1] = key;
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
        int[] iArr2 = this.keyTable;
        int key2 = iArr2[index2];
        if (key2 == 0) {
            iArr2[index2] = key;
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
        int[] iArr3 = this.keyTable;
        int key3 = iArr3[index3];
        if (key3 == 0) {
            iArr3[index3] = key;
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

    private void push(int insertKey, float insertValue, int index1, int key1, int index2, int key2, int index3, int key3) {
        int evictedKey;
        float evictedValue;
        int[] keyTable = this.keyTable;
        float[] valueTable = this.valueTable;
        int mask = this.mask;
        int pushIterations = this.pushIterations;
        int insertKey2 = insertKey;
        float insertValue2 = insertValue;
        int index12 = index1;
        int key12 = key1;
        int index22 = index2;
        int key22 = key2;
        int index32 = index3;
        int i = 0;
        int key32 = key3;
        while (true) {
            int random = MathUtils.random(2);
            if (random == 0) {
                evictedKey = key12;
                float evictedValue2 = valueTable[index12];
                keyTable[index12] = insertKey2;
                valueTable[index12] = insertValue2;
                evictedValue = evictedValue2;
            } else if (random == 1) {
                evictedKey = key22;
                float evictedValue3 = valueTable[index22];
                keyTable[index22] = insertKey2;
                valueTable[index22] = insertValue2;
                evictedValue = evictedValue3;
            } else {
                evictedKey = key32;
                float evictedValue4 = valueTable[index32];
                keyTable[index32] = insertKey2;
                valueTable[index32] = insertValue2;
                evictedValue = evictedValue4;
            }
            index12 = evictedKey & mask;
            key12 = keyTable[index12];
            if (key12 == 0) {
                keyTable[index12] = evictedKey;
                valueTable[index12] = evictedValue;
                int mask2 = this.size;
                int key33 = mask2 + 1;
                this.size = key33;
                if (mask2 >= this.threshold) {
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
                int i2 = this.size;
                this.size = i2 + 1;
                if (i2 >= this.threshold) {
                    resize(this.capacity << 1);
                    return;
                }
                return;
            }
            index32 = hash3(evictedKey);
            key32 = keyTable[index32];
            if (key32 == 0) {
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
            int[] keyTable2 = keyTable;
            i++;
            if (i != pushIterations) {
                insertKey2 = evictedKey;
                insertValue2 = evictedValue;
                mask = mask3;
                keyTable = keyTable2;
            } else {
                putStash(evictedKey, evictedValue);
                return;
            }
        }
    }

    private void putStash(int key, float value) {
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

    public float get(int key, float defaultValue) {
        if (key == 0) {
            return !this.hasZeroValue ? defaultValue : this.zeroValue;
        }
        int index = this.mask & key;
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

    private float getStash(int key, float defaultValue) {
        int[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key == keyTable[i]) {
                return this.valueTable[i];
            }
            i++;
        }
        return defaultValue;
    }

    public float getAndIncrement(int key, float defaultValue, float increment) {
        if (key == 0) {
            if (this.hasZeroValue) {
                float value = this.zeroValue;
                this.zeroValue += increment;
                return value;
            }
            this.hasZeroValue = true;
            this.zeroValue = defaultValue + increment;
            this.size++;
            return defaultValue;
        }
        int index = this.mask & key;
        if (key != this.keyTable[index]) {
            index = hash2(key);
            if (key != this.keyTable[index]) {
                index = hash3(key);
                if (key != this.keyTable[index]) {
                    return getAndIncrementStash(key, defaultValue, increment);
                }
            }
        }
        float[] fArr = this.valueTable;
        float value2 = fArr[index];
        fArr[index] = value2 + increment;
        return value2;
    }

    private float getAndIncrementStash(int key, float defaultValue, float increment) {
        int[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key != keyTable[i]) {
                i++;
            } else {
                float[] fArr = this.valueTable;
                float value = fArr[i];
                fArr[i] = value + increment;
                return value;
            }
        }
        put(key, defaultValue + increment);
        return defaultValue;
    }

    public float remove(int key, float defaultValue) {
        if (key == 0) {
            if (this.hasZeroValue) {
                this.hasZeroValue = false;
                this.size--;
                return this.zeroValue;
            }
            return defaultValue;
        }
        int index = this.mask & key;
        int[] iArr = this.keyTable;
        if (key == iArr[index]) {
            iArr[index] = 0;
            float oldValue = this.valueTable[index];
            this.size--;
            return oldValue;
        }
        int index2 = hash2(key);
        int[] iArr2 = this.keyTable;
        if (key == iArr2[index2]) {
            iArr2[index2] = 0;
            float oldValue2 = this.valueTable[index2];
            this.size--;
            return oldValue2;
        }
        int index3 = hash3(key);
        int[] iArr3 = this.keyTable;
        if (key == iArr3[index3]) {
            iArr3[index3] = 0;
            float oldValue3 = this.valueTable[index3];
            this.size--;
            return oldValue3;
        }
        float oldValue4 = removeStash(key, defaultValue);
        return oldValue4;
    }

    float removeStash(int key, float defaultValue) {
        int[] keyTable = this.keyTable;
        int i = this.capacity;
        int n = this.stashSize + i;
        while (i < n) {
            if (key != keyTable[i]) {
                i++;
            } else {
                float oldValue = this.valueTable[i];
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
            int[] iArr = this.keyTable;
            iArr[index] = iArr[lastIndex];
            float[] fArr = this.valueTable;
            fArr[index] = fArr[lastIndex];
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
                this.hasZeroValue = false;
                this.size = 0;
                this.stashSize = 0;
                return;
            }
        }
    }

    public boolean containsValue(float value) {
        if (this.hasZeroValue && this.zeroValue == value) {
            return true;
        }
        int[] keyTable = this.keyTable;
        float[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                if (keyTable[i2] != 0 && valueTable[i2] == value) {
                    return true;
                }
                i = i2;
            } else {
                return false;
            }
        }
    }

    public boolean containsValue(float value, float epsilon) {
        if (this.hasZeroValue && Math.abs(this.zeroValue - value) <= epsilon) {
            return true;
        }
        float[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                if (Math.abs(valueTable[i2] - value) <= epsilon) {
                    return true;
                }
                i = i2;
            } else {
                return false;
            }
        }
    }

    public boolean containsKey(int key) {
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
            if (key == keyTable[i]) {
                return true;
            }
            i++;
        }
        return false;
    }

    public int findKey(float value, int notFound) {
        if (this.hasZeroValue && this.zeroValue == value) {
            return 0;
        }
        int[] keyTable = this.keyTable;
        float[] valueTable = this.valueTable;
        int i = this.capacity + this.stashSize;
        while (true) {
            int i2 = i - 1;
            if (i > 0) {
                if (keyTable[i2] != 0 && valueTable[i2] == value) {
                    return keyTable[i2];
                }
                i = i2;
            } else {
                return notFound;
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
        int[] oldKeyTable = this.keyTable;
        float[] oldValueTable = this.valueTable;
        int i = this.stashCapacity;
        this.keyTable = new int[newSize + i];
        this.valueTable = new float[i + newSize];
        int oldSize = this.size;
        this.size = this.hasZeroValue ? 1 : 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i2 = 0; i2 < oldEndIndex; i2++) {
                int key = oldKeyTable[i2];
                if (key != 0) {
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
        int h = this.hasZeroValue ? 0 + Float.floatToIntBits(this.zeroValue) : 0;
        int[] keyTable = this.keyTable;
        float[] valueTable = this.valueTable;
        int n = this.capacity + this.stashSize;
        for (int i = 0; i < n; i++) {
            int key = keyTable[i];
            if (key != 0) {
                float value = valueTable[i];
                h = h + (key * 31) + Float.floatToIntBits(value);
            }
        }
        return h;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof IntFloatMap) {
            IntFloatMap other = (IntFloatMap) obj;
            if (other.size != this.size) {
                return false;
            }
            boolean z = other.hasZeroValue;
            boolean z2 = this.hasZeroValue;
            if (z != z2) {
                return false;
            }
            if (!z2 || other.zeroValue == this.zeroValue) {
                int[] keyTable = this.keyTable;
                float[] valueTable = this.valueTable;
                int n = this.capacity + this.stashSize;
                for (int i = 0; i < n; i++) {
                    int key = keyTable[i];
                    if (key != 0) {
                        float otherValue = other.get(key, 0.0f);
                        if (otherValue == 0.0f && !other.containsKey(key)) {
                            return false;
                        }
                        float value = valueTable[i];
                        if (otherValue != value) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0044  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x005a  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:15:0x003f -> B:16:0x0040). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String toString() {
        /*
            r7 = this;
            int r0 = r7.size
            if (r0 != 0) goto L8
            java.lang.String r0 = "{}"
            return r0
        L8:
            com.badlogic.gdx.utils.StringBuilder r0 = new com.badlogic.gdx.utils.StringBuilder
            r1 = 32
            r0.<init>(r1)
            r1 = 123(0x7b, float:1.72E-43)
            r0.append(r1)
            int[] r1 = r7.keyTable
            float[] r2 = r7.valueTable
            int r3 = r1.length
            boolean r4 = r7.hasZeroValue
            r5 = 61
            if (r4 == 0) goto L2a
            java.lang.String r4 = "0="
            r0.append(r4)
            float r4 = r7.zeroValue
            r0.append(r4)
            goto L40
        L2a:
            int r4 = r3 + (-1)
            if (r3 <= 0) goto L3f
            r3 = r1[r4]
            if (r3 != 0) goto L34
            r3 = r4
            goto L2a
        L34:
            r0.append(r3)
            r0.append(r5)
            r6 = r2[r4]
            r0.append(r6)
        L3f:
            r3 = r4
        L40:
            int r4 = r3 + (-1)
            if (r3 <= 0) goto L5a
            r3 = r1[r4]
            if (r3 != 0) goto L49
            goto L3f
        L49:
            java.lang.String r6 = ", "
            r0.append(r6)
            r0.append(r3)
            r0.append(r5)
            r6 = r2[r4]
            r0.append(r6)
            goto L3f
        L5a:
            r3 = 125(0x7d, float:1.75E-43)
            r0.append(r3)
            java.lang.String r3 = r0.toString()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.utils.IntFloatMap.toString():java.lang.String");
    }

    @Override // java.lang.Iterable
    public Iterator<Entry> iterator() {
        return entries();
    }

    public Entries entries() {
        if (Collections.allocateIterators) {
            return new Entries(this);
        }
        if (this.entries1 == null) {
            this.entries1 = new Entries(this);
            this.entries2 = new Entries(this);
        }
        if (!this.entries1.valid) {
            this.entries1.reset();
            Entries entries = this.entries1;
            entries.valid = true;
            this.entries2.valid = false;
            return entries;
        }
        this.entries2.reset();
        Entries entries2 = this.entries2;
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
    public static class Entry {
        public int key;
        public float value;

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class MapIterator {
        static final int INDEX_ILLEGAL = -2;
        static final int INDEX_ZERO = -1;
        int currentIndex;
        public boolean hasNext;
        final IntFloatMap map;
        int nextIndex;
        boolean valid = true;

        public MapIterator(IntFloatMap map) {
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
            int[] keyTable = this.map.keyTable;
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
                this.map.hasZeroValue = false;
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
                }
            }
            this.currentIndex = -2;
            IntFloatMap intFloatMap = this.map;
            intFloatMap.size--;
        }
    }

    /* loaded from: classes21.dex */
    public static class Entries extends MapIterator implements Iterable<Entry>, Iterator<Entry> {
        private Entry entry;

        @Override // com.badlogic.gdx.utils.IntFloatMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Entries(IntFloatMap map) {
            super(map);
            this.entry = new Entry();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Entry next() {
            if (!this.hasNext) {
                throw new NoSuchElementException();
            }
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            int[] keyTable = this.map.keyTable;
            if (this.nextIndex == -1) {
                Entry entry = this.entry;
                entry.key = 0;
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
        public Iterator<Entry> iterator() {
            return this;
        }

        @Override // com.badlogic.gdx.utils.IntFloatMap.MapIterator, java.util.Iterator
        public void remove() {
            super.remove();
        }
    }

    /* loaded from: classes21.dex */
    public static class Values extends MapIterator {
        @Override // com.badlogic.gdx.utils.IntFloatMap.MapIterator, java.util.Iterator
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        @Override // com.badlogic.gdx.utils.IntFloatMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Values(IntFloatMap map) {
            super(map);
        }

        public boolean hasNext() {
            if (!this.valid) {
                throw new GdxRuntimeException("#iterator() cannot be used nested.");
            }
            return this.hasNext;
        }

        public float next() {
            float value;
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

        public FloatArray toArray() {
            FloatArray array = new FloatArray(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }

    /* loaded from: classes21.dex */
    public static class Keys extends MapIterator {
        @Override // com.badlogic.gdx.utils.IntFloatMap.MapIterator, java.util.Iterator
        public /* bridge */ /* synthetic */ void remove() {
            super.remove();
        }

        @Override // com.badlogic.gdx.utils.IntFloatMap.MapIterator
        public /* bridge */ /* synthetic */ void reset() {
            super.reset();
        }

        public Keys(IntFloatMap map) {
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
            int key = this.nextIndex == -1 ? 0 : this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return key;
        }

        public IntArray toArray() {
            IntArray array = new IntArray(true, this.map.size);
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }
}
