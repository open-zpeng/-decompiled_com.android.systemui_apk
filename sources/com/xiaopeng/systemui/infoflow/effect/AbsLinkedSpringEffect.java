package com.xiaopeng.systemui.infoflow.effect;

import android.graphics.Canvas;
import androidx.annotation.CallSuper;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.xiaopeng.systemui.infoflow.effect.AbsLinkedSpringEffect.State;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public abstract class AbsLinkedSpringEffect<T extends State> extends AbsEffect {
    private static final float DEFAULT_AMPLITUDE_ENHANCE = 0.3f;
    private OnUpdateFloatValueListener mOnUpdateFloatValueListener;
    private final List<T> mStateList = new ArrayList();
    private float mAmplitudeEnhance = DEFAULT_AMPLITUDE_ENHANCE;
    private SpringEffectInterpolator mMainEffectInterpolator = new SpringEffectInterpolator();

    /* loaded from: classes24.dex */
    public interface OnUpdateFloatValueListener {
        void onUpdate(float f);
    }

    protected abstract T createState(SpringAnimation springAnimation, FloatValueHolder floatValueHolder, float f, int i);

    public AbsLinkedSpringEffect(int count) {
        FloatValueHolder floatValueHolder;
        SpringAnimation before = null;
        SpringForce springForce = this.mMainEffectInterpolator.getSpringAnimation().getSpring();
        springForce.setStiffness(200.0f);
        springForce.setDampingRatio(1.0f);
        SpringAnimation mainSpringAnimation = this.mMainEffectInterpolator.getSpringAnimation();
        for (int i = 0; i < count; i++) {
            float amplitudeEnhance = (i * this.mAmplitudeEnhance) + 1.0f;
            if (i == 0) {
                floatValueHolder = this.mMainEffectInterpolator.getFloatValueHolder();
                before = mainSpringAnimation;
            } else {
                floatValueHolder = new FloatValueHolder();
                before = createSpringAnimation(before, floatValueHolder);
            }
            this.mStateList.add(createState(before, floatValueHolder, amplitudeEnhance, i));
        }
    }

    public void updateFloatValue(OnUpdateFloatValueListener updateFloatValueListener) {
        this.mOnUpdateFloatValueListener = updateFloatValueListener;
    }

    public OnUpdateFloatValueListener getOnUpdateFloatValueListener() {
        return this.mOnUpdateFloatValueListener;
    }

    public int getStateCount() {
        return this.mStateList.size();
    }

    public List<T> getStateList() {
        return this.mStateList;
    }

    public float getAmplitudeEnhance() {
        return this.mAmplitudeEnhance;
    }

    public void setAmplitudeEnhance(float amplitudeEnhance) {
        this.mAmplitudeEnhance = amplitudeEnhance;
        int i = -1;
        for (T state : this.mStateList) {
            i++;
            state.amplitudeEnhance = (i * this.mAmplitudeEnhance) + 1.0f;
        }
    }

    public void setMainAnimationStiffness(float stiffness) {
        this.mMainEffectInterpolator.getSpringAnimation().getSpring().setStiffness(stiffness);
    }

    public SpringEffectInterpolator getMainEffectInterpolator() {
        return this.mMainEffectInterpolator;
    }

    public void setMainAnimationDampingRatio(float dampingRatio) {
        this.mMainEffectInterpolator.getSpringAnimation().getSpring().setDampingRatio(dampingRatio);
    }

    public void setChildAnimationStiffness(float stiffness) {
        int index = -1;
        for (T state : this.mStateList) {
            index++;
            if (index > 0) {
                state.springAnimation.getSpring().setStiffness(stiffness);
            }
        }
    }

    public void setChildAnimationDampingRatio(float dampingRatio) {
        int index = -1;
        for (T state : this.mStateList) {
            index++;
            if (index > 0) {
                state.springAnimation.getSpring().setDampingRatio(dampingRatio);
            }
        }
    }

    private SpringAnimation createSpringAnimation(SpringAnimation before, FloatValueHolder floatValueHolder) {
        final SpringAnimation springAnimation = new SpringAnimation(floatValueHolder);
        SpringForce force = new SpringForce();
        force.setDampingRatio(1.0f);
        force.setStiffness(1500.0f);
        if (before != null) {
            before.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.effect.AbsLinkedSpringEffect.1
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                public void onAnimationUpdate(DynamicAnimation dynamicAnimation, float value, float velocity) {
                    springAnimation.animateToFinalPosition(value);
                    springAnimation.start();
                }
            });
        }
        springAnimation.setSpring(force);
        return springAnimation;
    }

    private void wave() {
        for (int index = 0; index < this.mStateList.size(); index++) {
            T state = this.mStateList.get(index);
            float value = state.floatValueHolder.getValue();
            state.currentDistance = (int) (state.distance + (state.amplitudeEnhance * value));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    @CallSuper
    public void onDraw(Canvas canvas) {
        wave();
        OnUpdateFloatValueListener onUpdateFloatValueListener = this.mOnUpdateFloatValueListener;
        if (onUpdateFloatValueListener != null) {
            onUpdateFloatValueListener.onUpdate(this.mMainEffectInterpolator.getFloatValueHolder().getValue());
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.effect.AbsEffect
    public void onUpdate(float value) {
        this.mMainEffectInterpolator.update(value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes24.dex */
    public static class State {
        float amplitudeEnhance;
        int currentDistance;
        final int distance;
        final FloatValueHolder floatValueHolder;
        int maxDistance;
        SpringAnimation springAnimation;

        public State(SpringAnimation springAnimation, FloatValueHolder floatValueHolder, float amplitudeEnhance, int distance, int currentDistance) {
            this.springAnimation = springAnimation;
            this.floatValueHolder = floatValueHolder;
            this.amplitudeEnhance = amplitudeEnhance;
            this.distance = distance;
            this.currentDistance = currentDistance;
        }
    }
}
