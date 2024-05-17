package com.android.systemui.assist;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.DeviceConfig;
import android.util.Log;
import androidx.annotation.Nullable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.AssistUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.DumpController;
import com.android.systemui.Dumpable;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.NavigationModeController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public final class AssistHandleBehaviorController implements AssistHandleCallbacks, Dumpable {
    private static final long DEFAULT_SHOWN_FREQUENCY_THRESHOLD_MS = 0;
    private static final String TAG = "AssistHandleBehavior";
    private final AssistUtils mAssistUtils;
    private final Map<AssistHandleBehavior, BehaviorController> mBehaviorMap;
    private final Context mContext;
    private final DeviceConfigHelper mDeviceConfigHelper;
    private final Handler mHandler;
    private long mHandlesLastHiddenAt;
    private boolean mInGesturalMode;
    private final Provider<ScreenDecorations> mScreenDecorations;
    private long mShowAndGoEndsAt;
    private static final long DEFAULT_SHOW_AND_GO_DURATION_MS = TimeUnit.SECONDS.toMillis(3);
    private static final AssistHandleBehavior DEFAULT_BEHAVIOR = AssistHandleBehavior.REMINDER_EXP;
    private final Runnable mHideHandles = new Runnable() { // from class: com.android.systemui.assist.-$$Lambda$AssistHandleBehaviorController$XubZVLOT9vWCBnL-QqZRgbOELVA
        @Override // java.lang.Runnable
        public final void run() {
            AssistHandleBehaviorController.this.hideHandles();
        }
    };
    private final Runnable mShowAndGo = new Runnable() { // from class: com.android.systemui.assist.-$$Lambda$AssistHandleBehaviorController$oeveMWAQo5jd5bG1H5Ci7Dy4X74
        @Override // java.lang.Runnable
        public final void run() {
            AssistHandleBehaviorController.this.showAndGoInternal();
        }
    };
    private boolean mHandlesShowing = false;
    private AssistHandleBehavior mCurrentBehavior = AssistHandleBehavior.OFF;

    /* JADX INFO: Access modifiers changed from: package-private */
    @Inject
    public AssistHandleBehaviorController(Context context, AssistUtils assistUtils, @Named("assist_handle_thread") Handler handler, Provider<ScreenDecorations> screenDecorations, DeviceConfigHelper deviceConfigHelper, Map<AssistHandleBehavior, BehaviorController> behaviorMap, NavigationModeController navigationModeController, DumpController dumpController) {
        this.mContext = context;
        this.mAssistUtils = assistUtils;
        this.mHandler = handler;
        this.mScreenDecorations = screenDecorations;
        this.mDeviceConfigHelper = deviceConfigHelper;
        this.mBehaviorMap = behaviorMap;
        this.mInGesturalMode = QuickStepContract.isGesturalMode(navigationModeController.addListener(new NavigationModeController.ModeChangedListener() { // from class: com.android.systemui.assist.-$$Lambda$AssistHandleBehaviorController$UX7PPcltnlTgxyL7MxmLbVmQRcI
            @Override // com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener
            public final void onNavigationModeChanged(int i) {
                AssistHandleBehaviorController.this.handleNavigationModeChange(i);
            }
        }));
        setBehavior(getBehaviorMode());
        DeviceConfigHelper deviceConfigHelper2 = this.mDeviceConfigHelper;
        final Handler handler2 = this.mHandler;
        Objects.requireNonNull(handler2);
        deviceConfigHelper2.addOnPropertiesChangedListener(new Executor() { // from class: com.android.systemui.assist.-$$Lambda$LfzJt661qZfn2w-6SYHFbD3aMy0
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                handler2.post(runnable);
            }
        }, new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.systemui.assist.-$$Lambda$AssistHandleBehaviorController$q1QjkwrdHAyLNN1tG8mZqypuW-0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                AssistHandleBehaviorController.this.lambda$new$0$AssistHandleBehaviorController(properties);
            }
        });
        dumpController.addListener(this);
    }

    public /* synthetic */ void lambda$new$0$AssistHandleBehaviorController(DeviceConfig.Properties properties) {
        if (properties.getKeyset().contains("assist_handles_behavior_mode")) {
            setBehavior(properties.getString("assist_handles_behavior_mode", (String) null));
        }
    }

    @Override // com.android.systemui.assist.AssistHandleCallbacks
    public void hide() {
        clearPendingCommands();
        this.mHandler.post(this.mHideHandles);
    }

    @Override // com.android.systemui.assist.AssistHandleCallbacks
    public void showAndGo() {
        clearPendingCommands();
        this.mHandler.post(this.mShowAndGo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAndGoInternal() {
        maybeShowHandles(false);
        long showAndGoDuration = getShowAndGoDuration();
        this.mShowAndGoEndsAt = SystemClock.elapsedRealtime() + showAndGoDuration;
        this.mHandler.postDelayed(this.mHideHandles, showAndGoDuration);
    }

    @Override // com.android.systemui.assist.AssistHandleCallbacks
    public void showAndGoDelayed(long delayMs, boolean hideIfShowing) {
        clearPendingCommands();
        if (hideIfShowing) {
            this.mHandler.post(this.mHideHandles);
        }
        this.mHandler.postDelayed(this.mShowAndGo, delayMs);
    }

    @Override // com.android.systemui.assist.AssistHandleCallbacks
    public void showAndStay() {
        clearPendingCommands();
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.assist.-$$Lambda$AssistHandleBehaviorController$jLNVwoO6t8_VWqmD__-vvvJFYqA
            @Override // java.lang.Runnable
            public final void run() {
                AssistHandleBehaviorController.this.lambda$showAndStay$1$AssistHandleBehaviorController();
            }
        });
    }

    public /* synthetic */ void lambda$showAndStay$1$AssistHandleBehaviorController() {
        maybeShowHandles(true);
    }

    public long getShowAndGoRemainingTimeMs() {
        return Long.max(this.mShowAndGoEndsAt - SystemClock.elapsedRealtime(), 0L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean areHandlesShowing() {
        return this.mHandlesShowing;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAssistantGesturePerformed() {
        this.mBehaviorMap.get(this.mCurrentBehavior).onAssistantGesturePerformed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onAssistHandlesRequested() {
        if (this.mInGesturalMode) {
            this.mBehaviorMap.get(this.mCurrentBehavior).onAssistHandlesRequested();
        }
    }

    void setBehavior(AssistHandleBehavior behavior) {
        if (this.mCurrentBehavior == behavior) {
            return;
        }
        if (!this.mBehaviorMap.containsKey(behavior)) {
            Log.e(TAG, "Unsupported behavior requested: " + behavior.toString());
            return;
        }
        if (this.mInGesturalMode) {
            this.mBehaviorMap.get(this.mCurrentBehavior).onModeDeactivated();
            this.mBehaviorMap.get(behavior).onModeActivated(this.mContext, this);
        }
        this.mCurrentBehavior = behavior;
    }

    private void setBehavior(@Nullable String behavior) {
        try {
            setBehavior(AssistHandleBehavior.valueOf(behavior));
        } catch (IllegalArgumentException | NullPointerException e) {
            Log.e(TAG, "Invalid behavior: " + behavior, e);
        }
    }

    private boolean handlesUnblocked(boolean ignoreThreshold) {
        long timeSinceHidden = SystemClock.elapsedRealtime() - this.mHandlesLastHiddenAt;
        boolean notThrottled = ignoreThreshold || timeSinceHidden >= getShownFrequencyThreshold();
        ComponentName assistantComponent = this.mAssistUtils.getAssistComponentForUser(KeyguardUpdateMonitor.getCurrentUser());
        return notThrottled && assistantComponent != null;
    }

    private long getShownFrequencyThreshold() {
        return this.mDeviceConfigHelper.getLong("assist_handles_shown_frequency_threshold_ms", 0L);
    }

    private long getShowAndGoDuration() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_duration_ms", DEFAULT_SHOW_AND_GO_DURATION_MS);
    }

    private String getBehaviorMode() {
        return this.mDeviceConfigHelper.getString("assist_handles_behavior_mode", DEFAULT_BEHAVIOR.toString());
    }

    private void maybeShowHandles(boolean ignoreThreshold) {
        if (!this.mHandlesShowing && handlesUnblocked(ignoreThreshold)) {
            ScreenDecorations screenDecorations = this.mScreenDecorations.get();
            if (screenDecorations == null) {
                Log.w(TAG, "Couldn't show handles, ScreenDecorations unavailable");
                return;
            }
            this.mHandlesShowing = true;
            screenDecorations.lambda$setAssistHintVisible$1$ScreenDecorations(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideHandles() {
        if (!this.mHandlesShowing) {
            return;
        }
        ScreenDecorations screenDecorations = this.mScreenDecorations.get();
        if (screenDecorations == null) {
            Log.w(TAG, "Couldn't hide handles, ScreenDecorations unavailable");
            return;
        }
        this.mHandlesShowing = false;
        this.mHandlesLastHiddenAt = SystemClock.elapsedRealtime();
        screenDecorations.lambda$setAssistHintVisible$1$ScreenDecorations(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNavigationModeChange(int navigationMode) {
        boolean inGesturalMode = QuickStepContract.isGesturalMode(navigationMode);
        if (this.mInGesturalMode == inGesturalMode) {
            return;
        }
        this.mInGesturalMode = inGesturalMode;
        if (this.mInGesturalMode) {
            this.mBehaviorMap.get(this.mCurrentBehavior).onModeActivated(this.mContext, this);
            return;
        }
        this.mBehaviorMap.get(this.mCurrentBehavior).onModeDeactivated();
        hide();
    }

    private void clearPendingCommands() {
        this.mHandler.removeCallbacks(this.mHideHandles);
        this.mHandler.removeCallbacks(this.mShowAndGo);
        this.mShowAndGoEndsAt = 0L;
    }

    @VisibleForTesting
    void setInGesturalModeForTest(boolean inGesturalMode) {
        this.mInGesturalMode = inGesturalMode;
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("Current AssistHandleBehaviorController State:");
        pw.println("   mHandlesShowing=" + this.mHandlesShowing);
        pw.println("   mHandlesLastHiddenAt=" + this.mHandlesLastHiddenAt);
        pw.println("   mInGesturalMode=" + this.mInGesturalMode);
        pw.println("   Phenotype Flags:");
        pw.println("      assist_handles_show_and_go_duration_ms=" + getShowAndGoDuration());
        pw.println("      assist_handles_shown_frequency_threshold_ms=" + getShownFrequencyThreshold());
        pw.println("      assist_handles_behavior_mode=" + getBehaviorMode());
        pw.println("   mCurrentBehavior=" + this.mCurrentBehavior.toString());
        this.mBehaviorMap.get(this.mCurrentBehavior).dump(pw, "   ");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public interface BehaviorController {
        void onModeActivated(Context context, AssistHandleCallbacks assistHandleCallbacks);

        default void onModeDeactivated() {
        }

        default void onAssistantGesturePerformed() {
        }

        default void onAssistHandlesRequested() {
        }

        default void dump(PrintWriter pw, String prefix) {
        }
    }
}
