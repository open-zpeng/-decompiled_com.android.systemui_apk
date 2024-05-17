package com.android.systemui.biometrics;
/* loaded from: classes21.dex */
public interface DialogViewCallback {
    void onErrorShown();

    void onNegativePressed();

    void onPositivePressed();

    void onTryAgainPressed();

    void onUserCanceled();
}
