package com.xiaopeng.speech.protocol.query.speech.carcontrol;

import com.alibaba.fastjson.parser.JSONLexer;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechCarControlEvent;
import kotlin.text.Typography;
/* loaded from: classes23.dex */
public class SpeechCarControlQuery_Processor implements IQueryProcessor {
    private SpeechCarControlQuery mTarget;

    public SpeechCarControlQuery_Processor(SpeechCarControlQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2060488725:
                if (event.equals(SpeechCarControlEvent.FRONT_COLLISION_STATE)) {
                    c = Typography.dollar;
                    break;
                }
                c = 65535;
                break;
            case -1974077749:
                if (event.equals(SpeechCarControlEvent.LIGHTMEHOME_STATE)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1952147251:
                if (event.equals(SpeechCarControlEvent.SEAT_POSITION)) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case -1941037730:
                if (event.equals(SpeechCarControlEvent.BELT_ELECTRIC_STATE)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case -1878001850:
                if (event.equals(SpeechCarControlEvent.LAMP_FAR)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1741151858:
                if (event.equals(SpeechCarControlEvent.LAMP_HEADGROUP)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1664162452:
                if (event.equals(SpeechCarControlEvent.WARN_BLIND_AREA_STATE)) {
                    c = ')';
                    break;
                }
                c = 65535;
                break;
            case -1599309710:
                if (event.equals(SpeechCarControlEvent.TRUNK_STATE)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -1375618344:
                if (event.equals(SpeechCarControlEvent.WARN_LANE_DEPARTURE_STATE)) {
                    c = '(';
                    break;
                }
                c = 65535;
                break;
            case -1368961668:
                if (event.equals(SpeechCarControlEvent.GET_WIPER_LEVEL)) {
                    c = '7';
                    break;
                }
                c = 65535;
                break;
            case -1288966170:
                if (event.equals(SpeechCarControlEvent.GET_TIRE_PRESSURE)) {
                    c = '8';
                    break;
                }
                c = 65535;
                break;
            case -1209587430:
                if (event.equals(SpeechCarControlEvent.ICM_VOLUME_WARN)) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case -1073244956:
                if (event.equals(SpeechCarControlEvent.SEAT_ERROR)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -1021329318:
                if (event.equals(SpeechCarControlEvent.GET_CRUISE_CONTROL_STATUS)) {
                    c = Typography.greater;
                    break;
                }
                c = 65535;
                break;
            case -1008347830:
                if (event.equals(SpeechCarControlEvent.LCC_STATE)) {
                    c = ';';
                    break;
                }
                c = 65535;
                break;
            case -870356283:
                if (event.equals(SpeechCarControlEvent.LAMP_INNER)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -786298349:
                if (event.equals(SpeechCarControlEvent.LOCK_STATE)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -722435495:
                if (event.equals(SpeechCarControlEvent.METER_LIMIT_INTEL)) {
                    c = '%';
                    break;
                }
                c = 65535;
                break;
            case -555222776:
                if (event.equals(SpeechCarControlEvent.SPEED_CAR)) {
                    c = '0';
                    break;
                }
                c = 65535;
                break;
            case -501644054:
                if (event.equals(SpeechCarControlEvent.BELT_REARSEAT_STATE)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -372830826:
                if (event.equals(SpeechCarControlEvent.WINDOW_LOCK_STATE)) {
                    c = Typography.less;
                    break;
                }
                c = 65535;
                break;
            case -346554662:
                if (event.equals(SpeechCarControlEvent.DOOR_STATE)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -258533676:
                if (event.equals(SpeechCarControlEvent.METER_LAST_START)) {
                    c = '#';
                    break;
                }
                c = 65535;
                break;
            case -139083951:
                if (event.equals(SpeechCarControlEvent.LOCK_DRIVE_AUTIO)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -126307048:
                if (event.equals(SpeechCarControlEvent.WINDOW_STATE)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -122050850:
                if (event.equals(SpeechCarControlEvent.IS_CAR_TRIP)) {
                    c = '6';
                    break;
                }
                c = 65535;
                break;
            case -86335604:
                if (event.equals(SpeechCarControlEvent.DRIVE_MODE)) {
                    c = JSONLexer.EOI;
                    break;
                }
                c = 65535;
                break;
            case -65424329:
                if (event.equals(SpeechCarControlEvent.ATWS_STATE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -20170038:
                if (event.equals(SpeechCarControlEvent.RECYCLE_LV)) {
                    c = '+';
                    break;
                }
                c = 65535;
                break;
            case 106241666:
                if (event.equals(SpeechCarControlEvent.METER_LAST_CHARGE)) {
                    c = Typography.quote;
                    break;
                }
                c = 65535;
                break;
            case 224275343:
                if (event.equals(SpeechCarControlEvent.WARN_RADAR_SLOW_STATE)) {
                    c = '*';
                    break;
                }
                c = 65535;
                break;
            case 234544457:
                if (event.equals(SpeechCarControlEvent.LAMP_REARFOG)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 404150481:
                if (event.equals(SpeechCarControlEvent.ICM_STATE)) {
                    c = '1';
                    break;
                }
                c = 65535;
                break;
            case 465177042:
                if (event.equals(SpeechCarControlEvent.SPEED_LIMIT_STATE)) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case 467392178:
                if (event.equals(SpeechCarControlEvent.SPEED_LIMIT_VALUE)) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case 473786283:
                if (event.equals(SpeechCarControlEvent.BRAKE_WARN)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 637798997:
                if (event.equals(SpeechCarControlEvent.TRUNK_OPENNER_STATE)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 646093110:
                if (event.equals(SpeechCarControlEvent.SEAT_WELCOME_MODE_STATE)) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case 714771718:
                if (event.equals(SpeechCarControlEvent.IG_STATE)) {
                    c = '2';
                    break;
                }
                c = 65535;
                break;
            case 717411520:
                if (event.equals(SpeechCarControlEvent.SHIFT_STATE)) {
                    c = ',';
                    break;
                }
                c = 65535;
                break;
            case 768526207:
                if (event.equals(SpeechCarControlEvent.SEAT_MAIN_HUMAN_STATE)) {
                    c = '-';
                    break;
                }
                c = 65535;
                break;
            case 778341606:
                if (event.equals(SpeechCarControlEvent.ROTATION_STEER)) {
                    c = '/';
                    break;
                }
                c = 65535;
                break;
            case 793162096:
                if (event.equals(SpeechCarControlEvent.ICM_WIND_LV)) {
                    c = '5';
                    break;
                }
                c = 65535;
                break;
            case 918463002:
                if (event.equals(SpeechCarControlEvent.ICM_DRIVER_TEMP)) {
                    c = '4';
                    break;
                }
                c = 65535;
                break;
            case 1181629464:
                if (event.equals(SpeechCarControlEvent.SEAT_PASSENGER_HUMAN_STATE)) {
                    c = '.';
                    break;
                }
                c = 65535;
                break;
            case 1238236251:
                if (event.equals(SpeechCarControlEvent.SEAT_DIRECTION)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 1294982829:
                if (event.equals(SpeechCarControlEvent.LOCK_UNLOCK_RESPONSE)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 1319619428:
                if (event.equals(SpeechCarControlEvent.OLED_STATE)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 1355038943:
                if (event.equals(SpeechCarControlEvent.WARN_SIDE_REVER_STATE)) {
                    c = '\'';
                    break;
                }
                c = 65535;
                break;
            case 1375556580:
                if (event.equals(SpeechCarControlEvent.GET_STATUS_LOW_SOC)) {
                    c = '=';
                    break;
                }
                c = 65535;
                break;
            case 1436055782:
                if (event.equals(SpeechCarControlEvent.LAMP_LOCATION)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1501829463:
                if (event.equals(SpeechCarControlEvent.METER_A)) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case 1501829464:
                if (event.equals(SpeechCarControlEvent.METER_B)) {
                    c = ' ';
                    break;
                }
                c = 65535;
                break;
            case 1574537530:
                if (event.equals(SpeechCarControlEvent.METER_TOTAL)) {
                    c = '!';
                    break;
                }
                c = 65535;
                break;
            case 1693782079:
                if (event.equals(SpeechCarControlEvent.ACC_STATE)) {
                    c = AccessibilityUtils.ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR;
                    break;
                }
                c = 65535;
                break;
            case 1902828131:
                if (event.equals(SpeechCarControlEvent.LOCK_PARKING_AUTIO)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 1911726553:
                if (event.equals(SpeechCarControlEvent.LAMP_NEAR)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1955438847:
                if (event.equals(SpeechCarControlEvent.MODE_WELCOME_STATE)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 2019589129:
                if (event.equals(SpeechCarControlEvent.ICM_WIND_MODE)) {
                    c = '3';
                    break;
                }
                c = 65535;
                break;
            case 2043201887:
                if (event.equals(SpeechCarControlEvent.GET_TIRE_PRESSURE_WARNINGS)) {
                    c = '9';
                    break;
                }
                c = 65535;
                break;
            case 2063463053:
                if (event.equals(SpeechCarControlEvent.LANE_CHANGE_ASSIST_STATE)) {
                    c = Typography.amp;
                    break;
                }
                c = 65535;
                break;
            case 2119417017:
                if (event.equals(SpeechCarControlEvent.PASSENGER_SEAT_POSITION)) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case 2133982950:
                if (event.equals(SpeechCarControlEvent.STEER_EPS)) {
                    c = 27;
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
                return Boolean.valueOf(this.mTarget.getRearFogLamp(event, data));
            case 1:
                return Boolean.valueOf(this.mTarget.getNearLampState(event, data));
            case 2:
                return Boolean.valueOf(this.mTarget.getLocationLampState(event, data));
            case 3:
                return Boolean.valueOf(this.mTarget.getFarLampState(event, data));
            case 4:
                return Integer.valueOf(this.mTarget.getHeadLampGroup(event, data));
            case 5:
                return Boolean.valueOf(this.mTarget.getInternalLight(event, data));
            case 6:
                return Boolean.valueOf(this.mTarget.getEmergencyBrakeWarning(event, data));
            case 7:
                return Integer.valueOf(this.mTarget.getATWSState(event, data));
            case '\b':
                return Integer.valueOf(this.mTarget.getOled(event, data));
            case '\t':
                return Integer.valueOf(this.mTarget.getLightMeHome(event, data));
            case '\n':
                return Boolean.valueOf(this.mTarget.getDriveAutoLock(event, data));
            case 11:
                return Boolean.valueOf(this.mTarget.getParkingAutoUnlock(event, data));
            case '\f':
                return Boolean.valueOf(this.mTarget.getDoorLockState(event, data));
            case '\r':
                return Integer.valueOf(this.mTarget.getTrunk(event, data));
            case 14:
                return Integer.valueOf(this.mTarget.getTrunkOpennerState(event, data));
            case 15:
                return Boolean.valueOf(this.mTarget.getChairWelcomeMode(event, data));
            case 16:
                return Boolean.valueOf(this.mTarget.getElectricSeatBelt(event, data));
            case 17:
                return Boolean.valueOf(this.mTarget.getRearSeatBeltWarning(event, data));
            case 18:
                return Integer.valueOf(this.mTarget.getUnlockResponse(event, data));
            case 19:
                return this.mTarget.getDoorsState(event, data);
            case 20:
                return this.mTarget.getWindowsState(event, data);
            case 21:
                return this.mTarget.getChairDirection(event, data);
            case 22:
                return Boolean.valueOf(this.mTarget.getSeatErrorState(event, data));
            case 23:
                return this.mTarget.getChairLocationValue(event, data);
            case 24:
                return this.mTarget.getPassengerChairLocationValue(event, data);
            case 25:
                return Boolean.valueOf(this.mTarget.getWelcomeModeBackStatus(event, data));
            case 26:
                return Integer.valueOf(this.mTarget.getDrivingMode(event, data));
            case 27:
                return Integer.valueOf(this.mTarget.getSteeringWheelEPS(event, data));
            case 28:
                return Integer.valueOf(this.mTarget.getIcmAlarmVolume(event, data));
            case 29:
                return Boolean.valueOf(this.mTarget.getSpeedLimitWarningSwitch(event, data));
            case 30:
                return Integer.valueOf(this.mTarget.getSpeedLimitWarningValue(event, data));
            case 31:
                return Double.valueOf(this.mTarget.getMeterMileageA(event, data));
            case ' ':
                return Double.valueOf(this.mTarget.getMeterMileageB(event, data));
            case '!':
                return Double.valueOf(this.mTarget.getDriveTotalMileage(event, data));
            case '\"':
                return Double.valueOf(this.mTarget.getLastChargeMileage(event, data));
            case '#':
                return Double.valueOf(this.mTarget.getLastStartUpMileage(event, data));
            case '$':
                return Integer.valueOf(this.mTarget.getFrontCollisionSecurity(event, data));
            case '%':
                return Integer.valueOf(this.mTarget.getIntelligentSpeedLimit(event, data));
            case '&':
                return Integer.valueOf(this.mTarget.getLaneChangeAssist(event, data));
            case '\'':
                return Integer.valueOf(this.mTarget.getSideReversingWarning(event, data));
            case '(':
                return Integer.valueOf(this.mTarget.getLaneDepartureWarning(event, data));
            case ')':
                return Integer.valueOf(this.mTarget.getBlindAreaDetectionWarning(event, data));
            case '*':
                return Boolean.valueOf(this.mTarget.getRadarWarningVoiceStatus(event, data));
            case '+':
                return Integer.valueOf(this.mTarget.getEnergyRecycleLevel(event, data));
            case ',':
                return Integer.valueOf(this.mTarget.getShiftStatus(event, data));
            case '-':
                return Integer.valueOf(this.mTarget.getDriverSeatStatus(event, data));
            case '.':
                return Integer.valueOf(this.mTarget.getPassengerSeatStatus(event, data));
            case '/':
                return Double.valueOf(this.mTarget.getSteerWheelRotationAngle(event, data));
            case '0':
                return Double.valueOf(this.mTarget.getCarSpeed(event, data));
            case '1':
                return Boolean.valueOf(this.mTarget.getIcmConnectionState(event, data));
            case '2':
                return Integer.valueOf(this.mTarget.getBCMIgStatus(event, data));
            case '3':
                return Integer.valueOf(this.mTarget.getICMWindBlowMode(event, data));
            case '4':
                return Double.valueOf(this.mTarget.getICMDriverTempValue(event, data));
            case '5':
                return Integer.valueOf(this.mTarget.getICMWindLevel(event, data));
            case '6':
                return Boolean.valueOf(this.mTarget.isCarTrip(event, data));
            case '7':
                return Integer.valueOf(this.mTarget.getWiperInterval(event, data));
            case '8':
                return this.mTarget.getTirePressureAll(event, data);
            case '9':
                return this.mTarget.getAllTirePressureWarnings(event, data);
            case ':':
                return Integer.valueOf(this.mTarget.getACCStatus(event, data));
            case ';':
                return Integer.valueOf(this.mTarget.getLCCStatus(event, data));
            case '<':
                return Integer.valueOf(this.mTarget.getWindowLockStatus(event, data));
            case '=':
                return Integer.valueOf(this.mTarget.getLowSocStatus(event, data));
            case '>':
                return Integer.valueOf(this.mTarget.getCruiseControlStatus(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechCarControlEvent.LAMP_REARFOG, SpeechCarControlEvent.LAMP_NEAR, SpeechCarControlEvent.LAMP_LOCATION, SpeechCarControlEvent.LAMP_FAR, SpeechCarControlEvent.LAMP_HEADGROUP, SpeechCarControlEvent.LAMP_INNER, SpeechCarControlEvent.BRAKE_WARN, SpeechCarControlEvent.ATWS_STATE, SpeechCarControlEvent.OLED_STATE, SpeechCarControlEvent.LIGHTMEHOME_STATE, SpeechCarControlEvent.LOCK_DRIVE_AUTIO, SpeechCarControlEvent.LOCK_PARKING_AUTIO, SpeechCarControlEvent.LOCK_STATE, SpeechCarControlEvent.TRUNK_STATE, SpeechCarControlEvent.TRUNK_OPENNER_STATE, SpeechCarControlEvent.MODE_WELCOME_STATE, SpeechCarControlEvent.BELT_ELECTRIC_STATE, SpeechCarControlEvent.BELT_REARSEAT_STATE, SpeechCarControlEvent.LOCK_UNLOCK_RESPONSE, SpeechCarControlEvent.DOOR_STATE, SpeechCarControlEvent.WINDOW_STATE, SpeechCarControlEvent.SEAT_DIRECTION, SpeechCarControlEvent.SEAT_ERROR, SpeechCarControlEvent.SEAT_POSITION, SpeechCarControlEvent.PASSENGER_SEAT_POSITION, SpeechCarControlEvent.SEAT_WELCOME_MODE_STATE, SpeechCarControlEvent.DRIVE_MODE, SpeechCarControlEvent.STEER_EPS, SpeechCarControlEvent.ICM_VOLUME_WARN, SpeechCarControlEvent.SPEED_LIMIT_STATE, SpeechCarControlEvent.SPEED_LIMIT_VALUE, SpeechCarControlEvent.METER_A, SpeechCarControlEvent.METER_B, SpeechCarControlEvent.METER_TOTAL, SpeechCarControlEvent.METER_LAST_CHARGE, SpeechCarControlEvent.METER_LAST_START, SpeechCarControlEvent.FRONT_COLLISION_STATE, SpeechCarControlEvent.METER_LIMIT_INTEL, SpeechCarControlEvent.LANE_CHANGE_ASSIST_STATE, SpeechCarControlEvent.WARN_SIDE_REVER_STATE, SpeechCarControlEvent.WARN_LANE_DEPARTURE_STATE, SpeechCarControlEvent.WARN_BLIND_AREA_STATE, SpeechCarControlEvent.WARN_RADAR_SLOW_STATE, SpeechCarControlEvent.RECYCLE_LV, SpeechCarControlEvent.SHIFT_STATE, SpeechCarControlEvent.SEAT_MAIN_HUMAN_STATE, SpeechCarControlEvent.SEAT_PASSENGER_HUMAN_STATE, SpeechCarControlEvent.ROTATION_STEER, SpeechCarControlEvent.SPEED_CAR, SpeechCarControlEvent.ICM_STATE, SpeechCarControlEvent.IG_STATE, SpeechCarControlEvent.ICM_WIND_MODE, SpeechCarControlEvent.ICM_DRIVER_TEMP, SpeechCarControlEvent.ICM_WIND_LV, SpeechCarControlEvent.IS_CAR_TRIP, SpeechCarControlEvent.GET_WIPER_LEVEL, SpeechCarControlEvent.GET_TIRE_PRESSURE, SpeechCarControlEvent.GET_TIRE_PRESSURE_WARNINGS, SpeechCarControlEvent.ACC_STATE, SpeechCarControlEvent.LCC_STATE, SpeechCarControlEvent.WINDOW_LOCK_STATE, SpeechCarControlEvent.GET_STATUS_LOW_SOC, SpeechCarControlEvent.GET_CRUISE_CONTROL_STATUS};
    }
}
