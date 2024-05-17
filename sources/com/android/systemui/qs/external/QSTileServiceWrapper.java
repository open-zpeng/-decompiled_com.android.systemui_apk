package com.android.systemui.qs.external;

import android.os.IBinder;
import android.service.quicksettings.IQSTileService;
import android.util.Log;
/* loaded from: classes21.dex */
public class QSTileServiceWrapper {
    private static final String TAG = "IQSTileServiceWrapper";
    private final IQSTileService mService;

    public QSTileServiceWrapper(IQSTileService service) {
        this.mService = service;
    }

    public IBinder asBinder() {
        return this.mService.asBinder();
    }

    public boolean onTileAdded() {
        try {
            this.mService.onTileAdded();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Caught exception from TileService", e);
            return false;
        }
    }

    public boolean onTileRemoved() {
        try {
            this.mService.onTileRemoved();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Caught exception from TileService", e);
            return false;
        }
    }

    public boolean onStartListening() {
        try {
            this.mService.onStartListening();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Caught exception from TileService", e);
            return false;
        }
    }

    public boolean onStopListening() {
        try {
            this.mService.onStopListening();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Caught exception from TileService", e);
            return false;
        }
    }

    public boolean onClick(IBinder token) {
        try {
            this.mService.onClick(token);
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Caught exception from TileService", e);
            return false;
        }
    }

    public boolean onUnlockComplete() {
        try {
            this.mService.onUnlockComplete();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Caught exception from TileService", e);
            return false;
        }
    }

    public IQSTileService getService() {
        return this.mService;
    }
}
