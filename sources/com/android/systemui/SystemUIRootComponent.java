package com.android.systemui;

import com.android.systemui.Dependency;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.util.leak.GarbageMonitor;
import dagger.Component;
import javax.inject.Named;
import javax.inject.Singleton;
@Component(modules = {DependencyProvider.class, DependencyBinder.class, ServiceBinder.class, SystemUIFactory.ContextHolder.class, SystemUIModule.class, SystemUIDefaultModule.class})
@Singleton
/* loaded from: classes21.dex */
public interface SystemUIRootComponent {
    @Named(Dependency.ALLOW_NOTIFICATION_LONG_PRESS_NAME)
    boolean allowNotificationLongPressName();

    @Singleton
    Dependency.DependencyInjector createDependency();

    @Singleton
    FragmentService.FragmentCreator createFragmentCreator();

    @Singleton
    GarbageMonitor createGarbageMonitor();

    InjectionInflationController.ViewCreator createViewCreator();

    @Singleton
    ConfigurationController getConfigurationController();

    @Singleton
    StatusBar.StatusBarInjector getStatusBarInjector();

    void inject(SystemUIAppComponentFactory systemUIAppComponentFactory);
}
