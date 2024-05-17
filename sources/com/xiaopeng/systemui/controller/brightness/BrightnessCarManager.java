package com.xiaopeng.systemui.controller.brightness;

import android.car.hardware.icm.CarIcmManager;
import android.content.Context;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.util.FeatureOption;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class BrightnessCarManager {
    private static final String TAG = "XmartBrightness_CarManager";
    private static BrightnessCarManager sCarManager = null;
    private CarController.CarServiceAdapter mCarService;
    private Context mContext;
    private int mIcmBrightness = 0;
    private int mCmsBrightness = 0;
    private int mCmsBrightnessSource = 0;
    private boolean mIgOnCmsBrightness = false;
    private int mCmsAutoMode = 0;

    public static BrightnessCarManager get(Context context) {
        if (sCarManager == null) {
            synchronized (BrightnessManager.class) {
                if (sCarManager == null) {
                    sCarManager = new BrightnessCarManager(context);
                }
            }
        }
        return sCarManager;
    }

    private BrightnessCarManager(Context context) {
        this.mContext = context;
        this.mCarService = CarController.getInstance(this.mContext).getCarServiceAdapter();
    }

    public boolean isIcmConnected() {
        if (FeatureOption.FO_ICM_TYPE == 2) {
            return true;
        }
        CarController.CarServiceAdapter carServiceAdapter = this.mCarService;
        if (carServiceAdapter != null) {
            try {
                CarIcmManager manager = carServiceAdapter.getIcmManager();
                if (manager != null) {
                    if (manager.getIcmConnectionState() == 1) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean isCarConnected() {
        CarController.CarServiceAdapter carServiceAdapter = this.mCarService;
        return carServiceAdapter != null && carServiceAdapter.isCarServiceReady();
    }

    public boolean brightnessCallbackSupport() {
        return FeatureOption.FO_ICM_TYPE == 1 || FeatureOption.FO_ICM_TYPE == 2;
    }

    public void onCarEventChanged(int propId, Object value) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("onCarEventChanged propId=");
            sb.append(propId);
            sb.append(" value=");
            sb.append(value != null ? value.toString() : "null");
            Logger.i(TAG, sb.toString());
            switch (propId) {
                case 554702353:
                    boolean sysReady = false;
                    if (value != null) {
                        JSONObject jsonObject = new JSONObject(value.toString());
                        sysReady = "SysReady".equals(jsonObject.getString("SyncMode"));
                    }
                    if (sysReady) {
                        onIcmReady();
                        return;
                    }
                    return;
                case 557847561:
                    int state = ((Integer) value).intValue();
                    if (1 == state) {
                        this.mIgOnCmsBrightness = true;
                        onLampChanged();
                        onEnvironmentChanged();
                        return;
                    }
                    return;
                case 557848078:
                    boolean connected = ((Integer) value).intValue() == 1;
                    if (connected) {
                        onIcmReady();
                    }
                    return;
                case 557848124:
                    this.mIcmBrightness = ((Integer) value).intValue();
                    BrightnessManager.get(this.mContext).onIcmBrightnessChanged(2);
                    return;
                case 557849633:
                case 557849640:
                    onLampChanged();
                    return;
                case 557849794:
                    onEnvironmentChanged();
                    return;
                case 557859329:
                    this.mCmsAutoMode = ((Integer) value).intValue();
                    BrightnessManager.get(this.mContext).onCMSAutoModeChanged(2);
                    return;
                case 557916704:
                    try {
                        Integer[] values = (Integer[]) value;
                        int direction = values[0].intValue() == 0 ? 1 : -1;
                        int adj = values[1].intValue();
                        int brightness = this.mIcmBrightness + (direction * 5 * adj);
                        BrightnessSettings.putInt(this.mContext, "screen_brightness_2", Math.max(1, Math.min(100, brightness)));
                        return;
                    } catch (Exception e) {
                        return;
                    }
                case 557924883:
                    Integer[] values2 = (Integer[]) value;
                    this.mCmsBrightness = values2[0].intValue();
                    this.mCmsBrightnessSource = values2[1].intValue();
                    Logger.i(TAG, "ID_CMS_BRIGHT_SET_WITH_FLAG mCmsBrightness=" + this.mCmsBrightness + " mCmsBrightnessSource=" + this.mCmsBrightnessSource);
                    if (this.mIgOnCmsBrightness) {
                        if (this.mCmsBrightnessSource == 2) {
                            int CdcuBrightness = BrightnessSettings.getInt(this.mContext, BrightnessSettings.KEY_CMS_BRIGHTNESS, this.mCmsBrightness);
                            setCMSBrightness(CdcuBrightness);
                        }
                        this.mIgOnCmsBrightness = false;
                    }
                    if (this.mCmsBrightnessSource == 1) {
                        this.mCmsBrightness = (this.mCmsBrightness & 255) | 512;
                        BrightnessSettings.putInt(this.mContext, BrightnessSettings.KEY_CMS_BRIGHTNESS, this.mCmsBrightness);
                        return;
                    }
                    return;
                default:
                    return;
            }
        } catch (Exception e2) {
        }
    }

    public void setCMSAutoMode(int value) {
        boolean carConnected = isCarConnected();
        if (carConnected) {
            try {
                this.mCarService.setCMSAutoBrightSw(value);
            } catch (Exception e) {
                Logger.e(TAG, "setCMSAutoMode failed");
            }
        }
    }

    public int getCMSAutoMode() {
        boolean carConnected = isCarConnected();
        if (carConnected) {
            try {
                return this.mCarService.getCMSAutoBrightSwSt();
            } catch (Exception e) {
                Logger.e(TAG, "getCMSAutoMode failed");
            }
        }
        return this.mCmsAutoMode;
    }

    public void setCMSBrightness(int brightness) {
        boolean carConnected = isCarConnected();
        if (carConnected) {
            try {
                this.mCarService.setCMSBrightWithFlag(brightness, 1);
                Logger.e(TAG, "setCmsBrightness success");
            } catch (Exception e) {
                Logger.e(TAG, "setCmsBrightness failed");
            }
        }
    }

    public void setCMSTemporaryBrightness(int brightness) {
        boolean carConnected = isCarConnected();
        if (carConnected) {
            try {
                this.mCarService.setCMSBrightWithFlag(brightness, 0);
            } catch (Exception e) {
                Logger.e(TAG, "setCMSTemporaryBrightness failed");
            }
        }
    }

    public int getIcmBrightness() {
        int brightness = this.mIcmBrightness;
        boolean icmConnected = isIcmConnected();
        boolean carConnected = isCarConnected();
        boolean callbackSupport = brightnessCallbackSupport();
        if (FeatureOption.FO_ICM_TYPE == 2) {
            brightness = BrightnessSettings.getInt(this.mContext, "screen_brightness_callback_2", brightness);
        } else if (carConnected) {
            try {
                brightness = this.mCarService.getIcmBrightness();
            } catch (Exception e) {
                brightness = this.mIcmBrightness;
            }
            if (!callbackSupport) {
                brightness = this.mIcmBrightness;
            }
        }
        Logger.i(TAG, "getIcmBrightness brightness=" + brightness + " carConnected=" + carConnected + " icmConnected=" + icmConnected);
        return brightness;
    }

    public void setIcmBrightness(int brightness) {
        boolean carConnected = isCarConnected();
        boolean icmConnected = isIcmConnected();
        boolean callbackSupport = brightnessCallbackSupport();
        if (FeatureOption.FO_ICM_TYPE == 2) {
            int icm_brightness = BrightnessSettings.getRealBrightnessByPercent(brightness);
            BrightnessSettings.putInt(this.mContext, BrightnessSettings.KEY_SCREEN_BRIGHTNESS_FOR_ICM, icm_brightness);
            BrightnessSettings.putInt(this.mContext, "screen_brightness_callback_2", brightness);
            this.mIcmBrightness = brightness;
        } else if (carConnected) {
            try {
                this.mCarService.setIcmBrightness(brightness);
                if (!callbackSupport) {
                    this.mIcmBrightness = brightness;
                }
            } catch (Exception e) {
                Logger.e(TAG, "setIcmBrightness failed");
            }
        }
        Logger.i(TAG, "setIcmBrightness connected=" + carConnected + " icmConnected=" + icmConnected + " brightness=" + brightness + " value=" + this.mIcmBrightness);
    }

    public void onIcmReady() {
        BrightnessManager.get(this.mContext).onIcmReady();
        this.mIcmBrightness = BrightnessManager.get(this.mContext).getBrightness(2);
    }

    public void onLampChanged() {
        if (BrightnessSettings.BRIGHTNESS_DARK_ENV_SUPPORT) {
            return;
        }
        BrightnessManager.get(this.mContext).onDarkBrightnessChanged();
    }

    public void onEnvironmentChanged() {
        if (BrightnessSettings.BRIGHTNESS_DARK_ENV_SUPPORT) {
            BrightnessManager.get(this.mContext).onDarkBrightnessChanged();
        }
    }

    public boolean hasCiuDevice() {
        return false;
    }

    public boolean isLampActive() {
        boolean carConnected = isCarConnected();
        if (!carConnected) {
            return false;
        }
        try {
            boolean isFarLampOn = this.mCarService.isFarLampOn();
            int lampState = this.mCarService.getNearLampState();
            int lampGroup = this.mCarService.getHeadLampGroup();
            boolean z = false;
            boolean lampActive = lampState == 1 || lampGroup == 2;
            if (!isFarLampOn && lampActive) {
                z = true;
            }
            boolean isLampActive = z;
            return isLampActive;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnvironmentActive() {
        boolean carConnected = isCarConnected();
        if (!carConnected) {
            return false;
        }
        int env = this.mCarService.getEnvironmentMode();
        boolean z = true;
        if (env != 2 && env != 1) {
            z = false;
        }
        boolean isEnvrActive = z;
        return isEnvrActive;
    }
}
