package com.android.systemui.statusbar.notification.stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.systemui.Dumpable;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.policy.HeadsUpUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
/* loaded from: classes21.dex */
public class ViewState implements Dumpable {
    public float alpha;
    public boolean gone;
    public boolean hidden;
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float xTranslation;
    public float yTranslation;
    public float zTranslation;
    protected static final AnimationProperties NO_NEW_ANIMATIONS = new AnimationProperties() { // from class: com.android.systemui.statusbar.notification.stack.ViewState.1
        AnimationFilter mAnimationFilter = new AnimationFilter();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    };
    private static final int TAG_ANIMATOR_TRANSLATION_X = R.id.translation_x_animator_tag;
    private static final int TAG_ANIMATOR_TRANSLATION_Y = R.id.translation_y_animator_tag;
    private static final int TAG_ANIMATOR_TRANSLATION_Z = R.id.translation_z_animator_tag;
    private static final int TAG_ANIMATOR_ALPHA = R.id.alpha_animator_tag;
    private static final int TAG_END_TRANSLATION_X = R.id.translation_x_animator_end_value_tag;
    private static final int TAG_END_TRANSLATION_Y = R.id.translation_y_animator_end_value_tag;
    private static final int TAG_END_TRANSLATION_Z = R.id.translation_z_animator_end_value_tag;
    private static final int TAG_END_ALPHA = R.id.alpha_animator_end_value_tag;
    private static final int TAG_START_TRANSLATION_X = R.id.translation_x_animator_start_value_tag;
    private static final int TAG_START_TRANSLATION_Y = R.id.translation_y_animator_start_value_tag;
    private static final int TAG_START_TRANSLATION_Z = R.id.translation_z_animator_start_value_tag;
    private static final int TAG_START_ALPHA = R.id.alpha_animator_start_value_tag;
    private static final AnimatableProperty SCALE_X_PROPERTY = new AnimatableProperty() { // from class: com.android.systemui.statusbar.notification.stack.ViewState.2
        @Override // com.android.systemui.statusbar.notification.AnimatableProperty
        public int getAnimationStartTag() {
            return R.id.scale_x_animator_start_value_tag;
        }

        @Override // com.android.systemui.statusbar.notification.AnimatableProperty
        public int getAnimationEndTag() {
            return R.id.scale_x_animator_end_value_tag;
        }

        @Override // com.android.systemui.statusbar.notification.AnimatableProperty
        public int getAnimatorTag() {
            return R.id.scale_x_animator_tag;
        }

        @Override // com.android.systemui.statusbar.notification.AnimatableProperty
        public Property getProperty() {
            return View.SCALE_X;
        }
    };
    private static final AnimatableProperty SCALE_Y_PROPERTY = new AnimatableProperty() { // from class: com.android.systemui.statusbar.notification.stack.ViewState.3
        @Override // com.android.systemui.statusbar.notification.AnimatableProperty
        public int getAnimationStartTag() {
            return R.id.scale_y_animator_start_value_tag;
        }

        @Override // com.android.systemui.statusbar.notification.AnimatableProperty
        public int getAnimationEndTag() {
            return R.id.scale_y_animator_end_value_tag;
        }

        @Override // com.android.systemui.statusbar.notification.AnimatableProperty
        public int getAnimatorTag() {
            return R.id.scale_y_animator_tag;
        }

        @Override // com.android.systemui.statusbar.notification.AnimatableProperty
        public Property getProperty() {
            return View.SCALE_Y;
        }
    };

    public void copyFrom(ViewState viewState) {
        this.alpha = viewState.alpha;
        this.xTranslation = viewState.xTranslation;
        this.yTranslation = viewState.yTranslation;
        this.zTranslation = viewState.zTranslation;
        this.gone = viewState.gone;
        this.hidden = viewState.hidden;
        this.scaleX = viewState.scaleX;
        this.scaleY = viewState.scaleY;
    }

    public void initFrom(View view) {
        this.alpha = view.getAlpha();
        this.xTranslation = view.getTranslationX();
        this.yTranslation = view.getTranslationY();
        this.zTranslation = view.getTranslationZ();
        this.gone = view.getVisibility() == 8;
        this.hidden = view.getVisibility() == 4;
        this.scaleX = view.getScaleX();
        this.scaleY = view.getScaleY();
    }

    public void applyToView(View view) {
        int newLayerType;
        if (this.gone) {
            return;
        }
        boolean animatingX = isAnimating(view, TAG_ANIMATOR_TRANSLATION_X);
        if (animatingX) {
            updateAnimationX(view);
        } else {
            float translationX = view.getTranslationX();
            float f = this.xTranslation;
            if (translationX != f) {
                view.setTranslationX(f);
            }
        }
        boolean animatingY = isAnimating(view, TAG_ANIMATOR_TRANSLATION_Y);
        if (animatingY) {
            updateAnimationY(view);
        } else {
            float translationY = view.getTranslationY();
            float f2 = this.yTranslation;
            if (translationY != f2) {
                view.setTranslationY(f2);
            }
        }
        boolean animatingZ = isAnimating(view, TAG_ANIMATOR_TRANSLATION_Z);
        if (animatingZ) {
            updateAnimationZ(view);
        } else {
            float translationZ = view.getTranslationZ();
            float f3 = this.zTranslation;
            if (translationZ != f3) {
                view.setTranslationZ(f3);
            }
        }
        boolean animatingScaleX = isAnimating(view, SCALE_X_PROPERTY);
        if (animatingScaleX) {
            updateAnimation(view, SCALE_X_PROPERTY, this.scaleX);
        } else {
            float scaleX = view.getScaleX();
            float f4 = this.scaleX;
            if (scaleX != f4) {
                view.setScaleX(f4);
            }
        }
        boolean animatingScaleY = isAnimating(view, SCALE_Y_PROPERTY);
        if (animatingScaleY) {
            updateAnimation(view, SCALE_Y_PROPERTY, this.scaleY);
        } else {
            float scaleY = view.getScaleY();
            float f5 = this.scaleY;
            if (scaleY != f5) {
                view.setScaleY(f5);
            }
        }
        int oldVisibility = view.getVisibility();
        boolean newLayerTypeIsHardware = true;
        boolean becomesInvisible = this.alpha == 0.0f || (this.hidden && !(isAnimating(view) && oldVisibility == 0));
        boolean animatingAlpha = isAnimating(view, TAG_ANIMATOR_ALPHA);
        if (animatingAlpha) {
            updateAlphaAnimation(view);
        } else {
            float alpha = view.getAlpha();
            float f6 = this.alpha;
            if (alpha != f6) {
                boolean becomesFullyVisible = f6 == 1.0f;
                if (becomesInvisible || becomesFullyVisible || !view.hasOverlappingRendering()) {
                    newLayerTypeIsHardware = false;
                }
                int layerType = view.getLayerType();
                if (newLayerTypeIsHardware) {
                    newLayerType = 2;
                } else {
                    newLayerType = 0;
                }
                if (layerType != newLayerType) {
                    view.setLayerType(newLayerType, null);
                }
                view.setAlpha(this.alpha);
            }
        }
        int newVisibility = becomesInvisible ? 4 : 0;
        if (newVisibility != oldVisibility) {
            if (!(view instanceof ExpandableView) || !((ExpandableView) view).willBeGone()) {
                view.setVisibility(newVisibility);
            }
        }
    }

    public boolean isAnimating(View view) {
        return isAnimating(view, TAG_ANIMATOR_TRANSLATION_X) || isAnimating(view, TAG_ANIMATOR_TRANSLATION_Y) || isAnimating(view, TAG_ANIMATOR_TRANSLATION_Z) || isAnimating(view, TAG_ANIMATOR_ALPHA) || isAnimating(view, SCALE_X_PROPERTY) || isAnimating(view, SCALE_Y_PROPERTY);
    }

    private static boolean isAnimating(View view, int tag) {
        return getChildTag(view, tag) != null;
    }

    public static boolean isAnimating(View view, AnimatableProperty property) {
        return getChildTag(view, property.getAnimatorTag()) != null;
    }

    public void animateTo(View child, AnimationProperties animationProperties) {
        boolean wasVisible = child.getVisibility() == 0;
        float alpha = this.alpha;
        if (!wasVisible && ((alpha != 0.0f || child.getAlpha() != 0.0f) && !this.gone && !this.hidden)) {
            child.setVisibility(0);
        }
        float childAlpha = child.getAlpha();
        boolean alphaChanging = this.alpha != childAlpha;
        if (child instanceof ExpandableView) {
            alphaChanging &= true ^ ((ExpandableView) child).willBeGone();
        }
        if (child.getTranslationX() != this.xTranslation) {
            startXTranslationAnimation(child, animationProperties);
        } else {
            abortAnimation(child, TAG_ANIMATOR_TRANSLATION_X);
        }
        if (child.getTranslationY() != this.yTranslation) {
            startYTranslationAnimation(child, animationProperties);
        } else {
            abortAnimation(child, TAG_ANIMATOR_TRANSLATION_Y);
        }
        if (child.getTranslationZ() != this.zTranslation) {
            startZTranslationAnimation(child, animationProperties);
        } else {
            abortAnimation(child, TAG_ANIMATOR_TRANSLATION_Z);
        }
        float scaleX = child.getScaleX();
        float f = this.scaleX;
        if (scaleX != f) {
            PropertyAnimator.startAnimation(child, SCALE_X_PROPERTY, f, animationProperties);
        } else {
            abortAnimation(child, SCALE_X_PROPERTY.getAnimatorTag());
        }
        float scaleY = child.getScaleY();
        float f2 = this.scaleY;
        if (scaleY != f2) {
            PropertyAnimator.startAnimation(child, SCALE_Y_PROPERTY, f2, animationProperties);
        } else {
            abortAnimation(child, SCALE_Y_PROPERTY.getAnimatorTag());
        }
        if (alphaChanging) {
            startAlphaAnimation(child, animationProperties);
        } else {
            abortAnimation(child, TAG_ANIMATOR_ALPHA);
        }
    }

    private void updateAlphaAnimation(View view) {
        startAlphaAnimation(view, NO_NEW_ANIMATIONS);
    }

    private void startAlphaAnimation(final View child, AnimationProperties properties) {
        Float previousStartValue = (Float) getChildTag(child, TAG_START_ALPHA);
        Float previousEndValue = (Float) getChildTag(child, TAG_END_ALPHA);
        final float newEndValue = this.alpha;
        if (previousEndValue != null && previousEndValue.floatValue() == newEndValue) {
            return;
        }
        ObjectAnimator previousAnimator = (ObjectAnimator) getChildTag(child, TAG_ANIMATOR_ALPHA);
        AnimationFilter filter = properties.getAnimationFilter();
        if (!filter.animateAlpha) {
            if (previousAnimator != null) {
                PropertyValuesHolder[] values = previousAnimator.getValues();
                float relativeDiff = newEndValue - previousEndValue.floatValue();
                float newStartValue = previousStartValue.floatValue() + relativeDiff;
                values[0].setFloatValues(newStartValue, newEndValue);
                child.setTag(TAG_START_ALPHA, Float.valueOf(newStartValue));
                child.setTag(TAG_END_ALPHA, Float.valueOf(newEndValue));
                previousAnimator.setCurrentPlayTime(previousAnimator.getCurrentPlayTime());
                return;
            }
            child.setAlpha(newEndValue);
            if (newEndValue == 0.0f) {
                child.setVisibility(4);
            }
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(child, View.ALPHA, child.getAlpha(), newEndValue);
        animator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        child.setLayerType(2, null);
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.ViewState.4
            public boolean mWasCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                child.setLayerType(0, null);
                if (newEndValue == 0.0f && !this.mWasCancelled) {
                    child.setVisibility(4);
                }
                child.setTag(ViewState.TAG_ANIMATOR_ALPHA, null);
                child.setTag(ViewState.TAG_START_ALPHA, null);
                child.setTag(ViewState.TAG_END_ALPHA, null);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mWasCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                this.mWasCancelled = false;
            }
        });
        long newDuration = cancelAnimatorAndGetNewDuration(properties.duration, previousAnimator);
        animator.setDuration(newDuration);
        if (properties.delay > 0 && (previousAnimator == null || previousAnimator.getAnimatedFraction() == 0.0f)) {
            animator.setStartDelay(properties.delay);
        }
        AnimatorListenerAdapter listener = properties.getAnimationFinishListener();
        if (listener != null) {
            animator.addListener(listener);
        }
        startAnimator(animator, listener);
        child.setTag(TAG_ANIMATOR_ALPHA, animator);
        child.setTag(TAG_START_ALPHA, Float.valueOf(child.getAlpha()));
        child.setTag(TAG_END_ALPHA, Float.valueOf(newEndValue));
    }

    private void updateAnimationZ(View view) {
        startZTranslationAnimation(view, NO_NEW_ANIMATIONS);
    }

    private void updateAnimation(View view, AnimatableProperty property, float endValue) {
        PropertyAnimator.startAnimation(view, property, endValue, NO_NEW_ANIMATIONS);
    }

    private void startZTranslationAnimation(final View child, AnimationProperties properties) {
        Float previousStartValue = (Float) getChildTag(child, TAG_START_TRANSLATION_Z);
        Float previousEndValue = (Float) getChildTag(child, TAG_END_TRANSLATION_Z);
        float newEndValue = this.zTranslation;
        if (previousEndValue != null && previousEndValue.floatValue() == newEndValue) {
            return;
        }
        ObjectAnimator previousAnimator = (ObjectAnimator) getChildTag(child, TAG_ANIMATOR_TRANSLATION_Z);
        AnimationFilter filter = properties.getAnimationFilter();
        if (!filter.animateZ) {
            if (previousAnimator != null) {
                PropertyValuesHolder[] values = previousAnimator.getValues();
                float relativeDiff = newEndValue - previousEndValue.floatValue();
                float newStartValue = previousStartValue.floatValue() + relativeDiff;
                values[0].setFloatValues(newStartValue, newEndValue);
                child.setTag(TAG_START_TRANSLATION_Z, Float.valueOf(newStartValue));
                child.setTag(TAG_END_TRANSLATION_Z, Float.valueOf(newEndValue));
                previousAnimator.setCurrentPlayTime(previousAnimator.getCurrentPlayTime());
                return;
            }
            child.setTranslationZ(newEndValue);
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Z, child.getTranslationZ(), newEndValue);
        animator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        long newDuration = cancelAnimatorAndGetNewDuration(properties.duration, previousAnimator);
        animator.setDuration(newDuration);
        if (properties.delay > 0 && (previousAnimator == null || previousAnimator.getAnimatedFraction() == 0.0f)) {
            animator.setStartDelay(properties.delay);
        }
        AnimatorListenerAdapter listener = properties.getAnimationFinishListener();
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.ViewState.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                child.setTag(ViewState.TAG_ANIMATOR_TRANSLATION_Z, null);
                child.setTag(ViewState.TAG_START_TRANSLATION_Z, null);
                child.setTag(ViewState.TAG_END_TRANSLATION_Z, null);
            }
        });
        startAnimator(animator, listener);
        child.setTag(TAG_ANIMATOR_TRANSLATION_Z, animator);
        child.setTag(TAG_START_TRANSLATION_Z, Float.valueOf(child.getTranslationZ()));
        child.setTag(TAG_END_TRANSLATION_Z, Float.valueOf(newEndValue));
    }

    private void updateAnimationX(View view) {
        startXTranslationAnimation(view, NO_NEW_ANIMATIONS);
    }

    private void startXTranslationAnimation(final View child, AnimationProperties properties) {
        Float previousStartValue = (Float) getChildTag(child, TAG_START_TRANSLATION_X);
        Float previousEndValue = (Float) getChildTag(child, TAG_END_TRANSLATION_X);
        float newEndValue = this.xTranslation;
        if (previousEndValue != null && previousEndValue.floatValue() == newEndValue) {
            return;
        }
        ObjectAnimator previousAnimator = (ObjectAnimator) getChildTag(child, TAG_ANIMATOR_TRANSLATION_X);
        AnimationFilter filter = properties.getAnimationFilter();
        if (!filter.animateX) {
            if (previousAnimator != null) {
                PropertyValuesHolder[] values = previousAnimator.getValues();
                float relativeDiff = newEndValue - previousEndValue.floatValue();
                float newStartValue = previousStartValue.floatValue() + relativeDiff;
                values[0].setFloatValues(newStartValue, newEndValue);
                child.setTag(TAG_START_TRANSLATION_X, Float.valueOf(newStartValue));
                child.setTag(TAG_END_TRANSLATION_X, Float.valueOf(newEndValue));
                previousAnimator.setCurrentPlayTime(previousAnimator.getCurrentPlayTime());
                return;
            }
            child.setTranslationX(newEndValue);
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_X, child.getTranslationX(), newEndValue);
        Interpolator customInterpolator = properties.getCustomInterpolator(child, View.TRANSLATION_X);
        Interpolator interpolator = customInterpolator != null ? customInterpolator : Interpolators.FAST_OUT_SLOW_IN;
        animator.setInterpolator(interpolator);
        long newDuration = cancelAnimatorAndGetNewDuration(properties.duration, previousAnimator);
        animator.setDuration(newDuration);
        if (properties.delay > 0 && (previousAnimator == null || previousAnimator.getAnimatedFraction() == 0.0f)) {
            animator.setStartDelay(properties.delay);
        }
        AnimatorListenerAdapter listener = properties.getAnimationFinishListener();
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.ViewState.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                child.setTag(ViewState.TAG_ANIMATOR_TRANSLATION_X, null);
                child.setTag(ViewState.TAG_START_TRANSLATION_X, null);
                child.setTag(ViewState.TAG_END_TRANSLATION_X, null);
            }
        });
        startAnimator(animator, listener);
        child.setTag(TAG_ANIMATOR_TRANSLATION_X, animator);
        child.setTag(TAG_START_TRANSLATION_X, Float.valueOf(child.getTranslationX()));
        child.setTag(TAG_END_TRANSLATION_X, Float.valueOf(newEndValue));
    }

    private void updateAnimationY(View view) {
        startYTranslationAnimation(view, NO_NEW_ANIMATIONS);
    }

    private void startYTranslationAnimation(final View child, AnimationProperties properties) {
        Float previousStartValue = (Float) getChildTag(child, TAG_START_TRANSLATION_Y);
        Float previousEndValue = (Float) getChildTag(child, TAG_END_TRANSLATION_Y);
        float newEndValue = this.yTranslation;
        if (previousEndValue != null && previousEndValue.floatValue() == newEndValue) {
            return;
        }
        ObjectAnimator previousAnimator = (ObjectAnimator) getChildTag(child, TAG_ANIMATOR_TRANSLATION_Y);
        AnimationFilter filter = properties.getAnimationFilter();
        if (!filter.shouldAnimateY(child)) {
            if (previousAnimator != null) {
                PropertyValuesHolder[] values = previousAnimator.getValues();
                float relativeDiff = newEndValue - previousEndValue.floatValue();
                float newStartValue = previousStartValue.floatValue() + relativeDiff;
                values[0].setFloatValues(newStartValue, newEndValue);
                child.setTag(TAG_START_TRANSLATION_Y, Float.valueOf(newStartValue));
                child.setTag(TAG_END_TRANSLATION_Y, Float.valueOf(newEndValue));
                previousAnimator.setCurrentPlayTime(previousAnimator.getCurrentPlayTime());
                return;
            }
            child.setTranslationY(newEndValue);
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, child.getTranslationY(), newEndValue);
        Interpolator customInterpolator = properties.getCustomInterpolator(child, View.TRANSLATION_Y);
        Interpolator interpolator = customInterpolator != null ? customInterpolator : Interpolators.FAST_OUT_SLOW_IN;
        animator.setInterpolator(interpolator);
        long newDuration = cancelAnimatorAndGetNewDuration(properties.duration, previousAnimator);
        animator.setDuration(newDuration);
        if (properties.delay > 0 && (previousAnimator == null || previousAnimator.getAnimatedFraction() == 0.0f)) {
            animator.setStartDelay(properties.delay);
        }
        AnimatorListenerAdapter listener = properties.getAnimationFinishListener();
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.ViewState.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                HeadsUpUtil.setIsClickedHeadsUpNotification(child, false);
                child.setTag(ViewState.TAG_ANIMATOR_TRANSLATION_Y, null);
                child.setTag(ViewState.TAG_START_TRANSLATION_Y, null);
                child.setTag(ViewState.TAG_END_TRANSLATION_Y, null);
                ViewState.this.onYTranslationAnimationFinished(child);
            }
        });
        startAnimator(animator, listener);
        child.setTag(TAG_ANIMATOR_TRANSLATION_Y, animator);
        child.setTag(TAG_START_TRANSLATION_Y, Float.valueOf(child.getTranslationY()));
        child.setTag(TAG_END_TRANSLATION_Y, Float.valueOf(newEndValue));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onYTranslationAnimationFinished(View view) {
        if (this.hidden && !this.gone) {
            view.setVisibility(4);
        }
    }

    public static void startAnimator(Animator animator, AnimatorListenerAdapter listener) {
        if (listener != null) {
            listener.onAnimationStart(animator);
        }
        animator.start();
    }

    public static <T> T getChildTag(View child, int tag) {
        return (T) child.getTag(tag);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void abortAnimation(View child, int animatorTag) {
        Animator previousAnimator = (Animator) getChildTag(child, animatorTag);
        if (previousAnimator != null) {
            previousAnimator.cancel();
        }
    }

    public static long cancelAnimatorAndGetNewDuration(long duration, ValueAnimator previousAnimator) {
        if (previousAnimator == null) {
            return duration;
        }
        long newDuration = Math.max(previousAnimator.getDuration() - previousAnimator.getCurrentPlayTime(), duration);
        previousAnimator.cancel();
        return newDuration;
    }

    public static float getFinalTranslationX(View view) {
        if (view == null) {
            return 0.0f;
        }
        ValueAnimator xAnimator = (ValueAnimator) getChildTag(view, TAG_ANIMATOR_TRANSLATION_X);
        if (xAnimator == null) {
            return view.getTranslationX();
        }
        return ((Float) getChildTag(view, TAG_END_TRANSLATION_X)).floatValue();
    }

    public static float getFinalTranslationY(View view) {
        if (view == null) {
            return 0.0f;
        }
        ValueAnimator yAnimator = (ValueAnimator) getChildTag(view, TAG_ANIMATOR_TRANSLATION_Y);
        if (yAnimator == null) {
            return view.getTranslationY();
        }
        return ((Float) getChildTag(view, TAG_END_TRANSLATION_Y)).floatValue();
    }

    public static float getFinalTranslationZ(View view) {
        if (view == null) {
            return 0.0f;
        }
        ValueAnimator zAnimator = (ValueAnimator) getChildTag(view, TAG_ANIMATOR_TRANSLATION_Z);
        if (zAnimator == null) {
            return view.getTranslationZ();
        }
        return ((Float) getChildTag(view, TAG_END_TRANSLATION_Z)).floatValue();
    }

    public static boolean isAnimatingY(View child) {
        return getChildTag(child, TAG_ANIMATOR_TRANSLATION_Y) != null;
    }

    public void cancelAnimations(View view) {
        Animator animator = (Animator) getChildTag(view, TAG_ANIMATOR_TRANSLATION_X);
        if (animator != null) {
            animator.cancel();
        }
        Animator animator2 = (Animator) getChildTag(view, TAG_ANIMATOR_TRANSLATION_Y);
        if (animator2 != null) {
            animator2.cancel();
        }
        Animator animator3 = (Animator) getChildTag(view, TAG_ANIMATOR_TRANSLATION_Z);
        if (animator3 != null) {
            animator3.cancel();
        }
        Animator animator4 = (Animator) getChildTag(view, TAG_ANIMATOR_ALPHA);
        if (animator4 != null) {
            animator4.cancel();
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        StringBuilder result = new StringBuilder();
        result.append("ViewState { ");
        boolean first = true;
        for (Class currentClass = getClass(); currentClass != null; currentClass = currentClass.getSuperclass()) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (!Modifier.isStatic(modifiers) && !field.isSynthetic() && !Modifier.isTransient(modifiers)) {
                    if (!first) {
                        result.append(", ");
                    }
                    try {
                        result.append(field.getName());
                        result.append(": ");
                        field.setAccessible(true);
                        result.append(field.get(this));
                    } catch (IllegalAccessException e) {
                    }
                    first = false;
                }
            }
        }
        result.append(" }");
        pw.print(result);
    }
}
