package dagger.internal;

import dagger.releasablereferences.ReleasableReferenceManager;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
@GwtIncompatible
/* loaded from: classes25.dex */
public final class ReferenceReleasingProviderManager implements ReleasableReferenceManager {
    private final Queue<WeakReference<ReferenceReleasingProvider<?>>> providers = new ConcurrentLinkedQueue();
    private final Class<? extends Annotation> scope;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public enum Operation {
        RELEASE { // from class: dagger.internal.ReferenceReleasingProviderManager.Operation.1
            @Override // dagger.internal.ReferenceReleasingProviderManager.Operation
            void execute(ReferenceReleasingProvider<?> provider) {
                provider.releaseStrongReference();
            }
        },
        RESTORE { // from class: dagger.internal.ReferenceReleasingProviderManager.Operation.2
            @Override // dagger.internal.ReferenceReleasingProviderManager.Operation
            void execute(ReferenceReleasingProvider<?> provider) {
                provider.restoreStrongReference();
            }
        };

        abstract void execute(ReferenceReleasingProvider<?> provider);
    }

    public ReferenceReleasingProviderManager(Class<? extends Annotation> scope) {
        this.scope = (Class) Preconditions.checkNotNull(scope);
    }

    public void addProvider(ReferenceReleasingProvider<?> provider) {
        this.providers.add(new WeakReference<>(provider));
    }

    @Override // dagger.releasablereferences.ReleasableReferenceManager
    public Class<? extends Annotation> scope() {
        return this.scope;
    }

    @Override // dagger.releasablereferences.ReleasableReferenceManager
    public void releaseStrongReferences() {
        execute(Operation.RELEASE);
    }

    @Override // dagger.releasablereferences.ReleasableReferenceManager
    public void restoreStrongReferences() {
        execute(Operation.RESTORE);
    }

    private void execute(Operation operation) {
        Iterator<WeakReference<ReferenceReleasingProvider<?>>> iterator = this.providers.iterator();
        while (iterator.hasNext()) {
            ReferenceReleasingProvider<?> provider = iterator.next().get();
            if (provider == null) {
                iterator.remove();
            } else {
                operation.execute(provider);
            }
        }
    }
}
