package com.android.systemui.tuner;

import android.os.Bundle;
import androidx.preference.PreferenceFragment;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class OtherPrefs extends PreferenceFragment {
    @Override // androidx.preference.PreferenceFragment
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.other_settings);
    }
}
