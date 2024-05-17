package com.xiaopeng.systemui.qs;

import com.alibaba.fastjson.parser.JSONLexer;
import com.xiaopeng.systemui.qs.tilemodels.ACChargingCoverTileModel;
import com.xiaopeng.systemui.qs.tilemodels.AirConditioningCleaningTileModel;
import com.xiaopeng.systemui.qs.tilemodels.AutoWiperSpeedTileModel;
import com.xiaopeng.systemui.qs.tilemodels.BrightnessTileModel;
import com.xiaopeng.systemui.qs.tilemodels.ChargingCoverTileModel;
import com.xiaopeng.systemui.qs.tilemodels.ChildModeTileModel;
import com.xiaopeng.systemui.qs.tilemodels.ChildSafetyModeTileModel;
import com.xiaopeng.systemui.qs.tilemodels.CleanModeTileModel;
import com.xiaopeng.systemui.qs.tilemodels.CloseBackBoxTileModel;
import com.xiaopeng.systemui.qs.tilemodels.ContentProviderTileModel;
import com.xiaopeng.systemui.qs.tilemodels.EasyLoadTileModel;
import com.xiaopeng.systemui.qs.tilemodels.MediaTypeTileModel;
import com.xiaopeng.systemui.qs.tilemodels.OpenBackBoxTileModel;
import com.xiaopeng.systemui.qs.tilemodels.PanoramicViewTileModel;
import com.xiaopeng.systemui.qs.tilemodels.PsnBrightnessTileModel;
import com.xiaopeng.systemui.qs.tilemodels.PsnSoundTileModel;
import com.xiaopeng.systemui.qs.tilemodels.RearMirrorAngleTileModel;
import com.xiaopeng.systemui.qs.tilemodels.ScreenOffTileModel;
import com.xiaopeng.systemui.qs.tilemodels.SeatHeatVentTileModel;
import com.xiaopeng.systemui.qs.tilemodels.SeatMassageTileModel;
import com.xiaopeng.systemui.qs.tilemodels.SteeringWheelTileModel;
import com.xiaopeng.systemui.qs.tilemodels.WaitingModeTileModel;
import com.xiaopeng.systemui.qs.tilemodels.XpTileModel;
/* loaded from: classes24.dex */
public class XpTileFactory {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static XpTileModel createTile(String key) {
        char c;
        switch (key.hashCode()) {
            case -2136764233:
                if (key.equals("air_conditioning_cleaning_switch")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1868200807:
                if (key.equals("driver_seat_heat_adjustment")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1756583756:
                if (key.equals("driver_seat_vent_adjustment")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -1659133291:
                if (key.equals("psn_massage_adjustment")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case -1602302530:
                if (key.equals("panoramic_view")) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -1436700797:
                if (key.equals(XpTilesConfig.CHILD_MODE_RIGHT)) {
                    c = 23;
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
            case -924962427:
                if (key.equals("rear_mirror_angle_switch")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -847527463:
                if (key.equals("clean_mode")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -779549843:
                if (key.equals("ac_charging_cover_switch")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -642526387:
                if (key.equals("passenger_volume_adjustment")) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -323622464:
                if (key.equals(XpTilesConfig.CHILD_MODE_LEFT)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -132873247:
                if (key.equals("passenger_screen_off")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -61899240:
                if (key.equals("driver_massage_adjustment")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 36070600:
                if (key.equals("open_back_box")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 362639578:
                if (key.equals("close_back_box")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 788168150:
                if (key.equals("psn_seat_heat_adjustment")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 899785201:
                if (key.equals("psn_seat_vent_adjustment")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 940987760:
                if (key.equals("auto_wiper_speed_switch")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1041003101:
                if (key.equals("child_mode_sw")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1550524598:
                if (key.equals("screen_brightness_1")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case 1792492629:
                if (key.equals(XpTilesConfig.WAITING_MODE)) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case 1797403052:
                if (key.equals("passenger_screen_off_in_drv")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 1853221403:
                if (key.equals("brightness_adjustment")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1923500803:
                if (key.equals(XpTilesConfig.SUSPENSION_ADJUSTMENT)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 1939875509:
                if (key.equals(XpTilesConfig.MEDIA_TYPE)) {
                    c = JSONLexer.EOI;
                    break;
                }
                c = 65535;
                break;
            case 2119462413:
                if (key.equals(XpTilesConfig.STEERING_WHEEL)) {
                    c = 24;
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
                return new ACChargingCoverTileModel(key);
            case 1:
                return new AirConditioningCleaningTileModel(key);
            case 2:
                return new AutoWiperSpeedTileModel(key);
            case 3:
                return new OpenBackBoxTileModel(XpTilesConfig.BACK_BOX_OPEN_CLOSE);
            case 4:
                return new BrightnessTileModel(key);
            case 5:
                return new ChargingCoverTileModel(key);
            case 6:
                return new ChildSafetyModeTileModel(key);
            case 7:
                return new CleanModeTileModel(key);
            case '\b':
                return new CloseBackBoxTileModel(XpTilesConfig.BACK_BOX_OPEN_CLOSE);
            case '\t':
                return new RearMirrorAngleTileModel(key);
            case '\n':
            case 11:
            case '\f':
            case '\r':
                return new SeatHeatVentTileModel(key);
            case 14:
            case 15:
                return new SeatMassageTileModel(key);
            case 16:
            case 17:
                return new ScreenOffTileModel(key);
            case 18:
                return new PsnSoundTileModel(key);
            case 19:
                return new PsnBrightnessTileModel(key);
            case 20:
                return new PanoramicViewTileModel(key);
            case 21:
                return new EasyLoadTileModel(key);
            case 22:
            case 23:
                return new ChildModeTileModel(key);
            case 24:
                return new SteeringWheelTileModel(key);
            case 25:
                return new WaitingModeTileModel(key);
            case 26:
                return new MediaTypeTileModel(key);
            default:
                return new ContentProviderTileModel(key);
        }
    }
}
