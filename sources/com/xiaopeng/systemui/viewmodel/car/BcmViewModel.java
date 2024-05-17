package com.xiaopeng.systemui.viewmodel.car;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.android.internal.util.ArrayUtils;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.CarController;
import java.util.Arrays;
/* loaded from: classes24.dex */
public class BcmViewModel implements IBcmViewModel {
    private static final String TAG = "BCN";
    public static final int WIRELESS_CHARGE_STATUS_CHARGE_DONE = 2;
    public static final int WIRELESS_CHARGE_STATUS_CHARGE_ERROR = 3;
    public static final int WIRELESS_CHARGE_STATUS_CHARGING = 1;
    public static final int WIRELESS_CHARGE_STATUS_NOT_CHARGING = 0;
    private Context mContext;
    private int mPsnWirelessChargeError;
    private int mPsnWirelessChargeStatus;
    private int mWirelessChargeError;
    private int mWirelessChargeStatus;
    private final MutableLiveData<int[]> mDoorState = new MutableLiveData<>();
    private final MutableLiveData<Integer> mNearLampState = new MutableLiveData<>();
    private final MutableLiveData<Integer> mHeadLampGroup = new MutableLiveData<>();
    private final MutableLiveData<Integer> mWirelessCharge = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPsnWirelessCharge = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPsnSeatHeatLevel = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPsnSeatVentLevel = new MutableLiveData<>();
    private final MutableLiveData<Integer> mSeatHeatLevel = new MutableLiveData<>();
    private final MutableLiveData<Integer> mSeatVentLevel = new MutableLiveData<>();
    private CarControllerCallback mCarCallback = new CarControllerCallback();

    /* loaded from: classes24.dex */
    private class CarControllerCallback implements CarController.CarCallback {
        private CarControllerCallback() {
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarControlChanged(int type, Object newValue) {
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarServiceChanged(int type, Object newValue) {
            if (type == 1000) {
                BcmViewModel.this.initLiveData();
            } else if (type == 3105) {
                BcmViewModel.this.mWirelessChargeStatus = ((Integer) newValue).intValue();
                BcmViewModel.this.refreshWirelessStatus();
            } else if (type == 31051) {
                BcmViewModel.this.mWirelessChargeError = ((Integer) newValue).intValue();
                BcmViewModel.this.refreshWirelessStatus();
            } else if (type != 31111) {
                switch (type) {
                    case CarController.TYPE_BCM_HEAD_LAMPS /* 3101 */:
                        if (newValue != BcmViewModel.this.mHeadLampGroup.getValue()) {
                            BcmViewModel.this.mHeadLampGroup.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                            return;
                        }
                        return;
                    case CarController.TYPE_BCM_DOOR_STATE /* 3102 */:
                        try {
                            int[] values = ArrayUtils.convertToIntArray(Arrays.asList((Integer[]) newValue));
                            if (values != BcmViewModel.this.mDoorState.getValue()) {
                                BcmViewModel.this.mDoorState.postValue(values);
                                return;
                            }
                            return;
                        } catch (Exception e) {
                            return;
                        }
                    case CarController.TYPE_BCM_NEAR_LAMPS /* 3103 */:
                        if (newValue != BcmViewModel.this.mNearLampState.getValue()) {
                            BcmViewModel.this.mNearLampState.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                            return;
                        }
                        return;
                    default:
                        switch (type) {
                            case 3107:
                                BcmViewModel.this.mPsnSeatHeatLevel.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                                return;
                            case CarController.TYPE_BCM_PSN_SEAT_VENT_LEVEL /* 3108 */:
                                BcmViewModel.this.mPsnSeatVentLevel.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                                return;
                            case CarController.TYPE_BCM_SEAT_HEAT_LEVEL /* 3109 */:
                                Log.d("BcmViewModel", "CarController.TYPE_BCM_SEAT_HEAT_LEVEL Changed: " + newValue);
                                BcmViewModel.this.mSeatHeatLevel.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                                return;
                            case CarController.TYPE_BCM_SEAT_VENT_LEVEL /* 3110 */:
                                BcmViewModel.this.mSeatVentLevel.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                                return;
                            case CarController.TYPE_BCM_FR_WIRELESS_CHARGE /* 3111 */:
                                BcmViewModel.this.mPsnWirelessChargeStatus = ((Integer) newValue).intValue();
                                BcmViewModel.this.refreshPsnWirelessStatus();
                                return;
                            default:
                                return;
                        }
                }
            } else {
                BcmViewModel.this.mPsnWirelessChargeError = ((Integer) newValue).intValue();
                BcmViewModel.this.refreshPsnWirelessStatus();
            }
        }
    }

    public BcmViewModel(Context context) {
        this.mContext = context;
        initLiveData();
        CarController.getInstance(this.mContext).addCallback(this.mCarCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initLiveData() {
        CarController.CarServiceAdapter adapter = CarController.getInstance(this.mContext).getCarServiceAdapter();
        if (adapter != null && adapter.isCarServiceReady()) {
            this.mDoorState.setValue(adapter.getDoorsState());
            this.mNearLampState.setValue(Integer.valueOf(adapter.getNearLampState()));
            this.mHeadLampGroup.setValue(Integer.valueOf(adapter.getHeadLampGroup()));
            this.mWirelessChargeStatus = adapter.getWirelessChangeStatus();
            this.mWirelessChargeError = adapter.getWirelessChangeErrorStatus();
            this.mPsnWirelessChargeStatus = adapter.getPsnWirelessChangeStatus();
            this.mPsnWirelessChargeError = adapter.getPsnWirelessChangeErrorStatus();
            this.mPsnSeatHeatLevel.setValue(Integer.valueOf(adapter.getPsnSeatHeatLevel()));
            this.mPsnSeatVentLevel.setValue(Integer.valueOf(adapter.getPsnSeatVentLevel()));
            this.mSeatHeatLevel.setValue(Integer.valueOf(adapter.getSeatHeatLevel()));
            this.mSeatVentLevel.setValue(Integer.valueOf(adapter.getSeatVentLevel()));
        } else {
            this.mDoorState.setValue(new int[]{0, 0, 0, 0});
            this.mNearLampState.setValue(0);
            this.mHeadLampGroup.setValue(0);
            this.mWirelessCharge.setValue(0);
            this.mPsnWirelessCharge.setValue(0);
            this.mPsnSeatHeatLevel.setValue(0);
            this.mPsnSeatVentLevel.setValue(0);
            this.mSeatHeatLevel.setValue(0);
            this.mSeatVentLevel.setValue(0);
        }
        refreshWirelessStatus();
        refreshPsnWirelessStatus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshWirelessStatus() {
        int code;
        MutableLiveData<Integer> wirelessCharge = this.mWirelessCharge;
        int i = this.mWirelessChargeError;
        if (i == 1 || i == 2 || i == 5 || i == 6) {
            code = 3;
        } else {
            int i2 = this.mWirelessChargeStatus;
            if (i2 == 1) {
                code = 1;
            } else if (i2 == 2) {
                code = 2;
            } else {
                code = 0;
            }
        }
        if (wirelessCharge.getValue() != null) {
            int value = wirelessCharge.getValue().intValue();
            if (value != code) {
                wirelessCharge.postValue(Integer.valueOf(code));
            }
        } else {
            wirelessCharge.postValue(Integer.valueOf(code));
        }
        Logger.i(TAG, "refreshWirelessStatus result=" + code + " ,status:" + this.mWirelessChargeStatus + " , error:" + this.mWirelessChargeError);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshPsnWirelessStatus() {
        int code;
        MutableLiveData<Integer> wirelessCharge = this.mPsnWirelessCharge;
        int i = this.mPsnWirelessChargeError;
        if (i == 1 || i == 2 || i == 5 || i == 6) {
            code = 3;
        } else {
            int i2 = this.mPsnWirelessChargeStatus;
            if (i2 == 1) {
                code = 1;
            } else if (i2 == 2) {
                code = 2;
            } else {
                code = 0;
            }
        }
        if (wirelessCharge.getValue() != null) {
            int value = wirelessCharge.getValue().intValue();
            if (value != code) {
                wirelessCharge.postValue(Integer.valueOf(code));
            }
        } else {
            wirelessCharge.postValue(Integer.valueOf(code));
        }
        Logger.i(TAG, "refreshPsnWirelessStatus result=" + code + " ,status:" + this.mPsnWirelessChargeStatus + " , error:" + this.mPsnWirelessChargeError);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IBcmViewModel
    public int getNearLampState() {
        return this.mNearLampState.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IBcmViewModel
    public int getHeadLampGroup() {
        return this.mHeadLampGroup.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IBcmViewModel
    public int[] getDoorState() {
        return this.mDoorState.getValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IBcmViewModel
    public int getWirelessChargeStatus() {
        return this.mWirelessCharge.getValue().intValue();
    }

    public int getPsnWirelessChargeStatus() {
        return this.mPsnWirelessCharge.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IBcmViewModel
    public MutableLiveData<Integer> getPsnSeatHeatLevel() {
        return this.mPsnSeatHeatLevel;
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IBcmViewModel
    public MutableLiveData<Integer> getPsnSeatVentLevel() {
        return this.mPsnSeatVentLevel;
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IBcmViewModel
    public MutableLiveData<Integer> getSeatHeatLevel() {
        return this.mSeatHeatLevel;
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.IBcmViewModel
    public MutableLiveData<Integer> getSeatVentLevel() {
        return this.mSeatVentLevel;
    }

    public void setPsnSeatHeatLevel(int level) {
        CarController.getInstance(this.mContext).getCarServiceAdapter().setPsnSeatHeatLevel(level);
    }

    public void setPsnSeatVentLevel(int level) {
        CarController.getInstance(this.mContext).getCarServiceAdapter().setPsnSeatVentLevel(level);
    }

    public void setSeatHeatLevel(int level) {
        CarController.getInstance(this.mContext).getCarServiceAdapter().setSeatHeatLevel(level);
    }

    public void setSeatVentLevel(int level) {
        CarController.getInstance(this.mContext).getCarServiceAdapter().setSeatVentLevel(level);
    }

    public MutableLiveData<int[]> getDoorStateData() {
        return this.mDoorState;
    }

    public MutableLiveData<Integer> getHeadLampGroupData() {
        return this.mHeadLampGroup;
    }

    public MutableLiveData<Integer> getNearLampStateData() {
        return this.mNearLampState;
    }

    public MutableLiveData<Integer> getWirelessChargeData() {
        return this.mWirelessCharge;
    }

    public MutableLiveData<Integer> getPsnWirelessChargeData() {
        return this.mPsnWirelessCharge;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public MutableLiveData<Integer> getSeatPropData(String key) {
        char c;
        switch (key.hashCode()) {
            case -1868200807:
                if (key.equals("driver_seat_heat_adjustment")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1756583756:
                if (key.equals("driver_seat_vent_adjustment")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 788168150:
                if (key.equals("psn_seat_heat_adjustment")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 899785201:
                if (key.equals("psn_seat_vent_adjustment")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            MutableLiveData<Integer> seatPropData = getPsnSeatHeatLevel();
            return seatPropData;
        } else if (c == 1) {
            MutableLiveData<Integer> seatPropData2 = getPsnSeatVentLevel();
            return seatPropData2;
        } else if (c == 2) {
            MutableLiveData<Integer> seatPropData3 = getSeatHeatLevel();
            return seatPropData3;
        } else if (c == 3) {
            MutableLiveData<Integer> seatPropData4 = getSeatVentLevel();
            return seatPropData4;
        } else {
            throw new IllegalStateException("GetSeatPropData Unexpected value: " + key);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public int getSeatPropValue(String key) {
        char c;
        switch (key.hashCode()) {
            case -1868200807:
                if (key.equals("driver_seat_heat_adjustment")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1756583756:
                if (key.equals("driver_seat_vent_adjustment")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 788168150:
                if (key.equals("psn_seat_heat_adjustment")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 899785201:
                if (key.equals("psn_seat_vent_adjustment")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            int seatPropValue = getPsnSeatHeatLevel().getValue().intValue();
            return seatPropValue;
        } else if (c == 1) {
            int seatPropValue2 = getPsnSeatVentLevel().getValue().intValue();
            return seatPropValue2;
        } else if (c == 2) {
            int seatPropValue3 = getSeatHeatLevel().getValue().intValue();
            return seatPropValue3;
        } else if (c == 3) {
            int seatPropValue4 = getSeatVentLevel().getValue().intValue();
            return seatPropValue4;
        } else {
            throw new IllegalStateException("getSeatPropValue Unexpected value: " + key);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void setSeatPropData(String key, int value) {
        char c;
        switch (key.hashCode()) {
            case -1868200807:
                if (key.equals("driver_seat_heat_adjustment")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1756583756:
                if (key.equals("driver_seat_vent_adjustment")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 788168150:
                if (key.equals("psn_seat_heat_adjustment")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 899785201:
                if (key.equals("psn_seat_vent_adjustment")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            setPsnSeatHeatLevel(value);
        } else if (c == 1) {
            setPsnSeatVentLevel(value);
        } else if (c == 2) {
            setSeatHeatLevel(value);
        } else if (c == 3) {
            setSeatVentLevel(value);
        } else {
            throw new IllegalStateException("SetSeatPropData Unexpected value: " + key);
        }
    }
}
