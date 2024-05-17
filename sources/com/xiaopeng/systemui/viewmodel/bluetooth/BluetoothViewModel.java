package com.xiaopeng.systemui.viewmodel.bluetooth;

import android.content.Context;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.controller.BluetoothController;
/* loaded from: classes24.dex */
public class BluetoothViewModel implements IBluetoothViewModel, BluetoothController.Callback, BluetoothController.PsnBluetoothCallback {
    private BluetoothController mBluetoothController;
    private Context mContext;
    private final MutableLiveData<Integer> mBluetoothState = new MutableLiveData<>();
    private final MutableLiveData<Integer> mBluetoothNumber = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPsnBluetoothState = new MutableLiveData<>();

    public BluetoothViewModel(Context context) {
        this.mContext = context;
        initLiveData();
    }

    public void initViewModel(Looper looper) {
        this.mBluetoothController = BluetoothController.getInstance();
        this.mBluetoothController.addBluetoothCallback(this);
        this.mBluetoothController.addPsnBluetoothCallback(this);
        initLiveData();
    }

    private void initLiveData() {
        BluetoothController bluetoothController = this.mBluetoothController;
        if (bluetoothController == null) {
            this.mBluetoothState.setValue(10);
            this.mBluetoothNumber.setValue(0);
            this.mPsnBluetoothState.setValue(10);
            return;
        }
        this.mBluetoothState.setValue(Integer.valueOf(bluetoothController.getBluetoothState()));
        this.mBluetoothNumber.setValue(Integer.valueOf(this.mBluetoothController.getBluetoothNumber()));
        this.mPsnBluetoothState.setValue(Integer.valueOf(this.mBluetoothController.getPsnBluetoothState()));
    }

    @Override // com.xiaopeng.systemui.controller.BluetoothController.Callback
    public void onBluetoothChanged(int state, int number) {
        if (state != this.mBluetoothState.getValue().intValue()) {
            this.mBluetoothState.setValue(Integer.valueOf(state));
        }
        if (number != this.mBluetoothNumber.getValue().intValue()) {
            this.mBluetoothNumber.setValue(Integer.valueOf(number));
        }
    }

    @Override // com.xiaopeng.systemui.viewmodel.bluetooth.IBluetoothViewModel
    public int getBluetoothNumber() {
        return this.mBluetoothNumber.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.bluetooth.IBluetoothViewModel
    public int getBluetoothState() {
        return this.mBluetoothState.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.bluetooth.IBluetoothViewModel
    public int getPsnBluetoothState() {
        return this.mPsnBluetoothState.getValue().intValue();
    }

    public LiveData<Integer> getPsnBluetoothStateData() {
        return this.mPsnBluetoothState;
    }

    public boolean isBluetoothConnected() {
        return this.mBluetoothState.getValue().intValue() == 2;
    }

    public static int getBluetoothLevel(int state) {
        if (state != 0) {
            if (state != 1) {
                if (state != 2) {
                    switch (state) {
                        case 10:
                            return 0;
                        case 11:
                            return 1;
                        case 12:
                            return 2;
                        default:
                            return 0;
                    }
                }
                return 4;
            }
            return 3;
        }
        return 2;
    }

    public MutableLiveData<Integer> getBluetoothStateData() {
        return this.mBluetoothState;
    }

    public MutableLiveData<Integer> getBluetoothNumberData() {
        return this.mBluetoothNumber;
    }

    @Override // com.xiaopeng.systemui.controller.BluetoothController.PsnBluetoothCallback
    public void onPsnBluetoothStateChanged(int state) {
        this.mPsnBluetoothState.postValue(Integer.valueOf(state));
    }
}
