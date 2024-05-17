package com.android.systemui.tuner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R;
import com.android.systemui.shared.plugins.PluginPrefs;
/* loaded from: classes21.dex */
public class TunerFragment extends PreferenceFragment {
    private static final String KEY_BATTERY_PCT = "battery_pct";
    private static final String KEY_PLUGINS = "plugins";
    private static final int MENU_REMOVE = 2;
    public static final String SETTING_SEEN_TUNER_WARNING = "seen_tuner_warning";
    private static final String TAG = "TunerFragment";
    private static final String WARNING_TAG = "tuner_warning";
    private static final CharSequence KEY_DOZE = "doze";
    private static final String[] DEBUG_ONLY = {"nav_bar", "lockscreen", "picture_in_picture"};

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override // android.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override // androidx.preference.PreferenceFragment
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.tuner_prefs);
        if (!PluginPrefs.hasPlugins(getContext())) {
            getPreferenceScreen().removePreference(findPreference(KEY_PLUGINS));
        }
        if (!alwaysOnAvailable()) {
            getPreferenceScreen().removePreference(findPreference(KEY_DOZE));
        }
        if (!Build.IS_DEBUGGABLE) {
            int i = 0;
            while (true) {
                String[] strArr = DEBUG_ONLY;
                if (i >= strArr.length) {
                    break;
                }
                Preference preference = findPreference(strArr[i]);
                if (preference != null) {
                    getPreferenceScreen().removePreference(preference);
                }
                i++;
            }
        }
        if (Settings.Secure.getInt(getContext().getContentResolver(), SETTING_SEEN_TUNER_WARNING, 0) == 0 && getFragmentManager().findFragmentByTag(WARNING_TAG) == null) {
            new TunerWarningFragment().show(getFragmentManager(), WARNING_TAG);
        }
    }

    private boolean alwaysOnAvailable() {
        return new AmbientDisplayConfiguration(getContext()).alwaysOnAvailable();
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.system_ui_tuner);
        MetricsLogger.visibility(getContext(), 227, true);
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        MetricsLogger.visibility(getContext(), 227, false);
    }

    @Override // android.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 2, 0, R.string.remove_from_settings);
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 2) {
            TunerService.showResetRequest(getContext(), new Runnable() { // from class: com.android.systemui.tuner.TunerFragment.1
                @Override // java.lang.Runnable
                public void run() {
                    if (TunerFragment.this.getActivity() != null) {
                        TunerFragment.this.getActivity().finish();
                    }
                }
            });
            return true;
        } else if (itemId == 16908332) {
            getActivity().finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /* loaded from: classes21.dex */
    public static class TunerWarningFragment extends DialogFragment {
        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getContext()).setTitle(R.string.tuner_warning_title).setMessage(R.string.tuner_warning).setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() { // from class: com.android.systemui.tuner.TunerFragment.TunerWarningFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    Settings.Secure.putInt(TunerWarningFragment.this.getContext().getContentResolver(), TunerFragment.SETTING_SEEN_TUNER_WARNING, 1);
                }
            }).show();
        }
    }
}
