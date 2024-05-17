package com.android.systemui.qs.external;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.quicksettings.IQSTileService;
import android.service.quicksettings.Tile;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.android.systemui.qs.external.TileLifecycleManager;
import java.util.List;
import java.util.Objects;
/* loaded from: classes21.dex */
public class TileServiceManager {
    public static final boolean DEBUG = true;
    private static final long MIN_BIND_TIME = 5000;
    @VisibleForTesting
    static final String PREFS_FILE = "CustomTileModes";
    private static final String TAG = "TileServiceManager";
    private static final long UNBIND_DELAY = 30000;
    private boolean mBindAllowed;
    private boolean mBindRequested;
    private boolean mBound;
    private final Handler mHandler;
    private boolean mJustBound;
    @VisibleForTesting
    final Runnable mJustBoundOver;
    private long mLastUpdate;
    private boolean mPendingBind;
    private int mPriority;
    private final TileServices mServices;
    private boolean mShowingDialog;
    private boolean mStarted;
    private final TileLifecycleManager mStateManager;
    private final Runnable mUnbind;
    private final BroadcastReceiver mUninstallReceiver;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TileServiceManager(TileServices tileServices, Handler handler, ComponentName component, Tile tile) {
        this(tileServices, handler, new TileLifecycleManager(handler, tileServices.getContext(), tileServices, tile, new Intent().setComponent(component), new UserHandle(ActivityManager.getCurrentUser())));
    }

    @VisibleForTesting
    TileServiceManager(TileServices tileServices, Handler handler, TileLifecycleManager tileLifecycleManager) {
        this.mPendingBind = true;
        this.mStarted = false;
        this.mUnbind = new Runnable() { // from class: com.android.systemui.qs.external.TileServiceManager.1
            @Override // java.lang.Runnable
            public void run() {
                if (TileServiceManager.this.mBound && !TileServiceManager.this.mBindRequested) {
                    TileServiceManager.this.unbindService();
                }
            }
        };
        this.mJustBoundOver = new Runnable() { // from class: com.android.systemui.qs.external.TileServiceManager.2
            @Override // java.lang.Runnable
            public void run() {
                TileServiceManager.this.mJustBound = false;
                TileServiceManager.this.mServices.recalculateBindAllowance();
            }
        };
        this.mUninstallReceiver = new BroadcastReceiver() { // from class: com.android.systemui.qs.external.TileServiceManager.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (!"android.intent.action.PACKAGE_REMOVED".equals(intent.getAction())) {
                    return;
                }
                Uri data = intent.getData();
                String pkgName = data.getEncodedSchemeSpecificPart();
                ComponentName component = TileServiceManager.this.mStateManager.getComponent();
                if (!Objects.equals(pkgName, component.getPackageName())) {
                    return;
                }
                if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    Intent queryIntent = new Intent("android.service.quicksettings.action.QS_TILE");
                    queryIntent.setPackage(pkgName);
                    PackageManager pm = context.getPackageManager();
                    List<ResolveInfo> services = pm.queryIntentServicesAsUser(queryIntent, 0, ActivityManager.getCurrentUser());
                    for (ResolveInfo info : services) {
                        if (Objects.equals(info.serviceInfo.packageName, component.getPackageName()) && Objects.equals(info.serviceInfo.name, component.getClassName())) {
                            return;
                        }
                    }
                }
                TileServiceManager.this.mServices.getHost().removeTile(component);
            }
        };
        this.mServices = tileServices;
        this.mHandler = handler;
        this.mStateManager = tileLifecycleManager;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        Context context = this.mServices.getContext();
        context.registerReceiverAsUser(this.mUninstallReceiver, new UserHandle(ActivityManager.getCurrentUser()), filter, null, this.mHandler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLifecycleStarted() {
        return this.mStarted;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startLifecycleManagerAndAddTile() {
        this.mStarted = true;
        ComponentName component = this.mStateManager.getComponent();
        Context context = this.mServices.getContext();
        if (!TileLifecycleManager.isTileAdded(context, component)) {
            TileLifecycleManager.setTileAdded(context, component, true);
            this.mStateManager.onTileAdded();
            this.mStateManager.flushMessagesAndUnbind();
        }
    }

    public void setTileChangeListener(TileLifecycleManager.TileChangeListener changeListener) {
        this.mStateManager.setTileChangeListener(changeListener);
    }

    public boolean isActiveTile() {
        return this.mStateManager.isActiveTile();
    }

    public void setShowingDialog(boolean dialog) {
        this.mShowingDialog = dialog;
    }

    public IQSTileService getTileService() {
        return this.mStateManager;
    }

    public IBinder getToken() {
        return this.mStateManager.getToken();
    }

    public void setBindRequested(boolean bindRequested) {
        if (this.mBindRequested == bindRequested) {
            return;
        }
        this.mBindRequested = bindRequested;
        if (this.mBindAllowed && this.mBindRequested && !this.mBound) {
            this.mHandler.removeCallbacks(this.mUnbind);
            bindService();
        } else {
            this.mServices.recalculateBindAllowance();
        }
        if (this.mBound && !this.mBindRequested) {
            this.mHandler.postDelayed(this.mUnbind, UNBIND_DELAY);
        }
    }

    public void setLastUpdate(long lastUpdate) {
        this.mLastUpdate = lastUpdate;
        if (this.mBound && isActiveTile()) {
            this.mStateManager.onStopListening();
            setBindRequested(false);
        }
        this.mServices.recalculateBindAllowance();
    }

    public void handleDestroy() {
        setBindAllowed(false);
        this.mServices.getContext().unregisterReceiver(this.mUninstallReceiver);
        this.mStateManager.handleDestroy();
    }

    public void setBindAllowed(boolean allowed) {
        if (this.mBindAllowed == allowed) {
            return;
        }
        this.mBindAllowed = allowed;
        if (!this.mBindAllowed && this.mBound) {
            unbindService();
        } else if (this.mBindAllowed && this.mBindRequested && !this.mBound) {
            bindService();
        }
    }

    public boolean hasPendingBind() {
        return this.mPendingBind;
    }

    public void clearPendingBind() {
        this.mPendingBind = false;
    }

    private void bindService() {
        if (this.mBound) {
            Log.e(TAG, "Service already bound");
            return;
        }
        this.mPendingBind = true;
        this.mBound = true;
        this.mJustBound = true;
        this.mHandler.postDelayed(this.mJustBoundOver, MIN_BIND_TIME);
        this.mStateManager.setBindService(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindService() {
        if (!this.mBound) {
            Log.e(TAG, "Service not bound");
            return;
        }
        this.mBound = false;
        this.mJustBound = false;
        this.mStateManager.setBindService(false);
    }

    public void calculateBindPriority(long currentTime) {
        if (this.mStateManager.hasPendingClick()) {
            this.mPriority = Integer.MAX_VALUE;
        } else if (this.mShowingDialog) {
            this.mPriority = 2147483646;
        } else if (this.mJustBound) {
            this.mPriority = 2147483645;
        } else if (!this.mBindRequested) {
            this.mPriority = Integer.MIN_VALUE;
        } else {
            long timeSinceUpdate = currentTime - this.mLastUpdate;
            if (timeSinceUpdate > 2147483644) {
                this.mPriority = 2147483644;
            } else {
                this.mPriority = (int) timeSinceUpdate;
            }
        }
    }

    public int getBindPriority() {
        return this.mPriority;
    }
}
