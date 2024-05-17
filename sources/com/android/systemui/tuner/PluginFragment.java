package com.android.systemui.tuner;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.view.View;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.android.internal.util.ArrayUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.plugins.PluginEnablerImpl;
import com.android.systemui.shared.plugins.PluginEnabler;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.plugins.PluginPrefs;
import com.android.systemui.tuner.PluginFragment;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class PluginFragment extends PreferenceFragment {
    public static final String ACTION_PLUGIN_SETTINGS = "com.android.systemui.action.PLUGIN_SETTINGS";
    private PluginEnabler mPluginEnabler;
    private PluginPrefs mPluginPrefs;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.tuner.PluginFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            PluginFragment.this.loadPrefs();
        }
    };

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        getContext().registerReceiver(this.mReceiver, filter);
        getContext().registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(this.mReceiver);
    }

    @Override // androidx.preference.PreferenceFragment
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.mPluginEnabler = new PluginEnablerImpl(getContext());
        loadPrefs();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadPrefs() {
        final PluginManager manager = (PluginManager) Dependency.get(PluginManager.class);
        final PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getContext());
        screen.setOrderingAsAdded(false);
        final Context prefContext = getPreferenceManager().getContext();
        this.mPluginPrefs = new PluginPrefs(getContext());
        PackageManager pm = getContext().getPackageManager();
        Set<String> pluginActions = this.mPluginPrefs.getPluginList();
        final ArrayMap<String, ArraySet<String>> plugins = new ArrayMap<>();
        for (String action : pluginActions) {
            String name = toName(action);
            List<ResolveInfo> result = pm.queryIntentServices(new Intent(action), 512);
            for (ResolveInfo info : result) {
                String packageName = info.serviceInfo.packageName;
                if (!plugins.containsKey(packageName)) {
                    plugins.put(packageName, new ArraySet<>());
                }
                plugins.get(packageName).add(name);
            }
        }
        List<PackageInfo> apps = pm.getPackagesHoldingPermissions(new String[]{"com.android.systemui.permission.PLUGIN"}, 516);
        apps.forEach(new Consumer() { // from class: com.android.systemui.tuner.-$$Lambda$PluginFragment$iW8kXrJfaof7fDZHqMxR_RNftYk
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PluginFragment.this.lambda$loadPrefs$0$PluginFragment(plugins, manager, prefContext, screen, (PackageInfo) obj);
            }
        });
        setPreferenceScreen(screen);
    }

    public /* synthetic */ void lambda$loadPrefs$0$PluginFragment(ArrayMap plugins, PluginManager manager, Context prefContext, PreferenceScreen screen, PackageInfo app) {
        if (!plugins.containsKey(app.packageName) || ArrayUtils.contains(manager.getWhitelistedPlugins(), app.packageName)) {
            return;
        }
        SwitchPreference pref = new PluginPreference(prefContext, app, this.mPluginEnabler);
        pref.setSummary("Plugins: " + toString((ArraySet) plugins.get(app.packageName)));
        screen.addPreference(pref);
    }

    private String toString(ArraySet<String> plugins) {
        StringBuilder b = new StringBuilder();
        Iterator<String> it = plugins.iterator();
        while (it.hasNext()) {
            String string = it.next();
            if (b.length() != 0) {
                b.append(", ");
            }
            b.append(string);
        }
        return b.toString();
    }

    private String toName(String action) {
        String[] split;
        String str = action.replace("com.android.systemui.action.PLUGIN_", "");
        StringBuilder b = new StringBuilder();
        for (String s : str.split("_")) {
            if (b.length() != 0) {
                b.append(' ');
            }
            b.append(s.substring(0, 1));
            b.append(s.substring(1).toLowerCase());
        }
        return b.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class PluginPreference extends SwitchPreference {
        private final boolean mHasSettings;
        private final PackageInfo mInfo;
        private final PluginEnabler mPluginEnabler;

        public PluginPreference(Context prefContext, PackageInfo info, PluginEnabler pluginEnabler) {
            super(prefContext);
            PackageManager pm = prefContext.getPackageManager();
            this.mHasSettings = pm.resolveActivity(new Intent(PluginFragment.ACTION_PLUGIN_SETTINGS).setPackage(info.packageName), 0) != null;
            this.mInfo = info;
            this.mPluginEnabler = pluginEnabler;
            setTitle(info.applicationInfo.loadLabel(pm));
            setChecked(isPluginEnabled());
            setWidgetLayoutResource(R.layout.tuner_widget_settings_switch);
        }

        private boolean isPluginEnabled() {
            for (int i = 0; i < this.mInfo.services.length; i++) {
                ComponentName componentName = new ComponentName(this.mInfo.packageName, this.mInfo.services[i].name);
                if (!this.mPluginEnabler.isEnabled(componentName)) {
                    return false;
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.preference.Preference
        public boolean persistBoolean(boolean isEnabled) {
            boolean shouldSendBroadcast = false;
            for (int i = 0; i < this.mInfo.services.length; i++) {
                ComponentName componentName = new ComponentName(this.mInfo.packageName, this.mInfo.services[i].name);
                if (this.mPluginEnabler.isEnabled(componentName) != isEnabled) {
                    if (isEnabled) {
                        this.mPluginEnabler.setEnabled(componentName);
                    } else {
                        this.mPluginEnabler.setDisabled(componentName, 1);
                    }
                    shouldSendBroadcast = true;
                }
            }
            if (shouldSendBroadcast) {
                String pkg = this.mInfo.packageName;
                Intent intent = new Intent(PluginManager.PLUGIN_CHANGED, pkg != null ? Uri.fromParts("package", pkg, null) : null);
                getContext().sendBroadcast(intent);
            }
            return true;
        }

        @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder holder) {
            super.onBindViewHolder(holder);
            holder.findViewById(R.id.settings).setVisibility(this.mHasSettings ? 0 : 8);
            holder.findViewById(R.id.divider).setVisibility(this.mHasSettings ? 0 : 8);
            holder.findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.tuner.-$$Lambda$PluginFragment$PluginPreference$Xt_y65tw1Tc7XykRWrNNbIDklTs
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    PluginFragment.PluginPreference.this.lambda$onBindViewHolder$0$PluginFragment$PluginPreference(view);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.systemui.tuner.-$$Lambda$PluginFragment$PluginPreference$hyhKFHxbkbEXGxqXV7_N3Il_7XE
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    return PluginFragment.PluginPreference.this.lambda$onBindViewHolder$1$PluginFragment$PluginPreference(view);
                }
            });
        }

        public /* synthetic */ void lambda$onBindViewHolder$0$PluginFragment$PluginPreference(View v) {
            ResolveInfo result = v.getContext().getPackageManager().resolveActivity(new Intent(PluginFragment.ACTION_PLUGIN_SETTINGS).setPackage(this.mInfo.packageName), 0);
            if (result != null) {
                v.getContext().startActivity(new Intent().setComponent(new ComponentName(result.activityInfo.packageName, result.activityInfo.name)));
            }
        }

        public /* synthetic */ boolean lambda$onBindViewHolder$1$PluginFragment$PluginPreference(View v) {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", this.mInfo.packageName, null));
            getContext().startActivity(intent);
            return true;
        }
    }
}
