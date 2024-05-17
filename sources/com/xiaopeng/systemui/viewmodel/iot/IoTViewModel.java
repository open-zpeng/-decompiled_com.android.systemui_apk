package com.xiaopeng.systemui.viewmodel.iot;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.xiaopeng.systemui.viewmodel.IViewModel;
import com.xiaopeng.xuimanager.iot.BaseDevice;
import com.xiaopeng.xuimanager.iot.IDeviceListener;
import com.xiaopeng.xuimanager.iot.IoTManager;
import java.util.List;
import java.util.Map;
/* loaded from: classes24.dex */
public class IoTViewModel implements IViewModel {
    public static final int IOT_CHILD_SAFETY_SEAT_CONNECTED = 1;
    public static final int IOT_CHILD_SAFETY_SEAT_NOT_CONNECTED = 0;
    public static final int IOT_DEVICE_NOT_CONNECTED = -1;
    private static final String TAG = IoTViewModel.class.getSimpleName();
    private BaseDevice mBaseDevice;
    private IoTManager mIoTManager = IoTManager.getInstance();
    private MutableLiveData<Integer> mStatus = new MutableLiveData<>();
    private IDeviceListener mIoTDeviceListener = new IDeviceListener() { // from class: com.xiaopeng.systemui.viewmodel.iot.IoTViewModel.1
        public void onDeviceAdd(List<BaseDevice> list) {
            String str = IoTViewModel.TAG;
            Log.d(str, "@" + this + ",onDeviceAdd,size=" + list.size());
            BaseDevice device = list.get(0);
            if ("SafetySeat-GlobalKids".equals(device.getDeviceType())) {
                if (IoTViewModel.this.mBaseDevice != null) {
                    IoTViewModel ioTViewModel = IoTViewModel.this;
                    ioTViewModel.unSubscribeNotification(ioTViewModel.mBaseDevice);
                }
                IoTViewModel.this.mBaseDevice = device;
                String str2 = IoTViewModel.TAG;
                Log.d(str2, "device: " + IoTViewModel.this.mBaseDevice + "add!");
                IoTViewModel ioTViewModel2 = IoTViewModel.this;
                ioTViewModel2.updateChildSafetySeatStatus(ioTViewModel2.mBaseDevice.getPropertyMap());
                IoTViewModel ioTViewModel3 = IoTViewModel.this;
                ioTViewModel3.subscribeNotifications(ioTViewModel3.mBaseDevice);
            }
        }

        public void onPropertiesUpdated(String deviceId, Map<String, String> map) {
            String str = IoTViewModel.TAG;
            Log.d(str, "@" + this + "onPropertiesUpdated,id=" + deviceId);
            if (deviceId.equals(IoTViewModel.this.mBaseDevice.getDeviceId())) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String str2 = IoTViewModel.TAG;
                    Log.d(str2, "key:" + entry.getKey() + ",value:" + entry.getValue());
                    if ("isofix_stat".equals(entry.getKey()) || "connect_state".equals(entry.getKey())) {
                        IoTViewModel.this.updateChildSafetySeatStatus(map);
                    }
                }
            }
        }

        public void onOperationResult(String s, String s1, String s2) {
            String str = IoTViewModel.TAG;
            Log.d(str, "@" + this + "onOperationResult id=" + s + ",result=" + s2);
        }
    };

    public LiveData<Integer> getDeviceStatus() {
        return this.mStatus;
    }

    public IoTViewModel(Context context) {
        this.mIoTManager.reset();
        this.mIoTManager.init(context.getApplicationContext());
        List<BaseDevice> mDeviceList = getDevice("by_dev_type", "SafetySeat-GlobalKids");
        this.mStatus.setValue(-1);
        this.mIoTManager.registerListener(this.mIoTDeviceListener);
        if (mDeviceList != null && mDeviceList.size() > 0) {
            this.mBaseDevice = mDeviceList.get(0);
            String str = TAG;
            Log.d(str, "device: " + this.mBaseDevice + "add!");
            updateChildSafetySeatStatus(this.mBaseDevice.getPropertyMap());
            subscribeNotifications(this.mBaseDevice);
        }
    }

    public List<BaseDevice> getDevice(String getByType, String type) {
        return this.mIoTManager.getDevice("by_dev_type", "SafetySeat-GlobalKids");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void subscribeNotifications(BaseDevice device) {
        this.mIoTManager.subscribeNotifications(device);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unSubscribeNotification(BaseDevice device) {
        this.mIoTManager.unSubscribeNotifications(device);
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x0083, code lost:
        if (r2.equals("0") != false) goto L22;
     */
    /* JADX WARN: Removed duplicated region for block: B:50:0x008f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0089 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateChildSafetySeatStatus(java.util.Map<java.lang.String, java.lang.String> r10) {
        /*
            r9 = this;
            java.util.Set r0 = r10.keySet()
            java.util.Iterator r0 = r0.iterator()
        L8:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L9c
            java.lang.Object r1 = r0.next()
            java.lang.String r1 = (java.lang.String) r1
            int r2 = r1.hashCode()
            r3 = -795621124(0xffffffffd093c8fc, float:-1.98353797E10)
            r4 = 0
            r5 = -1
            r6 = 1
            if (r2 == r3) goto L30
            r3 = 13876163(0xd3bbc3, float:1.9444646E-38)
            if (r2 == r3) goto L26
        L25:
            goto L3a
        L26:
            java.lang.String r2 = "isofix_stat"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L25
            r2 = r6
            goto L3b
        L30:
            java.lang.String r2 = "connect_state"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L25
            r2 = r4
            goto L3b
        L3a:
            r2 = r5
        L3b:
            if (r2 == 0) goto L50
            if (r2 == r6) goto L40
            goto L9a
        L40:
            java.lang.Object r2 = r10.get(r1)
            java.lang.String r2 = (java.lang.String) r2
            androidx.lifecycle.MutableLiveData<java.lang.Integer> r3 = r9.mStatus
            java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
            r3.postValue(r4)
            goto L9a
        L50:
            java.lang.Object r2 = r10.get(r1)
            java.lang.String r2 = (java.lang.String) r2
            int r3 = r2.hashCode()
            r7 = 48
            r8 = 2
            if (r3 == r7) goto L7d
            r4 = 49
            if (r3 == r4) goto L73
            r4 = 48625(0xbdf1, float:6.8138E-41)
            if (r3 == r4) goto L69
        L68:
            goto L86
        L69:
            java.lang.String r3 = "100"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L68
            r4 = r8
            goto L87
        L73:
            java.lang.String r3 = "1"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L68
            r4 = r6
            goto L87
        L7d:
            java.lang.String r3 = "0"
            boolean r3 = r2.equals(r3)
            if (r3 == 0) goto L68
            goto L87
        L86:
            r4 = r5
        L87:
            if (r4 == 0) goto L8f
            if (r4 == r6) goto L8e
            if (r4 == r8) goto L8e
            goto L99
        L8e:
            goto L99
        L8f:
            androidx.lifecycle.MutableLiveData<java.lang.Integer> r3 = r9.mStatus
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r3.postValue(r4)
        L99:
        L9a:
            goto L8
        L9c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.viewmodel.iot.IoTViewModel.updateChildSafetySeatStatus(java.util.Map):void");
    }
}
