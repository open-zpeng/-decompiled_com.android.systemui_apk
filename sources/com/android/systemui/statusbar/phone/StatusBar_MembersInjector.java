package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.notification.BypassHeadsUpNotifier;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.util.InjectionInflationController;
import dagger.MembersInjector;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class StatusBar_MembersInjector implements MembersInjector<StatusBar> {
    private final Provider<Boolean> mAllowNotificationLongPressProvider;
    private final Provider<BypassHeadsUpNotifier> mBypassHeadsUpNotifierProvider;
    private final Provider<DynamicPrivacyController> mDynamicPrivacyControllerProvider;
    private final Provider<HeadsUpManagerPhone> mHeadsUpManagerProvider;
    private final Provider<InjectionInflationController> mInjectionInflaterProvider;
    private final Provider<KeyguardBypassController> mKeyguardBypassControllerProvider;
    private final Provider<KeyguardLiftController> mKeyguardLiftControllerProvider;
    private final Provider<PulseExpansionHandler> mPulseExpansionHandlerProvider;
    private final Provider<NotificationWakeUpCoordinator> mWakeUpCoordinatorProvider;

    public StatusBar_MembersInjector(Provider<InjectionInflationController> mInjectionInflaterProvider, Provider<PulseExpansionHandler> mPulseExpansionHandlerProvider, Provider<NotificationWakeUpCoordinator> mWakeUpCoordinatorProvider, Provider<KeyguardBypassController> mKeyguardBypassControllerProvider, Provider<HeadsUpManagerPhone> mHeadsUpManagerProvider, Provider<DynamicPrivacyController> mDynamicPrivacyControllerProvider, Provider<BypassHeadsUpNotifier> mBypassHeadsUpNotifierProvider, Provider<KeyguardLiftController> mKeyguardLiftControllerProvider, Provider<Boolean> mAllowNotificationLongPressProvider) {
        this.mInjectionInflaterProvider = mInjectionInflaterProvider;
        this.mPulseExpansionHandlerProvider = mPulseExpansionHandlerProvider;
        this.mWakeUpCoordinatorProvider = mWakeUpCoordinatorProvider;
        this.mKeyguardBypassControllerProvider = mKeyguardBypassControllerProvider;
        this.mHeadsUpManagerProvider = mHeadsUpManagerProvider;
        this.mDynamicPrivacyControllerProvider = mDynamicPrivacyControllerProvider;
        this.mBypassHeadsUpNotifierProvider = mBypassHeadsUpNotifierProvider;
        this.mKeyguardLiftControllerProvider = mKeyguardLiftControllerProvider;
        this.mAllowNotificationLongPressProvider = mAllowNotificationLongPressProvider;
    }

    public static MembersInjector<StatusBar> create(Provider<InjectionInflationController> mInjectionInflaterProvider, Provider<PulseExpansionHandler> mPulseExpansionHandlerProvider, Provider<NotificationWakeUpCoordinator> mWakeUpCoordinatorProvider, Provider<KeyguardBypassController> mKeyguardBypassControllerProvider, Provider<HeadsUpManagerPhone> mHeadsUpManagerProvider, Provider<DynamicPrivacyController> mDynamicPrivacyControllerProvider, Provider<BypassHeadsUpNotifier> mBypassHeadsUpNotifierProvider, Provider<KeyguardLiftController> mKeyguardLiftControllerProvider, Provider<Boolean> mAllowNotificationLongPressProvider) {
        return new StatusBar_MembersInjector(mInjectionInflaterProvider, mPulseExpansionHandlerProvider, mWakeUpCoordinatorProvider, mKeyguardBypassControllerProvider, mHeadsUpManagerProvider, mDynamicPrivacyControllerProvider, mBypassHeadsUpNotifierProvider, mKeyguardLiftControllerProvider, mAllowNotificationLongPressProvider);
    }

    @Override // dagger.MembersInjector
    public void injectMembers(StatusBar instance) {
        injectMInjectionInflater(instance, this.mInjectionInflaterProvider.get());
        injectMPulseExpansionHandler(instance, this.mPulseExpansionHandlerProvider.get());
        injectMWakeUpCoordinator(instance, this.mWakeUpCoordinatorProvider.get());
        injectMKeyguardBypassController(instance, this.mKeyguardBypassControllerProvider.get());
        injectMHeadsUpManager(instance, this.mHeadsUpManagerProvider.get());
        injectMDynamicPrivacyController(instance, this.mDynamicPrivacyControllerProvider.get());
        injectMBypassHeadsUpNotifier(instance, this.mBypassHeadsUpNotifierProvider.get());
        injectMKeyguardLiftController(instance, this.mKeyguardLiftControllerProvider.get());
        injectMAllowNotificationLongPress(instance, this.mAllowNotificationLongPressProvider.get().booleanValue());
    }

    public static void injectMInjectionInflater(StatusBar instance, InjectionInflationController mInjectionInflater) {
        instance.mInjectionInflater = mInjectionInflater;
    }

    public static void injectMPulseExpansionHandler(StatusBar instance, PulseExpansionHandler mPulseExpansionHandler) {
        instance.mPulseExpansionHandler = mPulseExpansionHandler;
    }

    public static void injectMWakeUpCoordinator(StatusBar instance, NotificationWakeUpCoordinator mWakeUpCoordinator) {
        instance.mWakeUpCoordinator = mWakeUpCoordinator;
    }

    public static void injectMKeyguardBypassController(StatusBar instance, KeyguardBypassController mKeyguardBypassController) {
        instance.mKeyguardBypassController = mKeyguardBypassController;
    }

    public static void injectMHeadsUpManager(StatusBar instance, HeadsUpManagerPhone mHeadsUpManager) {
        instance.mHeadsUpManager = mHeadsUpManager;
    }

    public static void injectMDynamicPrivacyController(StatusBar instance, DynamicPrivacyController mDynamicPrivacyController) {
        instance.mDynamicPrivacyController = mDynamicPrivacyController;
    }

    public static void injectMBypassHeadsUpNotifier(StatusBar instance, BypassHeadsUpNotifier mBypassHeadsUpNotifier) {
        instance.mBypassHeadsUpNotifier = mBypassHeadsUpNotifier;
    }

    public static void injectMKeyguardLiftController(StatusBar instance, KeyguardLiftController mKeyguardLiftController) {
        instance.mKeyguardLiftController = mKeyguardLiftController;
    }

    public static void injectMAllowNotificationLongPress(StatusBar instance, boolean mAllowNotificationLongPress) {
        instance.mAllowNotificationLongPress = mAllowNotificationLongPress;
    }
}
