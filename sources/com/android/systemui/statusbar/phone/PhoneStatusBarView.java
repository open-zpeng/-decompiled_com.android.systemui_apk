package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.util.leak.RotationUtils;
import java.util.Objects;
/* loaded from: classes21.dex */
public class PhoneStatusBarView extends PanelBar {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_GESTURES = false;
    private static final int NO_VALUE = Integer.MIN_VALUE;
    private static final String TAG = "PhoneStatusBarView";
    StatusBar mBar;
    private DarkIconDispatcher.DarkReceiver mBattery;
    private View mCenterIconSpace;
    private final CommandQueue mCommandQueue;
    private int mCutoutSideNudge;
    private View mCutoutSpace;
    private DisplayCutout mDisplayCutout;
    private boolean mHeadsUpVisible;
    private Runnable mHideExpandedRunnable;
    boolean mIsFullyOpenedPanel;
    private int mLastOrientation;
    private float mMinFraction;
    private int mRotationOrientation;
    private ScrimController mScrimController;
    private int mStatusBarHeight;

    public PhoneStatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsFullyOpenedPanel = false;
        this.mHideExpandedRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.PhoneStatusBarView.1
            @Override // java.lang.Runnable
            public void run() {
                if (PhoneStatusBarView.this.mPanelFraction == 0.0f) {
                    PhoneStatusBarView.this.mBar.makeExpandedInvisible();
                }
            }
        };
        this.mCutoutSideNudge = 0;
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class);
    }

    public void setBar(StatusBar bar) {
        this.mBar = bar;
    }

    public void setScrimController(ScrimController scrimController) {
        this.mScrimController = scrimController;
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar, android.view.View
    public void onFinishInflate() {
        this.mBattery = (DarkIconDispatcher.DarkReceiver) findViewById(R.id.battery);
        this.mCutoutSpace = findViewById(R.id.cutout_space_view);
        this.mCenterIconSpace = findViewById(R.id.centered_icon_area);
        updateResources();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class)).addDarkReceiver(this.mBattery);
        if (updateOrientationAndCutout(getResources().getConfiguration().orientation)) {
            updateLayoutForCutout();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class)).removeDarkReceiver(this.mBattery);
        this.mDisplayCutout = null;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateResources();
        if (updateOrientationAndCutout(newConfig.orientation)) {
            updateLayoutForCutout();
            requestLayout();
        }
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (updateOrientationAndCutout(this.mLastOrientation)) {
            updateLayoutForCutout();
            requestLayout();
        }
        return super.onApplyWindowInsets(insets);
    }

    private boolean updateOrientationAndCutout(int newOrientation) {
        boolean changed = false;
        if (newOrientation != Integer.MIN_VALUE) {
            if (this.mLastOrientation != newOrientation) {
                changed = true;
                this.mLastOrientation = newOrientation;
            }
            this.mRotationOrientation = RotationUtils.getExactRotation(this.mContext);
        }
        if (!Objects.equals(getRootWindowInsets().getDisplayCutout(), this.mDisplayCutout)) {
            this.mDisplayCutout = getRootWindowInsets().getDisplayCutout();
            return true;
        }
        return changed;
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar
    public boolean panelEnabled() {
        return this.mCommandQueue.panelsEnabled();
    }

    public boolean onRequestSendAccessibilityEventInternal(View child, AccessibilityEvent event) {
        if (super.onRequestSendAccessibilityEventInternal(child, event)) {
            AccessibilityEvent record = AccessibilityEvent.obtain();
            onInitializeAccessibilityEvent(record);
            dispatchPopulateAccessibilityEvent(record);
            event.appendRecord(record);
            return true;
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar
    public void onPanelPeeked() {
        super.onPanelPeeked();
        this.mBar.makeExpandedVisible(false);
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar
    public void onPanelCollapsed() {
        super.onPanelCollapsed();
        post(this.mHideExpandedRunnable);
        this.mIsFullyOpenedPanel = false;
    }

    public void removePendingHideExpandedRunnables() {
        removeCallbacks(this.mHideExpandedRunnable);
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar
    public void onPanelFullyOpened() {
        super.onPanelFullyOpened();
        if (!this.mIsFullyOpenedPanel) {
            this.mPanel.sendAccessibilityEvent(32);
        }
        this.mIsFullyOpenedPanel = true;
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        boolean barConsumedEvent = this.mBar.interceptTouchEvent(event);
        return barConsumedEvent || super.onTouchEvent(event);
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar
    public void onTrackingStarted() {
        super.onTrackingStarted();
        this.mBar.onTrackingStarted();
        this.mScrimController.onTrackingStarted();
        removePendingHideExpandedRunnables();
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar
    public void onClosingFinished() {
        super.onClosingFinished();
        this.mBar.onClosingFinished();
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar
    public void onTrackingStopped(boolean expand) {
        super.onTrackingStopped(expand);
        this.mBar.onTrackingStopped(expand);
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar
    public void onExpandingFinished() {
        super.onExpandingFinished();
        this.mScrimController.onExpandingFinished();
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.mBar.interceptTouchEvent(event) || super.onInterceptTouchEvent(event);
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar
    public void panelScrimMinFractionChanged(float minFraction) {
        if (Float.isNaN(minFraction)) {
            throw new IllegalArgumentException("minFraction cannot be NaN");
        }
        if (this.mMinFraction != minFraction) {
            this.mMinFraction = minFraction;
            updateScrimFraction();
        }
    }

    @Override // com.android.systemui.statusbar.phone.PanelBar
    public void panelExpansionChanged(float frac, boolean expanded) {
        super.panelExpansionChanged(frac, expanded);
        updateScrimFraction();
        if ((frac == 0.0f || frac == 1.0f) && this.mBar.getNavigationBarView() != null) {
            this.mBar.getNavigationBarView().onStatusBarPanelStateChanged();
        }
    }

    private void updateScrimFraction() {
        float scrimFraction = this.mPanelFraction;
        if (this.mMinFraction < 1.0f) {
            float f = this.mPanelFraction;
            float f2 = this.mMinFraction;
            scrimFraction = Math.max((f - f2) / (1.0f - f2), 0.0f);
        }
        this.mScrimController.setPanelExpansion(scrimFraction);
    }

    public void updateResources() {
        this.mCutoutSideNudge = getResources().getDimensionPixelSize(R.dimen.display_cutout_margin_consumption);
        boolean isRtl = getLayoutDirection() == 1;
        int statusBarPaddingTop = getResources().getDimensionPixelSize(R.dimen.status_bar_padding_top);
        int statusBarPaddingStart = getResources().getDimensionPixelSize(R.dimen.status_bar_padding_start);
        int statusBarPaddingEnd = getResources().getDimensionPixelSize(R.dimen.status_bar_padding_end);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        this.mStatusBarHeight = getResources().getDimensionPixelSize(R.dimen.status_bar_height);
        layoutParams.height = this.mStatusBarHeight;
        View sbContents = findViewById(R.id.status_bar_contents);
        sbContents.setPadding(isRtl ? statusBarPaddingEnd : statusBarPaddingStart, statusBarPaddingTop, isRtl ? statusBarPaddingStart : statusBarPaddingEnd, 0);
        findViewById(R.id.notification_lights_out).setPadding(0, statusBarPaddingStart, 0, 0);
        setLayoutParams(layoutParams);
    }

    private void updateLayoutForCutout() {
        Pair<Integer, Integer> cornerCutoutMargins = cornerCutoutMargins(this.mDisplayCutout, getDisplay(), this.mRotationOrientation, this.mStatusBarHeight);
        updateCutoutLocation(cornerCutoutMargins);
        updateSafeInsets(cornerCutoutMargins);
    }

    private void updateCutoutLocation(Pair<Integer, Integer> cornerCutoutMargins) {
        if (this.mCutoutSpace == null) {
            return;
        }
        DisplayCutout displayCutout = this.mDisplayCutout;
        if (displayCutout == null || displayCutout.isEmpty() || this.mLastOrientation != 1 || cornerCutoutMargins != null) {
            this.mCenterIconSpace.setVisibility(0);
            this.mCutoutSpace.setVisibility(8);
            return;
        }
        this.mCenterIconSpace.setVisibility(8);
        this.mCutoutSpace.setVisibility(0);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mCutoutSpace.getLayoutParams();
        Rect bounds = new Rect();
        ScreenDecorations.DisplayCutoutView.boundsFromDirection(this.mDisplayCutout, 48, bounds);
        bounds.left += this.mCutoutSideNudge;
        bounds.right -= this.mCutoutSideNudge;
        lp.width = bounds.width();
        lp.height = bounds.height();
    }

    private void updateSafeInsets(Pair<Integer, Integer> cornerCutoutMargins) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        DisplayCutout displayCutout = this.mDisplayCutout;
        if (displayCutout == null || displayCutout.isEmpty() || cornerCutoutMargins == null) {
            lp.leftMargin = 0;
            lp.rightMargin = 0;
            return;
        }
        lp.leftMargin = ((Integer) cornerCutoutMargins.first).intValue();
        lp.rightMargin = ((Integer) cornerCutoutMargins.second).intValue();
        WindowInsets insets = getRootWindowInsets();
        int leftInset = insets.getSystemWindowInsetLeft();
        int rightInset = insets.getSystemWindowInsetRight();
        if (lp.leftMargin <= leftInset) {
            lp.leftMargin = 0;
        }
        if (lp.rightMargin <= rightInset) {
            lp.rightMargin = 0;
        }
    }

    public static Pair<Integer, Integer> cornerCutoutMargins(DisplayCutout cutout, Display display) {
        return cornerCutoutMargins(cutout, display, 0, -1);
    }

    private static Pair<Integer, Integer> cornerCutoutMargins(DisplayCutout cutout, Display display, int rotationOrientation, int statusBarHeight) {
        if (cutout == null) {
            return null;
        }
        Point size = new Point();
        display.getRealSize(size);
        if (rotationOrientation != 0) {
            return new Pair<>(Integer.valueOf(cutout.getSafeInsetLeft()), Integer.valueOf(cutout.getSafeInsetRight()));
        }
        Rect bounds = new Rect();
        ScreenDecorations.DisplayCutoutView.boundsFromDirection(cutout, 48, bounds);
        if (statusBarHeight >= 0 && bounds.top > statusBarHeight) {
            return null;
        }
        if (bounds.left <= 0) {
            return new Pair<>(Integer.valueOf(bounds.right), 0);
        }
        if (bounds.right < size.x) {
            return null;
        }
        return new Pair<>(0, Integer.valueOf(size.x - bounds.left));
    }

    public void setHeadsUpVisible(boolean headsUpVisible) {
        this.mHeadsUpVisible = headsUpVisible;
        updateVisibility();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.phone.PanelBar
    public boolean shouldPanelBeVisible() {
        return this.mHeadsUpVisible || super.shouldPanelBeVisible();
    }
}
