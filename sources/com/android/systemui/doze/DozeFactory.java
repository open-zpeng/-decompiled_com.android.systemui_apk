package com.android.systemui.doze;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Handler;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.android.systemui.dock.DockManager;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.util.AsyncSensorManager;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
/* loaded from: classes21.dex */
public class DozeFactory {
    public DozeMachine assembleMachine(DozeService dozeService, FalsingManager falsingManager) {
        SensorManager sensorManager = (SensorManager) Dependency.get(AsyncSensorManager.class);
        AlarmManager alarmManager = (AlarmManager) dozeService.getSystemService(AlarmManager.class);
        DockManager dockManager = (DockManager) Dependency.get(DockManager.class);
        WakefulnessLifecycle wakefulnessLifecycle = (WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class);
        DozeHost host = getHost(dozeService);
        AmbientDisplayConfiguration config = new AmbientDisplayConfiguration(dozeService);
        DozeParameters params = DozeParameters.getInstance(dozeService);
        Handler handler = new Handler();
        WakeLock wakeLock = new DelayedWakeLock(handler, WakeLock.createPartial(dozeService, "Doze"));
        DozeMachine.Service wrappedService = DozeSuspendScreenStatePreventingAdapter.wrapIfNeeded(DozeScreenStatePreventingAdapter.wrapIfNeeded(new DozeBrightnessHostForwarder(dozeService, host), params), params);
        DozeMachine machine = new DozeMachine(wrappedService, config, wakeLock, wakefulnessLifecycle, (BatteryController) Dependency.get(BatteryController.class));
        machine.setParts(new DozeMachine.Part[]{new DozePauser(handler, machine, alarmManager, params.getPolicy()), new DozeFalsingManagerAdapter(falsingManager), createDozeTriggers(dozeService, sensorManager, host, alarmManager, config, params, handler, wakeLock, machine, dockManager), createDozeUi(dozeService, host, wakeLock, machine, handler, alarmManager, params), new DozeScreenState(wrappedService, handler, params, wakeLock), createDozeScreenBrightness(dozeService, wrappedService, sensorManager, host, params, handler), new DozeWallpaperState(dozeService, getBiometricUnlockController(dozeService)), new DozeDockHandler(dozeService, machine, host, config, handler, dockManager), new DozeAuthRemover(dozeService)});
        return machine;
    }

    private DozeMachine.Part createDozeScreenBrightness(Context context, DozeMachine.Service service, SensorManager sensorManager, DozeHost host, DozeParameters params, Handler handler) {
        Sensor sensor = DozeSensors.findSensorWithType(sensorManager, context.getString(R.string.doze_brightness_sensor_type));
        return new DozeScreenBrightness(context, service, sensorManager, sensor, host, handler, params.getPolicy());
    }

    private DozeTriggers createDozeTriggers(Context context, SensorManager sensorManager, DozeHost host, AlarmManager alarmManager, AmbientDisplayConfiguration config, DozeParameters params, Handler handler, WakeLock wakeLock, DozeMachine machine, DockManager dockManager) {
        return new DozeTriggers(context, machine, host, alarmManager, config, params, sensorManager, handler, wakeLock, true, dockManager);
    }

    private DozeMachine.Part createDozeUi(Context context, DozeHost host, WakeLock wakeLock, DozeMachine machine, Handler handler, AlarmManager alarmManager, DozeParameters params) {
        return new DozeUi(context, alarmManager, machine, wakeLock, host, handler, params, KeyguardUpdateMonitor.getInstance(context));
    }

    public static DozeHost getHost(DozeService service) {
        Application appCandidate = service.getApplication();
        SystemUIApplication app = (SystemUIApplication) appCandidate;
        return (DozeHost) app.getComponent(DozeHost.class);
    }

    public static BiometricUnlockController getBiometricUnlockController(DozeService service) {
        Application appCandidate = service.getApplication();
        SystemUIApplication app = (SystemUIApplication) appCandidate;
        return (BiometricUnlockController) app.getComponent(BiometricUnlockController.class);
    }
}
