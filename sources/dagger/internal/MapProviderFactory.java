package dagger.internal;

import dagger.Lazy;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.inject.Provider;
/* loaded from: classes25.dex */
public final class MapProviderFactory<K, V> implements Factory<Map<K, Provider<V>>>, Lazy<Map<K, Provider<V>>> {
    private final Map<K, Provider<V>> contributingMap;

    public static <K, V> Builder<K, V> builder(int size) {
        return new Builder<>(size);
    }

    private MapProviderFactory(Map<K, Provider<V>> contributingMap) {
        this.contributingMap = Collections.unmodifiableMap(contributingMap);
    }

    @Override // javax.inject.Provider
    public Map<K, Provider<V>> get() {
        return this.contributingMap;
    }

    /* loaded from: classes25.dex */
    public static final class Builder<K, V> {
        private final LinkedHashMap<K, Provider<V>> map;

        private Builder(int size) {
            this.map = DaggerCollections.newLinkedHashMapWithExpectedSize(size);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Builder<K, V> put(K key, Provider<V> providerOfValue) {
            this.map.put(Preconditions.checkNotNull(key, "key"), Preconditions.checkNotNull(providerOfValue, "provider"));
            return this;
        }

        public MapProviderFactory<K, V> build() {
            return new MapProviderFactory<>(this.map);
        }
    }
}
