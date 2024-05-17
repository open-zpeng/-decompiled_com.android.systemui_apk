package com.android.systemui.assist;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.slice.Clock;
import com.android.systemui.assist.AssistHandleBehaviorController;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import dagger.Lazy;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
/* JADX INFO: Access modifiers changed from: package-private */
@Singleton
/* loaded from: classes21.dex */
public final class AssistHandleReminderExpBehavior implements AssistHandleBehaviorController.BehaviorController {
    private static final int DEFAULT_LEARNING_COUNT = 10;
    private static final long DEFAULT_SHOW_AND_GO_DELAYED_SHORT_DELAY_MS = 150;
    private static final boolean DEFAULT_SHOW_WHEN_TAUGHT = false;
    private static final boolean DEFAULT_SUPPRESS_ON_APPS = true;
    private static final boolean DEFAULT_SUPPRESS_ON_LAUNCHER = false;
    private static final boolean DEFAULT_SUPPRESS_ON_LOCKSCREEN = false;
    private static final String LEARNED_HINT_LAST_SHOWN_KEY = "reminder_exp_learned_hint_last_shown";
    private static final String LEARNING_EVENT_COUNT_KEY = "reminder_exp_learning_event_count";
    private static final String LEARNING_TIME_ELAPSED_KEY = "reminder_exp_learning_time_elapsed";
    private final Lazy<ActivityManagerWrapper> mActivityManagerWrapper;
    @Nullable
    private AssistHandleCallbacks mAssistHandleCallbacks;
    private final Clock mClock;
    private int mConsecutiveTaskSwitches;
    @Nullable
    private Context mContext;
    @Nullable
    private ComponentName mDefaultHome;
    private final DeviceConfigHelper mDeviceConfigHelper;
    private final Handler mHandler;
    private boolean mIsAwake;
    private boolean mIsDozing;
    private boolean mIsLauncherShowing;
    private boolean mIsLearned;
    private boolean mIsNavBarHidden;
    private long mLastLearningTimestamp;
    private long mLearnedHintLastShownEpochDay;
    private int mLearningCount;
    private long mLearningTimeElapsed;
    private boolean mOnLockscreen;
    private final Lazy<OverviewProxyService> mOverviewProxyService;
    private final Lazy<PackageManagerWrapper> mPackageManagerWrapper;
    private int mRunningTaskId;
    private final Lazy<StatusBarStateController> mStatusBarStateController;
    private final Lazy<WakefulnessLifecycle> mWakefulnessLifecycle;
    private static final long DEFAULT_LEARNING_TIME_MS = TimeUnit.DAYS.toMillis(10);
    private static final long DEFAULT_SHOW_AND_GO_DELAYED_LONG_DELAY_MS = TimeUnit.SECONDS.toMillis(1);
    private static final long DEFAULT_SHOW_AND_GO_DELAY_RESET_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(3);
    private static final String[] DEFAULT_HOME_CHANGE_ACTIONS = {PackageManagerWrapper.ACTION_PREFERRED_ACTIVITY_CHANGED, "android.intent.action.BOOT_COMPLETED", "android.intent.action.PACKAGE_ADDED", "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REMOVED"};
    private final StatusBarStateController.StateListener mStatusBarStateListener = new StatusBarStateController.StateListener() { // from class: com.android.systemui.assist.AssistHandleReminderExpBehavior.1
        @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
        public void onStateChanged(int newState) {
            AssistHandleReminderExpBehavior.this.handleStatusBarStateChanged(newState);
        }

        @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
        public void onDozingChanged(boolean isDozing) {
            AssistHandleReminderExpBehavior.this.handleDozingChanged(isDozing);
        }
    };
    private final TaskStackChangeListener mTaskStackChangeListener = new TaskStackChangeListener() { // from class: com.android.systemui.assist.AssistHandleReminderExpBehavior.2
        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) {
            AssistHandleReminderExpBehavior.this.handleTaskStackTopChanged(taskInfo.taskId, taskInfo.topActivity);
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskCreated(int taskId, ComponentName componentName) {
            AssistHandleReminderExpBehavior.this.handleTaskStackTopChanged(taskId, componentName);
        }
    };
    private final OverviewProxyService.OverviewProxyListener mOverviewProxyListener = new OverviewProxyService.OverviewProxyListener() { // from class: com.android.systemui.assist.AssistHandleReminderExpBehavior.3
        @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
        public void onOverviewShown(boolean fromHome) {
            AssistHandleReminderExpBehavior.this.handleOverviewShown();
        }

        @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
        public void onSystemUiStateChanged(int sysuiStateFlags) {
            AssistHandleReminderExpBehavior.this.handleSystemUiStateChanged(sysuiStateFlags);
        }
    };
    private final WakefulnessLifecycle.Observer mWakefulnessLifecycleObserver = new WakefulnessLifecycle.Observer() { // from class: com.android.systemui.assist.AssistHandleReminderExpBehavior.4
        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onStartedWakingUp() {
            AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(false);
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onFinishedWakingUp() {
            AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(true);
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onStartedGoingToSleep() {
            AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(false);
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onFinishedGoingToSleep() {
            AssistHandleReminderExpBehavior.this.handleWakefullnessChanged(false);
        }
    };
    private final BroadcastReceiver mDefaultHomeBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.systemui.assist.AssistHandleReminderExpBehavior.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            AssistHandleReminderExpBehavior assistHandleReminderExpBehavior = AssistHandleReminderExpBehavior.this;
            assistHandleReminderExpBehavior.mDefaultHome = assistHandleReminderExpBehavior.getCurrentDefaultHome();
        }
    };
    private final Runnable mResetConsecutiveTaskSwitches = new Runnable() { // from class: com.android.systemui.assist.-$$Lambda$AssistHandleReminderExpBehavior$pwcnWUhYSvHUPTaX_vnnVqcvKYA
        @Override // java.lang.Runnable
        public final void run() {
            AssistHandleReminderExpBehavior.this.resetConsecutiveTaskSwitches();
        }
    };
    private final IntentFilter mDefaultHomeIntentFilter = new IntentFilter();

    /* JADX INFO: Access modifiers changed from: package-private */
    @Inject
    public AssistHandleReminderExpBehavior(@Named("uptime") Clock clock, @Named("assist_handle_thread") Handler handler, DeviceConfigHelper deviceConfigHelper, Lazy<StatusBarStateController> statusBarStateController, Lazy<ActivityManagerWrapper> activityManagerWrapper, Lazy<OverviewProxyService> overviewProxyService, Lazy<WakefulnessLifecycle> wakefulnessLifecycle, Lazy<PackageManagerWrapper> packageManagerWrapper) {
        String[] strArr;
        this.mClock = clock;
        this.mHandler = handler;
        this.mDeviceConfigHelper = deviceConfigHelper;
        this.mStatusBarStateController = statusBarStateController;
        this.mActivityManagerWrapper = activityManagerWrapper;
        this.mOverviewProxyService = overviewProxyService;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        this.mPackageManagerWrapper = packageManagerWrapper;
        for (String action : DEFAULT_HOME_CHANGE_ACTIONS) {
            this.mDefaultHomeIntentFilter.addAction(action);
        }
    }

    @Override // com.android.systemui.assist.AssistHandleBehaviorController.BehaviorController
    public void onModeActivated(Context context, AssistHandleCallbacks callbacks) {
        this.mContext = context;
        this.mAssistHandleCallbacks = callbacks;
        this.mConsecutiveTaskSwitches = 0;
        this.mDefaultHome = getCurrentDefaultHome();
        context.registerReceiver(this.mDefaultHomeBroadcastReceiver, this.mDefaultHomeIntentFilter);
        this.mOnLockscreen = onLockscreen(this.mStatusBarStateController.get().getState());
        this.mIsDozing = this.mStatusBarStateController.get().isDozing();
        this.mStatusBarStateController.get().addCallback(this.mStatusBarStateListener);
        ActivityManager.RunningTaskInfo runningTaskInfo = this.mActivityManagerWrapper.get().getRunningTask();
        this.mRunningTaskId = runningTaskInfo == null ? 0 : runningTaskInfo.taskId;
        this.mActivityManagerWrapper.get().registerTaskStackListener(this.mTaskStackChangeListener);
        this.mOverviewProxyService.get().addCallback(this.mOverviewProxyListener);
        this.mIsAwake = this.mWakefulnessLifecycle.get().getWakefulness() == 2;
        this.mWakefulnessLifecycle.get().addObserver(this.mWakefulnessLifecycleObserver);
        this.mLearningTimeElapsed = Settings.Secure.getLong(context.getContentResolver(), LEARNING_TIME_ELAPSED_KEY, 0L);
        this.mLearningCount = Settings.Secure.getInt(context.getContentResolver(), LEARNING_EVENT_COUNT_KEY, 0);
        this.mLearnedHintLastShownEpochDay = Settings.Secure.getLong(context.getContentResolver(), LEARNED_HINT_LAST_SHOWN_KEY, 0L);
        this.mLastLearningTimestamp = this.mClock.currentTimeMillis();
        callbackForCurrentState(false);
    }

    @Override // com.android.systemui.assist.AssistHandleBehaviorController.BehaviorController
    public void onModeDeactivated() {
        this.mAssistHandleCallbacks = null;
        Context context = this.mContext;
        if (context != null) {
            context.unregisterReceiver(this.mDefaultHomeBroadcastReceiver);
            Settings.Secure.putLong(this.mContext.getContentResolver(), LEARNING_TIME_ELAPSED_KEY, 0L);
            Settings.Secure.putInt(this.mContext.getContentResolver(), LEARNING_EVENT_COUNT_KEY, 0);
            Settings.Secure.putLong(this.mContext.getContentResolver(), LEARNED_HINT_LAST_SHOWN_KEY, 0L);
            this.mContext = null;
        }
        this.mStatusBarStateController.get().removeCallback(this.mStatusBarStateListener);
        this.mActivityManagerWrapper.get().unregisterTaskStackListener(this.mTaskStackChangeListener);
        this.mOverviewProxyService.get().removeCallback(this.mOverviewProxyListener);
        this.mWakefulnessLifecycle.get().removeObserver(this.mWakefulnessLifecycleObserver);
    }

    @Override // com.android.systemui.assist.AssistHandleBehaviorController.BehaviorController
    public void onAssistantGesturePerformed() {
        Context context = this.mContext;
        if (context == null) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        int i = this.mLearningCount + 1;
        this.mLearningCount = i;
        Settings.Secure.putLong(contentResolver, LEARNING_EVENT_COUNT_KEY, i);
    }

    @Override // com.android.systemui.assist.AssistHandleBehaviorController.BehaviorController
    public void onAssistHandlesRequested() {
        if (this.mAssistHandleCallbacks != null && isFullyAwake() && !this.mIsNavBarHidden && !this.mOnLockscreen) {
            this.mAssistHandleCallbacks.showAndGo();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public ComponentName getCurrentDefaultHome() {
        List<ResolveInfo> homeActivities = new ArrayList<>();
        ComponentName defaultHome = this.mPackageManagerWrapper.get().getHomeActivities(homeActivities);
        if (defaultHome != null) {
            return defaultHome;
        }
        int topPriority = Integer.MIN_VALUE;
        ComponentName topComponent = null;
        for (ResolveInfo resolveInfo : homeActivities) {
            if (resolveInfo.priority > topPriority) {
                topComponent = resolveInfo.activityInfo.getComponentName();
                topPriority = resolveInfo.priority;
            } else if (resolveInfo.priority == topPriority) {
                topComponent = null;
            }
        }
        return topComponent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStatusBarStateChanged(int newState) {
        boolean onLockscreen = onLockscreen(newState);
        if (this.mOnLockscreen == onLockscreen) {
            return;
        }
        resetConsecutiveTaskSwitches();
        this.mOnLockscreen = onLockscreen;
        callbackForCurrentState(!onLockscreen);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDozingChanged(boolean isDozing) {
        if (this.mIsDozing == isDozing) {
            return;
        }
        resetConsecutiveTaskSwitches();
        this.mIsDozing = isDozing;
        callbackForCurrentState(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWakefullnessChanged(boolean isAwake) {
        if (this.mIsAwake == isAwake) {
            return;
        }
        resetConsecutiveTaskSwitches();
        this.mIsAwake = isAwake;
        callbackForCurrentState(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleTaskStackTopChanged(int taskId, @Nullable ComponentName taskComponentName) {
        if (this.mRunningTaskId == taskId || taskComponentName == null) {
            return;
        }
        this.mRunningTaskId = taskId;
        this.mIsLauncherShowing = taskComponentName.equals(this.mDefaultHome);
        if (this.mIsLauncherShowing) {
            resetConsecutiveTaskSwitches();
        } else {
            rescheduleConsecutiveTaskSwitchesReset();
            this.mConsecutiveTaskSwitches++;
        }
        callbackForCurrentState(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSystemUiStateChanged(int sysuiStateFlags) {
        boolean isNavBarHidden = (sysuiStateFlags & 2) != 0;
        if (this.mIsNavBarHidden == isNavBarHidden) {
            return;
        }
        resetConsecutiveTaskSwitches();
        this.mIsNavBarHidden = isNavBarHidden;
        callbackForCurrentState(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOverviewShown() {
        resetConsecutiveTaskSwitches();
        callbackForCurrentState(false);
    }

    private boolean onLockscreen(int statusBarState) {
        return statusBarState == 1 || statusBarState == 2;
    }

    private void callbackForCurrentState(boolean justUnlocked) {
        updateLearningStatus();
        if (this.mIsLearned) {
            callbackForLearnedState(justUnlocked);
        } else {
            callbackForUnlearnedState();
        }
    }

    private void callbackForLearnedState(boolean justUnlocked) {
        if (this.mAssistHandleCallbacks == null) {
            return;
        }
        if (!isFullyAwake() || this.mIsNavBarHidden || this.mOnLockscreen || !getShowWhenTaught()) {
            this.mAssistHandleCallbacks.hide();
        } else if (justUnlocked) {
            long currentEpochDay = LocalDate.now().toEpochDay();
            if (this.mLearnedHintLastShownEpochDay < currentEpochDay) {
                Context context = this.mContext;
                if (context != null) {
                    Settings.Secure.putLong(context.getContentResolver(), LEARNED_HINT_LAST_SHOWN_KEY, currentEpochDay);
                }
                this.mLearnedHintLastShownEpochDay = currentEpochDay;
                this.mAssistHandleCallbacks.showAndGo();
            }
        }
    }

    private void callbackForUnlearnedState() {
        if (this.mAssistHandleCallbacks == null) {
            return;
        }
        if (!isFullyAwake() || this.mIsNavBarHidden || isSuppressed()) {
            this.mAssistHandleCallbacks.hide();
        } else if (this.mOnLockscreen) {
            this.mAssistHandleCallbacks.showAndStay();
        } else if (this.mIsLauncherShowing) {
            this.mAssistHandleCallbacks.showAndGo();
        } else if (this.mConsecutiveTaskSwitches == 1) {
            this.mAssistHandleCallbacks.showAndGoDelayed(getShowAndGoDelayedShortDelayMs(), false);
        } else {
            this.mAssistHandleCallbacks.showAndGoDelayed(getShowAndGoDelayedLongDelayMs(), true);
        }
    }

    private boolean isSuppressed() {
        if (this.mOnLockscreen) {
            return getSuppressOnLockscreen();
        }
        if (this.mIsLauncherShowing) {
            return getSuppressOnLauncher();
        }
        return getSuppressOnApps();
    }

    private void updateLearningStatus() {
        if (this.mContext == null) {
            return;
        }
        long currentTimestamp = this.mClock.currentTimeMillis();
        this.mLearningTimeElapsed += currentTimestamp - this.mLastLearningTimestamp;
        this.mLastLearningTimestamp = currentTimestamp;
        this.mIsLearned = this.mLearningCount >= getLearningCount() || this.mLearningTimeElapsed >= getLearningTimeMs();
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.assist.-$$Lambda$AssistHandleReminderExpBehavior$b5N62AJXKgTBT_CGtHJhp-XuFas
            @Override // java.lang.Runnable
            public final void run() {
                AssistHandleReminderExpBehavior.this.lambda$updateLearningStatus$0$AssistHandleReminderExpBehavior();
            }
        });
    }

    public /* synthetic */ void lambda$updateLearningStatus$0$AssistHandleReminderExpBehavior() {
        Settings.Secure.putLong(this.mContext.getContentResolver(), LEARNING_TIME_ELAPSED_KEY, this.mLearningTimeElapsed);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetConsecutiveTaskSwitches() {
        this.mHandler.removeCallbacks(this.mResetConsecutiveTaskSwitches);
        this.mConsecutiveTaskSwitches = 0;
    }

    private void rescheduleConsecutiveTaskSwitchesReset() {
        this.mHandler.removeCallbacks(this.mResetConsecutiveTaskSwitches);
        this.mHandler.postDelayed(this.mResetConsecutiveTaskSwitches, getShowAndGoDelayResetTimeoutMs());
    }

    private boolean isFullyAwake() {
        return this.mIsAwake && !this.mIsDozing;
    }

    private long getLearningTimeMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_learn_time_ms", DEFAULT_LEARNING_TIME_MS);
    }

    private int getLearningCount() {
        return this.mDeviceConfigHelper.getInt("assist_handles_learn_count", 10);
    }

    private long getShowAndGoDelayedShortDelayMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_delayed_short_delay_ms", 150L);
    }

    private long getShowAndGoDelayedLongDelayMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_delayed_long_delay_ms", DEFAULT_SHOW_AND_GO_DELAYED_LONG_DELAY_MS);
    }

    private long getShowAndGoDelayResetTimeoutMs() {
        return this.mDeviceConfigHelper.getLong("assist_handles_show_and_go_delay_reset_timeout_ms", DEFAULT_SHOW_AND_GO_DELAY_RESET_TIMEOUT_MS);
    }

    private boolean getSuppressOnLockscreen() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_suppress_on_lockscreen", false);
    }

    private boolean getSuppressOnLauncher() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_suppress_on_launcher", false);
    }

    private boolean getSuppressOnApps() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_suppress_on_apps", true);
    }

    private boolean getShowWhenTaught() {
        return this.mDeviceConfigHelper.getBoolean("assist_handles_show_when_taught", false);
    }

    @Override // com.android.systemui.assist.AssistHandleBehaviorController.BehaviorController
    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "Current AssistHandleReminderExpBehavior State:");
        pw.println(prefix + "   mOnLockscreen=" + this.mOnLockscreen);
        pw.println(prefix + "   mIsDozing=" + this.mIsDozing);
        pw.println(prefix + "   mIsAwake=" + this.mIsAwake);
        pw.println(prefix + "   mRunningTaskId=" + this.mRunningTaskId);
        pw.println(prefix + "   mDefaultHome=" + this.mDefaultHome);
        pw.println(prefix + "   mIsNavBarHidden=" + this.mIsNavBarHidden);
        pw.println(prefix + "   mIsLauncherShowing=" + this.mIsLauncherShowing);
        pw.println(prefix + "   mConsecutiveTaskSwitches=" + this.mConsecutiveTaskSwitches);
        pw.println(prefix + "   mIsLearned=" + this.mIsLearned);
        pw.println(prefix + "   mLastLearningTimestamp=" + this.mLastLearningTimestamp);
        pw.println(prefix + "   mLearningTimeElapsed=" + this.mLearningTimeElapsed);
        pw.println(prefix + "   mLearningCount=" + this.mLearningCount);
        pw.println(prefix + "   mLearnedHintLastShownEpochDay=" + this.mLearnedHintLastShownEpochDay);
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("   mAssistHandleCallbacks present: ");
        sb.append(this.mAssistHandleCallbacks != null);
        pw.println(sb.toString());
        pw.println(prefix + "   Phenotype Flags:");
        pw.println(prefix + "      assist_handles_learn_time_ms=" + getLearningTimeMs());
        pw.println(prefix + "      assist_handles_learn_count=" + getLearningCount());
        pw.println(prefix + "      assist_handles_show_and_go_delayed_short_delay_ms=" + getShowAndGoDelayedShortDelayMs());
        pw.println(prefix + "      assist_handles_show_and_go_delayed_long_delay_ms=" + getShowAndGoDelayedLongDelayMs());
        pw.println(prefix + "      assist_handles_show_and_go_delay_reset_timeout_ms=" + getShowAndGoDelayResetTimeoutMs());
        pw.println(prefix + "      assist_handles_suppress_on_lockscreen=" + getSuppressOnLockscreen());
        pw.println(prefix + "      assist_handles_suppress_on_launcher=" + getSuppressOnLauncher());
        pw.println(prefix + "      assist_handles_suppress_on_apps=" + getSuppressOnApps());
        pw.println(prefix + "      assist_handles_show_when_taught=" + getShowWhenTaught());
    }
}
