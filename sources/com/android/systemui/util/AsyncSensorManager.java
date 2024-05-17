package com.android.systemui.util;

import android.content.Context;
import android.hardware.HardwareBuffer;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorDirectChannel;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.MemoryFile;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.SensorManagerPlugin;
import com.android.systemui.shared.plugins.PluginManager;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class AsyncSensorManager extends SensorManager implements PluginListener<SensorManagerPlugin> {
    private static final String TAG = "AsyncSensorManager";
    @VisibleForTesting
    final Handler mHandler;
    private final HandlerThread mHandlerThread;
    private final SensorManager mInner;
    private final List<SensorManagerPlugin> mPlugins;
    private final List<Sensor> mSensorCache;

    @Inject
    public AsyncSensorManager(Context context, PluginManager pluginManager) {
        this((SensorManager) context.getSystemService(SensorManager.class), pluginManager);
    }

    @VisibleForTesting
    AsyncSensorManager(SensorManager sensorManager, PluginManager pluginManager) {
        this.mHandlerThread = new HandlerThread("async_sensor");
        this.mInner = sensorManager;
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
        this.mSensorCache = this.mInner.getSensorList(-1);
        this.mPlugins = new ArrayList();
        pluginManager.addPluginListener((PluginListener) this, SensorManagerPlugin.class, true);
    }

    protected List<Sensor> getFullSensorList() {
        return this.mSensorCache;
    }

    protected List<Sensor> getFullDynamicSensorList() {
        return this.mInner.getSensorList(-1);
    }

    protected boolean registerListenerImpl(final SensorEventListener listener, final Sensor sensor, final int delayUs, final Handler handler, final int maxReportLatencyUs, int reservedFlags) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.util.-$$Lambda$AsyncSensorManager$d7xLBI7qZK784-fy2ffbXtJPEGA
            @Override // java.lang.Runnable
            public final void run() {
                AsyncSensorManager.this.lambda$registerListenerImpl$0$AsyncSensorManager(listener, sensor, delayUs, maxReportLatencyUs, handler);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$registerListenerImpl$0$AsyncSensorManager(SensorEventListener listener, Sensor sensor, int delayUs, int maxReportLatencyUs, Handler handler) {
        if (!this.mInner.registerListener(listener, sensor, delayUs, maxReportLatencyUs, handler)) {
            Log.e(TAG, "Registering " + listener + " for " + sensor + " failed.");
        }
    }

    protected boolean flushImpl(SensorEventListener listener) {
        return this.mInner.flush(listener);
    }

    protected SensorDirectChannel createDirectChannelImpl(MemoryFile memoryFile, HardwareBuffer hardwareBuffer) {
        throw new UnsupportedOperationException("not implemented");
    }

    protected void destroyDirectChannelImpl(SensorDirectChannel channel) {
        throw new UnsupportedOperationException("not implemented");
    }

    protected int configureDirectChannelImpl(SensorDirectChannel channel, Sensor s, int rate) {
        throw new UnsupportedOperationException("not implemented");
    }

    public /* synthetic */ void lambda$registerDynamicSensorCallbackImpl$1$AsyncSensorManager(SensorManager.DynamicSensorCallback callback, Handler handler) {
        this.mInner.registerDynamicSensorCallback(callback, handler);
    }

    protected void registerDynamicSensorCallbackImpl(final SensorManager.DynamicSensorCallback callback, final Handler handler) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.util.-$$Lambda$AsyncSensorManager$Hwm7wwA6xT-rLeZcpNr7J2BNQWE
            @Override // java.lang.Runnable
            public final void run() {
                AsyncSensorManager.this.lambda$registerDynamicSensorCallbackImpl$1$AsyncSensorManager(callback, handler);
            }
        });
    }

    public /* synthetic */ void lambda$unregisterDynamicSensorCallbackImpl$2$AsyncSensorManager(SensorManager.DynamicSensorCallback callback) {
        this.mInner.unregisterDynamicSensorCallback(callback);
    }

    protected void unregisterDynamicSensorCallbackImpl(final SensorManager.DynamicSensorCallback callback) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.util.-$$Lambda$AsyncSensorManager$ioH8tBFNQaFrSnUQEwQdi_ri4K0
            @Override // java.lang.Runnable
            public final void run() {
                AsyncSensorManager.this.lambda$unregisterDynamicSensorCallbackImpl$2$AsyncSensorManager(callback);
            }
        });
    }

    protected boolean requestTriggerSensorImpl(final TriggerEventListener listener, final Sensor sensor) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.util.-$$Lambda$AsyncSensorManager$q1TQFoUPad2_Ye0DbYS5yACL5CU
            @Override // java.lang.Runnable
            public final void run() {
                AsyncSensorManager.this.lambda$requestTriggerSensorImpl$3$AsyncSensorManager(listener, sensor);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$requestTriggerSensorImpl$3$AsyncSensorManager(TriggerEventListener listener, Sensor sensor) {
        if (!this.mInner.requestTriggerSensor(listener, sensor)) {
            Log.e(TAG, "Requesting " + listener + " for " + sensor + " failed.");
        }
    }

    protected boolean cancelTriggerSensorImpl(final TriggerEventListener listener, final Sensor sensor, boolean disable) {
        Preconditions.checkArgument(disable);
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.util.-$$Lambda$AsyncSensorManager$ssfdtdJfSGgmlHJqcz8km7BLSQE
            @Override // java.lang.Runnable
            public final void run() {
                AsyncSensorManager.this.lambda$cancelTriggerSensorImpl$4$AsyncSensorManager(listener, sensor);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$cancelTriggerSensorImpl$4$AsyncSensorManager(TriggerEventListener listener, Sensor sensor) {
        if (!this.mInner.cancelTriggerSensor(listener, sensor)) {
            Log.e(TAG, "Canceling " + listener + " for " + sensor + " failed.");
        }
    }

    public boolean registerPluginListener(final SensorManagerPlugin.Sensor sensor, final SensorManagerPlugin.SensorEventListener listener) {
        if (this.mPlugins.isEmpty()) {
            Log.w(TAG, "No plugins registered");
            return false;
        }
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.util.-$$Lambda$AsyncSensorManager$bmj-jXZbXydcVql7MvEeUnLrVqg
            @Override // java.lang.Runnable
            public final void run() {
                AsyncSensorManager.this.lambda$registerPluginListener$5$AsyncSensorManager(sensor, listener);
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$registerPluginListener$5$AsyncSensorManager(SensorManagerPlugin.Sensor sensor, SensorManagerPlugin.SensorEventListener listener) {
        for (int i = 0; i < this.mPlugins.size(); i++) {
            this.mPlugins.get(i).registerListener(sensor, listener);
        }
    }

    public void unregisterPluginListener(final SensorManagerPlugin.Sensor sensor, final SensorManagerPlugin.SensorEventListener listener) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.util.-$$Lambda$AsyncSensorManager$M0vK92hy3xzJzHU8n6zHhFvx24s
            @Override // java.lang.Runnable
            public final void run() {
                AsyncSensorManager.this.lambda$unregisterPluginListener$6$AsyncSensorManager(sensor, listener);
            }
        });
    }

    public /* synthetic */ void lambda$unregisterPluginListener$6$AsyncSensorManager(SensorManagerPlugin.Sensor sensor, SensorManagerPlugin.SensorEventListener listener) {
        for (int i = 0; i < this.mPlugins.size(); i++) {
            this.mPlugins.get(i).unregisterListener(sensor, listener);
        }
    }

    protected boolean initDataInjectionImpl(boolean enable) {
        throw new UnsupportedOperationException("not implemented");
    }

    protected boolean injectSensorDataImpl(Sensor sensor, float[] values, int accuracy, long timestamp) {
        throw new UnsupportedOperationException("not implemented");
    }

    public /* synthetic */ void lambda$setOperationParameterImpl$7$AsyncSensorManager(SensorAdditionalInfo parameter) {
        this.mInner.setOperationParameter(parameter);
    }

    protected boolean setOperationParameterImpl(final SensorAdditionalInfo parameter) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.util.-$$Lambda$AsyncSensorManager$id4CwPb4nY3VZjm8r_nmcqrEaU8
            @Override // java.lang.Runnable
            public final void run() {
                AsyncSensorManager.this.lambda$setOperationParameterImpl$7$AsyncSensorManager(parameter);
            }
        });
        return true;
    }

    protected void unregisterListenerImpl(final SensorEventListener listener, final Sensor sensor) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.util.-$$Lambda$AsyncSensorManager$_FJ7HnXlU155n13C8v2xB8y9Vy0
            @Override // java.lang.Runnable
            public final void run() {
                AsyncSensorManager.this.lambda$unregisterListenerImpl$8$AsyncSensorManager(sensor, listener);
            }
        });
    }

    public /* synthetic */ void lambda$unregisterListenerImpl$8$AsyncSensorManager(Sensor sensor, SensorEventListener listener) {
        if (sensor == null) {
            this.mInner.unregisterListener(listener);
        } else {
            this.mInner.unregisterListener(listener, sensor);
        }
    }

    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginConnected(SensorManagerPlugin plugin, Context pluginContext) {
        this.mPlugins.add(plugin);
    }

    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginDisconnected(SensorManagerPlugin plugin) {
        this.mPlugins.remove(plugin);
    }
}
