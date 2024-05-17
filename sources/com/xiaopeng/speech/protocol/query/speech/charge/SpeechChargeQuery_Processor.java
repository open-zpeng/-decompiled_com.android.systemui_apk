package com.xiaopeng.speech.protocol.query.speech.charge;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechChargeEvent;
/* loaded from: classes23.dex */
public class SpeechChargeQuery_Processor implements IQueryProcessor {
    private SpeechChargeQuery mTarget;

    public SpeechChargeQuery_Processor(SpeechChargeQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2112164824:
                if (event.equals(SpeechChargeEvent.CHARGE_MILEAGE_NUM)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1562235108:
                if (event.equals(SpeechChargeEvent.CHARGE_GET_GUN_LOCK_ST)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -1431793793:
                if (event.equals(SpeechChargeEvent.CHARGE_ADD_ELECTRI)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1056966063:
                if (event.equals(SpeechChargeEvent.CHARGE_DC_CHARGING_CURRENT)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -1033894871:
                if (event.equals(SpeechChargeEvent.CHARGE_DC_CHARGING_VOLT)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case -954113537:
                if (event.equals(SpeechChargeEvent.CHARGE_WLTP_MILEAGE_NUM)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -927637931:
                if (event.equals(SpeechChargeEvent.CHARGE_AVER_VEH_CONSUME)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -735246937:
                if (event.equals(SpeechChargeEvent.GET_BATTERY_COOL_STATUS)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -685146427:
                if (event.equals(SpeechChargeEvent.CHARGE_CHARGING_ERROR)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -672173746:
                if (event.equals(SpeechChargeEvent.CHARGE_CHARGING_STATE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -460071101:
                if (event.equals(SpeechChargeEvent.CHARGE_COLD_WARN_TIP)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -370089957:
                if (event.equals(SpeechChargeEvent.CHARGE_AC_INPUT_STATE)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 105277287:
                if (event.equals(SpeechChargeEvent.GET_AVER_CONSUME_100KM)) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 185907206:
                if (event.equals(SpeechChargeEvent.CHARGE_CLTC_MILEAGE_NUM)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 299529912:
                if (event.equals(SpeechChargeEvent.CHARGE_HVAC_CONSUME)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 391995010:
                if (event.equals(SpeechChargeEvent.CHARGE_GET_LIMIT_SOC)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 497707660:
                if (event.equals(SpeechChargeEvent.CHARGE_AC_CHARGING_VOLT)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 610188398:
                if (event.equals(SpeechChargeEvent.CHARGE_ELECTRIC_PERCENT)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 855595168:
                if (event.equals(SpeechChargeEvent.CHARGE_CHARGING_GUN_STATE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 959650120:
                if (event.equals(SpeechChargeEvent.CHARGE_ENDURANCE_MILEAGE_MODE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 984976355:
                if (event.equals(SpeechChargeEvent.CHARGE_BATTERY_WARM_STATE)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 1026255738:
                if (event.equals(SpeechChargeEvent.CHARGE_CHARGING_MAX_SOC)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 1416974162:
                if (event.equals(SpeechChargeEvent.CHARGE_SOH)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case 1476449550:
                if (event.equals(SpeechChargeEvent.CHARGE_AC_CHARGING_CURRENT)) {
                    c = '\f';
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
                return Integer.valueOf(this.mTarget.getAddedElectricity(event, data));
            case 1:
                return Integer.valueOf(this.mTarget.getChargingError(event, data));
            case 2:
                return Integer.valueOf(this.mTarget.getChargingGunStatus(event, data));
            case 3:
                return Integer.valueOf(this.mTarget.getChargeStatus(event, data));
            case 4:
                return Integer.valueOf(this.mTarget.getMileageNumber(event, data));
            case 5:
                return Integer.valueOf(this.mTarget.getWltpMileageNumber(event, data));
            case 6:
                return Integer.valueOf(this.mTarget.getCltcMileageNumber(event, data));
            case 7:
                return Integer.valueOf(this.mTarget.getEnduranceMileageMode(event, data));
            case '\b':
                return Double.valueOf(this.mTarget.getElectricityPercent(event, data));
            case '\t':
                return Boolean.valueOf(this.mTarget.getColdWarningTips(event, data));
            case '\n':
                return Double.valueOf(this.mTarget.getHvacConsume(event, data));
            case 11:
                return Boolean.valueOf(this.mTarget.getBatteryWarmingStatus(event, data));
            case '\f':
                return Double.valueOf(this.mTarget.getACChargingCurrent(event, data));
            case '\r':
                return Double.valueOf(this.mTarget.getACChargingVolt(event, data));
            case 14:
                return Double.valueOf(this.mTarget.getDCChargingCurrent(event, data));
            case 15:
                return Double.valueOf(this.mTarget.getDCChargingVolt(event, data));
            case 16:
                return Boolean.valueOf(this.mTarget.getACInputStatus(event, data));
            case 17:
                return this.mTarget.getAverageVehConsume(event, data);
            case 18:
                return this.mTarget.getChargingMaxSoc(event, data);
            case 19:
                return Double.valueOf(this.mTarget.getSOH(event, data));
            case 20:
                return Boolean.valueOf(this.mTarget.getChargeGunLockSt(event, data));
            case 21:
                return Integer.valueOf(this.mTarget.getChargeLimitSoc(event, data));
            case 22:
                return Boolean.valueOf(this.mTarget.getBatteryCoolStatus(event, data));
            case 23:
                return this.mTarget.getAverageVehConsume100km(event, data);
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechChargeEvent.CHARGE_ADD_ELECTRI, SpeechChargeEvent.CHARGE_CHARGING_ERROR, SpeechChargeEvent.CHARGE_CHARGING_GUN_STATE, SpeechChargeEvent.CHARGE_CHARGING_STATE, SpeechChargeEvent.CHARGE_MILEAGE_NUM, SpeechChargeEvent.CHARGE_WLTP_MILEAGE_NUM, SpeechChargeEvent.CHARGE_CLTC_MILEAGE_NUM, SpeechChargeEvent.CHARGE_ENDURANCE_MILEAGE_MODE, SpeechChargeEvent.CHARGE_ELECTRIC_PERCENT, SpeechChargeEvent.CHARGE_COLD_WARN_TIP, SpeechChargeEvent.CHARGE_HVAC_CONSUME, SpeechChargeEvent.CHARGE_BATTERY_WARM_STATE, SpeechChargeEvent.CHARGE_AC_CHARGING_CURRENT, SpeechChargeEvent.CHARGE_AC_CHARGING_VOLT, SpeechChargeEvent.CHARGE_DC_CHARGING_CURRENT, SpeechChargeEvent.CHARGE_DC_CHARGING_VOLT, SpeechChargeEvent.CHARGE_AC_INPUT_STATE, SpeechChargeEvent.CHARGE_AVER_VEH_CONSUME, SpeechChargeEvent.CHARGE_CHARGING_MAX_SOC, SpeechChargeEvent.CHARGE_SOH, SpeechChargeEvent.CHARGE_GET_GUN_LOCK_ST, SpeechChargeEvent.CHARGE_GET_LIMIT_SOC, SpeechChargeEvent.GET_BATTERY_COOL_STATUS, SpeechChargeEvent.GET_AVER_CONSUME_100KM};
    }
}
