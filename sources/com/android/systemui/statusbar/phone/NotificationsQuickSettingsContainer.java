package com.android.systemui.statusbar.phone;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import androidx.annotation.DimenRes;
import com.android.systemui.R;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.statusbar.notification.AboveShelfObserver;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.ToIntFunction;
/* loaded from: classes21.dex */
public class NotificationsQuickSettingsContainer extends FrameLayout implements ViewStub.OnInflateListener, FragmentHostManager.FragmentListener, AboveShelfObserver.HasViewAboveShelfChangedListener {
    private int mBottomPadding;
    private boolean mCustomizerAnimating;
    private ArrayList<View> mDrawingOrderedChildren;
    private boolean mHasViewsAboveShelf;
    private final Comparator<View> mIndexComparator;
    private boolean mInflated;
    private View mKeyguardStatusBar;
    private ArrayList<View> mLayoutDrawingOrder;
    private boolean mQsExpanded;
    private FrameLayout mQsFrame;
    private NotificationStackScrollLayout mStackScroller;
    private int mStackScrollerMargin;
    private View mUserSwitcher;

    public NotificationsQuickSettingsContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDrawingOrderedChildren = new ArrayList<>();
        this.mLayoutDrawingOrder = new ArrayList<>();
        this.mIndexComparator = Comparator.comparingInt(new ToIntFunction() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$rYOLYKY9UUHboooVhy4ZToEslhI
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return NotificationsQuickSettingsContainer.this.indexOfChild((View) obj);
            }
        });
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mQsFrame = (FrameLayout) findViewById(R.id.qs_frame);
        this.mStackScroller = (NotificationStackScrollLayout) findViewById(R.id.notification_stack_scroller);
        this.mStackScrollerMargin = ((FrameLayout.LayoutParams) this.mStackScroller.getLayoutParams()).bottomMargin;
        this.mKeyguardStatusBar = findViewById(R.id.keyguard_header);
        ViewStub userSwitcher = (ViewStub) findViewById(R.id.keyguard_user_switcher);
        userSwitcher.setOnInflateListener(this);
        this.mUserSwitcher = userSwitcher;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FragmentHostManager.get(this).addTagListener(QS.TAG, this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FragmentHostManager.get(this).removeTagListener(QS.TAG, this);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        reloadWidth(this.mQsFrame, R.dimen.qs_panel_width);
        reloadWidth(this.mStackScroller, R.dimen.notification_panel_width);
    }

    private void reloadWidth(View view, @DimenRes int width) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = getResources().getDimensionPixelSize(width);
        view.setLayoutParams(params);
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        this.mBottomPadding = insets.getStableInsetBottom();
        setPadding(0, 0, 0, this.mBottomPadding);
        return insets;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        this.mDrawingOrderedChildren.clear();
        this.mLayoutDrawingOrder.clear();
        if (this.mInflated && this.mUserSwitcher.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add(this.mUserSwitcher);
            this.mLayoutDrawingOrder.add(this.mUserSwitcher);
        }
        if (this.mKeyguardStatusBar.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add(this.mKeyguardStatusBar);
            this.mLayoutDrawingOrder.add(this.mKeyguardStatusBar);
        }
        if (this.mStackScroller.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add(this.mStackScroller);
            this.mLayoutDrawingOrder.add(this.mStackScroller);
        }
        if (this.mQsFrame.getVisibility() == 0) {
            this.mDrawingOrderedChildren.add(this.mQsFrame);
            this.mLayoutDrawingOrder.add(this.mQsFrame);
        }
        if (this.mHasViewsAboveShelf) {
            this.mDrawingOrderedChildren.remove(this.mStackScroller);
            this.mDrawingOrderedChildren.add(this.mStackScroller);
        }
        this.mLayoutDrawingOrder.sort(this.mIndexComparator);
        super.dispatchDraw(canvas);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int layoutIndex = this.mLayoutDrawingOrder.indexOf(child);
        if (layoutIndex >= 0) {
            return super.drawChild(canvas, this.mDrawingOrderedChildren.get(layoutIndex), drawingTime);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override // android.view.ViewStub.OnInflateListener
    public void onInflate(ViewStub stub, View inflated) {
        if (stub == this.mUserSwitcher) {
            this.mUserSwitcher = inflated;
            this.mInflated = true;
        }
    }

    @Override // com.android.systemui.fragments.FragmentHostManager.FragmentListener
    public void onFragmentViewCreated(String tag, Fragment fragment) {
        QS container = (QS) fragment;
        container.setContainer(this);
    }

    public void setQsExpanded(boolean expanded) {
        if (this.mQsExpanded != expanded) {
            this.mQsExpanded = expanded;
            invalidate();
        }
    }

    public void setCustomizerAnimating(boolean isAnimating) {
        if (this.mCustomizerAnimating != isAnimating) {
            this.mCustomizerAnimating = isAnimating;
            invalidate();
        }
    }

    public void setCustomizerShowing(boolean isShowing) {
        if (!isShowing) {
            setPadding(0, 0, 0, this.mBottomPadding);
            setBottomMargin(this.mStackScroller, this.mStackScrollerMargin);
        } else {
            setPadding(0, 0, 0, 0);
            setBottomMargin(this.mStackScroller, 0);
        }
        this.mStackScroller.setQsCustomizerShowing(isShowing);
    }

    private void setBottomMargin(View v, int bottomMargin) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
        params.bottomMargin = bottomMargin;
        v.setLayoutParams(params);
    }

    @Override // com.android.systemui.statusbar.notification.AboveShelfObserver.HasViewAboveShelfChangedListener
    public void onHasViewsAboveShelfChanged(boolean hasViewsAboveShelf) {
        this.mHasViewsAboveShelf = hasViewsAboveShelf;
        invalidate();
    }
}
