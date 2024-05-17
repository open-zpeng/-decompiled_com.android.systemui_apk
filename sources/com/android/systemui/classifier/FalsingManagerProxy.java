package com.android.systemui.classifier;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.provider.DeviceConfig;
import android.view.MotionEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.classifier.brightline.BrightLineFalsingManager;
import com.android.systemui.classifier.brightline.FalsingDataProvider;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.FalsingPlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.ProximitySensor;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class FalsingManagerProxy implements FalsingManager {
    private static final String PROXIMITY_SENSOR_TAG = "FalsingManager";
    private FalsingManager mInternalFalsingManager;
    private final Handler mMainHandler;
    private final ProximitySensor mProximitySensor;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Inject
    public FalsingManagerProxy(final Context context, PluginManager pluginManager, @Named("main_handler") Handler handler, ProximitySensor proximitySensor) {
        this.mMainHandler = handler;
        this.mProximitySensor = proximitySensor;
        this.mProximitySensor.setTag(PROXIMITY_SENSOR_TAG);
        DeviceConfig.addOnPropertiesChangedListener("systemui", new Executor() { // from class: com.android.systemui.classifier.-$$Lambda$FalsingManagerProxy$qZ6lxH8vX6Mj0Cv4-e94eYSfUGA
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                FalsingManagerProxy.this.lambda$new$0$FalsingManagerProxy(runnable);
            }
        }, new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.systemui.classifier.-$$Lambda$FalsingManagerProxy$gca_JCTVGHkvAjBNMeOIeE6opNs
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                FalsingManagerProxy.this.lambda$new$1$FalsingManagerProxy(context, properties);
            }
        });
        setupFalsingManager(context);
        PluginListener<FalsingPlugin> mPluginListener = new PluginListener<FalsingPlugin>() { // from class: com.android.systemui.classifier.FalsingManagerProxy.1
            @Override // com.android.systemui.plugins.PluginListener
            public void onPluginConnected(FalsingPlugin plugin, Context context2) {
                FalsingManager pluginFalsingManager = plugin.getFalsingManager(context2);
                if (pluginFalsingManager != null) {
                    FalsingManagerProxy.this.mInternalFalsingManager.cleanup();
                    FalsingManagerProxy.this.mInternalFalsingManager = pluginFalsingManager;
                }
            }

            @Override // com.android.systemui.plugins.PluginListener
            public void onPluginDisconnected(FalsingPlugin plugin) {
                FalsingManagerProxy.this.mInternalFalsingManager = new FalsingManagerImpl(context);
            }
        };
        pluginManager.addPluginListener(mPluginListener, FalsingPlugin.class);
    }

    public /* synthetic */ void lambda$new$0$FalsingManagerProxy(Runnable command) {
        this.mMainHandler.post(command);
    }

    public /* synthetic */ void lambda$new$1$FalsingManagerProxy(Context context, DeviceConfig.Properties properties) {
        onDeviceConfigPropertiesChanged(context, properties.getNamespace());
    }

    private void onDeviceConfigPropertiesChanged(Context context, String namespace) {
        if (!"systemui".equals(namespace)) {
            return;
        }
        setupFalsingManager(context);
    }

    @VisibleForTesting
    public void setupFalsingManager(Context context) {
        boolean brightlineEnabled = DeviceConfig.getBoolean("systemui", "brightline_falsing_manager_enabled", true);
        FalsingManager falsingManager = this.mInternalFalsingManager;
        if (falsingManager != null) {
            falsingManager.cleanup();
        }
        if (!brightlineEnabled) {
            this.mInternalFalsingManager = new FalsingManagerImpl(context);
        } else {
            this.mInternalFalsingManager = new BrightLineFalsingManager(new FalsingDataProvider(context.getResources().getDisplayMetrics()), KeyguardUpdateMonitor.getInstance(context), this.mProximitySensor);
        }
    }

    @VisibleForTesting
    FalsingManager getInternalFalsingManager() {
        return this.mInternalFalsingManager;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onSucccessfulUnlock() {
        this.mInternalFalsingManager.onSucccessfulUnlock();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationActive() {
        this.mInternalFalsingManager.onNotificationActive();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setShowingAod(boolean showingAod) {
        this.mInternalFalsingManager.setShowingAod(showingAod);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStartDraggingDown() {
        this.mInternalFalsingManager.onNotificatonStartDraggingDown();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isUnlockingDisabled() {
        return this.mInternalFalsingManager.isUnlockingDisabled();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isFalseTouch() {
        return this.mInternalFalsingManager.isFalseTouch();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStopDraggingDown() {
        this.mInternalFalsingManager.onNotificatonStartDraggingDown();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setNotificationExpanded() {
        this.mInternalFalsingManager.setNotificationExpanded();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isClassiferEnabled() {
        return this.mInternalFalsingManager.isClassiferEnabled();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onQsDown() {
        this.mInternalFalsingManager.onQsDown();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setQsExpanded(boolean expanded) {
        this.mInternalFalsingManager.setQsExpanded(expanded);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean shouldEnforceBouncer() {
        return this.mInternalFalsingManager.shouldEnforceBouncer();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTrackingStarted(boolean secure) {
        this.mInternalFalsingManager.onTrackingStarted(secure);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTrackingStopped() {
        this.mInternalFalsingManager.onTrackingStopped();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onLeftAffordanceOn() {
        this.mInternalFalsingManager.onLeftAffordanceOn();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onCameraOn() {
        this.mInternalFalsingManager.onCameraOn();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onAffordanceSwipingStarted(boolean rightCorner) {
        this.mInternalFalsingManager.onAffordanceSwipingStarted(rightCorner);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onAffordanceSwipingAborted() {
        this.mInternalFalsingManager.onAffordanceSwipingAborted();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onStartExpandingFromPulse() {
        this.mInternalFalsingManager.onStartExpandingFromPulse();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onExpansionFromPulseStopped() {
        this.mInternalFalsingManager.onExpansionFromPulseStopped();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public Uri reportRejectedTouch() {
        return this.mInternalFalsingManager.reportRejectedTouch();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenOnFromTouch() {
        this.mInternalFalsingManager.onScreenOnFromTouch();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isReportingEnabled() {
        return this.mInternalFalsingManager.isReportingEnabled();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onUnlockHintStarted() {
        this.mInternalFalsingManager.onUnlockHintStarted();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onCameraHintStarted() {
        this.mInternalFalsingManager.onCameraHintStarted();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onLeftAffordanceHintStarted() {
        this.mInternalFalsingManager.onLeftAffordanceHintStarted();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenTurningOn() {
        this.mInternalFalsingManager.onScreenTurningOn();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenOff() {
        this.mInternalFalsingManager.onScreenOff();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStopDismissing() {
        this.mInternalFalsingManager.onNotificatonStopDismissing();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationDismissed() {
        this.mInternalFalsingManager.onNotificationDismissed();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStartDismissing() {
        this.mInternalFalsingManager.onNotificatonStartDismissing();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationDoubleTap(boolean accepted, float dx, float dy) {
        this.mInternalFalsingManager.onNotificationDoubleTap(accepted, dx, dy);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onBouncerShown() {
        this.mInternalFalsingManager.onBouncerShown();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onBouncerHidden() {
        this.mInternalFalsingManager.onBouncerHidden();
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTouchEvent(MotionEvent ev, int width, int height) {
        this.mInternalFalsingManager.onTouchEvent(ev, width, height);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void dump(PrintWriter pw) {
        this.mInternalFalsingManager.dump(pw);
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void cleanup() {
        this.mInternalFalsingManager.cleanup();
    }
}
