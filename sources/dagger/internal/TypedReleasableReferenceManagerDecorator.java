package dagger.internal;

import dagger.releasablereferences.ReleasableReferenceManager;
import dagger.releasablereferences.TypedReleasableReferenceManager;
import java.lang.annotation.Annotation;
@GwtIncompatible
/* loaded from: classes25.dex */
public final class TypedReleasableReferenceManagerDecorator<M extends Annotation> implements TypedReleasableReferenceManager<M> {
    private final ReleasableReferenceManager delegate;
    private final M metadata;

    public TypedReleasableReferenceManagerDecorator(ReleasableReferenceManager delegate, M metadata) {
        this.delegate = (ReleasableReferenceManager) Preconditions.checkNotNull(delegate);
        this.metadata = (M) Preconditions.checkNotNull(metadata);
    }

    @Override // dagger.releasablereferences.ReleasableReferenceManager
    public Class<? extends Annotation> scope() {
        return this.delegate.scope();
    }

    @Override // dagger.releasablereferences.TypedReleasableReferenceManager
    public M metadata() {
        return this.metadata;
    }

    @Override // dagger.releasablereferences.ReleasableReferenceManager
    public void releaseStrongReferences() {
        this.delegate.releaseStrongReferences();
    }

    @Override // dagger.releasablereferences.ReleasableReferenceManager
    public void restoreStrongReferences() {
        this.delegate.restoreStrongReferences();
    }
}
