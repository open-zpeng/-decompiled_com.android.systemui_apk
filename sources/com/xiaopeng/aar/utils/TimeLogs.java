package com.xiaopeng.aar.utils;

import androidx.annotation.RestrictTo;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.aar.utils.ThreadUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class TimeLogs implements Runnable {
    private long mEndTime;
    private boolean mIsMicrosecond;
    private String mName;
    private long mStartTime;
    private final LinkedHashMap<String, Long> mTagTimeMap = new LinkedHashMap<>();
    private long mTempTime;

    private TimeLogs() {
    }

    public static TimeLogs create() {
        return new TimeLogs();
    }

    public void setMicrosecond(boolean microsecond) {
        this.mIsMicrosecond = microsecond;
    }

    public void start(String name) {
        this.mName = name;
        this.mStartTime = System.nanoTime();
        this.mTempTime = this.mStartTime;
    }

    public void record(String tag) {
        long t = System.nanoTime() - this.mTempTime;
        this.mTagTimeMap.put(tag, Long.valueOf(t));
        this.mTempTime = System.nanoTime();
    }

    public void end() {
        this.mEndTime = System.nanoTime() - this.mStartTime;
        ThreadUtils.SINGLE.post(this);
    }

    @Override // java.lang.Runnable
    public void run() {
        Set<Map.Entry<String, Long>> sets = this.mTagTimeMap.entrySet();
        StringBuilder sb = new StringBuilder();
        sb.append("total:");
        if (this.mIsMicrosecond) {
            sb.append(this.mEndTime / 1000);
            sb.append("μs");
        } else {
            sb.append((this.mEndTime / 1000) / 1000);
            sb.append("ms");
        }
        for (Map.Entry<String, Long> item : sets) {
            sb.append(", ");
            sb.append(item.getKey());
            sb.append(NavigationBarInflaterView.KEY_IMAGE_DELIM);
            if (this.mIsMicrosecond) {
                sb.append(item.getValue().longValue() / 1000);
            } else {
                sb.append((item.getValue().longValue() / 1000) / 1000);
            }
        }
        this.mTagTimeMap.clear();
        String simpleName = TimeLogs.class.getSimpleName();
        LogUtils.d(simpleName, this.mName + sb.toString());
    }
}
