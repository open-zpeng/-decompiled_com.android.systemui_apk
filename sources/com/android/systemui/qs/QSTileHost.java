package com.android.systemui.qs;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.DumpController;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.qs.QSFactory;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.plugins.qs.QSTileView;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.qs.external.TileLifecycleManager;
import com.android.systemui.qs.external.TileServices;
import com.android.systemui.qs.tileimpl.QSFactoryImpl;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.phone.AutoTileManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.leak.GarbageMonitor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class QSTileHost implements QSHost, TunerService.Tunable, PluginListener<QSFactory>, Dumpable {
    public static final String TILES_SETTING = "sysui_qs_tiles";
    private AutoTileManager mAutoTiles;
    private final Context mContext;
    private int mCurrentUser;
    private final DumpController mDumpController;
    private final StatusBarIconController mIconController;
    private final PluginManager mPluginManager;
    private final TileServices mServices;
    private StatusBar mStatusBar;
    private final TunerService mTunerService;
    private static final String TAG = "QSTileHost";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private final LinkedHashMap<String, QSTile> mTiles = new LinkedHashMap<>();
    protected final ArrayList<String> mTileSpecs = new ArrayList<>();
    private final List<QSHost.Callback> mCallbacks = new ArrayList();
    private final ArrayList<QSFactory> mQsFactories = new ArrayList<>();

    @Inject
    public QSTileHost(Context context, StatusBarIconController iconController, QSFactoryImpl defaultFactory, @Named("main_handler") Handler mainHandler, @Named("background_looper") Looper bgLooper, PluginManager pluginManager, final TunerService tunerService, final Provider<AutoTileManager> autoTiles, DumpController dumpController) {
        this.mIconController = iconController;
        this.mContext = context;
        this.mTunerService = tunerService;
        this.mPluginManager = pluginManager;
        this.mDumpController = dumpController;
        this.mServices = new TileServices(this, bgLooper);
        defaultFactory.setHost(this);
        this.mQsFactories.add(defaultFactory);
        pluginManager.addPluginListener((PluginListener) this, QSFactory.class, true);
        this.mDumpController.addListener(this);
        mainHandler.post(new Runnable() { // from class: com.android.systemui.qs.-$$Lambda$QSTileHost$8OyZkY1GXlSGEY9CusSz83dAxOw
            @Override // java.lang.Runnable
            public final void run() {
                QSTileHost.this.lambda$new$0$QSTileHost(tunerService, autoTiles);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$QSTileHost(TunerService tunerService, Provider autoTiles) {
        tunerService.addTunable(this, TILES_SETTING);
        this.mAutoTiles = (AutoTileManager) autoTiles.get();
    }

    public StatusBarIconController getIconController() {
        return this.mIconController;
    }

    public void destroy() {
        this.mTiles.values().forEach(new Consumer() { // from class: com.android.systemui.qs.-$$Lambda$QSTileHost$sip37eSG6wpZ0BfWCQCwFvD9UyM
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((QSTile) obj).destroy();
            }
        });
        this.mAutoTiles.destroy();
        this.mTunerService.removeTunable(this);
        this.mServices.destroy();
        this.mPluginManager.removePluginListener(this);
        this.mDumpController.removeListener(this);
    }

    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginConnected(QSFactory plugin, Context pluginContext) {
        this.mQsFactories.add(0, plugin);
        String value = this.mTunerService.getValue(TILES_SETTING);
        onTuningChanged(TILES_SETTING, "");
        onTuningChanged(TILES_SETTING, value);
    }

    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginDisconnected(QSFactory plugin) {
        this.mQsFactories.remove(plugin);
        String value = this.mTunerService.getValue(TILES_SETTING);
        onTuningChanged(TILES_SETTING, "");
        onTuningChanged(TILES_SETTING, value);
    }

    @Override // com.android.systemui.qs.QSHost
    public void addCallback(QSHost.Callback callback) {
        this.mCallbacks.add(callback);
    }

    @Override // com.android.systemui.qs.QSHost
    public void removeCallback(QSHost.Callback callback) {
        this.mCallbacks.remove(callback);
    }

    @Override // com.android.systemui.qs.QSHost
    public Collection<QSTile> getTiles() {
        return this.mTiles.values();
    }

    @Override // com.android.systemui.qs.QSHost
    public void warn(String message, Throwable t) {
    }

    @Override // com.android.systemui.qs.QSHost
    public void collapsePanels() {
        if (this.mStatusBar == null) {
            this.mStatusBar = (StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
        }
        this.mStatusBar.postAnimateCollapsePanels();
    }

    @Override // com.android.systemui.qs.QSHost
    public void forceCollapsePanels() {
        if (this.mStatusBar == null) {
            this.mStatusBar = (StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
        }
        this.mStatusBar.postAnimateForceCollapsePanels();
    }

    @Override // com.android.systemui.qs.QSHost
    public void openPanels() {
        if (this.mStatusBar == null) {
            this.mStatusBar = (StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
        }
        this.mStatusBar.postAnimateOpenPanels();
    }

    @Override // com.android.systemui.qs.QSHost
    public Context getContext() {
        return this.mContext;
    }

    @Override // com.android.systemui.qs.QSHost
    public TileServices getTileServices() {
        return this.mServices;
    }

    @Override // com.android.systemui.qs.QSHost
    public int indexOf(String spec) {
        return this.mTileSpecs.indexOf(spec);
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        if (!TILES_SETTING.equals(key)) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "Recreating tiles");
        }
        if (newValue == null && UserManager.isDeviceInDemoMode(this.mContext)) {
            newValue = this.mContext.getResources().getString(R.string.quick_settings_tiles_retail_mode);
        }
        final List<String> tileSpecs = loadTileSpecs(this.mContext, newValue);
        int currentUser = ActivityManager.getCurrentUser();
        if (tileSpecs.equals(this.mTileSpecs) && currentUser == this.mCurrentUser) {
            return;
        }
        this.mTiles.entrySet().stream().filter(new Predicate() { // from class: com.android.systemui.qs.-$$Lambda$QSTileHost$tL3GWCpuev-DvXg1noj_yj7fk3Y
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return QSTileHost.lambda$onTuningChanged$2(tileSpecs, (Map.Entry) obj);
            }
        }).forEach(new Consumer() { // from class: com.android.systemui.qs.-$$Lambda$QSTileHost$_TW3-g9Ui2otBinO5ZHSBKxrVFI
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                QSTileHost.lambda$onTuningChanged$3((Map.Entry) obj);
            }
        });
        LinkedHashMap<String, QSTile> newTiles = new LinkedHashMap<>();
        for (String tileSpec : tileSpecs) {
            QSTile tile = this.mTiles.get(tileSpec);
            if (tile != null && (!(tile instanceof CustomTile) || ((CustomTile) tile).getUser() == currentUser)) {
                if (tile.isAvailable()) {
                    if (DEBUG) {
                        Log.d(TAG, "Adding " + tile);
                    }
                    tile.removeCallbacks();
                    if (!(tile instanceof CustomTile) && this.mCurrentUser != currentUser) {
                        tile.userSwitch(currentUser);
                    }
                    newTiles.put(tileSpec, tile);
                } else {
                    tile.destroy();
                }
            } else {
                if (DEBUG) {
                    Log.d(TAG, "Creating tile: " + tileSpec);
                }
                try {
                    QSTile tile2 = createTile(tileSpec);
                    if (tile2 != null) {
                        if (tile2.isAvailable()) {
                            tile2.setTileSpec(tileSpec);
                            newTiles.put(tileSpec, tile2);
                        } else {
                            tile2.destroy();
                        }
                    }
                } catch (Throwable t) {
                    Log.w(TAG, "Error creating tile for spec: " + tileSpec, t);
                }
            }
        }
        this.mCurrentUser = currentUser;
        List<String> currentSpecs = new ArrayList<>(this.mTileSpecs);
        this.mTileSpecs.clear();
        this.mTileSpecs.addAll(tileSpecs);
        this.mTiles.clear();
        this.mTiles.putAll(newTiles);
        if (newTiles.isEmpty() && !tileSpecs.isEmpty()) {
            if (DEBUG) {
                Log.d(TAG, "No valid tiles on tuning changed. Setting to default.");
            }
            changeTiles(currentSpecs, loadTileSpecs(this.mContext, ""));
            return;
        }
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            this.mCallbacks.get(i).onTilesChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$onTuningChanged$2(List tileSpecs, Map.Entry tile) {
        return !tileSpecs.contains(tile.getKey());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onTuningChanged$3(Map.Entry tile) {
        if (DEBUG) {
            Log.d(TAG, "Destroying tile: " + ((String) tile.getKey()));
        }
        ((QSTile) tile.getValue()).destroy();
    }

    @Override // com.android.systemui.qs.QSHost
    public void removeTile(final String spec) {
        changeTileSpecs(new Predicate() { // from class: com.android.systemui.qs.-$$Lambda$QSTileHost$lvnGvThFo7-HeGkbFqhwU9KCtaQ
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean remove;
                remove = ((List) obj).remove(spec);
                return remove;
            }
        });
    }

    @Override // com.android.systemui.qs.QSHost
    public void unmarkTileAsAutoAdded(String spec) {
        AutoTileManager autoTileManager = this.mAutoTiles;
        if (autoTileManager != null) {
            autoTileManager.unmarkTileAsAutoAdded(spec);
        }
    }

    public void addTile(final String spec) {
        changeTileSpecs(new Predicate() { // from class: com.android.systemui.qs.-$$Lambda$QSTileHost$iiTl64od8Xx0qaz8exmdhzyHaWg
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean add;
                add = ((List) obj).add(spec);
                return add;
            }
        });
    }

    private void changeTileSpecs(Predicate<List<String>> changeFunction) {
        String setting = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), TILES_SETTING, ActivityManager.getCurrentUser());
        List<String> tileSpecs = loadTileSpecs(this.mContext, setting);
        if (changeFunction.test(tileSpecs)) {
            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), TILES_SETTING, TextUtils.join(",", tileSpecs), ActivityManager.getCurrentUser());
        }
    }

    public void addTile(ComponentName tile) {
        List<String> newSpecs = new ArrayList<>(this.mTileSpecs);
        newSpecs.add(0, CustomTile.toSpec(tile));
        changeTiles(this.mTileSpecs, newSpecs);
    }

    public void removeTile(ComponentName tile) {
        List<String> newSpecs = new ArrayList<>(this.mTileSpecs);
        newSpecs.remove(CustomTile.toSpec(tile));
        changeTiles(this.mTileSpecs, newSpecs);
    }

    public void changeTiles(List<String> previousTiles, List<String> newTiles) {
        int NP = previousTiles.size();
        newTiles.size();
        for (int i = 0; i < NP; i++) {
            String tileSpec = previousTiles.get(i);
            if (tileSpec.startsWith(CustomTile.PREFIX) && !newTiles.contains(tileSpec)) {
                ComponentName component = CustomTile.getComponentFromSpec(tileSpec);
                Intent intent = new Intent().setComponent(component);
                TileLifecycleManager lifecycleManager = new TileLifecycleManager(new Handler(), this.mContext, this.mServices, new Tile(), intent, new UserHandle(ActivityManager.getCurrentUser()));
                lifecycleManager.onStopListening();
                lifecycleManager.onTileRemoved();
                TileLifecycleManager.setTileAdded(this.mContext, component, false);
                lifecycleManager.flushMessagesAndUnbind();
            }
        }
        if (DEBUG) {
            Log.d(TAG, "saveCurrentTiles " + newTiles);
        }
        Settings.Secure.putStringForUser(getContext().getContentResolver(), TILES_SETTING, TextUtils.join(",", newTiles), ActivityManager.getCurrentUser());
    }

    public QSTile createTile(String tileSpec) {
        for (int i = 0; i < this.mQsFactories.size(); i++) {
            QSTile t = this.mQsFactories.get(i).createTile(tileSpec);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    public QSTileView createTileView(QSTile tile, boolean collapsedView) {
        for (int i = 0; i < this.mQsFactories.size(); i++) {
            QSTileView view = this.mQsFactories.get(i).createTileView(tile, collapsedView);
            if (view != null) {
                return view;
            }
        }
        throw new RuntimeException("Default factory didn't create view for " + tile.getTileSpec());
    }

    protected static List<String> loadTileSpecs(Context context, String tileList) {
        Resources res = context.getResources();
        String defaultTileList = res.getString(R.string.quick_settings_tiles_default);
        if (TextUtils.isEmpty(tileList)) {
            tileList = res.getString(R.string.quick_settings_tiles);
            if (DEBUG) {
                Log.d(TAG, "Loaded tile specs from config: " + tileList);
            }
        } else if (DEBUG) {
            Log.d(TAG, "Loaded tile specs from setting: " + tileList);
        }
        ArrayList<String> tiles = new ArrayList<>();
        boolean addedDefault = false;
        for (String tile : tileList.split(",")) {
            String tile2 = tile.trim();
            if (!tile2.isEmpty()) {
                if (tile2.equals("default")) {
                    if (!addedDefault) {
                        tiles.addAll(Arrays.asList(defaultTileList.split(",")));
                        if (Build.IS_DEBUGGABLE) {
                            tiles.add(GarbageMonitor.MemoryTile.TILE_SPEC);
                        }
                        addedDefault = true;
                    }
                } else {
                    tiles.add(tile2);
                }
            }
        }
        return tiles;
    }

    @Override // com.android.systemui.Dumpable
    public void dump(final FileDescriptor fd, final PrintWriter pw, final String[] args) {
        pw.println("QSTileHost:");
        this.mTiles.values().stream().filter(new Predicate() { // from class: com.android.systemui.qs.-$$Lambda$QSTileHost$w0YHlhMwIm7qnoeEO7kRZCq47o8
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return QSTileHost.lambda$dump$6((QSTile) obj);
            }
        }).forEach(new Consumer() { // from class: com.android.systemui.qs.-$$Lambda$QSTileHost$8dGA3dPDXgH8k-YhV5jUASLKyAo
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((Dumpable) ((QSTile) obj)).dump(fd, pw, args);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$dump$6(QSTile obj) {
        return obj instanceof Dumpable;
    }
}
