package com.android.systemui.assist.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.metrics.LogMaker;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.assist.AssistManager;
import com.xiaopeng.systemui.helper.WindowHelper;
/* loaded from: classes21.dex */
public class DefaultUiController implements AssistManager.UiController {
    private static final long ANIM_DURATION_MS = 200;
    private static final String TAG = "DefaultUiController";
    protected InvocationLightsView mInvocationLightsView;
    protected final FrameLayout mRoot;
    private final WindowManager mWindowManager;
    private final PathInterpolator mProgressInterpolator = new PathInterpolator(0.83f, 0.0f, 0.84f, 1.0f);
    private boolean mAttached = false;
    private boolean mInvocationInProgress = false;
    private float mLastInvocationProgress = 0.0f;
    private ValueAnimator mInvocationAnimator = new ValueAnimator();
    private final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(-1, -2, 0, 0, WindowHelper.TYPE_NAVIGATION_BAR_PANEL, 808, -3);

    public DefaultUiController(Context context) {
        this.mRoot = new FrameLayout(context);
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        WindowManager.LayoutParams layoutParams = this.mLayoutParams;
        layoutParams.privateFlags = 64;
        layoutParams.gravity = 80;
        layoutParams.setTitle("Assist");
        this.mInvocationLightsView = (InvocationLightsView) LayoutInflater.from(context).inflate(R.layout.invocation_lights, (ViewGroup) this.mRoot, false);
        this.mRoot.addView(this.mInvocationLightsView);
    }

    @Override // com.android.systemui.assist.AssistManager.UiController
    public void processBundle(Bundle bundle) {
        Log.e(TAG, "Bundle received but handling is not implemented; ignoring");
    }

    @Override // com.android.systemui.assist.AssistManager.UiController
    public void onInvocationProgress(int type, float progress) {
        boolean invocationWasInProgress = this.mInvocationInProgress;
        if (progress == 1.0f) {
            animateInvocationCompletion(type, 0.0f);
        } else if (progress == 0.0f) {
            hide();
        } else {
            if (!this.mInvocationInProgress) {
                attach();
                this.mInvocationInProgress = true;
                updateAssistHandleVisibility();
            }
            setProgressInternal(type, progress);
        }
        this.mLastInvocationProgress = progress;
        logInvocationProgressMetrics(type, progress, invocationWasInProgress);
    }

    @Override // com.android.systemui.assist.AssistManager.UiController
    public void onGestureCompletion(float velocity) {
        animateInvocationCompletion(1, velocity);
    }

    @Override // com.android.systemui.assist.AssistManager.UiController
    public void hide() {
        detach();
        if (this.mInvocationAnimator.isRunning()) {
            this.mInvocationAnimator.cancel();
        }
        this.mInvocationLightsView.hide();
        this.mInvocationInProgress = false;
        updateAssistHandleVisibility();
    }

    protected static void logInvocationProgressMetrics(int type, float progress, boolean invocationWasInProgress) {
        if (!invocationWasInProgress && progress > 0.0f) {
            MetricsLogger.action(new LogMaker(1716).setType(4).setSubtype(((AssistManager) Dependency.get(AssistManager.class)).toLoggingSubType(type)));
        }
        if (invocationWasInProgress && progress == 0.0f) {
            MetricsLogger.action(new LogMaker(1716).setType(5).setSubtype(1));
        }
    }

    private void updateAssistHandleVisibility() {
        ScreenDecorations decorations = (ScreenDecorations) SysUiServiceProvider.getComponent(this.mRoot.getContext(), ScreenDecorations.class);
        decorations.lambda$setAssistHintBlocked$2$ScreenDecorations(this.mInvocationInProgress);
    }

    private void attach() {
        if (!this.mAttached) {
            this.mWindowManager.addView(this.mRoot, this.mLayoutParams);
            this.mAttached = true;
        }
    }

    private void detach() {
        if (this.mAttached) {
            this.mWindowManager.removeViewImmediate(this.mRoot);
            this.mAttached = false;
        }
    }

    private void setProgressInternal(int type, float progress) {
        this.mInvocationLightsView.onInvocationProgress(this.mProgressInterpolator.getInterpolation(progress));
    }

    private void animateInvocationCompletion(final int type, float velocity) {
        this.mInvocationAnimator = ValueAnimator.ofFloat(this.mLastInvocationProgress, 1.0f);
        this.mInvocationAnimator.setStartDelay(1L);
        this.mInvocationAnimator.setDuration(200L);
        this.mInvocationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.assist.ui.-$$Lambda$DefaultUiController$DsyFMixn8vpgo7pkqARg9d_ZEVw
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                DefaultUiController.this.lambda$animateInvocationCompletion$0$DefaultUiController(type, valueAnimator);
            }
        });
        this.mInvocationAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.assist.ui.DefaultUiController.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                DefaultUiController.this.mInvocationInProgress = false;
                DefaultUiController.this.mLastInvocationProgress = 0.0f;
                DefaultUiController.this.hide();
            }
        });
        this.mInvocationAnimator.start();
    }

    public /* synthetic */ void lambda$animateInvocationCompletion$0$DefaultUiController(int type, ValueAnimator animation) {
        setProgressInternal(type, ((Float) animation.getAnimatedValue()).floatValue());
    }
}
