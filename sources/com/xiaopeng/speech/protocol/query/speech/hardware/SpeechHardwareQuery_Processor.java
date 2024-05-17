package com.xiaopeng.speech.protocol.query.speech.hardware;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechHardwareEvent;
/* loaded from: classes23.dex */
public class SpeechHardwareQuery_Processor implements IQueryProcessor {
    private SpeechHardwareQuery mTarget;

    public SpeechHardwareQuery_Processor(SpeechHardwareQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1802748326:
                if (event.equals(SpeechHardwareEvent.HARDWARE_CAR_TYPE)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1289226848:
                if (event.equals(SpeechHardwareEvent.HARDWARE_CPU_TEMP)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -147500196:
                if (event.equals(SpeechHardwareEvent.HARDWARE_ROLL_SPEED)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 412745551:
                if (event.equals(SpeechHardwareEvent.HARDWARE_CTRL_CURR)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 413236465:
                if (event.equals(SpeechHardwareEvent.HARDWARE_CTRL_TEMP)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 413305630:
                if (event.equals(SpeechHardwareEvent.HARDWARE_CTRL_VOLT)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1437417016:
                if (event.equals(SpeechHardwareEvent.HARDWARE_IPU_FAIL_INFO)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1466238608:
                if (event.equals(SpeechHardwareEvent.HARDWARE_MCU_ID)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1677700304:
                if (event.equals(SpeechHardwareEvent.HARDWARE_TORQUE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1749101026:
                if (event.equals(SpeechHardwareEvent.HARDWARE_STREAM_TYPE)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 2019491538:
                if (event.equals(SpeechHardwareEvent.HARDWARE_MOTOR_STATE)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 2120141088:
                if (event.equals(SpeechHardwareEvent.HARDWARE_CHECKLIST)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 2143370611:
                if (event.equals(SpeechHardwareEvent.HARDWARE_MOTOR_TEMP)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return this.mTarget.getMcuHardWareId(event, data);
            case 1:
                return Integer.valueOf(this.mTarget.getCpuTemperature(event, data));
            case 2:
                return Integer.valueOf(this.mTarget.getIpuFailStInfo(event, data));
            case 3:
                return Double.valueOf(this.mTarget.getCtrlVolt(event, data));
            case 4:
                return Double.valueOf(this.mTarget.getCtrlCurr(event, data));
            case 5:
                return Integer.valueOf(this.mTarget.getCtrlTemp(event, data));
            case 6:
                return Integer.valueOf(this.mTarget.getMotorTemp(event, data));
            case 7:
                return Double.valueOf(this.mTarget.getTorque(event, data));
            case '\b':
                return Integer.valueOf(this.mTarget.getRollSpeed(event, data));
            case '\t':
                return Integer.valueOf(this.mTarget.getMotorStatus(event, data));
            case '\n':
                return this.mTarget.getCarType(event, data);
            case 11:
                return Integer.valueOf(this.mTarget.getStreamType(event, data));
            case '\f':
                return Integer.valueOf(this.mTarget.getChecklistStatus(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechHardwareEvent.HARDWARE_MCU_ID, SpeechHardwareEvent.HARDWARE_CPU_TEMP, SpeechHardwareEvent.HARDWARE_IPU_FAIL_INFO, SpeechHardwareEvent.HARDWARE_CTRL_VOLT, SpeechHardwareEvent.HARDWARE_CTRL_CURR, SpeechHardwareEvent.HARDWARE_CTRL_TEMP, SpeechHardwareEvent.HARDWARE_MOTOR_TEMP, SpeechHardwareEvent.HARDWARE_TORQUE, SpeechHardwareEvent.HARDWARE_ROLL_SPEED, SpeechHardwareEvent.HARDWARE_MOTOR_STATE, SpeechHardwareEvent.HARDWARE_CAR_TYPE, SpeechHardwareEvent.HARDWARE_STREAM_TYPE, SpeechHardwareEvent.HARDWARE_CHECKLIST};
    }
}
