package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.hardware.SensorPrivacyManager;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class SensorPrivacyControllerImpl implements SensorPrivacyController, SensorPrivacyManager.OnSensorPrivacyChangedListener {
    private final List<SensorPrivacyController.OnSensorPrivacyChangedListener> mListeners;
    private Object mLock = new Object();
    private boolean mSensorPrivacyEnabled;
    private SensorPrivacyManager mSensorPrivacyManager;

    @Inject
    public SensorPrivacyControllerImpl(Context context) {
        this.mSensorPrivacyManager = (SensorPrivacyManager) context.getSystemService("sensor_privacy");
        this.mSensorPrivacyEnabled = this.mSensorPrivacyManager.isSensorPrivacyEnabled();
        this.mSensorPrivacyManager.addSensorPrivacyListener(this);
        this.mListeners = new ArrayList(1);
    }

    @Override // com.android.systemui.statusbar.policy.SensorPrivacyController
    public boolean isSensorPrivacyEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSensorPrivacyEnabled;
        }
        return z;
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(SensorPrivacyController.OnSensorPrivacyChangedListener listener) {
        synchronized (this.mLock) {
            this.mListeners.add(listener);
            notifyListenerLocked(listener);
        }
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(SensorPrivacyController.OnSensorPrivacyChangedListener listener) {
        synchronized (this.mLock) {
            this.mListeners.remove(listener);
        }
    }

    public void onSensorPrivacyChanged(boolean enabled) {
        synchronized (this.mLock) {
            this.mSensorPrivacyEnabled = enabled;
            for (SensorPrivacyController.OnSensorPrivacyChangedListener listener : this.mListeners) {
                notifyListenerLocked(listener);
            }
        }
    }

    private void notifyListenerLocked(SensorPrivacyController.OnSensorPrivacyChangedListener listener) {
        listener.onSensorPrivacyChanged(this.mSensorPrivacyEnabled);
    }
}
