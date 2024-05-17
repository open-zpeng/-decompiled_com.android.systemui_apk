package com.xiaopeng.speech.protocol.query.navi;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QueryNaviEvent;
/* loaded from: classes23.dex */
public class MapQuery_Processor implements IQueryProcessor {
    private MapQuery mTarget;

    public MapQuery_Processor(MapQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1925707028:
                if (event.equals(QueryNaviEvent.GET_POI_DETAILS_FAVORITE_STATUS)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -1697099311:
                if (event.equals(QueryNaviEvent.IS_CRUISE)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1571917085:
                if (event.equals(QueryNaviEvent.SCALE_CURRENT_LEVEL)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -1223775572:
                if (event.equals(QueryNaviEvent.CONTEXT_INFO_LIST_TOP)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case -1195178996:
                if (event.equals(QueryNaviEvent.REPLY_FAVORITE_OPEN_STATUS)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -851414168:
                if (event.equals(QueryNaviEvent.GET_NAVIGATION_INFO)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -628698164:
                if (event.equals(QueryNaviEvent.CONTEXT_INFO_LIST_ONEPAGE)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -372606704:
                if (event.equals(QueryNaviEvent.IS_NAVIGATION)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -98628845:
                if (event.equals(QueryNaviEvent.REPLY_MAIN_ROAD_STATUS)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 13841718:
                if (event.equals(QueryNaviEvent.IS_ACCOUNT_LOGIN)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 18796803:
                if (event.equals(QueryNaviEvent.IS_ZOOMOUT_MIN)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 238106971:
                if (event.equals(QueryNaviEvent.IS_SR_MAP)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 334610996:
                if (event.equals(QueryNaviEvent.GET_ADDRESS)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1182098865:
                if (event.equals(QueryNaviEvent.REPLY_SIDE_ROAD_STATUS)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 1454660265:
                if (event.equals(QueryNaviEvent.IS_CALCULATE_PATH)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1693312668:
                if (event.equals(QueryNaviEvent.IS_EXPLORE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1964219764:
                if (event.equals(QueryNaviEvent.CONTEXT_INFO_LIST_BOTTOM)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 2037098922:
                if (event.equals(QueryNaviEvent.IS_ZOOMIN_MAX)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 2041908174:
                if (event.equals(QueryNaviEvent.REPLY_OPEN_CONTROLS_STATUS)) {
                    c = '\t';
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
                return Boolean.valueOf(this.mTarget.isCruise(event, data));
            case 1:
                return Boolean.valueOf(this.mTarget.isExplorePath(event, data));
            case 2:
                return Boolean.valueOf(this.mTarget.isNavigation(event, data));
            case 3:
                return this.mTarget.getAddress(event, data);
            case 4:
                return this.mTarget.getNavigationInfo(event, data);
            case 5:
                return Boolean.valueOf(this.mTarget.isZoominMax(event, data));
            case 6:
                return Boolean.valueOf(this.mTarget.isZoomoutMax(event, data));
            case 7:
                return Boolean.valueOf(this.mTarget.isCalculatePath(event, data));
            case '\b':
                return Integer.valueOf(this.mTarget.getFavoriteOpenStatus(event, data));
            case '\t':
                return Integer.valueOf(this.mTarget.getOpenControlStatus(event, data));
            case '\n':
                return Integer.valueOf(this.mTarget.getSwitchMainRoadStatus(event, data));
            case 11:
                return Integer.valueOf(this.mTarget.getSwitchSideRoadStatus(event, data));
            case '\f':
                return Integer.valueOf(this.mTarget.getCurrentScaleLevel(event, data));
            case '\r':
                return Integer.valueOf(this.mTarget.getPoiDetailsFavoriteStatus(event, data));
            case 14:
                return Boolean.valueOf(this.mTarget.isSRMap(event, data));
            case 15:
                return Integer.valueOf(this.mTarget.getListTop(event, data));
            case 16:
                return Integer.valueOf(this.mTarget.getListBottom(event, data));
            case 17:
                return Integer.valueOf(this.mTarget.getListPageInfo(event, data));
            case 18:
                return Boolean.valueOf(this.mTarget.isAccountLogin(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryNaviEvent.IS_CRUISE, QueryNaviEvent.IS_EXPLORE, QueryNaviEvent.IS_NAVIGATION, QueryNaviEvent.GET_ADDRESS, QueryNaviEvent.GET_NAVIGATION_INFO, QueryNaviEvent.IS_ZOOMIN_MAX, QueryNaviEvent.IS_ZOOMOUT_MIN, QueryNaviEvent.IS_CALCULATE_PATH, QueryNaviEvent.REPLY_FAVORITE_OPEN_STATUS, QueryNaviEvent.REPLY_OPEN_CONTROLS_STATUS, QueryNaviEvent.REPLY_MAIN_ROAD_STATUS, QueryNaviEvent.REPLY_SIDE_ROAD_STATUS, QueryNaviEvent.SCALE_CURRENT_LEVEL, QueryNaviEvent.GET_POI_DETAILS_FAVORITE_STATUS, QueryNaviEvent.IS_SR_MAP, QueryNaviEvent.CONTEXT_INFO_LIST_TOP, QueryNaviEvent.CONTEXT_INFO_LIST_BOTTOM, QueryNaviEvent.CONTEXT_INFO_LIST_ONEPAGE, QueryNaviEvent.IS_ACCOUNT_LOGIN};
    }
}
