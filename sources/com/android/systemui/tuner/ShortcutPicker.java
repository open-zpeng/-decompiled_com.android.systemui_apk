package com.android.systemui.tuner;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.tuner.ShortcutParser;
import com.android.systemui.tuner.TunerService;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class ShortcutPicker extends PreferenceFragment implements TunerService.Tunable {
    private String mKey;
    private SelectablePreference mNonePreference;
    private final ArrayList<SelectablePreference> mSelectablePreferences = new ArrayList<>();
    private TunerService mTunerService;

    @Override // androidx.preference.PreferenceFragment
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final Context context = getPreferenceManager().getContext();
        final PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
        screen.setOrderingAsAdded(true);
        final PreferenceCategory otherApps = new PreferenceCategory(context);
        otherApps.setTitle(R.string.tuner_other_apps);
        this.mNonePreference = new SelectablePreference(context);
        this.mSelectablePreferences.add(this.mNonePreference);
        this.mNonePreference.setTitle(R.string.lockscreen_none);
        this.mNonePreference.setIcon(R.drawable.ic_remove_circle);
        screen.addPreference(this.mNonePreference);
        LauncherApps apps = (LauncherApps) getContext().getSystemService(LauncherApps.class);
        List<LauncherActivityInfo> activities = apps.getActivityList(null, Process.myUserHandle());
        screen.addPreference(otherApps);
        activities.forEach(new Consumer() { // from class: com.android.systemui.tuner.-$$Lambda$ShortcutPicker$S1ImsFAQfzG0QWSEvA0DqvnEIeY
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ShortcutPicker.this.lambda$onCreatePreferences$1$ShortcutPicker(context, screen, otherApps, (LauncherActivityInfo) obj);
            }
        });
        screen.removePreference(otherApps);
        for (int i = 0; i < otherApps.getPreferenceCount(); i++) {
            Preference p = otherApps.getPreference(0);
            otherApps.removePreference(p);
            p.setOrder(Integer.MAX_VALUE);
            screen.addPreference(p);
        }
        setPreferenceScreen(screen);
        this.mKey = getArguments().getString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT");
        this.mTunerService = (TunerService) Dependency.get(TunerService.class);
        this.mTunerService.addTunable(this, this.mKey);
    }

    public /* synthetic */ void lambda$onCreatePreferences$1$ShortcutPicker(final Context context, final PreferenceScreen screen, PreferenceCategory otherApps, final LauncherActivityInfo info) {
        try {
            List<ShortcutParser.Shortcut> shortcuts = new ShortcutParser(getContext(), info.getComponentName()).getShortcuts();
            AppPreference appPreference = new AppPreference(context, info);
            this.mSelectablePreferences.add(appPreference);
            if (shortcuts.size() != 0) {
                screen.addPreference(appPreference);
                shortcuts.forEach(new Consumer() { // from class: com.android.systemui.tuner.-$$Lambda$ShortcutPicker$UYdUITYIUaxXrzDZbzfvlRGD9eA
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ShortcutPicker.this.lambda$onCreatePreferences$0$ShortcutPicker(context, info, screen, (ShortcutParser.Shortcut) obj);
                    }
                });
                return;
            }
            otherApps.addPreference(appPreference);
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    public /* synthetic */ void lambda$onCreatePreferences$0$ShortcutPicker(Context context, LauncherActivityInfo info, PreferenceScreen screen, ShortcutParser.Shortcut shortcut) {
        ShortcutPreference shortcutPref = new ShortcutPreference(context, shortcut, info.getLabel());
        this.mSelectablePreferences.add(shortcutPref);
        screen.addPreference(shortcutPref);
    }

    @Override // androidx.preference.PreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        this.mTunerService.setValue(this.mKey, preference.toString());
        getActivity().onBackPressed();
        return true;
    }

    @Override // android.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (LockscreenFragment.LOCKSCREEN_LEFT_BUTTON.equals(this.mKey)) {
            getActivity().setTitle(R.string.lockscreen_shortcut_left);
        } else {
            getActivity().setTitle(R.string.lockscreen_shortcut_right);
        }
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mTunerService.removeTunable(this);
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        final String v = newValue != null ? newValue : "";
        this.mSelectablePreferences.forEach(new Consumer() { // from class: com.android.systemui.tuner.-$$Lambda$ShortcutPicker$i1fIZ726bN-ySXwulncRN12T1Qg
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                r2.setChecked(v.equals(((SelectablePreference) obj).toString()));
            }
        });
    }

    /* loaded from: classes21.dex */
    private static class AppPreference extends SelectablePreference {
        private boolean mBinding;
        private final LauncherActivityInfo mInfo;

        public AppPreference(Context context, LauncherActivityInfo info) {
            super(context);
            this.mInfo = info;
            setTitle(context.getString(R.string.tuner_launch_app, info.getLabel()));
            setSummary(context.getString(R.string.tuner_app, info.getLabel()));
        }

        @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder holder) {
            this.mBinding = true;
            if (getIcon() == null) {
                setIcon(this.mInfo.getBadgedIcon(getContext().getResources().getConfiguration().densityDpi));
            }
            this.mBinding = false;
            super.onBindViewHolder(holder);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.preference.Preference
        public void notifyChanged() {
            if (this.mBinding) {
                return;
            }
            super.notifyChanged();
        }

        @Override // com.android.systemui.tuner.SelectablePreference, androidx.preference.Preference
        public String toString() {
            return this.mInfo.getComponentName().flattenToString();
        }
    }

    /* loaded from: classes21.dex */
    private static class ShortcutPreference extends SelectablePreference {
        private boolean mBinding;
        private final ShortcutParser.Shortcut mShortcut;

        public ShortcutPreference(Context context, ShortcutParser.Shortcut shortcut, CharSequence appLabel) {
            super(context);
            this.mShortcut = shortcut;
            setTitle(shortcut.label);
            setSummary(context.getString(R.string.tuner_app, appLabel));
        }

        @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder holder) {
            this.mBinding = true;
            if (getIcon() == null) {
                setIcon(this.mShortcut.icon.loadDrawable(getContext()));
            }
            this.mBinding = false;
            super.onBindViewHolder(holder);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.preference.Preference
        public void notifyChanged() {
            if (this.mBinding) {
                return;
            }
            super.notifyChanged();
        }

        @Override // com.android.systemui.tuner.SelectablePreference, androidx.preference.Preference
        public String toString() {
            return this.mShortcut.toString();
        }
    }
}
