package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.VisibleForTesting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
/* loaded from: classes21.dex */
public class NearestTouchFrame extends FrameLayout {
    private final ArrayList<View> mClickableChildren;
    private final boolean mIsActive;
    private final int[] mOffset;
    private final int[] mTmpInt;
    private View mTouchingChild;

    public NearestTouchFrame(Context context, AttributeSet attrs) {
        this(context, attrs, context.getResources().getConfiguration());
    }

    @VisibleForTesting
    NearestTouchFrame(Context context, AttributeSet attrs, Configuration c) {
        super(context, attrs);
        this.mClickableChildren = new ArrayList<>();
        this.mTmpInt = new int[2];
        this.mOffset = new int[2];
        this.mIsActive = c.smallestScreenWidthDp < 600;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mClickableChildren.clear();
        addClickableChildren(this);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getLocationInWindow(this.mOffset);
    }

    private void addClickableChildren(ViewGroup group) {
        int N = group.getChildCount();
        for (int i = 0; i < N; i++) {
            View child = group.getChildAt(i);
            if (child.isClickable()) {
                this.mClickableChildren.add(child);
            } else if (child instanceof ViewGroup) {
                addClickableChildren((ViewGroup) child);
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mIsActive) {
            if (event.getAction() == 0) {
                this.mTouchingChild = findNearestChild(event);
            }
            View view = this.mTouchingChild;
            if (view != null) {
                event.offsetLocation((view.getWidth() / 2) - event.getX(), (this.mTouchingChild.getHeight() / 2) - event.getY());
                return this.mTouchingChild.getVisibility() == 0 && this.mTouchingChild.dispatchTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }

    private View findNearestChild(final MotionEvent event) {
        if (this.mClickableChildren.isEmpty()) {
            return null;
        }
        return (View) this.mClickableChildren.stream().filter(new Predicate() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$dFYK0EjGBZUG5FTAJ9pyZPnsifY
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((View) obj).isAttachedToWindow();
            }
        }).map(new Function() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NearestTouchFrame$c68uozdLu3LZY-hrzFrFQ-dtMIM
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return NearestTouchFrame.this.lambda$findNearestChild$0$NearestTouchFrame(event, (View) obj);
            }
        }).min(Comparator.comparingInt(new ToIntFunction() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NearestTouchFrame$NP6mvtRuXVTLLChUNbbl4JUIMyU
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int intValue;
                intValue = ((Integer) ((Pair) obj).first).intValue();
                return intValue;
            }
        })).map(new Function() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NearestTouchFrame$KtkvB6kuUFBlaLB_chuEtrCrZqA
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return NearestTouchFrame.lambda$findNearestChild$2((Pair) obj);
            }
        }).orElse(null);
    }

    public /* synthetic */ Pair lambda$findNearestChild$0$NearestTouchFrame(MotionEvent event, View v) {
        return new Pair(Integer.valueOf(distance(v, event)), v);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ View lambda$findNearestChild$2(Pair data) {
        return (View) data.second;
    }

    private int distance(View v, MotionEvent event) {
        v.getLocationInWindow(this.mTmpInt);
        int[] iArr = this.mTmpInt;
        int i = iArr[0];
        int[] iArr2 = this.mOffset;
        int left = i - iArr2[0];
        int top = iArr[1] - iArr2[1];
        int right = v.getWidth() + left;
        int bottom = v.getHeight() + top;
        int x = Math.min(Math.abs(left - ((int) event.getX())), Math.abs(((int) event.getX()) - right));
        int y = Math.min(Math.abs(top - ((int) event.getY())), Math.abs(((int) event.getY()) - bottom));
        return Math.max(x, y);
    }
}
