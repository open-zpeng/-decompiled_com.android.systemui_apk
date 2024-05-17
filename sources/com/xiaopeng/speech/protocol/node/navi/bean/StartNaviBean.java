package com.xiaopeng.speech.protocol.node.navi.bean;

import com.xiaopeng.speech.common.util.DontProguardClass;
import com.xiaopeng.speech.protocol.utils.CarTypeUtils;
@DontProguardClass
/* loaded from: classes23.dex */
public class StartNaviBean {
    public static final int PATH_PREF_AVOID_CONGESTION = 4;
    public static final int PATH_PREF_AVOID_FEE_CONGESTION = 8;
    public static final int PATH_PREF_DEFAULT = 0;
    public static final int PATH_PREF_HIGH_SPEED = 2;
    public static final int PATH_PREF_LOW_FEE = 1;
    public static final int PATH_PREF_NO_HIGH_SPEED = 6;
    public static final int PATH_PREF_NO_HIGH_SPEED_FEE_CONGESTION = 9;
    public static final int PATH_PREF_NO_HIGH_SPEED_NO_FEE = 7;
    private boolean fullSceneSwitch = false;
    private int naviType;
    private int pathRef;
    private PoiBean poiBean;
    private int routeSelectRef;
    private NaviType type;

    /* loaded from: classes23.dex */
    public enum NaviType {
        POI,
        WAYPOINT
    }

    public boolean isFullSceneSwitch() {
        return this.fullSceneSwitch;
    }

    public void setFullSceneSwitch(boolean fullSceneSwitch) {
        this.fullSceneSwitch = fullSceneSwitch;
    }

    public StartNaviBean() {
    }

    public StartNaviBean(PoiBean poiBean, String pathRef, String type) {
        this.poiBean = poiBean;
        this.pathRef = convertPathRef(pathRef);
        if ("waypoint".equals(type)) {
            this.type = NaviType.WAYPOINT;
        } else {
            this.type = NaviType.POI;
        }
    }

    public PoiBean getPoiBean() {
        return this.poiBean;
    }

    public void setPoiBean(PoiBean poiBean) {
        this.poiBean = poiBean;
    }

    public int getPathRef() {
        return this.pathRef;
    }

    public void setPathRef(int pathRef) {
        this.pathRef = pathRef;
    }

    public NaviType getType() {
        return this.type;
    }

    public void setType(NaviType type) {
        this.type = type;
    }

    public String toString() {
        return "StartNaviBean{poiBean=" + this.poiBean + ", naviType=" + this.naviType + ", routeSelectRef=" + this.routeSelectRef + ", pathRef='" + this.pathRef + "'}";
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

    private int convertPathRef(String ref) {
        if (CarTypeUtils.isOverseasCarType()) {
            return NaviPreferenceBean.prefNameToPrefId(ref);
        }
        char c = 65535;
        switch (ref.hashCode()) {
            case -482414100:
                if (ref.equals("躲避收费和拥堵")) {
                    c = 5;
                    break;
                }
                break;
            case -154630622:
                if (ref.equals("不走高速躲避收费和拥堵")) {
                    c = 6;
                    break;
                }
                break;
            case 35696354:
                if (ref.equals("费用少")) {
                    c = 0;
                    break;
                }
                break;
            case 631315594:
                if (ref.equals("不走高速")) {
                    c = 3;
                    break;
                }
                break;
            case 1124620989:
                if (ref.equals("躲避拥堵")) {
                    c = 2;
                    break;
                }
                break;
            case 1217019831:
                if (ref.equals("高速优先")) {
                    c = 1;
                    break;
                }
                break;
            case 2036169147:
                if (ref.equals("不走高速且避免收费")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 4;
            case 3:
                return 6;
            case 4:
                return 7;
            case 5:
                return 8;
            case 6:
                return 9;
            default:
                return 0;
        }
    }
}
