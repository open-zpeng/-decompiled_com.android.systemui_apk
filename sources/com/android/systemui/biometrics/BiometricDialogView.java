package com.android.systemui.biometrics;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.biometrics.BiometricDialogView;
import com.android.systemui.util.leak.RotationUtils;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.helper.WindowHelper;
/* loaded from: classes21.dex */
public abstract class BiometricDialogView extends LinearLayout {
    private static final int ANIMATION_DURATION_AWAY = 350;
    private static final int ANIMATION_DURATION_SHOW = 250;
    private static final String KEY_CONFIRM_ENABLED = "key_confirm_enabled";
    private static final String KEY_CONFIRM_VISIBILITY = "key_confirm_visibility";
    private static final String KEY_ERROR_TEXT_COLOR = "key_error_text_color";
    private static final String KEY_ERROR_TEXT_IS_TEMPORARY = "key_error_text_is_temporary";
    private static final String KEY_ERROR_TEXT_STRING = "key_error_text_string";
    private static final String KEY_ERROR_TEXT_VISIBILITY = "key_error_text_visibility";
    private static final String KEY_STATE = "key_state";
    private static final String KEY_TRY_AGAIN_VISIBILITY = "key_try_again_visibility";
    protected static final int MSG_RESET_MESSAGE = 1;
    protected static final int STATE_AUTHENTICATED = 4;
    protected static final int STATE_AUTHENTICATING = 1;
    protected static final int STATE_ERROR = 2;
    protected static final int STATE_IDLE = 0;
    protected static final int STATE_PENDING_CONFIRMATION = 3;
    private static final String TAG = "BiometricDialogView";
    private final AccessibilityManager mAccessibilityManager;
    private boolean mAnimatingAway;
    private final float mAnimationTranslationOffset;
    protected final ImageView mBiometricIcon;
    private Bundle mBundle;
    protected final DialogViewCallback mCallback;
    private boolean mCompletedAnimatingIn;
    protected final TextView mDescriptionText;
    private final DevicePolicyManager mDevicePolicyManager;
    protected final LinearLayout mDialog;
    private final float mDialogWidth;
    private final int mErrorColor;
    protected final TextView mErrorText;
    protected Handler mHandler;
    protected final ViewGroup mLayout;
    private final Interpolator mLinearOutSlowIn;
    protected final Button mNegativeButton;
    private boolean mPendingDismissDialog;
    protected final Button mPositiveButton;
    protected boolean mRequireConfirmation;
    private Bundle mRestoredState;
    private final Runnable mShowAnimationRunnable;
    private boolean mSkipIntro;
    private int mState;
    protected final TextView mSubtitleText;
    protected final int mTextColor;
    protected final TextView mTitleText;
    protected final Button mTryAgainButton;
    private int mUserId;
    private final UserManager mUserManager;
    private boolean mWasForceRemoved;
    private final WindowManager mWindowManager;
    private final IBinder mWindowToken;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int getAuthenticatedAccessibilityResourceId();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int getDelayAfterAuthenticatedDurationMs();

    protected abstract int getHintStringResourceId();

    protected abstract int getIconDescriptionResourceId();

    protected abstract void handleResetMessage();

    protected abstract boolean shouldGrayAreaDismissDialog();

    protected abstract void updateIcon(int i, int i2);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.biometrics.BiometricDialogView$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            BiometricDialogView.this.mLayout.animate().alpha(1.0f).setDuration(250L).setInterpolator(BiometricDialogView.this.mLinearOutSlowIn).withLayer().start();
            BiometricDialogView.this.mDialog.animate().translationY(0.0f).setDuration(250L).setInterpolator(BiometricDialogView.this.mLinearOutSlowIn).withLayer().withEndAction(new Runnable() { // from class: com.android.systemui.biometrics.-$$Lambda$BiometricDialogView$1$-qm-KE8gu8fBxrvu_aT4hBUtdMU
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricDialogView.AnonymousClass1.this.lambda$run$0$BiometricDialogView$1();
                }
            }).start();
        }

        public /* synthetic */ void lambda$run$0$BiometricDialogView$1() {
            BiometricDialogView.this.onDialogAnimatedIn();
        }
    }

    public BiometricDialogView(Context context, DialogViewCallback callback) {
        super(context);
        this.mWindowToken = new Binder();
        this.mState = 0;
        this.mShowAnimationRunnable = new AnonymousClass1();
        this.mHandler = new Handler() { // from class: com.android.systemui.biometrics.BiometricDialogView.2
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BiometricDialogView.this.handleResetMessage();
                    return;
                }
                Log.e(BiometricDialogView.TAG, "Unhandled message: " + msg.what);
            }
        };
        this.mCallback = callback;
        this.mLinearOutSlowIn = Interpolators.LINEAR_OUT_SLOW_IN;
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService(AccessibilityManager.class);
        this.mWindowManager = (WindowManager) this.mContext.getSystemService(WindowManager.class);
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mDevicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class);
        this.mAnimationTranslationOffset = getResources().getDimension(R.dimen.biometric_dialog_animation_translation_offset);
        this.mErrorColor = getResources().getColor(R.color.biometric_dialog_error);
        this.mTextColor = getResources().getColor(R.color.biometric_dialog_gray);
        DisplayMetrics metrics = new DisplayMetrics();
        this.mWindowManager.getDefaultDisplay().getMetrics(metrics);
        this.mDialogWidth = Math.min(metrics.widthPixels, metrics.heightPixels);
        LayoutInflater factory = LayoutInflater.from(getContext());
        this.mLayout = (ViewGroup) factory.inflate(R.layout.biometric_dialog, (ViewGroup) this, false);
        addView(this.mLayout);
        this.mLayout.setOnKeyListener(new View.OnKeyListener() { // from class: com.android.systemui.biometrics.BiometricDialogView.3
            boolean downPressed = false;

            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 4) {
                    return false;
                }
                if (event.getAction() == 0 && !this.downPressed) {
                    this.downPressed = true;
                } else if (event.getAction() == 0) {
                    this.downPressed = false;
                } else if (event.getAction() == 1 && this.downPressed) {
                    this.downPressed = false;
                    BiometricDialogView.this.mCallback.onUserCanceled();
                }
                return true;
            }
        });
        View space = this.mLayout.findViewById(R.id.space);
        View leftSpace = this.mLayout.findViewById(R.id.left_space);
        View rightSpace = this.mLayout.findViewById(R.id.right_space);
        this.mDialog = (LinearLayout) this.mLayout.findViewById(R.id.dialog);
        this.mTitleText = (TextView) this.mLayout.findViewById(R.id.title);
        this.mSubtitleText = (TextView) this.mLayout.findViewById(R.id.subtitle);
        this.mDescriptionText = (TextView) this.mLayout.findViewById(R.id.description);
        this.mBiometricIcon = (ImageView) this.mLayout.findViewById(R.id.biometric_icon);
        this.mErrorText = (TextView) this.mLayout.findViewById(R.id.error);
        this.mNegativeButton = (Button) this.mLayout.findViewById(R.id.button2);
        this.mPositiveButton = (Button) this.mLayout.findViewById(R.id.button1);
        this.mTryAgainButton = (Button) this.mLayout.findViewById(R.id.button_try_again);
        this.mBiometricIcon.setContentDescription(getResources().getString(getIconDescriptionResourceId()));
        setDismissesDialog(space);
        setDismissesDialog(leftSpace);
        setDismissesDialog(rightSpace);
        this.mNegativeButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.biometrics.-$$Lambda$BiometricDialogView$RYcTkb_tfg9qgMigefa-LgT2rmQ
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BiometricDialogView.this.lambda$new$0$BiometricDialogView(view);
            }
        });
        this.mPositiveButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.biometrics.-$$Lambda$BiometricDialogView$2B_4hvtZC5hBNK8tMhbM4pc0Qyc
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BiometricDialogView.this.lambda$new$2$BiometricDialogView(view);
            }
        });
        this.mTryAgainButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.biometrics.-$$Lambda$BiometricDialogView$0WbgvKDg-E592VyX7dMGcDXbGTQ
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BiometricDialogView.this.lambda$new$3$BiometricDialogView(view);
            }
        });
        this.mLayout.setFocusableInTouchMode(true);
        this.mLayout.requestFocus();
    }

    public /* synthetic */ void lambda$new$0$BiometricDialogView(View v) {
        int i = this.mState;
        if (i == 3 || i == 4) {
            this.mCallback.onUserCanceled();
        } else {
            this.mCallback.onNegativePressed();
        }
    }

    public /* synthetic */ void lambda$new$2$BiometricDialogView(View v) {
        updateState(4);
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.systemui.biometrics.-$$Lambda$BiometricDialogView$Qw9PC-sGZ_LOQrNNiplnrZAouws
            @Override // java.lang.Runnable
            public final void run() {
                BiometricDialogView.this.lambda$new$1$BiometricDialogView();
            }
        }, getDelayAfterAuthenticatedDurationMs());
    }

    public /* synthetic */ void lambda$new$1$BiometricDialogView() {
        this.mCallback.onPositivePressed();
    }

    public /* synthetic */ void lambda$new$3$BiometricDialogView(View v) {
        handleResetMessage();
        updateState(1);
        showTryAgainButton(false);
        this.mPositiveButton.setVisibility(0);
        this.mPositiveButton.setEnabled(false);
        this.mCallback.onTryAgainPressed();
    }

    public void onSaveState(Bundle bundle) {
        bundle.putInt(KEY_TRY_AGAIN_VISIBILITY, this.mTryAgainButton.getVisibility());
        bundle.putInt(KEY_CONFIRM_VISIBILITY, this.mPositiveButton.getVisibility());
        bundle.putBoolean(KEY_CONFIRM_ENABLED, this.mPositiveButton.isEnabled());
        bundle.putInt(KEY_STATE, this.mState);
        bundle.putInt(KEY_ERROR_TEXT_VISIBILITY, this.mErrorText.getVisibility());
        bundle.putCharSequence(KEY_ERROR_TEXT_STRING, this.mErrorText.getText());
        bundle.putBoolean(KEY_ERROR_TEXT_IS_TEMPORARY, this.mHandler.hasMessages(1));
        bundle.putInt(KEY_ERROR_TEXT_COLOR, this.mErrorText.getCurrentTextColor());
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ImageView backgroundView = (ImageView) this.mLayout.findViewById(R.id.background);
        if (this.mUserManager.isManagedProfile(this.mUserId)) {
            Drawable image = getResources().getDrawable(R.drawable.work_challenge_background, this.mContext.getTheme());
            image.setColorFilter(this.mDevicePolicyManager.getOrganizationColorForUser(this.mUserId), PorterDuff.Mode.DARKEN);
            backgroundView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            backgroundView.setImageDrawable(image);
        } else {
            backgroundView.setImageDrawable(null);
            backgroundView.setBackgroundColor(R.color.biometric_dialog_dim_color);
        }
        this.mNegativeButton.setVisibility(0);
        if (RotationUtils.getRotation(this.mContext) != 0) {
            this.mDialog.getLayoutParams().width = (int) this.mDialogWidth;
        }
        if (this.mRestoredState == null) {
            updateState(1);
            this.mNegativeButton.setText(this.mBundle.getCharSequence("negative_text"));
            int hint = getHintStringResourceId();
            if (hint != 0) {
                this.mErrorText.setText(hint);
                this.mErrorText.setContentDescription(this.mContext.getString(hint));
                this.mErrorText.setVisibility(0);
            } else {
                this.mErrorText.setVisibility(4);
            }
            announceAccessibilityEvent();
        } else {
            updateState(this.mState);
        }
        CharSequence titleText = this.mBundle.getCharSequence(SpeechWidget.WIDGET_TITLE);
        this.mTitleText.setVisibility(0);
        this.mTitleText.setText(titleText);
        CharSequence subtitleText = this.mBundle.getCharSequence("subtitle");
        if (TextUtils.isEmpty(subtitleText)) {
            this.mSubtitleText.setVisibility(8);
            announceAccessibilityEvent();
        } else {
            this.mSubtitleText.setVisibility(0);
            this.mSubtitleText.setText(subtitleText);
        }
        CharSequence descriptionText = this.mBundle.getCharSequence("description");
        if (TextUtils.isEmpty(descriptionText)) {
            this.mDescriptionText.setVisibility(8);
            announceAccessibilityEvent();
        } else {
            this.mDescriptionText.setVisibility(0);
            this.mDescriptionText.setText(descriptionText);
        }
        if (requiresConfirmation() && this.mRestoredState == null) {
            this.mPositiveButton.setVisibility(0);
            this.mPositiveButton.setEnabled(false);
        }
        if (this.mWasForceRemoved || this.mSkipIntro) {
            this.mLayout.animate().cancel();
            this.mDialog.animate().cancel();
            this.mDialog.setAlpha(1.0f);
            this.mDialog.setTranslationY(0.0f);
            this.mLayout.setAlpha(1.0f);
            this.mCompletedAnimatingIn = true;
        } else {
            this.mDialog.setTranslationY(this.mAnimationTranslationOffset);
            this.mLayout.setAlpha(0.0f);
            postOnAnimation(this.mShowAnimationRunnable);
        }
        this.mWasForceRemoved = false;
        this.mSkipIntro = false;
    }

    private void setDismissesDialog(View v) {
        v.setClickable(true);
        v.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.biometrics.-$$Lambda$BiometricDialogView$agcwyvTgMSypTMy6oXZQaR3oBGY
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                BiometricDialogView.this.lambda$setDismissesDialog$4$BiometricDialogView(view);
            }
        });
    }

    public /* synthetic */ void lambda$setDismissesDialog$4$BiometricDialogView(View v1) {
        if (this.mState != 4 && shouldGrayAreaDismissDialog()) {
            this.mCallback.onUserCanceled();
        }
    }

    public void startDismiss() {
        if (!this.mCompletedAnimatingIn) {
            Log.w(TAG, "startDismiss(): waiting for onDialogAnimatedIn");
            this.mPendingDismissDialog = true;
            return;
        }
        this.mAnimatingAway = true;
        final Runnable endActionRunnable = new Runnable() { // from class: com.android.systemui.biometrics.BiometricDialogView.4
            @Override // java.lang.Runnable
            public void run() {
                BiometricDialogView.this.mWindowManager.removeView(BiometricDialogView.this);
                BiometricDialogView.this.mAnimatingAway = false;
                BiometricDialogView.this.handleResetMessage();
                BiometricDialogView.this.showTryAgainButton(false);
                BiometricDialogView.this.updateState(0);
            }
        };
        postOnAnimation(new Runnable() { // from class: com.android.systemui.biometrics.BiometricDialogView.5
            @Override // java.lang.Runnable
            public void run() {
                BiometricDialogView.this.mLayout.animate().alpha(0.0f).setDuration(350L).setInterpolator(BiometricDialogView.this.mLinearOutSlowIn).withLayer().start();
                BiometricDialogView.this.mDialog.animate().translationY(BiometricDialogView.this.mAnimationTranslationOffset).setDuration(350L).setInterpolator(BiometricDialogView.this.mLinearOutSlowIn).withLayer().withEndAction(endActionRunnable).start();
            }
        });
    }

    public void forceRemove() {
        this.mLayout.animate().cancel();
        this.mDialog.animate().cancel();
        this.mWindowManager.removeView(this);
        this.mAnimatingAway = false;
        this.mWasForceRemoved = true;
    }

    public void setSkipIntro(boolean skip) {
        this.mSkipIntro = skip;
    }

    public boolean isAnimatingAway() {
        return this.mAnimatingAway;
    }

    public void setBundle(Bundle bundle) {
        this.mBundle = bundle;
    }

    public void setRequireConfirmation(boolean requireConfirmation) {
        this.mRequireConfirmation = requireConfirmation;
    }

    public boolean requiresConfirmation() {
        return this.mRequireConfirmation;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public ViewGroup getLayout() {
        return this.mLayout;
    }

    protected void showTemporaryMessage(String message) {
        this.mHandler.removeMessages(1);
        this.mErrorText.setText(message);
        this.mErrorText.setTextColor(this.mErrorColor);
        this.mErrorText.setContentDescription(message);
        this.mErrorText.setVisibility(0);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1), OsdController.TN.DURATION_TIMEOUT_SHORT);
    }

    public void onHelpReceived(String message) {
        updateState(2);
        showTemporaryMessage(message);
    }

    public void onAuthenticationFailed(String message) {
        updateState(2);
        showTemporaryMessage(message);
    }

    public void onErrorReceived(String error) {
        updateState(2);
        showTemporaryMessage(error);
        showTryAgainButton(false);
        this.mCallback.onErrorShown();
    }

    public void updateState(int newState) {
        if (newState == 3) {
            this.mHandler.removeMessages(1);
            this.mErrorText.setTextColor(this.mTextColor);
            this.mErrorText.setText(R.string.biometric_dialog_tap_confirm);
            this.mErrorText.setContentDescription(getResources().getString(R.string.biometric_dialog_tap_confirm));
            this.mErrorText.setVisibility(0);
            announceAccessibilityEvent();
            this.mPositiveButton.setVisibility(0);
            this.mPositiveButton.setEnabled(true);
        } else if (newState == 4) {
            this.mPositiveButton.setVisibility(8);
            this.mNegativeButton.setVisibility(8);
            this.mErrorText.setVisibility(4);
            announceAccessibilityEvent();
        }
        if (newState == 3 || newState == 4) {
            this.mNegativeButton.setText(R.string.cancel);
            this.mNegativeButton.setContentDescription(getResources().getString(R.string.cancel));
        } else {
            this.mNegativeButton.setText(this.mBundle.getCharSequence("negative_text"));
        }
        updateIcon(this.mState, newState);
        this.mState = newState;
    }

    public void showTryAgainButton(boolean show) {
    }

    public void onDialogAnimatedIn() {
        this.mCompletedAnimatingIn = true;
        if (this.mPendingDismissDialog) {
            Log.d(TAG, "onDialogAnimatedIn(): mPendingDismissDialog=true, dismissing now");
            startDismiss();
            this.mPendingDismissDialog = false;
        }
    }

    public void restoreState(Bundle bundle) {
        this.mRestoredState = bundle;
        int tryAgainVisibility = bundle.getInt(KEY_TRY_AGAIN_VISIBILITY);
        this.mTryAgainButton.setVisibility(tryAgainVisibility);
        int confirmVisibility = bundle.getInt(KEY_CONFIRM_VISIBILITY);
        this.mPositiveButton.setVisibility(confirmVisibility);
        boolean confirmEnabled = bundle.getBoolean(KEY_CONFIRM_ENABLED);
        this.mPositiveButton.setEnabled(confirmEnabled);
        this.mState = bundle.getInt(KEY_STATE);
        this.mErrorText.setText(bundle.getCharSequence(KEY_ERROR_TEXT_STRING));
        this.mErrorText.setContentDescription(bundle.getCharSequence(KEY_ERROR_TEXT_STRING));
        int errorTextVisibility = bundle.getInt(KEY_ERROR_TEXT_VISIBILITY);
        this.mErrorText.setVisibility(errorTextVisibility);
        if (errorTextVisibility == 4 || tryAgainVisibility == 4 || confirmVisibility == 4) {
            announceAccessibilityEvent();
        }
        this.mErrorText.setTextColor(bundle.getInt(KEY_ERROR_TEXT_COLOR));
        if (bundle.getBoolean(KEY_ERROR_TEXT_IS_TEMPORARY)) {
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1), OsdController.TN.DURATION_TIMEOUT_SHORT);
        }
    }

    protected int getState() {
        return this.mState;
    }

    @Override // android.view.View
    public WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1, WindowHelper.TYPE_STATUS_BAR_PANEL, 16777216, -3);
        lp.privateFlags |= 16;
        lp.setTitle(TAG);
        lp.token = this.mWindowToken;
        return lp;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void announceAccessibilityEvent() {
        if (!this.mAccessibilityManager.isEnabled()) {
            return;
        }
        AccessibilityEvent event = AccessibilityEvent.obtain();
        event.setEventType(2048);
        event.setContentChangeTypes(1);
        this.mDialog.sendAccessibilityEventUnchecked(event);
        LinearLayout linearLayout = this.mDialog;
        linearLayout.notifySubtreeAccessibilityStateChanged(linearLayout, linearLayout, 1);
    }
}
