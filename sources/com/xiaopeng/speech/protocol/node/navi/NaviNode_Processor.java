package com.xiaopeng.speech.protocol.node.navi;

import com.alibaba.fastjson.parser.JSONLexer;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.NaviEvent;
import kotlin.text.Typography;
/* loaded from: classes23.dex */
public class NaviNode_Processor implements ICommandProcessor {
    private NaviNode mTarget;

    public NaviNode_Processor(NaviNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2136970098:
                if (event.equals(NaviEvent.CONTROL_CLOSE)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -2132799004:
                if (event.equals(NaviEvent.DRIVE_RADAR_ROUTE)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -2121968904:
                if (event.equals(NaviEvent.CONTROL_START)) {
                    c = '0';
                    break;
                }
                c = 65535;
                break;
            case -1987448085:
                if (event.equals(NaviEvent.SEARCH_CLOSE)) {
                    c = '%';
                    break;
                }
                c = 65535;
                break;
            case -1920967676:
                if (event.equals(NaviEvent.CONTROL_CHARGE_SERVICE_OPEN)) {
                    c = 'W';
                    break;
                }
                c = 65535;
                break;
            case -1909621251:
                if (event.equals(NaviEvent.AUTO_REROUTE_ASK_FIRST)) {
                    c = 'N';
                    break;
                }
                c = 65535;
                break;
            case -1907058252:
                if (event.equals(NaviEvent.CONTROL_CHARGE_CLOSE)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -1775506535:
                if (event.equals(NaviEvent.MOVE_NAV_METRE_SET)) {
                    c = 'J';
                    break;
                }
                c = 65535;
                break;
            case -1667606279:
                if (event.equals(NaviEvent.MAP_ZOOMIN_MAX)) {
                    c = '*';
                    break;
                }
                c = 65535;
                break;
            case -1667539314:
                if (event.equals(NaviEvent.AUTO_REROUTE_NEVER)) {
                    c = 'O';
                    break;
                }
                c = 65535;
                break;
            case -1582358593:
                if (event.equals(NaviEvent.LIST_ITEM_SELECTED)) {
                    c = 'U';
                    break;
                }
                c = 65535;
                break;
            case -1518898323:
                if (event.equals(NaviEvent.CONTROL_OVERVIEW_CLOSE)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1475394262:
                if (event.equals(NaviEvent.DRIVE_AVOID_CHARGE)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -1467020709:
                if (event.equals(NaviEvent.CONTROL_OPEN_RIBBON_MAP)) {
                    c = 'C';
                    break;
                }
                c = 65535;
                break;
            case -1380219901:
                if (event.equals(NaviEvent.MAP_ZOOMIN)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1361583363:
                if (event.equals(NaviEvent.SELECT_ROUTE_COUNT)) {
                    c = Typography.greater;
                    break;
                }
                c = 65535;
                break;
            case -1316967297:
                if (event.equals(NaviEvent.CONTROL_SETTINGS_OPEN)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1274994428:
                if (event.equals(NaviEvent.CONTROL_VOL_ON)) {
                    c = '6';
                    break;
                }
                c = 65535;
                break;
            case -1169768037:
                if (event.equals(NaviEvent.NAVIGATING_GET)) {
                    c = ' ';
                    break;
                }
                c = 65535;
                break;
            case -1141056686:
                if (event.equals(NaviEvent.DRIVE_HIGHWAY_FIRST)) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case -1121697225:
                if (event.equals(NaviEvent.ADDRESS_GET)) {
                    c = '-';
                    break;
                }
                c = 65535;
                break;
            case -982285267:
                if (event.equals(NaviEvent.DRIVE_AVOID_CONTROLS_OFF)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -977315249:
                if (event.equals(NaviEvent.CONTROL_SPEECH_EYE)) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case -957206192:
                if (event.equals(NaviEvent.CONTROL_SPEECH_EYE_OFF)) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case -873701781:
                if (event.equals(NaviEvent.PARKING_SELECT)) {
                    c = '9';
                    break;
                }
                c = 65535;
                break;
            case -870121750:
                if (event.equals(NaviEvent.CONTROL_VOL_OFF)) {
                    c = '7';
                    break;
                }
                c = 65535;
                break;
            case -793405345:
                if (event.equals(NaviEvent.DRIVE_HIGHWAY_NO)) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case -780474528:
                if (event.equals(NaviEvent.DRIVE_HIGHWAY_NO_OFF)) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case -741372363:
                if (event.equals(NaviEvent.CONTROL_OVERVIEW_OPEN)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -735619553:
                if (event.equals(NaviEvent.ADDRESS_PENDING_ROUTE)) {
                    c = '.';
                    break;
                }
                c = 65535;
                break;
            case -682936661:
                if (event.equals(NaviEvent.DRIVE_AVOID_CHARGE_OFF)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case -629123846:
                if (event.equals(NaviEvent.ROUTE_SELECT)) {
                    c = Typography.less;
                    break;
                }
                c = 65535;
                break;
            case -585100656:
                if (event.equals(NaviEvent.WAYPOINT_SEARCH)) {
                    c = '?';
                    break;
                }
                c = 65535;
                break;
            case -573495894:
                if (event.equals(NaviEvent.CONTROL_HISTORY)) {
                    c = 'E';
                    break;
                }
                c = 65535;
                break;
            case -550413506:
                if (event.equals(NaviEvent.SIDE_ROAD)) {
                    c = '\'';
                    break;
                }
                c = 65535;
                break;
            case -483469262:
                if (event.equals(NaviEvent.CONTROL_PARK_RECOMMEND_OFF)) {
                    c = 'H';
                    break;
                }
                c = 65535;
                break;
            case -483373155:
                if (event.equals(NaviEvent.DRIVE_AVOID_CONGESTION)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -471273732:
                if (event.equals(NaviEvent.MAP_SHOW_SET)) {
                    c = 'P';
                    break;
                }
                c = 65535;
                break;
            case -463743293:
                if (event.equals(NaviEvent.CONFIRM_OK)) {
                    c = AccessibilityUtils.ENABLED_ACCESSIBILITY_SERVICES_SEPARATOR;
                    break;
                }
                c = 65535;
                break;
            case -432687108:
                if (event.equals(NaviEvent.CONTROL_SMART_SCALE_OFF)) {
                    c = JSONLexer.EOI;
                    break;
                }
                c = 65535;
                break;
            case -336305704:
                if (event.equals(NaviEvent.CONTROL_POI_DETAILS_FAVORITE_ADD)) {
                    c = 'Q';
                    break;
                }
                c = 65535;
                break;
            case -336302782:
                if (event.equals(NaviEvent.CONTROL_POI_DETAILS_FAVORITE_DEL)) {
                    c = 'R';
                    break;
                }
                c = 65535;
                break;
            case -287805857:
                if (event.equals(NaviEvent.CONTROL_WAYPOINT_START)) {
                    c = '@';
                    break;
                }
                c = 65535;
                break;
            case -257936759:
                if (event.equals(NaviEvent.CONTROL_SECURITY_REMIND)) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case -245623106:
                if (event.equals(NaviEvent.CONTROL_DISPLAY_CAR)) {
                    c = '4';
                    break;
                }
                c = 65535;
                break;
            case -60216351:
                if (event.equals(NaviEvent.CONFIRM_CANCEL)) {
                    c = ';';
                    break;
                }
                c = 65535;
                break;
            case 30697630:
                if (event.equals(NaviEvent.CONTROL_ROAD_AHEAD_OFF)) {
                    c = ')';
                    break;
                }
                c = 65535;
                break;
            case 162862128:
                if (event.equals(NaviEvent.MAP_ZOOMOUT)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 189975919:
                if (event.equals(NaviEvent.CONTROL_DISPLAY_NORTH)) {
                    c = '3';
                    break;
                }
                c = 65535;
                break;
            case 219354725:
                if (event.equals(NaviEvent.DRIVE_RADAR_ROUTE_OFF)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case 235759773:
                if (event.equals(NaviEvent.CONTROL_ROAD_AHEAD)) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case 255171078:
                if (event.equals(NaviEvent.CONTROL_SPEECH_GENERAL)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case 295754221:
                if (event.equals(NaviEvent.ROUTE_NEARBY_SEARCH)) {
                    c = '8';
                    break;
                }
                c = 65535;
                break;
            case 338160499:
                if (event.equals(NaviEvent.CONTROL_SPEECH_DETAIL)) {
                    c = '2';
                    break;
                }
                c = 65535;
                break;
            case 354485006:
                if (event.equals(NaviEvent.CONTROL_CHARGE_OPEN)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 398795396:
                if (event.equals(NaviEvent.MAP_OVERVIEW)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 431524135:
                if (event.equals(NaviEvent.CONTROL_SPEECH_SUPER_SIMPLE)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 560836717:
                if (event.equals(NaviEvent.ALERTS_PREFERENCE_SET)) {
                    c = 'K';
                    break;
                }
                c = 65535;
                break;
            case 592257019:
                if (event.equals(NaviEvent.SCALE_LEVEL_SET)) {
                    c = 'I';
                    break;
                }
                c = 65535;
                break;
            case 757845918:
                if (event.equals(NaviEvent.DRIVE_AVOID_CONGESTION_OFF)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 771097812:
                if (event.equals(NaviEvent.CONTROL_SPEECH_SIMPLE)) {
                    c = '1';
                    break;
                }
                c = 65535;
                break;
            case 784930555:
                if (event.equals(NaviEvent.CONTROL_SMART_SCALE)) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case 838195437:
                if (event.equals(NaviEvent.ROAD_INFO_CLOSE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 858683573:
                if (event.equals(NaviEvent.ROAD_INFO_OPEN)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 950634676:
                if (event.equals(NaviEvent.CONTROL_HISTORY_CLOSE)) {
                    c = 'T';
                    break;
                }
                c = 65535;
                break;
            case 1047011356:
                if (event.equals(NaviEvent.MAIN_ROAD)) {
                    c = Typography.amp;
                    break;
                }
                c = 65535;
                break;
            case 1082133391:
                if (event.equals(NaviEvent.ADDRESS_SET)) {
                    c = '/';
                    break;
                }
                c = 65535;
                break;
            case 1137052564:
                if (event.equals(NaviEvent.MAP_ZOOMOUT_MIN)) {
                    c = '+';
                    break;
                }
                c = 65535;
                break;
            case 1247853748:
                if (event.equals(NaviEvent.SETTINGS_INFO_GET)) {
                    c = 'F';
                    break;
                }
                c = 65535;
                break;
            case 1255072401:
                if (event.equals(NaviEvent.POI_SEARCH)) {
                    c = '!';
                    break;
                }
                c = 65535;
                break;
            case 1356046610:
                if (event.equals(NaviEvent.NEARBY_SEARCH)) {
                    c = ',';
                    break;
                }
                c = 65535;
                break;
            case 1631355563:
                if (event.equals(NaviEvent.AVOID_ROUTE_SET)) {
                    c = 'L';
                    break;
                }
                c = 65535;
                break;
            case 1632960358:
                if (event.equals(NaviEvent.CONTROL_CLOSE_SMALL_MAP)) {
                    c = 'B';
                    break;
                }
                c = 65535;
                break;
            case 1687314057:
                if (event.equals(NaviEvent.AUTO_REROUTE_BETTER_ROUTE)) {
                    c = 'M';
                    break;
                }
                c = 65535;
                break;
            case 1735961708:
                if (event.equals(NaviEvent.SELECT_PARKING_COUNT)) {
                    c = '=';
                    break;
                }
                c = 65535;
                break;
            case 1755806727:
                if (event.equals(NaviEvent.CONTROL_CLOSE_RIBBON_MAP)) {
                    c = 'D';
                    break;
                }
                c = 65535;
                break;
            case 1759009930:
                if (event.equals(NaviEvent.CONTROL_SECURITY_REMIND_OFF)) {
                    c = Typography.quote;
                    break;
                }
                c = 65535;
                break;
            case 1777247148:
                if (event.equals(NaviEvent.DRIVE_AVOID_CONTROLS)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case 1793190503:
                if (event.equals(NaviEvent.CONTROL_DISPLAY_3D)) {
                    c = '5';
                    break;
                }
                c = 65535;
                break;
            case 1832000151:
                if (event.equals(NaviEvent.CONTROL_SETTINGS_CLOSE)) {
                    c = 'S';
                    break;
                }
                c = 65535;
                break;
            case 1854152476:
                if (event.equals(NaviEvent.CONTROL_OPEN_SMALL_MAP)) {
                    c = 'A';
                    break;
                }
                c = 65535;
                break;
            case 1890678995:
                if (event.equals(NaviEvent.DRIVE_HIGHWAY_FIRST_OFF)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 1926343575:
                if (event.equals(NaviEvent.CONTROL_GAODE_ACCOUNT_BING_PAGE_OPEN)) {
                    c = 'V';
                    break;
                }
                c = 65535;
                break;
            case 1961191760:
                if (event.equals(NaviEvent.CONTROL_FAVORITE_CLOSE)) {
                    c = '(';
                    break;
                }
                c = 65535;
                break;
            case 1963423819:
                if (event.equals(NaviEvent.MAP_ENTER_FIND_PATH)) {
                    c = '#';
                    break;
                }
                c = 65535;
                break;
            case 2040031459:
                if (event.equals(NaviEvent.MAP_EXIT_FIND_PATH)) {
                    c = Typography.dollar;
                    break;
                }
                c = 65535;
                break;
            case 2062614204:
                if (event.equals(NaviEvent.CONTROL_PARK_RECOMMEND_ON)) {
                    c = 'G';
                    break;
                }
                c = 65535;
                break;
            case 2141835250:
                if (event.equals(NaviEvent.CONTROL_FAVORITE_OPEN)) {
                    c = '\b';
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
                this.mTarget.onControlClose(event, data);
                return;
            case 1:
                this.mTarget.onMapZoomIn(event, data);
                return;
            case 2:
                this.mTarget.onMapZoomOut(event, data);
                return;
            case 3:
                this.mTarget.onOpenTraffic(event, data);
                return;
            case 4:
                this.mTarget.onCloseTraffic(event, data);
                return;
            case 5:
                this.mTarget.onControlOverviewOpen(event, data);
                return;
            case 6:
                this.mTarget.onControlOverviewClose(event, data);
                return;
            case 7:
                this.mTarget.onMapOverview(event, data);
                return;
            case '\b':
                this.mTarget.onControlFavoriteOpen(event, data);
                return;
            case '\t':
                this.mTarget.onControlSettingsOpen(event, data);
                return;
            case '\n':
                this.mTarget.onControlChargeOpen(event, data);
                return;
            case 11:
                this.mTarget.onControlChargeClose(event, data);
                return;
            case '\f':
                this.mTarget.onDriveAvoidCongestion(event, data);
                return;
            case '\r':
                this.mTarget.onDriveAvoidCongestionOff(event, data);
                return;
            case 14:
                this.mTarget.onDriveAvoidCharge(event, data);
                return;
            case 15:
                this.mTarget.onDriveAvoidChargeOff(event, data);
                return;
            case 16:
                this.mTarget.onDriveHighwayFirstOff(event, data);
                return;
            case 17:
                this.mTarget.onDriveAvoidControls(event, data);
                return;
            case 18:
                this.mTarget.onDriveAvoidControlsOff(event, data);
                return;
            case 19:
                this.mTarget.onDriveRadarRoute(event, data);
                return;
            case 20:
                this.mTarget.onDriveRadarRouteOff(event, data);
                return;
            case 21:
                this.mTarget.onControlSpeechSuperSimple(event, data);
                return;
            case 22:
                this.mTarget.onControlSpeechGeneral(event, data);
                return;
            case 23:
                this.mTarget.onControlSpeechEye(event, data);
                return;
            case 24:
                this.mTarget.onControlSpeechEyeOff(event, data);
                return;
            case 25:
                this.mTarget.onControlSmartScale(event, data);
                return;
            case 26:
                this.mTarget.onControlSmartScaleOff(event, data);
                return;
            case 27:
                this.mTarget.onControlSecurityRemind(event, data);
                return;
            case 28:
                this.mTarget.onControlRoadAhead(event, data);
                return;
            case 29:
                this.mTarget.onDriveHighwayNo(event, data);
                return;
            case 30:
                this.mTarget.onDriveHighwayNoOff(event, data);
                return;
            case 31:
                this.mTarget.onDriveHighwayFirst(event, data);
                return;
            case ' ':
                this.mTarget.onNavigatingGet(event, data);
                return;
            case '!':
                this.mTarget.onPoiSearch(event, data);
                return;
            case '\"':
                this.mTarget.onControlSecurityRemindOff(event, data);
                return;
            case '#':
                this.mTarget.onMapEnterFindPath(event, data);
                return;
            case '$':
                this.mTarget.onMapExitFindPath(event, data);
                return;
            case '%':
                this.mTarget.onSearchClose(event, data);
                return;
            case '&':
                this.mTarget.onMainRoad(event, data);
                return;
            case '\'':
                this.mTarget.onSideRoad(event, data);
                return;
            case '(':
                this.mTarget.onControlFavoriteClose(event, data);
                return;
            case ')':
                this.mTarget.onControlRoadAheadOff(event, data);
                return;
            case '*':
                this.mTarget.onMapZoominMax(event, data);
                return;
            case '+':
                this.mTarget.onMapZoomoutMin(event, data);
                return;
            case ',':
                this.mTarget.onNearbySearch(event, data);
                return;
            case '-':
                this.mTarget.onAddressGet(event, data);
                return;
            case '.':
                this.mTarget.onAddressPendingRoute(event, data);
                return;
            case '/':
                this.mTarget.onAddressSet(event, data);
                return;
            case '0':
                this.mTarget.onControlStart(event, data);
                return;
            case '1':
                this.mTarget.onControlSpeechSimple(event, data);
                return;
            case '2':
                this.mTarget.onControlSpeechDetail(event, data);
                return;
            case '3':
                this.mTarget.onControlDisPlayNorth(event, data);
                return;
            case '4':
                this.mTarget.onControlDisPlayCar(event, data);
                return;
            case '5':
                this.mTarget.onControlDisplay3D(event, data);
                return;
            case '6':
                this.mTarget.onControlVolOn(event, data);
                return;
            case '7':
                this.mTarget.onControlVolOff(event, data);
                return;
            case '8':
                this.mTarget.onRouteNearbySearch(event, data);
                return;
            case '9':
                this.mTarget.onParkingSelect(event, data);
                return;
            case ':':
                this.mTarget.onConfirmOk(event, data);
                return;
            case ';':
                this.mTarget.onConfirmCancel(event, data);
                return;
            case '<':
                this.mTarget.onRouteSelect(event, data);
                return;
            case '=':
                this.mTarget.onSelectParkingCount(event, data);
                return;
            case '>':
                this.mTarget.onSelectRouteCount(event, data);
                return;
            case '?':
                this.mTarget.onWaypointSearch(event, data);
                return;
            case '@':
                this.mTarget.onControlWaypointStart(event, data);
                return;
            case 'A':
                this.mTarget.onControlOpenSmallMap(event, data);
                return;
            case 'B':
                this.mTarget.onControlCloseSmallMap(event, data);
                return;
            case 'C':
                this.mTarget.onControlOpenRibbonMap(event, data);
                return;
            case 'D':
                this.mTarget.onControlCloseRibbonMap(event, data);
                return;
            case 'E':
                this.mTarget.onControlHistory(event, data);
                return;
            case 'F':
                this.mTarget.onGetSettingsInfo(event, data);
                return;
            case 'G':
                this.mTarget.onControlParkRecommendOn(event, data);
                return;
            case 'H':
                this.mTarget.onControlParkRecommendOff(event, data);
                return;
            case 'I':
                this.mTarget.onSetScaleLevel(event, data);
                return;
            case 'J':
                this.mTarget.onSetOritentionMetre(event, data);
                return;
            case 'K':
                this.mTarget.onAlertsPreferenceSet(event, data);
                return;
            case 'L':
                this.mTarget.onAvoidRouteSet(event, data);
                return;
            case 'M':
                this.mTarget.onAutoRerouteBetterRoute(event, data);
                return;
            case 'N':
                this.mTarget.onAutoRerouteAskFirst(event, data);
                return;
            case 'O':
                this.mTarget.onAutoRerouteNever(event, data);
                return;
            case 'P':
                this.mTarget.onMapShowSet(event, data);
                return;
            case 'Q':
                this.mTarget.onPoiDetailsFavoriteAdd(event, data);
                return;
            case 'R':
                this.mTarget.onPoiDetailsFavoriteDel(event, data);
                return;
            case 'S':
                this.mTarget.onControlSettingsCLose(event, data);
                return;
            case 'T':
                this.mTarget.onControlHistoryCLose(event, data);
                return;
            case 'U':
                this.mTarget.onListItemSelected(event, data);
                return;
            case 'V':
                this.mTarget.onControlGaodeAccountPageOpen(event, data);
                return;
            case 'W':
                this.mTarget.onControlChargeServiceOpen(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{NaviEvent.CONTROL_CLOSE, NaviEvent.MAP_ZOOMIN, NaviEvent.MAP_ZOOMOUT, NaviEvent.ROAD_INFO_OPEN, NaviEvent.ROAD_INFO_CLOSE, NaviEvent.CONTROL_OVERVIEW_OPEN, NaviEvent.CONTROL_OVERVIEW_CLOSE, NaviEvent.MAP_OVERVIEW, NaviEvent.CONTROL_FAVORITE_OPEN, NaviEvent.CONTROL_SETTINGS_OPEN, NaviEvent.CONTROL_CHARGE_OPEN, NaviEvent.CONTROL_CHARGE_CLOSE, NaviEvent.DRIVE_AVOID_CONGESTION, NaviEvent.DRIVE_AVOID_CONGESTION_OFF, NaviEvent.DRIVE_AVOID_CHARGE, NaviEvent.DRIVE_AVOID_CHARGE_OFF, NaviEvent.DRIVE_HIGHWAY_FIRST_OFF, NaviEvent.DRIVE_AVOID_CONTROLS, NaviEvent.DRIVE_AVOID_CONTROLS_OFF, NaviEvent.DRIVE_RADAR_ROUTE, NaviEvent.DRIVE_RADAR_ROUTE_OFF, NaviEvent.CONTROL_SPEECH_SUPER_SIMPLE, NaviEvent.CONTROL_SPEECH_GENERAL, NaviEvent.CONTROL_SPEECH_EYE, NaviEvent.CONTROL_SPEECH_EYE_OFF, NaviEvent.CONTROL_SMART_SCALE, NaviEvent.CONTROL_SMART_SCALE_OFF, NaviEvent.CONTROL_SECURITY_REMIND, NaviEvent.CONTROL_ROAD_AHEAD, NaviEvent.DRIVE_HIGHWAY_NO, NaviEvent.DRIVE_HIGHWAY_NO_OFF, NaviEvent.DRIVE_HIGHWAY_FIRST, NaviEvent.NAVIGATING_GET, NaviEvent.POI_SEARCH, NaviEvent.CONTROL_SECURITY_REMIND_OFF, NaviEvent.MAP_ENTER_FIND_PATH, NaviEvent.MAP_EXIT_FIND_PATH, NaviEvent.SEARCH_CLOSE, NaviEvent.MAIN_ROAD, NaviEvent.SIDE_ROAD, NaviEvent.CONTROL_FAVORITE_CLOSE, NaviEvent.CONTROL_ROAD_AHEAD_OFF, NaviEvent.MAP_ZOOMIN_MAX, NaviEvent.MAP_ZOOMOUT_MIN, NaviEvent.NEARBY_SEARCH, NaviEvent.ADDRESS_GET, NaviEvent.ADDRESS_PENDING_ROUTE, NaviEvent.ADDRESS_SET, NaviEvent.CONTROL_START, NaviEvent.CONTROL_SPEECH_SIMPLE, NaviEvent.CONTROL_SPEECH_DETAIL, NaviEvent.CONTROL_DISPLAY_NORTH, NaviEvent.CONTROL_DISPLAY_CAR, NaviEvent.CONTROL_DISPLAY_3D, NaviEvent.CONTROL_VOL_ON, NaviEvent.CONTROL_VOL_OFF, NaviEvent.ROUTE_NEARBY_SEARCH, NaviEvent.PARKING_SELECT, NaviEvent.CONFIRM_OK, NaviEvent.CONFIRM_CANCEL, NaviEvent.ROUTE_SELECT, NaviEvent.SELECT_PARKING_COUNT, NaviEvent.SELECT_ROUTE_COUNT, NaviEvent.WAYPOINT_SEARCH, NaviEvent.CONTROL_WAYPOINT_START, NaviEvent.CONTROL_OPEN_SMALL_MAP, NaviEvent.CONTROL_CLOSE_SMALL_MAP, NaviEvent.CONTROL_OPEN_RIBBON_MAP, NaviEvent.CONTROL_CLOSE_RIBBON_MAP, NaviEvent.CONTROL_HISTORY, NaviEvent.SETTINGS_INFO_GET, NaviEvent.CONTROL_PARK_RECOMMEND_ON, NaviEvent.CONTROL_PARK_RECOMMEND_OFF, NaviEvent.SCALE_LEVEL_SET, NaviEvent.MOVE_NAV_METRE_SET, NaviEvent.ALERTS_PREFERENCE_SET, NaviEvent.AVOID_ROUTE_SET, NaviEvent.AUTO_REROUTE_BETTER_ROUTE, NaviEvent.AUTO_REROUTE_ASK_FIRST, NaviEvent.AUTO_REROUTE_NEVER, NaviEvent.MAP_SHOW_SET, NaviEvent.CONTROL_POI_DETAILS_FAVORITE_ADD, NaviEvent.CONTROL_POI_DETAILS_FAVORITE_DEL, NaviEvent.CONTROL_SETTINGS_CLOSE, NaviEvent.CONTROL_HISTORY_CLOSE, NaviEvent.LIST_ITEM_SELECTED, NaviEvent.CONTROL_GAODE_ACCOUNT_BING_PAGE_OPEN, NaviEvent.CONTROL_CHARGE_SERVICE_OPEN};
    }
}
