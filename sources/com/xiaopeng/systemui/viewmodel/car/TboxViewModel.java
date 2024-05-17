package com.xiaopeng.systemui.viewmodel.car;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.controller.CarController;
/* loaded from: classes24.dex */
public class TboxViewModel implements ITboxViewModel {
    private Context mContext;
    private final MutableLiveData<Integer> mNetworkRssi = new MutableLiveData<>();
    private final MutableLiveData<Integer> mNetworkType = new MutableLiveData<>();
    private final MutableLiveData<Integer> mNetworkRsrp = new MutableLiveData<>();
    private final MutableLiveData<Integer> mTboxConnectStatus = new MutableLiveData<>();
    private CarControllerCallback mCarCallback = new CarControllerCallback();

    /* loaded from: classes24.dex */
    private class CarControllerCallback implements CarController.CarCallback {
        private CarControllerCallback() {
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarControlChanged(int type, Object newValue) {
        }

        @Override // com.xiaopeng.systemui.controller.CarController.CarCallback
        public void onCarServiceChanged(int type, Object newValue) {
            if (type != 1000) {
                switch (type) {
                    case 3001:
                        TboxViewModel.this.mNetworkRssi.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                        return;
                    case 3002:
                        TboxViewModel.this.mNetworkType.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                        return;
                    case CarController.TYPE_TBOX_NETWORK_RSRP /* 3003 */:
                        TboxViewModel.this.mNetworkRsrp.postValue(Integer.valueOf(((Integer) newValue).intValue()));
                        return;
                    case CarController.TYPE_TBOX_CONNECT_STATUS /* 3004 */:
                        int status = ((Integer) newValue).intValue();
                        TboxViewModel.this.mTboxConnectStatus.postValue(Integer.valueOf(status));
                        if (status == 0) {
                            TboxViewModel.this.mNetworkRsrp.postValue(0);
                            TboxViewModel.this.mNetworkRssi.postValue(0);
                            TboxViewModel.this.mNetworkType.postValue(0);
                            return;
                        }
                        return;
                    default:
                        return;
                }
            }
            TboxViewModel.this.initLiveData();
        }
    }

    public TboxViewModel(Context context) {
        this.mContext = context;
        initLiveData();
        CarController.getInstance(this.mContext).addCallback(this.mCarCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initLiveData() {
        this.mNetworkRssi.setValue(0);
        this.mNetworkType.setValue(-1);
        this.mNetworkRsrp.setValue(0);
        this.mTboxConnectStatus.setValue(0);
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.ITboxViewModel
    public int getNetworkRssi() {
        return this.mNetworkRssi.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.ITboxViewModel
    public int getNetworkType() {
        return this.mNetworkType.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.car.ITboxViewModel
    public int getNetworkRsrp() {
        return this.mNetworkRsrp.getValue().intValue();
    }

    public MutableLiveData<Integer> getNetworkRssiData() {
        return this.mNetworkRssi;
    }

    public MutableLiveData<Integer> getNetworkTypeData() {
        return this.mNetworkType;
    }

    public MutableLiveData<Integer> getNetworkRsrpData() {
        return this.mNetworkRsrp;
    }

    public MutableLiveData<Integer> getTboxConnectStatusData() {
        return this.mTboxConnectStatus;
    }
}
