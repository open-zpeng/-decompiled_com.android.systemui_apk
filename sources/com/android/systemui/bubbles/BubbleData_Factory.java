package com.android.systemui.bubbles;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class BubbleData_Factory implements Factory<BubbleData> {
    private final Provider<Context> contextProvider;

    public BubbleData_Factory(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override // javax.inject.Provider
    public BubbleData get() {
        return provideInstance(this.contextProvider);
    }

    public static BubbleData provideInstance(Provider<Context> contextProvider) {
        return new BubbleData(contextProvider.get());
    }

    public static BubbleData_Factory create(Provider<Context> contextProvider) {
        return new BubbleData_Factory(contextProvider);
    }

    public static BubbleData newBubbleData(Context context) {
        return new BubbleData(context);
    }
}
