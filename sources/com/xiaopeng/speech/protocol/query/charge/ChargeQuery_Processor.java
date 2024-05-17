package com.xiaopeng.speech.protocol.query.charge;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QueryChargeEvent;
/* loaded from: classes23.dex */
public class ChargeQuery_Processor implements IQueryProcessor {
    private ChargeQuery mTarget;

    public ChargeQuery_Processor(ChargeQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1573444385:
                if (event.equals(QueryChargeEvent.IS_HAS_CHARGING_ORDER)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -1304630877:
                if (event.equals(QueryChargeEvent.GET_ENDURANCE_MODE_STATUS)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -1051995197:
                if (event.equals(QueryChargeEvent.GET_RESTART_STATUS)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -909786873:
                if (event.equals(QueryChargeEvent.CHARGE_GET_TRUNK_POWER_STATUS)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -649353844:
                if (event.equals(QueryChargeEvent.IS_SUPPORT_OPEN_PORT)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 133650785:
                if (event.equals(QueryChargeEvent.GET_LIMITS_ADJUST_MAX)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 133651023:
                if (event.equals(QueryChargeEvent.GET_LIMITS_ADJUST_MIN)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 146588932:
                if (event.equals(QueryChargeEvent.CHARGE_HAS_SUN_ROOF)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 208008097:
                if (event.equals(QueryChargeEvent.IS_SUPPORT_SMART_MODE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 317374728:
                if (event.equals(QueryChargeEvent.CHARGE_HAS_CAR_REFRIGERATOR)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 420332312:
                if (event.equals(QueryChargeEvent.POWER_CHARGE_STATUS)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 665776654:
                if (event.equals(QueryChargeEvent.CHARGE_DISCHARGE_LIMIT_MAX_VALUE)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 1164694924:
                if (event.equals(QueryChargeEvent.GET_STOP_STATUS)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1352058264:
                if (event.equals(QueryChargeEvent.GET_PAGE_OPEN_STATUS)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 1438255228:
                if (event.equals(QueryChargeEvent.CHARGE_DISCHARGE_LIMIT_MIN_VALUE)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 1499862672:
                if (event.equals(QueryChargeEvent.GET_START_STATUS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1865070220:
                if (event.equals(QueryChargeEvent.IS_SUPPORT_LIMITS_ADJUST)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1890968681:
                if (event.equals(QueryChargeEvent.CHARGE_TRUNK_POWER_STATUS_OPEN)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case 2074342788:
                if (event.equals(QueryChargeEvent.IS_CHARGE_READY_PAGE)) {
                    c = '\t';
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
                return Boolean.valueOf(this.mTarget.isSupportOpenPort(event, data));
            case 1:
                return Integer.valueOf(this.mTarget.getStartStatus(event, data));
            case 2:
                return Integer.valueOf(this.mTarget.getStopStatus(event, data));
            case 3:
                return Integer.valueOf(this.mTarget.getRestartStatus(event, data));
            case 4:
                return Boolean.valueOf(this.mTarget.isSupportLimitsAdjust(event, data));
            case 5:
                return Integer.valueOf(this.mTarget.getLimitsAdjustMin(event, data));
            case 6:
                return Integer.valueOf(this.mTarget.getLimitsAdjustMax(event, data));
            case 7:
                return Boolean.valueOf(this.mTarget.isSupportSmartMode(event, data));
            case '\b':
                return Boolean.valueOf(this.mTarget.isHasChargingOrder(event, data));
            case '\t':
                return Boolean.valueOf(this.mTarget.isChargeReadyPage(event, data));
            case '\n':
                return Integer.valueOf(this.mTarget.getChargeStatus(event, data));
            case 11:
                return Integer.valueOf(this.mTarget.getPageOpenStatus(event, data));
            case '\f':
                return Boolean.valueOf(this.mTarget.hasCarRefrigerator(event, data));
            case '\r':
                return Boolean.valueOf(this.mTarget.hasSunRoof(event, data));
            case 14:
                return Integer.valueOf(this.mTarget.getTrunkPowerStatus(event, data));
            case 15:
                return Integer.valueOf(this.mTarget.getMinDischargeLimit(event, data));
            case 16:
                return Integer.valueOf(this.mTarget.getMaxDischargeLimit(event, data));
            case 17:
                return Integer.valueOf(this.mTarget.getTrunkPowerStatusForOpen(event, data));
            case 18:
                return Boolean.valueOf(this.mTarget.isSupportEnduranceMode(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryChargeEvent.IS_SUPPORT_OPEN_PORT, QueryChargeEvent.GET_START_STATUS, QueryChargeEvent.GET_STOP_STATUS, QueryChargeEvent.GET_RESTART_STATUS, QueryChargeEvent.IS_SUPPORT_LIMITS_ADJUST, QueryChargeEvent.GET_LIMITS_ADJUST_MIN, QueryChargeEvent.GET_LIMITS_ADJUST_MAX, QueryChargeEvent.IS_SUPPORT_SMART_MODE, QueryChargeEvent.IS_HAS_CHARGING_ORDER, QueryChargeEvent.IS_CHARGE_READY_PAGE, QueryChargeEvent.POWER_CHARGE_STATUS, QueryChargeEvent.GET_PAGE_OPEN_STATUS, QueryChargeEvent.CHARGE_HAS_CAR_REFRIGERATOR, QueryChargeEvent.CHARGE_HAS_SUN_ROOF, QueryChargeEvent.CHARGE_GET_TRUNK_POWER_STATUS, QueryChargeEvent.CHARGE_DISCHARGE_LIMIT_MIN_VALUE, QueryChargeEvent.CHARGE_DISCHARGE_LIMIT_MAX_VALUE, QueryChargeEvent.CHARGE_TRUNK_POWER_STATUS_OPEN, QueryChargeEvent.GET_ENDURANCE_MODE_STATUS};
    }
}
