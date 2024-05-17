package com.android.systemui.plugins;

import com.android.systemui.plugins.annotations.ProvidesInterface;
@ProvidesInterface(action = SensorManagerPlugin.ACTION, version = 1)
/* loaded from: classes21.dex */
public interface SensorManagerPlugin extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_SENSOR_MANAGER";
    public static final int VERSION = 1;

    /* loaded from: classes21.dex */
    public interface SensorEventListener {
        void onSensorChanged(SensorEvent sensorEvent);
    }

    void registerListener(Sensor sensor, SensorEventListener sensorEventListener);

    void unregisterListener(Sensor sensor, SensorEventListener sensorEventListener);

    /* loaded from: classes21.dex */
    public static class Sensor {
        public static final int TYPE_SKIP_STATUS = 4;
        public static final int TYPE_SWIPE = 3;
        public static final int TYPE_WAKE_DISPLAY = 2;
        public static final int TYPE_WAKE_LOCK_SCREEN = 1;
        private int mType;

        public Sensor(int type) {
            this.mType = type;
        }

        public int getType() {
            return this.mType;
        }

        public String toString() {
            return "{PluginSensor type=\"" + this.mType + "\"}";
        }
    }

    /* loaded from: classes21.dex */
    public static class SensorEvent {
        Sensor mSensor;
        float[] mValues;
        int mVendorType;

        public SensorEvent(Sensor sensor, int vendorType) {
            this(sensor, vendorType, null);
        }

        public SensorEvent(Sensor sensor, int vendorType, float[] values) {
            this.mSensor = sensor;
            this.mVendorType = vendorType;
            this.mValues = values;
        }

        public Sensor getSensor() {
            return this.mSensor;
        }

        public float[] getValues() {
            return this.mValues;
        }

        public int getVendorType() {
            return this.mVendorType;
        }
    }
}
