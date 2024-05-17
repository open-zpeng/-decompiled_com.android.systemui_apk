package com.xiaopeng.speech.protocol.node.carac;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.protocol.bean.SpeechParam;
import com.xiaopeng.speech.protocol.event.CaracEvent;
import com.xiaopeng.speech.protocol.node.carac.bean.ChangeValue;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class CaracNode extends SpeechNode<CaracListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.HVAC_OFF)
    public void onHvacOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onHvacOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.HVAC_ON)
    public void onHvacOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onHvacOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_ON)
    public void onAcOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onAcOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_OFF)
    public void onAcOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onAcOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AUTO_ON)
    public void onAutoOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onAutoOn();
            }
        }
    }

    protected void onAutoOffSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onAutoOffSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AUTO_OFF)
    public void onAutoOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onAutoOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.BLOW_FOOT_ON)
    public void onBlowFootOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onBlowFootOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.HEAD_FOOT_ON)
    public void onHeadFootOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onHeadFootOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.BLOW_HEAD_ON)
    public void onBlowHeadOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onBlowHeadOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.DEMIST_FRONT_OFF)
    public void onDemistFrontOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onDemistFrontOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.DEMIST_FRONT_ON)
    public void onDemistFrontOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onDemistFrontOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.DEMIST_REAR_OFF)
    public void onDemistRearOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onDemistRearOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.DEMIST_REAR_ON)
    public void onDemistRearOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onDemistRearOn();
            }
        }
    }

    protected void onDemistFootOnSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onDemistFootOnSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.DEMIST_FOOT_ON)
    public void onDemistFootOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onDemistFootOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.INNER_OFF)
    public void onInnerOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onInnerOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.INNER_ON)
    public void onInnerOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onInnerOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_DOWN)
    public void onWindDown(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindDown(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_UP)
    public void onWindUp(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindUp(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_SET)
    public void onWindSet(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindSet(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.REAR_HEAT_OFF)
    public void onRearHeatOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearHeatOff();
            }
        }
    }

    protected void onRearHeatOffSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearHeatOffSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.REAR_HEAT_ON)
    public void onRearHeatOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearHeatOn();
            }
        }
    }

    protected void onRearHeatOnSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearHeatOnSupport();
            }
        }
    }

    protected void onTempDownHalfSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDownHalfSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_DOWN)
    public void onTempDown(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDown(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_UP)
    public void onTempUp(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempUp(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_SET)
    public void onTempSet(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempSet(changeValue);
            }
        }
    }

    protected void onTempUpHalfSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempUpHalfSupport();
            }
        }
    }

    protected void onPurifierOpenSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onPurifierOpenSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.PURIFIER_OPEN)
    public void onPurifierOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onPurifierOpen();
            }
        }
    }

    protected void onPurifierCloseSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onPurifierCloseSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.PURIFIER_CLOSE)
    public void onPurifierClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onPurifierClose();
            }
        }
    }

    protected void onPurifierPm25(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onPurifierPm25();
            }
        }
    }

    protected void onTempDriverUpSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDriverUpSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_DRIVER_UP)
    public void onTempDriverUp(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDriverUp(changeValue);
            }
        }
    }

    protected void onTempDriverDownSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDriverDownSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_DRIVER_DOWN)
    public void onTempDriverDown(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDriverDown(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_DRIVER_SET)
    public void onTempDriverSet(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDriverSet(changeValue);
            }
        }
    }

    protected void onTempPassengerUpSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempPassengerUpSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_PASSENGER_UP)
    public void onTempPassengerUp(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempPassengerUp(changeValue);
            }
        }
    }

    protected void onTempPassengerDownSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempPassengerDownSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_PASSENGER_DOWN)
    public void onTempPassengerDown(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempPassengerDown(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_PASSENGER_SET)
    public void onTempPassengerSet(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempPassengerSet(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_DUAL_SYN)
    public void onTempDualSyn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDualSyn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_DUAL_OFF)
    public void onTempDualOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDualOff();
            }
        }
    }

    protected void onDataTempTTS(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onDataTempTTS();
            }
        }
    }

    protected void onDataWindTTS(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onDataWindTTS();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_MAX)
    public void onWindMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        SpeechParam param = SpeechParam.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindMax(param);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_MIN)
    public void onWindMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        SpeechParam param = SpeechParam.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindMin(param);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_MIN)
    public void onTempMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        SpeechParam param = SpeechParam.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempMin(param);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_MAX)
    public void onTempMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempMax();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_DRIVE_MIN)
    public void onTempDriveMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDriveMin();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_DRIVE_MAX)
    public void onTempDriveMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempDriveMax();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_PASSENGER_MIN)
    public void onTempPassengerMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempPassengerMin();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_PASSENGER_MAX)
    public void onTempPassengerMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onTempPassengerMax();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_HEATING_OPEN)
    public void onSeatHeatingOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatHeatingOpen();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_HEATING_CLOSE)
    public void onSeatHeatingClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatHeatingClose();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_PSN_HEAT_OPEN)
    public void onSeatPsnHeatingOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatPsnHeatingOpen();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_PSN_HEAT_CLOSE)
    public void onSeatPsnHeatingClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatPsnHeatingClose();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_HEATING_MAX)
    public void onSeatHeatingMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatHeatingMax();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_HEATING_MIN)
    public void onSeatHeatingMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatHeatingMin();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_HEATING_UP)
    public void onSeatHeatingUp(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatHeatingUp(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_HEATING_DOWN)
    public void onSeatHeatingDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatHeatingDown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_VENTILATE_ON)
    public void onSeatVentilateOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        SpeechParam param = SpeechParam.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatVentilateOn(param);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_VENTILATE_OFF)
    public void onSeatVentilateOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatVentilateOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_VENTILATE_MAX)
    public void onSeatVentilateMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatVentilateMax();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_VENTILATE_MIN)
    public void onSeatVentilateMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatVentilateMin();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_VENTILATE_DOWN)
    public void onSeatVentilateDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatVentilateDown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_VENTILATE_UP)
    public void onSeatVentilateUp(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatVentilateUp(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_VENTILATE_DRIVER_SET)
    public void onSeatVentilateDriverSet(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatVentilateDriverSet(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_HEAT_DRIVER_SET)
    public void onSeatHeatDriverSet(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatHeatDriverSet(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_HEAT_PASSENGER_SET)
    public void onSeatHeatPassengerSet(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatHeatPassengerSet(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_HEAT_PASSENGER_UP)
    public void onSeatHeatPassengerUp(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatHeatPassengerUp(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.SEAT_HEAT_PASSENGER_DOWN)
    public void onSeatHeatPassengerDown(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onSeatHeatPassengerDown(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.OPEN_FAST_FRIDGE)
    public void onOpenFastFridge(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onOpenFastFridge();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.OPEN_FRESH_AIR)
    public void onOpenFreshAir(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onOpenFreshAir();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.EXIT_FAST_FRIDGE)
    public void onExitFastFridge(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onExitFastFridge();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.EXIT_FRESH_AIR)
    public void onExitFreshAir(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onExitFreshAir();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_UNIDIRECTION_SET)
    public void onWindUnidirectionSet(String event, String data) {
        int pilotValue = 0;
        int directionValue = 7;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String pilot = jsonObject.optString("pilot");
            String direction = jsonObject.optString(VuiConstants.EVENT_VALUE_DIRECTION);
            pilotValue = Integer.parseInt(pilot);
            directionValue = Integer.parseInt(direction);
        } catch (Throwable th) {
            LogUtils.e("CaracNode", "onWindUnidirectionSet string data error, data = " + data);
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindUnidirectionSet(pilotValue, directionValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_UNIDIRECTION)
    public void onWindUnindirection(String event, String data) {
        int pilotValue = 0;
        int directionValue = 7;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String pilot = jsonObject.optString("pilot");
            String direction = jsonObject.optString(VuiConstants.EVENT_VALUE_DIRECTION);
            pilotValue = Integer.parseInt(pilot);
            directionValue = Integer.parseInt(direction);
        } catch (Throwable th) {
            LogUtils.e("CaracNode", "onWindUnindirection string data error, data = " + data);
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindUnidirection(pilotValue, directionValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_EVAD)
    public void onWindEvad(String event, String data) {
        int pilotValue = 0;
        int directionValue = 7;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String pilot = jsonObject.optString("pilot");
            String direction = jsonObject.optString(VuiConstants.EVENT_VALUE_DIRECTION);
            pilotValue = Integer.parseInt(pilot);
            directionValue = Integer.parseInt(direction);
        } catch (Throwable th) {
            LogUtils.e("CaracNode", "onWindEvad string data error, data = " + data);
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindEvad(pilotValue, directionValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_FRONT)
    public void onWindFront(String event, String data) {
        int pilotValue = 0;
        int directionValue = 7;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String pilot = jsonObject.optString("pilot");
            String direction = jsonObject.optString(VuiConstants.EVENT_VALUE_DIRECTION);
            pilotValue = Integer.parseInt(pilot);
            directionValue = Integer.parseInt(direction);
        } catch (Throwable th) {
            LogUtils.e("CaracNode", "onWindFront string data error, data = " + data);
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindFront(pilotValue, directionValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_FREE)
    public void onWindFree(String event, String data) {
        int pilotValue = 0;
        int directionValue = 7;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String pilot = jsonObject.optString("pilot");
            String direction = jsonObject.optString(VuiConstants.EVENT_VALUE_DIRECTION);
            pilotValue = Integer.parseInt(pilot);
            directionValue = Integer.parseInt(direction);
        } catch (Throwable th) {
            LogUtils.e("CaracNode", "onWindFree string data error, data = " + data);
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindFree(pilotValue, directionValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_AUTO_SWEEP_ON)
    public void onWindAutoSweepOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindAutoSweepOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WIND_AUTO_SWEEP_OFF)
    public void onWindAutoSweepOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindAutoSweepOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AQS_ON)
    public void onAqsOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onAqsOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AQS_OFF)
    public void onAqsOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onAqsOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.MODES_ECO_ON)
    public void onModesEcoOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onModesEcoOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.MODES_ECO_OFF)
    public void onModesEcoOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onModesEcoOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.HEATING_ON)
    public void onHeatingOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onHeatingOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.HEATING_OFF)
    public void onHeatingOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onHeatingOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.NATURE_ON)
    public void onNatureOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onNatureOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.NATURE_OFF)
    public void onNatureOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onNatureOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.WINDOW_ON)
    public void onWindowOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindowOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_PSN_SYN_ON)
    public void onPsnTempSynOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onPsnTempSynOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.TEMP_PSN_SYN_OFF)
    public void onPsnTempSynOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onPsnTempSynOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.OPEN_AC_PANEL)
    public void onHvacPanelOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onAcPanelOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.OPEN_AC_INTELLIGENT_PSN_ON)
    public void openIntelligentPsn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).openIntelligentPsn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.CLOSE_AC_INTELLIGENT_PSN_OFF)
    public void closeIntelligentPsn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).closeIntelligentPsn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.MIRROR_ON)
    public void onMirrorOn(String event, String data) {
        int pilotValue = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String pilot = jsonObject.optString("pilot");
            pilotValue = Integer.parseInt(pilot);
        } catch (Throwable th) {
            LogUtils.e("CaracNode", "onMirrorOn string data error, data = " + data);
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onMirrorOn(pilotValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.MIRROR_OFF)
    public void onMirrorOff(String event, String data) {
        int pilotValue = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String pilot = jsonObject.optString("pilot");
            pilotValue = Integer.parseInt(pilot);
        } catch (Throwable th) {
            LogUtils.e("CaracNode", "onMirrorOff string data error, data = " + data);
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onMirrorOff(pilotValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.OUTLET_ON)
    public void onWindOutletOn(String event, String data) {
        int pilotValue = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String pilot = jsonObject.optString("pilot");
            pilotValue = Integer.parseInt(pilot);
        } catch (Throwable th) {
            LogUtils.e("CaracNode", "nWindOutletOn string data error, data = " + data);
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindOutletOn(pilotValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.OUTLET_OFF)
    public void onWindOutletOff(String event, String data) {
        int pilotValue = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String pilot = jsonObject.optString("pilot");
            pilotValue = Integer.parseInt(pilot);
        } catch (Throwable th) {
            LogUtils.e("CaracNode", "onWindOutletOff string data error, data = " + data);
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onWindOutletOff(pilotValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_LEFT_REAR_SEAT_HEAT_OPEN)
    public void onLeftRearSeatHeatingOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearSeatHeatingOpenClose(0, 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_RIGHT_REAR_SEAT_HEAT_OPEN)
    public void onRightRearSeatHeatingOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearSeatHeatingOpenClose(0, 1);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_LEFT_REAR_SEAT_HEAT_CLOSE)
    public void onLeftRearSeatHeatingClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearSeatHeatingOpenClose(1, 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_RIGHT_REAR_SEAT_HEAT_CLOSE)
    public void onRightRearSeatHeatingClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearSeatHeatingOpenClose(1, 1);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_LEFT_REAR_SEAT_HEAT_SET)
    public void onLeftRearSeatHeatSet(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearSeatHeatSet(0, changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_RIGHT_REAR_SEAT_HEAT_SET)
    public void onRightRearSeatHeatSet(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearSeatHeatSet(1, changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_LEFT_REAR_SEAT_HEAT_UP)
    public void onLeftRearSeatHeatUp(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearSeatHeatUp(0, changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_RIGHT_REAR_SEAT_HEAT_UP)
    public void onRightRearSeatHeatUp(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearSeatHeatUp(1, changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_LEFT_REAR_SEAT_HEAT_DOWN)
    public void onLeftRearSeatHeatDown(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearSeatHeatDown(0, changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.AC_RIGHT_REAR_SEAT_HEAT_DOWN)
    public void onRightRearSeatHeatDown(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onRearSeatHeatDown(1, changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.HEAD_WINDOW_ON)
    public void onHeadWindowOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onHeadWindowOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.HEAD_WINDOW_FOOT_ON)
    public void onHeadWindowFootOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onHeadWindowFootOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CaracEvent.HEAD_WINDOW_OFF)
    public void onHeadWindowOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CaracListener) obj).onHeadWindowOff();
            }
        }
    }
}
