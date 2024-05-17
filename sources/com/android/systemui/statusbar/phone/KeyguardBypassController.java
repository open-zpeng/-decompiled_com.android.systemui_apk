package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.hardware.biometrics.BiometricSourceType;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.tuner.TunerService;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.inject.Singleton;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: KeyguardBypassController.kt */
@Singleton
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u0000 22\u00020\u0001:\u00012B'\b\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\u0006\u0010(\u001a\u00020\fJ\u0006\u0010)\u001a\u00020\fJ\u000e\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020-J\u0006\u0010.\u001a\u00020+J\u000e\u0010/\u001a\u00020\f2\u0006\u00100\u001a\u00020\u001bJ\u0006\u00101\u001a\u00020+R\u001a\u0010\u000b\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R \u0010\u0012\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\f8F@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000eR\u000e\u0010\u0014\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u0015\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u000e\"\u0004\b\u0016\u0010\u0010R\u001a\u0010\u0017\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u000e\"\u0004\b\u0019\u0010\u0010R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R$\u0010\u001d\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\f@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u000e\"\u0004\b\u001f\u0010\u0010R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010 \u001a\u00020!X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u000e\u0010&\u001a\u00020'X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00063"}, d2 = {"Lcom/android/systemui/statusbar/phone/KeyguardBypassController;", "", "context", "Landroid/content/Context;", "tunerService", "Lcom/android/systemui/tuner/TunerService;", "statusBarStateController", "Lcom/android/systemui/plugins/statusbar/StatusBarStateController;", "lockscreenUserManager", "Lcom/android/systemui/statusbar/NotificationLockscreenUserManager;", "(Landroid/content/Context;Lcom/android/systemui/tuner/TunerService;Lcom/android/systemui/plugins/statusbar/StatusBarStateController;Lcom/android/systemui/statusbar/NotificationLockscreenUserManager;)V", "bouncerShowing", "", "getBouncerShowing", "()Z", "setBouncerShowing", "(Z)V", "<set-?>", "bypassEnabled", "getBypassEnabled", "hasFaceFeature", "isPulseExpanding", "setPulseExpanding", "launchingAffordance", "getLaunchingAffordance", "setLaunchingAffordance", "pendingUnlockType", "Landroid/hardware/biometrics/BiometricSourceType;", VuiConstants.ELEMENT_VALUE, "qSExpanded", "getQSExpanded", "setQSExpanded", "unlockController", "Lcom/android/systemui/statusbar/phone/BiometricUnlockController;", "getUnlockController", "()Lcom/android/systemui/statusbar/phone/BiometricUnlockController;", "setUnlockController", "(Lcom/android/systemui/statusbar/phone/BiometricUnlockController;)V", "unlockMethodCache", "Lcom/android/systemui/statusbar/phone/UnlockMethodCache;", "canBypass", "canPlaySubtleWindowAnimations", "dump", "", "pw", "Ljava/io/PrintWriter;", "maybePerformPendingUnlock", "onBiometricAuthenticated", "biometricSourceType", "onStartedGoingToSleep", "Companion", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class KeyguardBypassController {
    public static final int BYPASS_PANEL_FADE_DURATION = 67;
    public static final Companion Companion = new Companion(null);
    private boolean bouncerShowing;
    private boolean bypassEnabled;
    private boolean hasFaceFeature;
    private boolean isPulseExpanding;
    private boolean launchingAffordance;
    private BiometricSourceType pendingUnlockType;
    private boolean qSExpanded;
    private final StatusBarStateController statusBarStateController;
    @NotNull
    public BiometricUnlockController unlockController;
    private final UnlockMethodCache unlockMethodCache;

    @NotNull
    public final BiometricUnlockController getUnlockController() {
        BiometricUnlockController biometricUnlockController = this.unlockController;
        if (biometricUnlockController == null) {
            Intrinsics.throwUninitializedPropertyAccessException("unlockController");
        }
        return biometricUnlockController;
    }

    public final void setUnlockController(@NotNull BiometricUnlockController biometricUnlockController) {
        Intrinsics.checkParameterIsNotNull(biometricUnlockController, "<set-?>");
        this.unlockController = biometricUnlockController;
    }

    public final boolean isPulseExpanding() {
        return this.isPulseExpanding;
    }

    public final void setPulseExpanding(boolean z) {
        this.isPulseExpanding = z;
    }

    public final boolean getBypassEnabled() {
        return this.bypassEnabled && this.unlockMethodCache.isFaceAuthEnabled();
    }

    public final boolean getBouncerShowing() {
        return this.bouncerShowing;
    }

    public final void setBouncerShowing(boolean z) {
        this.bouncerShowing = z;
    }

    public final boolean getLaunchingAffordance() {
        return this.launchingAffordance;
    }

    public final void setLaunchingAffordance(boolean z) {
        this.launchingAffordance = z;
    }

    public final boolean getQSExpanded() {
        return this.qSExpanded;
    }

    public final void setQSExpanded(boolean value) {
        boolean changed = this.qSExpanded != value;
        this.qSExpanded = value;
        if (changed && !value) {
            maybePerformPendingUnlock();
        }
    }

    @Inject
    public KeyguardBypassController(@NotNull Context context, @NotNull final TunerService tunerService, @NotNull StatusBarStateController statusBarStateController, @NotNull NotificationLockscreenUserManager lockscreenUserManager) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(tunerService, "tunerService");
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(lockscreenUserManager, "lockscreenUserManager");
        UnlockMethodCache unlockMethodCache = UnlockMethodCache.getInstance(context);
        Intrinsics.checkExpressionValueIsNotNull(unlockMethodCache, "UnlockMethodCache.getInstance(context)");
        this.unlockMethodCache = unlockMethodCache;
        this.statusBarStateController = statusBarStateController;
        this.hasFaceFeature = context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face");
        if (!this.hasFaceFeature) {
            return;
        }
        statusBarStateController.addCallback(new StatusBarStateController.StateListener() { // from class: com.android.systemui.statusbar.phone.KeyguardBypassController.1
            @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
            public void onStateChanged(int newState) {
                if (newState != 1) {
                    KeyguardBypassController.this.pendingUnlockType = null;
                }
            }
        });
        final int dismissByDefault = context.getResources().getBoolean(17891457) ? 1 : 0;
        tunerService.addTunable(new TunerService.Tunable() { // from class: com.android.systemui.statusbar.phone.KeyguardBypassController.2
            @Override // com.android.systemui.tuner.TunerService.Tunable
            public void onTuningChanged(@Nullable String key, @Nullable String newValue) {
                KeyguardBypassController.this.bypassEnabled = tunerService.getValue(key, dismissByDefault) != 0;
            }
        }, "face_unlock_dismisses_keyguard");
        lockscreenUserManager.addUserChangedListener(new NotificationLockscreenUserManager.UserChangedListener() { // from class: com.android.systemui.statusbar.phone.KeyguardBypassController.3
            @Override // com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener
            public final void onUserChanged(int it) {
                KeyguardBypassController.this.pendingUnlockType = null;
            }
        });
    }

    public final boolean onBiometricAuthenticated(@NotNull BiometricSourceType biometricSourceType) {
        Intrinsics.checkParameterIsNotNull(biometricSourceType, "biometricSourceType");
        if (getBypassEnabled()) {
            boolean can = canBypass();
            if (!can && (this.isPulseExpanding || this.qSExpanded)) {
                this.pendingUnlockType = biometricSourceType;
            }
            return can;
        }
        return true;
    }

    public final void maybePerformPendingUnlock() {
        BiometricSourceType biometricSourceType = this.pendingUnlockType;
        if (biometricSourceType != null) {
            if (biometricSourceType == null) {
                Intrinsics.throwNpe();
            }
            if (onBiometricAuthenticated(biometricSourceType)) {
                BiometricUnlockController biometricUnlockController = this.unlockController;
                if (biometricUnlockController == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("unlockController");
                }
                biometricUnlockController.startWakeAndUnlock(this.pendingUnlockType);
                this.pendingUnlockType = null;
            }
        }
    }

    public final boolean canBypass() {
        if (getBypassEnabled()) {
            if (this.bouncerShowing) {
                return true;
            }
            return (this.statusBarStateController.getState() != 1 || this.launchingAffordance || this.isPulseExpanding || this.qSExpanded) ? false : true;
        }
        return false;
    }

    public final boolean canPlaySubtleWindowAnimations() {
        return getBypassEnabled() && this.statusBarStateController.getState() == 1 && !this.qSExpanded;
    }

    public final void onStartedGoingToSleep() {
        this.pendingUnlockType = null;
    }

    public final void dump(@NotNull PrintWriter pw) {
        Intrinsics.checkParameterIsNotNull(pw, "pw");
        pw.println("KeyguardBypassController:");
        pw.print("  pendingUnlockType: ");
        pw.println(this.pendingUnlockType);
        pw.print("  bypassEnabled: ");
        pw.println(getBypassEnabled());
        pw.print("  canBypass: ");
        pw.println(canBypass());
        pw.print("  bouncerShowing: ");
        pw.println(this.bouncerShowing);
        pw.print("  isPulseExpanding: ");
        pw.println(this.isPulseExpanding);
        pw.print("  launchingAffordance: ");
        pw.println(this.launchingAffordance);
        pw.print("  qSExpanded: ");
        pw.println(this.qSExpanded);
        pw.print("  hasFaceFeature: ");
        pw.println(this.hasFaceFeature);
    }

    /* compiled from: KeyguardBypassController.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"Lcom/android/systemui/statusbar/phone/KeyguardBypassController$Companion;", "", "()V", "BYPASS_PANEL_FADE_DURATION", "", "name"}, k = 1, mv = {1, 1, 13})
    /* loaded from: classes21.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
