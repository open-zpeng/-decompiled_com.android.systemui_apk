package com.android.keyguard;

import android.graphics.Bitmap;
import android.hardware.biometrics.BiometricSourceType;
import android.os.SystemClock;
import com.android.internal.telephony.IccCardConstants;
import com.android.keyguard.KeyguardUpdateMonitor;
import java.util.TimeZone;
/* loaded from: classes19.dex */
public class KeyguardUpdateMonitorCallback {
    private static final long VISIBILITY_CHANGED_COLLAPSE_MS = 1000;
    private boolean mShowing;
    private long mVisibilityChangedCalled;

    public void onRefreshBatteryInfo(KeyguardUpdateMonitor.BatteryStatus status) {
    }

    public void onTimeChanged() {
    }

    public void onTimeZoneChanged(TimeZone timeZone) {
    }

    public void onRefreshCarrierInfo() {
    }

    public void onRingerModeChanged(int state) {
    }

    public void onPhoneStateChanged(int phoneState) {
    }

    public void onKeyguardVisibilityChanged(boolean showing) {
    }

    public void onKeyguardVisibilityChangedRaw(boolean showing) {
        long now = SystemClock.elapsedRealtime();
        if (showing == this.mShowing && now - this.mVisibilityChangedCalled < 1000) {
            return;
        }
        onKeyguardVisibilityChanged(showing);
        this.mVisibilityChangedCalled = now;
        this.mShowing = showing;
    }

    public void onKeyguardBouncerChanged(boolean bouncer) {
    }

    public void onClockVisibilityChanged() {
    }

    public void onDeviceProvisioned() {
    }

    public void onDevicePolicyManagerStateChanged() {
    }

    public void onUserSwitching(int userId) {
    }

    public void onUserSwitchComplete(int userId) {
    }

    public void onTelephonyCapable(boolean capable) {
    }

    public void onSimStateChanged(int subId, int slotId, IccCardConstants.State simState) {
    }

    public void onUserInfoChanged(int userId) {
    }

    public void onUserUnlocked() {
    }

    public void onBootCompleted() {
    }

    public void onEmergencyCallAction() {
    }

    public void onSetBackground(Bitmap bitmap) {
    }

    @Deprecated
    public void onStartedWakingUp() {
    }

    @Deprecated
    public void onStartedGoingToSleep(int why) {
    }

    @Deprecated
    public void onFinishedGoingToSleep(int why) {
    }

    @Deprecated
    public void onScreenTurnedOn() {
    }

    @Deprecated
    public void onScreenTurnedOff() {
    }

    public void onTrustChanged(int userId) {
    }

    public void onTrustManagedChanged(int userId) {
    }

    public void onTrustGrantedWithFlags(int flags, int userId) {
    }

    public void onBiometricAcquired(BiometricSourceType biometricSourceType) {
    }

    public void onBiometricAuthFailed(BiometricSourceType biometricSourceType) {
    }

    public void onBiometricAuthenticated(int userId, BiometricSourceType biometricSourceType) {
    }

    public void onBiometricHelp(int msgId, String helpString, BiometricSourceType biometricSourceType) {
    }

    public void onBiometricError(int msgId, String errString, BiometricSourceType biometricSourceType) {
    }

    public void onFaceUnlockStateChanged(boolean running, int userId) {
    }

    public void onBiometricRunningStateChanged(boolean running, BiometricSourceType biometricSourceType) {
    }

    public void onStrongAuthStateChanged(int userId) {
    }

    public void onHasLockscreenWallpaperChanged(boolean hasLockscreenWallpaper) {
    }

    public void onDreamingStateChanged(boolean dreaming) {
    }

    public void onTrustAgentErrorMessage(CharSequence message) {
    }

    public void onLogoutEnabledChanged() {
    }

    public void onBiometricsCleared() {
    }
}
