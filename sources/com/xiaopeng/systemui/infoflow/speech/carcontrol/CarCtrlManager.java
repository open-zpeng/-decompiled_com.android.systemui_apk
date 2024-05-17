package com.xiaopeng.systemui.infoflow.speech.carcontrol;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.atl.CarAtlManager;
import android.car.hardware.bcm.CarBcmManager;
import android.car.hardware.hvac.CarHvacManager;
import android.car.hardware.icm.CarIcmManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.infoflow.icm.IcmManager;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xuimanager.ambientlight.AmbientLightManager;
/* loaded from: classes24.dex */
public class CarCtrlManager {
    private static final String TAG = "CarCtrlManager";
    private static volatile CarCtrlManager mInstance;
    private AmbientLightManager.AmbientLightEventListener mAmbientLightEventListener;
    private Car mCar;
    private CarAtlManager mCarAtlManager;
    private CarBcmManager mCarBcmManager;
    private CarIcmManager mCarIcmManager;
    private Context mContext;
    private CarHvacManager mHvacManager;
    private final ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CarCtrlManager.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            CarCtrlManager.this.initCarATLServiceManager();
            CarCtrlManager.this.initCarHvacManager();
            CarCtrlManager.this.initCarIcmManager();
            CarCtrlManager.this.initCarBcmManager();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void initCarBcmManager() {
        try {
            this.mCarBcmManager = (CarBcmManager) this.mCar.getCarManager("xp_bcm");
        } catch (Exception e) {
            handleException(e);
        }
    }

    public static CarCtrlManager getInstance() {
        if (mInstance == null) {
            synchronized (IcmManager.class) {
                if (mInstance == null) {
                    mInstance = new CarCtrlManager();
                }
            }
        }
        return mInstance;
    }

    public synchronized void init(Context context) {
        if (this.mCar != null) {
            Logger.w(TAG, "the icm have been init");
            return;
        }
        this.mContext = context;
        this.mCar = Car.createCar(this.mContext, this.mServiceConnection);
        this.mCar.connect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initCarATLServiceManager() {
        try {
            this.mCarAtlManager = (CarAtlManager) this.mCar.getCarManager("xp_atl");
        } catch (Exception e) {
            handleException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initCarIcmManager() {
        try {
            this.mCarIcmManager = (CarIcmManager) this.mCar.getCarManager(CarClientWrapper.XP_ICM_SERVICE);
        } catch (Exception e) {
            handleException(e);
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

    public CarIcmManager getCarIcmManager() {
        Logger.d(TAG, "getCarIcmManager");
        return this.mCarIcmManager;
    }

    public CarBcmManager getCarBcmManager() {
        Logger.d(TAG, "getCarBcmManager");
        return this.mCarBcmManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initCarHvacManager() {
        try {
            this.mHvacManager = (CarHvacManager) this.mCar.getCarManager("hvac");
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
    }

    void handleException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            Logger.e(TAG, "IllegalArgumentException:" + e);
            return;
        }
        Logger.e(TAG, e.toString());
    }
}
