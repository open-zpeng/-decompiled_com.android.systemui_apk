package com.xiaopeng.systemui.viewmodel.car;

import com.xiaopeng.systemui.viewmodel.IViewModel;
/* loaded from: classes24.dex */
public interface ICarViewModel extends IViewModel {
    int getChargeState();

    boolean isCarControlLoadReady();

    boolean isCenterLocked();

    boolean isDriverSeatActive();

    boolean isPassengerSeatActive();

    void setCenterLock(boolean z);
}
