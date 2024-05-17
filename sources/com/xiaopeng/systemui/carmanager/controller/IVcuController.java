package com.xiaopeng.systemui.carmanager.controller;

import com.xiaopeng.systemui.carmanager.IBaseCallback;
import com.xiaopeng.systemui.carmanager.IBaseCarController;
/* loaded from: classes24.dex */
public interface IVcuController extends IBaseCarController<Callback> {
    public static final int GEAR_LEVEL_D = 1;
    public static final int GEAR_LEVEL_INVALID = 0;
    public static final int GEAR_LEVEL_N = 2;
    public static final int GEAR_LEVEL_P = 4;
    public static final int GEAR_LEVEL_R = 3;

    /* loaded from: classes24.dex */
    public interface Callback extends IBaseCallback {
        void onGearChanged(int i);
    }

    int getAvailableMileage();

    int getGearLevel();
}
