package com.android.systemui.util.leak;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Debug;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.google.android.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class LeakReporter {
    public static final String FILEPROVIDER_AUTHORITY = "com.android.systemui.fileprovider";
    static final String LEAK_DIR = "leak";
    static final String LEAK_DUMP = "leak.dump";
    static final String LEAK_HPROF = "leak.hprof";
    static final String TAG = "LeakReporter";
    private final Context mContext;
    private final LeakDetector mLeakDetector;
    private final String mLeakReportEmail;

    @Inject
    public LeakReporter(Context context, LeakDetector leakDetector, @Named("leak_report_email") String leakReportEmail) {
        this.mContext = context;
        this.mLeakDetector = leakDetector;
        this.mLeakReportEmail = leakReportEmail;
    }

    public void dumpLeak(int garbageCount) {
        try {
            File leakDir = new File(this.mContext.getCacheDir(), LEAK_DIR);
            leakDir.mkdir();
            File hprofFile = new File(leakDir, LEAK_HPROF);
            Debug.dumpHprofData(hprofFile.getAbsolutePath());
            File dumpFile = new File(leakDir, LEAK_DUMP);
            FileOutputStream fos = new FileOutputStream(dumpFile);
            PrintWriter w = new PrintWriter(fos);
            w.print("Build: ");
            w.println(SystemProperties.get("ro.build.description"));
            w.println();
            w.flush();
            this.mLeakDetector.dump(fos.getFD(), w, new String[0]);
            w.close();
            fos.close();
            NotificationManager notiMan = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(LEAK_DIR, "Leak Alerts", 4);
            channel.enableVibration(true);
            notiMan.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this.mContext, channel.getId()).setAutoCancel(true).setShowWhen(true).setContentTitle("Memory Leak Detected").setContentText(String.format("SystemUI has detected %d leaked objects. Tap to send", Integer.valueOf(garbageCount))).setSmallIcon(17303526).setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, getIntent(hprofFile, dumpFile), 201326592, null, UserHandle.CURRENT));
            notiMan.notify(TAG, 0, builder.build());
        } catch (IOException e) {
            Log.e(TAG, "Couldn't dump heap for leak", e);
        }
    }

    private Intent getIntent(File hprofFile, File dumpFile) {
        Uri dumpUri = FileProvider.getUriForFile(this.mContext, FILEPROVIDER_AUTHORITY, dumpFile);
        Uri hprofUri = FileProvider.getUriForFile(this.mContext, FILEPROVIDER_AUTHORITY, hprofFile);
        Intent intent = new Intent("android.intent.action.SEND_MULTIPLE");
        intent.addFlags(1);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.leakreport");
        intent.putExtra("android.intent.extra.SUBJECT", "SystemUI leak report");
        StringBuilder messageBody = new StringBuilder("Build info: ").append(SystemProperties.get("ro.build.description"));
        intent.putExtra("android.intent.extra.TEXT", messageBody.toString());
        ClipData clipData = new ClipData(null, new String[]{"application/vnd.android.leakreport"}, new ClipData.Item(null, null, null, dumpUri));
        ArrayList<Uri> attachments = Lists.newArrayList(new Uri[]{dumpUri});
        clipData.addItem(new ClipData.Item(null, null, null, hprofUri));
        attachments.add(hprofUri);
        intent.setClipData(clipData);
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", attachments);
        String leakReportEmail = this.mLeakReportEmail;
        if (leakReportEmail != null) {
            intent.putExtra("android.intent.extra.EMAIL", new String[]{leakReportEmail});
        }
        return intent;
    }
}
