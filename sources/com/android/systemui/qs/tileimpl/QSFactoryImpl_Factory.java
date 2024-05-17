package com.android.systemui.qs.tileimpl;

import com.android.systemui.qs.tiles.AirplaneModeTile;
import com.android.systemui.qs.tiles.BatterySaverTile;
import com.android.systemui.qs.tiles.BluetoothTile;
import com.android.systemui.qs.tiles.CastTile;
import com.android.systemui.qs.tiles.CellularTile;
import com.android.systemui.qs.tiles.ColorInversionTile;
import com.android.systemui.qs.tiles.DataSaverTile;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.qs.tiles.FlashlightTile;
import com.android.systemui.qs.tiles.HotspotTile;
import com.android.systemui.qs.tiles.LocationTile;
import com.android.systemui.qs.tiles.NfcTile;
import com.android.systemui.qs.tiles.NightDisplayTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.android.systemui.qs.tiles.UiModeNightTile;
import com.android.systemui.qs.tiles.UserTile;
import com.android.systemui.qs.tiles.WifiTile;
import com.android.systemui.qs.tiles.WorkModeTile;
import com.android.systemui.util.leak.GarbageMonitor;
import dagger.internal.Factory;
import javax.inject.Provider;
/* loaded from: classes21.dex */
public final class QSFactoryImpl_Factory implements Factory<QSFactoryImpl> {
    private final Provider<AirplaneModeTile> airplaneModeTileProvider;
    private final Provider<BatterySaverTile> batterySaverTileProvider;
    private final Provider<BluetoothTile> bluetoothTileProvider;
    private final Provider<CastTile> castTileProvider;
    private final Provider<CellularTile> cellularTileProvider;
    private final Provider<ColorInversionTile> colorInversionTileProvider;
    private final Provider<DataSaverTile> dataSaverTileProvider;
    private final Provider<DndTile> dndTileProvider;
    private final Provider<FlashlightTile> flashlightTileProvider;
    private final Provider<HotspotTile> hotspotTileProvider;
    private final Provider<LocationTile> locationTileProvider;
    private final Provider<GarbageMonitor.MemoryTile> memoryTileProvider;
    private final Provider<NfcTile> nfcTileProvider;
    private final Provider<NightDisplayTile> nightDisplayTileProvider;
    private final Provider<RotationLockTile> rotationLockTileProvider;
    private final Provider<UiModeNightTile> uiModeNightTileProvider;
    private final Provider<UserTile> userTileProvider;
    private final Provider<WifiTile> wifiTileProvider;
    private final Provider<WorkModeTile> workModeTileProvider;

    public QSFactoryImpl_Factory(Provider<WifiTile> wifiTileProvider, Provider<BluetoothTile> bluetoothTileProvider, Provider<CellularTile> cellularTileProvider, Provider<DndTile> dndTileProvider, Provider<ColorInversionTile> colorInversionTileProvider, Provider<AirplaneModeTile> airplaneModeTileProvider, Provider<WorkModeTile> workModeTileProvider, Provider<RotationLockTile> rotationLockTileProvider, Provider<FlashlightTile> flashlightTileProvider, Provider<LocationTile> locationTileProvider, Provider<CastTile> castTileProvider, Provider<HotspotTile> hotspotTileProvider, Provider<UserTile> userTileProvider, Provider<BatterySaverTile> batterySaverTileProvider, Provider<DataSaverTile> dataSaverTileProvider, Provider<NightDisplayTile> nightDisplayTileProvider, Provider<NfcTile> nfcTileProvider, Provider<GarbageMonitor.MemoryTile> memoryTileProvider, Provider<UiModeNightTile> uiModeNightTileProvider) {
        this.wifiTileProvider = wifiTileProvider;
        this.bluetoothTileProvider = bluetoothTileProvider;
        this.cellularTileProvider = cellularTileProvider;
        this.dndTileProvider = dndTileProvider;
        this.colorInversionTileProvider = colorInversionTileProvider;
        this.airplaneModeTileProvider = airplaneModeTileProvider;
        this.workModeTileProvider = workModeTileProvider;
        this.rotationLockTileProvider = rotationLockTileProvider;
        this.flashlightTileProvider = flashlightTileProvider;
        this.locationTileProvider = locationTileProvider;
        this.castTileProvider = castTileProvider;
        this.hotspotTileProvider = hotspotTileProvider;
        this.userTileProvider = userTileProvider;
        this.batterySaverTileProvider = batterySaverTileProvider;
        this.dataSaverTileProvider = dataSaverTileProvider;
        this.nightDisplayTileProvider = nightDisplayTileProvider;
        this.nfcTileProvider = nfcTileProvider;
        this.memoryTileProvider = memoryTileProvider;
        this.uiModeNightTileProvider = uiModeNightTileProvider;
    }

    @Override // javax.inject.Provider
    public QSFactoryImpl get() {
        return provideInstance(this.wifiTileProvider, this.bluetoothTileProvider, this.cellularTileProvider, this.dndTileProvider, this.colorInversionTileProvider, this.airplaneModeTileProvider, this.workModeTileProvider, this.rotationLockTileProvider, this.flashlightTileProvider, this.locationTileProvider, this.castTileProvider, this.hotspotTileProvider, this.userTileProvider, this.batterySaverTileProvider, this.dataSaverTileProvider, this.nightDisplayTileProvider, this.nfcTileProvider, this.memoryTileProvider, this.uiModeNightTileProvider);
    }

    public static QSFactoryImpl provideInstance(Provider<WifiTile> wifiTileProvider, Provider<BluetoothTile> bluetoothTileProvider, Provider<CellularTile> cellularTileProvider, Provider<DndTile> dndTileProvider, Provider<ColorInversionTile> colorInversionTileProvider, Provider<AirplaneModeTile> airplaneModeTileProvider, Provider<WorkModeTile> workModeTileProvider, Provider<RotationLockTile> rotationLockTileProvider, Provider<FlashlightTile> flashlightTileProvider, Provider<LocationTile> locationTileProvider, Provider<CastTile> castTileProvider, Provider<HotspotTile> hotspotTileProvider, Provider<UserTile> userTileProvider, Provider<BatterySaverTile> batterySaverTileProvider, Provider<DataSaverTile> dataSaverTileProvider, Provider<NightDisplayTile> nightDisplayTileProvider, Provider<NfcTile> nfcTileProvider, Provider<GarbageMonitor.MemoryTile> memoryTileProvider, Provider<UiModeNightTile> uiModeNightTileProvider) {
        return new QSFactoryImpl(wifiTileProvider, bluetoothTileProvider, cellularTileProvider, dndTileProvider, colorInversionTileProvider, airplaneModeTileProvider, workModeTileProvider, rotationLockTileProvider, flashlightTileProvider, locationTileProvider, castTileProvider, hotspotTileProvider, userTileProvider, batterySaverTileProvider, dataSaverTileProvider, nightDisplayTileProvider, nfcTileProvider, memoryTileProvider, uiModeNightTileProvider);
    }

    public static QSFactoryImpl_Factory create(Provider<WifiTile> wifiTileProvider, Provider<BluetoothTile> bluetoothTileProvider, Provider<CellularTile> cellularTileProvider, Provider<DndTile> dndTileProvider, Provider<ColorInversionTile> colorInversionTileProvider, Provider<AirplaneModeTile> airplaneModeTileProvider, Provider<WorkModeTile> workModeTileProvider, Provider<RotationLockTile> rotationLockTileProvider, Provider<FlashlightTile> flashlightTileProvider, Provider<LocationTile> locationTileProvider, Provider<CastTile> castTileProvider, Provider<HotspotTile> hotspotTileProvider, Provider<UserTile> userTileProvider, Provider<BatterySaverTile> batterySaverTileProvider, Provider<DataSaverTile> dataSaverTileProvider, Provider<NightDisplayTile> nightDisplayTileProvider, Provider<NfcTile> nfcTileProvider, Provider<GarbageMonitor.MemoryTile> memoryTileProvider, Provider<UiModeNightTile> uiModeNightTileProvider) {
        return new QSFactoryImpl_Factory(wifiTileProvider, bluetoothTileProvider, cellularTileProvider, dndTileProvider, colorInversionTileProvider, airplaneModeTileProvider, workModeTileProvider, rotationLockTileProvider, flashlightTileProvider, locationTileProvider, castTileProvider, hotspotTileProvider, userTileProvider, batterySaverTileProvider, dataSaverTileProvider, nightDisplayTileProvider, nfcTileProvider, memoryTileProvider, uiModeNightTileProvider);
    }

    public static QSFactoryImpl newQSFactoryImpl(Provider<WifiTile> wifiTileProvider, Provider<BluetoothTile> bluetoothTileProvider, Provider<CellularTile> cellularTileProvider, Provider<DndTile> dndTileProvider, Provider<ColorInversionTile> colorInversionTileProvider, Provider<AirplaneModeTile> airplaneModeTileProvider, Provider<WorkModeTile> workModeTileProvider, Provider<RotationLockTile> rotationLockTileProvider, Provider<FlashlightTile> flashlightTileProvider, Provider<LocationTile> locationTileProvider, Provider<CastTile> castTileProvider, Provider<HotspotTile> hotspotTileProvider, Provider<UserTile> userTileProvider, Provider<BatterySaverTile> batterySaverTileProvider, Provider<DataSaverTile> dataSaverTileProvider, Provider<NightDisplayTile> nightDisplayTileProvider, Provider<NfcTile> nfcTileProvider, Provider<GarbageMonitor.MemoryTile> memoryTileProvider, Provider<UiModeNightTile> uiModeNightTileProvider) {
        return new QSFactoryImpl(wifiTileProvider, bluetoothTileProvider, cellularTileProvider, dndTileProvider, colorInversionTileProvider, airplaneModeTileProvider, workModeTileProvider, rotationLockTileProvider, flashlightTileProvider, locationTileProvider, castTileProvider, hotspotTileProvider, userTileProvider, batterySaverTileProvider, dataSaverTileProvider, nightDisplayTileProvider, nfcTileProvider, memoryTileProvider, uiModeNightTileProvider);
    }
}
