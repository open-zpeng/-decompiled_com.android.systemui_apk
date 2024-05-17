package com.android.systemui.statusbar.notification.stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
/* loaded from: classes21.dex */
public class StackStateAnimator {
    public static final int ANIMATION_DELAY_HEADS_UP = 120;
    public static final int ANIMATION_DELAY_HEADS_UP_CLICKED = 120;
    public static final int ANIMATION_DELAY_PER_ELEMENT_GO_TO_FULL_SHADE = 48;
    public static final int ANIMATION_DELAY_PER_ELEMENT_INTERRUPTING = 80;
    public static final int ANIMATION_DELAY_PER_ELEMENT_MANUAL = 32;
    public static final int ANIMATION_DURATION_APPEAR_DISAPPEAR = 464;
    public static final int ANIMATION_DURATION_BLOCKING_HELPER_FADE = 240;
    public static final int ANIMATION_DURATION_CLOSE_REMOTE_INPUT = 150;
    public static final int ANIMATION_DURATION_DIMMED_ACTIVATED = 220;
    public static final int ANIMATION_DURATION_GO_TO_FULL_SHADE = 448;
    public static final int ANIMATION_DURATION_HEADS_UP_APPEAR = 550;
    public static final int ANIMATION_DURATION_HEADS_UP_APPEAR_CLOSED = (int) (HeadsUpAppearInterpolator.getFractionUntilOvershoot() * 550.0f);
    public static final int ANIMATION_DURATION_HEADS_UP_DISAPPEAR = 300;
    public static final int ANIMATION_DURATION_PULSE_APPEAR = 550;
    public static final int ANIMATION_DURATION_STANDARD = 360;
    public static final int ANIMATION_DURATION_SWIPE = 260;
    public static final int ANIMATION_DURATION_WAKEUP = 500;
    public static final int DELAY_EFFECT_MAX_INDEX_DIFFERENCE = 2;
    private static final int MAX_STAGGER_COUNT = 5;
    private ValueAnimator mBottomOverScrollAnimator;
    private long mCurrentAdditionalDelay;
    private long mCurrentLength;
    private final int mGoToFullShadeAppearingTranslation;
    private int mHeadsUpAppearHeightBottom;
    public NotificationStackScrollLayout mHostLayout;
    private final int mPulsingAppearingTranslation;
    private boolean mShadeExpanded;
    private NotificationShelf mShelf;
    private float mStatusBarIconLocation;
    private ValueAnimator mTopOverScrollAnimator;
    private final ExpandableViewState mTmpState = new ExpandableViewState();
    private ArrayList<NotificationStackScrollLayout.AnimationEvent> mNewEvents = new ArrayList<>();
    private ArrayList<View> mNewAddChildren = new ArrayList<>();
    private HashSet<View> mHeadsUpAppearChildren = new HashSet<>();
    private HashSet<View> mHeadsUpDisappearChildren = new HashSet<>();
    private HashSet<Animator> mAnimatorSet = new HashSet<>();
    private Stack<AnimatorListenerAdapter> mAnimationListenerPool = new Stack<>();
    private AnimationFilter mAnimationFilter = new AnimationFilter();
    private ArrayList<ExpandableView> mTransientViewsToRemove = new ArrayList<>();
    private int[] mTmpLocation = new int[2];
    private final AnimationProperties mAnimationProperties = new AnimationProperties() { // from class: com.android.systemui.statusbar.notification.stack.StackStateAnimator.1
        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return StackStateAnimator.this.mAnimationFilter;
        }

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimatorListenerAdapter getAnimationFinishListener() {
            return StackStateAnimator.this.getGlobalAnimationFinishedListener();
        }

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public boolean wasAdded(View view) {
            return StackStateAnimator.this.mNewAddChildren.contains(view);
        }

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public Interpolator getCustomInterpolator(View child, Property property) {
            if (StackStateAnimator.this.mHeadsUpAppearChildren.contains(child) && View.TRANSLATION_Y.equals(property)) {
                return Interpolators.HEADS_UP_APPEAR;
            }
            return null;
        }
    };

    public StackStateAnimator(NotificationStackScrollLayout hostLayout) {
        this.mHostLayout = hostLayout;
        this.mGoToFullShadeAppearingTranslation = hostLayout.getContext().getResources().getDimensionPixelSize(R.dimen.go_to_full_shade_appearing_translation);
        this.mPulsingAppearingTranslation = hostLayout.getContext().getResources().getDimensionPixelSize(R.dimen.pulsing_notification_appear_translation);
    }

    public boolean isRunning() {
        return !this.mAnimatorSet.isEmpty();
    }

    public void startAnimationForEvents(ArrayList<NotificationStackScrollLayout.AnimationEvent> mAnimationEvents, long additionalDelay) {
        processAnimationEvents(mAnimationEvents);
        int childCount = this.mHostLayout.getChildCount();
        this.mAnimationFilter.applyCombination(this.mNewEvents);
        this.mCurrentAdditionalDelay = additionalDelay;
        this.mCurrentLength = NotificationStackScrollLayout.AnimationEvent.combineLength(this.mNewEvents);
        int animationStaggerCount = 0;
        for (int i = 0; i < childCount; i++) {
            ExpandableView child = (ExpandableView) this.mHostLayout.getChildAt(i);
            ExpandableViewState viewState = child.getViewState();
            if (viewState != null && child.getVisibility() != 8 && !applyWithoutAnimation(child, viewState)) {
                if (this.mAnimationProperties.wasAdded(child) && animationStaggerCount < 5) {
                    animationStaggerCount++;
                }
                initAnimationProperties(child, viewState, animationStaggerCount);
                viewState.animateTo(child, this.mAnimationProperties);
            }
        }
        if (!isRunning()) {
            onAnimationFinished();
        }
        this.mHeadsUpAppearChildren.clear();
        this.mHeadsUpDisappearChildren.clear();
        this.mNewEvents.clear();
        this.mNewAddChildren.clear();
    }

    private void initAnimationProperties(ExpandableView child, ExpandableViewState viewState, int animationStaggerCount) {
        boolean wasAdded = this.mAnimationProperties.wasAdded(child);
        this.mAnimationProperties.duration = this.mCurrentLength;
        adaptDurationWhenGoingToFullShade(child, viewState, wasAdded, animationStaggerCount);
        this.mAnimationProperties.delay = 0L;
        if (!wasAdded) {
            if (this.mAnimationFilter.hasDelays) {
                if (viewState.yTranslation == child.getTranslationY() && viewState.zTranslation == child.getTranslationZ() && viewState.alpha == child.getAlpha() && viewState.height == child.getActualHeight() && viewState.clipTopAmount == child.getClipTopAmount()) {
                    return;
                }
            } else {
                return;
            }
        }
        this.mAnimationProperties.delay = this.mCurrentAdditionalDelay + calculateChildAnimationDelay(viewState, animationStaggerCount);
    }

    private void adaptDurationWhenGoingToFullShade(ExpandableView child, ExpandableViewState viewState, boolean wasAdded, int animationStaggerCount) {
        if (wasAdded && this.mAnimationFilter.hasGoToFullShadeEvent) {
            child.setTranslationY(child.getTranslationY() + this.mGoToFullShadeAppearingTranslation);
            float longerDurationFactor = (float) Math.pow(animationStaggerCount, 0.699999988079071d);
            this.mAnimationProperties.duration = (100.0f * longerDurationFactor) + 514;
        }
    }

    private boolean applyWithoutAnimation(ExpandableView child, ExpandableViewState viewState) {
        if (this.mShadeExpanded || ViewState.isAnimatingY(child) || this.mHeadsUpDisappearChildren.contains(child) || this.mHeadsUpAppearChildren.contains(child) || NotificationStackScrollLayout.isPinnedHeadsUp(child)) {
            return false;
        }
        viewState.applyToView(child);
        return true;
    }

    private long calculateChildAnimationDelay(ExpandableViewState viewState, int animationStaggerCount) {
        ExpandableView viewAfterChangingView;
        if (this.mAnimationFilter.hasGoToFullShadeEvent) {
            return calculateDelayGoToFullShade(viewState, animationStaggerCount);
        }
        if (this.mAnimationFilter.customDelay != -1) {
            return this.mAnimationFilter.customDelay;
        }
        long minDelay = 0;
        Iterator<NotificationStackScrollLayout.AnimationEvent> it = this.mNewEvents.iterator();
        while (it.hasNext()) {
            NotificationStackScrollLayout.AnimationEvent event = it.next();
            long delayPerElement = 80;
            int i = event.animationType;
            if (i == 0) {
                int ownIndex = viewState.notGoneIndex;
                int changingIndex = event.mChangingView.getViewState().notGoneIndex;
                int difference = Math.abs(ownIndex - changingIndex);
                long delay = (2 - Math.max(0, Math.min(2, difference - 1))) * 80;
                minDelay = Math.max(delay, minDelay);
            } else {
                if (i != 1) {
                    if (i == 2) {
                        delayPerElement = 32;
                    }
                }
                int ownIndex2 = viewState.notGoneIndex;
                boolean noNextView = event.viewAfterChangingView == null;
                if (noNextView) {
                    viewAfterChangingView = this.mHostLayout.getLastChildNotGone();
                } else {
                    viewAfterChangingView = (ExpandableView) event.viewAfterChangingView;
                }
                if (viewAfterChangingView != null) {
                    int nextIndex = viewAfterChangingView.getViewState().notGoneIndex;
                    if (ownIndex2 >= nextIndex) {
                        ownIndex2++;
                    }
                    int difference2 = Math.abs(ownIndex2 - nextIndex);
                    long delay2 = Math.max(0, Math.min(2, difference2 - 1)) * delayPerElement;
                    minDelay = Math.max(delay2, minDelay);
                }
            }
        }
        return minDelay;
    }

    private long calculateDelayGoToFullShade(ExpandableViewState viewState, int animationStaggerCount) {
        int shelfIndex = this.mShelf.getNotGoneIndex();
        float index = viewState.notGoneIndex;
        long result = 0;
        if (index > shelfIndex) {
            float diff = (float) Math.pow(animationStaggerCount, 0.699999988079071d);
            result = 0 + ((long) (diff * 48.0f * 0.25d));
            index = shelfIndex;
        }
        return result + (48.0f * ((float) Math.pow(index, 0.699999988079071d)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AnimatorListenerAdapter getGlobalAnimationFinishedListener() {
        if (!this.mAnimationListenerPool.empty()) {
            return this.mAnimationListenerPool.pop();
        }
        return new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.StackStateAnimator.2
            private boolean mWasCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                StackStateAnimator.this.mAnimatorSet.remove(animation);
                if (StackStateAnimator.this.mAnimatorSet.isEmpty() && !this.mWasCancelled) {
                    StackStateAnimator.this.onAnimationFinished();
                }
                StackStateAnimator.this.mAnimationListenerPool.push(this);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.mWasCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                this.mWasCancelled = false;
                StackStateAnimator.this.mAnimatorSet.add(animation);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationFinished() {
        this.mHostLayout.onChildAnimationFinished();
        Iterator<ExpandableView> it = this.mTransientViewsToRemove.iterator();
        while (it.hasNext()) {
            ExpandableView transientViewsToRemove = it.next();
            transientViewsToRemove.getTransientContainer().removeTransientView(transientViewsToRemove);
        }
        this.mTransientViewsToRemove.clear();
    }

    private void processAnimationEvents(ArrayList<NotificationStackScrollLayout.AnimationEvent> animationEvents) {
        int i;
        Runnable endRunnable;
        float targetLocation;
        boolean needsAnimation;
        float translationDirection;
        Iterator<NotificationStackScrollLayout.AnimationEvent> it = animationEvents.iterator();
        while (it.hasNext()) {
            NotificationStackScrollLayout.AnimationEvent event = it.next();
            final ExpandableView changingView = event.mChangingView;
            if (event.animationType == 0) {
                ExpandableViewState viewState = changingView.getViewState();
                if (viewState != null && !viewState.gone) {
                    viewState.applyToView(changingView);
                    this.mNewAddChildren.add(changingView);
                    this.mNewEvents.add(event);
                }
            } else {
                if (event.animationType == 1) {
                    if (changingView.getVisibility() != 0) {
                        removeTransientView(changingView);
                    } else {
                        if (event.viewAfterChangingView == null) {
                            translationDirection = -1.0f;
                        } else {
                            float ownPosition = changingView.getTranslationY();
                            if ((changingView instanceof ExpandableNotificationRow) && (event.viewAfterChangingView instanceof ExpandableNotificationRow)) {
                                ExpandableNotificationRow changingRow = (ExpandableNotificationRow) changingView;
                                ExpandableNotificationRow nextRow = (ExpandableNotificationRow) event.viewAfterChangingView;
                                if (changingRow.isRemoved() && changingRow.wasChildInGroupWhenRemoved() && !nextRow.isChildInGroup()) {
                                    ownPosition = changingRow.getTranslationWhenRemoved();
                                }
                            }
                            int actualHeight = changingView.getActualHeight();
                            float translationDirection2 = ((((ExpandableView) event.viewAfterChangingView).getViewState().yTranslation - ((actualHeight / 2.0f) + ownPosition)) * 2.0f) / actualHeight;
                            translationDirection = Math.max(Math.min(translationDirection2, 1.0f), -1.0f);
                        }
                        changingView.performRemoveAnimation(464L, 0L, translationDirection, false, 0.0f, new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$StackStateAnimator$TZG1mUHYcGvJktxtVi9se9juSC8
                            @Override // java.lang.Runnable
                            public final void run() {
                                StackStateAnimator.removeTransientView(ExpandableView.this);
                            }
                        }, null);
                    }
                } else if (event.animationType == 2) {
                    if (Math.abs(changingView.getTranslation()) == changingView.getWidth() && changingView.getTransientContainer() != null) {
                        changingView.getTransientContainer().removeTransientView(changingView);
                    }
                } else if (event.animationType == 10) {
                    ((ExpandableNotificationRow) event.mChangingView).prepareExpansionChanged();
                } else if (event.animationType == 11) {
                    this.mTmpState.copyFrom(changingView.getViewState());
                    if (event.headsUpFromBottom) {
                        this.mTmpState.yTranslation = this.mHeadsUpAppearHeightBottom;
                    } else {
                        this.mTmpState.yTranslation = 0.0f;
                        changingView.performAddAnimation(0L, ANIMATION_DURATION_HEADS_UP_APPEAR_CLOSED, true);
                    }
                    this.mHeadsUpAppearChildren.add(changingView);
                    this.mTmpState.applyToView(changingView);
                } else if (event.animationType == 12 || event.animationType == 13) {
                    this.mHeadsUpDisappearChildren.add(changingView);
                    if (event.animationType == 13) {
                        i = 120;
                    } else {
                        i = 0;
                    }
                    int extraDelay = i;
                    if (changingView.getParent() != null) {
                        endRunnable = null;
                    } else {
                        this.mHostLayout.addTransientView(changingView, 0);
                        changingView.setTransientContainer(this.mHostLayout);
                        this.mTmpState.initFrom(changingView);
                        ExpandableViewState expandableViewState = this.mTmpState;
                        expandableViewState.yTranslation = 0.0f;
                        this.mAnimationFilter.animateY = true;
                        AnimationProperties animationProperties = this.mAnimationProperties;
                        animationProperties.delay = extraDelay + 120;
                        animationProperties.duration = 300L;
                        expandableViewState.animateTo(changingView, animationProperties);
                        Runnable endRunnable2 = new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$StackStateAnimator$_Pk5aD8YGtEkv3ND7OecxMpqHJ4
                            @Override // java.lang.Runnable
                            public final void run() {
                                StackStateAnimator.removeTransientView(ExpandableView.this);
                            }
                        };
                        endRunnable = endRunnable2;
                    }
                    boolean needsAnimation2 = true;
                    if (!(changingView instanceof ExpandableNotificationRow)) {
                        targetLocation = 0.0f;
                        needsAnimation = true;
                    } else {
                        ExpandableNotificationRow row = (ExpandableNotificationRow) changingView;
                        if (row.isDismissed()) {
                            needsAnimation2 = false;
                        }
                        NotificationEntry entry = row.getEntry();
                        StatusBarIconView icon = entry.icon;
                        if (entry.centeredIcon != null && entry.centeredIcon.getParent() != null) {
                            icon = entry.centeredIcon;
                        }
                        if (icon.getParent() == null) {
                            targetLocation = 0.0f;
                            needsAnimation = needsAnimation2;
                        } else {
                            icon.getLocationOnScreen(this.mTmpLocation);
                            float iconPosition = (this.mTmpLocation[0] - icon.getTranslationX()) + ViewState.getFinalTranslationX(icon) + (icon.getWidth() * 0.25f);
                            this.mHostLayout.getLocationOnScreen(this.mTmpLocation);
                            float targetLocation2 = iconPosition - this.mTmpLocation[0];
                            targetLocation = targetLocation2;
                            needsAnimation = needsAnimation2;
                        }
                    }
                    if (needsAnimation) {
                        long removeAnimationDelay = changingView.performRemoveAnimation(420L, extraDelay, 0.0f, true, targetLocation, endRunnable, getGlobalAnimationFinishedListener());
                        this.mAnimationProperties.delay += removeAnimationDelay;
                    } else if (endRunnable != null) {
                        endRunnable.run();
                    }
                }
                this.mNewEvents.add(event);
            }
        }
    }

    public static void removeTransientView(ExpandableView viewToRemove) {
        if (viewToRemove.getTransientContainer() != null) {
            viewToRemove.getTransientContainer().removeTransientView(viewToRemove);
        }
    }

    public void animateOverScrollToAmount(float targetAmount, final boolean onTop, final boolean isRubberbanded) {
        float startOverScrollAmount = this.mHostLayout.getCurrentOverScrollAmount(onTop);
        if (targetAmount == startOverScrollAmount) {
            return;
        }
        cancelOverScrollAnimators(onTop);
        ValueAnimator overScrollAnimator = ValueAnimator.ofFloat(startOverScrollAmount, targetAmount);
        overScrollAnimator.setDuration(360L);
        overScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.stack.StackStateAnimator.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentOverScroll = ((Float) animation.getAnimatedValue()).floatValue();
                StackStateAnimator.this.mHostLayout.setOverScrollAmount(currentOverScroll, onTop, false, false, isRubberbanded);
            }
        });
        overScrollAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        overScrollAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.stack.StackStateAnimator.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (onTop) {
                    StackStateAnimator.this.mTopOverScrollAnimator = null;
                } else {
                    StackStateAnimator.this.mBottomOverScrollAnimator = null;
                }
            }
        });
        overScrollAnimator.start();
        if (onTop) {
            this.mTopOverScrollAnimator = overScrollAnimator;
        } else {
            this.mBottomOverScrollAnimator = overScrollAnimator;
        }
    }

    public void cancelOverScrollAnimators(boolean onTop) {
        ValueAnimator currentAnimator = onTop ? this.mTopOverScrollAnimator : this.mBottomOverScrollAnimator;
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
    }

    public void setHeadsUpAppearHeightBottom(int headsUpAppearHeightBottom) {
        this.mHeadsUpAppearHeightBottom = headsUpAppearHeightBottom;
    }

    public void setShadeExpanded(boolean shadeExpanded) {
        this.mShadeExpanded = shadeExpanded;
    }

    public void setShelf(NotificationShelf shelf) {
        this.mShelf = shelf;
    }
}
