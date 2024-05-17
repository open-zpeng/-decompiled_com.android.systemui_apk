package com.android.systemui.statusbar.phone;

import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: ConfigurationControllerImpl.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001a\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u001e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\u001aA\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020\u00060\u00052\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020\u00010\u0005H\u0086\b¨\u0006\b"}, d2 = {"filterForEach", "", "T", "", "f", "Lkotlin/Function1;", "", "execute", "name"}, k = 2, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class ConfigurationControllerImplKt {
    public static final <T> void filterForEach(@NotNull Collection<? extends T> receiver$0, @NotNull Function1<? super T, Boolean> f, @NotNull Function1<? super T, Unit> execute) {
        Intrinsics.checkParameterIsNotNull(receiver$0, "receiver$0");
        Intrinsics.checkParameterIsNotNull(f, "f");
        Intrinsics.checkParameterIsNotNull(execute, "execute");
        Collection<? extends T> $receiver$iv = receiver$0;
        Iterator<T> it = $receiver$iv.iterator();
        while (it.hasNext()) {
            Object element$iv = (T) it.next();
            if (f.invoke(element$iv).booleanValue()) {
                execute.invoke(element$iv);
            }
        }
    }
}
