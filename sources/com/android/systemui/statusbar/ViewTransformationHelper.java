package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.TransformState;
import java.util.Stack;
/* loaded from: classes21.dex */
public class ViewTransformationHelper implements TransformableView, TransformState.TransformInfo {
    private static final int TAG_CONTAINS_TRANSFORMED_VIEW = R.id.contains_transformed_view;
    private ValueAnimator mViewTransformationAnimation;
    private ArrayMap<Integer, View> mTransformedViews = new ArrayMap<>();
    private ArrayMap<Integer, CustomTransformation> mCustomTransformations = new ArrayMap<>();

    public void addTransformedView(int key, View transformedView) {
        this.mTransformedViews.put(Integer.valueOf(key), transformedView);
    }

    public void reset() {
        this.mTransformedViews.clear();
    }

    public void setCustomTransformation(CustomTransformation transformation, int viewType) {
        this.mCustomTransformations.put(Integer.valueOf(viewType), transformation);
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public TransformState getCurrentState(int fadingView) {
        View view = this.mTransformedViews.get(Integer.valueOf(fadingView));
        if (view != null && view.getVisibility() != 8) {
            return TransformState.createFrom(view, this);
        }
        return null;
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformTo(final TransformableView notification, final Runnable endRunnable) {
        ValueAnimator valueAnimator = this.mViewTransformationAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mViewTransformationAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mViewTransformationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.ViewTransformationHelper.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewTransformationHelper.this.transformTo(notification, animation.getAnimatedFraction());
            }
        });
        this.mViewTransformationAnimation.setInterpolator(Interpolators.LINEAR);
        this.mViewTransformationAnimation.setDuration(360L);
        this.mViewTransformationAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.ViewTransformationHelper.2
            public boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (this.mCancelled) {
                    ViewTransformationHelper.this.abortTransformations();
                    return;
                }
                Runnable runnable = endRunnable;
                if (runnable != null) {
                    runnable.run();
                }
                ViewTransformationHelper.this.setVisible(false);
                ViewTransformationHelper.this.mViewTransformationAnimation = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mCancelled = true;
            }
        });
        this.mViewTransformationAnimation.start();
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformTo(TransformableView notification, float transformationAmount) {
        for (Integer viewType : this.mTransformedViews.keySet()) {
            TransformState ownState = getCurrentState(viewType.intValue());
            if (ownState != null) {
                CustomTransformation customTransformation = this.mCustomTransformations.get(viewType);
                if (customTransformation != null && customTransformation.transformTo(ownState, notification, transformationAmount)) {
                    ownState.recycle();
                } else {
                    TransformState otherState = notification.getCurrentState(viewType.intValue());
                    if (otherState != null) {
                        ownState.transformViewTo(otherState, transformationAmount);
                        otherState.recycle();
                    } else {
                        ownState.disappear(transformationAmount, notification);
                    }
                    ownState.recycle();
                }
            }
        }
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformFrom(final TransformableView notification) {
        ValueAnimator valueAnimator = this.mViewTransformationAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mViewTransformationAnimation = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.mViewTransformationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.ViewTransformationHelper.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewTransformationHelper.this.transformFrom(notification, animation.getAnimatedFraction());
            }
        });
        this.mViewTransformationAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.ViewTransformationHelper.4
            public boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (this.mCancelled) {
                    ViewTransformationHelper.this.abortTransformations();
                } else {
                    ViewTransformationHelper.this.setVisible(true);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mCancelled = true;
            }
        });
        this.mViewTransformationAnimation.setInterpolator(Interpolators.LINEAR);
        this.mViewTransformationAnimation.setDuration(360L);
        this.mViewTransformationAnimation.start();
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void transformFrom(TransformableView notification, float transformationAmount) {
        for (Integer viewType : this.mTransformedViews.keySet()) {
            TransformState ownState = getCurrentState(viewType.intValue());
            if (ownState != null) {
                CustomTransformation customTransformation = this.mCustomTransformations.get(viewType);
                if (customTransformation != null && customTransformation.transformFrom(ownState, notification, transformationAmount)) {
                    ownState.recycle();
                } else {
                    TransformState otherState = notification.getCurrentState(viewType.intValue());
                    if (otherState != null) {
                        ownState.transformViewFrom(otherState, transformationAmount);
                        otherState.recycle();
                    } else {
                        ownState.appear(transformationAmount, notification);
                    }
                    ownState.recycle();
                }
            }
        }
    }

    @Override // com.android.systemui.statusbar.TransformableView
    public void setVisible(boolean visible) {
        ValueAnimator valueAnimator = this.mViewTransformationAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        for (Integer viewType : this.mTransformedViews.keySet()) {
            TransformState ownState = getCurrentState(viewType.intValue());
            if (ownState != null) {
                ownState.setVisible(visible, false);
                ownState.recycle();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void abortTransformations() {
        for (Integer viewType : this.mTransformedViews.keySet()) {
            TransformState ownState = getCurrentState(viewType.intValue());
            if (ownState != null) {
                ownState.abortTransformation();
                ownState.recycle();
            }
        }
    }

    public void addRemainingTransformTypes(View viewRoot) {
        int id;
        int numValues = this.mTransformedViews.size();
        for (int i = 0; i < numValues; i++) {
            View view = this.mTransformedViews.valueAt(i);
            while (true) {
                View view2 = view;
                if (view2 != viewRoot.getParent()) {
                    view2.setTag(TAG_CONTAINS_TRANSFORMED_VIEW, true);
                    view = view2.getParent();
                }
            }
        }
        Stack<View> stack = new Stack<>();
        stack.push(viewRoot);
        while (!stack.isEmpty()) {
            View child = stack.pop();
            Boolean containsView = (Boolean) child.getTag(TAG_CONTAINS_TRANSFORMED_VIEW);
            if (containsView == null && (id = child.getId()) != -1) {
                addTransformedView(id, child);
            } else {
                child.setTag(TAG_CONTAINS_TRANSFORMED_VIEW, null);
                if ((child instanceof ViewGroup) && !this.mTransformedViews.containsValue(child)) {
                    ViewGroup group = (ViewGroup) child;
                    for (int i2 = 0; i2 < group.getChildCount(); i2++) {
                        stack.push(group.getChildAt(i2));
                    }
                }
            }
        }
    }

    public void resetTransformedView(View view) {
        TransformState state = TransformState.createFrom(view, this);
        state.setVisible(true, true);
        state.recycle();
    }

    public ArraySet<View> getAllTransformingViews() {
        return new ArraySet<>(this.mTransformedViews.values());
    }

    @Override // com.android.systemui.statusbar.notification.TransformState.TransformInfo
    public boolean isAnimating() {
        ValueAnimator valueAnimator = this.mViewTransformationAnimation;
        return valueAnimator != null && valueAnimator.isRunning();
    }

    /* loaded from: classes21.dex */
    public static abstract class CustomTransformation {
        public abstract boolean transformFrom(TransformState transformState, TransformableView transformableView, float f);

        public abstract boolean transformTo(TransformState transformState, TransformableView transformableView, float f);

        public boolean initTransformation(TransformState ownState, TransformState otherState) {
            return false;
        }

        public boolean customTransformTarget(TransformState ownState, TransformState otherState) {
            return false;
        }

        public Interpolator getCustomInterpolator(int interpolationType, boolean isFrom) {
            return null;
        }
    }
}
