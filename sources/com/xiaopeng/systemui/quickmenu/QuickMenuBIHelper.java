package com.xiaopeng.systemui.quickmenu;

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

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static void sendBIData(String key, int nextState, int screenId) {
        char c;
        BIHelper.ID id = BIHelper.ID.panel;
        BIHelper.Action action = BIHelper.Action.click;
        BIHelper.Screen screen = BIHelper.Screen.main;
        String status = "";
        String extra = "";
        switch (key.hashCode()) {
            case -2136764233:
                if (key.equals("air_conditioning_cleaning_switch")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -2102841088:
                if (key.equals("speech_setting_switch")) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -2078090512:
                if (key.equals("intelligent_deodorization_switch")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case -2027031142:
                if (key.equals("full_window_close_switch")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -2009121622:
                if (key.equals("full_window_open_switch")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -1868200807:
                if (key.equals("driver_seat_heat_adjustment")) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case -1756583756:
                if (key.equals("driver_seat_vent_adjustment")) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case -1674495063:
                if (key.equals("downhill_auxiliary_switch")) {
                    c = '\'';
                    break;
                }
                c = 65535;
                break;
            case -1659133291:
                if (key.equals("psn_massage_adjustment")) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case -1654800892:
                if (key.equals("wind_adjustment")) {
                    c = Typography.quote;
                    break;
                }
                c = 65535;
                break;
            case -1635033136:
                if (key.equals("ihb_switch")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1473685539:
                if (key.equals("rapid_cooling_switch")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case -1282712593:
                if (key.equals("meditation_mode_switch")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -980570102:
                if (key.equals("dc_charging_cover_switch")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -945317973:
                if (key.equals("vehicle_sound_wave_out")) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case -924962427:
                if (key.equals("rear_mirror_angle_switch")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -847527463:
                if (key.equals("clean_mode")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -832639000:
                if (key.equals("sleep_mode_switch")) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -830389567:
                if (key.equals("movie_mode_switch")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -779549843:
                if (key.equals("ac_charging_cover_switch")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -642526387:
                if (key.equals("passenger_volume_adjustment")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -551990896:
                if (key.equals("open_window_air")) {
                    c = '(';
                    break;
                }
                c = 65535;
                break;
            case -371465321:
                if (key.equals("space_mode_switch")) {
                    c = '*';
                    break;
                }
                c = 65535;
                break;
            case -236779268:
                if (key.equals("back_box_lock_switch")) {
                    c = '!';
                    break;
                }
                c = 65535;
                break;
            case -132873247:
                if (key.equals("passenger_screen_off")) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -61899240:
                if (key.equals("driver_massage_adjustment")) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 36070600:
                if (key.equals("open_back_box")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 141904477:
                if (key.equals("passenger_temperature_adjustment")) {
                    c = ',';
                    break;
                }
                c = 65535;
                break;
            case 308941893:
                if (key.equals("open_rear_mirror")) {
                    c = '%';
                    break;
                }
                c = 65535;
                break;
            case 362639578:
                if (key.equals("close_back_box")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 499705892:
                if (key.equals("auto_hold_switch")) {
                    c = ')';
                    break;
                }
                c = 65535;
                break;
            case 772979880:
                if (key.equals("media_adjustment")) {
                    c = Typography.dollar;
                    break;
                }
                c = 65535;
                break;
            case 788168150:
                if (key.equals("psn_seat_heat_adjustment")) {
                    c = JSONLexer.EOI;
                    break;
                }
                c = 65535;
                break;
            case 899785201:
                if (key.equals("psn_seat_vent_adjustment")) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case 939337000:
                if (key.equals("vehicle_sound_wave_in")) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case 940987760:
                if (key.equals("auto_wiper_speed_switch")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1024440051:
                if (key.equals("close_rear_mirror")) {
                    c = Typography.amp;
                    break;
                }
                c = 65535;
                break;
            case 1041003101:
                if (key.equals("child_mode_sw")) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case 1550524598:
                if (key.equals("screen_brightness_1")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1655014668:
                if (key.equals("psn_temperature_adjustment")) {
                    c = '#';
                    break;
                }
                c = 65535;
                break;
            case 1797403052:
                if (key.equals("passenger_screen_off_in_drv")) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 1829750706:
                if (key.equals("volume_adjustment")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1853221403:
                if (key.equals("brightness_adjustment")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1989404601:
                if (key.equals("driver_mode_switch")) {
                    c = ' ';
                    break;
                }
                c = 65535;
                break;
            case 2119863363:
                if (key.equals("passenger_screen_brightness_mode")) {
                    c = '+';
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
                id = BIHelper.ID.brightness;
                status = "0";
                break;
            case 1:
                id = BIHelper.ID.brightness;
                status = "1";
                break;
            case 2:
                id = BIHelper.ID.rearview_mirror;
                break;
            case 3:
                id = BIHelper.ID.volume;
                break;
            case 4:
                id = BIHelper.ID.volume;
                status = "1";
                break;
            case 5:
                id = BIHelper.ID.charge_port;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                status = CarModelsManager.getConfig().isDualChargePort() ? "1" : "0";
                break;
            case 6:
                id = BIHelper.ID.charge_port;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                status = "2";
                break;
            case 7:
                id = BIHelper.ID.wiper;
                extra = Integer.toString(nextState);
                break;
            case '\b':
                id = BIHelper.ID.clean_screen;
                break;
            case '\t':
                id = BIHelper.ID.ihb;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                break;
            case '\n':
            case 11:
                id = BIHelper.ID.truck;
                extra = Integer.toString(nextState);
                break;
            case '\f':
                id = BIHelper.ID.window_all_open;
                break;
            case '\r':
                id = BIHelper.ID.window_all_close;
                break;
            case 14:
                id = BIHelper.ID.purify;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                break;
            case 15:
                id = BIHelper.ID.flash_cool;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                break;
            case 16:
                id = BIHelper.ID.refresh;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                break;
            case 17:
                id = BIHelper.ID.meditation;
                break;
            case 18:
                id = BIHelper.ID.sleep;
                break;
            case 19:
                id = BIHelper.ID.movie;
                break;
            case 20:
                id = BIHelper.ID.xiaop;
                break;
            case 21:
            case 22:
                id = BIHelper.ID.screen_off;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                screen = screenId == 0 ? BIHelper.Screen.main : BIHelper.Screen.second;
                break;
            case 23:
                id = BIHelper.ID.seat_massg;
                status = "0";
                break;
            case 24:
                id = BIHelper.ID.seat_massg;
                status = "1";
                break;
            case 25:
                id = BIHelper.ID.seat_heat;
                status = "0";
                break;
            case 26:
                id = BIHelper.ID.seat_heat;
                status = "1";
                break;
            case 27:
                id = BIHelper.ID.seat_vent;
                status = "0";
                break;
            case 28:
                id = BIHelper.ID.seat_vent;
                status = "1";
                break;
            case 29:
                id = BIHelper.ID.childsafe;
                break;
            case 30:
                id = BIHelper.ID.soundwave_in;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                break;
            case 31:
                id = BIHelper.ID.soundwave_out;
                action = nextState == 1 ? BIHelper.Action.close : BIHelper.Action.open;
                break;
            case ' ':
                id = BIHelper.ID.driver_mode;
                extra = Integer.toString(nextState);
                break;
            case '!':
            case '\"':
            case '#':
            case '$':
            case '%':
            case '&':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
                return;
        }
        BIHelper.sendBIData(id, BIHelper.Type.panel, action, screen, status, extra);
    }
}
