package com.android.systemui.power;

import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.speech.jarvisproto.DMEnd;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: BatteryStateSnapshot.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b'\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B?\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003¢\u0006\u0002\u0010\u000bBm\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\r\u0012\u0006\u0010\u000f\u001a\u00020\r\u0012\u0006\u0010\u0010\u001a\u00020\r\u0012\u0006\u0010\u0011\u001a\u00020\u0005\u0012\u0006\u0010\u0012\u001a\u00020\u0005¢\u0006\u0002\u0010\u0013J\t\u0010#\u001a\u00020\u0003HÆ\u0003J\t\u0010$\u001a\u00020\rHÆ\u0003J\t\u0010%\u001a\u00020\rHÆ\u0003J\t\u0010&\u001a\u00020\u0005HÆ\u0003J\t\u0010'\u001a\u00020\u0005HÆ\u0003J\t\u0010(\u001a\u00020\u0005HÆ\u0003J\t\u0010)\u001a\u00020\u0005HÆ\u0003J\t\u0010*\u001a\u00020\u0003HÆ\u0003J\t\u0010+\u001a\u00020\u0003HÆ\u0003J\t\u0010,\u001a\u00020\u0003HÆ\u0003J\t\u0010-\u001a\u00020\u0003HÆ\u0003J\t\u0010.\u001a\u00020\rHÆ\u0003J\t\u0010/\u001a\u00020\rHÆ\u0003J\u008b\u0001\u00100\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\r2\b\b\u0002\u0010\u000f\u001a\u00020\r2\b\b\u0002\u0010\u0010\u001a\u00020\r2\b\b\u0002\u0010\u0011\u001a\u00020\u00052\b\b\u0002\u0010\u0012\u001a\u00020\u0005HÆ\u0001J\u0013\u00101\u001a\u00020\u00052\b\u00102\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u00103\u001a\u00020\u0003HÖ\u0001J\t\u00104\u001a\u000205HÖ\u0001R\u0011\u0010\u000e\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u0011\u0010\u0011\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u001aR\u001e\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001aR\u0011\u0010\u0012\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u001aR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u001aR\u0011\u0010\n\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0017R\u0011\u0010\u0010\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0015R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001aR\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u0017R\u0011\u0010\u000f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0015R\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0015¨\u00066"}, d2 = {"Lcom/android/systemui/power/BatteryStateSnapshot;", "", "batteryLevel", "", "isPowerSaver", "", "plugged", "bucket", "batteryStatus", "severeLevelThreshold", "lowLevelThreshold", "(IZZIIII)V", "timeRemainingMillis", "", "averageTimeToDischargeMillis", "severeThresholdMillis", "lowThresholdMillis", "isBasedOnUsage", "isLowWarningEnabled", "(IZZIIIIJJJJZZ)V", "getAverageTimeToDischargeMillis", "()J", "getBatteryLevel", "()I", "getBatteryStatus", "getBucket", "()Z", "<set-?>", "isHybrid", "getLowLevelThreshold", "getLowThresholdMillis", "getPlugged", "getSevereLevelThreshold", "getSevereThresholdMillis", "getTimeRemainingMillis", "component1", "component10", "component11", "component12", "component13", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", DMEnd.REASON_OTHER, "hashCode", "toString", "", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class BatteryStateSnapshot {
    private final long averageTimeToDischargeMillis;
    private final int batteryLevel;
    private final int batteryStatus;
    private final int bucket;
    private final boolean isBasedOnUsage;
    private boolean isHybrid;
    private final boolean isLowWarningEnabled;
    private final boolean isPowerSaver;
    private final int lowLevelThreshold;
    private final long lowThresholdMillis;
    private final boolean plugged;
    private final int severeLevelThreshold;
    private final long severeThresholdMillis;
    private final long timeRemainingMillis;

    public final int component1() {
        return this.batteryLevel;
    }

    public final long component10() {
        return this.severeThresholdMillis;
    }

    public final long component11() {
        return this.lowThresholdMillis;
    }

    public final boolean component12() {
        return this.isBasedOnUsage;
    }

    public final boolean component13() {
        return this.isLowWarningEnabled;
    }

    public final boolean component2() {
        return this.isPowerSaver;
    }

    public final boolean component3() {
        return this.plugged;
    }

    public final int component4() {
        return this.bucket;
    }

    public final int component5() {
        return this.batteryStatus;
    }

    public final int component6() {
        return this.severeLevelThreshold;
    }

    public final int component7() {
        return this.lowLevelThreshold;
    }

    public final long component8() {
        return this.timeRemainingMillis;
    }

    public final long component9() {
        return this.averageTimeToDischargeMillis;
    }

    @NotNull
    public final BatteryStateSnapshot copy(int i, boolean z, boolean z2, int i2, int i3, int i4, int i5, long j, long j2, long j3, long j4, boolean z3, boolean z4) {
        return new BatteryStateSnapshot(i, z, z2, i2, i3, i4, i5, j, j2, j3, j4, z3, z4);
    }

    public boolean equals(@Nullable Object obj) {
        if (this != obj) {
            if (obj instanceof BatteryStateSnapshot) {
                BatteryStateSnapshot batteryStateSnapshot = (BatteryStateSnapshot) obj;
                if (this.batteryLevel == batteryStateSnapshot.batteryLevel) {
                    if (this.isPowerSaver == batteryStateSnapshot.isPowerSaver) {
                        if (this.plugged == batteryStateSnapshot.plugged) {
                            if (this.bucket == batteryStateSnapshot.bucket) {
                                if (this.batteryStatus == batteryStateSnapshot.batteryStatus) {
                                    if (this.severeLevelThreshold == batteryStateSnapshot.severeLevelThreshold) {
                                        if (this.lowLevelThreshold == batteryStateSnapshot.lowLevelThreshold) {
                                            if (this.timeRemainingMillis == batteryStateSnapshot.timeRemainingMillis) {
                                                if (this.averageTimeToDischargeMillis == batteryStateSnapshot.averageTimeToDischargeMillis) {
                                                    if (this.severeThresholdMillis == batteryStateSnapshot.severeThresholdMillis) {
                                                        if (this.lowThresholdMillis == batteryStateSnapshot.lowThresholdMillis) {
                                                            if (this.isBasedOnUsage == batteryStateSnapshot.isBasedOnUsage) {
                                                                if (this.isLowWarningEnabled == batteryStateSnapshot.isLowWarningEnabled) {
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int hashCode() {
        int hashCode = Integer.hashCode(this.batteryLevel) * 31;
        boolean z = this.isPowerSaver;
        int i = z;
        if (z != 0) {
            i = 1;
        }
        int i2 = (hashCode + i) * 31;
        boolean z2 = this.plugged;
        int i3 = z2;
        if (z2 != 0) {
            i3 = 1;
        }
        int hashCode2 = (((((((((((((((((i2 + i3) * 31) + Integer.hashCode(this.bucket)) * 31) + Integer.hashCode(this.batteryStatus)) * 31) + Integer.hashCode(this.severeLevelThreshold)) * 31) + Integer.hashCode(this.lowLevelThreshold)) * 31) + Long.hashCode(this.timeRemainingMillis)) * 31) + Long.hashCode(this.averageTimeToDischargeMillis)) * 31) + Long.hashCode(this.severeThresholdMillis)) * 31) + Long.hashCode(this.lowThresholdMillis)) * 31;
        boolean z3 = this.isBasedOnUsage;
        int i4 = z3;
        if (z3 != 0) {
            i4 = 1;
        }
        int i5 = (hashCode2 + i4) * 31;
        boolean z4 = this.isLowWarningEnabled;
        int i6 = z4;
        if (z4 != 0) {
            i6 = 1;
        }
        return i5 + i6;
    }

    @NotNull
    public String toString() {
        return "BatteryStateSnapshot(batteryLevel=" + this.batteryLevel + ", isPowerSaver=" + this.isPowerSaver + ", plugged=" + this.plugged + ", bucket=" + this.bucket + ", batteryStatus=" + this.batteryStatus + ", severeLevelThreshold=" + this.severeLevelThreshold + ", lowLevelThreshold=" + this.lowLevelThreshold + ", timeRemainingMillis=" + this.timeRemainingMillis + ", averageTimeToDischargeMillis=" + this.averageTimeToDischargeMillis + ", severeThresholdMillis=" + this.severeThresholdMillis + ", lowThresholdMillis=" + this.lowThresholdMillis + ", isBasedOnUsage=" + this.isBasedOnUsage + ", isLowWarningEnabled=" + this.isLowWarningEnabled + NavigationBarInflaterView.KEY_CODE_END;
    }

    public BatteryStateSnapshot(int batteryLevel, boolean isPowerSaver, boolean plugged, int bucket, int batteryStatus, int severeLevelThreshold, int lowLevelThreshold, long timeRemainingMillis, long averageTimeToDischargeMillis, long severeThresholdMillis, long lowThresholdMillis, boolean isBasedOnUsage, boolean isLowWarningEnabled) {
        this.batteryLevel = batteryLevel;
        this.isPowerSaver = isPowerSaver;
        this.plugged = plugged;
        this.bucket = bucket;
        this.batteryStatus = batteryStatus;
        this.severeLevelThreshold = severeLevelThreshold;
        this.lowLevelThreshold = lowLevelThreshold;
        this.timeRemainingMillis = timeRemainingMillis;
        this.averageTimeToDischargeMillis = averageTimeToDischargeMillis;
        this.severeThresholdMillis = severeThresholdMillis;
        this.lowThresholdMillis = lowThresholdMillis;
        this.isBasedOnUsage = isBasedOnUsage;
        this.isLowWarningEnabled = isLowWarningEnabled;
        this.isHybrid = true;
    }

    public final int getBatteryLevel() {
        return this.batteryLevel;
    }

    public final boolean isPowerSaver() {
        return this.isPowerSaver;
    }

    public final boolean getPlugged() {
        return this.plugged;
    }

    public final int getBucket() {
        return this.bucket;
    }

    public final int getBatteryStatus() {
        return this.batteryStatus;
    }

    public final int getSevereLevelThreshold() {
        return this.severeLevelThreshold;
    }

    public final int getLowLevelThreshold() {
        return this.lowLevelThreshold;
    }

    public final long getTimeRemainingMillis() {
        return this.timeRemainingMillis;
    }

    public final long getAverageTimeToDischargeMillis() {
        return this.averageTimeToDischargeMillis;
    }

    public final long getSevereThresholdMillis() {
        return this.severeThresholdMillis;
    }

    public final long getLowThresholdMillis() {
        return this.lowThresholdMillis;
    }

    public final boolean isBasedOnUsage() {
        return this.isBasedOnUsage;
    }

    public final boolean isLowWarningEnabled() {
        return this.isLowWarningEnabled;
    }

    public final boolean isHybrid() {
        return this.isHybrid;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public BatteryStateSnapshot(int r19, boolean r20, boolean r21, int r22, int r23, int r24, int r25) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r21
            r4 = r22
            r5 = r23
            r6 = r24
            r7 = r25
            r8 = -1
            long r8 = (long) r8
            r12 = r8
            r14 = r8
            r10 = r8
            r16 = 0
            r17 = 1
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r10, r12, r14, r16, r17)
            r0 = 0
            r1 = r18
            r1.isHybrid = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.power.BatteryStateSnapshot.<init>(int, boolean, boolean, int, int, int, int):void");
    }
}
