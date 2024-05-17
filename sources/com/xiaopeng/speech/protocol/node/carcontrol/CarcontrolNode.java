package com.xiaopeng.speech.protocol.node.carcontrol;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.bean.ChangeValue;
import com.xiaopeng.speech.protocol.bean.SpeechParam;
import com.xiaopeng.speech.protocol.bean.base.CommandValue;
import com.xiaopeng.speech.protocol.event.CarcontrolEvent;
import com.xiaopeng.speech.protocol.node.carcontrol.bean.ControlReason;
import com.xiaopeng.speech.protocol.node.carcontrol.bean.SeatValue;
import com.xiaopeng.speech.protocol.node.carcontrol.bean.UserBookValue;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class CarcontrolNode extends SpeechNode<CarcontrolListener> {
    private static final String KEY_PERCENT = "percent";

    private int getPercent(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has(KEY_PERCENT)) {
                int percent = jsonObject.optInt(KEY_PERCENT);
                return percent;
            }
            return 100;
        } catch (Exception e) {
            e.printStackTrace();
            return 100;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_HOME_OFF)
    public void onLightHomeOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightHomeOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_HOME_ON)
    public void onLightHomeOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightHomeOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_LOW_OFF)
    public void onLightLowOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        ControlReason controlReason = ControlReason.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightLowOff(controlReason);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_LOW_ON)
    public void onLightLowOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightLowOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_POSITION_ON)
    public void onLightPositionOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightPositionOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_POSITION_OFF)
    public void onLightPositionOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightPositionOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_AUTO_ON)
    public void onLightAutoOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightAutoOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_AUTO_OFF)
    public void onLightAutoOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightAutoOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MIST_LIGHT_OFF)
    public void onMistLightOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onMistLightOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MIST_LIGHT_ON)
    public void onMistLightOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onMistLightOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MIRROR_REAR_CLOSE)
    public void onMirrorRearClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onMirrorRearClose();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MIRROR_REAR_ON)
    public void onMirrorRearOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onMirrorRearOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.TRUNK_OPEN)
    public void onTrunkOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onTrunkOpen();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_DRIVER_CLOSE)
    public void onWindowDriverClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(0, 1, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_DRIVER_OPEN)
    public void onWindowDriverOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(0, 0, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_PASSENGER_CLOSE)
    public void onWindowPassengerClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(1, 1, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_PASSENGER_OPEN)
    public void onWindowPassengerOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(1, 0, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOWS_CLOSE)
    public void onWindowsClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(6, 1, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOWS_OPEN)
    public void onWindowsOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(6, 0, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.TRUNK_CLOSE)
    public void onTrunkClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onTrunkClose();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOWS_VENTILATE_ON)
    public void onWindowsVentilateOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsVentilateOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOWS_VENTILATE_OFF)
    public void onWindowsVentilateOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsVentilateOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MODES_DRIVING_SPORT)
    public void onModesDrivingSport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onModesDrivingSport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MODES_DRIVING_CONSERVATION)
    public void onModesDrivingConservation(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onModesDrivingConservation();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MODES_DRIVING_NORMAL)
    public void onModesDrivingNormal(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onModesDrivingNormal();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MODES_STEERING_SOFT)
    public void onModesSteeringSoft(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onModesSteeringSoft();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MODES_STEERING_NORMAL)
    public void onModesSteeringNormal(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onModesSteeringNormal();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MODES_STEERING_SPORT)
    public void onModesSteeringSport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onModesSteeringSport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.ENERGY_RECYCLE_HIGH)
    public void onEnergyRecycleHigh(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onEnergyRecycleHigh();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.ENERGY_RECYCLE_LOW)
    public void onEnergyRecycleLow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onEnergyRecycleLow();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.ENERGY_RECYCLE_MEDIA)
    public void onEnergyRecycleMedia(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onEnergyRecycleMedia();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.ENERGY_RECYCLE_UP)
    public void onEnergyRecycleUp(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onEnergyRecycleUp();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.ENERGY_RECYCLE_DOWN)
    public void onEnergyRecycleDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onEnergyRecycleDown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_RIGHT_REAR_OPEN)
    public void onWindowRightRearOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(3, 0, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_RIGHT_REAR_CLOSE)
    public void onWindowRightRearClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(3, 1, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_LEFT_REAR_OPEN)
    public void onWindowLeftRearOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(2, 0, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_LEFT_REAR_CLOSE)
    public void onWindowLeftRearClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(2, 1, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_FRONT_OPEN)
    public void onWindowFrontOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(7, 0, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_FRONT_CLOSE)
    public void onWindowFrontClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(7, 1, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_REAR_OPEN)
    public void onWindowRearOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(8, 0, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_REAR_CLOSE)
    public void onWindowRearClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(8, 1, getPercent(data));
            }
        }
    }

    protected void onModesSportSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onModesSportSupport();
            }
        }
    }

    protected void onModesConservationSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onModesConservationSupport();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_MOVE_UP)
    public void onSeatMoveUp(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatMoveUp();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_MOVE_DOWN)
    public void onSeatMoveDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatMoveDown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_MOVE_HIGHEST)
    public void onSeatMoveHighest(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatMoveHighest();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_MOVE_LOWEST)
    public void onSeatMoveLowest(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatMoveLowest();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_MOVE_BACK)
    public void onSeatMoveBack(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        ChangeValue changeValue = ChangeValue.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatMoveBack(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_MOVE_FORWARD)
    public void onSeatMoveForward(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        ChangeValue changeValue = ChangeValue.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatMoveForward(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_MOVE_REAR)
    public void onSeatMoveRear(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        SpeechParam speechParam = SpeechParam.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatMoveRear(speechParam);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_MOVE_FOREMOST)
    public void onSeatMoveForemost(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatMoveForemost();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_BACKREST_BACK)
    public void onSeatBackrestBack(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        SpeechParam speechParam = SpeechParam.fromJson(data);
        ChangeValue changeValue = ChangeValue.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatBackrestBack(changeValue, speechParam);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_BACKREST_FORWARD)
    public void onSeatBackrestForward(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        ChangeValue changeValue = ChangeValue.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatBackrestForward(changeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_BACKREST_REAR)
    public void onSeatBackrestRear(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        SpeechParam speechParam = SpeechParam.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatBackrestRear(speechParam);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_BACKREST_FOREMOST)
    public void onSeatBackrestForemost(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatBackrestForemost();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_ADJUST)
    public void onSeatAdjust(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        SeatValue value = SeatValue.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatAdjust(value);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_LIGHT_RESUME)
    public void onControlLightResume(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlLightResume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_SEAT_RESUME)
    public void onControlSeatResume(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlSeatResume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LOW_VOLUME_ON)
    public void onLowVolumeOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLowVolumeOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LOW_VOLUME_OFF)
    public void onLowVolumeOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLowVolumeOff();
            }
        }
    }

    protected void onCheckUserBook(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        UserBookValue userBookValue = UserBookValue.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onCheckUserBook(userBookValue);
            }
        }
    }

    protected void onOpenUserBook(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onOpenUserBook();
            }
        }
    }

    protected void onCloseUserBook(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onCloseUserBook();
            }
        }
    }

    protected void onLightTopAutoOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightTopAutoOn();
            }
        }
    }

    protected void onLightTopAutoOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightTopAutoOff();
            }
        }
    }

    protected void onLightTopOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightTopOn();
            }
        }
    }

    protected void onLightTopOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightTopOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_ATMOSPHERE_ON)
    public void onLightAtmosphereOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightAtmosphereOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_ATMOSPHERE_OFF)
    public void onLightAtmosphereOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightAtmosphereOff(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.MIRROR_REAR_SET)
    public void onMirrorRearSet(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onMirrorRearSet(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WIPER_SPEED_UP)
    public void onWiperSpeedUp(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWiperSpeedUp();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WIPER_SPEED_DOWN)
    public void onWiperSpeedDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWiperSpeedDown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WIPER_SLOW)
    public void onWiperSlow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWiperSlow();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WIPER_HIGH)
    public void onWiperHigh(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWiperHigh();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WIPER_MEDIUM)
    public void onWiperMedium(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWiperMedium();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WIPER_SUPERHIGH)
    public void onWiperSuperhigh(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWiperSuperhigh();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.TRUNK_UNLOCK)
    public void onTrunkUnlock(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onTrunkUnlock();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_LEFT_CLOSE)
    public void onWindowsLeftClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(4, 1, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_LEFT_OPEN)
    public void onWindowsLeftOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(4, 0, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_RIGHT_CLOSE)
    public void onWindowsRightClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(5, 1, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.WINDOW_RIGHT_OPEN)
    public void onWindowsRightOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onWindowsControl(5, 0, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LEG_UP)
    public void onLegUp(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLegUp(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LEG_DOWN)
    public void onLegDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLegDown(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LEG_HIGHEST)
    public void onLegHighest(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLegHighest(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LEG_LOWEST)
    public void onLegLowest(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLegLowest(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.SEAT_RESTORE)
    public void onSeatRestore(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onSeatRestore();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.DIRECT_PORT_ON)
    public void onRightChargePortOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onChargePortControl(1, 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.DIRECT_PORT_OFF)
    public void onRightChargePortClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onChargePortControl(1, 1);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.ALTERNATING_PORT_OFF)
    public void onLeftChargePortClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onChargePortControl(0, 1);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.ALTERNATING_PORT_ON)
    public void onLeftChargePortOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onChargePortControl(0, 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.TIRE_PRESSURE_SHOW)
    public void onTirePressureShow(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onTirePressureShow();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_ATMOSPHERE_COLOR)
    public void onLightAtmosphereColor(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightAtmosphereColor(new CommandValue(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_ATMOSPHERE_BRIGHTNESS_SET)
    public void onLightAtmosphereBrightnessSet(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightAtmosphereBrightnessSet(new CommandValue(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_ATMOSPHERE_BRIGHTNESS_UP)
    public void onLightAtmosphereBrightnessUp() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightAtmosphereBrightnessUp();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_ATMOSPHERE_BRIGHTNESS_DOWN)
    public void onLightAtmosphereBrightnessDown() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightAtmosphereBrightnessDown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_ATMOSPHERE_BRIGHTNESS_MAX)
    public void onLightAtmosphereBrightnessMax() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightAtmosphereBrightnessMax();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.LIGHT_ATMOSPHERE_BRIGHTNESS_MIN)
    public void onLightAtmosphereBrightnessMin() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onLightAtmosphereBrightnessMin();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.PSN_SEAT_MOVE_UP)
    public void onPsnSeatMoveUp() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onPsnSeatMoveUp();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.PSN_SEAT_MOVE_DOWN)
    public void onPsnSeatMoveDown() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onPsnSeatMoveDown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.PSN_SEAT_BACKREST_BACK)
    public void onPsnSeatBackrestBack() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onPsnSeatBackrestBack();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.PSN_SEAT_BACKREST_FORWARD)
    public void onPsnSeatBackrestForward() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onPsnSeatBackrestForward();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.PSN_SEAT_MOVE_BACK)
    public void onPsnSeatMoveBack() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onPsnSeatMoveBack();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.PSN_SEAT_MOVE_FORWARD)
    public void onPsnSeatMoveForward() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onPsnSeatMoveForward();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_XPEDAL_ON)
    public void onControlXpedalOn() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlXpedalOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_XPEDAL_OFF)
    public void onControlXpedalOff() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlXpedalOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_SCISSOR_LEFT_DOOR_ON)
    public void onControlScissorLeftDoorOn() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlScissorLeftDoorOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_SCISSOR_RIGHT_DOOR_ON)
    public void onControlScissorRightDoorOn() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlScissorRightDoorOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_SCISSOR_LEFT_DOOR_OFF)
    public void onControlScissorLeftDoorOff() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlScissorLeftDoorOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_SCISSOR_RIGHT_DOOR_OFF)
    public void onControlScissorRightDoorOff() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlScissorRightDoorOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_SCISSOR_LEFT_DOOR_PAUSE)
    public void onControlScissorLeftDoorPause() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlScissorLeftDoorPause();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_SCISSOR_RIGHT_DOOR_PAUSE)
    public void onControlScissorRightDoorPause() {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlScissorRightDoorPause();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_ELECTRIC_CURTAIN_OPEN)
    public void onControlElectricCurtainOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlSunShade(0, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_ELECTRIC_CURTAIN_CLOSE)
    public void onControlElectricCurtainClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlSunShade(1, getPercent(data));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_COMFORTABLE_DRIVING_MODE_OPEN)
    public void onControlComfortableDrivingModeOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlComfortableDrivingModeOpen();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_COMFORTABLE_DRIVING_MODE_CLOSE)
    public void onControlComfortableDrivingModeClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).onControlComfortableDrivingModeClose();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_LIGHT_LANGUAGE_ON)
    public void onControlLightLanguageOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).setLluSw(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_LIGHT_LANGUAGE_OFF)
    public void onControlLightLanguageOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).setLluSw(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CarcontrolEvent.CONTROL_CAPSULE_UNIVERSAL_SET)
    public void setCapsuleUniversal(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CarcontrolListener) obj).setCapsuleUniversal(getModeFromJson(data));
            }
        }
    }

    private String getModeFromJson(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        try {
            JSONObject modeJson = new JSONObject(data);
            return modeJson.has("mode") ? modeJson.optString("mode") : "";
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
