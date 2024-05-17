package com.xiaopeng.systemui.carmanager;

import android.car.Car;
import android.car.hardware.atl.CarAtlManager;
import android.car.hardware.bcm.CarBcmManager;
import android.car.hardware.hvac.CarHvacManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carmanager.controller.IVcuController;
import com.xiaopeng.systemui.carmanager.impl.IcmController;
import com.xiaopeng.systemui.carmanager.impl.VcuController;
import com.xiaopeng.systemui.carmanager.impl.VcuControllerWrapper;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class CarClientWrapper {
    private CarAtlManager mCarAtlManager;
    private CarBcmManager mCarBcmManager;
    private Car mCarClient;
    private Context mContext;
    private CarHvacManager mHvacManager;
    private static final String TAG = CarClientWrapper.class.getSimpleName();
    public static final String XP_VCU_SERVICE = "xp_vcu";
    public static final String XP_ICM_SERVICE = "xp_icm";
    private static final String[] CAR_SVC_ARRAY = {XP_VCU_SERVICE, XP_ICM_SERVICE};
    private final HashMap<String, BaseCarController<?, ?>> mControllers = new HashMap<>();
    private final ServiceConnection mCarConnectionCb = new ServiceConnection() { // from class: com.xiaopeng.systemui.carmanager.CarClientWrapper.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(CarClientWrapper.TAG, "on Car Service Connected");
            CarClientWrapper.this.initCarATLServiceManager();
            CarClientWrapper.this.initCarHvacManager();
            CarClientWrapper.this.initCarBcmManager();
            CarClientWrapper.this.initCarControllers();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(CarClientWrapper.TAG, "on Car Service  Disconnected");
            CarClientWrapper.this.reconnectToCar();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final CarClientWrapper sInstance = new CarClientWrapper();

        private SingleHolder() {
        }
    }

    public static CarClientWrapper getInstance() {
        return SingleHolder.sInstance;
    }

    public void connectToCar(Context context) {
        this.mContext = context;
        this.mCarClient = Car.createCar(context, this.mCarConnectionCb);
        Logger.d(TAG, "Start to connect Car service");
        this.mCarClient.connect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initCarControllers() {
        String[] strArr;
        for (String serviceName : CAR_SVC_ARRAY) {
            BaseCarController controller = this.mControllers.get(serviceName);
            if (controller == null) {
                this.mControllers.put(serviceName, createCarController(serviceName, this.mCarClient));
            } else {
                controller.disconnect();
                controller.initCarManager(this.mCarClient);
            }
        }
    }

    public BaseCarController getController(String serviceName) {
        return this.mControllers.get(serviceName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reconnectToCar() {
        connectToCar(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initCarATLServiceManager() {
        try {
            this.mCarAtlManager = (CarAtlManager) this.mCarClient.getCarManager("xp_atl");
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initCarHvacManager() {
        try {
            this.mHvacManager = (CarHvacManager) this.mCarClient.getCarManager("hvac");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initCarBcmManager() {
        try {
            this.mCarBcmManager = (CarBcmManager) this.mCarClient.getCarManager("xp_bcm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CarHvacManager getHvacManager() {
        Logger.d(TAG, "getHvacManager");
        return this.mHvacManager;
    }

    public CarAtlManager getCarAtlManager() {
        Logger.d(TAG, "getCarAtlManager");
        return this.mCarAtlManager;
    }

    public CarBcmManager getCarBcmManager() {
        Logger.d(TAG, "getCarAtlManager");
        return this.mCarBcmManager;
    }

    private BaseCarController createCarController(String serviceName, Car carClient) {
        char c;
        BaseCarController controller;
        int hashCode = serviceName.hashCode();
        if (hashCode != -753100596) {
            if (hashCode == -753088095 && serviceName.equals(XP_VCU_SERVICE)) {
                c = 1;
            }
            c = 65535;
        } else {
            if (serviceName.equals(XP_ICM_SERVICE)) {
                c = 0;
            }
            c = 65535;
        }
        if (c == 0) {
            controller = IcmController.createCarController(carClient);
        } else if (c == 1) {
            controller = VcuController.createCarController(carClient);
            VcuControllerWrapper.getInstance().setVcuController((IVcuController) controller);
        } else {
            throw new IllegalArgumentException("Can not create controller for " + serviceName);
        }
        if (controller != null) {
            controller.initCarManager(carClient);
        }
        return controller;
    }
}
