package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Binder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class StatusBarWindowController implements RemoteInputController.Callback, Dumpable, ConfigurationController.ConfigurationListener {
    private static final String TAG = "StatusBarWindowController";
    private final IActivityManager mActivityManager;
    private int mBarHeight;
    private final ArrayList<WeakReference<StatusBarWindowCallback>> mCallbacks;
    private final SysuiColorExtractor mColorExtractor;
    private final Context mContext;
    private final State mCurrentState;
    private final DozeParameters mDozeParameters;
    private ForcePluginOpenListener mForcePluginOpenListener;
    private boolean mHasTopUi;
    private boolean mHasTopUiChanged;
    private final KeyguardBypassController mKeyguardBypassController;
    private final Display.Mode mKeyguardDisplayMode;
    private final boolean mKeyguardScreenRotation;
    private OtherwisedCollapsedListener mListener;
    private final long mLockScreenDisplayTimeout;
    private WindowManager.LayoutParams mLp;
    private final WindowManager.LayoutParams mLpChanged;
    private float mScreenBrightnessDoze;
    private final StatusBarStateController.StateListener mStateListener;
    private ViewGroup mStatusBarView;
    private final WindowManager mWindowManager;

    /* loaded from: classes21.dex */
    public interface ForcePluginOpenListener {
        void onChange(boolean z);
    }

    /* loaded from: classes21.dex */
    public interface OtherwisedCollapsedListener {
        void setWouldOtherwiseCollapse(boolean z);
    }

    @Inject
    public StatusBarWindowController(Context context, StatusBarStateController statusBarStateController, ConfigurationController configurationController, KeyguardBypassController keyguardBypassController) {
        this(context, (WindowManager) context.getSystemService(WindowManager.class), ActivityManager.getService(), DozeParameters.getInstance(context), statusBarStateController, configurationController, keyguardBypassController);
    }

    @VisibleForTesting
    public StatusBarWindowController(Context context, WindowManager windowManager, IActivityManager activityManager, DozeParameters dozeParameters, StatusBarStateController statusBarStateController, ConfigurationController configurationController, KeyguardBypassController keyguardBypassController) {
        this.mCurrentState = new State();
        this.mCallbacks = Lists.newArrayList();
        this.mColorExtractor = (SysuiColorExtractor) Dependency.get(SysuiColorExtractor.class);
        this.mStateListener = new StatusBarStateController.StateListener() { // from class: com.android.systemui.statusbar.phone.StatusBarWindowController.1
            @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
            public void onStateChanged(int newState) {
                StatusBarWindowController.this.setStatusBarState(newState);
            }

            @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
            public void onDozingChanged(boolean isDozing) {
                StatusBarWindowController.this.setDozing(isDozing);
            }
        };
        this.mContext = context;
        this.mWindowManager = windowManager;
        this.mActivityManager = activityManager;
        this.mKeyguardScreenRotation = shouldEnableKeyguardScreenRotation();
        this.mDozeParameters = dozeParameters;
        this.mScreenBrightnessDoze = this.mDozeParameters.getScreenBrightnessDoze();
        this.mLpChanged = new WindowManager.LayoutParams();
        this.mKeyguardBypassController = keyguardBypassController;
        this.mLockScreenDisplayTimeout = context.getResources().getInteger(R.integer.config_lockScreenDisplayTimeout);
        ((SysuiStatusBarStateController) statusBarStateController).addCallback(this.mStateListener, 1);
        configurationController.addCallback(this);
        Display.Mode[] supportedModes = context.getDisplay().getSupportedModes();
        final Display.Mode currentMode = context.getDisplay().getMode();
        final int keyguardRefreshRate = context.getResources().getInteger(R.integer.config_keyguardRefreshRate);
        this.mKeyguardDisplayMode = (Display.Mode) Arrays.stream(supportedModes).filter(new Predicate() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarWindowController$oQ5uMbmLGGiJ-Y9OIfGd2BLwohw
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return StatusBarWindowController.lambda$new$0(keyguardRefreshRate, currentMode, (Display.Mode) obj);
            }
        }).findFirst().orElse(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$new$0(int keyguardRefreshRate, Display.Mode currentMode, Display.Mode mode) {
        return ((int) mode.getRefreshRate()) == keyguardRefreshRate && mode.getPhysicalWidth() == currentMode.getPhysicalWidth() && mode.getPhysicalHeight() == currentMode.getPhysicalHeight();
    }

    public void registerCallback(StatusBarWindowCallback callback) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            if (this.mCallbacks.get(i).get() == callback) {
                return;
            }
        }
        this.mCallbacks.add(new WeakReference<>(callback));
    }

    private boolean shouldEnableKeyguardScreenRotation() {
        Resources res = this.mContext.getResources();
        return SystemProperties.getBoolean("lockscreen.rot_override", false) || res.getBoolean(R.bool.config_enableLockScreenRotation);
    }

    public void add(ViewGroup statusBarView, int barHeight) {
        this.mLp = new WindowManager.LayoutParams(-1, barHeight, 2000, -2138832824, -3);
        this.mLp.token = new Binder();
        WindowManager.LayoutParams layoutParams = this.mLp;
        layoutParams.gravity = 48;
        layoutParams.softInputMode = 16;
        layoutParams.setTitle(StatusBar.TAG);
        this.mLp.packageName = this.mContext.getPackageName();
        WindowManager.LayoutParams layoutParams2 = this.mLp;
        layoutParams2.layoutInDisplayCutoutMode = 1;
        this.mStatusBarView = statusBarView;
        this.mBarHeight = barHeight;
        this.mWindowManager.addView(this.mStatusBarView, layoutParams2);
        this.mLpChanged.copyFrom(this.mLp);
        onThemeChanged();
    }

    public ViewGroup getStatusBarView() {
        return this.mStatusBarView;
    }

    public void setDozeScreenBrightness(int value) {
        this.mScreenBrightnessDoze = value / 255.0f;
    }

    private void setKeyguardDark(boolean dark) {
        int vis;
        int vis2 = this.mStatusBarView.getSystemUiVisibility();
        if (dark) {
            vis = vis2 | 16 | 8192;
        } else {
            vis = vis2 & (-17) & (-8193);
        }
        this.mStatusBarView.setSystemUiVisibility(vis);
    }

    private void applyKeyguardFlags(State state) {
        if (state.keyguardShowing) {
            this.mLpChanged.privateFlags |= 1024;
        } else {
            this.mLpChanged.privateFlags &= -1025;
        }
        boolean bypassOnKeyguard = true;
        boolean scrimsOccludingWallpaper = state.scrimsVisibility == 2;
        boolean keyguardOrAod = state.keyguardShowing || (state.dozing && this.mDozeParameters.getAlwaysOn());
        if (keyguardOrAod && !state.backdropShowing && !scrimsOccludingWallpaper) {
            this.mLpChanged.flags |= 1048576;
        } else {
            this.mLpChanged.flags &= -1048577;
        }
        if (state.dozing) {
            this.mLpChanged.privateFlags |= 524288;
        } else {
            this.mLpChanged.privateFlags &= -524289;
        }
        if (this.mKeyguardDisplayMode != null) {
            if (!this.mKeyguardBypassController.getBypassEnabled() || state.statusBarState != 1 || state.keyguardFadingAway || state.keyguardGoingAway) {
                bypassOnKeyguard = false;
            }
            if (state.dozing || bypassOnKeyguard) {
                this.mLpChanged.preferredDisplayModeId = this.mKeyguardDisplayMode.getModeId();
            } else {
                this.mLpChanged.preferredDisplayModeId = 0;
            }
            Trace.setCounter("display_mode_id", this.mLpChanged.preferredDisplayModeId);
        }
    }

    private void adjustScreenOrientation(State state) {
        if (state.isKeyguardShowingAndNotOccluded() || state.dozing) {
            if (this.mKeyguardScreenRotation) {
                this.mLpChanged.screenOrientation = 2;
                return;
            } else {
                this.mLpChanged.screenOrientation = 5;
                return;
            }
        }
        this.mLpChanged.screenOrientation = -1;
    }

    private void applyFocusableFlag(State state) {
        boolean panelFocusable = state.statusBarFocusable && state.panelExpanded;
        if ((state.bouncerShowing && (state.keyguardOccluded || state.keyguardNeedsInput)) || ((NotificationRemoteInputManager.ENABLE_REMOTE_INPUT && state.remoteInputActive) || state.bubbleExpanded)) {
            this.mLpChanged.flags &= -9;
            WindowManager.LayoutParams layoutParams = this.mLpChanged;
            layoutParams.flags = (-131073) & layoutParams.flags;
        } else if (state.isKeyguardShowingAndNotOccluded() || panelFocusable) {
            this.mLpChanged.flags &= -9;
            this.mLpChanged.flags |= 131072;
        } else {
            this.mLpChanged.flags |= 8;
            WindowManager.LayoutParams layoutParams2 = this.mLpChanged;
            layoutParams2.flags = (-131073) & layoutParams2.flags;
        }
        this.mLpChanged.softInputMode = 16;
    }

    private void applyForceShowNavigationFlag(State state) {
        if (state.panelExpanded || state.bouncerShowing || (NotificationRemoteInputManager.ENABLE_REMOTE_INPUT && state.remoteInputActive)) {
            this.mLpChanged.privateFlags |= 8388608;
            return;
        }
        this.mLpChanged.privateFlags &= -8388609;
    }

    private void applyHeight(State state) {
        boolean expanded = isExpanded(state);
        if (state.forcePluginOpen) {
            OtherwisedCollapsedListener otherwisedCollapsedListener = this.mListener;
            if (otherwisedCollapsedListener != null) {
                otherwisedCollapsedListener.setWouldOtherwiseCollapse(expanded);
            }
            expanded = true;
        }
        if (expanded) {
            this.mLpChanged.height = -1;
            return;
        }
        this.mLpChanged.height = this.mBarHeight;
    }

    private boolean isExpanded(State state) {
        return !state.forceCollapsed && (state.isKeyguardShowingAndNotOccluded() || state.panelVisible || state.keyguardFadingAway || state.bouncerShowing || state.headsUpShowing || state.bubblesShowing || state.scrimsVisibility != 0);
    }

    private void applyFitsSystemWindows(State state) {
        boolean fitsSystemWindows = !state.isKeyguardShowingAndNotOccluded();
        ViewGroup viewGroup = this.mStatusBarView;
        if (viewGroup != null && viewGroup.getFitsSystemWindows() != fitsSystemWindows) {
            this.mStatusBarView.setFitsSystemWindows(fitsSystemWindows);
            this.mStatusBarView.requestApplyInsets();
        }
    }

    private void applyUserActivityTimeout(State state) {
        if (state.isKeyguardShowingAndNotOccluded() && state.statusBarState == 1 && !state.qsExpanded) {
            this.mLpChanged.userActivityTimeout = state.bouncerShowing ? 10000L : this.mLockScreenDisplayTimeout;
            return;
        }
        this.mLpChanged.userActivityTimeout = -1L;
    }

    private void applyInputFeatures(State state) {
        if (state.isKeyguardShowingAndNotOccluded() && state.statusBarState == 1 && !state.qsExpanded && !state.forceUserActivity) {
            this.mLpChanged.inputFeatures |= 4;
            return;
        }
        this.mLpChanged.inputFeatures &= -5;
    }

    private void applyStatusBarColorSpaceAgnosticFlag(State state) {
        if (!isExpanded(state)) {
            this.mLpChanged.privateFlags |= 16777216;
            return;
        }
        this.mLpChanged.privateFlags &= -16777217;
    }

    private void apply(State state) {
        applyKeyguardFlags(state);
        applyForceStatusBarVisibleFlag(state);
        applyFocusableFlag(state);
        applyForceShowNavigationFlag(state);
        adjustScreenOrientation(state);
        applyHeight(state);
        applyUserActivityTimeout(state);
        applyInputFeatures(state);
        applyFitsSystemWindows(state);
        applyModalFlag(state);
        applyBrightness(state);
        applyHasTopUi(state);
        applyNotTouchable(state);
        applyStatusBarColorSpaceAgnosticFlag(state);
        WindowManager.LayoutParams layoutParams = this.mLp;
        if (layoutParams != null && layoutParams.copyFrom(this.mLpChanged) != 0) {
            this.mWindowManager.updateViewLayout(this.mStatusBarView, this.mLp);
        }
        boolean z = this.mHasTopUi;
        boolean z2 = this.mHasTopUiChanged;
        if (z != z2) {
            try {
                this.mActivityManager.setHasTopUi(z2);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to call setHasTopUi", e);
            }
            this.mHasTopUi = this.mHasTopUiChanged;
        }
        notifyStateChangedCallbacks();
    }

    public void notifyStateChangedCallbacks() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            StatusBarWindowCallback cb = this.mCallbacks.get(i).get();
            if (cb != null) {
                cb.onStateChanged(this.mCurrentState.keyguardShowing, this.mCurrentState.keyguardOccluded, this.mCurrentState.bouncerShowing);
            }
        }
    }

    private void applyForceStatusBarVisibleFlag(State state) {
        if (state.forceStatusBarVisible || state.forcePluginOpen) {
            this.mLpChanged.privateFlags |= 4096;
            return;
        }
        this.mLpChanged.privateFlags &= -4097;
    }

    private void applyModalFlag(State state) {
        if (state.headsUpShowing) {
            this.mLpChanged.flags |= 32;
            return;
        }
        this.mLpChanged.flags &= -33;
    }

    private void applyBrightness(State state) {
        if (state.forceDozeBrightness) {
            this.mLpChanged.screenBrightness = this.mScreenBrightnessDoze;
            return;
        }
        this.mLpChanged.screenBrightness = -1.0f;
    }

    private void applyHasTopUi(State state) {
        this.mHasTopUiChanged = state.forceHasTopUi || isExpanded(state);
    }

    private void applyNotTouchable(State state) {
        if (state.notTouchable) {
            this.mLpChanged.flags |= 16;
            return;
        }
        this.mLpChanged.flags &= -17;
    }

    public void setKeyguardShowing(boolean showing) {
        State state = this.mCurrentState;
        state.keyguardShowing = showing;
        apply(state);
    }

    public void setKeyguardOccluded(boolean occluded) {
        State state = this.mCurrentState;
        state.keyguardOccluded = occluded;
        apply(state);
    }

    public void setKeyguardNeedsInput(boolean needsInput) {
        State state = this.mCurrentState;
        state.keyguardNeedsInput = needsInput;
        apply(state);
    }

    public void setPanelVisible(boolean visible) {
        State state = this.mCurrentState;
        state.panelVisible = visible;
        state.statusBarFocusable = visible;
        apply(state);
    }

    public void setStatusBarFocusable(boolean focusable) {
        State state = this.mCurrentState;
        state.statusBarFocusable = focusable;
        apply(state);
    }

    public void setBouncerShowing(boolean showing) {
        State state = this.mCurrentState;
        state.bouncerShowing = showing;
        apply(state);
    }

    public void setBackdropShowing(boolean showing) {
        State state = this.mCurrentState;
        state.backdropShowing = showing;
        apply(state);
    }

    public void setKeyguardFadingAway(boolean keyguardFadingAway) {
        State state = this.mCurrentState;
        state.keyguardFadingAway = keyguardFadingAway;
        apply(state);
    }

    public void setQsExpanded(boolean expanded) {
        State state = this.mCurrentState;
        state.qsExpanded = expanded;
        apply(state);
    }

    public void setForceUserActivity(boolean forceUserActivity) {
        State state = this.mCurrentState;
        state.forceUserActivity = forceUserActivity;
        apply(state);
    }

    public void setScrimsVisibility(int scrimsVisibility) {
        State state = this.mCurrentState;
        state.scrimsVisibility = scrimsVisibility;
        apply(state);
    }

    public void setHeadsUpShowing(boolean showing) {
        State state = this.mCurrentState;
        state.headsUpShowing = showing;
        apply(state);
    }

    public void setWallpaperSupportsAmbientMode(boolean supportsAmbientMode) {
        State state = this.mCurrentState;
        state.wallpaperSupportsAmbientMode = supportsAmbientMode;
        apply(state);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setStatusBarState(int state) {
        State state2 = this.mCurrentState;
        state2.statusBarState = state;
        apply(state2);
    }

    public void setForceStatusBarVisible(boolean forceStatusBarVisible) {
        State state = this.mCurrentState;
        state.forceStatusBarVisible = forceStatusBarVisible;
        apply(state);
    }

    public void setForceWindowCollapsed(boolean force) {
        State state = this.mCurrentState;
        state.forceCollapsed = force;
        apply(state);
    }

    public void setPanelExpanded(boolean isExpanded) {
        State state = this.mCurrentState;
        state.panelExpanded = isExpanded;
        apply(state);
    }

    @Override // com.android.systemui.statusbar.RemoteInputController.Callback
    public void onRemoteInputActive(boolean remoteInputActive) {
        State state = this.mCurrentState;
        state.remoteInputActive = remoteInputActive;
        apply(state);
    }

    public void setForceDozeBrightness(boolean forceDozeBrightness) {
        State state = this.mCurrentState;
        state.forceDozeBrightness = forceDozeBrightness;
        apply(state);
    }

    public void setDozing(boolean dozing) {
        State state = this.mCurrentState;
        state.dozing = dozing;
        apply(state);
    }

    public void setBarHeight(int barHeight) {
        this.mBarHeight = barHeight;
        apply(this.mCurrentState);
    }

    public void setForcePluginOpen(boolean forcePluginOpen) {
        State state = this.mCurrentState;
        state.forcePluginOpen = forcePluginOpen;
        apply(state);
        ForcePluginOpenListener forcePluginOpenListener = this.mForcePluginOpenListener;
        if (forcePluginOpenListener != null) {
            forcePluginOpenListener.onChange(forcePluginOpen);
        }
    }

    public boolean getForcePluginOpen() {
        return this.mCurrentState.forcePluginOpen;
    }

    public void setNotTouchable(boolean notTouchable) {
        State state = this.mCurrentState;
        state.notTouchable = notTouchable;
        apply(state);
    }

    public void setBubblesShowing(boolean bubblesShowing) {
        State state = this.mCurrentState;
        state.bubblesShowing = bubblesShowing;
        apply(state);
    }

    public boolean getBubblesShowing() {
        return this.mCurrentState.bubblesShowing;
    }

    public void setBubbleExpanded(boolean bubbleExpanded) {
        State state = this.mCurrentState;
        state.bubbleExpanded = bubbleExpanded;
        apply(state);
    }

    public boolean getBubbleExpanded() {
        return this.mCurrentState.bubbleExpanded;
    }

    public boolean getPanelExpanded() {
        return this.mCurrentState.panelExpanded;
    }

    public void setStateListener(OtherwisedCollapsedListener listener) {
        this.mListener = listener;
    }

    public void setForcePluginOpenListener(ForcePluginOpenListener listener) {
        this.mForcePluginOpenListener = listener;
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("StatusBarWindowController:");
        pw.println("  mKeyguardDisplayMode=" + this.mKeyguardDisplayMode);
        pw.println(this.mCurrentState);
    }

    public boolean isShowingWallpaper() {
        return !this.mCurrentState.backdropShowing;
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onThemeChanged() {
        if (this.mStatusBarView == null) {
            return;
        }
        boolean useDarkText = this.mColorExtractor.getNeutralColors().supportsDarkText();
        setKeyguardDark(useDarkText);
    }

    public void setKeyguardGoingAway(boolean goingAway) {
        State state = this.mCurrentState;
        state.keyguardGoingAway = goingAway;
        apply(state);
    }

    public boolean getForceHasTopUi() {
        return this.mCurrentState.forceHasTopUi;
    }

    public void setForceHasTopUi(boolean forceHasTopUi) {
        State state = this.mCurrentState;
        state.forceHasTopUi = forceHasTopUi;
        apply(state);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class State {
        boolean backdropShowing;
        boolean bouncerShowing;
        boolean bubbleExpanded;
        boolean bubblesShowing;
        boolean dozing;
        boolean forceCollapsed;
        boolean forceDozeBrightness;
        boolean forceHasTopUi;
        boolean forcePluginOpen;
        boolean forceStatusBarVisible;
        boolean forceUserActivity;
        boolean headsUpShowing;
        boolean keyguardFadingAway;
        boolean keyguardGoingAway;
        boolean keyguardNeedsInput;
        boolean keyguardOccluded;
        boolean keyguardShowing;
        boolean notTouchable;
        boolean panelExpanded;
        boolean panelVisible;
        boolean qsExpanded;
        boolean remoteInputActive;
        int scrimsVisibility;
        boolean statusBarFocusable;
        int statusBarState;
        boolean wallpaperSupportsAmbientMode;

        private State() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isKeyguardShowingAndNotOccluded() {
            return this.keyguardShowing && !this.keyguardOccluded;
        }

        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("Window State {");
            result.append("\n");
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields) {
                result.append("  ");
                try {
                    result.append(field.getName());
                    result.append(": ");
                    result.append(field.get(this));
                } catch (IllegalAccessException e) {
                }
                result.append("\n");
            }
            result.append("}");
            return result.toString();
        }
    }
}
