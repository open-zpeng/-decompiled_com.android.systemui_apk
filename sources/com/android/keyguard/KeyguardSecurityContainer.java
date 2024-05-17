package com.android.keyguard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.metrics.LogMaker;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.util.StatsLog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.VisibleForTesting;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardSecurityContainer;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.settingslib.utils.ThreadUtils;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.statusbar.phone.UnlockMethodCache;
import com.android.systemui.util.InjectionInflationController;
import com.xiaopeng.systemui.controller.CarController;
/* loaded from: classes19.dex */
public class KeyguardSecurityContainer extends FrameLayout implements KeyguardSecurityView {
    private static final int BOUNCER_DISMISS_BIOMETRIC = 2;
    private static final int BOUNCER_DISMISS_EXTENDED_ACCESS = 3;
    private static final int BOUNCER_DISMISS_NONE_SECURITY = 0;
    private static final int BOUNCER_DISMISS_PASSWORD = 1;
    private static final int BOUNCER_DISMISS_SIM = 4;
    private static final boolean DEBUG = false;
    private static final float MIN_DRAG_SIZE = 10.0f;
    private static final float SLOP_SCALE = 2.0f;
    private static final String TAG = "KeyguardSecurityView";
    private static final float TOUCH_Y_MULTIPLIER = 0.25f;
    private static final int USER_TYPE_PRIMARY = 1;
    private static final int USER_TYPE_SECONDARY_USER = 3;
    private static final int USER_TYPE_WORK_PROFILE = 2;
    private int mActivePointerId;
    private AlertDialog mAlertDialog;
    private KeyguardSecurityCallback mCallback;
    private KeyguardSecurityModel.SecurityMode mCurrentSecuritySelection;
    private KeyguardSecurityView mCurrentSecurityView;
    private InjectionInflationController mInjectionInflationController;
    private boolean mIsDragging;
    private boolean mIsVerifyUnlockOnly;
    private float mLastTouchY;
    private LockPatternUtils mLockPatternUtils;
    private final MetricsLogger mMetricsLogger;
    private KeyguardSecurityCallback mNullCallback;
    private SecurityCallback mSecurityCallback;
    private KeyguardSecurityModel mSecurityModel;
    private KeyguardSecurityViewFlipper mSecurityViewFlipper;
    private final SpringAnimation mSpringAnimation;
    private float mStartTouchY;
    private boolean mSwipeUpToRetry;
    private final UnlockMethodCache mUnlockMethodCache;
    private final KeyguardUpdateMonitor mUpdateMonitor;
    private final VelocityTracker mVelocityTracker;
    private final ViewConfiguration mViewConfiguration;

    /* loaded from: classes19.dex */
    public interface SecurityCallback {
        boolean dismiss(boolean z, int i);

        void finish(boolean z, int i);

        void onCancelClicked();

        void onSecurityModeChanged(KeyguardSecurityModel.SecurityMode securityMode, boolean z);

        void reset();

        void userActivity();
    }

    public KeyguardSecurityContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyguardSecurityContainer(Context context) {
        this(context, null, 0);
    }

    public KeyguardSecurityContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCurrentSecuritySelection = KeyguardSecurityModel.SecurityMode.Invalid;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
        this.mLastTouchY = -1.0f;
        this.mActivePointerId = -1;
        this.mStartTouchY = -1.0f;
        this.mCallback = new AnonymousClass1();
        this.mNullCallback = new KeyguardSecurityCallback() { // from class: com.android.keyguard.KeyguardSecurityContainer.2
            @Override // com.android.keyguard.KeyguardSecurityCallback
            public void userActivity() {
            }

            @Override // com.android.keyguard.KeyguardSecurityCallback
            public void reportUnlockAttempt(int userId, boolean success, int timeoutMs) {
            }

            @Override // com.android.keyguard.KeyguardSecurityCallback
            public boolean isVerifyUnlockOnly() {
                return false;
            }

            @Override // com.android.keyguard.KeyguardSecurityCallback
            public void dismiss(boolean securityVerified, int targetUserId) {
            }

            @Override // com.android.keyguard.KeyguardSecurityCallback
            public void onUserInput() {
            }

            @Override // com.android.keyguard.KeyguardSecurityCallback
            public void reset() {
            }
        };
        this.mSecurityModel = new KeyguardSecurityModel(context);
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        this.mSpringAnimation = new SpringAnimation(this, DynamicAnimation.Y);
        this.mInjectionInflationController = new InjectionInflationController(SystemUIFactory.getInstance().getRootComponent());
        this.mUnlockMethodCache = UnlockMethodCache.getInstance(context);
        this.mViewConfiguration = ViewConfiguration.get(context);
    }

    public void setSecurityCallback(SecurityCallback callback) {
        this.mSecurityCallback = callback;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void onResume(int reason) {
        if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            getSecurityView(this.mCurrentSecuritySelection).onResume(reason);
        }
        updateBiometricRetry();
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void onPause() {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mAlertDialog = null;
        }
        if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            getSecurityView(this.mCurrentSecuritySelection).onPause();
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x000e, code lost:
        if (r0 != 3) goto L9;
     */
    @Override // android.view.ViewGroup
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            int r0 = r7.getActionMasked()
            r1 = 0
            if (r0 == 0) goto L4c
            r2 = 1
            if (r0 == r2) goto L49
            r3 = 2
            if (r0 == r3) goto L11
            r2 = 3
            if (r0 == r2) goto L49
            goto L62
        L11:
            boolean r0 = r6.mIsDragging
            if (r0 == 0) goto L16
            return r2
        L16:
            boolean r0 = r6.mSwipeUpToRetry
            if (r0 != 0) goto L1b
            return r1
        L1b:
            com.android.keyguard.KeyguardSecurityView r0 = r6.mCurrentSecurityView
            boolean r0 = r0.disallowInterceptTouch(r7)
            if (r0 == 0) goto L24
            return r1
        L24:
            int r0 = r6.mActivePointerId
            int r0 = r7.findPointerIndex(r0)
            android.view.ViewConfiguration r3 = r6.mViewConfiguration
            int r3 = r3.getScaledTouchSlop()
            float r3 = (float) r3
            r4 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 * r4
            com.android.keyguard.KeyguardSecurityView r4 = r6.mCurrentSecurityView
            if (r4 == 0) goto L62
            r4 = -1
            if (r0 == r4) goto L62
            float r4 = r6.mStartTouchY
            float r5 = r7.getY(r0)
            float r4 = r4 - r5
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 <= 0) goto L62
            r6.mIsDragging = r2
            return r2
        L49:
            r6.mIsDragging = r1
            goto L62
        L4c:
            int r0 = r7.getActionIndex()
            float r2 = r7.getY(r0)
            r6.mStartTouchY = r2
            int r2 = r7.getPointerId(r0)
            r6.mActivePointerId = r2
            android.view.VelocityTracker r2 = r6.mVelocityTracker
            r2.clear()
        L62:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.keyguard.KeyguardSecurityContainer.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action != 1) {
            if (action == 2) {
                this.mVelocityTracker.addMovement(event);
                int pointerIndex = event.findPointerIndex(this.mActivePointerId);
                float y = event.getY(pointerIndex);
                float f = this.mLastTouchY;
                if (f != -1.0f) {
                    float dy = y - f;
                    setTranslationY(getTranslationY() + (0.25f * dy));
                }
                this.mLastTouchY = y;
            } else if (action != 3) {
                if (action == 6) {
                    int index = event.getActionIndex();
                    int pointerId = event.getPointerId(index);
                    if (pointerId == this.mActivePointerId) {
                        int newPointerIndex = index == 0 ? 1 : 0;
                        this.mLastTouchY = event.getY(newPointerIndex);
                        this.mActivePointerId = event.getPointerId(newPointerIndex);
                    }
                }
            }
            if (action == 1 && (-getTranslationY()) > TypedValue.applyDimension(1, MIN_DRAG_SIZE, getResources().getDisplayMetrics()) && !this.mUpdateMonitor.isFaceDetectionRunning()) {
                this.mUpdateMonitor.requestFaceAuth();
                this.mCallback.userActivity();
                showMessage(null, null);
            }
            return true;
        }
        this.mActivePointerId = -1;
        this.mLastTouchY = -1.0f;
        this.mIsDragging = false;
        startSpringAnimation(this.mVelocityTracker.getYVelocity());
        if (action == 1) {
            this.mUpdateMonitor.requestFaceAuth();
            this.mCallback.userActivity();
            showMessage(null, null);
        }
        return true;
    }

    private void startSpringAnimation(float startVelocity) {
        this.mSpringAnimation.setStartVelocity(startVelocity).animateToFinalPosition(0.0f);
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void startAppearAnimation() {
        if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            getSecurityView(this.mCurrentSecuritySelection).startAppearAnimation();
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public boolean startDisappearAnimation(Runnable onFinishRunnable) {
        if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            return getSecurityView(this.mCurrentSecuritySelection).startDisappearAnimation(onFinishRunnable);
        }
        return false;
    }

    private void updateBiometricRetry() {
        KeyguardSecurityModel.SecurityMode securityMode = getSecurityMode();
        this.mSwipeUpToRetry = (!this.mUnlockMethodCache.isFaceAuthEnabled() || securityMode == KeyguardSecurityModel.SecurityMode.SimPin || securityMode == KeyguardSecurityModel.SecurityMode.SimPuk || securityMode == KeyguardSecurityModel.SecurityMode.None) ? false : true;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public CharSequence getTitle() {
        return this.mSecurityViewFlipper.getTitle();
    }

    private KeyguardSecurityView getSecurityView(KeyguardSecurityModel.SecurityMode securityMode) {
        int securityViewIdForMode = getSecurityViewIdForMode(securityMode);
        KeyguardSecurityView view = null;
        int children = this.mSecurityViewFlipper.getChildCount();
        int child = 0;
        while (true) {
            if (child >= children) {
                break;
            } else if (this.mSecurityViewFlipper.getChildAt(child).getId() != securityViewIdForMode) {
                child++;
            } else {
                view = (KeyguardSecurityView) this.mSecurityViewFlipper.getChildAt(child);
                break;
            }
        }
        int layoutId = getLayoutIdFor(securityMode);
        if (view == null && layoutId != 0) {
            LayoutInflater inflater = LayoutInflater.from(this.mContext);
            View v = this.mInjectionInflationController.injectable(inflater).inflate(layoutId, (ViewGroup) this.mSecurityViewFlipper, false);
            this.mSecurityViewFlipper.addView(v);
            updateSecurityView(v);
            KeyguardSecurityView view2 = (KeyguardSecurityView) v;
            view2.reset();
            return view2;
        }
        return view;
    }

    private void updateSecurityView(View view) {
        if (view instanceof KeyguardSecurityView) {
            KeyguardSecurityView ksv = (KeyguardSecurityView) view;
            ksv.setKeyguardCallback(this.mCallback);
            ksv.setLockPatternUtils(this.mLockPatternUtils);
            return;
        }
        Log.w(TAG, "View " + view + " is not a KeyguardSecurityView");
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        this.mSecurityViewFlipper = (KeyguardSecurityViewFlipper) findViewById(R.id.view_flipper);
        this.mSecurityViewFlipper.setLockPatternUtils(this.mLockPatternUtils);
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void setLockPatternUtils(LockPatternUtils utils) {
        this.mLockPatternUtils = utils;
        this.mSecurityModel.setLockPatternUtils(utils);
        this.mSecurityViewFlipper.setLockPatternUtils(this.mLockPatternUtils);
    }

    @Override // android.view.View
    protected boolean fitSystemWindows(Rect insets) {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), insets.bottom);
        insets.bottom = 0;
        return false;
    }

    private void showDialog(String title, String message) {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        this.mAlertDialog = new AlertDialog.Builder(this.mContext).setTitle(title).setMessage(message).setCancelable(false).setNeutralButton(R.string.ok, (DialogInterface.OnClickListener) null).create();
        if (!(this.mContext instanceof Activity)) {
            this.mAlertDialog.getWindow().setType(CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE);
        }
        this.mAlertDialog.show();
    }

    private void showTimeoutDialog(int userId, int timeoutMs) {
        int timeoutInSeconds = timeoutMs / 1000;
        int messageId = 0;
        int i = AnonymousClass3.$SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[this.mSecurityModel.getSecurityMode(userId).ordinal()];
        if (i == 1) {
            messageId = R.string.kg_too_many_failed_pattern_attempts_dialog_message;
        } else if (i == 2) {
            messageId = R.string.kg_too_many_failed_pin_attempts_dialog_message;
        } else if (i == 3) {
            messageId = R.string.kg_too_many_failed_password_attempts_dialog_message;
        }
        if (messageId != 0) {
            String message = this.mContext.getString(messageId, Integer.valueOf(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(userId)), Integer.valueOf(timeoutInSeconds));
            showDialog(null, message);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.keyguard.KeyguardSecurityContainer$3  reason: invalid class name */
    /* loaded from: classes19.dex */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode = new int[KeyguardSecurityModel.SecurityMode.values().length];

        static {
            try {
                $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[KeyguardSecurityModel.SecurityMode.Pattern.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[KeyguardSecurityModel.SecurityMode.PIN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[KeyguardSecurityModel.SecurityMode.Password.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[KeyguardSecurityModel.SecurityMode.Invalid.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[KeyguardSecurityModel.SecurityMode.None.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[KeyguardSecurityModel.SecurityMode.SimPin.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[KeyguardSecurityModel.SecurityMode.SimPuk.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    private void showAlmostAtWipeDialog(int attempts, int remaining, int userType) {
        String message = null;
        if (userType == 1) {
            message = this.mContext.getString(R.string.kg_failed_attempts_almost_at_wipe, Integer.valueOf(attempts), Integer.valueOf(remaining));
        } else if (userType == 2) {
            message = this.mContext.getString(R.string.kg_failed_attempts_almost_at_erase_profile, Integer.valueOf(attempts), Integer.valueOf(remaining));
        } else if (userType == 3) {
            message = this.mContext.getString(R.string.kg_failed_attempts_almost_at_erase_user, Integer.valueOf(attempts), Integer.valueOf(remaining));
        }
        showDialog(null, message);
    }

    private void showWipeDialog(int attempts, int userType) {
        String message = null;
        if (userType == 1) {
            message = this.mContext.getString(R.string.kg_failed_attempts_now_wiping, Integer.valueOf(attempts));
        } else if (userType == 2) {
            message = this.mContext.getString(R.string.kg_failed_attempts_now_erasing_profile, Integer.valueOf(attempts));
        } else if (userType == 3) {
            message = this.mContext.getString(R.string.kg_failed_attempts_now_erasing_user, Integer.valueOf(attempts));
        }
        showDialog(null, message);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reportFailedUnlockAttempt(int userId, int timeoutMs) {
        int remainingBeforeWipe;
        int failedAttempts = this.mLockPatternUtils.getCurrentFailedPasswordAttempts(userId) + 1;
        DevicePolicyManager dpm = this.mLockPatternUtils.getDevicePolicyManager();
        int failedAttemptsBeforeWipe = dpm.getMaximumFailedPasswordsForWipe(null, userId);
        if (failedAttemptsBeforeWipe > 0) {
            remainingBeforeWipe = failedAttemptsBeforeWipe - failedAttempts;
        } else {
            remainingBeforeWipe = Integer.MAX_VALUE;
        }
        if (remainingBeforeWipe < 5) {
            int expiringUser = dpm.getProfileWithMinimumFailedPasswordsForWipe(userId);
            int userType = 1;
            if (expiringUser == userId) {
                if (expiringUser != 0) {
                    userType = 3;
                }
            } else if (expiringUser != -10000) {
                userType = 2;
            }
            if (remainingBeforeWipe > 0) {
                showAlmostAtWipeDialog(failedAttempts, remainingBeforeWipe, userType);
            } else {
                Slog.i(TAG, "Too many unlock attempts; user " + expiringUser + " will be wiped!");
                showWipeDialog(failedAttempts, userType);
            }
        }
        this.mLockPatternUtils.reportFailedPasswordAttempt(userId);
        if (timeoutMs > 0) {
            this.mLockPatternUtils.reportPasswordLockout(timeoutMs, userId);
            showTimeoutDialog(userId, timeoutMs);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void showPrimarySecurityScreen(boolean turningOff) {
        KeyguardSecurityModel.SecurityMode securityMode = this.mSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
        showSecurityScreen(securityMode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean showNextSecurityScreenOrFinish(boolean authenticated, int targetUserId) {
        boolean finish = false;
        boolean strongAuth = false;
        int eventSubtype = -1;
        if (this.mUpdateMonitor.getUserHasTrust(targetUserId)) {
            finish = true;
            eventSubtype = 3;
        } else if (this.mUpdateMonitor.getUserUnlockedWithBiometric(targetUserId)) {
            finish = true;
            eventSubtype = 2;
        } else if (KeyguardSecurityModel.SecurityMode.None == this.mCurrentSecuritySelection) {
            KeyguardSecurityModel.SecurityMode securityMode = this.mSecurityModel.getSecurityMode(targetUserId);
            if (KeyguardSecurityModel.SecurityMode.None == securityMode) {
                finish = true;
                eventSubtype = 0;
            } else {
                showSecurityScreen(securityMode);
            }
        } else if (authenticated) {
            int i = AnonymousClass3.$SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[this.mCurrentSecuritySelection.ordinal()];
            if (i == 1 || i == 2 || i == 3) {
                strongAuth = true;
                finish = true;
                eventSubtype = 1;
            } else if (i == 6 || i == 7) {
                KeyguardSecurityModel.SecurityMode securityMode2 = this.mSecurityModel.getSecurityMode(targetUserId);
                if (securityMode2 == KeyguardSecurityModel.SecurityMode.None && this.mLockPatternUtils.isLockScreenDisabled(KeyguardUpdateMonitor.getCurrentUser())) {
                    finish = true;
                    eventSubtype = 4;
                } else {
                    showSecurityScreen(securityMode2);
                }
            } else {
                Log.v(TAG, "Bad security screen " + this.mCurrentSecuritySelection + ", fail safe");
                showPrimarySecurityScreen(false);
            }
        }
        if (eventSubtype != -1) {
            this.mMetricsLogger.write(new LogMaker(197).setType(5).setSubtype(eventSubtype));
        }
        if (finish) {
            this.mSecurityCallback.finish(strongAuth, targetUserId);
        }
        return finish;
    }

    private void showSecurityScreen(KeyguardSecurityModel.SecurityMode securityMode) {
        KeyguardSecurityModel.SecurityMode securityMode2 = this.mCurrentSecuritySelection;
        if (securityMode == securityMode2) {
            return;
        }
        KeyguardSecurityView oldView = getSecurityView(securityMode2);
        KeyguardSecurityView newView = getSecurityView(securityMode);
        if (oldView != null) {
            oldView.onPause();
            oldView.setKeyguardCallback(this.mNullCallback);
        }
        if (securityMode != KeyguardSecurityModel.SecurityMode.None) {
            newView.onResume(2);
            newView.setKeyguardCallback(this.mCallback);
        }
        int childCount = this.mSecurityViewFlipper.getChildCount();
        int securityViewIdForMode = getSecurityViewIdForMode(securityMode);
        int i = 0;
        while (true) {
            if (i >= childCount) {
                break;
            } else if (this.mSecurityViewFlipper.getChildAt(i).getId() != securityViewIdForMode) {
                i++;
            } else {
                this.mSecurityViewFlipper.setDisplayedChild(i);
                break;
            }
        }
        this.mCurrentSecuritySelection = securityMode;
        this.mCurrentSecurityView = newView;
        this.mSecurityCallback.onSecurityModeChanged(securityMode, securityMode != KeyguardSecurityModel.SecurityMode.None && newView.needsInput());
    }

    private KeyguardSecurityViewFlipper getFlipper() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof KeyguardSecurityViewFlipper) {
                return (KeyguardSecurityViewFlipper) child;
            }
        }
        return null;
    }

    /* renamed from: com.android.keyguard.KeyguardSecurityContainer$1  reason: invalid class name */
    /* loaded from: classes19.dex */
    class AnonymousClass1 implements KeyguardSecurityCallback {
        AnonymousClass1() {
        }

        @Override // com.android.keyguard.KeyguardSecurityCallback
        public void userActivity() {
            if (KeyguardSecurityContainer.this.mSecurityCallback != null) {
                KeyguardSecurityContainer.this.mSecurityCallback.userActivity();
            }
        }

        @Override // com.android.keyguard.KeyguardSecurityCallback
        public void onUserInput() {
            KeyguardSecurityContainer.this.mUpdateMonitor.cancelFaceAuth();
        }

        @Override // com.android.keyguard.KeyguardSecurityCallback
        public void dismiss(boolean authenticated, int targetId) {
            KeyguardSecurityContainer.this.mSecurityCallback.dismiss(authenticated, targetId);
        }

        @Override // com.android.keyguard.KeyguardSecurityCallback
        public boolean isVerifyUnlockOnly() {
            return KeyguardSecurityContainer.this.mIsVerifyUnlockOnly;
        }

        @Override // com.android.keyguard.KeyguardSecurityCallback
        public void reportUnlockAttempt(int userId, boolean success, int timeoutMs) {
            if (success) {
                StatsLog.write(64, 2);
                KeyguardSecurityContainer.this.mLockPatternUtils.reportSuccessfulPasswordAttempt(userId);
                ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.keyguard.-$$Lambda$KeyguardSecurityContainer$1$ZmZG61mJJm4DEtN57wo5kJoWZGk
                    @Override // java.lang.Runnable
                    public final void run() {
                        KeyguardSecurityContainer.AnonymousClass1.lambda$reportUnlockAttempt$0();
                    }
                });
            } else {
                StatsLog.write(64, 1);
                KeyguardSecurityContainer.this.reportFailedUnlockAttempt(userId, timeoutMs);
            }
            KeyguardSecurityContainer.this.mMetricsLogger.write(new LogMaker(197).setType(success ? 10 : 11));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$reportUnlockAttempt$0() {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
            }
            Runtime.getRuntime().gc();
        }

        @Override // com.android.keyguard.KeyguardSecurityCallback
        public void reset() {
            KeyguardSecurityContainer.this.mSecurityCallback.reset();
        }

        @Override // com.android.keyguard.KeyguardSecurityCallback
        public void onCancelClicked() {
            KeyguardSecurityContainer.this.mSecurityCallback.onCancelClicked();
        }
    }

    private int getSecurityViewIdForMode(KeyguardSecurityModel.SecurityMode securityMode) {
        int i = AnonymousClass3.$SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[securityMode.ordinal()];
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 6) {
                        if (i == 7) {
                            return R.id.keyguard_sim_puk_view;
                        }
                        return 0;
                    }
                    return R.id.keyguard_sim_pin_view;
                }
                return R.id.keyguard_password_view;
            }
            return R.id.keyguard_pin_view;
        }
        return R.id.keyguard_pattern_view;
    }

    @VisibleForTesting
    public int getLayoutIdFor(KeyguardSecurityModel.SecurityMode securityMode) {
        int i = AnonymousClass3.$SwitchMap$com$android$keyguard$KeyguardSecurityModel$SecurityMode[securityMode.ordinal()];
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 6) {
                        if (i == 7) {
                            return R.layout.keyguard_sim_puk_view;
                        }
                        return 0;
                    }
                    return R.layout.keyguard_sim_pin_view;
                }
                return R.layout.keyguard_password_view;
            }
            return R.layout.keyguard_pin_view;
        }
        return R.layout.keyguard_pattern_view;
    }

    public KeyguardSecurityModel.SecurityMode getSecurityMode() {
        return this.mSecurityModel.getSecurityMode(KeyguardUpdateMonitor.getCurrentUser());
    }

    public KeyguardSecurityModel.SecurityMode getCurrentSecurityMode() {
        return this.mCurrentSecuritySelection;
    }

    public KeyguardSecurityView getCurrentSecurityView() {
        return this.mCurrentSecurityView;
    }

    public void verifyUnlock() {
        this.mIsVerifyUnlockOnly = true;
        showSecurityScreen(getSecurityMode());
    }

    public KeyguardSecurityModel.SecurityMode getCurrentSecuritySelection() {
        return this.mCurrentSecuritySelection;
    }

    public void dismiss(boolean authenticated, int targetUserId) {
        this.mCallback.dismiss(authenticated, targetUserId);
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public boolean needsInput() {
        return this.mSecurityViewFlipper.needsInput();
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void setKeyguardCallback(KeyguardSecurityCallback callback) {
        this.mSecurityViewFlipper.setKeyguardCallback(callback);
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void reset() {
        this.mSecurityViewFlipper.reset();
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public KeyguardSecurityCallback getCallback() {
        return this.mSecurityViewFlipper.getCallback();
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showPromptReason(int reason) {
        if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            if (reason != 0) {
                Log.i(TAG, "Strong auth required, reason: " + reason);
            }
            getSecurityView(this.mCurrentSecuritySelection).showPromptReason(reason);
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showMessage(CharSequence message, ColorStateList colorState) {
        if (this.mCurrentSecuritySelection != KeyguardSecurityModel.SecurityMode.None) {
            getSecurityView(this.mCurrentSecuritySelection).showMessage(message, colorState);
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showUsabilityHint() {
        this.mSecurityViewFlipper.showUsabilityHint();
    }
}
