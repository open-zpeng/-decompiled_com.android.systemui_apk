package com.xiaopeng.xui.app.delegate;

import android.os.Handler;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.xui.utils.XLogUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
/* loaded from: classes25.dex */
class XTimeLogs implements Runnable {
    private long mEndTime;
    private String mName;
    private long mStartTime;
    private long mTempTime;
    private Handler mHandler = new Handler();
    private LinkedHashMap<String, Long> mTagTimeMap = new LinkedHashMap<>();

    private XTimeLogs() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static XTimeLogs create() {
        return new XTimeLogs();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start(String name) {
        this.mName = name;
        this.mStartTime = System.nanoTime();
        this.mTempTime = this.mStartTime;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void record(String tag) {
        long t = System.nanoTime() - this.mTempTime;
        this.mTagTimeMap.put(tag, Long.valueOf(t));
        this.mTempTime = System.nanoTime();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void end() {
        this.mEndTime = System.nanoTime() - this.mStartTime;
        this.mHandler.post(this);
    }

    @Override // java.lang.Runnable
    public void run() {
        Set<Map.Entry<String, Long>> sets = this.mTagTimeMap.entrySet();
        StringBuilder sb = new StringBuilder();
        sb.append("total:");
        sb.append(this.mEndTime / 1000);
        sb.append("μs");
        for (Map.Entry<String, Long> item : sets) {
            sb.append(", ");
            sb.append(item.getKey());
            sb.append(NavigationBarInflaterView.KEY_IMAGE_DELIM);
            sb.append(item.getValue().longValue() / 1000);
        }
        this.mTagTimeMap.clear();
        XLogUtils.d(this.mName, sb.toString());
    }
}
