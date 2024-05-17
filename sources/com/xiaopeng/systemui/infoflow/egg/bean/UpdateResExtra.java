package com.xiaopeng.systemui.infoflow.egg.bean;
/* loaded from: classes24.dex */
public class UpdateResExtra {
    public long endTime;
    public int showHourEnd;
    public int showHourStart;
    public long startTime;
    public long uid;

    public UpdateResExtra(long startTime, long endTime, int showHourStart, int showHourEnd, long uid) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.showHourStart = showHourStart;
        this.showHourEnd = showHourEnd;
        this.uid = uid;
    }
}
