package com.xiaopeng.systemui.infoflow.effect;

import android.util.Log;
import java.util.Locale;
/* loaded from: classes24.dex */
public class FpsMonitor {
    private int currentFps;
    private String currentFpsInfo;
    private float currentTfp;
    private long lastLog;
    private final long logInterval;
    private long start;
    private int sumFrames;
    private long sumTime;
    private String tag;

    public FpsMonitor() {
        this("FpsMonitor");
    }

    public FpsMonitor(String tag) {
        this.tag = tag;
        this.lastLog = -1L;
        this.logInterval = 1000L;
        this.start = -1L;
        this.currentFpsInfo = "";
    }

    public final void frameStart() {
        if (this.start != -1) {
            throw new RuntimeException("Do you forget to call frameEnd? ");
        }
        this.start = System.currentTimeMillis();
        if (this.lastLog == -1) {
            this.lastLog = this.start;
        }
    }

    public final String frameEnd() {
        if (this.start == -1) {
            throw new RuntimeException("Do you forget to call frameStart? ");
        }
        long end = System.currentTimeMillis();
        this.sumTime += end - this.start;
        this.sumFrames++;
        if (end - this.lastLog > this.logInterval) {
            this.currentFps = this.sumFrames;
            StringBuilder sb = new StringBuilder();
            sb.append(this.sumFrames);
            StringBuilder stringBuilder = sb.append(" FPS\t\t");
            this.currentTfp = (((float) this.sumTime) * 1.0f) / this.sumFrames;
            String s = String.format(Locale.getDefault(), "%.2f", Float.valueOf(this.currentTfp));
            stringBuilder.append(s);
            stringBuilder.append(" ms/f");
            this.currentFpsInfo = stringBuilder.toString();
            Log.d(this.tag, this.currentFpsInfo);
            this.sumTime = 0L;
            this.sumFrames = 0;
            this.lastLog = end;
        }
        this.start = -1L;
        return this.currentFpsInfo;
    }

    public final String getCurrentFpsInfo() {
        return this.currentFpsInfo;
    }

    public final float getCurrentTfp() {
        return this.currentTfp;
    }

    public final int getCurrentFps() {
        return this.currentFps;
    }
}
