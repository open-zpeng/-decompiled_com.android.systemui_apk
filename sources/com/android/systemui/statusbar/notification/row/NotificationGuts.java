package com.android.systemui.statusbar.notification.row;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
/* loaded from: classes21.dex */
public class NotificationGuts extends FrameLayout {
    private static final long CLOSE_GUTS_DELAY = 8000;
    private static final String TAG = "NotificationGuts";
    private int mActualHeight;
    private Drawable mBackground;
    private int mClipBottomAmount;
    private int mClipTopAmount;
    private OnGutsClosedListener mClosedListener;
    private boolean mExposed;
    private Runnable mFalsingCheck;
    private GutsContent mGutsContent;
    private Handler mHandler;
    private OnHeightChangedListener mHeightListener;
    private boolean mNeedsFalsingProtection;

    /* loaded from: classes21.dex */
    public interface OnGutsClosedListener {
        void onGutsClosed(NotificationGuts notificationGuts);
    }

    /* loaded from: classes21.dex */
    public interface OnHeightChangedListener {
        void onHeightChanged(NotificationGuts notificationGuts);
    }

    /* loaded from: classes21.dex */
    private interface OnSettingsClickListener {
        void onClick(View view, int i);
    }

    /* loaded from: classes21.dex */
    public interface GutsContent {
        int getActualHeight();

        View getContentView();

        boolean handleCloseControls(boolean z, boolean z2);

        void setGutsParent(NotificationGuts notificationGuts);

        boolean shouldBeSaved();

        boolean willBeRemoved();

        default boolean isLeavebehind() {
            return false;
        }

        default void onFinishedClosing() {
        }
    }

    public NotificationGuts(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        this.mHandler = new Handler();
        this.mFalsingCheck = new Runnable() { // from class: com.android.systemui.statusbar.notification.row.NotificationGuts.1
            @Override // java.lang.Runnable
            public void run() {
                if (NotificationGuts.this.mNeedsFalsingProtection && NotificationGuts.this.mExposed) {
                    NotificationGuts.this.closeControls(-1, -1, false, false);
                }
            }
        };
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Theme, 0, 0);
        ta.recycle();
    }

    public NotificationGuts(Context context) {
        this(context, null);
    }

    public void setGutsContent(GutsContent content) {
        this.mGutsContent = content;
        removeAllViews();
        addView(this.mGutsContent.getContentView());
    }

    public GutsContent getGutsContent() {
        return this.mGutsContent;
    }

    public void resetFalsingCheck() {
        this.mHandler.removeCallbacks(this.mFalsingCheck);
        if (this.mNeedsFalsingProtection && this.mExposed) {
            this.mHandler.postDelayed(this.mFalsingCheck, CLOSE_GUTS_DELAY);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        draw(canvas, this.mBackground);
    }

    private void draw(Canvas canvas, Drawable drawable) {
        int top = this.mClipTopAmount;
        int bottom = this.mActualHeight - this.mClipBottomAmount;
        if (drawable != null && top < bottom) {
            drawable.setBounds(0, top, getWidth(), bottom);
            drawable.draw(canvas);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mBackground = this.mContext.getDrawable(com.android.systemui.R.drawable.notification_guts_bg);
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mBackground;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        drawableStateChanged(this.mBackground);
    }

    private void drawableStateChanged(Drawable d) {
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float x, float y) {
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setHotspot(x, y);
        }
    }

    public void openControls(boolean shouldDoCircularReveal, int x, int y, boolean needsFalsingProtection, @Nullable Runnable onAnimationEnd) {
        animateOpen(shouldDoCircularReveal, x, y, onAnimationEnd);
        setExposed(true, needsFalsingProtection);
    }

    public void closeControls(boolean leavebehinds, boolean controls, int x, int y, boolean force) {
        GutsContent gutsContent = this.mGutsContent;
        if (gutsContent != null) {
            if ((gutsContent.isLeavebehind() && leavebehinds) || (!this.mGutsContent.isLeavebehind() && controls)) {
                closeControls(x, y, this.mGutsContent.shouldBeSaved(), force);
            }
        }
    }

    public void closeControls(int x, int y, boolean save, boolean force) {
        boolean wasBlockingHelperDismissed = ((NotificationBlockingHelperManager) Dependency.get(NotificationBlockingHelperManager.class)).dismissCurrentBlockingHelper();
        if (getWindowToken() == null) {
            OnGutsClosedListener onGutsClosedListener = this.mClosedListener;
            if (onGutsClosedListener != null) {
                onGutsClosedListener.onGutsClosed(this);
                return;
            }
            return;
        }
        GutsContent gutsContent = this.mGutsContent;
        if (gutsContent == null || !gutsContent.handleCloseControls(save, force) || wasBlockingHelperDismissed) {
            animateClose(x, y, !wasBlockingHelperDismissed);
            setExposed(false, this.mNeedsFalsingProtection);
            OnGutsClosedListener onGutsClosedListener2 = this.mClosedListener;
            if (onGutsClosedListener2 != null) {
                onGutsClosedListener2.onGutsClosed(this);
            }
        }
    }

    private void animateOpen(boolean shouldDoCircularReveal, int x, int y, @Nullable Runnable onAnimationEnd) {
        if (!isAttachedToWindow()) {
            Log.w(TAG, "Failed to animate guts open");
        } else if (shouldDoCircularReveal) {
            double horz = Math.max(getWidth() - x, x);
            double vert = Math.max(getHeight() - y, y);
            float r = (float) Math.hypot(horz, vert);
            setAlpha(1.0f);
            Animator a = ViewAnimationUtils.createCircularReveal(this, x, y, 0.0f, r);
            a.setDuration(360L);
            a.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
            a.addListener(new AnimateOpenListener(onAnimationEnd));
            a.start();
        } else {
            setAlpha(0.0f);
            animate().alpha(1.0f).setDuration(240L).setInterpolator(Interpolators.ALPHA_IN).setListener(new AnimateOpenListener(onAnimationEnd)).start();
        }
    }

    @VisibleForTesting
    void animateClose(int x, int y, boolean shouldDoCircularReveal) {
        if (!isAttachedToWindow()) {
            Log.w(TAG, "Failed to animate guts close");
            this.mGutsContent.onFinishedClosing();
        } else if (shouldDoCircularReveal) {
            if (x == -1 || y == -1) {
                y = getTop() + (getHeight() / 2);
                x = (getLeft() + getRight()) / 2;
            }
            double horz = Math.max(getWidth() - x, x);
            double vert = Math.max(getHeight() - y, y);
            float r = (float) Math.hypot(horz, vert);
            Animator a = ViewAnimationUtils.createCircularReveal(this, x, y, r, 0.0f);
            a.setDuration(360L);
            a.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
            a.addListener(new AnimateCloseListener(this, this.mGutsContent));
            a.start();
        } else {
            animate().alpha(0.0f).setDuration(240L).setInterpolator(Interpolators.ALPHA_OUT).setListener(new AnimateCloseListener(this, this.mGutsContent)).start();
        }
    }

    public void setActualHeight(int actualHeight) {
        this.mActualHeight = actualHeight;
        invalidate();
    }

    public int getActualHeight() {
        return this.mActualHeight;
    }

    public int getIntrinsicHeight() {
        GutsContent gutsContent = this.mGutsContent;
        return (gutsContent == null || !this.mExposed) ? getHeight() : gutsContent.getActualHeight();
    }

    public void setClipTopAmount(int clipTopAmount) {
        this.mClipTopAmount = clipTopAmount;
        invalidate();
    }

    public void setClipBottomAmount(int clipBottomAmount) {
        this.mClipBottomAmount = clipBottomAmount;
        invalidate();
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setClosedListener(OnGutsClosedListener listener) {
        this.mClosedListener = listener;
    }

    public void setHeightChangedListener(OnHeightChangedListener listener) {
        this.mHeightListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onHeightChanged() {
        OnHeightChangedListener onHeightChangedListener = this.mHeightListener;
        if (onHeightChangedListener != null) {
            onHeightChangedListener.onHeightChanged(this);
        }
    }

    @VisibleForTesting
    void setExposed(boolean exposed, boolean needsFalsingProtection) {
        GutsContent gutsContent;
        boolean wasExposed = this.mExposed;
        this.mExposed = exposed;
        this.mNeedsFalsingProtection = needsFalsingProtection;
        if (this.mExposed && this.mNeedsFalsingProtection) {
            resetFalsingCheck();
        } else {
            this.mHandler.removeCallbacks(this.mFalsingCheck);
        }
        if (wasExposed != this.mExposed && (gutsContent = this.mGutsContent) != null) {
            View contentView = gutsContent.getContentView();
            contentView.sendAccessibilityEvent(32);
            if (this.mExposed) {
                contentView.requestAccessibilityFocus();
            }
        }
    }

    public boolean willBeRemoved() {
        GutsContent gutsContent = this.mGutsContent;
        if (gutsContent != null) {
            return gutsContent.willBeRemoved();
        }
        return false;
    }

    public boolean isExposed() {
        return this.mExposed;
    }

    public boolean isLeavebehind() {
        GutsContent gutsContent = this.mGutsContent;
        return gutsContent != null && gutsContent.isLeavebehind();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class AnimateOpenListener extends AnimatorListenerAdapter {
        final Runnable mOnAnimationEnd;

        private AnimateOpenListener(Runnable onAnimationEnd) {
            this.mOnAnimationEnd = onAnimationEnd;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            Runnable runnable = this.mOnAnimationEnd;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class AnimateCloseListener extends AnimatorListenerAdapter {
        private final GutsContent mGutsContent;
        final View mView;

        private AnimateCloseListener(View view, GutsContent gutsContent) {
            this.mView = view;
            this.mGutsContent = gutsContent;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            if (!NotificationGuts.this.isExposed()) {
                this.mView.setVisibility(8);
                this.mGutsContent.onFinishedClosing();
            }
        }
    }
}
