package com.android.systemui.statusbar.policy;
/* loaded from: classes21.dex */
public interface SensorPrivacyController extends CallbackController<OnSensorPrivacyChangedListener> {

    /* loaded from: classes21.dex */
    public interface OnSensorPrivacyChangedListener {
        void onSensorPrivacyChanged(boolean z);
    }

    boolean isSensorPrivacyEnabled();
}
