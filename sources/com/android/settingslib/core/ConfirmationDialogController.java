package com.android.settingslib.core;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
/* loaded from: classes20.dex */
public interface ConfirmationDialogController {
    void dismissConfirmationDialog();

    String getPreferenceKey();

    boolean isConfirmationDialogShowing();

    void showConfirmationDialog(@Nullable Preference preference);
}
