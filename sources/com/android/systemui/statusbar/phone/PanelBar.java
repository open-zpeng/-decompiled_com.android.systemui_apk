package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
/* loaded from: classes21.dex */
public abstract class PanelBar extends FrameLayout {
    public static final boolean DEBUG = false;
    private static final String PANEL_BAR_SUPER_PARCELABLE = "panel_bar_super_parcelable";
    private static final boolean SPEW = false;
    private static final String STATE = "state";
    public static final int STATE_CLOSED = 0;
    public static final int STATE_OPEN = 2;
    public static final int STATE_OPENING = 1;
    public static final String TAG = PanelBar.class.getSimpleName();
    private boolean mBouncerShowing;
    private boolean mExpanded;
    PanelView mPanel;
    protected float mPanelFraction;
    private int mState;
    private boolean mTracking;

    public abstract void panelScrimMinFractionChanged(float f);

    public static final void LOG(String fmt, Object... args) {
    }

    public void go(int state) {
        this.mState = state;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PANEL_BAR_SUPER_PARCELABLE, super.onSaveInstanceState());
        bundle.putInt("state", this.mState);
        return bundle;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !(state instanceof Bundle)) {
            super.onRestoreInstanceState(state);
            return;
        }
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(PANEL_BAR_SUPER_PARCELABLE));
        if (((Bundle) state).containsKey("state")) {
            go(bundle.getInt("state", 0));
        }
    }

    public PanelBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mState = 0;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setPanel(PanelView pv) {
        this.mPanel = pv;
        pv.setBar(this);
    }

    public void setBouncerShowing(boolean showing) {
        this.mBouncerShowing = showing;
        int important = showing ? 4 : 0;
        setImportantForAccessibility(important);
        updateVisibility();
        PanelView panelView = this.mPanel;
        if (panelView != null) {
            panelView.setImportantForAccessibility(important);
        }
    }

    public float getExpansionFraction() {
        return this.mPanelFraction;
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateVisibility() {
        this.mPanel.setVisibility(shouldPanelBeVisible() ? 0 : 4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldPanelBeVisible() {
        return this.mExpanded || this.mBouncerShowing;
    }

    public boolean panelEnabled() {
        return true;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!panelEnabled()) {
            if (event.getAction() == 0) {
                Log.v(TAG, String.format("onTouch: all panels disabled, ignoring touch at (%d,%d)", Integer.valueOf((int) event.getX()), Integer.valueOf((int) event.getY())));
            }
            return false;
        }
        if (event.getAction() == 0) {
            PanelView panel = this.mPanel;
            if (panel == null) {
                Log.v(TAG, String.format("onTouch: no panel for touch at (%d,%d)", Integer.valueOf((int) event.getX()), Integer.valueOf((int) event.getY())));
                return true;
            }
            boolean enabled = panel.isEnabled();
            if (!enabled) {
                Log.v(TAG, String.format("onTouch: panel (%s) is disabled, ignoring touch at (%d,%d)", panel, Integer.valueOf((int) event.getX()), Integer.valueOf((int) event.getY())));
                return true;
            }
        }
        PanelView panel2 = this.mPanel;
        return panel2 == null || panel2.onTouchEvent(event);
    }

    public void panelExpansionChanged(float frac, boolean expanded) {
        if (Float.isNaN(frac)) {
            throw new IllegalArgumentException("frac cannot be NaN");
        }
        boolean fullyClosed = true;
        boolean fullyOpened = false;
        PanelView pv = this.mPanel;
        this.mExpanded = expanded;
        this.mPanelFraction = frac;
        updateVisibility();
        if (expanded) {
            if (this.mState == 0) {
                go(1);
                onPanelPeeked();
            }
            fullyClosed = false;
            float thisFrac = pv.getExpandedFraction();
            fullyOpened = thisFrac >= 1.0f;
        }
        if (fullyOpened && !this.mTracking) {
            go(2);
            onPanelFullyOpened();
        } else if (fullyClosed && !this.mTracking && this.mState != 0) {
            go(0);
            onPanelCollapsed();
        }
    }

    public void collapsePanel(boolean animate, boolean delayed, float speedUpFactor) {
        boolean waiting = false;
        PanelView pv = this.mPanel;
        if (animate && !pv.isFullyCollapsed()) {
            pv.collapse(delayed, speedUpFactor);
            waiting = true;
        } else {
            pv.resetViews(false);
            pv.setExpandedFraction(0.0f);
            pv.cancelPeek();
        }
        if (!waiting && this.mState != 0) {
            go(0);
            onPanelCollapsed();
        }
    }

    public void onPanelPeeked() {
    }

    public boolean isClosed() {
        return this.mState == 0;
    }

    public void onPanelCollapsed() {
    }

    public void onPanelFullyOpened() {
    }

    public void onTrackingStarted() {
        this.mTracking = true;
    }

    public void onTrackingStopped(boolean expand) {
        this.mTracking = false;
    }

    public void onExpandingFinished() {
    }

    public void onClosingFinished() {
    }
}
