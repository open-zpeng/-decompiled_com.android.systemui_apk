package com.xiaopeng.speech.protocol.node.navi;

import com.xiaopeng.speech.INodeListener;
import com.xiaopeng.speech.protocol.node.navi.bean.AddressBean;
import com.xiaopeng.speech.protocol.node.navi.bean.NaviPreferenceBean;
import com.xiaopeng.speech.protocol.node.navi.bean.NearbySearchBean;
import com.xiaopeng.speech.protocol.node.navi.bean.PathBean;
import com.xiaopeng.speech.protocol.node.navi.bean.PoiBean;
import com.xiaopeng.speech.protocol.node.navi.bean.PoiSearchBean;
import com.xiaopeng.speech.protocol.node.navi.bean.SelectParkingBean;
import com.xiaopeng.speech.protocol.node.navi.bean.SelectRouteBean;
import com.xiaopeng.speech.protocol.node.navi.bean.StartNaviBean;
import com.xiaopeng.speech.protocol.node.navi.bean.WaypointSearchBean;
/* loaded from: classes23.dex */
public interface NaviListener extends INodeListener {
    void onAddressGet(AddressBean addressBean);

    void onAddressSet(AddressBean addressBean, PoiBean poiBean);

    void onCloseTraffic();

    void onConfirmCancel();

    void onConfirmOk();

    void onControlChargeClose();

    void onControlChargeOpen();

    void onControlClose();

    void onControlCloseRibbonMap();

    void onControlCloseSmallMap();

    void onControlDisPlayCar();

    void onControlDisPlayNorth();

    void onControlDisplay3D();

    void onControlFavoriteClose();

    void onControlFavoriteOpen();

    void onControlHistory();

    void onControlOpenRibbonMap();

    void onControlOpenSmallMap();

    void onControlOverviewClose();

    void onControlOverviewOpen();

    void onControlRoadAhead();

    void onControlRoadAheadOff();

    void onControlSecurityRemind();

    void onControlSecurityRemindOff();

    void onControlSettingsOpen();

    void onControlSmartScale();

    void onControlSmartScaleOff();

    void onControlSpeechDetail();

    void onControlSpeechEye();

    void onControlSpeechEyeOff();

    void onControlSpeechGeneral();

    void onControlSpeechSimple();

    void onControlSpeechSuperSimple();

    void onControlStart(StartNaviBean startNaviBean);

    void onControlVolOff(boolean z, int i);

    void onControlVolOn(boolean z, int i);

    void onControlWaypointStart(PathBean pathBean);

    void onDataControlDisplay3dTts();

    void onDataControlDisplayCarTts();

    void onDataControlDisplayNorthTts();

    void onDriveAdvoidTrafficControl();

    void onDriveAvoidCharge();

    void onDriveAvoidChargeOff();

    void onDriveAvoidCongestion();

    void onDriveAvoidCongestionOff();

    void onDriveAvoidControls();

    void onDriveAvoidControlsOff();

    void onDriveHighwayFirst();

    void onDriveHighwayFirstOff();

    void onDriveHighwayNo();

    void onDriveHighwayNoOff();

    void onDriveRadarRoute();

    void onDriveRadarRouteOff();

    void onGetSettingsInfo();

    void onMainRoad();

    void onMapEnterFindPath();

    void onMapExitFindPath();

    void onMapOverview();

    void onMapZoomIn();

    void onMapZoomOut();

    void onMapZoominMax();

    void onMapZoomoutMin();

    boolean onNavigatingGet();

    void onNearbySearch(NearbySearchBean nearbySearchBean);

    void onOpenTraffic();

    void onParkingSelect(SelectParkingBean selectParkingBean);

    void onPoiSearch(PoiSearchBean poiSearchBean);

    void onRouteNearbySearch(NearbySearchBean nearbySearchBean);

    void onRouteSelect(SelectRouteBean selectRouteBean);

    void onSearchClose();

    void onSelectParkingCount(SelectParkingBean selectParkingBean);

    void onSelectRouteCount(SelectRouteBean selectRouteBean);

    void onSideRoad();

    void onWaypointSearch(WaypointSearchBean waypointSearchBean);

    default void onControlParkRecommendOn() {
    }

    default void onControlParkRecommendOff() {
    }

    default void onSetScaleLevel(int level) {
    }

    default void onSetOritentionMetre(double metre, int oritention) {
    }

    default void onAlertsPreferenceSet(NaviPreferenceBean pref) {
    }

    default void onAvoidRouteSet(NaviPreferenceBean pref) {
    }

    default void onMapShowSet(NaviPreferenceBean pref) {
    }

    default void onAutoRerouteBetterRoute() {
    }

    default void onAutoRerouteAskFirst() {
    }

    default void onAutoRerouteNever() {
    }

    default void onPoiDetailsFavoriteAdd() {
    }

    default void onPoiDetailsFavoriteDel() {
    }

    default void onControlSettingsClose() {
    }

    default void onControlHistoryClose() {
    }

    default void onListItemSelected(int index) {
    }

    default void onControlChargeServiceOpen(String stationId) {
    }

    default void onControlGaodeAccountPageOpen(int pageId) {
    }
}
