package com.android.systemui.statusbar.notification;

import android.util.FloatProperty;
import android.util.Property;
import android.view.View;
import com.android.systemui.R;
import java.util.function.BiConsumer;
import java.util.function.Function;
/* loaded from: classes21.dex */
public abstract class AnimatableProperty {
    public static final AnimatableProperty X = from(View.X, R.id.x_animator_tag, R.id.x_animator_tag_start_value, R.id.x_animator_tag_end_value);
    public static final AnimatableProperty Y = from(View.Y, R.id.y_animator_tag, R.id.y_animator_tag_start_value, R.id.y_animator_tag_end_value);

    public abstract int getAnimationEndTag();

    public abstract int getAnimationStartTag();

    public abstract int getAnimatorTag();

    public abstract Property getProperty();

    public static <T extends View> AnimatableProperty from(String name, final BiConsumer<T, Float> setter, final Function<T, Float> getter, final int animatorTag, final int startValueTag, final int endValueTag) {
        final Property<T, Float> property = new FloatProperty<T>(name) { // from class: com.android.systemui.statusbar.notification.AnimatableProperty.1
            /* JADX WARN: Incorrect types in method signature: (TT;)Ljava/lang/Float; */
            @Override // android.util.Property
            public Float get(View view) {
                return (Float) getter.apply(view);
            }

            /* JADX WARN: Incorrect types in method signature: (TT;F)V */
            @Override // android.util.FloatProperty
            public void setValue(View view, float value) {
                setter.accept(view, Float.valueOf(value));
            }
        };
        return new AnimatableProperty() { // from class: com.android.systemui.statusbar.notification.AnimatableProperty.2
            @Override // com.android.systemui.statusbar.notification.AnimatableProperty
            public int getAnimationStartTag() {
                return startValueTag;
            }

            @Override // com.android.systemui.statusbar.notification.AnimatableProperty
            public int getAnimationEndTag() {
                return endValueTag;
            }

            @Override // com.android.systemui.statusbar.notification.AnimatableProperty
            public int getAnimatorTag() {
                return animatorTag;
            }

            @Override // com.android.systemui.statusbar.notification.AnimatableProperty
            public Property getProperty() {
                return property;
            }
        };
    }

    public static <T extends View> AnimatableProperty from(final Property<T, Float> property, final int animatorTag, final int startValueTag, final int endValueTag) {
        return new AnimatableProperty() { // from class: com.android.systemui.statusbar.notification.AnimatableProperty.3
            @Override // com.android.systemui.statusbar.notification.AnimatableProperty
            public int getAnimationStartTag() {
                return startValueTag;
            }

            @Override // com.android.systemui.statusbar.notification.AnimatableProperty
            public int getAnimationEndTag() {
                return endValueTag;
            }

            @Override // com.android.systemui.statusbar.notification.AnimatableProperty
            public int getAnimatorTag() {
                return animatorTag;
            }

            @Override // com.android.systemui.statusbar.notification.AnimatableProperty
            public Property getProperty() {
                return property;
            }
        };
    }
}
