package com.xiaopeng.speech.protocol.query.carcontrol;

import com.alibaba.fastjson.parser.JSONLexer;
import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QueryCarControlEvent;
import kotlin.text.Typography;
/* loaded from: classes23.dex */
public class CarControlQuery_Processor implements IQueryProcessor {
    private CarControlQuery mTarget;

    public CarControlQuery_Processor(CarControlQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2121187452:
                if (event.equals(QueryCarControlEvent.CONTROL_COMFORTABLE_DRIVING_MODE_SUPPORT)) {
                    c = ')';
                    break;
                }
                c = 65535;
                break;
            case -2054773087:
                if (event.equals(QueryCarControlEvent.GET_PAGE_OPEN_STATUS)) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case -2034277098:
                if (event.equals(QueryCarControlEvent.CONTROL_SCISSOR_DOOR_LEFT_RUNNING_SUPPORT)) {
                    c = '%';
                    break;
                }
                c = 65535;
                break;
            case -1897516928:
                if (event.equals(QueryCarControlEvent.GET_STATUS_LIGHT_ATMOSPHERE_COLOR)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -1678437728:
                if (event.equals(QueryCarControlEvent.CONTROL_LEG_HEIGHT_GET)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case -1656659569:
                if (event.equals(QueryCarControlEvent.GET_EXTRA_TRUNK_STATUS)) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case -1550136303:
                if (event.equals(QueryCarControlEvent.CONTROL_SCISSOR_DOOR_RIGHT_RUNNING_SUPPORT)) {
                    c = Typography.amp;
                    break;
                }
                c = 65535;
                break;
            case -1521777381:
                if (event.equals(QueryCarControlEvent.GET_CURR_WIPER_LEVEL)) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case -1482534751:
                if (event.equals(QueryCarControlEvent.GET_SUPPORT_SEAT)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1431154399:
                if (event.equals(QueryCarControlEvent.CONTROL_SUPPORT_ENERGY_RECYCLE_REASON)) {
                    c = ' ';
                    break;
                }
                c = 65535;
                break;
            case -1414817355:
                if (event.equals(QueryCarControlEvent.CONTROL_CAPSULE_MODE)) {
                    c = '-';
                    break;
                }
                c = 65535;
                break;
            case -1412321594:
                if (event.equals(QueryCarControlEvent.GET_SET_SPEECH_WAKEUP_STATUS)) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case -1392585057:
                if (event.equals(QueryCarControlEvent.GET_STATUS_OPEN_R_CHARGE_PORT)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -1268774449:
                if (event.equals(QueryCarControlEvent.CONTROL_SCISSOR_DOOR_LEFT_CLOSE_SUPPORT)) {
                    c = '#';
                    break;
                }
                c = 65535;
                break;
            case -1193245651:
                if (event.equals(QueryCarControlEvent.GET_SUPPORT_OPEN_TRUNK)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1144792468:
                if (event.equals(QueryCarControlEvent.GET_STATUS_LIGHT_ATMOSPHERE_BRIGHTNESS)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case -1139999239:
                if (event.equals(QueryCarControlEvent.IS_SUPPORT_CLOSE_MIRROR)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1102263664:
                if (event.equals(QueryCarControlEvent.IS_SUPPORT_UNLOCK_TRUNK)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -952459949:
                if (event.equals(QueryCarControlEvent.GET_STATUS_CLOSE_R_CHARGE_PORT)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -945429027:
                if (event.equals(QueryCarControlEvent.CONTROL_VIP_CHAIR_STATUS)) {
                    c = ',';
                    break;
                }
                c = 65535;
                break;
            case -932480699:
                if (event.equals(QueryCarControlEvent.GET_STATUS_OPEN_L_CHARGE_PORT)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -894608748:
                if (event.equals(QueryCarControlEvent.CONTROL_XPEDAL_SUPPORT)) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case -598119942:
                if (event.equals(QueryCarControlEvent.XPU_NGP_STATUS)) {
                    c = '+';
                    break;
                }
                c = 65535;
                break;
            case -178106686:
                if (event.equals(QueryCarControlEvent.IS_SUPPORT_OPEN_R_CHARGE_PORT)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 6319454:
                if (event.equals(QueryCarControlEvent.CONTROL_GET_WINDOWS_STATE_SUPPORT)) {
                    c = '(';
                    break;
                }
                c = 65535;
                break;
            case 264514798:
                if (event.equals(QueryCarControlEvent.IS_SUPPORT_DRIVING_MODE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 325191539:
                if (event.equals(QueryCarControlEvent.GET_WINDOW_STATUS)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 355863260:
                if (event.equals(QueryCarControlEvent.GET_MIRROR_STATUS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 359526324:
                if (event.equals(QueryCarControlEvent.IS_SUPPORT_OPEN_L_CHARGE_PORT)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 425873261:
                if (event.equals(QueryCarControlEvent.GET_STATUS_CLOSE_L_CHARGE_PORT)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case 484711863:
                if (event.equals(QueryCarControlEvent.IS_SUPPORT_CONTROL_MIRROR)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 548112633:
                if (event.equals(QueryCarControlEvent.IS_STEERING_MODE_ADJUSTABLE)) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 580869774:
                if (event.equals(QueryCarControlEvent.IS_SUPPORT_CLOSE_R_CHARGE_PORT)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 631100506:
                if (event.equals(QueryCarControlEvent.CONTROL_SCISSOR_DOOR_RIGHT_OPEN_SUPPORT)) {
                    c = Typography.quote;
                    break;
                }
                c = 65535;
                break;
            case 676171332:
                if (event.equals(QueryCarControlEvent.CONTROL_LOW_SPEED_ANALOG_SOUND_SUPPORT)) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case 689991955:
                if (event.equals(QueryCarControlEvent.CONTROL_ELECTRIC_CURTAIN_SUPPORT)) {
                    c = '\'';
                    break;
                }
                c = 65535;
                break;
            case 700759210:
                if (event.equals(QueryCarControlEvent.IS_SUPPORT_LITE_ATMOSPHERE)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 956386737:
                if (event.equals(QueryCarControlEvent.IS_SUPPORT_ENERGY_RECYCLE)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1200226524:
                if (event.equals(QueryCarControlEvent.IS_SUPPORT_CLOSE_L_CHARGE_PORT)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 1333952193:
                if (event.equals(QueryCarControlEvent.GET_SUPPORT_CLOSE_TRUNK)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1338946660:
                if (event.equals(QueryCarControlEvent.GET_PSN_SUPPORT_SEAT)) {
                    c = JSONLexer.EOI;
                    break;
                }
                c = 65535;
                break;
            case 1364128650:
                if (event.equals(QueryCarControlEvent.CONTROL_SCISSOR_DOOR_RIGHT_CLOSE_SUPPORT)) {
                    c = Typography.dollar;
                    break;
                }
                c = 65535;
                break;
            case 1397046919:
                if (event.equals(QueryCarControlEvent.IS_TAIRPRESSURE_NORMAL)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1654546805:
                if (event.equals(QueryCarControlEvent.CONTROL_SCISSOR_DOOR_LEFT_OPEN_SUPPORT)) {
                    c = '!';
                    break;
                }
                c = 65535;
                break;
            case 1697319758:
                if (event.equals(QueryCarControlEvent.CONTROL_LAMP_SIGNAL_SUPPORT)) {
                    c = '*';
                    break;
                }
                c = 65535;
                break;
            case 1961007153:
                if (event.equals(QueryCarControlEvent.GET_TRUNK_STATUS)) {
                    c = 28;
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
                return Boolean.valueOf(this.mTarget.isSupportCloseMirror(event, data));
            case 1:
                return Integer.valueOf(this.mTarget.getMirrorStatus(event, data));
            case 2:
                return Integer.valueOf(this.mTarget.getSupportOpenTrunk(event, data));
            case 3:
                return Integer.valueOf(this.mTarget.getSupportCloseTrunk(event, data));
            case 4:
                return Integer.valueOf(this.mTarget.getWindowStatus(event, data));
            case 5:
                return Boolean.valueOf(this.mTarget.isSupportEngryRecycle(event, data));
            case 6:
                return Integer.valueOf(this.mTarget.getSupportSeat(event, data));
            case 7:
                return Boolean.valueOf(this.mTarget.isSupportDrivingMode(event, data));
            case '\b':
                return Boolean.valueOf(this.mTarget.isSupportAtmosphere(event, data));
            case '\t':
                return Boolean.valueOf(this.mTarget.isTirePressureNormal(event, data));
            case '\n':
                return Boolean.valueOf(this.mTarget.isSupportUnlockTrunk(event, data));
            case 11:
                return Boolean.valueOf(this.mTarget.isSupportCloseLeftChargePort(event, data));
            case '\f':
                return Boolean.valueOf(this.mTarget.isSupportCloseRightChargePort(event, data));
            case '\r':
                return Boolean.valueOf(this.mTarget.isSupportOpenLeftChargePort(event, data));
            case 14:
                return Boolean.valueOf(this.mTarget.isSupportOpenRightChargePort(event, data));
            case 15:
                return Boolean.valueOf(this.mTarget.isSupportControlMirror(event, data));
            case 16:
                return Integer.valueOf(this.mTarget.getLegHeight(event, data));
            case 17:
                return Integer.valueOf(this.mTarget.getStatusCloseLeftChargePort(event, data));
            case 18:
                return Integer.valueOf(this.mTarget.getStatusCloseRightChargePort(event, data));
            case 19:
                return Integer.valueOf(this.mTarget.getStatusOpenLeftChargePort(event, data));
            case 20:
                return Integer.valueOf(this.mTarget.getStatusOpenRightChargePort(event, data));
            case 21:
                return Integer.valueOf(this.mTarget.getAtmosphereBrightnessStatus(event, data));
            case 22:
                return Integer.valueOf(this.mTarget.getAtmosphereColorStatus(event, data));
            case 23:
                return Integer.valueOf(this.mTarget.isSteeringModeAdjustable(event, data));
            case 24:
                return Integer.valueOf(this.mTarget.getGuiPageOpenState(event, data));
            case 25:
                return Integer.valueOf(this.mTarget.getWiperInterval(event, data));
            case 26:
                return Integer.valueOf(this.mTarget.getSupportPsnSeat(event, data));
            case 27:
                return Integer.valueOf(this.mTarget.getExtraTrunkStatus(event, data));
            case 28:
                return Integer.valueOf(this.mTarget.getTrunkStatus(event, data));
            case 29:
                return Integer.valueOf(this.mTarget.getDoorKeyValue(event, data));
            case 30:
                return Integer.valueOf(this.mTarget.getControlLowSpeedAnalogSoundSupport(event, data));
            case 31:
                return Integer.valueOf(this.mTarget.getControlXpedalSupport(event, data));
            case ' ':
                return Integer.valueOf(this.mTarget.getControlSupportEnergyRecycleReason(event, data));
            case '!':
                return Integer.valueOf(this.mTarget.getControlScissorDoorLeftOpenSupport(event, data));
            case '\"':
                return Integer.valueOf(this.mTarget.getControlScissorDoorCloseSupport(event, data));
            case '#':
                return Integer.valueOf(this.mTarget.getControlScissorDoorLeftCloseSupport(event, data));
            case '$':
                return Integer.valueOf(this.mTarget.getControlScissorDoorRightCloseSupport(event, data));
            case '%':
                return Integer.valueOf(this.mTarget.getControlScissorDoorLeftRunningSupport(event, data));
            case '&':
                return Integer.valueOf(this.mTarget.getControlScissorDoorRightRunningSupport(event, data));
            case '\'':
                return Integer.valueOf(this.mTarget.getControlElectricCurtainSupport(event, data));
            case '(':
                return this.mTarget.getControlWindowsStateSupport(event, data);
            case ')':
                return Integer.valueOf(this.mTarget.getControlComfortableDrivingModeSupport(event, data));
            case '*':
                return Integer.valueOf(this.mTarget.getControlLampSignalSupport(event, data));
            case '+':
                return Integer.valueOf(this.mTarget.getNgpStatus(event, data));
            case ',':
                return Integer.valueOf(this.mTarget.getVipChairStatus(event, data));
            case '-':
                return Integer.valueOf(this.mTarget.getCapsuleCurrentMode(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryCarControlEvent.IS_SUPPORT_CLOSE_MIRROR, QueryCarControlEvent.GET_MIRROR_STATUS, QueryCarControlEvent.GET_SUPPORT_OPEN_TRUNK, QueryCarControlEvent.GET_SUPPORT_CLOSE_TRUNK, QueryCarControlEvent.GET_WINDOW_STATUS, QueryCarControlEvent.IS_SUPPORT_ENERGY_RECYCLE, QueryCarControlEvent.GET_SUPPORT_SEAT, QueryCarControlEvent.IS_SUPPORT_DRIVING_MODE, QueryCarControlEvent.IS_SUPPORT_LITE_ATMOSPHERE, QueryCarControlEvent.IS_TAIRPRESSURE_NORMAL, QueryCarControlEvent.IS_SUPPORT_UNLOCK_TRUNK, QueryCarControlEvent.IS_SUPPORT_CLOSE_L_CHARGE_PORT, QueryCarControlEvent.IS_SUPPORT_CLOSE_R_CHARGE_PORT, QueryCarControlEvent.IS_SUPPORT_OPEN_L_CHARGE_PORT, QueryCarControlEvent.IS_SUPPORT_OPEN_R_CHARGE_PORT, QueryCarControlEvent.IS_SUPPORT_CONTROL_MIRROR, QueryCarControlEvent.CONTROL_LEG_HEIGHT_GET, QueryCarControlEvent.GET_STATUS_CLOSE_L_CHARGE_PORT, QueryCarControlEvent.GET_STATUS_CLOSE_R_CHARGE_PORT, QueryCarControlEvent.GET_STATUS_OPEN_L_CHARGE_PORT, QueryCarControlEvent.GET_STATUS_OPEN_R_CHARGE_PORT, QueryCarControlEvent.GET_STATUS_LIGHT_ATMOSPHERE_BRIGHTNESS, QueryCarControlEvent.GET_STATUS_LIGHT_ATMOSPHERE_COLOR, QueryCarControlEvent.IS_STEERING_MODE_ADJUSTABLE, QueryCarControlEvent.GET_PAGE_OPEN_STATUS, QueryCarControlEvent.GET_CURR_WIPER_LEVEL, QueryCarControlEvent.GET_PSN_SUPPORT_SEAT, QueryCarControlEvent.GET_EXTRA_TRUNK_STATUS, QueryCarControlEvent.GET_TRUNK_STATUS, QueryCarControlEvent.GET_SET_SPEECH_WAKEUP_STATUS, QueryCarControlEvent.CONTROL_LOW_SPEED_ANALOG_SOUND_SUPPORT, QueryCarControlEvent.CONTROL_XPEDAL_SUPPORT, QueryCarControlEvent.CONTROL_SUPPORT_ENERGY_RECYCLE_REASON, QueryCarControlEvent.CONTROL_SCISSOR_DOOR_LEFT_OPEN_SUPPORT, QueryCarControlEvent.CONTROL_SCISSOR_DOOR_RIGHT_OPEN_SUPPORT, QueryCarControlEvent.CONTROL_SCISSOR_DOOR_LEFT_CLOSE_SUPPORT, QueryCarControlEvent.CONTROL_SCISSOR_DOOR_RIGHT_CLOSE_SUPPORT, QueryCarControlEvent.CONTROL_SCISSOR_DOOR_LEFT_RUNNING_SUPPORT, QueryCarControlEvent.CONTROL_SCISSOR_DOOR_RIGHT_RUNNING_SUPPORT, QueryCarControlEvent.CONTROL_ELECTRIC_CURTAIN_SUPPORT, QueryCarControlEvent.CONTROL_GET_WINDOWS_STATE_SUPPORT, QueryCarControlEvent.CONTROL_COMFORTABLE_DRIVING_MODE_SUPPORT, QueryCarControlEvent.CONTROL_LAMP_SIGNAL_SUPPORT, QueryCarControlEvent.XPU_NGP_STATUS, QueryCarControlEvent.CONTROL_VIP_CHAIR_STATUS, QueryCarControlEvent.CONTROL_CAPSULE_MODE};
    }
}
