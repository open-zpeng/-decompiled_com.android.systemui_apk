package com.xiaopeng.systemui.speech.model;

import android.view.View;
import android.view.animation.Animation;
/* loaded from: classes24.dex */
public class AnimationListenerExUtils {
    public static void start(View view, Animation animation, final Runnable runnable) {
        animation.setAnimationListener(new Animation.AnimationListener() { // from class: com.xiaopeng.systemui.speech.model.AnimationListenerExUtils.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation2) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation2) {
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation2) {
            }
        });
        view.startAnimation(animation);
    }
}
