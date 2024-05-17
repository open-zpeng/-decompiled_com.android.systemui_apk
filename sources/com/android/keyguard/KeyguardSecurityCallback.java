package com.android.keyguard;
/* loaded from: classes19.dex */
public interface KeyguardSecurityCallback {
    void dismiss(boolean z, int i);

    boolean isVerifyUnlockOnly();

    void onUserInput();

    void reportUnlockAttempt(int i, boolean z, int i2);

    void reset();

    void userActivity();

    default void onCancelClicked() {
    }
}
