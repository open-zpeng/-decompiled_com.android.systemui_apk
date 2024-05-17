package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.UserManager;
import android.util.Log;
import com.android.systemui.statusbar.policy.HotspotController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class HotspotControllerImpl implements HotspotController, WifiManager.SoftApCallback {
    private final ArrayList<HotspotController.Callback> mCallbacks = new ArrayList<>();
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private int mHotspotState;
    private final Handler mMainHandler;
    private int mNumConnectedDevices;
    private boolean mWaitingForTerminalState;
    private final WifiManager mWifiManager;
    private static final String TAG = "HotspotController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    @Inject
    public HotspotControllerImpl(Context context, @Named("main_handler") Handler mainHandler) {
        this.mContext = context;
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mMainHandler = mainHandler;
    }

    @Override // com.android.systemui.statusbar.policy.HotspotController
    public boolean isHotspotSupported() {
        return this.mConnectivityManager.isTetheringSupported() && this.mConnectivityManager.getTetherableWifiRegexs().length != 0 && UserManager.get(this.mContext).isUserAdmin(ActivityManager.getCurrentUser());
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("HotspotController state:");
        pw.print("  mHotspotState=");
        pw.println(stateToString(this.mHotspotState));
        pw.print("  mNumConnectedDevices=");
        pw.println(this.mNumConnectedDevices);
        pw.print("  mWaitingForTerminalState=");
        pw.println(this.mWaitingForTerminalState);
    }

    private static String stateToString(int hotspotState) {
        switch (hotspotState) {
            case 10:
                return "DISABLING";
            case 11:
                return "DISABLED";
            case 12:
                return "ENABLING";
            case 13:
                return "ENABLED";
            case 14:
                return "FAILED";
            default:
                return null;
        }
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(final HotspotController.Callback callback) {
        synchronized (this.mCallbacks) {
            if (callback != null) {
                if (!this.mCallbacks.contains(callback)) {
                    if (DEBUG) {
                        Log.d(TAG, "addCallback " + callback);
                    }
                    this.mCallbacks.add(callback);
                    if (this.mWifiManager != null) {
                        if (this.mCallbacks.size() == 1) {
                            this.mWifiManager.registerSoftApCallback(this, this.mMainHandler);
                        } else {
                            this.mMainHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$HotspotControllerImpl$C17PPPxxCR-pTmr2izVaDhyC9AQ
                                @Override // java.lang.Runnable
                                public final void run() {
                                    HotspotControllerImpl.this.lambda$addCallback$0$HotspotControllerImpl(callback);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$addCallback$0$HotspotControllerImpl(HotspotController.Callback callback) {
        callback.onHotspotChanged(isHotspotEnabled(), this.mNumConnectedDevices);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(HotspotController.Callback callback) {
        if (callback == null) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "removeCallback " + callback);
        }
        synchronized (this.mCallbacks) {
            this.mCallbacks.remove(callback);
            if (this.mCallbacks.isEmpty() && this.mWifiManager != null) {
                this.mWifiManager.unregisterSoftApCallback(this);
            }
        }
    }

    @Override // com.android.systemui.statusbar.policy.HotspotController
    public boolean isHotspotEnabled() {
        return this.mHotspotState == 13;
    }

    @Override // com.android.systemui.statusbar.policy.HotspotController
    public boolean isHotspotTransient() {
        return this.mWaitingForTerminalState || this.mHotspotState == 12;
    }

    @Override // com.android.systemui.statusbar.policy.HotspotController
    public void setHotspotEnabled(boolean enabled) {
        if (this.mWaitingForTerminalState) {
            if (DEBUG) {
                Log.d(TAG, "Ignoring setHotspotEnabled; waiting for terminal state.");
            }
        } else if (!enabled) {
            this.mConnectivityManager.stopTethering(0);
        } else {
            this.mWaitingForTerminalState = true;
            if (DEBUG) {
                Log.d(TAG, "Starting tethering");
            }
            this.mConnectivityManager.startTethering(0, false, new ConnectivityManager.OnStartTetheringCallback() { // from class: com.android.systemui.statusbar.policy.HotspotControllerImpl.1
                public void onTetheringFailed() {
                    if (HotspotControllerImpl.DEBUG) {
                        Log.d(HotspotControllerImpl.TAG, "onTetheringFailed");
                    }
                    HotspotControllerImpl.this.maybeResetSoftApState();
                    HotspotControllerImpl.this.fireHotspotChangedCallback();
                }
            });
        }
    }

    @Override // com.android.systemui.statusbar.policy.HotspotController
    public int getNumConnectedDevices() {
        return this.mNumConnectedDevices;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireHotspotChangedCallback() {
        synchronized (this.mCallbacks) {
            Iterator<HotspotController.Callback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                HotspotController.Callback callback = it.next();
                callback.onHotspotChanged(isHotspotEnabled(), this.mNumConnectedDevices);
            }
        }
    }

    public void onStateChanged(int state, int failureReason) {
        this.mHotspotState = state;
        maybeResetSoftApState();
        if (!isHotspotEnabled()) {
            this.mNumConnectedDevices = 0;
        }
        fireHotspotChangedCallback();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeResetSoftApState() {
        if (!this.mWaitingForTerminalState) {
            return;
        }
        int i = this.mHotspotState;
        if (i != 11 && i != 13) {
            if (i == 14) {
                this.mConnectivityManager.stopTethering(0);
            } else {
                return;
            }
        }
        this.mWaitingForTerminalState = false;
    }

    public void onNumClientsChanged(int numConnectedDevices) {
        this.mNumConnectedDevices = numConnectedDevices;
        fireHotspotChangedCallback();
    }
}
