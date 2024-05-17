package com.xiaopeng.systemui.carmanager.impl;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.vcu.CarVcuManager;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carmanager.BaseCarController;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.carmanager.controller.IVcuController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes24.dex */
public class VcuController extends BaseCarController<CarVcuManager, IVcuController.Callback> implements IVcuController {
    protected static final String TAG = "VcuController";
    private final CarVcuManager.CarVcuEventCallback mCarVcuEventCallback = new CarVcuManager.CarVcuEventCallback() { // from class: com.xiaopeng.systemui.carmanager.impl.VcuController.1
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            Logger.d(VcuController.TAG, "onChangeEvent: " + carPropertyValue);
            VcuController.this.handleCarEventsUpdate(carPropertyValue);
        }

        public void onErrorEvent(int propertyId, int zone) {
            Logger.d(VcuController.TAG, "onErrorEvent: " + propertyId);
        }
    };

    /* JADX WARN: Type inference failed for: r0v4, types: [C, android.car.hardware.vcu.CarVcuManager] */
    public VcuController(Car carClient) {
        try {
            this.mCarManager = (CarVcuManager) carClient.getCarManager(CarClientWrapper.XP_VCU_SERVICE);
        } catch (CarNotConnectedException e) {
        }
    }

    public static BaseCarController createCarController(Car carClient) {
        return new VcuController(carClient);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r1v5, types: [C, android.car.hardware.vcu.CarVcuManager] */
    @Override // com.xiaopeng.systemui.carmanager.BaseCarController
    public void initCarManager(Car carClient) {
        Logger.d(TAG, "Init start");
        try {
            this.mCarManager = (CarVcuManager) carClient.getCarManager(CarClientWrapper.XP_VCU_SERVICE);
            if (this.mCarManager != 0) {
                ((CarVcuManager) this.mCarManager).registerPropCallback(this.mPropertyIds, this.mCarVcuEventCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.d(TAG, "Init end");
    }

    @Override // com.xiaopeng.systemui.carmanager.BaseCarController
    protected List<Integer> getRegisterPropertyIds() {
        List<Integer> propertyIds = new ArrayList<>();
        propertyIds.add(557847045);
        return propertyIds;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.carmanager.BaseCarController
    public void disconnect() {
        if (this.mCarManager != 0) {
            try {
                ((CarVcuManager) this.mCarManager).unregisterPropCallback(this.mPropertyIds, this.mCarVcuEventCallback);
            } catch (CarNotConnectedException e) {
            }
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.BaseCarController
    protected void handleEventsUpdate(CarPropertyValue<?> value) {
        if (value.getPropertyId() == 557847045) {
            handleGearUpdate(((Integer) getValue(value)).intValue());
            return;
        }
        Logger.d(TAG, "handle unknown event: " + value);
    }

    private void handleGearUpdate(int gear) {
        synchronized (this.mCallbackLock) {
            Iterator it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                IVcuController.Callback callback = (IVcuController.Callback) it.next();
                callback.onGearChanged(gear);
            }
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.controller.IVcuController
    public int getGearLevel() {
        try {
            int value = getIntProperty(557847045);
            return value;
        } catch (Exception e) {
            try {
                int value2 = ((CarVcuManager) this.mCarManager).getDisplayGearLevel();
                return value2;
            } catch (Exception e2) {
                return 0;
            }
        }
    }

    @Override // com.xiaopeng.systemui.carmanager.controller.IVcuController
    public int getAvailableMileage() {
        int value;
        try {
            value = getIntProperty(557847057);
        } catch (Exception e) {
            try {
                int value2 = ((CarVcuManager) this.mCarManager).getAvalibleDrivingDistance();
                value = value2;
            } catch (Exception e2) {
                Logger.d(TAG, "getAvailableMileage failed: " + e.getMessage());
                value = -1;
            }
        }
        Logger.i(TAG, "current AvailableMileage: " + value);
        return value;
    }
}
