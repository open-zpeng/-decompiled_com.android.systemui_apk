package com.xiaopeng.xui.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.xiaopeng.xui.Xui;
@Deprecated
/* loaded from: classes25.dex */
public class XOsd {
    public static final int ID_SHARED_PRIMARY = 0;
    public static final int ID_SHARED_SECONDARY = 1;

    public static void showOsd(@NonNull CharSequence title, Icon icon, int progress, int progressMin, int progressMax) {
        showOsd(title, null, null, icon, null, progress, progressMin, progressMax);
    }

    public static void showOsd(@NonNull CharSequence title, Icon icon, CharSequence content) {
        showOsd(title, null, null, icon, content, 0, 0, 0);
    }

    public static void showOsd(@NonNull CharSequence title, Icon left, Icon right, Icon icon, CharSequence content) {
        showOsd(title, left, right, icon, content, 0, 0, 0);
    }

    public static void showOsd(@NonNull CharSequence title, Icon left, Icon right, Icon icon, CharSequence content, int progress, int progressMin, int progressMax) {
        showOsd(title, left, right, icon, content, progress, progressMin, progressMax, -1);
    }

    public static void showOsd(@NonNull CharSequence title, Icon left, Icon right, Icon icon, CharSequence content, int progress, int progressMin, int progressMax, int sharedId) {
        if (!TextUtils.isEmpty(title) && Xui.getContext() != null) {
            NotificationManager manager = (NotificationManager) Xui.getContext().getSystemService("notification");
            NotificationChannel channel = new NotificationChannel("channel_id_osd", "channel_name_osd", 3);
            manager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(Xui.getContext(), "channel_id_osd");
            builder.setSmallIcon(17301595);
            Bundle extra = builder.getExtras();
            extra.putCharSequence("android.osd.title", title);
            extra.putParcelable("android.osd.title.icon.left", left);
            extra.putParcelable("android.osd.title.icon.right", right);
            extra.putParcelable("android.osd.icon", icon);
            extra.putCharSequence("android.osd.content", content);
            extra.putInt("android.osd.progress", progress);
            extra.putInt("android.osd.progress.min", progressMin);
            extra.putInt("android.osd.progress.max", progressMax);
            extra.putInt("android.displayFlag", 2);
            if (sharedId != -1) {
                extra.putInt("android.osd.shared.id", sharedId);
            }
            builder.setExtras(extra);
            manager.notify(100, builder.build());
        }
    }
}
