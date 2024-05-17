package com.android.systemui.biometrics;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class FingerprintDialogView extends BiometricDialogView {
    private static final String TAG = "FingerprintDialogView";

    public FingerprintDialogView(Context context, DialogViewCallback callback) {
        super(context, callback);
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    protected void handleResetMessage() {
        updateState(1);
        this.mErrorText.setText(getHintStringResourceId());
        this.mErrorText.setTextColor(this.mTextColor);
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    protected int getHintStringResourceId() {
        return R.string.fingerprint_dialog_touch_sensor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.biometrics.BiometricDialogView
    public int getAuthenticatedAccessibilityResourceId() {
        return 17040024;
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    protected int getIconDescriptionResourceId() {
        return R.string.accessibility_fingerprint_dialog_fingerprint_icon;
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    protected void updateIcon(int lastState, int newState) {
        AnimatedVectorDrawable animation;
        Drawable icon = getAnimationForTransition(lastState, newState);
        if (icon == null) {
            Log.e(TAG, "Animation not found, " + lastState + " -> " + newState);
            return;
        }
        if (icon instanceof AnimatedVectorDrawable) {
            animation = (AnimatedVectorDrawable) icon;
        } else {
            animation = null;
        }
        this.mBiometricIcon.setImageDrawable(icon);
        if (animation != null && shouldAnimateForTransition(lastState, newState)) {
            animation.forceAnimationOnUI();
            animation.start();
        }
    }

    protected boolean shouldAnimateForTransition(int oldState, int newState) {
        if (newState == 2) {
            return true;
        }
        if (oldState == 2 && newState == 1) {
            return true;
        }
        if (oldState == 1 && newState == 4) {
            return false;
        }
        return (!(oldState == 2 && newState == 4) && newState == 1) ? false : false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.biometrics.BiometricDialogView
    public int getDelayAfterAuthenticatedDurationMs() {
        return 0;
    }

    @Override // com.android.systemui.biometrics.BiometricDialogView
    protected boolean shouldGrayAreaDismissDialog() {
        return true;
    }

    protected Drawable getAnimationForTransition(int oldState, int newState) {
        int iconRes;
        if (newState != 2) {
            if (oldState != 2 || newState != 1) {
                if (oldState == 1 && newState == 4) {
                    iconRes = R.drawable.fingerprint_dialog_fp_to_error;
                } else if (oldState == 2 && newState == 4) {
                    iconRes = R.drawable.fingerprint_dialog_fp_to_error;
                } else if (newState == 1) {
                    iconRes = R.drawable.fingerprint_dialog_fp_to_error;
                } else {
                    return null;
                }
            } else {
                iconRes = R.drawable.fingerprint_dialog_error_to_fp;
            }
        } else {
            iconRes = R.drawable.fingerprint_dialog_fp_to_error;
        }
        return this.mContext.getDrawable(iconRes);
    }
}
