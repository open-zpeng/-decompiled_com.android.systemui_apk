package com.xiaopeng.systemui.infoflow.speech.core.speech.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import com.xiaopeng.speech.protocol.SpeechModel;
import com.xiaopeng.speech.protocol.bean.CardValue;
import com.xiaopeng.speech.protocol.node.carac.AbsCaracListener;
import com.xiaopeng.speech.protocol.node.carac.CaracNode;
import com.xiaopeng.speech.protocol.node.carac.bean.ChangeValue;
import com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener;
import com.xiaopeng.speech.protocol.node.controlcard.ControlCardNode;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.speech.core.speech.SpeechManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.CarListenerManager;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class CarModel extends SpeechModel {
    private static final int DEF_ICM_BRIGHTNESS = 60;
    private static final int DEF_SCREEN_BRIGHTNESS = 153;
    private static final String KEY_ICM_BRIGHTNESS = "screen_brightness_2";
    private static final String KEY_SCREEN_BRIGHTNESS = "screen_brightness";
    private static final String TAG = CarModel.class.getSimpleName();
    protected SpeechManager mSpeechManager;
    private Handler mHandler = new Handler();
    private Context mContext = ContextUtils.getContext();
    private BrightnessObserver mBrightnessObserver = new BrightnessObserver(this.mHandler);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public class BrightnessObserver extends ContentObserver {
        private final Uri ICM_BRIGHTNESS;
        private final Uri SCREEN_BRIGHTNESS;

        public BrightnessObserver(Handler handler) {
            super(handler);
            this.ICM_BRIGHTNESS = Settings.System.getUriFor("screen_brightness_2");
            this.SCREEN_BRIGHTNESS = Settings.System.getUriFor("screen_brightness");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (selfChange) {
                return;
            }
            CarModel.this.onBrightnessChanged(selfChange, uri);
        }

        public void startObserving() {
            Log.d(CarModel.TAG, "startObserving() called");
            ContentResolver resolver = CarModel.this.mContext.getContentResolver();
            resolver.unregisterContentObserver(this);
            resolver.registerContentObserver(this.ICM_BRIGHTNESS, false, this, -1);
            resolver.registerContentObserver(this.SCREEN_BRIGHTNESS, false, this, -1);
        }

        public void stopObserving() {
            Log.d(CarModel.TAG, "stopObserving() called");
            ContentResolver resolver = CarModel.this.mContext.getContentResolver();
            resolver.unregisterContentObserver(this);
        }
    }

    public CarModel(SpeechManager speechManager) {
        this.mSpeechManager = speechManager;
        subscribe(CaracNode.class, new AbsCaracListener() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.CarModel.1
            @Override // com.xiaopeng.speech.protocol.node.carac.AbsCaracListener, com.xiaopeng.speech.protocol.node.carac.CaracListener
            public void onHvacOn() {
                super.onHvacOn();
            }

            @Override // com.xiaopeng.speech.protocol.node.carac.AbsCaracListener, com.xiaopeng.speech.protocol.node.carac.CaracListener
            public void onTempDriverUp(ChangeValue changeValue) {
                CarModel.this.getCarListenerManager().onTempDriverUp(changeValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.carac.AbsCaracListener, com.xiaopeng.speech.protocol.node.carac.CaracListener
            public void onTempDriverDown(ChangeValue changeValue) {
                CarModel.this.getCarListenerManager().onTempDriverDown(changeValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.carac.AbsCaracListener, com.xiaopeng.speech.protocol.node.carac.CaracListener
            public void onTempDriverSet(ChangeValue changeValue) {
            }
        });
        subscribe(ControlCardNode.class, new AbsControlCardListener() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.CarModel.2
            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showAcTempCard(CardValue cardValue) {
                String str = CarModel.TAG;
                Logger.d(str, "showCtrlCard cardValue:" + cardValue.toString());
                CarModel.this.getCarListenerManager().showCtrlCard(5, cardValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showAcDriverTempCard(CardValue cardValue) {
                Logger.d(CarModel.TAG, "showAcDriverTempCard");
                CarModel.this.getCarListenerManager().showCtrlCard(6, cardValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showAcPassengerTempCard(CardValue cardValue) {
                Logger.d(CarModel.TAG, "showAcPassengerTempCard");
                CarModel.this.getCarListenerManager().showCtrlCard(7, cardValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showAcWindCard(CardValue cardValue) {
                Logger.d(CarModel.TAG, "showAcWindCard");
                CarModel.this.getCarListenerManager().showCtrlCard(1, cardValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showAtmosphereBrightnessCard(CardValue cardValue) {
                String str = CarModel.TAG;
                Logger.d(str, "showAtmosphereBrightnessCard cardValue:" + cardValue);
                CarModel.this.getCarListenerManager().showCtrlCard(11, cardValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showAtmosphereBrightnessColorCard(CardValue cardValue) {
                Logger.d(CarModel.TAG, "showAtmosphereBrightnessColorCard");
                CarModel.this.getCarListenerManager().showCtrlCard(12, cardValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showAcSeatHeatingDriverCard(CardValue cardValue) {
                CarModel.this.getCarListenerManager().showCtrlCard(14, cardValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showAcSeatHeatingPassengerCard(CardValue cardValue) {
                CarModel.this.getCarListenerManager().showCtrlCard(13, cardValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showAcSeatVentilateDriverCard(CardValue cardValue) {
                CarModel.this.getCarListenerManager().showCtrlCard(15, cardValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showSystemScreenBrightnessCard(CardValue cardValue) {
                CarModel.this.getCarListenerManager().showCtrlCard(16, cardValue);
            }

            @Override // com.xiaopeng.speech.protocol.node.controlcard.AbsControlCardListener, com.xiaopeng.speech.protocol.node.controlcard.ControlCardListener
            public void showSystemIcmBrightnessCard(CardValue cardValue) {
                CarModel.this.getCarListenerManager().showCtrlCard(17, cardValue);
            }
        });
        BrightnessObserver brightnessObserver = this.mBrightnessObserver;
        if (brightnessObserver != null) {
            brightnessObserver.startObserving();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBrightnessChanged(boolean selfChange, Uri uri) {
        BrightnessObserver brightnessObserver = this.mBrightnessObserver;
        if (brightnessObserver != null && uri != null) {
            if (!brightnessObserver.ICM_BRIGHTNESS.equals(uri)) {
                if (this.mBrightnessObserver.SCREEN_BRIGHTNESS.equals(uri)) {
                    int value = getIntForUser("screen_brightness", 60);
                    String str = TAG;
                    Logger.d(str, "screenBrightness = " + value);
                    int value2 = ((int) ((((float) value) / 255.0f) * 100.0f)) + 1;
                    if (value2 > 100) {
                        value2 = 100;
                    }
                    getCarListenerManager().onScreenBrightnessChanged(value2);
                    return;
                }
                return;
            }
            int value3 = getIntForUser("screen_brightness_2", 60);
            String str2 = TAG;
            Logger.d(str2, "icmBrightness = " + value3);
            getCarListenerManager().onIcmBrightnessChanged(value3);
        }
    }

    private int getIntForUser(String key, int defaultValue) {
        return Settings.System.getIntForUser(this.mContext.getContentResolver(), key, defaultValue, -2);
    }

    public CarListenerManager getCarListenerManager() {
        return this.mSpeechManager.getCarListenerManager();
    }
}
