package com.android.systemui;

import dagger.MembersInjector;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class SystemUIAppComponentFactory_MembersInjector implements MembersInjector<SystemUIAppComponentFactory> {
    private final Provider<ContextComponentHelper> mComponentHelperProvider;

    public SystemUIAppComponentFactory_MembersInjector(Provider<ContextComponentHelper> mComponentHelperProvider) {
        this.mComponentHelperProvider = mComponentHelperProvider;
    }

    public static MembersInjector<SystemUIAppComponentFactory> create(Provider<ContextComponentHelper> mComponentHelperProvider) {
        return new SystemUIAppComponentFactory_MembersInjector(mComponentHelperProvider);
    }

    @Override // dagger.MembersInjector
    public void injectMembers(SystemUIAppComponentFactory instance) {
        injectMComponentHelper(instance, this.mComponentHelperProvider.get());
    }

    public static void injectMComponentHelper(SystemUIAppComponentFactory instance, ContextComponentHelper mComponentHelper) {
        instance.mComponentHelper = mComponentHelper;
    }
}
