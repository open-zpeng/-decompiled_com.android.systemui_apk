package com.android.systemui.statusbar.policy;

import com.android.systemui.DemoMode;
import com.android.systemui.Dumpable;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public interface BatteryController extends DemoMode, Dumpable, CallbackController<BatteryStateChangeCallback> {

    /* loaded from: classes21.dex */
    public interface EstimateFetchCompletion {
        void onBatteryRemainingEstimateRetrieved(String str);
    }

    @Override // com.android.systemui.Dumpable
    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    boolean isAodPowerSave();

    boolean isPowerSave();

    void setPowerSaveMode(boolean z);

    /* loaded from: classes21.dex */
    public interface BatteryStateChangeCallback {
        default void onBatteryLevelChanged(int level, boolean pluggedIn, boolean charging) {
        }

        default void onPowerSaveChanged(boolean isPowerSave) {
        }
    }

    default void getEstimatedTimeRemainingString(EstimateFetchCompletion completion) {
    }
}
