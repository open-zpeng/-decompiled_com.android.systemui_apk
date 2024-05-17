package com.android.systemui.chooser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
/* loaded from: classes21.dex */
public class ChooserHelper {
    private static final String TAG = "ChooserHelper";

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void onChoose(Activity activity) {
        Intent thisIntent = activity.getIntent();
        Bundle thisExtras = thisIntent.getExtras();
        Intent chosenIntent = (Intent) thisIntent.getParcelableExtra("android.intent.extra.INTENT");
        Bundle options = (Bundle) thisIntent.getParcelableExtra("android.app.extra.OPTIONS");
        IBinder permissionToken = thisExtras.getBinder("android.app.extra.PERMISSION_TOKEN");
        boolean ignoreTargetSecurity = thisIntent.getBooleanExtra("android.app.extra.EXTRA_IGNORE_TARGET_SECURITY", false);
        int userId = thisIntent.getIntExtra("android.intent.extra.USER_ID", -1);
        StrictMode.disableDeathOnFileUriExposure();
        try {
            try {
                activity.startActivityAsCaller(chosenIntent, options, permissionToken, ignoreTargetSecurity, userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            StrictMode.enableDeathOnFileUriExposure();
        }
    }
}
