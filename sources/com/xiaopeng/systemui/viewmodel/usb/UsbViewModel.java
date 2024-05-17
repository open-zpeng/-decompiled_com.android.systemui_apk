package com.xiaopeng.systemui.viewmodel.usb;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.UsbController;
/* loaded from: classes24.dex */
public class UsbViewModel implements IUsbViewModel, UsbController.OnUsbListener {
    private static final String TAG = "UsbViewModel";
    private Context mContext;
    private final MutableLiveData<Integer> mUsbType = new MutableLiveData<>();

    public UsbViewModel(Context context) {
        this.mContext = context;
        UsbController.getInstance(this.mContext).setUsbListener(this);
        initLiveData();
    }

    private void initLiveData() {
        int currentUsbStatus = UsbController.getInstance(this.mContext).getCurrentUsbStatus();
        this.mUsbType.setValue(Integer.valueOf(currentUsbStatus));
    }

    @Override // com.xiaopeng.systemui.controller.UsbController.OnUsbListener
    public void onUsbTypeChanged(int type) {
        Logger.d(TAG, "onUsbTypeChanged : " + type);
        this.mUsbType.postValue(Integer.valueOf(type));
    }

    @Override // com.xiaopeng.systemui.viewmodel.usb.IUsbViewModel
    public int getUsbType() {
        return this.mUsbType.getValue().intValue();
    }

    public MutableLiveData<Integer> getUsbTypeData() {
        return this.mUsbType;
    }
}
