package com.xiaopeng.systemui;

import com.alibaba.fastjson.parser.JSONLexer;
import com.xiaopeng.systemui.quickmenu.tiles.ACChargingCoverTile;
import com.xiaopeng.systemui.quickmenu.tiles.AirConditioningCleaningTile;
import com.xiaopeng.systemui.quickmenu.tiles.AutoHoldTile;
import com.xiaopeng.systemui.quickmenu.tiles.AutoWiperSpeedTile;
import com.xiaopeng.systemui.quickmenu.tiles.BackBoxLockTile;
import com.xiaopeng.systemui.quickmenu.tiles.BrightnessTile;
import com.xiaopeng.systemui.quickmenu.tiles.ChargingCoverTile;
import com.xiaopeng.systemui.quickmenu.tiles.ChildSafetyModeTile;
import com.xiaopeng.systemui.quickmenu.tiles.CleanModeTile;
import com.xiaopeng.systemui.quickmenu.tiles.CloseBackBoxTile;
import com.xiaopeng.systemui.quickmenu.tiles.CloseRearMirrorTile;
import com.xiaopeng.systemui.quickmenu.tiles.DefaultTile;
import com.xiaopeng.systemui.quickmenu.tiles.DownhilAuxiliaryTile;
import com.xiaopeng.systemui.quickmenu.tiles.DriverModeTile;
import com.xiaopeng.systemui.quickmenu.tiles.FullWindowCloseTile;
import com.xiaopeng.systemui.quickmenu.tiles.FullWindowOpenTile;
import com.xiaopeng.systemui.quickmenu.tiles.IHBTile;
import com.xiaopeng.systemui.quickmenu.tiles.IntelligentDeodorizationTile;
import com.xiaopeng.systemui.quickmenu.tiles.MeditationModeTile;
import com.xiaopeng.systemui.quickmenu.tiles.MovieModeTile;
import com.xiaopeng.systemui.quickmenu.tiles.OpenBackBoxTile;
import com.xiaopeng.systemui.quickmenu.tiles.OpenRearMirrorTile;
import com.xiaopeng.systemui.quickmenu.tiles.OpenWindowAirTile;
import com.xiaopeng.systemui.quickmenu.tiles.PanoramicViewTile;
import com.xiaopeng.systemui.quickmenu.tiles.PsnBrightnessTile;
import com.xiaopeng.systemui.quickmenu.tiles.PsnSoundTile;
import com.xiaopeng.systemui.quickmenu.tiles.PsnTemperatureTile;
import com.xiaopeng.systemui.quickmenu.tiles.RapidCoolingTile;
import com.xiaopeng.systemui.quickmenu.tiles.RearMirrorAngleTile;
import com.xiaopeng.systemui.quickmenu.tiles.ScreenOffTile;
import com.xiaopeng.systemui.quickmenu.tiles.SeatDefaultTile;
import com.xiaopeng.systemui.quickmenu.tiles.SeatMassageTile;
import com.xiaopeng.systemui.quickmenu.tiles.SleepModeTile;
import com.xiaopeng.systemui.quickmenu.tiles.SoundTile;
import com.xiaopeng.systemui.quickmenu.tiles.SpeechSettingTile;
import com.xiaopeng.systemui.quickmenu.tiles.TemperatureTile;
import com.xiaopeng.systemui.quickmenu.tiles.VehicleSoundWaveTile;
import com.xiaopeng.systemui.quickmenu.tiles.WindTile;
import com.xiaopeng.systemui.quickmenu.tiles.XpTile;
import kotlin.text.Typography;
/* loaded from: classes24.dex */
public class TileFactory {
    public XpTile createTile(String key) {
        String str;
        char c;
        char c2;
        switch (key.hashCode()) {
            case -2136764233:
                str = "speech_setting_switch";
                if (key.equals("air_conditioning_cleaning_switch")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -2102841088:
                str = "speech_setting_switch";
                if (key.equals(str)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -2078090512:
                if (key.equals("intelligent_deodorization_switch")) {
                    c2 = 16;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -2027031142:
                if (key.equals("full_window_close_switch")) {
                    c2 = 5;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -2009121622:
                if (key.equals("full_window_open_switch")) {
                    c2 = 2;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -1868200807:
                if (key.equals("driver_seat_heat_adjustment")) {
                    c2 = '#';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -1756583756:
                if (key.equals("driver_seat_vent_adjustment")) {
                    c2 = Typography.dollar;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -1674495063:
                if (key.equals("downhill_auxiliary_switch")) {
                    c2 = '\n';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -1654800892:
                if (key.equals("wind_adjustment")) {
                    c2 = 24;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -1635033136:
                if (key.equals("ihb_switch")) {
                    c2 = 27;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -1602302530:
                if (key.equals("panoramic_view")) {
                    c2 = ')';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -1473685539:
                if (key.equals("rapid_cooling_switch")) {
                    c2 = 15;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -1282712593:
                if (key.equals("meditation_mode_switch")) {
                    c2 = 17;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -980570102:
                if (key.equals("dc_charging_cover_switch")) {
                    c2 = 23;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -945317973:
                if (key.equals("vehicle_sound_wave_out")) {
                    c2 = Typography.amp;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -924962427:
                if (key.equals("rear_mirror_angle_switch")) {
                    c2 = JSONLexer.EOI;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -847527463:
                if (key.equals("clean_mode")) {
                    c2 = 14;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -832639000:
                if (key.equals("sleep_mode_switch")) {
                    c2 = 21;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -830389567:
                if (key.equals("movie_mode_switch")) {
                    c2 = 22;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -779549843:
                if (key.equals("ac_charging_cover_switch")) {
                    c2 = 4;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -642526387:
                if (key.equals("passenger_volume_adjustment")) {
                    c2 = 30;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -551990896:
                if (key.equals("open_window_air")) {
                    c2 = 6;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -236779268:
                if (key.equals("back_box_lock_switch")) {
                    c2 = 3;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -132873247:
                if (key.equals("passenger_screen_off")) {
                    c2 = 28;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case -61899240:
                if (key.equals("driver_massage_adjustment")) {
                    c2 = '\'';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 36070600:
                if (key.equals("open_back_box")) {
                    c2 = 11;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 141904477:
                if (key.equals("passenger_temperature_adjustment")) {
                    c2 = 31;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 308941893:
                if (key.equals("open_rear_mirror")) {
                    c2 = '\b';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 362639578:
                if (key.equals("close_back_box")) {
                    c2 = '\f';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 499705892:
                if (key.equals("auto_hold_switch")) {
                    c2 = 7;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 788168150:
                if (key.equals("psn_seat_heat_adjustment")) {
                    c2 = '!';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 899785201:
                if (key.equals("psn_seat_vent_adjustment")) {
                    c2 = Typography.quote;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 939337000:
                if (key.equals("vehicle_sound_wave_in")) {
                    c2 = '%';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 940987760:
                if (key.equals("auto_wiper_speed_switch")) {
                    c2 = 1;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 1024440051:
                if (key.equals("close_rear_mirror")) {
                    c2 = '\t';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 1041003101:
                if (key.equals("child_mode_sw")) {
                    c2 = '(';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 1550524598:
                if (key.equals("screen_brightness_1")) {
                    c2 = ' ';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 1655014668:
                if (key.equals("psn_temperature_adjustment")) {
                    c2 = 25;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 1797403052:
                if (key.equals("passenger_screen_off_in_drv")) {
                    c2 = 29;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 1829750706:
                if (key.equals("volume_adjustment")) {
                    c2 = 20;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 1853221403:
                if (key.equals("brightness_adjustment")) {
                    c2 = '\r';
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            case 1989404601:
                if (key.equals("driver_mode_switch")) {
                    c2 = 0;
                    c = c2;
                    str = "speech_setting_switch";
                    break;
                }
                str = "speech_setting_switch";
                c = 65535;
                break;
            default:
                str = "speech_setting_switch";
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return new DriverModeTile("driver_mode_switch");
            case 1:
                return new AutoWiperSpeedTile("auto_wiper_speed_switch");
            case 2:
                return new FullWindowOpenTile("full_window_open_switch");
            case 3:
                return new BackBoxLockTile("back_box_lock_switch");
            case 4:
                return new ACChargingCoverTile("ac_charging_cover_switch");
            case 5:
                return new FullWindowCloseTile("full_window_close_switch");
            case 6:
                return new OpenWindowAirTile("open_window_air");
            case 7:
                return new AutoHoldTile("auto_hold_switch");
            case '\b':
                return new OpenRearMirrorTile("open_rear_mirror");
            case '\t':
                return new CloseRearMirrorTile("close_rear_mirror");
            case '\n':
                return new DownhilAuxiliaryTile("downhill_auxiliary_switch");
            case 11:
                return new OpenBackBoxTile("open_back_box");
            case '\f':
                return new CloseBackBoxTile("close_back_box");
            case '\r':
                return new BrightnessTile("brightness_adjustment");
            case 14:
                return new CleanModeTile("clean_mode");
            case 15:
                return new RapidCoolingTile("rapid_cooling_switch");
            case 16:
                return new IntelligentDeodorizationTile("intelligent_deodorization_switch");
            case 17:
                return new MeditationModeTile("meditation_mode_switch");
            case 18:
                return new SpeechSettingTile(str);
            case 19:
                return new AirConditioningCleaningTile("air_conditioning_cleaning_switch");
            case 20:
                return new SoundTile("volume_adjustment");
            case 21:
                return new SleepModeTile("sleep_mode_switch");
            case 22:
                return new MovieModeTile("movie_mode_switch");
            case 23:
                return new ChargingCoverTile("dc_charging_cover_switch");
            case 24:
                return new WindTile("wind_adjustment");
            case 25:
                return new TemperatureTile("psn_temperature_adjustment");
            case 26:
                return new RearMirrorAngleTile("rear_mirror_angle_switch");
            case 27:
                return new IHBTile("ihb_switch");
            case 28:
                return new ScreenOffTile("passenger_screen_off");
            case 29:
                return new ScreenOffTile("passenger_screen_off_in_drv");
            case 30:
                return new PsnSoundTile("passenger_volume_adjustment");
            case 31:
                return new PsnTemperatureTile("passenger_temperature_adjustment");
            case ' ':
                return new PsnBrightnessTile("screen_brightness_1");
            case '!':
            case '\"':
            case '#':
            case '$':
                return new SeatDefaultTile(key);
            case '%':
            case '&':
                return new VehicleSoundWaveTile(key);
            case '\'':
                return new SeatMassageTile("driver_massage_adjustment");
            case '(':
                return new ChildSafetyModeTile("child_mode_sw");
            case ')':
                return new PanoramicViewTile("panoramic_view");
            default:
                return new DefaultTile(key);
        }
    }
}
