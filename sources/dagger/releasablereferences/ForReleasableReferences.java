package dagger.releasablereferences;

import dagger.internal.GwtIncompatible;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;
@GwtIncompatible
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Qualifier
@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: classes25.dex */
public @interface ForReleasableReferences {
    Class<? extends Annotation> value();
}
