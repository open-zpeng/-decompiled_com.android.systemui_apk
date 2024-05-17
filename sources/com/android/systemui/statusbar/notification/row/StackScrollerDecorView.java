package com.android.systemui.statusbar.notification.row;

import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Interpolators;
/* loaded from: classes21.dex */
public abstract class StackScrollerDecorView extends ExpandableView {
    protected View mContent;
    private boolean mContentAnimating;
    private final Runnable mContentVisibilityEndRunnable;
    private boolean mContentVisible;
    private int mDuration;
    private boolean mIsSecondaryVisible;
    private boolean mIsVisible;
    protected View mSecondaryView;

    protected abstract View findContentView();

    protected abstract View findSecondaryView();

    public /* synthetic */ void lambda$new$0$StackScrollerDecorView() {
        this.mContentAnimating = false;
        if (getVisibility() != 8 && !this.mIsVisible) {
            setVisibility(8);
            setWillBeGone(false);
            notifyHeightChanged(false);
        }
    }

    public StackScrollerDecorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsVisible = true;
        this.mContentVisible = true;
        this.mIsSecondaryVisible = true;
        this.mDuration = 260;
        this.mContentVisibilityEndRunnable = new Runnable() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$StackScrollerDecorView$GE_2dwloJkJho6ozN7VXOOo7f2I
            @Override // java.lang.Runnable
            public final void run() {
                StackScrollerDecorView.this.lambda$new$0$StackScrollerDecorView();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = findContentView();
        this.mSecondaryView = findSecondaryView();
        setVisible(false, false);
        setSecondaryVisible(false, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.ExpandableView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setOutlineProvider(null);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean isTransparent() {
        return true;
    }

    public void setContentVisible(boolean contentVisible) {
        setContentVisible(contentVisible, true);
    }

    private void setContentVisible(boolean contentVisible, boolean animate) {
        if (this.mContentVisible != contentVisible) {
            this.mContentAnimating = animate;
            setViewVisible(this.mContent, contentVisible, animate, this.mContentVisibilityEndRunnable);
            this.mContentVisible = contentVisible;
        }
        if (!this.mContentAnimating) {
            this.mContentVisibilityEndRunnable.run();
        }
    }

    public boolean isContentVisible() {
        return this.mContentVisible;
    }

    public void setVisible(boolean nowVisible, boolean animate) {
        if (this.mIsVisible != nowVisible) {
            this.mIsVisible = nowVisible;
            if (animate) {
                if (nowVisible) {
                    setVisibility(0);
                    setWillBeGone(false);
                    notifyHeightChanged(false);
                } else {
                    setWillBeGone(true);
                }
                setContentVisible(nowVisible, true);
                return;
            }
            setVisibility(nowVisible ? 0 : 8);
            setContentVisible(nowVisible, false);
            setWillBeGone(false);
            notifyHeightChanged(false);
        }
    }

    public void setSecondaryVisible(boolean nowVisible, boolean animate) {
        if (this.mIsSecondaryVisible != nowVisible) {
            setViewVisible(this.mSecondaryView, nowVisible, animate, null);
            this.mIsSecondaryVisible = nowVisible;
        }
    }

    @VisibleForTesting
    boolean isSecondaryVisible() {
        return this.mIsSecondaryVisible;
    }

    public boolean isVisible() {
        return this.mIsVisible;
    }

    void setDuration(int duration) {
        this.mDuration = duration;
    }

    private void setViewVisible(View view, boolean nowVisible, boolean animate, Runnable endRunnable) {
        if (view == null) {
            return;
        }
        view.animate().cancel();
        float endValue = nowVisible ? 1.0f : 0.0f;
        if (!animate) {
            view.setAlpha(endValue);
            if (endRunnable != null) {
                endRunnable.run();
                return;
            }
            return;
        }
        Interpolator interpolator = nowVisible ? Interpolators.ALPHA_IN : Interpolators.ALPHA_OUT;
        view.animate().alpha(endValue).setInterpolator(interpolator).setDuration(this.mDuration).withEndAction(endRunnable);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public long performRemoveAnimation(long duration, long delay, float translationDirection, boolean isHeadsUpAnimation, float endLocation, Runnable onFinishedRunnable, AnimatorListenerAdapter animationListener) {
        setContentVisible(false);
        return 0L;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void performAddAnimation(long delay, long duration, boolean isHeadsUpAppear) {
        setContentVisible(true);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView, android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }
}
