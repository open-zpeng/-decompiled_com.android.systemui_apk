package com.xiaopeng.systemui.infoflow.message.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.RecyclerView;
/* loaded from: classes24.dex */
public class PortItemAnimator extends BaseItemAnimator {
    @Override // com.xiaopeng.systemui.infoflow.message.anim.BaseItemAnimator
    public void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        this.mRemoveAnimations.add(holder);
        animation.setDuration(getRemoveDuration()).alpha(0.0f).scaleX(0.2f).scaleY(0.2f).setListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.infoflow.message.anim.PortItemAnimator.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                PortItemAnimator.this.dispatchRemoveStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                view.setAlpha(1.0f);
                PortItemAnimator.this.dispatchRemoveFinished(holder);
                PortItemAnimator.this.mRemoveAnimations.remove(holder);
                PortItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.anim.BaseItemAnimator
    public void animateAddImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        view.setTranslationY(-view.getMeasuredHeight());
        view.setAlpha(0.0f);
        this.mAddAnimations.add(holder);
        animation.alpha(1.0f).translationY(0.0f).setDuration(getAddDuration()).setListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.infoflow.message.anim.PortItemAnimator.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                PortItemAnimator.this.dispatchAddStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                view.setAlpha(1.0f);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                PortItemAnimator.this.dispatchAddFinished(holder);
                PortItemAnimator.this.mAddAnimations.remove(holder);
                PortItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }
}
