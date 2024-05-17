package com.xiaopeng.systemui.statusbar;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.util.SparseArray;
import android.view.inputmethod.InputMethodSystemProperty;
import androidx.annotation.VisibleForTesting;
import com.android.internal.os.SomeArgs;
import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.SystemUI;
import java.util.ArrayList;
/* loaded from: classes24.dex */
public class CommandQueue extends IStatusBar.Stub implements DisplayManager.DisplayListener {
    public static final int FLAG_EXCLUDE_COMPAT_MODE_PANEL = 16;
    public static final int FLAG_EXCLUDE_INPUT_METHODS_PANEL = 8;
    public static final int FLAG_EXCLUDE_NONE = 0;
    public static final int FLAG_EXCLUDE_NOTIFICATION_PANEL = 4;
    public static final int FLAG_EXCLUDE_RECENTS_PANEL = 2;
    public static final int FLAG_EXCLUDE_SEARCH_PANEL = 1;
    private static final int INDEX_MASK = 65535;
    private static final int MSG_ADD_QS_TILE = 1769472;
    private static final int MSG_APP_TRANSITION_CANCELLED = 1310720;
    private static final int MSG_APP_TRANSITION_FINISHED = 2031616;
    private static final int MSG_APP_TRANSITION_PENDING = 1245184;
    private static final int MSG_APP_TRANSITION_STARTING = 1376256;
    private static final int MSG_ASSIST_DISCLOSURE = 1441792;
    private static final int MSG_BIOMETRIC_AUTHENTICATED = 2621440;
    private static final int MSG_BIOMETRIC_ERROR = 2752512;
    private static final int MSG_BIOMETRIC_HELP = 2686976;
    private static final int MSG_BIOMETRIC_HIDE = 2818048;
    private static final int MSG_BIOMETRIC_SHOW = 2555904;
    private static final int MSG_CAMERA_LAUNCH_GESTURE = 1572864;
    private static final int MSG_CANCEL_PRELOAD_RECENT_APPS = 720896;
    private static final int MSG_CLICK_QS_TILE = 1900544;
    private static final int MSG_COLLAPSE_PANELS = 262144;
    private static final int MSG_DISABLE = 131072;
    private static final int MSG_DISMISS_KEYBOARD_SHORTCUTS = 2097152;
    private static final int MSG_DISPLAY_READY = 458752;
    private static final int MSG_EXPAND_NOTIFICATIONS = 196608;
    private static final int MSG_EXPAND_SETTINGS = 327680;
    private static final int MSG_HANDLE_SYSTEM_KEY = 2162688;
    private static final int MSG_HIDE_RECENT_APPS = 917504;
    private static final int MSG_ICON = 65536;
    private static final int MSG_MASK = -65536;
    private static final int MSG_PRELOAD_RECENT_APPS = 655360;
    private static final int MSG_RECENTS_ANIMATION_STATE_CHANGED = 3080192;
    private static final int MSG_REMOVE_QS_TILE = 1835008;
    private static final int MSG_ROTATION_PROPOSAL = 2490368;
    private static final int MSG_SET_SYSTEMUI_VISIBILITY = 393216;
    private static final int MSG_SET_TOP_APP_HIDES_STATUS_BAR = 2424832;
    private static final int MSG_SET_WINDOW_STATE = 786432;
    private static final int MSG_SHIFT = 16;
    private static final int MSG_SHOW_CHARGING_ANIMATION = 2883584;
    private static final int MSG_SHOW_GLOBAL_ACTIONS = 2228224;
    private static final int MSG_SHOW_IME_BUTTON = 524288;
    private static final int MSG_SHOW_PICTURE_IN_PICTURE_MENU = 1703936;
    private static final int MSG_SHOW_PINNING_TOAST_ENTER_EXIT = 2949120;
    private static final int MSG_SHOW_PINNING_TOAST_ESCAPE = 3014656;
    private static final int MSG_SHOW_RECENT_APPS = 851968;
    private static final int MSG_SHOW_SCREEN_PIN_REQUEST = 1179648;
    private static final int MSG_SHOW_SHUTDOWN_UI = 2359296;
    private static final int MSG_START_ASSIST = 1507328;
    private static final int MSG_TOGGLE_APP_SPLIT_SCREEN = 1966080;
    private static final int MSG_TOGGLE_KEYBOARD_SHORTCUTS = 1638400;
    private static final int MSG_TOGGLE_PANEL = 2293760;
    private static final int MSG_TOGGLE_RECENT_APPS = 589824;
    private static final int OP_REMOVE_ICON = 2;
    private static final int OP_SET_ICON = 1;
    private static final String SHOW_IME_SWITCHER_KEY = "showImeSwitcherKey";
    private final Object mLock = new Object();
    private ArrayList<Callbacks> mCallbacks = new ArrayList<>();
    private Handler mHandler = new H(Looper.getMainLooper());
    private SparseArray<Pair<Integer, Integer>> mDisplayDisabled = new SparseArray<>();
    private int mLastUpdatedImeDisplayId = -1;

    /* loaded from: classes24.dex */
    public interface Callbacks {
        default void setIcon(String slot, StatusBarIcon icon) {
        }

        default void removeIcon(String slot) {
        }

        default void disable(int displayId, int state1, int state2, boolean animate) {
        }

        default void animateExpandNotificationsPanel() {
        }

        default void animateCollapsePanels(int flags, boolean force) {
        }

        default void togglePanel() {
        }

        default void animateExpandSettingsPanel(String obj) {
        }

        default void setSystemUiVisibility(int displayId, int vis, int fullscreenStackVis, int dockedStackVis, int mask, Rect fullscreenStackBounds, Rect dockedStackBounds, boolean navbarColorManagedByIme) {
        }

        default void setImeWindowStatus(int displayId, IBinder token, int vis, int backDisposition, boolean showImeSwitcher) {
        }

        default void showRecentApps(boolean triggeredFromAltTab) {
        }

        default void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) {
        }

        default void toggleRecentApps() {
        }

        default void toggleSplitScreen() {
        }

        default void preloadRecentApps() {
        }

        default void dismissKeyboardShortcutsMenu() {
        }

        default void toggleKeyboardShortcutsMenu(int deviceId) {
        }

        default void cancelPreloadRecentApps() {
        }

        default void setWindowState(int displayId, int window, int state) {
        }

        default void showScreenPinningRequest(int taskId) {
        }

        default void appTransitionPending(int displayId, boolean forced) {
        }

        default void appTransitionCancelled(int displayId) {
        }

        default void appTransitionStarting(int displayId, long startTime, long duration, boolean forced) {
        }

        default void appTransitionFinished(int displayId) {
        }

        default void showAssistDisclosure() {
        }

        default void startAssist(Bundle args) {
        }

        default void onCameraLaunchGestureDetected(int source) {
        }

        default void showPictureInPictureMenu() {
        }

        default void setTopAppHidesStatusBar(boolean topAppHidesStatusBar) {
        }

        default void addQsTile(ComponentName tile) {
        }

        default void remQsTile(ComponentName tile) {
        }

        default void clickTile(ComponentName tile) {
        }

        default void handleSystemKey(int arg1) {
        }

        default void showPinningEnterExitToast(boolean entering) {
        }

        default void showPinningEscapeToast() {
        }

        default void handleShowGlobalActionsMenu() {
        }

        default void handleShowShutdownUi(boolean isReboot, String reason) {
        }

        default void showWirelessChargingAnimation(int batteryLevel) {
        }

        default void onRotationProposal(int rotation, boolean isValid) {
        }

        default void showBiometricDialog(Bundle bundle, IBiometricServiceReceiverInternal receiver, int type, boolean requireConfirmation, int userId) {
        }

        default void onBiometricAuthenticated(boolean authenticated, String failureReason) {
        }

        default void onBiometricHelp(String message) {
        }

        default void onBiometricError(String error) {
        }

        default void hideBiometricDialog() {
        }

        default void onDisplayReady(int displayId) {
        }

        default void onDisplayRemoved(int displayId) {
        }

        default void onRecentsAnimationStateChanged(boolean running) {
        }
    }

    @VisibleForTesting
    public CommandQueue(Context context) {
        ((DisplayManager) context.getSystemService(DisplayManager.class)).registerDisplayListener(this, this.mHandler);
        setDisabled(0, 0, 0);
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayAdded(int displayId) {
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayRemoved(int displayId) {
        synchronized (this.mLock) {
            this.mDisplayDisabled.remove(displayId);
        }
        for (int i = this.mCallbacks.size() - 1; i >= 0; i--) {
            this.mCallbacks.get(i).onDisplayRemoved(displayId);
        }
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayChanged(int displayId) {
    }

    public boolean panelsEnabled() {
        int disabled1 = getDisabled1(0);
        int disabled2 = getDisabled2(0);
        return (65536 & disabled1) == 0 && (disabled2 & 4) == 0 && !com.android.systemui.statusbar.phone.StatusBar.ONLY_CORE_APPS;
    }

    public void addCallback(Callbacks callbacks) {
        this.mCallbacks.add(callbacks);
        for (int i = 0; i < this.mDisplayDisabled.size(); i++) {
            int displayId = this.mDisplayDisabled.keyAt(i);
            int disabled1 = getDisabled1(displayId);
            int disabled2 = getDisabled2(displayId);
            callbacks.disable(displayId, disabled1, disabled2, false);
        }
    }

    public void removeCallback(Callbacks callbacks) {
        this.mCallbacks.remove(callbacks);
    }

    public void setIcon(String slot, StatusBarIcon icon) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(65536, 1, 0, new Pair(slot, icon)).sendToTarget();
        }
    }

    public void removeIcon(String slot) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(65536, 2, 0, slot).sendToTarget();
        }
    }

    public void disable(int displayId, int state1, int state2, boolean animate) {
        synchronized (this.mLock) {
            setDisabled(displayId, state1, state2);
            this.mHandler.removeMessages(131072);
            SomeArgs args = SomeArgs.obtain();
            args.argi1 = displayId;
            args.argi2 = state1;
            args.argi3 = state2;
            args.argi4 = animate ? 1 : 0;
            Message msg = this.mHandler.obtainMessage(131072, args);
            if (Looper.myLooper() == this.mHandler.getLooper()) {
                this.mHandler.handleMessage(msg);
                msg.recycle();
            } else {
                msg.sendToTarget();
            }
        }
    }

    public void disable(int displayId, int state1, int state2) {
        disable(displayId, state1, state2, true);
    }

    public void recomputeDisableFlags(int displayId, boolean animate) {
        int disabled1 = getDisabled1(displayId);
        int disabled2 = getDisabled2(displayId);
        disable(displayId, disabled1, disabled2, animate);
    }

    private void setDisabled(int displayId, int disabled1, int disabled2) {
        this.mDisplayDisabled.put(displayId, new Pair<>(Integer.valueOf(disabled1), Integer.valueOf(disabled2)));
    }

    private int getDisabled1(int displayId) {
        return ((Integer) getDisabled(displayId).first).intValue();
    }

    private int getDisabled2(int displayId) {
        return ((Integer) getDisabled(displayId).second).intValue();
    }

    private Pair<Integer, Integer> getDisabled(int displayId) {
        Pair<Integer, Integer> disablePair = this.mDisplayDisabled.get(displayId);
        if (disablePair == null) {
            Pair<Integer, Integer> disablePair2 = new Pair<>(0, 0);
            this.mDisplayDisabled.put(displayId, disablePair2);
            return disablePair2;
        }
        return disablePair;
    }

    public void animateExpandNotificationsPanel() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_EXPAND_NOTIFICATIONS);
            this.mHandler.sendEmptyMessage(MSG_EXPAND_NOTIFICATIONS);
        }
    }

    public void animateCollapsePanels() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(262144);
            this.mHandler.obtainMessage(262144, 0, 0).sendToTarget();
        }
    }

    public void animateCollapsePanels(int flags, boolean force) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(262144);
            this.mHandler.obtainMessage(262144, flags, force ? 1 : 0).sendToTarget();
        }
    }

    public void togglePanel() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_TOGGLE_PANEL);
            this.mHandler.obtainMessage(MSG_TOGGLE_PANEL, 0, 0).sendToTarget();
        }
    }

    public void animateExpandSettingsPanel(String subPanel) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_EXPAND_SETTINGS);
            this.mHandler.obtainMessage(MSG_EXPAND_SETTINGS, subPanel).sendToTarget();
        }
    }

    public void setSystemUiVisibility(int displayId, int vis, int fullscreenStackVis, int dockedStackVis, int mask, Rect fullscreenStackBounds, Rect dockedStackBounds, boolean navbarColorManagedByIme) {
        synchronized (this.mLock) {
            SomeArgs args = SomeArgs.obtain();
            args.argi1 = displayId;
            args.argi2 = vis;
            args.argi3 = fullscreenStackVis;
            args.argi4 = dockedStackVis;
            args.argi5 = mask;
            args.argi6 = navbarColorManagedByIme ? 1 : 0;
            args.arg1 = fullscreenStackBounds;
            args.arg2 = dockedStackBounds;
            this.mHandler.obtainMessage(MSG_SET_SYSTEMUI_VISIBILITY, args).sendToTarget();
        }
    }

    public void topAppWindowChanged(int displayId, boolean menuVisible) {
    }

    public void setImeWindowStatus(int displayId, IBinder token, int vis, int backDisposition, boolean showImeSwitcher) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(524288);
            SomeArgs args = SomeArgs.obtain();
            args.argi1 = displayId;
            args.argi2 = vis;
            args.argi3 = backDisposition;
            args.argi4 = showImeSwitcher ? 1 : 0;
            args.arg1 = token;
            Message m = this.mHandler.obtainMessage(524288, args);
            m.sendToTarget();
        }
    }

    public void showRecentApps(boolean triggeredFromAltTab) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_SHOW_RECENT_APPS);
            this.mHandler.obtainMessage(MSG_SHOW_RECENT_APPS, triggeredFromAltTab ? 1 : 0, 0, null).sendToTarget();
        }
    }

    public void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_HIDE_RECENT_APPS);
            this.mHandler.obtainMessage(MSG_HIDE_RECENT_APPS, triggeredFromAltTab ? 1 : 0, triggeredFromHomeKey ? 1 : 0, null).sendToTarget();
        }
    }

    public void toggleSplitScreen() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_TOGGLE_APP_SPLIT_SCREEN);
            this.mHandler.obtainMessage(MSG_TOGGLE_APP_SPLIT_SCREEN, 0, 0, null).sendToTarget();
        }
    }

    public void toggleRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_TOGGLE_RECENT_APPS);
            Message msg = this.mHandler.obtainMessage(MSG_TOGGLE_RECENT_APPS, 0, 0, null);
            msg.setAsynchronous(true);
            msg.sendToTarget();
        }
    }

    public void preloadRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_PRELOAD_RECENT_APPS);
            this.mHandler.obtainMessage(MSG_PRELOAD_RECENT_APPS, 0, 0, null).sendToTarget();
        }
    }

    public void cancelPreloadRecentApps() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_CANCEL_PRELOAD_RECENT_APPS);
            this.mHandler.obtainMessage(MSG_CANCEL_PRELOAD_RECENT_APPS, 0, 0, null).sendToTarget();
        }
    }

    public void dismissKeyboardShortcutsMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(2097152);
            this.mHandler.obtainMessage(2097152).sendToTarget();
        }
    }

    public void toggleKeyboardShortcutsMenu(int deviceId) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_TOGGLE_KEYBOARD_SHORTCUTS);
            this.mHandler.obtainMessage(MSG_TOGGLE_KEYBOARD_SHORTCUTS, deviceId, 0).sendToTarget();
        }
    }

    public void showPictureInPictureMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_SHOW_PICTURE_IN_PICTURE_MENU);
            this.mHandler.obtainMessage(MSG_SHOW_PICTURE_IN_PICTURE_MENU).sendToTarget();
        }
    }

    public void setWindowState(int displayId, int window, int state) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_SET_WINDOW_STATE, displayId, window, Integer.valueOf(state)).sendToTarget();
        }
    }

    public void showScreenPinningRequest(int taskId) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_SHOW_SCREEN_PIN_REQUEST, taskId, 0, null).sendToTarget();
        }
    }

    public void appTransitionPending(int displayId) {
        appTransitionPending(displayId, false);
    }

    public void appTransitionPending(int displayId, boolean forced) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_APP_TRANSITION_PENDING, displayId, forced ? 1 : 0).sendToTarget();
        }
    }

    public void appTransitionCancelled(int displayId) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_APP_TRANSITION_CANCELLED, displayId, 0).sendToTarget();
        }
    }

    public void appTransitionStarting(int displayId, long startTime, long duration) {
        appTransitionStarting(displayId, startTime, duration, false);
    }

    public void appTransitionStarting(int displayId, long startTime, long duration, boolean forced) {
        synchronized (this.mLock) {
            SomeArgs args = SomeArgs.obtain();
            args.argi1 = displayId;
            args.argi2 = forced ? 1 : 0;
            args.arg1 = Long.valueOf(startTime);
            args.arg2 = Long.valueOf(duration);
            this.mHandler.obtainMessage(MSG_APP_TRANSITION_STARTING, args).sendToTarget();
        }
    }

    public void appTransitionFinished(int displayId) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_APP_TRANSITION_FINISHED, displayId, 0).sendToTarget();
        }
    }

    public void showAssistDisclosure() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_ASSIST_DISCLOSURE);
            this.mHandler.obtainMessage(MSG_ASSIST_DISCLOSURE).sendToTarget();
        }
    }

    public void startAssist(Bundle args) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_START_ASSIST);
            this.mHandler.obtainMessage(MSG_START_ASSIST, args).sendToTarget();
        }
    }

    public void onCameraLaunchGestureDetected(int source) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_CAMERA_LAUNCH_GESTURE);
            this.mHandler.obtainMessage(MSG_CAMERA_LAUNCH_GESTURE, source, 0).sendToTarget();
        }
    }

    public void addQsTile(ComponentName tile) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_ADD_QS_TILE, tile).sendToTarget();
        }
    }

    public void remQsTile(ComponentName tile) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_REMOVE_QS_TILE, tile).sendToTarget();
        }
    }

    public void clickQsTile(ComponentName tile) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_CLICK_QS_TILE, tile).sendToTarget();
        }
    }

    public void handleSystemKey(int key) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_HANDLE_SYSTEM_KEY, key, 0).sendToTarget();
        }
    }

    public void showPinningEnterExitToast(boolean entering) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_SHOW_PINNING_TOAST_ENTER_EXIT, Boolean.valueOf(entering)).sendToTarget();
        }
    }

    public void showPinningEscapeToast() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_SHOW_PINNING_TOAST_ESCAPE).sendToTarget();
        }
    }

    public void showGlobalActionsMenu() {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_SHOW_GLOBAL_ACTIONS);
            this.mHandler.obtainMessage(MSG_SHOW_GLOBAL_ACTIONS).sendToTarget();
        }
    }

    public void setTopAppHidesStatusBar(boolean hidesStatusBar) {
        this.mHandler.removeMessages(MSG_SET_TOP_APP_HIDES_STATUS_BAR);
        this.mHandler.obtainMessage(MSG_SET_TOP_APP_HIDES_STATUS_BAR, hidesStatusBar ? 1 : 0, 0).sendToTarget();
    }

    public void showShutdownUi(boolean isReboot, String reason) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_SHOW_SHUTDOWN_UI);
            this.mHandler.obtainMessage(MSG_SHOW_SHUTDOWN_UI, isReboot ? 1 : 0, 0, reason).sendToTarget();
        }
    }

    public void showWirelessChargingAnimation(int batteryLevel) {
        this.mHandler.removeMessages(MSG_SHOW_CHARGING_ANIMATION);
        this.mHandler.obtainMessage(MSG_SHOW_CHARGING_ANIMATION, batteryLevel, 0).sendToTarget();
    }

    public void onProposedRotationChanged(int rotation, boolean isValid) {
        synchronized (this.mLock) {
            this.mHandler.removeMessages(MSG_ROTATION_PROPOSAL);
            this.mHandler.obtainMessage(MSG_ROTATION_PROPOSAL, rotation, isValid ? 1 : 0, null).sendToTarget();
        }
    }

    public void showBiometricDialog(Bundle bundle, IBiometricServiceReceiverInternal receiver, int type, boolean requireConfirmation, int userId) {
        synchronized (this.mLock) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = bundle;
            args.arg2 = receiver;
            args.argi1 = type;
            args.arg3 = Boolean.valueOf(requireConfirmation);
            args.argi2 = userId;
            this.mHandler.obtainMessage(MSG_BIOMETRIC_SHOW, args).sendToTarget();
        }
    }

    public void onBiometricAuthenticated(boolean authenticated, String failureReason) {
        synchronized (this.mLock) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = Boolean.valueOf(authenticated);
            args.arg2 = failureReason;
            this.mHandler.obtainMessage(MSG_BIOMETRIC_AUTHENTICATED, args).sendToTarget();
        }
    }

    public void onBiometricHelp(String message) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_BIOMETRIC_HELP, message).sendToTarget();
        }
    }

    public void onBiometricError(String error) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_BIOMETRIC_ERROR, error).sendToTarget();
        }
    }

    public void hideBiometricDialog() {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_BIOMETRIC_HIDE).sendToTarget();
        }
    }

    public void onDisplayReady(int displayId) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_DISPLAY_READY, displayId, 0).sendToTarget();
        }
    }

    public void onRecentsAnimationStateChanged(boolean running) {
        synchronized (this.mLock) {
            this.mHandler.obtainMessage(MSG_RECENTS_ANIMATION_STATE_CHANGED, running ? 1 : 0, 0).sendToTarget();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleShowImeButton(int displayId, IBinder token, int vis, int backDisposition, boolean showImeSwitcher) {
        int i;
        if (displayId == -1) {
            return;
        }
        if (!InputMethodSystemProperty.MULTI_CLIENT_IME_ENABLED && (i = this.mLastUpdatedImeDisplayId) != displayId && i != -1) {
            sendImeInvisibleStatusForPrevNavBar();
        }
        for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
            this.mCallbacks.get(i2).setImeWindowStatus(displayId, token, vis, backDisposition, showImeSwitcher);
        }
        this.mLastUpdatedImeDisplayId = displayId;
    }

    private void sendImeInvisibleStatusForPrevNavBar() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            this.mCallbacks.get(i).setImeWindowStatus(this.mLastUpdatedImeDisplayId, null, 4, 0, false);
        }
    }

    /* loaded from: classes24.dex */
    private final class H extends Handler {
        private H(Looper l) {
            super(l);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int what = msg.what & (-65536);
            switch (what) {
                case 65536:
                    int i = msg.arg1;
                    if (i == 1) {
                        Pair<String, StatusBarIcon> p = (Pair) msg.obj;
                        for (int i2 = 0; i2 < CommandQueue.this.mCallbacks.size(); i2++) {
                            ((Callbacks) CommandQueue.this.mCallbacks.get(i2)).setIcon((String) p.first, (StatusBarIcon) p.second);
                        }
                        return;
                    } else if (i == 2) {
                        for (int i3 = 0; i3 < CommandQueue.this.mCallbacks.size(); i3++) {
                            ((Callbacks) CommandQueue.this.mCallbacks.get(i3)).removeIcon((String) msg.obj);
                        }
                        return;
                    } else {
                        return;
                    }
                case 131072:
                    SomeArgs args = (SomeArgs) msg.obj;
                    for (int i4 = 0; i4 < CommandQueue.this.mCallbacks.size(); i4++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i4)).disable(args.argi1, args.argi2, args.argi3, args.argi4 != 0);
                    }
                    return;
                case CommandQueue.MSG_EXPAND_NOTIFICATIONS /* 196608 */:
                    for (int i5 = 0; i5 < CommandQueue.this.mCallbacks.size(); i5++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i5)).animateExpandNotificationsPanel();
                    }
                    return;
                case 262144:
                    for (int i6 = 0; i6 < CommandQueue.this.mCallbacks.size(); i6++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i6)).animateCollapsePanels(msg.arg1, msg.arg2 != 0);
                    }
                    return;
                case CommandQueue.MSG_EXPAND_SETTINGS /* 327680 */:
                    for (int i7 = 0; i7 < CommandQueue.this.mCallbacks.size(); i7++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i7)).animateExpandSettingsPanel((String) msg.obj);
                    }
                    return;
                case CommandQueue.MSG_SET_SYSTEMUI_VISIBILITY /* 393216 */:
                    SomeArgs args2 = (SomeArgs) msg.obj;
                    for (int i8 = 0; i8 < CommandQueue.this.mCallbacks.size(); i8++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i8)).setSystemUiVisibility(args2.argi1, args2.argi2, args2.argi3, args2.argi4, args2.argi5, (Rect) args2.arg1, (Rect) args2.arg2, args2.argi6 == 1);
                    }
                    args2.recycle();
                    return;
                case CommandQueue.MSG_DISPLAY_READY /* 458752 */:
                    for (int i9 = 0; i9 < CommandQueue.this.mCallbacks.size(); i9++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i9)).onDisplayReady(msg.arg1);
                    }
                    return;
                case 524288:
                    SomeArgs args3 = (SomeArgs) msg.obj;
                    CommandQueue.this.handleShowImeButton(args3.argi1, (IBinder) args3.arg1, args3.argi2, args3.argi3, args3.argi4 != 0);
                    return;
                case CommandQueue.MSG_TOGGLE_RECENT_APPS /* 589824 */:
                    for (int i10 = 0; i10 < CommandQueue.this.mCallbacks.size(); i10++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i10)).toggleRecentApps();
                    }
                    return;
                case CommandQueue.MSG_PRELOAD_RECENT_APPS /* 655360 */:
                    for (int i11 = 0; i11 < CommandQueue.this.mCallbacks.size(); i11++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i11)).preloadRecentApps();
                    }
                    return;
                case CommandQueue.MSG_CANCEL_PRELOAD_RECENT_APPS /* 720896 */:
                    for (int i12 = 0; i12 < CommandQueue.this.mCallbacks.size(); i12++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i12)).cancelPreloadRecentApps();
                    }
                    return;
                case CommandQueue.MSG_SET_WINDOW_STATE /* 786432 */:
                    for (int i13 = 0; i13 < CommandQueue.this.mCallbacks.size(); i13++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i13)).setWindowState(msg.arg1, msg.arg2, ((Integer) msg.obj).intValue());
                    }
                    return;
                case CommandQueue.MSG_SHOW_RECENT_APPS /* 851968 */:
                    for (int i14 = 0; i14 < CommandQueue.this.mCallbacks.size(); i14++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i14)).showRecentApps(msg.arg1 != 0);
                    }
                    return;
                case CommandQueue.MSG_HIDE_RECENT_APPS /* 917504 */:
                    for (int i15 = 0; i15 < CommandQueue.this.mCallbacks.size(); i15++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i15)).hideRecentApps(msg.arg1 != 0, msg.arg2 != 0);
                    }
                    return;
                case CommandQueue.MSG_SHOW_SCREEN_PIN_REQUEST /* 1179648 */:
                    for (int i16 = 0; i16 < CommandQueue.this.mCallbacks.size(); i16++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i16)).showScreenPinningRequest(msg.arg1);
                    }
                    return;
                case CommandQueue.MSG_APP_TRANSITION_PENDING /* 1245184 */:
                    for (int i17 = 0; i17 < CommandQueue.this.mCallbacks.size(); i17++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i17)).appTransitionPending(msg.arg1, msg.arg2 != 0);
                    }
                    return;
                case CommandQueue.MSG_APP_TRANSITION_CANCELLED /* 1310720 */:
                    for (int i18 = 0; i18 < CommandQueue.this.mCallbacks.size(); i18++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i18)).appTransitionCancelled(msg.arg1);
                    }
                    return;
                case CommandQueue.MSG_APP_TRANSITION_STARTING /* 1376256 */:
                    SomeArgs args4 = (SomeArgs) msg.obj;
                    for (int i19 = 0; i19 < CommandQueue.this.mCallbacks.size(); i19++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i19)).appTransitionStarting(args4.argi1, ((Long) args4.arg1).longValue(), ((Long) args4.arg2).longValue(), args4.argi2 != 0);
                    }
                    return;
                case CommandQueue.MSG_ASSIST_DISCLOSURE /* 1441792 */:
                    for (int i20 = 0; i20 < CommandQueue.this.mCallbacks.size(); i20++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i20)).showAssistDisclosure();
                    }
                    return;
                case CommandQueue.MSG_START_ASSIST /* 1507328 */:
                    for (int i21 = 0; i21 < CommandQueue.this.mCallbacks.size(); i21++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i21)).startAssist((Bundle) msg.obj);
                    }
                    return;
                case CommandQueue.MSG_CAMERA_LAUNCH_GESTURE /* 1572864 */:
                    for (int i22 = 0; i22 < CommandQueue.this.mCallbacks.size(); i22++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i22)).onCameraLaunchGestureDetected(msg.arg1);
                    }
                    return;
                case CommandQueue.MSG_TOGGLE_KEYBOARD_SHORTCUTS /* 1638400 */:
                    for (int i23 = 0; i23 < CommandQueue.this.mCallbacks.size(); i23++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i23)).toggleKeyboardShortcutsMenu(msg.arg1);
                    }
                    return;
                case CommandQueue.MSG_SHOW_PICTURE_IN_PICTURE_MENU /* 1703936 */:
                    for (int i24 = 0; i24 < CommandQueue.this.mCallbacks.size(); i24++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i24)).showPictureInPictureMenu();
                    }
                    return;
                case CommandQueue.MSG_ADD_QS_TILE /* 1769472 */:
                    for (int i25 = 0; i25 < CommandQueue.this.mCallbacks.size(); i25++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i25)).addQsTile((ComponentName) msg.obj);
                    }
                    return;
                case CommandQueue.MSG_REMOVE_QS_TILE /* 1835008 */:
                    for (int i26 = 0; i26 < CommandQueue.this.mCallbacks.size(); i26++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i26)).remQsTile((ComponentName) msg.obj);
                    }
                    return;
                case CommandQueue.MSG_CLICK_QS_TILE /* 1900544 */:
                    for (int i27 = 0; i27 < CommandQueue.this.mCallbacks.size(); i27++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i27)).clickTile((ComponentName) msg.obj);
                    }
                    return;
                case CommandQueue.MSG_TOGGLE_APP_SPLIT_SCREEN /* 1966080 */:
                    for (int i28 = 0; i28 < CommandQueue.this.mCallbacks.size(); i28++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i28)).toggleSplitScreen();
                    }
                    return;
                case CommandQueue.MSG_APP_TRANSITION_FINISHED /* 2031616 */:
                    for (int i29 = 0; i29 < CommandQueue.this.mCallbacks.size(); i29++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i29)).appTransitionFinished(msg.arg1);
                    }
                    return;
                case 2097152:
                    for (int i30 = 0; i30 < CommandQueue.this.mCallbacks.size(); i30++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i30)).dismissKeyboardShortcutsMenu();
                    }
                    return;
                case CommandQueue.MSG_HANDLE_SYSTEM_KEY /* 2162688 */:
                    for (int i31 = 0; i31 < CommandQueue.this.mCallbacks.size(); i31++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i31)).handleSystemKey(msg.arg1);
                    }
                    return;
                case CommandQueue.MSG_SHOW_GLOBAL_ACTIONS /* 2228224 */:
                    for (int i32 = 0; i32 < CommandQueue.this.mCallbacks.size(); i32++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i32)).handleShowGlobalActionsMenu();
                    }
                    return;
                case CommandQueue.MSG_TOGGLE_PANEL /* 2293760 */:
                    for (int i33 = 0; i33 < CommandQueue.this.mCallbacks.size(); i33++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i33)).togglePanel();
                    }
                    return;
                case CommandQueue.MSG_SHOW_SHUTDOWN_UI /* 2359296 */:
                    for (int i34 = 0; i34 < CommandQueue.this.mCallbacks.size(); i34++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i34)).handleShowShutdownUi(msg.arg1 != 0, (String) msg.obj);
                    }
                    return;
                case CommandQueue.MSG_SET_TOP_APP_HIDES_STATUS_BAR /* 2424832 */:
                    for (int i35 = 0; i35 < CommandQueue.this.mCallbacks.size(); i35++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i35)).setTopAppHidesStatusBar(msg.arg1 != 0);
                    }
                    return;
                case CommandQueue.MSG_ROTATION_PROPOSAL /* 2490368 */:
                    for (int i36 = 0; i36 < CommandQueue.this.mCallbacks.size(); i36++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i36)).onRotationProposal(msg.arg1, msg.arg2 != 0);
                    }
                    return;
                case CommandQueue.MSG_BIOMETRIC_SHOW /* 2555904 */:
                    CommandQueue.this.mHandler.removeMessages(CommandQueue.MSG_BIOMETRIC_ERROR);
                    CommandQueue.this.mHandler.removeMessages(CommandQueue.MSG_BIOMETRIC_HELP);
                    CommandQueue.this.mHandler.removeMessages(CommandQueue.MSG_BIOMETRIC_AUTHENTICATED);
                    SomeArgs someArgs = (SomeArgs) msg.obj;
                    for (int i37 = 0; i37 < CommandQueue.this.mCallbacks.size(); i37++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i37)).showBiometricDialog((Bundle) someArgs.arg1, (IBiometricServiceReceiverInternal) someArgs.arg2, someArgs.argi1, ((Boolean) someArgs.arg3).booleanValue(), someArgs.argi2);
                    }
                    someArgs.recycle();
                    return;
                case CommandQueue.MSG_BIOMETRIC_AUTHENTICATED /* 2621440 */:
                    SomeArgs someArgs2 = (SomeArgs) msg.obj;
                    for (int i38 = 0; i38 < CommandQueue.this.mCallbacks.size(); i38++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i38)).onBiometricAuthenticated(((Boolean) someArgs2.arg1).booleanValue(), (String) someArgs2.arg2);
                    }
                    someArgs2.recycle();
                    return;
                case CommandQueue.MSG_BIOMETRIC_HELP /* 2686976 */:
                    for (int i39 = 0; i39 < CommandQueue.this.mCallbacks.size(); i39++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i39)).onBiometricHelp((String) msg.obj);
                    }
                    return;
                case CommandQueue.MSG_BIOMETRIC_ERROR /* 2752512 */:
                    for (int i40 = 0; i40 < CommandQueue.this.mCallbacks.size(); i40++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i40)).onBiometricError((String) msg.obj);
                    }
                    return;
                case CommandQueue.MSG_BIOMETRIC_HIDE /* 2818048 */:
                    for (int i41 = 0; i41 < CommandQueue.this.mCallbacks.size(); i41++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i41)).hideBiometricDialog();
                    }
                    return;
                case CommandQueue.MSG_SHOW_CHARGING_ANIMATION /* 2883584 */:
                    for (int i42 = 0; i42 < CommandQueue.this.mCallbacks.size(); i42++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i42)).showWirelessChargingAnimation(msg.arg1);
                    }
                    return;
                case CommandQueue.MSG_SHOW_PINNING_TOAST_ENTER_EXIT /* 2949120 */:
                    for (int i43 = 0; i43 < CommandQueue.this.mCallbacks.size(); i43++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i43)).showPinningEnterExitToast(((Boolean) msg.obj).booleanValue());
                    }
                    return;
                case CommandQueue.MSG_SHOW_PINNING_TOAST_ESCAPE /* 3014656 */:
                    for (int i44 = 0; i44 < CommandQueue.this.mCallbacks.size(); i44++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i44)).showPinningEscapeToast();
                    }
                    return;
                case CommandQueue.MSG_RECENTS_ANIMATION_STATE_CHANGED /* 3080192 */:
                    for (int i45 = 0; i45 < CommandQueue.this.mCallbacks.size(); i45++) {
                        ((Callbacks) CommandQueue.this.mCallbacks.get(i45)).onRecentsAnimationStateChanged(msg.arg1 > 0);
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes24.dex */
    public static class CommandQueueStart extends SystemUI {
        @Override // com.android.systemui.SystemUI
        public void start() {
            putComponent(CommandQueue.class, new CommandQueue(this.mContext));
        }
    }
}
