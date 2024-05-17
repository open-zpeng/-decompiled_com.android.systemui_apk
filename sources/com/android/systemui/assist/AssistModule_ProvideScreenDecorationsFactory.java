package com.android.systemui.assist;

import android.content.Context;
import com.android.systemui.ScreenDecorations;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AssistModule_ProvideScreenDecorationsFactory implements Factory<ScreenDecorations> {
    private final Provider<Context> contextProvider;

    public AssistModule_ProvideScreenDecorationsFactory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public ScreenDecorations get() {
        return provideInstance(this.contextProvider);
    }

    public static ScreenDecorations provideInstance(Provider<Context> contextProvider) {
        return proxyProvideScreenDecorations(contextProvider.get());
    }

    public static AssistModule_ProvideScreenDecorationsFactory create(Provider<Context> contextProvider) {
        return new AssistModule_ProvideScreenDecorationsFactory(contextProvider);
    }

    public static ScreenDecorations proxyProvideScreenDecorations(Context context) {
        return (ScreenDecorations) Preconditions.checkNotNull(AssistModule.provideScreenDecorations(context), "Cannot return null from a non-@Nullable @Provides method");
    }
}
