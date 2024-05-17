package com.xiaopeng.systemui.viewmodel.car;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
/* loaded from: classes24.dex */
public class CarViewModel implements ICarViewModel {
    private static final String TAG = "CarViewModel";
    private Context mContext;
    private final MutableLiveData<Boolean> mDriverActive = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mPassengerActive = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mCenterLocked = new MutableLiveData<>();
    private final MutableLiveData<Integer> mChargeState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mCarControlLoadReady = new MutableLiveData<>();
    private final MutableLiveData<Integer> mIgStatus = new MutableLiveData<>();
    private Runnable mGetIgStatusRunnable = new Runnable() { // from class: com.xiaopeng.systemui.viewmodel.car.CarViewModel.1
        @Override // java.lang.Runnable
        public void run() {
            CarViewModel.this.mIgStatus.postValue(Integer.valueOf(CarController.getInstance(CarViewModel.this.mContext).getCarServiceAdapter().getIgStatus()));
        }
    };
    private CarControllerCallback mCarCallback = new CarControllerCallback();

    /* loaded from: classes24.dex */
    private class CarControllerCallback implements CarController.CarCallback {
        private CarControllerCallback() {
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarControlChanged(int type, Object newValue) {
            if (type == 1001) {
                CarViewModel.this.initLiveData();
            } else if (type != 4001) {
                switch (type) {
                    case 2001:
                        CarViewModel.this.mDriverActive.postValue(Boolean.valueOf(((Boolean) newValue).booleanValue()));
                        return;
                    case 2002:
                        CarViewModel.this.mPassengerActive.postValue(Boolean.valueOf(((Boolean) newValue).booleanValue()));
                        return;
                    case 2003:
                        CarViewModel.this.mCenterLocked.postValue(Boolean.valueOf(((Boolean) newValue).booleanValue()));
                        return;
                    case 2004:
                        CarViewModel.this.mChargeState.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                        return;
                    default:
                        return;
                }
            } else {
                CarViewModel.this.mCarControlLoadReady.postValue((Boolean) newValue);
            }
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarServiceChanged(int type, Object newValue) {
            if (type == 3401) {
                ThreadUtils.removeRunnable(CarViewModel.this.mGetIgStatusRunnable);
                CarViewModel.this.mIgStatus.postValue((Integer) newValue);
            }
        }
    }

    public CarViewModel(Context context) {
        this.mContext = context;
        initLiveData();
        ThreadUtils.postDelayed(0, this.mGetIgStatusRunnable, 0L);
        CarController.getInstance(this.mContext).addCallback(this.mCarCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initLiveData() {
        if (CarController.getInstance(this.mContext).getCarControlAdapter().isCarControlReady()) {
            this.mDriverActive.setValue(Boolean.valueOf(CarController.getInstance(this.mContext).getCarControlAdapter().isDrvSeatOccupied()));
            this.mPassengerActive.setValue(Boolean.valueOf(CarController.getInstance(this.mContext).getCarControlAdapter().isPsnSeatOccupied()));
            this.mCenterLocked.setValue(Boolean.valueOf(CarController.getInstance(this.mContext).getCarControlAdapter().isCentralLockOn()));
            this.mChargeState.setValue(Integer.valueOf(CarController.getInstance(this.mContext).getCarControlAdapter().getChargeStatus().ordinal()));
            this.mCarControlLoadReady.setValue(Boolean.valueOf(CarController.getInstance(this.mContext).getCarControlAdapter().isCarControlLoadReady()));
        } else {
            this.mDriverActive.setValue(false);
            this.mPassengerActive.setValue(false);
            this.mCenterLocked.setValue(false);
            this.mChargeState.setValue(0);
            this.mCarControlLoadReady.setValue(false);
        }
        this.mIgStatus.setValue(0);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.ICarViewModel
    public boolean isDriverSeatActive() {
        return this.mDriverActive.getValue().booleanValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.ICarViewModel
    public boolean isPassengerSeatActive() {
        return this.mPassengerActive.getValue().booleanValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.ICarViewModel
    public boolean isCenterLocked() {
        return this.mCenterLocked.getValue().booleanValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.ICarViewModel
    public int getChargeState() {
        return this.mChargeState.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.ICarViewModel
    public void setCenterLock(boolean locked) {
        CarController.getInstance(this.mContext).getCarControlAdapter().setCenterLock(locked);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.ICarViewModel
    public boolean isCarControlLoadReady() {
        boolean ready = this.mCarControlLoadReady.getValue().booleanValue();
        Logger.d(TAG, "isCarControlLoadReady : ready = " + ready);
        if (!ready) {
            ready = CarController.getInstance(this.mContext).getCarControlAdapter().isCarControlLoadReady();
            if (ready) {
                this.mCarControlLoadReady.setValue(true);
            }
            Logger.d(TAG, "isCarControlLoadReady : ready2 = " + ready);
        }
        return ready;
    }

    public int getIgStatus() {
        return this.mIgStatus.getValue().intValue();
    }

    public MutableLiveData<Boolean> getDriverActiveData() {
        return this.mDriverActive;
    }

    public MutableLiveData<Boolean> getPassengerActiveData() {
        return this.mPassengerActive;
    }

    public MutableLiveData<Boolean> getCenterLockedData() {
        return this.mCenterLocked;
    }

    public MutableLiveData<Integer> getChargeStateData() {
        return this.mChargeState;
    }

    public MutableLiveData<Boolean> getCarControlReadyData() {
        return this.mCarControlLoadReady;
    }

    public MutableLiveData<Integer> getIgStatusData() {
        return this.mIgStatus;
    }
}
