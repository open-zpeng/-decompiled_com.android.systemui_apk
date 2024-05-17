package com.xiaopeng.systemui.helper;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
/* loaded from: classes24.dex */
public class ThemeHelper {
    private static final String TAG = "ThemeHelper";
    private static final int TRANSITION_TIMEOUT = 500;

    public static void setCompoundDrawables(TextView view, Drawable drawable, int index) {
        if (view != null && index >= 0 && index <= 3) {
            Drawable[] drawables = view.getCompoundDrawables();
            if (drawables != null && drawables.length == 4) {
                if (drawable != null) {
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                }
                drawables[index] = drawable;
            }
            view.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
    }

    public static void setCompoundDrawables(TextView view, Drawable drawable, int index, int level) {
        if (view != null && index >= 0 && index <= 3) {
            Drawable[] drawables = view.getCompoundDrawables();
            if (drawables != null && drawables.length == 4) {
                if (drawable != null) {
                    drawable.setLevel(level);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                }
                drawables[index] = drawable;
            }
            view.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
    }

    public static void setActivated(ViewGroup root, int state) {
        if (root != null) {
            int childCount = root.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = root.getChildAt(i);
                if (child != null) {
                    if (child instanceof ViewGroup) {
                        setActivated((ViewGroup) child, state);
                    } else {
                        setActivated((ViewGroup) child, state);
                    }
                }
            }
        }
    }

    private static void startTextColorAnimation(final TextView view, final int startColor, final int endColor) {
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.systemui.helper.ThemeHelper.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (view != null) {
                        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(startColor), Integer.valueOf(endColor));
                        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.helper.ThemeHelper.1.1
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int color = ((Integer) animation.getAnimatedValue()).intValue();
                                view.setTextColor(color);
                            }
                        });
                        colorAnimator.setDuration(500L);
                        colorAnimator.start();
                    }
                } catch (Exception e) {
                }
            }
        };
        ThreadHelper.runOnMainThread(runnable);
    }

    private static void startImageDrawableTransition(final ImageView view, final Drawable startDrawable, final Drawable endDrawable) {
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.systemui.helper.ThemeHelper.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (view != null && startDrawable != null && endDrawable != null) {
                        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{startDrawable, endDrawable});
                        view.setImageDrawable(transitionDrawable);
                        transitionDrawable.startTransition(500);
                        view.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.helper.ThemeHelper.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                view.setImageDrawable(endDrawable);
                            }
                        }, 500L);
                    }
                } catch (Exception e) {
                }
            }
        };
        ThreadHelper.runOnMainThread(runnable);
    }

    private static void startViewBackgroundTransition(final View view, final Drawable startDrawable, final Drawable endDrawable) {
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.systemui.helper.ThemeHelper.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (view != null && startDrawable != null && endDrawable != null) {
                        int startLevel = startDrawable.getLevel();
                        int endLevel = endDrawable.getLevel();
                        if (startLevel != endLevel) {
                            endDrawable.setLevel(startLevel);
                        }
                        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{startDrawable, endDrawable});
                        view.setBackground(transitionDrawable);
                        transitionDrawable.startTransition(500);
                        view.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.helper.ThemeHelper.3.1
                            @Override // java.lang.Runnable
                            public void run() {
                                view.setBackground(endDrawable);
                            }
                        }, 500L);
                    }
                } catch (Exception e) {
                }
            }
        };
        ThreadHelper.runOnMainThread(runnable);
    }
}
