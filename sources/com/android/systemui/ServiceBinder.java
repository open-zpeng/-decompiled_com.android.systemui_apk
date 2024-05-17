package com.android.systemui;

import com.android.systemui.doze.DozeService;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
@Module
/* loaded from: classes21.dex */
public abstract class ServiceBinder {
    @Binds
    public abstract ContextComponentHelper bindComponentHelper(ContextComponentResolver contextComponentResolver);

    @ClassKey(DozeService.class)
    @Binds
    @IntoMap
    public abstract Object bindDozeService(DozeService dozeService);
}
