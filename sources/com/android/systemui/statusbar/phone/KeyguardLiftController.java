package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.util.Assert;
import com.android.systemui.util.AsyncSensorManager;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: KeyguardLiftController.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u001d\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u000bH\u0016J\u0010\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u000bH\u0016J\u0010\u0010\u0019\u001a\u00020\u00152\u0006\u0010\u001a\u001a\u00020\u000bH\u0016J\b\u0010\u001b\u001a\u00020\u0015H\u0002R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0012\u001a\n \u000f*\u0004\u0018\u00010\u00130\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001c"}, d2 = {"Lcom/android/systemui/statusbar/phone/KeyguardLiftController;", "Lcom/android/systemui/plugins/statusbar/StatusBarStateController$StateListener;", "Lcom/android/keyguard/KeyguardUpdateMonitorCallback;", "context", "Landroid/content/Context;", "statusBarStateController", "Lcom/android/systemui/plugins/statusbar/StatusBarStateController;", "asyncSensorManager", "Lcom/android/systemui/util/AsyncSensorManager;", "(Landroid/content/Context;Lcom/android/systemui/plugins/statusbar/StatusBarStateController;Lcom/android/systemui/util/AsyncSensorManager;)V", "bouncerVisible", "", "isListening", "keyguardUpdateMonitor", "Lcom/android/keyguard/KeyguardUpdateMonitor;", "kotlin.jvm.PlatformType", "listener", "Landroid/hardware/TriggerEventListener;", "pickupSensor", "Landroid/hardware/Sensor;", "onDozingChanged", "", "isDozing", "onKeyguardBouncerChanged", "bouncer", "onKeyguardVisibilityChanged", "showing", "updateListeningState", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class KeyguardLiftController extends KeyguardUpdateMonitorCallback implements StatusBarStateController.StateListener {
    private final AsyncSensorManager asyncSensorManager;
    private boolean bouncerVisible;
    private boolean isListening;
    private final KeyguardUpdateMonitor keyguardUpdateMonitor;
    private final TriggerEventListener listener;
    private final Sensor pickupSensor;
    private final StatusBarStateController statusBarStateController;

    public KeyguardLiftController(@NotNull Context context, @NotNull StatusBarStateController statusBarStateController, @NotNull AsyncSensorManager asyncSensorManager) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(asyncSensorManager, "asyncSensorManager");
        this.statusBarStateController = statusBarStateController;
        this.asyncSensorManager = asyncSensorManager;
        this.keyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(context);
        this.pickupSensor = this.asyncSensorManager.getDefaultSensor(25);
        this.statusBarStateController.addCallback(this);
        this.keyguardUpdateMonitor.registerCallback(this);
        updateListeningState();
        this.listener = new TriggerEventListener() { // from class: com.android.systemui.statusbar.phone.KeyguardLiftController$listener$1
            @Override // android.hardware.TriggerEventListener
            public void onTrigger(@Nullable TriggerEvent event) {
                KeyguardUpdateMonitor keyguardUpdateMonitor;
                Assert.isMainThread();
                KeyguardLiftController.this.isListening = false;
                KeyguardLiftController.this.updateListeningState();
                keyguardUpdateMonitor = KeyguardLiftController.this.keyguardUpdateMonitor;
                keyguardUpdateMonitor.requestFaceAuth();
            }
        };
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        updateListeningState();
    }

    @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
    public void onKeyguardBouncerChanged(boolean bouncer) {
        this.bouncerVisible = bouncer;
        updateListeningState();
    }

    @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
    public void onKeyguardVisibilityChanged(boolean showing) {
        updateListeningState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateListeningState() {
        if (this.pickupSensor == null) {
            return;
        }
        KeyguardUpdateMonitor keyguardUpdateMonitor = this.keyguardUpdateMonitor;
        Intrinsics.checkExpressionValueIsNotNull(keyguardUpdateMonitor, "keyguardUpdateMonitor");
        boolean shouldListen = true;
        boolean onKeyguard = keyguardUpdateMonitor.isKeyguardVisible() && !this.statusBarStateController.isDozing();
        if (!onKeyguard && !this.bouncerVisible) {
            shouldListen = false;
        }
        if (shouldListen != this.isListening) {
            this.isListening = shouldListen;
            if (shouldListen) {
                this.asyncSensorManager.requestTriggerSensor(this.listener, this.pickupSensor);
            } else {
                this.asyncSensorManager.cancelTriggerSensor(this.listener, this.pickupSensor);
            }
        }
    }
}
