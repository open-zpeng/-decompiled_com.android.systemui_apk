package com.xiaopeng.systemui.qs;

import com.alibaba.fastjson.parser.JSONLexer;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.utils.BIHelper;
import kotlin.text.Typography;
/* loaded from: classes24.dex */
public class QuickMenuBIHelper {
    public static final String BI_STATUS_0 = "0";
    public static final String BI_STATUS_1 = "1";
    public static final String BI_STATUS_2 = "2";
    public static final String BI_STATUS_3 = "3";

    public static void sendBIData(String key, int nextState, int screenId) {
        String extra;
        BIHelper.ID id = BIHelper.ID.panel;
        BIHelper.Action action = BIHelper.Action.click;
        BIHelper.Screen screen = screenId == 0 ? BIHelper.Screen.main : BIHelper.Screen.second;
        String status = "";
        char c = 65535;
        switch (key.hashCode()) {
            case -2136764233:
                if (key.equals("air_conditioning_cleaning_switch")) {
                    c = 14;
                    break;
                }
                break;
            case -2102841088:
                if (key.equals("speech_setting_switch")) {
                    c = 20;
                    break;
                }
                break;
            case -2078090512:
                if (key.equals("intelligent_deodorization_switch")) {
                    c = 16;
                    break;
                }
                break;
            case -2027031142:
                if (key.equals("full_window_close_switch")) {
                    c = '\r';
                    break;
                }
                break;
            case -2009121622:
                if (key.equals("full_window_open_switch")) {
                    c = '\f';
                    break;
                }
                break;
            case -1868200807:
                if (key.equals("driver_seat_heat_adjustment")) {
                    c = 25;
                    break;
                }
                break;
            case -1756583756:
                if (key.equals("driver_seat_vent_adjustment")) {
                    c = 27;
                    break;
                }
                break;
            case -1674495063:
                if (key.equals("downhill_auxiliary_switch")) {
                    c = '-';
                    break;
                }
                break;
            case -1659133291:
                if (key.equals("psn_massage_adjustment")) {
                    c = 24;
                    break;
                }
                break;
            case -1654800892:
                if (key.equals("wind_adjustment")) {
                    c = '(';
                    break;
                }
                break;
            case -1635033136:
                if (key.equals("ihb_switch")) {
                    c = '\t';
                    break;
                }
                break;
            case -1602302530:
                if (key.equals("panoramic_view")) {
                    c = Typography.amp;
                    break;
                }
                break;
            case -1473685539:
                if (key.equals("rapid_cooling_switch")) {
                    c = 15;
                    break;
                }
                break;
            case -1436700797:
                if (key.equals(XpTilesConfig.CHILD_MODE_RIGHT)) {
                    c = Typography.quote;
                    break;
                }
                break;
            case -1282712593:
                if (key.equals("meditation_mode_switch")) {
                    c = 17;
                    break;
                }
                break;
            case -980570102:
                if (key.equals("dc_charging_cover_switch")) {
                    c = 5;
                    break;
                }
                break;
            case -945317973:
                if (key.equals("vehicle_sound_wave_out")) {
                    c = 31;
                    break;
                }
                break;
            case -924962427:
                if (key.equals("rear_mirror_angle_switch")) {
                    c = 2;
                    break;
                }
                break;
            case -847527463:
                if (key.equals("clean_mode")) {
                    c = '\b';
                    break;
                }
                break;
            case -832639000:
                if (key.equals("sleep_mode_switch")) {
                    c = 18;
                    break;
                }
                break;
            case -830389567:
                if (key.equals("movie_mode_switch")) {
                    c = 19;
                    break;
                }
                break;
            case -779549843:
                if (key.equals("ac_charging_cover_switch")) {
                    c = 6;
                    break;
                }
                break;
            case -642526387:
                if (key.equals("passenger_volume_adjustment")) {
                    c = 4;
                    break;
                }
                break;
            case -551990896:
                if (key.equals("open_window_air")) {
                    c = '.';
                    break;
                }
                break;
            case -371465321:
                if (key.equals("space_mode_switch")) {
                    c = '0';
                    break;
                }
                break;
            case -323622464:
                if (key.equals(XpTilesConfig.CHILD_MODE_LEFT)) {
                    c = '!';
                    break;
                }
                break;
            case -236779268:
                if (key.equals("back_box_lock_switch")) {
                    c = '\'';
                    break;
                }
                break;
            case -132873247:
                if (key.equals("passenger_screen_off")) {
                    c = 22;
                    break;
                }
                break;
            case -61899240:
                if (key.equals("driver_massage_adjustment")) {
                    c = 23;
                    break;
                }
                break;
            case 36070600:
                if (key.equals("open_back_box")) {
                    c = 11;
                    break;
                }
                break;
            case 141904477:
                if (key.equals("passenger_temperature_adjustment")) {
                    c = '2';
                    break;
                }
                break;
            case 308941893:
                if (key.equals("open_rear_mirror")) {
                    c = '+';
                    break;
                }
                break;
            case 362639578:
                if (key.equals("close_back_box")) {
                    c = '\n';
                    break;
                }
                break;
            case 499705892:
                if (key.equals("auto_hold_switch")) {
                    c = '/';
                    break;
                }
                break;
            case 772979880:
                if (key.equals("media_adjustment")) {
                    c = '*';
                    break;
                }
                break;
            case 788168150:
                if (key.equals("psn_seat_heat_adjustment")) {
                    c = JSONLexer.EOI;
                    break;
                }
                break;
            case 899785201:
                if (key.equals("psn_seat_vent_adjustment")) {
                    c = 28;
                    break;
                }
                break;
            case 939337000:
                if (key.equals("vehicle_sound_wave_in")) {
                    c = 30;
                    break;
                }
                break;
            case 940987760:
                if (key.equals("auto_wiper_speed_switch")) {
                    c = 7;
                    break;
                }
                break;
            case 1024440051:
                if (key.equals("close_rear_mirror")) {
                    c = ',';
                    break;
                }
                break;
            case 1041003101:
                if (key.equals("child_mode_sw")) {
                    c = 29;
                    break;
                }
                break;
            case 1550524598:
                if (key.equals("screen_brightness_1")) {
                    c = 1;
                    break;
                }
                break;
            case 1655014668:
                if (key.equals("psn_temperature_adjustment")) {
                    c = ')';
                    break;
                }
                break;
            case 1792492629:
                if (key.equals(XpTilesConfig.WAITING_MODE)) {
                    c = '%';
                    break;
                }
                break;
            case 1797403052:
                if (key.equals("passenger_screen_off_in_drv")) {
                    c = 21;
                    break;
                }
                break;
            case 1829750706:
                if (key.equals("volume_adjustment")) {
                    c = 3;
                    break;
                }
                break;
            case 1853221403:
                if (key.equals("brightness_adjustment")) {
                    c = 0;
                    break;
                }
                break;
            case 1923500803:
                if (key.equals(XpTilesConfig.SUSPENSION_ADJUSTMENT)) {
                    c = Typography.dollar;
                    break;
                }
                break;
            case 1989404601:
                if (key.equals("driver_mode_switch")) {
                    c = ' ';
                    break;
                }
                break;
            case 2119462413:
                if (key.equals(XpTilesConfig.STEERING_WHEEL)) {
                    c = '#';
                    break;
                }
                break;
            case 2119863363:
                if (key.equals("passenger_screen_brightness_mode")) {
                    c = '1';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                id = BIHelper.ID.brightness;
                status = "0";
                extra = "";
                break;
            case 1:
                id = BIHelper.ID.brightness;
                status = "1";
                extra = "";
                break;
            case 2:
                id = BIHelper.ID.rearview_mirror;
                extra = "";
                break;
            case 3:
                id = BIHelper.ID.volume;
                extra = "";
                break;
            case 4:
                id = BIHelper.ID.volume;
                status = "1";
                extra = "";
                break;
            case 5:
                id = BIHelper.ID.charge_port;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                status = CarModelsManager.getConfig().isDualChargePort() ? "1" : "0";
                extra = "";
                break;
            case 6:
                id = BIHelper.ID.charge_port;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                status = "2";
                extra = "";
                break;
            case 7:
                id = BIHelper.ID.wiper;
                String extra2 = Integer.toString(nextState);
                extra = extra2;
                break;
            case '\b':
                id = BIHelper.ID.clean_screen;
                extra = "";
                break;
            case '\t':
                id = BIHelper.ID.ihb;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                extra = "";
                break;
            case '\n':
            case 11:
                id = BIHelper.ID.truck;
                String extra3 = Integer.toString(nextState);
                extra = extra3;
                break;
            case '\f':
                id = BIHelper.ID.window_all_open;
                extra = "";
                break;
            case '\r':
                id = BIHelper.ID.window_all_close;
                extra = "";
                break;
            case 14:
                id = BIHelper.ID.purify;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                extra = "";
                break;
            case 15:
                id = BIHelper.ID.flash_cool;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                extra = "";
                break;
            case 16:
                id = BIHelper.ID.refresh;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                extra = "";
                break;
            case 17:
                id = BIHelper.ID.meditation;
                extra = "";
                break;
            case 18:
                id = BIHelper.ID.sleep;
                extra = "";
                break;
            case 19:
                id = BIHelper.ID.movie;
                extra = "";
                break;
            case 20:
                id = BIHelper.ID.xiaop;
                extra = "";
                break;
            case 21:
            case 22:
                id = BIHelper.ID.screen_off;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                extra = "";
                break;
            case 23:
                id = BIHelper.ID.seat_massg;
                status = "0";
                extra = "";
                break;
            case 24:
                id = BIHelper.ID.seat_massg;
                status = "1";
                extra = "";
                break;
            case 25:
                id = BIHelper.ID.seat_heat;
                status = "0";
                String extra4 = String.valueOf(nextState);
                extra = extra4;
                break;
            case 26:
                id = BIHelper.ID.seat_heat;
                status = "1";
                String extra5 = String.valueOf(nextState);
                extra = extra5;
                break;
            case 27:
                id = BIHelper.ID.seat_vent;
                status = "0";
                String extra6 = String.valueOf(nextState);
                extra = extra6;
                break;
            case 28:
                id = BIHelper.ID.seat_vent;
                status = "1";
                String extra7 = String.valueOf(nextState);
                extra = extra7;
                break;
            case 29:
                id = BIHelper.ID.childsafe;
                extra = "";
                break;
            case 30:
                id = BIHelper.ID.soundwave_in;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                extra = "";
                break;
            case 31:
                id = BIHelper.ID.soundwave_out;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                extra = "";
                break;
            case ' ':
                id = BIHelper.ID.driver_mode;
                String extra8 = Integer.toString(nextState);
                extra = extra8;
                break;
            case '!':
                id = BIHelper.ID.child_mode_left;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                extra = "";
                break;
            case '\"':
                id = BIHelper.ID.child_mode_right;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                extra = "";
                break;
            case '#':
                id = BIHelper.ID.steering_wheel;
                extra = "";
                break;
            case '$':
                id = BIHelper.ID.easy_load;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                extra = "";
                break;
            case '%':
                id = BIHelper.ID.waiting_mode;
                extra = "";
                break;
            case '&':
                id = BIHelper.ID.panoramic_view;
                extra = "";
                break;
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case '0':
            case '1':
            case '2':
                return;
            default:
                extra = "";
                break;
        }
        BIHelper.sendBIData(id, BIHelper.Type.panel, action, screen, status, extra);
    }
}
