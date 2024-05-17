package com.android.systemui.qs;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.DumpController;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class QSPanel_Factory implements Factory<QSPanel> {
    private final Provider<AttributeSet> attrsProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DumpController> dumpControllerProvider;

    public QSPanel_Factory(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<DumpController> dumpControllerProvider) {
        this.contextProvider = contextProvider;
        this.attrsProvider = attrsProvider;
        this.dumpControllerProvider = dumpControllerProvider;
    }

    @Override // javax.inject.Provider
    public QSPanel get() {
        return provideInstance(this.contextProvider, this.attrsProvider, this.dumpControllerProvider);
    }

    public static QSPanel provideInstance(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<DumpController> dumpControllerProvider) {
        return new QSPanel(contextProvider.get(), attrsProvider.get(), dumpControllerProvider.get());
    }

    public static QSPanel_Factory create(Provider<Context> contextProvider, Provider<AttributeSet> attrsProvider, Provider<DumpController> dumpControllerProvider) {
        return new QSPanel_Factory(contextProvider, attrsProvider, dumpControllerProvider);
    }

    public static QSPanel newQSPanel(Context context, AttributeSet attrs, DumpController dumpController) {
        return new QSPanel(context, attrs, dumpController);
    }
}
