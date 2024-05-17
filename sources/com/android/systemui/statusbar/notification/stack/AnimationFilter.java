package com.android.systemui.statusbar.notification.stack;

import android.util.Property;
import android.view.View;
import androidx.collection.ArraySet;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class AnimationFilter {
    public static final int NO_DELAY = -1;
    boolean animateAlpha;
    boolean animateDimmed;
    boolean animateHeight;
    boolean animateHideSensitive;
    boolean animateTopInset;
    boolean animateX;
    boolean animateY;
    boolean animateZ;
    long customDelay;
    boolean hasDelays;
    boolean hasGoToFullShadeEvent;
    ArraySet<View> animateYViews = new ArraySet<>();
    private ArraySet<Property> mAnimatedProperties = new ArraySet<>();

    public AnimationFilter animateAlpha() {
        this.animateAlpha = true;
        return this;
    }

    public AnimationFilter animateScale() {
        animate(View.SCALE_X);
        animate(View.SCALE_Y);
        return this;
    }

    public AnimationFilter animateX() {
        this.animateX = true;
        return this;
    }

    public AnimationFilter animateY() {
        this.animateY = true;
        return this;
    }

    public AnimationFilter hasDelays() {
        this.hasDelays = true;
        return this;
    }

    public AnimationFilter animateZ() {
        this.animateZ = true;
        return this;
    }

    public AnimationFilter animateHeight() {
        this.animateHeight = true;
        return this;
    }

    public AnimationFilter animateTopInset() {
        this.animateTopInset = true;
        return this;
    }

    public AnimationFilter animateDimmed() {
        this.animateDimmed = true;
        return this;
    }

    public AnimationFilter animateHideSensitive() {
        this.animateHideSensitive = true;
        return this;
    }

    public AnimationFilter animateY(View view) {
        this.animateYViews.add(view);
        return this;
    }

    public boolean shouldAnimateY(View view) {
        return this.animateY || this.animateYViews.contains(view);
    }

    public void applyCombination(ArrayList<NotificationStackScrollLayout.AnimationEvent> events) {
        reset();
        int size = events.size();
        for (int i = 0; i < size; i++) {
            NotificationStackScrollLayout.AnimationEvent ev = events.get(i);
            combineFilter(events.get(i).filter);
            if (ev.animationType == 7) {
                this.hasGoToFullShadeEvent = true;
            }
            if (ev.animationType == 12) {
                this.customDelay = 120L;
            } else if (ev.animationType == 13) {
                this.customDelay = 240L;
            }
        }
    }

    public void combineFilter(AnimationFilter filter) {
        this.animateAlpha |= filter.animateAlpha;
        this.animateX |= filter.animateX;
        this.animateY |= filter.animateY;
        this.animateYViews.addAll((ArraySet<? extends View>) filter.animateYViews);
        this.animateZ |= filter.animateZ;
        this.animateHeight |= filter.animateHeight;
        this.animateTopInset |= filter.animateTopInset;
        this.animateDimmed |= filter.animateDimmed;
        this.animateHideSensitive |= filter.animateHideSensitive;
        this.hasDelays |= filter.hasDelays;
        this.mAnimatedProperties.addAll((ArraySet<? extends Property>) filter.mAnimatedProperties);
    }

    public void reset() {
        this.animateAlpha = false;
        this.animateX = false;
        this.animateY = false;
        this.animateYViews.clear();
        this.animateZ = false;
        this.animateHeight = false;
        this.animateTopInset = false;
        this.animateDimmed = false;
        this.animateHideSensitive = false;
        this.hasDelays = false;
        this.hasGoToFullShadeEvent = false;
        this.customDelay = -1L;
        this.mAnimatedProperties.clear();
    }

    public AnimationFilter animate(Property property) {
        this.mAnimatedProperties.add(property);
        return this;
    }

    public boolean shouldAnimateProperty(Property property) {
        return this.mAnimatedProperties.contains(property);
    }
}
