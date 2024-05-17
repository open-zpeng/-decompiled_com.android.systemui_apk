package com.android.systemui.shared.plugins;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import androidx.mediarouter.media.SystemMediaRouteProvider;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginFragment;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginEnabler;
import com.android.systemui.shared.plugins.VersionInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public class PluginInstanceManager<T extends Plugin> {
    private static final boolean DEBUG = false;
    public static final String PLUGIN_PERMISSION = "com.android.systemui.permission.PLUGIN";
    private static final String TAG = "PluginInstanceManager";
    private final boolean isDebuggable;
    private final String mAction;
    private final boolean mAllowMultiple;
    private final Context mContext;
    private final PluginListener<T> mListener;
    @VisibleForTesting
    final PluginInstanceManager<T>.MainHandler mMainHandler;
    private final PluginManagerImpl mManager;
    @VisibleForTesting
    final PluginInstanceManager<T>.PluginHandler mPluginHandler;
    private final PackageManager mPm;
    private final VersionInfo mVersion;
    private final ArraySet<String> mWhitelistedPlugins;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PluginInstanceManager(Context context, String action, PluginListener<T> listener, boolean allowMultiple, Looper looper, VersionInfo version, PluginManagerImpl manager) {
        this(context, context.getPackageManager(), action, listener, allowMultiple, looper, version, manager, Build.IS_DEBUGGABLE, manager.getWhitelistedPlugins());
    }

    @VisibleForTesting
    PluginInstanceManager(Context context, PackageManager pm, String action, PluginListener<T> listener, boolean allowMultiple, Looper looper, VersionInfo version, PluginManagerImpl manager, boolean debuggable, String[] pluginWhitelist) {
        this.mWhitelistedPlugins = new ArraySet<>();
        this.mMainHandler = new MainHandler(Looper.getMainLooper());
        this.mPluginHandler = new PluginHandler(looper);
        this.mManager = manager;
        this.mContext = context;
        this.mPm = pm;
        this.mAction = action;
        this.mListener = listener;
        this.mAllowMultiple = allowMultiple;
        this.mVersion = version;
        this.mWhitelistedPlugins.addAll(Arrays.asList(pluginWhitelist));
        this.isDebuggable = debuggable;
    }

    public PluginInfo<T> getPlugin() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException("Must be called from UI thread");
        }
        this.mPluginHandler.handleQueryPlugins(null);
        if (((PluginHandler) this.mPluginHandler).mPlugins.size() > 0) {
            this.mMainHandler.removeMessages(1);
            PluginInfo<T> info = (PluginInfo) ((PluginHandler) this.mPluginHandler).mPlugins.get(0);
            PluginPrefs.setHasPlugins(this.mContext);
            info.mPlugin.onCreate(this.mContext, ((PluginInfo) info).mPluginContext);
            return info;
        }
        return null;
    }

    public void loadAll() {
        this.mPluginHandler.sendEmptyMessage(1);
    }

    public void destroy() {
        ArrayList<PluginInfo> plugins = new ArrayList<>(((PluginHandler) this.mPluginHandler).mPlugins);
        Iterator<PluginInfo> it = plugins.iterator();
        while (it.hasNext()) {
            PluginInfo plugin = it.next();
            this.mMainHandler.obtainMessage(2, plugin.mPlugin).sendToTarget();
        }
    }

    public void onPackageRemoved(String pkg) {
        this.mPluginHandler.obtainMessage(3, pkg).sendToTarget();
    }

    public void onPackageChange(String pkg) {
        this.mPluginHandler.obtainMessage(3, pkg).sendToTarget();
        this.mPluginHandler.obtainMessage(2, pkg).sendToTarget();
    }

    public boolean checkAndDisable(String className) {
        boolean disableAny = false;
        ArrayList<PluginInfo> plugins = new ArrayList<>(((PluginHandler) this.mPluginHandler).mPlugins);
        Iterator<PluginInfo> it = plugins.iterator();
        while (it.hasNext()) {
            PluginInfo info = it.next();
            if (className.startsWith(info.mPackage)) {
                disable(info, 2);
                disableAny = true;
            }
        }
        return disableAny;
    }

    public boolean disableAll() {
        ArrayList<PluginInfo> plugins = new ArrayList<>(((PluginHandler) this.mPluginHandler).mPlugins);
        for (int i = 0; i < plugins.size(); i++) {
            disable(plugins.get(i), 3);
        }
        int i2 = plugins.size();
        return i2 != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPluginWhitelisted(ComponentName pluginName) {
        Iterator<String> it = this.mWhitelistedPlugins.iterator();
        while (it.hasNext()) {
            String componentNameOrPackage = it.next();
            ComponentName componentName = ComponentName.unflattenFromString(componentNameOrPackage);
            if (componentName == null) {
                if (componentNameOrPackage.equals(pluginName.getPackageName())) {
                    return true;
                }
            } else if (componentName.equals(pluginName)) {
                return true;
            }
        }
        return false;
    }

    private void disable(PluginInfo info, @PluginEnabler.DisableReason int reason) {
        ComponentName pluginComponent = new ComponentName(info.mPackage, info.mClass);
        if (isPluginWhitelisted(pluginComponent)) {
            return;
        }
        Log.w(TAG, "Disabling plugin " + pluginComponent.flattenToShortString());
        this.mManager.getPluginEnabler().setDisabled(pluginComponent, reason);
    }

    public <T> boolean dependsOn(Plugin p, Class<T> cls) {
        ArrayList<PluginInfo> plugins = new ArrayList<>(((PluginHandler) this.mPluginHandler).mPlugins);
        Iterator<PluginInfo> it = plugins.iterator();
        while (it.hasNext()) {
            PluginInfo info = it.next();
            if (info.mPlugin.getClass().getName().equals(p.getClass().getName())) {
                return info.mVersion != null && info.mVersion.hasClass(cls);
            }
        }
        return false;
    }

    public String toString() {
        return String.format("%s@%s (action=%s)", getClass().getSimpleName(), Integer.valueOf(hashCode()), this.mAction);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class MainHandler extends Handler {
        private static final int PLUGIN_CONNECTED = 1;
        private static final int PLUGIN_DISCONNECTED = 2;

        public MainHandler(Looper looper) {
            super(looper);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                PluginPrefs.setHasPlugins(PluginInstanceManager.this.mContext);
                PluginInfo<T> info = (PluginInfo) msg.obj;
                PluginInstanceManager.this.mManager.handleWtfs();
                if (!(msg.obj instanceof PluginFragment)) {
                    ((Plugin) info.mPlugin).onCreate(PluginInstanceManager.this.mContext, ((PluginInfo) info).mPluginContext);
                }
                PluginInstanceManager.this.mListener.onPluginConnected((Plugin) info.mPlugin, ((PluginInfo) info).mPluginContext);
            } else if (i == 2) {
                PluginInstanceManager.this.mListener.onPluginDisconnected((Plugin) msg.obj);
                if (!(msg.obj instanceof PluginFragment)) {
                    ((Plugin) msg.obj).onDestroy();
                }
            } else {
                super.handleMessage(msg);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class PluginHandler extends Handler {
        private static final int QUERY_ALL = 1;
        private static final int QUERY_PKG = 2;
        private static final int REMOVE_PKG = 3;
        private final ArrayList<PluginInfo<T>> mPlugins;

        public PluginHandler(Looper looper) {
            super(looper);
            this.mPlugins = new ArrayList<>();
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                for (int i2 = this.mPlugins.size() - 1; i2 >= 0; i2--) {
                    PluginInfo<T> plugin = this.mPlugins.get(i2);
                    PluginInstanceManager.this.mListener.onPluginDisconnected(plugin.mPlugin);
                    if (!(plugin.mPlugin instanceof PluginFragment)) {
                        plugin.mPlugin.onDestroy();
                    }
                }
                this.mPlugins.clear();
                handleQueryPlugins(null);
            } else if (i == 2) {
                String p = (String) msg.obj;
                if (PluginInstanceManager.this.mAllowMultiple || this.mPlugins.size() == 0) {
                    handleQueryPlugins(p);
                }
            } else if (i == 3) {
                String pkg = (String) msg.obj;
                for (int i3 = this.mPlugins.size() - 1; i3 >= 0; i3--) {
                    PluginInfo<T> plugin2 = this.mPlugins.get(i3);
                    if (plugin2.mPackage.equals(pkg)) {
                        PluginInstanceManager.this.mMainHandler.obtainMessage(2, plugin2.mPlugin).sendToTarget();
                        this.mPlugins.remove(i3);
                    }
                }
            } else {
                super.handleMessage(msg);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleQueryPlugins(String pkgName) {
            Intent intent = new Intent(PluginInstanceManager.this.mAction);
            if (pkgName != null) {
                intent.setPackage(pkgName);
            }
            List<ResolveInfo> result = PluginInstanceManager.this.mPm.queryIntentServices(intent, 0);
            if (result.size() > 1 && !PluginInstanceManager.this.mAllowMultiple) {
                Log.w(PluginInstanceManager.TAG, "Multiple plugins found for " + PluginInstanceManager.this.mAction);
                return;
            }
            for (ResolveInfo info : result) {
                ComponentName name = new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name);
                PluginInfo<T> t = handleLoadPlugin(name);
                if (t != null) {
                    this.mPlugins.add(t);
                    PluginInstanceManager.this.mMainHandler.obtainMessage(1, t).sendToTarget();
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        protected PluginInfo<T> handleLoadPlugin(ComponentName component) {
            Plugin plugin;
            if (PluginInstanceManager.this.isDebuggable || PluginInstanceManager.this.isPluginWhitelisted(component)) {
                if (!PluginInstanceManager.this.mManager.getPluginEnabler().isEnabled(component)) {
                    return null;
                }
                String pkg = component.getPackageName();
                String cls = component.getClassName();
                try {
                    ApplicationInfo info = PluginInstanceManager.this.mPm.getApplicationInfo(pkg, 0);
                    if (PluginInstanceManager.this.mPm.checkPermission("com.android.systemui.permission.PLUGIN", pkg) == 0) {
                        ClassLoader classLoader = PluginInstanceManager.this.mManager.getClassLoader(info);
                        Context pluginContext = new PluginContextWrapper(PluginInstanceManager.this.mContext.createApplicationContext(info, 0), classLoader);
                        Class<?> pluginClass = Class.forName(cls, true, classLoader);
                        Plugin plugin2 = (Plugin) pluginClass.newInstance();
                        try {
                            VersionInfo version = checkVersion(pluginClass, plugin2, PluginInstanceManager.this.mVersion);
                            plugin = plugin2;
                            try {
                                return new PluginInfo<>(pkg, cls, plugin, pluginContext, version);
                            } catch (VersionInfo.InvalidVersionException e) {
                                e = e;
                                VersionInfo.InvalidVersionException e2 = e;
                                int icon = PluginInstanceManager.this.mContext.getResources().getIdentifier("tuner", "drawable", PluginInstanceManager.this.mContext.getPackageName());
                                int color = Resources.getSystem().getIdentifier("system_notification_accent_color", "color", SystemMediaRouteProvider.PACKAGE_NAME);
                                Notification.Builder nb = new Notification.Builder(PluginInstanceManager.this.mContext, PluginManager.NOTIFICATION_CHANNEL_ID).setStyle(new Notification.BigTextStyle()).setSmallIcon(icon).setWhen(0L).setShowWhen(false).setVisibility(1).setColor(PluginInstanceManager.this.mContext.getColor(color));
                                String label = cls;
                                try {
                                    label = PluginInstanceManager.this.mPm.getServiceInfo(component, 0).loadLabel(PluginInstanceManager.this.mPm).toString();
                                } catch (PackageManager.NameNotFoundException e3) {
                                }
                                if (!e2.isTooNew()) {
                                    Notification.Builder contentTitle = nb.setContentTitle("Plugin \"" + label + "\" is too old");
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Contact plugin developer to get an updated version.\n");
                                    sb.append(e2.getMessage());
                                    contentTitle.setContentText(sb.toString());
                                } else {
                                    Notification.Builder contentTitle2 = nb.setContentTitle("Plugin \"" + label + "\" is too new");
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("Check to see if an OTA is available.\n");
                                    sb2.append(e2.getMessage());
                                    contentTitle2.setContentText(sb2.toString());
                                }
                                Intent intent = new Intent("com.android.systemui.action.DISABLE_PLUGIN");
                                Intent i = intent.setData(Uri.parse("package://" + component.flattenToString()));
                                PendingIntent pi = PendingIntent.getBroadcast(PluginInstanceManager.this.mContext, 0, i, 0);
                                nb.addAction(new Notification.Action.Builder((Icon) null, "Disable plugin", pi).build());
                                ((NotificationManager) PluginInstanceManager.this.mContext.getSystemService(NotificationManager.class)).notifyAsUser(cls, 6, nb.build(), UserHandle.ALL);
                                Log.w(PluginInstanceManager.TAG, "Plugin has invalid interface version " + plugin.getVersion() + ", expected " + PluginInstanceManager.this.mVersion);
                                return null;
                            }
                        } catch (VersionInfo.InvalidVersionException e4) {
                            e = e4;
                            plugin = plugin2;
                        }
                    } else {
                        Log.d(PluginInstanceManager.TAG, "Plugin doesn't have permission: " + pkg);
                        return null;
                    }
                } catch (Throwable e5) {
                    Log.w(PluginInstanceManager.TAG, "Couldn't load plugin: " + pkg, e5);
                    return null;
                }
            } else {
                Log.w(PluginInstanceManager.TAG, "Plugin cannot be loaded on production build: " + component);
                return null;
            }
        }

        private VersionInfo checkVersion(Class<?> pluginClass, T plugin, VersionInfo version) throws VersionInfo.InvalidVersionException {
            VersionInfo pv = new VersionInfo().addClass(pluginClass);
            if (pv.hasVersionInfo()) {
                version.checkVersion(pv);
                return pv;
            }
            int fallbackVersion = plugin.getVersion();
            if (fallbackVersion != version.getDefaultVersion()) {
                throw new VersionInfo.InvalidVersionException("Invalid legacy version", false);
            }
            return null;
        }
    }

    /* loaded from: classes21.dex */
    public static class PluginContextWrapper extends ContextWrapper {
        private final ClassLoader mClassLoader;
        private LayoutInflater mInflater;

        public PluginContextWrapper(Context base, ClassLoader classLoader) {
            super(base);
            this.mClassLoader = classLoader;
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public ClassLoader getClassLoader() {
            return this.mClassLoader;
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public Object getSystemService(String name) {
            if ("layout_inflater".equals(name)) {
                if (this.mInflater == null) {
                    this.mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
                }
                return this.mInflater;
            }
            return getBaseContext().getSystemService(name);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static class PluginInfo<T> {
        private String mClass;
        String mPackage;
        T mPlugin;
        private final Context mPluginContext;
        private final VersionInfo mVersion;

        public PluginInfo(String pkg, String cls, T plugin, Context pluginContext, VersionInfo info) {
            this.mPlugin = plugin;
            this.mClass = cls;
            this.mPackage = pkg;
            this.mPluginContext = pluginContext;
            this.mVersion = info;
        }
    }
}
