package com.android.systemui.statusbar.phone;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.EncryptionHelper;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.NetworkController;
/* loaded from: classes21.dex */
public class CollapsedStatusBarFragment extends Fragment implements CommandQueue.Callbacks, StatusBarStateController.StateListener {
    private static final String EXTRA_PANEL_STATE = "panel_state";
    public static final int FADE_IN_DELAY = 50;
    public static final int FADE_IN_DURATION = 320;
    public static final String STATUS_BAR_ICON_MANAGER_TAG = "status_bar_icon_manager";
    public static final String TAG = "CollapsedStatusBarFragment";
    private View mCenteredIconArea;
    private View mClockView;
    private CommandQueue mCommandQueue;
    private StatusBarIconController.DarkIconManager mDarkIconManager;
    private int mDisabled1;
    private KeyguardMonitor mKeyguardMonitor;
    private NetworkController mNetworkController;
    private View mNotificationIconAreaInner;
    private View mOperatorNameFrame;
    private NetworkController.SignalCallback mSignalCallback = new NetworkController.SignalCallback() { // from class: com.android.systemui.statusbar.phone.CollapsedStatusBarFragment.1
        @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
        public void setIsAirplaneMode(NetworkController.IconState icon) {
            CollapsedStatusBarFragment.this.mCommandQueue.recomputeDisableFlags(CollapsedStatusBarFragment.this.getContext().getDisplayId(), true);
        }
    };
    private PhoneStatusBarView mStatusBar;
    private StatusBar mStatusBarComponent;
    private StatusBarStateController mStatusBarStateController;
    private LinearLayout mSystemIconArea;

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
        this.mNetworkController = (NetworkController) Dependency.get(NetworkController.class);
        this.mStatusBarStateController = (StatusBarStateController) Dependency.get(StatusBarStateController.class);
        this.mStatusBarComponent = (StatusBar) SysUiServiceProvider.getComponent(getContext(), StatusBar.class);
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(getContext(), CommandQueue.class);
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.status_bar, container, false);
    }

    @Override // android.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mStatusBar = (PhoneStatusBarView) view;
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_PANEL_STATE)) {
            this.mStatusBar.restoreHierarchyState(savedInstanceState.getSparseParcelableArray(EXTRA_PANEL_STATE));
        }
        this.mDarkIconManager = new StatusBarIconController.DarkIconManager((LinearLayout) view.findViewById(R.id.statusIcons));
        this.mDarkIconManager.setShouldLog(true);
        ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).addIconGroup(this.mDarkIconManager);
        this.mSystemIconArea = (LinearLayout) this.mStatusBar.findViewById(R.id.system_icon_area);
        this.mClockView = this.mStatusBar.findViewById(R.id.clock);
        showSystemIconArea(false);
        showClock(false);
        initEmergencyCryptkeeperText();
        initOperatorName();
    }

    @Override // android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SparseArray<Parcelable> states = new SparseArray<>();
        this.mStatusBar.saveHierarchyState(states);
        outState.putSparseParcelableArray(EXTRA_PANEL_STATE, states);
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        this.mCommandQueue.addCallback((CommandQueue.Callbacks) this);
        this.mStatusBarStateController.addCallback(this);
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        this.mCommandQueue.removeCallback((CommandQueue.Callbacks) this);
        this.mStatusBarStateController.removeCallback(this);
    }

    @Override // android.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ((StatusBarIconController) Dependency.get(StatusBarIconController.class)).removeIconGroup(this.mDarkIconManager);
        if (this.mNetworkController.hasEmergencyCryptKeeperText()) {
            this.mNetworkController.removeCallback(this.mSignalCallback);
        }
    }

    public void initNotificationIconArea(NotificationIconAreaController notificationIconAreaController) {
        ViewGroup notificationIconArea = (ViewGroup) this.mStatusBar.findViewById(R.id.notification_icon_area);
        this.mNotificationIconAreaInner = notificationIconAreaController.getNotificationInnerAreaView();
        if (this.mNotificationIconAreaInner.getParent() != null) {
            ((ViewGroup) this.mNotificationIconAreaInner.getParent()).removeView(this.mNotificationIconAreaInner);
        }
        notificationIconArea.addView(this.mNotificationIconAreaInner);
        ViewGroup statusBarCenteredIconArea = (ViewGroup) this.mStatusBar.findViewById(R.id.centered_icon_area);
        this.mCenteredIconArea = notificationIconAreaController.getCenteredNotificationAreaView();
        if (this.mCenteredIconArea.getParent() != null) {
            ((ViewGroup) this.mCenteredIconArea.getParent()).removeView(this.mCenteredIconArea);
        }
        statusBarCenteredIconArea.addView(this.mCenteredIconArea);
        showNotificationIconArea(false);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void disable(int displayId, int state1, int state2, boolean animate) {
        if (displayId != getContext().getDisplayId()) {
            return;
        }
        int state12 = adjustDisableFlags(state1);
        int old1 = this.mDisabled1;
        int diff1 = state12 ^ old1;
        this.mDisabled1 = state12;
        if ((diff1 & 1048576) != 0) {
            if ((1048576 & state12) != 0) {
                hideSystemIconArea(animate);
                hideOperatorName(animate);
            } else {
                showSystemIconArea(animate);
                showOperatorName(animate);
            }
        }
        if ((diff1 & 131072) != 0) {
            if ((131072 & state12) != 0) {
                hideNotificationIconArea(animate);
            } else {
                showNotificationIconArea(animate);
            }
        }
        if ((diff1 & 8388608) != 0 || this.mClockView.getVisibility() != clockHiddenMode()) {
            if ((8388608 & state12) != 0) {
                hideClock(animate);
            } else {
                showClock(animate);
            }
        }
    }

    protected int adjustDisableFlags(int state) {
        boolean headsUpVisible = this.mStatusBarComponent.headsUpShouldBeVisible();
        if (headsUpVisible) {
            state |= 8388608;
        }
        if (!this.mKeyguardMonitor.isLaunchTransitionFadingAway() && !this.mKeyguardMonitor.isKeyguardFadingAway() && shouldHideNotificationIcons() && (this.mStatusBarStateController.getState() != 1 || !headsUpVisible)) {
            state = state | 131072 | 1048576 | 8388608;
        }
        if (this.mNetworkController != null && EncryptionHelper.IS_DATA_ENCRYPTED) {
            if (this.mNetworkController.hasEmergencyCryptKeeperText()) {
                state |= 131072;
            }
            if (!this.mNetworkController.isRadioOn()) {
                state |= 1048576;
            }
        }
        if (this.mStatusBarStateController.isDozing() && this.mStatusBarComponent.getPanel().hasCustomClock()) {
            return state | 9437184;
        }
        return state;
    }

    private boolean shouldHideNotificationIcons() {
        return (!this.mStatusBar.isClosed() && this.mStatusBarComponent.hideStatusBarIconsWhenExpanded()) || this.mStatusBarComponent.hideStatusBarIconsForBouncer();
    }

    public void hideSystemIconArea(boolean animate) {
        animateHide(this.mSystemIconArea, animate);
    }

    public void showSystemIconArea(boolean animate) {
        animateShow(this.mSystemIconArea, animate);
    }

    public void hideClock(boolean animate) {
        animateHiddenState(this.mClockView, clockHiddenMode(), animate);
    }

    public void showClock(boolean animate) {
        animateShow(this.mClockView, animate);
    }

    private int clockHiddenMode() {
        if (!this.mStatusBar.isClosed() && !this.mKeyguardMonitor.isShowing() && !this.mStatusBarStateController.isDozing()) {
            return 4;
        }
        return 8;
    }

    public void hideNotificationIconArea(boolean animate) {
        animateHide(this.mNotificationIconAreaInner, animate);
        animateHide(this.mCenteredIconArea, animate);
    }

    public void showNotificationIconArea(boolean animate) {
        animateShow(this.mNotificationIconAreaInner, animate);
        animateShow(this.mCenteredIconArea, animate);
    }

    public void hideOperatorName(boolean animate) {
        View view = this.mOperatorNameFrame;
        if (view != null) {
            animateHide(view, animate);
        }
    }

    public void showOperatorName(boolean animate) {
        View view = this.mOperatorNameFrame;
        if (view != null) {
            animateShow(view, animate);
        }
    }

    private void animateHiddenState(final View v, final int state, boolean animate) {
        v.animate().cancel();
        if (!animate) {
            v.setAlpha(0.0f);
            v.setVisibility(state);
            return;
        }
        v.animate().alpha(0.0f).setDuration(160L).setStartDelay(0L).setInterpolator(Interpolators.ALPHA_OUT).withEndAction(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$CollapsedStatusBarFragment$27RMKG7VU7GD3kVXbGdyl_3FVd4
            @Override // java.lang.Runnable
            public final void run() {
                v.setVisibility(state);
            }
        });
    }

    private void animateHide(View v, boolean animate) {
        animateHiddenState(v, 4, animate);
    }

    private void animateShow(View v, boolean animate) {
        v.animate().cancel();
        v.setVisibility(0);
        if (!animate) {
            v.setAlpha(1.0f);
            return;
        }
        v.animate().alpha(1.0f).setDuration(320L).setInterpolator(Interpolators.ALPHA_IN).setStartDelay(50L).withEndAction(null);
        if (this.mKeyguardMonitor.isKeyguardFadingAway()) {
            v.animate().setDuration(this.mKeyguardMonitor.getKeyguardFadingAwayDuration()).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).setStartDelay(this.mKeyguardMonitor.getKeyguardFadingAwayDelay()).start();
        }
    }

    private void initEmergencyCryptkeeperText() {
        View emergencyViewStub = this.mStatusBar.findViewById(R.id.emergency_cryptkeeper_text);
        if (this.mNetworkController.hasEmergencyCryptKeeperText()) {
            if (emergencyViewStub != null) {
                ((ViewStub) emergencyViewStub).inflate();
            }
            this.mNetworkController.addCallback(this.mSignalCallback);
        } else if (emergencyViewStub != null) {
            ViewGroup parent = (ViewGroup) emergencyViewStub.getParent();
            parent.removeView(emergencyViewStub);
        }
    }

    private void initOperatorName() {
        if (getResources().getBoolean(R.bool.config_showOperatorNameInStatusBar)) {
            ViewStub stub = (ViewStub) this.mStatusBar.findViewById(R.id.operator_name);
            this.mOperatorNameFrame = stub.inflate();
        }
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        int displayId = getContext().getDisplayId();
        int i = this.mDisabled1;
        disable(displayId, i, i, false);
    }
}
