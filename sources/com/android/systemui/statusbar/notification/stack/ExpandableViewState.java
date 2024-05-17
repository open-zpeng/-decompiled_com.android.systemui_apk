package com.android.systemui.statusbar.notification.stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
/* loaded from: classes21.dex */
public class ExpandableViewState extends ViewState {
    public static final int LOCATION_BOTTOM_STACK_HIDDEN = 16;
    public static final int LOCATION_BOTTOM_STACK_PEEKING = 8;
    public static final int LOCATION_FIRST_HUN = 1;
    public static final int LOCATION_GONE = 64;
    public static final int LOCATION_HIDDEN_TOP = 2;
    public static final int LOCATION_MAIN_AREA = 4;
    public static final int LOCATION_UNKNOWN = 0;
    private static final int TAG_ANIMATOR_HEIGHT = R.id.height_animator_tag;
    private static final int TAG_ANIMATOR_TOP_INSET = R.id.top_inset_animator_tag;
    private static final int TAG_END_HEIGHT = R.id.height_animator_end_value_tag;
    private static final int TAG_END_TOP_INSET = R.id.top_inset_animator_end_value_tag;
    private static final int TAG_START_HEIGHT = R.id.height_animator_start_value_tag;
    private static final int TAG_START_TOP_INSET = R.id.top_inset_animator_start_value_tag;
    public static final int VISIBLE_LOCATIONS = 5;
    public boolean belowSpeedBump;
    public int clipTopAmount;
    public boolean dimmed;
    public boolean headsUpIsVisible;
    public int height;
    public boolean hideSensitive;
    public boolean inShelf;
    public int location;
    public int notGoneIndex;

    @Override // com.android.systemui.statusbar.notification.stack.ViewState
    public void copyFrom(ViewState viewState) {
        super.copyFrom(viewState);
        if (viewState instanceof ExpandableViewState) {
            ExpandableViewState svs = (ExpandableViewState) viewState;
            this.height = svs.height;
            this.dimmed = svs.dimmed;
            this.hideSensitive = svs.hideSensitive;
            this.belowSpeedBump = svs.belowSpeedBump;
            this.clipTopAmount = svs.clipTopAmount;
            this.notGoneIndex = svs.notGoneIndex;
            this.location = svs.location;
            this.headsUpIsVisible = svs.headsUpIsVisible;
        }
    }

    @Override // com.android.systemui.statusbar.notification.stack.ViewState
    public void applyToView(View view) {
        super.applyToView(view);
        if (view instanceof ExpandableView) {
            ExpandableView expandableView = (ExpandableView) view;
            int height = expandableView.getActualHeight();
            int newHeight = this.height;
            if (height != newHeight) {
                expandableView.setActualHeight(newHeight, false);
            }
            expandableView.setDimmed(this.dimmed, false);
            expandableView.setHideSensitive(this.hideSensitive, false, 0L, 0L);
            expandableView.setBelowSpeedBump(this.belowSpeedBump);
            float oldClipTopAmount = expandableView.getClipTopAmount();
            int i = this.clipTopAmount;
            if (oldClipTopAmount != i) {
                expandableView.setClipTopAmount(i);
            }
            expandableView.setTransformingInShelf(false);
            expandableView.setInShelf(this.inShelf);
            if (this.headsUpIsVisible) {
                expandableView.setHeadsUpIsVisible();
            }
        }
    }

    @Override // com.android.systemui.statusbar.notification.stack.ViewState
    public void animateTo(View child, AnimationProperties properties) {
        super.animateTo(child, properties);
        if (!(child instanceof ExpandableView)) {
            return;
        }
        ExpandableView expandableView = (ExpandableView) child;
        AnimationFilter animationFilter = properties.getAnimationFilter();
        if (this.height != expandableView.getActualHeight()) {
            startHeightAnimation(expandableView, properties);
        } else {
            abortAnimation(child, TAG_ANIMATOR_HEIGHT);
        }
        if (this.clipTopAmount != expandableView.getClipTopAmount()) {
            startInsetAnimation(expandableView, properties);
        } else {
            abortAnimation(child, TAG_ANIMATOR_TOP_INSET);
        }
        expandableView.setDimmed(this.dimmed, animationFilter.animateDimmed);
        expandableView.setBelowSpeedBump(this.belowSpeedBump);
        expandableView.setHideSensitive(this.hideSensitive, animationFilter.animateHideSensitive, properties.delay, properties.duration);
        if (properties.wasAdded(child) && !this.hidden) {
            expandableView.performAddAnimation(properties.delay, properties.duration, false);
        }
        if (!expandableView.isInShelf() && this.inShelf) {
            expandableView.setTransformingInShelf(true);
        }
        expandableView.setInShelf(this.inShelf);
        if (this.headsUpIsVisible) {
            expandableView.setHeadsUpIsVisible();
        }
    }

    private void startHeightAnimation(final ExpandableView child, AnimationProperties properties) {
        Integer previousStartValue = (Integer) getChildTag(child, TAG_START_HEIGHT);
        Integer previousEndValue = (Integer) getChildTag(child, TAG_END_HEIGHT);
        int newEndValue = this.height;
        if (previousEndValue != null && previousEndValue.intValue() == newEndValue) {
            return;
        }
        ValueAnimator previousAnimator = (ValueAnimator) getChildTag(child, TAG_ANIMATOR_HEIGHT);
        AnimationFilter filter = properties.getAnimationFilter();
        if (filter.animateHeight) {
            ValueAnimator animator = ValueAnimator.ofInt(child.getActualHeight(), newEndValue);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.stack.ExpandableViewState.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    child.setActualHeight(((Integer) animation.getAnimatedValue()).intValue(), false);
                }
            });
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
            animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.ExpandableViewState.2
                boolean mWasCancelled;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    child.setTag(ExpandableViewState.TAG_ANIMATOR_HEIGHT, null);
                    child.setTag(ExpandableViewState.TAG_START_HEIGHT, null);
                    child.setTag(ExpandableViewState.TAG_END_HEIGHT, null);
                    child.setActualHeightAnimating(false);
                    if (this.mWasCancelled) {
                        return;
                    }
                    ExpandableView expandableView = child;
                    if (expandableView instanceof ExpandableNotificationRow) {
                        ((ExpandableNotificationRow) expandableView).setGroupExpansionChanging(false);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    this.mWasCancelled = false;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    this.mWasCancelled = true;
                }
            });
            startAnimator(animator, listener);
            child.setTag(TAG_ANIMATOR_HEIGHT, animator);
            child.setTag(TAG_START_HEIGHT, Integer.valueOf(child.getActualHeight()));
            child.setTag(TAG_END_HEIGHT, Integer.valueOf(newEndValue));
            child.setActualHeightAnimating(true);
        } else if (previousAnimator != null) {
            PropertyValuesHolder[] values = previousAnimator.getValues();
            int relativeDiff = newEndValue - previousEndValue.intValue();
            int newStartValue = previousStartValue.intValue() + relativeDiff;
            values[0].setIntValues(newStartValue, newEndValue);
            child.setTag(TAG_START_HEIGHT, Integer.valueOf(newStartValue));
            child.setTag(TAG_END_HEIGHT, Integer.valueOf(newEndValue));
            previousAnimator.setCurrentPlayTime(previousAnimator.getCurrentPlayTime());
        } else {
            child.setActualHeight(newEndValue, false);
        }
    }

    private void startInsetAnimation(final ExpandableView child, AnimationProperties properties) {
        Integer previousStartValue = (Integer) getChildTag(child, TAG_START_TOP_INSET);
        Integer previousEndValue = (Integer) getChildTag(child, TAG_END_TOP_INSET);
        int newEndValue = this.clipTopAmount;
        if (previousEndValue != null && previousEndValue.intValue() == newEndValue) {
            return;
        }
        ValueAnimator previousAnimator = (ValueAnimator) getChildTag(child, TAG_ANIMATOR_TOP_INSET);
        AnimationFilter filter = properties.getAnimationFilter();
        if (!filter.animateTopInset) {
            if (previousAnimator != null) {
                PropertyValuesHolder[] values = previousAnimator.getValues();
                int relativeDiff = newEndValue - previousEndValue.intValue();
                int newStartValue = previousStartValue.intValue() + relativeDiff;
                values[0].setIntValues(newStartValue, newEndValue);
                child.setTag(TAG_START_TOP_INSET, Integer.valueOf(newStartValue));
                child.setTag(TAG_END_TOP_INSET, Integer.valueOf(newEndValue));
                previousAnimator.setCurrentPlayTime(previousAnimator.getCurrentPlayTime());
                return;
            }
            child.setClipTopAmount(newEndValue);
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(child.getClipTopAmount(), newEndValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.stack.ExpandableViewState.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                child.setClipTopAmount(((Integer) animation.getAnimatedValue()).intValue());
            }
        });
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
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.ExpandableViewState.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                child.setTag(ExpandableViewState.TAG_ANIMATOR_TOP_INSET, null);
                child.setTag(ExpandableViewState.TAG_START_TOP_INSET, null);
                child.setTag(ExpandableViewState.TAG_END_TOP_INSET, null);
            }
        });
        startAnimator(animator, listener);
        child.setTag(TAG_ANIMATOR_TOP_INSET, animator);
        child.setTag(TAG_START_TOP_INSET, Integer.valueOf(child.getClipTopAmount()));
        child.setTag(TAG_END_TOP_INSET, Integer.valueOf(newEndValue));
    }

    public static int getFinalActualHeight(ExpandableView view) {
        if (view == null) {
            return 0;
        }
        ValueAnimator heightAnimator = (ValueAnimator) getChildTag(view, TAG_ANIMATOR_HEIGHT);
        if (heightAnimator == null) {
            return view.getActualHeight();
        }
        return ((Integer) getChildTag(view, TAG_END_HEIGHT)).intValue();
    }

    @Override // com.android.systemui.statusbar.notification.stack.ViewState
    public void cancelAnimations(View view) {
        super.cancelAnimations(view);
        Animator animator = (Animator) getChildTag(view, TAG_ANIMATOR_HEIGHT);
        if (animator != null) {
            animator.cancel();
        }
        Animator animator2 = (Animator) getChildTag(view, TAG_ANIMATOR_TOP_INSET);
        if (animator2 != null) {
            animator2.cancel();
        }
    }
}
