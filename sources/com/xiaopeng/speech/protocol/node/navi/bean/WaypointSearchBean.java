package com.xiaopeng.speech.protocol.node.navi.bean;

import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class WaypointSearchBean {
    private String destinationName;
    private String destinationType;
    private boolean isSetAsDestination;

    public static WaypointSearchBean fromJson(String data) {
        WaypointSearchBean waypointBean = new WaypointSearchBean();
        try {
            JSONObject jsonObject = new JSONObject(data);
            waypointBean.destinationName = jsonObject.optString("destinationName");
            waypointBean.destinationType = jsonObject.optString("destinationType");
            waypointBean.isSetAsDestination = jsonObject.optInt("isSetAsDestination", 0) == 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return waypointBean;
    }

    public String getDestinationName() {
        return this.destinationName;
    }

    public String getDestinationType() {
        return this.destinationType;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public boolean isSetAsDestination() {
        return this.isSetAsDestination;
    }

    public void setSetAsDestination(boolean setAsDestination) {
        this.isSetAsDestination = setAsDestination;
    }

    public String toString() {
        return "WaypointSearchBean{destinationName='" + this.destinationName + "', destinationType='" + this.destinationType + "', isSetAsDestination=" + this.isSetAsDestination + '}';
    }
}
