package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.display.DisplayManager;
import android.hardware.input.InputManager;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.util.MathUtils;
import android.util.StatsLog;
import android.view.IPinnedStackController;
import android.view.IPinnedStackListener;
import android.view.ISystemGestureExclusionListener;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.InputMonitor;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.shared.system.MetricsLoggerCompat;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.statusbar.phone.EdgeBackGestureHandler;
import com.android.systemui.statusbar.phone.RegionSamplingHelper;
import com.xiaopeng.systemui.helper.WindowHelper;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
/* loaded from: classes21.dex */
public class EdgeBackGestureHandler implements DisplayManager.DisplayListener {
    private static final int MAX_LONG_PRESS_TIMEOUT = SystemProperties.getInt("gestures.back_timeout", 250);
    private static final String TAG = "EdgeBackGestureHandler";
    private final Context mContext;
    private final int mDisplayId;
    private NavigationBarEdgePanel mEdgePanel;
    private WindowManager.LayoutParams mEdgePanelLp;
    private int mEdgeWidth;
    private final int mFingerOffset;
    private InputEventReceiver mInputEventReceiver;
    private InputMonitor mInputMonitor;
    private boolean mIsAttached;
    private boolean mIsEnabled;
    private boolean mIsGesturalModeEnabled;
    private boolean mIsOnLeftEdge;
    private int mLeftInset;
    private final int mLongPressTimeout;
    private final Executor mMainExecutor;
    private final int mMinArrowPosition;
    private final int mNavBarHeight;
    private final OverviewProxyService mOverviewProxyService;
    private RegionSamplingHelper mRegionSamplingHelper;
    private int mRightInset;
    private final float mTouchSlop;
    private final WindowManager mWm;
    private final IPinnedStackListener.Stub mImeChangedListener = new IPinnedStackListener.Stub() { // from class: com.android.systemui.statusbar.phone.EdgeBackGestureHandler.1
        public void onListenerRegistered(IPinnedStackController controller) {
        }

        public void onImeVisibilityChanged(boolean imeVisible, int imeHeight) {
            EdgeBackGestureHandler.this.mImeHeight = imeVisible ? imeHeight : 0;
        }

        public void onShelfVisibilityChanged(boolean shelfVisible, int shelfHeight) {
        }

        public void onMinimizedStateChanged(boolean isMinimized) {
        }

        public void onMovementBoundsChanged(Rect insetBounds, Rect normalBounds, Rect animatingBounds, boolean fromImeAdjustment, boolean fromShelfAdjustment, int displayRotation) {
        }

        public void onActionsChanged(ParceledListSlice actions) {
        }
    };
    private ISystemGestureExclusionListener mGestureExclusionListener = new AnonymousClass2();
    private final Point mDisplaySize = new Point();
    private final Region mExcludeRegion = new Region();
    private final Region mUnrestrictedExcludeRegion = new Region();
    private final PointF mDownPoint = new PointF();
    private boolean mThresholdCrossed = false;
    private boolean mAllowGesture = false;
    private boolean mInRejectedExclusion = false;
    private int mImeHeight = 0;
    private final Rect mSamplingRect = new Rect();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.EdgeBackGestureHandler$2  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass2 extends ISystemGestureExclusionListener.Stub {
        AnonymousClass2() {
        }

        public void onSystemGestureExclusionChanged(int displayId, final Region systemGestureExclusion, final Region unrestrictedOrNull) {
            if (displayId == EdgeBackGestureHandler.this.mDisplayId) {
                EdgeBackGestureHandler.this.mMainExecutor.execute(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$EdgeBackGestureHandler$2$bflo4-0OxOxhTzQub5WRfHDxuRU
                    @Override // java.lang.Runnable
                    public final void run() {
                        EdgeBackGestureHandler.AnonymousClass2.this.lambda$onSystemGestureExclusionChanged$0$EdgeBackGestureHandler$2(systemGestureExclusion, unrestrictedOrNull);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onSystemGestureExclusionChanged$0$EdgeBackGestureHandler$2(Region systemGestureExclusion, Region unrestrictedOrNull) {
            EdgeBackGestureHandler.this.mExcludeRegion.set(systemGestureExclusion);
            EdgeBackGestureHandler.this.mUnrestrictedExcludeRegion.set(unrestrictedOrNull != null ? unrestrictedOrNull : systemGestureExclusion);
        }
    }

    public EdgeBackGestureHandler(Context context, OverviewProxyService overviewProxyService) {
        Resources res = context.getResources();
        this.mContext = context;
        this.mDisplayId = context.getDisplayId();
        this.mMainExecutor = context.getMainExecutor();
        this.mWm = (WindowManager) context.getSystemService(WindowManager.class);
        this.mOverviewProxyService = overviewProxyService;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 0.75f;
        this.mLongPressTimeout = Math.min(MAX_LONG_PRESS_TIMEOUT, ViewConfiguration.getLongPressTimeout());
        this.mNavBarHeight = res.getDimensionPixelSize(R.dimen.navigation_bar_frame_height);
        this.mMinArrowPosition = res.getDimensionPixelSize(R.dimen.navigation_edge_arrow_min_y);
        this.mFingerOffset = res.getDimensionPixelSize(R.dimen.navigation_edge_finger_offset);
        updateCurrentUserResources(res);
    }

    public void updateCurrentUserResources(Resources res) {
        this.mEdgeWidth = res.getDimensionPixelSize(17105051);
    }

    public void onNavBarAttached() {
        this.mIsAttached = true;
        updateIsEnabled();
    }

    public void onNavBarDetached() {
        this.mIsAttached = false;
        updateIsEnabled();
    }

    public void onNavigationModeChanged(int mode, Context currentUserContext) {
        this.mIsGesturalModeEnabled = QuickStepContract.isGesturalMode(mode);
        updateIsEnabled();
        updateCurrentUserResources(currentUserContext.getResources());
    }

    private void disposeInputChannel() {
        InputEventReceiver inputEventReceiver = this.mInputEventReceiver;
        if (inputEventReceiver != null) {
            inputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        InputMonitor inputMonitor = this.mInputMonitor;
        if (inputMonitor != null) {
            inputMonitor.dispose();
            this.mInputMonitor = null;
        }
    }

    private void updateIsEnabled() {
        boolean isEnabled = this.mIsAttached && this.mIsGesturalModeEnabled;
        if (isEnabled == this.mIsEnabled) {
            return;
        }
        this.mIsEnabled = isEnabled;
        disposeInputChannel();
        NavigationBarEdgePanel navigationBarEdgePanel = this.mEdgePanel;
        if (navigationBarEdgePanel != null) {
            this.mWm.removeView(navigationBarEdgePanel);
            this.mEdgePanel = null;
            this.mRegionSamplingHelper.stop();
            this.mRegionSamplingHelper = null;
        }
        if (!this.mIsEnabled) {
            WindowManagerWrapper.getInstance().removePinnedStackListener(this.mImeChangedListener);
            ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).unregisterDisplayListener(this);
            try {
                WindowManagerGlobal.getWindowManagerService().unregisterSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
                return;
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to unregister window manager callbacks", e);
                return;
            }
        }
        updateDisplaySize();
        ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).registerDisplayListener(this, this.mContext.getMainThreadHandler());
        try {
            WindowManagerWrapper.getInstance().addPinnedStackListener(this.mImeChangedListener);
            WindowManagerGlobal.getWindowManagerService().registerSystemGestureExclusionListener(this.mGestureExclusionListener, this.mDisplayId);
        } catch (RemoteException e2) {
            Log.e(TAG, "Failed to register window manager callbacks", e2);
        }
        this.mInputMonitor = InputManager.getInstance().monitorGestureInput("edge-swipe", this.mDisplayId);
        this.mInputEventReceiver = new SysUiInputEventReceiver(this.mInputMonitor.getInputChannel(), Looper.getMainLooper());
        this.mEdgePanel = new NavigationBarEdgePanel(this.mContext);
        this.mEdgePanelLp = new WindowManager.LayoutParams(this.mContext.getResources().getDimensionPixelSize(R.dimen.navigation_edge_panel_width), this.mContext.getResources().getDimensionPixelSize(R.dimen.navigation_edge_panel_height), WindowHelper.TYPE_NAVIGATION_BAR_PANEL, 8388904, -3);
        this.mEdgePanelLp.privateFlags |= 16;
        this.mEdgePanelLp.setTitle(TAG + this.mDisplayId);
        this.mEdgePanelLp.accessibilityTitle = this.mContext.getString(R.string.nav_bar_edge_panel);
        WindowManager.LayoutParams layoutParams = this.mEdgePanelLp;
        layoutParams.windowAnimations = 0;
        this.mEdgePanel.setLayoutParams(layoutParams);
        this.mWm.addView(this.mEdgePanel, this.mEdgePanelLp);
        this.mRegionSamplingHelper = new RegionSamplingHelper(this.mEdgePanel, new RegionSamplingHelper.SamplingCallback() { // from class: com.android.systemui.statusbar.phone.EdgeBackGestureHandler.3
            @Override // com.android.systemui.statusbar.phone.RegionSamplingHelper.SamplingCallback
            public void onRegionDarknessChanged(boolean isRegionDark) {
                EdgeBackGestureHandler.this.mEdgePanel.setIsDark(!isRegionDark, true);
            }

            @Override // com.android.systemui.statusbar.phone.RegionSamplingHelper.SamplingCallback
            public Rect getSampledRegion(View sampledView) {
                return EdgeBackGestureHandler.this.mSamplingRect;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onInputEvent(InputEvent ev) {
        if (ev instanceof MotionEvent) {
            onMotionEvent((MotionEvent) ev);
        }
    }

    private boolean isWithinTouchRegion(int x, int y) {
        if (y > this.mDisplaySize.y - Math.max(this.mImeHeight, this.mNavBarHeight)) {
            return false;
        }
        if (x <= this.mEdgeWidth + this.mLeftInset || x >= (this.mDisplaySize.x - this.mEdgeWidth) - this.mRightInset) {
            boolean isInExcludedRegion = this.mExcludeRegion.contains(x, y);
            if (isInExcludedRegion) {
                this.mOverviewProxyService.notifyBackAction(false, -1, -1, false, !this.mIsOnLeftEdge);
                StatsLog.write(MetricsLoggerCompat.OVERVIEW_ACTIVITY, 3, y, this.mIsOnLeftEdge ? 1 : 2);
            } else {
                this.mInRejectedExclusion = this.mUnrestrictedExcludeRegion.contains(x, y);
            }
            return !isInExcludedRegion;
        }
        return false;
    }

    private void cancelGesture(MotionEvent ev) {
        this.mAllowGesture = false;
        this.mInRejectedExclusion = false;
        MotionEvent cancelEv = MotionEvent.obtain(ev);
        cancelEv.setAction(3);
        this.mEdgePanel.handleTouch(cancelEv);
        cancelEv.recycle();
    }

    private void onMotionEvent(MotionEvent ev) {
        int i;
        int action = ev.getActionMasked();
        boolean z = true;
        if (action == 0) {
            int stateFlags = this.mOverviewProxyService.getSystemUiStateFlags();
            this.mIsOnLeftEdge = ev.getX() <= ((float) (this.mEdgeWidth + this.mLeftInset));
            this.mInRejectedExclusion = false;
            if (QuickStepContract.isBackGestureDisabled(stateFlags) || !isWithinTouchRegion((int) ev.getX(), (int) ev.getY())) {
                z = false;
            }
            this.mAllowGesture = z;
            if (this.mAllowGesture) {
                WindowManager.LayoutParams layoutParams = this.mEdgePanelLp;
                if (this.mIsOnLeftEdge) {
                    i = 51;
                } else {
                    i = 53;
                }
                layoutParams.gravity = i;
                this.mEdgePanel.setIsLeftPanel(this.mIsOnLeftEdge);
                this.mEdgePanel.handleTouch(ev);
                updateEdgePanelPosition(ev.getY());
                this.mWm.updateViewLayout(this.mEdgePanel, this.mEdgePanelLp);
                this.mRegionSamplingHelper.start(this.mSamplingRect);
                this.mDownPoint.set(ev.getX(), ev.getY());
                this.mThresholdCrossed = false;
            }
        } else if (this.mAllowGesture) {
            if (!this.mThresholdCrossed) {
                if (action == 5) {
                    cancelGesture(ev);
                    return;
                } else if (action == 2) {
                    if (ev.getEventTime() - ev.getDownTime() > this.mLongPressTimeout) {
                        cancelGesture(ev);
                        return;
                    }
                    float dx = Math.abs(ev.getX() - this.mDownPoint.x);
                    float dy = Math.abs(ev.getY() - this.mDownPoint.y);
                    if (dy > dx && dy > this.mTouchSlop) {
                        cancelGesture(ev);
                        return;
                    } else if (dx > dy && dx > this.mTouchSlop) {
                        this.mThresholdCrossed = true;
                        this.mInputMonitor.pilferPointers();
                    }
                }
            }
            this.mEdgePanel.handleTouch(ev);
            boolean isUp = action == 1;
            if (isUp) {
                boolean performAction = this.mEdgePanel.shouldTriggerBack();
                int i2 = 4;
                if (performAction) {
                    sendEvent(0, 4);
                    sendEvent(1, 4);
                }
                this.mOverviewProxyService.notifyBackAction(performAction, (int) this.mDownPoint.x, (int) this.mDownPoint.y, false, !this.mIsOnLeftEdge);
                if (performAction) {
                    if (this.mInRejectedExclusion) {
                        i2 = 2;
                    } else {
                        i2 = 1;
                    }
                }
                int backtype = i2;
                StatsLog.write(MetricsLoggerCompat.OVERVIEW_ACTIVITY, backtype, (int) this.mDownPoint.y, this.mIsOnLeftEdge ? 1 : 2);
            }
            if (isUp || action == 3) {
                this.mRegionSamplingHelper.stop();
                return;
            }
            updateSamplingRect();
            this.mRegionSamplingHelper.updateSamplingRect();
        }
    }

    private void updateEdgePanelPosition(float touchY) {
        float position = touchY - this.mFingerOffset;
        float position2 = Math.max(position, this.mMinArrowPosition) - (this.mEdgePanelLp.height / 2.0f);
        this.mEdgePanelLp.y = MathUtils.constrain((int) position2, 0, this.mDisplaySize.y);
        updateSamplingRect();
    }

    private void updateSamplingRect() {
        int top = this.mEdgePanelLp.y;
        int left = this.mIsOnLeftEdge ? this.mLeftInset : (this.mDisplaySize.x - this.mRightInset) - this.mEdgePanelLp.width;
        int right = this.mEdgePanelLp.width + left;
        int bottom = this.mEdgePanelLp.height + top;
        this.mSamplingRect.set(left, top, right, bottom);
        this.mEdgePanel.adjustRectToBoundingBox(this.mSamplingRect);
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayAdded(int displayId) {
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayRemoved(int displayId) {
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayChanged(int displayId) {
        if (displayId == this.mDisplayId) {
            updateDisplaySize();
        }
    }

    private void updateDisplaySize() {
        ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(this.mDisplayId).getRealSize(this.mDisplaySize);
    }

    private void sendEvent(int action, int code) {
        long when = SystemClock.uptimeMillis();
        KeyEvent ev = new KeyEvent(when, when, action, code, 0, 0, -1, 0, 72, 257);
        BubbleController bubbleController = (BubbleController) Dependency.get(BubbleController.class);
        int bubbleDisplayId = bubbleController.getExpandedDisplayId(this.mContext);
        if (code == 4 && bubbleDisplayId != -1) {
            ev.setDisplayId(bubbleDisplayId);
        }
        InputManager.getInstance().injectInputEvent(ev, 0);
    }

    public void setInsets(int leftInset, int rightInset) {
        this.mLeftInset = leftInset;
        this.mRightInset = rightInset;
    }

    public void dump(PrintWriter pw) {
        pw.println("EdgeBackGestureHandler:");
        pw.println("  mIsEnabled=" + this.mIsEnabled);
        pw.println("  mAllowGesture=" + this.mAllowGesture);
        pw.println("  mInRejectedExclusion" + this.mInRejectedExclusion);
        pw.println("  mExcludeRegion=" + this.mExcludeRegion);
        pw.println("  mUnrestrictedExcludeRegion=" + this.mUnrestrictedExcludeRegion);
        pw.println("  mImeHeight=" + this.mImeHeight);
        pw.println("  mIsAttached=" + this.mIsAttached);
        pw.println("  mEdgeWidth=" + this.mEdgeWidth);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public class SysUiInputEventReceiver extends InputEventReceiver {
        SysUiInputEventReceiver(InputChannel channel, Looper looper) {
            super(channel, looper);
        }

        public void onInputEvent(InputEvent event) {
            EdgeBackGestureHandler.this.onInputEvent(event);
            finishInputEvent(event, true);
        }
    }
}
