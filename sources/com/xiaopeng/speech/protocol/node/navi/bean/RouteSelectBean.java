package com.xiaopeng.speech.protocol.node.navi.bean;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class RouteSelectBean {
    public int batteryStatus;
    public String batteryStatusTips;
    public String remainDistance;
    public long remainDistanceValue;
    public String routeLeftDistance;
    public long routeLeftDistanceValue;
    public String routeTypeName;
    public String routeTypeNo;
    public String totalTimeLine1;
    public long totalTimeLine1Value;
    public String totalTimeLine2;
    public String trafficCost;
    public String trafficSignal;

    public static RouteSelectBean fromJson(String data) {
        RouteSelectBean routeSelectBean = new RouteSelectBean();
        try {
            JSONObject json = new JSONObject(data);
            routeSelectBean.routeTypeNo = json.optString("routeTypeNo");
            routeSelectBean.routeTypeName = json.optString("routeTypeName");
            routeSelectBean.trafficSignal = json.optString("trafficSignal");
            routeSelectBean.trafficCost = json.optString("trafficCost");
            routeSelectBean.routeLeftDistance = json.optString("routeLeftDistance");
            routeSelectBean.totalTimeLine1 = json.optString("totalTimeLine1");
            routeSelectBean.totalTimeLine2 = json.optString("totalTimeLine2");
            routeSelectBean.remainDistance = json.optString("remainDistance");
            routeSelectBean.batteryStatus = json.optInt("batteryStatus");
            routeSelectBean.batteryStatusTips = json.optString("batteryStatusTips");
            routeSelectBean.routeLeftDistanceValue = json.optLong("routeLeftDistanceValue");
            routeSelectBean.totalTimeLine1Value = json.optLong("totalTimeLine1Value");
            routeSelectBean.remainDistanceValue = json.optLong("remainDistanceValue");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routeSelectBean;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("routeTypeNo", this.routeTypeNo);
        json.put("routeTypeName", this.routeTypeName);
        json.put("trafficSignal", this.trafficSignal);
        json.put("trafficCost", this.trafficCost);
        json.put("routeLeftDistance", this.routeLeftDistance);
        json.put("totalTimeLine1", this.totalTimeLine1);
        json.put("totalTimeLine2", this.totalTimeLine2);
        json.put("remainDistance", this.remainDistance);
        json.put("batteryStatus", this.batteryStatus);
        json.put("batteryStatusTips", this.batteryStatusTips);
        json.put("routeLeftDistanceValue", this.routeLeftDistanceValue);
        json.put("totalTimeLine1Value", this.totalTimeLine1Value);
        json.put("remainDistanceValue", this.remainDistanceValue);
        return json;
    }

    public String toString() {
        return "RouteSelectBean{routeTypeName='" + this.routeTypeName + "', routeTypeNo='" + this.routeTypeNo + "', trafficSignal='" + this.trafficSignal + "', trafficCost='" + this.trafficCost + "', routeLeftDistance='" + this.routeLeftDistance + "', totalTimeLine1='" + this.totalTimeLine1 + "', totalTimeLine2='" + this.totalTimeLine2 + "', remainDistance='" + this.remainDistance + "', batteryStatus='" + this.batteryStatus + "', batteryStatusTips='" + this.batteryStatusTips + "', routeLeftDistanceValue='" + this.routeLeftDistanceValue + "', totalTimeLine1Value='" + this.totalTimeLine1Value + "', remainDistanceValue='" + this.remainDistanceValue + "'}";
    }
}
