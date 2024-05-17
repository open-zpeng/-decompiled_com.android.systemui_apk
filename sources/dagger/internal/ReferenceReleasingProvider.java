package dagger.internal;

import java.lang.ref.WeakReference;
import javax.inject.Provider;
@GwtIncompatible
/* loaded from: classes25.dex */
public final class ReferenceReleasingProvider<T> implements Provider<T> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Object NULL = new Object();
    private final Provider<T> provider;
    private volatile Object strongReference;
    private volatile WeakReference<T> weakReference;

    private ReferenceReleasingProvider(Provider<T> provider) {
        this.provider = provider;
    }

    public void releaseStrongReference() {
        Object value = this.strongReference;
        if (value != null && value != NULL) {
            synchronized (this) {
                this.weakReference = new WeakReference<>(value);
                this.strongReference = null;
            }
        }
    }

    public void restoreStrongReference() {
        Object value;
        Object value2 = this.strongReference;
        if (this.weakReference != null && value2 == null) {
            synchronized (this) {
                Object value3 = this.strongReference;
                if (this.weakReference != null && value3 == null && (value = this.weakReference.get()) != null) {
                    this.strongReference = value;
                    this.weakReference = null;
                }
            }
        }
    }

    @Override // javax.inject.Provider
    public T get() {
        Object value = currentValue();
        if (value == null) {
            synchronized (this) {
                value = currentValue();
                if (value == null) {
                    value = this.provider.get();
                    if (value == null) {
                        value = NULL;
                    }
                    this.strongReference = value;
                }
            }
        }
        if (value == NULL) {
            return null;
        }
        return (T) value;
    }

    private Object currentValue() {
        Object value = this.strongReference;
        if (value != null) {
            return value;
        }
        if (this.weakReference != null) {
            return this.weakReference.get();
        }
        return null;
    }

    public static <T> ReferenceReleasingProvider<T> create(Provider<T> delegate, ReferenceReleasingProviderManager references) {
        ReferenceReleasingProvider<T> provider = new ReferenceReleasingProvider<>((Provider) Preconditions.checkNotNull(delegate));
        references.addProvider(provider);
        return provider;
    }
}
