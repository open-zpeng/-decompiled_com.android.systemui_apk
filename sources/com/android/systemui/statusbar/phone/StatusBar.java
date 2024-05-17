package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.IWallpaperManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.app.UiModeManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.service.notification.StatusBarNotification;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.view.Display;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.RemoteAnimationAdapter;
import android.view.ThreadedRenderer;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.DateTimeView;
import android.widget.ImageView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.RegisterStatusBarResult;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.ActivityStarterDelegate;
import com.android.systemui.AutoReinflateContainer;
import com.android.systemui.DemoMode;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.EventLogTags;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.InitController;
import com.android.systemui.Interpolators;
import com.android.systemui.Prefs;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.appops.AppOpsController;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.charging.WirelessChargingAnimation;
import com.android.systemui.classifier.FalsingLog;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.doze.DozeHost;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.doze.DozeReceiver;
import com.android.systemui.fragments.ExtensionFragmentListener;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.globalactions.GlobalActionsDialog;
import com.android.systemui.keyguard.KeyguardSliceProvider;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.QSFragment;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.ScreenPinningRequest;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.stackdivider.WindowManagerProxy;
import com.android.systemui.statusbar.BackDropView;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.EmptyShadeView;
import com.android.systemui.statusbar.GestureRecorder;
import com.android.systemui.statusbar.KeyboardShortcuts;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.BypassHeadsUpNotifier;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.NotificationClicker;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationInterruptionStateProvider;
import com.android.systemui.statusbar.notification.NotificationListController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.ViewGroupFadeHelper;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.UnlockMethodCache;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.volume.VolumeComponent;
import com.badlogic.gdx.graphics.GL30;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import dagger.Subcomponent;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.text.Typography;
/* loaded from: classes21.dex */
public class StatusBar extends SystemUI implements DemoMode, ActivityStarter, UnlockMethodCache.OnUnlockMethodChangedListener, OnHeadsUpChangedListener, CommandQueue.Callbacks, ZenModeController.Callback, ColorExtractor.OnColorsChangedListener, ConfigurationController.ConfigurationListener, StatusBarStateController.StateListener, ShadeController, ActivityLaunchAnimator.Callback, AppOpsController.Callback {
    public static final String ACTION_FAKE_ARTWORK = "fake_artwork";
    protected static final int[] APP_OPS;
    private static final String BANNER_ACTION_CANCEL = "com.android.systemui.statusbar.banner_action_cancel";
    private static final String BANNER_ACTION_SETUP = "com.android.systemui.statusbar.banner_action_setup";
    public static final boolean CHATTY = false;
    protected static final boolean CLOSE_PANEL_WHEN_EMPTIED = true;
    public static final boolean DEBUG = false;
    public static final boolean DEBUG_CAMERA_LIFT = false;
    public static final boolean DEBUG_GESTURES = false;
    public static final boolean DEBUG_MEDIA_FAKE_ARTWORK = false;
    public static final boolean DEBUG_WINDOW_STATE = false;
    public static final boolean DUMPTRUCK = true;
    public static final boolean ENABLE_LOCKSCREEN_WALLPAPER = true;
    public static final int FADE_KEYGUARD_DURATION = 300;
    public static final int FADE_KEYGUARD_DURATION_PULSING = 96;
    public static final int FADE_KEYGUARD_START_DELAY = 100;
    private static final int HINT_RESET_DELAY_MS = 1200;
    private static final long LAUNCH_TRANSITION_TIMEOUT_MS = 5000;
    protected static final int MSG_CANCEL_PRELOAD_RECENT_APPS = 1023;
    private static final int MSG_CLOSE_PANELS = 1001;
    protected static final int MSG_DISMISS_KEYBOARD_SHORTCUTS_MENU = 1027;
    protected static final int MSG_HIDE_RECENT_APPS = 1020;
    private static final int MSG_LAUNCH_TRANSITION_TIMEOUT = 1003;
    private static final int MSG_OPEN_NOTIFICATION_PANEL = 1000;
    private static final int MSG_OPEN_SETTINGS_PANEL = 1002;
    protected static final int MSG_PRELOAD_RECENT_APPS = 1022;
    protected static final int MSG_TOGGLE_KEYBOARD_SHORTCUTS_MENU = 1026;
    public static final boolean MULTIUSER_DEBUG = false;
    public static final boolean ONLY_CORE_APPS;
    public static final boolean SHOW_LOCKSCREEN_MEDIA_ARTWORK = true;
    public static final boolean SPEW = false;
    public static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    public static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    public static final String SYSTEM_DIALOG_REASON_SCREENSHOT = "screenshot";
    public static final String TAG = "StatusBar";
    protected AccessibilityManager mAccessibilityManager;
    private ActivityIntentHelper mActivityIntentHelper;
    private ActivityLaunchAnimator mActivityLaunchAnimator;
    @Inject
    @Named(Dependency.ALLOW_NOTIFICATION_LONG_PRESS_NAME)
    boolean mAllowNotificationLongPress;
    private View mAmbientIndicationContainer;
    protected AppOpsController mAppOpsController;
    protected AssistManager mAssistManager;
    @VisibleForTesting
    protected AutoHideController mAutoHideController;
    protected IStatusBarService mBarService;
    protected BatteryController mBatteryController;
    protected BiometricUnlockController mBiometricUnlockController;
    protected boolean mBouncerShowing;
    private boolean mBouncerWasShowingWhenHidden;
    private BrightnessMirrorController mBrightnessMirrorController;
    private boolean mBrightnessMirrorVisible;
    protected BubbleController mBubbleController;
    @Inject
    BypassHeadsUpNotifier mBypassHeadsUpNotifier;
    private long[] mCameraLaunchGestureVibePattern;
    protected SysuiColorExtractor mColorExtractor;
    protected CommandQueue mCommandQueue;
    private boolean mDemoMode;
    private boolean mDemoModeAllowed;
    protected boolean mDeviceInteractive;
    protected DevicePolicyManager mDevicePolicyManager;
    protected Display mDisplay;
    private int mDisplayId;
    protected DozeScrimController mDozeScrimController;
    protected boolean mDozing;
    private boolean mDozingRequested;
    private NotificationEntry mDraggedDownEntry;
    private IDreamManager mDreamManager;
    @Inject
    DynamicPrivacyController mDynamicPrivacyController;
    protected EmptyShadeView mEmptyShadeView;
    protected NotificationEntryManager mEntryManager;
    private boolean mExpandedVisible;
    protected FalsingManager mFalsingManager;
    protected ForegroundServiceController mForegroundServiceController;
    protected PowerManager.WakeLock mGestureWakeLock;
    protected NotificationGroupAlertTransferHelper mGroupAlertTransferHelper;
    protected NotificationGroupManager mGroupManager;
    protected NotificationGutsManager mGutsManager;
    private HeadsUpAppearanceController mHeadsUpAppearanceController;
    @Inject
    protected HeadsUpManagerPhone mHeadsUpManager;
    private boolean mHideIconsForBouncer;
    protected StatusBarIconController mIconController;
    private PhoneStatusBarPolicy mIconPolicy;
    @Inject
    InjectionInflationController mInjectionInflater;
    private int mInteractingWindows;
    protected boolean mIsKeyguard;
    private boolean mIsOccluded;
    @Inject
    KeyguardBypassController mKeyguardBypassController;
    KeyguardIndicationController mKeyguardIndicationController;
    @Inject
    protected KeyguardLiftController mKeyguardLiftController;
    protected KeyguardManager mKeyguardManager;
    protected KeyguardMonitor mKeyguardMonitor;
    @VisibleForTesting
    KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    protected KeyguardViewMediator mKeyguardViewMediator;
    private ViewMediatorCallback mKeyguardViewMediatorCallback;
    private int mLastCameraLaunchSource;
    private int mLastLoggedStateFingerprint;
    private boolean mLaunchCameraOnFinishedGoingToSleep;
    private boolean mLaunchCameraWhenFinishedWaking;
    private Runnable mLaunchTransitionEndRunnable;
    protected LightBarController mLightBarController;
    protected NotificationLockscreenUserManager mLockscreenUserManager;
    protected LockscreenWallpaper mLockscreenWallpaper;
    protected NotificationMediaManager mMediaManager;
    protected NavigationBarController mNavigationBarController;
    protected NetworkController mNetworkController;
    private boolean mNoAnimationOnNextBarModeChange;
    private NotificationActivityStarter mNotificationActivityStarter;
    protected NotificationIconAreaController mNotificationIconAreaController;
    protected NotificationInterruptionStateProvider mNotificationInterruptionStateProvider;
    protected NotificationListController mNotificationListController;
    protected NotificationListener mNotificationListener;
    protected NotificationLogger mNotificationLogger;
    protected NotificationPanelView mNotificationPanel;
    protected NotificationShelf mNotificationShelf;
    protected boolean mPanelExpanded;
    private View mPendingRemoteInputView;
    protected PowerManager mPowerManager;
    protected StatusBarNotificationPresenter mPresenter;
    @Inject
    PulseExpansionHandler mPulseExpansionHandler;
    private boolean mPulsing;
    private QSPanel mQSPanel;
    protected Recents mRecents;
    protected NotificationRemoteInputManager mRemoteInputManager;
    private View mReportRejectedTouch;
    protected ScreenLifecycle mScreenLifecycle;
    private ScreenPinningRequest mScreenPinningRequest;
    protected ScrimController mScrimController;
    private ShadeController mShadeController;
    private StatusBarSignalPolicy mSignalPolicy;
    protected ViewGroup mStackScroller;
    protected int mState;
    protected StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private int mStatusBarMode;
    private LogMaker mStatusBarStateLog;
    protected PhoneStatusBarView mStatusBarView;
    protected StatusBarWindowView mStatusBarWindow;
    protected StatusBarWindowController mStatusBarWindowController;
    private boolean mStatusBarWindowHidden;
    private boolean mTopHidesStatusBar;
    private UiModeManager mUiModeManager;
    protected UnlockMethodCache mUnlockMethodCache;
    protected UserSwitcherController mUserSwitcherController;
    private boolean mVibrateOnOpening;
    private Vibrator mVibrator;
    protected VibratorHelper mVibratorHelper;
    protected NotificationViewHierarchyManager mViewHierarchyManager;
    protected boolean mVisible;
    private boolean mVisibleToUser;
    protected VisualStabilityManager mVisualStabilityManager;
    private VolumeComponent mVolumeComponent;
    private boolean mWakeUpComingFromTouch;
    @Inject
    NotificationWakeUpCoordinator mWakeUpCoordinator;
    private PointF mWakeUpTouchLocation;
    @VisibleForTesting
    protected WakefulnessLifecycle mWakefulnessLifecycle;
    private boolean mWallpaperSupported;
    private boolean mWereIconsJustHidden;
    protected WindowManager mWindowManager;
    protected IWindowManager mWindowManagerService;
    protected ZenModeController mZenController;
    public static final boolean ENABLE_CHILD_NOTIFICATIONS = SystemProperties.getBoolean("debug.child_notifs", true);
    private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    private int mNaturalBarHeight = -1;
    private final Point mCurrentDisplaySize = new Point();
    private int mStatusBarWindowState = 0;
    @VisibleForTesting
    DozeServiceHost mDozeServiceHost = new DozeServiceHost();
    private final Object mQueueLock = new Object();
    private RemoteInputQuickSettingsDisabler mRemoteInputQuickSettingsDisabler = (RemoteInputQuickSettingsDisabler) Dependency.get(RemoteInputQuickSettingsDisabler.class);
    private final int[] mAbsPos = new int[2];
    private final ArrayList<Runnable> mPostCollapseRunnables = new ArrayList<>();
    protected final NotificationAlertingManager mNotificationAlertingManager = (NotificationAlertingManager) Dependency.get(NotificationAlertingManager.class);
    private int mDisabled1 = 0;
    private int mDisabled2 = 0;
    private int mSystemUiVisibility = 0;
    private final Rect mLastFullscreenStackBounds = new Rect();
    private final Rect mLastDockedStackBounds = new Rect();
    private final DisplayMetrics mDisplayMetrics = (DisplayMetrics) Dependency.get(DisplayMetrics.class);
    private final GestureRecorder mGestureRec = null;
    private final MetricsLogger mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
    @VisibleForTesting
    protected boolean mUserSetup = false;
    private final DeviceProvisionedController.DeviceProvisionedListener mUserSetupObserver = new DeviceProvisionedController.DeviceProvisionedListener() { // from class: com.android.systemui.statusbar.phone.StatusBar.1
        @Override // com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener
        public void onUserSetupChanged() {
            boolean userSetup = StatusBar.this.mDeviceProvisionedController.isUserSetup(StatusBar.this.mDeviceProvisionedController.getCurrentUser());
            Log.d(StatusBar.TAG, "mUserSetupObserver - DeviceProvisionedListener called for user " + StatusBar.this.mDeviceProvisionedController.getCurrentUser());
            if (userSetup != StatusBar.this.mUserSetup) {
                StatusBar statusBar = StatusBar.this;
                statusBar.mUserSetup = userSetup;
                if (!statusBar.mUserSetup && StatusBar.this.mStatusBarView != null) {
                    StatusBar.this.animateCollapseQuickSettings();
                }
                if (StatusBar.this.mNotificationPanel != null) {
                    StatusBar.this.mNotificationPanel.setUserSetupComplete(StatusBar.this.mUserSetup);
                }
                StatusBar.this.updateQsExpansionEnabled();
            }
        }
    };
    protected final H mHandler = createHandler();
    private final UiOffloadThread mUiOffloadThread = (UiOffloadThread) Dependency.get(UiOffloadThread.class);
    private final BroadcastReceiver mWallpaperChangedReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.phone.StatusBar.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (!StatusBar.this.mWallpaperSupported) {
                Log.wtf(StatusBar.TAG, "WallpaperManager not supported");
                return;
            }
            WallpaperManager wallpaperManager = (WallpaperManager) context.getSystemService(WallpaperManager.class);
            WallpaperInfo info = wallpaperManager.getWallpaperInfo(-2);
            boolean deviceSupportsAodWallpaper = StatusBar.this.mContext.getResources().getBoolean(17891421);
            boolean supportsAmbientMode = true;
            boolean imageWallpaperInAmbient = !DozeParameters.getInstance(StatusBar.this.mContext).getDisplayNeedsBlanking();
            if (!deviceSupportsAodWallpaper || ((info != null || !imageWallpaperInAmbient) && (info == null || !info.supportsAmbientMode()))) {
                supportsAmbientMode = false;
            }
            StatusBar.this.mStatusBarWindowController.setWallpaperSupportsAmbientMode(supportsAmbientMode);
            StatusBar.this.mScrimController.setWallpaperSupportsAmbientMode(supportsAmbientMode);
        }
    };
    private final int[] mTmpInt2 = new int[2];
    private final ScrimController.Callback mUnlockScrimCallback = new ScrimController.Callback() { // from class: com.android.systemui.statusbar.phone.StatusBar.3
        @Override // com.android.systemui.statusbar.phone.ScrimController.Callback
        public void onFinished() {
            if (StatusBar.this.mStatusBarKeyguardViewManager == null) {
                Log.w(StatusBar.TAG, "Tried to notify keyguard visibility when mStatusBarKeyguardViewManager was null");
            } else if (StatusBar.this.mKeyguardMonitor.isKeyguardFadingAway()) {
                StatusBar.this.mStatusBarKeyguardViewManager.onKeyguardFadedAway();
            }
        }

        @Override // com.android.systemui.statusbar.phone.ScrimController.Callback
        public void onCancelled() {
            onFinished();
        }
    };
    private final View.OnClickListener mGoToLockedShadeListener = new View.OnClickListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$yGW3L-liHoPrdVSisJBkD7OsnTE
        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            StatusBar.this.lambda$new$0$StatusBar(view);
        }
    };
    private final SysuiStatusBarStateController mStatusBarStateController = (SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class);
    private final KeyguardUpdateMonitorCallback mUpdateCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.statusbar.phone.StatusBar.4
        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onDreamingStateChanged(boolean dreaming) {
            if (dreaming) {
                StatusBar.this.maybeEscalateHeadsUp();
            }
        }

        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onStrongAuthStateChanged(int userId) {
            super.onStrongAuthStateChanged(userId);
            StatusBar.this.mEntryManager.updateNotifications();
        }
    };
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private final BubbleController.BubbleExpandListener mBubbleExpandListener = new BubbleController.BubbleExpandListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$be2UvXBqvJVkeR4_MOL5Z579OFk
        @Override // com.android.systemui.bubbles.BubbleController.BubbleExpandListener
        public final void onBubbleExpandChanged(boolean z, String str) {
            StatusBar.this.lambda$new$1$StatusBar(z, str);
        }
    };
    private final Runnable mAnimateCollapsePanels = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$hcoUGmHpwgtk12ln4V8HNBe6RFA
        @Override // java.lang.Runnable
        public final void run() {
            StatusBar.this.animateCollapsePanels();
        }
    };
    private final Runnable mCheckBarModes = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$KBnY14rlKZ6x8gvk_goBuFrr5eE
        @Override // java.lang.Runnable
        public final void run() {
            StatusBar.this.checkBarModes();
        }
    };
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.phone.StatusBar.8
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action)) {
                KeyboardShortcuts.dismiss();
                if (StatusBar.this.mRemoteInputManager.getController() != null) {
                    StatusBar.this.mRemoteInputManager.getController().closeRemoteInputs();
                }
                if (StatusBar.this.mBubbleController != null && StatusBar.this.mBubbleController.isStackExpanded()) {
                    StatusBar.this.mBubbleController.collapseStack();
                }
                if (StatusBar.this.mLockscreenUserManager.isCurrentProfile(getSendingUserId())) {
                    int flags = 0;
                    String reason = intent.getStringExtra(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY);
                    if (reason != null && reason.equals("recentapps")) {
                        flags = 0 | 2;
                    }
                    StatusBar.this.animateCollapsePanels(flags);
                }
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                if (StatusBar.this.mStatusBarWindowController != null) {
                    StatusBar.this.mStatusBarWindowController.setNotTouchable(false);
                }
                if (StatusBar.this.mBubbleController != null && StatusBar.this.mBubbleController.isStackExpanded()) {
                    StatusBar.this.mBubbleController.collapseStack();
                }
                StatusBar.this.finishBarAnimations();
                StatusBar.this.resetUserExpandedStates();
            } else if ("android.app.action.SHOW_DEVICE_MONITORING_DIALOG".equals(action)) {
                StatusBar.this.mQSPanel.showDeviceMonitoringDialog();
            }
        }
    };
    private final BroadcastReceiver mDemoReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.phone.StatusBar.9
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DemoMode.ACTION_DEMO.equals(action)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String command = bundle.getString("command", "").trim().toLowerCase();
                    if (command.length() > 0) {
                        try {
                            StatusBar.this.dispatchDemoCommand(command, bundle);
                            return;
                        } catch (Throwable t) {
                            Log.w(StatusBar.TAG, "Error running demo command, intent=" + intent, t);
                            return;
                        }
                    }
                    return;
                }
                return;
            }
            StatusBar.ACTION_FAKE_ARTWORK.equals(action);
        }
    };
    final Runnable mStartTracing = new Runnable() { // from class: com.android.systemui.statusbar.phone.StatusBar.10
        @Override // java.lang.Runnable
        public void run() {
            StatusBar.this.vibrate();
            SystemClock.sleep(250L);
            Log.d(StatusBar.TAG, "startTracing");
            Debug.startMethodTracing("/data/statusbar-traces/trace");
            StatusBar.this.mHandler.postDelayed(StatusBar.this.mStopTracing, 10000L);
        }
    };
    final Runnable mStopTracing = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$RAI9_BB0sxI6fAXVPwmNkObnx6k
        @Override // java.lang.Runnable
        public final void run() {
            StatusBar.this.lambda$new$22$StatusBar();
        }
    };
    @VisibleForTesting
    final WakefulnessLifecycle.Observer mWakefulnessObserver = new AnonymousClass12();
    final ScreenLifecycle.Observer mScreenObserver = new ScreenLifecycle.Observer() { // from class: com.android.systemui.statusbar.phone.StatusBar.13
        @Override // com.android.systemui.keyguard.ScreenLifecycle.Observer
        public void onScreenTurningOn() {
            StatusBar.this.mFalsingManager.onScreenTurningOn();
            StatusBar.this.mNotificationPanel.onScreenTurningOn();
        }

        @Override // com.android.systemui.keyguard.ScreenLifecycle.Observer
        public void onScreenTurnedOn() {
            StatusBar.this.mScrimController.onScreenTurnedOn();
        }

        @Override // com.android.systemui.keyguard.ScreenLifecycle.Observer
        public void onScreenTurnedOff() {
            StatusBar.this.mFalsingManager.onScreenOff();
            StatusBar.this.mScrimController.onScreenTurnedOff();
            StatusBar.this.updateIsKeyguard();
        }
    };
    protected DeviceProvisionedController mDeviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
    private final BroadcastReceiver mBannerActionBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.phone.StatusBar.14
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (StatusBar.BANNER_ACTION_CANCEL.equals(action) || StatusBar.BANNER_ACTION_SETUP.equals(action)) {
                NotificationManager noMan = (NotificationManager) StatusBar.this.mContext.getSystemService("notification");
                noMan.cancel(5);
                Settings.Secure.putInt(StatusBar.this.mContext.getContentResolver(), "show_note_about_notification_hiding", 0);
                if (StatusBar.BANNER_ACTION_SETUP.equals(action)) {
                    StatusBar.this.animateCollapsePanels(2, true);
                    StatusBar.this.mContext.startActivity(new Intent("android.settings.ACTION_APP_NOTIFICATION_REDACTION").addFlags(268435456));
                }
            }
        }
    };

    @Subcomponent
    /* loaded from: classes21.dex */
    public interface StatusBarInjector {
        void createStatusBar(StatusBar statusBar);
    }

    static {
        boolean onlyCoreApps;
        try {
            IPackageManager packageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
            onlyCoreApps = packageManager.isOnlyCoreApps();
        } catch (RemoteException e) {
            onlyCoreApps = false;
        }
        ONLY_CORE_APPS = onlyCoreApps;
        APP_OPS = new int[]{26, 24, 27, 0, 1};
    }

    public /* synthetic */ void lambda$new$0$StatusBar(View v) {
        if (this.mState == 1) {
            wakeUpIfDozing(SystemClock.uptimeMillis(), v, "SHADE_CLICK");
            goToLockedShade(null);
        }
    }

    public /* synthetic */ void lambda$new$1$StatusBar(boolean isExpanding, String key) {
        this.mEntryManager.updateNotifications();
        updateScrimController();
    }

    @Override // com.android.systemui.appops.AppOpsController.Callback
    public void onActiveStateChanged(final int code, final int uid, final String packageName, final boolean active) {
        ((Handler) Dependency.get(Dependency.MAIN_HANDLER)).post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$1N2jdpaP82HJRT31BJo2G2gJK5c
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$onActiveStateChanged$2$StatusBar(code, uid, packageName, active);
            }
        });
    }

    public /* synthetic */ void lambda$onActiveStateChanged$2$StatusBar(int code, int uid, String packageName, boolean active) {
        this.mForegroundServiceController.onAppOpChanged(code, uid, packageName, active);
        this.mNotificationListController.updateNotificationsForAppOp(code, uid, packageName, active);
    }

    @Override // com.android.systemui.SystemUI
    public void start() {
        RegisterStatusBarResult result;
        WakefulnessLifecycle.Observer observer;
        ScreenLifecycle.Observer observer2;
        getDependencies();
        ScreenLifecycle screenLifecycle = this.mScreenLifecycle;
        if (screenLifecycle != null && (observer2 = this.mScreenObserver) != null) {
            screenLifecycle.addObserver(observer2);
        }
        WakefulnessLifecycle wakefulnessLifecycle = this.mWakefulnessLifecycle;
        if (wakefulnessLifecycle != null && (observer = this.mWakefulnessObserver) != null) {
            wakefulnessLifecycle.addObserver(observer);
        }
        this.mNotificationListener.registerAsSystemService();
        BubbleController bubbleController = this.mBubbleController;
        if (bubbleController != null) {
            bubbleController.setExpandListener(this.mBubbleExpandListener);
        }
        this.mUiModeManager = (UiModeManager) this.mContext.getSystemService(UiModeManager.class);
        this.mKeyguardViewMediator = (KeyguardViewMediator) getComponent(KeyguardViewMediator.class);
        this.mActivityIntentHelper = new ActivityIntentHelper(this.mContext);
        KeyguardSliceProvider sliceProvider = KeyguardSliceProvider.getAttachedInstance();
        if (sliceProvider != null) {
            sliceProvider.initDependencies(this.mMediaManager, this.mStatusBarStateController, this.mKeyguardBypassController, DozeParameters.getInstance(this.mContext));
        } else {
            Log.w(TAG, "Cannot init KeyguardSliceProvider dependencies");
        }
        this.mColorExtractor.addOnColorsChangedListener(this);
        this.mStatusBarStateController.addCallback(this, 0);
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.checkService("dreams"));
        this.mDisplay = this.mWindowManager.getDefaultDisplay();
        this.mDisplayId = this.mDisplay.getDisplayId();
        updateDisplaySize();
        this.mVibrateOnOpening = this.mContext.getResources().getBoolean(R.bool.config_vibrateOnIconAnimation);
        this.mVibratorHelper = (VibratorHelper) Dependency.get(VibratorHelper.class);
        DateTimeView.setReceiverHandler((Handler) Dependency.get(Dependency.TIME_TICK_HANDLER));
        putComponent(StatusBar.class, this);
        this.mWindowManagerService = WindowManagerGlobal.getWindowManagerService();
        this.mDevicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        this.mKeyguardUpdateMonitor.setKeyguardBypassController(this.mKeyguardBypassController);
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mRecents = (Recents) getComponent(Recents.class);
        this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService("keyguard");
        this.mFalsingManager = (FalsingManager) Dependency.get(FalsingManager.class);
        this.mWallpaperSupported = ((WallpaperManager) this.mContext.getSystemService(WallpaperManager.class)).isWallpaperSupported();
        this.mCommandQueue = (CommandQueue) getComponent(CommandQueue.class);
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
        try {
            RegisterStatusBarResult result2 = this.mBarService.registerStatusBar(this.mCommandQueue);
            result = result2;
        } catch (RemoteException ex) {
            ex.rethrowFromSystemServer();
            result = null;
        }
        createAndAddWindows(result);
        if (this.mWallpaperSupported) {
            IntentFilter wallpaperChangedFilter = new IntentFilter("android.intent.action.WALLPAPER_CHANGED");
            this.mContext.registerReceiverAsUser(this.mWallpaperChangedReceiver, UserHandle.ALL, wallpaperChangedFilter, null, null);
            this.mWallpaperChangedReceiver.onReceive(this.mContext, null);
        }
        setUpPresenter();
        setSystemUiVisibility(this.mDisplayId, result.mSystemUiVisibility, result.mFullscreenStackSysUiVisibility, result.mDockedStackSysUiVisibility, -1, result.mFullscreenStackBounds, result.mDockedStackBounds, result.mNavbarColorManagedByIme);
        setImeWindowStatus(this.mDisplayId, result.mImeToken, result.mImeWindowVis, result.mImeBackDisposition, result.mShowImeSwitcher);
        int numIcons = result.mIcons.size();
        for (int i = 0; i < numIcons; i++) {
            this.mCommandQueue.setIcon((String) result.mIcons.keyAt(i), (StatusBarIcon) result.mIcons.valueAt(i));
        }
        IntentFilter internalFilter = new IntentFilter();
        internalFilter.addAction(BANNER_ACTION_CANCEL);
        internalFilter.addAction(BANNER_ACTION_SETUP);
        this.mContext.registerReceiver(this.mBannerActionBroadcastReceiver, internalFilter, "com.android.systemui.permission.SELF", null);
        if (this.mWallpaperSupported) {
            IWallpaperManager wallpaperManager = IWallpaperManager.Stub.asInterface(ServiceManager.getService("wallpaper"));
            try {
                wallpaperManager.setInAmbientMode(false, 0L);
            } catch (RemoteException e) {
            }
        }
        this.mIconPolicy = new PhoneStatusBarPolicy(this.mContext, this.mIconController);
        this.mSignalPolicy = new StatusBarSignalPolicy(this.mContext, this.mIconController);
        this.mUnlockMethodCache = UnlockMethodCache.getInstance(this.mContext);
        this.mUnlockMethodCache.addListener(this);
        startKeyguard();
        this.mKeyguardUpdateMonitor.registerCallback(this.mUpdateCallback);
        putComponent(DozeHost.class, this.mDozeServiceHost);
        this.mScreenPinningRequest = new ScreenPinningRequest(this.mContext);
        ((ActivityStarterDelegate) Dependency.get(ActivityStarterDelegate.class)).setActivityStarterImpl(this);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
        ((InitController) Dependency.get(InitController.class)).addPostInitTask(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$c2AOy3A7uAuedqvDvblQbirmzTM
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.updateAreThereNotifications();
            }
        });
        final int disabledFlags1 = result.mDisabledFlags1;
        final int disabledFlags2 = result.mDisabledFlags2;
        ((InitController) Dependency.get(InitController.class)).addPostInitTask(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$QO7mysP-BJLAKP36FTSzhErEZZ8
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$start$3$StatusBar(disabledFlags1, disabledFlags2);
            }
        });
    }

    protected void makeStatusBarView(RegisterStatusBarResult result) {
        Context context = this.mContext;
        updateDisplaySize();
        updateResources();
        updateTheme();
        inflateStatusBarWindow(context);
        this.mStatusBarWindow.setService(this);
        this.mStatusBarWindow.setBypassController(this.mKeyguardBypassController);
        this.mStatusBarWindow.setOnTouchListener(getStatusBarWindowTouchListener());
        this.mNotificationPanel = (NotificationPanelView) this.mStatusBarWindow.findViewById(R.id.notification_panel);
        this.mStackScroller = (ViewGroup) this.mStatusBarWindow.findViewById(R.id.notification_stack_scroller);
        this.mZenController.addCallback(this);
        NotificationListContainer notifListContainer = (NotificationListContainer) this.mStackScroller;
        this.mNotificationLogger.setUpWithContainer(notifListContainer);
        this.mNotificationIconAreaController = SystemUIFactory.getInstance().createNotificationIconAreaController(context, this, this.mWakeUpCoordinator, this.mKeyguardBypassController, this.mStatusBarStateController);
        this.mWakeUpCoordinator.setIconAreaController(this.mNotificationIconAreaController);
        inflateShelf();
        this.mNotificationIconAreaController.setupShelf(this.mNotificationShelf);
        NotificationPanelView notificationPanelView = this.mNotificationPanel;
        final NotificationIconAreaController notificationIconAreaController = this.mNotificationIconAreaController;
        Objects.requireNonNull(notificationIconAreaController);
        notificationPanelView.setOnReinflationListener(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$LyXF2jzAv77MElAagmeOMv_-4xQ
            @Override // java.lang.Runnable
            public final void run() {
                NotificationIconAreaController.this.initAodIcons();
            }
        });
        this.mNotificationPanel.addExpansionListener(this.mWakeUpCoordinator);
        ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class)).addDarkReceiver(this.mNotificationIconAreaController);
        ((PluginDependencyProvider) Dependency.get(PluginDependencyProvider.class)).allowPluginDependency(DarkIconDispatcher.class);
        ((PluginDependencyProvider) Dependency.get(PluginDependencyProvider.class)).allowPluginDependency(StatusBarStateController.class);
        FragmentHostManager.get(this.mStatusBarWindow).addTagListener(CollapsedStatusBarFragment.TAG, new FragmentHostManager.FragmentListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$dy7qc-M4vmC01_Sduz1UMseDUmo
            @Override // com.android.systemui.fragments.FragmentHostManager.FragmentListener
            public final void onFragmentViewCreated(String str, Fragment fragment) {
                StatusBar.this.lambda$makeStatusBarView$4$StatusBar(str, fragment);
            }
        }).getFragmentManager().beginTransaction().replace(R.id.status_bar_container, new CollapsedStatusBarFragment(), CollapsedStatusBarFragment.TAG).commit();
        this.mIconController = (StatusBarIconController) Dependency.get(StatusBarIconController.class);
        this.mHeadsUpManager.setUp(this.mStatusBarWindow, this.mGroupManager, this, this.mVisualStabilityManager);
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this.mHeadsUpManager);
        this.mHeadsUpManager.addListener(this);
        this.mHeadsUpManager.addListener(this.mNotificationPanel);
        this.mHeadsUpManager.addListener(this.mGroupManager);
        this.mHeadsUpManager.addListener(this.mGroupAlertTransferHelper);
        this.mHeadsUpManager.addListener(this.mVisualStabilityManager);
        this.mNotificationPanel.setHeadsUpManager(this.mHeadsUpManager);
        this.mGroupManager.setHeadsUpManager(this.mHeadsUpManager);
        this.mGroupAlertTransferHelper.setHeadsUpManager(this.mHeadsUpManager);
        this.mNotificationLogger.setHeadsUpManager(this.mHeadsUpManager);
        putComponent(HeadsUpManager.class, this.mHeadsUpManager);
        createNavigationBar(result);
        if (this.mWallpaperSupported) {
            this.mLockscreenWallpaper = new LockscreenWallpaper(this.mContext, this, this.mHandler);
        }
        this.mKeyguardIndicationController = SystemUIFactory.getInstance().createKeyguardIndicationController(this.mContext, (ViewGroup) this.mStatusBarWindow.findViewById(R.id.keyguard_indication_area), (LockIcon) this.mStatusBarWindow.findViewById(R.id.lock_icon));
        this.mNotificationPanel.setKeyguardIndicationController(this.mKeyguardIndicationController);
        this.mAmbientIndicationContainer = this.mStatusBarWindow.findViewById(R.id.ambient_indication_container);
        BatteryController batteryController = this.mBatteryController;
        if (batteryController != null) {
            batteryController.addCallback(new BatteryController.BatteryStateChangeCallback() { // from class: com.android.systemui.statusbar.phone.StatusBar.5
                @Override // com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback
                public void onPowerSaveChanged(boolean isPowerSave) {
                    StatusBar.this.mHandler.post(StatusBar.this.mCheckBarModes);
                    if (StatusBar.this.mDozeServiceHost != null) {
                        StatusBar.this.mDozeServiceHost.firePowerSaveChanged(isPowerSave);
                    }
                }

                @Override // com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback
                public void onBatteryLevelChanged(int level, boolean pluggedIn, boolean charging) {
                }
            });
        }
        this.mAutoHideController = (AutoHideController) Dependency.get(AutoHideController.class);
        this.mAutoHideController.setStatusBar(this);
        this.mLightBarController = (LightBarController) Dependency.get(LightBarController.class);
        ScrimView scrimBehind = (ScrimView) this.mStatusBarWindow.findViewById(R.id.scrim_behind);
        ScrimView scrimInFront = (ScrimView) this.mStatusBarWindow.findViewById(R.id.scrim_in_front);
        this.mScrimController = SystemUIFactory.getInstance().createScrimController(scrimBehind, scrimInFront, this.mLockscreenWallpaper, new TriConsumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$yOhqIPn374xbhtcciPbikc06Y7E
            public final void accept(Object obj, Object obj2, Object obj3) {
                StatusBar.this.lambda$makeStatusBarView$5$StatusBar((ScrimState) obj, (Float) obj2, (ColorExtractor.GradientColors) obj3);
            }
        }, new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$D6uZssDIjl3zb9PActa_b2Y0wNo
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                StatusBar.this.lambda$makeStatusBarView$6$StatusBar((Integer) obj);
            }
        }, DozeParameters.getInstance(this.mContext), (AlarmManager) this.mContext.getSystemService(AlarmManager.class), this.mKeyguardMonitor);
        this.mNotificationPanel.initDependencies(this, this.mGroupManager, this.mNotificationShelf, this.mHeadsUpManager, this.mNotificationIconAreaController, this.mScrimController);
        this.mDozeScrimController = new DozeScrimController(DozeParameters.getInstance(context));
        BackDropView backdrop = (BackDropView) this.mStatusBarWindow.findViewById(R.id.backdrop);
        this.mMediaManager.setup(backdrop, (ImageView) backdrop.findViewById(R.id.backdrop_front), (ImageView) backdrop.findViewById(R.id.backdrop_back), this.mScrimController, this.mLockscreenWallpaper);
        this.mVolumeComponent = (VolumeComponent) getComponent(VolumeComponent.class);
        this.mNotificationPanel.setUserSetupComplete(this.mUserSetup);
        if (UserManager.get(this.mContext).isUserSwitcherEnabled()) {
            createUserSwitcher();
        }
        NotificationPanelView notificationPanelView2 = this.mNotificationPanel;
        final StatusBarWindowView statusBarWindowView = this.mStatusBarWindow;
        Objects.requireNonNull(statusBarWindowView);
        notificationPanelView2.setLaunchAffordanceListener(new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$HshNPAFauaSwgr5N8iT9CKLXoqs
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                StatusBarWindowView.this.onShowingLaunchAffordanceChanged(((Boolean) obj).booleanValue());
            }
        });
        setUpQuickSettingsTilePanel();
        this.mReportRejectedTouch = this.mStatusBarWindow.findViewById(R.id.report_rejected_touch);
        if (this.mReportRejectedTouch != null) {
            updateReportRejectedTouchVisibility();
            this.mReportRejectedTouch.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$XQOpWl97Dmi1_PDOREwJc80t2Z4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    StatusBar.this.lambda$makeStatusBarView$7$StatusBar(view);
                }
            });
        }
        PowerManager pm = (PowerManager) this.mContext.getSystemService("power");
        if (!pm.isScreenOn()) {
            this.mBroadcastReceiver.onReceive(this.mContext, new Intent("android.intent.action.SCREEN_OFF"));
        }
        this.mGestureWakeLock = pm.newWakeLock(10, "GestureWakeLock");
        this.mVibrator = (Vibrator) this.mContext.getSystemService(Vibrator.class);
        int[] pattern = this.mContext.getResources().getIntArray(R.array.config_cameraLaunchGestureVibePattern);
        this.mCameraLaunchGestureVibePattern = new long[pattern.length];
        for (int i = 0; i < pattern.length; i++) {
            this.mCameraLaunchGestureVibePattern[i] = pattern[i];
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.app.action.SHOW_DEVICE_MONITORING_DIALOG");
        context.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, filter, null, null);
        IntentFilter demoFilter = new IntentFilter();
        demoFilter.addAction(DemoMode.ACTION_DEMO);
        context.registerReceiverAsUser(this.mDemoReceiver, UserHandle.ALL, demoFilter, "android.permission.DUMP", null);
        this.mDeviceProvisionedController.addCallback(this.mUserSetupObserver);
        this.mUserSetupObserver.onUserSetupChanged();
        ThreadedRenderer.overrideProperty("disableProfileBars", OOBEEvent.STRING_TRUE);
        ThreadedRenderer.overrideProperty("ambientRatio", String.valueOf(1.5f));
    }

    public /* synthetic */ void lambda$makeStatusBarView$4$StatusBar(String tag, Fragment fragment) {
        CollapsedStatusBarFragment statusBarFragment = (CollapsedStatusBarFragment) fragment;
        statusBarFragment.initNotificationIconArea(this.mNotificationIconAreaController);
        PhoneStatusBarView oldStatusBarView = this.mStatusBarView;
        this.mStatusBarView = (PhoneStatusBarView) fragment.getView();
        this.mStatusBarView.setBar(this);
        this.mStatusBarView.setPanel(this.mNotificationPanel);
        this.mStatusBarView.setScrimController(this.mScrimController);
        if (this.mHeadsUpManager.hasPinnedHeadsUp()) {
            this.mNotificationPanel.notifyBarPanelExpansionChanged();
        }
        this.mStatusBarView.setBouncerShowing(this.mBouncerShowing);
        if (oldStatusBarView != null) {
            float fraction = oldStatusBarView.getExpansionFraction();
            boolean expanded = oldStatusBarView.isExpanded();
            this.mStatusBarView.panelExpansionChanged(fraction, expanded);
        }
        HeadsUpAppearanceController oldController = this.mHeadsUpAppearanceController;
        HeadsUpAppearanceController headsUpAppearanceController = this.mHeadsUpAppearanceController;
        if (headsUpAppearanceController != null) {
            headsUpAppearanceController.destroy();
        }
        this.mHeadsUpAppearanceController = new HeadsUpAppearanceController(this.mNotificationIconAreaController, this.mHeadsUpManager, this.mStatusBarWindow, this.mStatusBarStateController, this.mKeyguardBypassController, this.mWakeUpCoordinator);
        this.mHeadsUpAppearanceController.readFrom(oldController);
        this.mStatusBarWindow.setStatusBarView(this.mStatusBarView);
        updateAreThereNotifications();
        checkBarModes();
    }

    public /* synthetic */ void lambda$makeStatusBarView$5$StatusBar(ScrimState state, Float alpha, ColorExtractor.GradientColors color) {
        this.mLightBarController.setScrimState(state, alpha.floatValue(), color);
    }

    public /* synthetic */ void lambda$makeStatusBarView$6$StatusBar(Integer scrimsVisible) {
        StatusBarWindowController statusBarWindowController = this.mStatusBarWindowController;
        if (statusBarWindowController != null) {
            statusBarWindowController.setScrimsVisibility(scrimsVisible.intValue());
        }
        StatusBarWindowView statusBarWindowView = this.mStatusBarWindow;
        if (statusBarWindowView != null) {
            statusBarWindowView.onScrimVisibilityChanged(scrimsVisible.intValue());
        }
    }

    public /* synthetic */ void lambda$makeStatusBarView$7$StatusBar(View v) {
        Uri session = this.mFalsingManager.reportRejectedTouch();
        if (session == null) {
            return;
        }
        StringWriter message = new StringWriter();
        message.write("Build info: ");
        message.write(SystemProperties.get("ro.build.description"));
        message.write("\nSerial number: ");
        message.write(SystemProperties.get("ro.serialno"));
        message.write("\n");
        PrintWriter falsingPw = new PrintWriter(message);
        FalsingLog.dump(falsingPw);
        falsingPw.flush();
        startActivityDismissingKeyguard(Intent.createChooser(new Intent("android.intent.action.SEND").setType("*/*").putExtra("android.intent.extra.SUBJECT", "Rejected touch report").putExtra("android.intent.extra.STREAM", session).putExtra("android.intent.extra.TEXT", message.toString()), "Share rejected touch report").addFlags(268435456), true, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public QS createDefaultQSFragment() {
        return (QS) FragmentHostManager.get(this.mStatusBarWindow).create(QSFragment.class);
    }

    private void setUpPresenter() {
        this.mActivityLaunchAnimator = new ActivityLaunchAnimator(this.mStatusBarWindow, this, this.mNotificationPanel, (NotificationListContainer) this.mStackScroller);
        NotificationRowBinderImpl rowBinder = new NotificationRowBinderImpl(this.mContext, this.mAllowNotificationLongPress, this.mKeyguardBypassController, this.mStatusBarStateController);
        this.mPresenter = new StatusBarNotificationPresenter(this.mContext, this.mNotificationPanel, this.mHeadsUpManager, this.mStatusBarWindow, this.mStackScroller, this.mDozeScrimController, this.mScrimController, this.mActivityLaunchAnimator, this.mDynamicPrivacyController, this.mNotificationAlertingManager, rowBinder);
        this.mNotificationListController = new NotificationListController(this.mEntryManager, (NotificationListContainer) this.mStackScroller, this.mForegroundServiceController, this.mDeviceProvisionedController);
        AppOpsController appOpsController = this.mAppOpsController;
        if (appOpsController != null) {
            appOpsController.addCallback(APP_OPS, this);
        }
        this.mNotificationShelf.setOnActivatedListener(this.mPresenter);
        this.mRemoteInputManager.getController().addCallback(this.mStatusBarWindowController);
        StatusBarRemoteInputCallback mStatusBarRemoteInputCallback = (StatusBarRemoteInputCallback) Dependency.get(NotificationRemoteInputManager.Callback.class);
        this.mShadeController = (ShadeController) Dependency.get(ShadeController.class);
        ActivityStarter activityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);
        this.mNotificationActivityStarter = new StatusBarNotificationActivityStarter(this.mContext, this.mCommandQueue, this.mAssistManager, this.mNotificationPanel, this.mPresenter, this.mEntryManager, this.mHeadsUpManager, activityStarter, this.mActivityLaunchAnimator, this.mBarService, this.mStatusBarStateController, this.mKeyguardManager, this.mDreamManager, this.mRemoteInputManager, mStatusBarRemoteInputCallback, this.mGroupManager, this.mLockscreenUserManager, this.mShadeController, this.mKeyguardMonitor, this.mNotificationInterruptionStateProvider, this.mMetricsLogger, new LockPatternUtils(this.mContext), (Handler) Dependency.get(Dependency.MAIN_HANDLER), (Handler) Dependency.get(Dependency.BG_HANDLER), this.mActivityIntentHelper, this.mBubbleController);
        this.mGutsManager.setNotificationActivityStarter(this.mNotificationActivityStarter);
        this.mEntryManager.setRowBinder(rowBinder);
        rowBinder.setNotificationClicker(new NotificationClicker(this, (BubbleController) Dependency.get(BubbleController.class), this.mNotificationActivityStarter));
        this.mGroupAlertTransferHelper.bind(this.mEntryManager, this.mGroupManager);
        this.mNotificationListController.bind();
    }

    protected void getDependencies() {
        this.mIconController = (StatusBarIconController) Dependency.get(StatusBarIconController.class);
        this.mLightBarController = (LightBarController) Dependency.get(LightBarController.class);
        this.mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
        this.mScreenLifecycle = (ScreenLifecycle) Dependency.get(ScreenLifecycle.class);
        this.mWakefulnessLifecycle = (WakefulnessLifecycle) Dependency.get(WakefulnessLifecycle.class);
        this.mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
        this.mForegroundServiceController = (ForegroundServiceController) Dependency.get(ForegroundServiceController.class);
        this.mGroupAlertTransferHelper = (NotificationGroupAlertTransferHelper) Dependency.get(NotificationGroupAlertTransferHelper.class);
        this.mGroupManager = (NotificationGroupManager) Dependency.get(NotificationGroupManager.class);
        this.mGutsManager = (NotificationGutsManager) Dependency.get(NotificationGutsManager.class);
        this.mLockscreenUserManager = (NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class);
        this.mMediaManager = (NotificationMediaManager) Dependency.get(NotificationMediaManager.class);
        this.mNotificationInterruptionStateProvider = (NotificationInterruptionStateProvider) Dependency.get(NotificationInterruptionStateProvider.class);
        this.mNotificationListener = (NotificationListener) Dependency.get(NotificationListener.class);
        this.mNotificationLogger = (NotificationLogger) Dependency.get(NotificationLogger.class);
        this.mRemoteInputManager = (NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class);
        this.mViewHierarchyManager = (NotificationViewHierarchyManager) Dependency.get(NotificationViewHierarchyManager.class);
        this.mVisualStabilityManager = (VisualStabilityManager) Dependency.get(VisualStabilityManager.class);
        this.mBatteryController = (BatteryController) Dependency.get(BatteryController.class);
        this.mNetworkController = (NetworkController) Dependency.get(NetworkController.class);
        this.mZenController = (ZenModeController) Dependency.get(ZenModeController.class);
        this.mAppOpsController = (AppOpsController) Dependency.get(AppOpsController.class);
        this.mAssistManager = (AssistManager) Dependency.get(AssistManager.class);
        this.mBubbleController = (BubbleController) Dependency.get(BubbleController.class);
        this.mColorExtractor = (SysuiColorExtractor) Dependency.get(SysuiColorExtractor.class);
        this.mNavigationBarController = (NavigationBarController) Dependency.get(NavigationBarController.class);
        this.mUserSwitcherController = (UserSwitcherController) Dependency.get(UserSwitcherController.class);
        this.mVibratorHelper = (VibratorHelper) Dependency.get(VibratorHelper.class);
    }

    protected void setUpQuickSettingsTilePanel() {
        View container = this.mStatusBarWindow.findViewById(R.id.qs_frame);
        if (container != null) {
            FragmentHostManager fragmentHostManager = FragmentHostManager.get(container);
            ExtensionFragmentListener.attachExtensonToFragment(container, QS.TAG, R.id.qs_frame, ((ExtensionController) Dependency.get(ExtensionController.class)).newExtension(QS.class).withPlugin(QS.class).withDefault(new Supplier() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$Zqmz5npIKuMPJHZWVxICwxzCPwk
                @Override // java.util.function.Supplier
                public final Object get() {
                    return StatusBar.this.createDefaultQSFragment();
                }
            }).build());
            this.mBrightnessMirrorController = new BrightnessMirrorController(this.mStatusBarWindow, new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$I-Ke2gaYgNrG7C2lLDNDpUI7nes
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    StatusBar.this.lambda$setUpQuickSettingsTilePanel$8$StatusBar((Boolean) obj);
                }
            });
            fragmentHostManager.addTagListener(QS.TAG, new FragmentHostManager.FragmentListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$e4ONl8wj6wM_4EgNJAT409WKbVc
                @Override // com.android.systemui.fragments.FragmentHostManager.FragmentListener
                public final void onFragmentViewCreated(String str, Fragment fragment) {
                    StatusBar.this.lambda$setUpQuickSettingsTilePanel$9$StatusBar(str, fragment);
                }
            });
        }
    }

    public /* synthetic */ void lambda$setUpQuickSettingsTilePanel$8$StatusBar(Boolean visible) {
        this.mBrightnessMirrorVisible = visible.booleanValue();
        updateScrimController();
    }

    public /* synthetic */ void lambda$setUpQuickSettingsTilePanel$9$StatusBar(String tag, Fragment f) {
        QS qs = (QS) f;
        if (qs instanceof QSFragment) {
            this.mQSPanel = ((QSFragment) qs).getQsPanel();
            this.mQSPanel.setBrightnessMirror(this.mBrightnessMirrorController);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: setUpDisableFlags */
    public void lambda$start$3$StatusBar(int state1, int state2) {
        this.mCommandQueue.disable(this.mDisplayId, state1, state2, false);
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void addAfterKeyguardGoneRunnable(Runnable runnable) {
        this.mStatusBarKeyguardViewManager.addAfterKeyguardGoneRunnable(runnable);
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public boolean isDozing() {
        return this.mDozing;
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void wakeUpIfDozing(long time, View where, String why) {
        if (this.mDozing) {
            PowerManager pm = (PowerManager) this.mContext.getSystemService(PowerManager.class);
            pm.wakeUp(time, 4, "com.android.systemui:" + why);
            this.mWakeUpComingFromTouch = true;
            where.getLocationInWindow(this.mTmpInt2);
            this.mWakeUpTouchLocation = new PointF((float) (this.mTmpInt2[0] + (where.getWidth() / 2)), (float) (this.mTmpInt2[1] + (where.getHeight() / 2)));
            this.mFalsingManager.onScreenOnFromTouch();
        }
    }

    protected void createNavigationBar(RegisterStatusBarResult result) {
        this.mNavigationBarController.createNavigationBars(true, result);
    }

    protected View.OnTouchListener getStatusBarWindowTouchListener() {
        return new View.OnTouchListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$n71p2lA3I37oyoKRz8xFfo1UnRo
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return StatusBar.this.lambda$getStatusBarWindowTouchListener$10$StatusBar(view, motionEvent);
            }
        };
    }

    public /* synthetic */ boolean lambda$getStatusBarWindowTouchListener$10$StatusBar(View v, MotionEvent event) {
        this.mAutoHideController.checkUserAutoHide(event);
        this.mRemoteInputManager.checkRemoteInputOutside(event);
        if (event.getAction() == 0 && this.mExpandedVisible) {
            animateCollapsePanels();
        }
        return this.mStatusBarWindow.onTouchEvent(event);
    }

    private void inflateShelf() {
        this.mNotificationShelf = (NotificationShelf) this.mInjectionInflater.injectable(LayoutInflater.from(this.mContext)).inflate(R.layout.status_bar_notification_shelf, this.mStackScroller, false);
        this.mNotificationShelf.setOnClickListener(this.mGoToLockedShadeListener);
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.onDensityOrFontScaleChanged();
        }
        ((UserInfoControllerImpl) Dependency.get(UserInfoController.class)).onDensityOrFontScaleChanged();
        ((UserSwitcherController) Dependency.get(UserSwitcherController.class)).onDensityOrFontScaleChanged();
        KeyguardUserSwitcher keyguardUserSwitcher = this.mKeyguardUserSwitcher;
        if (keyguardUserSwitcher != null) {
            keyguardUserSwitcher.onDensityOrFontScaleChanged();
        }
        this.mNotificationIconAreaController.onDensityOrFontScaleChanged(this.mContext);
        this.mHeadsUpManager.onDensityOrFontScaleChanged();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onThemeChanged() {
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (statusBarKeyguardViewManager != null) {
            statusBarKeyguardViewManager.onThemeChanged();
        }
        View view = this.mAmbientIndicationContainer;
        if (view instanceof AutoReinflateContainer) {
            ((AutoReinflateContainer) view).inflateLayout();
        }
        this.mNotificationIconAreaController.onThemeChanged();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onOverlayChanged() {
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.onOverlayChanged();
        }
        this.mNotificationPanel.onThemeChanged();
        onThemeChanged();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onUiModeChanged() {
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.onUiModeChanged();
        }
    }

    protected void createUserSwitcher() {
        this.mKeyguardUserSwitcher = new KeyguardUserSwitcher(this.mContext, (ViewStub) this.mStatusBarWindow.findViewById(R.id.keyguard_user_switcher), (KeyguardStatusBarView) this.mStatusBarWindow.findViewById(R.id.keyguard_header), this.mNotificationPanel);
    }

    protected void inflateStatusBarWindow(Context context) {
        this.mStatusBarWindow = (StatusBarWindowView) this.mInjectionInflater.injectable(LayoutInflater.from(context)).inflate(R.layout.super_status_bar, (ViewGroup) null);
    }

    protected void startKeyguard() {
        Trace.beginSection("StatusBar#startKeyguard");
        KeyguardViewMediator keyguardViewMediator = (KeyguardViewMediator) getComponent(KeyguardViewMediator.class);
        this.mBiometricUnlockController = new BiometricUnlockController(this.mContext, this.mDozeScrimController, keyguardViewMediator, this.mScrimController, this, UnlockMethodCache.getInstance(this.mContext), new Handler(), this.mKeyguardUpdateMonitor, this.mKeyguardBypassController);
        putComponent(BiometricUnlockController.class, this.mBiometricUnlockController);
        this.mStatusBarKeyguardViewManager = keyguardViewMediator.registerStatusBar(this, getBouncerContainer(), this.mNotificationPanel, this.mBiometricUnlockController, (ViewGroup) this.mStatusBarWindow.findViewById(R.id.lock_icon_container), this.mStackScroller, this.mKeyguardBypassController, this.mFalsingManager);
        this.mKeyguardIndicationController.setStatusBarKeyguardViewManager(this.mStatusBarKeyguardViewManager);
        this.mBiometricUnlockController.setStatusBarKeyguardViewManager(this.mStatusBarKeyguardViewManager);
        this.mRemoteInputManager.getController().addCallback(this.mStatusBarKeyguardViewManager);
        this.mDynamicPrivacyController.setStatusBarKeyguardViewManager(this.mStatusBarKeyguardViewManager);
        this.mKeyguardViewMediatorCallback = keyguardViewMediator.getViewMediatorCallback();
        this.mLightBarController.setBiometricUnlockController(this.mBiometricUnlockController);
        this.mMediaManager.setBiometricUnlockController(this.mBiometricUnlockController);
        ((KeyguardDismissUtil) Dependency.get(KeyguardDismissUtil.class)).setDismissHandler(new KeyguardDismissHandler() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$SBKxeejWdiPVIq--MxMI8pU8ipA
            @Override // com.android.systemui.statusbar.phone.KeyguardDismissHandler
            public final void executeWhenUnlocked(ActivityStarter.OnDismissAction onDismissAction, boolean z) {
                StatusBar.this.executeWhenUnlocked(onDismissAction, z);
            }
        });
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View getStatusBarView() {
        return this.mStatusBarView;
    }

    public StatusBarWindowView getStatusBarWindow() {
        return this.mStatusBarWindow;
    }

    protected ViewGroup getBouncerContainer() {
        return this.mStatusBarWindow;
    }

    public int getStatusBarHeight() {
        if (this.mNaturalBarHeight < 0) {
            Resources res = this.mContext.getResources();
            this.mNaturalBarHeight = res.getDimensionPixelSize(17105438);
        }
        return this.mNaturalBarHeight;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean toggleSplitScreenMode(int metricsDockAction, int metricsUndockAction) {
        if (this.mRecents == null) {
            return false;
        }
        int dockSide = WindowManagerProxy.getInstance().getDockSide();
        if (dockSide == -1) {
            int navbarPos = WindowManagerWrapper.getInstance().getNavBarPosition(this.mDisplayId);
            if (navbarPos == -1) {
                return false;
            }
            int createMode = navbarPos == 1 ? 1 : 0;
            return this.mRecents.splitPrimaryTask(createMode, null, metricsDockAction);
        }
        Divider divider = (Divider) getComponent(Divider.class);
        if (divider != null) {
            if (divider.isMinimized() && !divider.isHomeStackResizable()) {
                return false;
            }
            divider.onUndockingTask();
            if (metricsUndockAction != -1) {
                this.mMetricsLogger.action(metricsUndockAction);
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0026, code lost:
        if (com.android.systemui.statusbar.phone.StatusBar.ONLY_CORE_APPS == false) goto L17;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateQsExpansionEnabled() {
        /*
            r3 = this;
            com.android.systemui.statusbar.policy.DeviceProvisionedController r0 = r3.mDeviceProvisionedController
            boolean r0 = r0.isDeviceProvisioned()
            r1 = 1
            if (r0 == 0) goto L29
            boolean r0 = r3.mUserSetup
            if (r0 != 0) goto L17
            com.android.systemui.statusbar.policy.UserSwitcherController r0 = r3.mUserSwitcherController
            if (r0 == 0) goto L17
            boolean r0 = r0.isSimpleUserSwitcher()
            if (r0 != 0) goto L29
        L17:
            int r0 = r3.mDisabled2
            r2 = r0 & 4
            if (r2 != 0) goto L29
            r0 = r0 & r1
            if (r0 != 0) goto L29
            boolean r0 = r3.mDozing
            if (r0 != 0) goto L29
            boolean r0 = com.android.systemui.statusbar.phone.StatusBar.ONLY_CORE_APPS
            if (r0 != 0) goto L29
            goto L2a
        L29:
            r1 = 0
        L2a:
            r0 = r1
            com.android.systemui.statusbar.phone.NotificationPanelView r1 = r3.mNotificationPanel
            r1.setQsExpansionEnabled(r0)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "updateQsExpansionEnabled - QS Expand enabled: "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "StatusBar"
            android.util.Log.d(r2, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.StatusBar.updateQsExpansionEnabled():void");
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void addQsTile(ComponentName tile) {
        QSPanel qSPanel = this.mQSPanel;
        if (qSPanel != null && qSPanel.getHost() != null) {
            this.mQSPanel.getHost().addTile(tile);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void remQsTile(ComponentName tile) {
        QSPanel qSPanel = this.mQSPanel;
        if (qSPanel != null && qSPanel.getHost() != null) {
            this.mQSPanel.getHost().removeTile(tile);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void clickTile(ComponentName tile) {
        this.mQSPanel.clickTile(tile);
    }

    public boolean areNotificationsHidden() {
        return this.mZenController.areNotificationsHiddenInShade();
    }

    public void requestNotificationUpdate() {
        this.mEntryManager.updateNotifications();
    }

    public void requestFaceAuth() {
        if (!this.mUnlockMethodCache.canSkipBouncer()) {
            this.mKeyguardUpdateMonitor.requestFaceAuth();
        }
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void updateAreThereNotifications() {
        PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
        if (phoneStatusBarView != null) {
            final View nlo = phoneStatusBarView.findViewById(R.id.notification_lights_out);
            boolean showDot = hasActiveNotifications() && !areLightsOn();
            if (showDot != (nlo.getAlpha() == 1.0f)) {
                if (showDot) {
                    nlo.setAlpha(0.0f);
                    nlo.setVisibility(0);
                }
                nlo.animate().alpha(showDot ? 1.0f : 0.0f).setDuration(showDot ? 750L : 250L).setInterpolator(new AccelerateInterpolator(2.0f)).setListener(showDot ? null : new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.StatusBar.6
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator _a) {
                        nlo.setVisibility(8);
                    }
                }).start();
            }
        }
        this.mMediaManager.findAndUpdateMediaNotifications();
    }

    private void updateReportRejectedTouchVisibility() {
        View view = this.mReportRejectedTouch;
        if (view == null) {
            return;
        }
        view.setVisibility((this.mState == 1 && !this.mDozing && this.mFalsingManager.isReportingEnabled()) ? 0 : 4);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void disable(int displayId, int state1, int state2, boolean animate) {
        if (displayId != this.mDisplayId) {
            return;
        }
        int state22 = this.mRemoteInputQuickSettingsDisabler.adjustDisableFlags(state2);
        boolean z = animate & (this.mStatusBarWindowState != 2);
        int old1 = this.mDisabled1;
        int diff1 = state1 ^ old1;
        this.mDisabled1 = state1;
        int old2 = this.mDisabled2;
        int diff2 = state22 ^ old2;
        this.mDisabled2 = state22;
        StringBuilder flagdbg = new StringBuilder();
        flagdbg.append("disable<");
        flagdbg.append((state1 & 65536) != 0 ? 'E' : 'e');
        flagdbg.append((diff1 & 65536) != 0 ? '!' : ' ');
        flagdbg.append((state1 & 131072) != 0 ? 'I' : 'i');
        flagdbg.append((diff1 & 131072) != 0 ? '!' : ' ');
        flagdbg.append((state1 & 262144) != 0 ? 'A' : 'a');
        flagdbg.append((diff1 & 262144) != 0 ? '!' : ' ');
        flagdbg.append((state1 & 1048576) != 0 ? 'S' : 's');
        flagdbg.append((diff1 & 1048576) != 0 ? '!' : ' ');
        flagdbg.append((4194304 & state1) != 0 ? 'B' : 'b');
        flagdbg.append((4194304 & diff1) != 0 ? '!' : ' ');
        flagdbg.append((2097152 & state1) != 0 ? 'H' : 'h');
        flagdbg.append((2097152 & diff1) != 0 ? '!' : ' ');
        flagdbg.append((state1 & 16777216) != 0 ? 'R' : 'r');
        flagdbg.append((diff1 & 16777216) != 0 ? '!' : ' ');
        flagdbg.append((8388608 & state1) != 0 ? 'C' : 'c');
        flagdbg.append((8388608 & diff1) != 0 ? '!' : ' ');
        flagdbg.append((33554432 & state1) != 0 ? 'S' : 's');
        flagdbg.append((33554432 & diff1) != 0 ? '!' : ' ');
        flagdbg.append("> disable2<");
        flagdbg.append((state22 & 1) != 0 ? 'Q' : 'q');
        flagdbg.append((diff2 & 1) != 0 ? '!' : ' ');
        flagdbg.append((state22 & 2) != 0 ? 'I' : 'i');
        flagdbg.append((diff2 & 2) != 0 ? '!' : ' ');
        flagdbg.append((state22 & 4) != 0 ? 'N' : 'n');
        flagdbg.append((diff2 & 4) == 0 ? ' ' : '!');
        flagdbg.append(Typography.greater);
        Log.d(TAG, flagdbg.toString());
        if ((diff1 & 65536) != 0 && (65536 & state1) != 0) {
            animateCollapsePanels();
        }
        if ((diff1 & 16777216) != 0 && (16777216 & state1) != 0) {
            this.mHandler.removeMessages(1020);
            this.mHandler.sendEmptyMessage(1020);
        }
        if ((diff1 & 262144) != 0) {
            this.mNotificationInterruptionStateProvider.setDisableNotificationAlerts((262144 & state1) != 0);
        }
        if ((diff2 & 1) != 0) {
            updateQsExpansionEnabled();
        }
        if ((diff2 & 4) != 0) {
            updateQsExpansionEnabled();
            if ((state1 & 4) != 0) {
                animateCollapsePanels();
            }
        }
    }

    protected H createHandler() {
        return new H();
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startActivity(Intent intent, boolean onlyProvisioned, boolean dismissShade, int flags) {
        startActivityDismissingKeyguard(intent, onlyProvisioned, dismissShade, flags);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startActivity(Intent intent, boolean dismissShade) {
        startActivityDismissingKeyguard(intent, false, dismissShade);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startActivity(Intent intent, boolean onlyProvisioned, boolean dismissShade) {
        startActivityDismissingKeyguard(intent, onlyProvisioned, dismissShade);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startActivity(Intent intent, boolean dismissShade, ActivityStarter.Callback callback) {
        startActivityDismissingKeyguard(intent, false, dismissShade, false, callback, 0);
    }

    public void setQsExpanded(boolean expanded) {
        int i;
        this.mStatusBarWindowController.setQsExpanded(expanded);
        NotificationPanelView notificationPanelView = this.mNotificationPanel;
        if (expanded) {
            i = 4;
        } else {
            i = 0;
        }
        notificationPanelView.setStatusAccessibilityImportance(i);
        if (getNavigationBarView() != null) {
            getNavigationBarView().onStatusBarPanelStateChanged();
        }
    }

    public boolean isWakeUpComingFromTouch() {
        return this.mWakeUpComingFromTouch;
    }

    public boolean isFalsingThresholdNeeded() {
        return this.mStatusBarStateController.getState() == 1;
    }

    public void onKeyguardViewManagerStatesUpdated() {
        logStateToEventlog();
    }

    @Override // com.android.systemui.statusbar.phone.UnlockMethodCache.OnUnlockMethodChangedListener
    public void onUnlockMethodStateChanged() {
        updateKeyguardState();
        logStateToEventlog();
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpPinnedModeChanged(boolean inPinnedMode) {
        if (inPinnedMode) {
            this.mStatusBarWindowController.setHeadsUpShowing(true);
            this.mStatusBarWindowController.setForceStatusBarVisible(true);
            if (this.mNotificationPanel.isFullyCollapsed()) {
                this.mNotificationPanel.requestLayout();
                this.mStatusBarWindowController.setForceWindowCollapsed(true);
                this.mNotificationPanel.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$a1PwGueSv8bkjX5GxiVzM2PDffE
                    @Override // java.lang.Runnable
                    public final void run() {
                        StatusBar.this.lambda$onHeadsUpPinnedModeChanged$11$StatusBar();
                    }
                });
                return;
            }
            return;
        }
        boolean bypassKeyguard = this.mKeyguardBypassController.getBypassEnabled() && this.mState == 1;
        if (this.mNotificationPanel.isFullyCollapsed() && !this.mNotificationPanel.isTracking() && !bypassKeyguard) {
            this.mHeadsUpManager.setHeadsUpGoingAway(true);
            this.mNotificationPanel.runAfterAnimationFinished(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$vQbe7Nr2PT8-R2UTHbkZ0b3R-4w
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBar.this.lambda$onHeadsUpPinnedModeChanged$12$StatusBar();
                }
            });
            return;
        }
        this.mStatusBarWindowController.setHeadsUpShowing(false);
        if (bypassKeyguard) {
            this.mStatusBarWindowController.setForceStatusBarVisible(false);
        }
    }

    public /* synthetic */ void lambda$onHeadsUpPinnedModeChanged$11$StatusBar() {
        this.mStatusBarWindowController.setForceWindowCollapsed(false);
    }

    public /* synthetic */ void lambda$onHeadsUpPinnedModeChanged$12$StatusBar() {
        if (!this.mHeadsUpManager.hasPinnedHeadsUp()) {
            this.mStatusBarWindowController.setHeadsUpShowing(false);
            this.mHeadsUpManager.setHeadsUpGoingAway(false);
        }
        this.mRemoteInputManager.onPanelCollapsed();
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpStateChanged(NotificationEntry entry, boolean isHeadsUp) {
        this.mEntryManager.updateNotifications();
        if (isDozing() && isHeadsUp) {
            entry.setPulseSuppressed(false);
            this.mDozeServiceHost.fireNotificationPulse(entry);
            if (this.mPulsing) {
                this.mDozeScrimController.cancelPendingPulseTimeout();
            }
        }
        if (!isHeadsUp && !this.mHeadsUpManager.hasNotifications()) {
            this.mDozeScrimController.pulseOutNow();
        }
    }

    public boolean isKeyguardCurrentlySecure() {
        return !this.mUnlockMethodCache.canSkipBouncer();
    }

    public void setPanelExpanded(boolean isExpanded) {
        this.mPanelExpanded = isExpanded;
        updateHideIconsForBouncer(false);
        this.mStatusBarWindowController.setPanelExpanded(isExpanded);
        this.mVisualStabilityManager.setPanelExpanded(isExpanded);
        if (isExpanded && this.mStatusBarStateController.getState() != 1) {
            clearNotificationEffects();
        }
        if (!isExpanded) {
            this.mRemoteInputManager.onPanelCollapsed();
        }
    }

    public ViewGroup getNotificationScrollLayout() {
        return this.mStackScroller;
    }

    public boolean isPulsing() {
        return this.mPulsing;
    }

    public boolean hideStatusBarIconsWhenExpanded() {
        return this.mNotificationPanel.hideStatusBarIconsWhenExpanded();
    }

    public void onColorsChanged(ColorExtractor extractor, int which) {
        updateTheme();
    }

    public View getAmbientIndicationContainer() {
        return this.mAmbientIndicationContainer;
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public boolean isOccluded() {
        return this.mIsOccluded;
    }

    public void setOccluded(boolean occluded) {
        this.mIsOccluded = occluded;
        this.mScrimController.setKeyguardOccluded(occluded);
        updateHideIconsForBouncer(false);
    }

    public boolean hideStatusBarIconsForBouncer() {
        return this.mHideIconsForBouncer || this.mWereIconsJustHidden;
    }

    private void updateHideIconsForBouncer(boolean animate) {
        boolean shouldHideIconsForBouncer = false;
        boolean hideBecauseApp = this.mTopHidesStatusBar && this.mIsOccluded && (this.mStatusBarWindowHidden || this.mBouncerShowing);
        boolean hideBecauseKeyguard = (this.mPanelExpanded || this.mIsOccluded || !this.mBouncerShowing) ? false : true;
        if (hideBecauseApp || hideBecauseKeyguard) {
            shouldHideIconsForBouncer = true;
        }
        if (this.mHideIconsForBouncer != shouldHideIconsForBouncer) {
            this.mHideIconsForBouncer = shouldHideIconsForBouncer;
            if (!shouldHideIconsForBouncer && this.mBouncerWasShowingWhenHidden) {
                this.mWereIconsJustHidden = true;
                this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$a1IsrkRZhqgkId0jst0xYX6PoT4
                    @Override // java.lang.Runnable
                    public final void run() {
                        StatusBar.this.lambda$updateHideIconsForBouncer$13$StatusBar();
                    }
                }, 500L);
            } else {
                this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, animate);
            }
        }
        if (shouldHideIconsForBouncer) {
            this.mBouncerWasShowingWhenHidden = this.mBouncerShowing;
        }
    }

    public /* synthetic */ void lambda$updateHideIconsForBouncer$13$StatusBar() {
        this.mWereIconsJustHidden = false;
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
    }

    public boolean headsUpShouldBeVisible() {
        return this.mHeadsUpAppearanceController.shouldBeVisible();
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController, com.android.systemui.statusbar.notification.ActivityLaunchAnimator.Callback
    public void onLaunchAnimationCancelled() {
        if (!this.mPresenter.isCollapsing()) {
            onClosingFinished();
        }
    }

    @Override // com.android.systemui.statusbar.notification.ActivityLaunchAnimator.Callback
    public void onExpandAnimationFinished(boolean launchIsFullScreen) {
        if (!this.mPresenter.isCollapsing()) {
            onClosingFinished();
        }
        if (launchIsFullScreen) {
            instantCollapseNotificationPanel();
        }
    }

    @Override // com.android.systemui.statusbar.notification.ActivityLaunchAnimator.Callback
    public void onExpandAnimationTimedOut() {
        ActivityLaunchAnimator activityLaunchAnimator;
        if (this.mPresenter.isPresenterFullyCollapsed() && !this.mPresenter.isCollapsing() && (activityLaunchAnimator = this.mActivityLaunchAnimator) != null && !activityLaunchAnimator.isLaunchForActivity()) {
            onClosingFinished();
        } else {
            collapsePanel(true);
        }
    }

    @Override // com.android.systemui.statusbar.notification.ActivityLaunchAnimator.Callback
    public boolean areLaunchAnimationsEnabled() {
        return this.mState == 0;
    }

    public boolean isDeviceInVrMode() {
        return this.mPresenter.isDeviceInVrMode();
    }

    public NotificationPresenter getPresenter() {
        return this.mPresenter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class H extends Handler {
        protected H() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message m) {
            int i = m.what;
            if (i == 1026) {
                StatusBar.this.toggleKeyboardShortcuts(m.arg1);
            } else if (i == StatusBar.MSG_DISMISS_KEYBOARD_SHORTCUTS_MENU) {
                StatusBar.this.dismissKeyboardShortcuts();
            } else {
                switch (i) {
                    case 1000:
                        StatusBar.this.animateExpandNotificationsPanel();
                        return;
                    case 1001:
                        StatusBar.this.animateCollapsePanels();
                        return;
                    case 1002:
                        StatusBar.this.animateExpandSettingsPanel((String) m.obj);
                        return;
                    case 1003:
                        StatusBar.this.onLaunchTransitionTimeout();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public void maybeEscalateHeadsUp() {
        this.mHeadsUpManager.getAllEntries().forEach(new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$Qz8oyL0qAMzuJuwPLHs4cVCa7kg
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                StatusBar.lambda$maybeEscalateHeadsUp$14((NotificationEntry) obj);
            }
        });
        this.mHeadsUpManager.releaseAllImmediately();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$maybeEscalateHeadsUp$14(NotificationEntry entry) {
        StatusBarNotification sbn = entry.notification;
        Notification notification = sbn.getNotification();
        if (notification.fullScreenIntent != null) {
            try {
                EventLog.writeEvent(36003, sbn.getKey());
                notification.fullScreenIntent.send();
                entry.notifyFullScreenIntentLaunched();
            } catch (PendingIntent.CanceledException e) {
            }
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void handleSystemKey(int key) {
        if (this.mCommandQueue.panelsEnabled() && this.mKeyguardMonitor.isDeviceInteractive()) {
            if ((!this.mKeyguardMonitor.isShowing() || this.mKeyguardMonitor.isOccluded()) && this.mUserSetup) {
                if (280 == key) {
                    this.mMetricsLogger.action(493);
                    this.mNotificationPanel.collapse(false, 1.0f);
                } else if (281 == key) {
                    this.mMetricsLogger.action(494);
                    if (this.mNotificationPanel.isFullyCollapsed()) {
                        if (this.mVibrateOnOpening) {
                            this.mVibratorHelper.vibrate(2);
                        }
                        this.mNotificationPanel.expand(true);
                        ((NotificationListContainer) this.mStackScroller).setWillExpand(true);
                        this.mHeadsUpManager.unpinAll(true);
                        this.mMetricsLogger.count("panel_open", 1);
                    } else if (!this.mNotificationPanel.isInSettings() && !this.mNotificationPanel.isExpanding()) {
                        this.mNotificationPanel.flingSettings(0.0f, 0);
                        this.mMetricsLogger.count("panel_open_qs", 1);
                    }
                }
            }
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void showPinningEnterExitToast(boolean entering) {
        if (getNavigationBarView() != null) {
            getNavigationBarView().showPinningEnterExitToast(entering);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void showPinningEscapeToast() {
        if (getNavigationBarView() != null) {
            getNavigationBarView().showPinningEscapeToast();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void makeExpandedVisible(boolean force) {
        if (!force && (this.mExpandedVisible || !this.mCommandQueue.panelsEnabled())) {
            return;
        }
        this.mExpandedVisible = true;
        this.mStatusBarWindowController.setPanelVisible(true);
        visibilityChanged(true);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, !force);
        setInteracting(1, true);
    }

    public void animateCollapsePanels() {
        animateCollapsePanels(0);
    }

    public void postAnimateCollapsePanels() {
        this.mHandler.post(this.mAnimateCollapsePanels);
    }

    public void postAnimateForceCollapsePanels() {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$vsXwLw7AvX4yDOof5dgbuWdLbIs
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$postAnimateForceCollapsePanels$15$StatusBar();
            }
        });
    }

    public /* synthetic */ void lambda$postAnimateForceCollapsePanels$15$StatusBar() {
        animateCollapsePanels(0, true);
    }

    public void postAnimateOpenPanels() {
        this.mHandler.sendEmptyMessage(1002);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void togglePanel() {
        if (this.mPanelExpanded) {
            animateCollapsePanels();
        } else {
            animateExpandNotificationsPanel();
        }
    }

    public void animateCollapsePanels(int flags) {
        animateCollapsePanels(flags, false, false, 1.0f);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks, com.android.systemui.statusbar.phone.ShadeController
    public void animateCollapsePanels(int flags, boolean force) {
        animateCollapsePanels(flags, force, false, 1.0f);
    }

    public void animateCollapsePanels(int flags, boolean force, boolean delayed) {
        animateCollapsePanels(flags, force, delayed, 1.0f);
    }

    public void animateCollapsePanels(int flags, boolean force, boolean delayed, float speedUpFactor) {
        if (!force && this.mState != 0) {
            runPostCollapseRunnables();
            return;
        }
        if ((flags & 2) == 0 && !this.mHandler.hasMessages(1020)) {
            this.mHandler.removeMessages(1020);
            this.mHandler.sendEmptyMessage(1020);
        }
        Log.v(TAG, "mStatusBarWindow: " + this.mStatusBarWindow + " canPanelBeCollapsed(): " + this.mNotificationPanel.canPanelBeCollapsed());
        if (this.mStatusBarWindow != null && this.mNotificationPanel.canPanelBeCollapsed()) {
            this.mStatusBarWindowController.setStatusBarFocusable(false);
            this.mStatusBarWindow.cancelExpandHelper();
            this.mStatusBarView.collapsePanel(true, delayed, speedUpFactor);
            return;
        }
        BubbleController bubbleController = this.mBubbleController;
        if (bubbleController != null) {
            bubbleController.collapseStack();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runPostCollapseRunnables() {
        ArrayList<Runnable> clonedList = new ArrayList<>(this.mPostCollapseRunnables);
        this.mPostCollapseRunnables.clear();
        int size = clonedList.size();
        for (int i = 0; i < size; i++) {
            clonedList.get(i).run();
        }
        this.mStatusBarKeyguardViewManager.readyForKeyguardDone();
    }

    public void onInputFocusTransfer(boolean start, float velocity) {
        if (!this.mCommandQueue.panelsEnabled()) {
            return;
        }
        if (start) {
            this.mNotificationPanel.startWaitingForOpenPanelGesture();
        } else {
            this.mNotificationPanel.stopWaitingForOpenPanelGesture(velocity);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void animateExpandNotificationsPanel() {
        if (!this.mCommandQueue.panelsEnabled()) {
            return;
        }
        this.mNotificationPanel.expandWithoutQs();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void animateExpandSettingsPanel(String subPanel) {
        if (this.mCommandQueue.panelsEnabled() && this.mUserSetup) {
            if (subPanel != null) {
                this.mQSPanel.openDetails(subPanel);
            }
            this.mNotificationPanel.expandWithQs();
        }
    }

    public void animateCollapseQuickSettings() {
        if (this.mState == 0) {
            this.mStatusBarView.collapsePanel(true, false, 1.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void makeExpandedInvisible() {
        if (!this.mExpandedVisible || this.mStatusBarWindow == null) {
            return;
        }
        this.mStatusBarView.collapsePanel(false, false, 1.0f);
        this.mNotificationPanel.closeQs();
        this.mExpandedVisible = false;
        visibilityChanged(false);
        this.mStatusBarWindowController.setPanelVisible(false);
        this.mStatusBarWindowController.setForceStatusBarVisible(false);
        this.mGutsManager.closeAndSaveGuts(true, true, true, -1, -1, true);
        runPostCollapseRunnables();
        setInteracting(1, false);
        if (!this.mNotificationActivityStarter.isCollapsingToShowActivityOverLockscreen()) {
            showBouncerIfKeyguard();
        }
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, this.mNotificationPanel.hideStatusBarIconsWhenExpanded());
        if (!this.mStatusBarKeyguardViewManager.isShowing()) {
            WindowManagerGlobal.getInstance().trimMemory(20);
        }
    }

    public boolean interceptTouchEvent(MotionEvent event) {
        if (this.mStatusBarWindowState == 0) {
            boolean upOrCancel = event.getAction() == 1 || event.getAction() == 3;
            if (upOrCancel && !this.mExpandedVisible) {
                setInteracting(1, false);
            } else {
                setInteracting(1, true);
            }
        }
        return false;
    }

    public GestureRecorder getGestureRecorder() {
        return this.mGestureRec;
    }

    public BiometricUnlockController getBiometricUnlockController() {
        return this.mBiometricUnlockController;
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void setWindowState(int displayId, int window, int state) {
        if (displayId != this.mDisplayId) {
            return;
        }
        boolean showing = state == 0;
        if (this.mStatusBarWindow != null && window == 1 && this.mStatusBarWindowState != state) {
            this.mStatusBarWindowState = state;
            if (!showing && this.mState == 0) {
                this.mStatusBarView.collapsePanel(false, false, 1.0f);
            }
            if (this.mStatusBarView != null) {
                this.mStatusBarWindowHidden = state == 2;
                updateHideIconsForBouncer(false);
            }
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void setSystemUiVisibility(int displayId, int vis, int fullscreenStackVis, int dockedStackVis, int mask, Rect fullscreenStackBounds, Rect dockedStackBounds, boolean navbarColorManagedByIme) {
        boolean sbModeChanged;
        if (displayId != this.mDisplayId) {
            return;
        }
        int oldVal = this.mSystemUiVisibility;
        int newVal = ((~mask) & oldVal) | (vis & mask);
        int diff = newVal ^ oldVal;
        if (diff == 0) {
            sbModeChanged = false;
        } else {
            this.mSystemUiVisibility = newVal;
            if ((diff & 1) != 0) {
                updateAreThereNotifications();
            }
            if ((vis & 268435456) != 0) {
                this.mNoAnimationOnNextBarModeChange = true;
            }
            int sbMode = computeStatusBarMode(oldVal, newVal);
            boolean sbModeChanged2 = sbMode != -1;
            if (sbModeChanged2 && sbMode != this.mStatusBarMode) {
                this.mStatusBarMode = sbMode;
                checkBarModes();
                this.mAutoHideController.touchAutoHide();
            }
            this.mStatusBarStateController.setSystemUiVisibility(this.mSystemUiVisibility);
            sbModeChanged = sbModeChanged2;
        }
        this.mLightBarController.onSystemUiVisibilityChanged(fullscreenStackVis, dockedStackVis, mask, fullscreenStackBounds, dockedStackBounds, sbModeChanged, this.mStatusBarMode, navbarColorManagedByIme);
    }

    protected final int getSystemUiVisibility() {
        return this.mSystemUiVisibility;
    }

    protected final int getDisplayId() {
        return this.mDisplayId;
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void showWirelessChargingAnimation(int batteryLevel) {
        if (this.mDozing || this.mKeyguardManager.isKeyguardLocked()) {
            WirelessChargingAnimation.makeWirelessChargingAnimation(this.mContext, null, batteryLevel, new WirelessChargingAnimation.Callback() { // from class: com.android.systemui.statusbar.phone.StatusBar.7
                @Override // com.android.systemui.charging.WirelessChargingAnimation.Callback
                public void onAnimationStarting() {
                    CrossFadeHelper.fadeOut(StatusBar.this.mNotificationPanel, 1.0f);
                }

                @Override // com.android.systemui.charging.WirelessChargingAnimation.Callback
                public void onAnimationEnded() {
                    CrossFadeHelper.fadeIn(StatusBar.this.mNotificationPanel);
                }
            }, this.mDozing).show();
        } else {
            WirelessChargingAnimation.makeWirelessChargingAnimation(this.mContext, null, batteryLevel, null, false).show();
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void onRecentsAnimationStateChanged(boolean running) {
        setInteracting(2, running);
    }

    protected int computeStatusBarMode(int oldVal, int newVal) {
        return computeBarMode(oldVal, newVal);
    }

    protected BarTransitions getStatusBarTransitions() {
        return this.mStatusBarWindow.getBarTransitions();
    }

    protected int computeBarMode(int oldVis, int newVis) {
        int oldMode = barMode(oldVis);
        int newMode = barMode(newVis);
        if (oldMode == newMode) {
            return -1;
        }
        return newMode;
    }

    private int barMode(int vis) {
        if ((67108864 & vis) != 0) {
            return 1;
        }
        if ((1073741824 & vis) == 0) {
            if ((vis & 9) == 9) {
                return 6;
            }
            if ((vis & 8) != 0) {
                return 4;
            }
            if ((vis & 1) != 0) {
                return 3;
            }
            return 0;
        }
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkBarModes() {
        if (this.mDemoMode) {
            return;
        }
        if (this.mStatusBarView != null && getStatusBarTransitions() != null) {
            checkBarMode(this.mStatusBarMode, this.mStatusBarWindowState, getStatusBarTransitions());
        }
        this.mNavigationBarController.checkNavBarModes(this.mDisplayId);
        this.mNoAnimationOnNextBarModeChange = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setQsScrimEnabled(boolean scrimEnabled) {
        this.mNotificationPanel.setQsScrimEnabled(scrimEnabled);
    }

    void checkBarMode(int mode, int windowState, BarTransitions transitions) {
        boolean anim = (this.mNoAnimationOnNextBarModeChange || !this.mDeviceInteractive || windowState == 2) ? false : true;
        transitions.transitionTo(mode, anim);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishBarAnimations() {
        StatusBarWindowView statusBarWindowView = this.mStatusBarWindow;
        if (statusBarWindowView != null && statusBarWindowView.getBarTransitions() != null) {
            this.mStatusBarWindow.getBarTransitions().finishAnimations();
        }
        this.mNavigationBarController.finishBarAnimations(this.mDisplayId);
    }

    public void setInteracting(int barWindow, boolean interacting) {
        int i;
        boolean changing = ((this.mInteractingWindows & barWindow) != 0) != interacting;
        if (interacting) {
            i = this.mInteractingWindows | barWindow;
        } else {
            i = this.mInteractingWindows & (~barWindow);
        }
        this.mInteractingWindows = i;
        if (this.mInteractingWindows != 0) {
            this.mAutoHideController.suspendAutoHide();
        } else {
            this.mAutoHideController.resumeSuspendedAutoHide();
        }
        if (changing && interacting && barWindow == 2) {
            this.mNavigationBarController.touchAutoDim(this.mDisplayId);
            dismissVolumeDialog();
        }
        checkBarModes();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissVolumeDialog() {
        VolumeComponent volumeComponent = this.mVolumeComponent;
        if (volumeComponent != null) {
            volumeComponent.dismissNow();
        }
    }

    public boolean inFullscreenMode() {
        return (this.mSystemUiVisibility & 6) != 0;
    }

    public boolean inImmersiveMode() {
        return (this.mSystemUiVisibility & GL30.GL_COLOR) != 0;
    }

    private boolean areLightsOn() {
        return (this.mSystemUiVisibility & 1) == 0;
    }

    public static String viewInfo(View v) {
        return "[(" + v.getLeft() + "," + v.getTop() + ")(" + v.getRight() + "," + v.getBottom() + ") " + v.getWidth() + "x" + v.getHeight() + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // com.android.systemui.SystemUI
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        synchronized (this.mQueueLock) {
            pw.println("Current Status Bar state:");
            pw.println("  mExpandedVisible=" + this.mExpandedVisible);
            pw.println("  mDisplayMetrics=" + this.mDisplayMetrics);
            pw.println("  mStackScroller: " + viewInfo(this.mStackScroller));
            pw.println("  mStackScroller: " + viewInfo(this.mStackScroller) + " scroll " + this.mStackScroller.getScrollX() + "," + this.mStackScroller.getScrollY());
        }
        pw.print("  mInteractingWindows=");
        pw.println(this.mInteractingWindows);
        pw.print("  mStatusBarWindowState=");
        pw.println(StatusBarManager.windowStateToString(this.mStatusBarWindowState));
        pw.print("  mStatusBarMode=");
        pw.println(BarTransitions.modeToString(this.mStatusBarMode));
        pw.print("  mDozing=");
        pw.println(this.mDozing);
        pw.print("  mZenMode=");
        pw.println(Settings.Global.zenModeToString(Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode", 0)));
        pw.print("  mWallpaperSupported= ");
        pw.println(this.mWallpaperSupported);
        StatusBarWindowView statusBarWindowView = this.mStatusBarWindow;
        if (statusBarWindowView != null) {
            dumpBarTransitions(pw, "mStatusBarWindow", statusBarWindowView.getBarTransitions());
        }
        pw.println("  StatusBarWindowView: ");
        StatusBarWindowView statusBarWindowView2 = this.mStatusBarWindow;
        if (statusBarWindowView2 != null) {
            statusBarWindowView2.dump(fd, pw, args);
        }
        pw.println("  mMediaManager: ");
        NotificationMediaManager notificationMediaManager = this.mMediaManager;
        if (notificationMediaManager != null) {
            notificationMediaManager.dump(fd, pw, args);
        }
        pw.println("  Panels: ");
        if (this.mNotificationPanel != null) {
            pw.println("    mNotificationPanel=" + this.mNotificationPanel + " params=" + this.mNotificationPanel.getLayoutParams().debug(""));
            pw.print("      ");
            this.mNotificationPanel.dump(fd, pw, args);
        }
        pw.println("  mStackScroller: ");
        if (this.mStackScroller instanceof Dumpable) {
            pw.print("      ");
            ((Dumpable) this.mStackScroller).dump(fd, pw, args);
        }
        pw.println("  Theme:");
        String nightMode = this.mUiModeManager == null ? "null" : this.mUiModeManager.getNightMode() + "";
        pw.println("    dark theme: " + nightMode + " (auto: 0, yes: 2, no: 1" + NavigationBarInflaterView.KEY_CODE_END);
        boolean lightWpTheme = this.mContext.getThemeResId() == R.style.Theme_SystemUI_Light;
        pw.println("    light wallpaper theme: " + lightWpTheme);
        DozeLog.dump(pw);
        BiometricUnlockController biometricUnlockController = this.mBiometricUnlockController;
        if (biometricUnlockController != null) {
            biometricUnlockController.dump(pw);
        }
        KeyguardIndicationController keyguardIndicationController = this.mKeyguardIndicationController;
        if (keyguardIndicationController != null) {
            keyguardIndicationController.dump(fd, pw, args);
        }
        ScrimController scrimController = this.mScrimController;
        if (scrimController != null) {
            scrimController.dump(fd, pw, args);
        }
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (statusBarKeyguardViewManager != null) {
            statusBarKeyguardViewManager.dump(pw);
        }
        synchronized (this.mEntryManager.getNotificationData()) {
            this.mEntryManager.getNotificationData().dump(pw, "  ");
        }
        HeadsUpManagerPhone headsUpManagerPhone = this.mHeadsUpManager;
        if (headsUpManagerPhone != null) {
            headsUpManagerPhone.dump(fd, pw, args);
        } else {
            pw.println("  mHeadsUpManager: null");
        }
        NotificationGroupManager notificationGroupManager = this.mGroupManager;
        if (notificationGroupManager != null) {
            notificationGroupManager.dump(fd, pw, args);
        } else {
            pw.println("  mGroupManager: null");
        }
        BubbleController bubbleController = this.mBubbleController;
        if (bubbleController != null) {
            bubbleController.dump(fd, pw, args);
        }
        LightBarController lightBarController = this.mLightBarController;
        if (lightBarController != null) {
            lightBarController.dump(fd, pw, args);
        }
        UnlockMethodCache unlockMethodCache = this.mUnlockMethodCache;
        if (unlockMethodCache != null) {
            unlockMethodCache.dump(pw);
        }
        KeyguardBypassController keyguardBypassController = this.mKeyguardBypassController;
        if (keyguardBypassController != null) {
            keyguardBypassController.dump(pw);
        }
        KeyguardUpdateMonitor keyguardUpdateMonitor = this.mKeyguardUpdateMonitor;
        if (keyguardUpdateMonitor != null) {
            keyguardUpdateMonitor.dump(fd, pw, args);
        }
        ((FalsingManager) Dependency.get(FalsingManager.class)).dump(pw);
        FalsingLog.dump(pw);
        pw.println("SharedPreferences:");
        for (Map.Entry<String, ?> entry : Prefs.getAll(this.mContext).entrySet()) {
            pw.print("  ");
            pw.print(entry.getKey());
            pw.print("=");
            pw.println(entry.getValue());
        }
    }

    private /* synthetic */ void lambda$dump$16() {
        this.mStatusBarView.getLocationOnScreen(this.mAbsPos);
        Log.d(TAG, "mStatusBarView: ----- (" + this.mAbsPos[0] + "," + this.mAbsPos[1] + ") " + this.mStatusBarView.getWidth() + "x" + getStatusBarHeight());
        this.mStatusBarView.debug();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void dumpBarTransitions(PrintWriter pw, String var, BarTransitions transitions) {
        pw.print("  ");
        pw.print(var);
        pw.print(".BarTransitions.mMode=");
        pw.println(BarTransitions.modeToString(transitions.getMode()));
    }

    public void createAndAddWindows(RegisterStatusBarResult result) {
        makeStatusBarView(result);
        this.mStatusBarWindowController = (StatusBarWindowController) Dependency.get(StatusBarWindowController.class);
        this.mStatusBarWindowController.add(this.mStatusBarWindow, getStatusBarHeight());
    }

    void updateDisplaySize() {
        this.mDisplay.getMetrics(this.mDisplayMetrics);
        this.mDisplay.getSize(this.mCurrentDisplaySize);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getDisplayDensity() {
        return this.mDisplayMetrics.density;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getDisplayWidth() {
        return this.mDisplayMetrics.widthPixels;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getDisplayHeight() {
        return this.mDisplayMetrics.heightPixels;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRotation() {
        return this.mDisplay.getRotation();
    }

    public void startActivityDismissingKeyguard(Intent intent, boolean onlyProvisioned, boolean dismissShade, int flags) {
        startActivityDismissingKeyguard(intent, onlyProvisioned, dismissShade, false, null, flags);
    }

    public void startActivityDismissingKeyguard(Intent intent, boolean onlyProvisioned, boolean dismissShade) {
        startActivityDismissingKeyguard(intent, onlyProvisioned, dismissShade, 0);
    }

    public void startActivityDismissingKeyguard(final Intent intent, boolean onlyProvisioned, boolean dismissShade, final boolean disallowEnterPictureInPictureWhileLaunching, final ActivityStarter.Callback callback, final int flags) {
        if (!onlyProvisioned || this.mDeviceProvisionedController.isDeviceProvisioned()) {
            boolean afterKeyguardGone = this.mActivityIntentHelper.wouldLaunchResolverActivity(intent, this.mLockscreenUserManager.getCurrentUserId());
            Runnable runnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$cYI_U_ShQVlsmm6P5qEeF15rkKQ
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBar.this.lambda$startActivityDismissingKeyguard$17$StatusBar(intent, flags, disallowEnterPictureInPictureWhileLaunching, callback);
                }
            };
            Runnable cancelRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$GXuArppP3Gxe5JvIROZsOAy5v74
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBar.lambda$startActivityDismissingKeyguard$18(ActivityStarter.Callback.this);
                }
            };
            executeRunnableDismissingKeyguard(runnable, cancelRunnable, dismissShade, afterKeyguardGone, true);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x007c  */
    /* JADX WARN: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$startActivityDismissingKeyguard$17$StatusBar(android.content.Intent r20, int r21, boolean r22, com.android.systemui.plugins.ActivityStarter.Callback r23) {
        /*
            r19 = this;
            r1 = r19
            r14 = r20
            r15 = r23
            com.android.systemui.assist.AssistManager r0 = r1.mAssistManager
            r0.hideAssist()
            r0 = 335544320(0x14000000, float:6.4623485E-27)
            r14.setFlags(r0)
            r20.addFlags(r21)
            r16 = -96
            android.app.ActivityOptions r0 = new android.app.ActivityOptions
            r2 = 0
            android.os.Bundle r2 = getActivityOptions(r2)
            r0.<init>(r2)
            r13 = r0
            r12 = r22
            r13.setDisallowEnterPictureInPictureWhileLaunching(r12)
            android.content.Intent r0 = com.android.systemui.statusbar.phone.KeyguardBottomAreaView.INSECURE_CAMERA_INTENT
            if (r14 != r0) goto L2d
            r0 = 3
            r13.setRotationAnimationHint(r0)
        L2d:
            java.lang.String r0 = r20.getAction()
            java.lang.String r2 = "android.settings.panel.action.VOLUME"
            if (r0 != r2) goto L39
            r0 = 1
            r13.setDisallowEnterPictureInPictureWhileLaunching(r0)
        L39:
            android.app.IActivityTaskManager r2 = android.app.ActivityTaskManager.getService()     // Catch: android.os.RemoteException -> L6e
            r3 = 0
            android.content.Context r0 = r1.mContext     // Catch: android.os.RemoteException -> L6e
            java.lang.String r4 = r0.getBasePackageName()     // Catch: android.os.RemoteException -> L6e
            android.content.Context r0 = r1.mContext     // Catch: android.os.RemoteException -> L6e
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch: android.os.RemoteException -> L6e
            java.lang.String r6 = r14.resolveTypeIfNeeded(r0)     // Catch: android.os.RemoteException -> L6e
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 268435456(0x10000000, float:2.5243549E-29)
            r11 = 0
            android.os.Bundle r0 = r13.toBundle()     // Catch: android.os.RemoteException -> L6e
            android.os.UserHandle r5 = android.os.UserHandle.CURRENT     // Catch: android.os.RemoteException -> L6e
            int r17 = r5.getIdentifier()     // Catch: android.os.RemoteException -> L6e
            r5 = r20
            r12 = r0
            r18 = r13
            r13 = r17
            int r0 = r2.startActivityAsUser(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch: android.os.RemoteException -> L6c
            r16 = r0
            goto L7a
        L6c:
            r0 = move-exception
            goto L71
        L6e:
            r0 = move-exception
            r18 = r13
        L71:
            java.lang.String r2 = "StatusBar"
            java.lang.String r3 = "Unable to start activity"
            android.util.Log.w(r2, r3, r0)
            r0 = r16
        L7a:
            if (r15 == 0) goto L7f
            r15.onActivityStarted(r0)
        L7f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.StatusBar.lambda$startActivityDismissingKeyguard$17$StatusBar(android.content.Intent, int, boolean, com.android.systemui.plugins.ActivityStarter$Callback):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$startActivityDismissingKeyguard$18(ActivityStarter.Callback callback) {
        if (callback != null) {
            callback.onActivityStarted(-96);
        }
    }

    public void readyForKeyguardDone() {
        this.mStatusBarKeyguardViewManager.readyForKeyguardDone();
    }

    public void executeRunnableDismissingKeyguard(final Runnable runnable, Runnable cancelAction, final boolean dismissShade, boolean afterKeyguardGone, final boolean deferred) {
        dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$L4kE_3rylr6H_pNi7mB0rm5zMes
            @Override // com.android.systemui.plugins.ActivityStarter.OnDismissAction
            public final boolean onDismiss() {
                return StatusBar.this.lambda$executeRunnableDismissingKeyguard$19$StatusBar(runnable, dismissShade, deferred);
            }
        }, cancelAction, afterKeyguardGone);
    }

    public /* synthetic */ boolean lambda$executeRunnableDismissingKeyguard$19$StatusBar(Runnable runnable, boolean dismissShade, boolean deferred) {
        if (runnable != null) {
            if (this.mStatusBarKeyguardViewManager.isShowing() && this.mStatusBarKeyguardViewManager.isOccluded()) {
                this.mStatusBarKeyguardViewManager.addAfterKeyguardGoneRunnable(runnable);
            } else {
                AsyncTask.execute(runnable);
            }
        }
        if (dismissShade) {
            if (this.mExpandedVisible && !this.mBouncerShowing) {
                animateCollapsePanels(2, true, true);
            } else {
                this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$h1YVkfulr3o8W-Bsc2YTikmPmYI
                    @Override // java.lang.Runnable
                    public final void run() {
                        StatusBar.this.runPostCollapseRunnables();
                    }
                });
            }
        } else if (isInLaunchTransition() && this.mNotificationPanel.isLaunchTransitionFinished()) {
            H h = this.mHandler;
            final StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
            Objects.requireNonNull(statusBarKeyguardViewManager);
            h.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$JQMd1r5WuAA5n3kv4yv5u3MFjI8
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarKeyguardViewManager.this.readyForKeyguardDone();
                }
            });
        }
        return deferred;
    }

    public void resetUserExpandedStates() {
        ArrayList<NotificationEntry> activeNotifications = this.mEntryManager.getNotificationData().getActiveNotifications();
        int notificationCount = activeNotifications.size();
        for (int i = 0; i < notificationCount; i++) {
            NotificationEntry entry = activeNotifications.get(i);
            entry.resetUserExpansion();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void executeWhenUnlocked(ActivityStarter.OnDismissAction action, boolean requiresShadeOpen) {
        if (this.mStatusBarKeyguardViewManager.isShowing() && requiresShadeOpen) {
            this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
        }
        dismissKeyguardThenExecute(action, null, false);
    }

    protected void dismissKeyguardThenExecute(ActivityStarter.OnDismissAction action, boolean afterKeyguardGone) {
        dismissKeyguardThenExecute(action, null, afterKeyguardGone);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void dismissKeyguardThenExecute(ActivityStarter.OnDismissAction action, Runnable cancelAction, boolean afterKeyguardGone) {
        if (this.mWakefulnessLifecycle.getWakefulness() == 0 && this.mUnlockMethodCache.canSkipBouncer() && !this.mStatusBarStateController.leaveOpenOnKeyguardHide() && isPulsing()) {
            this.mBiometricUnlockController.startWakeAndUnlock(2);
        }
        if (this.mStatusBarKeyguardViewManager.isShowing()) {
            this.mStatusBarKeyguardViewManager.dismissWithAction(action, cancelAction, afterKeyguardGone);
        } else {
            action.onDismiss();
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onConfigChanged(Configuration newConfig) {
        updateResources();
        updateDisplaySize();
        this.mViewHierarchyManager.updateRowStates();
        this.mScreenPinningRequest.onConfigurationChanged();
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void setLockscreenUser(int newUserId) {
        LockscreenWallpaper lockscreenWallpaper = this.mLockscreenWallpaper;
        if (lockscreenWallpaper != null) {
            lockscreenWallpaper.setCurrentUser(newUserId);
        }
        this.mScrimController.setCurrentUser(newUserId);
        if (this.mWallpaperSupported) {
            this.mWallpaperChangedReceiver.onReceive(this.mContext, null);
        }
    }

    void updateResources() {
        QSPanel qSPanel = this.mQSPanel;
        if (qSPanel != null) {
            qSPanel.updateResources();
        }
        loadDimens();
        PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
        if (phoneStatusBarView != null) {
            phoneStatusBarView.updateResources();
        }
        NotificationPanelView notificationPanelView = this.mNotificationPanel;
        if (notificationPanelView != null) {
            notificationPanelView.updateResources();
        }
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.updateResources();
        }
    }

    protected void loadDimens() {
        int i;
        Resources res = this.mContext.getResources();
        int oldBarHeight = this.mNaturalBarHeight;
        this.mNaturalBarHeight = res.getDimensionPixelSize(17105438);
        StatusBarWindowController statusBarWindowController = this.mStatusBarWindowController;
        if (statusBarWindowController != null && (i = this.mNaturalBarHeight) != oldBarHeight) {
            statusBarWindowController.setBarHeight(i);
        }
    }

    protected void handleVisibleToUserChanged(boolean visibleToUser) {
        if (visibleToUser) {
            handleVisibleToUserChangedImpl(visibleToUser);
            this.mNotificationLogger.startNotificationLogging();
            return;
        }
        this.mNotificationLogger.stopNotificationLogging();
        handleVisibleToUserChangedImpl(visibleToUser);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handlePeekToExpandTransistion() {
        try {
            int notificationLoad = this.mEntryManager.getNotificationData().getActiveNotifications().size();
            this.mBarService.onPanelRevealed(false, notificationLoad);
        } catch (RemoteException e) {
        }
    }

    private void handleVisibleToUserChangedImpl(boolean visibleToUser) {
        int i;
        if (visibleToUser) {
            boolean pinnedHeadsUp = this.mHeadsUpManager.hasPinnedHeadsUp();
            final boolean clearNotificationEffects = !this.mPresenter.isPresenterFullyCollapsed() && ((i = this.mState) == 0 || i == 2);
            int notificationLoad = this.mEntryManager.getNotificationData().getActiveNotifications().size();
            if (pinnedHeadsUp && this.mPresenter.isPresenterFullyCollapsed()) {
                notificationLoad = 1;
            }
            final int finalNotificationLoad = notificationLoad;
            this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$HmJQbKES5h2Nfz54WrIvhU_YRh4
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBar.this.lambda$handleVisibleToUserChangedImpl$20$StatusBar(clearNotificationEffects, finalNotificationLoad);
                }
            });
            return;
        }
        this.mUiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$c9qjiwrIU9RXDCI3JWlVp8xvdoU
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$handleVisibleToUserChangedImpl$21$StatusBar();
            }
        });
    }

    public /* synthetic */ void lambda$handleVisibleToUserChangedImpl$20$StatusBar(boolean clearNotificationEffects, int finalNotificationLoad) {
        try {
            this.mBarService.onPanelRevealed(clearNotificationEffects, finalNotificationLoad);
        } catch (RemoteException e) {
        }
    }

    public /* synthetic */ void lambda$handleVisibleToUserChangedImpl$21$StatusBar() {
        try {
            this.mBarService.onPanelHidden();
        } catch (RemoteException e) {
        }
    }

    private void logStateToEventlog() {
        boolean isShowing = this.mStatusBarKeyguardViewManager.isShowing();
        boolean isOccluded = this.mStatusBarKeyguardViewManager.isOccluded();
        boolean isBouncerShowing = this.mStatusBarKeyguardViewManager.isBouncerShowing();
        boolean isSecure = this.mUnlockMethodCache.isMethodSecure();
        boolean canSkipBouncer = this.mUnlockMethodCache.canSkipBouncer();
        int stateFingerprint = getLoggingFingerprint(this.mState, isShowing, isOccluded, isBouncerShowing, isSecure, canSkipBouncer);
        if (stateFingerprint != this.mLastLoggedStateFingerprint) {
            if (this.mStatusBarStateLog == null) {
                this.mStatusBarStateLog = new LogMaker(0);
            }
            this.mMetricsLogger.write(this.mStatusBarStateLog.setCategory(isBouncerShowing ? 197 : 196).setType(isShowing ? 1 : 2).setSubtype(isSecure ? 1 : 0));
            EventLogTags.writeSysuiStatusBarState(this.mState, isShowing ? 1 : 0, isOccluded ? 1 : 0, isBouncerShowing ? 1 : 0, isSecure ? 1 : 0, canSkipBouncer ? 1 : 0);
            this.mLastLoggedStateFingerprint = stateFingerprint;
        }
    }

    private static int getLoggingFingerprint(int statusBarState, boolean keyguardShowing, boolean keyguardOccluded, boolean bouncerShowing, boolean secure, boolean currentlyInsecure) {
        return (statusBarState & 255) | ((keyguardShowing ? 1 : 0) << 8) | ((keyguardOccluded ? 1 : 0) << 9) | ((bouncerShowing ? 1 : 0) << 10) | ((secure ? 1 : 0) << 11) | ((currentlyInsecure ? 1 : 0) << 12);
    }

    void postStartTracing() {
        this.mHandler.postDelayed(this.mStartTracing, 3000L);
    }

    void vibrate() {
        Vibrator vib = (Vibrator) this.mContext.getSystemService("vibrator");
        vib.vibrate(250L, VIBRATION_ATTRIBUTES);
    }

    public /* synthetic */ void lambda$new$22$StatusBar() {
        Debug.stopMethodTracing();
        Log.d(TAG, "stopTracing");
        vibrate();
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void postQSRunnableDismissingKeyguard(final Runnable runnable) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$GjkAle6Yh2ihV-21EScdNFN2cPY
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$postQSRunnableDismissingKeyguard$24$StatusBar(runnable);
            }
        });
    }

    public /* synthetic */ void lambda$postQSRunnableDismissingKeyguard$24$StatusBar(final Runnable runnable) {
        this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
        executeRunnableDismissingKeyguard(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$AWaoQD-Cpm4WLbje2ihIy1hyU7w
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$postQSRunnableDismissingKeyguard$23$StatusBar(runnable);
            }
        }, null, false, false, false);
    }

    public /* synthetic */ void lambda$postQSRunnableDismissingKeyguard$23$StatusBar(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void postStartActivityDismissingKeyguard(final PendingIntent intent) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$Ca0j1OxP0PIbbWLJ9seRzJaosY4
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$postStartActivityDismissingKeyguard$25$StatusBar(intent);
            }
        });
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void postStartActivityDismissingKeyguard(final Intent intent, int delay) {
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$CSd9n4rtnrfFyOdT2eTFRNUO5xM
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$postStartActivityDismissingKeyguard$26$StatusBar(intent);
            }
        }, delay);
    }

    public /* synthetic */ void lambda$postStartActivityDismissingKeyguard$26$StatusBar(Intent intent) {
        handleStartActivityDismissingKeyguard(intent, true);
    }

    private void handleStartActivityDismissingKeyguard(Intent intent, boolean onlyProvisioned) {
        startActivityDismissingKeyguard(intent, onlyProvisioned, true);
    }

    @Override // com.android.systemui.DemoMode
    public void dispatchDemoCommand(String command, Bundle args) {
        VolumeComponent volumeComponent;
        int barMode = 1;
        if (!this.mDemoModeAllowed) {
            this.mDemoModeAllowed = Settings.Global.getInt(this.mContext.getContentResolver(), DemoMode.DEMO_MODE_ALLOWED, 0) != 0;
        }
        if (this.mDemoModeAllowed) {
            if (command.equals("enter")) {
                this.mDemoMode = true;
            } else if (command.equals(DemoMode.COMMAND_EXIT)) {
                this.mDemoMode = false;
                checkBarModes();
            } else if (!this.mDemoMode) {
                dispatchDemoCommand("enter", new Bundle());
            }
            boolean modeChange = command.equals("enter") || command.equals(DemoMode.COMMAND_EXIT);
            if ((modeChange || command.equals("volume")) && (volumeComponent = this.mVolumeComponent) != null) {
                volumeComponent.dispatchDemoCommand(command, args);
            }
            if (modeChange || command.equals(DemoMode.COMMAND_CLOCK)) {
                dispatchDemoCommandToView(command, args, R.id.clock);
            }
            if (modeChange || command.equals(DemoMode.COMMAND_BATTERY)) {
                this.mBatteryController.dispatchDemoCommand(command, args);
            }
            if (modeChange || command.equals("status")) {
                ((StatusBarIconControllerImpl) this.mIconController).dispatchDemoCommand(command, args);
            }
            if (this.mNetworkController != null && (modeChange || command.equals("network"))) {
                this.mNetworkController.dispatchDemoCommand(command, args);
            }
            if (modeChange || command.equals(DemoMode.COMMAND_NOTIFICATIONS)) {
                PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
                View notifications = phoneStatusBarView == null ? null : phoneStatusBarView.findViewById(R.id.notification_icon_area);
                if (notifications != null) {
                    String visible = args.getString("visible");
                    int vis = (this.mDemoMode && OOBEEvent.STRING_FALSE.equals(visible)) ? 4 : 0;
                    notifications.setVisibility(vis);
                }
            }
            if (command.equals(DemoMode.COMMAND_BARS)) {
                String mode = args.getString("mode");
                if ("opaque".equals(mode)) {
                    barMode = 0;
                } else if ("translucent".equals(mode)) {
                    barMode = 2;
                } else if (!"semi-transparent".equals(mode)) {
                    if ("transparent".equals(mode)) {
                        barMode = 4;
                    } else {
                        barMode = "warning".equals(mode) ? 5 : -1;
                    }
                }
                if (barMode != -1) {
                    StatusBarWindowView statusBarWindowView = this.mStatusBarWindow;
                    if (statusBarWindowView != null && statusBarWindowView.getBarTransitions() != null) {
                        this.mStatusBarWindow.getBarTransitions().transitionTo(barMode, true);
                    }
                    this.mNavigationBarController.transitionTo(this.mDisplayId, barMode, true);
                }
            }
            if (modeChange || command.equals(DemoMode.COMMAND_OPERATOR)) {
                dispatchDemoCommandToView(command, args, R.id.operator_name);
            }
        }
    }

    private void dispatchDemoCommandToView(String command, Bundle args, int id) {
        PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
        if (phoneStatusBarView == null) {
            return;
        }
        View v = phoneStatusBarView.findViewById(id);
        if (v instanceof DemoMode) {
            ((DemoMode) v).dispatchDemoCommand(command, args);
        }
    }

    public void showKeyguard() {
        this.mStatusBarStateController.setKeyguardRequested(true);
        this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(false);
        this.mPendingRemoteInputView = null;
        updateIsKeyguard();
        AssistManager assistManager = this.mAssistManager;
        if (assistManager != null) {
            assistManager.onLockscreenShown();
        }
    }

    public boolean hideKeyguard() {
        this.mStatusBarStateController.setKeyguardRequested(false);
        return updateIsKeyguard();
    }

    public boolean isFullScreenUserSwitcherState() {
        return this.mState == 3;
    }

    private boolean isAutomotive() {
        return this.mContext != null && this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateIsKeyguard() {
        boolean wakeAndUnlocking = this.mBiometricUnlockController.getMode() == 1;
        if (this.mScreenLifecycle == null && isAutomotive()) {
            Log.w(TAG, "updateIsKeyguard(): mScreenLifeCycle not set yet");
            this.mScreenLifecycle = (ScreenLifecycle) Dependency.get(ScreenLifecycle.class);
        }
        boolean keyguardForDozing = this.mDozingRequested && (!this.mDeviceInteractive || (isGoingToSleep() && (isScreenFullyOff() || this.mIsKeyguard)));
        boolean shouldBeKeyguard = (this.mStatusBarStateController.isKeyguardRequested() || keyguardForDozing) && !wakeAndUnlocking;
        if (keyguardForDozing) {
            updatePanelExpansionForKeyguard();
        }
        if (shouldBeKeyguard) {
            if (!isGoingToSleep() || this.mScreenLifecycle.getScreenState() != 3) {
                showKeyguardImpl();
            }
            return false;
        }
        return hideKeyguardImpl();
    }

    public void showKeyguardImpl() {
        this.mIsKeyguard = true;
        KeyguardMonitor keyguardMonitor = this.mKeyguardMonitor;
        if (keyguardMonitor != null && keyguardMonitor.isLaunchTransitionFadingAway()) {
            this.mNotificationPanel.animate().cancel();
            onLaunchTransitionFadingEnded();
        }
        this.mHandler.removeMessages(1003);
        UserSwitcherController userSwitcherController = this.mUserSwitcherController;
        if (userSwitcherController != null && userSwitcherController.useFullscreenUserSwitcher()) {
            this.mStatusBarStateController.setState(3);
        } else if (!this.mPulseExpansionHandler.isWakingToShadeLocked()) {
            this.mStatusBarStateController.setState(1);
        }
        updatePanelExpansionForKeyguard();
        NotificationEntry notificationEntry = this.mDraggedDownEntry;
        if (notificationEntry != null) {
            notificationEntry.setUserLocked(false);
            this.mDraggedDownEntry.notifyHeightChanged(false);
            this.mDraggedDownEntry = null;
        }
    }

    private void updatePanelExpansionForKeyguard() {
        if (this.mState == 1 && this.mBiometricUnlockController.getMode() != 1 && !this.mBouncerShowing) {
            instantExpandNotificationsPanel();
        } else if (this.mState == 3) {
            instantCollapseNotificationPanel();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLaunchTransitionFadingEnded() {
        this.mNotificationPanel.setAlpha(1.0f);
        this.mNotificationPanel.onAffordanceLaunchEnded();
        releaseGestureWakeLock();
        runLaunchTransitionEndRunnable();
        this.mKeyguardMonitor.setLaunchTransitionFadingAway(false);
        this.mPresenter.updateMediaMetaData(true, true);
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void addPostCollapseAction(Runnable r) {
        this.mPostCollapseRunnables.add(r);
    }

    public boolean isInLaunchTransition() {
        return this.mNotificationPanel.isLaunchTransitionRunning() || this.mNotificationPanel.isLaunchTransitionFinished();
    }

    public void fadeKeyguardAfterLaunchTransition(final Runnable beforeFading, Runnable endRunnable) {
        this.mHandler.removeMessages(1003);
        this.mLaunchTransitionEndRunnable = endRunnable;
        Runnable hideRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$urITUg_bdosu58crbZMswPW7bvo
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$fadeKeyguardAfterLaunchTransition$27$StatusBar(beforeFading);
            }
        };
        if (this.mNotificationPanel.isLaunchTransitionRunning()) {
            this.mNotificationPanel.setLaunchTransitionEndRunnable(hideRunnable);
        } else {
            hideRunnable.run();
        }
    }

    public /* synthetic */ void lambda$fadeKeyguardAfterLaunchTransition$27$StatusBar(Runnable beforeFading) {
        this.mKeyguardMonitor.setLaunchTransitionFadingAway(true);
        if (beforeFading != null) {
            beforeFading.run();
        }
        updateScrimController();
        this.mPresenter.updateMediaMetaData(false, true);
        this.mNotificationPanel.setAlpha(1.0f);
        this.mNotificationPanel.animate().alpha(0.0f).setStartDelay(100L).setDuration(300L).withLayer().withEndAction(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$GDSEpzokV1v2-uNGuP8V5K9Jrjw
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.onLaunchTransitionFadingEnded();
            }
        });
        this.mCommandQueue.appTransitionStarting(this.mDisplayId, SystemClock.uptimeMillis(), 120L, true);
    }

    public void fadeKeyguardWhilePulsing() {
        this.mNotificationPanel.animate().alpha(0.0f).setStartDelay(0L).setDuration(96L).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$KlfqlCeP0_t3Ji18QuG8__kx4ws
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$fadeKeyguardWhilePulsing$28$StatusBar();
            }
        }).start();
    }

    public /* synthetic */ void lambda$fadeKeyguardWhilePulsing$28$StatusBar() {
        hideKeyguard();
        this.mStatusBarKeyguardViewManager.onKeyguardFadedAway();
    }

    public void animateKeyguardUnoccluding() {
        this.mNotificationPanel.setExpandedFraction(0.0f);
        animateExpandNotificationsPanel();
    }

    public void startLaunchTransitionTimeout() {
        this.mHandler.sendEmptyMessageDelayed(1003, LAUNCH_TRANSITION_TIMEOUT_MS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLaunchTransitionTimeout() {
        Log.w(TAG, "Launch transition: Timeout!");
        this.mNotificationPanel.onAffordanceLaunchEnded();
        releaseGestureWakeLock();
        this.mNotificationPanel.resetViews(false);
    }

    private void runLaunchTransitionEndRunnable() {
        if (this.mLaunchTransitionEndRunnable != null) {
            Runnable r = this.mLaunchTransitionEndRunnable;
            this.mLaunchTransitionEndRunnable = null;
            r.run();
        }
    }

    public boolean hideKeyguardImpl() {
        this.mIsKeyguard = false;
        Trace.beginSection("StatusBar#hideKeyguard");
        boolean staying = this.mStatusBarStateController.leaveOpenOnKeyguardHide();
        if (!this.mStatusBarStateController.setState(0)) {
            this.mLockscreenUserManager.updatePublicMode();
        }
        if (this.mStatusBarStateController.leaveOpenOnKeyguardHide()) {
            if (!this.mStatusBarStateController.isKeyguardRequested()) {
                this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(false);
            }
            long delay = this.mKeyguardMonitor.calculateGoingToFullShadeDelay();
            this.mNotificationPanel.animateToFullShade(delay);
            NotificationEntry notificationEntry = this.mDraggedDownEntry;
            if (notificationEntry != null) {
                notificationEntry.setUserLocked(false);
                this.mDraggedDownEntry = null;
            }
            this.mNavigationBarController.disableAnimationsDuringHide(this.mDisplayId, delay);
        } else if (!this.mNotificationPanel.isCollapsing()) {
            instantCollapseNotificationPanel();
        }
        QSPanel qSPanel = this.mQSPanel;
        if (qSPanel != null) {
            qSPanel.refreshAllTiles();
        }
        this.mHandler.removeMessages(1003);
        releaseGestureWakeLock();
        this.mNotificationPanel.onAffordanceLaunchEnded();
        this.mNotificationPanel.animate().cancel();
        this.mNotificationPanel.setAlpha(1.0f);
        ViewGroupFadeHelper.reset(this.mNotificationPanel);
        updateScrimController();
        Trace.endSection();
        return staying;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseGestureWakeLock() {
        if (this.mGestureWakeLock.isHeld()) {
            this.mGestureWakeLock.release();
        }
    }

    public void keyguardGoingAway() {
        this.mKeyguardMonitor.notifyKeyguardGoingAway(true);
        this.mCommandQueue.appTransitionPending(this.mDisplayId, true);
    }

    public void setKeyguardFadingAway(long startTime, long delay, long fadeoutDuration, boolean isBypassFading) {
        this.mCommandQueue.appTransitionStarting(this.mDisplayId, (startTime + fadeoutDuration) - 120, 120L, true);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, fadeoutDuration > 0);
        this.mCommandQueue.appTransitionStarting(this.mDisplayId, startTime - 120, 120L, true);
        this.mKeyguardMonitor.notifyKeyguardFadingAway(delay, fadeoutDuration, isBypassFading);
    }

    public void finishKeyguardFadingAway() {
        this.mKeyguardMonitor.notifyKeyguardDoneFading();
        this.mScrimController.setExpansionAffectsAlpha(true);
    }

    protected void updateTheme() {
        boolean lockDarkText = this.mColorExtractor.getNeutralColors().supportsDarkText();
        int themeResId = lockDarkText ? R.style.Theme_SystemUI_Light : R.style.Theme_SystemUI;
        if (this.mContext.getThemeResId() != themeResId) {
            this.mContext.setTheme(themeResId);
            ((ConfigurationController) Dependency.get(ConfigurationController.class)).notifyThemeChanged();
        }
    }

    private void updateDozingState() {
        Trace.traceCounter(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, "dozing", this.mDozing ? 1 : 0);
        Trace.beginSection("StatusBar#updateDozingState");
        boolean sleepingFromKeyguard = this.mStatusBarKeyguardViewManager.isGoingToSleepVisibleNotOccluded();
        boolean animate = false;
        boolean wakeAndUnlock = this.mBiometricUnlockController.getMode() == 1;
        if ((!this.mDozing && this.mDozeServiceHost.shouldAnimateWakeup() && !wakeAndUnlock) || (this.mDozing && this.mDozeServiceHost.shouldAnimateScreenOff() && sleepingFromKeyguard)) {
            animate = true;
        }
        this.mNotificationPanel.setDozing(this.mDozing, animate, this.mWakeUpTouchLocation);
        updateQsExpansionEnabled();
        Trace.endSection();
    }

    public void userActivity() {
        if (this.mState == 1) {
            this.mKeyguardViewMediatorCallback.userActivity();
        }
    }

    public boolean interceptMediaKey(KeyEvent event) {
        return this.mState == 1 && this.mStatusBarKeyguardViewManager.interceptMediaKey(event);
    }

    protected boolean shouldUnlockOnMenuPressed() {
        return this.mDeviceInteractive && this.mState != 0 && this.mStatusBarKeyguardViewManager.shouldDismissOnMenuPressed();
    }

    public boolean onMenuPressed() {
        if (shouldUnlockOnMenuPressed()) {
            animateCollapsePanels(2, true);
            return true;
        }
        return false;
    }

    public void endAffordanceLaunch() {
        releaseGestureWakeLock();
        this.mNotificationPanel.onAffordanceLaunchEnded();
    }

    public boolean onBackPressed() {
        boolean isScrimmedBouncer = this.mScrimController.getState() == ScrimState.BOUNCER_SCRIMMED;
        if (this.mStatusBarKeyguardViewManager.onBackPressed(isScrimmedBouncer)) {
            if (!isScrimmedBouncer) {
                this.mNotificationPanel.expandWithoutQs();
            }
            return true;
        } else if (this.mNotificationPanel.isQsExpanded()) {
            if (this.mNotificationPanel.isQsDetailShowing()) {
                this.mNotificationPanel.closeQsDetail();
            } else {
                this.mNotificationPanel.animateCloseQs(false);
            }
            return true;
        } else {
            int i = this.mState;
            if (i != 1 && i != 2) {
                if (this.mNotificationPanel.canPanelBeCollapsed()) {
                    animateCollapsePanels();
                } else {
                    BubbleController bubbleController = this.mBubbleController;
                    if (bubbleController != null) {
                        bubbleController.performBackPressIfNeeded();
                    }
                }
                return true;
            }
            KeyguardUserSwitcher keyguardUserSwitcher = this.mKeyguardUserSwitcher;
            return keyguardUserSwitcher != null && keyguardUserSwitcher.hideIfNotSimple(true);
        }
    }

    public boolean onSpacePressed() {
        if (this.mDeviceInteractive && this.mState != 0) {
            animateCollapsePanels(2, true);
            return true;
        }
        return false;
    }

    private void showBouncerIfKeyguard() {
        int i = this.mState;
        if ((i == 1 || i == 2) && !this.mKeyguardViewMediator.isHiding()) {
            showBouncer(true);
        }
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void showBouncer(boolean scrimmed) {
        this.mStatusBarKeyguardViewManager.showBouncer(scrimmed);
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void instantExpandNotificationsPanel() {
        makeExpandedVisible(true);
        this.mNotificationPanel.expand(false);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, false);
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public boolean closeShadeIfOpen() {
        if (!this.mNotificationPanel.isFullyCollapsed()) {
            this.mCommandQueue.animateCollapsePanels(2, true);
            visibilityChanged(false);
            this.mAssistManager.hideAssist();
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void postOnShadeExpanded(final Runnable executable) {
        this.mNotificationPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.systemui.statusbar.phone.StatusBar.11
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (StatusBar.this.getStatusBarWindow().getHeight() != StatusBar.this.getStatusBarHeight()) {
                    StatusBar.this.mNotificationPanel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    StatusBar.this.mNotificationPanel.post(executable);
                }
            }
        });
    }

    private void instantCollapseNotificationPanel() {
        this.mNotificationPanel.instantCollapse();
        runPostCollapseRunnables();
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStatePreChange(int oldState, int newState) {
        if (this.mVisible && (newState == 2 || ((SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class)).goingToFullShade())) {
            clearNotificationEffects();
        }
        if (newState == 1) {
            this.mRemoteInputManager.onPanelCollapsed();
            maybeEscalateHeadsUp();
        }
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
        this.mState = newState;
        updateReportRejectedTouchVisibility();
        updateDozing();
        updateTheme();
        this.mNavigationBarController.touchAutoDim(this.mDisplayId);
        Trace.beginSection("StatusBar#updateKeyguardState");
        if (this.mState == 1) {
            this.mKeyguardIndicationController.setVisible(true);
            KeyguardUserSwitcher keyguardUserSwitcher = this.mKeyguardUserSwitcher;
            if (keyguardUserSwitcher != null) {
                keyguardUserSwitcher.setKeyguard(true, this.mStatusBarStateController.fromShadeLocked());
            }
            PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
            if (phoneStatusBarView != null) {
                phoneStatusBarView.removePendingHideExpandedRunnables();
            }
            View view = this.mAmbientIndicationContainer;
            if (view != null) {
                view.setVisibility(0);
            }
        } else {
            this.mKeyguardIndicationController.setVisible(false);
            KeyguardUserSwitcher keyguardUserSwitcher2 = this.mKeyguardUserSwitcher;
            if (keyguardUserSwitcher2 != null) {
                keyguardUserSwitcher2.setKeyguard(false, this.mStatusBarStateController.goingToFullShade() || this.mState == 2 || this.mStatusBarStateController.fromShadeLocked());
            }
            View view2 = this.mAmbientIndicationContainer;
            if (view2 != null) {
                view2.setVisibility(4);
            }
        }
        updateDozingState();
        checkBarModes();
        updateScrimController();
        this.mPresenter.updateMediaMetaData(false, this.mState != 1);
        updateKeyguardState();
        Trace.endSection();
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        Trace.beginSection("StatusBar#updateDozing");
        this.mDozing = isDozing;
        boolean dozingAnimated = this.mDozingRequested && DozeParameters.getInstance(this.mContext).shouldControlScreenOff();
        this.mNotificationPanel.resetViews(dozingAnimated);
        updateQsExpansionEnabled();
        this.mKeyguardViewMediator.setDozing(this.mDozing);
        this.mEntryManager.updateNotifications();
        updateDozingState();
        updateScrimController();
        updateReportRejectedTouchVisibility();
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDozing() {
        boolean dozing = (this.mDozingRequested && this.mState == 1) || this.mBiometricUnlockController.getMode() == 2;
        if (this.mBiometricUnlockController.getMode() == 1) {
            dozing = false;
        }
        this.mStatusBarStateController.setIsDozing(dozing);
    }

    private void updateKeyguardState() {
        KeyguardMonitor keyguardMonitor = this.mKeyguardMonitor;
        if (keyguardMonitor != null) {
            keyguardMonitor.notifyKeyguardState(this.mStatusBarKeyguardViewManager.isShowing(), this.mUnlockMethodCache.isMethodSecure(), this.mStatusBarKeyguardViewManager.isOccluded());
        }
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void onActivationReset() {
        this.mKeyguardIndicationController.hideTransientIndication();
    }

    public void onTrackingStarted() {
        runPostCollapseRunnables();
    }

    public void onClosingFinished() {
        runPostCollapseRunnables();
        if (!this.mPresenter.isPresenterFullyCollapsed()) {
            this.mStatusBarWindowController.setStatusBarFocusable(true);
        }
    }

    public void onUnlockHintStarted() {
        this.mFalsingManager.onUnlockHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R.string.keyguard_unlock);
    }

    public void onHintFinished() {
        this.mKeyguardIndicationController.hideTransientIndicationDelayed(1200L);
    }

    public void onCameraHintStarted() {
        this.mFalsingManager.onCameraHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R.string.camera_hint);
    }

    public void onVoiceAssistHintStarted() {
        this.mFalsingManager.onLeftAffordanceHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R.string.voice_hint);
    }

    public void onPhoneHintStarted() {
        this.mFalsingManager.onLeftAffordanceHintStarted();
        this.mKeyguardIndicationController.showTransientIndication(R.string.phone_hint);
    }

    public void onTrackingStopped(boolean expand) {
        int i = this.mState;
        if ((i == 1 || i == 2) && !expand && !this.mUnlockMethodCache.canSkipBouncer()) {
            showBouncer(false);
        }
    }

    public NavigationBarView getNavigationBarView() {
        return this.mNavigationBarController.getNavigationBarView(this.mDisplayId);
    }

    public KeyguardBottomAreaView getKeyguardBottomAreaView() {
        return this.mNotificationPanel.getKeyguardBottomAreaView();
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void goToLockedShade(View expandView) {
        if ((this.mDisabled2 & 4) != 0) {
            return;
        }
        int userId = this.mLockscreenUserManager.getCurrentUserId();
        NotificationEntry entry = null;
        if (expandView instanceof ExpandableNotificationRow) {
            entry = ((ExpandableNotificationRow) expandView).getEntry();
            entry.setUserExpanded(true, true);
            entry.setGroupExpansionChanging(true);
            if (entry.notification != null) {
                userId = entry.notification.getUserId();
            }
        }
        NotificationLockscreenUserManager notificationLockscreenUserManager = this.mLockscreenUserManager;
        boolean fullShadeNeedsBouncer = (notificationLockscreenUserManager.userAllowsPrivateNotificationsInPublic(notificationLockscreenUserManager.getCurrentUserId()) && this.mLockscreenUserManager.shouldShowLockscreenNotifications() && !this.mFalsingManager.shouldEnforceBouncer()) ? false : true;
        if (this.mKeyguardBypassController.getBypassEnabled()) {
            fullShadeNeedsBouncer = false;
        }
        if (this.mLockscreenUserManager.isLockscreenPublicMode(userId) && fullShadeNeedsBouncer) {
            this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
            showBouncerIfKeyguard();
            this.mDraggedDownEntry = entry;
            this.mPendingRemoteInputView = null;
            return;
        }
        this.mNotificationPanel.animateToFullShade(0L);
        this.mStatusBarStateController.setState(2);
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void goToKeyguard() {
        if (this.mState == 2) {
            this.mStatusBarStateController.setState(1);
        }
    }

    public void setBouncerShowing(boolean bouncerShowing) {
        this.mBouncerShowing = bouncerShowing;
        this.mKeyguardBypassController.setBouncerShowing(bouncerShowing);
        this.mPulseExpansionHandler.setBouncerShowing(bouncerShowing);
        this.mStatusBarWindow.setBouncerShowingScrimmed(isBouncerShowingScrimmed());
        PhoneStatusBarView phoneStatusBarView = this.mStatusBarView;
        if (phoneStatusBarView != null) {
            phoneStatusBarView.setBouncerShowing(bouncerShowing);
        }
        updateHideIconsForBouncer(true);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
        updateScrimController();
        if (!this.mBouncerShowing) {
            updatePanelExpansionForKeyguard();
        }
    }

    public void collapseShade() {
        if (this.mNotificationPanel.isTracking()) {
            this.mStatusBarWindow.cancelCurrentTouch();
        }
        if (this.mPanelExpanded && this.mState == 0) {
            animateCollapsePanels();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.StatusBar$12  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass12 implements WakefulnessLifecycle.Observer {
        AnonymousClass12() {
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onFinishedGoingToSleep() {
            StatusBar.this.mNotificationPanel.onAffordanceLaunchEnded();
            StatusBar.this.releaseGestureWakeLock();
            StatusBar.this.mLaunchCameraWhenFinishedWaking = false;
            StatusBar statusBar = StatusBar.this;
            statusBar.mDeviceInteractive = false;
            statusBar.mWakeUpComingFromTouch = false;
            StatusBar.this.mWakeUpTouchLocation = null;
            StatusBar.this.mVisualStabilityManager.setScreenOn(false);
            StatusBar.this.updateVisibleToUser();
            StatusBar.this.updateNotificationPanelTouchState();
            StatusBar.this.mStatusBarWindow.cancelCurrentTouch();
            if (StatusBar.this.mLaunchCameraOnFinishedGoingToSleep) {
                StatusBar.this.mLaunchCameraOnFinishedGoingToSleep = false;
                StatusBar.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$12$y9_RRyD4rDeCN3cFnbhrxNLuI7g
                    @Override // java.lang.Runnable
                    public final void run() {
                        StatusBar.AnonymousClass12.this.lambda$onFinishedGoingToSleep$0$StatusBar$12();
                    }
                });
            }
            StatusBar.this.updateIsKeyguard();
        }

        public /* synthetic */ void lambda$onFinishedGoingToSleep$0$StatusBar$12() {
            StatusBar statusBar = StatusBar.this;
            statusBar.onCameraLaunchGestureDetected(statusBar.mLastCameraLaunchSource);
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onStartedGoingToSleep() {
            StatusBar.this.updateNotificationPanelTouchState();
            StatusBar.this.notifyHeadsUpGoingToSleep();
            StatusBar.this.dismissVolumeDialog();
            StatusBar.this.mWakeUpCoordinator.setFullyAwake(false);
            StatusBar.this.mBypassHeadsUpNotifier.setFullyAwake(false);
            StatusBar.this.mKeyguardBypassController.onStartedGoingToSleep();
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onStartedWakingUp() {
            StatusBar statusBar = StatusBar.this;
            statusBar.mDeviceInteractive = true;
            statusBar.mWakeUpCoordinator.setWakingUp(true);
            if (!StatusBar.this.mKeyguardBypassController.getBypassEnabled()) {
                StatusBar.this.mHeadsUpManager.releaseAllImmediately();
            }
            StatusBar.this.mVisualStabilityManager.setScreenOn(true);
            StatusBar.this.updateVisibleToUser();
            StatusBar.this.updateIsKeyguard();
            StatusBar.this.mDozeServiceHost.stopDozing();
            StatusBar.this.updateNotificationPanelTouchState();
            StatusBar.this.mPulseExpansionHandler.onStartedWakingUp();
        }

        @Override // com.android.systemui.keyguard.WakefulnessLifecycle.Observer
        public void onFinishedWakingUp() {
            StatusBar.this.mWakeUpCoordinator.setFullyAwake(true);
            StatusBar.this.mBypassHeadsUpNotifier.setFullyAwake(true);
            StatusBar.this.mWakeUpCoordinator.setWakingUp(false);
            if (StatusBar.this.mLaunchCameraWhenFinishedWaking) {
                StatusBar.this.mNotificationPanel.launchCamera(false, StatusBar.this.mLastCameraLaunchSource);
                StatusBar.this.mLaunchCameraWhenFinishedWaking = false;
            }
            StatusBar.this.updateScrimController();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotificationPanelTouchState() {
        boolean goingToSleepWithoutAnimation = isGoingToSleep() && !DozeParameters.getInstance(this.mContext).shouldControlScreenOff();
        boolean disabled = !(this.mDeviceInteractive || this.mPulsing) || goingToSleepWithoutAnimation;
        this.mNotificationPanel.setTouchAndAnimationDisabled(disabled);
        this.mNotificationIconAreaController.setAnimationsEnabled(disabled ? false : true);
    }

    public int getWakefulnessState() {
        return this.mWakefulnessLifecycle.getWakefulness();
    }

    private void vibrateForCameraGesture() {
        this.mVibrator.vibrate(this.mCameraLaunchGestureVibePattern, -1);
    }

    public boolean isScreenFullyOff() {
        return this.mScreenLifecycle.getScreenState() == 0;
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void showScreenPinningRequest(int taskId) {
        if (this.mKeyguardMonitor.isShowing()) {
            return;
        }
        showScreenPinningRequest(taskId, true);
    }

    public void showScreenPinningRequest(int taskId, boolean allowCancel) {
        this.mScreenPinningRequest.showPrompt(taskId, allowCancel);
    }

    public boolean hasActiveNotifications() {
        return !this.mEntryManager.getNotificationData().getActiveNotifications().isEmpty();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void appTransitionCancelled(int displayId) {
        if (displayId == this.mDisplayId) {
            ((Divider) getComponent(Divider.class)).onAppTransitionFinished();
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void appTransitionFinished(int displayId) {
        if (displayId == this.mDisplayId) {
            ((Divider) getComponent(Divider.class)).onAppTransitionFinished();
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void onCameraLaunchGestureDetected(int source) {
        this.mLastCameraLaunchSource = source;
        if (isGoingToSleep()) {
            this.mLaunchCameraOnFinishedGoingToSleep = true;
            return;
        }
        if (!this.mNotificationPanel.canCameraGestureBeLaunched(this.mStatusBarKeyguardViewManager.isShowing() && (this.mExpandedVisible || this.mBouncerShowing))) {
            return;
        }
        if (!this.mDeviceInteractive) {
            PowerManager pm = (PowerManager) this.mContext.getSystemService(PowerManager.class);
            pm.wakeUp(SystemClock.uptimeMillis(), 5, "com.android.systemui:CAMERA_GESTURE");
        }
        vibrateForCameraGesture();
        if (source == 1) {
            Log.v(TAG, "Camera launch");
            this.mKeyguardUpdateMonitor.onCameraLaunched();
        }
        if (!this.mStatusBarKeyguardViewManager.isShowing()) {
            startActivityDismissingKeyguard(KeyguardBottomAreaView.INSECURE_CAMERA_INTENT, false, true, true, null, 0);
            return;
        }
        if (!this.mDeviceInteractive) {
            this.mGestureWakeLock.acquire(6000L);
        }
        if (isWakingUpOrAwake()) {
            if (this.mStatusBarKeyguardViewManager.isBouncerShowing()) {
                this.mStatusBarKeyguardViewManager.reset(true);
            }
            this.mNotificationPanel.launchCamera(this.mDeviceInteractive, source);
            updateScrimController();
            return;
        }
        this.mLaunchCameraWhenFinishedWaking = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isCameraAllowedByAdmin() {
        if (this.mDevicePolicyManager.getCameraDisabled(null, this.mLockscreenUserManager.getCurrentUserId())) {
            return false;
        }
        return !(this.mStatusBarKeyguardViewManager == null || (isKeyguardShowing() && isKeyguardSecure())) || (this.mDevicePolicyManager.getKeyguardDisabledFeatures(null, this.mLockscreenUserManager.getCurrentUserId()) & 2) == 0;
    }

    private boolean isGoingToSleep() {
        return this.mWakefulnessLifecycle.getWakefulness() == 3;
    }

    private boolean isWakingUpOrAwake() {
        return this.mWakefulnessLifecycle.getWakefulness() == 2 || this.mWakefulnessLifecycle.getWakefulness() == 1;
    }

    public void notifyBiometricAuthModeChanged() {
        updateDozing();
        updateScrimController();
        this.mStatusBarWindow.onBiometricAuthModeChanged(this.mBiometricUnlockController.isWakeAndUnlock(), this.mBiometricUnlockController.isBiometricUnlock());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateScrimController() {
        KeyguardMonitor keyguardMonitor;
        Trace.beginSection("StatusBar#updateScrimController");
        boolean unlocking = this.mBiometricUnlockController.isWakeAndUnlock() || ((keyguardMonitor = this.mKeyguardMonitor) != null && keyguardMonitor.isKeyguardFadingAway());
        this.mScrimController.setExpansionAffectsAlpha(true ^ this.mBiometricUnlockController.isBiometricUnlock());
        boolean launchingAffordanceWithPreview = this.mNotificationPanel.isLaunchingAffordanceWithPreview();
        this.mScrimController.setLaunchingAffordanceWithPreview(launchingAffordanceWithPreview);
        if (this.mBouncerShowing) {
            ScrimState state = this.mStatusBarKeyguardViewManager.bouncerNeedsScrimming() ? ScrimState.BOUNCER_SCRIMMED : ScrimState.BOUNCER;
            this.mScrimController.transitionTo(state);
        } else if (isInLaunchTransition() || this.mLaunchCameraWhenFinishedWaking || launchingAffordanceWithPreview) {
            this.mScrimController.transitionTo(ScrimState.UNLOCKED, this.mUnlockScrimCallback);
        } else if (this.mBrightnessMirrorVisible) {
            this.mScrimController.transitionTo(ScrimState.BRIGHTNESS_MIRROR);
        } else if (isPulsing()) {
            this.mScrimController.transitionTo(ScrimState.PULSING, this.mDozeScrimController.getScrimCallback());
        } else if (this.mDozing && !unlocking) {
            this.mScrimController.transitionTo(ScrimState.AOD);
        } else if (this.mIsKeyguard && !unlocking) {
            this.mScrimController.transitionTo(ScrimState.KEYGUARD);
        } else {
            BubbleController bubbleController = this.mBubbleController;
            if (bubbleController != null && bubbleController.isStackExpanded()) {
                this.mScrimController.transitionTo(ScrimState.BUBBLE_EXPANDED);
            } else {
                this.mScrimController.transitionTo(ScrimState.UNLOCKED, this.mUnlockScrimCallback);
            }
        }
        Trace.endSection();
    }

    public boolean isKeyguardShowing() {
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (statusBarKeyguardViewManager == null) {
            Slog.i(TAG, "isKeyguardShowing() called before startKeyguard(), returning true");
            return true;
        }
        return statusBarKeyguardViewManager.isShowing();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public final class DozeServiceHost implements DozeHost {
        private boolean mAnimateScreenOff;
        private boolean mAnimateWakeup;
        private boolean mIgnoreTouchWhilePulsing;
        private final ArrayList<DozeHost.Callback> mCallbacks = new ArrayList<>();
        @VisibleForTesting
        boolean mWakeLockScreenPerformsAuth = SystemProperties.getBoolean("persist.sysui.wake_performs_auth", true);

        DozeServiceHost() {
        }

        public String toString() {
            return "PSB.DozeServiceHost[mCallbacks=" + this.mCallbacks.size() + NavigationBarInflaterView.SIZE_MOD_END;
        }

        public void firePowerSaveChanged(boolean active) {
            Iterator<DozeHost.Callback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                DozeHost.Callback callback = it.next();
                callback.onPowerSaveChanged(active);
            }
        }

        public void fireNotificationPulse(final NotificationEntry entry) {
            Runnable pulseSupressedListener = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$DozeServiceHost$EU0Iwy8ToUOMfEU-xCMm_-jerTo
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBar.DozeServiceHost.this.lambda$fireNotificationPulse$0$StatusBar$DozeServiceHost(entry);
                }
            };
            Iterator<DozeHost.Callback> it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                DozeHost.Callback callback = it.next();
                callback.onNotificationAlerted(pulseSupressedListener);
            }
        }

        public /* synthetic */ void lambda$fireNotificationPulse$0$StatusBar$DozeServiceHost(NotificationEntry entry) {
            entry.setPulseSuppressed(true);
            StatusBar.this.mNotificationIconAreaController.updateAodNotificationIcons();
        }

        @Override // com.android.systemui.doze.DozeHost
        public void addCallback(DozeHost.Callback callback) {
            this.mCallbacks.add(callback);
        }

        @Override // com.android.systemui.doze.DozeHost
        public void removeCallback(DozeHost.Callback callback) {
            this.mCallbacks.remove(callback);
        }

        @Override // com.android.systemui.doze.DozeHost
        public void startDozing() {
            if (!StatusBar.this.mDozingRequested) {
                StatusBar.this.mDozingRequested = true;
                DozeLog.traceDozing(StatusBar.this.mContext, StatusBar.this.mDozing);
                StatusBar.this.updateDozing();
                StatusBar.this.updateIsKeyguard();
            }
        }

        @Override // com.android.systemui.doze.DozeHost
        public void pulseWhileDozing(final DozeHost.PulseCallback callback, int reason) {
            if (reason == 5) {
                StatusBar.this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 4, "com.android.systemui:LONG_PRESS");
                StatusBar.this.startAssist(new Bundle());
                return;
            }
            if (reason == 8) {
                StatusBar.this.mScrimController.setWakeLockScreenSensorActive(true);
            }
            if (reason == 6 && StatusBar.this.mStatusBarWindow != null) {
                StatusBar.this.mStatusBarWindow.suppressWakeUpGesture(true);
            }
            final boolean passiveAuthInterrupt = reason == 8 && this.mWakeLockScreenPerformsAuth;
            StatusBar.this.mPulsing = true;
            StatusBar.this.mDozeScrimController.pulse(new DozeHost.PulseCallback() { // from class: com.android.systemui.statusbar.phone.StatusBar.DozeServiceHost.1
                @Override // com.android.systemui.doze.DozeHost.PulseCallback
                public void onPulseStarted() {
                    callback.onPulseStarted();
                    StatusBar.this.updateNotificationPanelTouchState();
                    setPulsing(true);
                }

                @Override // com.android.systemui.doze.DozeHost.PulseCallback
                public void onPulseFinished() {
                    StatusBar.this.mPulsing = false;
                    callback.onPulseFinished();
                    StatusBar.this.updateNotificationPanelTouchState();
                    StatusBar.this.mScrimController.setWakeLockScreenSensorActive(false);
                    if (StatusBar.this.mStatusBarWindow != null) {
                        StatusBar.this.mStatusBarWindow.suppressWakeUpGesture(false);
                    }
                    setPulsing(false);
                }

                private void setPulsing(boolean pulsing) {
                    StatusBar.this.mStatusBarStateController.setPulsing(pulsing);
                    StatusBar.this.mStatusBarKeyguardViewManager.setPulsing(pulsing);
                    StatusBar.this.mKeyguardViewMediator.setPulsing(pulsing);
                    StatusBar.this.mNotificationPanel.setPulsing(pulsing);
                    StatusBar.this.mVisualStabilityManager.setPulsing(pulsing);
                    StatusBar.this.mStatusBarWindow.setPulsing(pulsing);
                    DozeServiceHost.this.mIgnoreTouchWhilePulsing = false;
                    if (StatusBar.this.mKeyguardUpdateMonitor != null && passiveAuthInterrupt) {
                        StatusBar.this.mKeyguardUpdateMonitor.onAuthInterruptDetected(pulsing);
                    }
                    StatusBar.this.updateScrimController();
                    StatusBar.this.mPulseExpansionHandler.setPulsing(pulsing);
                    StatusBar.this.mWakeUpCoordinator.setPulsing(pulsing);
                }
            }, reason);
            StatusBar.this.updateScrimController();
        }

        @Override // com.android.systemui.doze.DozeHost
        public void stopDozing() {
            if (StatusBar.this.mDozingRequested) {
                StatusBar.this.mDozingRequested = false;
                DozeLog.traceDozing(StatusBar.this.mContext, StatusBar.this.mDozing);
                StatusBar.this.updateDozing();
            }
        }

        @Override // com.android.systemui.doze.DozeHost
        public void onIgnoreTouchWhilePulsing(boolean ignore) {
            if (ignore != this.mIgnoreTouchWhilePulsing) {
                DozeLog.tracePulseTouchDisabledByProx(StatusBar.this.mContext, ignore);
            }
            this.mIgnoreTouchWhilePulsing = ignore;
            if (StatusBar.this.isDozing() && ignore) {
                StatusBar.this.mStatusBarWindow.cancelCurrentTouch();
            }
        }

        @Override // com.android.systemui.doze.DozeHost
        public void dozeTimeTick() {
            StatusBar.this.mNotificationPanel.dozeTimeTick();
            if (StatusBar.this.mAmbientIndicationContainer instanceof DozeReceiver) {
                ((DozeReceiver) StatusBar.this.mAmbientIndicationContainer).dozeTimeTick();
            }
        }

        @Override // com.android.systemui.doze.DozeHost
        public boolean isPowerSaveActive() {
            return StatusBar.this.mBatteryController.isAodPowerSave();
        }

        @Override // com.android.systemui.doze.DozeHost
        public boolean isPulsingBlocked() {
            return StatusBar.this.mBiometricUnlockController.getMode() == 1;
        }

        @Override // com.android.systemui.doze.DozeHost
        public boolean isProvisioned() {
            return StatusBar.this.mDeviceProvisionedController.isDeviceProvisioned() && StatusBar.this.mDeviceProvisionedController.isCurrentUserSetup();
        }

        @Override // com.android.systemui.doze.DozeHost
        public boolean isBlockingDoze() {
            if (StatusBar.this.mBiometricUnlockController.hasPendingAuthentication()) {
                Log.i(StatusBar.TAG, "Blocking AOD because fingerprint has authenticated");
                return true;
            }
            return false;
        }

        @Override // com.android.systemui.doze.DozeHost
        public void extendPulse(int reason) {
            if (reason == 8) {
                StatusBar.this.mScrimController.setWakeLockScreenSensorActive(true);
            }
            if (StatusBar.this.mDozeScrimController.isPulsing() && StatusBar.this.mHeadsUpManager.hasNotifications()) {
                StatusBar.this.mHeadsUpManager.extendHeadsUp();
            } else {
                StatusBar.this.mDozeScrimController.extendPulse();
            }
        }

        @Override // com.android.systemui.doze.DozeHost
        public void stopPulsing() {
            if (StatusBar.this.mDozeScrimController.isPulsing()) {
                StatusBar.this.mDozeScrimController.pulseOutNow();
            }
        }

        @Override // com.android.systemui.doze.DozeHost
        public void setAnimateWakeup(boolean animateWakeup) {
            if (StatusBar.this.mWakefulnessLifecycle.getWakefulness() == 2 || StatusBar.this.mWakefulnessLifecycle.getWakefulness() == 1) {
                return;
            }
            this.mAnimateWakeup = animateWakeup;
        }

        @Override // com.android.systemui.doze.DozeHost
        public void setAnimateScreenOff(boolean animateScreenOff) {
            this.mAnimateScreenOff = animateScreenOff;
        }

        @Override // com.android.systemui.doze.DozeHost
        public void onSlpiTap(float screenX, float screenY) {
            if (screenX > 0.0f && screenY > 0.0f && StatusBar.this.mAmbientIndicationContainer != null && StatusBar.this.mAmbientIndicationContainer.getVisibility() == 0) {
                StatusBar.this.mAmbientIndicationContainer.getLocationOnScreen(StatusBar.this.mTmpInt2);
                float viewX = screenX - StatusBar.this.mTmpInt2[0];
                float viewY = screenY - StatusBar.this.mTmpInt2[1];
                if (0.0f <= viewX && viewX <= StatusBar.this.mAmbientIndicationContainer.getWidth() && 0.0f <= viewY && viewY <= StatusBar.this.mAmbientIndicationContainer.getHeight()) {
                    dispatchTap(StatusBar.this.mAmbientIndicationContainer, viewX, viewY);
                }
            }
        }

        @Override // com.android.systemui.doze.DozeHost
        public void setDozeScreenBrightness(int value) {
            StatusBar.this.mStatusBarWindowController.setDozeScreenBrightness(value);
        }

        @Override // com.android.systemui.doze.DozeHost
        public void setAodDimmingScrim(float scrimOpacity) {
            StatusBar.this.mScrimController.setAodFrontScrimAlpha(scrimOpacity);
        }

        @Override // com.android.systemui.doze.DozeHost
        public void prepareForGentleWakeUp() {
            StatusBar.this.mScrimController.prepareForGentleWakeUp();
        }

        private void dispatchTap(View view, float x, float y) {
            long now = SystemClock.elapsedRealtime();
            dispatchTouchEvent(view, x, y, now, 0);
            dispatchTouchEvent(view, x, y, now, 1);
        }

        private void dispatchTouchEvent(View view, float x, float y, long now, int action) {
            MotionEvent ev = MotionEvent.obtain(now, now, action, x, y, 0);
            view.dispatchTouchEvent(ev);
            ev.recycle();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean shouldAnimateWakeup() {
            return this.mAnimateWakeup;
        }

        public boolean shouldAnimateScreenOff() {
            return this.mAnimateScreenOff;
        }
    }

    public boolean shouldIgnoreTouch() {
        return isDozing() && this.mDozeServiceHost.mIgnoreTouchWhilePulsing;
    }

    public boolean isDeviceInteractive() {
        return this.mDeviceInteractive;
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public void collapsePanel(boolean animate) {
        if (animate) {
            boolean willCollapse = collapsePanel();
            if (!willCollapse) {
                runPostCollapseRunnables();
            }
        } else if (!this.mPresenter.isPresenterFullyCollapsed()) {
            instantCollapseNotificationPanel();
            visibilityChanged(false);
        } else {
            runPostCollapseRunnables();
        }
    }

    @Override // com.android.systemui.statusbar.phone.ShadeController
    public boolean collapsePanel() {
        if (this.mNotificationPanel.isFullyCollapsed()) {
            return false;
        }
        animateCollapsePanels(2, true, true);
        visibilityChanged(false);
        return true;
    }

    public void setNotificationSnoozed(StatusBarNotification sbn, NotificationSwipeActionHelper.SnoozeOption snoozeOption) {
        if (snoozeOption.getSnoozeCriterion() != null) {
            this.mNotificationListener.snoozeNotification(sbn.getKey(), snoozeOption.getSnoozeCriterion().getId());
        } else {
            this.mNotificationListener.snoozeNotification(sbn.getKey(), snoozeOption.getMinutesToSnoozeFor() * 60 * 1000);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void toggleSplitScreen() {
        toggleSplitScreenMode(-1, -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void awakenDreams() {
        ((UiOffloadThread) Dependency.get(UiOffloadThread.class)).submit(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$1WMogreweqZK6K0lu_QDplFsRxA
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$awakenDreams$29$StatusBar();
            }
        });
    }

    public /* synthetic */ void lambda$awakenDreams$29$StatusBar() {
        try {
            this.mDreamManager.awaken();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void preloadRecentApps() {
        this.mHandler.removeMessages(MSG_PRELOAD_RECENT_APPS);
        this.mHandler.sendEmptyMessage(MSG_PRELOAD_RECENT_APPS);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void cancelPreloadRecentApps() {
        this.mHandler.removeMessages(1023);
        this.mHandler.sendEmptyMessage(1023);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void dismissKeyboardShortcutsMenu() {
        this.mHandler.removeMessages(MSG_DISMISS_KEYBOARD_SHORTCUTS_MENU);
        this.mHandler.sendEmptyMessage(MSG_DISMISS_KEYBOARD_SHORTCUTS_MENU);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void toggleKeyboardShortcutsMenu(int deviceId) {
        this.mHandler.removeMessages(1026);
        this.mHandler.obtainMessage(1026, deviceId, 0).sendToTarget();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void setTopAppHidesStatusBar(boolean topAppHidesStatusBar) {
        this.mTopHidesStatusBar = topAppHidesStatusBar;
        if (!topAppHidesStatusBar && this.mWereIconsJustHidden) {
            this.mWereIconsJustHidden = false;
            this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, true);
        }
        updateHideIconsForBouncer(true);
    }

    protected void toggleKeyboardShortcuts(int deviceId) {
        KeyboardShortcuts.toggle(this.mContext, deviceId);
    }

    protected void dismissKeyboardShortcuts() {
        KeyboardShortcuts.dismiss();
    }

    public void onPanelLaidOut() {
        updateKeyguardMaxNotifications();
    }

    public void updateKeyguardMaxNotifications() {
        if (this.mState == 1) {
            int maxBefore = this.mPresenter.getMaxNotificationsWhileLocked(false);
            int maxNotifications = this.mPresenter.getMaxNotificationsWhileLocked(true);
            if (maxBefore != maxNotifications) {
                this.mViewHierarchyManager.updateRowStates();
            }
        }
    }

    public void executeActionDismissingKeyguard(final Runnable action, boolean afterKeyguardGone) {
        if (this.mDeviceProvisionedController.isDeviceProvisioned()) {
            dismissKeyguardThenExecute(new ActivityStarter.OnDismissAction() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$5tYL8h68wV-hXi-SExxOPbxnmho
                @Override // com.android.systemui.plugins.ActivityStarter.OnDismissAction
                public final boolean onDismiss() {
                    return StatusBar.this.lambda$executeActionDismissingKeyguard$31$StatusBar(action);
                }
            }, afterKeyguardGone);
        }
    }

    public /* synthetic */ boolean lambda$executeActionDismissingKeyguard$31$StatusBar(final Runnable action) {
        new Thread(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$rrJIUwwFAGtQcqHBV4p5CzxIhOg
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.lambda$executeActionDismissingKeyguard$30(action);
            }
        }).start();
        return collapsePanel();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$executeActionDismissingKeyguard$30(Runnable action) {
        try {
            ActivityManager.getService().resumeAppSwitches();
        } catch (RemoteException e) {
        }
        action.run();
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    /* renamed from: startPendingIntentDismissingKeyguard */
    public void lambda$postStartActivityDismissingKeyguard$25$StatusBar(PendingIntent intent) {
        startPendingIntentDismissingKeyguard(intent, null);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startPendingIntentDismissingKeyguard(PendingIntent intent, Runnable intentSentUiThreadCallback) {
        startPendingIntentDismissingKeyguard(intent, intentSentUiThreadCallback, null);
    }

    @Override // com.android.systemui.plugins.ActivityStarter
    public void startPendingIntentDismissingKeyguard(final PendingIntent intent, final Runnable intentSentUiThreadCallback, final View associatedView) {
        boolean afterKeyguardGone = intent.isActivity() && this.mActivityIntentHelper.wouldLaunchResolverActivity(intent.getIntent(), this.mLockscreenUserManager.getCurrentUserId());
        executeActionDismissingKeyguard(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBar$mKzh129ZNRMAmJIVxc67nbFDtvE
            @Override // java.lang.Runnable
            public final void run() {
                StatusBar.this.lambda$startPendingIntentDismissingKeyguard$32$StatusBar(intent, associatedView, intentSentUiThreadCallback);
            }
        }, afterKeyguardGone);
    }

    public /* synthetic */ void lambda$startPendingIntentDismissingKeyguard$32$StatusBar(PendingIntent intent, View associatedView, Runnable intentSentUiThreadCallback) {
        try {
            intent.send(null, 0, null, null, null, null, getActivityOptions(this.mActivityLaunchAnimator.getLaunchAnimation(associatedView, this.mShadeController.isOccluded())));
        } catch (PendingIntent.CanceledException e) {
            Log.w(TAG, "Sending intent failed: " + e);
        }
        if (intent.isActivity()) {
            this.mAssistManager.hideAssist();
        }
        if (intentSentUiThreadCallback != null) {
            postOnUiThread(intentSentUiThreadCallback);
        }
    }

    private void postOnUiThread(Runnable runnable) {
        this.mMainThreadHandler.post(runnable);
    }

    public static Bundle getActivityOptions(RemoteAnimationAdapter animationAdapter) {
        ActivityOptions options;
        if (animationAdapter != null) {
            options = ActivityOptions.makeRemoteAnimation(animationAdapter);
        } else {
            options = ActivityOptions.makeBasic();
        }
        options.setLaunchWindowingMode(4);
        return options.toBundle();
    }

    protected void visibilityChanged(boolean visible) {
        if (this.mVisible != visible) {
            this.mVisible = visible;
            if (!visible) {
                this.mGutsManager.closeAndSaveGuts(true, true, true, -1, -1, true);
            }
        }
        updateVisibleToUser();
    }

    protected void updateVisibleToUser() {
        boolean oldVisibleToUser = this.mVisibleToUser;
        this.mVisibleToUser = this.mVisible && this.mDeviceInteractive;
        boolean z = this.mVisibleToUser;
        if (oldVisibleToUser != z) {
            handleVisibleToUserChanged(z);
        }
    }

    public void clearNotificationEffects() {
        try {
            this.mBarService.clearNotificationEffects();
        } catch (RemoteException e) {
        }
    }

    protected void notifyHeadsUpGoingToSleep() {
        maybeEscalateHeadsUp();
    }

    public boolean isBouncerShowing() {
        return this.mBouncerShowing;
    }

    public boolean isBouncerShowingScrimmed() {
        return isBouncerShowing() && this.mStatusBarKeyguardViewManager.bouncerNeedsScrimming();
    }

    public static PackageManager getPackageManagerForUser(Context context, int userId) {
        Context contextForUser = context;
        if (userId >= 0) {
            try {
                contextForUser = context.createPackageContextAsUser(context.getPackageName(), 4, new UserHandle(userId));
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return contextForUser.getPackageManager();
    }

    public boolean isKeyguardSecure() {
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = this.mStatusBarKeyguardViewManager;
        if (statusBarKeyguardViewManager == null) {
            Slog.w(TAG, "isKeyguardSecure() called before startKeyguard(), returning false", new Throwable());
            return false;
        }
        return statusBarKeyguardViewManager.isSecure();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void showAssistDisclosure() {
        AssistManager assistManager = this.mAssistManager;
        if (assistManager != null) {
            assistManager.showDisclosure();
        }
    }

    public NotificationPanelView getPanel() {
        return this.mNotificationPanel;
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void startAssist(Bundle args) {
        AssistManager assistManager = this.mAssistManager;
        if (assistManager != null) {
            assistManager.startAssist(args);
        }
    }

    public NotificationGutsManager getGutsManager() {
        return this.mGutsManager;
    }

    public int getStatusBarMode() {
        return this.mStatusBarMode;
    }
}
