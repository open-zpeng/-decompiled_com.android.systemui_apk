package com.xiaopeng.systemui.quickmenu;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.hvac.CarHvacManager;
import android.car.hardware.mcu.CarMcuManager;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class CarSettingsManager {
    private static final String TAG = "CarSettingsManager";
    private static CarSettingsManager sInstance;
    private CarHvacManager mCarHvacManager;
    private CarMcuManager mCarMcuManager;
    private List<OnServiceConnectCompleteListener> mOnServiceConnectCompleteListenerList = new ArrayList();
    private Car mCarApiClient = Car.createCar(ContextUtils.getContext(), new ServiceConnection() { // from class: com.xiaopeng.systemui.quickmenu.CarSettingsManager.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            CarSettingsManager.this.initCarHvacManager();
            CarSettingsManager.this.initCarMcuManager();
            Log.d(CarSettingsManager.TAG, "xpcarservice init completed!");
            CarSettingsManager.this.notifyServiceConnectComplete();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
        }
    });

    /* loaded from: classes24.dex */
    public interface OnServiceConnectCompleteListener {
        void onServiceConnectComplete();
    }

    public void addServiceConnectCompleteListener(OnServiceConnectCompleteListener listener) {
        if (!this.mOnServiceConnectCompleteListenerList.contains(listener)) {
            this.mOnServiceConnectCompleteListenerList.add(listener);
        }
    }

    public void removeServiceConnectCompleteListener(OnServiceConnectCompleteListener listener) {
        this.mOnServiceConnectCompleteListenerList.remove(listener);
    }

    public void notifyServiceConnectComplete() {
        for (OnServiceConnectCompleteListener listener : this.mOnServiceConnectCompleteListenerList) {
            listener.onServiceConnectComplete();
        }
    }

    public static CarSettingsManager getInstance() {
        if (sInstance == null) {
            synchronized (CarSettingsManager.class) {
                if (sInstance == null) {
                    sInstance = new CarSettingsManager();
                }
            }
        }
        return sInstance;
    }

    private CarSettingsManager() {
        registerCar();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initCarMcuManager() {
        try {
            this.mCarMcuManager = (CarMcuManager) this.mCarApiClient.getCarManager("xp_mcu");
            Log.d(TAG, "carservice init CarMCU manager");
        } catch (Exception e) {
            e.printStackTrace();
        } catch (CarNotConnectedException e2) {
            Log.e(TAG, "Car not connected");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initCarHvacManager() {
        try {
            this.mCarHvacManager = (CarHvacManager) this.mCarApiClient.getCarManager("hvac");
            Log.d(TAG, "carservice init hvac manager");
        } catch (Exception e) {
            e.printStackTrace();
        } catch (CarNotConnectedException e2) {
            Log.d(TAG, "Car not connected");
        }
    }

    private void registerCar() {
        Log.d(TAG, "xpsettings carservice connect");
        this.mCarApiClient.connect();
    }

    public void releaseCar() {
        Car car = this.mCarApiClient;
        if (car != null) {
            car.disconnect();
        }
    }

    public int getMinWindSpeed() {
        int minWind = 0;
        Log.d(TAG, "carservice getMinWindSpeed ");
        CarHvacManager carHvacManager = this.mCarHvacManager;
        if (carHvacManager == null) {
            return 0;
        }
        try {
            minWind = carHvacManager.getMinWindSpeedLevel();
            Log.d(TAG, "carservice getMinWindSpeed over minWind:" + minWind);
            return minWind;
        } catch (Throwable e) {
            e.printStackTrace();
            return minWind;
        }
    }

    public int getMaxWindSpeed() {
        int maxWind = 0;
        Log.d(TAG, "carservice getMaxWindSpeed ");
        CarHvacManager carHvacManager = this.mCarHvacManager;
        if (carHvacManager == null) {
            return 0;
        }
        try {
            maxWind = carHvacManager.getMaxWindSpeedLevel();
            Log.d(TAG, "carservice getMaxWindSpeed over maxWind:" + maxWind);
            return maxWind;
        } catch (Throwable e) {
            e.printStackTrace();
            return maxWind;
        }
    }

    public int getMinTemperature() {
        int minTemperature = 0;
        Log.d(TAG, "carservice getMaxWindSpeed ");
        CarHvacManager carHvacManager = this.mCarHvacManager;
        if (carHvacManager == null) {
            return 0;
        }
        try {
            minTemperature = carHvacManager.getMinHavcTemperature();
            Log.d(TAG, "carservice getMaxWindSpeed over minTemperature:" + minTemperature);
            return minTemperature;
        } catch (Throwable e) {
            e.printStackTrace();
            return minTemperature;
        }
    }

    public int getMaxTemperature() {
        int maxTemperature = 0;
        Log.d(TAG, "carservice getMaxWindSpeed ");
        CarHvacManager carHvacManager = this.mCarHvacManager;
        if (carHvacManager == null) {
            return 0;
        }
        try {
            maxTemperature = carHvacManager.getMaxHavcTemperature();
            Log.d(TAG, "carservice getMaxWindSpeed over maxTemperature:" + maxTemperature);
            return maxTemperature;
        } catch (Throwable e) {
            e.printStackTrace();
            return maxTemperature;
        }
    }

    public boolean hasCiuDevice() {
        int state = 2;
        CarMcuManager carMcuManager = this.mCarMcuManager;
        if (carMcuManager != null) {
            try {
                state = carMcuManager.getCiuState();
                Log.d(TAG, "carservice getCiuState:" + state);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return state == 1;
    }
}
