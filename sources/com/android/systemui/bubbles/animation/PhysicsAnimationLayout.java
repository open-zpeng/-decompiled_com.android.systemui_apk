package com.android.systemui.bubbles.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.systemui.R;
import com.android.systemui.bubbles.animation.PhysicsAnimationLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes21.dex */
public class PhysicsAnimationLayout extends FrameLayout {
    private static final String TAG = "Bubbs.PAL";
    @Nullable
    protected PhysicsAnimationController mController;
    protected final HashMap<DynamicAnimation.ViewProperty, Runnable> mEndActionForProperty;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public static abstract class PhysicsAnimationController {
        protected static final int NONE = -1;
        protected PhysicsAnimationLayout mLayout;

        /* loaded from: classes21.dex */
        interface ChildAnimationConfigurator {
            void configureAnimationForChildAtIndex(int i, PhysicsPropertyAnimator physicsPropertyAnimator);
        }

        /* loaded from: classes21.dex */
        interface MultiAnimationStarter {
            void startAll(Runnable... runnableArr);
        }

        abstract Set<DynamicAnimation.ViewProperty> getAnimatedProperties();

        abstract int getNextAnimationInChain(DynamicAnimation.ViewProperty viewProperty, int i);

        abstract float getOffsetForChainedPropertyAnimation(DynamicAnimation.ViewProperty viewProperty);

        abstract SpringForce getSpringForce(DynamicAnimation.ViewProperty viewProperty, View view);

        abstract void onActiveControllerForLayout(PhysicsAnimationLayout physicsAnimationLayout);

        abstract void onChildAdded(View view, int i);

        abstract void onChildRemoved(View view, int i, Runnable runnable);

        abstract void onChildReordered(View view, int i, int i2);

        /* JADX INFO: Access modifiers changed from: protected */
        public boolean isActiveController() {
            PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
            return physicsAnimationLayout != null && this == physicsAnimationLayout.mController;
        }

        protected void setLayout(PhysicsAnimationLayout layout) {
            this.mLayout = layout;
            onActiveControllerForLayout(layout);
        }

        protected PhysicsAnimationLayout getLayout() {
            return this.mLayout;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public PhysicsPropertyAnimator animationForChild(View child) {
            PhysicsPropertyAnimator animator = (PhysicsPropertyAnimator) child.getTag(R.id.physics_animator_tag);
            if (animator == null) {
                PhysicsAnimationLayout physicsAnimationLayout = this.mLayout;
                Objects.requireNonNull(physicsAnimationLayout);
                animator = new PhysicsPropertyAnimator(child);
                child.setTag(R.id.physics_animator_tag, animator);
            }
            animator.clearAnimator();
            animator.setAssociatedController(this);
            return animator;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public PhysicsPropertyAnimator animationForChildAtIndex(int index) {
            return animationForChild(this.mLayout.getChildAt(index));
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public MultiAnimationStarter animationsForChildrenFromIndex(int startIndex, ChildAnimationConfigurator configurator) {
            final Set<DynamicAnimation.ViewProperty> allAnimatedProperties = new HashSet<>();
            final List<PhysicsPropertyAnimator> allChildAnims = new ArrayList<>();
            for (int i = startIndex; i < this.mLayout.getChildCount(); i++) {
                PhysicsPropertyAnimator anim = animationForChildAtIndex(i);
                configurator.configureAnimationForChildAtIndex(i, anim);
                allAnimatedProperties.addAll(anim.getAnimatedProperties());
                allChildAnims.add(anim);
            }
            return new MultiAnimationStarter() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$PhysicsAnimationLayout$PhysicsAnimationController$QukG2X_vIQ5QkpRissMu_oS31l0
                @Override // com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsAnimationController.MultiAnimationStarter
                public final void startAll(Runnable[] runnableArr) {
                    PhysicsAnimationLayout.PhysicsAnimationController.this.lambda$animationsForChildrenFromIndex$1$PhysicsAnimationLayout$PhysicsAnimationController(allAnimatedProperties, allChildAnims, runnableArr);
                }
            };
        }

        public /* synthetic */ void lambda$animationsForChildrenFromIndex$1$PhysicsAnimationLayout$PhysicsAnimationController(Set allAnimatedProperties, List allChildAnims, final Runnable[] endActions) {
            Runnable runAllEndActions = new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$PhysicsAnimationLayout$PhysicsAnimationController$Q2IEgFt-VQbcjE9VQhU6hzQCTEA
                @Override // java.lang.Runnable
                public final void run() {
                    PhysicsAnimationLayout.PhysicsAnimationController.lambda$animationsForChildrenFromIndex$0(endActions);
                }
            };
            if (this.mLayout.getChildCount() == 0) {
                runAllEndActions.run();
                return;
            }
            if (endActions != null) {
                setEndActionForMultipleProperties(runAllEndActions, (DynamicAnimation.ViewProperty[]) allAnimatedProperties.toArray(new DynamicAnimation.ViewProperty[0]));
            }
            Iterator it = allChildAnims.iterator();
            while (it.hasNext()) {
                PhysicsPropertyAnimator childAnim = (PhysicsPropertyAnimator) it.next();
                childAnim.start(new Runnable[0]);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$animationsForChildrenFromIndex$0(Runnable[] endActions) {
            for (Runnable action : endActions) {
                action.run();
            }
        }

        protected void setEndActionForProperty(Runnable action, DynamicAnimation.ViewProperty property) {
            this.mLayout.mEndActionForProperty.put(property, action);
        }

        protected void setEndActionForMultipleProperties(final Runnable action, final DynamicAnimation.ViewProperty... properties) {
            Runnable checkIfAllFinished = new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$PhysicsAnimationLayout$PhysicsAnimationController$k470cCDrnNZB7vKHsf7OzOwkMRY
                @Override // java.lang.Runnable
                public final void run() {
                    PhysicsAnimationLayout.PhysicsAnimationController.this.lambda$setEndActionForMultipleProperties$2$PhysicsAnimationLayout$PhysicsAnimationController(properties, action);
                }
            };
            for (DynamicAnimation.ViewProperty property : properties) {
                setEndActionForProperty(checkIfAllFinished, property);
            }
        }

        public /* synthetic */ void lambda$setEndActionForMultipleProperties$2$PhysicsAnimationLayout$PhysicsAnimationController(DynamicAnimation.ViewProperty[] properties, Runnable action) {
            if (!this.mLayout.arePropertiesAnimating(properties)) {
                action.run();
                for (DynamicAnimation.ViewProperty property : properties) {
                    removeEndActionForProperty(property);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void removeEndActionForProperty(DynamicAnimation.ViewProperty property) {
            this.mLayout.mEndActionForProperty.remove(property);
        }
    }

    public PhysicsAnimationLayout(Context context) {
        super(context);
        this.mEndActionForProperty = new HashMap<>();
    }

    public void setActiveController(PhysicsAnimationController controller) {
        cancelAllAnimations();
        this.mEndActionForProperty.clear();
        this.mController = controller;
        this.mController.setLayout(this);
        for (DynamicAnimation.ViewProperty property : this.mController.getAnimatedProperties()) {
            setUpAnimationsForProperty(property);
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addViewInternal(child, index, params, false);
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(final View view) {
        if (this.mController != null) {
            int index = indexOfChild(view);
            super.removeView(view);
            addTransientView(view, index);
            this.mController.onChildRemoved(view, index, new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$PhysicsAnimationLayout$VGQ81KsCYiJ-C0alb-wfA2McXCU
                @Override // java.lang.Runnable
                public final void run() {
                    PhysicsAnimationLayout.this.lambda$removeView$0$PhysicsAnimationLayout(view);
                }
            });
            return;
        }
        super.removeView(view);
    }

    public /* synthetic */ void lambda$removeView$0$PhysicsAnimationLayout(View view) {
        cancelAnimationsOnView(view);
        removeTransientView(view);
    }

    @Override // android.view.ViewGroup
    public void removeViewAt(int index) {
        removeView(getChildAt(index));
    }

    public void reorderView(View view, int index) {
        int oldIndex = indexOfChild(view);
        super.removeView(view);
        addViewInternal(view, index, view.getLayoutParams(), true);
        PhysicsAnimationController physicsAnimationController = this.mController;
        if (physicsAnimationController != null) {
            physicsAnimationController.onChildReordered(view, oldIndex, index);
        }
    }

    public boolean arePropertiesAnimating(DynamicAnimation.ViewProperty... properties) {
        for (int i = 0; i < getChildCount(); i++) {
            if (arePropertiesAnimatingOnView(getChildAt(i), properties)) {
                return true;
            }
        }
        return false;
    }

    public boolean arePropertiesAnimatingOnView(View view, DynamicAnimation.ViewProperty... properties) {
        ObjectAnimator targetAnimator = getTargetAnimatorFromView(view);
        for (DynamicAnimation.ViewProperty property : properties) {
            SpringAnimation animation = getAnimationFromView(property, view);
            if (animation != null && animation.isRunning()) {
                return true;
            }
            boolean isTranslation = property.equals(DynamicAnimation.TRANSLATION_X) || property.equals(DynamicAnimation.TRANSLATION_Y);
            if (isTranslation && targetAnimator != null && targetAnimator.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public void cancelAllAnimations() {
        PhysicsAnimationController physicsAnimationController = this.mController;
        if (physicsAnimationController == null) {
            return;
        }
        cancelAllAnimationsOfProperties((DynamicAnimation.ViewProperty[]) physicsAnimationController.getAnimatedProperties().toArray(new DynamicAnimation.ViewProperty[0]));
    }

    public void cancelAllAnimationsOfProperties(DynamicAnimation.ViewProperty... properties) {
        if (this.mController == null) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            for (DynamicAnimation.ViewProperty property : properties) {
                DynamicAnimation anim = getAnimationAtIndex(property, i);
                if (anim != null) {
                    anim.cancel();
                }
            }
        }
    }

    public void cancelAnimationsOnView(View view) {
        ObjectAnimator targetAnimator = getTargetAnimatorFromView(view);
        if (targetAnimator != null) {
            targetAnimator.cancel();
        }
        for (DynamicAnimation.ViewProperty property : this.mController.getAnimatedProperties()) {
            getAnimationFromView(property, view).cancel();
        }
    }

    protected boolean isActiveController(PhysicsAnimationController controller) {
        return this.mController == controller;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isFirstChildXLeftOfCenter(float x) {
        return getChildCount() > 0 && ((float) (getChildAt(0).getWidth() / 2)) + x < ((float) (getWidth() / 2));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String getReadablePropertyName(DynamicAnimation.ViewProperty property) {
        if (property.equals(DynamicAnimation.TRANSLATION_X)) {
            return "TRANSLATION_X";
        }
        if (property.equals(DynamicAnimation.TRANSLATION_Y)) {
            return "TRANSLATION_Y";
        }
        if (property.equals(DynamicAnimation.SCALE_X)) {
            return "SCALE_X";
        }
        if (property.equals(DynamicAnimation.SCALE_Y)) {
            return "SCALE_Y";
        }
        if (property.equals(DynamicAnimation.ALPHA)) {
            return "ALPHA";
        }
        return "Unknown animation property.";
    }

    private void addViewInternal(View child, int index, ViewGroup.LayoutParams params, boolean isReorder) {
        super.addView(child, index, params);
        PhysicsAnimationController physicsAnimationController = this.mController;
        if (physicsAnimationController != null && !isReorder) {
            for (DynamicAnimation.ViewProperty property : physicsAnimationController.getAnimatedProperties()) {
                setUpAnimationForChild(property, child, index);
            }
            this.mController.onChildAdded(child, index);
        }
    }

    private SpringAnimation getAnimationAtIndex(DynamicAnimation.ViewProperty property, int index) {
        return getAnimationFromView(property, getChildAt(index));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SpringAnimation getAnimationFromView(DynamicAnimation.ViewProperty property, View view) {
        return (SpringAnimation) view.getTag(getTagIdForProperty(property));
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public ObjectAnimator getTargetAnimatorFromView(View view) {
        return (ObjectAnimator) view.getTag(R.id.target_animator_tag);
    }

    private void setUpAnimationsForProperty(DynamicAnimation.ViewProperty property) {
        for (int i = 0; i < getChildCount(); i++) {
            setUpAnimationForChild(property, getChildAt(i), i);
        }
    }

    private void setUpAnimationForChild(final DynamicAnimation.ViewProperty property, final View child, int index) {
        SpringAnimation newAnim = new SpringAnimation(child, property);
        newAnim.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$PhysicsAnimationLayout$zwbjiGEsnfRdNGFmqcdzTxp4TUg
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                PhysicsAnimationLayout.this.lambda$setUpAnimationForChild$1$PhysicsAnimationLayout(child, property, dynamicAnimation, f, f2);
            }
        });
        newAnim.setSpring(this.mController.getSpringForce(property, child));
        newAnim.addEndListener(new AllAnimationsForPropertyFinishedEndListener(property));
        child.setTag(getTagIdForProperty(property), newAnim);
    }

    public /* synthetic */ void lambda$setUpAnimationForChild$1$PhysicsAnimationLayout(View child, DynamicAnimation.ViewProperty property, DynamicAnimation animation, float value, float velocity) {
        int indexOfChild = indexOfChild(child);
        int nextAnimInChain = this.mController.getNextAnimationInChain(property, indexOfChild);
        if (nextAnimInChain == -1 || indexOfChild < 0) {
            return;
        }
        float offset = this.mController.getOffsetForChainedPropertyAnimation(property);
        if (nextAnimInChain < getChildCount()) {
            getAnimationAtIndex(property, nextAnimInChain).animateToFinalPosition(value + offset);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getTagIdForProperty(DynamicAnimation.ViewProperty property) {
        if (property.equals(DynamicAnimation.TRANSLATION_X)) {
            return R.id.translation_x_dynamicanimation_tag;
        }
        if (property.equals(DynamicAnimation.TRANSLATION_Y)) {
            return R.id.translation_y_dynamicanimation_tag;
        }
        if (property.equals(DynamicAnimation.SCALE_X)) {
            return R.id.scale_x_dynamicanimation_tag;
        }
        if (property.equals(DynamicAnimation.SCALE_Y)) {
            return R.id.scale_y_dynamicanimation_tag;
        }
        if (property.equals(DynamicAnimation.ALPHA)) {
            return R.id.alpha_dynamicanimation_tag;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class AllAnimationsForPropertyFinishedEndListener implements DynamicAnimation.OnAnimationEndListener {
        private DynamicAnimation.ViewProperty mProperty;

        AllAnimationsForPropertyFinishedEndListener(DynamicAnimation.ViewProperty property) {
            this.mProperty = property;
        }

        @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
        public void onAnimationEnd(DynamicAnimation anim, boolean canceled, float value, float velocity) {
            Runnable callback;
            if (!PhysicsAnimationLayout.this.arePropertiesAnimating(this.mProperty) && PhysicsAnimationLayout.this.mEndActionForProperty.containsKey(this.mProperty) && (callback = PhysicsAnimationLayout.this.mEndActionForProperty.get(this.mProperty)) != null) {
                callback.run();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public class PhysicsPropertyAnimator {
        private PhysicsAnimationController mAssociatedController;
        @Nullable
        private ObjectAnimator mPathAnimator;
        @Nullable
        private Runnable[] mPositionEndActions;
        private View mView;
        private float mDefaultStartVelocity = -3.4028235E38f;
        private long mStartDelay = 0;
        private float mDampingRatio = -1.0f;
        private float mStiffness = -1.0f;
        private Map<DynamicAnimation.ViewProperty, Runnable[]> mEndActionsForProperty = new HashMap();
        private Map<DynamicAnimation.ViewProperty, Float> mPositionStartVelocities = new HashMap();
        private Map<DynamicAnimation.ViewProperty, Float> mAnimatedProperties = new HashMap();
        private Map<DynamicAnimation.ViewProperty, Float> mInitialPropertyValues = new HashMap();
        private PointF mCurrentPointOnPath = new PointF();
        private final FloatProperty<PhysicsPropertyAnimator> mCurrentPointOnPathXProperty = new FloatProperty<PhysicsPropertyAnimator>("PathX") { // from class: com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsPropertyAnimator.1
            @Override // android.util.FloatProperty
            public void setValue(PhysicsPropertyAnimator object, float value) {
                PhysicsPropertyAnimator.this.mCurrentPointOnPath.x = value;
            }

            @Override // android.util.Property
            public Float get(PhysicsPropertyAnimator object) {
                return Float.valueOf(PhysicsPropertyAnimator.this.mCurrentPointOnPath.x);
            }
        };
        private final FloatProperty<PhysicsPropertyAnimator> mCurrentPointOnPathYProperty = new FloatProperty<PhysicsPropertyAnimator>("PathY") { // from class: com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsPropertyAnimator.2
            @Override // android.util.FloatProperty
            public void setValue(PhysicsPropertyAnimator object, float value) {
                PhysicsPropertyAnimator.this.mCurrentPointOnPath.y = value;
            }

            @Override // android.util.Property
            public Float get(PhysicsPropertyAnimator object) {
                return Float.valueOf(PhysicsPropertyAnimator.this.mCurrentPointOnPath.y);
            }
        };

        protected PhysicsPropertyAnimator(View view) {
            this.mView = view;
        }

        public PhysicsPropertyAnimator property(DynamicAnimation.ViewProperty property, float value, Runnable... endActions) {
            this.mAnimatedProperties.put(property, Float.valueOf(value));
            this.mEndActionsForProperty.put(property, endActions);
            return this;
        }

        public PhysicsPropertyAnimator alpha(float alpha, Runnable... endActions) {
            return property(DynamicAnimation.ALPHA, alpha, endActions);
        }

        public PhysicsPropertyAnimator alpha(float from, float to, Runnable... endActions) {
            this.mInitialPropertyValues.put(DynamicAnimation.ALPHA, Float.valueOf(from));
            return alpha(to, endActions);
        }

        public PhysicsPropertyAnimator translationX(float translationX, Runnable... endActions) {
            this.mPathAnimator = null;
            return property(DynamicAnimation.TRANSLATION_X, translationX, endActions);
        }

        public PhysicsPropertyAnimator translationX(float from, float to, Runnable... endActions) {
            this.mInitialPropertyValues.put(DynamicAnimation.TRANSLATION_X, Float.valueOf(from));
            return translationX(to, endActions);
        }

        public PhysicsPropertyAnimator translationY(float translationY, Runnable... endActions) {
            this.mPathAnimator = null;
            return property(DynamicAnimation.TRANSLATION_Y, translationY, endActions);
        }

        public PhysicsPropertyAnimator translationY(float from, float to, Runnable... endActions) {
            this.mInitialPropertyValues.put(DynamicAnimation.TRANSLATION_Y, Float.valueOf(from));
            return translationY(to, endActions);
        }

        public PhysicsPropertyAnimator position(float translationX, float translationY, Runnable... endActions) {
            this.mPositionEndActions = endActions;
            translationX(translationX, new Runnable[0]);
            return translationY(translationY, new Runnable[0]);
        }

        public PhysicsPropertyAnimator followAnimatedTargetAlongPath(Path path, int targetAnimDuration, TimeInterpolator targetAnimInterpolator, Runnable... endActions) {
            this.mPathAnimator = ObjectAnimator.ofFloat(this, this.mCurrentPointOnPathXProperty, this.mCurrentPointOnPathYProperty, path);
            this.mPathAnimator.setDuration(targetAnimDuration);
            this.mPathAnimator.setInterpolator(targetAnimInterpolator);
            this.mPositionEndActions = endActions;
            clearTranslationValues();
            return this;
        }

        private void clearTranslationValues() {
            this.mAnimatedProperties.remove(DynamicAnimation.TRANSLATION_X);
            this.mAnimatedProperties.remove(DynamicAnimation.TRANSLATION_Y);
            this.mInitialPropertyValues.remove(DynamicAnimation.TRANSLATION_X);
            this.mInitialPropertyValues.remove(DynamicAnimation.TRANSLATION_Y);
            PhysicsAnimationLayout.this.mEndActionForProperty.remove(DynamicAnimation.TRANSLATION_X);
            PhysicsAnimationLayout.this.mEndActionForProperty.remove(DynamicAnimation.TRANSLATION_Y);
        }

        public PhysicsPropertyAnimator scaleX(float scaleX, Runnable... endActions) {
            return property(DynamicAnimation.SCALE_X, scaleX, endActions);
        }

        public PhysicsPropertyAnimator scaleX(float from, float to, Runnable... endActions) {
            this.mInitialPropertyValues.put(DynamicAnimation.SCALE_X, Float.valueOf(from));
            return scaleX(to, endActions);
        }

        public PhysicsPropertyAnimator scaleY(float scaleY, Runnable... endActions) {
            return property(DynamicAnimation.SCALE_Y, scaleY, endActions);
        }

        public PhysicsPropertyAnimator scaleY(float from, float to, Runnable... endActions) {
            this.mInitialPropertyValues.put(DynamicAnimation.SCALE_Y, Float.valueOf(from));
            return scaleY(to, endActions);
        }

        public PhysicsPropertyAnimator withStartVelocity(float startVel) {
            this.mDefaultStartVelocity = startVel;
            return this;
        }

        public PhysicsPropertyAnimator withDampingRatio(float dampingRatio) {
            this.mDampingRatio = dampingRatio;
            return this;
        }

        public PhysicsPropertyAnimator withStiffness(float stiffness) {
            this.mStiffness = stiffness;
            return this;
        }

        public PhysicsPropertyAnimator withPositionStartVelocities(float velX, float velY) {
            this.mPositionStartVelocities.put(DynamicAnimation.TRANSLATION_X, Float.valueOf(velX));
            this.mPositionStartVelocities.put(DynamicAnimation.TRANSLATION_Y, Float.valueOf(velY));
            return this;
        }

        public PhysicsPropertyAnimator withStartDelay(long startDelay) {
            this.mStartDelay = startDelay;
            return this;
        }

        public void start(final Runnable... after) {
            if (!PhysicsAnimationLayout.this.isActiveController(this.mAssociatedController)) {
                Log.w(PhysicsAnimationLayout.TAG, "Only the active animation controller is allowed to start animations. Use PhysicsAnimationLayout#setActiveController to set the active animation controller.");
                return;
            }
            Set<DynamicAnimation.ViewProperty> properties = getAnimatedProperties();
            if (after != null && after.length > 0) {
                DynamicAnimation.ViewProperty[] propertiesArray = (DynamicAnimation.ViewProperty[]) properties.toArray(new DynamicAnimation.ViewProperty[0]);
                this.mAssociatedController.setEndActionForMultipleProperties(new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$iuqdgR2C6CC4Qpac87e6S6WedyM
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhysicsAnimationLayout.PhysicsPropertyAnimator.lambda$start$0(after);
                    }
                }, propertiesArray);
            }
            if (this.mPositionEndActions != null) {
                final SpringAnimation translationXAnim = PhysicsAnimationLayout.this.getAnimationFromView(DynamicAnimation.TRANSLATION_X, this.mView);
                final SpringAnimation translationYAnim = PhysicsAnimationLayout.this.getAnimationFromView(DynamicAnimation.TRANSLATION_Y, this.mView);
                Runnable waitForBothXAndY = new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$3DhSPSm-kLIWL6PRkLpBmJ3MVps
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhysicsAnimationLayout.PhysicsPropertyAnimator.this.lambda$start$1$PhysicsAnimationLayout$PhysicsPropertyAnimator(translationXAnim, translationYAnim);
                    }
                };
                this.mEndActionsForProperty.put(DynamicAnimation.TRANSLATION_X, new Runnable[]{waitForBothXAndY});
                this.mEndActionsForProperty.put(DynamicAnimation.TRANSLATION_Y, new Runnable[]{waitForBothXAndY});
            }
            if (this.mPathAnimator != null) {
                startPathAnimation();
            }
            for (DynamicAnimation.ViewProperty property : properties) {
                if (this.mPathAnimator != null && (property.equals(DynamicAnimation.TRANSLATION_X) || property.equals(DynamicAnimation.TRANSLATION_Y))) {
                    return;
                }
                if (this.mInitialPropertyValues.containsKey(property)) {
                    property.setValue(this.mView, this.mInitialPropertyValues.get(property).floatValue());
                }
                SpringForce defaultSpringForce = PhysicsAnimationLayout.this.mController.getSpringForce(property, this.mView);
                View view = this.mView;
                float floatValue = this.mAnimatedProperties.get(property).floatValue();
                float floatValue2 = this.mPositionStartVelocities.getOrDefault(property, Float.valueOf(this.mDefaultStartVelocity)).floatValue();
                long j = this.mStartDelay;
                float f = this.mStiffness;
                if (f < 0.0f) {
                    f = defaultSpringForce.getStiffness();
                }
                float f2 = f;
                float f3 = this.mDampingRatio;
                if (f3 < 0.0f) {
                    f3 = defaultSpringForce.getDampingRatio();
                }
                animateValueForChild(property, view, floatValue, floatValue2, j, f2, f3, this.mEndActionsForProperty.get(property));
            }
            clearAnimator();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$start$0(Runnable[] after) {
            for (Runnable callback : after) {
                callback.run();
            }
        }

        public /* synthetic */ void lambda$start$1$PhysicsAnimationLayout$PhysicsPropertyAnimator(SpringAnimation translationXAnim, SpringAnimation translationYAnim) {
            if (!translationXAnim.isRunning() && !translationYAnim.isRunning()) {
                Runnable[] runnableArr = this.mPositionEndActions;
                if (runnableArr != null) {
                    for (Runnable callback : runnableArr) {
                        callback.run();
                    }
                }
                this.mPositionEndActions = null;
            }
        }

        protected Set<DynamicAnimation.ViewProperty> getAnimatedProperties() {
            HashSet<DynamicAnimation.ViewProperty> animatedProperties = new HashSet<>(this.mAnimatedProperties.keySet());
            if (this.mPathAnimator != null) {
                animatedProperties.add(DynamicAnimation.TRANSLATION_X);
                animatedProperties.add(DynamicAnimation.TRANSLATION_Y);
            }
            return animatedProperties;
        }

        protected void animateValueForChild(DynamicAnimation.ViewProperty property, View view, final float value, final float startVel, long startDelay, final float stiffness, final float dampingRatio, final Runnable... afterCallbacks) {
            if (view != null) {
                final SpringAnimation animation = (SpringAnimation) view.getTag(PhysicsAnimationLayout.this.getTagIdForProperty(property));
                if (afterCallbacks != null) {
                    animation.addEndListener(new OneTimeEndListener() { // from class: com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsPropertyAnimator.3
                        @Override // com.android.systemui.bubbles.animation.OneTimeEndListener, androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                        public void onAnimationEnd(DynamicAnimation animation2, boolean canceled, float value2, float velocity) {
                            Runnable[] runnableArr;
                            super.onAnimationEnd(animation2, canceled, value2, velocity);
                            for (Runnable runnable : afterCallbacks) {
                                runnable.run();
                            }
                        }
                    });
                }
                final SpringForce animationSpring = animation.getSpring();
                if (animationSpring == null) {
                    return;
                }
                Runnable configureAndStartAnimation = new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$YrUNYDpshnd98P1tIxCkdc37pTc
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhysicsAnimationLayout.PhysicsPropertyAnimator.lambda$animateValueForChild$2(SpringForce.this, stiffness, dampingRatio, startVel, animation, value);
                    }
                };
                if (startDelay > 0) {
                    PhysicsAnimationLayout.this.postDelayed(configureAndStartAnimation, startDelay);
                } else {
                    configureAndStartAnimation.run();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$animateValueForChild$2(SpringForce animationSpring, float stiffness, float dampingRatio, float startVel, SpringAnimation animation, float value) {
            animationSpring.setStiffness(stiffness);
            animationSpring.setDampingRatio(dampingRatio);
            if (startVel > -3.4028235E38f) {
                animation.setStartVelocity(startVel);
            }
            animationSpring.setFinalPosition(value);
            animation.start();
        }

        private void updateValueForChild(DynamicAnimation.ViewProperty property, View view, float position) {
            SpringAnimation animation;
            SpringForce animationSpring;
            if (view == null || (animationSpring = (animation = (SpringAnimation) view.getTag(PhysicsAnimationLayout.this.getTagIdForProperty(property))).getSpring()) == null) {
                return;
            }
            animationSpring.setFinalPosition(position);
            animation.start();
        }

        protected void startPathAnimation() {
            final SpringForce defaultSpringForceX = PhysicsAnimationLayout.this.mController.getSpringForce(DynamicAnimation.TRANSLATION_X, this.mView);
            final SpringForce defaultSpringForceY = PhysicsAnimationLayout.this.mController.getSpringForce(DynamicAnimation.TRANSLATION_Y, this.mView);
            long j = this.mStartDelay;
            if (j > 0) {
                this.mPathAnimator.setStartDelay(j);
            }
            final Runnable updatePhysicsAnims = new Runnable() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$VvmQYTYF92KoaeTMVxzFjdA4FFA
                @Override // java.lang.Runnable
                public final void run() {
                    PhysicsAnimationLayout.PhysicsPropertyAnimator.this.lambda$startPathAnimation$3$PhysicsAnimationLayout$PhysicsPropertyAnimator();
                }
            };
            this.mPathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.bubbles.animation.-$$Lambda$PhysicsAnimationLayout$PhysicsPropertyAnimator$1Xv4slF4ncwrmkshsfcHipCSgjk
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    updatePhysicsAnims.run();
                }
            });
            this.mPathAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.bubbles.animation.PhysicsAnimationLayout.PhysicsPropertyAnimator.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    float stiffness;
                    float dampingRatio;
                    float stiffness2;
                    float dampingRatio2;
                    PhysicsPropertyAnimator physicsPropertyAnimator = PhysicsPropertyAnimator.this;
                    DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.TRANSLATION_X;
                    View view = PhysicsPropertyAnimator.this.mView;
                    float f = PhysicsPropertyAnimator.this.mCurrentPointOnPath.x;
                    float f2 = PhysicsPropertyAnimator.this.mDefaultStartVelocity;
                    if (PhysicsPropertyAnimator.this.mStiffness >= 0.0f) {
                        stiffness = PhysicsPropertyAnimator.this.mStiffness;
                    } else {
                        stiffness = defaultSpringForceX.getStiffness();
                    }
                    float f3 = stiffness;
                    if (PhysicsPropertyAnimator.this.mDampingRatio >= 0.0f) {
                        dampingRatio = PhysicsPropertyAnimator.this.mDampingRatio;
                    } else {
                        dampingRatio = defaultSpringForceX.getDampingRatio();
                    }
                    physicsPropertyAnimator.animateValueForChild(viewProperty, view, f, f2, 0L, f3, dampingRatio, new Runnable[0]);
                    PhysicsPropertyAnimator physicsPropertyAnimator2 = PhysicsPropertyAnimator.this;
                    DynamicAnimation.ViewProperty viewProperty2 = DynamicAnimation.TRANSLATION_Y;
                    View view2 = PhysicsPropertyAnimator.this.mView;
                    float f4 = PhysicsPropertyAnimator.this.mCurrentPointOnPath.y;
                    float f5 = PhysicsPropertyAnimator.this.mDefaultStartVelocity;
                    if (PhysicsPropertyAnimator.this.mStiffness >= 0.0f) {
                        stiffness2 = PhysicsPropertyAnimator.this.mStiffness;
                    } else {
                        stiffness2 = defaultSpringForceY.getStiffness();
                    }
                    float f6 = stiffness2;
                    if (PhysicsPropertyAnimator.this.mDampingRatio >= 0.0f) {
                        dampingRatio2 = PhysicsPropertyAnimator.this.mDampingRatio;
                    } else {
                        dampingRatio2 = defaultSpringForceY.getDampingRatio();
                    }
                    physicsPropertyAnimator2.animateValueForChild(viewProperty2, view2, f4, f5, 0L, f6, dampingRatio2, new Runnable[0]);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    updatePhysicsAnims.run();
                }
            });
            ObjectAnimator targetAnimator = PhysicsAnimationLayout.this.getTargetAnimatorFromView(this.mView);
            if (targetAnimator != null) {
                targetAnimator.cancel();
            }
            this.mView.setTag(R.id.target_animator_tag, this.mPathAnimator);
            this.mPathAnimator.start();
        }

        public /* synthetic */ void lambda$startPathAnimation$3$PhysicsAnimationLayout$PhysicsPropertyAnimator() {
            updateValueForChild(DynamicAnimation.TRANSLATION_X, this.mView, this.mCurrentPointOnPath.x);
            updateValueForChild(DynamicAnimation.TRANSLATION_Y, this.mView, this.mCurrentPointOnPath.y);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearAnimator() {
            this.mInitialPropertyValues.clear();
            this.mAnimatedProperties.clear();
            this.mPositionStartVelocities.clear();
            this.mDefaultStartVelocity = -3.4028235E38f;
            this.mStartDelay = 0L;
            this.mStiffness = -1.0f;
            this.mDampingRatio = -1.0f;
            this.mEndActionsForProperty.clear();
            this.mPathAnimator = null;
            this.mPositionEndActions = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAssociatedController(PhysicsAnimationController controller) {
            this.mAssociatedController = controller;
        }
    }

    protected boolean canReceivePointerEvents() {
        return false;
    }
}
