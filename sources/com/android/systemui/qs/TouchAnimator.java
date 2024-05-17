package com.android.systemui.qs;

import android.util.FloatProperty;
import android.util.MathUtils;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes21.dex */
public class TouchAnimator {
    private static final FloatProperty<TouchAnimator> POSITION = new FloatProperty<TouchAnimator>(VuiConstants.ELEMENT_POSITION) { // from class: com.android.systemui.qs.TouchAnimator.1
        @Override // android.util.FloatProperty
        public void setValue(TouchAnimator touchAnimator, float value) {
            touchAnimator.setPosition(value);
        }

        @Override // android.util.Property
        public Float get(TouchAnimator touchAnimator) {
            return Float.valueOf(touchAnimator.mLastT);
        }
    };
    private final float mEndDelay;
    private final Interpolator mInterpolator;
    private final KeyframeSet[] mKeyframeSets;
    private float mLastT;
    private final Listener mListener;
    private final float mSpan;
    private final float mStartDelay;
    private final Object[] mTargets;

    /* loaded from: classes21.dex */
    public interface Listener {
        void onAnimationAtEnd();

        void onAnimationAtStart();

        void onAnimationStarted();
    }

    private TouchAnimator(Object[] targets, KeyframeSet[] keyframeSets, float startDelay, float endDelay, Interpolator interpolator, Listener listener) {
        this.mLastT = -1.0f;
        this.mTargets = targets;
        this.mKeyframeSets = keyframeSets;
        this.mStartDelay = startDelay;
        this.mEndDelay = endDelay;
        this.mSpan = (1.0f - this.mEndDelay) - this.mStartDelay;
        this.mInterpolator = interpolator;
        this.mListener = listener;
    }

    public void setPosition(float fraction) {
        float t = MathUtils.constrain((fraction - this.mStartDelay) / this.mSpan, 0.0f, 1.0f);
        Interpolator interpolator = this.mInterpolator;
        if (interpolator != null) {
            t = interpolator.getInterpolation(t);
        }
        float f = this.mLastT;
        if (t == f) {
            return;
        }
        Listener listener = this.mListener;
        if (listener != null) {
            if (t == 1.0f) {
                listener.onAnimationAtEnd();
            } else if (t == 0.0f) {
                listener.onAnimationAtStart();
            } else if (f <= 0.0f || f == 1.0f) {
                this.mListener.onAnimationStarted();
            }
            this.mLastT = t;
        }
        int i = 0;
        while (true) {
            Object[] objArr = this.mTargets;
            if (i < objArr.length) {
                this.mKeyframeSets[i].setValue(t, objArr[i]);
                i++;
            } else {
                return;
            }
        }
    }

    /* loaded from: classes21.dex */
    public static class ListenerAdapter implements Listener {
        @Override // com.android.systemui.qs.TouchAnimator.Listener
        public void onAnimationAtStart() {
        }

        @Override // com.android.systemui.qs.TouchAnimator.Listener
        public void onAnimationAtEnd() {
        }

        @Override // com.android.systemui.qs.TouchAnimator.Listener
        public void onAnimationStarted() {
        }
    }

    /* loaded from: classes21.dex */
    public static class Builder {
        private float mEndDelay;
        private Interpolator mInterpolator;
        private Listener mListener;
        private float mStartDelay;
        private List<Object> mTargets = new ArrayList();
        private List<KeyframeSet> mValues = new ArrayList();

        public Builder addFloat(Object target, String property, float... values) {
            add(target, KeyframeSet.ofFloat(getProperty(target, property, Float.TYPE), values));
            return this;
        }

        public Builder addInt(Object target, String property, int... values) {
            add(target, KeyframeSet.ofInt(getProperty(target, property, Integer.TYPE), values));
            return this;
        }

        private void add(Object target, KeyframeSet keyframeSet) {
            this.mTargets.add(target);
            this.mValues.add(keyframeSet);
        }

        private static Property getProperty(Object target, String property, Class<?> cls) {
            if (target instanceof View) {
                char c = 65535;
                switch (property.hashCode()) {
                    case -1225497657:
                        if (property.equals("translationX")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1225497656:
                        if (property.equals("translationY")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -1225497655:
                        if (property.equals("translationZ")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -908189618:
                        if (property.equals("scaleX")) {
                            c = 7;
                            break;
                        }
                        break;
                    case -908189617:
                        if (property.equals("scaleY")) {
                            c = '\b';
                            break;
                        }
                        break;
                    case -40300674:
                        if (property.equals("rotation")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 120:
                        if (property.equals("x")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 121:
                        if (property.equals("y")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 92909918:
                        if (property.equals(ThemeManager.AttributeSet.ALPHA)) {
                            c = 3;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        return View.TRANSLATION_X;
                    case 1:
                        return View.TRANSLATION_Y;
                    case 2:
                        return View.TRANSLATION_Z;
                    case 3:
                        return View.ALPHA;
                    case 4:
                        return View.ROTATION;
                    case 5:
                        return View.X;
                    case 6:
                        return View.Y;
                    case 7:
                        return View.SCALE_X;
                    case '\b':
                        return View.SCALE_Y;
                }
            }
            if ((target instanceof TouchAnimator) && VuiConstants.ELEMENT_POSITION.equals(property)) {
                return TouchAnimator.POSITION;
            }
            return Property.of(target.getClass(), cls, property);
        }

        public Builder setStartDelay(float startDelay) {
            this.mStartDelay = startDelay;
            return this;
        }

        public Builder setEndDelay(float endDelay) {
            this.mEndDelay = endDelay;
            return this;
        }

        public Builder setInterpolator(Interpolator intepolator) {
            this.mInterpolator = intepolator;
            return this;
        }

        public Builder setListener(Listener listener) {
            this.mListener = listener;
            return this;
        }

        public TouchAnimator build() {
            List<Object> list = this.mTargets;
            Object[] array = list.toArray(new Object[list.size()]);
            List<KeyframeSet> list2 = this.mValues;
            return new TouchAnimator(array, (KeyframeSet[]) list2.toArray(new KeyframeSet[list2.size()]), this.mStartDelay, this.mEndDelay, this.mInterpolator, this.mListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static abstract class KeyframeSet {
        private final float mFrameWidth;
        private final int mSize;

        protected abstract void interpolate(int i, float f, Object obj);

        public KeyframeSet(int size) {
            this.mSize = size;
            this.mFrameWidth = 1.0f / (size - 1);
        }

        void setValue(float fraction, Object target) {
            int i = MathUtils.constrain((int) Math.ceil(fraction / this.mFrameWidth), 1, this.mSize - 1);
            float f = this.mFrameWidth;
            float amount = (fraction - ((i - 1) * f)) / f;
            interpolate(i, amount, target);
        }

        public static KeyframeSet ofInt(Property property, int... values) {
            return new IntKeyframeSet(property, values);
        }

        public static KeyframeSet ofFloat(Property property, float... values) {
            return new FloatKeyframeSet(property, values);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class FloatKeyframeSet<T> extends KeyframeSet {
        private final Property<T, Float> mProperty;
        private final float[] mValues;

        public FloatKeyframeSet(Property<T, Float> property, float[] values) {
            super(values.length);
            this.mProperty = property;
            this.mValues = values;
        }

        @Override // com.android.systemui.qs.TouchAnimator.KeyframeSet
        protected void interpolate(int index, float amount, Object target) {
            float[] fArr = this.mValues;
            float firstFloat = fArr[index - 1];
            float secondFloat = fArr[index];
            this.mProperty.set(target, Float.valueOf(((secondFloat - firstFloat) * amount) + firstFloat));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class IntKeyframeSet<T> extends KeyframeSet {
        private final Property<T, Integer> mProperty;
        private final int[] mValues;

        public IntKeyframeSet(Property<T, Integer> property, int[] values) {
            super(values.length);
            this.mProperty = property;
            this.mValues = values;
        }

        @Override // com.android.systemui.qs.TouchAnimator.KeyframeSet
        protected void interpolate(int index, float amount, Object target) {
            int[] iArr = this.mValues;
            int firstFloat = iArr[index - 1];
            int secondFloat = iArr[index];
            this.mProperty.set(target, Integer.valueOf((int) (firstFloat + ((secondFloat - firstFloat) * amount))));
        }
    }
}
