package com.android.systemui.util.leak;

import android.content.Context;
import android.os.Looper;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class GarbageMonitor_Factory implements Factory<GarbageMonitor> {
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;
    private final Provider<LeakDetector> leakDetectorProvider;
    private final Provider<LeakReporter> leakReporterProvider;

    public GarbageMonitor_Factory(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider, Provider<LeakDetector> leakDetectorProvider, Provider<LeakReporter> leakReporterProvider) {
        this.contextProvider = contextProvider;
        this.bgLooperProvider = bgLooperProvider;
        this.leakDetectorProvider = leakDetectorProvider;
        this.leakReporterProvider = leakReporterProvider;
    }

    @Override // javax.inject.Provider
    public GarbageMonitor get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider, this.leakDetectorProvider, this.leakReporterProvider);
    }

    public static GarbageMonitor provideInstance(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider, Provider<LeakDetector> leakDetectorProvider, Provider<LeakReporter> leakReporterProvider) {
        return new GarbageMonitor(contextProvider.get(), bgLooperProvider.get(), leakDetectorProvider.get(), leakReporterProvider.get());
    }

    public static GarbageMonitor_Factory create(Provider<Context> contextProvider, Provider<Looper> bgLooperProvider, Provider<LeakDetector> leakDetectorProvider, Provider<LeakReporter> leakReporterProvider) {
        return new GarbageMonitor_Factory(contextProvider, bgLooperProvider, leakDetectorProvider, leakReporterProvider);
    }

    public static GarbageMonitor newGarbageMonitor(Context context, Looper bgLooper, LeakDetector leakDetector, LeakReporter leakReporter) {
        return new GarbageMonitor(context, bgLooper, leakDetector, leakReporter);
    }
}
