package com.android.systemui.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.provider.Settings;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.shared.plugins.PluginManager;
import java.util.Arrays;
/* loaded from: classes21.dex */
public class NotificationChannels extends SystemUI {
    public static String ALERTS = PluginManager.NOTIFICATION_CHANNEL_ID;
    public static String SCREENSHOTS_LEGACY = "SCN";
    public static String SCREENSHOTS_HEADSUP = "SCN_HEADSUP";
    public static String GENERAL = "GEN";
    public static String STORAGE = "DSK";
    public static String TVPIP = "TPP";
    public static String BATTERY = "BAT";
    public static String HINTS = "HNT";

    public static void createAll(Context context) {
        int i;
        NotificationManager nm = (NotificationManager) context.getSystemService(NotificationManager.class);
        NotificationChannel batteryChannel = new NotificationChannel(BATTERY, context.getString(R.string.notification_channel_battery), 5);
        String soundPath = Settings.Global.getString(context.getContentResolver(), "low_battery_sound");
        batteryChannel.setSound(Uri.parse("file://" + soundPath), new AudioAttributes.Builder().setContentType(4).setUsage(10).build());
        batteryChannel.setBlockableSystem(true);
        NotificationChannel alerts = new NotificationChannel(ALERTS, context.getString(R.string.notification_channel_alerts), 4);
        NotificationChannel general = new NotificationChannel(GENERAL, context.getString(R.string.notification_channel_general), 1);
        String str = STORAGE;
        String string = context.getString(R.string.notification_channel_storage);
        if (isTv(context)) {
            i = 3;
        } else {
            i = 2;
        }
        NotificationChannel storage = new NotificationChannel(str, string, i);
        NotificationChannel hint = new NotificationChannel(HINTS, context.getString(R.string.notification_channel_hints), 3);
        nm.createNotificationChannels(Arrays.asList(alerts, general, storage, createScreenshotChannel(context.getString(R.string.notification_channel_screenshot), nm.getNotificationChannel(SCREENSHOTS_LEGACY)), batteryChannel, hint));
        nm.deleteNotificationChannel(SCREENSHOTS_LEGACY);
        if (isTv(context)) {
            nm.createNotificationChannel(new NotificationChannel(TVPIP, context.getString(R.string.notification_channel_tv_pip), 5));
        }
    }

    @VisibleForTesting
    static NotificationChannel createScreenshotChannel(String name, NotificationChannel legacySS) {
        NotificationChannel screenshotChannel = new NotificationChannel(SCREENSHOTS_HEADSUP, name, 4);
        screenshotChannel.setSound(null, new AudioAttributes.Builder().setUsage(5).build());
        screenshotChannel.setBlockableSystem(true);
        if (legacySS != null) {
            int userlock = legacySS.getUserLockedFields();
            if ((userlock & 4) != 0) {
                screenshotChannel.setImportance(legacySS.getImportance());
            }
            if ((userlock & 32) != 0) {
                screenshotChannel.setSound(legacySS.getSound(), legacySS.getAudioAttributes());
            }
            if ((userlock & 16) != 0) {
                screenshotChannel.setVibrationPattern(legacySS.getVibrationPattern());
            }
            if ((userlock & 8) != 0) {
                screenshotChannel.setLightColor(legacySS.getLightColor());
            }
        }
        return screenshotChannel;
    }

    @Override // com.android.systemui.SystemUI
    public void start() {
        createAll(this.mContext);
    }

    private static boolean isTv(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature("android.software.leanback");
    }
}
