package com.android.systemui.statusbar.phone;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.Lifecycle;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.LatencyTracker;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.Recents;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.ContextualButton;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.KeyButtonView;
import com.android.systemui.util.LifecycleFragment;
import com.xiaopeng.systemui.helper.WindowHelper;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class NavigationBarFragment extends LifecycleFragment implements CommandQueue.Callbacks, NavigationModeController.ModeChangedListener, AutoHideElement {
    private static final long AUTODIM_TIMEOUT_MS = 2250;
    private static final boolean DEBUG = false;
    private static final String EXTRA_DISABLE2_STATE = "disabled2_state";
    private static final String EXTRA_DISABLE_STATE = "disabled_state";
    private static final String EXTRA_SYSTEM_UI_VISIBILITY = "system_ui_visibility";
    private static final int LOCK_TO_APP_GESTURE_TOLERENCE = 200;
    public static final String TAG = "NavigationBar";
    private AccessibilityManager mAccessibilityManager;
    private final AccessibilityManagerWrapper mAccessibilityManagerWrapper;
    protected final AssistManager mAssistManager;
    private boolean mAssistantAvailable;
    private AutoHideController mAutoHideController;
    private CommandQueue mCommandQueue;
    private ContentResolver mContentResolver;
    private final DeviceProvisionedController mDeviceProvisionedController;
    private int mDisabledFlags1;
    private int mDisabledFlags2;
    @VisibleForTesting
    public int mDisplayId;
    private Divider mDivider;
    public boolean mHomeBlockedThisTouch;
    private boolean mIsOnDefaultDisplay;
    private long mLastLockToAppLongPress;
    private int mLayoutDirection;
    private LightBarController mLightBarController;
    private Locale mLocale;
    private MagnificationContentObserver mMagnificationObserver;
    private final MetricsLogger mMetricsLogger;
    private int mNavBarMode;
    private int mNavigationBarMode;
    private final NavigationModeController mNavigationModeController;
    private OverviewProxyService mOverviewProxyService;
    private Recents mRecents;
    private ScreenDecorations mScreenDecorations;
    private StatusBar mStatusBar;
    private final StatusBarStateController mStatusBarStateController;
    private WindowManager mWindowManager;
    protected NavigationBarView mNavigationBarView = null;
    private int mNavigationBarWindowState = 0;
    private int mNavigationIconHints = 0;
    private int mSystemUiVisibility = 0;
    private Handler mHandler = (Handler) Dependency.get(Dependency.MAIN_HANDLER);
    private final OverviewProxyService.OverviewProxyListener mOverviewProxyListener = new OverviewProxyService.OverviewProxyListener() { // from class: com.android.systemui.statusbar.phone.NavigationBarFragment.1
        {
            NavigationBarFragment.this = this;
        }

        @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
        public void onConnectionChanged(boolean isConnected) {
            NavigationBarFragment.this.mNavigationBarView.updateStates();
            NavigationBarFragment.this.updateScreenPinningGestures();
            if (isConnected) {
                NavigationBarFragment navigationBarFragment = NavigationBarFragment.this;
                navigationBarFragment.sendAssistantAvailability(navigationBarFragment.mAssistantAvailable);
            }
        }

        @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
        public void onQuickStepStarted() {
            NavigationBarFragment.this.mNavigationBarView.getRotationButtonController().setRotateSuggestionButtonState(false);
            NavigationBarFragment.this.mStatusBar.collapsePanel(true);
        }

        @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
        public void startAssistant(Bundle bundle) {
            NavigationBarFragment.this.mAssistManager.startAssist(bundle);
        }

        @Override // com.android.systemui.recents.OverviewProxyService.OverviewProxyListener
        public void onNavBarButtonAlphaChanged(float alpha, boolean animate) {
            ButtonDispatcher buttonDispatcher = null;
            if (!QuickStepContract.isSwipeUpMode(NavigationBarFragment.this.mNavBarMode)) {
                if (QuickStepContract.isGesturalMode(NavigationBarFragment.this.mNavBarMode)) {
                    buttonDispatcher = NavigationBarFragment.this.mNavigationBarView.getHomeHandle();
                }
            } else {
                buttonDispatcher = NavigationBarFragment.this.mNavigationBarView.getBackButton();
            }
            if (buttonDispatcher != null) {
                buttonDispatcher.setVisibility(alpha > 0.0f ? 0 : 4);
                buttonDispatcher.setAlpha(alpha, animate);
            }
        }
    };
    private final ContextualButton.ContextButtonListener mRotationButtonListener = new ContextualButton.ContextButtonListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$xnm4oWC06-iZWq-zBbKv8ubGVFU
        @Override // com.android.systemui.statusbar.phone.ContextualButton.ContextButtonListener
        public final void onVisibilityChanged(ContextualButton contextualButton, boolean z) {
            NavigationBarFragment.this.lambda$new$0$NavigationBarFragment(contextualButton, z);
        }
    };
    private final Runnable mAutoDim = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$Wf_FUQzkbSdMD9hXKJaXOD_rVSY
        @Override // java.lang.Runnable
        public final void run() {
            NavigationBarFragment.this.lambda$new$1$NavigationBarFragment();
        }
    };
    private final ContentObserver mAssistContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.systemui.statusbar.phone.NavigationBarFragment.2
        {
            NavigationBarFragment.this = this;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            boolean available = NavigationBarFragment.this.mAssistManager.getAssistInfoForUser(-2) != null;
            if (NavigationBarFragment.this.mAssistantAvailable != available) {
                NavigationBarFragment.this.sendAssistantAvailability(available);
                NavigationBarFragment.this.mAssistantAvailable = available;
            }
        }
    };
    private final AccessibilityManager.AccessibilityServicesStateChangeListener mAccessibilityListener = new AccessibilityManager.AccessibilityServicesStateChangeListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$dxES00kAyC8r2RmY9FwTYgUhoj8
        public final void onAccessibilityServicesStateChanged(AccessibilityManager accessibilityManager) {
            NavigationBarFragment.this.updateAccessibilityServicesState(accessibilityManager);
        }
    };
    private final Consumer<Integer> mRotationWatcher = new Consumer() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$JVziusX7tSv19aMDDuLOI-SWKI8
        @Override // java.util.function.Consumer
        public final void accept(Object obj) {
            NavigationBarFragment.this.lambda$new$4$NavigationBarFragment((Integer) obj);
        }
    };
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.phone.NavigationBarFragment.3
        {
            NavigationBarFragment.this = this;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_OFF".equals(action) || "android.intent.action.SCREEN_ON".equals(action)) {
                NavigationBarFragment.this.notifyNavigationBarScreenOn();
                if ("android.intent.action.SCREEN_ON".equals(action)) {
                    if (NavBarTintController.isEnabled(NavigationBarFragment.this.getContext(), NavigationBarFragment.this.mNavBarMode)) {
                        NavigationBarFragment.this.mNavigationBarView.getTintController().start();
                    }
                } else {
                    NavigationBarFragment.this.mNavigationBarView.getTintController().stop();
                }
            }
            if ("android.intent.action.USER_SWITCHED".equals(action)) {
                NavigationBarFragment navigationBarFragment = NavigationBarFragment.this;
                navigationBarFragment.updateAccessibilityServicesState(navigationBarFragment.mAccessibilityManager);
            }
        }
    };

    public /* synthetic */ void lambda$new$0$NavigationBarFragment(ContextualButton button, boolean visible) {
        if (visible) {
            this.mAutoHideController.touchAutoHide();
        }
    }

    public /* synthetic */ void lambda$new$1$NavigationBarFragment() {
        getBarTransitions().setAutoDim(true);
    }

    @Inject
    public NavigationBarFragment(AccessibilityManagerWrapper accessibilityManagerWrapper, DeviceProvisionedController deviceProvisionedController, MetricsLogger metricsLogger, AssistManager assistManager, OverviewProxyService overviewProxyService, NavigationModeController navigationModeController, StatusBarStateController statusBarStateController) {
        this.mNavBarMode = 0;
        this.mAccessibilityManagerWrapper = accessibilityManagerWrapper;
        this.mDeviceProvisionedController = deviceProvisionedController;
        this.mStatusBarStateController = statusBarStateController;
        this.mMetricsLogger = metricsLogger;
        this.mAssistManager = assistManager;
        this.mAssistantAvailable = this.mAssistManager.getAssistInfoForUser(-2) != null;
        this.mOverviewProxyService = overviewProxyService;
        this.mNavigationModeController = navigationModeController;
        this.mNavBarMode = navigationModeController.addListener(this);
    }

    @Override // com.android.systemui.util.LifecycleFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(getContext(), CommandQueue.class);
        this.mCommandQueue.observe(getLifecycle(), (Lifecycle) this);
        this.mStatusBar = (StatusBar) SysUiServiceProvider.getComponent(getContext(), StatusBar.class);
        this.mRecents = (Recents) SysUiServiceProvider.getComponent(getContext(), Recents.class);
        this.mDivider = (Divider) SysUiServiceProvider.getComponent(getContext(), Divider.class);
        this.mWindowManager = (WindowManager) getContext().getSystemService(WindowManager.class);
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService(AccessibilityManager.class);
        this.mContentResolver = getContext().getContentResolver();
        this.mMagnificationObserver = new MagnificationContentObserver(getContext().getMainThreadHandler());
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_display_magnification_navbar_enabled"), false, this.mMagnificationObserver, -1);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("assistant"), false, this.mAssistContentObserver, -1);
        if (savedInstanceState != null) {
            this.mDisabledFlags1 = savedInstanceState.getInt(EXTRA_DISABLE_STATE, 0);
            this.mDisabledFlags2 = savedInstanceState.getInt(EXTRA_DISABLE2_STATE, 0);
            this.mSystemUiVisibility = savedInstanceState.getInt(EXTRA_SYSTEM_UI_VISIBILITY, 0);
        }
        this.mAccessibilityManagerWrapper.addCallback(this.mAccessibilityListener);
        this.mCommandQueue.recomputeDisableFlags(this.mDisplayId, false);
    }

    @Override // com.android.systemui.util.LifecycleFragment, android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mNavigationModeController.removeListener(this);
        this.mAccessibilityManagerWrapper.removeCallback(this.mAccessibilityListener);
        this.mContentResolver.unregisterContentObserver(this.mMagnificationObserver);
        this.mContentResolver.unregisterContentObserver(this.mAssistContentObserver);
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_bar, container, false);
    }

    @Override // android.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mNavigationBarView = (NavigationBarView) view;
        Display display = view.getDisplay();
        if (display != null) {
            this.mDisplayId = display.getDisplayId();
            this.mIsOnDefaultDisplay = this.mDisplayId == 0;
        }
        this.mNavigationBarView.setComponents(this.mStatusBar.getPanel(), this.mAssistManager);
        this.mNavigationBarView.setDisabledFlags(this.mDisabledFlags1);
        this.mNavigationBarView.setOnVerticalChangedListener(new NavigationBarView.OnVerticalChangedListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$eFJm5m1txtISSi8Cx3m3pc8Nvjw
            @Override // com.android.systemui.statusbar.phone.NavigationBarView.OnVerticalChangedListener
            public final void onVerticalChanged(boolean z) {
                NavigationBarFragment.this.onVerticalChanged(z);
            }
        });
        this.mNavigationBarView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$X9JO9eLzlFoQkYf8XrZG-l2EMsk
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                boolean onNavigationTouch;
                onNavigationTouch = NavigationBarFragment.this.onNavigationTouch(view2, motionEvent);
                return onNavigationTouch;
            }
        });
        if (savedInstanceState != null) {
            this.mNavigationBarView.getLightTransitionsController().restoreState(savedInstanceState);
        }
        this.mNavigationBarView.setNavigationIconHints(this.mNavigationIconHints);
        this.mNavigationBarView.setWindowVisible(isNavBarWindowVisible());
        prepareNavigationBarView();
        checkNavBarModes();
        IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.USER_SWITCHED");
        getContext().registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, filter, null, null);
        notifyNavigationBarScreenOn();
        this.mOverviewProxyService.addCallback(this.mOverviewProxyListener);
        updateSystemUiStateFlags(-1);
        if (this.mIsOnDefaultDisplay) {
            this.mNavigationBarView.getRotateSuggestionButton().setListener(this.mRotationButtonListener);
            RotationButtonController rotationButtonController = this.mNavigationBarView.getRotationButtonController();
            rotationButtonController.addRotationCallback(this.mRotationWatcher);
            if (display != null && rotationButtonController.isRotationLocked()) {
                rotationButtonController.setRotationLockedAtAngle(display.getRotation());
            }
        } else {
            this.mDisabledFlags2 |= 16;
        }
        setDisabled2Flags(this.mDisabledFlags2);
        this.mScreenDecorations = (ScreenDecorations) SysUiServiceProvider.getComponent(getContext(), ScreenDecorations.class);
        getBarTransitions().addDarkIntensityListener(this.mScreenDecorations);
    }

    @Override // android.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            navigationBarView.getBarTransitions().removeDarkIntensityListener(this.mScreenDecorations);
            this.mNavigationBarView.getBarTransitions().destroy();
            this.mNavigationBarView.getLightTransitionsController().destroy(getContext());
        }
        this.mOverviewProxyService.removeCallback(this.mOverviewProxyListener);
        getContext().unregisterReceiver(this.mBroadcastReceiver);
    }

    @Override // android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_DISABLE_STATE, this.mDisabledFlags1);
        outState.putInt(EXTRA_DISABLE2_STATE, this.mDisabledFlags2);
        outState.putInt(EXTRA_SYSTEM_UI_VISIBILITY, this.mSystemUiVisibility);
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            navigationBarView.getLightTransitionsController().saveState(outState);
        }
    }

    @Override // android.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Locale locale = getContext().getResources().getConfiguration().locale;
        int ld = TextUtils.getLayoutDirectionFromLocale(locale);
        if (!locale.equals(this.mLocale) || ld != this.mLayoutDirection) {
            this.mLocale = locale;
            this.mLayoutDirection = ld;
            refreshLayout(ld);
        }
        repositionNavigationBar();
    }

    @Override // android.app.Fragment
    public void dump(String prefix, FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mNavigationBarView != null) {
            pw.print("  mNavigationBarWindowState=");
            pw.println(StatusBarManager.windowStateToString(this.mNavigationBarWindowState));
            pw.print("  mNavigationBarMode=");
            pw.println(BarTransitions.modeToString(this.mNavigationBarMode));
            StatusBar.dumpBarTransitions(pw, "mNavigationBarView", this.mNavigationBarView.getBarTransitions());
        }
        pw.print("  mNavigationBarView=");
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView == null) {
            pw.println("null");
        } else {
            navigationBarView.dump(fd, pw, args);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void setImeWindowStatus(int displayId, IBinder token, int vis, int backDisposition, boolean showImeSwitcher) {
        int hints;
        if (displayId != this.mDisplayId) {
            return;
        }
        boolean imeShown = (vis & 2) != 0;
        int hints2 = this.mNavigationIconHints;
        if (backDisposition == 0 || backDisposition == 1 || backDisposition == 2) {
            if (imeShown) {
                hints2 |= 1;
            } else {
                hints2 &= -2;
            }
        } else if (backDisposition == 3) {
            hints2 &= -2;
        }
        if (showImeSwitcher) {
            hints = hints2 | 2;
        } else {
            hints = hints2 & (-3);
        }
        if (hints == this.mNavigationIconHints) {
            return;
        }
        this.mNavigationIconHints = hints;
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            navigationBarView.setNavigationIconHints(hints);
        }
        checkBarModes();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void setWindowState(int displayId, int window, int state) {
        if (displayId == this.mDisplayId && this.mNavigationBarView != null && window == 2 && this.mNavigationBarWindowState != state) {
            this.mNavigationBarWindowState = state;
            updateSystemUiStateFlags(-1);
            this.mNavigationBarView.setWindowVisible(isNavBarWindowVisible());
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void onRotationProposal(int rotation, boolean isValid) {
        int winRotation = this.mNavigationBarView.getDisplay().getRotation();
        boolean rotateSuggestionsDisabled = RotationButtonController.hasDisable2RotateSuggestionFlag(this.mDisabledFlags2);
        RotationButtonController rotationButtonController = this.mNavigationBarView.getRotationButtonController();
        rotationButtonController.getRotationButton();
        if (rotateSuggestionsDisabled) {
            return;
        }
        rotationButtonController.onRotationProposal(rotation, winRotation, isValid);
    }

    public void restoreSystemUiVisibilityState() {
        int barMode = computeBarMode(0, this.mSystemUiVisibility);
        if (barMode != -1) {
            this.mNavigationBarMode = barMode;
        }
        checkNavBarModes();
        this.mAutoHideController.touchAutoHide();
        this.mLightBarController.onNavigationVisibilityChanged(this.mSystemUiVisibility, 0, true, this.mNavigationBarMode, false);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void setSystemUiVisibility(int displayId, int vis, int fullscreenStackVis, int dockedStackVis, int mask, Rect fullscreenStackBounds, Rect dockedStackBounds, boolean navbarColorManagedByIme) {
        boolean nbModeChanged;
        if (displayId != this.mDisplayId) {
            return;
        }
        int oldVal = this.mSystemUiVisibility;
        int newVal = ((~mask) & oldVal) | (vis & mask);
        int diff = newVal ^ oldVal;
        if (diff == 0) {
            nbModeChanged = false;
        } else {
            this.mSystemUiVisibility = newVal;
            int nbMode = getView() == null ? -1 : computeBarMode(oldVal, newVal);
            boolean nbModeChanged2 = nbMode != -1;
            if (nbModeChanged2) {
                int i = this.mNavigationBarMode;
                if (i != nbMode) {
                    if (i == 4 || i == 6) {
                        this.mNavigationBarView.hideRecentsOnboarding();
                    }
                    this.mNavigationBarMode = nbMode;
                    checkNavBarModes();
                }
                this.mAutoHideController.touchAutoHide();
            }
            nbModeChanged = nbModeChanged2;
        }
        this.mLightBarController.onNavigationVisibilityChanged(vis, mask, nbModeChanged, this.mNavigationBarMode, navbarColorManagedByIme);
    }

    private int computeBarMode(int oldVis, int newVis) {
        int oldMode = barMode(oldVis);
        int newMode = barMode(newVis);
        if (oldMode == newMode) {
            return -1;
        }
        return newMode;
    }

    private int barMode(int vis) {
        if ((134217728 & vis) != 0) {
            return 1;
        }
        if ((Integer.MIN_VALUE & vis) != 0) {
            return 2;
        }
        if ((vis & 134217729) == 134217729) {
            return 6;
        }
        if ((32768 & vis) != 0) {
            return 4;
        }
        if ((vis & 1) != 0) {
            return 3;
        }
        return 0;
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void disable(int displayId, int state1, int state2, boolean animate) {
        int masked2;
        if (displayId != this.mDisplayId) {
            return;
        }
        int masked = 56623104 & state1;
        if (masked != this.mDisabledFlags1) {
            this.mDisabledFlags1 = masked;
            NavigationBarView navigationBarView = this.mNavigationBarView;
            if (navigationBarView != null) {
                navigationBarView.setDisabledFlags(state1);
            }
            updateScreenPinningGestures();
        }
        if (this.mIsOnDefaultDisplay && (masked2 = state2 & 16) != this.mDisabledFlags2) {
            this.mDisabledFlags2 = masked2;
            setDisabled2Flags(masked2);
        }
    }

    private void setDisabled2Flags(int state2) {
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            navigationBarView.getRotationButtonController().onDisable2FlagChanged(state2);
        }
    }

    private void refreshLayout(int layoutDirection) {
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            navigationBarView.setLayoutDirection(layoutDirection);
        }
    }

    private boolean shouldDisableNavbarGestures() {
        return (this.mDeviceProvisionedController.isDeviceProvisioned() && (this.mDisabledFlags1 & 33554432) == 0) ? false : true;
    }

    private void repositionNavigationBar() {
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView == null || !navigationBarView.isAttachedToWindow()) {
            return;
        }
        prepareNavigationBarView();
        this.mWindowManager.updateViewLayout((View) this.mNavigationBarView.getParent(), ((View) this.mNavigationBarView.getParent()).getLayoutParams());
    }

    public void updateScreenPinningGestures() {
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView == null) {
            return;
        }
        boolean recentsVisible = navigationBarView.isRecentsButtonVisible();
        ButtonDispatcher backButton = this.mNavigationBarView.getBackButton();
        if (recentsVisible) {
            backButton.setOnLongClickListener(new $$Lambda$NavigationBarFragment$dtGeJfWz2E4_XAoQgX8peIw4kU8(this));
        } else {
            backButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$oZtQ9jE1OTI8AtitIxsN6ETT4sc
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    boolean onLongPressBackHome;
                    onLongPressBackHome = NavigationBarFragment.this.onLongPressBackHome(view);
                    return onLongPressBackHome;
                }
            });
        }
    }

    public void notifyNavigationBarScreenOn() {
        this.mNavigationBarView.updateNavButtonIcons();
    }

    private void prepareNavigationBarView() {
        this.mNavigationBarView.reorient();
        ButtonDispatcher recentsButton = this.mNavigationBarView.getRecentsButton();
        recentsButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$0mmLLxBq7RxotphHQB_RtYb4SpQ
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NavigationBarFragment.this.onRecentsClick(view);
            }
        });
        recentsButton.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$VEqqEZFjg0f3lWOW2BJ66Oo_2aE
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean onRecentsTouch;
                onRecentsTouch = NavigationBarFragment.this.onRecentsTouch(view, motionEvent);
                return onRecentsTouch;
            }
        });
        recentsButton.setLongClickable(true);
        recentsButton.setOnLongClickListener(new $$Lambda$NavigationBarFragment$dtGeJfWz2E4_XAoQgX8peIw4kU8(this));
        ButtonDispatcher backButton = this.mNavigationBarView.getBackButton();
        backButton.setLongClickable(true);
        ButtonDispatcher homeButton = this.mNavigationBarView.getHomeButton();
        homeButton.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$y_1OHmWTpLl8uCcO3A0Am620g94
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean onHomeTouch;
                onHomeTouch = NavigationBarFragment.this.onHomeTouch(view, motionEvent);
                return onHomeTouch;
            }
        });
        homeButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$8vcstZEv0YyG7EUTK_UrsNSFXRo
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return NavigationBarFragment.this.onHomeLongClick(view);
            }
        });
        ButtonDispatcher accessibilityButton = this.mNavigationBarView.getAccessibilityButton();
        accessibilityButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$Ylizyb5K7ZQr77j1Ehc8SUjcI6E
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NavigationBarFragment.this.onAccessibilityClick(view);
            }
        });
        accessibilityButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$RtBTLxltRKo37YrTKiaCXCxwRDg
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                boolean onAccessibilityLongClick;
                onAccessibilityLongClick = NavigationBarFragment.this.onAccessibilityLongClick(view);
                return onAccessibilityLongClick;
            }
        });
        updateAccessibilityServicesState(this.mAccessibilityManager);
        updateScreenPinningGestures();
    }

    public boolean onHomeTouch(View v, MotionEvent event) {
        if (!this.mHomeBlockedThisTouch || event.getActionMasked() == 0) {
            int action = event.getAction();
            if (action == 0) {
                this.mHomeBlockedThisTouch = false;
                TelecomManager telecomManager = (TelecomManager) getContext().getSystemService(TelecomManager.class);
                if (telecomManager != null && telecomManager.isRinging() && this.mStatusBar.isKeyguardShowing()) {
                    Log.i(TAG, "Ignoring HOME; there's a ringing incoming call. No heads up");
                    this.mHomeBlockedThisTouch = true;
                    return true;
                }
            } else if (action == 1 || action == 3) {
                this.mStatusBar.awakenDreams();
            }
            return false;
        }
        return true;
    }

    public void onVerticalChanged(boolean isVertical) {
        this.mStatusBar.setQsScrimEnabled(!isVertical);
    }

    public boolean onNavigationTouch(View v, MotionEvent event) {
        this.mAutoHideController.checkUserAutoHide(event);
        return false;
    }

    @VisibleForTesting
    public boolean onHomeLongClick(View v) {
        if (!this.mNavigationBarView.isRecentsButtonVisible() && ActivityManagerWrapper.getInstance().isScreenPinningActive()) {
            return onLongPressBackHome(v);
        }
        if (shouldDisableNavbarGestures()) {
            return false;
        }
        this.mMetricsLogger.action(239);
        Bundle args = new Bundle();
        args.putInt(AssistManager.INVOCATION_TYPE_KEY, 5);
        this.mAssistManager.startAssist(args);
        this.mStatusBar.awakenDreams();
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null) {
            navigationBarView.abortCurrentGesture();
            return true;
        }
        return true;
    }

    public boolean onRecentsTouch(View v, MotionEvent event) {
        int action = event.getAction() & 255;
        if (action == 0) {
            this.mCommandQueue.preloadRecentApps();
            return false;
        } else if (action == 3) {
            this.mCommandQueue.cancelPreloadRecentApps();
            return false;
        } else if (action == 1 && !v.isPressed()) {
            this.mCommandQueue.cancelPreloadRecentApps();
            return false;
        } else {
            return false;
        }
    }

    public void onRecentsClick(View v) {
        if (LatencyTracker.isEnabled(getContext())) {
            LatencyTracker.getInstance(getContext()).onActionStart(1);
        }
        this.mStatusBar.awakenDreams();
        this.mCommandQueue.toggleRecentApps();
    }

    public boolean onLongPressBackHome(View v) {
        return onLongPressNavigationButtons(v, R.id.back, R.id.home);
    }

    public boolean onLongPressBackRecents(View v) {
        return onLongPressNavigationButtons(v, R.id.back, R.id.recent_apps);
    }

    private boolean onLongPressNavigationButtons(View v, int btnId1, int btnId2) {
        boolean sendBackLongPress = false;
        try {
            IActivityTaskManager activityManager = ActivityTaskManager.getService();
            boolean touchExplorationEnabled = this.mAccessibilityManager.isTouchExplorationEnabled();
            boolean inLockTaskMode = activityManager.isInLockTaskMode();
            if (inLockTaskMode && !touchExplorationEnabled) {
                long time = System.currentTimeMillis();
                if (time - this.mLastLockToAppLongPress < 200) {
                    if (1 != 0) {
                        activityManager.stopSystemLockTaskMode();
                        this.mNavigationBarView.updateNavButtonIcons();
                    }
                    return true;
                }
                if (v.getId() == btnId1) {
                    ButtonDispatcher button = btnId2 == R.id.recent_apps ? this.mNavigationBarView.getRecentsButton() : this.mNavigationBarView.getHomeButton();
                    if (!button.getCurrentView().isPressed()) {
                        sendBackLongPress = true;
                    }
                }
                this.mLastLockToAppLongPress = time;
            } else if (v.getId() == btnId1) {
                sendBackLongPress = true;
            } else if (touchExplorationEnabled && inLockTaskMode) {
                if (1 != 0) {
                    activityManager.stopSystemLockTaskMode();
                    this.mNavigationBarView.updateNavButtonIcons();
                }
                return true;
            } else if (v.getId() == btnId2) {
                boolean onLongPressRecents = btnId2 == R.id.recent_apps ? onLongPressRecents() : onHomeLongClick(this.mNavigationBarView.getHomeButton().getCurrentView());
                if (0 != 0) {
                    activityManager.stopSystemLockTaskMode();
                    this.mNavigationBarView.updateNavButtonIcons();
                }
                return onLongPressRecents;
            }
            if (0 != 0) {
                activityManager.stopSystemLockTaskMode();
                this.mNavigationBarView.updateNavButtonIcons();
            }
        } catch (RemoteException e) {
            Log.d(TAG, "Unable to reach activity manager", e);
        }
        if (sendBackLongPress) {
            KeyButtonView keyButtonView = (KeyButtonView) v;
            keyButtonView.sendEvent(0, 128);
            keyButtonView.sendAccessibilityEvent(2);
            return true;
        }
        return false;
    }

    private boolean onLongPressRecents() {
        if (this.mRecents == null || !ActivityTaskManager.supportsMultiWindow(getContext()) || !this.mDivider.getView().getSnapAlgorithm().isSplitScreenFeasible() || ActivityManager.isLowRamDeviceStatic() || this.mOverviewProxyService.getProxy() != null) {
            return false;
        }
        return this.mStatusBar.toggleSplitScreenMode(271, 286);
    }

    public void onAccessibilityClick(View v) {
        Display display = v.getDisplay();
        this.mAccessibilityManager.notifyAccessibilityButtonClicked(display != null ? display.getDisplayId() : 0);
    }

    public boolean onAccessibilityLongClick(View v) {
        Intent intent = new Intent("com.android.internal.intent.action.CHOOSE_ACCESSIBILITY_BUTTON");
        intent.addFlags(268468224);
        v.getContext().startActivityAsUser(intent, UserHandle.CURRENT);
        return true;
    }

    public void updateAccessibilityServicesState(AccessibilityManager accessibilityManager) {
        boolean[] feedbackEnabled = new boolean[1];
        int a11yFlags = getA11yButtonState(feedbackEnabled);
        boolean clickable = (a11yFlags & 16) != 0;
        boolean longClickable = (a11yFlags & 32) != 0;
        this.mNavigationBarView.setAccessibilityButtonState(clickable, longClickable);
        updateSystemUiStateFlags(a11yFlags);
    }

    public void updateSystemUiStateFlags(int a11yFlags) {
        if (a11yFlags < 0) {
            a11yFlags = getA11yButtonState(null);
        }
        boolean clickable = (a11yFlags & 16) != 0;
        boolean longClickable = (a11yFlags & 32) != 0;
        this.mOverviewProxyService.setSystemUiStateFlag(16, clickable, this.mDisplayId);
        this.mOverviewProxyService.setSystemUiStateFlag(32, longClickable, this.mDisplayId);
        this.mOverviewProxyService.setSystemUiStateFlag(2, true ^ isNavBarWindowVisible(), this.mDisplayId);
    }

    public int getA11yButtonState(boolean[] outFeedbackEnabled) {
        int requestingServices = 0;
        try {
            if (Settings.Secure.getIntForUser(this.mContentResolver, "accessibility_display_magnification_navbar_enabled", -2) == 1) {
                requestingServices = 0 + 1;
            }
        } catch (Settings.SettingNotFoundException e) {
        }
        boolean feedbackEnabled = false;
        List<AccessibilityServiceInfo> services = this.mAccessibilityManager.getEnabledAccessibilityServiceList(-1);
        int i = services.size() - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            AccessibilityServiceInfo info = services.get(i);
            if ((info.flags & 256) != 0) {
                requestingServices++;
            }
            if (info.feedbackType != 0 && info.feedbackType != 16) {
                feedbackEnabled = true;
            }
            i--;
        }
        if (outFeedbackEnabled != null) {
            outFeedbackEnabled[0] = feedbackEnabled;
        }
        int i2 = requestingServices < 1 ? 0 : 16;
        int i3 = requestingServices >= 2 ? 32 : 0;
        return i2 | i3;
    }

    public void sendAssistantAvailability(boolean available) {
        if (this.mOverviewProxyService.getProxy() != null) {
            try {
                this.mOverviewProxyService.getProxy().onAssistantAvailable(available && QuickStepContract.isGesturalMode(this.mNavBarMode));
            } catch (RemoteException e) {
                Log.w(TAG, "Unable to send assistant availability data to launcher");
            }
        }
    }

    public void touchAutoDim() {
        getBarTransitions().setAutoDim(false);
        this.mHandler.removeCallbacks(this.mAutoDim);
        int state = this.mStatusBarStateController.getState();
        if (state != 1 && state != 2) {
            this.mHandler.postDelayed(this.mAutoDim, AUTODIM_TIMEOUT_MS);
        }
    }

    public void setLightBarController(LightBarController lightBarController) {
        this.mLightBarController = lightBarController;
        this.mLightBarController.setNavigationBar(this.mNavigationBarView.getLightTransitionsController());
    }

    public void setAutoHideController(AutoHideController autoHideController) {
        this.mAutoHideController = autoHideController;
        this.mAutoHideController.setNavigationBar(this);
    }

    @Override // com.android.systemui.statusbar.phone.AutoHideElement
    public boolean isSemiTransparent() {
        return this.mNavigationBarMode == 1;
    }

    @Override // com.android.systemui.statusbar.phone.AutoHideElement
    public void synchronizeState() {
        checkNavBarModes();
    }

    private void checkBarModes() {
        if (this.mIsOnDefaultDisplay) {
            this.mStatusBar.checkBarModes();
        } else {
            checkNavBarModes();
        }
    }

    public boolean isNavBarWindowVisible() {
        return this.mNavigationBarWindowState == 0;
    }

    public void checkNavBarModes() {
        boolean anim = this.mStatusBar.isDeviceInteractive() && this.mNavigationBarWindowState != 2;
        this.mNavigationBarView.getBarTransitions().transitionTo(this.mNavigationBarMode, anim);
    }

    @Override // com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener
    public void onNavigationModeChanged(int mode) {
        this.mNavBarMode = mode;
        updateScreenPinningGestures();
        int userId = ActivityManagerWrapper.getInstance().getCurrentUserId();
        if (userId != 0) {
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$NAJe_hU0PesszyJ9wLZ6PYoS4_0
                @Override // java.lang.Runnable
                public final void run() {
                    NavigationBarFragment.this.lambda$onNavigationModeChanged$2$NavigationBarFragment();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onNavigationModeChanged$2$NavigationBarFragment() {
        FragmentHostManager fragmentHost = FragmentHostManager.get(this.mNavigationBarView);
        fragmentHost.reloadFragments();
    }

    public void disableAnimationsDuringHide(long delay) {
        this.mNavigationBarView.setLayoutTransitionsEnabled(false);
        this.mNavigationBarView.postDelayed(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarFragment$i3mhmHWtnMuMGNO528iejYx75q0
            @Override // java.lang.Runnable
            public final void run() {
                NavigationBarFragment.this.lambda$disableAnimationsDuringHide$3$NavigationBarFragment();
            }
        }, 448 + delay);
    }

    public /* synthetic */ void lambda$disableAnimationsDuringHide$3$NavigationBarFragment() {
        this.mNavigationBarView.setLayoutTransitionsEnabled(true);
    }

    public void transitionTo(int barMode, boolean animate) {
        getBarTransitions().transitionTo(barMode, animate);
    }

    public NavigationBarTransitions getBarTransitions() {
        return this.mNavigationBarView.getBarTransitions();
    }

    public void finishBarAnimations() {
        this.mNavigationBarView.getBarTransitions().finishAnimations();
    }

    /* loaded from: classes21.dex */
    private class MagnificationContentObserver extends ContentObserver {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public MagnificationContentObserver(Handler handler) {
            super(handler);
            NavigationBarFragment.this = r1;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            NavigationBarFragment navigationBarFragment = NavigationBarFragment.this;
            navigationBarFragment.updateAccessibilityServicesState(navigationBarFragment.mAccessibilityManager);
        }
    }

    public /* synthetic */ void lambda$new$4$NavigationBarFragment(Integer rotation) {
        NavigationBarView navigationBarView = this.mNavigationBarView;
        if (navigationBarView != null && navigationBarView.needsReorient(rotation.intValue())) {
            repositionNavigationBar();
        }
    }

    public static View create(Context context, final FragmentHostManager.FragmentListener listener) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1, WindowHelper.TYPE_NAVIGATION_BAR, 545521768, -3);
        lp.token = new Binder();
        lp.setTitle(TAG + context.getDisplayId());
        lp.accessibilityTitle = context.getString(R.string.nav_bar);
        lp.windowAnimations = 0;
        lp.privateFlags = lp.privateFlags | 16777216;
        View navigationBarView = LayoutInflater.from(context).inflate(R.layout.navigation_bar_window, (ViewGroup) null);
        if (navigationBarView == null) {
            return null;
        }
        NavigationBarFragment fragment = (NavigationBarFragment) FragmentHostManager.get(navigationBarView).create(NavigationBarFragment.class);
        navigationBarView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.android.systemui.statusbar.phone.NavigationBarFragment.4
            {
                NavigationBarFragment.this = fragment;
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
                FragmentHostManager fragmentHost = FragmentHostManager.get(v);
                fragmentHost.getFragmentManager().beginTransaction().replace(R.id.navigation_bar_frame, NavigationBarFragment.this, NavigationBarFragment.TAG).commit();
                fragmentHost.addTagListener(NavigationBarFragment.TAG, listener);
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
                FragmentHostManager.removeAndDestroy(v);
            }
        });
        ((WindowManager) context.getSystemService(WindowManager.class)).addView(navigationBarView, lp);
        return navigationBarView;
    }

    @VisibleForTesting
    int getNavigationIconHints() {
        return this.mNavigationIconHints;
    }
}
