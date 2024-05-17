package com.xiaopeng.systemui.navigationbar;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import androidx.lifecycle.Observer;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.utils.BIHelper;
import com.xiaopeng.systemui.viewmodel.car.HvacViewModel;
/* loaded from: classes24.dex */
public class NavigationHvacHandler {
    private static final int PROGRESS_SCALE = 10;
    private static final String TAG = "NavigationHvacHandler";
    private Context mContext;
    private HvacInfo mHvacInfo;
    private OnHvacListener mHvacListener;
    private HvacViewModel mHvacViewModel;
    private NavigationBar mNavigationBar;
    private INavigationBarView mNavigationBarView;
    private boolean mPowerOn = false;
    private boolean mHvacAuto = false;
    private int mHvacWindSpeed = 0;
    private int mHvacWindColor = 2;
    private int mHvacQuality = -1;
    private boolean mFrontDefrost = false;
    private boolean mBackDefrost = false;
    private boolean mHvacDriverSync = false;
    private float mHvacTempDriver = 18.0f;
    private float mHvacTempPassenger = 18.0f;
    private boolean mHvacPurgeMode = false;
    private boolean mHvacAutoDefogWorkSt = false;
    private boolean mHvacPanelFocused = false;
    private Handler mHandler = new Handler();

    public void onTemperatureProgressChanged(int type, float temperature, boolean fromUser) {
        int hvacType = CarController.TYPE_HVAC_DRIVER_TEMP;
        if (type == 1) {
            hvacType = CarController.TYPE_HVAC_DRIVER_TEMP;
        } else if (type == 2) {
            hvacType = CarController.TYPE_HVAC_PASSENGER_TEMP;
        }
        onHvacItemChanged(hvacType, Float.valueOf(temperature));
        if (CarModelsManager.getFeature().needSyncTemperature() && fromUser) {
            checkAndSyncTemperature(hvacType, temperature);
        }
    }

    public HvacInfo getHvacInfo() {
        return this.mHvacInfo;
    }

    public int getInnerQuality() {
        return this.mHvacQuality;
    }

    public float getTemperature(int type) {
        if (type == 2) {
            Logger.d(TAG, "getTemperature : " + type + " temp = " + this.mHvacTempPassenger);
            return this.mHvacTempPassenger;
        }
        Logger.d(TAG, "getTemperature : " + type + " temp = " + this.mHvacTempDriver);
        return this.mHvacTempDriver;
    }

    /* loaded from: classes24.dex */
    public interface OnHvacListener {
        default void onComboViewScroll() {
        }

        default void onHvacItemChanged(int type, Object value) {
        }
    }

    public NavigationHvacHandler(Context context, NavigationBar navigationBar, HvacViewModel model) {
        this.mContext = context;
        this.mHvacViewModel = model;
        this.mNavigationBar = navigationBar;
    }

    public void setNavigationBarView(INavigationBarView navigationBarView) {
        this.mNavigationBarView = navigationBarView;
    }

    public void setListener(OnHvacListener listener) {
        this.mHvacListener = listener;
    }

    public void init() {
        initHvacViewModel();
    }

    public void onActivityChanged(ComponentName cn) {
        boolean focused = ActivityController.isHvacPanelFocused();
        if (focused != this.mHvacPanelFocused) {
            onHvacPanelChanged(focused);
        }
        this.mHvacPanelFocused = focused;
    }

    private void initHvacViewModel() {
        Logger.d(TAG, "initHvacViewModel() called");
        this.mHvacViewModel.getHvacPowerData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$jQkDrqAfrqrTB9Vvmg72A-Z8JRI
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$0$NavigationHvacHandler((Boolean) obj);
            }
        });
        this.mHvacViewModel.getHvacAutoData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$_GXQLYDbC0KgOnyildzcZNXRl8M
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$1$NavigationHvacHandler((Boolean) obj);
            }
        });
        this.mHvacViewModel.getHvacWindSpeedData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$O4fhHb5nnBPnBDwVUeBwQqBueaY
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$2$NavigationHvacHandler((Integer) obj);
            }
        });
        this.mHvacViewModel.getHvacWindColorData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$XPObhOipb-SThRdjknGGzPFLRNk
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$3$NavigationHvacHandler((Integer) obj);
            }
        });
        this.mHvacViewModel.getHvacQualityInnerData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$MdOl9B_bxUmURwp-Xni01QNBF5Y
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$4$NavigationHvacHandler((Integer) obj);
            }
        });
        this.mHvacViewModel.getHvacFrontDefrostData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$NX4ja9MaLYlfe5bz7y0lZHz4DEM
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$5$NavigationHvacHandler((Boolean) obj);
            }
        });
        this.mHvacViewModel.getHvacBackDefrostData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$2IoNdJUVbRZA1mILFtuhC4JftTU
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$6$NavigationHvacHandler((Boolean) obj);
            }
        });
        this.mHvacViewModel.getHvacDriverSyncData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$3k5SoSphF08HiVppmcfXamEn7i4
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$7$NavigationHvacHandler((Boolean) obj);
            }
        });
        this.mHvacViewModel.getHvacTempDriverData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$qyGA4uYQp4qLE2AWPEn1XWxctDM
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$8$NavigationHvacHandler((Float) obj);
            }
        });
        this.mHvacViewModel.getHvacTempPassengerData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$T5SG5CsoF9BL2TfcrCYf2p-BDUQ
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$9$NavigationHvacHandler((Float) obj);
            }
        });
        this.mHvacViewModel.getHvacPurgeModeData().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$RLM-CeTduit-ZPkvjUMHcXndLQI
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$10$NavigationHvacHandler((Boolean) obj);
            }
        });
        this.mHvacViewModel.getHvacAutoDefogWorkSt().observe(this.mNavigationBar, new Observer() { // from class: com.xiaopeng.systemui.navigationbar.-$$Lambda$NavigationHvacHandler$dNd7FHejhCYQzsDJN1ts1gfUbyM
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                NavigationHvacHandler.this.lambda$initHvacViewModel$11$NavigationHvacHandler((Boolean) obj);
            }
        });
        initValue();
        onHvacChanged();
        onTemperatureChanged(1);
        onTemperatureChanged(2);
        onHvacItemChanged(CarController.TYPE_HVAC_POWER, Boolean.valueOf(this.mHvacViewModel.isHvacPowerOn()));
        onHvacItemChanged(CarController.TYPE_HVAC_AUTO, Boolean.valueOf(this.mHvacViewModel.isHvacAuto()));
        onHvacItemChanged(CarController.TYPE_HVAC_WIND_SPEED, Integer.valueOf(this.mHvacViewModel.getHvacWindSpeed()));
        onHvacItemChanged(CarController.TYPE_HVAC_QUALITY_INNER, Integer.valueOf(this.mHvacViewModel.getHvacQualityInner()));
    }

    public /* synthetic */ void lambda$initHvacViewModel$0$NavigationHvacHandler(Boolean value) {
        Logger.d(TAG, "hvacPowerData changed");
        if (this.mPowerOn != value.booleanValue()) {
            this.mPowerOn = value.booleanValue();
            onHvacChanged();
            onHvacItemChanged(CarController.TYPE_HVAC_POWER, value);
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$1$NavigationHvacHandler(Boolean value) {
        if (this.mHvacAuto != value.booleanValue()) {
            this.mHvacAuto = value.booleanValue();
            onHvacChanged();
            onHvacItemChanged(CarController.TYPE_HVAC_AUTO, value);
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$2$NavigationHvacHandler(Integer value) {
        if (this.mHvacWindSpeed != value.intValue()) {
            this.mHvacWindSpeed = value.intValue();
            onHvacChanged();
            onHvacItemChanged(CarController.TYPE_HVAC_WIND_SPEED, value);
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$3$NavigationHvacHandler(Integer value) {
        if (this.mHvacWindColor != value.intValue()) {
            this.mHvacWindColor = value.intValue();
            onHvacChanged();
            onHvacItemChanged(CarController.TYPE_HVAC_WIND_COLOR, value);
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$4$NavigationHvacHandler(Integer value) {
        if (this.mHvacQuality != value.intValue()) {
            this.mHvacQuality = value.intValue();
            onHvacItemChanged(CarController.TYPE_HVAC_QUALITY_INNER, value);
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$5$NavigationHvacHandler(Boolean value) {
        if (this.mFrontDefrost != value.booleanValue()) {
            this.mFrontDefrost = value.booleanValue();
            onHvacChanged();
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$6$NavigationHvacHandler(Boolean value) {
        if (this.mBackDefrost != value.booleanValue()) {
            this.mBackDefrost = value.booleanValue();
            onHvacChanged();
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$7$NavigationHvacHandler(Boolean value) {
        if (this.mHvacDriverSync != value.booleanValue()) {
            this.mHvacDriverSync = value.booleanValue();
            onHvacChanged();
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$8$NavigationHvacHandler(Float value) {
        if (this.mHvacTempDriver != value.floatValue()) {
            this.mHvacTempDriver = value.floatValue();
            Logger.d(TAG, "initHvacViewModel : mHvacTempDriver = " + this.mHvacTempDriver);
            onTemperatureChanged(1);
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$9$NavigationHvacHandler(Float value) {
        if (this.mHvacTempPassenger != value.floatValue()) {
            this.mHvacTempPassenger = value.floatValue();
            Logger.d(TAG, "initHvacViewModel : mHvacTempPassenger = " + this.mHvacTempPassenger);
            onTemperatureChanged(2);
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$10$NavigationHvacHandler(Boolean value) {
        if (this.mHvacPurgeMode != value.booleanValue()) {
            this.mHvacPurgeMode = value.booleanValue();
            onHvacItemChanged(CarController.TYPE_HVAC_PURGE_MODE, value);
        }
    }

    public /* synthetic */ void lambda$initHvacViewModel$11$NavigationHvacHandler(Boolean value) {
        if (this.mHvacAutoDefogWorkSt != value.booleanValue()) {
            this.mHvacAutoDefogWorkSt = value.booleanValue();
            onHvacItemChanged(CarController.TYPE_HVAC_AUTO_DEFOG_WORK, value);
        }
    }

    public void onHvacComboSingleTapConfirmed(int displayId) {
        startCarHvac(displayId);
        BIHelper.sendBIData(BIHelper.ID.air_conditioning, BIHelper.Type.dock, BIHelper.Action.click, displayId == 0 ? BIHelper.Screen.main : BIHelper.Screen.second);
    }

    private void initValue() {
        this.mPowerOn = this.mHvacViewModel.getHvacPowerData().getValue().booleanValue();
        this.mHvacAuto = this.mHvacViewModel.getHvacAutoData().getValue().booleanValue();
        this.mHvacWindSpeed = this.mHvacViewModel.getHvacWindSpeedData().getValue().intValue();
        this.mHvacWindColor = this.mHvacViewModel.getHvacWindColorData().getValue().intValue();
        this.mHvacQuality = this.mHvacViewModel.getHvacQualityInnerData().getValue().intValue();
        this.mFrontDefrost = this.mHvacViewModel.getHvacFrontDefrostData().getValue().booleanValue();
        this.mBackDefrost = this.mHvacViewModel.getHvacBackDefrostData().getValue().booleanValue();
        this.mHvacDriverSync = this.mHvacViewModel.getHvacDriverSyncData().getValue().booleanValue();
        this.mHvacTempDriver = this.mHvacViewModel.getHvacTempDriverData().getValue().floatValue();
        this.mHvacTempPassenger = this.mHvacViewModel.getHvacTempPassengerData().getValue().floatValue();
        this.mHvacPurgeMode = this.mHvacViewModel.getHvacPurgeModeData().getValue().booleanValue();
        this.mHvacAutoDefogWorkSt = this.mHvacViewModel.getHvacAutoDefogWorkSt().getValue().booleanValue();
    }

    private void onHvacChanged() {
        final boolean isAuto = this.mHvacViewModel.isHvacAuto();
        final boolean isPowerOn = this.mHvacViewModel.isHvacPowerOn();
        final boolean isDriverSync = this.mHvacViewModel.isHvacDriverSync();
        final boolean isPassengerSync = this.mHvacViewModel.isHvacPassengerSync();
        final boolean isBackDefrostOn = this.mHvacViewModel.isHvacBackDefrostOn();
        final boolean isFrontDefrostOn = this.mHvacViewModel.isHvacFrontDefrostOn();
        final int wind = this.mHvacViewModel.getHvacWindSpeed();
        final int windColor = this.mHvacViewModel.getHvacWindColor();
        StringBuffer buffer = new StringBuffer();
        buffer.append("onHvacChanged");
        buffer.append(" isAuto=" + isAuto);
        buffer.append(" isPowerOn=" + isPowerOn);
        buffer.append(" isDriverSync=" + isDriverSync);
        buffer.append(" isPassengerSync=" + isPassengerSync);
        buffer.append(" isBackDefrostOn=" + isBackDefrostOn);
        buffer.append(" isFrontDefrostOn=" + isFrontDefrostOn);
        buffer.append(" wind=" + wind);
        buffer.append(" windColor = " + windColor);
        Logger.d(TAG, buffer.toString());
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.systemui.navigationbar.NavigationHvacHandler.1
            @Override // java.lang.Runnable
            public void run() {
                Logger.d(NavigationHvacHandler.TAG, "onHvacChanged run");
                HvacInfo hvacInfo = new HvacInfo();
                hvacInfo.isAuto = isAuto;
                hvacInfo.isPowerOn = isPowerOn;
                hvacInfo.windLevel = wind;
                hvacInfo.windColor = windColor;
                hvacInfo.isDriverSync = isDriverSync;
                hvacInfo.isPassengerSync = isPassengerSync;
                hvacInfo.isBackDefrostOn = isBackDefrostOn;
                hvacInfo.isFrontDefrostOn = isFrontDefrostOn;
                NavigationHvacHandler.this.mNavigationBarView.setHvacInfo(hvacInfo);
                NavigationHvacHandler.this.mHvacInfo = hvacInfo;
            }
        };
        this.mHandler.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onHvacItemChanged(int type, Object value) {
        OnHvacListener onHvacListener = this.mHvacListener;
        if (onHvacListener != null) {
            onHvacListener.onHvacItemChanged(type, value);
        }
    }

    private void onHvacPanelChanged(boolean focused) {
        this.mNavigationBarView.onHvacPanelChanged(focused);
    }

    private void onTemperatureChanged(final int type) {
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.systemui.navigationbar.NavigationHvacHandler.2
            @Override // java.lang.Runnable
            public void run() {
                int hvacType = -1;
                float temperature = 18.0f;
                int i = type;
                if (i == 1) {
                    hvacType = CarController.TYPE_HVAC_DRIVER_TEMP;
                    temperature = NavigationHvacHandler.this.mHvacViewModel.getHvacDriverTemperature();
                    NavigationHvacHandler.this.mNavigationBarView.setDriverTemperature(temperature);
                    if (CarModelsManager.getFeature().needSyncTemperature()) {
                        NavigationHvacHandler.this.mNavigationBarView.setPassengerTemperature(temperature);
                        NavigationHvacHandler.this.checkAndSyncTemperature(CarController.TYPE_HVAC_DRIVER_TEMP, temperature);
                    }
                } else if (i == 2) {
                    hvacType = CarController.TYPE_HVAC_PASSENGER_TEMP;
                    temperature = NavigationHvacHandler.this.mHvacViewModel.getHvacPassengerTemperature();
                    NavigationHvacHandler.this.mNavigationBarView.setPassengerTemperature(temperature);
                    if (CarModelsManager.getFeature().needSyncTemperature()) {
                        NavigationHvacHandler.this.mNavigationBarView.setDriverTemperature(temperature);
                        NavigationHvacHandler.this.checkAndSyncTemperature(CarController.TYPE_HVAC_PASSENGER_TEMP, temperature);
                    }
                }
                Logger.i(NavigationHvacHandler.TAG, "onTemperatureChanged hvacType=" + hvacType + " temperature=" + temperature);
                NavigationHvacHandler.this.onHvacItemChanged(hvacType, Float.valueOf(temperature));
            }
        };
        this.mHandler.post(runnable);
    }

    public void setPower(boolean on) {
        this.mHvacViewModel.setHvacPower(on);
    }

    public void setDefrost(boolean front, boolean enable) {
        if (front) {
            this.mHvacViewModel.setHvacFrontDefrostOn(enable);
        } else {
            this.mHvacViewModel.setHvacBackDefrostOn(enable);
        }
    }

    public void setSynchronized(int type, boolean enable) {
        if (type == 1) {
            this.mHvacViewModel.setHvacDriverSync(enable);
        } else if (type == 2) {
            this.mHvacViewModel.setHvacPassengerSync(enable);
        }
    }

    public static int getProgressByTemperature(float temperature) {
        return (int) ((temperature / 0.5f) * 10.0f);
    }

    public static float getTemperatureByProgress(int progress) {
        if (progress > 640 || progress < 360) {
            return 18.0f;
        }
        float temperature = (progress / 10) * 0.5f;
        if (progress % 10 > 0 && temperature == 18.0f) {
            return 18.5f;
        }
        return temperature;
    }

    public void setTemperature(int type, boolean step, float temperature) {
        Logger.d(TAG, "setTemperature type=" + type + " step=" + step + " temperature=" + temperature);
        if (type == 1) {
            if (step) {
                this.mHvacViewModel.setHvacDriverTemperatureStep(temperature > 0.0f);
            } else {
                this.mHvacViewModel.setHvacDriverTemperature(temperature);
            }
        } else if (type == 2) {
            if (!CarModelsManager.getFeature().needSyncTemperature()) {
                if (step) {
                    this.mHvacViewModel.setHvacPassengerTemperatureStep(temperature > 0.0f);
                } else {
                    this.mHvacViewModel.setHvacPassengerTemperature(temperature);
                }
            } else if (step) {
                this.mHvacViewModel.setHvacDriverTemperatureStep(temperature > 0.0f);
            } else {
                this.mHvacViewModel.setHvacDriverTemperature(temperature);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkAndSyncTemperature(int type, float temperature) {
        Logger.d(TAG, "checkAndSyncTemperature : type = " + type + " temperature = " + temperature);
        if (type != 2104) {
            if (type == 2106) {
                onHvacItemChanged(CarController.TYPE_HVAC_DRIVER_TEMP, Float.valueOf(temperature));
                return;
            }
            return;
        }
        onHvacItemChanged(CarController.TYPE_HVAC_PASSENGER_TEMP, Float.valueOf(temperature));
    }

    public void startCarHvac(int displayId) {
        String currentActivityName = PackageHelper.getTopActivityName(this.mContext);
        Logger.i(TAG, "startCarHvac currentActivity = " + currentActivityName);
        PackageHelper.startCarHvac(this.mContext, displayId);
    }
}
