package com.android.systemui.qs.external;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.quicksettings.IQSService;
import android.service.quicksettings.Tile;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.Dependency;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class TileServices extends IQSService.Stub {
    static final int DEFAULT_MAX_BOUND = 3;
    static final int REDUCED_MAX_BOUND = 1;
    private static final Comparator<TileServiceManager> SERVICE_SORT = new Comparator<TileServiceManager>() { // from class: com.android.systemui.qs.external.TileServices.3
        @Override // java.util.Comparator
        public int compare(TileServiceManager left, TileServiceManager right) {
            return -Integer.compare(left.getBindPriority(), right.getBindPriority());
        }
    };
    private static final String TAG = "TileServices";
    private final Context mContext;
    private final Handler mHandler;
    private final QSTileHost mHost;
    private final Handler mMainHandler;
    private final ArrayMap<CustomTile, TileServiceManager> mServices = new ArrayMap<>();
    private final ArrayMap<ComponentName, CustomTile> mTiles = new ArrayMap<>();
    private final ArrayMap<IBinder, CustomTile> mTokenMap = new ArrayMap<>();
    private int mMaxBound = 3;
    private final BroadcastReceiver mRequestListeningReceiver = new BroadcastReceiver() { // from class: com.android.systemui.qs.external.TileServices.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.service.quicksettings.action.REQUEST_LISTENING".equals(intent.getAction())) {
                TileServices.this.requestListening((ComponentName) intent.getParcelableExtra("android.intent.extra.COMPONENT_NAME"));
            }
        }
    };

    public TileServices(QSTileHost host, Looper looper) {
        this.mHost = host;
        this.mContext = this.mHost.getContext();
        this.mContext.registerReceiver(this.mRequestListeningReceiver, new IntentFilter("android.service.quicksettings.action.REQUEST_LISTENING"));
        this.mHandler = new Handler(looper);
        this.mMainHandler = new Handler(Looper.getMainLooper());
    }

    public Context getContext() {
        return this.mContext;
    }

    public QSTileHost getHost() {
        return this.mHost;
    }

    public TileServiceManager getTileWrapper(CustomTile tile) {
        ComponentName component = tile.getComponent();
        TileServiceManager service = onCreateTileService(component, tile.getQsTile());
        synchronized (this.mServices) {
            this.mServices.put(tile, service);
            this.mTiles.put(component, tile);
            this.mTokenMap.put(service.getToken(), tile);
        }
        service.startLifecycleManagerAndAddTile();
        return service;
    }

    protected TileServiceManager onCreateTileService(ComponentName component, Tile tile) {
        return new TileServiceManager(this, this.mHandler, component, tile);
    }

    public void freeService(CustomTile tile, TileServiceManager service) {
        synchronized (this.mServices) {
            service.setBindAllowed(false);
            service.handleDestroy();
            this.mServices.remove(tile);
            this.mTokenMap.remove(service.getToken());
            this.mTiles.remove(tile.getComponent());
            final String slot = tile.getComponent().getClassName();
            this.mMainHandler.post(new Runnable() { // from class: com.android.systemui.qs.external.-$$Lambda$TileServices$m2qCzd8BVbBUzSnClFn7o_chF7k
                @Override // java.lang.Runnable
                public final void run() {
                    TileServices.this.lambda$freeService$0$TileServices(slot);
                }
            });
        }
    }

    public /* synthetic */ void lambda$freeService$0$TileServices(String slot) {
        this.mHost.getIconController().removeAllIconsForSlot(slot);
    }

    public void setMemoryPressure(boolean memoryPressure) {
        this.mMaxBound = memoryPressure ? 1 : 3;
        recalculateBindAllowance();
    }

    public void recalculateBindAllowance() {
        ArrayList<TileServiceManager> services;
        synchronized (this.mServices) {
            services = new ArrayList<>(this.mServices.values());
        }
        int N = services.size();
        if (N > this.mMaxBound) {
            long currentTime = System.currentTimeMillis();
            for (int i = 0; i < N; i++) {
                services.get(i).calculateBindPriority(currentTime);
            }
            Collections.sort(services, SERVICE_SORT);
        }
        int i2 = 0;
        while (i2 < this.mMaxBound && i2 < N) {
            services.get(i2).setBindAllowed(true);
            i2++;
        }
        while (i2 < N) {
            services.get(i2).setBindAllowed(false);
            i2++;
        }
    }

    private void verifyCaller(CustomTile tile) {
        try {
            String packageName = tile.getComponent().getPackageName();
            int uid = this.mContext.getPackageManager().getPackageUidAsUser(packageName, Binder.getCallingUserHandle().getIdentifier());
            if (Binder.getCallingUid() != uid) {
                throw new SecurityException("Component outside caller's uid");
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new SecurityException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestListening(ComponentName component) {
        synchronized (this.mServices) {
            CustomTile customTile = getTileForComponent(component);
            if (customTile == null) {
                Log.d(TAG, "Couldn't find tile for " + component);
                return;
            }
            TileServiceManager service = this.mServices.get(customTile);
            if (service.isActiveTile()) {
                service.setBindRequested(true);
                try {
                    service.getTileService().onStartListening();
                } catch (RemoteException e) {
                }
            }
        }
    }

    public void updateQsTile(Tile tile, IBinder token) {
        CustomTile customTile = getTileForToken(token);
        if (customTile != null) {
            verifyCaller(customTile);
            synchronized (this.mServices) {
                TileServiceManager tileServiceManager = this.mServices.get(customTile);
                if (tileServiceManager != null && tileServiceManager.isLifecycleStarted()) {
                    tileServiceManager.clearPendingBind();
                    tileServiceManager.setLastUpdate(System.currentTimeMillis());
                    customTile.updateState(tile);
                    customTile.refreshState();
                    return;
                }
                Log.e(TAG, "TileServiceManager not started for " + customTile.getComponent(), new IllegalStateException());
            }
        }
    }

    public void onStartSuccessful(IBinder token) {
        CustomTile customTile = getTileForToken(token);
        if (customTile != null) {
            verifyCaller(customTile);
            synchronized (this.mServices) {
                TileServiceManager tileServiceManager = this.mServices.get(customTile);
                if (tileServiceManager != null && tileServiceManager.isLifecycleStarted()) {
                    tileServiceManager.clearPendingBind();
                    customTile.refreshState();
                    return;
                }
                Log.e(TAG, "TileServiceManager not started for " + customTile.getComponent(), new IllegalStateException());
            }
        }
    }

    public void onShowDialog(IBinder token) {
        CustomTile customTile = getTileForToken(token);
        if (customTile != null) {
            verifyCaller(customTile);
            customTile.onDialogShown();
            this.mHost.forceCollapsePanels();
            this.mServices.get(customTile).setShowingDialog(true);
        }
    }

    public void onDialogHidden(IBinder token) {
        CustomTile customTile = getTileForToken(token);
        if (customTile != null) {
            verifyCaller(customTile);
            this.mServices.get(customTile).setShowingDialog(false);
            customTile.onDialogHidden();
        }
    }

    public void onStartActivity(IBinder token) {
        CustomTile customTile = getTileForToken(token);
        if (customTile != null) {
            verifyCaller(customTile);
            this.mHost.forceCollapsePanels();
        }
    }

    public void updateStatusIcon(IBinder token, Icon icon, String contentDescription) {
        StatusBarIcon statusBarIcon;
        CustomTile customTile = getTileForToken(token);
        if (customTile != null) {
            verifyCaller(customTile);
            try {
                final ComponentName componentName = customTile.getComponent();
                String packageName = componentName.getPackageName();
                UserHandle userHandle = getCallingUserHandle();
                PackageInfo info = this.mContext.getPackageManager().getPackageInfoAsUser(packageName, 0, userHandle.getIdentifier());
                if (info.applicationInfo.isSystemApp()) {
                    if (icon != null) {
                        statusBarIcon = new StatusBarIcon(userHandle, packageName, icon, 0, 0, contentDescription);
                    } else {
                        statusBarIcon = null;
                    }
                    final StatusBarIcon statusIcon = statusBarIcon;
                    this.mMainHandler.post(new Runnable() { // from class: com.android.systemui.qs.external.TileServices.1
                        @Override // java.lang.Runnable
                        public void run() {
                            StatusBarIconController iconController = TileServices.this.mHost.getIconController();
                            iconController.setIcon(componentName.getClassName(), statusIcon);
                            iconController.setExternalIcon(componentName.getClassName());
                        }
                    });
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
    }

    public Tile getTile(IBinder token) {
        CustomTile customTile = getTileForToken(token);
        if (customTile != null) {
            verifyCaller(customTile);
            return customTile.getQsTile();
        }
        return null;
    }

    public void startUnlockAndRun(IBinder token) {
        CustomTile customTile = getTileForToken(token);
        if (customTile != null) {
            verifyCaller(customTile);
            customTile.startUnlockAndRun();
        }
    }

    public boolean isLocked() {
        KeyguardMonitor keyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
        return keyguardMonitor.isShowing();
    }

    public boolean isSecure() {
        KeyguardMonitor keyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
        return keyguardMonitor.isSecure() && keyguardMonitor.isShowing();
    }

    private CustomTile getTileForToken(IBinder token) {
        CustomTile customTile;
        synchronized (this.mServices) {
            customTile = this.mTokenMap.get(token);
        }
        return customTile;
    }

    private CustomTile getTileForComponent(ComponentName component) {
        CustomTile customTile;
        synchronized (this.mServices) {
            customTile = this.mTiles.get(component);
        }
        return customTile;
    }

    public void destroy() {
        synchronized (this.mServices) {
            this.mServices.values().forEach(new Consumer() { // from class: com.android.systemui.qs.external.-$$Lambda$TileServices$Lg27aAn4hq-sUnglRCmiW1zJ7sc
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((TileServiceManager) obj).handleDestroy();
                }
            });
            this.mContext.unregisterReceiver(this.mRequestListeningReceiver);
        }
    }
}
