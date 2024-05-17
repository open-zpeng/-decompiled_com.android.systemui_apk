package com.xiaopeng.systemui.viewmodel.car;

import com.xiaopeng.systemui.viewmodel.IViewModel;
/* loaded from: classes24.dex */
public interface IHvacViewModel extends IViewModel {

    /* loaded from: classes24.dex */
    public interface HvacCallback {
        void onBackDefrostChanged(boolean z);

        void onHvacAutoModeChanged(boolean z);

        void onHvacDriverSyncChanged(boolean z);

        void onHvacExternalTempChanged(float f);

        void onHvacFrontDefrostChanged(boolean z);

        void onHvacInnerAqChanged(int i);

        void onHvacPowerModeChanged(boolean z);

        void onHvacPsnSyncChanged(boolean z);

        void onHvacSeatHeatChanged(int i);

        void onHvacSeatVentChanged(int i);

        void onHvacTempDriver(float f);

        void onHvacTempPsn(float f);

        void onHvacWindSpeedLevelChanged(int i);
    }

    int getHvacDriverSeatHeat();

    int getHvacDriverSeatWind();

    float getHvacDriverTemperature();

    float getHvacExternalTemperature();

    float getHvacPassengerTemperature();

    int getHvacQualityInner();

    int getHvacWindColor();

    int getHvacWindSpeed();

    boolean isHvacAuto();

    boolean isHvacBackDefrostOn();

    boolean isHvacDriverSync();

    boolean isHvacFrontDefrostOn();

    boolean isHvacPassengerSync();

    boolean isHvacPowerOn();

    void setHvacBackDefrostOn(boolean z);

    void setHvacCallback(HvacCallback hvacCallback);

    void setHvacDriverSync(boolean z);

    void setHvacDriverTemperature(float f);

    void setHvacDriverTemperatureStep(boolean z);

    void setHvacFrontDefrostOn(boolean z);

    void setHvacPassengerSync(boolean z);

    void setHvacPassengerTemperature(float f);

    void setHvacPassengerTemperatureStep(boolean z);

    void setHvacPower(boolean z);
}
