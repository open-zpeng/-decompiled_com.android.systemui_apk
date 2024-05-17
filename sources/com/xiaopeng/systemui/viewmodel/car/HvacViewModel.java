package com.xiaopeng.systemui.viewmodel.car;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.viewmodel.car.IHvacViewModel;
/* loaded from: classes24.dex */
public class HvacViewModel implements IHvacViewModel {
    private static final String TAG = "HvacViewModel";
    private Context mContext;
    private final MutableLiveData<Boolean> mHvacPower = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mHvacAuto = new MutableLiveData<>();
    private final MutableLiveData<Float> mHvacTempExternal = new MutableLiveData<>();
    private final MutableLiveData<Float> mHvacTempPassenger = new MutableLiveData<>();
    private final MutableLiveData<Float> mHvacTempDriver = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mHvacDriverSync = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mHvacPassengerSync = new MutableLiveData<>();
    private final MutableLiveData<Integer> mHvacWindSpeed = new MutableLiveData<>();
    private final MutableLiveData<Integer> mHvacWindColor = new MutableLiveData<>();
    private final MutableLiveData<Integer> mHvacDriverSeatWind = new MutableLiveData<>();
    private final MutableLiveData<Integer> mHvacDriverSeatHeat = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mHvacBackDefrost = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mHvacFrontDefrost = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mHvacPurgeMode = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mHvacAutoDefogWorkSt = new MutableLiveData<>();
    private final MutableLiveData<Integer> mHvacQualityInner = new MutableLiveData<>();
    private CarControllerCallback mCarCallback = new CarControllerCallback();

    /* loaded from: classes24.dex */
    private class CarControllerCallback implements CarController.CarCallback {
        private CarControllerCallback() {
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarControlChanged(int type, Object newValue) {
            Logger.d(HvacViewModel.TAG, "onCarControlChanged() called with: type = [" + type + "], newValue = [" + newValue + NavigationBarInflaterView.SIZE_MOD_END);
            if (type != 1001) {
                switch (type) {
                    case CarController.TYPE_HVAC_POWER /* 2101 */:
                        HvacViewModel.this.mHvacPower.postValue((Boolean) newValue);
                        break;
                    case CarController.TYPE_HVAC_AUTO /* 2102 */:
                        HvacViewModel.this.mHvacAuto.postValue((Boolean) newValue);
                        break;
                    case CarController.TYPE_HVAC_EXTERNAL_TEMP /* 2103 */:
                        HvacViewModel.this.mHvacTempExternal.postValue(Float.valueOf(((Float) newValue).floatValue()));
                        break;
                    case CarController.TYPE_HVAC_DRIVER_TEMP /* 2104 */:
                        HvacViewModel.this.mHvacTempDriver.postValue(Float.valueOf(((Float) newValue).floatValue()));
                        break;
                    case CarController.TYPE_HVAC_DRIVER_SYNC /* 2105 */:
                        HvacViewModel.this.mHvacDriverSync.postValue((Boolean) newValue);
                        break;
                    case CarController.TYPE_HVAC_PASSENGER_TEMP /* 2106 */:
                        HvacViewModel.this.mHvacTempPassenger.postValue(Float.valueOf(((Float) newValue).floatValue()));
                        break;
                    case CarController.TYPE_HVAC_PASSENGER_SYNC /* 2107 */:
                        HvacViewModel.this.mHvacPassengerSync.postValue((Boolean) newValue);
                        break;
                    case CarController.TYPE_HVAC_FRONT_DEFROST /* 2108 */:
                        HvacViewModel.this.mHvacFrontDefrost.postValue((Boolean) newValue);
                        break;
                    case CarController.TYPE_HVAC_BACK_DEFROST /* 2109 */:
                        HvacViewModel.this.mHvacBackDefrost.postValue((Boolean) newValue);
                        break;
                    case CarController.TYPE_HVAC_WIND_SPEED /* 2110 */:
                        if (CarModelsManager.getFeature().isValidWindSpeed(((Integer) newValue).intValue())) {
                            HvacViewModel.this.mHvacWindSpeed.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                            break;
                        }
                        break;
                    case CarController.TYPE_HVAC_QUALITY_INNER /* 2111 */:
                        HvacViewModel.this.mHvacQualityInner.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                        break;
                    case CarController.TYPE_HVAC_DRIVER_SEAT_HEAT /* 2112 */:
                        HvacViewModel.this.mHvacDriverSeatHeat.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                        break;
                    case CarController.TYPE_HVAC_DRIVER_SEAT_WIND /* 2113 */:
                        HvacViewModel.this.mHvacDriverSeatWind.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                        break;
                    case CarController.TYPE_HVAC_WIND_COLOR /* 2114 */:
                        HvacViewModel.this.mHvacWindColor.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                        break;
                    case CarController.TYPE_HVAC_PURGE_MODE /* 2115 */:
                        HvacViewModel.this.mHvacPurgeMode.postValue((Boolean) newValue);
                        break;
                    case CarController.TYPE_HVAC_AUTO_DEFOG_WORK /* 2116 */:
                        HvacViewModel.this.mHvacAutoDefogWorkSt.postValue((Boolean) newValue);
                        break;
                }
            } else {
                HvacViewModel.this.initLiveData();
            }
            HvacViewModel.this.mHvacPower.postValue(Boolean.valueOf(CarController.getInstance(HvacViewModel.this.mContext).getCarControlAdapter().isHvacPowerOn()));
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarServiceChanged(int type, Object newValue) {
        }
    }

    public HvacViewModel(Context context) {
        this.mContext = context;
        initLiveData();
        CarController.getInstance(this.mContext).addCallback(this.mCarCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initLiveData() {
        CarController.CarControlAdapter adapter = CarController.getInstance(this.mContext).getCarControlAdapter();
        CarController.ContentObserverAdapter contentObserverAdapter = CarController.getInstance(this.mContext).getContentObserverAdapter();
        if (adapter != null && adapter.isCarControlReady()) {
            this.mHvacPower.setValue(Boolean.valueOf(adapter.isHvacPowerOn()));
            this.mHvacAuto.setValue(Boolean.valueOf(adapter.isHvacAutoMode()));
            this.mHvacTempExternal.setValue(Float.valueOf(adapter.getHvacExternalTemp()));
            this.mHvacTempPassenger.setValue(Float.valueOf(adapter.getHvacPsnTemp()));
            this.mHvacTempDriver.setValue(Float.valueOf(adapter.getHvacDriverTemp()));
            this.mHvacDriverSync.setValue(Boolean.valueOf(adapter.isHvacDriverSyncEnabled()));
            this.mHvacPassengerSync.setValue(Boolean.valueOf(adapter.isHvacPsnSyncEnabled()));
            this.mHvacWindSpeed.setValue(Integer.valueOf(adapter.getHvacFanSpeed()));
            this.mHvacWindColor.setValue(Integer.valueOf(adapter.getHvacWindColor()));
            this.mHvacDriverSeatWind.setValue(Integer.valueOf(adapter.getDrvSeatVentLevel()));
            this.mHvacDriverSeatHeat.setValue(Integer.valueOf(adapter.getDrvSeatHeatLevel()));
            this.mHvacBackDefrost.setValue(Boolean.valueOf(adapter.isHvacBackDefrostEnable()));
            this.mHvacFrontDefrost.setValue(Boolean.valueOf(adapter.isHvacFrontDefrostEnable()));
            this.mHvacQualityInner.setValue(Integer.valueOf(adapter.getHvacInnerAq()));
            this.mHvacPurgeMode.setValue(Boolean.valueOf(adapter.getHvacPurgeMode()));
            this.mHvacAutoDefogWorkSt.setValue(Boolean.valueOf(contentObserverAdapter.getHvacAutoDefogWork()));
            return;
        }
        this.mHvacPower.setValue(false);
        this.mHvacAuto.setValue(false);
        this.mHvacTempExternal.setValue(Float.valueOf(0.0f));
        this.mHvacTempPassenger.setValue(Float.valueOf(18.0f));
        this.mHvacTempDriver.setValue(Float.valueOf(18.0f));
        this.mHvacDriverSync.setValue(false);
        this.mHvacPassengerSync.setValue(false);
        this.mHvacWindSpeed.setValue(0);
        this.mHvacWindColor.setValue(2);
        this.mHvacDriverSeatWind.setValue(0);
        this.mHvacDriverSeatHeat.setValue(0);
        this.mHvacBackDefrost.setValue(false);
        this.mHvacFrontDefrost.setValue(false);
        this.mHvacQualityInner.setValue(-1);
        this.mHvacPurgeMode.setValue(false);
        this.mHvacAutoDefogWorkSt.setValue(false);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public void setHvacCallback(IHvacViewModel.HvacCallback callback) {
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public boolean isHvacPowerOn() {
        return this.mHvacPower.getValue().booleanValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public void setHvacPower(boolean on) {
        CarController.getInstance(this.mContext).getCarControlAdapter().setHvacPower(on);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public boolean isHvacAuto() {
        return this.mHvacAuto.getValue().booleanValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public float getHvacExternalTemperature() {
        return this.mHvacTempExternal.getValue().floatValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public void setHvacDriverTemperatureStep(boolean isUp) {
        CarController.getInstance(this.mContext).getCarControlAdapter().setHvacDriverStep(isUp);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public void setHvacDriverTemperature(float temp) {
        CarController.getInstance(this.mContext).getCarControlAdapter().setHvacDriverTemp(temp);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public float getHvacDriverTemperature() {
        float temperature = this.mHvacTempDriver.getValue().floatValue();
        float temperature2 = temperature <= 32.0f ? temperature : 32.0f;
        return temperature2 >= 18.0f ? temperature2 : 18.0f;
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public void setHvacDriverSync(boolean enable) {
        CarController.getInstance(this.mContext).getCarControlAdapter().setHvacDriverSyncMode(enable);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public boolean isHvacDriverSync() {
        return this.mHvacDriverSync.getValue().booleanValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public void setHvacPassengerTemperatureStep(boolean isUp) {
        CarController.getInstance(this.mContext).getCarControlAdapter().setHvacPsnStep(isUp);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public void setHvacPassengerTemperature(float temp) {
        CarController.getInstance(this.mContext).getCarControlAdapter().setHvacPsnTemp(temp);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public float getHvacPassengerTemperature() {
        float temperature = this.mHvacTempPassenger.getValue().floatValue();
        float temperature2 = temperature <= 32.0f ? temperature : 32.0f;
        return temperature2 >= 18.0f ? temperature2 : 18.0f;
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public void setHvacPassengerSync(boolean enable) {
        CarController.getInstance(this.mContext).getCarControlAdapter().setHvacPsnSyncMode(enable);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public boolean isHvacPassengerSync() {
        return this.mHvacPassengerSync.getValue().booleanValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public void setHvacFrontDefrostOn(boolean enable) {
        Logger.d(TAG, "setHvacFrontDefrostOn : enable = " + enable);
        CarController.getInstance(this.mContext).getCarControlAdapter().setHvacFrontDefrostEnable(enable);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public boolean isHvacFrontDefrostOn() {
        return this.mHvacFrontDefrost.getValue().booleanValue() && this.mHvacPower.getValue().booleanValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public void setHvacBackDefrostOn(boolean enable) {
        CarController.getInstance(this.mContext).getCarControlAdapter().setHvacBackDefrostEnable(enable);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public boolean isHvacBackDefrostOn() {
        return this.mHvacBackDefrost.getValue().booleanValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public int getHvacWindColor() {
        return this.mHvacWindColor.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public int getHvacWindSpeed() {
        int speed = this.mHvacWindSpeed.getValue().intValue();
        if (speed != 14) {
            int speed2 = speed < 10 ? speed : 10;
            return speed2 < 0 ? 0 : speed2;
        } else if (!CarModelsManager.getFeature().isAutoFanSpeedSupport()) {
            return 10;
        } else {
            return speed;
        }
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public int getHvacQualityInner() {
        return this.mHvacQualityInner.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public int getHvacDriverSeatHeat() {
        return this.mHvacDriverSeatHeat.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IHvacViewModel
    public int getHvacDriverSeatWind() {
        return this.mHvacDriverSeatWind.getValue().intValue();
    }

    public MutableLiveData<Boolean> getHvacPowerData() {
        return this.mHvacPower;
    }

    public MutableLiveData<Boolean> getHvacAutoData() {
        return this.mHvacAuto;
    }

    public MutableLiveData<Float> getHvacTempExternalData() {
        return this.mHvacTempExternal;
    }

    public MutableLiveData<Float> getHvacTempPassengerData() {
        return this.mHvacTempPassenger;
    }

    public MutableLiveData<Float> getHvacTempDriverData() {
        return this.mHvacTempDriver;
    }

    public MutableLiveData<Boolean> getHvacDriverSyncData() {
        return this.mHvacDriverSync;
    }

    public MutableLiveData<Boolean> getHvacPassengerSyncData() {
        return this.mHvacPassengerSync;
    }

    public MutableLiveData<Integer> getHvacWindSpeedData() {
        return this.mHvacWindSpeed;
    }

    public MutableLiveData<Integer> getHvacWindColorData() {
        return this.mHvacWindColor;
    }

    public MutableLiveData<Integer> getHvacDriverSeatWindData() {
        return this.mHvacDriverSeatWind;
    }

    public MutableLiveData<Integer> getHvacDriverSeatHeatData() {
        return this.mHvacDriverSeatHeat;
    }

    public MutableLiveData<Boolean> getHvacBackDefrostData() {
        return this.mHvacBackDefrost;
    }

    public MutableLiveData<Boolean> getHvacFrontDefrostData() {
        return this.mHvacFrontDefrost;
    }

    public MutableLiveData<Integer> getHvacQualityInnerData() {
        return this.mHvacQualityInner;
    }

    public MutableLiveData<Boolean> getHvacPurgeModeData() {
        return this.mHvacPurgeMode;
    }

    public MutableLiveData<Boolean> getHvacAutoDefogWorkSt() {
        return this.mHvacAutoDefogWorkSt;
    }
}
