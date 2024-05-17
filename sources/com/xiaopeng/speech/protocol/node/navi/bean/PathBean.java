package com.xiaopeng.speech.protocol.node.navi.bean;

import android.text.TextUtils;
import com.xiaopeng.speech.common.util.DontProguardClass;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
@DontProguardClass
/* loaded from: classes23.dex */
public class PathBean {
    private PoiBean destPoint;
    private int distance;
    private int naviType;
    private String rawAmapJson;
    private int routeSelectRef;
    private PoiBean startPoint;
    private int strategy;
    private int time;
    private int toll;
    private ArrayList<ViaBean> viaPoints;

    public static PathBean fromJson(String fromJson) {
        if (TextUtils.isEmpty(fromJson)) {
            return null;
        }
        try {
            JSONObject data = new JSONObject(fromJson);
            JSONObject pathBeanObj = data.optJSONObject("pathBean");
            if (pathBeanObj != null) {
                PathBean pathBean = new PathBean();
                pathBean.startPoint = PoiBean.fromJson(pathBeanObj.optString("startPoint"));
                pathBean.destPoint = PoiBean.fromJson(pathBeanObj.optString("destPoint"));
                pathBean.strategy = pathBeanObj.optInt("strategy");
                pathBean.distance = pathBeanObj.optInt("distance");
                pathBean.time = pathBeanObj.optInt("time");
                pathBean.toll = pathBeanObj.optInt(NaviPreferenceBean.TOLL);
                pathBean.rawAmapJson = pathBeanObj.optString("rawAmapJson");
                pathBean.naviType = pathBeanObj.optInt("naviType");
                pathBean.routeSelectRef = pathBeanObj.optInt("routeSelectRef");
                JSONArray viaPointsArray = pathBeanObj.optJSONArray("viaPoints");
                if (viaPointsArray != null && viaPointsArray.length() > 0) {
                    pathBean.viaPoints = new ArrayList<>(viaPointsArray.length());
                    for (int i = 0; i < viaPointsArray.length(); i++) {
                        JSONObject viaObj = viaPointsArray.getJSONObject(i);
                        ViaBean viaBean = new ViaBean();
                        viaBean.setPointInfo(PoiBean.fromJson(viaObj.optString("pointInfo")));
                        viaBean.setViaType(viaObj.optInt("viaType"));
                        pathBean.viaPoints.add(viaBean);
                    }
                }
                return pathBean;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PoiBean getStartPoint() {
        return this.startPoint;
    }

    public void setStartPoint(PoiBean startPoint) {
        this.startPoint = startPoint;
    }

    public PoiBean getDestPoint() {
        return this.destPoint;
    }

    public void setDestPoint(PoiBean destPoint) {
        this.destPoint = destPoint;
    }

    public ArrayList<ViaBean> getViaPoints() {
        return this.viaPoints;
    }

    public void setViaPoints(ArrayList<ViaBean> viaPoints) {
        this.viaPoints = viaPoints;
    }

    public int getStrategy() {
        return this.strategy;
    }

    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }

    public int getDistance() {
        return this.distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getToll() {
        return this.toll;
    }

    public void setToll(int toll) {
        this.toll = toll;
    }

    public String getRawAmapJson() {
        return this.rawAmapJson;
    }

    public void setRawAmapJson(String rawAmapJson) {
        this.rawAmapJson = rawAmapJson;
    }

    public String toString() {
        return "PathBean{destPoint=" + this.destPoint + ", viaPoints=" + this.viaPoints + ", strategy=" + this.strategy + ", distance=" + this.distance + ", time=" + this.time + ", toll=" + this.toll + ", naviType=" + this.naviType + ", routeSelectRef=" + this.routeSelectRef + ", rawAmapJson='" + this.rawAmapJson + "'}";
    }

    public int getNaviType() {
        return this.naviType;
    }

    public void setNaviType(int naviType) {
        this.naviType = naviType;
    }

    public int getRouteSelectRef() {
        return this.routeSelectRef;
    }

    public void setRouteSelectRef(int routeSelectRef) {
        this.routeSelectRef = routeSelectRef;
    }
}
