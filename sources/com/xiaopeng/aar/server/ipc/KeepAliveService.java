package com.xiaopeng.aar.server.ipc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.xiaopeng.aar.R;
/* loaded from: classes22.dex */
public class KeepAliveService extends Service {
    private static final String CRITICAL = "Critical";

    @Override // android.app.Service
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, buildForegroundNotification());
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
    }

    private Notification buildForegroundNotification() {
        return new Notification.Builder(this, CRITICAL).setContentTitle("KeepAliveService").setSmallIcon(R.drawable.x_ic_logo).build();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CRITICAL, CRITICAL, 3);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
