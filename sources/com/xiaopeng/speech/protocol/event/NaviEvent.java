package com.xiaopeng.speech.protocol.event;
/* loaded from: classes23.dex */
public class NaviEvent {
    public static final String ADDRESS_GET = "native://navi.address.get";
    public static final String ADDRESS_PENDING_ROUTE = "command://navi.address.pending.route";
    public static final String ADDRESS_SET = "command://navi.address.set";
    public static final String ALERTS_PREFERENCE_SET = "command://navi.alerts.preference.set";
    public static final String AUTO_REROUTE_ASK_FIRST = "command://navi.auto.reroute.ask.first";
    public static final String AUTO_REROUTE_BETTER_ROUTE = "command://navi.auto.reroute.better.route";
    public static final String AUTO_REROUTE_NEVER = "command://navi.auto.reroute.never";
    public static final String AVOID_ROUTE_SET = "command://navi.avoid.route.set";
    public static final String CONFIRM_CANCEL = "command://navi.confirm.cancel";
    public static final String CONFIRM_OK = "command://navi.confirm.ok";
    public static final String CONTROL_CHARGE_CLOSE = "native://navi.control.charge.close";
    public static final String CONTROL_CHARGE_OPEN = "native://navi.control.charge.open";
    public static final String CONTROL_CHARGE_SERVICE_OPEN = "command://navi.charge.service.open";
    public static final String CONTROL_CLOSE = "command://navi.control.close";
    public static final String CONTROL_CLOSE_RIBBON_MAP = "command://navi.ribbon.map.close";
    public static final String CONTROL_CLOSE_SMALL_MAP = "command://navi.small.map.close";
    public static final String CONTROL_DISPLAY_3D = "command://navi.control.display.3d";
    public static final String CONTROL_DISPLAY_CAR = "command://navi.control.display.car";
    public static final String CONTROL_DISPLAY_NORTH = "command://navi.control.display.north";
    public static final String CONTROL_FAVORITE_CLOSE = "command://navi.control.favorite.close";
    public static final String CONTROL_FAVORITE_OPEN = "command://navi.control.favorite.open";
    public static final String CONTROL_GAODE_ACCOUNT_BING_PAGE_OPEN = "command://navi.gaode.account.page.open";
    public static final String CONTROL_HISTORY = "command://navi.control.history";
    public static final String CONTROL_HISTORY_CLOSE = "command://navi.control.history.close";
    public static final String CONTROL_OPEN_RIBBON_MAP = "command://navi.ribbon.map.open";
    public static final String CONTROL_OPEN_SMALL_MAP = "command://navi.small.map.open";
    public static final String CONTROL_OVERVIEW_CLOSE = "command://navi.control.overview.close";
    public static final String CONTROL_OVERVIEW_OPEN = "command://navi.control.overview.open";
    public static final String CONTROL_PARK_RECOMMEND_OFF = "command://navi.park.recommend.off";
    public static final String CONTROL_PARK_RECOMMEND_ON = "command://navi.park.recommend.on";
    public static final String CONTROL_POI_DETAILS_FAVORITE_ADD = "command://navi.poi.details.favorite.add";
    public static final String CONTROL_POI_DETAILS_FAVORITE_DEL = "command://navi.poi.details.favorite.del";
    public static final String CONTROL_ROAD_AHEAD = "command://navi.control.road.ahead";
    public static final String CONTROL_ROAD_AHEAD_OFF = "command://navi.control.road.ahead.off";
    public static final String CONTROL_SECURITY_REMIND = "command://navi.control.security.remind";
    public static final String CONTROL_SECURITY_REMIND_OFF = "command://navi.control.security.remind.off";
    public static final String CONTROL_SETTINGS_CLOSE = "command://navi.control.settings.close";
    public static final String CONTROL_SETTINGS_OPEN = "native://navi.control.settings.open";
    public static final String CONTROL_SMART_SCALE = "command://navi.control.smart.scale";
    public static final String CONTROL_SMART_SCALE_OFF = "command://navi.control.smart.scale.off";
    public static final String CONTROL_SPEECH_DETAIL = "command://navi.control.speech.detail";
    public static final String CONTROL_SPEECH_EYE = "command://navi.control.speech.eye";
    public static final String CONTROL_SPEECH_EYE_OFF = "command://navi.control.speech.eye.off";
    public static final String CONTROL_SPEECH_GENERAL = "command://navi.control.speech.general";
    public static final String CONTROL_SPEECH_SIMPLE = "command://navi.control.speech.simple";
    public static final String CONTROL_SPEECH_SUPER_SIMPLE = "command://navi.control.speech.super.simple";
    public static final String CONTROL_START = "command://navi.control.start";
    public static final String CONTROL_VOL_OFF = "command://navi.control.vol.off";
    public static final String CONTROL_VOL_ON = "command://navi.control.vol.on";
    public static final String CONTROL_WAYPOINT_START = "command://navi.control.waypoint.start";
    public static final String DRIVE_AVOID_CHARGE = "command://navi.drive.avoid.charge";
    public static final String DRIVE_AVOID_CHARGE_OFF = "command://navi.drive.avoid.charge.off";
    public static final String DRIVE_AVOID_CONGESTION = "command://navi.drive.avoid.congestion";
    public static final String DRIVE_AVOID_CONGESTION_OFF = "command://navi.drive.avoid.congestion.off";
    public static final String DRIVE_AVOID_CONTROLS = "command://navi.drive.avoid.controls";
    public static final String DRIVE_AVOID_CONTROLS_OFF = "command://navi.drive.avoid.controls.off";
    public static final String DRIVE_HIGHWAY_FIRST = "command://navi.drive.highway.first";
    public static final String DRIVE_HIGHWAY_FIRST_OFF = "command://navi.drive.highway.first.off";
    public static final String DRIVE_HIGHWAY_NO = "command://navi.drive.highway.no";
    public static final String DRIVE_HIGHWAY_NO_OFF = "command://navi.drive.highway.no.off";
    public static final String DRIVE_RADAR_ROUTE = "command://navi.drive.radar.route";
    public static final String DRIVE_RADAR_ROUTE_OFF = "command://navi.drive.radar.route.off";
    public static final String LIST_ITEM_SELECTED = "command://navi.list.item.selected";
    public static final String MAIN_ROAD = "command://navi.main.road";
    public static final String MAP_ENTER_FIND_PATH = "command://navi.enter.find.path";
    public static final String MAP_EXIT_FIND_PATH = "command://navi.exit.find.path";
    public static final String MAP_OVERVIEW = "command://navi.map.overview";
    public static final String MAP_SHOW_SET = "command://navi.map.show.set";
    public static final String MAP_ZOOMIN = "command://navi.map.zoomin";
    public static final String MAP_ZOOMIN_MAX = "command://navi.map.zoomin.max";
    public static final String MAP_ZOOMOUT = "command://navi.map.zoomout";
    public static final String MAP_ZOOMOUT_MIN = "command://navi.map.zoomout.min";
    public static final String MOVE_NAV_METRE_SET = "command://navi.oritention.metre.set";
    public static final String NAVIGATING_GET = "native://navi.navigating.get";
    public static final String NEARBY_SEARCH = "native://navi.nearby.search";
    public static final String PARKING_SELECT = "command://navi.parking.select";
    public static final String POI_SEARCH = "native://navi.poi.search";
    public static final String ROAD_INFO_CLOSE = "command://navi.road.info.close";
    public static final String ROAD_INFO_OPEN = "command://navi.road.info.open";
    public static final String ROUTE_NEARBY_SEARCH = "native://navi.route.nearby.search";
    public static final String ROUTE_SELECT = "command://navi.route.select";
    public static final String SCALE_LEVEL_SET = "command://navi.scale.level.set";
    public static final String SEARCH_CLOSE = "command://navi.search.close";
    public static final String SELECT_PARKING_COUNT = "native://navi.select.parking.count";
    public static final String SELECT_ROUTE_COUNT = "native://navi.select.route.count";
    public static final String SETTINGS_INFO_GET = "native://navi.settings.info";
    public static final String SIDE_ROAD = "command://navi.side.road";
    public static final String WAYPOINT_SEARCH = "native://navi.waypoint.search";
}
