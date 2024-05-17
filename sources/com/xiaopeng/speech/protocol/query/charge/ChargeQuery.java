package com.xiaopeng.speech.protocol.query.charge;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.QueryChargeEvent;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class ChargeQuery extends SpeechQuery<IChargeCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.IS_SUPPORT_OPEN_PORT)
    public boolean isSupportOpenPort(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).isSupportOpenPort();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.GET_START_STATUS)
    public int getStartStatus(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getStartStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.GET_STOP_STATUS)
    public int getStopStatus(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getStopStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.GET_RESTART_STATUS)
    public int getRestartStatus(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getRestartStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.IS_SUPPORT_LIMITS_ADJUST)
    public boolean isSupportLimitsAdjust(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).isSupportLimitsAdjust();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.GET_LIMITS_ADJUST_MIN)
    public int getLimitsAdjustMin(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getLimitsAdjustMin();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.GET_LIMITS_ADJUST_MAX)
    public int getLimitsAdjustMax(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getLimitsAdjustMax();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.IS_SUPPORT_SMART_MODE)
    public boolean isSupportSmartMode(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).isSupportSmartMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.IS_HAS_CHARGING_ORDER)
    public boolean isHasChargingOrder(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).isHasChargingOrder();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.IS_CHARGE_READY_PAGE)
    public boolean isChargeReadyPage(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).isChargeReadyPage();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.POWER_CHARGE_STATUS)
    public int getChargeStatus(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getChargeStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.GET_PAGE_OPEN_STATUS)
    public int getPageOpenStatus(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getGuiPageOpenState(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.CHARGE_HAS_CAR_REFRIGERATOR)
    public boolean hasCarRefrigerator(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).hasCarRefrigerator();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.CHARGE_HAS_SUN_ROOF)
    public boolean hasSunRoof(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).hasSunRoof();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.CHARGE_GET_TRUNK_POWER_STATUS)
    public int getTrunkPowerStatus(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getTrunkPowerStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.CHARGE_DISCHARGE_LIMIT_MIN_VALUE)
    public int getMinDischargeLimit(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getMinDischargeLimit();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.CHARGE_DISCHARGE_LIMIT_MAX_VALUE)
    public int getMaxDischargeLimit(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getMaxDischargeLimit();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.CHARGE_TRUNK_POWER_STATUS_OPEN)
    public int getTrunkPowerStatusForOpen(String event, String data) {
        return ((IChargeCaller) this.mQueryCaller).getTrunkPowerStatusForOpen();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryChargeEvent.GET_ENDURANCE_MODE_STATUS)
    public boolean isSupportEnduranceMode(String event, String data) {
        int mode = 1;
        try {
            JSONObject jsonObject = new JSONObject(data);
            mode = jsonObject.optInt("mode", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((IChargeCaller) this.mQueryCaller).isSupportEnduranceMode(mode);
    }
}
