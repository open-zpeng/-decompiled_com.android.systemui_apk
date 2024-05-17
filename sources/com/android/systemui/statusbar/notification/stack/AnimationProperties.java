package com.android.systemui.statusbar.notification.stack;

import android.animation.AnimatorListenerAdapter;
import android.util.ArrayMap;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;
/* loaded from: classes21.dex */
public class AnimationProperties {
    public long delay;
    public long duration;
    private AnimatorListenerAdapter mAnimatorListenerAdapter;
    private ArrayMap<Property, Interpolator> mInterpolatorMap;

    public AnimationFilter getAnimationFilter() {
        return new AnimationFilter() { // from class: com.android.systemui.statusbar.notification.stack.AnimationProperties.1
            @Override // com.android.systemui.statusbar.notification.stack.AnimationFilter
            public boolean shouldAnimateProperty(Property property) {
                return true;
            }
        };
    }

    public AnimatorListenerAdapter getAnimationFinishListener() {
        return this.mAnimatorListenerAdapter;
    }

    public AnimationProperties setAnimationFinishListener(AnimatorListenerAdapter listener) {
        this.mAnimatorListenerAdapter = listener;
        return this;
    }

    public boolean wasAdded(View view) {
        return false;
    }

    public Interpolator getCustomInterpolator(View child, Property property) {
        ArrayMap<Property, Interpolator> arrayMap = this.mInterpolatorMap;
        if (arrayMap != null) {
            return arrayMap.get(property);
        }
        return null;
    }

    public void combineCustomInterpolators(AnimationProperties iconAnimationProperties) {
        ArrayMap<Property, Interpolator> map = iconAnimationProperties.mInterpolatorMap;
        if (map != null) {
            if (this.mInterpolatorMap == null) {
                this.mInterpolatorMap = new ArrayMap<>();
            }
            this.mInterpolatorMap.putAll((ArrayMap<? extends Property, ? extends Interpolator>) map);
        }
    }

    public AnimationProperties setCustomInterpolator(Property property, Interpolator interpolator) {
        if (this.mInterpolatorMap == null) {
            this.mInterpolatorMap = new ArrayMap<>();
        }
        this.mInterpolatorMap.put(property, interpolator);
        return this;
    }

    public AnimationProperties setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public AnimationProperties setDelay(long delay) {
        this.delay = delay;
        return this;
    }

    public AnimationProperties resetCustomInterpolators() {
        this.mInterpolatorMap = null;
        return this;
    }
}
