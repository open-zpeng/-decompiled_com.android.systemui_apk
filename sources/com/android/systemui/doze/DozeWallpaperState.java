package com.android.systemui.doze;

import android.app.IWallpaperManager;
import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.DozeParameters;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class DozeWallpaperState implements DozeMachine.Part {
    private final BiometricUnlockController mBiometricUnlockController;
    private final DozeParameters mDozeParameters;
    private boolean mIsAmbientMode;
    private final IWallpaperManager mWallpaperManagerService;
    private static final String TAG = "DozeWallpaperState";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    public DozeWallpaperState(Context context, BiometricUnlockController biometricUnlockController) {
        this(IWallpaperManager.Stub.asInterface(ServiceManager.getService("wallpaper")), biometricUnlockController, DozeParameters.getInstance(context));
    }

    @VisibleForTesting
    DozeWallpaperState(IWallpaperManager wallpaperManagerService, BiometricUnlockController biometricUnlockController, DozeParameters parameters) {
        this.mWallpaperManagerService = wallpaperManagerService;
        this.mBiometricUnlockController = biometricUnlockController;
        this.mDozeParameters = parameters;
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void transitionTo(DozeMachine.State oldState, DozeMachine.State newState) {
        boolean isAmbientMode;
        boolean wakingUpFromPulse;
        switch (newState) {
            case DOZE:
            case DOZE_AOD:
            case DOZE_AOD_PAUSING:
            case DOZE_AOD_PAUSED:
            case DOZE_REQUEST_PULSE:
            case DOZE_PULSE_DONE:
            case DOZE_PULSING:
                isAmbientMode = true;
                break;
            default:
                isAmbientMode = false;
                break;
        }
        if (isAmbientMode) {
            wakingUpFromPulse = this.mDozeParameters.shouldControlScreenOff();
        } else {
            boolean wakingUpFromPulse2 = oldState == DozeMachine.State.DOZE_PULSING && newState == DozeMachine.State.FINISH;
            boolean fastDisplay = !this.mDozeParameters.getDisplayNeedsBlanking();
            wakingUpFromPulse = (fastDisplay && !this.mBiometricUnlockController.unlockedByWakeAndUnlock()) || wakingUpFromPulse2;
        }
        if (isAmbientMode != this.mIsAmbientMode) {
            this.mIsAmbientMode = isAmbientMode;
            if (this.mWallpaperManagerService != null) {
                long duration = wakingUpFromPulse ? 500L : 0L;
                try {
                    if (DEBUG) {
                        Log.i(TAG, "AOD wallpaper state changed to: " + this.mIsAmbientMode + ", animationDuration: " + duration);
                    }
                    this.mWallpaperManagerService.setInAmbientMode(this.mIsAmbientMode, duration);
                } catch (RemoteException e) {
                    Log.w(TAG, "Cannot notify state to WallpaperManagerService: " + this.mIsAmbientMode);
                }
            }
        }
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void dump(PrintWriter pw) {
        pw.println("DozeWallpaperState:");
        pw.println(" isAmbientMode: " + this.mIsAmbientMode);
        StringBuilder sb = new StringBuilder();
        sb.append(" hasWallpaperService: ");
        sb.append(this.mWallpaperManagerService != null);
        pw.println(sb.toString());
    }
}
