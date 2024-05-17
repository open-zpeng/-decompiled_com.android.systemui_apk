package com.android.systemui.statusbar.policy;

import android.sysprop.VoldProperties;
/* loaded from: classes21.dex */
public class EncryptionHelper {
    public static final boolean IS_DATA_ENCRYPTED = isDataEncrypted();

    private static boolean isDataEncrypted() {
        String voldState = (String) VoldProperties.decrypt().orElse("");
        return "1".equals(voldState) || "trigger_restart_min_framework".equals(voldState);
    }
}
