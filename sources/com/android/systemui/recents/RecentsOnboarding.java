package com.android.systemui.recents;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.Prefs;
import com.android.systemui.R;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.recents.IOverviewProxy;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.xiaopeng.lib.framework.moduleinterface.carcontroller.IInputController;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
@TargetApi(28)
/* loaded from: classes21.dex */
public class RecentsOnboarding {
    private static final int BACKOFF_DISMISSAL_COUNT_ON_SWIPE_UP_SHOW = 1;
    private static final long HIDE_DURATION_MS = 100;
    private static final int MAX_DISMISSAL_ON_SWIPE_UP_SHOW = 2;
    private static final boolean ONBOARDING_ENABLED = true;
    private static final int QUICK_SCRUB_SHOW_ON_OVERVIEW_OPENED_COUNT = 10;
    private static final boolean RESET_PREFS_FOR_DEBUG = false;
    private static final long SHOW_DELAY_MS = 500;
    private static final long SHOW_DURATION_MS = 300;
    private static final int SWIPE_UP_SHOW_ON_APP_LAUNCH_AFTER_DISMISS = 5;
    private static final int SWIPE_UP_SHOW_ON_APP_LAUNCH_AFTER_DISMISS_BACK_OFF = 40;
    private static final int SWIPE_UP_SHOW_ON_OVERVIEW_OPENED_FROM_HOME_COUNT = 3;
    private static final String TAG = "RecentsOnboarding";
    private final View mArrowView;
    private Set<String> mBlacklistedPackages;
    private final Context mContext;
    private final ImageView mDismissView;
    private boolean mHasDismissedQuickScrubTip;
    private boolean mHasDismissedSwipeUpTip;
    private final View mLayout;
    private boolean mLayoutAttachedToWindow;
    private int mNavBarHeight;
    private int mNumAppsLaunchedSinceSwipeUpTipDismiss;
    private final int mOnboardingToastArrowRadius;
    private final int mOnboardingToastColor;
    private int mOverviewOpenedCountSinceQuickScrubTipDismiss;
    private boolean mOverviewProxyListenerRegistered;
    private final OverviewProxyService mOverviewProxyService;
    private boolean mTaskListenerRegistered;
    private final TextView mTextView;
    private final WindowManager mWindowManager;
    private int mNavBarMode = 0;
    private final TaskStackChangeListener mTaskListener = new TaskStackChangeListener() { // from class: com.android.systemui.recents.RecentsOnboarding.1
        private String mLastPackageName;

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskCreated(int taskId, ComponentName componentName) {
            onAppLaunch();
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskMovedToFront(int taskId) {
            onAppLaunch();
        }

        private void onAppLaunch() {
            int swipeUpShowOnAppLauncherAfterDismiss;
            ActivityManager.RunningTaskInfo info = ActivityManagerWrapper.getInstance().getRunningTask(0);
            if (info != null) {
                if (RecentsOnboarding.this.mBlacklistedPackages.contains(info.baseActivity.getPackageName())) {
                    RecentsOnboarding.this.hide(true);
                } else if (info.baseActivity.getPackageName().equals(this.mLastPackageName)) {
                } else {
                    this.mLastPackageName = info.baseActivity.getPackageName();
                    int activityType = info.configuration.windowConfiguration.getActivityType();
                    if (activityType == 1) {
                        boolean alreadySeenSwipeUpOnboarding = RecentsOnboarding.this.hasSeenSwipeUpOnboarding();
                        boolean alreadySeenQuickScrubsOnboarding = RecentsOnboarding.this.hasSeenQuickScrubOnboarding();
                        if (alreadySeenSwipeUpOnboarding && alreadySeenQuickScrubsOnboarding) {
                            RecentsOnboarding.this.onDisconnectedFromLauncher();
                            return;
                        }
                        boolean shouldLog = false;
                        if (!alreadySeenSwipeUpOnboarding) {
                            if (RecentsOnboarding.this.getOpenedOverviewFromHomeCount() >= 3) {
                                if (RecentsOnboarding.this.mHasDismissedSwipeUpTip) {
                                    int hasDimissedSwipeUpOnboardingCount = RecentsOnboarding.this.getDismissedSwipeUpOnboardingCount();
                                    if (hasDimissedSwipeUpOnboardingCount > 2) {
                                        return;
                                    }
                                    if (hasDimissedSwipeUpOnboardingCount <= 1) {
                                        swipeUpShowOnAppLauncherAfterDismiss = 5;
                                    } else {
                                        swipeUpShowOnAppLauncherAfterDismiss = 40;
                                    }
                                    RecentsOnboarding.access$608(RecentsOnboarding.this);
                                    if (RecentsOnboarding.this.mNumAppsLaunchedSinceSwipeUpTipDismiss >= swipeUpShowOnAppLauncherAfterDismiss) {
                                        RecentsOnboarding.this.mNumAppsLaunchedSinceSwipeUpTipDismiss = 0;
                                        shouldLog = RecentsOnboarding.this.show(R.string.recents_swipe_up_onboarding);
                                    }
                                } else {
                                    shouldLog = RecentsOnboarding.this.show(R.string.recents_swipe_up_onboarding);
                                }
                                if (shouldLog) {
                                    RecentsOnboarding.this.notifyOnTip(0, 0);
                                    return;
                                }
                                return;
                            }
                            return;
                        } else if (RecentsOnboarding.this.getOpenedOverviewCount() >= 10) {
                            if (RecentsOnboarding.this.mHasDismissedQuickScrubTip) {
                                if (RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss >= 10) {
                                    RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss = 0;
                                    shouldLog = RecentsOnboarding.this.show(R.string.recents_quick_scrub_onboarding);
                                }
                            } else {
                                shouldLog = RecentsOnboarding.this.show(R.string.recents_quick_scrub_onboarding);
                            }
                            if (shouldLog) {
                                RecentsOnboarding.this.notifyOnTip(0, 1);
                                return;
                            }
                            return;
                        } else {
                            return;
                        }
                    }
                    RecentsOnboarding.this.hide(false);
                }
            }
        }
    };
    private OverviewProxyService.OverviewProxyListener mOverviewProxyListener = new OverviewProxyService.OverviewProxyListener() { // from class: com.android.systemui.recents.RecentsOnboarding.2
        @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
        public void onOverviewShown(boolean fromHome) {
            if (!RecentsOnboarding.this.hasSeenSwipeUpOnboarding() && !fromHome) {
                RecentsOnboarding.this.setHasSeenSwipeUpOnboarding(true);
            }
            if (fromHome) {
                RecentsOnboarding.this.incrementOpenedOverviewFromHomeCount();
            }
            RecentsOnboarding.this.incrementOpenedOverviewCount();
            if (RecentsOnboarding.this.getOpenedOverviewCount() >= 10 && RecentsOnboarding.this.mHasDismissedQuickScrubTip) {
                RecentsOnboarding.access$1008(RecentsOnboarding.this);
            }
        }

        @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
        public void onQuickStepStarted() {
            RecentsOnboarding.this.hide(true);
        }

        @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
        public void onQuickScrubStarted() {
            boolean alreadySeenQuickScrubsOnboarding = RecentsOnboarding.this.hasSeenQuickScrubOnboarding();
            if (!alreadySeenQuickScrubsOnboarding) {
                RecentsOnboarding.this.setHasSeenQuickScrubOnboarding(true);
            }
        }
    };
    private final View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() { // from class: com.android.systemui.recents.RecentsOnboarding.3
        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
            if (view == RecentsOnboarding.this.mLayout) {
                RecentsOnboarding.this.mContext.registerReceiver(RecentsOnboarding.this.mReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
                RecentsOnboarding.this.mLayoutAttachedToWindow = true;
                if (view.getTag().equals(Integer.valueOf(R.string.recents_swipe_up_onboarding))) {
                    RecentsOnboarding.this.mHasDismissedSwipeUpTip = false;
                } else {
                    RecentsOnboarding.this.mHasDismissedQuickScrubTip = false;
                }
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            if (view == RecentsOnboarding.this.mLayout) {
                RecentsOnboarding.this.mLayoutAttachedToWindow = false;
                if (view.getTag().equals(Integer.valueOf(R.string.recents_quick_scrub_onboarding))) {
                    RecentsOnboarding.this.mHasDismissedQuickScrubTip = true;
                    if (RecentsOnboarding.this.hasDismissedQuickScrubOnboardingOnce()) {
                        RecentsOnboarding.this.setHasSeenQuickScrubOnboarding(true);
                    } else {
                        RecentsOnboarding.this.setHasDismissedQuickScrubOnboardingOnce(true);
                    }
                    RecentsOnboarding.this.mOverviewOpenedCountSinceQuickScrubTipDismiss = 0;
                }
                RecentsOnboarding.this.mContext.unregisterReceiver(RecentsOnboarding.this.mReceiver);
            }
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.recents.RecentsOnboarding.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                RecentsOnboarding.this.hide(false);
            }
        }
    };

    static /* synthetic */ int access$1008(RecentsOnboarding x0) {
        int i = x0.mOverviewOpenedCountSinceQuickScrubTipDismiss;
        x0.mOverviewOpenedCountSinceQuickScrubTipDismiss = i + 1;
        return i;
    }

    static /* synthetic */ int access$608(RecentsOnboarding x0) {
        int i = x0.mNumAppsLaunchedSinceSwipeUpTipDismiss;
        x0.mNumAppsLaunchedSinceSwipeUpTipDismiss = i + 1;
        return i;
    }

    public RecentsOnboarding(Context context, OverviewProxyService overviewProxyService) {
        this.mContext = context;
        this.mOverviewProxyService = overviewProxyService;
        Resources res = context.getResources();
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mBlacklistedPackages = new HashSet();
        Collections.addAll(this.mBlacklistedPackages, res.getStringArray(R.array.recents_onboarding_blacklisted_packages));
        this.mLayout = LayoutInflater.from(this.mContext).inflate(R.layout.recents_onboarding, (ViewGroup) null);
        this.mTextView = (TextView) this.mLayout.findViewById(R.id.onboarding_text);
        this.mDismissView = (ImageView) this.mLayout.findViewById(R.id.dismiss);
        this.mArrowView = this.mLayout.findViewById(R.id.arrow);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843829, typedValue, true);
        this.mOnboardingToastColor = res.getColor(typedValue.resourceId);
        this.mOnboardingToastArrowRadius = res.getDimensionPixelSize(R.dimen.recents_onboarding_toast_arrow_corner_radius);
        this.mLayout.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        this.mDismissView.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.recents.-$$Lambda$RecentsOnboarding$VU_OZtWyvAx7bVWSUdhKQFeocZE
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RecentsOnboarding.this.lambda$new$0$RecentsOnboarding(view);
            }
        });
        ViewGroup.LayoutParams arrowLp = this.mArrowView.getLayoutParams();
        ShapeDrawable arrowDrawable = new ShapeDrawable(TriangleShape.create(arrowLp.width, arrowLp.height, false));
        Paint arrowPaint = arrowDrawable.getPaint();
        arrowPaint.setColor(this.mOnboardingToastColor);
        arrowPaint.setPathEffect(new CornerPathEffect(this.mOnboardingToastArrowRadius));
        this.mArrowView.setBackground(arrowDrawable);
    }

    public /* synthetic */ void lambda$new$0$RecentsOnboarding(View v) {
        hide(true);
        if (v.getTag().equals(Integer.valueOf(R.string.recents_swipe_up_onboarding))) {
            this.mHasDismissedSwipeUpTip = true;
            this.mNumAppsLaunchedSinceSwipeUpTipDismiss = 0;
            setDismissedSwipeUpOnboardingCount(getDismissedSwipeUpOnboardingCount() + 1);
            if (getDismissedSwipeUpOnboardingCount() > 2) {
                setHasSeenSwipeUpOnboarding(true);
            }
            notifyOnTip(1, 0);
            return;
        }
        notifyOnTip(1, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyOnTip(int action, int target) {
        try {
            IOverviewProxy overviewProxy = this.mOverviewProxyService.getProxy();
            if (overviewProxy != null) {
                overviewProxy.onTip(action, target);
            }
        } catch (RemoteException e) {
        }
    }

    public void onNavigationModeChanged(int mode) {
        this.mNavBarMode = mode;
    }

    public void onConnectedToLauncher() {
        if (QuickStepContract.isGesturalMode(this.mNavBarMode)) {
            return;
        }
        if (hasSeenSwipeUpOnboarding() && hasSeenQuickScrubOnboarding()) {
            return;
        }
        if (!this.mOverviewProxyListenerRegistered) {
            this.mOverviewProxyService.addCallback(this.mOverviewProxyListener);
            this.mOverviewProxyListenerRegistered = true;
        }
        if (!this.mTaskListenerRegistered) {
            ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskListener);
            this.mTaskListenerRegistered = true;
        }
    }

    public void onDisconnectedFromLauncher() {
        if (this.mOverviewProxyListenerRegistered) {
            this.mOverviewProxyService.removeCallback(this.mOverviewProxyListener);
            this.mOverviewProxyListenerRegistered = false;
        }
        if (this.mTaskListenerRegistered) {
            ActivityManagerWrapper.getInstance().unregisterTaskStackListener(this.mTaskListener);
            this.mTaskListenerRegistered = false;
        }
        this.mHasDismissedSwipeUpTip = false;
        this.mHasDismissedQuickScrubTip = false;
        this.mNumAppsLaunchedSinceSwipeUpTipDismiss = 0;
        this.mOverviewOpenedCountSinceQuickScrubTipDismiss = 0;
        hide(true);
    }

    public void onConfigurationChanged(Configuration newConfiguration) {
        if (newConfiguration.orientation != 1) {
            hide(false);
        }
    }

    public boolean show(int stringRes) {
        int gravity;
        int layoutDirection;
        if (shouldShow()) {
            this.mDismissView.setTag(Integer.valueOf(stringRes));
            this.mLayout.setTag(Integer.valueOf(stringRes));
            this.mTextView.setText(stringRes);
            int orientation = this.mContext.getResources().getConfiguration().orientation;
            if (this.mLayoutAttachedToWindow || orientation != 1) {
                return false;
            }
            this.mLayout.setSystemUiVisibility(256);
            if (stringRes == R.string.recents_swipe_up_onboarding) {
                layoutDirection = 81;
                gravity = 0;
            } else {
                int layoutDirection2 = this.mContext.getResources().getConfiguration().getLayoutDirection();
                int gravity2 = (layoutDirection2 == 0 ? 3 : 5) | 80;
                int layoutDirection3 = this.mContext.getResources().getDimensionPixelSize(R.dimen.recents_quick_scrub_onboarding_margin_start);
                gravity = layoutDirection3;
                layoutDirection = gravity2;
            }
            this.mWindowManager.addView(this.mLayout, getWindowLayoutParams(layoutDirection, gravity));
            this.mLayout.setAlpha(0.0f);
            this.mLayout.animate().alpha(1.0f).withLayer().setStartDelay(500L).setDuration(SHOW_DURATION_MS).setInterpolator(new DecelerateInterpolator()).start();
            return true;
        }
        return false;
    }

    private boolean shouldShow() {
        return SystemProperties.getBoolean("persist.quickstep.onboarding.enabled", (((UserManager) this.mContext.getSystemService(UserManager.class)).isDemoUser() || ActivityManager.isRunningInTestHarness()) ? false : true);
    }

    public void hide(boolean animate) {
        if (this.mLayoutAttachedToWindow) {
            if (animate) {
                this.mLayout.animate().alpha(0.0f).withLayer().setStartDelay(0L).setDuration(HIDE_DURATION_MS).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() { // from class: com.android.systemui.recents.-$$Lambda$RecentsOnboarding$qki5o8zqrWEPaWaslagffDePdhg
                    @Override // java.lang.Runnable
                    public final void run() {
                        RecentsOnboarding.this.lambda$hide$1$RecentsOnboarding();
                    }
                }).start();
                return;
            }
            this.mLayout.animate().cancel();
            this.mWindowManager.removeViewImmediate(this.mLayout);
        }
    }

    public /* synthetic */ void lambda$hide$1$RecentsOnboarding() {
        this.mWindowManager.removeViewImmediate(this.mLayout);
    }

    public void setNavBarHeight(int navBarHeight) {
        this.mNavBarHeight = navBarHeight;
    }

    public void dump(PrintWriter pw) {
        pw.println("RecentsOnboarding {");
        pw.println("      mTaskListenerRegistered: " + this.mTaskListenerRegistered);
        pw.println("      mOverviewProxyListenerRegistered: " + this.mOverviewProxyListenerRegistered);
        pw.println("      mLayoutAttachedToWindow: " + this.mLayoutAttachedToWindow);
        pw.println("      mHasDismissedSwipeUpTip: " + this.mHasDismissedSwipeUpTip);
        pw.println("      mHasDismissedQuickScrubTip: " + this.mHasDismissedQuickScrubTip);
        pw.println("      mNumAppsLaunchedSinceSwipeUpTipDismiss: " + this.mNumAppsLaunchedSinceSwipeUpTipDismiss);
        pw.println("      hasSeenSwipeUpOnboarding: " + hasSeenSwipeUpOnboarding());
        pw.println("      hasSeenQuickScrubOnboarding: " + hasSeenQuickScrubOnboarding());
        pw.println("      getDismissedSwipeUpOnboardingCount: " + getDismissedSwipeUpOnboardingCount());
        pw.println("      hasDismissedQuickScrubOnboardingOnce: " + hasDismissedQuickScrubOnboardingOnce());
        pw.println("      getOpenedOverviewCount: " + getOpenedOverviewCount());
        pw.println("      getOpenedOverviewFromHomeCount: " + getOpenedOverviewFromHomeCount());
        pw.println("    }");
    }

    private WindowManager.LayoutParams getWindowLayoutParams(int gravity, int x) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-2, -2, x, (-this.mNavBarHeight) / 2, 2038, IInputController.KEYCODE_KNOB_VOL_UP, -3);
        lp.privateFlags |= 16;
        lp.setTitle(TAG);
        lp.gravity = gravity;
        return lp;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasSeenSwipeUpOnboarding() {
        return Prefs.getBoolean(this.mContext, Prefs.Key.HAS_SEEN_RECENTS_SWIPE_UP_ONBOARDING, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHasSeenSwipeUpOnboarding(boolean hasSeenSwipeUpOnboarding) {
        Prefs.putBoolean(this.mContext, Prefs.Key.HAS_SEEN_RECENTS_SWIPE_UP_ONBOARDING, hasSeenSwipeUpOnboarding);
        if (hasSeenSwipeUpOnboarding && hasSeenQuickScrubOnboarding()) {
            onDisconnectedFromLauncher();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasSeenQuickScrubOnboarding() {
        return Prefs.getBoolean(this.mContext, Prefs.Key.HAS_SEEN_RECENTS_QUICK_SCRUB_ONBOARDING, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHasSeenQuickScrubOnboarding(boolean hasSeenQuickScrubOnboarding) {
        Prefs.putBoolean(this.mContext, Prefs.Key.HAS_SEEN_RECENTS_QUICK_SCRUB_ONBOARDING, hasSeenQuickScrubOnboarding);
        if (hasSeenQuickScrubOnboarding && hasSeenSwipeUpOnboarding()) {
            onDisconnectedFromLauncher();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDismissedSwipeUpOnboardingCount() {
        return Prefs.getInt(this.mContext, Prefs.Key.DISMISSED_RECENTS_SWIPE_UP_ONBOARDING_COUNT, 0);
    }

    private void setDismissedSwipeUpOnboardingCount(int dismissedSwipeUpOnboardingCount) {
        Prefs.putInt(this.mContext, Prefs.Key.DISMISSED_RECENTS_SWIPE_UP_ONBOARDING_COUNT, dismissedSwipeUpOnboardingCount);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasDismissedQuickScrubOnboardingOnce() {
        return Prefs.getBoolean(this.mContext, Prefs.Key.HAS_DISMISSED_RECENTS_QUICK_SCRUB_ONBOARDING_ONCE, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHasDismissedQuickScrubOnboardingOnce(boolean hasDismissedQuickScrubOnboardingOnce) {
        Prefs.putBoolean(this.mContext, Prefs.Key.HAS_DISMISSED_RECENTS_QUICK_SCRUB_ONBOARDING_ONCE, hasDismissedQuickScrubOnboardingOnce);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getOpenedOverviewFromHomeCount() {
        return Prefs.getInt(this.mContext, Prefs.Key.OVERVIEW_OPENED_FROM_HOME_COUNT, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void incrementOpenedOverviewFromHomeCount() {
        int openedOverviewFromHomeCount = getOpenedOverviewFromHomeCount();
        if (openedOverviewFromHomeCount >= 3) {
            return;
        }
        setOpenedOverviewFromHomeCount(openedOverviewFromHomeCount + 1);
    }

    private void setOpenedOverviewFromHomeCount(int openedOverviewFromHomeCount) {
        Prefs.putInt(this.mContext, Prefs.Key.OVERVIEW_OPENED_FROM_HOME_COUNT, openedOverviewFromHomeCount);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getOpenedOverviewCount() {
        return Prefs.getInt(this.mContext, Prefs.Key.OVERVIEW_OPENED_COUNT, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void incrementOpenedOverviewCount() {
        int openedOverviewCount = getOpenedOverviewCount();
        if (openedOverviewCount >= 10) {
            return;
        }
        setOpenedOverviewCount(openedOverviewCount + 1);
    }

    private void setOpenedOverviewCount(int openedOverviewCount) {
        Prefs.putInt(this.mContext, Prefs.Key.OVERVIEW_OPENED_COUNT, openedOverviewCount);
    }
}
