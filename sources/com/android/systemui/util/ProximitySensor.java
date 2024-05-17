package com.android.systemui.util;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.android.systemui.R;
import com.android.systemui.util.ProximitySensor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class ProximitySensor {
    private static final boolean DEBUG = false;
    private static final String TAG = "ProxSensor";
    private boolean mNear;
    private final Sensor mSensor;
    private final AsyncSensorManager mSensorManager;
    private final float mThreshold;
    private final boolean mUsingBrightnessSensor;
    private SensorEventListener mSensorEventListener = new SensorEventListener() { // from class: com.android.systemui.util.ProximitySensor.1
        @Override // android.hardware.SensorEventListener
        public synchronized void onSensorChanged(SensorEvent event) {
            ProximitySensor.this.onSensorEvent(event);
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private List<ProximitySensorListener> mListeners = new ArrayList();
    private String mTag = null;

    /* loaded from: classes21.dex */
    public interface ProximitySensorListener {
        void onProximitySensorEvent(ProximityEvent proximityEvent);
    }

    @Inject
    public ProximitySensor(Context context, AsyncSensorManager sensorManager) {
        this.mSensorManager = sensorManager;
        Sensor sensor = findCustomProxSensor(context, sensorManager);
        if (sensor == null) {
            this.mUsingBrightnessSensor = false;
            sensor = sensorManager.getDefaultSensor(8);
        } else {
            this.mUsingBrightnessSensor = true;
        }
        this.mSensor = sensor;
        Sensor sensor2 = this.mSensor;
        if (sensor2 != null) {
            if (this.mUsingBrightnessSensor) {
                this.mThreshold = getBrightnessSensorThreshold(context.getResources());
                return;
            } else {
                this.mThreshold = sensor2.getMaximumRange();
                return;
            }
        }
        this.mThreshold = 0.0f;
    }

    public void setTag(String tag) {
        this.mTag = tag;
    }

    @Deprecated
    public static Sensor findCustomProxSensor(Context context, SensorManager sensorManager) {
        String sensorType = context.getString(R.string.proximity_sensor_type);
        if (sensorType.isEmpty()) {
            return null;
        }
        List<Sensor> sensorList = sensorManager.getSensorList(-1);
        for (Sensor s : sensorList) {
            if (sensorType.equals(s.getStringType())) {
                return s;
            }
        }
        return null;
    }

    @Deprecated
    public static float getBrightnessSensorThreshold(Resources resources) {
        return resources.getFloat(R.dimen.proximity_sensor_threshold);
    }

    public boolean getSensorAvailable() {
        return this.mSensor != null;
    }

    public boolean register(ProximitySensorListener listener) {
        if (!getSensorAvailable()) {
            return false;
        }
        logDebug("using brightness sensor? " + this.mUsingBrightnessSensor);
        this.mListeners.add(listener);
        if (this.mListeners.size() == 1) {
            logDebug("registering sensor listener");
            this.mSensorManager.registerListener(this.mSensorEventListener, this.mSensor, 1);
        }
        return true;
    }

    public void unregister(ProximitySensorListener listener) {
        this.mListeners.remove(listener);
        if (this.mListeners.size() == 0) {
            logDebug("unregistering sensor listener");
            this.mSensorManager.unregisterListener(this.mSensorEventListener);
        }
    }

    public boolean isNear() {
        return getSensorAvailable() && this.mNear;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSensorEvent(final SensorEvent event) {
        if (this.mUsingBrightnessSensor) {
            this.mNear = event.values[0] <= this.mThreshold;
        } else {
            this.mNear = event.values[0] < this.mThreshold;
        }
        this.mListeners.forEach(new Consumer() { // from class: com.android.systemui.util.-$$Lambda$ProximitySensor$u0k_WP8tod8dCJ2LgCU12LZcmjA
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ProximitySensor.this.lambda$onSensorEvent$0$ProximitySensor(event, (ProximitySensor.ProximitySensorListener) obj);
            }
        });
    }

    public /* synthetic */ void lambda$onSensorEvent$0$ProximitySensor(SensorEvent event, ProximitySensorListener proximitySensorListener) {
        proximitySensorListener.onProximitySensorEvent(new ProximityEvent(this.mNear, event.timestamp));
    }

    /* loaded from: classes21.dex */
    public static class ProximityEvent {
        private final boolean mNear;
        private final long mTimestampNs;

        public ProximityEvent(boolean near, long timestampNs) {
            this.mNear = near;
            this.mTimestampNs = timestampNs;
        }

        public boolean getNear() {
            return this.mNear;
        }

        public long getTimestampNs() {
            return this.mTimestampNs;
        }
    }

    private void logDebug(String msg) {
    }
}
