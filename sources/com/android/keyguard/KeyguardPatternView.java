package com.android.keyguard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.keyguard.EmergencyButton;
import com.android.settingslib.animation.AppearAnimationCreator;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.android.systemui.R;
import com.xiaopeng.systemui.controller.OsdController;
import java.util.List;
/* loaded from: classes19.dex */
public class KeyguardPatternView extends LinearLayout implements KeyguardSecurityView, AppearAnimationCreator<LockPatternView.CellState>, EmergencyButton.EmergencyButtonCallback {
    private static final boolean DEBUG = false;
    public static final float DISAPPEAR_MULTIPLIER_LOCKED = 1.5f;
    private static final int MIN_PATTERN_BEFORE_POKE_WAKELOCK = 2;
    private static final int PATTERNS_TOUCH_AREA_EXTENSION = 40;
    private static final int PATTERN_CLEAR_TIMEOUT_MS = 2000;
    private static final String TAG = "SecurityPatternView";
    private static final int UNLOCK_PATTERN_WAKE_INTERVAL_MS = 7000;
    private final AppearAnimationUtils mAppearAnimationUtils;
    private KeyguardSecurityCallback mCallback;
    private Runnable mCancelPatternRunnable;
    private ViewGroup mContainer;
    private CountDownTimer mCountdownTimer;
    private final DisappearAnimationUtils mDisappearAnimationUtils;
    private final DisappearAnimationUtils mDisappearAnimationUtilsLocked;
    private int mDisappearYTranslation;
    private View mEcaView;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private long mLastPokeTime;
    private final Rect mLockPatternScreenBounds;
    private LockPatternUtils mLockPatternUtils;
    private LockPatternView mLockPatternView;
    private AsyncTask<?, ?, ?> mPendingLockCheck;
    @VisibleForTesting
    KeyguardMessageArea mSecurityMessageDisplay;
    private final Rect mTempRect;
    private final int[] mTmpPosition;

    /* loaded from: classes19.dex */
    enum FooterMode {
        Normal,
        ForgotLockPattern,
        VerifyUnlocked
    }

    public KeyguardPatternView(Context context) {
        this(context, null);
    }

    public KeyguardPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTmpPosition = new int[2];
        this.mTempRect = new Rect();
        this.mLockPatternScreenBounds = new Rect();
        this.mCountdownTimer = null;
        this.mLastPokeTime = -7000L;
        this.mCancelPatternRunnable = new Runnable() { // from class: com.android.keyguard.KeyguardPatternView.1
            @Override // java.lang.Runnable
            public void run() {
                KeyguardPatternView.this.mLockPatternView.clearPattern();
            }
        };
        this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        this.mAppearAnimationUtils = new AppearAnimationUtils(context, 220L, 1.5f, 2.0f, AnimationUtils.loadInterpolator(this.mContext, 17563662));
        this.mDisappearAnimationUtils = new DisappearAnimationUtils(context, 125L, 1.2f, 0.6f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearAnimationUtilsLocked = new DisappearAnimationUtils(context, 187L, 1.2f, 0.6f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearYTranslation = getResources().getDimensionPixelSize(R.dimen.disappear_y_translation);
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void setKeyguardCallback(KeyguardSecurityCallback callback) {
        this.mCallback = callback;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void setLockPatternUtils(LockPatternUtils utils) {
        this.mLockPatternUtils = utils;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
        if (lockPatternUtils == null) {
            lockPatternUtils = new LockPatternUtils(this.mContext);
        }
        this.mLockPatternUtils = lockPatternUtils;
        this.mLockPatternView = findViewById(R.id.lockPatternView);
        this.mLockPatternView.setSaveEnabled(false);
        this.mLockPatternView.setOnPatternListener(new UnlockPatternListener());
        this.mLockPatternView.setInStealthMode(!this.mLockPatternUtils.isVisiblePatternEnabled(KeyguardUpdateMonitor.getCurrentUser()));
        this.mLockPatternView.setTactileFeedbackEnabled(this.mLockPatternUtils.isTactileFeedbackEnabled());
        this.mEcaView = findViewById(R.id.keyguard_selector_fade_container);
        this.mContainer = (ViewGroup) findViewById(R.id.container);
        EmergencyButton button = (EmergencyButton) findViewById(R.id.emergency_call_button);
        if (button != null) {
            button.setCallback(this);
        }
        View cancelBtn = findViewById(R.id.cancel_button);
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.keyguard.-$$Lambda$KeyguardPatternView$N-2kmt4uZ3ZvQBB4SmVDuZJ_Wqw
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    KeyguardPatternView.this.lambda$onFinishInflate$0$KeyguardPatternView(view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onFinishInflate$0$KeyguardPatternView(View view) {
        this.mCallback.reset();
        this.mCallback.onCancelClicked();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSecurityMessageDisplay = KeyguardMessageArea.findSecurityMessageDisplay(this);
    }

    @Override // com.android.keyguard.EmergencyButton.EmergencyButtonCallback
    public void onEmergencyButtonClickedWhenInCall() {
        this.mCallback.reset();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = super.onTouchEvent(ev);
        long elapsed = SystemClock.elapsedRealtime() - this.mLastPokeTime;
        if (result && elapsed > 6900) {
            this.mLastPokeTime = SystemClock.elapsedRealtime();
        }
        boolean z = false;
        this.mTempRect.set(0, 0, 0, 0);
        offsetRectIntoDescendantCoords(this.mLockPatternView, this.mTempRect);
        ev.offsetLocation(this.mTempRect.left, this.mTempRect.top);
        boolean result2 = (this.mLockPatternView.dispatchTouchEvent(ev) || result) ? true : true;
        ev.offsetLocation(-this.mTempRect.left, -this.mTempRect.top);
        return result2;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mLockPatternView.getLocationOnScreen(this.mTmpPosition);
        Rect rect = this.mLockPatternScreenBounds;
        int[] iArr = this.mTmpPosition;
        rect.set(iArr[0] - 40, iArr[1] - 40, iArr[0] + this.mLockPatternView.getWidth() + 40, this.mTmpPosition[1] + this.mLockPatternView.getHeight() + 40);
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void reset() {
        this.mLockPatternView.setInStealthMode(!this.mLockPatternUtils.isVisiblePatternEnabled(KeyguardUpdateMonitor.getCurrentUser()));
        this.mLockPatternView.enableInput();
        this.mLockPatternView.setEnabled(true);
        this.mLockPatternView.clearPattern();
        if (this.mSecurityMessageDisplay == null) {
            return;
        }
        long deadline = this.mLockPatternUtils.getLockoutAttemptDeadline(KeyguardUpdateMonitor.getCurrentUser());
        if (deadline != 0) {
            handleAttemptLockout(deadline);
        } else {
            displayDefaultSecurityMessage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayDefaultSecurityMessage() {
        KeyguardMessageArea keyguardMessageArea = this.mSecurityMessageDisplay;
        if (keyguardMessageArea != null) {
            keyguardMessageArea.setMessage("");
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showUsabilityHint() {
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public boolean disallowInterceptTouch(MotionEvent event) {
        return !this.mLockPatternView.isEmpty() || this.mLockPatternScreenBounds.contains((int) event.getRawX(), (int) event.getRawY());
    }

    public void cleanUp() {
        this.mLockPatternUtils = null;
        this.mLockPatternView.setOnPatternListener((LockPatternView.OnPatternListener) null);
    }

    /* loaded from: classes19.dex */
    private class UnlockPatternListener implements LockPatternView.OnPatternListener {
        private UnlockPatternListener() {
        }

        public void onPatternStart() {
            KeyguardPatternView.this.mLockPatternView.removeCallbacks(KeyguardPatternView.this.mCancelPatternRunnable);
            KeyguardPatternView.this.mSecurityMessageDisplay.setMessage("");
        }

        public void onPatternCleared() {
        }

        public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {
            KeyguardPatternView.this.mCallback.userActivity();
            KeyguardPatternView.this.mCallback.onUserInput();
        }

        public void onPatternDetected(List<LockPatternView.Cell> pattern) {
            KeyguardPatternView.this.mLockPatternView.disableInput();
            if (KeyguardPatternView.this.mPendingLockCheck != null) {
                KeyguardPatternView.this.mPendingLockCheck.cancel(false);
            }
            final int userId = KeyguardUpdateMonitor.getCurrentUser();
            if (pattern.size() < 4) {
                KeyguardPatternView.this.mLockPatternView.enableInput();
                onPatternChecked(userId, false, 0, false);
                return;
            }
            if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionStart(3);
                LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionStart(4);
            }
            KeyguardPatternView keyguardPatternView = KeyguardPatternView.this;
            keyguardPatternView.mPendingLockCheck = LockPatternChecker.checkPattern(keyguardPatternView.mLockPatternUtils, pattern, userId, new LockPatternChecker.OnCheckCallback() { // from class: com.android.keyguard.KeyguardPatternView.UnlockPatternListener.1
                public void onEarlyMatched() {
                    if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionEnd(3);
                    }
                    UnlockPatternListener.this.onPatternChecked(userId, true, 0, true);
                }

                public void onChecked(boolean matched, int timeoutMs) {
                    if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionEnd(4);
                    }
                    KeyguardPatternView.this.mLockPatternView.enableInput();
                    KeyguardPatternView.this.mPendingLockCheck = null;
                    if (!matched) {
                        UnlockPatternListener.this.onPatternChecked(userId, false, timeoutMs, true);
                    }
                }

                public void onCancelled() {
                    if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionEnd(4);
                    }
                }
            });
            if (pattern.size() > 2) {
                KeyguardPatternView.this.mCallback.userActivity();
                KeyguardPatternView.this.mCallback.onUserInput();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onPatternChecked(int userId, boolean matched, int timeoutMs, boolean isValidPattern) {
            boolean dismissKeyguard = KeyguardUpdateMonitor.getCurrentUser() == userId;
            if (matched) {
                KeyguardPatternView.this.mCallback.reportUnlockAttempt(userId, true, 0);
                if (dismissKeyguard) {
                    KeyguardPatternView.this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    KeyguardPatternView.this.mCallback.dismiss(true, userId);
                    return;
                }
                return;
            }
            KeyguardPatternView.this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
            if (isValidPattern) {
                KeyguardPatternView.this.mCallback.reportUnlockAttempt(userId, false, timeoutMs);
                if (timeoutMs > 0) {
                    long deadline = KeyguardPatternView.this.mLockPatternUtils.setLockoutAttemptDeadline(userId, timeoutMs);
                    KeyguardPatternView.this.handleAttemptLockout(deadline);
                }
            }
            if (timeoutMs == 0) {
                KeyguardPatternView.this.mSecurityMessageDisplay.setMessage(R.string.kg_wrong_pattern);
                KeyguardPatternView.this.mLockPatternView.postDelayed(KeyguardPatternView.this.mCancelPatternRunnable, (long) OsdController.TN.DURATION_TIMEOUT_SHORT);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r10v0, types: [com.android.keyguard.KeyguardPatternView$2] */
    public void handleAttemptLockout(long elapsedRealtimeDeadline) {
        this.mLockPatternView.clearPattern();
        this.mLockPatternView.setEnabled(false);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long secondsInFuture = (long) Math.ceil((elapsedRealtimeDeadline - elapsedRealtime) / 1000.0d);
        this.mCountdownTimer = new CountDownTimer(secondsInFuture * 1000, 1000L) { // from class: com.android.keyguard.KeyguardPatternView.2
            @Override // android.os.CountDownTimer
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) Math.round(millisUntilFinished / 1000.0d);
                KeyguardPatternView.this.mSecurityMessageDisplay.setMessage(KeyguardPatternView.this.mContext.getResources().getQuantityString(R.plurals.kg_too_many_failed_attempts_countdown, secondsRemaining, Integer.valueOf(secondsRemaining)));
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
                KeyguardPatternView.this.mLockPatternView.setEnabled(true);
                KeyguardPatternView.this.displayDefaultSecurityMessage();
            }
        }.start();
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public boolean needsInput() {
        return false;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void onPause() {
        CountDownTimer countDownTimer = this.mCountdownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            this.mCountdownTimer = null;
        }
        AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
        if (asyncTask != null) {
            asyncTask.cancel(false);
            this.mPendingLockCheck = null;
        }
        displayDefaultSecurityMessage();
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void onResume(int reason) {
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public KeyguardSecurityCallback getCallback() {
        return this.mCallback;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showPromptReason(int reason) {
        if (reason != 0) {
            if (reason == 1) {
                this.mSecurityMessageDisplay.setMessage(R.string.kg_prompt_reason_restart_pattern);
            } else if (reason == 2) {
                this.mSecurityMessageDisplay.setMessage(R.string.kg_prompt_reason_timeout_pattern);
            } else if (reason == 3) {
                this.mSecurityMessageDisplay.setMessage(R.string.kg_prompt_reason_device_admin);
            } else if (reason == 4) {
                this.mSecurityMessageDisplay.setMessage(R.string.kg_prompt_reason_user_request);
            } else {
                this.mSecurityMessageDisplay.setMessage(R.string.kg_prompt_reason_timeout_pattern);
            }
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showMessage(CharSequence message, ColorStateList colorState) {
        if (colorState != null) {
            this.mSecurityMessageDisplay.setNextMessageColor(colorState);
        }
        this.mSecurityMessageDisplay.setMessage(message);
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void startAppearAnimation() {
        enableClipping(false);
        setAlpha(1.0f);
        setTranslationY(this.mAppearAnimationUtils.getStartTranslation());
        AppearAnimationUtils.startTranslationYAnimation(this, 0L, 500L, 0.0f, this.mAppearAnimationUtils.getInterpolator());
        this.mAppearAnimationUtils.startAnimation2d(this.mLockPatternView.getCellStates(), new Runnable() { // from class: com.android.keyguard.KeyguardPatternView.3
            @Override // java.lang.Runnable
            public void run() {
                KeyguardPatternView.this.enableClipping(true);
            }
        }, this);
        if (!TextUtils.isEmpty(this.mSecurityMessageDisplay.getText())) {
            AppearAnimationUtils appearAnimationUtils = this.mAppearAnimationUtils;
            appearAnimationUtils.createAnimation((View) this.mSecurityMessageDisplay, 0L, 220L, appearAnimationUtils.getStartTranslation(), true, this.mAppearAnimationUtils.getInterpolator(), (Runnable) null);
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public boolean startDisappearAnimation(final Runnable finishRunnable) {
        float durationMultiplier;
        DisappearAnimationUtils disappearAnimationUtils;
        if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {
            durationMultiplier = 1.5f;
        } else {
            durationMultiplier = 1.0f;
        }
        this.mLockPatternView.clearPattern();
        enableClipping(false);
        setTranslationY(0.0f);
        AppearAnimationUtils.startTranslationYAnimation(this, 0L, 300.0f * durationMultiplier, -this.mDisappearAnimationUtils.getStartTranslation(), this.mDisappearAnimationUtils.getInterpolator());
        if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {
            disappearAnimationUtils = this.mDisappearAnimationUtilsLocked;
        } else {
            disappearAnimationUtils = this.mDisappearAnimationUtils;
        }
        disappearAnimationUtils.startAnimation2d(this.mLockPatternView.getCellStates(), new Runnable() { // from class: com.android.keyguard.-$$Lambda$KeyguardPatternView$i51b4f44m8j5rvWUlLMM4eRNauI
            @Override // java.lang.Runnable
            public final void run() {
                KeyguardPatternView.this.lambda$startDisappearAnimation$1$KeyguardPatternView(finishRunnable);
            }
        }, this);
        if (!TextUtils.isEmpty(this.mSecurityMessageDisplay.getText())) {
            DisappearAnimationUtils disappearAnimationUtils2 = this.mDisappearAnimationUtils;
            disappearAnimationUtils2.createAnimation((View) this.mSecurityMessageDisplay, 0L, 200.0f * durationMultiplier, 3.0f * (-disappearAnimationUtils2.getStartTranslation()), false, this.mDisappearAnimationUtils.getInterpolator(), (Runnable) null);
            return true;
        }
        return true;
    }

    public /* synthetic */ void lambda$startDisappearAnimation$1$KeyguardPatternView(Runnable finishRunnable) {
        enableClipping(true);
        if (finishRunnable != null) {
            finishRunnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableClipping(boolean enable) {
        setClipChildren(enable);
        this.mContainer.setClipToPadding(enable);
        this.mContainer.setClipChildren(enable);
    }

    @Override // com.android.settingslib.animation.AppearAnimationCreator
    public void createAnimation(LockPatternView.CellState animatedCell, long delay, long duration, float translationY, boolean appearing, Interpolator interpolator, Runnable finishListener) {
        this.mLockPatternView.startCellStateAnimation(animatedCell, 1.0f, appearing ? 1.0f : 0.0f, appearing ? translationY : 0.0f, appearing ? 0.0f : translationY, appearing ? 0.0f : 1.0f, 1.0f, delay, duration, interpolator, finishListener);
        if (finishListener != null) {
            this.mAppearAnimationUtils.createAnimation(this.mEcaView, delay, duration, translationY, appearing, interpolator, (Runnable) null);
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public CharSequence getTitle() {
        return getContext().getString(17040176);
    }
}
