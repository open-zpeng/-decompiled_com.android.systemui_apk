package com.xiaopeng.systemui.infoflow.message.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.systemui.infoflow.dao.InfoFlowConfigDao;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class LandItemAnimator extends BaseItemAnimator {
    private static final String TAG = "LandItemAnimator";

    @Override // com.xiaopeng.systemui.infoflow.message.anim.BaseItemAnimator
    public void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        this.mRemoveAnimations.add(holder);
        animation.setDuration(getAnimationDuration()).alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.infoflow.message.anim.LandItemAnimator.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                LandItemAnimator.this.dispatchRemoveStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                view.setAlpha(1.0f);
                LandItemAnimator.this.dispatchRemoveFinished(holder);
                LandItemAnimator.this.mRemoveAnimations.remove(holder);
                LandItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.anim.BaseItemAnimator
    public void animateAddImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        view.setScaleX(0.84f);
        view.setScaleY(0.84f);
        view.setAlpha(0.0f);
        this.mAddAnimations.add(holder);
        animation.alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(getAnimationDuration()).setListener(new AnimatorListenerAdapter() { // from class: com.xiaopeng.systemui.infoflow.message.anim.LandItemAnimator.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                LandItemAnimator.this.dispatchAddStarting(holder);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                view.setAlpha(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                view.setAlpha(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                animation.setListener(null);
                LandItemAnimator.this.dispatchAddFinished(holder);
                LandItemAnimator.this.mAddAnimations.remove(holder);
                LandItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    private long getAnimationDuration() {
        long animationDuration = InfoFlowConfigDao.getInstance().getConfig().cardAnimationDuration;
        Logger.d(TAG, "animationDuration--" + animationDuration);
        return animationDuration;
    }
}
