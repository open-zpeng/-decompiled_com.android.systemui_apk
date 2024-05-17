package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Handler;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class SmartReplyConstants_Factory implements Factory<SmartReplyConstants> {
    private final Provider<Context> contextProvider;
    private final Provider<Handler> handlerProvider;

    public SmartReplyConstants_Factory(Provider<Handler> handlerProvider, Provider<Context> contextProvider) {
        this.handlerProvider = handlerProvider;
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public SmartReplyConstants get() {
        return provideInstance(this.handlerProvider, this.contextProvider);
    }

    public static SmartReplyConstants provideInstance(Provider<Handler> handlerProvider, Provider<Context> contextProvider) {
        return new SmartReplyConstants(handlerProvider.get(), contextProvider.get());
    }

    public static SmartReplyConstants_Factory create(Provider<Handler> handlerProvider, Provider<Context> contextProvider) {
        return new SmartReplyConstants_Factory(handlerProvider, contextProvider);
    }

    public static SmartReplyConstants newSmartReplyConstants(Handler handler, Context context) {
        return new SmartReplyConstants(handler, context);
    }
}
