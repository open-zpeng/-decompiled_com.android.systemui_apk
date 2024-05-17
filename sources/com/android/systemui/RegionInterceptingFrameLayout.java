package com.android.systemui;

import android.content.Context;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
/* loaded from: classes21.dex */
public class RegionInterceptingFrameLayout extends FrameLayout {
    private final ViewTreeObserver.OnComputeInternalInsetsListener mInsetsListener;

    public RegionInterceptingFrameLayout(Context context) {
        super(context);
        this.mInsetsListener = new ViewTreeObserver.OnComputeInternalInsetsListener() { // from class: com.android.systemui.-$$Lambda$RegionInterceptingFrameLayout$JlFdsR9I_9ubvsna7k1PTnmr7OI
            public final void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
                RegionInterceptingFrameLayout.this.lambda$new$0$RegionInterceptingFrameLayout(internalInsetsInfo);
            }
        };
    }

    public RegionInterceptingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInsetsListener = new ViewTreeObserver.OnComputeInternalInsetsListener() { // from class: com.android.systemui.-$$Lambda$RegionInterceptingFrameLayout$JlFdsR9I_9ubvsna7k1PTnmr7OI
            public final void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
                RegionInterceptingFrameLayout.this.lambda$new$0$RegionInterceptingFrameLayout(internalInsetsInfo);
            }
        };
    }

    public RegionInterceptingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInsetsListener = new ViewTreeObserver.OnComputeInternalInsetsListener() { // from class: com.android.systemui.-$$Lambda$RegionInterceptingFrameLayout$JlFdsR9I_9ubvsna7k1PTnmr7OI
            public final void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
                RegionInterceptingFrameLayout.this.lambda$new$0$RegionInterceptingFrameLayout(internalInsetsInfo);
            }
        };
    }

    public RegionInterceptingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mInsetsListener = new ViewTreeObserver.OnComputeInternalInsetsListener() { // from class: com.android.systemui.-$$Lambda$RegionInterceptingFrameLayout$JlFdsR9I_9ubvsna7k1PTnmr7OI
            public final void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
                RegionInterceptingFrameLayout.this.lambda$new$0$RegionInterceptingFrameLayout(internalInsetsInfo);
            }
        };
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsListener);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mInsetsListener);
    }

    public /* synthetic */ void lambda$new$0$RegionInterceptingFrameLayout(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
        Region unionRegion;
        internalInsetsInfo.setTouchableInsets(3);
        internalInsetsInfo.touchableRegion.setEmpty();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof RegionInterceptableView) {
                RegionInterceptableView riv = (RegionInterceptableView) child;
                if (riv.shouldInterceptTouch() && (unionRegion = riv.getInterceptRegion()) != null) {
                    internalInsetsInfo.touchableRegion.op(unionRegion, Region.Op.UNION);
                }
            }
        }
    }

    /* loaded from: classes21.dex */
    public interface RegionInterceptableView {
        Region getInterceptRegion();

        default boolean shouldInterceptTouch() {
            return false;
        }
    }
}
