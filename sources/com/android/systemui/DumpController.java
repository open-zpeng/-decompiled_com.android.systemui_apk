package com.android.systemui;

import androidx.annotation.GuardedBy;
import com.android.internal.util.Preconditions;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.inject.Singleton;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: DumpController.kt */
@Singleton
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0007\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\u0007\b\u0007¢\u0006\u0002\u0010\u0002J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0001J/\u0010\r\u001a\u00020\u000b2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u000e\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u0013H\u0016¢\u0006\u0002\u0010\u0015J\u000e\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0001R\u001c\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\u00050\u00048\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u00078F¢\u0006\u0006\u001a\u0004\b\b\u0010\t¨\u0006\u0018"}, d2 = {"Lcom/android/systemui/DumpController;", "Lcom/android/systemui/Dumpable;", "()V", "listeners", "", "Ljava/lang/ref/WeakReference;", "numListeners", "", "getNumListeners", "()I", "addListener", "", "listener", "dump", "fd", "Ljava/io/FileDescriptor;", "pw", "Ljava/io/PrintWriter;", "args", "", "", "(Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V", "removeListener", "Companion", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class DumpController implements Dumpable {
    public static final Companion Companion = new Companion(null);
    private static final boolean DEBUG = false;
    private static final String TAG = "DumpController";
    @GuardedBy("listeners")
    private final List<WeakReference<Dumpable>> listeners = new ArrayList();

    /* compiled from: DumpController.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0007"}, d2 = {"Lcom/android/systemui/DumpController$Companion;", "", "()V", "DEBUG", "", "TAG", "", "name"}, k = 1, mv = {1, 1, 13})
    /* loaded from: classes21.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    public final int getNumListeners() {
        return this.listeners.size();
    }

    public final void addListener(@NotNull Dumpable listener) {
        Intrinsics.checkParameterIsNotNull(listener, "listener");
        boolean z = false;
        Preconditions.checkNotNull(listener, "The listener to be added cannot be null", new Object[0]);
        synchronized (this.listeners) {
            Iterable $receiver$iv = this.listeners;
            if (!($receiver$iv instanceof Collection) || !((Collection) $receiver$iv).isEmpty()) {
                Iterator<T> it = $receiver$iv.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Object element$iv = it.next();
                    WeakReference it2 = (WeakReference) element$iv;
                    if (Intrinsics.areEqual((Dumpable) it2.get(), listener)) {
                        z = true;
                        break;
                    }
                }
            }
            if (!z) {
                this.listeners.add(new WeakReference<>(listener));
            }
            Unit unit = Unit.INSTANCE;
        }
    }

    public final void removeListener(@NotNull final Dumpable listener) {
        Intrinsics.checkParameterIsNotNull(listener, "listener");
        synchronized (this.listeners) {
            CollectionsKt.removeAll((List) this.listeners, (Function1) new Function1<WeakReference<Dumpable>, Boolean>() { // from class: com.android.systemui.DumpController$removeListener$$inlined$synchronized$lambda$1
                /* JADX INFO: Access modifiers changed from: package-private */
                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                {
                    super(1);
                }

                @Override // kotlin.jvm.functions.Function1
                public /* bridge */ /* synthetic */ Boolean invoke(WeakReference<Dumpable> weakReference) {
                    return Boolean.valueOf(invoke2(weakReference));
                }

                /* renamed from: invoke  reason: avoid collision after fix types in other method */
                public final boolean invoke2(@NotNull WeakReference<Dumpable> it) {
                    Intrinsics.checkParameterIsNotNull(it, "it");
                    return Intrinsics.areEqual(it.get(), listener) || it.get() == null;
                }
            });
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(@Nullable FileDescriptor fd, @NotNull PrintWriter pw, @Nullable String[] args) {
        Intrinsics.checkParameterIsNotNull(pw, "pw");
        pw.println("DumpController state:");
        synchronized (this.listeners) {
            Iterable $receiver$iv = this.listeners;
            for (Object element$iv : $receiver$iv) {
                WeakReference it = (WeakReference) element$iv;
                Dumpable dumpable = (Dumpable) it.get();
                if (dumpable != null) {
                    dumpable.dump(fd, pw, args);
                }
            }
            Unit unit = Unit.INSTANCE;
        }
    }
}
