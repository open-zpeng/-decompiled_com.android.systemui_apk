package com.android.systemui;

import android.app.ActivityThread;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.ArraySet;
import android.util.Log;
import android.util.TimingsTraceLog;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIApplication;
import com.android.systemui.plugins.OverlayPlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.util.NotificationChannels;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class SystemUIApplication extends Application implements SysUiServiceProvider {
    private static final boolean DEBUG = false;
    public static final String TAG = "SystemUIService";
    private static SystemUIApplication sApplication;
    private boolean mBootCompleted;
    private final Map<Class<?>, Object> mComponents = new HashMap();
    private ContextAvailableCallback mContextAvailableCallback;
    private SystemUI[] mServices;
    private boolean mServicesStarted;

    /* loaded from: classes21.dex */
    interface ContextAvailableCallback {
        void onContextAvailable(Context context);
    }

    public SystemUIApplication() {
        Log.v(TAG, "SystemUIApplication constructed.");
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "SystemUIApplication created.");
        TimingsTraceLog log = new TimingsTraceLog("SystemUIBootTiming", (long) PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM);
        log.traceBegin("DependencyInjection");
        this.mContextAvailableCallback.onContextAvailable(this);
        log.traceEnd();
        setTheme(R.style.Theme_SystemUI);
        sApplication = this;
        if (Process.myUserHandle().equals(UserHandle.SYSTEM)) {
            IntentFilter bootCompletedFilter = new IntentFilter("android.intent.action.BOOT_COMPLETED");
            bootCompletedFilter.setPriority(1000);
            registerReceiver(new BroadcastReceiver() { // from class: com.android.systemui.SystemUIApplication.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if (SystemUIApplication.this.mBootCompleted) {
                        return;
                    }
                    SystemUIApplication.this.unregisterReceiver(this);
                    SystemUIApplication.this.mBootCompleted = true;
                    if (SystemUIApplication.this.mServicesStarted) {
                        int N = SystemUIApplication.this.mServices.length;
                        for (int i = 0; i < N; i++) {
                            SystemUIApplication.this.mServices[i].onBootCompleted();
                        }
                    }
                }
            }, bootCompletedFilter);
            IntentFilter localeChangedFilter = new IntentFilter("android.intent.action.LOCALE_CHANGED");
            registerReceiver(new BroadcastReceiver() { // from class: com.android.systemui.SystemUIApplication.2
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if ("android.intent.action.LOCALE_CHANGED".equals(intent.getAction()) && SystemUIApplication.this.mBootCompleted) {
                        NotificationChannels.createAll(context);
                    }
                }
            }, localeChangedFilter);
            return;
        }
        String processName = ActivityThread.currentProcessName();
        ApplicationInfo info = getApplicationInfo();
        if (processName != null) {
            if (processName.startsWith(info.processName + NavigationBarInflaterView.KEY_IMAGE_DELIM)) {
                return;
            }
        }
        startSecondaryUserServicesIfNeeded();
    }

    public void startServicesIfNeeded() {
        String[] names = getResources().getStringArray(R.array.config_carSystemUIServiceComponents);
        startServicesIfNeeded("StartServices", names);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startSecondaryUserServicesIfNeeded() {
        String[] names = getResources().getStringArray(R.array.config_systemUIServiceComponentsPerUser);
        startServicesIfNeeded("StartSecondaryServices", names);
    }

    private void startServicesIfNeeded(String metricsPrefix, String[] services) {
        if (this.mServicesStarted) {
            return;
        }
        this.mServices = new SystemUI[services.length];
        if (!this.mBootCompleted && "1".equals(SystemProperties.get("sys.boot_completed"))) {
            this.mBootCompleted = true;
        }
        Log.v(TAG, "Starting SystemUI services for user " + Process.myUserHandle().getIdentifier() + ".");
        TimingsTraceLog log = new TimingsTraceLog("SystemUIBootTiming", (long) PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM);
        log.traceBegin(metricsPrefix);
        int N = services.length;
        for (int i = 0; i < N; i++) {
            String clsName = services[i];
            log.traceBegin(metricsPrefix + clsName);
            long ti = System.currentTimeMillis();
            try {
                Class cls = Class.forName(clsName);
                Object o = cls.newInstance();
                if (o instanceof SystemUI.Injector) {
                    o = ((SystemUI.Injector) o).apply(this);
                }
                this.mServices[i] = (SystemUI) o;
                SystemUI[] systemUIArr = this.mServices;
                systemUIArr[i].mContext = this;
                systemUIArr[i].mComponents = this.mComponents;
                try {
                    systemUIArr[i].start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log.traceEnd();
                long ti2 = System.currentTimeMillis() - ti;
                if (ti2 > 1000) {
                    Log.w(TAG, "Initialization of " + cls.getName() + " took " + ti2 + " ms");
                }
                if (this.mBootCompleted) {
                    this.mServices[i].onBootCompleted();
                }
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex2) {
                throw new RuntimeException(ex2);
            } catch (InstantiationException ex3) {
                throw new RuntimeException(ex3);
            }
        }
        ((InitController) Dependency.get(InitController.class)).executePostInitTasks();
        log.traceEnd();
        Handler mainHandler = new Handler(Looper.getMainLooper());
        ((PluginManager) Dependency.get(PluginManager.class)).addPluginListener((PluginListener) new AnonymousClass3(mainHandler), OverlayPlugin.class, true);
        this.mServicesStarted = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.SystemUIApplication$3  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass3 implements PluginListener<OverlayPlugin> {
        private ArraySet<OverlayPlugin> mOverlays = new ArraySet<>();
        final /* synthetic */ Handler val$mainHandler;

        AnonymousClass3(Handler handler) {
            this.val$mainHandler = handler;
        }

        @Override // com.android.systemui.plugins.PluginListener
        public void onPluginConnected(final OverlayPlugin plugin, Context pluginContext) {
            this.val$mainHandler.post(new Runnable() { // from class: com.android.systemui.SystemUIApplication.3.1
                @Override // java.lang.Runnable
                public void run() {
                    StatusBar statusBar = (StatusBar) SystemUIApplication.this.getComponent(StatusBar.class);
                    if (statusBar != null) {
                        plugin.setup(statusBar.getStatusBarWindow(), statusBar.getNavigationBarView(), new Callback(plugin), DozeParameters.getInstance(SystemUIApplication.this.getBaseContext()));
                    }
                }
            });
        }

        @Override // com.android.systemui.plugins.PluginListener
        public void onPluginDisconnected(final OverlayPlugin plugin) {
            this.val$mainHandler.post(new Runnable() { // from class: com.android.systemui.SystemUIApplication.3.2
                @Override // java.lang.Runnable
                public void run() {
                    AnonymousClass3.this.mOverlays.remove(plugin);
                    ((StatusBarWindowController) Dependency.get(StatusBarWindowController.class)).setForcePluginOpen(AnonymousClass3.this.mOverlays.size() != 0);
                }
            });
        }

        /* renamed from: com.android.systemui.SystemUIApplication$3$Callback */
        /* loaded from: classes21.dex */
        class Callback implements OverlayPlugin.Callback {
            private final OverlayPlugin mPlugin;

            Callback(OverlayPlugin plugin) {
                this.mPlugin = plugin;
            }

            @Override // com.android.systemui.plugins.OverlayPlugin.Callback
            public void onHoldStatusBarOpenChange() {
                if (this.mPlugin.holdStatusBarOpen()) {
                    AnonymousClass3.this.mOverlays.add(this.mPlugin);
                } else {
                    AnonymousClass3.this.mOverlays.remove(this.mPlugin);
                }
                AnonymousClass3.this.val$mainHandler.post(new AnonymousClass1());
            }

            /* JADX INFO: Access modifiers changed from: package-private */
            /* renamed from: com.android.systemui.SystemUIApplication$3$Callback$1  reason: invalid class name */
            /* loaded from: classes21.dex */
            public class AnonymousClass1 implements Runnable {
                AnonymousClass1() {
                }

                @Override // java.lang.Runnable
                public void run() {
                    ((StatusBarWindowController) Dependency.get(StatusBarWindowController.class)).setStateListener(new StatusBarWindowController.OtherwisedCollapsedListener() { // from class: com.android.systemui.-$$Lambda$SystemUIApplication$3$Callback$1$sx3y3YDR9PfTcBFpqL5skj6JDUg
                        @Override // com.android.systemui.statusbar.phone.StatusBarWindowController.OtherwisedCollapsedListener
                        public final void setWouldOtherwiseCollapse(boolean z) {
                            SystemUIApplication.AnonymousClass3.Callback.AnonymousClass1.this.lambda$run$1$SystemUIApplication$3$Callback$1(z);
                        }
                    });
                    ((StatusBarWindowController) Dependency.get(StatusBarWindowController.class)).setForcePluginOpen(AnonymousClass3.this.mOverlays.size() != 0);
                }

                public /* synthetic */ void lambda$run$1$SystemUIApplication$3$Callback$1(final boolean b) {
                    AnonymousClass3.this.mOverlays.forEach(new Consumer() { // from class: com.android.systemui.-$$Lambda$SystemUIApplication$3$Callback$1$BwolTXxR8lk33KXtnn_kk1xKxjQ
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((OverlayPlugin) obj).setCollapseDesired(b);
                        }
                    });
                }
            }
        }
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.mServicesStarted) {
            SystemUIFactory.getInstance().getRootComponent().getConfigurationController().onConfigurationChanged(newConfig);
            int len = this.mServices.length;
            for (int i = 0; i < len; i++) {
                SystemUI[] systemUIArr = this.mServices;
                if (systemUIArr[i] != null) {
                    systemUIArr[i].onConfigurationChanged(newConfig);
                }
            }
        }
    }

    public static Application getApplication() {
        return sApplication;
    }

    @Override // com.android.systemui.SysUiServiceProvider
    public <T> T getComponent(Class<T> interfaceType) {
        return (T) this.mComponents.get(interfaceType);
    }

    public SystemUI[] getServices() {
        return this.mServices;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContextAvailableCallback(ContextAvailableCallback callback) {
        this.mContextAvailableCallback = callback;
    }

    public static Context getContext() {
        return sApplication.getApplicationContext();
    }
}
