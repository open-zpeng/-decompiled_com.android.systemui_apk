package com.android.systemui.tuner;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.fragments.FragmentService;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class TunerActivity extends Activity implements PreferenceFragment.OnPreferenceStartFragmentCallback, PreferenceFragment.OnPreferenceStartScreenCallback {
    private static final String TAG_TUNER = "tuner";

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(Integer.MIN_VALUE);
        boolean showDemoMode = true;
        requestWindowFeature(1);
        setContentView(R.layout.tuner_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar != null) {
            setActionBar(toolbar);
        }
        Dependency.initDependencies(SystemUIFactory.getInstance().getRootComponent());
        if (getFragmentManager().findFragmentByTag(TAG_TUNER) == null) {
            String action = getIntent().getAction();
            showDemoMode = (action == null || !action.equals("com.android.settings.action.DEMO_MODE")) ? false : false;
            PreferenceFragment fragment = showDemoMode ? new DemoModeFragment() : new TunerFragment();
            getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, TAG_TUNER).commit();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        Dependency.destroy(FragmentService.class, new Consumer() { // from class: com.android.systemui.tuner.-$$Lambda$TunerActivity$RI23eCWQLUIRemsdYo0hJRYd5ug
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((FragmentService) obj).destroyAll();
            }
        });
        Dependency.clearDependencies();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (!getFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }

    @Override // androidx.preference.PreferenceFragment.OnPreferenceStartFragmentCallback
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        try {
            Class<?> cls = Class.forName(pref.getFragment());
            Fragment fragment = (Fragment) cls.newInstance();
            Bundle b = new Bundle(1);
            b.putString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT", pref.getKey());
            fragment.setArguments(b);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            setTitle(pref.getTitle());
            transaction.replace(R.id.content_frame, fragment);
            transaction.addToBackStack("PreferenceFragment");
            transaction.commit();
            return true;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Log.d("TunerActivity", "Problem launching fragment", e);
            return false;
        }
    }

    @Override // androidx.preference.PreferenceFragment.OnPreferenceStartScreenCallback
    public boolean onPreferenceStartScreen(PreferenceFragment caller, PreferenceScreen pref) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        SubSettingsFragment fragment = new SubSettingsFragment();
        Bundle b = new Bundle(1);
        b.putString("androidx.preference.PreferenceFragmentCompat.PREFERENCE_ROOT", pref.getKey());
        fragment.setArguments(b);
        fragment.setTargetFragment(caller, 0);
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack("PreferenceFragment");
        transaction.commit();
        return true;
    }

    /* loaded from: classes21.dex */
    public static class SubSettingsFragment extends PreferenceFragment {
        private PreferenceScreen mParentScreen;

        @Override // androidx.preference.PreferenceFragment
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            this.mParentScreen = (PreferenceScreen) ((PreferenceFragment) getTargetFragment()).getPreferenceScreen().findPreference(rootKey);
            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext());
            setPreferenceScreen(screen);
            while (this.mParentScreen.getPreferenceCount() > 0) {
                Preference p = this.mParentScreen.getPreference(0);
                this.mParentScreen.removePreference(p);
                screen.addPreference(p);
            }
        }

        @Override // android.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            PreferenceScreen screen = getPreferenceScreen();
            while (screen.getPreferenceCount() > 0) {
                Preference p = screen.getPreference(0);
                screen.removePreference(p);
                this.mParentScreen.addPreference(p);
            }
        }
    }
}
