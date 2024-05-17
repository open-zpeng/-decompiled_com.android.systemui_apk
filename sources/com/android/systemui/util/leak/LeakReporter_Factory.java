package com.android.systemui.util.leak;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class LeakReporter_Factory implements Factory<LeakReporter> {
    private final Provider<Context> contextProvider;
    private final Provider<LeakDetector> leakDetectorProvider;
    private final Provider<String> leakReportEmailProvider;

    public LeakReporter_Factory(Provider<Context> contextProvider, Provider<LeakDetector> leakDetectorProvider, Provider<String> leakReportEmailProvider) {
        this.contextProvider = contextProvider;
        this.leakDetectorProvider = leakDetectorProvider;
        this.leakReportEmailProvider = leakReportEmailProvider;
    }

    @Override // javax.inject.Provider
    public LeakReporter get() {
        return provideInstance(this.contextProvider, this.leakDetectorProvider, this.leakReportEmailProvider);
    }

    public static LeakReporter provideInstance(Provider<Context> contextProvider, Provider<LeakDetector> leakDetectorProvider, Provider<String> leakReportEmailProvider) {
        return new LeakReporter(contextProvider.get(), leakDetectorProvider.get(), leakReportEmailProvider.get());
    }

    public static LeakReporter_Factory create(Provider<Context> contextProvider, Provider<LeakDetector> leakDetectorProvider, Provider<String> leakReportEmailProvider) {
        return new LeakReporter_Factory(contextProvider, leakDetectorProvider, leakReportEmailProvider);
    }

    public static LeakReporter newLeakReporter(Context context, LeakDetector leakDetector, String leakReportEmail) {
        return new LeakReporter(context, leakDetector, leakReportEmail);
    }
}
