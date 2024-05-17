package com.android.systemui.biometrics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.android.systemui.R;
import com.xiaopeng.systemui.controller.OsdController;
/* loaded from: classes21.dex */
public class FaceDialogView extends BiometricDialogView {
    private static final int GROW_DURATION = 150;
    private static final int HIDE_DIALOG_DELAY = 500;
    private static final int IMPLICIT_Y_PADDING = 16;
    private static final String KEY_DIALOG_ANIMATED_IN = "key_dialog_animated_in";
    private static final String KEY_DIALOG_SIZE = "key_dialog_size";
    private static final int SIZE_BIG = 3;
    private static final int SIZE_GROWING = 2;
    private static final int SIZE_SMALL = 1;
    private static final int SIZE_UNKNOWN = 0;
    private static final String TAG = "FaceDialogView";
    private static final int TEXT_ANIMATE_DISTANCE = 32;
    private boolean mDialogAnimatedIn;
    private final Runnable mErrorToIdleAnimationRunnable;
    private IconController mIconController;
    private float mIconOriginalY;
    private DialogOutlineProvider mOutlineProvider;
    private int mSize;

    /* loaded from: classes21.dex */
    private final class IconController extends Animatable2.AnimationCallback {
        private boolean mLastPulseDirection;
        int mState = 0;

        IconController() {
        }

        public void animateOnce(int iconRes) {
            animateIcon(iconRes, false);
        }

        public void showStatic(int iconRes) {
            FaceDialogView.this.mBiometricIcon.setImageDrawable(FaceDialogView.this.mContext.getDrawable(iconRes));
        }

        public void startPulsing() {
            this.mLastPulseDirection = false;
            animateIcon(R.drawable.face_dialog_pulse_dark_to_light, true);
        }

        public void showIcon(int iconRes) {
            Drawable drawable = FaceDialogView.this.mContext.getDrawable(iconRes);
            FaceDialogView.this.mBiometricIcon.setImageDrawable(drawable);
        }

        private void animateIcon(int iconRes, boolean repeat) {
            AnimatedVectorDrawable icon = (AnimatedVectorDrawable) FaceDialogView.this.mContext.getDrawable(iconRes);
            FaceDialogView.this.mBiometricIcon.setImageDrawable(icon);
            icon.forceAnimationOnUI();
            if (repeat) {
                icon.registerAnimationCallback(this);
            }
            icon.start();
        }

        private void pulseInNextDirection() {
            int iconRes = this.mLastPulseDirection ? R.drawable.face_dialog_pulse_dark_to_light : R.drawable.face_dialog_pulse_light_to_dark;
            animateIcon(iconRes, true);
            this.mLastPulseDirection = true ^ this.mLastPulseDirection;
        }

        @Override // android.graphics.drawable.Animatable2.AnimationCallback
        public void onAnimationEnd(Drawable drawable) {
            super.onAnimationEnd(drawable);
            if (this.mState == 1) {
                pulseInNextDirection();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class DialogOutlineProvider extends ViewOutlineProvider {
        float mY;

        private DialogOutlineProvider() {
        }

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, (int) this.mY, FaceDialogView.this.mDialog.getWidth(), FaceDialogView.this.mDialog.getBottom(), FaceDialogView.this.getResources().getDimension(R.dimen.biometric_dialog_corner_size));
        }

        int calculateSmall() {
            float padding = FaceDialogView.this.dpToPixels(16.0f);
            return (FaceDialogView.this.mDialog.getHeight() - FaceDialogView.this.mBiometricIcon.getHeight()) - (((int) padding) * 2);
        }

        void setOutlineY(float y) {
            this.mY = y;
        }
    }

    public /* synthetic */ void lambda$new$0$FaceDialogView() {
        updateState(0);
        this.mErrorText.setVisibility(4);
        announceAccessibilityEvent();
    }

    public FaceDialogView(Context context, DialogViewCallback callback) {
        super(context, callback);
        this.mOutlineProvider = new DialogOutlineProvider();
        this.mErrorToIdleAnimationRunnable = new Runnable() { // from class: com.android.systemui.biometrics.-$$Lambda$FaceDialogView$czD-cP2iyglsmecT6GyDucy4syc
            @Override // java.lang.Runnable
            public final void run() {
                FaceDialogView.this.lambda$new$0$FaceDialogView();
            }
        };
        this.mIconController = new IconController();
    }

    private void updateSize(int newSize) {
        float padding = dpToPixels(16.0f);
        float iconSmallPositionY = (this.mDialog.getHeight() - this.mBiometricIcon.getHeight()) - padding;
        if (newSize != 1) {
            if (this.mSize == 1 && newSize == 3) {
                this.mSize = 2;
                ValueAnimator outlineAnimator = ValueAnimator.ofFloat(this.mOutlineProvider.calculateSmall(), 0.0f);
                outlineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.biometrics.-$$Lambda$FaceDialogView$MYsjnJHs10NhJPXX4FLFafo9YY8
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        FaceDialogView.this.lambda$updateSize$1$FaceDialogView(valueAnimator);
                    }
                });
                ValueAnimator iconAnimator = ValueAnimator.ofFloat(iconSmallPositionY, this.mIconOriginalY);
                iconAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.biometrics.-$$Lambda$FaceDialogView$sSRypCm7hC9Of8-MaBum8gJxI9Q
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        FaceDialogView.this.lambda$updateSize$2$FaceDialogView(valueAnimator);
                    }
                });
                ValueAnimator textSlideAnimator = ValueAnimator.ofFloat(dpToPixels(32.0f), 0.0f);
                textSlideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.biometrics.-$$Lambda$FaceDialogView$6DWEWGhnaIhNrLSCCr7Op0b0jD4
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        FaceDialogView.this.lambda$updateSize$3$FaceDialogView(valueAnimator);
                    }
                });
                ValueAnimator opacityAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                opacityAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.biometrics.-$$Lambda$FaceDialogView$y85DatSeGK11aptJj_FqyvqURDw
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        FaceDialogView.this.lambda$updateSize$4$FaceDialogView(valueAnimator);
                    }
                });
                AnimatorSet as = new AnimatorSet();
                as.setDuration(150L);
                as.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.biometrics.FaceDialogView.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        FaceDialogView.this.mTitleText.setVisibility(0);
                        FaceDialogView.this.mErrorText.setVisibility(0);
                        FaceDialogView.this.mNegativeButton.setVisibility(0);
                        FaceDialogView.this.mTryAgainButton.setVisibility(0);
                        if (!TextUtils.isEmpty(FaceDialogView.this.mSubtitleText.getText())) {
                            FaceDialogView.this.mSubtitleText.setVisibility(0);
                        }
                        if (!TextUtils.isEmpty(FaceDialogView.this.mDescriptionText.getText())) {
                            FaceDialogView.this.mDescriptionText.setVisibility(0);
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        FaceDialogView.this.mSize = 3;
                    }
                });
                as.play(outlineAnimator).with(iconAnimator).with(opacityAnimator).with(textSlideAnimator);
                as.start();
                return;
            } else if (this.mSize == 3) {
                this.mDialog.setClipToOutline(false);
                this.mDialog.invalidateOutline();
                this.mBiometricIcon.setY(this.mIconOriginalY);
                this.mSize = newSize;
                return;
            } else {
                return;
            }
        }
        this.mTitleText.setVisibility(4);
        this.mErrorText.setVisibility(4);
        this.mNegativeButton.setVisibility(4);
        if (!TextUtils.isEmpty(this.mSubtitleText.getText())) {
            this.mSubtitleText.setVisibility(4);
        }
        if (!TextUtils.isEmpty(this.mDescriptionText.getText())) {
            this.mDescriptionText.setVisibility(4);
        }
        this.mBiometricIcon.setY(iconSmallPositionY);
        this.mDialog.setOutlineProvider(this.mOutlineProvider);
        DialogOutlineProvider dialogOutlineProvider = this.mOutlineProvider;
        dialogOutlineProvider.setOutlineY(dialogOutlineProvider.calculateSmall());
        this.mDialog.setClipToOutline(true);
        this.mDialog.invalidateOutline();
        this.mSize = newSize;
        announceAccessibilityEvent();
    }

    public /* synthetic */ void lambda$updateSize$1$FaceDialogView(ValueAnimator animation) {
        float y = ((Float) animation.getAnimatedValue()).floatValue();
        this.mOutlineProvider.setOutlineY(y);
        this.mDialog.invalidateOutline();
    }

    public /* synthetic */ void lambda$updateSize$2$FaceDialogView(ValueAnimator animation) {
        float y = ((Float) animation.getAnimatedValue()).floatValue();
        this.mBiometricIcon.setY(y);
    }

    public /* synthetic */ void lambda$updateSize$3$FaceDialogView(ValueAnimator animation) {
        float y = ((Float) animation.getAnimatedValue()).floatValue();
        this.mErrorText.setTranslationY(y);
    }

    public /* synthetic */ void lambda$updateSize$4$FaceDialogView(ValueAnimator animation) {
        float opacity = ((Float) animation.getAnimatedValue()).floatValue();
        this.mTitleText.setAlpha(opacity);
        this.mErrorText.setAlpha(opacity);
        this.mNegativeButton.setAlpha(opacity);
        this.mTryAgainButton.setAlpha(opacity);
        if (!TextUtils.isEmpty(this.mSubtitleText.getText())) {
            this.mSubtitleText.setAlpha(opacity);
        }
        if (!TextUtils.isEmpty(this.mDescriptionText.getText())) {
            this.mDescriptionText.setAlpha(opacity);
        }
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    public void onSaveState(Bundle bundle) {
        super.onSaveState(bundle);
        bundle.putInt(KEY_DIALOG_SIZE, this.mSize);
        bundle.putBoolean(KEY_DIALOG_ANIMATED_IN, this.mDialogAnimatedIn);
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    protected void handleResetMessage() {
        this.mErrorText.setTextColor(this.mTextColor);
        this.mErrorText.setVisibility(4);
        announceAccessibilityEvent();
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    public void restoreState(Bundle bundle) {
        super.restoreState(bundle);
        this.mSize = bundle.getInt(KEY_DIALOG_SIZE);
        this.mDialogAnimatedIn = bundle.getBoolean(KEY_DIALOG_ANIMATED_IN);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mIconOriginalY == 0.0f) {
            this.mIconOriginalY = this.mBiometricIcon.getY();
        }
        int i = this.mSize;
        if (i != 0) {
            if (i == 1) {
                updateSize(1);
            }
        } else if (!requiresConfirmation()) {
            updateSize(1);
        } else {
            updateSize(3);
        }
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    public void onErrorReceived(String error) {
        super.onErrorReceived(error);
        if (this.mSize == 1) {
            updateSize(3);
        }
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    public void onAuthenticationFailed(String message) {
        super.onAuthenticationFailed(message);
        showTryAgainButton(true);
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    public void showTryAgainButton(boolean show) {
        if (show && this.mSize == 1) {
            updateSize(3);
        } else if (!show) {
            this.mTryAgainButton.setVisibility(8);
            announceAccessibilityEvent();
        } else {
            this.mTryAgainButton.setVisibility(0);
        }
        if (show) {
            this.mPositiveButton.setVisibility(8);
            announceAccessibilityEvent();
        }
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    protected int getHintStringResourceId() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.biometrics.BiometricDialogView
    public int getAuthenticatedAccessibilityResourceId() {
        if (this.mRequireConfirmation) {
            return 17039985;
        }
        return 17039986;
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    protected int getIconDescriptionResourceId() {
        return R.string.accessibility_face_dialog_face_icon;
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    protected void updateIcon(int oldState, int newState) {
        IconController iconController = this.mIconController;
        iconController.mState = newState;
        if (newState == 1) {
            this.mHandler.removeCallbacks(this.mErrorToIdleAnimationRunnable);
            if (this.mDialogAnimatedIn) {
                this.mIconController.startPulsing();
            } else {
                this.mIconController.showIcon(R.drawable.face_dialog_pulse_dark_to_light);
            }
            this.mBiometricIcon.setContentDescription(this.mContext.getString(R.string.biometric_dialog_face_icon_description_authenticating));
        } else if (oldState == 3 && newState == 4) {
            iconController.animateOnce(R.drawable.face_dialog_dark_to_checkmark);
            this.mBiometricIcon.setContentDescription(this.mContext.getString(R.string.biometric_dialog_face_icon_description_confirmed));
        } else if (oldState == 2 && newState == 0) {
            this.mIconController.animateOnce(R.drawable.face_dialog_error_to_idle);
            this.mBiometricIcon.setContentDescription(this.mContext.getString(R.string.biometric_dialog_face_icon_description_idle));
        } else if (oldState == 2 && newState == 4) {
            this.mHandler.removeCallbacks(this.mErrorToIdleAnimationRunnable);
            this.mIconController.animateOnce(R.drawable.face_dialog_dark_to_checkmark);
            this.mBiometricIcon.setContentDescription(this.mContext.getString(R.string.biometric_dialog_face_icon_description_authenticated));
        } else if (newState == 2) {
            if (!this.mHandler.hasCallbacks(this.mErrorToIdleAnimationRunnable)) {
                this.mIconController.animateOnce(R.drawable.face_dialog_dark_to_error);
                this.mHandler.postDelayed(this.mErrorToIdleAnimationRunnable, OsdController.TN.DURATION_TIMEOUT_SHORT);
            }
        } else if (oldState == 1 && newState == 4) {
            this.mIconController.animateOnce(R.drawable.face_dialog_dark_to_checkmark);
            this.mBiometricIcon.setContentDescription(this.mContext.getString(R.string.biometric_dialog_face_icon_description_authenticated));
        } else if (newState == 3) {
            this.mHandler.removeCallbacks(this.mErrorToIdleAnimationRunnable);
            this.mIconController.animateOnce(R.drawable.face_dialog_wink_from_dark);
            this.mBiometricIcon.setContentDescription(this.mContext.getString(R.string.biometric_dialog_face_icon_description_authenticated));
        } else if (newState == 0) {
            this.mIconController.showStatic(R.drawable.face_dialog_idle_static);
            this.mBiometricIcon.setContentDescription(this.mContext.getString(R.string.biometric_dialog_face_icon_description_idle));
        } else {
            Log.w(TAG, "Unknown animation from " + oldState + " -> " + newState);
        }
        if (oldState == 2 && newState == 2) {
            this.mHandler.removeCallbacks(this.mErrorToIdleAnimationRunnable);
            this.mHandler.postDelayed(this.mErrorToIdleAnimationRunnable, OsdController.TN.DURATION_TIMEOUT_SHORT);
        }
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    public void onDialogAnimatedIn() {
        super.onDialogAnimatedIn();
        this.mDialogAnimatedIn = true;
        this.mIconController.startPulsing();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.biometrics.BiometricDialogView
    public int getDelayAfterAuthenticatedDurationMs() {
        return 500;
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    protected boolean shouldGrayAreaDismissDialog() {
        return this.mSize != 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float dpToPixels(float dp) {
        return (this.mContext.getResources().getDisplayMetrics().densityDpi / 160.0f) * dp;
    }

    private float pixelsToDp(float pixels) {
        return pixels / (this.mContext.getResources().getDisplayMetrics().densityDpi / 160.0f);
    }
}
