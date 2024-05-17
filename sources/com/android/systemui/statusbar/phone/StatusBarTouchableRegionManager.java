package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import com.android.systemui.Dependency;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.policy.ConfigurationController;
/* loaded from: classes21.dex */
public final class StatusBarTouchableRegionManager implements ViewTreeObserver.OnComputeInternalInsetsListener, ConfigurationController.ConfigurationListener {
    private final Context mContext;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private final StatusBar mStatusBar;
    private int mStatusBarHeight;
    private final View mStatusBarWindowView;
    private final BubbleController mBubbleController = (BubbleController) Dependency.get(BubbleController.class);
    private boolean mIsStatusBarExpanded = false;
    private boolean mShouldAdjustInsets = false;
    private boolean mForceCollapsedUntilLayout = false;
    private final StatusBarWindowController mStatusBarWindowController = (StatusBarWindowController) Dependency.get(StatusBarWindowController.class);

    public StatusBarTouchableRegionManager(Context context, HeadsUpManagerPhone headsUpManager, StatusBar statusBar, View statusBarWindowView) {
        this.mContext = context;
        this.mHeadsUpManager = headsUpManager;
        this.mStatusBar = statusBar;
        this.mStatusBarWindowView = statusBarWindowView;
        initResources();
        this.mBubbleController.setBubbleStateChangeListener(new BubbleController.BubbleStateChangeListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarTouchableRegionManager$gPLkE4bCtewGoGzOAAWq2ceZj1A
            @Override // com.android.systemui.bubbles.BubbleController.BubbleStateChangeListener
            public final void onHasBubblesChanged(boolean z) {
                StatusBarTouchableRegionManager.this.lambda$new$0$StatusBarTouchableRegionManager(z);
            }
        });
        this.mStatusBarWindowController.setForcePluginOpenListener(new StatusBarWindowController.ForcePluginOpenListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$StatusBarTouchableRegionManager$hTH-4E1pAu4IyAouqMqgWhw8_mw
            @Override // com.android.systemui.statusbar.phone.StatusBarWindowController.ForcePluginOpenListener
            public final void onChange(boolean z) {
                StatusBarTouchableRegionManager.this.lambda$new$1$StatusBarTouchableRegionManager(z);
            }
        });
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
    }

    public /* synthetic */ void lambda$new$0$StatusBarTouchableRegionManager(boolean hasBubbles) {
        updateTouchableRegion();
    }

    public /* synthetic */ void lambda$new$1$StatusBarTouchableRegionManager(boolean forceOpen) {
        updateTouchableRegion();
    }

    public void updateTouchableRegion() {
        View view = this.mStatusBarWindowView;
        boolean shouldObserve = true;
        boolean hasCutoutInset = (view == null || view.getRootWindowInsets() == null || this.mStatusBarWindowView.getRootWindowInsets().getDisplayCutout() == null) ? false : true;
        if (!this.mHeadsUpManager.hasPinnedHeadsUp() && !this.mHeadsUpManager.isHeadsUpGoingAway() && !this.mBubbleController.hasBubbles() && !this.mForceCollapsedUntilLayout && !hasCutoutInset && !this.mStatusBarWindowController.getForcePluginOpen()) {
            shouldObserve = false;
        }
        if (shouldObserve == this.mShouldAdjustInsets) {
            return;
        }
        if (shouldObserve) {
            this.mStatusBarWindowView.getViewTreeObserver().addOnComputeInternalInsetsListener(this);
            this.mStatusBarWindowView.requestLayout();
        } else {
            this.mStatusBarWindowView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this);
        }
        this.mShouldAdjustInsets = shouldObserve;
    }

    public void updateTouchableRegionAfterLayout() {
        this.mForceCollapsedUntilLayout = true;
        this.mStatusBarWindowView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.systemui.statusbar.phone.StatusBarTouchableRegionManager.1
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (StatusBarTouchableRegionManager.this.mStatusBarWindowView.getHeight() <= StatusBarTouchableRegionManager.this.mStatusBarHeight) {
                    StatusBarTouchableRegionManager.this.mStatusBarWindowView.removeOnLayoutChangeListener(this);
                    StatusBarTouchableRegionManager.this.mForceCollapsedUntilLayout = false;
                    StatusBarTouchableRegionManager.this.updateTouchableRegion();
                }
            }
        });
    }

    public void setIsStatusBarExpanded(boolean isExpanded) {
        if (isExpanded != this.mIsStatusBarExpanded) {
            this.mIsStatusBarExpanded = isExpanded;
            if (isExpanded) {
                this.mForceCollapsedUntilLayout = false;
            }
            updateTouchableRegion();
        }
    }

    public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo info) {
        if (this.mIsStatusBarExpanded || this.mStatusBar.isBouncerShowing()) {
            return;
        }
        this.mHeadsUpManager.updateTouchableRegion(info);
        Rect bubbleRect = this.mBubbleController.getTouchableRegion();
        if (bubbleRect != null) {
            info.touchableRegion.union(bubbleRect);
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onConfigChanged(Configuration newConfig) {
        initResources();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        initResources();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onOverlayChanged() {
        initResources();
    }

    private void initResources() {
        Resources resources = this.mContext.getResources();
        this.mStatusBarHeight = resources.getDimensionPixelSize(17105438);
    }
}
