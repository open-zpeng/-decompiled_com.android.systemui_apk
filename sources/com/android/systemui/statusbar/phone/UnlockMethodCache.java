package com.android.systemui.statusbar.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Build;
import android.os.Trace;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes21.dex */
public class UnlockMethodCache {
    private static final String AUTH_BROADCAST_KEY = "debug_trigger_auth";
    private static final boolean DEBUG_AUTH_WITH_ADB = false;
    private static UnlockMethodCache sInstance;
    private boolean mCanSkipBouncer;
    private boolean mFaceAuthEnabled;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final LockPatternUtils mLockPatternUtils;
    private boolean mSecure;
    private boolean mTrustManaged;
    private boolean mTrusted;
    private final ArrayList<OnUnlockMethodChangedListener> mListeners = new ArrayList<>();
    private boolean mDebugUnlocked = false;
    private final KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.statusbar.phone.UnlockMethodCache.2
        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onUserSwitchComplete(int userId) {
            UnlockMethodCache.this.update(false);
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onTrustChanged(int userId) {
            UnlockMethodCache.this.update(false);
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onTrustManagedChanged(int userId) {
            UnlockMethodCache.this.update(false);
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onStartedWakingUp() {
            UnlockMethodCache.this.update(false);
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onBiometricAuthenticated(int userId, BiometricSourceType biometricSourceType) {
            Trace.beginSection("KeyguardUpdateMonitorCallback#onBiometricAuthenticated");
            if (UnlockMethodCache.this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed()) {
                UnlockMethodCache.this.update(false);
                Trace.endSection();
                return;
            }
            Trace.endSection();
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onFaceUnlockStateChanged(boolean running, int userId) {
            UnlockMethodCache.this.update(false);
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onStrongAuthStateChanged(int userId) {
            UnlockMethodCache.this.update(false);
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onScreenTurnedOff() {
            UnlockMethodCache.this.update(false);
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onKeyguardVisibilityChanged(boolean showing) {
            UnlockMethodCache.this.update(false);
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onBiometricsCleared() {
            UnlockMethodCache.this.update(false);
        }
    };

    /* loaded from: classes21.dex */
    public interface OnUnlockMethodChangedListener {
        void onUnlockMethodStateChanged();
    }

    private UnlockMethodCache(Context ctx) {
        this.mLockPatternUtils = new LockPatternUtils(ctx);
        this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(ctx);
        KeyguardUpdateMonitor.getInstance(ctx).registerCallback(this.mCallback);
        update(true);
        boolean z = Build.IS_DEBUGGABLE;
    }

    /* renamed from: com.android.systemui.statusbar.phone.UnlockMethodCache$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    class AnonymousClass1 extends BroadcastReceiver {
        AnonymousClass1() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
        }
    }

    public static UnlockMethodCache getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UnlockMethodCache(context);
        }
        return sInstance;
    }

    public boolean isMethodSecure() {
        return this.mSecure;
    }

    public boolean isTrusted() {
        return this.mTrusted;
    }

    public boolean canSkipBouncer() {
        return this.mCanSkipBouncer;
    }

    public void addListener(OnUnlockMethodChangedListener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(OnUnlockMethodChangedListener listener) {
        this.mListeners.remove(listener);
    }

    public boolean isFaceAuthEnabled() {
        return this.mFaceAuthEnabled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update(boolean updateAlways) {
        boolean canSkipBouncer;
        Trace.beginSection("UnlockMethodCache#update");
        int user = KeyguardUpdateMonitor.getCurrentUser();
        boolean secure = this.mLockPatternUtils.isSecure(user);
        boolean changed = false;
        if (!secure || this.mKeyguardUpdateMonitor.getUserCanSkipBouncer(user)) {
            canSkipBouncer = true;
        } else {
            boolean z = Build.IS_DEBUGGABLE;
            canSkipBouncer = false;
        }
        boolean trustManaged = this.mKeyguardUpdateMonitor.getUserTrustIsManaged(user);
        boolean trusted = this.mKeyguardUpdateMonitor.getUserHasTrust(user);
        boolean faceAuthEnabled = this.mKeyguardUpdateMonitor.isFaceAuthEnabledForUser(user);
        if (secure != this.mSecure || canSkipBouncer != this.mCanSkipBouncer || trustManaged != this.mTrustManaged || this.mFaceAuthEnabled != faceAuthEnabled) {
            changed = true;
        }
        if (changed || updateAlways) {
            this.mSecure = secure;
            this.mCanSkipBouncer = canSkipBouncer;
            this.mTrusted = trusted;
            this.mTrustManaged = trustManaged;
            this.mFaceAuthEnabled = faceAuthEnabled;
            notifyListeners();
        }
        Trace.endSection();
    }

    private void notifyListeners() {
        Iterator<OnUnlockMethodChangedListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            OnUnlockMethodChangedListener listener = it.next();
            listener.onUnlockMethodStateChanged();
        }
    }

    public void dump(PrintWriter pw) {
        pw.println("UnlockMethodCache");
        pw.println("  mSecure: " + this.mSecure);
        pw.println("  mCanSkipBouncer: " + this.mCanSkipBouncer);
        pw.println("  mTrustManaged: " + this.mTrustManaged);
        pw.println("  mTrusted: " + this.mTrusted);
        pw.println("  mDebugUnlocked: " + this.mDebugUnlocked);
        pw.println("  mFaceAuthEnabled: " + this.mFaceAuthEnabled);
    }

    public boolean isTrustManaged() {
        return this.mTrustManaged;
    }
}
