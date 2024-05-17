package com.android.systemui.qs.customize;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.widget.Button;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.util.leak.GarbageMonitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public class TileQueryHelper {
    private static final String TAG = "TileQueryHelper";
    private final Context mContext;
    private boolean mFinished;
    private final TileStateListener mListener;
    private final ArrayList<TileInfo> mTiles = new ArrayList<>();
    private final ArraySet<String> mSpecs = new ArraySet<>();
    private final Handler mBgHandler = new Handler((Looper) Dependency.get(Dependency.BG_LOOPER));
    private final Handler mMainHandler = (Handler) Dependency.get(Dependency.MAIN_HANDLER);

    /* loaded from: classes21.dex */
    public static class TileInfo {
        public boolean isSystem;
        public String spec;
        public QSTile.State state;
    }

    /* loaded from: classes21.dex */
    public interface TileStateListener {
        void onTilesChanged(List<TileInfo> list);
    }

    public TileQueryHelper(Context context, TileStateListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public void queryTiles(QSTileHost host) {
        this.mTiles.clear();
        this.mSpecs.clear();
        this.mFinished = false;
        addCurrentAndStockTiles(host);
        addPackageTiles(host);
    }

    public boolean isFinished() {
        return this.mFinished;
    }

    private void addCurrentAndStockTiles(QSTileHost host) {
        QSTile tile;
        String stock = this.mContext.getString(R.string.quick_settings_tiles_stock);
        String current = Settings.Secure.getString(this.mContext.getContentResolver(), QSTileHost.TILES_SETTING);
        ArrayList<String> possibleTiles = new ArrayList<>();
        if (current != null) {
            possibleTiles.addAll(Arrays.asList(current.split(",")));
        } else {
            current = "";
        }
        String[] stockSplit = stock.split(",");
        for (String spec : stockSplit) {
            if (!current.contains(spec)) {
                possibleTiles.add(spec);
            }
        }
        if (Build.IS_DEBUGGABLE && !current.contains(GarbageMonitor.MemoryTile.TILE_SPEC)) {
            possibleTiles.add(GarbageMonitor.MemoryTile.TILE_SPEC);
        }
        final ArrayList<QSTile> tilesToAdd = new ArrayList<>();
        Iterator<String> it = possibleTiles.iterator();
        while (it.hasNext()) {
            String spec2 = it.next();
            if (!spec2.startsWith(CustomTile.PREFIX) && (tile = host.createTile(spec2)) != null) {
                if (!tile.isAvailable()) {
                    tile.destroy();
                } else {
                    tile.setListening(this, true);
                    tile.refreshState();
                    tile.setListening(this, false);
                    tile.setTileSpec(spec2);
                    tilesToAdd.add(tile);
                }
            }
        }
        this.mBgHandler.post(new Runnable() { // from class: com.android.systemui.qs.customize.-$$Lambda$TileQueryHelper$sMzDfkcNEMwHLLe95kLdEn4WPkc
            @Override // java.lang.Runnable
            public final void run() {
                TileQueryHelper.this.lambda$addCurrentAndStockTiles$0$TileQueryHelper(tilesToAdd);
            }
        });
    }

    public /* synthetic */ void lambda$addCurrentAndStockTiles$0$TileQueryHelper(ArrayList tilesToAdd) {
        Iterator it = tilesToAdd.iterator();
        while (it.hasNext()) {
            QSTile tile = (QSTile) it.next();
            QSTile.State state = tile.getState().copy();
            state.label = tile.getTileLabel();
            tile.destroy();
            addTile(tile.getTileSpec(), null, state, true);
        }
        notifyTilesChanged(false);
    }

    private void addPackageTiles(final QSTileHost host) {
        this.mBgHandler.post(new Runnable() { // from class: com.android.systemui.qs.customize.-$$Lambda$TileQueryHelper$-7aqDrq4N73id-i9gI_WE72bklw
            @Override // java.lang.Runnable
            public final void run() {
                TileQueryHelper.this.lambda$addPackageTiles$1$TileQueryHelper(host);
            }
        });
    }

    public /* synthetic */ void lambda$addPackageTiles$1$TileQueryHelper(QSTileHost host) {
        Collection<QSTile> params = host.getTiles();
        PackageManager pm = this.mContext.getPackageManager();
        List<ResolveInfo> services = pm.queryIntentServicesAsUser(new Intent("android.service.quicksettings.action.QS_TILE"), 0, ActivityManager.getCurrentUser());
        String stockTiles = this.mContext.getString(R.string.quick_settings_tiles_stock);
        for (ResolveInfo info : services) {
            String packageName = info.serviceInfo.packageName;
            ComponentName componentName = new ComponentName(packageName, info.serviceInfo.name);
            if (!stockTiles.contains(componentName.flattenToString())) {
                CharSequence appLabel = info.serviceInfo.applicationInfo.loadLabel(pm);
                String spec = CustomTile.toSpec(componentName);
                QSTile.State state = getState(params, spec);
                if (state != null) {
                    addTile(spec, appLabel, state, false);
                } else if (info.serviceInfo.icon != 0 || info.serviceInfo.applicationInfo.icon != 0) {
                    Drawable icon = info.serviceInfo.loadIcon(pm);
                    if ("android.permission.BIND_QUICK_SETTINGS_TILE".equals(info.serviceInfo.permission) && icon != null) {
                        icon.mutate();
                        icon.setTint(this.mContext.getColor(17170443));
                        CharSequence label = info.serviceInfo.loadLabel(pm);
                        createStateAndAddTile(spec, icon, label != null ? label.toString() : "null", appLabel);
                    }
                }
            }
        }
        notifyTilesChanged(true);
    }

    private void notifyTilesChanged(final boolean finished) {
        final ArrayList<TileInfo> tilesToReturn = new ArrayList<>(this.mTiles);
        this.mMainHandler.post(new Runnable() { // from class: com.android.systemui.qs.customize.-$$Lambda$TileQueryHelper$td1yVFso44MefBPUi6jpDHx3Yoc
            @Override // java.lang.Runnable
            public final void run() {
                TileQueryHelper.this.lambda$notifyTilesChanged$2$TileQueryHelper(tilesToReturn, finished);
            }
        });
    }

    public /* synthetic */ void lambda$notifyTilesChanged$2$TileQueryHelper(ArrayList tilesToReturn, boolean finished) {
        this.mListener.onTilesChanged(tilesToReturn);
        this.mFinished = finished;
    }

    private QSTile.State getState(Collection<QSTile> tiles, String spec) {
        for (QSTile tile : tiles) {
            if (spec.equals(tile.getTileSpec())) {
                return tile.getState().copy();
            }
        }
        return null;
    }

    private void addTile(String spec, CharSequence appLabel, QSTile.State state, boolean isSystem) {
        if (this.mSpecs.contains(spec)) {
            return;
        }
        TileInfo info = new TileInfo();
        info.state = state;
        info.state.dualTarget = false;
        info.state.expandedAccessibilityClassName = Button.class.getName();
        info.spec = spec;
        info.state.secondaryLabel = (isSystem || TextUtils.equals(state.label, appLabel)) ? null : appLabel;
        info.isSystem = isSystem;
        this.mTiles.add(info);
        this.mSpecs.add(spec);
    }

    private void createStateAndAddTile(String spec, Drawable drawable, CharSequence label, CharSequence appLabel) {
        QSTile.State state = new QSTile.State();
        state.state = 1;
        state.label = label;
        state.contentDescription = label;
        state.icon = new QSTileImpl.DrawableIcon(drawable);
        addTile(spec, appLabel, state, false);
    }
}
