package com.android.systemui.util.leak;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.util.leak.GarbageMonitor;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/* loaded from: classes21.dex */
public class DumpTruck {
    private static final int BUFSIZ = 1048576;
    private static final String FILEPROVIDER_AUTHORITY = "com.android.systemui.fileprovider";
    private static final String FILEPROVIDER_PATH = "leak";
    private static final String TAG = "DumpTruck";
    final StringBuilder body = new StringBuilder();
    private final Context context;
    private Uri hprofUri;
    private long pss;

    public DumpTruck(Context context) {
        this.context = context;
    }

    public DumpTruck captureHeaps(int[] pids) {
        DumpTruck dumpTruck;
        DumpTruck dumpTruck2 = this;
        GarbageMonitor gm = (GarbageMonitor) Dependency.get(GarbageMonitor.class);
        File dumpDir = new File(dumpTruck2.context.getCacheDir(), FILEPROVIDER_PATH);
        dumpDir.mkdirs();
        dumpTruck2.hprofUri = null;
        dumpTruck2.body.setLength(0);
        StringBuilder sb = dumpTruck2.body;
        sb.append("Build: ");
        sb.append(Build.DISPLAY);
        sb.append("\n\nProcesses:\n");
        ArrayList<String> paths = new ArrayList<>();
        int myPid = Process.myPid();
        int[] pids_copy = Arrays.copyOf(pids, pids.length);
        int length = pids_copy.length;
        int i = 0;
        while (i < length) {
            int pid = pids_copy[i];
            StringBuilder sb2 = dumpTruck2.body;
            sb2.append("  pid ");
            sb2.append(pid);
            if (gm == null) {
                dumpTruck = dumpTruck2;
            } else {
                GarbageMonitor.ProcessMemInfo info = gm.getMemInfo(pid);
                if (info == null) {
                    dumpTruck = dumpTruck2;
                } else {
                    StringBuilder sb3 = dumpTruck2.body;
                    sb3.append(NavigationBarInflaterView.KEY_IMAGE_DELIM);
                    sb3.append(" up=");
                    sb3.append(info.getUptime());
                    sb3.append(" pss=");
                    sb3.append(info.currentPss);
                    sb3.append(" uss=");
                    sb3.append(info.currentUss);
                    dumpTruck = this;
                    dumpTruck.pss = info.currentPss;
                }
            }
            if (pid == myPid) {
                String path = new File(dumpDir, String.format("heap-%d.ahprof", Integer.valueOf(pid))).getPath();
                Log.v(TAG, "Dumping memory info for process " + pid + " to " + path);
                try {
                    Debug.dumpHprofData(path);
                    paths.add(path);
                    dumpTruck.body.append(" (hprof attached)");
                } catch (IOException e) {
                    Log.e(TAG, "error dumping memory:", e);
                    StringBuilder sb4 = dumpTruck.body;
                    sb4.append("\n** Could not dump heap: \n");
                    sb4.append(e.toString());
                    sb4.append("\n");
                }
            }
            dumpTruck.body.append("\n");
            i++;
            dumpTruck2 = dumpTruck;
        }
        DumpTruck dumpTruck3 = dumpTruck2;
        try {
            String zipfile = new File(dumpDir, String.format("hprof-%d.zip", Long.valueOf(System.currentTimeMillis()))).getCanonicalPath();
            if (zipUp(zipfile, paths)) {
                File pathFile = new File(zipfile);
                dumpTruck3.hprofUri = FileProvider.getUriForFile(dumpTruck3.context, "com.android.systemui.fileprovider", pathFile);
                Log.v(TAG, "Heap dump accessible at URI: " + dumpTruck3.hprofUri);
            }
        } catch (IOException e2) {
            Log.e(TAG, "unable to zip up heapdumps", e2);
            StringBuilder sb5 = dumpTruck3.body;
            sb5.append("\n** Could not zip up files: \n");
            sb5.append(e2.toString());
            sb5.append("\n");
        }
        return dumpTruck3;
    }

    public Uri getDumpUri() {
        return this.hprofUri;
    }

    public Intent createShareIntent() {
        Intent shareIntent = new Intent("android.intent.action.SEND_MULTIPLE");
        shareIntent.addFlags(268435456);
        shareIntent.addFlags(1);
        shareIntent.putExtra("android.intent.extra.SUBJECT", String.format("SystemUI memory dump (pss=%dM)", Long.valueOf(this.pss / 1024)));
        shareIntent.putExtra("android.intent.extra.TEXT", this.body.toString());
        if (this.hprofUri != null) {
            ArrayList<Uri> uriList = new ArrayList<>();
            uriList.add(this.hprofUri);
            shareIntent.setType("application/zip");
            shareIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", uriList);
            ClipData clipdata = new ClipData(new ClipDescription("content", new String[]{"text/plain"}), new ClipData.Item(this.hprofUri));
            shareIntent.setClipData(clipdata);
            shareIntent.addFlags(1);
        }
        return shareIntent;
    }

    private static boolean zipUp(String zipfilePath, ArrayList<String> paths) {
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipfilePath));
            byte[] buf = new byte[1048576];
            Iterator<String> it = paths.iterator();
            while (it.hasNext()) {
                String filename = it.next();
                InputStream is = new BufferedInputStream(new FileInputStream(filename));
                ZipEntry entry = new ZipEntry(filename);
                zos.putNextEntry(entry);
                while (true) {
                    int len = is.read(buf, 0, 1048576);
                    if (len <= 0) {
                        break;
                    }
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                $closeResource(null, is);
            }
            $closeResource(null, zos);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "error zipping up profile data", e);
            return false;
        }
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }
}
