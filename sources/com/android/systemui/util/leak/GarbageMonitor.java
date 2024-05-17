package com.android.systemui.util.leak;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.util.LongSparseArray;
import androidx.slice.core.SliceHints;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.util.leak.GarbageMonitor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import kotlin.text.Typography;
@Singleton
/* loaded from: classes21.dex */
public class GarbageMonitor implements Dumpable {
    private static final boolean DEBUG;
    private static final int DO_GARBAGE_INSPECTION = 1000;
    private static final int DO_HEAP_TRACK = 3000;
    private static final boolean ENABLE_AM_HEAP_LIMIT;
    private static final String FORCE_ENABLE_LEAK_REPORTING = "sysui_force_enable_leak_reporting";
    private static final int GARBAGE_ALLOWANCE = 5;
    private static final long GARBAGE_INSPECTION_INTERVAL = 900000;
    private static final boolean HEAP_TRACKING_ENABLED;
    private static final int HEAP_TRACK_HISTORY_LEN = 720;
    private static final long HEAP_TRACK_INTERVAL = 60000;
    private static final boolean LEAK_REPORTING_ENABLED;
    private static final String SETTINGS_KEY_AM_HEAP_LIMIT = "systemui_am_heap_limit";
    private static final String TAG = "GarbageMonitor";
    private final ActivityManager mAm;
    private final Context mContext;
    private DumpTruck mDumpTruck;
    private final Handler mHandler;
    private long mHeapLimit;
    private final LeakReporter mLeakReporter;
    private MemoryTile mQSTile;
    private final TrackedGarbage mTrackedGarbage;
    private final LongSparseArray<ProcessMemInfo> mData = new LongSparseArray<>();
    private final ArrayList<Long> mPids = new ArrayList<>();
    private int[] mPidsArray = new int[1];

    static {
        boolean z = false;
        if (Build.IS_DEBUGGABLE && SystemProperties.getBoolean("debug.enable_leak_reporting", false)) {
            z = true;
        }
        LEAK_REPORTING_ENABLED = z;
        HEAP_TRACKING_ENABLED = Build.IS_DEBUGGABLE;
        ENABLE_AM_HEAP_LIMIT = Build.IS_DEBUGGABLE;
        DEBUG = Log.isLoggable(TAG, 3);
    }

    @Inject
    public GarbageMonitor(Context context, @Named("background_looper") Looper bgLooper, LeakDetector leakDetector, LeakReporter leakReporter) {
        this.mContext = context.getApplicationContext();
        this.mAm = (ActivityManager) context.getSystemService(SliceHints.HINT_ACTIVITY);
        this.mHandler = new BackgroundHeapCheckHandler(bgLooper);
        this.mTrackedGarbage = leakDetector.getTrackedGarbage();
        this.mLeakReporter = leakReporter;
        this.mDumpTruck = new DumpTruck(this.mContext);
        if (ENABLE_AM_HEAP_LIMIT) {
            this.mHeapLimit = Settings.Global.getInt(context.getContentResolver(), SETTINGS_KEY_AM_HEAP_LIMIT, this.mContext.getResources().getInteger(R.integer.watch_heap_limit));
        }
    }

    public void startLeakMonitor() {
        if (this.mTrackedGarbage == null) {
            return;
        }
        this.mHandler.sendEmptyMessage(1000);
    }

    public void startHeapTracking() {
        startTrackingProcess(Process.myPid(), this.mContext.getPackageName(), System.currentTimeMillis());
        this.mHandler.sendEmptyMessage(3000);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean gcAndCheckGarbage() {
        if (this.mTrackedGarbage.countOldGarbage() > 5) {
            Runtime.getRuntime().gc();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reinspectGarbageAfterGc() {
        int count = this.mTrackedGarbage.countOldGarbage();
        if (count > 5) {
            this.mLeakReporter.dumpLeak(count);
        }
    }

    public ProcessMemInfo getMemInfo(int pid) {
        return this.mData.get(pid);
    }

    public int[] getTrackedProcesses() {
        return this.mPidsArray;
    }

    public void startTrackingProcess(long pid, String name, long start) {
        synchronized (this.mPids) {
            if (this.mPids.contains(Long.valueOf(pid))) {
                return;
            }
            this.mPids.add(Long.valueOf(pid));
            updatePidsArrayL();
            this.mData.put(pid, new ProcessMemInfo(pid, name, start));
        }
    }

    private void updatePidsArrayL() {
        int N = this.mPids.size();
        this.mPidsArray = new int[N];
        StringBuffer sb = new StringBuffer("Now tracking processes: ");
        for (int i = 0; i < N; i++) {
            int p = this.mPids.get(i).intValue();
            this.mPidsArray[i] = p;
            sb.append(p);
            sb.append(" ");
        }
        if (DEBUG) {
            Log.v(TAG, sb.toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        synchronized (this.mPids) {
            Debug.MemoryInfo[] dinfos = this.mAm.getProcessMemoryInfo(this.mPidsArray);
            int i = 0;
            while (true) {
                if (i >= dinfos.length) {
                    break;
                }
                Debug.MemoryInfo dinfo = dinfos[i];
                if (i > this.mPids.size()) {
                    if (DEBUG) {
                        Log.e(TAG, "update: unknown process info received: " + dinfo);
                    }
                } else {
                    long pid = this.mPids.get(i).intValue();
                    ProcessMemInfo info = this.mData.get(pid);
                    long[] jArr = info.pss;
                    int i2 = info.head;
                    long totalPss = dinfo.getTotalPss();
                    info.currentPss = totalPss;
                    jArr[i2] = totalPss;
                    long[] jArr2 = info.uss;
                    int i3 = info.head;
                    long totalPrivateDirty = dinfo.getTotalPrivateDirty();
                    info.currentUss = totalPrivateDirty;
                    jArr2[i3] = totalPrivateDirty;
                    info.head = (info.head + 1) % info.pss.length;
                    if (info.currentPss > info.max) {
                        info.max = info.currentPss;
                    }
                    if (info.currentUss > info.max) {
                        info.max = info.currentUss;
                    }
                    if (info.currentPss == 0) {
                        if (DEBUG) {
                            Log.v(TAG, "update: pid " + pid + " has pss=0, it probably died");
                        }
                        this.mData.remove(pid);
                    }
                    i++;
                }
            }
            for (int i4 = this.mPids.size() - 1; i4 >= 0; i4--) {
                if (this.mData.get(this.mPids.get(i4).intValue()) == null) {
                    this.mPids.remove(i4);
                    updatePidsArrayL();
                }
            }
        }
        MemoryTile memoryTile = this.mQSTile;
        if (memoryTile != null) {
            memoryTile.update();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTile(MemoryTile tile) {
        this.mQSTile = tile;
        if (tile != null) {
            tile.update();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String formatBytes(long b) {
        String[] SUFFIXES = {"B", "K", "M", "G", "T"};
        int i = 0;
        while (i < SUFFIXES.length && b >= 1024) {
            b /= 1024;
            i++;
        }
        return b + SUFFIXES[i];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Intent dumpHprofAndGetShareIntent() {
        return this.mDumpTruck.captureHeaps(getTrackedProcesses()).createShareIntent();
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("GarbageMonitor params:");
        pw.println(String.format("   mHeapLimit=%d KB", Long.valueOf(this.mHeapLimit)));
        pw.println(String.format("   GARBAGE_INSPECTION_INTERVAL=%d (%.1f mins)", Long.valueOf((long) GARBAGE_INSPECTION_INTERVAL), Float.valueOf(15.0f)));
        pw.println(String.format("   HEAP_TRACK_INTERVAL=%d (%.1f mins)", 60000L, Float.valueOf(1.0f)));
        pw.println(String.format("   HEAP_TRACK_HISTORY_LEN=%d (%.1f hr total)", Integer.valueOf((int) HEAP_TRACK_HISTORY_LEN), Float.valueOf(12.0f)));
        pw.println("GarbageMonitor tracked processes:");
        Iterator<Long> it = this.mPids.iterator();
        while (it.hasNext()) {
            long pid = it.next().longValue();
            ProcessMemInfo pmi = this.mData.get(pid);
            if (pmi != null) {
                pmi.dump(fd, pw, args);
            }
        }
    }

    /* loaded from: classes21.dex */
    private static class MemoryIconDrawable extends Drawable {
        final Drawable baseIcon;
        final float dp;
        long limit;
        final Paint paint = new Paint();
        long pss;

        MemoryIconDrawable(Context context) {
            this.baseIcon = context.getDrawable(R.drawable.ic_memory).mutate();
            this.dp = context.getResources().getDisplayMetrics().density;
            this.paint.setColor(QSTileImpl.getColorForState(context, 2));
        }

        public void setPss(long pss) {
            if (pss != this.pss) {
                this.pss = pss;
                invalidateSelf();
            }
        }

        public void setLimit(long limit) {
            if (limit != this.limit) {
                this.limit = limit;
                invalidateSelf();
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            this.baseIcon.draw(canvas);
            long j = this.limit;
            if (j > 0) {
                long j2 = this.pss;
                if (j2 > 0) {
                    float frac = Math.min(1.0f, ((float) j2) / ((float) j));
                    Rect bounds = getBounds();
                    canvas.translate(bounds.left + (this.dp * 8.0f), bounds.top + (this.dp * 5.0f));
                    float f = this.dp;
                    canvas.drawRect(0.0f, (1.0f - frac) * f * 14.0f, (8.0f * f) + 1.0f, (f * 14.0f) + 1.0f, this.paint);
                }
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);
            this.baseIcon.setBounds(left, top, right, bottom);
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return this.baseIcon.getIntrinsicHeight();
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return this.baseIcon.getIntrinsicWidth();
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            this.baseIcon.setAlpha(i);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            this.baseIcon.setColorFilter(colorFilter);
            this.paint.setColorFilter(colorFilter);
        }

        @Override // android.graphics.drawable.Drawable
        public void setTint(int tint) {
            super.setTint(tint);
            this.baseIcon.setTint(tint);
        }

        @Override // android.graphics.drawable.Drawable
        public void setTintList(ColorStateList tint) {
            super.setTintList(tint);
            this.baseIcon.setTintList(tint);
        }

        @Override // android.graphics.drawable.Drawable
        public void setTintMode(PorterDuff.Mode tintMode) {
            super.setTintMode(tintMode);
            this.baseIcon.setTintMode(tintMode);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -3;
        }
    }

    /* loaded from: classes21.dex */
    private static class MemoryGraphIcon extends QSTile.Icon {
        long limit;
        long pss;

        private MemoryGraphIcon() {
        }

        public void setPss(long pss) {
            this.pss = pss;
        }

        public void setHeapLimit(long limit) {
            this.limit = limit;
        }

        @Override // com.android.systemui.plugins.qs.QSTile.Icon
        public Drawable getDrawable(Context context) {
            MemoryIconDrawable drawable = new MemoryIconDrawable(context);
            drawable.setPss(this.pss);
            drawable.setLimit(this.limit);
            return drawable;
        }
    }

    /* loaded from: classes21.dex */
    public static class MemoryTile extends QSTileImpl<QSTile.State> {
        public static final boolean ADD_TO_DEFAULT_ON_DEBUGGABLE_BUILDS = true;
        public static final String TILE_SPEC = "dbg:mem";
        private boolean dumpInProgress;
        private final GarbageMonitor gm;
        private ProcessMemInfo pmi;

        @Inject
        public MemoryTile(QSHost host) {
            super(host);
            this.gm = SystemUIFactory.getInstance().getRootComponent().createGarbageMonitor();
        }

        @Override // com.android.systemui.qs.tileimpl.QSTileImpl
        public QSTile.State newTileState() {
            return new QSTile.State();
        }

        @Override // com.android.systemui.qs.tileimpl.QSTileImpl
        public Intent getLongClickIntent() {
            return new Intent();
        }

        @Override // com.android.systemui.qs.tileimpl.QSTileImpl
        protected void handleClick() {
            if (this.dumpInProgress) {
                return;
            }
            this.dumpInProgress = true;
            refreshState();
            new AnonymousClass1("HeapDumpThread").start();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.systemui.util.leak.GarbageMonitor$MemoryTile$1  reason: invalid class name */
        /* loaded from: classes21.dex */
        public class AnonymousClass1 extends Thread {
            AnonymousClass1(String x0) {
                super(x0);
            }

            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                }
                final Intent shareIntent = MemoryTile.this.gm.dumpHprofAndGetShareIntent();
                MemoryTile.this.mHandler.post(new Runnable() { // from class: com.android.systemui.util.leak.-$$Lambda$GarbageMonitor$MemoryTile$1$cmBeuqKr1b9hrY1trlao7X6pfIc
                    @Override // java.lang.Runnable
                    public final void run() {
                        GarbageMonitor.MemoryTile.AnonymousClass1.this.lambda$run$0$GarbageMonitor$MemoryTile$1(shareIntent);
                    }
                });
            }

            public /* synthetic */ void lambda$run$0$GarbageMonitor$MemoryTile$1(Intent shareIntent) {
                MemoryTile.this.dumpInProgress = false;
                MemoryTile.this.refreshState();
                MemoryTile.this.getHost().collapsePanels();
                MemoryTile.this.mContext.startActivity(shareIntent);
            }
        }

        @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
        public int getMetricsCategory() {
            return 0;
        }

        @Override // com.android.systemui.qs.tileimpl.QSTileImpl
        public void handleSetListening(boolean listening) {
            GarbageMonitor garbageMonitor = this.gm;
            if (garbageMonitor != null) {
                garbageMonitor.setTile(listening ? this : null);
            }
            ActivityManager am = (ActivityManager) this.mContext.getSystemService(ActivityManager.class);
            if (listening && this.gm.mHeapLimit > 0) {
                am.setWatchHeapLimit(this.gm.mHeapLimit * 1024);
            } else {
                am.clearWatchHeapLimit();
            }
        }

        @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
        public CharSequence getTileLabel() {
            return getState().label;
        }

        @Override // com.android.systemui.qs.tileimpl.QSTileImpl
        protected void handleUpdateState(QSTile.State state, Object arg) {
            String string;
            this.pmi = this.gm.getMemInfo(Process.myPid());
            MemoryGraphIcon icon = new MemoryGraphIcon();
            icon.setHeapLimit(this.gm.mHeapLimit);
            state.state = this.dumpInProgress ? 0 : 2;
            if (this.dumpInProgress) {
                string = "Dumping...";
            } else {
                string = this.mContext.getString(R.string.heap_dump_tile_name);
            }
            state.label = string;
            ProcessMemInfo processMemInfo = this.pmi;
            if (processMemInfo != null) {
                icon.setPss(processMemInfo.currentPss);
                state.secondaryLabel = String.format("pss: %s / %s", GarbageMonitor.formatBytes(this.pmi.currentPss * 1024), GarbageMonitor.formatBytes(this.gm.mHeapLimit * 1024));
            } else {
                icon.setPss(0L);
                state.secondaryLabel = null;
            }
            state.icon = icon;
        }

        public void update() {
            refreshState();
        }

        public long getPss() {
            ProcessMemInfo processMemInfo = this.pmi;
            if (processMemInfo != null) {
                return processMemInfo.currentPss;
            }
            return 0L;
        }

        public long getHeapLimit() {
            GarbageMonitor garbageMonitor = this.gm;
            if (garbageMonitor != null) {
                return garbageMonitor.mHeapLimit;
            }
            return 0L;
        }
    }

    /* loaded from: classes21.dex */
    public static class ProcessMemInfo implements Dumpable {
        public long currentPss;
        public long currentUss;
        public String name;
        public long pid;
        public long startTime;
        public long[] pss = new long[GarbageMonitor.HEAP_TRACK_HISTORY_LEN];
        public long[] uss = new long[GarbageMonitor.HEAP_TRACK_HISTORY_LEN];
        public long max = 1;
        public int head = 0;

        public ProcessMemInfo(long pid, String name, long start) {
            this.pid = pid;
            this.name = name;
            this.startTime = start;
        }

        public long getUptime() {
            return System.currentTimeMillis() - this.startTime;
        }

        @Override // com.android.systemui.Dumpable
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            pw.print("{ \"pid\": ");
            pw.print(this.pid);
            pw.print(", \"name\": \"");
            pw.print(this.name.replace(Typography.quote, '-'));
            pw.print("\", \"start\": ");
            pw.print(this.startTime);
            pw.print(", \"pss\": [");
            for (int i = 0; i < this.pss.length; i++) {
                if (i > 0) {
                    pw.print(",");
                }
                long[] jArr = this.pss;
                pw.print(jArr[(this.head + i) % jArr.length]);
            }
            pw.print("], \"uss\": [");
            for (int i2 = 0; i2 < this.uss.length; i2++) {
                if (i2 > 0) {
                    pw.print(",");
                }
                long[] jArr2 = this.uss;
                pw.print(jArr2[(this.head + i2) % jArr2.length]);
            }
            pw.println("] }");
        }
    }

    /* loaded from: classes21.dex */
    public static class Service extends SystemUI implements Dumpable {
        private GarbageMonitor mGarbageMonitor;

        @Override // com.android.systemui.SystemUI
        public void start() {
            boolean forceEnable = Settings.Secure.getInt(this.mContext.getContentResolver(), GarbageMonitor.FORCE_ENABLE_LEAK_REPORTING, 0) != 0;
            this.mGarbageMonitor = SystemUIFactory.getInstance().getRootComponent().createGarbageMonitor();
            if (GarbageMonitor.LEAK_REPORTING_ENABLED || forceEnable) {
                this.mGarbageMonitor.startLeakMonitor();
            }
            if (GarbageMonitor.HEAP_TRACKING_ENABLED || forceEnable) {
                this.mGarbageMonitor.startHeapTracking();
            }
        }

        @Override // com.android.systemui.SystemUI
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            GarbageMonitor garbageMonitor = this.mGarbageMonitor;
            if (garbageMonitor != null) {
                garbageMonitor.dump(fd, pw, args);
            }
        }
    }

    /* loaded from: classes21.dex */
    private class BackgroundHeapCheckHandler extends Handler {
        BackgroundHeapCheckHandler(Looper onLooper) {
            super(onLooper);
            if (Looper.getMainLooper().equals(onLooper)) {
                throw new RuntimeException("BackgroundHeapCheckHandler may not run on the ui thread");
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message m) {
            int i = m.what;
            if (i != 1000) {
                if (i == 3000) {
                    GarbageMonitor.this.update();
                    removeMessages(3000);
                    sendEmptyMessageDelayed(3000, 60000L);
                    return;
                }
                return;
            }
            if (GarbageMonitor.this.gcAndCheckGarbage()) {
                final GarbageMonitor garbageMonitor = GarbageMonitor.this;
                postDelayed(new Runnable() { // from class: com.android.systemui.util.leak.-$$Lambda$XMHjUeThvUDRPlJmBo9djG71pM8
                    @Override // java.lang.Runnable
                    public final void run() {
                        GarbageMonitor.this.reinspectGarbageAfterGc();
                    }
                }, 100L);
            }
            removeMessages(1000);
            sendEmptyMessageDelayed(1000, GarbageMonitor.GARBAGE_INSPECTION_INTERVAL);
        }
    }
}
