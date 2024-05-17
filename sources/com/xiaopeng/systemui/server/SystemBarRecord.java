package com.xiaopeng.systemui.server;

import android.os.SystemClock;
import com.xiaopeng.xuimanager.systemui.systembar.SystemBarItem;
/* loaded from: classes24.dex */
public class SystemBarRecord {
    private SystemBarItem bar;
    private String id;
    private String pkg;
    private long time = SystemClock.elapsedRealtime();

    /* JADX INFO: Access modifiers changed from: package-private */
    public SystemBarRecord(String pkg, String id, SystemBarItem bar) {
        this.pkg = pkg;
        this.id = id;
        this.bar = bar;
    }

    public String getBarKey() {
        return getBarKey(this.pkg, this.id);
    }

    public static String getBarKey(String pkg, String id) {
        return pkg + "|" + id;
    }

    public String getPkg() {
        return this.pkg;
    }

    public String getId() {
        return this.id;
    }

    public long getTime() {
        return this.time;
    }

    public SystemBarItem getBar() {
        return this.bar;
    }

    public String toString() {
        return "SystemBarRecord{pkg='" + this.pkg + "', id='" + this.id + "', bar=" + this.bar + '}';
    }
}
