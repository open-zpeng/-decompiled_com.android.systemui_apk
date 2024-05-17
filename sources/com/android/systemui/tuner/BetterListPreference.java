package com.android.systemui.tuner;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.ListPreference;
/* loaded from: classes21.dex */
public class BetterListPreference extends ListPreference {
    private CharSequence mSummary;

    public BetterListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // androidx.preference.ListPreference, androidx.preference.Preference
    public void setSummary(CharSequence summary) {
        super.setSummary(summary);
        this.mSummary = summary;
    }

    @Override // androidx.preference.ListPreference, androidx.preference.Preference
    public CharSequence getSummary() {
        return this.mSummary;
    }
}
