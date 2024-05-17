package com.android.systemui.shared.system;

import android.graphics.Region;
import android.os.RemoteException;
import android.util.Log;
import android.view.ISystemGestureExclusionListener;
import android.view.WindowManagerGlobal;
/* loaded from: classes21.dex */
public abstract class SystemGestureExclusionListenerCompat {
    private static final String TAG = "SGEListenerCompat";
    private final int mDisplayId;
    private ISystemGestureExclusionListener mGestureExclusionListener = new ISystemGestureExclusionListener.Stub() { // from class: com.android.systemui.shared.system.SystemGestureExclusionListenerCompat.1
        public void onSystemGestureExclusionChanged(int displayId, Region systemGestureExclusion, Region unrestrictedOrNull) {
            if (displayId == SystemGestureExclusionListenerCompat.this.mDisplayId) {
                Region unrestricted = unrestrictedOrNull == null ? systemGestureExclusion : unrestrictedOrNull;
                SystemGestureExclusionListenerCompat.this.onExclusionChanged(systemGestureExclusion, unrestricted);
            }
        }
    };
    private boolean mRegistered;

    public abstract void onExclusionChanged(Region region);

    public SystemGestureExclusionListenerCompat(int displayId) {
        this.mDisplayId = displayId;
    }

    public void onExclusionChanged(Region systemGestureExclusion, Region systemGestureExclusionUnrestricted) {
        onExclusionChanged(systemGestureExclusion);
    }

    public void register() {
        if (!this.mRegistered) {
            try {
                WindowManagerGlobal.getWindowManagerService().registerSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
                this.mRegistered = true;
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to register window manager callbacks", e);
            }
        }
    }

    public void unregister() {
        if (this.mRegistered) {
            try {
                WindowManagerGlobal.getWindowManagerService().unregisterSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
                this.mRegistered = false;
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to unregister window manager callbacks", e);
            }
        }
    }
}
