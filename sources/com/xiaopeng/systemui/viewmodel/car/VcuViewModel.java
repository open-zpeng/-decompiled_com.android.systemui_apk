package com.xiaopeng.systemui.viewmodel.car;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
/* loaded from: classes24.dex */
public class VcuViewModel implements IVcuViewModel {
    private static final String TAG = "VcuViewModel";
    private Context mContext;
    private final MutableLiveData<Integer> mDrivingMode = new MutableLiveData<>();
    private final MutableLiveData<Integer> mElecPercent = new MutableLiveData<>();
    private final MutableLiveData<Float> mDriveDistance = new MutableLiveData<>();
    private final MutableLiveData<Integer> mEnduranceMileageMode = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDisplayGearLevel = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDcPreWarmState = new MutableLiveData<>();
    private CarControllerCallback mCarCallback = new CarControllerCallback();

    /* loaded from: classes24.dex */
    private class CarControllerCallback implements CarController.CarCallback {
        private CarControllerCallback() {
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarControlChanged(int type, Object newValue) {
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarServiceChanged(int type, final Object newValue) {
            if (type == 1000) {
                VcuViewModel.this.initLiveData();
            } else if (type == 3201) {
                VcuViewModel.this.mDrivingMode.postValue(Integer.valueOf(((Integer) newValue).intValue()));
            } else if (type == 3204) {
                VcuViewModel.this.mDisplayGearLevel.postValue((Integer) newValue);
            } else if (type == 3205) {
                VcuViewModel.this.mDcPreWarmState.postValue((Integer) newValue);
            } else {
                switch (type) {
                    case CarController.TYPE_CAR_ELEC_PERCENT /* 2005 */:
                        Logger.d(VcuViewModel.TAG, "elecPercent = " + newValue);
                        VcuViewModel.this.mElecPercent.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                        return;
                    case CarController.TYPE_CAR_DRIVE_DISTANCE /* 2006 */:
                    case CarController.TYPE_CAR_NEDC_DRIVE_DISTANCE /* 2010 */:
                        int mode = ((Integer) VcuViewModel.this.mEnduranceMileageMode.getValue()).intValue();
                        Logger.d(VcuViewModel.TAG, "type: " + type + "; mode = " + mode + ", DriveDistance = " + newValue);
                        if (mode == 0) {
                            VcuViewModel.this.mDriveDistance.postValue(Float.valueOf(newValue != null ? ((Float) newValue).floatValue() : 0.0f));
                            return;
                        }
                        return;
                    case CarController.TYPE_CAR_ENDURANCE_MILEAGE_MODE /* 2007 */:
                        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.viewmodel.car.VcuViewModel.CarControllerCallback.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (VcuViewModel.this.mEnduranceMileageMode != newValue) {
                                    VcuViewModel.this.updateDriveDistance();
                                    VcuViewModel.this.mEnduranceMileageMode.setValue((Integer) newValue);
                                }
                            }
                        });
                        return;
                    case 2008:
                        int mode2 = ((Integer) VcuViewModel.this.mEnduranceMileageMode.getValue()).intValue();
                        Logger.d(VcuViewModel.TAG, "type: " + type + "; mode = " + mode2 + ", DriveDistance = " + newValue);
                        if (mode2 == 1) {
                            VcuViewModel.this.mDriveDistance.postValue(Float.valueOf(newValue != null ? ((Float) newValue).floatValue() : 0.0f));
                            return;
                        }
                        return;
                    case CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE /* 2009 */:
                        int mode3 = ((Integer) VcuViewModel.this.mEnduranceMileageMode.getValue()).intValue();
                        Logger.d(VcuViewModel.TAG, "type: " + type + "; mode = " + mode3 + ", DriveDistance = " + newValue);
                        if (mode3 == 2) {
                            VcuViewModel.this.mDriveDistance.postValue(Float.valueOf(newValue != null ? ((Float) newValue).floatValue() : 0.0f));
                            return;
                        }
                        return;
                    case CarController.TYPE_CAR_DYNAMIC_DRIVE_DISTANCE /* 2011 */:
                        int mode4 = ((Integer) VcuViewModel.this.mEnduranceMileageMode.getValue()).intValue();
                        Logger.d(VcuViewModel.TAG, "type: " + type + "; mode = " + mode4 + ", DriveDistance = " + newValue);
                        if (mode4 == 3) {
                            VcuViewModel.this.mDriveDistance.postValue(Float.valueOf(newValue != null ? ((Float) newValue).floatValue() : 0.0f));
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public VcuViewModel(Context context) {
        this.mContext = context;
        initLiveData();
        CarController.getInstance(this.mContext).addCallback(this.mCarCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDriveDistance() {
        CarController.CarServiceAdapter adapter = CarController.getInstance(this.mContext).getCarServiceAdapter();
        this.mDriveDistance.postValue(Float.valueOf(adapter.getDriveDistance()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initLiveData() {
        CarController.CarServiceAdapter adapter = CarController.getInstance(this.mContext).getCarServiceAdapter();
        if (adapter != null && adapter.isCarServiceReady()) {
            this.mDrivingMode.setValue(Integer.valueOf(adapter.getDrivingMode()));
            this.mElecPercent.setValue(Integer.valueOf(adapter.getElecPercent()));
            this.mDriveDistance.setValue(Float.valueOf(adapter.getDriveDistance()));
            this.mEnduranceMileageMode.setValue(Integer.valueOf(adapter.getEnduranceMileageMode()));
            this.mDisplayGearLevel.setValue(Integer.valueOf(adapter.getGearLevel()));
            this.mDcPreWarmState.setValue(Integer.valueOf(adapter.getDcPreWarmState()));
            return;
        }
        this.mDrivingMode.setValue(0);
        this.mElecPercent.setValue(0);
        this.mDriveDistance.setValue(Float.valueOf(0.0f));
        this.mEnduranceMileageMode.setValue(0);
        this.mDisplayGearLevel.setValue(0);
        this.mDcPreWarmState.setValue(0);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IVcuViewModel
    public int getDrivingMode() {
        return this.mDrivingMode.getValue().intValue();
    }

    public MutableLiveData<Integer> getDrivingModeData() {
        return this.mDrivingMode;
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IVcuViewModel
    public int getElecPercent() {
        return this.mElecPercent.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IVcuViewModel
    public float getDriveDistance() {
        return this.mDriveDistance.getValue().floatValue();
    }

    public MutableLiveData<Integer> getElecPercentData() {
        return this.mElecPercent;
    }

    public MutableLiveData<Float> getDriveDistanceData() {
        return this.mDriveDistance;
    }

    public MutableLiveData<Integer> getGearLevelData() {
        return this.mDisplayGearLevel;
    }

    public int getGearLevel() {
        return this.mDisplayGearLevel.getValue().intValue();
    }

    public MutableLiveData<Integer> getDcPreWarmStateData() {
        return this.mDcPreWarmState;
    }
}
