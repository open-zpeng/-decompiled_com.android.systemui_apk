package com.android.systemui.tuner;

import android.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import com.android.systemui.tuner.CustomListPreference;
/* loaded from: classes21.dex */
public abstract class TunerPreferenceFragment extends PreferenceFragment {
    @Override // androidx.preference.PreferenceFragment, androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment f = null;
        if (preference instanceof CustomListPreference) {
            f = CustomListPreference.CustomListPreferenceDialogFragment.newInstance(preference.getKey());
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
        f.setTargetFragment(this, 0);
        f.show(getFragmentManager(), "dialog_preference");
    }
}
