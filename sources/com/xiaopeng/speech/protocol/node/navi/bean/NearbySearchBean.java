package com.xiaopeng.speech.protocol.node.navi.bean;

import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.protocol.utils.CarTypeUtils;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class NearbySearchBean {
    private static final String TAG = "NearbySearchBean";
    private String destination;
    private String destinationAddr;
    private String destinationType;

    public static NearbySearchBean fromJson(String data) {
        NearbySearchBean poiSearchBean = new NearbySearchBean();
        try {
            JSONObject json = new JSONObject(data);
            String destinationPrefix = json.optString("destinationPrefix", json.optString("终点修饰"));
            boolean isOverseasCarType = CarTypeUtils.isOverseasCarType();
            LogUtils.d(TAG, "fromJson, isOverseasCarType: " + isOverseasCarType);
            if (!isOverseasCarType) {
                poiSearchBean.destination = destinationPrefix + json.optString("destination", json.optString("终点目标"));
            } else {
                poiSearchBean.destinationAddr = destinationPrefix;
                poiSearchBean.destination = json.optString("destination", json.optString("终点目标"));
            }
            poiSearchBean.destinationType = json.optString("destinationType", json.optString("终点类型"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return poiSearchBean;
    }

    public String getDestination() {
        return this.destination;
    }

    public String getDestinationAddr() {
        return this.destinationAddr;
    }

    public String getDestinationType() {
        return this.destinationType;
    }

    public String toString() {
        return "NearbySearchBean{destination='" + this.destination + "', destinationAddr='" + this.destinationAddr + "', destinationType='" + this.destinationType + "'}";
    }
}
