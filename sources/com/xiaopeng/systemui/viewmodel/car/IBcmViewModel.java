package com.xiaopeng.systemui.viewmodel.car;

import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.viewmodel.IViewModel;
/* loaded from: classes24.dex */
public interface IBcmViewModel extends IViewModel {
    int[] getDoorState();

    int getHeadLampGroup();

    int getNearLampState();

    MutableLiveData<Integer> getPsnSeatHeatLevel();

    MutableLiveData<Integer> getPsnSeatVentLevel();

    MutableLiveData<Integer> getSeatHeatLevel();

    MutableLiveData<Integer> getSeatVentLevel();

    int getWirelessChargeStatus();
}
