package com.android.systemui.tuner;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.util.leak.LeakDetector;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class TunerServiceImpl_Factory implements Factory<TunerServiceImpl> {
    private final Provider<Context> contextProvider;
    private final Provider<LeakDetector> leakDetectorProvider;
    private final Provider<Handler> mainHandlerProvider;

    public TunerServiceImpl_Factory(Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider, Provider<LeakDetector> leakDetectorProvider) {
        this.contextProvider = contextProvider;
        this.mainHandlerProvider = mainHandlerProvider;
        this.leakDetectorProvider = leakDetectorProvider;
    }

    @Override // javax.inject.Provider
    public TunerServiceImpl get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.leakDetectorProvider);
    }

    public static TunerServiceImpl provideInstance(Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider, Provider<LeakDetector> leakDetectorProvider) {
        return new TunerServiceImpl(contextProvider.get(), mainHandlerProvider.get(), leakDetectorProvider.get());
    }

    public static TunerServiceImpl_Factory create(Provider<Context> contextProvider, Provider<Handler> mainHandlerProvider, Provider<LeakDetector> leakDetectorProvider) {
        return new TunerServiceImpl_Factory(contextProvider, mainHandlerProvider, leakDetectorProvider);
    }

    public static TunerServiceImpl newTunerServiceImpl(Context context, Handler mainHandler, LeakDetector leakDetector) {
        return new TunerServiceImpl(context, mainHandler, leakDetector);
    }
}
