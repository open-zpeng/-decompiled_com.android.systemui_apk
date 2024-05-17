package com.xiaopeng.speech.protocol.node.charge;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.event.ChargeEvent;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class ChargeNode extends SpeechNode<ChargeListener> {
    public static final String CHARGE_CLTC_MILEAGE = "CLTC";
    public static final String CHARGE_NEDC_MILEAGE = "NEDC";
    public static final String CHARGE_WLTP_MILEAGE = "WLTP";

    protected void onPortOpenSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onPortOpenSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.PORT_OPEN)
    public void onPortOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onPortOpen();
            }
        }
    }

    protected void onStartSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onStartSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.START)
    public void onStart(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onStart();
            }
        }
    }

    protected void onRestartSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onRestartSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.RESTART)
    public void onRestart(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onRestart();
            }
        }
    }

    protected void onStopSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onStopSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.STOP)
    public void onStop(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onStop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.UI_OPEN)
    public void onUiOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onUiOpen();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.UI_CLOSE)
    public void onUiClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onUiClose();
            }
        }
    }

    protected void onModePercentSupport(String event, String data) {
        int target = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            target = Integer.parseInt(jsonObject.optString("target"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onModePercentSupport(target);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.MODE_PERCENT)
    public void onModePercent(String event, String data) {
        int target = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            target = Integer.parseInt(jsonObject.optString("target"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onModePercent(target);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.MODE_FULL)
    public void onModeFull(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onModeFull();
            }
        }
    }

    protected void onModeSmartOnSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onModeSmartOnSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.MODE_SMART_ON)
    public void onModeSmartOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onModeSmartOn();
            }
        }
    }

    protected void onModeSmartCloseSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onModeSmartCloseSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.MODE_SMART_CLOSE)
    public void onModeSmartClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onModeSmartClose();
            }
        }
    }

    protected void onModeSmartOffSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onModeSmartOffSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.MODE_SMART_OFF)
    public void onModeSmartOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onModeSmartOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.CHARGE_CHANGE_WLTP_MILEAGE)
    public void onChangeWltpMileageMode(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onChangeMileageMode(CHARGE_WLTP_MILEAGE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.CHARGE_CHANGE_NEDC_MILEAGE)
    public void onChangeNedcMileageMode(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onChangeMileageMode(CHARGE_NEDC_MILEAGE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.CHARGE_TRUNK_POWER_POWER_ON)
    public void onChargeTrunkPowerOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onChargeTrunkPower(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.CHARGE_TRUNK_POWER_POWER_OFF)
    public void onChargeTrunkPowerClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onChargeTrunkPower(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.DISCHARGE_LIMIT_VALUE_SET)
    public void setDischargeLimit(String event, String data) {
        int target = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            target = Integer.parseInt(jsonObject.optString("target"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).setDischargeLimit(target);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = ChargeEvent.CHARGE_CHANGE_CLTC_MILEAGE)
    public void onChangeCltcMileageMode(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((ChargeListener) obj).onChangeMileageMode(CHARGE_CLTC_MILEAGE);
            }
        }
    }
}
