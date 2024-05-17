package com.xiaopeng.systemui.infoflow.helper;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.rastermill.FrameSequenceDrawable;
import android.support.rastermill.FrameSequenceUtil;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class AnimationHelper {
    private static final String TAG = "AnimationHelper";
    private Animation mCarInAnimation;
    private Animation mCarOutAnimation;
    private Context mContext;

    /* loaded from: classes24.dex */
    public interface CardAnimationListener {
        void onAnimationEnd();
    }

    public AnimationHelper(Context context) {
        this.mContext = context;
        this.mCarOutAnimation = AnimationUtils.loadAnimation(this.mContext, R.anim.card_out);
        this.mCarOutAnimation.setInterpolator(new XBesselCurve3Interpolator(0.34f, 0.0f, 0.66f, 1.0f));
        this.mCarInAnimation = AnimationUtils.loadAnimation(context, R.anim.card_in);
        this.mCarInAnimation.setInterpolator(new XBesselCurve3Interpolator(0.2f, 0.0f, 0.0f, 1.0f));
    }

    public void hideCard(View view, CardAnimationListener animationListener) {
        hideCard(view, animationListener, true);
    }

    public void hideCard(final View view, final CardAnimationListener animationListener, final boolean autoHideViewOnEnd) {
        Logger.d(TAG, "hideCard() called with: view = [" + view + "], animationListener = [" + animationListener + NavigationBarInflaterView.SIZE_MOD_END);
        this.mCarOutAnimation.setAnimationListener(null);
        this.mCarOutAnimation.cancel();
        if (view.getVisibility() != 0) {
            if (animationListener != null) {
                animationListener.onAnimationEnd();
                return;
            }
            return;
        }
        Logger.d(TAG, "hideCard()");
        view.startAnimation(this.mCarOutAnimation);
        if (animationListener != null) {
            this.mCarOutAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.xiaopeng.systemui.infoflow.helper.AnimationHelper.1
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    Logger.d(AnimationHelper.TAG, "hideCard - onAnimationEnd view = [" + view + "] set gone when animation end");
                    animation.setAnimationListener(null);
                    animationListener.onAnimationEnd();
                    view.clearAnimation();
                    if (autoHideViewOnEnd) {
                        view.setVisibility(8);
                    }
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    public void showCard(final View view) {
        Logger.d(TAG, "showCard() called with: view = [" + view + "] " + view.getVisibility());
        this.mCarOutAnimation.setAnimationListener(null);
        this.mCarInAnimation.cancel();
        view.setVisibility(0);
        view.startAnimation(this.mCarInAnimation);
        this.mCarInAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.xiaopeng.systemui.infoflow.helper.AnimationHelper.2
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                animation.setAnimationListener(null);
                view.clearAnimation();
                Logger.d(AnimationHelper.TAG, "showCard - onAnimationEnd : view = " + view + " visible = " + view.getVisibility());
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void destroyAnimation() {
        Logger.d(TAG, "destroyAnimation");
        Animation animation = this.mCarInAnimation;
        if (animation != null) {
            animation.cancel();
        }
        Animation animation2 = this.mCarOutAnimation;
        if (animation2 != null) {
            animation2.cancel();
        }
    }

    public static void startAnim(ImageView view, int resourceId) {
        FrameSequenceUtil.with(view).resourceId(resourceId).applyAsync();
    }

    public static void startAnimOnce(ImageView view, int resourceId, int finishId, FrameSequenceDrawable.OnFinishedListener finishedListener) {
        FrameSequenceUtil.with(view).resourceId(resourceId).finish(finishId).onFinishedListener(finishedListener).loopBehavior(1).loopCount(1).applyAsync();
    }

    public static void startAnimInfinite(ImageView view, int resourceId) {
        if (view != null) {
            FrameSequenceUtil.with(view).resourceId(resourceId).loopBehavior(2).applyAsync();
        }
    }

    public static AnimationDrawable startAnimationDrawableAnim(Context context, ImageView view, int resourceId) {
        if (view != null) {
            Drawable drawable = view.getDrawable();
            if (drawable instanceof AnimationDrawable) {
                ((AnimationDrawable) drawable).stop();
            }
            view.setImageResource(resourceId);
            view.setImageDrawable(context.getDrawable(resourceId));
            AnimationDrawable animationDrawable = (AnimationDrawable) view.getDrawable();
            animationDrawable.start();
            return animationDrawable;
        }
        return null;
    }

    public static void startAnimInfiniteWithoutDestroy(ImageView view, int resourceId) {
        if (view != null) {
            FrameSequenceUtil.with(view).resourceId(resourceId).loopBehavior(2).apply();
        }
    }

    public static void destroyAnim(ImageView view) {
        Logger.d(TAG, "destroyAnim");
        if (view != null) {
            FrameSequenceUtil.destroy(view);
        }
    }

    public static void pauseAnim(ImageView view) {
        if (view != null) {
            FrameSequenceUtil.stop(view);
        }
    }

    public static void resumeAnim(ImageView view) {
        if (view != null) {
            FrameSequenceUtil.start(view);
        }
    }

    public static void startScaleAnimation(View view, float fromXScale, float toXScale, float fromYScale, float toYScale, int duration, Interpolator interpolator, Animation.AnimationListener animationListener) {
        view.clearAnimation();
        Animation scaleAnimation = new ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale, 1, 0.5f, 1, 1.0f);
        scaleAnimation.setDuration(duration);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(interpolator);
        scaleAnimation.setRepeatCount(0);
        if (animationListener != null) {
            scaleAnimation.setAnimationListener(animationListener);
        }
        view.startAnimation(scaleAnimation);
    }

    public static void startAlphaAnimation(View view, float fromAlpha, float toAlpha, int duration, Interpolator interpolator, Animation.AnimationListener animationListener) {
        view.clearAnimation();
        Animation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setInterpolator(interpolator);
        alphaAnimation.setRepeatCount(0);
        if (animationListener != null) {
            alphaAnimation.setAnimationListener(animationListener);
        }
        view.startAnimation(alphaAnimation);
    }

    public static void stopAnimationDrawable(AnimationDrawable animationDrawable) {
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }
}
