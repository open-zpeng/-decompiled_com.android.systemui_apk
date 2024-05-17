package com.xiaopeng.systemui.controller;

import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.bcm.CarBcmManager;
import android.car.hardware.icm.CarIcmManager;
import android.car.hardware.input.CarInputManager;
import android.car.hardware.mcu.CarMcuManager;
import android.car.hardware.tbox.CarTboxManager;
import android.car.hardware.vcu.CarVcuManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v4.media.MediaPlayer2;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.libcarcontrol.CarControlCallback;
import com.xiaopeng.libcarcontrol.CarControlManager;
import com.xiaopeng.libcarcontrol.ChargeStatus;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.controller.SystemController;
import com.xiaopeng.systemui.controller.brightness.BrightnessCarManager;
import com.xiaopeng.systemui.infoflow.checking.CarCheckHelper;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.utils.Utils;
import com.xiaopeng.util.FeatureOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class CarController implements SystemController.OnTimeFormatChangedListener {
    public static final int DISTANCE_MODE_CLTC = 2;
    public static final int DISTANCE_MODE_DYNAMIC = 3;
    public static final int DISTANCE_MODE_NEDC = 0;
    public static final int DISTANCE_MODE_WLTP = 1;
    public static final int DRIVING_MODE_STATUS_COMFORT = 0;
    public static final int DRIVING_MODE_STATUS_ECO = 1;
    public static final int DRIVING_MODE_STATUS_SPORT = 2;
    public static final int HVAC_WIND_COLOR_COLD = 2;
    public static final int HVAC_WIND_COLOR_HOT = 3;
    public static final int HVAC_WIND_COLOR_NATURE = 1;
    private static final int MSG_CONNECT_CAR_CONTROL = 100;
    private static final int MSG_CONNECT_CAR_SERVICE = 101;
    private static final int MSG_INIT_CAR_CONTROL = 102;
    private static final int MSG_INIT_CAR_SERVICE = 103;
    private static final int MSG_REGISTER_OBSERVER = 201;
    private static final int MSG_SYNC_CAR_TIME = 104;
    private static final int RECONNECT_CONUT_MAX = 100;
    private static final String TAG = "CarController";
    public static final float TEMPERATURE_MAX = 32.0f;
    public static final float TEMPERATURE_MIN = 18.0f;
    public static final float TEMPERATURE_UNIT = 0.5f;
    public static final int TYPE_BCM_DOOR_STATE = 3102;
    public static final int TYPE_BCM_DRIVE_MODE_CHANGE = 3106;
    public static final int TYPE_BCM_FR_WIRELESS_CHARGE = 3111;
    public static final int TYPE_BCM_FR_WIRELESS_CHARGE_ERROR = 31111;
    public static final int TYPE_BCM_HEAD_LAMPS = 3101;
    public static final int TYPE_BCM_NEAR_LAMPS = 3103;
    public static final int TYPE_BCM_PSN_SEAT_HEAT_LEVEL = 3107;
    public static final int TYPE_BCM_PSN_SEAT_VENT_LEVEL = 3108;
    public static final int TYPE_BCM_SEAT_HEAT_LEVEL = 3109;
    public static final int TYPE_BCM_SEAT_VENT_LEVEL = 3110;
    public static final int TYPE_BCM_TRUNK = 3104;
    public static final int TYPE_BCM_WIRELESS_CHARGE = 3105;
    public static final int TYPE_BCM_WIRELESS_CHARGE_ERROR = 31051;
    public static final int TYPE_CAR_CENTER_LOCK = 2003;
    public static final int TYPE_CAR_CHARGE_STATE = 2004;
    public static final int TYPE_CAR_CLTC_DRIVE_DISTANCE = 2009;
    public static final int TYPE_CAR_CONTROL = 1001;
    public static final int TYPE_CAR_CONTROL_LOAD_READY = 4001;
    public static final int TYPE_CAR_DRIVER_ACTIVE = 2001;
    public static final int TYPE_CAR_DRIVE_DISTANCE = 2006;
    public static final int TYPE_CAR_DYNAMIC_DRIVE_DISTANCE = 2011;
    public static final int TYPE_CAR_ELEC_PERCENT = 2005;
    public static final int TYPE_CAR_ENDURANCE_MILEAGE_MODE = 2007;
    public static final int TYPE_CAR_NEDC_DRIVE_DISTANCE = 2010;
    public static final int TYPE_CAR_PASSENGER_ACTIVE = 2002;
    public static final int TYPE_CAR_SERVICE = 1000;
    public static final int TYPE_CAR_WLTP_DRIVE_DISTANCE = 2008;
    public static final int TYPE_DRIVER = 1;
    public static final int TYPE_HVAC_AUTO = 2102;
    public static final int TYPE_HVAC_AUTO_DEFOG_WORK = 2116;
    public static final int TYPE_HVAC_BACK_DEFROST = 2109;
    public static final int TYPE_HVAC_DRIVER_SEAT_HEAT = 2112;
    public static final int TYPE_HVAC_DRIVER_SEAT_WIND = 2113;
    public static final int TYPE_HVAC_DRIVER_SYNC = 2105;
    public static final int TYPE_HVAC_DRIVER_TEMP = 2104;
    public static final int TYPE_HVAC_EXTERNAL_TEMP = 2103;
    public static final int TYPE_HVAC_FRONT_DEFROST = 2108;
    public static final int TYPE_HVAC_PASSENGER_SYNC = 2107;
    public static final int TYPE_HVAC_PASSENGER_TEMP = 2106;
    public static final int TYPE_HVAC_POWER = 2101;
    public static final int TYPE_HVAC_PURGE_MODE = 2115;
    public static final int TYPE_HVAC_QUALITY_INNER = 2111;
    public static final int TYPE_HVAC_WIND_COLOR = 2114;
    public static final int TYPE_HVAC_WIND_SPEED = 2110;
    public static final int TYPE_INPUT_BACK_LIGHT = 3301;
    public static final int TYPE_MCU_IG_STATUS = 3401;
    public static final int TYPE_PASSENGER = 2;
    public static final int TYPE_TBOX_CONNECT_STATUS = 3004;
    public static final int TYPE_TBOX_NETWORK_RSRP = 3003;
    public static final int TYPE_TBOX_NETWORK_RSSI = 3001;
    public static final int TYPE_TBOX_NETWORK_TYPE = 3002;
    public static final int TYPE_VCU_DC_PRE_WARM = 3205;
    public static final int TYPE_VCU_DISPLAY_GEAR_LEVEL = 3204;
    public static final int TYPE_VCU_DRIVE_MODE = 3201;
    public static final int TYPE_VCU_EVSYS_READYST = 3202;
    public static final int TYPE_VCU_EXHIBITION_MODE = 3203;
    private static CarController sCarController = null;
    private CarControlAdapter mCarControlAdapter;
    private CarServiceAdapter mCarServiceAdapter;
    private ContentObserverAdapter mContentObserverAdapter;
    private Context mContext;
    private Handler mHandler;
    private int mCarControlCount = 0;
    private int mCarServiceCount = 0;
    private int mNetworkType = -1;
    private int mNetworkRssi = 0;
    private int mNetworkRsrp = 0;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.controller.CarController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            intent.getAction();
            ((Handler) Dependency.get(Dependency.TIME_TICK_HANDLER)).post(new Runnable() { // from class: com.xiaopeng.systemui.controller.CarController.1.1
                @Override // java.lang.Runnable
                public void run() {
                    if (CarController.this.mCarServiceAdapter != null) {
                        CarController.this.mCarServiceAdapter.handleTimeSyncTask();
                    }
                }
            });
        }
    };
    private ArrayList<CarCallback> mCarCallbacks = new ArrayList<>();
    private List<HardkeyCallback> mHardkeyCallbacks = new ArrayList();
    private HandlerThread mHandlerThread = new HandlerThread(TAG);

    /* loaded from: classes24.dex */
    public interface CarCallback {
        void onCarControlChanged(int i, Object obj);

        void onCarServiceChanged(int i, Object obj);
    }

    /* loaded from: classes24.dex */
    public interface HardkeyCallback {
        void onHardkeyPressed(KeyEvent keyEvent);
    }

    @Override // com.xiaopeng.systemui.controller.SystemController.OnTimeFormatChangedListener
    public void onTimeFormatChanged() {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.controller.CarController.2
            @Override // java.lang.Runnable
            public void run() {
                if (CarController.this.mCarServiceAdapter != null) {
                    CarController.this.mCarServiceAdapter.handleTimeSyncTask();
                }
            }
        });
    }

    private CarController(Context context) {
        this.mContext = context;
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: com.xiaopeng.systemui.controller.CarController.3
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                Logger.i(CarController.TAG, "handleMessage : msg = " + msg.what);
                int i = msg.what;
                if (i != 201) {
                    switch (i) {
                        case 100:
                            CarController.this.reconnectCarControlWhenBoot();
                            return;
                        case 101:
                            CarController.this.reconnectCarControlWhenBoot();
                            return;
                        case 102:
                            if (CarController.this.mCarControlAdapter != null) {
                                boolean isCarControlReady = CarController.this.mCarControlAdapter.isCarControlReady();
                                Logger.i(CarController.TAG, "handleMessage isCarControlReady=" + isCarControlReady);
                                if (isCarControlReady) {
                                    CarController.this.onCarControlChanged(1001, 1);
                                    return;
                                } else {
                                    sendEmptyMessageDelayed(102, 200L);
                                    return;
                                }
                            }
                            return;
                        case 103:
                            CarController.this.onCarServiceChanged(1000, 1);
                            return;
                        case 104:
                            if (CarController.this.mCarServiceAdapter != null) {
                                CarController.this.mCarServiceAdapter.handleTimeSyncTask();
                                CarController.this.mCarServiceAdapter.startTimeSyncTask(30000L);
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                } else if (CarController.this.mCarControlAdapter != null) {
                    CarController.this.mCarControlAdapter.tryToRegisterObserver();
                }
            }
        };
        this.mCarControlAdapter = new CarControlAdapter(context);
        this.mCarServiceAdapter = new CarServiceAdapter(context);
        onCarControlChanged(1001, 0);
        onCarServiceChanged(1000, 0);
        reconnectCarControlWhenBoot();
        reconnectCarServiceWhenBoot();
        registerIntentReceiver();
        SystemController.getInstance(this.mContext).addOnTimeFormatChangeListener(this);
    }

    public static CarController getInstance(Context context) {
        if (sCarController == null) {
            synchronized (CarController.class) {
                if (sCarController == null) {
                    sCarController = new CarController(context);
                }
            }
        }
        return sCarController;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        Logger.i(TAG, "icmDayNight : onConfigurationChanged");
        if (ThemeManager.isThemeChanged(newConfig)) {
            final boolean isNight = ThemeManager.isNightMode(this.mContext);
            Runnable runnable = new Runnable() { // from class: com.xiaopeng.systemui.controller.CarController.4
                @Override // java.lang.Runnable
                public void run() {
                    Logger.i(CarController.TAG, "icmDayNight : isNight = " + isNight);
                    if (CarController.this.mCarServiceAdapter != null) {
                        Logger.i(CarController.TAG, "icmDayNight : setIcmDayNightMode = " + isNight);
                        CarController.this.mCarServiceAdapter.setIcmDayNightMode(isNight);
                    }
                }
            };
            this.mHandler.postDelayed(runnable, 0L);
        }
    }

    public CarControlAdapter getCarControlAdapter() {
        if (this.mCarControlAdapter == null) {
            this.mCarControlAdapter = new CarControlAdapter(this.mContext);
        }
        return this.mCarControlAdapter;
    }

    public ContentObserverAdapter getContentObserverAdapter() {
        if (this.mContentObserverAdapter == null) {
            this.mContentObserverAdapter = new ContentObserverAdapter();
        }
        return this.mContentObserverAdapter;
    }

    public CarServiceAdapter getCarServiceAdapter() {
        if (this.mCarServiceAdapter == null) {
            this.mCarServiceAdapter = new CarServiceAdapter(this.mContext);
        }
        return this.mCarServiceAdapter;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reconnectCarControlWhenBoot() {
        Logger.i(TAG, "reconnectCarControlWhenBoot");
        if (this.mCarControlCount < 100) {
            connectCarControlIfNeed();
            CarControlAdapter carControlAdapter = this.mCarControlAdapter;
            if (carControlAdapter != null && !carControlAdapter.isCarControlReady()) {
                this.mHandler.removeMessages(100);
                this.mHandler.sendEmptyMessageDelayed(100, 500L);
                Logger.i(TAG, "reconnectCarControlWhenBoot send delay 500");
            }
            this.mCarControlCount++;
        }
    }

    private void reconnectCarServiceWhenBoot() {
        Logger.i(TAG, "reconnectCarServiceWhenBoot");
        if (this.mCarServiceCount < 100) {
            connectCarServiceIfNeed();
            CarServiceAdapter carServiceAdapter = this.mCarServiceAdapter;
            if (carServiceAdapter != null && !carServiceAdapter.isCarServiceReady()) {
                this.mHandler.removeMessages(101);
                this.mHandler.sendEmptyMessageDelayed(101, 500L);
                Logger.i(TAG, "reconnectCarServiceWhenBoot send delay 500");
            }
            this.mCarServiceCount++;
        }
    }

    public void addCallback(CarCallback callback) {
        ArrayList<CarCallback> arrayList = this.mCarCallbacks;
        if (arrayList != null) {
            arrayList.add(callback);
        }
    }

    public void removeCallback(CarCallback callback) {
        ArrayList<CarCallback> arrayList = this.mCarCallbacks;
        if (arrayList != null) {
            arrayList.remove(callback);
        }
    }

    public void addHardkeyCallback(HardkeyCallback callback) {
        List<HardkeyCallback> list = this.mHardkeyCallbacks;
        if (list != null) {
            list.add(callback);
        }
    }

    public void removeHardkeyCallback(HardkeyCallback callback) {
        List<HardkeyCallback> list = this.mHardkeyCallbacks;
        if (list != null) {
            list.remove(callback);
        }
    }

    public void connectCarServiceIfNeed() {
        this.mCarServiceAdapter.connectCarServiceIfNeed(this.mContext);
    }

    public void connectCarControlIfNeed() {
        this.mCarControlAdapter.connectCarControlIfNeed(this.mContext);
    }

    public void openRearTrunk() {
        CarControlAdapter carControlAdapter = this.mCarControlAdapter;
        if (carControlAdapter != null) {
            carControlAdapter.openRearTrunk();
        }
    }

    public void setDriveMode(int driveMode) {
        CarControlAdapter carControlAdapter = this.mCarControlAdapter;
        if (carControlAdapter != null) {
            try {
                carControlAdapter.setDrivingMode(driveMode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void openChargePort(boolean leftSide) {
        CarControlAdapter carControlAdapter = this.mCarControlAdapter;
        if (carControlAdapter != null) {
            carControlAdapter.openChargePort(leftSide);
        }
    }

    public void onTboxConnectionChanged(int tboxStatus) {
        onCarServiceChanged(TYPE_TBOX_CONNECT_STATUS, Integer.valueOf(tboxStatus));
        if (tboxStatus == 0) {
            this.mNetworkRsrp = 0;
            this.mNetworkRssi = 0;
            this.mNetworkType = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCarControlChanged(final int type, final Object newValue) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.controller.CarController.5
            @Override // java.lang.Runnable
            public void run() {
                if (CarController.this.mCarCallbacks != null) {
                    int i = type;
                    if (i == 2104 || i == 2106) {
                        Logger.d(CarController.TAG, "onCarControlChanged : type = " + type + " callback = " + CarController.this.mCarCallbacks);
                    }
                    Iterator it = CarController.this.mCarCallbacks.iterator();
                    while (it.hasNext()) {
                        CarCallback callback = (CarCallback) it.next();
                        callback.onCarControlChanged(type, newValue);
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCarServiceChanged(final int type, final Object newValue) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.controller.CarController.6
            @Override // java.lang.Runnable
            public void run() {
                if (CarController.this.mCarCallbacks != null) {
                    Iterator it = CarController.this.mCarCallbacks.iterator();
                    while (it.hasNext()) {
                        CarCallback callback = (CarCallback) it.next();
                        callback.onCarServiceChanged(type, newValue);
                    }
                }
            }
        });
    }

    private void onHvacHardkeyPressed(KeyEvent event) {
        List<HardkeyCallback> list = this.mHardkeyCallbacks;
        if (list != null) {
            for (HardkeyCallback callback : list) {
                callback.onHardkeyPressed(event);
            }
        }
    }

    private void registerIntentReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        this.mContext.registerReceiverAsUser(this.mIntentReceiver, UserHandle.ALL, filter, null, (Handler) Dependency.get(Dependency.TIME_TICK_HANDLER));
    }

    public void handleHvacKeyEvent(KeyEvent event) {
        CarControlAdapter carControlAdapter;
        if (event != null) {
            int action = event.getAction();
            if (action == 1 && Utils.isFastClick()) {
                Logger.d(TAG, "isFastClick");
                return;
            }
            int keycode = event.getKeyCode();
            Logger.i(TAG, "handleHvacKeyEvent keycode=" + keycode);
            switch (keycode) {
                case MediaPlayer2.MEDIA_INFO_BAD_INTERLEAVING /* 800 */:
                    if (action == 1 && (carControlAdapter = this.mCarControlAdapter) != null) {
                        carControlAdapter.setHvacPower(true ^ carControlAdapter.isHvacPowerOn());
                        return;
                    }
                    return;
                case MediaPlayer2.MEDIA_INFO_NOT_SEEKABLE /* 801 */:
                    if (action == 0) {
                        onHvacHardkeyPressed(event);
                        this.mCarControlAdapter.setHvacDriverStep(true);
                        return;
                    }
                    return;
                case MediaPlayer2.MEDIA_INFO_METADATA_UPDATE /* 802 */:
                    if (action == 0) {
                        onHvacHardkeyPressed(event);
                        this.mCarControlAdapter.setHvacDriverStep(false);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes24.dex */
    public class CarControlAdapter {
        private CarControlManager mCarControl;
        private CarControlCallback mCarControlCallback = new CarControlCallback() { // from class: com.xiaopeng.systemui.controller.CarController.CarControlAdapter.1
            public void onCarServiceConnected() {
                boolean isCarControlReady = CarControlAdapter.this.isCarControlReady();
                Logger.i(CarController.TAG, "onCarControlConnected isCarControlReady=" + isCarControlReady);
                CarController.this.mHandler.sendEmptyMessageDelayed(102, isCarControlReady ? 0L : 200L);
            }

            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacPowerChanged(boolean isPowerOn) {
                super.onHvacPowerChanged(isPowerOn);
                Logger.i(CarController.TAG, "onHvacPowerChanged isPowerOn=" + isPowerOn);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_POWER, Boolean.valueOf(isPowerOn));
            }

            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacAutoChanged(boolean isAuto) {
                super.onHvacAutoChanged(isAuto);
                Logger.i(CarController.TAG, "onHvacAutoChanged isAuto=" + isAuto);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_AUTO, Boolean.valueOf(isAuto));
            }

            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacFanSpeedChanged(int level) {
                super.onHvacFanSpeedChanged(level);
                Logger.i(CarController.TAG, "onHvacFanSpeedChanged level=" + level);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_WIND_SPEED, Integer.valueOf(level));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacDriverTempChanged(float temp) {
                super.onHvacDriverTempChanged(temp);
                Logger.i(CarController.TAG, "onHvacDriverTempChanged temp=" + temp);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_DRIVER_TEMP, Float.valueOf(temp));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacDriverSyncChanged(boolean isSync) {
                super.onHvacDriverSyncChanged(isSync);
                Logger.i(CarController.TAG, "onHvacDriverSyncChanged isSync=" + isSync);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_DRIVER_SYNC, Boolean.valueOf(isSync));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacPsnTempChanged(float temp) {
                super.onHvacPsnTempChanged(temp);
                Logger.i(CarController.TAG, "onHvacPsnTempChanged temp=" + temp);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_PASSENGER_TEMP, Float.valueOf(temp));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacPsnSyncChanged(boolean isSync) {
                super.onHvacPsnSyncChanged(isSync);
                Logger.i(CarController.TAG, "onHvacPsnSyncChanged isSync=" + isSync);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_PASSENGER_SYNC, Boolean.valueOf(isSync));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacFrontDefrostChanged(boolean enabled) {
                super.onHvacFrontDefrostChanged(enabled);
                Logger.i(CarController.TAG, "onHvacFrontDefrostChanged enabled=" + enabled);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_FRONT_DEFROST, Boolean.valueOf(enabled));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacBackDefrostChanged(boolean enabled) {
                super.onHvacBackDefrostChanged(enabled);
                Logger.i(CarController.TAG, "onHvacBackDefrostChanged enabled=" + enabled);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_BACK_DEFROST, Boolean.valueOf(enabled));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacInnerAqChanged(int aqValue) {
                super.onHvacInnerAqChanged(aqValue);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_QUALITY_INNER, Integer.valueOf(aqValue));
            }

            protected void onDrvSeatHeatLevelChanged(int level) {
                Logger.i(CarController.TAG, "onDrvSeatHeatLevelChanged level=" + level);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_DRIVER_SEAT_HEAT, Integer.valueOf(level));
            }

            protected void onDrvSeatVentLevelChanged(int level) {
                Logger.i(CarController.TAG, "onDrvSeatVentLevelChanged level=" + level);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_DRIVER_SEAT_WIND, Integer.valueOf(level));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacExternalTempChanged(float temp) {
                super.onHvacExternalTempChanged(temp);
                Logger.i(CarController.TAG, "onHvacExternalTempChanged temp=" + temp);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_EXTERNAL_TEMP, Float.valueOf(temp));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onChargeStatusChanged(ChargeStatus status) {
                super.onChargeStatusChanged(status);
                Logger.i(CarController.TAG, "onChargeStatusChanged status=" + status.ordinal());
                CarController.this.onCarControlChanged(2004, Integer.valueOf(status.ordinal()));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onCentralLockChanged(boolean locked) {
                super.onCentralLockChanged(locked);
                Logger.i(CarController.TAG, "onCentralLockChanged locked=" + locked);
                CarController.this.onCarControlChanged(2003, Boolean.valueOf(locked));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onDrvSeatOccupiedChanged(boolean occupied) {
                super.onDrvSeatOccupiedChanged(occupied);
                Logger.i(CarController.TAG, "onDrvSeatOccupiedChanged occupied=" + occupied);
                CarController.this.onCarControlChanged(2001, Boolean.valueOf(occupied));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onPsnSeatOccupiedChanged(boolean occupied) {
                super.onPsnSeatOccupiedChanged(occupied);
                Logger.i(CarController.TAG, "onPsnSeatOccupiedChanged occupied=" + occupied);
                CarController.this.onCarControlChanged(2002, Boolean.valueOf(occupied));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onHvacWindModeColorChanged(int mode) {
                super.onHvacWindModeColorChanged(mode);
                Logger.i(CarController.TAG, "onHvacWindModeColorChanged mode=" + mode);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_WIND_COLOR, Integer.valueOf(mode));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onCarControlReadyChanged(boolean isReady) {
                super.onCarControlReadyChanged(isReady);
                Logger.i(CarController.TAG, "onCarControlReadyChanged isReady=" + isReady);
                CarController.this.onCarControlChanged(4001, Boolean.valueOf(isReady));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onAirPurgeModeChanged(boolean airPurge) {
                super.onAirPurgeModeChanged(airPurge);
                Logger.i(CarController.TAG, "onAirPurgeModeChanged airPurge = " + airPurge);
                CarController.this.onCarControlChanged(CarController.TYPE_HVAC_PURGE_MODE, Boolean.valueOf(airPurge));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.xiaopeng.libcarcontrol.CarControlCallback
            public void onDriveModeChanged(int driveMode) {
                super.onDriveModeChanged(driveMode);
                Logger.i(CarController.TAG, "onDriveModeChanged driveMode = " + driveMode);
                CarController.this.onCarControlChanged(3106, Integer.valueOf(driveMode));
            }
        };
        private Context mCarControlContext;

        public CarControlAdapter(Context context) {
            this.mCarControlContext = context;
            try {
                this.mCarControl = CarControlManager.getInstance(context);
                tryToRegisterObserver();
                registerCallback(this.mCarControlCallback);
                connectCarControlIfNeed(context);
                CarController.this.onCarControlChanged(1001, 1);
            } catch (Exception e) {
                Logger.i(CarController.TAG, "CarControlAdapter() e=" + e);
            }
        }

        protected void tryToRegisterObserver() {
            Logger.d(CarController.TAG, "tryToRegisterObserver");
            CarControlManager carControlManager = this.mCarControl;
            if (carControlManager != null && !carControlManager.registerSystemObserver()) {
                CarController.this.mHandler.sendEmptyMessageDelayed(201, 1000L);
            }
        }

        public CarControlManager getCarControl() {
            if (this.mCarControl == null) {
                try {
                    this.mCarControl = CarControlManager.getInstance(this.mCarControlContext);
                } catch (Exception e) {
                    Logger.i(CarController.TAG, "getCarControl() e=" + e);
                }
            }
            return this.mCarControl;
        }

        public void registerCallback(CarControlCallback callback) {
            CarControlManager ccm = getCarControl();
            if (ccm != null) {
                ccm.registerCallback(callback);
            }
        }

        public void unregisterCallback(CarControlCallback callback) {
            CarControlManager ccm = getCarControl();
            if (ccm != null) {
                ccm.unregisterCallback(callback);
            }
        }

        public void openRearTrunk() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                ccm.openRearTrunk();
            } else {
                connectCarControlIfNeed(this.mCarControlContext);
            }
        }

        public void openChargePort(boolean leftSide) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                ccm.openChargePort(leftSide);
            } else {
                connectCarControlIfNeed(this.mCarControlContext);
            }
        }

        public boolean isCarControlReady() {
            CarControlManager ccm = getCarControl();
            return ccm != null;
        }

        public void connectCarControlIfNeed(Context context) {
            CarControlManager ccm = getCarControl();
            boolean isCarControlReady = isCarControlReady();
            Logger.i(CarController.TAG, "connectCarControlIfNeed isCarControlReady=" + isCarControlReady);
            if (ccm != null && !isCarControlReady) {
                Logger.i(CarController.TAG, "connectCarControl");
            }
        }

        public boolean isHvacPowerOn() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.isHvacPowerOn();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public void setHvacPower(boolean on) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                ccm.setHvacPower(on);
            } else {
                connectCarControlIfNeed(this.mCarControlContext);
            }
        }

        public boolean isHvacAutoMode() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.isHvacAutoMode();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public int getHvacFanSpeed() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.getHvacFanSpeed();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return 0;
        }

        public boolean setHvacDriverStep(boolean isUp) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.setHvacDriverStep(isUp);
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public void setHvacDriverTemp(float temp) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                ccm.setHvacDriverTemp(temp);
            } else {
                connectCarControlIfNeed(this.mCarControlContext);
            }
        }

        public float getHvacDriverTemp() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.getHvacDriverTemp();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return 0.0f;
        }

        public void setHvacDriverSyncMode(boolean enable) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                ccm.setHvacDriverSyncMode(enable);
            } else {
                connectCarControlIfNeed(this.mCarControlContext);
            }
        }

        public boolean isHvacDriverSyncEnabled() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.isHvacDriverSyncEnabled();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return true;
        }

        public boolean setHvacPsnStep(boolean isUp) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.setHvacPsnStep(isUp);
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public void setHvacPsnTemp(float temp) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                ccm.setHvacPsnTemp(temp);
            } else {
                connectCarControlIfNeed(this.mCarControlContext);
            }
        }

        public float getHvacPsnTemp() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.getHvacPsnTemp();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return 0.0f;
        }

        public void setHvacPsnSyncMode(boolean enable) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                ccm.setHvacPsnSyncMode(enable);
            } else {
                connectCarControlIfNeed(this.mCarControlContext);
            }
        }

        public boolean isHvacPsnSyncEnabled() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.isHvacPsnSyncEnabled();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public void setHvacFrontDefrostEnable(boolean enable) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                ccm.setHvacFrontDefrostEnable(enable);
            } else {
                connectCarControlIfNeed(this.mCarControlContext);
            }
        }

        public boolean isHvacFrontDefrostEnable() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.isHvacFrontDefrostEnable();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public void setHvacBackDefrostEnable(boolean enable) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                ccm.setHvacBackDefrostEnable(enable);
            } else {
                connectCarControlIfNeed(this.mCarControlContext);
            }
        }

        public boolean isHvacBackDefrostEnable() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.isHvacBackDefrostEnable();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public int getHvacInnerAq() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.getHvacInnerAq();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return 0;
        }

        public boolean getHvacPurgeMode() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.getHvacAirPurgeMode();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public ChargeStatus getChargeStatus() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.getChargeStatus();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return ChargeStatus.Prepare;
        }

        public int getDrvSeatHeatLevel() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm == null || !isServiceConnected) {
                connectCarControlIfNeed(this.mCarControlContext);
                return 0;
            }
            return 0;
        }

        public int getDrvSeatVentLevel() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm == null || !isServiceConnected) {
                connectCarControlIfNeed(this.mCarControlContext);
                return 0;
            }
            return 0;
        }

        public boolean isCentralLockOn() {
            try {
                CarControlManager ccm = getCarControl();
                boolean isServiceConnected = isCarControlReady();
                if (ccm != null && isServiceConnected) {
                    return ccm.isCentralLockOn();
                }
                connectCarControlIfNeed(this.mCarControlContext);
                return false;
            } catch (Exception e) {
                Logger.i(CarController.TAG, "isCentralLockOn e=" + e);
                return false;
            }
        }

        public boolean isDrvSeatOccupied() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.isDrvSeatOccupied();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public boolean isPsnSeatOccupied() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.isPsnSeatOccupied();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public int getDrivingMode() {
            CarControlManager carControlManager = this.mCarControl;
            if (carControlManager != null) {
                try {
                    return carControlManager.getDriveMode();
                } catch (Exception e) {
                    return -1;
                }
            }
            return -1;
        }

        public void setCenterLock(boolean locked) {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                Logger.i(CarController.TAG, "setCenterLock locked=" + locked);
                ccm.setCentralLock(locked);
                return;
            }
            connectCarControlIfNeed(this.mCarControlContext);
        }

        public float getHvacExternalTemp() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.getHvacExternalTemp();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return 0.0f;
        }

        public int getHvacWindColor() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.getHvacWindModeColor();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return 0;
        }

        public boolean isCarControlLoadReady() {
            CarControlManager ccm = getCarControl();
            boolean isServiceConnected = isCarControlReady();
            if (ccm != null && isServiceConnected) {
                return ccm.isCarControlReady();
            }
            connectCarControlIfNeed(this.mCarControlContext);
            return false;
        }

        public void setDrivingMode(int driveMode) {
            CarControlManager carControlManager = this.mCarControl;
            if (carControlManager != null) {
                carControlManager.setDriveMode(driveMode);
            }
        }
    }

    /* loaded from: classes24.dex */
    public class ContentObserverAdapter {
        public static final String KEY_HVAC_AUTO_DEFOG_WORK_ST = "auto_defog_work_st";
        private ContentObserver mContentObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.controller.CarController.ContentObserverAdapter.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Logger.i(CarController.TAG, "SettingProvider onChange, key = " + uri);
                if (uri.equals(Settings.System.getUriFor(ContentObserverAdapter.KEY_HVAC_AUTO_DEFOG_WORK_ST))) {
                    String dbValue = Settings.System.getString(CarController.this.mContext.getContentResolver(), ContentObserverAdapter.KEY_HVAC_AUTO_DEFOG_WORK_ST);
                    if (!TextUtils.isEmpty(dbValue)) {
                        String[] value = dbValue.split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
                        CarController.this.onCarControlChanged(CarController.TYPE_HVAC_AUTO_DEFOG_WORK, Boolean.valueOf(value[0].equals("1")));
                    }
                }
            }
        };

        public ContentObserverAdapter() {
            try {
                registerContentObserver(this.mContentObserver, KEY_HVAC_AUTO_DEFOG_WORK_ST);
            } catch (Exception e) {
                Logger.i(CarController.TAG, "ContentObserverAdapter() e=" + e);
            }
        }

        public void registerContentObserver(ContentObserver observer, String key) {
            CarController.this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(key), true, observer);
        }

        public boolean getHvacAutoDefogWork() {
            String mCurrentStateValue = Settings.System.getString(CarController.this.mContext.getContentResolver(), KEY_HVAC_AUTO_DEFOG_WORK_ST);
            if (TextUtils.isEmpty(mCurrentStateValue)) {
                return false;
            }
            String[] value = mCurrentStateValue.split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
            Logger.i(CarController.TAG, "ContentProvider getvalue  key:auto_defog_work_stvalue[0]: " + Boolean.parseBoolean(value[0]));
            return Boolean.parseBoolean(value[0]);
        }
    }

    /* loaded from: classes24.dex */
    public class CarServiceAdapter {
        private CarBcmManager mBcmManager;
        private Car mCar;
        private Context mCarServiceContext;
        private CarIcmManager mIcmManager;
        private CarInputManager mInputManager;
        private CarMcuManager mMcuManager;
        private CarTboxManager mTboxManager;
        private CarVcuManager mVcuManager;
        private boolean mServiceConnected = false;
        private Collection<Integer> mBcmIds = new ArrayList();
        private Collection<Integer> mVcuIds = new ArrayList();
        private Collection<Integer> mIcmIds = new ArrayList();
        private Collection<Integer> mMcuIds = new ArrayList();
        private Collection<Integer> mTboxIds = new ArrayList();
        private Collection<Integer> mInputIds = new ArrayList();
        private final ServiceConnection mCarServiceConnection = new ServiceConnection() { // from class: com.xiaopeng.systemui.controller.CarController.CarServiceAdapter.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                try {
                    CarServiceAdapter.this.mServiceConnected = true;
                    CarServiceAdapter.this.initBcmManager();
                    CarServiceAdapter.this.initVcuManager();
                    CarServiceAdapter.this.initMcuManager();
                    CarServiceAdapter.this.initIcmManager();
                    CarServiceAdapter.this.initTboxManager();
                    CarServiceAdapter.this.initInputManager();
                    boolean isCarServiceReady = CarServiceAdapter.this.isCarServiceReady();
                    Logger.i(CarController.TAG, "onCarServiceConnected isCarServiceReady=" + isCarServiceReady);
                    if (isCarServiceReady) {
                        CarController.this.onCarServiceChanged(1000, 1);
                    } else {
                        CarServiceAdapter.this.connectCarServiceIfNeed(CarServiceAdapter.this.mCarServiceContext);
                    }
                } catch (Exception e) {
                    Logger.e(CarController.TAG, "CarConnection e=" + e);
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                CarServiceAdapter.this.mServiceConnected = false;
                CarServiceAdapter carServiceAdapter = CarServiceAdapter.this;
                carServiceAdapter.connectCarServiceIfNeed(carServiceAdapter.mCarServiceContext);
            }
        };
        private CarTboxManager.CarTboxEventCallback mTboxCallback = new CarTboxManager.CarTboxEventCallback() { // from class: com.xiaopeng.systemui.controller.CarController.CarServiceAdapter.2
            public void onChangeEvent(CarPropertyValue val) {
                CarServiceAdapter.this.handleTboxEvent(val);
            }

            public void onErrorEvent(int var1, int var2) {
                Logger.i(CarController.TAG, "onErrorEvent");
            }
        };
        private CarBcmManager.CarBcmEventCallback mBcmCallback = new CarBcmManager.CarBcmEventCallback() { // from class: com.xiaopeng.systemui.controller.CarController.CarServiceAdapter.5
            public void onChangeEvent(CarPropertyValue carPropertyValue) {
                CarServiceAdapter.this.handleBcmEvent(carPropertyValue);
            }

            public void onErrorEvent(int i, int i1) {
            }
        };
        private CarVcuManager.CarVcuEventCallback mVcuCallback = new CarVcuManager.CarVcuEventCallback() { // from class: com.xiaopeng.systemui.controller.CarController.CarServiceAdapter.6
            public void onChangeEvent(CarPropertyValue carPropertyValue) {
                CarServiceAdapter.this.handleVcuEvent(carPropertyValue);
            }

            public void onErrorEvent(int i, int i1) {
            }
        };
        private CarMcuManager.CarMcuEventCallback mMcuCallback = new CarMcuManager.CarMcuEventCallback() { // from class: com.xiaopeng.systemui.controller.CarController.CarServiceAdapter.7
            public void onChangeEvent(CarPropertyValue carPropertyValue) {
                CarServiceAdapter.this.handleMcuEvent(carPropertyValue);
            }

            public void onErrorEvent(int i, int i1) {
            }
        };
        private CarIcmManager.CarIcmEventCallback mIcmCallback = new CarIcmManager.CarIcmEventCallback() { // from class: com.xiaopeng.systemui.controller.CarController.CarServiceAdapter.8
            public void onChangeEvent(CarPropertyValue carPropertyValue) {
                CarServiceAdapter.this.handleIcmEvent(carPropertyValue);
            }

            public void onErrorEvent(int i, int i1) {
            }
        };
        private CarInputManager.CarInputEventCallback mInputCallback = new CarInputManager.CarInputEventCallback() { // from class: com.xiaopeng.systemui.controller.CarController.CarServiceAdapter.9
            public void onChangeEvent(CarPropertyValue carPropertyValue) {
                CarServiceAdapter.this.handleInputEvent(carPropertyValue);
            }

            public void onErrorEvent(int i, int i1) {
            }
        };

        public CarServiceAdapter(Context context) {
            this.mCarServiceContext = context;
        }

        public boolean isCarServiceReady() {
            return this.mServiceConnected && isTboxReady() && isBcmReady() && isVcuReady() && isIcmReady() && isMcuReady() && isInputReady();
        }

        public void connectCarServiceIfNeed(Context context) {
            boolean isCarServiceReady = isCarServiceReady();
            Logger.i(CarController.TAG, "connectCarServiceIfNeed isCarServiceReady=" + isCarServiceReady);
            if (this.mCar == null || !isCarServiceReady) {
                this.mCar = Car.createCar(context, this.mCarServiceConnection);
                this.mCar.connect();
            }
        }

        public boolean isTboxReady() {
            return this.mTboxManager != null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initTboxManager() {
            try {
                this.mTboxIds.add(554700817);
                this.mTboxManager = (CarTboxManager) this.mCar.getCarManager("xp_tbox");
                this.mTboxManager.registerPropCallback(this.mTboxIds, this.mTboxCallback);
                requestTboxSimStatus();
                requestTboxModemStatus();
            } catch (Exception e) {
                Logger.e(CarController.TAG, "initTboxManager e=" + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:40:0x00e8 A[Catch: Exception -> 0x0111, TryCatch #0 {Exception -> 0x0111, blocks: (B:9:0x0015, B:22:0x0057, B:24:0x005f, B:26:0x0067, B:29:0x0070, B:35:0x00c7, B:37:0x00cf, B:41:0x00f8, B:43:0x0100, B:38:0x00e0, B:40:0x00e8, B:30:0x008d), top: B:48:0x0015 }] */
        /* JADX WARN: Removed duplicated region for block: B:43:0x0100 A[Catch: Exception -> 0x0111, TRY_LEAVE, TryCatch #0 {Exception -> 0x0111, blocks: (B:9:0x0015, B:22:0x0057, B:24:0x005f, B:26:0x0067, B:29:0x0070, B:35:0x00c7, B:37:0x00cf, B:41:0x00f8, B:43:0x0100, B:38:0x00e0, B:40:0x00e8, B:30:0x008d), top: B:48:0x0015 }] */
        /* JADX WARN: Removed duplicated region for block: B:54:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void handleTboxEvent(android.car.hardware.CarPropertyValue r13) {
            /*
                Method dump skipped, instructions count: 295
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.controller.CarController.CarServiceAdapter.handleTboxEvent(android.car.hardware.CarPropertyValue):void");
        }

        private void requestTboxSimStatus() {
            Runnable runnable = new Runnable() { // from class: com.xiaopeng.systemui.controller.CarController.CarServiceAdapter.3
                @Override // java.lang.Runnable
                public void run() {
                    int times = 5;
                    while (CarServiceAdapter.this.mTboxManager == null) {
                        try {
                            int times2 = times - 1;
                            if (times == 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000L);
                                times = times2;
                            } catch (Exception e) {
                                e = e;
                                e.printStackTrace();
                                return;
                            }
                        } catch (Exception e2) {
                            e = e2;
                        }
                    }
                    if (CarServiceAdapter.this.mTboxManager != null) {
                        CarServiceAdapter.this.mTboxManager.getSimStatusAsync();
                    }
                }
            };
            CarController.this.mHandler.post(runnable);
        }

        private void requestTboxModemStatus() {
            Runnable runnable = new Runnable() { // from class: com.xiaopeng.systemui.controller.CarController.CarServiceAdapter.4
                @Override // java.lang.Runnable
                public void run() {
                    int times = 5;
                    while (CarServiceAdapter.this.mTboxManager == null) {
                        try {
                            int times2 = times - 1;
                            if (times == 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000L);
                                times = times2;
                            } catch (Exception e) {
                                e = e;
                                e.printStackTrace();
                                return;
                            }
                        } catch (Exception e2) {
                            e = e2;
                        }
                    }
                    if (CarServiceAdapter.this.mTboxManager != null) {
                        CarServiceAdapter.this.mTboxManager.requestTboxModemStatus();
                    }
                }
            };
            CarController.this.mHandler.post(runnable);
        }

        public boolean isBcmReady() {
            return this.mBcmManager != null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initBcmManager() {
            try {
                this.mBcmIds.add(557849633);
                this.mBcmIds.add(557849640);
                this.mBcmIds.add(557849713);
                this.mBcmIds.add(557849714);
                this.mBcmIds.add(557849989);
                this.mBcmIds.add(557849990);
                this.mBcmIds.add(557849794);
                this.mBcmIds.add(557849701);
                this.mBcmIds.add(356517140);
                this.mBcmIds.add(557849638);
                this.mBcmIds.add(356517139);
                this.mBcmIds.add(557859329);
                this.mBcmIds.add(557924883);
                this.mBcmManager = (CarBcmManager) this.mCar.getCarManager("xp_bcm");
                this.mBcmManager.registerPropCallback(this.mBcmIds, this.mBcmCallback);
            } catch (Exception e) {
                Logger.i(CarController.TAG, "initBcmManager e=" + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleBcmEvent(CarPropertyValue val) {
            Logger.i(CarController.TAG, "handleBcmEvent val=" + val);
            if (val != null && val.getValue() != null) {
                switch (val.getPropertyId()) {
                    case 356517139:
                        Object value = val.getValue();
                        CarController.this.onCarServiceChanged(CarController.TYPE_BCM_SEAT_VENT_LEVEL, value);
                        return;
                    case 356517140:
                        Object value2 = val.getValue();
                        CarController.this.onCarServiceChanged(CarController.TYPE_BCM_PSN_SEAT_VENT_LEVEL, value2);
                        return;
                    case 557849610:
                        try {
                            Object value3 = val.getValue();
                            CarController.this.onCarServiceChanged(CarController.TYPE_BCM_TRUNK, value3);
                            return;
                        } catch (Exception e) {
                            return;
                        }
                    case 557849633:
                        try {
                            Object value4 = val.getValue();
                            CarController.this.onCarServiceChanged(CarController.TYPE_BCM_NEAR_LAMPS, Integer.valueOf(((Integer) value4).intValue()));
                            BrightnessCarManager.get(CarController.this.mContext).onCarEventChanged(val.getPropertyId(), val.getValue());
                            return;
                        } catch (Exception e2) {
                            return;
                        }
                    case 557849638:
                        Object value5 = val.getValue();
                        CarController.this.onCarServiceChanged(CarController.TYPE_BCM_SEAT_HEAT_LEVEL, value5);
                        return;
                    case 557849640:
                        try {
                            Object value6 = val.getValue();
                            CarController.this.onCarServiceChanged(CarController.TYPE_BCM_HEAD_LAMPS, Integer.valueOf(((Integer) value6).intValue()));
                            BrightnessCarManager.get(CarController.this.mContext).onCarEventChanged(val.getPropertyId(), val.getValue());
                            return;
                        } catch (Exception e3) {
                            return;
                        }
                    case 557849701:
                        Object value7 = val.getValue();
                        CarController.this.onCarServiceChanged(3107, value7);
                        return;
                    case 557849713:
                        Object value8 = val.getValue();
                        CarController.this.onCarServiceChanged(CarController.TYPE_BCM_WIRELESS_CHARGE, value8);
                        return;
                    case 557849714:
                        Object value9 = val.getValue();
                        CarController.this.onCarServiceChanged(CarController.TYPE_BCM_WIRELESS_CHARGE_ERROR, value9);
                        return;
                    case 557849794:
                        try {
                            Object value10 = val.getValue();
                            ((Integer) value10).intValue();
                            BrightnessCarManager.get(CarController.this.mContext).onCarEventChanged(val.getPropertyId(), val.getValue());
                            return;
                        } catch (Exception e4) {
                            return;
                        }
                    case 557849989:
                        Object value11 = val.getValue();
                        CarController.this.onCarServiceChanged(CarController.TYPE_BCM_FR_WIRELESS_CHARGE, value11);
                        return;
                    case 557849990:
                        Object value12 = val.getValue();
                        CarController.this.onCarServiceChanged(CarController.TYPE_BCM_FR_WIRELESS_CHARGE_ERROR, value12);
                        return;
                    case 557859329:
                        try {
                            int value13 = ((Integer) val.getValue()).intValue();
                            BrightnessCarManager.get(CarController.this.mContext).onCarEventChanged(val.getPropertyId(), Integer.valueOf(value13));
                            return;
                        } catch (Exception e5) {
                            return;
                        }
                    case 557915161:
                        try {
                            Object value14 = val.getValue();
                            CarController.this.onCarServiceChanged(CarController.TYPE_BCM_DOOR_STATE, value14);
                            return;
                        } catch (Exception e6) {
                            return;
                        }
                    case 557924883:
                        try {
                            Object value15 = val.getValue();
                            BrightnessCarManager.get(CarController.this.mContext).onCarEventChanged(val.getPropertyId(), value15);
                            return;
                        } catch (Exception e7) {
                            return;
                        }
                    default:
                        return;
                }
            }
        }

        public int getNearLampState() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    return carBcmManager.getNearLampState();
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public int getHeadLampGroup() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    return carBcmManager.getHeadLampGroup();
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public int getPsnSeatHeatLevel() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    return carBcmManager.getBcmPsnSeatHeatLevel();
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public void setPsnSeatHeatLevel(int level) {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    carBcmManager.setBcmPsnSeatHeatLevel(level);
                } catch (Exception e) {
                }
            }
        }

        public int getPsnSeatVentLevel() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    return carBcmManager.getPassengerSeatBlowLevel();
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public void setPsnSeatVentLevel(int level) {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    carBcmManager.setPassengerSeatBlowLevel(level);
                } catch (Exception e) {
                }
            }
        }

        public int getSeatHeatLevel() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    return carBcmManager.getBcmSeatHeatLevel();
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public void setSeatHeatLevel(int level) {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    carBcmManager.setBcmSeatHeatLevel(level);
                } catch (Exception e) {
                }
            }
        }

        public int getSeatVentLevel() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    return carBcmManager.getBcmSeatBlowLevel();
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public void setSeatVentLevel(int level) {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    carBcmManager.setBcmSeatBlowLevel(level);
                } catch (Exception e) {
                }
            }
        }

        public boolean isFarLampOn() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    return carBcmManager.getFarLampState() == 1;
                } catch (Exception e) {
                }
            }
            return false;
        }

        public int getEnvironmentMode() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    return carBcmManager.getEnvironmentMode();
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public int[] getDoorsState() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    return carBcmManager.getDoorsState();
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        public int getTrunkState() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    return carBcmManager.getTrunk();
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public int getWirelessChangeStatus() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    int code = carBcmManager.getCwcChargeSt();
                    Logger.i(CarController.TAG, "getWirelessChangeStatus code=" + code);
                    return code;
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public int getWirelessChangeErrorStatus() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    int code = carBcmManager.getCwcChargeErrorSt();
                    Logger.i(CarController.TAG, "getWirelessChangeErrorStatus code=" + code);
                    return code;
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public int getPsnWirelessChangeStatus() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    int code = carBcmManager.getFRCwcChargeSt();
                    Logger.i(CarController.TAG, "getPsnWirelessChangeStatus code=" + code);
                    return code;
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public int getPsnWirelessChangeErrorStatus() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    int code = carBcmManager.getFRCwcChargeErrorSt();
                    Logger.i(CarController.TAG, "getPsnWirelessChangeErrorStatus code=" + code);
                    return code;
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public void setCMSAutoBrightSw(int sw) {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    carBcmManager.setCMSAutoBrightSw(sw);
                    Logger.i(CarController.TAG, "setCMSAutoBrightSw sw=" + sw);
                } catch (Exception e) {
                }
            }
        }

        public int getCMSAutoBrightSwSt() {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    int sw = carBcmManager.getCMSAutoBrightSwSt();
                    Logger.i(CarController.TAG, "getCMSAutoBrightSwSt sw=" + sw);
                    return sw;
                } catch (Exception e) {
                    return 1;
                }
            }
            return 1;
        }

        public void setCMSBrightWithFlag(int value, int flag) {
            CarBcmManager carBcmManager = this.mBcmManager;
            if (carBcmManager != null) {
                try {
                    carBcmManager.setCMSBrightWithFlag(value, flag);
                    Logger.i(CarController.TAG, "setCMSBrightWithFlag value=" + value + " flag=" + flag);
                } catch (Exception e) {
                }
            }
        }

        public boolean isVcuReady() {
            return this.mBcmManager != null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initVcuManager() {
            try {
                this.mVcuIds.add(557847082);
                this.mVcuIds.add(557847057);
                this.mVcuIds.add(557847042);
                this.mVcuIds.add(557847127);
                this.mVcuIds.add(559944315);
                this.mVcuIds.add(559944314);
                this.mVcuIds.add(559944326);
                this.mVcuIds.add(559944335);
                this.mVcuIds.add(557847056);
                this.mVcuIds.add(557847045);
                this.mVcuIds.add(557847137);
                this.mVcuIds.add(557847148);
                this.mVcuManager = (CarVcuManager) this.mCar.getCarManager(CarClientWrapper.XP_VCU_SERVICE);
                this.mVcuManager.registerPropCallback(this.mVcuIds, this.mVcuCallback);
            } catch (Exception e) {
                Logger.i(CarController.TAG, "initVcuManager e=" + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleVcuEvent(CarPropertyValue val) {
            if (val != null && val.getValue() != null) {
                switch (val.getPropertyId()) {
                    case 557847042:
                        int value_ele = ((Integer) val.getValue()).intValue();
                        if (CarController.isEleValueValid(value_ele)) {
                            CarController.this.onCarServiceChanged(CarController.TYPE_CAR_ELEC_PERCENT, Integer.valueOf(value_ele));
                            return;
                        }
                        return;
                    case 557847045:
                        CarController.this.onCarServiceChanged(CarController.TYPE_VCU_DISPLAY_GEAR_LEVEL, val.getValue());
                        return;
                    case 557847056:
                        CarController.this.onCarServiceChanged(CarController.TYPE_VCU_EVSYS_READYST, val.getValue());
                        return;
                    case 557847082:
                        try {
                            int value = ((Integer) val.getValue()).intValue();
                            CarController.this.onCarServiceChanged(CarController.TYPE_VCU_DRIVE_MODE, Integer.valueOf(value));
                            return;
                        } catch (Exception e) {
                            return;
                        }
                    case 557847127:
                        CarController.this.onCarServiceChanged(CarController.TYPE_CAR_ENDURANCE_MILEAGE_MODE, val.getValue());
                        return;
                    case 557847137:
                        CarController.this.onCarServiceChanged(CarController.TYPE_VCU_EXHIBITION_MODE, val.getValue());
                        return;
                    case 557847148:
                        CarController.this.onCarServiceChanged(CarController.TYPE_VCU_DC_PRE_WARM, val.getValue());
                        return;
                    case 559944314:
                        CarController.this.onCarServiceChanged(CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE, val.getValue());
                        return;
                    case 559944315:
                        CarController.this.onCarServiceChanged(2008, val.getValue());
                        return;
                    case 559944326:
                        CarController.this.onCarServiceChanged(CarController.TYPE_CAR_NEDC_DRIVE_DISTANCE, val.getValue());
                        return;
                    case 559944335:
                        CarController.this.onCarServiceChanged(CarController.TYPE_CAR_DYNAMIC_DRIVE_DISTANCE, val.getValue());
                        return;
                    default:
                        return;
                }
            }
        }

        public int getDrivingMode() {
            CarVcuManager carVcuManager = this.mVcuManager;
            if (carVcuManager != null) {
                try {
                    return carVcuManager.getDrivingMode();
                } catch (Exception e) {
                    return 0;
                }
            }
            return 0;
        }

        public int getElecPercent() {
            CarVcuManager carVcuManager = this.mVcuManager;
            if (carVcuManager != null) {
                try {
                    return carVcuManager.getElectricityPercent();
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return 0;
        }

        public int getDcPreWarmState() {
            CarVcuManager carVcuManager = this.mVcuManager;
            if (carVcuManager != null) {
                try {
                    return carVcuManager.getDcPreWarmInStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return 0;
        }

        public float getDriveDistance() {
            float distance;
            if (this.mVcuManager != null) {
                try {
                    int mode = getEnduranceMileageMode();
                    if (mode == 1) {
                        distance = this.mVcuManager.getWltpAvailableDrivingDistanceFloat();
                    } else if (mode == 2) {
                        distance = this.mVcuManager.getCltcAvailableDrivingDistanceFloat();
                    } else if (mode == 3) {
                        distance = this.mVcuManager.getDynamicAvailableDrivingDistance();
                    } else {
                        distance = this.mVcuManager.getNedcAvalibleDrivingDistanceFloat();
                    }
                    Logger.d(CarController.TAG, "getDriveDistance mode: " + mode + " ,distance: " + distance);
                    return distance;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0.0f;
                }
            }
            return 0.0f;
        }

        public int getEnduranceMileageMode() {
            CarVcuManager carVcuManager = this.mVcuManager;
            if (carVcuManager != null) {
                try {
                    int mode = carVcuManager.getEnduranceMileageMode();
                    Logger.d(CarController.TAG, "getEnduranceMileageMode mode = " + mode);
                    return mode;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return 0;
        }

        public int getEvSysReadyState() {
            CarVcuManager carVcuManager = this.mVcuManager;
            if (carVcuManager != null) {
                try {
                    return carVcuManager.getEvSysReady();
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return 0;
        }

        public int getExhibitionMode() {
            CarVcuManager carVcuManager = this.mVcuManager;
            if (carVcuManager != null) {
                try {
                    return carVcuManager.getExhibModeSwitchStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return 0;
        }

        public int getGearLevel() {
            CarVcuManager carVcuManager = this.mVcuManager;
            if (carVcuManager != null) {
                try {
                    return carVcuManager.getDisplayGearLevel();
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return 0;
        }

        public boolean isMcuReady() {
            return this.mMcuManager != null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initMcuManager() {
            try {
                this.mMcuIds.add(557847561);
                this.mMcuManager = (CarMcuManager) this.mCar.getCarManager("xp_mcu");
                this.mMcuManager.registerPropCallback(this.mMcuIds, this.mMcuCallback);
            } catch (Exception e) {
                Logger.i(CarController.TAG, "initVcuManager e=" + e);
            }
        }

        public int getIgStatus() {
            CarMcuManager carMcuManager = this.mMcuManager;
            if (carMcuManager != null) {
                try {
                    return carMcuManager.getIgStatusFromMcu();
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleMcuEvent(CarPropertyValue val) {
            Logger.i(CarController.TAG, "handleMcuEvent val=" + val);
            if (val != null && val.getValue() != null && val.getPropertyId() == 557847561) {
                try {
                    int value = ((Integer) val.getValue()).intValue();
                    BrightnessCarManager.get(CarController.this.mContext).onCarEventChanged(val.getPropertyId(), Integer.valueOf(value));
                    CarController.this.onIgStatusChanged(value);
                    CarController.this.onCarServiceChanged(CarController.TYPE_MCU_IG_STATUS, Integer.valueOf(value));
                    Logger.i(CarController.TAG, "handleMcuEvent ID_MCU_IG_STATUS value=" + value);
                } catch (Exception e) {
                }
            }
        }

        public boolean isCiuExist() {
            int state = 2;
            CarMcuManager carMcuManager = this.mMcuManager;
            if (carMcuManager != null) {
                try {
                    state = carMcuManager.getCiuState();
                } catch (Throwable th) {
                }
            }
            return state == 1;
        }

        public int getFactoryModeSwitchStatus() {
            CarMcuManager carMcuManager = this.mMcuManager;
            if (carMcuManager != null) {
                try {
                    return carMcuManager.getFactoryModeSwitchStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return 0;
        }

        public boolean isIcmReady() {
            return this.mIcmManager != null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initIcmManager() {
            try {
                this.mIcmIds.add(557848078);
                this.mIcmIds.add(554702353);
                this.mIcmIds.add(557848124);
                this.mIcmManager = (CarIcmManager) this.mCar.getCarManager(CarClientWrapper.XP_ICM_SERVICE);
                this.mIcmManager.registerPropCallback(this.mIcmIds, this.mIcmCallback);
                setIcmDayNightMode(ThemeManager.isNightMode(CarController.this.mContext));
            } catch (Exception e) {
                Logger.i(CarController.TAG, "initIcmManager e=" + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleIcmEvent(CarPropertyValue val) {
            if (val != null && val.getValue() != null) {
                int propertyId = val.getPropertyId();
                if (propertyId == 554702353) {
                    try {
                        boolean isNight = ThemeManager.isNightMode(this.mCarServiceContext);
                        JSONObject jsonObject = new JSONObject(val.getValue().toString());
                        boolean sysReady = "SysReady".equals(jsonObject.getString("SyncMode"));
                        if (sysReady) {
                            handleTimeSyncTask();
                            setIcmDayNightMode(isNight);
                        }
                        BrightnessCarManager.get(CarController.this.mContext).onCarEventChanged(val.getPropertyId(), val.getValue());
                    } catch (Exception e) {
                        Logger.i(CarController.TAG, "handleIcmEvent sync e=" + e);
                    }
                } else if (propertyId != 557848078) {
                    if (propertyId == 557848124) {
                        BrightnessCarManager.get(CarController.this.mContext).onCarEventChanged(val.getPropertyId(), val.getValue());
                    }
                } else {
                    try {
                        boolean isNight2 = ThemeManager.isNightMode(this.mCarServiceContext);
                        boolean z = false;
                        if (this.mIcmManager != null && this.mIcmManager.getIcmConnectionState() == 1) {
                            z = true;
                        }
                        boolean connected = z;
                        if (connected) {
                            handleTimeSyncTask();
                            setIcmDayNightMode(isNight2);
                            BrightnessCarManager.get(CarController.this.mContext).onCarEventChanged(val.getPropertyId(), val.getValue());
                        }
                    } catch (Exception e2) {
                        Logger.i(CarController.TAG, "handleIcmEvent connected e=" + e2);
                    }
                }
            }
        }

        public CarIcmManager getIcmManager() {
            return this.mIcmManager;
        }

        public int getIcmBrightness() {
            CarIcmManager carIcmManager = this.mIcmManager;
            if (carIcmManager == null) {
                return 0;
            }
            try {
                int brightness = carIcmManager.getIcmBrightness();
                return brightness;
            } catch (Exception e) {
                Logger.i(CarController.TAG, "getIcmBrightness e=" + e);
                return 0;
            }
        }

        public void setIcmBrightness(int level) throws Exception {
            CarIcmManager carIcmManager = this.mIcmManager;
            if (carIcmManager != null) {
                carIcmManager.setIcmBrightness(level);
            }
        }

        public void setIcmSyncTime(String time) {
            try {
                if (this.mIcmManager != null) {
                    this.mIcmManager.setIcmSyncTime(time);
                }
            } catch (Exception e) {
            }
        }

        public void setIcmSystemTimeValue(int hour, int minutes) {
            try {
                if (this.mIcmManager != null) {
                    this.mIcmManager.setIcmSystemTimeValue(hour, minutes);
                }
            } catch (Exception e) {
            }
        }

        public void setIcmSyncSignal(String signal) {
            try {
                if (this.mIcmManager != null) {
                    this.mIcmManager.setIcmSyncSignal(signal);
                }
            } catch (Exception e) {
            }
        }

        public void setIcmDayNightSwitch(int status) {
            try {
                if (this.mIcmManager != null) {
                    this.mIcmManager.setIcmDayNightSwitch(status);
                }
            } catch (Exception e) {
            }
        }

        public void setIcmDayNightSyncMode(int mode) {
            try {
                if (this.mIcmManager != null) {
                    this.mIcmManager.setIcmDayNightMode(mode);
                }
            } catch (Exception e) {
            }
        }

        public void setIcmDayNightMode(boolean isNight) {
            if (FeatureOption.FO_ICM_TYPE == 0) {
                Logger.i(CarController.TAG, "setIcmDayNightMode isNight=" + isNight);
                setIcmDayNightSyncMode(isNight ? 1 : 0);
                return;
            }
            String signal = CarController.createDayNight(isNight);
            Logger.i(CarController.TAG, "setIcmDayNightMode signal=" + signal);
            if (!TextUtils.isEmpty(signal)) {
                setIcmSyncSignal(signal);
            }
        }

        public void startTimeSyncTask(long delayMillis) {
            CarController.this.mHandler.sendEmptyMessageDelayed(104, delayMillis);
        }

        public void handleTimeSyncTask() {
            if (FeatureOption.FO_ICM_TYPE == 0) {
                int[] time = CarController.createArraysTime(this.mCarServiceContext);
                if (time != null && time.length == 2) {
                    setIcmSystemTimeValue(time[0], time[1]);
                    return;
                }
                return;
            }
            setIcmSyncTime(CarController.createFormatTime(this.mCarServiceContext));
        }

        public boolean isInputReady() {
            return this.mInputManager != null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void initInputManager() {
            try {
                this.mInputIds.add(557916704);
                this.mInputManager = (CarInputManager) this.mCar.getCarManager("xp_input");
                this.mInputManager.registerPropCallback(this.mInputIds, this.mInputCallback);
            } catch (Exception e) {
                Logger.i(CarController.TAG, "initInputManager e=" + e);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleInputEvent(CarPropertyValue val) {
            Logger.i(CarController.TAG, "handleInputEvent val=" + val);
            if (val != null && val.getValue() != null && val.getPropertyId() == 557916704) {
                try {
                    CarController.this.onCarServiceChanged(CarController.TYPE_INPUT_BACK_LIGHT, val.getValue());
                    BrightnessCarManager.get(CarController.this.mContext).onCarEventChanged(val.getPropertyId(), val.getValue());
                } catch (Exception e) {
                }
            }
        }
    }

    public static int getInnerQualityLevel(int quality) {
        int level;
        if (quality < 0) {
            level = 0;
        } else if (quality < 0 || quality > 50) {
            if (quality <= 50 || quality > 100) {
                if (quality <= 100 || quality > 150) {
                    if (quality <= 150 || quality > 200) {
                        if (quality > 200 && quality <= 300) {
                            level = 4;
                        } else if (quality > 300 && quality <= 500) {
                            level = 5;
                        } else {
                            level = 5;
                        }
                    } else {
                        level = 3;
                    }
                } else {
                    level = 2;
                }
            } else {
                level = 1;
            }
        } else {
            level = 0;
        }
        Logger.i(TAG, "getInnerQualityLevel level=" + level);
        return level;
    }

    public static String getInnerQualityContent(Context context, int quality) {
        if (quality == 1023) {
            return "";
        }
        int level = getInnerQualityLevel(quality);
        int[] resId = {R.string.sysbar_inner_quality_1, R.string.sysbar_inner_quality_2, R.string.sysbar_inner_quality_3, R.string.sysbar_inner_quality_4, R.string.sysbar_inner_quality_5, R.string.sysbar_inner_quality_6};
        if (level < 0 || level > 5) {
            return "";
        }
        String content = context.getString(resId[level]);
        return content;
    }

    public static int getInnerQualityColor(int quality) {
        int level = getInnerQualityLevel(quality);
        int[] resId = {R.color.colorAirQuality1, R.color.colorAirQuality2, R.color.colorAirQuality3, R.color.colorAirQuality4, R.color.colorAirQuality5, R.color.colorAirQuality6};
        if (level >= 0 && level <= 5) {
            return resId[level];
        }
        return 0;
    }

    public static String getTemperatureUnit(Context context) {
        return context.getText(R.string.unit_temperature).toString();
    }

    public static int getBatteryLevel(int elecPercent) {
        if (elecPercent <= 0) {
            return 0;
        }
        if (elecPercent < 1 || elecPercent >= 11) {
            if (elecPercent < 11 || elecPercent >= 21) {
                if (elecPercent < 21 || elecPercent >= 40) {
                    if (elecPercent < 40 || elecPercent >= 50) {
                        if (elecPercent < 50 || elecPercent >= 60) {
                            if (elecPercent < 60 || elecPercent >= 70) {
                                if (elecPercent < 70 || elecPercent >= 80) {
                                    if (elecPercent >= 80 && elecPercent < 90) {
                                        return 8;
                                    }
                                    if (elecPercent >= 90 && elecPercent < 98) {
                                        return 9;
                                    }
                                    return 10;
                                }
                                return 7;
                            }
                            return 6;
                        }
                        return 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    public static int getBatteryState(int driveDistance) {
        if (driveDistance <= 30) {
            return 0;
        }
        if (driveDistance <= 60) {
            return 1;
        }
        return 2;
    }

    public static boolean isBatteryCharging(int chargeState) {
        return chargeState == ChargeStatus.Charging.ordinal();
    }

    public static boolean needShowDriveDistance(int chargeState) {
        return chargeState == ChargeStatus.Charging.ordinal() || chargeState == ChargeStatus.Appointment.ordinal() || chargeState == ChargeStatus.ChargeError.ordinal() || chargeState == ChargeStatus.ChargeDone.ordinal() || chargeState == ChargeStatus.Discharging.ordinal() || chargeState == ChargeStatus.DischargeDone.ordinal() || chargeState == ChargeStatus.DischargeError.ordinal();
    }

    public static boolean isEleValueValid(int eleValue) {
        if (eleValue < 0 || eleValue > 100) {
            Logger.i(TAG, "isEleValueValid: false");
            return false;
        }
        return true;
    }

    public static boolean isMileageValid(int mileage) {
        if (mileage < 0 || mileage > 1022) {
            Logger.i(TAG, "isMileageValid false");
            return false;
        }
        return true;
    }

    public static boolean isDoorClosed(int type, int[] state) {
        boolean z = true;
        if (state == null || state.length < 2) {
            return true;
        }
        if (type == 1) {
            if (state[0] != 0) {
                z = false;
            }
            boolean isClosed = z;
            return isClosed;
        } else if (type != 2) {
            return true;
        } else {
            if (state[1] != 0) {
                z = false;
            }
            boolean isClosed2 = z;
            return isClosed2;
        }
    }

    public static int getWindColorLevel(int color) {
        if (color == 2 || color != 3) {
            return 0;
        }
        return 1;
    }

    public static String getDriveDistanceContent(Context context, boolean needShowDriveDistance, int driveDistance) {
        if (driveDistance <= 20 && !needShowDriveDistance) {
            String content = "--" + context.getString(R.string.unit_distance);
            return content;
        }
        String content2 = driveDistance + context.getString(R.string.unit_distance);
        return content2;
    }

    public static int[] createArraysTime(Context context) {
        int[] time = new int[2];
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        try {
            int hour = calendar.get(11);
            int minute = calendar.get(12);
            time[0] = hour;
            time[1] = minute;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String createFormatTime(Context context) {
        boolean is24HourFormat = DateFormat.is24HourFormat(context);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        String timeFormat = is24HourFormat ? "HH:mm:ss" : "hh:mm:ss";
        SimpleDateFormat dateSimpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeSimpleFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());
        new StringBuffer("");
        JSONObject object = new JSONObject();
        if (context != null) {
            try {
                object.put("Date", dateSimpleFormat.format(calendar.getTime()));
                object.put("WeekDay", calendar.get(7));
                int i = 0;
                object.put("TimeFormat", DateFormat.is24HourFormat(context) ? 0 : 1);
                if (calendar.get(9) != 0) {
                    i = 1;
                }
                object.put("AmOrPm", i);
                object.put("Time", timeSimpleFormat.format(calendar.getTime()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object.toString();
    }

    public static String createDayNight(boolean isNight) {
        JSONObject object = new JSONObject();
        try {
            object.put("SyncMode", "DayNight");
            object.put("msgId", "");
            object.put("SyncProgress", isNight ? 1 : 0);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onIgStatusChanged(int igStatus) {
        if (igStatus == 0) {
            CarCheckHelper.notifyScreenOn(false);
        } else if (igStatus == 1) {
            CarCheckHelper.notifyScreenOn(true);
        }
    }
}
