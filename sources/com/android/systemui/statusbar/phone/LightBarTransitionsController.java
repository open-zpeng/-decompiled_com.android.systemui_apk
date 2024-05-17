package com.android.systemui.statusbar.phone;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.MathUtils;
import android.util.TimeUtils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.Interpolators;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class LightBarTransitionsController implements Dumpable, CommandQueue.Callbacks, StatusBarStateController.StateListener {
    public static final int DEFAULT_TINT_ANIMATION_DURATION = 120;
    private static final String EXTRA_DARK_INTENSITY = "dark_intensity";
    private final DarkIntensityApplier mApplier;
    private final Context mContext;
    private float mDarkIntensity;
    private int mDisplayId;
    private float mDozeAmount;
    private float mNextDarkIntensity;
    private float mPendingDarkIntensity;
    private ValueAnimator mTintAnimator;
    private boolean mTintChangePending;
    private boolean mTransitionDeferring;
    private long mTransitionDeferringDuration;
    private long mTransitionDeferringStartTime;
    private boolean mTransitionPending;
    private final Runnable mTransitionDeferringDoneRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.LightBarTransitionsController.1
        @Override // java.lang.Runnable
        public void run() {
            LightBarTransitionsController.this.mTransitionDeferring = false;
        }
    };
    private final Handler mHandler = new Handler();
    private final KeyguardMonitor mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
    private final StatusBarStateController mStatusBarStateController = (StatusBarStateController) Dependency.get(StatusBarStateController.class);

    /* loaded from: classes21.dex */
    public interface DarkIntensityApplier {
        void applyDarkIntensity(float f);

        int getTintAnimationDuration();
    }

    public LightBarTransitionsController(Context context, DarkIntensityApplier applier) {
        this.mApplier = applier;
        ((CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
        this.mStatusBarStateController.addCallback(this);
        this.mDozeAmount = this.mStatusBarStateController.getDozeAmount();
        this.mContext = context;
        this.mDisplayId = this.mContext.getDisplayId();
    }

    public void destroy(Context context) {
        ((CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class)).removeCallback((CommandQueue.Callbacks) this);
        this.mStatusBarStateController.removeCallback(this);
    }

    public void saveState(Bundle outState) {
        ValueAnimator valueAnimator = this.mTintAnimator;
        float intensity = (valueAnimator == null || !valueAnimator.isRunning()) ? this.mDarkIntensity : this.mNextDarkIntensity;
        outState.putFloat(EXTRA_DARK_INTENSITY, intensity);
    }

    public void restoreState(Bundle savedInstanceState) {
        setIconTintInternal(savedInstanceState.getFloat(EXTRA_DARK_INTENSITY, 0.0f));
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void appTransitionPending(int displayId, boolean forced) {
        if (this.mDisplayId == displayId) {
            if (this.mKeyguardMonitor.isKeyguardGoingAway() && !forced) {
                return;
            }
            this.mTransitionPending = true;
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void appTransitionCancelled(int displayId) {
        if (this.mDisplayId != displayId) {
            return;
        }
        if (this.mTransitionPending && this.mTintChangePending) {
            this.mTintChangePending = false;
            animateIconTint(this.mPendingDarkIntensity, 0L, this.mApplier.getTintAnimationDuration());
        }
        this.mTransitionPending = false;
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void appTransitionStarting(int displayId, long startTime, long duration, boolean forced) {
        if (this.mDisplayId == displayId) {
            if (this.mKeyguardMonitor.isKeyguardGoingAway() && !forced) {
                return;
            }
            if (this.mTransitionPending && this.mTintChangePending) {
                this.mTintChangePending = false;
                animateIconTint(this.mPendingDarkIntensity, Math.max(0L, startTime - SystemClock.uptimeMillis()), duration);
            } else if (this.mTransitionPending) {
                this.mTransitionDeferring = true;
                this.mTransitionDeferringStartTime = startTime;
                this.mTransitionDeferringDuration = duration;
                this.mHandler.removeCallbacks(this.mTransitionDeferringDoneRunnable);
                this.mHandler.postAtTime(this.mTransitionDeferringDoneRunnable, startTime);
            }
            this.mTransitionPending = false;
        }
    }

    public void setIconsDark(boolean dark, boolean animate) {
        if (!animate) {
            setIconTintInternal(dark ? 1.0f : 0.0f);
            this.mNextDarkIntensity = dark ? 1.0f : 0.0f;
        } else if (this.mTransitionPending) {
            deferIconTintChange(dark ? 1.0f : 0.0f);
        } else if (this.mTransitionDeferring) {
            animateIconTint(dark ? 1.0f : 0.0f, Math.max(0L, this.mTransitionDeferringStartTime - SystemClock.uptimeMillis()), this.mTransitionDeferringDuration);
        } else {
            animateIconTint(dark ? 1.0f : 0.0f, 0L, this.mApplier.getTintAnimationDuration());
        }
    }

    public float getCurrentDarkIntensity() {
        return this.mDarkIntensity;
    }

    private void deferIconTintChange(float darkIntensity) {
        if (this.mTintChangePending && darkIntensity == this.mPendingDarkIntensity) {
            return;
        }
        this.mTintChangePending = true;
        this.mPendingDarkIntensity = darkIntensity;
    }

    private void animateIconTint(float targetDarkIntensity, long delay, long duration) {
        if (this.mNextDarkIntensity == targetDarkIntensity) {
            return;
        }
        ValueAnimator valueAnimator = this.mTintAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mNextDarkIntensity = targetDarkIntensity;
        this.mTintAnimator = ValueAnimator.ofFloat(this.mDarkIntensity, targetDarkIntensity);
        this.mTintAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$LightBarTransitionsController$PJRveQsGC7aANrqdSv3tRYb3x7c
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                LightBarTransitionsController.this.lambda$animateIconTint$0$LightBarTransitionsController(valueAnimator2);
            }
        });
        this.mTintAnimator.setDuration(duration);
        this.mTintAnimator.setStartDelay(delay);
        this.mTintAnimator.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        this.mTintAnimator.start();
    }

    public /* synthetic */ void lambda$animateIconTint$0$LightBarTransitionsController(ValueAnimator animation) {
        setIconTintInternal(((Float) animation.getAnimatedValue()).floatValue());
    }

    private void setIconTintInternal(float darkIntensity) {
        this.mDarkIntensity = darkIntensity;
        dispatchDark();
    }

    private void dispatchDark() {
        this.mApplier.applyDarkIntensity(MathUtils.lerp(this.mDarkIntensity, 0.0f, this.mDozeAmount));
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("  mTransitionDeferring=");
        pw.print(this.mTransitionDeferring);
        if (this.mTransitionDeferring) {
            pw.println();
            pw.print("   mTransitionDeferringStartTime=");
            pw.println(TimeUtils.formatUptime(this.mTransitionDeferringStartTime));
            pw.print("   mTransitionDeferringDuration=");
            TimeUtils.formatDuration(this.mTransitionDeferringDuration, pw);
            pw.println();
        }
        pw.print("  mTransitionPending=");
        pw.print(this.mTransitionPending);
        pw.print(" mTintChangePending=");
        pw.println(this.mTintChangePending);
        pw.print("  mPendingDarkIntensity=");
        pw.print(this.mPendingDarkIntensity);
        pw.print(" mDarkIntensity=");
        pw.print(this.mDarkIntensity);
        pw.print(" mNextDarkIntensity=");
        pw.println(this.mNextDarkIntensity);
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozeAmountChanged(float linear, float eased) {
        this.mDozeAmount = eased;
        dispatchDark();
    }
}
