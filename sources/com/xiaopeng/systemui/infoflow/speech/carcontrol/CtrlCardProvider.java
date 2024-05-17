package com.xiaopeng.systemui.infoflow.speech.carcontrol;

import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.atl.CarAtlManager;
import android.car.hardware.bcm.CarBcmManager;
import android.car.hardware.hvac.CarHvacManager;
import android.util.Log;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.lib.utils.info.BuildInfoUtils;
import com.xiaopeng.libcarcontrol.CarControlCallback;
import com.xiaopeng.libcarcontrol.CarControlManager;
import com.xiaopeng.libcarcontrol.ChargeStatus;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.protocol.bean.CardValue;
import com.xiaopeng.speech.protocol.bean.base.CommandValue;
import com.xiaopeng.speech.protocol.event.CaracEvent;
import com.xiaopeng.speech.protocol.event.CarcontrolEvent;
import com.xiaopeng.speech.protocol.event.SystemEvent;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.carmanager.XuiClientWrapper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardProvider;
import com.xiaopeng.systemui.infoflow.speech.ui.model.CtrlCardContent;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.xuimanager.XUIServiceNotConnectedException;
import com.xiaopeng.xuimanager.ambientlight.AmbientLightManager;
/* loaded from: classes24.dex */
public class CtrlCardProvider implements ICtrlCardProvider {
    private static final String TAG = "CtrlCardProvider";
    private AmbientLightManager.AmbientLightEventListener mAmbientLightEventListener;
    private CarBcmManager.CarBcmEventCallback mCarBcmEventCallback;
    private CtrlCardContent mCtrlCardContent;
    private int mGroupType;
    private ICtrlCardPresenter mPresenter;
    private AcCallback mAcCallback = new AcCallback();
    private CarControlManager mCarControlManager = CarControlManager.getInstance(ContextUtils.getContext());
    private CarHvacManager mHvacManager = CarClientWrapper.getInstance().getHvacManager();
    private CarAtlManager mCarAtlManager = CarClientWrapper.getInstance().getCarAtlManager();
    private AmbientLightManager mAmbientLightManager = XuiClientWrapper.getInstance().getAtlManager();
    private CarBcmManager mCarBcmManager = CarClientWrapper.getInstance().getCarBcmManager();
    private CarAtlManager.CarAtlEventCallback mCarAtlEventCallback = new CarAtlManager.CarAtlEventCallback() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardProvider.1
        private int lastBrightValue;
        private int lastColorValue;

        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            final int value;
            final int value2 = ((Integer) carPropertyValue.getValue()).intValue();
            if (carPropertyValue.getPropertyId() == 557848582) {
                if (this.lastBrightValue != value2) {
                    Log.d(CtrlCardProvider.TAG, "onChangeEvent() atl ID_ATL_BRIGHTNESSCFG brightness =" + carPropertyValue.toString());
                    if (CtrlCardProvider.this.mPresenter != null) {
                        if (CtrlCardProvider.this.mGroupType == 11) {
                            ThreadUtils.runOnMainThreadDelay(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardProvider.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    if (CtrlCardProvider.this.mPresenter != null) {
                                        CtrlCardProvider.this.mPresenter.setATLBrightness(value2);
                                    }
                                }
                            }, 500L);
                        } else {
                            Log.d(CtrlCardProvider.TAG, "onChangeEvent: is not brightness mGroupType=" + CtrlCardProvider.this.mGroupType);
                        }
                    }
                    this.lastBrightValue = value2;
                }
            } else if (carPropertyValue.getPropertyId() == 557848579 && this.lastColorValue != (value = value2 + 1)) {
                Log.d(CtrlCardProvider.TAG, "onChangeEvent() atl ID_ATL_ATLS_ALLCTRL all color =" + carPropertyValue.toString());
                if (CtrlCardProvider.this.mPresenter != null) {
                    if (CtrlCardProvider.this.mGroupType == 12) {
                        ThreadUtils.runOnMainThreadDelay(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardProvider.1.2
                            @Override // java.lang.Runnable
                            public void run() {
                                if (CtrlCardProvider.this.mPresenter != null) {
                                    CtrlCardProvider.this.mPresenter.setAtlColor(value);
                                }
                            }
                        }, 500L);
                    } else {
                        Log.d(CtrlCardProvider.TAG, "onChangeEvent: is not color mGroupType=" + CtrlCardProvider.this.mGroupType);
                    }
                }
                this.lastColorValue = value;
            }
        }

        public void onErrorEvent(int propertyId, int zone) {
            Logger.e(CtrlCardProvider.TAG, "CarAtlManager.CarAtlEventCallback onErrorEvent");
        }
    };

    public CtrlCardProvider(ICtrlCardPresenter presenter) {
        this.mPresenter = presenter;
        try {
            this.mHvacManager.registerCallback(this.mAcCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
        this.mAmbientLightEventListener = new AmbientLightManager.AmbientLightEventListener() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardProvider.2
            public void onErrorEvent(int i, int i1) {
                Log.d(CtrlCardProvider.TAG, "AmbientLightManager.mAmbientLightEventListener onErrorEvent() called with: i = [" + i + "], i1 = [" + i1 + NavigationBarInflaterView.SIZE_MOD_END);
            }
        };
        this.mCarBcmEventCallback = new AnonymousClass3();
        try {
            this.mCarBcmManager.registerCallback(this.mCarBcmEventCallback);
        } catch (CarNotConnectedException e2) {
            e2.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardProvider$3  reason: invalid class name */
    /* loaded from: classes24.dex */
    public class AnonymousClass3 implements CarBcmManager.CarBcmEventCallback {
        private int lastSeatHeatLevel = -1;
        private int lastPsnSeatHeatLevel = -1;
        private int lastSeatVentLevel = -1;

        AnonymousClass3() {
        }

        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            final int value;
            if (carPropertyValue == null) {
                return;
            }
            int type = carPropertyValue.getPropertyId();
            if (type == 356517139) {
                final int value2 = ((Integer) carPropertyValue.getValue()).intValue();
                if (this.lastSeatVentLevel != value2) {
                    Log.d(CtrlCardProvider.TAG, "onChangeEvent() ID_BCM_SEAT_VENT_LEVEL =" + value2);
                    if (CtrlCardProvider.this.mPresenter != null) {
                        if (CtrlCardProvider.this.mGroupType == 15) {
                            ThreadUtils.runOnMainThreadDelay(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.-$$Lambda$CtrlCardProvider$3$Rub4l9toc8taqVtvmwKtYIUhOHw
                                @Override // java.lang.Runnable
                                public final void run() {
                                    CtrlCardProvider.AnonymousClass3.this.lambda$onChangeEvent$2$CtrlCardProvider$3(value2);
                                }
                            }, 500L);
                            return;
                        }
                        Log.d(CtrlCardProvider.TAG, "onChangeEvent: is not seatVent mGroupType=" + CtrlCardProvider.this.mGroupType);
                    }
                }
            } else if (type == 557849638) {
                final int value3 = ((Integer) carPropertyValue.getValue()).intValue();
                if (this.lastSeatHeatLevel != value3) {
                    Log.d(CtrlCardProvider.TAG, "onChangeEvent() ID_BCM_SEAT_HEAT_LEVEL =" + value3);
                    if (CtrlCardProvider.this.mPresenter != null) {
                        if (CtrlCardProvider.this.mGroupType == 14) {
                            ThreadUtils.runOnMainThreadDelay(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.-$$Lambda$CtrlCardProvider$3$3OrxSzpddo6hQyAE9jEsipY3CDI
                                @Override // java.lang.Runnable
                                public final void run() {
                                    CtrlCardProvider.AnonymousClass3.this.lambda$onChangeEvent$0$CtrlCardProvider$3(value3);
                                }
                            }, 500L);
                            return;
                        }
                        Log.d(CtrlCardProvider.TAG, "onChangeEvent: is not seatHeat mGroupType=" + CtrlCardProvider.this.mGroupType);
                    }
                }
            } else if (type == 557849701 && this.lastPsnSeatHeatLevel != (value = ((Integer) carPropertyValue.getValue()).intValue())) {
                Log.d(CtrlCardProvider.TAG, "onChangeEvent() ID_BCM_PSN_SEAT_HEAT_LEVEL =" + value);
                if (CtrlCardProvider.this.mPresenter != null) {
                    if (CtrlCardProvider.this.mGroupType == 13) {
                        ThreadUtils.runOnMainThreadDelay(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.-$$Lambda$CtrlCardProvider$3$yIeQHqsFvV31DBY1xqvGj6Dogjs
                            @Override // java.lang.Runnable
                            public final void run() {
                                CtrlCardProvider.AnonymousClass3.this.lambda$onChangeEvent$1$CtrlCardProvider$3(value);
                            }
                        }, 500L);
                        return;
                    }
                    Log.d(CtrlCardProvider.TAG, "onChangeEvent: is not psnSeatHeat mGroupType=" + CtrlCardProvider.this.mGroupType);
                }
            }
        }

        public /* synthetic */ void lambda$onChangeEvent$0$CtrlCardProvider$3(int value) {
            if (CtrlCardProvider.this.mPresenter != null) {
                CtrlCardProvider.this.mPresenter.setSeatHeatLevel(value);
                this.lastSeatHeatLevel = value;
            }
        }

        public /* synthetic */ void lambda$onChangeEvent$1$CtrlCardProvider$3(int value) {
            if (CtrlCardProvider.this.mPresenter != null) {
                CtrlCardProvider.this.mPresenter.setSeatHeatLevel(value);
                this.lastPsnSeatHeatLevel = value;
            }
        }

        public /* synthetic */ void lambda$onChangeEvent$2$CtrlCardProvider$3(int value) {
            if (CtrlCardProvider.this.mPresenter != null) {
                CtrlCardProvider.this.mPresenter.setSeatVentLevel(value);
                this.lastSeatVentLevel = value;
            }
        }

        public void onErrorEvent(int i, int i1) {
            Logger.d(CtrlCardProvider.TAG, "onErrorEvent() called with: i = [" + i + "], i1 = [" + i1 + NavigationBarInflaterView.SIZE_MOD_END);
        }
    }

    void handleException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            Logger.e(TAG, "IllegalArgumentException:" + e);
            return;
        }
        Logger.e(TAG, e.toString());
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardProvider
    public void register() {
        try {
            this.mCarAtlManager.registerCallback(this.mCarAtlEventCallback);
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
        try {
            this.mHvacManager.registerCallback(this.mAcCallback);
        } catch (CarNotConnectedException e2) {
            e2.printStackTrace();
        }
        AmbientLightManager ambientLightManager = this.mAmbientLightManager;
        if (ambientLightManager != null) {
            try {
                ambientLightManager.registerListener(this.mAmbientLightEventListener);
            } catch (XUIServiceNotConnectedException e3) {
                e3.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardProvider
    public void unRegister() {
        Log.d(TAG, "unRegister:");
        try {
            this.mCarAtlManager.unregisterCallback(this.mCarAtlEventCallback);
            this.mCarAtlEventCallback = null;
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
        AmbientLightManager ambientLightManager = this.mAmbientLightManager;
        if (ambientLightManager != null) {
            try {
                ambientLightManager.unregisterListener(this.mAmbientLightEventListener);
                this.mAmbientLightEventListener = null;
            } catch (XUIServiceNotConnectedException e2) {
                e2.printStackTrace();
            }
        }
        CarHvacManager carHvacManager = this.mHvacManager;
        if (carHvacManager != null) {
            carHvacManager.unregisterCallback(this.mAcCallback);
            this.mAcCallback = null;
        }
        this.mGroupType = -1;
        this.mPresenter = null;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardProvider
    public float getCurrentModeValue() {
        return this.mCarControlManager.getHvacDriverTemp();
    }

    public int getTempMode() {
        CarHvacManager carHvacManager = this.mHvacManager;
        if (carHvacManager == null) {
            return -1;
        }
        try {
            carHvacManager.getHvacTempSyncMode();
            int mode = this.mHvacManager.getHvacTempAcMode();
            return mode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void setHvacDriverTemp(float value) {
        String event;
        int intValue = (int) value;
        String d = "0";
        if (value > intValue) {
            d = BuildInfoUtils.BID_PT_SPECIAL_1;
        }
        Log.d(TAG, "setHvacDriverTemp() called with: value = [" + value + "]intValue=" + intValue + "d=" + d);
        int i = this.mGroupType;
        if (i == 6) {
            event = CaracEvent.TEMP_DRIVER_SET;
        } else if (i == 7) {
            event = CaracEvent.TEMP_PASSENGER_SET;
        } else {
            event = CaracEvent.TEMP_SET;
        }
        sendSpeechData(event, intValue, d);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardProvider
    public CtrlCardContent getCardContent(int groupType, CardValue cardValue) {
        int themeFirstColor;
        int currentCardType;
        this.mCtrlCardContent = new CtrlCardContent();
        this.mGroupType = groupType;
        if (isSetTemp(groupType)) {
            Logger.d(TAG, "groupType is temp rating");
            double acDriverTemp = 0.0d;
            if (cardValue != null) {
                acDriverTemp = cardValue.getAcDriverTemp();
            }
            this.mCtrlCardContent = getTempCtrlCardContent(groupType, (float) acDriverTemp, this.mCtrlCardContent);
        } else if (isSetWind(groupType)) {
            Logger.d(TAG, "groupType is wind rating");
            int acWindLv = 0;
            if (cardValue != null) {
                acWindLv = cardValue.getAcWindLv();
            }
            this.mCtrlCardContent = getWindCtrlCardContent(this.mCtrlCardContent, acWindLv);
        } else if (11 == groupType) {
            Logger.d(TAG, "groupType is lighting brightness");
            int brightnessLevel = 10;
            try {
                brightnessLevel = this.mCarAtlManager.getBrightnessLevel();
                currentCardType = 11;
            } catch (Exception e) {
                Logger.e(TAG, "getCardContent error msg=" + e.getMessage());
                currentCardType = 11;
            }
            if (cardValue != null && cardValue.getAtmosphereBrightness() != 0) {
                this.mCtrlCardContent.setData(cardValue.getAtmosphereBrightness() + "");
            } else {
                this.mCtrlCardContent.setData(brightnessLevel + "");
            }
            this.mCtrlCardContent.setType(currentCardType);
        } else if (12 == groupType) {
            Logger.d(TAG, "groupType is lighting color");
            int colorValue = 1;
            try {
                int themeFirstColor2 = this.mCarAtlManager.getThemeFirstColor();
                int themeSecondColor = this.mCarAtlManager.getThemeSecondColor();
                String effectType = this.mAmbientLightManager.getAmbientLightEffectType();
                int ambientLightDoubleSecondColor = this.mAmbientLightManager.getAmbientLightDoubleSecondColor(effectType);
                int ambientLightMonoColor = this.mAmbientLightManager.getAmbientLightMonoColor(effectType);
                int ambientLightDoubleFirstColor = this.mAmbientLightManager.getAmbientLightDoubleFirstColor(effectType);
                Log.d(TAG, "ambientLightDoubleSecondColor: " + ambientLightDoubleSecondColor + "ambientLightMonoColor: " + ambientLightMonoColor + "ambientLightDoubleFirstColor: " + ambientLightDoubleFirstColor);
                colorValue = themeFirstColor2;
                StringBuilder sb = new StringBuilder();
                sb.append("getCardContent lightcolor themeFirstColor=");
                sb.append(themeFirstColor2);
                sb.append("themeSecondColor=");
                sb.append(themeSecondColor);
                Logger.d(TAG, sb.toString());
                themeFirstColor = 12;
            } catch (Exception e2) {
                Logger.e(TAG, "getCardContent error msg=" + e2.getMessage());
                themeFirstColor = 12;
            }
            if (cardValue != null && cardValue.getAtmosphereColor() != 0) {
                this.mCtrlCardContent.setData(cardValue.getAtmosphereColor() + "");
            } else {
                this.mCtrlCardContent.setData(colorValue + "");
            }
            this.mCtrlCardContent.setType(themeFirstColor);
        } else if (13 == groupType) {
            this.mCtrlCardContent.setType(13);
            if (cardValue != null) {
                this.mCtrlCardContent.setData(cardValue.getAcSeatHeatingLv() + "");
            }
            Logger.d(TAG, "groupType is seat passenger hot");
        }
        if (14 == groupType) {
            this.mCtrlCardContent.setType(14);
            if (cardValue != null) {
                this.mCtrlCardContent.setData(cardValue.getAcSeatHeatingLv() + "");
            }
            Logger.d(TAG, "groupType is seat driver hot");
        }
        if (15 == groupType) {
            this.mCtrlCardContent.setType(15);
            if (cardValue != null) {
                this.mCtrlCardContent.setData(cardValue.getAcSeatVentilateLv() + "");
            }
            Logger.d(TAG, "groupType is seat vent");
        } else if (16 == groupType) {
            this.mCtrlCardContent.setType(16);
            if (cardValue != null && cardValue.getScreenBrightness() != 0) {
                this.mCtrlCardContent.setData(cardValue.getScreenBrightness() + "");
            }
            Logger.d(TAG, "groupType is screen brightness");
        } else if (17 == groupType) {
            this.mCtrlCardContent.setType(17);
            if (cardValue != null && cardValue.getIcmBrightness() != 0) {
                this.mCtrlCardContent.setData(cardValue.getIcmBrightness() + "");
            }
            Logger.d(TAG, "groupType is icm brightness");
        }
        Logger.d(TAG, "getCardContent= " + GsonUtil.toJson(this.mCtrlCardContent));
        return this.mCtrlCardContent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSetTemp(int groupType) {
        if (5 != groupType && 6 != groupType && 7 != groupType) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSetWind(int groupType) {
        if (1 != groupType) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CtrlCardContent getTempCtrlCardContent(int groupType, float currentTemp, CtrlCardContent ctrlCardContent) {
        int currentType = 8;
        String title = "";
        CarHvacManager carHvacManager = this.mHvacManager;
        if (carHvacManager != null) {
            try {
                int acHeatNatureSt = carHvacManager.getAcHeatNatureSt();
                Log.d(TAG, "getTempCtrlCardContent: acHeatNatureSt=" + acHeatNatureSt);
                if (acHeatNatureSt == 1) {
                    currentType = 8;
                    title = "- 制冷模式";
                } else if (acHeatNatureSt == 3) {
                    currentType = 9;
                    title = "- 制热模式";
                } else if (acHeatNatureSt == 5) {
                    currentType = 10;
                    title = "- 通风模式";
                } else if (acHeatNatureSt == 7) {
                    currentType = 10;
                    title = "- 自动模式";
                } else {
                    currentType = 8;
                }
            } catch (Exception e) {
                Logger.e(TAG, "getTempCtrlCardContent getAcHeatNatureSt error" + e.getMessage());
            }
            ctrlCardContent.setType(currentType);
        }
        float tempValue = 18.0f;
        try {
            if (groupType == 6) {
                ctrlCardContent.setTitle("主驾温度 " + title);
                tempValue = this.mHvacManager.getHvacTempDriverValue();
            } else if (groupType == 7) {
                ctrlCardContent.setTitle("副驾温度 " + title);
                tempValue = this.mHvacManager.getHvacTempPsnValue();
            } else if (groupType == 5) {
                float driverTemp = this.mHvacManager.getHvacTempDriverValue();
                float psnTemp = this.mHvacManager.getHvacTempPsnValue();
                if (driverTemp != psnTemp) {
                    ctrlCardContent.setTitle("主驾温度 " + title);
                } else {
                    ctrlCardContent.setTitle("空调温度 " + title);
                }
                tempValue = driverTemp;
            } else {
                tempValue = this.mHvacManager.getHvacInnerTemp();
                ctrlCardContent.setTitle("车内温度 " + title);
            }
        } catch (Exception e2) {
            Logger.e(TAG, "getTempCtrlCardContent gettempvaule error" + e2.getMessage());
        }
        if (currentTemp != 0.0f) {
            ctrlCardContent.setData(currentTemp + "");
        } else {
            ctrlCardContent.setData(tempValue + "");
        }
        Log.d(TAG, "getTempCtrlCardContent: " + GsonUtil.toJson(ctrlCardContent));
        return ctrlCardContent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CtrlCardContent getWindCtrlCardContent(CtrlCardContent ctrlCardContent, int currentWindLv) {
        int currentType = 2;
        int windLevel = 1;
        int acHeatNatureSt = 0;
        try {
            acHeatNatureSt = this.mHvacManager.getAcHeatNatureSt();
        } catch (Exception e) {
            Logger.e(TAG, "getWindCtrlCardContent acHeatNatureSt error" + e.getMessage());
        }
        Log.d(TAG, "getWindCtrlCardContent: acHeatNatureSt " + acHeatNatureSt);
        try {
            windLevel = this.mHvacManager.getHvacWindSpeedLevel();
            if (acHeatNatureSt == 1) {
                currentType = 2;
            } else if (acHeatNatureSt == 3) {
                currentType = 3;
            } else {
                currentType = 2;
            }
        } catch (Exception e2) {
            Logger.e(TAG, "getWindCtrlCardContent windLevel error=" + e2.getMessage());
        }
        ctrlCardContent.setType(currentType);
        if (currentWindLv != 0) {
            CtrlCardContent ctrlCardContent2 = this.mCtrlCardContent;
            ctrlCardContent2.setData(currentWindLv + "");
        } else {
            ctrlCardContent.setData(windLevel + "");
        }
        Log.d(TAG, "getWindCtrlCardContent() called with: ctrlCardContent = [" + GsonUtil.toJson(ctrlCardContent) + NavigationBarInflaterView.SIZE_MOD_END);
        return ctrlCardContent;
    }

    private void setAltColor(int value) {
        Log.d(TAG, "setAltColor() called with: value = [" + value + NavigationBarInflaterView.SIZE_MOD_END);
        CommandValue changeValue = new CommandValue("{\"color\":1}");
        changeValue.setColor(value);
        Logger.d(TAG, "setAltColor=" + GsonUtil.toJson(changeValue));
        SpeechClient.instance().getAgent().sendEvent(CarcontrolEvent.LIGHT_ATMOSPHERE_COLOR, GsonUtil.toJson(changeValue));
    }

    private void setAltBrightness(int value) {
        Log.d(TAG, "setAltBrightness() called with: value = [" + value + NavigationBarInflaterView.SIZE_MOD_END);
        CommandValue changeValue = new CommandValue("{\"number\":10}");
        if (value <= 10) {
            value = 10;
        }
        changeValue.setNumber(value);
        Logger.d(TAG, "setAltBrightness=" + GsonUtil.toJson(changeValue));
        SpeechClient.instance().getAgent().sendEvent(CarcontrolEvent.LIGHT_ATMOSPHERE_BRIGHTNESS_SET, GsonUtil.toJson(changeValue));
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardProvider
    public void passBack2SpeechValue(int currenttype, float unitValue) {
        if (currenttype == 2 || currenttype == 3 || currenttype == 4) {
            setHvacDriverWind(unitValue);
            return;
        }
        switch (currenttype) {
            case 8:
            case 9:
            case 10:
                setHvacDriverTemp(unitValue);
                return;
            case 11:
                setAltBrightness((int) unitValue);
                return;
            case 12:
                setAltColor((int) unitValue);
                return;
            case 13:
                setPassengerSeatHot((int) unitValue);
                return;
            case 14:
                setDriverSeatHot((int) unitValue);
                return;
            case 15:
                setSeatVent((int) unitValue);
                return;
            case 16:
                setScreenBrightness((int) unitValue);
                return;
            case 17:
                setIcmBrightness((int) unitValue);
                return;
            default:
                return;
        }
    }

    private void setSeatVent(int value) {
        sendSpeechData(CaracEvent.SEAT_VENTILATE_DRIVER_SET, value, "0");
    }

    private void setDriverSeatHot(int value) {
        sendSpeechData(CaracEvent.SEAT_HEAT_DRIVER_SET, value, "0");
    }

    private void setPassengerSeatHot(int value) {
        sendSpeechData(CaracEvent.SEAT_HEAT_PASSENGER_SET, value, "0");
    }

    private void setScreenBrightness(float progress) {
        int intProg = (int) progress;
        Log.d(TAG, "setScreenBrightness() called with: progress = [" + progress + NavigationBarInflaterView.SIZE_MOD_END);
        sendSpeechData(SystemEvent.BRIGHTNESS_SET_PERCENT, intProg, "0");
    }

    private void setIcmBrightness(float progress) {
        int intProg = (int) progress;
        Log.d(TAG, "setIcmBrightness() called with: progress = [" + progress + NavigationBarInflaterView.SIZE_MOD_END);
        sendSpeechData(SystemEvent.ICM_BRIGHTNESS_SET_PERCENT, intProg, "0");
    }

    private void setHvacDriverWind(float progress) {
        int intProg = (int) progress;
        Log.d(TAG, "setHvacDriverWind() called with: progress = [" + progress + NavigationBarInflaterView.SIZE_MOD_END);
        sendSpeechData(CaracEvent.WIND_SET, intProg, "0");
    }

    private void sendSpeechData(String event, int number, String d) {
        String data = "{\"number\":" + number + ",\"d\":" + d + "}";
        Log.d(TAG, "sendSpeechData data = [" + data + NavigationBarInflaterView.SIZE_MOD_END);
        SpeechClient.instance().getAgent().sendEvent(event, data);
    }

    /* loaded from: classes24.dex */
    private class AcCallback implements CarHvacManager.CarHvacEventCallback {
        private String lastData;

        private AcCallback() {
            this.lastData = "";
        }

        /* JADX WARN: Removed duplicated region for block: B:29:0x00cc  */
        /* JADX WARN: Removed duplicated region for block: B:7:0x0014  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onChangeEvent(android.car.hardware.CarPropertyValue r10) {
            /*
                Method dump skipped, instructions count: 338
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardProvider.AcCallback.onChangeEvent(android.car.hardware.CarPropertyValue):void");
        }

        public void onErrorEvent(int i, int i1) {
            Logger.e(CtrlCardProvider.TAG, "onErrorEvent: " + i + "  i1=" + i1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isHvacCard(int groupType) {
        return groupType == 10 || groupType == 9 || groupType == 8 || isSetTemp(groupType);
    }

    /* loaded from: classes24.dex */
    private class CtrlCallBack extends CarControlCallback {
        private CtrlCallBack() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacDriverTempChanged(float temp) {
        }

        protected void onDrvSeatHeatLevelChanged(final int level) {
            if (CtrlCardProvider.this.mPresenter != null) {
                ThreadUtils.runOnMainThreadDelay(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardProvider.CtrlCallBack.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CtrlCardProvider.this.mPresenter.setSeatHeatLevel(level);
                    }
                }, 500L);
            }
            Logger.d(CtrlCardProvider.TAG, "onDrvSeatHeatLevelChanged: level=" + level);
        }

        protected void onDrvSeatVentLevelChanged(final int level) {
            if (CtrlCardProvider.this.mPresenter != null) {
                ThreadUtils.runOnMainThreadDelay(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardProvider.CtrlCallBack.2
                    @Override // java.lang.Runnable
                    public void run() {
                        CtrlCardProvider.this.mPresenter.setSeatVentLevel(level);
                    }
                }, 500L);
            }
            Logger.d(CtrlCardProvider.TAG, "onDrvSeatVentLevelChanged: level=" + level);
        }

        protected void onCarServiceConnected() {
            Logger.d(CtrlCardProvider.TAG, "onCarServiceConnected: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacPowerChanged(boolean isPowerOn) {
            Logger.d(CtrlCardProvider.TAG, "onHvacPowerChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacAutoChanged(boolean isAuto) {
            Logger.d(CtrlCardProvider.TAG, "onHvacAutoChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacFanSpeedChanged(int level) {
            Logger.d(CtrlCardProvider.TAG, "onHvacFanSpeedChanged: level=" + level);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacDriverSyncChanged(boolean isSync) {
            Logger.d(CtrlCardProvider.TAG, "onHvacDriverSyncChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacPsnTempChanged(float temp) {
            Logger.d(CtrlCardProvider.TAG, "onHvacPsnTempChanged: temp=" + temp);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacPsnSyncChanged(boolean isSync) {
            Logger.d(CtrlCardProvider.TAG, "onHvacPsnSyncChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacFrontDefrostChanged(boolean enabled) {
            Logger.d(CtrlCardProvider.TAG, "onHvacFrontDefrostChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacBackDefrostChanged(boolean enabled) {
            Logger.d(CtrlCardProvider.TAG, "onHvacBackDefrostChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacInnerAqChanged(int aqValue) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onHvacExternalTempChanged(float temp) {
            Logger.d(CtrlCardProvider.TAG, "onHvacExternalTempChanged: temp=" + temp);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onChargeStatusChanged(ChargeStatus status) {
            Logger.d(CtrlCardProvider.TAG, "onChargeStatusChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onCentralLockChanged(boolean locked) {
            Logger.d(CtrlCardProvider.TAG, "onCentralLockChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onDrvSeatOccupiedChanged(boolean occupied) {
            Logger.d(CtrlCardProvider.TAG, "onDrvSeatOccupiedChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onPsnSeatOccupiedChanged(boolean occupied) {
            Logger.d(CtrlCardProvider.TAG, "onPsnSeatOccupiedChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onElecPercentChanged(int percent) {
            Logger.d(CtrlCardProvider.TAG, "onElecPercentChanged: ");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.xiaopeng.libcarcontrol.CarControlCallback
        public void onDriveDistanceChanged(int distance) {
            Logger.d(CtrlCardProvider.TAG, "onDriveDistanceChanged: ");
        }

        protected void onHvacWindModEconLourChanged(int mode) {
            Logger.d(CtrlCardProvider.TAG, "onHvacWindModEconLourChanged: mode" + mode);
        }
    }
}
