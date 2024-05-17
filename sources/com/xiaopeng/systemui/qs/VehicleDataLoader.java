package com.xiaopeng.systemui.qs;

import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.carconfig.config.IConfig;
import com.xiaopeng.systemui.carconfig.feature.IFeature;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class VehicleDataLoader {
    private static final String TAG = VehicleDataLoader.class.getSimpleName();
    private ArrayList<ArrayList<TileState>> mTileStateList = QsUtils.loadVehicleTileStateListFromJson();
    private IConfig mCarConfig = CarModelsManager.getConfig();
    private IFeature mCarFeature = CarModelsManager.getFeature();

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public ArrayList<TileState> getFilteredTileStates(int screenId) {
        char c;
        ArrayList<TileState> arrayList = this.mTileStateList.get(screenId);
        ArrayList<TileState> result = new ArrayList<>();
        result.addAll(arrayList);
        Iterator<TileState> it = arrayList.iterator();
        while (it.hasNext()) {
            TileState tilestate = it.next();
            boolean z = true;
            if (tilestate.configurable == 1) {
                boolean ifSupport = false;
                String str = tilestate.key;
                switch (str.hashCode()) {
                    case -1868200807:
                        if (str.equals("driver_seat_heat_adjustment")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1756583756:
                        if (str.equals("driver_seat_vent_adjustment")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1659133291:
                        if (str.equals("psn_massage_adjustment")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1602302530:
                        if (str.equals("panoramic_view")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case -61899240:
                        if (str.equals("driver_massage_adjustment")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 788168150:
                        if (str.equals("psn_seat_heat_adjustment")) {
                            c = 7;
                            break;
                        }
                        c = 65535;
                        break;
                    case 899785201:
                        if (str.equals("psn_seat_vent_adjustment")) {
                            c = '\b';
                            break;
                        }
                        c = 65535;
                        break;
                    case 1923500803:
                        if (str.equals(XpTilesConfig.SUSPENSION_ADJUSTMENT)) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 2119462413:
                        if (str.equals(XpTilesConfig.STEERING_WHEEL)) {
                            c = 4;
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
                        ifSupport = this.mCarConfig.isSeatHeatSupport();
                        break;
                    case 1:
                        ifSupport = this.mCarConfig.isSeatVentSupport();
                        break;
                    case 2:
                        ifSupport = this.mCarConfig.isDrvSeatMassSupport();
                        break;
                    case 3:
                        ifSupport = this.mCarConfig.isEasyLoadSupport();
                        break;
                    case 4:
                        ifSupport = this.mCarConfig.isSteeringWheelSupport();
                        break;
                    case 5:
                        if (!this.mCarFeature.isSupport360() || !this.mCarConfig.isAVMSupport()) {
                            z = false;
                        }
                        ifSupport = z;
                        Logger.i(TAG, "PANORAMIC_VIEW: " + ifSupport);
                        break;
                    case 6:
                        ifSupport = this.mCarConfig.isPsnSeatMassSupport();
                        break;
                    case 7:
                        ifSupport = this.mCarConfig.isPsnSeatHeatSupport();
                        break;
                    case '\b':
                        ifSupport = this.mCarConfig.isPsnSeatVentSupport();
                        break;
                }
                if (!ifSupport) {
                    result.remove(tilestate);
                }
            }
        }
        return result;
    }
}
