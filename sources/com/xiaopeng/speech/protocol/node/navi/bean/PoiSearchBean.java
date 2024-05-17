package com.xiaopeng.speech.protocol.node.navi.bean;

import android.text.TextUtils;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.protocol.utils.CarTypeUtils;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class PoiSearchBean {
    private static final String TAG = "PoiSearchBean";
    private String destination;
    private String destinationAddr;
    private String destinationType;
    private String pathPref;
    private String travelMode;

    public static PoiSearchBean fromJson(String data) {
        PoiSearchBean poiSearchBean = new PoiSearchBean();
        try {
            JSONObject json = new JSONObject(data);
            String destinationPrefix = json.optString("destinationPrefix");
            String destinationTarget = json.optString("destinationTarget");
            String destinationName = json.optString("destinationName");
            boolean isOverseasCarType = CarTypeUtils.isOverseasCarType();
            LogUtils.d(TAG, "fromJson, isOverseasCarType: " + isOverseasCarType);
            if (!isOverseasCarType) {
                poiSearchBean.destination = destinationPrefix;
                StringBuilder sb = new StringBuilder();
                sb.append(poiSearchBean.destination);
                sb.append(TextUtils.isEmpty(destinationTarget) ? destinationName : destinationTarget);
                poiSearchBean.destination = sb.toString();
            } else {
                poiSearchBean.destinationAddr = destinationPrefix;
                poiSearchBean.destination = TextUtils.isEmpty(destinationTarget) ? destinationName : destinationTarget;
            }
            poiSearchBean.travelMode = json.optString("travelMode");
            poiSearchBean.destinationType = json.optString("destinationType");
            poiSearchBean.pathPref = json.optString("pref");
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

    public String getTravelMode() {
        return this.travelMode;
    }

    public String getDestinationType() {
        return this.destinationType;
    }

    public String getPathPref() {
        return this.pathPref;
    }

    public String toString() {
        return "PoiSearchBean{destination='" + this.destination + "', destinationAddr='" + this.destinationAddr + "', travelMode='" + this.travelMode + "', destinationType='" + this.destinationType + "', pathPref='" + this.pathPref + "'}";
    }
}
