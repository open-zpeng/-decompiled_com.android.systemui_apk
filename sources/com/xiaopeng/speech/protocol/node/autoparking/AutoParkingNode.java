package com.xiaopeng.speech.protocol.node.autoparking;

import android.os.Binder;
import android.os.IBinder;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.jarvisproto.DMEnd;
import com.xiaopeng.speech.protocol.SpeechUtils;
import com.xiaopeng.speech.protocol.event.AutoParkingEvent;
import com.xiaopeng.speech.protocol.node.autoparking.bean.ParkingPositionBean;
import com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener;
import com.xiaopeng.speech.protocol.node.dialog.DialogNode;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.utils.CarTypeUtils;
import com.xiaopeng.speech.proxy.HotwordEngineProxy;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class AutoParkingNode extends SpeechNode<AutoParkingListener> {
    private static final String COMMAND_SPLIT = "#";
    private static final String FIND_SKILL_NAME = "沿途找车位";
    private static final String FIND_TASK_PARK = "沿途泊车";
    private static final String INTENT_FIND_POSITION_PARK = "找到车位";
    private static final long SET_EXIT_PARKING_TIMEOUT = 150;
    private static final String SKILL_NAME = "离线自动泊车";
    private static final String TAG = "AutoParkingNode";
    private static final String TASK_PARK = "自动泊车";
    private static final int TRIGGER_ID_FINDING = 10003;
    private static final int TRIGGER_ID_PARKING = 10002;
    private static int curTriggerID = -1;
    private IBinder mWakeupBinder = new Binder();
    private volatile boolean isShouldExitParking = false;

    public AutoParkingNode() {
        SpeechClient.instance().getNodeManager().subscribe(DialogNode.class, new AbsDialogListener() { // from class: com.xiaopeng.speech.protocol.node.autoparking.AutoParkingNode.1
            @Override // com.xiaopeng.speech.protocol.node.dialog.AbsDialogListener, com.xiaopeng.speech.protocol.node.dialog.DialogListener
            public void onDialogEnd(DialogEndReason endReason) {
                super.onDialogEnd(endReason);
                if (DMEnd.REASON_VOICE.equals(endReason.reason) || DMEnd.REASON_WHEEL.equals(endReason.reason)) {
                    AutoParkingNode.this.onExit("", "");
                }
            }
        });
    }

    public void findParkingPosition(String tts) {
        findParkingPosition(tts, false);
    }

    public void findParkingPosition(String tts, boolean isFindParking) {
        String skill;
        String task;
        String slots;
        if (CarTypeUtils.isE38ZH() || CarTypeUtils.isE28AZH()) {
            if (isFindParking) {
                triggerDialog(TRIGGER_ID_FINDING, tts, null);
                return;
            } else {
                triggerDialog(10002, tts, null);
                return;
            }
        }
        String slots2 = "";
        if (isFindParking) {
            skill = FIND_SKILL_NAME;
            task = FIND_TASK_PARK;
            try {
                slots2 = new JSONObject().put("tts", tts).put("isLocalSkill", true).put("command", "command://autoparking.park.start#native://autoparking.park.carport.count#command://autoparking.find.parking.space.continue#command://autoparking.find.parking.space.exit").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            slots = slots2;
        } else {
            skill = SKILL_NAME;
            task = TASK_PARK;
            try {
                String slots3 = new JSONObject().put("tts", tts).put("isLocalSkill", true).put("command", "command://autoparking.park.start#native://autoparking.park.carport.count#command://autoparking.exit").toString();
                slots = slots3;
            } catch (JSONException e2) {
                e2.printStackTrace();
                slots = "";
            }
        }
        stopCarSpeech();
        SpeechClient.instance().getAgent().triggerIntentWithBinder(this.mWakeupBinder, skill, task, INTENT_FIND_POSITION_PARK, slots);
    }

    public void triggerDialog(int triggerID, String tts, String data) {
        JSONObject jsonObject;
        LogUtils.i(TAG, "trigger dialog, id: %s, tts: %s, data: %s", Integer.valueOf(triggerID), tts, data);
        if (curTriggerID != -1) {
            LogUtils.i(TAG, "already in trigger, already disable wakeup.");
        } else {
            disableWakeup();
        }
        curTriggerID = triggerID;
        try {
            if (!SpeechUtils.isJson(data)) {
                jsonObject = new JSONObject();
            } else {
                jsonObject = new JSONObject(data);
            }
            jsonObject.putOpt("tts", tts);
            SpeechClient.instance().getAgent().triggerDialog(triggerID, new int[]{0}, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leaveTrigger() {
        LogUtils.i(TAG, "leave trigger, curTriggerID: %s", Integer.valueOf(curTriggerID));
        if (curTriggerID == -1) {
            LogUtils.i(TAG, "not in trigger, can not leave trigger.");
            return;
        }
        try {
            SpeechClient.instance().getAgent().leaveTriggerWithID(curTriggerID);
            enableWakeup();
            SpeechClient.instance().getWakeupEngine().stopDialogReason(TAG);
            curTriggerID = -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void suspendXP() {
        SpeechClient.instance().getWakeupEngine().suspendSpeechWithInfo(this.mWakeupBinder, "泊车", "为了你的驾驶安全，语音暂不可用哦", 2, false);
    }

    public void resumeXP() {
        SpeechClient.instance().getWakeupEngine().resumeSpeechWithTypeInfo(this.mWakeupBinder, "泊车", 2, false);
    }

    private void removeSetExitParkingRunnable() {
    }

    public boolean isShouldExitParking() {
        return this.isShouldExitParking;
    }

    public void setShouldExitParking(boolean exitParking) {
        removeSetExitParkingRunnable();
        this.isShouldExitParking = exitParking;
    }

    public void setWheelWakeupEnabled(boolean state) {
        SpeechClient.instance().getWakeupEngine().setWheelWakeupEnabled(this.mWakeupBinder, state);
    }

    public void enableWakeup() {
        if (!CarTypeUtils.isE38ZH() && !CarTypeUtils.isE28AZH()) {
            SpeechClient.instance().getWakeupEngine().enableMainWakeupWord(this.mWakeupBinder);
            SpeechClient.instance().getWakeupEngine().enableFastWakeEnhance(this.mWakeupBinder);
            SpeechClient.instance().getHotwordEngine().removeHotWords(HotwordEngineProxy.BY_AUTO_PARKING);
            SpeechClient.instance().getHotwordEngine().removeHotWords(HotwordEngineProxy.BY_PARKING_ALONG_THE_WAY);
            return;
        }
        SpeechClient.instance().getWakeupEngine().enableWakeupWithInfo(this.mWakeupBinder, 1, "泊车", 2);
    }

    public void disableWakeup() {
        if (!CarTypeUtils.isE38ZH() && !CarTypeUtils.isE28AZH()) {
            SpeechClient.instance().getWakeupEngine().disableMainWakeupWord(this.mWakeupBinder);
            SpeechClient.instance().getWakeupEngine().disableFastWakeEnhance(this.mWakeupBinder);
            return;
        }
        SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mWakeupBinder, 1, "泊车", "请告诉我你要泊入的车位哦", 2);
    }

    public void stopCarSpeech() {
        removeSetExitParkingRunnable();
        this.isShouldExitParking = false;
        SpeechClient.instance().getWakeupEngine().stopDialogReason(DMEnd.REASON_AUTO_PARK);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AutoParkingEvent.ACTIVATE)
    public void onActivate(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AutoParkingListener) obj).onActivate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AutoParkingEvent.EXIT)
    public void onExit(String event, String data) {
        removeSetExitParkingRunnable();
        this.isShouldExitParking = false;
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AutoParkingListener) obj).onExit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AutoParkingEvent.PARK_START)
    public void onParkStart(String event, String data) {
        removeSetExitParkingRunnable();
        this.isShouldExitParking = false;
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AutoParkingListener) obj).onParkStart(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AutoParkingEvent.PARK_CARPORT_COUNT)
    public void onParkCarportCount(String event, String data) {
        ParkingPositionBean positionBean = ParkingPositionBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AutoParkingListener) obj).onParkCarportCount(positionBean);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = AutoParkingEvent.MEMORY_PARKING_START)
    public void onMemoryParkingStart(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((AutoParkingListener) obj).onMemoryParkingStart();
            }
        }
    }
}
