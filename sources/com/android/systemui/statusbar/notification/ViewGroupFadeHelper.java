package com.android.systemui.statusbar.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ViewGroupFadeHelper.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\u0018\u0000 \u00032\u00020\u0001:\u0001\u0003B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0004"}, d2 = {"Lcom/android/systemui/statusbar/notification/ViewGroupFadeHelper;", "", "()V", "Companion", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class ViewGroupFadeHelper {
    public static final Companion Companion = new Companion(null);
    private static final Function1<View, Boolean> visibilityIncluder = new Function1<View, Boolean>() { // from class: com.android.systemui.statusbar.notification.ViewGroupFadeHelper$Companion$visibilityIncluder$1
        @Override // kotlin.jvm.functions.Function1
        public /* bridge */ /* synthetic */ Boolean invoke(View view) {
            return Boolean.valueOf(invoke2(view));
        }

        /* renamed from: invoke  reason: avoid collision after fix types in other method */
        public final boolean invoke2(@NotNull View view) {
            Intrinsics.checkParameterIsNotNull(view, "view");
            return view.getVisibility() == 0;
        }
    };

    @JvmStatic
    public static final void fadeOutAllChildrenExcept(@NotNull ViewGroup viewGroup, @NotNull View view, long j, @Nullable Runnable runnable) {
        Companion.fadeOutAllChildrenExcept(viewGroup, view, j, runnable);
    }

    @JvmStatic
    public static final void reset(@NotNull ViewGroup viewGroup) {
        Companion.reset(viewGroup);
    }

    /* compiled from: ViewGroupFadeHelper.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J*\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0007J2\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u00112\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00052\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004H\u0002J\u0010\u0010\u0013\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Lcom/android/systemui/statusbar/notification/ViewGroupFadeHelper$Companion;", "", "()V", "visibilityIncluder", "Lkotlin/Function1;", "Landroid/view/View;", "", "fadeOutAllChildrenExcept", "", "root", "Landroid/view/ViewGroup;", "excludedView", "duration", "", "endRunnable", "Ljava/lang/Runnable;", "gatherViews", "", "shouldInclude", "reset", "name"}, k = 1, mv = {1, 1, 13})
    /* loaded from: classes21.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        @JvmStatic
        public final void fadeOutAllChildrenExcept(@NotNull final ViewGroup root, @NotNull View excludedView, final long duration, @Nullable final Runnable endRunnable) {
            Intrinsics.checkParameterIsNotNull(root, "root");
            Intrinsics.checkParameterIsNotNull(excludedView, "excludedView");
            final Set viewsToFadeOut = gatherViews(root, excludedView, ViewGroupFadeHelper.visibilityIncluder);
            for (View viewToFade : viewsToFadeOut) {
                if (viewToFade.getHasOverlappingRendering() && viewToFade.getLayerType() == 0) {
                    viewToFade.setLayerType(2, null);
                    viewToFade.setTag(R.id.view_group_fade_helper_hardware_layer, true);
                }
            }
            ValueAnimator $receiver = ValueAnimator.ofFloat(1.0f, 0.0f);
            Intrinsics.checkExpressionValueIsNotNull($receiver, "this");
            $receiver.setDuration(duration);
            $receiver.setInterpolator(Interpolators.ALPHA_OUT);
            $receiver.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.ViewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$$inlined$apply$lambda$1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator animation) {
                    Float previousSetAlpha = (Float) root.getTag(R.id.view_group_fade_helper_previous_value_tag);
                    Intrinsics.checkExpressionValueIsNotNull(animation, "animation");
                    Object animatedValue = animation.getAnimatedValue();
                    if (animatedValue == null) {
                        throw new TypeCastException("null cannot be cast to non-null type kotlin.Float");
                    }
                    float newAlpha = ((Float) animatedValue).floatValue();
                    for (View viewToFade2 : viewsToFadeOut) {
                        if (!Intrinsics.areEqual(viewToFade2.getAlpha(), previousSetAlpha)) {
                            viewToFade2.setTag(R.id.view_group_fade_helper_restore_tag, Float.valueOf(viewToFade2.getAlpha()));
                        }
                        viewToFade2.setAlpha(newAlpha);
                    }
                    root.setTag(R.id.view_group_fade_helper_previous_value_tag, Float.valueOf(newAlpha));
                }
            });
            $receiver.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.ViewGroupFadeHelper$Companion$fadeOutAllChildrenExcept$$inlined$apply$lambda$2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(@Nullable Animator animation) {
                    Runnable runnable = endRunnable;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            $receiver.start();
            root.setTag(R.id.view_group_fade_helper_modified_views, viewsToFadeOut);
            root.setTag(R.id.view_group_fade_helper_animator, $receiver);
        }

        private final Set<View> gatherViews(ViewGroup root, View excludedView, Function1<? super View, Boolean> function1) {
            Set viewsToFadeOut = new LinkedHashSet();
            View viewContainingExcludedView = excludedView;
            for (ViewGroup parent = (ViewGroup) excludedView.getParent(); parent != null; parent = (ViewGroup) parent.getParent()) {
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    Intrinsics.checkExpressionValueIsNotNull(child, "child");
                    if (function1.invoke(child).booleanValue() && (!Intrinsics.areEqual(viewContainingExcludedView, child))) {
                        viewsToFadeOut.add(child);
                    }
                }
                if (Intrinsics.areEqual(parent, root)) {
                    break;
                }
                View viewContainingExcludedView2 = parent;
                viewContainingExcludedView = viewContainingExcludedView2;
            }
            return viewsToFadeOut;
        }

        @JvmStatic
        public final void reset(@NotNull ViewGroup root) {
            Intrinsics.checkParameterIsNotNull(root, "root");
            Set<View> modifiedViews = TypeIntrinsics.asMutableSet(root.getTag(R.id.view_group_fade_helper_modified_views));
            Animator animator = (Animator) root.getTag(R.id.view_group_fade_helper_animator);
            if (modifiedViews == null || animator == null) {
                return;
            }
            animator.cancel();
            Float lastSetValue = (Float) root.getTag(R.id.view_group_fade_helper_previous_value_tag);
            for (View viewToFade : modifiedViews) {
                Float restoreAlpha = (Float) viewToFade.getTag(R.id.view_group_fade_helper_restore_tag);
                if (restoreAlpha != null) {
                    if (Intrinsics.areEqual(lastSetValue, viewToFade.getAlpha())) {
                        viewToFade.setAlpha(restoreAlpha.floatValue());
                    }
                    Boolean needsLayerReset = (Boolean) viewToFade.getTag(R.id.view_group_fade_helper_hardware_layer);
                    if (Intrinsics.areEqual((Object) needsLayerReset, (Object) true)) {
                        viewToFade.setLayerType(0, null);
                        viewToFade.setTag(R.id.view_group_fade_helper_hardware_layer, null);
                    }
                    viewToFade.setTag(R.id.view_group_fade_helper_restore_tag, null);
                }
            }
            root.setTag(R.id.view_group_fade_helper_modified_views, null);
            root.setTag(R.id.view_group_fade_helper_previous_value_tag, null);
            root.setTag(R.id.view_group_fade_helper_animator, null);
        }
    }
}
