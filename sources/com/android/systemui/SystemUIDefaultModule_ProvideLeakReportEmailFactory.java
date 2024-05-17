package com.android.systemui;

import androidx.annotation.Nullable;
import dagger.internal.Factory;
/* loaded from: classes21.dex */
public final class SystemUIDefaultModule_ProvideLeakReportEmailFactory implements Factory<String> {
    private static final SystemUIDefaultModule_ProvideLeakReportEmailFactory INSTANCE = new SystemUIDefaultModule_ProvideLeakReportEmailFactory();

    @Override // javax.inject.Provider
    @Nullable
    public String get() {
        return provideInstance();
    }

    @Nullable
    public static String provideInstance() {
        return proxyProvideLeakReportEmail();
    }

    public static SystemUIDefaultModule_ProvideLeakReportEmailFactory create() {
        return INSTANCE;
    }

    @Nullable
    public static String proxyProvideLeakReportEmail() {
        return SystemUIDefaultModule.provideLeakReportEmail();
    }
}
