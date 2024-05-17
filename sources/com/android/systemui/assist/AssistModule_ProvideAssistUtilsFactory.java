package com.android.systemui.assist;

import android.content.Context;
import com.android.internal.app.AssistUtils;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class AssistModule_ProvideAssistUtilsFactory implements Factory<AssistUtils> {
    private final Provider<Context> contextProvider;

    public AssistModule_ProvideAssistUtilsFactory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public AssistUtils get() {
        return provideInstance(this.contextProvider);
    }

    public static AssistUtils provideInstance(Provider<Context> contextProvider) {
        return proxyProvideAssistUtils(contextProvider.get());
    }

    public static AssistModule_ProvideAssistUtilsFactory create(Provider<Context> contextProvider) {
        return new AssistModule_ProvideAssistUtilsFactory(contextProvider);
    }

    public static AssistUtils proxyProvideAssistUtils(Context context) {
        return (AssistUtils) Preconditions.checkNotNull(AssistModule.provideAssistUtils(context), "Cannot return null from a non-@Nullable @Provides method");
    }
}
