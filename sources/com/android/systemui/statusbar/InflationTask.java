package com.android.systemui.statusbar;
/* loaded from: classes21.dex */
public interface InflationTask {
    void abort();

    default void supersedeTask(InflationTask task) {
    }
}
