package com.xiaopeng.systemui.viewmodel.signal;

import android.content.Context;
import android.os.Looper;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.controller.NetworkController;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
/* loaded from: classes24.dex */
public class SignalViewModel implements ISignalViewModel, NetworkController.SignalCallback {
    protected static final String TAG = "SignalViewModel";
    private Context mContext;
    private NetworkController mNetworkController;
    private final MutableLiveData<Integer> mWifiLevel = new MutableLiveData<>();
    private final MutableLiveData<Integer> mWifiState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mWifiConnectionState = new MutableLiveData<>();

    public SignalViewModel(Context context) {
        this.mContext = context;
    }

    public void initViewModel(Looper looper) {
        initLiveData();
        this.mNetworkController = new NetworkController(this.mContext, looper);
        this.mNetworkController.addCallback(this);
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.viewmodel.signal.SignalViewModel.1
            @Override // java.lang.Runnable
            public void run() {
                SignalViewModel.this.mWifiConnectionState.postValue(Boolean.valueOf(SignalViewModel.this.mNetworkController.isWifiConnected() && !SignalViewModel.this.mNetworkController.isCarRecorderConnected()));
            }
        });
    }

    private void initLiveData() {
        this.mWifiLevel.setValue(0);
        this.mWifiState.setValue(0);
        this.mWifiConnectionState.setValue(false);
    }

    @Override // com.xiaopeng.systemui.controller.NetworkController.SignalCallback
    public void setWifiLevel(int level) {
        if (level != this.mWifiLevel.getValue().intValue()) {
            this.mWifiLevel.setValue(Integer.valueOf(level));
        }
    }

    @Override // com.xiaopeng.systemui.controller.NetworkController.SignalCallback
    public void setWifiState(int wifiState) {
        if (wifiState != this.mWifiState.getValue().intValue()) {
            this.mWifiState.setValue(Integer.valueOf(wifiState));
        }
    }

    @Override // com.xiaopeng.systemui.controller.NetworkController.SignalCallback
    public void setWifiConnectionState(boolean wifiConnectionState) {
        MutableLiveData<Boolean> mutableLiveData = this.mWifiConnectionState;
        if (mutableLiveData != null) {
            mutableLiveData.setValue(Boolean.valueOf(wifiConnectionState));
        }
    }

    @Override // com.xiaopeng.systemui.viewmodel.signal.ISignalViewModel
    public int getWifiLevel() {
        return this.mWifiLevel.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.signal.ISignalViewModel
    public int getWifiState() {
        return this.mWifiState.getValue().intValue();
    }

    @Override // com.xiaopeng.systemui.viewmodel.signal.ISignalViewModel
    public boolean isWifiConnected() {
        Boolean wifiConnected = this.mWifiConnectionState.getValue();
        if (wifiConnected != null) {
            return wifiConnected.booleanValue();
        }
        return false;
    }

    public MutableLiveData<Integer> getWifiLevelData() {
        return this.mWifiLevel;
    }

    public MutableLiveData<Integer> getWifiStateData() {
        return this.mWifiState;
    }

    public MutableLiveData<Boolean> getWifiConnectionStateData() {
        return this.mWifiConnectionState;
    }
}
