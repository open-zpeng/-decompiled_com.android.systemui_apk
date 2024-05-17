package com.xiaopeng.libcarcontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import com.xiaopeng.carcontrol.provider.CarControl;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes23.dex */
public final class CarControlManager {
    private static final String TAG = "CarControlManager";
    @SuppressLint({"StaticFieldLeak"})
    private static volatile CarControlManager sInstance;
    private final Object mCallbackLock = new Object();
    private final List<CarControlCallback> mCallbacks = new ArrayList();
    private Handler mContentHandler;
    private ContentObserver mContentObserver;
    private Context mContext;
    private ContentObserver mQuickContentObserver;

    public static CarControlManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CarControlManager.class) {
                if (sInstance == null) {
                    sInstance = new CarControlManager(context);
                }
            }
        }
        return sInstance;
    }

    private CarControlManager(Context context) {
        Log.d(TAG, TAG);
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("CarControl-ContentObserver");
        handlerThread.start();
        this.mContentHandler = new Handler(handlerThread.getLooper());
        registerObserver();
    }

    public void release() {
        this.mCallbacks.clear();
        unregisterContentObserver();
        this.mContext = null;
    }

    public void registerCallback(CarControlCallback callback) {
        if (callback != null) {
            synchronized (this.mCallbackLock) {
                this.mCallbacks.add(callback);
            }
        }
    }

    public void unregisterCallback(CarControlCallback callback) {
        if (callback != null) {
            synchronized (this.mCallbackLock) {
                this.mCallbacks.remove(callback);
            }
        }
    }

    public boolean setHvacPower(boolean onOff) {
        return CarControl.System.putBool(this.mContext.getContentResolver(), CarControl.System.HVAC_POWER_SET, onOff);
    }

    public boolean isHvacPowerOn() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.HVAC_POWER, false);
    }

    public boolean isHvacAutoMode() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.HVAC_AUTO, false);
    }

    public int getHvacFanSpeed() {
        return CarControl.System.getInt(this.mContext.getContentResolver(), CarControl.System.HVAC_WIND_LEVEL, 0);
    }

    public boolean setHvacDriverTemp(float temp) {
        return CarControl.System.putFloat(this.mContext.getContentResolver(), CarControl.System.HVAC_DRV_TEMP_SET, temp);
    }

    public boolean setHvacDriverStep(boolean isUp) {
        return CarControl.System.putBool(this.mContext.getContentResolver(), CarControl.System.HVAC_DRV_TEMP_STEP_SET, isUp);
    }

    public float getHvacDriverTemp() {
        return CarControl.System.getFloat(this.mContext.getContentResolver(), CarControl.System.HVAC_DRV_TEMP, 18.0f);
    }

    public boolean setHvacDriverSyncMode(boolean enable) {
        return CarControl.System.putBool(this.mContext.getContentResolver(), CarControl.System.HVAC_DRV_SYNC_SET, enable);
    }

    public boolean isHvacDriverSyncEnabled() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.HVAC_DRV_SYNC, false);
    }

    public boolean setHvacPsnTemp(float temp) {
        return CarControl.System.putFloat(this.mContext.getContentResolver(), CarControl.System.HVAC_PSN_TEMP_SET, temp);
    }

    public boolean setHvacPsnStep(boolean isUp) {
        return CarControl.System.putBool(this.mContext.getContentResolver(), CarControl.System.HVAC_PSN_TEMP_STEP_SET, isUp);
    }

    public float getHvacPsnTemp() {
        return CarControl.System.getFloat(this.mContext.getContentResolver(), CarControl.System.HVAC_PSN_TEMP, 18.0f);
    }

    public boolean setHvacPsnSyncMode(boolean enable) {
        return CarControl.System.putBool(this.mContext.getContentResolver(), CarControl.System.HVAC_PSN_SYNC_SET, enable);
    }

    public boolean isHvacPsnSyncEnabled() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.HVAC_PSN_SYNC, false);
    }

    public boolean setHvacFrontDefrostEnable(boolean enable) {
        return CarControl.System.putBool(this.mContext.getContentResolver(), CarControl.System.HVAC_FRONT_DEFROST_SET, enable);
    }

    public boolean isHvacFrontDefrostEnable() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.HVAC_FRONT_DEFROST, false);
    }

    public boolean setHvacBackDefrostEnable(boolean enable) {
        return CarControl.System.putBool(this.mContext.getContentResolver(), CarControl.System.HVAC_BACK_DEFROST_SET, enable);
    }

    public boolean isHvacBackDefrostEnable() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.HVAC_BACK_DEFROST, false);
    }

    public int getHvacInnerAq() {
        return CarControl.System.getInt(this.mContext.getContentResolver(), CarControl.System.HVAC_PM25, 0);
    }

    public float getHvacExternalTemp() {
        return CarControl.System.getFloat(this.mContext.getContentResolver(), CarControl.System.HVAC_OUT_TEMP, 0.0f);
    }

    public int getHvacWindModeColor() {
        return CarControl.System.getInt(this.mContext.getContentResolver(), CarControl.System.HVAC_WIND_MODE_COLOR, 0);
    }

    public boolean getHvacAirPurgeMode() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.HVAC_AIR_PURGE_MODE, false);
    }

    public boolean getHvacBlowerCtrlType() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.HVAC_BLOWER_CTRL_TYPE, false);
    }

    public boolean setCentralLock(boolean locked) {
        return CarControl.System.putBool(this.mContext.getContentResolver(), CarControl.System.CENTRAL_LOCK_SET, locked);
    }

    public boolean isCentralLockOn() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.CENTRAL_LOCK, false);
    }

    public boolean isDrvSeatOccupied() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.DRV_OCCUPIED, false);
    }

    public boolean isPsnSeatOccupied() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.PSN_OCCUPIED, false);
    }

    public boolean isCarControlReady() {
        return CarControl.System.getBool(this.mContext.getContentResolver(), CarControl.System.CAR_CONTROL_READY, false);
    }

    public ChargeStatus getChargeStatus() {
        int chargeStatus = CarControl.System.getInt(this.mContext.getContentResolver(), CarControl.System.CHARGE_STATUS, 0);
        return ChargeStatus.fromVcuChargeStatus(chargeStatus);
    }

    public int getElecPercent() {
        return CarControl.System.getInt(this.mContext.getContentResolver(), CarControl.System.BATTERY_PERCENT, 0);
    }

    public int getDriveDistance() {
        return CarControl.System.getInt(this.mContext.getContentResolver(), CarControl.System.AVAILABLE_DISTANCE, 0);
    }

    public boolean openRearTrunk() {
        return CarControl.Quick.putInt(this.mContext.getContentResolver(), CarControl.Quick.OPEN_REAR_TRUNK, 2);
    }

    public ChargePortState getChargePortState(boolean leftSide) {
        int state = CarControl.Quick.getInt(this.mContext.getContentResolver(), leftSide ? CarControl.Quick.LEFT_CHARGE_PORT_STATE : CarControl.Quick.RIGHT_CHARGE_PORT_STATE, -1);
        return ChargePortState.fromBcmValue(state);
    }

    public boolean openChargePort(boolean leftSide) {
        return CarControl.Quick.putInt(this.mContext.getContentResolver(), leftSide ? CarControl.Quick.LEFT_CHARGE_PORT : CarControl.Quick.RIGHT_CHARGE_PORT, 1);
    }

    public boolean closeChargePort(boolean leftSide) {
        return CarControl.Quick.putInt(this.mContext.getContentResolver(), leftSide ? CarControl.Quick.LEFT_CHARGE_PORT : CarControl.Quick.RIGHT_CHARGE_PORT, 0);
    }

    public boolean resetChargePort(boolean leftSide) {
        return CarControl.Quick.putInt(this.mContext.getContentResolver(), CarControl.Quick.RESET_CHARGE_PORT, leftSide ? 1 : 2);
    }

    public boolean setDriveMode(int driveMode) {
        return CarControl.System.putInt(this.mContext.getContentResolver(), CarControl.System.DRIVE_MODE_SET, driveMode);
    }

    public int getDriveMode() {
        return CarControl.System.getInt(this.mContext.getContentResolver(), CarControl.System.DRIVE_MODE, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacPowerChanged(boolean isPowerOn) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacPowerChanged(isPowerOn);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacAutoChanged(boolean isAuto) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacAutoChanged(isAuto);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacWindLevelChanged(int level) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacFanSpeedChanged(level);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacDrvTempChanged(float temp) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacDriverTempChanged(temp);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacDrvSyncChanged(boolean isSync) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacDriverSyncChanged(isSync);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacPsnTempChanged(float temp) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacPsnTempChanged(temp);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacPsnSyncChanged(boolean isSync) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacPsnSyncChanged(isSync);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacFrontDefrostChanged(boolean enabled) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacFrontDefrostChanged(enabled);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacBackDefrostChanged(boolean enabled) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacBackDefrostChanged(enabled);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacWindModeColorChanged(int value) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacWindModeColorChanged(value);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacAirPurgeModeChanged(boolean mode) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onAirPurgeModeChanged(mode);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacInnerAqChanged(int aqValue) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacInnerAqChanged(aqValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacOutTempChanged(float temp) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacExternalTempChanged(temp);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHvacBlowerCtrlType(boolean isAuto) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onHvacBlowerCtrlTypeChange(isAuto);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCentralLockChanged(boolean locked) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onCentralLockChanged(locked);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleChargeStatusChanged(int status) {
        ChargeStatus chargeStatus = ChargeStatus.fromVcuChargeStatus(status);
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onChargeStatusChanged(chargeStatus);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDrvSeatOccupied(boolean occupied) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onDrvSeatOccupiedChanged(occupied);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePsnSeatOccupied(boolean occupied) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onPsnSeatOccupiedChanged(occupied);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleCarControlReady(boolean isReady) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onCarControlReadyChanged(isReady);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleElecPercentChanged(int percent) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onElecPercentChanged(percent);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDriveDistanceChanged(int distance) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onDriveDistanceChanged(distance);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleChargePortStateChanged(boolean leftSide, int state) {
        ChargePortState chargePortState = ChargePortState.fromBcmValue(state);
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onChargePortStateChanged(leftSide, chargePortState);
            }
        }
    }

    private void handleAutoDriveModeChanged(boolean autoMode) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onAutoDriveModeChanged(autoMode);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDriveModeChanged(int driveMode) {
        synchronized (this.mCallbackLock) {
            for (CarControlCallback callback : this.mCallbacks) {
                callback.onDriveModeChanged(driveMode);
            }
        }
    }

    private void registerObserver() {
        if (this.mContentObserver == null) {
            this.mContentObserver = new ContentObserver(this.mContentHandler) { // from class: com.xiaopeng.libcarcontrol.CarControlManager.1
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange, Uri uri) {
                    if (uri == null) {
                        return;
                    }
                    Log.d(CarControlManager.TAG, "onChange: " + uri);
                    String key = uri.getLastPathSegment();
                    if (key == null) {
                        return;
                    }
                    char c = 65535;
                    switch (key.hashCode()) {
                        case -2027240739:
                            if (key.equals(CarControl.System.HVAC_BLOWER_CTRL_TYPE)) {
                                c = '\r';
                                break;
                            }
                            break;
                        case -1975165933:
                            if (key.equals(CarControl.System.BATTERY_PERCENT)) {
                                c = 16;
                                break;
                            }
                            break;
                        case -1967627652:
                            if (key.equals(CarControl.System.HVAC_WIND_LEVEL)) {
                                c = 6;
                                break;
                            }
                            break;
                        case -1613760547:
                            if (key.equals(CarControl.System.CHARGE_STATUS)) {
                                c = 14;
                                break;
                            }
                            break;
                        case -684943650:
                            if (key.equals(CarControl.System.HVAC_AUTO)) {
                                c = 1;
                                break;
                            }
                            break;
                        case -684506577:
                            if (key.equals(CarControl.System.HVAC_PM25)) {
                                c = 7;
                                break;
                            }
                            break;
                        case -681141809:
                            if (key.equals(CarControl.System.HVAC_WIND_MODE_COLOR)) {
                                c = 11;
                                break;
                            }
                            break;
                        case -535149397:
                            if (key.equals(CarControl.System.AVAILABLE_DISTANCE)) {
                                c = 15;
                                break;
                            }
                            break;
                        case -373706315:
                            if (key.equals(CarControl.System.CENTRAL_LOCK)) {
                                c = 17;
                                break;
                            }
                            break;
                        case 255260086:
                            if (key.equals(CarControl.System.HVAC_POWER)) {
                                c = 0;
                                break;
                            }
                            break;
                        case 498854741:
                            if (key.equals(CarControl.System.DRV_OCCUPIED)) {
                                c = 18;
                                break;
                            }
                            break;
                        case 577486648:
                            if (key.equals(CarControl.System.DRIVE_MODE)) {
                                c = 21;
                                break;
                            }
                            break;
                        case 649261694:
                            if (key.equals(CarControl.System.HVAC_FRONT_DEFROST)) {
                                c = '\t';
                                break;
                            }
                            break;
                        case 1132513595:
                            if (key.equals(CarControl.System.HVAC_AIR_PURGE_MODE)) {
                                c = '\f';
                                break;
                            }
                            break;
                        case 1236238596:
                            if (key.equals(CarControl.System.CAR_CONTROL_READY)) {
                                c = 20;
                                break;
                            }
                            break;
                        case 1255743617:
                            if (key.equals(CarControl.System.HVAC_DRV_SYNC)) {
                                c = 3;
                                break;
                            }
                            break;
                        case 1255754170:
                            if (key.equals(CarControl.System.HVAC_DRV_TEMP)) {
                                c = 2;
                                break;
                            }
                            break;
                        case 1353101630:
                            if (key.equals(CarControl.System.HVAC_PSN_SYNC)) {
                                c = 5;
                                break;
                            }
                            break;
                        case 1353112183:
                            if (key.equals(CarControl.System.HVAC_PSN_TEMP)) {
                                c = 4;
                                break;
                            }
                            break;
                        case 1545300410:
                            if (key.equals(CarControl.System.HVAC_BACK_DEFROST)) {
                                c = '\n';
                                break;
                            }
                            break;
                        case 1557084116:
                            if (key.equals(CarControl.System.HVAC_OUT_TEMP)) {
                                c = '\b';
                                break;
                            }
                            break;
                        case 1823004050:
                            if (key.equals(CarControl.System.PSN_OCCUPIED)) {
                                c = 19;
                                break;
                            }
                            break;
                    }
                    float f = 32.0f;
                    switch (c) {
                        case 0:
                            CarControlManager carControlManager = CarControlManager.this;
                            carControlManager.handleHvacPowerChanged(carControlManager.getBoolValue(key));
                            return;
                        case 1:
                            CarControlManager carControlManager2 = CarControlManager.this;
                            carControlManager2.handleHvacAutoChanged(carControlManager2.getBoolValue(key));
                            return;
                        case 2:
                            float temp = CarControlManager.this.getFloatValue(key);
                            CarControlManager carControlManager3 = CarControlManager.this;
                            if (temp < 18.0f) {
                                f = 18.0f;
                            } else if (temp <= 32.0f) {
                                f = temp;
                            }
                            carControlManager3.handleHvacDrvTempChanged(f);
                            return;
                        case 3:
                            CarControlManager carControlManager4 = CarControlManager.this;
                            carControlManager4.handleHvacDrvSyncChanged(carControlManager4.getBoolValue(key));
                            return;
                        case 4:
                            float temp2 = CarControlManager.this.getFloatValue(key);
                            CarControlManager carControlManager5 = CarControlManager.this;
                            if (temp2 < 18.0f) {
                                f = 18.0f;
                            } else if (temp2 <= 32.0f) {
                                f = temp2;
                            }
                            carControlManager5.handleHvacPsnTempChanged(f);
                            return;
                        case 5:
                            CarControlManager carControlManager6 = CarControlManager.this;
                            carControlManager6.handleHvacPsnSyncChanged(carControlManager6.getBoolValue(key));
                            return;
                        case 6:
                            CarControlManager carControlManager7 = CarControlManager.this;
                            carControlManager7.handleHvacWindLevelChanged(carControlManager7.getIntValue(key));
                            return;
                        case 7:
                            CarControlManager carControlManager8 = CarControlManager.this;
                            carControlManager8.handleHvacInnerAqChanged(carControlManager8.getIntValue(key));
                            return;
                        case '\b':
                            CarControlManager carControlManager9 = CarControlManager.this;
                            carControlManager9.handleHvacOutTempChanged(carControlManager9.getFloatValue(key));
                            return;
                        case '\t':
                            CarControlManager carControlManager10 = CarControlManager.this;
                            carControlManager10.handleHvacFrontDefrostChanged(carControlManager10.getBoolValue(key));
                            return;
                        case '\n':
                            CarControlManager carControlManager11 = CarControlManager.this;
                            carControlManager11.handleHvacBackDefrostChanged(carControlManager11.getBoolValue(key));
                            return;
                        case 11:
                            CarControlManager carControlManager12 = CarControlManager.this;
                            carControlManager12.handleHvacWindModeColorChanged(carControlManager12.getIntValue(key));
                            return;
                        case '\f':
                            CarControlManager carControlManager13 = CarControlManager.this;
                            carControlManager13.handleHvacAirPurgeModeChanged(carControlManager13.getBoolValue(key));
                            return;
                        case '\r':
                            CarControlManager carControlManager14 = CarControlManager.this;
                            carControlManager14.handleHvacBlowerCtrlType(carControlManager14.getBoolValue(key));
                            return;
                        case 14:
                            CarControlManager carControlManager15 = CarControlManager.this;
                            carControlManager15.handleChargeStatusChanged(carControlManager15.getIntValue(key));
                            return;
                        case 15:
                            CarControlManager carControlManager16 = CarControlManager.this;
                            carControlManager16.handleDriveDistanceChanged(carControlManager16.getIntValue(key));
                            return;
                        case 16:
                            CarControlManager carControlManager17 = CarControlManager.this;
                            carControlManager17.handleElecPercentChanged(carControlManager17.getIntValue(key));
                            return;
                        case 17:
                            CarControlManager carControlManager18 = CarControlManager.this;
                            carControlManager18.handleCentralLockChanged(carControlManager18.getBoolValue(key));
                            return;
                        case 18:
                            CarControlManager carControlManager19 = CarControlManager.this;
                            carControlManager19.handleDrvSeatOccupied(carControlManager19.getBoolValue(key));
                            return;
                        case 19:
                            CarControlManager carControlManager20 = CarControlManager.this;
                            carControlManager20.handlePsnSeatOccupied(carControlManager20.getBoolValue(key));
                            return;
                        case 20:
                            CarControlManager carControlManager21 = CarControlManager.this;
                            carControlManager21.handleCarControlReady(carControlManager21.getBoolValue(key));
                            return;
                        case 21:
                            CarControlManager carControlManager22 = CarControlManager.this;
                            carControlManager22.handleDriveModeChanged(carControlManager22.getIntValue(key));
                            return;
                        default:
                            return;
                    }
                }
            };
        }
        if (this.mQuickContentObserver == null) {
            this.mQuickContentObserver = new ContentObserver(this.mContentHandler) { // from class: com.xiaopeng.libcarcontrol.CarControlManager.2
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange, Uri uri) {
                    boolean z;
                    if (uri == null) {
                        return;
                    }
                    Log.d(CarControlManager.TAG, "onChange: " + uri);
                    String key = uri.getLastPathSegment();
                    if (key == null) {
                        return;
                    }
                    int hashCode = key.hashCode();
                    if (hashCode != -366343290) {
                        if (hashCode == 1227141723 && key.equals(CarControl.Quick.RIGHT_CHARGE_PORT_STATE)) {
                            z = true;
                        }
                        z = true;
                    } else {
                        if (key.equals(CarControl.Quick.LEFT_CHARGE_PORT_STATE)) {
                            z = false;
                        }
                        z = true;
                    }
                    if (!z) {
                        CarControlManager carControlManager = CarControlManager.this;
                        carControlManager.handleChargePortStateChanged(true, CarControl.Quick.getInt(carControlManager.mContext.getContentResolver(), key, -1));
                    } else if (z) {
                        CarControlManager carControlManager2 = CarControlManager.this;
                        carControlManager2.handleChargePortStateChanged(false, CarControl.Quick.getInt(carControlManager2.mContext.getContentResolver(), key, -1));
                    }
                }
            };
        }
    }

    public boolean registerSystemObserver() {
        try {
            this.mContext.getContentResolver().registerContentObserver(CarControl.System.CONTENT_URI, true, this.mContentObserver);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "register ContentObserver " + CarControl.System.CONTENT_URI + " failed", e);
            return false;
        }
    }

    private void unregisterContentObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        this.mContext.getContentResolver().unregisterContentObserver(this.mQuickContentObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getIntValue(String key) {
        return CarControl.System.getInt(this.mContext.getContentResolver(), key, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getFloatValue(String key) {
        return CarControl.System.getFloat(this.mContext.getContentResolver(), key, 0.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getBoolValue(String key) {
        return CarControl.System.getBool(this.mContext.getContentResolver(), key, false);
    }
}
