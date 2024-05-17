package com.android.systemui.statusbar.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.notification.stack.AnimationFilter;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.ViewState;
/* loaded from: classes21.dex */
public class PropertyAnimator {
    public static <T extends View> void setProperty(T view, AnimatableProperty animatableProperty, float newEndValue, AnimationProperties properties, boolean animated) {
        int animatorTag = animatableProperty.getAnimatorTag();
        ValueAnimator previousAnimator = (ValueAnimator) ViewState.getChildTag(view, animatorTag);
        if (previousAnimator != null || animated) {
            startAnimation(view, animatableProperty, newEndValue, properties);
        } else {
            animatableProperty.getProperty().set(view, Float.valueOf(newEndValue));
        }
    }

    public static <T extends View> void startAnimation(final T view, AnimatableProperty animatableProperty, float newEndValue, AnimationProperties properties) {
        final Property<T, Float> property = animatableProperty.getProperty();
        final int animationStartTag = animatableProperty.getAnimationStartTag();
        final int animationEndTag = animatableProperty.getAnimationEndTag();
        Float previousStartValue = (Float) ViewState.getChildTag(view, animationStartTag);
        Float previousEndValue = (Float) ViewState.getChildTag(view, animationEndTag);
        if (previousEndValue != null && previousEndValue.floatValue() == newEndValue) {
            return;
        }
        final int animatorTag = animatableProperty.getAnimatorTag();
        ValueAnimator previousAnimator = (ValueAnimator) ViewState.getChildTag(view, animatorTag);
        AnimationFilter filter = properties.getAnimationFilter();
        if (!filter.shouldAnimateProperty(property)) {
            if (previousAnimator == null) {
                property.set(view, Float.valueOf(newEndValue));
                return;
            }
            PropertyValuesHolder[] values = previousAnimator.getValues();
            float relativeDiff = newEndValue - previousEndValue.floatValue();
            float newStartValue = previousStartValue.floatValue() + relativeDiff;
            values[0].setFloatValues(newStartValue, newEndValue);
            view.setTag(animationStartTag, Float.valueOf(newStartValue));
            view.setTag(animationEndTag, Float.valueOf(newEndValue));
            previousAnimator.setCurrentPlayTime(previousAnimator.getCurrentPlayTime());
            return;
        }
        Float currentValue = property.get(view);
        ValueAnimator animator = ValueAnimator.ofFloat(currentValue.floatValue(), newEndValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$PropertyAnimator$VEXcQp-kY9kIrKbFhOrW7gy9zN4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                property.set(view, (Float) valueAnimator.getAnimatedValue());
            }
        });
        Interpolator customInterpolator = properties.getCustomInterpolator(view, property);
        Interpolator interpolator = customInterpolator != null ? customInterpolator : Interpolators.FAST_OUT_SLOW_IN;
        animator.setInterpolator(interpolator);
        long newDuration = ViewState.cancelAnimatorAndGetNewDuration(properties.duration, previousAnimator);
        animator.setDuration(newDuration);
        if (properties.delay > 0 && (previousAnimator == null || previousAnimator.getAnimatedFraction() == 0.0f)) {
            animator.setStartDelay(properties.delay);
        }
        AnimatorListenerAdapter listener = properties.getAnimationFinishListener();
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.PropertyAnimator.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                view.setTag(animatorTag, null);
                view.setTag(animationStartTag, null);
                view.setTag(animationEndTag, null);
            }
        });
        ViewState.startAnimator(animator, listener);
        view.setTag(animatorTag, animator);
        view.setTag(animationStartTag, currentValue);
        view.setTag(animationEndTag, Float.valueOf(newEndValue));
    }

    public static <T extends View> void applyImmediately(T view, AnimatableProperty property, float newValue) {
        cancelAnimation(view, property);
        property.getProperty().set(view, Float.valueOf(newValue));
    }

    public static void cancelAnimation(View view, AnimatableProperty property) {
        ValueAnimator animator = (ValueAnimator) view.getTag(property.getAnimatorTag());
        if (animator != null) {
            animator.cancel();
        }
    }

    public static boolean isAnimating(View view, AnimatableProperty property) {
        return view.getTag(property.getAnimatorTag()) != null;
    }
}
