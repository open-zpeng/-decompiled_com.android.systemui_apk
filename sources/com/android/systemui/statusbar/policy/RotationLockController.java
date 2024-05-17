package com.android.systemui.statusbar.policy;
/* loaded from: classes21.dex */
public interface RotationLockController extends Listenable, CallbackController<RotationLockControllerCallback> {

    /* loaded from: classes21.dex */
    public interface RotationLockControllerCallback {
        void onRotationLockStateChanged(boolean z, boolean z2);
    }

    int getRotationLockOrientation();

    boolean isRotationLockAffordanceVisible();

    boolean isRotationLocked();

    void setRotationLocked(boolean z);

    void setRotationLockedAtAngle(boolean z, int i);
}
