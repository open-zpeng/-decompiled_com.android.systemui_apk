package com.xiaopeng.speech.protocol.query.speech.hardware;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechHardwareEvent;
import com.xiaopeng.speech.protocol.query.speech.hardware.bean.StreamType;
/* loaded from: classes23.dex */
public class SpeechHardwareQuery extends SpeechQuery<ISpeechHardwareQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_MCU_ID)
    public String getMcuHardWareId(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getMcuHardWareId();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_CPU_TEMP)
    public int getCpuTemperature(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getCpuTemperature();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_IPU_FAIL_INFO)
    public int getIpuFailStInfo(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getIpuFailStInfo();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_CTRL_VOLT)
    public double getCtrlVolt(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getCtrlVolt();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_CTRL_CURR)
    public double getCtrlCurr(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getCtrlCurr();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_CTRL_TEMP)
    public int getCtrlTemp(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getCtrlTemp();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_MOTOR_TEMP)
    public int getMotorTemp(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getMotorTemp();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_TORQUE)
    public double getTorque(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getTorque();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_ROLL_SPEED)
    public int getRollSpeed(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getRollSpeed();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_MOTOR_STATE)
    public int getMotorStatus(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getMotorStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_CAR_TYPE)
    public String getCarType(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getCarType();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_STREAM_TYPE)
    public int getStreamType(String event, String data) {
        StreamType streamType = StreamType.fromJson(data);
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getStreamType(streamType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechHardwareEvent.HARDWARE_CHECKLIST)
    public int getChecklistStatus(String event, String data) {
        return ((ISpeechHardwareQueryCaller) this.mQueryCaller).getChecklistStatus(data);
    }
}
