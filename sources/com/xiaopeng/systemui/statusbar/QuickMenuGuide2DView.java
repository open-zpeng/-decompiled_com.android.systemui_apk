package com.xiaopeng.systemui.statusbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.helper.AnimationHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
/* loaded from: classes24.dex */
public class QuickMenuGuide2DView extends BaseQuickMenuGuideView implements View.OnAttachStateChangeListener {
    private static final int ANIM_ENTER_TIME = 272;
    private static final int ANIM_QUIT_TIME = 680;
    private static final int ANIM_RUN_TIME = 30000;
    private static final int ANIM_STATUS_ENTER = 0;
    private static final int ANIM_STATUS_GUIDE = 1;
    private static final int ANIM_STATUS_INVALID = -1;
    private static final int ANIM_STATUS_QUIT = 2;
    private static final int MSG_QUIT_GUIDE = 2;
    private static final int MSG_REMOVE_WINDOW = 3;
    private static final int MSG_START_ENTER_ANIM = 0;
    private static final int MSG_START_GUIDE_ANIM = 1;
    private static final String TAG = "QuickMenuGuide2DView";
    private ValueAnimator mFadeOutValueAnimator;
    private AnimatedImageView mIvGuide;
    private View mQuickMenuGuideLayout;
    private int mAnimStatus = -1;
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.statusbar.QuickMenuGuide2DView.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 0) {
                QuickMenuGuide2DView.this.startEnterAnim();
            } else if (i == 1) {
                QuickMenuGuide2DView.this.startQuickMenuGuide();
            } else if (i == 2) {
                QuickMenuGuide2DView.this.quitQuickMenuGuide();
            } else if (i == 3) {
                QuickMenuGuide2DView.this.removeWindow();
            }
        }
    };
    private Context mContext = ContextUtils.getContext();
    private WindowManager mWindowManager = StatusBarGlobal.getInstance(this.mContext).getWindowManager();

    @Override // com.xiaopeng.systemui.statusbar.BaseQuickMenuGuideView, com.xiaopeng.systemui.statusbar.IQuickMenuGuideView
    public void enterQuickMenuGuide() {
        if (this.mQuickMenuGuideLayout == null) {
            this.mQuickMenuGuideLayout = WindowHelper.addQuickMenuGuide(this.mContext, this.mWindowManager);
            this.mQuickMenuGuideLayout.addOnAttachStateChangeListener(this);
        }
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseQuickMenuGuideView, com.xiaopeng.systemui.statusbar.IQuickMenuGuideView
    public void quitQuickMenuGuide() {
        startQuitAnimation();
    }

    private void destroyQuickMenuGuide() {
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.sendEmptyMessageDelayed(3, 680L);
    }

    @Override // com.xiaopeng.systemui.statusbar.BaseQuickMenuGuideView, com.xiaopeng.systemui.IView
    public void onThemeChanged() {
        int i = this.mAnimStatus;
        if (i == 0) {
            enterQuickMenuGuide();
        } else if (i == 1) {
            startQuickMenuGuide();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startEnterAnim() {
        Logger.d(TAG, "startEnterAnim");
        this.mAnimStatus = 0;
        AnimationHelper.destroyAnim(this.mIvGuide);
        AnimationHelper.startAnim(this.mIvGuide, R.drawable.anim_quick_menu_guide_start);
        this.mHandler.sendEmptyMessageDelayed(1, 272L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startQuickMenuGuide() {
        Logger.d(TAG, "startQuickMenuGuide");
        this.mAnimStatus = 1;
        AnimationHelper.destroyAnim(this.mIvGuide);
        this.mHandler.removeCallbacksAndMessages(null);
        AnimationHelper.startAnimInfinite(this.mIvGuide, R.drawable.anim_quick_menu_guide);
        this.mHandler.sendEmptyMessageDelayed(2, 30000L);
    }

    private void startQuitAnimation() {
        Logger.d(TAG, "startQuitAnimation");
        this.mAnimStatus = 2;
        this.mHandler.removeCallbacksAndMessages(null);
        startFadeOutAnimation();
    }

    private void startFadeOutAnimation() {
        Logger.d(TAG, "startFadeOutAnimation");
        this.mAnimStatus = -1;
        if (this.mFadeOutValueAnimator == null) {
            this.mFadeOutValueAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.mFadeOutValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.statusbar.-$$Lambda$QuickMenuGuide2DView$YUctR94Y7hfi__v-80WEaarHvN4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    QuickMenuGuide2DView.this.lambda$startFadeOutAnimation$0$QuickMenuGuide2DView(valueAnimator);
                }
            });
            this.mFadeOutValueAnimator.setDuration(680L);
        }
        this.mFadeOutValueAnimator.start();
        destroyQuickMenuGuide();
    }

    public /* synthetic */ void lambda$startFadeOutAnimation$0$QuickMenuGuide2DView(ValueAnimator valueAnimator) {
        float alpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        AnimatedImageView animatedImageView = this.mIvGuide;
        if (animatedImageView != null) {
            animatedImageView.setAlpha(alpha);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeWindow() {
        View view = this.mQuickMenuGuideLayout;
        if (view != null && view.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mQuickMenuGuideLayout);
            this.mQuickMenuGuideLayout = null;
        }
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewAttachedToWindow(View v) {
        this.mIvGuide = (AnimatedImageView) this.mQuickMenuGuideLayout.findViewById(R.id.iv_guide);
        this.mHandler.sendEmptyMessage(0);
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewDetachedFromWindow(View v) {
    }
}
