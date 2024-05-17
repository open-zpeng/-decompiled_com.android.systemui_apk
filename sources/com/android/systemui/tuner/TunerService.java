package com.android.systemui.tuner;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.SystemUIDialog;
/* loaded from: classes21.dex */
public abstract class TunerService {
    public static final String ACTION_CLEAR = "com.android.systemui.action.CLEAR_TUNER";

    /* loaded from: classes21.dex */
    public interface Tunable {
        void onTuningChanged(String str, String str2);
    }

    public abstract void addTunable(Tunable tunable, String... strArr);

    public abstract void clearAll();

    public abstract void destroy();

    public abstract int getValue(String str, int i);

    public abstract String getValue(String str);

    public abstract String getValue(String str, String str2);

    public abstract void removeTunable(Tunable tunable);

    public abstract void setValue(String str, int i);

    public abstract void setValue(String str, String str2);

    private static Context userContext(Context context) {
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, new UserHandle(ActivityManager.getCurrentUser()));
        } catch (PackageManager.NameNotFoundException e) {
            return context;
        }
    }

    public static final void setTunerEnabled(Context context, boolean enabled) {
        int i;
        PackageManager packageManager = userContext(context).getPackageManager();
        ComponentName componentName = new ComponentName(context, TunerActivity.class);
        if (enabled) {
            i = 1;
        } else {
            i = 2;
        }
        packageManager.setComponentEnabledSetting(componentName, i, 1);
    }

    public static final boolean isTunerEnabled(Context context) {
        return userContext(context).getPackageManager().getComponentEnabledSetting(new ComponentName(context, TunerActivity.class)) == 1;
    }

    /* loaded from: classes21.dex */
    public static class ClearReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (TunerService.ACTION_CLEAR.equals(intent.getAction())) {
                ((TunerService) Dependency.get(TunerService.class)).clearAll();
            }
        }
    }

    public static final void showResetRequest(final Context context, final Runnable onDisabled) {
        SystemUIDialog dialog = new SystemUIDialog(context);
        dialog.setShowForAllUsers(true);
        dialog.setMessage(R.string.remove_from_settings_prompt);
        dialog.setButton(-2, context.getString(R.string.cancel), (DialogInterface.OnClickListener) null);
        dialog.setButton(-1, context.getString(R.string.guest_exit_guest_dialog_remove), new DialogInterface.OnClickListener() { // from class: com.android.systemui.tuner.TunerService.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog2, int which) {
                context.sendBroadcast(new Intent(TunerService.ACTION_CLEAR));
                TunerService.setTunerEnabled(context, false);
                Settings.Secure.putInt(context.getContentResolver(), TunerFragment.SETTING_SEEN_TUNER_WARNING, 0);
                Runnable runnable = onDisabled;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        dialog.show();
    }

    public static boolean parseIntegerSwitch(String value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value) != 0;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
