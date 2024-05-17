package com.android.systemui.plugins;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.shared.plugins.PluginEnabler;
/* loaded from: classes21.dex */
public class PluginEnablerImpl implements PluginEnabler {
    private static final String CRASH_DISABLED_PLUGINS_PREF_FILE = "auto_disabled_plugins_prefs";
    private final SharedPreferences mAutoDisabledPrefs;
    private PackageManager mPm;

    public PluginEnablerImpl(Context context) {
        this(context, context.getPackageManager());
    }

    @VisibleForTesting
    public PluginEnablerImpl(Context context, PackageManager pm) {
        this.mAutoDisabledPrefs = context.getSharedPreferences(CRASH_DISABLED_PLUGINS_PREF_FILE, 0);
        this.mPm = pm;
    }

    @Override // com.android.systemui.shared.plugins.PluginEnabler
    public void setEnabled(ComponentName component) {
        setDisabled(component, 0);
    }

    @Override // com.android.systemui.shared.plugins.PluginEnabler
    public void setDisabled(ComponentName component, @PluginEnabler.DisableReason int reason) {
        int desiredState;
        boolean enabled = reason == 0;
        if (enabled) {
            desiredState = 1;
        } else {
            desiredState = 2;
        }
        this.mPm.setComponentEnabledSetting(component, desiredState, 1);
        if (enabled) {
            this.mAutoDisabledPrefs.edit().remove(component.flattenToString()).apply();
        } else {
            this.mAutoDisabledPrefs.edit().putInt(component.flattenToString(), reason).apply();
        }
    }

    @Override // com.android.systemui.shared.plugins.PluginEnabler
    public boolean isEnabled(ComponentName component) {
        return this.mPm.getComponentEnabledSetting(component) != 2;
    }

    @Override // com.android.systemui.shared.plugins.PluginEnabler
    @PluginEnabler.DisableReason
    public int getDisableReason(ComponentName componentName) {
        if (isEnabled(componentName)) {
            return 0;
        }
        return this.mAutoDisabledPrefs.getInt(componentName.flattenToString(), 1);
    }
}
