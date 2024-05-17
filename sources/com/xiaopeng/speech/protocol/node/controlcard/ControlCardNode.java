package com.xiaopeng.speech.protocol.node.controlcard;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.bean.CardValue;
import com.xiaopeng.speech.protocol.event.ControlCardEvent;
/* loaded from: classes23.dex */
public class ControlCardNode extends SpeechNode<ControlCardListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_AC_TEMP)
    public void showAcTempCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForAcTemp(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showAcTempCard(cardValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_AC_DRIVER_TEMP)
    public void showAcDriverTempCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForAcTemp(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showAcDriverTempCard(cardValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_AC_PASSENGER_TEMP)
    public void showAcPassengerTempCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForAcTemp(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showAcPassengerTempCard(cardValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_AC_WIND)
    public void showAcWindCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForAcWindLv(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showAcWindCard(cardValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_ATMOSPWHERE_BRIGHTNESS)
    public void showAtmosphereBrightnessCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForAtmosphereBrightness(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showAtmosphereBrightnessCard(cardValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_ATMOSPWHERE_COLOR)
    public void showAtmosphereBrightnessColorCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForAtmosphereColor(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showAtmosphereBrightnessColorCard(cardValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_AC_SEAT_HEATING_DRIVER)
    public void showAcSeatHeatingDriverCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForAcSeatHeating(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showAcSeatHeatingDriverCard(cardValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_AC_SEAT_HEATING_PASSENGER)
    public void showAcSeatHeatingPassengerCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForAcSeatHeating(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showAcSeatHeatingPassengerCard(cardValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_AC_SEAT_VENTILATE_DRIVER)
    public void showAcSeatVentilateDriverCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForAcSeatVentilate(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showAcSeatVentilateDriverCard(cardValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_SYSTEM_SCREEN_BRIGHTNESS)
    public void showSystemScreenBrightnessCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForScreenBrightness(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showSystemScreenBrightnessCard(cardValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ControlCardEvent.SHOW_CARD_SYSTEM_ICM_BRIGHTNESS)
    public void showSystemIcmBrightnessCard(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            CardValue cardValue = CardValue.fromJsonForIcmBrightness(data);
            for (Object obj : listenerList) {
                ((ControlCardListener) obj).showSystemIcmBrightnessCard(cardValue);
            }
        }
    }
}
