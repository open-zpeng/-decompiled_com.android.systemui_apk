package com.android.systemui.qs.tileimpl;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;
import com.android.systemui.DemoMode;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSFactory;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.qs.external.CustomTile;
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
import com.android.systemui.qs.tiles.IntentTile;
import com.android.systemui.qs.tiles.LocationTile;
import com.android.systemui.qs.tiles.NfcTile;
import com.android.systemui.qs.tiles.NightDisplayTile;
import com.android.systemui.qs.tiles.RotationLockTile;
import com.android.systemui.qs.tiles.UiModeNightTile;
import com.android.systemui.qs.tiles.UserTile;
import com.android.systemui.qs.tiles.WifiTile;
import com.android.systemui.qs.tiles.WorkModeTile;
import com.android.systemui.statusbar.phone.AutoTileManager;
import com.android.systemui.util.leak.GarbageMonitor;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class QSFactoryImpl implements QSFactory {
    private static final String TAG = "QSFactory";
    private final Provider<AirplaneModeTile> mAirplaneModeTileProvider;
    private final Provider<BatterySaverTile> mBatterySaverTileProvider;
    private final Provider<BluetoothTile> mBluetoothTileProvider;
    private final Provider<CastTile> mCastTileProvider;
    private final Provider<CellularTile> mCellularTileProvider;
    private final Provider<ColorInversionTile> mColorInversionTileProvider;
    private final Provider<DataSaverTile> mDataSaverTileProvider;
    private final Provider<DndTile> mDndTileProvider;
    private final Provider<FlashlightTile> mFlashlightTileProvider;
    private QSTileHost mHost;
    private final Provider<HotspotTile> mHotspotTileProvider;
    private final Provider<LocationTile> mLocationTileProvider;
    private final Provider<GarbageMonitor.MemoryTile> mMemoryTileProvider;
    private final Provider<NfcTile> mNfcTileProvider;
    private final Provider<NightDisplayTile> mNightDisplayTileProvider;
    private final Provider<RotationLockTile> mRotationLockTileProvider;
    private final Provider<UiModeNightTile> mUiModeNightTileProvider;
    private final Provider<UserTile> mUserTileProvider;
    private final Provider<WifiTile> mWifiTileProvider;
    private final Provider<WorkModeTile> mWorkModeTileProvider;

    @Inject
    public QSFactoryImpl(Provider<WifiTile> wifiTileProvider, Provider<BluetoothTile> bluetoothTileProvider, Provider<CellularTile> cellularTileProvider, Provider<DndTile> dndTileProvider, Provider<ColorInversionTile> colorInversionTileProvider, Provider<AirplaneModeTile> airplaneModeTileProvider, Provider<WorkModeTile> workModeTileProvider, Provider<RotationLockTile> rotationLockTileProvider, Provider<FlashlightTile> flashlightTileProvider, Provider<LocationTile> locationTileProvider, Provider<CastTile> castTileProvider, Provider<HotspotTile> hotspotTileProvider, Provider<UserTile> userTileProvider, Provider<BatterySaverTile> batterySaverTileProvider, Provider<DataSaverTile> dataSaverTileProvider, Provider<NightDisplayTile> nightDisplayTileProvider, Provider<NfcTile> nfcTileProvider, Provider<GarbageMonitor.MemoryTile> memoryTileProvider, Provider<UiModeNightTile> uiModeNightTileProvider) {
        this.mWifiTileProvider = wifiTileProvider;
        this.mBluetoothTileProvider = bluetoothTileProvider;
        this.mCellularTileProvider = cellularTileProvider;
        this.mDndTileProvider = dndTileProvider;
        this.mColorInversionTileProvider = colorInversionTileProvider;
        this.mAirplaneModeTileProvider = airplaneModeTileProvider;
        this.mWorkModeTileProvider = workModeTileProvider;
        this.mRotationLockTileProvider = rotationLockTileProvider;
        this.mFlashlightTileProvider = flashlightTileProvider;
        this.mLocationTileProvider = locationTileProvider;
        this.mCastTileProvider = castTileProvider;
        this.mHotspotTileProvider = hotspotTileProvider;
        this.mUserTileProvider = userTileProvider;
        this.mBatterySaverTileProvider = batterySaverTileProvider;
        this.mDataSaverTileProvider = dataSaverTileProvider;
        this.mNightDisplayTileProvider = nightDisplayTileProvider;
        this.mNfcTileProvider = nfcTileProvider;
        this.mMemoryTileProvider = memoryTileProvider;
        this.mUiModeNightTileProvider = uiModeNightTileProvider;
    }

    public void setHost(QSTileHost host) {
        this.mHost = host;
    }

    @Override // com.android.systemui.plugins.qs.QSFactory
    public QSTile createTile(String tileSpec) {
        QSTileImpl tile = createTileInternal(tileSpec);
        if (tile != null) {
            tile.handleStale();
        }
        return tile;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private QSTileImpl createTileInternal(String tileSpec) {
        char c;
        switch (tileSpec.hashCode()) {
            case -2016941037:
                if (tileSpec.equals(AutoTileManager.INVERSION)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1183073498:
                if (tileSpec.equals("flashlight")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -677011630:
                if (tileSpec.equals("airplane")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -331239923:
                if (tileSpec.equals(DemoMode.COMMAND_BATTERY)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -40300674:
                if (tileSpec.equals("rotation")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 3154:
                if (tileSpec.equals("bt")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 99610:
                if (tileSpec.equals("dnd")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 108971:
                if (tileSpec.equals("nfc")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 3046207:
                if (tileSpec.equals(AutoTileManager.CAST)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 3049826:
                if (tileSpec.equals("cell")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3075958:
                if (tileSpec.equals("dark")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case 3599307:
                if (tileSpec.equals("user")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 3649301:
                if (tileSpec.equals("wifi")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 3655441:
                if (tileSpec.equals("work")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 104817688:
                if (tileSpec.equals(AutoTileManager.NIGHT)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 109211285:
                if (tileSpec.equals(AutoTileManager.SAVER)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 1099603663:
                if (tileSpec.equals(AutoTileManager.HOTSPOT)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 1901043637:
                if (tileSpec.equals("location")) {
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
                return this.mWifiTileProvider.get();
            case 1:
                return this.mBluetoothTileProvider.get();
            case 2:
                return this.mCellularTileProvider.get();
            case 3:
                return this.mDndTileProvider.get();
            case 4:
                return this.mColorInversionTileProvider.get();
            case 5:
                return this.mAirplaneModeTileProvider.get();
            case 6:
                return this.mWorkModeTileProvider.get();
            case 7:
                return this.mRotationLockTileProvider.get();
            case '\b':
                return this.mFlashlightTileProvider.get();
            case '\t':
                return this.mLocationTileProvider.get();
            case '\n':
                return this.mCastTileProvider.get();
            case 11:
                return this.mHotspotTileProvider.get();
            case '\f':
                return this.mUserTileProvider.get();
            case '\r':
                return this.mBatterySaverTileProvider.get();
            case 14:
                return this.mDataSaverTileProvider.get();
            case 15:
                return this.mNightDisplayTileProvider.get();
            case 16:
                return this.mNfcTileProvider.get();
            case 17:
                return this.mUiModeNightTileProvider.get();
            default:
                if (tileSpec.startsWith(IntentTile.PREFIX)) {
                    return IntentTile.create(this.mHost, tileSpec);
                }
                if (tileSpec.startsWith(CustomTile.PREFIX)) {
                    return CustomTile.create(this.mHost, tileSpec);
                }
                if (Build.IS_DEBUGGABLE && tileSpec.equals(GarbageMonitor.MemoryTile.TILE_SPEC)) {
                    return this.mMemoryTileProvider.get();
                }
                Log.w(TAG, "No stock tile spec: " + tileSpec);
                return null;
        }
    }

    @Override // com.android.systemui.plugins.qs.QSFactory
    public com.android.systemui.plugins.qs.QSTileView createTileView(QSTile tile, boolean collapsedView) {
        Context context = new ContextThemeWrapper(this.mHost.getContext(), R.style.qs_theme);
        QSIconView icon = tile.createTileView(context);
        if (collapsedView) {
            return new QSTileBaseView(context, icon, collapsedView);
        }
        return new QSTileView(context, icon);
    }
}
