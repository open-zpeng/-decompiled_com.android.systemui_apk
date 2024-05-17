package com.android.settingslib.inputmethod;

import android.content.Context;
import androidx.preference.SwitchPreference;
/* loaded from: classes20.dex */
public class SwitchWithNoTextPreference extends SwitchPreference {
    private static final String EMPTY_TEXT = "";

    public SwitchWithNoTextPreference(Context context) {
        super(context);
        setSwitchTextOn("");
        setSwitchTextOff("");
    }
}
