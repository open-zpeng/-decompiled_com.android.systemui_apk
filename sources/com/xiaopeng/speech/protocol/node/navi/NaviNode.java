package com.xiaopeng.speech.protocol.node.navi;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.actorapi.ResultActor;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.jarvisproto.FeedUIEvent;
import com.xiaopeng.speech.protocol.bean.FeedListUIValue;
import com.xiaopeng.speech.protocol.event.ContextEvent;
import com.xiaopeng.speech.protocol.event.NaviEvent;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import com.xiaopeng.speech.protocol.node.navi.bean.AddressBean;
import com.xiaopeng.speech.protocol.node.navi.bean.NaviPreferenceBean;
import com.xiaopeng.speech.protocol.node.navi.bean.NearbySearchBean;
import com.xiaopeng.speech.protocol.node.navi.bean.PathBean;
import com.xiaopeng.speech.protocol.node.navi.bean.PoiBean;
import com.xiaopeng.speech.protocol.node.navi.bean.PoiSearchBean;
import com.xiaopeng.speech.protocol.node.navi.bean.RouteSelectBean;
import com.xiaopeng.speech.protocol.node.navi.bean.SelectParkingBean;
import com.xiaopeng.speech.protocol.node.navi.bean.SelectRouteBean;
import com.xiaopeng.speech.protocol.node.navi.bean.StartNaviBean;
import com.xiaopeng.speech.protocol.node.navi.bean.WaypointSearchBean;
import com.xiaopeng.speech.speechwidget.ContentWidget;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.speechwidget.TextWidget;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class NaviNode extends SpeechNode<NaviListener> {
    private static final String ACTIVE_VOICE_TASK = "主动语音";
    public static final String ALL_ROUTE_WIDGET_ID = "-Route-2";
    public static final String BASE_ROUTE_WIDGET_ID = "-Route-1";
    private static final String COMMAND_SPLIT = "#";
    private static final String KEY_MODE = "mode";
    private static final String KEY_PULLUP_NAVI = "pullUpNavi";
    private static final String OFFLINE_SKILL = "离线命令词";
    private static final String SELECT_PARKING_INTENT = "停车场主动语音";
    private static final String SELECT_ROUTE_INTENT = "路线主动语音";
    private static final int STOP_DIALOG_OPT_FORCE = 0;
    private static final int STOP_DIALOG_OPT_OPTIONAL = 1;
    private boolean mAddressPendingRoute = false;

    @SpeechAnnotation(event = NaviEvent.CONTROL_CLOSE)
    public void onControlClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlClose();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.MAP_ZOOMIN)
    public void onMapZoomIn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onMapZoomIn();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.MAP_ZOOMOUT)
    public void onMapZoomOut(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onMapZoomOut();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.ROAD_INFO_OPEN)
    public void onOpenTraffic(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onOpenTraffic();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.ROAD_INFO_CLOSE)
    public void onCloseTraffic(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onCloseTraffic();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_OVERVIEW_OPEN)
    public void onControlOverviewOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlOverviewOpen();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_OVERVIEW_CLOSE)
    public void onControlOverviewClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlOverviewClose();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.MAP_OVERVIEW)
    public void onMapOverview(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onMapOverview();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_FAVORITE_OPEN)
    public void onControlFavoriteOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlFavoriteOpen();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SETTINGS_OPEN)
    public void onControlSettingsOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSettingsOpen();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_CHARGE_OPEN)
    public void onControlChargeOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlChargeOpen();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_CHARGE_CLOSE)
    public void onControlChargeClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlChargeClose();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_AVOID_CONGESTION)
    public void onDriveAvoidCongestion(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveAvoidCongestion();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_AVOID_CONGESTION_OFF)
    public void onDriveAvoidCongestionOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveAvoidCongestionOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_AVOID_CHARGE)
    public void onDriveAvoidCharge(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveAvoidCharge();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_AVOID_CHARGE_OFF)
    public void onDriveAvoidChargeOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveAvoidChargeOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_HIGHWAY_FIRST_OFF)
    public void onDriveHighwayFirstOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveHighwayFirstOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_AVOID_CONTROLS)
    public void onDriveAvoidControls(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveAvoidControls();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_AVOID_CONTROLS_OFF)
    public void onDriveAvoidControlsOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveAvoidControlsOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_RADAR_ROUTE)
    public void onDriveRadarRoute(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveRadarRoute();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_RADAR_ROUTE_OFF)
    public void onDriveRadarRouteOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveRadarRouteOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SPEECH_SUPER_SIMPLE)
    public void onControlSpeechSuperSimple(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSpeechSuperSimple();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SPEECH_GENERAL)
    public void onControlSpeechGeneral(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSpeechGeneral();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SPEECH_EYE)
    public void onControlSpeechEye(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSpeechEye();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SPEECH_EYE_OFF)
    public void onControlSpeechEyeOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSpeechEyeOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SMART_SCALE)
    public void onControlSmartScale(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSmartScale();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SMART_SCALE_OFF)
    public void onControlSmartScaleOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSmartScaleOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SECURITY_REMIND)
    public void onControlSecurityRemind(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSecurityRemind();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_ROAD_AHEAD)
    public void onControlRoadAhead(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlRoadAhead();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_HIGHWAY_NO)
    public void onDriveHighwayNo(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveHighwayNo();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_HIGHWAY_NO_OFF)
    public void onDriveHighwayNoOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveHighwayNoOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.DRIVE_HIGHWAY_FIRST)
    public void onDriveHighwayFirst(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveHighwayFirst();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.NAVIGATING_GET)
    public void onNavigatingGet(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onNavigatingGet();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.POI_SEARCH)
    public void onPoiSearch(String event, String data) {
        PoiSearchBean poiSearchBean = PoiSearchBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onPoiSearch(poiSearchBean);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SECURITY_REMIND_OFF)
    public void onControlSecurityRemindOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSecurityRemindOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.MAP_ENTER_FIND_PATH)
    public void onMapEnterFindPath(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onMapEnterFindPath();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.MAP_EXIT_FIND_PATH)
    public void onMapExitFindPath(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onMapExitFindPath();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.SEARCH_CLOSE)
    public void onSearchClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onSearchClose();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.MAIN_ROAD)
    public void onMainRoad(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onMainRoad();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.SIDE_ROAD)
    public void onSideRoad(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onSideRoad();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_FAVORITE_CLOSE)
    public void onControlFavoriteClose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlFavoriteClose();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_ROAD_AHEAD_OFF)
    public void onControlRoadAheadOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlRoadAheadOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.MAP_ZOOMIN_MAX)
    public void onMapZoominMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onMapZoominMax();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.MAP_ZOOMOUT_MIN)
    public void onMapZoomoutMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onMapZoomoutMin();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.NEARBY_SEARCH)
    public void onNearbySearch(String event, String data) {
        NearbySearchBean searchBean = NearbySearchBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onNearbySearch(searchBean);
            }
        }
    }

    public void postPoiResult(String searchKey, List<PoiBean> poiBeanList) {
        postPoiResult(NaviEvent.POI_SEARCH, searchKey, poiBeanList);
    }

    public void postNearbyResult(String searchKey, List<PoiBean> poiBeanList) {
        postPoiResult(NaviEvent.NEARBY_SEARCH, searchKey, poiBeanList);
    }

    public void postSearchPoiResult(String event, String searchKey, List<PoiBean> poiBeanList) {
        postPoiResult(event, searchKey, poiBeanList);
    }

    public void postAddressPosResult(String searchKey, List<PoiBean> poiBeanList) {
        postPoiResult(NaviEvent.POI_SEARCH, searchKey, poiBeanList);
    }

    public void postWaypointsFull(String searchKey) {
        postWaypointSearchResult(searchKey, null, true, true);
    }

    public void postWaypointsNotExitRoute(String searchKey) {
        postWaypointSearchResult(searchKey, null, false, false);
    }

    public void postWaypointSearchResult(String searchKey, List<PoiBean> poiBeanList) {
        postWaypointSearchResult(searchKey, poiBeanList, true, false);
    }

    private void postWaypointSearchResult(String searchKey, List<PoiBean> poiBeanList, boolean isExistRoute, boolean isWaypointListFull) {
        ListWidget listWidget = new ListWidget();
        listWidget.setTitle(searchKey);
        listWidget.setExist(isExistRoute);
        listWidget.setExtraType("navi");
        listWidget.addContent("isWaypointListFull", String.valueOf(isWaypointListFull));
        if (poiBeanList != null) {
            for (PoiBean poiBean : poiBeanList) {
                ContentWidget contentWidget = new ContentWidget();
                contentWidget.setTitle(poiBean.getName());
                contentWidget.setSubTitle(poiBean.getAddress());
                try {
                    contentWidget.addExtra("navi", poiBean.toJson().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listWidget.addContentWidget(contentWidget);
            }
        }
        SpeechClient.instance().getActorBridge().send(new ResultActor(NaviEvent.WAYPOINT_SEARCH).setResult(listWidget));
    }

    private void postPoiResult(String event, String searchKey, List<PoiBean> poiBeanList) {
        ListWidget listWidget = new ListWidget();
        listWidget.setTitle(searchKey);
        listWidget.setExtraType("navi");
        if (poiBeanList != null) {
            for (PoiBean poiBean : poiBeanList) {
                ContentWidget contentWidget = new ContentWidget();
                contentWidget.setTitle(poiBean.getName());
                contentWidget.setSubTitle(poiBean.getAddress());
                try {
                    contentWidget.addExtra("navi", poiBean.toJson().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listWidget.addContentWidget(contentWidget);
            }
        }
        SpeechClient.instance().getActorBridge().send(new ResultActor(event).setResult(listWidget));
    }

    @SpeechAnnotation(event = NaviEvent.ADDRESS_GET)
    public void onAddressGet(String event, String data) {
        AddressBean addressBean = AddressBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onAddressGet(addressBean);
            }
        }
        this.mAddressPendingRoute = false;
    }

    public void postAddressGetResult(boolean isExist, boolean hasBigData, PoiBean poiBean) {
        TextWidget textWidget = new TextWidget();
        textWidget.setText(isExist ? "success" : "fail");
        textWidget.addContent("hasBigData", hasBigData ? OOBEEvent.STRING_TRUE : OOBEEvent.STRING_FALSE);
        if (poiBean != null) {
            try {
                textWidget.addContent("address", poiBean.getAddress());
                textWidget.addExtra("navi", poiBean.toJson().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SpeechClient.instance().getActorBridge().send(new ResultActor(NaviEvent.ADDRESS_GET).setResult(textWidget));
    }

    @SpeechAnnotation(event = NaviEvent.ADDRESS_PENDING_ROUTE)
    public void onAddressPendingRoute(String event, String data) {
        LogUtils.i(this, "pending route");
        this.mAddressPendingRoute = true;
    }

    @SpeechAnnotation(event = NaviEvent.ADDRESS_SET)
    public void onAddressSet(String event, String data) {
        AddressBean addressBean = new AddressBean();
        PoiBean poiBean = null;
        String pathPref = null;
        String type = null;
        int naviType = 0;
        int routeSelectRef = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            addressBean.setAddressType(jsonObject.optString("addressType"));
            String posJson = jsonObject.optString("poi");
            poiBean = PoiBean.fromJson(posJson);
            pathPref = jsonObject.optString("pref");
            type = jsonObject.optString(VuiConstants.ELEMENT_TYPE);
            naviType = jsonObject.optInt("naviType");
            routeSelectRef = jsonObject.optInt("routeSelectRef");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onAddressSet(addressBean, poiBean);
            }
        }
        if (this.mAddressPendingRoute) {
            StartNaviBean startNaviBean = new StartNaviBean(poiBean, pathPref, type);
            startNaviBean.setNaviType(naviType);
            startNaviBean.setRouteSelectRef(routeSelectRef);
            doControlStart(startNaviBean);
        }
        this.mAddressPendingRoute = false;
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_START)
    public void onControlStart(String event, String data) {
        PoiBean poiBean = null;
        String pathPref = null;
        String type = null;
        int naviType = 0;
        int routeSelectRef = 0;
        boolean fullSceneSwitch = true;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String posJson = jsonObject.optString("poi");
            poiBean = PoiBean.fromJson(posJson);
            pathPref = jsonObject.optString("pref");
            type = jsonObject.optString(VuiConstants.ELEMENT_TYPE);
            naviType = jsonObject.optInt("naviType");
            routeSelectRef = jsonObject.optInt("routeSelectRef");
            if (jsonObject.has("fullSceneSwitch")) {
                fullSceneSwitch = jsonObject.getBoolean("fullSceneSwitch");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StartNaviBean startNaviBean = new StartNaviBean(poiBean, pathPref, type);
        startNaviBean.setNaviType(naviType);
        startNaviBean.setRouteSelectRef(routeSelectRef);
        startNaviBean.setFullSceneSwitch(fullSceneSwitch);
        LogUtils.d("NaviNode", "StartNaviBean = %s", startNaviBean.toString());
        doControlStart(startNaviBean);
    }

    private void doControlStart(StartNaviBean startNaviBean) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlStart(startNaviBean);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SPEECH_SIMPLE)
    public void onControlSpeechSimple(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSpeechSimple();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SPEECH_DETAIL)
    public void onControlSpeechDetail(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSpeechDetail();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_DISPLAY_NORTH)
    public void onControlDisPlayNorth(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlDisPlayNorth();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_DISPLAY_CAR)
    public void onControlDisPlayCar(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlDisPlayCar();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_DISPLAY_3D)
    public void onControlDisplay3D(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlDisplay3D();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_VOL_ON)
    public void onControlVolOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean pullUpNavi = true;
        int mode = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            pullUpNavi = jsonObject.optBoolean(KEY_PULLUP_NAVI, false);
            if (jsonObject.has(KEY_MODE)) {
                mode = jsonObject.optInt(KEY_MODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlVolOn(pullUpNavi, mode);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_VOL_OFF)
    public void onControlVolOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean pullUpNavi = true;
        int mode = 0;
        try {
            JSONObject jsonObject = new JSONObject(data);
            pullUpNavi = jsonObject.optBoolean(KEY_PULLUP_NAVI, false);
            if (jsonObject.has(KEY_MODE)) {
                mode = jsonObject.optInt(KEY_MODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlVolOff(pullUpNavi, mode);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.ROUTE_NEARBY_SEARCH)
    public void onRouteNearbySearch(String event, String data) {
        NearbySearchBean searchBean = NearbySearchBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onRouteNearbySearch(searchBean);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.PARKING_SELECT)
    public void onParkingSelect(String event, String data) {
        SelectParkingBean selectParkingBean = SelectParkingBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onParkingSelect(selectParkingBean);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONFIRM_OK)
    public void onConfirmOk(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onConfirmOk();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONFIRM_CANCEL)
    public void onConfirmCancel(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onConfirmCancel();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.ROUTE_SELECT)
    public void onRouteSelect(String event, String data) {
        SelectRouteBean selectRouteBean = SelectRouteBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onRouteSelect(selectRouteBean);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.SELECT_PARKING_COUNT)
    public void onSelectParkingCount(String event, String data) {
        SelectParkingBean selectParkingBean = SelectParkingBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onSelectParkingCount(selectParkingBean);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.SELECT_ROUTE_COUNT)
    public void onSelectRouteCount(String event, String data) {
        SelectRouteBean selectRouteBean = SelectRouteBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onSelectRouteCount(selectRouteBean);
            }
        }
    }

    public void onDataControlDisplay3dTts(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDataControlDisplay3dTts();
            }
        }
    }

    public void onDataControlDisplayCarTts(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDataControlDisplayCarTts();
            }
        }
    }

    public void onDataControlDisplayNorthTts(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDataControlDisplayNorthTts();
            }
        }
    }

    public void onDriveAdvoidTrafficControl(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onDriveAdvoidTrafficControl();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.WAYPOINT_SEARCH)
    public void onWaypointSearch(String event, String data) {
        WaypointSearchBean waypointSearch = WaypointSearchBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onWaypointSearch(waypointSearch);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_WAYPOINT_START)
    public void onControlWaypointStart(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        PathBean pathBean = PathBean.fromJson(data);
        LogUtils.d("NaviNode, pathBean =%s", pathBean == null ? "data is null" : pathBean.toString());
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlWaypointStart(pathBean);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_OPEN_SMALL_MAP)
    public void onControlOpenSmallMap(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlOpenSmallMap();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_CLOSE_SMALL_MAP)
    public void onControlCloseSmallMap(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlCloseSmallMap();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_OPEN_RIBBON_MAP)
    public void onControlOpenRibbonMap(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlOpenRibbonMap();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_CLOSE_RIBBON_MAP)
    public void onControlCloseRibbonMap(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlCloseRibbonMap();
            }
        }
    }

    public void selectParking(String tts) {
        SpeechClient.instance().getWakeupEngine().stopDialog();
        String slots = "";
        try {
            slots = new JSONObject().put("tts", tts).put("isLocalSkill", true).put("intent", SELECT_PARKING_INTENT).put("isAsrModeOffline", false).put("command", "native://navi.select.parking.count#command://navi.parking.select#command://navi.confirm.cancel").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SpeechClient.instance().getAgent().triggerIntent(OFFLINE_SKILL, ACTIVE_VOICE_TASK, SELECT_PARKING_INTENT, slots);
    }

    @Deprecated
    public void selectRoute(String tts) {
        SpeechClient.instance().getWakeupEngine().stopDialog();
        String slots = "";
        try {
            slots = new JSONObject().put("tts", tts).put("isLocalSkill", true).put("intent", SELECT_ROUTE_INTENT).put("isAsrModeOffline", false).put("command", "native://navi.select.route.count#command://navi.route.select#command://navi.confirm.cancel").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SpeechClient.instance().getAgent().triggerIntent(OFFLINE_SKILL, ACTIVE_VOICE_TASK, SELECT_ROUTE_INTENT, slots);
    }

    public void selectRoute(List<RouteSelectBean> list) {
        JSONObject data = new JSONObject();
        try {
            JSONArray jsArray = new JSONArray();
            if (list != null && list.size() > 0) {
                for (RouteSelectBean routeSelectBean : list) {
                    jsArray.put(routeSelectBean.toJson());
                }
            }
            data.put("route_list", jsArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SpeechClient.instance().getAgent().triggerEvent(FeedUIEvent.LIST_ROUT_UPLOAD, data.toString());
    }

    public void updateRouteSelect(List<RouteSelectBean> list) {
        ListWidget listWidget = new ListWidget();
        listWidget.setTitle(FeedListUIValue.TYPE_ROUTE);
        listWidget.setExtraType(ListWidget.EXTRA_TYPE_NAVI_ROUTE);
        if (list != null && list.size() > 0) {
            for (RouteSelectBean routeSelectBean : list) {
                ContentWidget contentWidget = new ContentWidget();
                contentWidget.setTitle(routeSelectBean.totalTimeLine1);
                contentWidget.setSubTitle(routeSelectBean.routeTypeName);
                try {
                    contentWidget.addExtra(ListWidget.EXTRA_TYPE_NAVI_ROUTE, routeSelectBean.toJson().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listWidget.addContentWidget(contentWidget);
            }
        }
        LogUtils.i("updateRouteSelect", "updateRouteSelect:" + listWidget.toString());
        SpeechClient.instance().getAgent().sendEvent(ContextEvent.WIDGET_LIST, listWidget.toString());
    }

    private SpeechWidget getRouteSelectWidget(List<RouteSelectBean> list) {
        ListWidget listWidget = new ListWidget();
        listWidget.setTitle(FeedListUIValue.TYPE_ROUTE);
        listWidget.setExtraType(ListWidget.EXTRA_TYPE_NAVI_ROUTE);
        if (list != null && list.size() > 0) {
            for (RouteSelectBean routeSelectBean : list) {
                ContentWidget contentWidget = new ContentWidget();
                contentWidget.setTitle(routeSelectBean.totalTimeLine1);
                contentWidget.setSubTitle(routeSelectBean.routeTypeName);
                try {
                    contentWidget.addExtra(ListWidget.EXTRA_TYPE_NAVI_ROUTE, routeSelectBean.toJson().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listWidget.addContentWidget(contentWidget);
            }
        }
        return listWidget;
    }

    public void stopSpeechDialog() {
        SpeechClient.instance().getWakeupEngine().stopDialog();
    }

    public void stopSpeechDialog(int stopOption) {
        LogUtils.i(this, "stopDialog option: " + stopOption);
        if (stopOption == 0) {
            SpeechClient.instance().getWakeupEngine().stopDialog();
            return;
        }
        try {
            JSONObject data = new JSONObject();
            data.put("from", "Navigation");
            SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.SCRIPT_QUIT, data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startSpeechDialog() {
        try {
            JSONObject data = new JSONObject();
            data.put("from", "Navigation");
            SpeechClient.instance().getAgent().triggerEvent(FeedUIEvent.SCRIPT_QUIT, data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void directNavigation() {
        try {
            JSONObject data = new JSONObject();
            data.put("from", "Navigation");
            SpeechClient.instance().getAgent().triggerEvent(FeedUIEvent.SCRIPT_QUIT, data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_HISTORY)
    public void onControlHistory(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlHistory();
            }
        }
    }

    public void syncRoute(List<RouteSelectBean> list, String id, boolean isBaseInfo) {
        JSONObject data = new JSONObject();
        try {
            JSONArray jsArray = new JSONArray();
            if (list != null && list.size() > 0) {
                for (RouteSelectBean routeSelectBean : list) {
                    jsArray.put(routeSelectBean.toJson());
                }
            }
            data.put("route_list", jsArray.toString());
            data.put("localStory", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isBaseInfo) {
            SpeechClient.instance().getAgent().triggerEvent(FeedUIEvent.LIST_ROUT_UPLOAD, data.toString());
        }
        syncRouteToInfoFlow(list, id, isBaseInfo);
    }

    public void syncRoute(List<RouteSelectBean> list, String id, boolean isBaseInfo, String poiInfo) {
        JSONObject data = new JSONObject();
        try {
            JSONArray jsArray = new JSONArray();
            if (list != null && list.size() > 0) {
                for (RouteSelectBean routeSelectBean : list) {
                    jsArray.put(routeSelectBean.toJson());
                }
            }
            data.put("route_list", jsArray.toString());
            data.put("localStory", id);
            data.put(VuiConstants.PROPS_POIINFO, poiInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isBaseInfo) {
            SpeechClient.instance().getAgent().triggerEvent(FeedUIEvent.LIST_ROUT_UPLOAD, data.toString());
        }
        syncRouteToInfoFlow(list, id, isBaseInfo);
    }

    public void syncRoute(List<RouteSelectBean> list, String id, boolean isBaseInfo, String poiInfo, boolean isChargeRoute) {
        JSONObject data = new JSONObject();
        try {
            JSONArray jsArray = new JSONArray();
            if (list != null && list.size() > 0) {
                for (RouteSelectBean routeSelectBean : list) {
                    jsArray.put(routeSelectBean.toJson());
                }
            }
            data.put("route_list", jsArray.toString());
            data.put("localStory", id);
            data.put(VuiConstants.PROPS_POIINFO, poiInfo);
            data.put("isChargeRoute", isChargeRoute);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isBaseInfo) {
            SpeechClient.instance().getAgent().triggerEvent(FeedUIEvent.LIST_ROUT_UPLOAD, data.toString());
        }
        syncRouteToInfoFlow(list, id, isBaseInfo);
    }

    public void syncRouteToInfoFlow(List<RouteSelectBean> list, String id, boolean isBaseInfo) {
        ListWidget listWidget = new ListWidget();
        listWidget.setTitle(FeedListUIValue.TYPE_ROUTE);
        listWidget.setExtraType(ListWidget.EXTRA_TYPE_NAVI_ROUTE);
        if (isBaseInfo) {
            listWidget.setWidgetId(id + BASE_ROUTE_WIDGET_ID);
        } else {
            listWidget.setWidgetId(id + ALL_ROUTE_WIDGET_ID);
        }
        if (list != null && list.size() > 0) {
            for (RouteSelectBean routeSelectBean : list) {
                ContentWidget contentWidget = new ContentWidget();
                contentWidget.setTitle(routeSelectBean.totalTimeLine1);
                contentWidget.setSubTitle(routeSelectBean.routeTypeName);
                try {
                    contentWidget.addExtra(ListWidget.EXTRA_TYPE_NAVI_ROUTE, routeSelectBean.toJson().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listWidget.addContentWidget(contentWidget);
            }
        }
        LogUtils.i("NaviNode", "syncRouteToInfoFlow:" + listWidget.toString());
        SpeechClient.instance().getAgent().sendEvent(ContextEvent.WIDGET_LIST, listWidget.toString());
    }

    @SpeechAnnotation(event = NaviEvent.SETTINGS_INFO_GET)
    public void onGetSettingsInfo(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onGetSettingsInfo();
            }
        }
    }

    public void postSettingsInfo(String info) {
        SpeechClient.instance().getActorBridge().send(new ResultActor(NaviEvent.SETTINGS_INFO_GET).setResult(info));
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_PARK_RECOMMEND_ON)
    public void onControlParkRecommendOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlParkRecommendOn();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_PARK_RECOMMEND_OFF)
    public void onControlParkRecommendOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlParkRecommendOff();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.SCALE_LEVEL_SET)
    public void onSetScaleLevel(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null && !TextUtils.isEmpty(data)) {
            try {
                JSONObject object = new JSONObject(data);
                if (object.has("level")) {
                    int level = object.getInt("level");
                    for (Object obj : listenerList) {
                        ((NaviListener) obj).onSetScaleLevel(level);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.MOVE_NAV_METRE_SET)
    public void onSetOritentionMetre(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null && !TextUtils.isEmpty(data)) {
            try {
                JSONObject object = new JSONObject(data);
                if (object.has("metre") && object.has("oritention")) {
                    double metre = object.getDouble("metre");
                    int oritention = object.getInt("oritention");
                    for (Object obj : listenerList) {
                        ((NaviListener) obj).onSetOritentionMetre(metre, oritention);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.ALERTS_PREFERENCE_SET)
    public void onAlertsPreferenceSet(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null && !TextUtils.isEmpty(data)) {
            NaviPreferenceBean pref = NaviPreferenceBean.fromJson(data);
            for (Object obj : listenerList) {
                ((NaviListener) obj).onAlertsPreferenceSet(pref);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.AVOID_ROUTE_SET)
    public void onAvoidRouteSet(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null && !TextUtils.isEmpty(data)) {
            NaviPreferenceBean pref = NaviPreferenceBean.fromJson(data);
            for (Object obj : listenerList) {
                ((NaviListener) obj).onAvoidRouteSet(pref);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.AUTO_REROUTE_BETTER_ROUTE)
    public void onAutoRerouteBetterRoute(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onAutoRerouteBetterRoute();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.AUTO_REROUTE_ASK_FIRST)
    public void onAutoRerouteAskFirst(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onAutoRerouteAskFirst();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.AUTO_REROUTE_NEVER)
    public void onAutoRerouteNever(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onAutoRerouteNever();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.MAP_SHOW_SET)
    public void onMapShowSet(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null && !TextUtils.isEmpty(data)) {
            NaviPreferenceBean pref = NaviPreferenceBean.fromJson(data);
            for (Object obj : listenerList) {
                ((NaviListener) obj).onMapShowSet(pref);
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_POI_DETAILS_FAVORITE_ADD)
    public void onPoiDetailsFavoriteAdd(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onPoiDetailsFavoriteAdd();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_POI_DETAILS_FAVORITE_DEL)
    public void onPoiDetailsFavoriteDel(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onPoiDetailsFavoriteDel();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_SETTINGS_CLOSE)
    public void onControlSettingsCLose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlSettingsClose();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_HISTORY_CLOSE)
    public void onControlHistoryCLose(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((NaviListener) obj).onControlHistoryClose();
            }
        }
    }

    @SpeechAnnotation(event = NaviEvent.LIST_ITEM_SELECTED)
    public void onListItemSelected(String event, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("index")) {
                int index = jsonObject.optInt("index");
                Object[] listenerList = this.mListenerList.collectCallbacks();
                if (listenerList != null) {
                    for (Object obj : listenerList) {
                        ((NaviListener) obj).onListItemSelected(index);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_GAODE_ACCOUNT_BING_PAGE_OPEN)
    public void onControlGaodeAccountPageOpen(String event, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("pageId")) {
                int pageId = jsonObject.optInt("pageId");
                Object[] listenerList = this.mListenerList.collectCallbacks();
                if (listenerList != null) {
                    for (Object obj : listenerList) {
                        ((NaviListener) obj).onControlGaodeAccountPageOpen(pageId);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SpeechAnnotation(event = NaviEvent.CONTROL_CHARGE_SERVICE_OPEN)
    public void onControlChargeServiceOpen(String event, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("stationId")) {
                String stationId = jsonObject.optString("stationId");
                Object[] listenerList = this.mListenerList.collectCallbacks();
                if (listenerList != null) {
                    for (Object obj : listenerList) {
                        ((NaviListener) obj).onControlChargeServiceOpen(stationId);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
