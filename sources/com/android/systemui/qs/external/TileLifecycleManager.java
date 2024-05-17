package com.android.systemui.qs.external;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.quicksettings.IQSService;
import android.service.quicksettings.IQSTileService;
import android.service.quicksettings.Tile;
import android.util.ArraySet;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes21.dex */
public class TileLifecycleManager extends BroadcastReceiver implements IQSTileService, ServiceConnection, IBinder.DeathRecipient {
    public static final boolean DEBUG = false;
    private static final int DEFAULT_BIND_RETRY_DELAY = 1000;
    private static final int MAX_BIND_RETRIES = 5;
    private static final int MSG_ON_ADDED = 0;
    private static final int MSG_ON_CLICK = 2;
    private static final int MSG_ON_REMOVED = 1;
    private static final int MSG_ON_UNLOCK_COMPLETE = 3;
    private static final String TAG = "TileLifecycleManager";
    private static final String TILES = "tiles_prefs";
    private int mBindRetryDelay;
    private int mBindTryCount;
    private boolean mBound;
    private TileChangeListener mChangeListener;
    private IBinder mClickBinder;
    private final Context mContext;
    private final Handler mHandler;
    private final Intent mIntent;
    private boolean mIsBound;
    private boolean mListening;
    private final PackageManagerAdapter mPackageManagerAdapter;
    private Set<Integer> mQueuedMessages;
    boolean mReceiverRegistered;
    private final IBinder mToken;
    private boolean mUnbindImmediate;
    private final UserHandle mUser;
    private QSTileServiceWrapper mWrapper;

    /* loaded from: classes21.dex */
    public interface TileChangeListener {
        void onTileChanged(ComponentName componentName);
    }

    public TileLifecycleManager(Handler handler, Context context, IQSService service, Tile tile, Intent intent, UserHandle user) {
        this(handler, context, service, tile, intent, user, new PackageManagerAdapter(context));
    }

    @VisibleForTesting
    TileLifecycleManager(Handler handler, Context context, IQSService service, Tile tile, Intent intent, UserHandle user, PackageManagerAdapter packageManagerAdapter) {
        this.mToken = new Binder();
        this.mQueuedMessages = new ArraySet();
        this.mBindRetryDelay = 1000;
        this.mContext = context;
        this.mHandler = handler;
        this.mIntent = intent;
        this.mIntent.putExtra("service", service.asBinder());
        this.mIntent.putExtra("token", this.mToken);
        this.mUser = user;
        this.mPackageManagerAdapter = packageManagerAdapter;
    }

    public ComponentName getComponent() {
        return this.mIntent.getComponent();
    }

    public boolean hasPendingClick() {
        boolean contains;
        synchronized (this.mQueuedMessages) {
            contains = this.mQueuedMessages.contains(2);
        }
        return contains;
    }

    public void setBindRetryDelay(int delayMs) {
        this.mBindRetryDelay = delayMs;
    }

    public boolean isActiveTile() {
        try {
            ServiceInfo info = this.mPackageManagerAdapter.getServiceInfo(this.mIntent.getComponent(), 8320);
            if (info.metaData != null) {
                return info.metaData.getBoolean("android.service.quicksettings.ACTIVE_TILE", false);
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void flushMessagesAndUnbind() {
        this.mUnbindImmediate = true;
        setBindService(true);
    }

    public void setBindService(boolean bind) {
        if (this.mBound && this.mUnbindImmediate) {
            this.mUnbindImmediate = false;
            return;
        }
        this.mBound = bind;
        if (bind) {
            if (this.mBindTryCount == 5) {
                startPackageListening();
                return;
            } else if (!checkComponentState()) {
                return;
            } else {
                this.mBindTryCount++;
                try {
                    this.mIsBound = this.mContext.bindServiceAsUser(this.mIntent, this, 34603041, this.mUser);
                    return;
                } catch (SecurityException e) {
                    Log.e(TAG, "Failed to bind to service", e);
                    this.mIsBound = false;
                    return;
                }
            }
        }
        this.mBindTryCount = 0;
        this.mWrapper = null;
        if (this.mIsBound) {
            this.mContext.unbindService(this);
            this.mIsBound = false;
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mBindTryCount = 0;
        QSTileServiceWrapper wrapper = new QSTileServiceWrapper(IQSTileService.Stub.asInterface(service));
        try {
            service.linkToDeath(this, 0);
        } catch (RemoteException e) {
        }
        this.mWrapper = wrapper;
        handlePendingMessages();
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        handleDeath();
    }

    private void handlePendingMessages() {
        ArraySet<Integer> queue;
        synchronized (this.mQueuedMessages) {
            queue = new ArraySet<>(this.mQueuedMessages);
            this.mQueuedMessages.clear();
        }
        if (queue.contains(0)) {
            onTileAdded();
        }
        if (this.mListening) {
            onStartListening();
        }
        if (queue.contains(2)) {
            if (!this.mListening) {
                Log.w(TAG, "Managed to get click on non-listening state...");
            } else {
                onClick(this.mClickBinder);
            }
        }
        if (queue.contains(3)) {
            if (!this.mListening) {
                Log.w(TAG, "Managed to get unlock on non-listening state...");
            } else {
                onUnlockComplete();
            }
        }
        if (queue.contains(1)) {
            if (this.mListening) {
                Log.w(TAG, "Managed to get remove in listening state...");
                onStopListening();
            }
            onTileRemoved();
        }
        if (this.mUnbindImmediate) {
            this.mUnbindImmediate = false;
            setBindService(false);
        }
    }

    public void handleDestroy() {
        if (this.mReceiverRegistered) {
            stopPackageListening();
        }
    }

    private void handleDeath() {
        if (this.mWrapper == null) {
            return;
        }
        this.mWrapper = null;
        if (this.mBound && checkComponentState()) {
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.qs.external.TileLifecycleManager.1
                @Override // java.lang.Runnable
                public void run() {
                    if (TileLifecycleManager.this.mBound) {
                        TileLifecycleManager.this.setBindService(true);
                    }
                }
            }, this.mBindRetryDelay);
        }
    }

    private boolean checkComponentState() {
        if (!isPackageAvailable() || !isComponentAvailable()) {
            startPackageListening();
            return false;
        }
        return true;
    }

    private void startPackageListening() {
        IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addDataScheme("package");
        this.mContext.registerReceiverAsUser(this, this.mUser, filter, null, this.mHandler);
        this.mContext.registerReceiverAsUser(this, this.mUser, new IntentFilter("android.intent.action.USER_UNLOCKED"), null, this.mHandler);
        this.mReceiverRegistered = true;
    }

    private void stopPackageListening() {
        this.mContext.unregisterReceiver(this);
        this.mReceiverRegistered = false;
    }

    public void setTileChangeListener(TileChangeListener changeListener) {
        this.mChangeListener = changeListener;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        TileChangeListener tileChangeListener;
        if (!"android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
            Uri data = intent.getData();
            String pkgName = data.getEncodedSchemeSpecificPart();
            if (!Objects.equals(pkgName, this.mIntent.getComponent().getPackageName())) {
                return;
            }
        }
        if ("android.intent.action.PACKAGE_CHANGED".equals(intent.getAction()) && (tileChangeListener = this.mChangeListener) != null) {
            tileChangeListener.onTileChanged(this.mIntent.getComponent());
        }
        stopPackageListening();
        if (this.mBound) {
            setBindService(true);
        }
    }

    private boolean isComponentAvailable() {
        this.mIntent.getComponent().getPackageName();
        try {
            ServiceInfo si = this.mPackageManagerAdapter.getServiceInfo(this.mIntent.getComponent(), 0, this.mUser.getIdentifier());
            return si != null;
        } catch (RemoteException e) {
            return false;
        }
    }

    private boolean isPackageAvailable() {
        String packageName = this.mIntent.getComponent().getPackageName();
        try {
            this.mPackageManagerAdapter.getPackageInfoAsUser(packageName, 0, this.mUser.getIdentifier());
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Package not available: " + packageName);
            return false;
        }
    }

    private void queueMessage(int message) {
        synchronized (this.mQueuedMessages) {
            this.mQueuedMessages.add(Integer.valueOf(message));
        }
    }

    public void onTileAdded() {
        QSTileServiceWrapper qSTileServiceWrapper = this.mWrapper;
        if (qSTileServiceWrapper == null || !qSTileServiceWrapper.onTileAdded()) {
            queueMessage(0);
            handleDeath();
        }
    }

    public void onTileRemoved() {
        QSTileServiceWrapper qSTileServiceWrapper = this.mWrapper;
        if (qSTileServiceWrapper == null || !qSTileServiceWrapper.onTileRemoved()) {
            queueMessage(1);
            handleDeath();
        }
    }

    public void onStartListening() {
        this.mListening = true;
        QSTileServiceWrapper qSTileServiceWrapper = this.mWrapper;
        if (qSTileServiceWrapper != null && !qSTileServiceWrapper.onStartListening()) {
            handleDeath();
        }
    }

    public void onStopListening() {
        this.mListening = false;
        QSTileServiceWrapper qSTileServiceWrapper = this.mWrapper;
        if (qSTileServiceWrapper != null && !qSTileServiceWrapper.onStopListening()) {
            handleDeath();
        }
    }

    public void onClick(IBinder iBinder) {
        QSTileServiceWrapper qSTileServiceWrapper = this.mWrapper;
        if (qSTileServiceWrapper == null || !qSTileServiceWrapper.onClick(iBinder)) {
            this.mClickBinder = iBinder;
            queueMessage(2);
            handleDeath();
        }
    }

    public void onUnlockComplete() {
        QSTileServiceWrapper qSTileServiceWrapper = this.mWrapper;
        if (qSTileServiceWrapper == null || !qSTileServiceWrapper.onUnlockComplete()) {
            queueMessage(3);
            handleDeath();
        }
    }

    public IBinder asBinder() {
        QSTileServiceWrapper qSTileServiceWrapper = this.mWrapper;
        if (qSTileServiceWrapper != null) {
            return qSTileServiceWrapper.asBinder();
        }
        return null;
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        handleDeath();
    }

    public IBinder getToken() {
        return this.mToken;
    }

    public static boolean isTileAdded(Context context, ComponentName component) {
        return context.getSharedPreferences(TILES, 0).getBoolean(component.flattenToString(), false);
    }

    public static void setTileAdded(Context context, ComponentName component, boolean added) {
        context.getSharedPreferences(TILES, 0).edit().putBoolean(component.flattenToString(), added).commit();
    }
}
