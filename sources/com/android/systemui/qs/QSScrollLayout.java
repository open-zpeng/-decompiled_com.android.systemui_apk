package com.android.systemui.qs;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.LinearLayout;
import androidx.core.widget.NestedScrollView;
import com.android.systemui.R;
import com.android.systemui.qs.touch.OverScroll;
import com.android.systemui.qs.touch.SwipeDetector;
/* loaded from: classes21.dex */
public class QSScrollLayout extends NestedScrollView {
    private static final Property<QSScrollLayout, Float> CONTENT_TRANS_Y = new Property<QSScrollLayout, Float>(Float.class, "qsScrollLayoutContentTransY") { // from class: com.android.systemui.qs.QSScrollLayout.1
        @Override // android.util.Property
        public Float get(QSScrollLayout qsScrollLayout) {
            return Float.valueOf(qsScrollLayout.mContentTranslationY);
        }

        @Override // android.util.Property
        public void set(QSScrollLayout qsScrollLayout, Float y) {
            qsScrollLayout.setContentTranslationY(y.floatValue());
        }
    };
    private float mContentTranslationY;
    private final int mFooterHeight;
    private int mLastMotionY;
    private final OverScrollHelper mOverScrollHelper;
    private final SwipeDetector mSwipeDetector;
    private final int mTouchSlop;

    public QSScrollLayout(Context context, View... children) {
        super(context);
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.mFooterHeight = getResources().getDimensionPixelSize(R.dimen.qs_footer_height);
        LinearLayout linearLayout = new LinearLayout(this.mContext);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        linearLayout.setOrientation(1);
        for (View view : children) {
            linearLayout.addView(view);
        }
        addView(linearLayout);
        setOverScrollMode(2);
        this.mOverScrollHelper = new OverScrollHelper();
        this.mSwipeDetector = new SwipeDetector(context, this.mOverScrollHelper, SwipeDetector.VERTICAL);
        this.mSwipeDetector.setDetectableScrollConditions(3, true);
    }

    @Override // androidx.core.widget.NestedScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (canScrollVertically(1) || canScrollVertically(-1)) {
            this.mSwipeDetector.onTouchEvent(ev);
            return super.onInterceptTouchEvent(ev) || this.mOverScrollHelper.isInOverScroll();
        }
        return false;
    }

    @Override // androidx.core.widget.NestedScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (!canScrollVertically(1) && !canScrollVertically(-1)) {
            return false;
        }
        this.mSwipeDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        canvas.translate(0.0f, this.mContentTranslationY);
        super.dispatchDraw(canvas);
        canvas.translate(0.0f, -this.mContentTranslationY);
    }

    public boolean shouldIntercept(MotionEvent ev) {
        if (ev.getY() > getBottom() - this.mFooterHeight) {
            return false;
        }
        if (ev.getActionMasked() == 0) {
            this.mLastMotionY = (int) ev.getY();
        } else if (ev.getActionMasked() == 2) {
            if (this.mLastMotionY >= 0 && Math.abs(ev.getY() - this.mLastMotionY) > this.mTouchSlop && canScrollVertically(1)) {
                requestParentDisallowInterceptTouchEvent(true);
                this.mLastMotionY = (int) ev.getY();
                return true;
            }
        } else if (ev.getActionMasked() == 3 || ev.getActionMasked() == 1) {
            this.mLastMotionY = -1;
            requestParentDisallowInterceptTouchEvent(false);
        }
        return false;
    }

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setContentTranslationY(float contentTranslationY) {
        this.mContentTranslationY = contentTranslationY;
        invalidate();
    }

    /* loaded from: classes21.dex */
    private class OverScrollHelper implements SwipeDetector.Listener {
        private float mFirstDisplacement;
        private boolean mIsInOverScroll;

        private OverScrollHelper() {
            this.mFirstDisplacement = 0.0f;
        }

        @Override // com.android.systemui.qs.touch.SwipeDetector.Listener
        public void onDragStart(boolean start) {
        }

        @Override // com.android.systemui.qs.touch.SwipeDetector.Listener
        public boolean onDrag(float displacement, float velocity) {
            boolean wasInOverScroll = this.mIsInOverScroll;
            boolean z = true;
            if ((QSScrollLayout.this.canScrollVertically(1) || displacement >= 0.0f) && (QSScrollLayout.this.canScrollVertically(-1) || displacement <= 0.0f)) {
                z = false;
            }
            this.mIsInOverScroll = z;
            if (wasInOverScroll && !this.mIsInOverScroll) {
                reset();
            } else if (this.mIsInOverScroll) {
                if (Float.compare(this.mFirstDisplacement, 0.0f) == 0) {
                    this.mFirstDisplacement = displacement;
                }
                float overscrollY = displacement - this.mFirstDisplacement;
                QSScrollLayout.this.setContentTranslationY(getDampedOverScroll(overscrollY));
            }
            return this.mIsInOverScroll;
        }

        @Override // com.android.systemui.qs.touch.SwipeDetector.Listener
        public void onDragEnd(float velocity, boolean fling) {
            reset();
        }

        private void reset() {
            if (Float.compare(QSScrollLayout.this.mContentTranslationY, 0.0f) != 0) {
                ObjectAnimator.ofFloat(QSScrollLayout.this, QSScrollLayout.CONTENT_TRANS_Y, 0.0f).setDuration(100L).start();
            }
            this.mIsInOverScroll = false;
            this.mFirstDisplacement = 0.0f;
        }

        public boolean isInOverScroll() {
            return this.mIsInOverScroll;
        }

        private float getDampedOverScroll(float y) {
            return OverScroll.dampedScroll(y, QSScrollLayout.this.getHeight());
        }
    }
}
