package com.android.keyguard;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardSecurityContainer;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.settingslib.Utils;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.xiaopeng.speech.speechwidget.ListWidget;
import java.io.File;
/* loaded from: classes19.dex */
public class KeyguardHostView extends FrameLayout implements KeyguardSecurityContainer.SecurityCallback {
    public static final boolean DEBUG = false;
    private static final String ENABLE_MENU_KEY_FILE = "/data/local/enable_menu_key";
    private static final boolean KEYGUARD_MANAGES_VOLUME = false;
    private static final String TAG = "KeyguardViewBase";
    private AudioManager mAudioManager;
    private Runnable mCancelAction;
    private ActivityStarter.OnDismissAction mDismissAction;
    protected LockPatternUtils mLockPatternUtils;
    private KeyguardSecurityContainer mSecurityContainer;
    private TelephonyManager mTelephonyManager;
    private final KeyguardUpdateMonitorCallback mUpdateCallback;
    protected ViewMediatorCallback mViewMediatorCallback;

    public KeyguardHostView(Context context) {
        this(context, null);
    }

    public KeyguardHostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTelephonyManager = null;
        this.mUpdateCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.keyguard.KeyguardHostView.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onUserSwitchComplete(int userId) {
                KeyguardHostView.this.getSecurityContainer().showPrimarySecurityScreen(false);
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onTrustGrantedWithFlags(int flags, int userId) {
                if (userId == KeyguardUpdateMonitor.getCurrentUser() && KeyguardHostView.this.isAttachedToWindow()) {
                    boolean bouncerVisible = KeyguardHostView.this.isVisibleToUser();
                    boolean initiatedByUser = (flags & 1) != 0;
                    boolean dismissKeyguard = (flags & 2) != 0;
                    if (initiatedByUser || dismissKeyguard) {
                        if (KeyguardHostView.this.mViewMediatorCallback.isScreenOn() && (bouncerVisible || dismissKeyguard)) {
                            if (!bouncerVisible) {
                                Log.i(KeyguardHostView.TAG, "TrustAgent dismissed Keyguard.");
                            }
                            KeyguardHostView.this.dismiss(false, userId);
                            return;
                        }
                        KeyguardHostView.this.mViewMediatorCallback.playTrustedSound();
                    }
                }
            }
        };
        KeyguardUpdateMonitor.getInstance(context).registerCallback(this.mUpdateCallback);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        ViewMediatorCallback viewMediatorCallback = this.mViewMediatorCallback;
        if (viewMediatorCallback != null) {
            viewMediatorCallback.keyguardDoneDrawing();
        }
    }

    public void setOnDismissAction(ActivityStarter.OnDismissAction action, Runnable cancelAction) {
        Runnable runnable = this.mCancelAction;
        if (runnable != null) {
            runnable.run();
            this.mCancelAction = null;
        }
        this.mDismissAction = action;
        this.mCancelAction = cancelAction;
    }

    public boolean hasDismissActions() {
        return (this.mDismissAction == null && this.mCancelAction == null) ? false : true;
    }

    public void cancelDismissAction() {
        setOnDismissAction(null, null);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        this.mSecurityContainer = (KeyguardSecurityContainer) findViewById(R.id.keyguard_security_container);
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mSecurityContainer.setLockPatternUtils(this.mLockPatternUtils);
        this.mSecurityContainer.setSecurityCallback(this);
        this.mSecurityContainer.showPrimarySecurityScreen(false);
    }

    public void showPrimarySecurityScreen() {
        this.mSecurityContainer.showPrimarySecurityScreen(false);
    }

    public KeyguardSecurityView getCurrentSecurityView() {
        KeyguardSecurityContainer keyguardSecurityContainer = this.mSecurityContainer;
        if (keyguardSecurityContainer != null) {
            return keyguardSecurityContainer.getCurrentSecurityView();
        }
        return null;
    }

    public void showPromptReason(int reason) {
        this.mSecurityContainer.showPromptReason(reason);
    }

    public void showMessage(CharSequence message, ColorStateList colorState) {
        this.mSecurityContainer.showMessage(message, colorState);
    }

    public void showErrorMessage(CharSequence message) {
        showMessage(message, Utils.getColorError(this.mContext));
    }

    public boolean dismiss(int targetUserId) {
        return dismiss(false, targetUserId);
    }

    public boolean handleBackKey() {
        if (this.mSecurityContainer.getCurrentSecuritySelection() != KeyguardSecurityModel.SecurityMode.None) {
            this.mSecurityContainer.dismiss(false, KeyguardUpdateMonitor.getCurrentUser());
            return true;
        }
        return false;
    }

    protected KeyguardSecurityContainer getSecurityContainer() {
        return this.mSecurityContainer;
    }

    @Override // com.android.keyguard.KeyguardSecurityContainer.SecurityCallback
    public boolean dismiss(boolean authenticated, int targetUserId) {
        return this.mSecurityContainer.showNextSecurityScreenOrFinish(authenticated, targetUserId);
    }

    @Override // com.android.keyguard.KeyguardSecurityContainer.SecurityCallback
    public void finish(boolean strongAuth, int targetUserId) {
        boolean deferKeyguardDone = false;
        ActivityStarter.OnDismissAction onDismissAction = this.mDismissAction;
        if (onDismissAction != null) {
            deferKeyguardDone = onDismissAction.onDismiss();
            this.mDismissAction = null;
            this.mCancelAction = null;
        }
        ViewMediatorCallback viewMediatorCallback = this.mViewMediatorCallback;
        if (viewMediatorCallback != null) {
            if (deferKeyguardDone) {
                viewMediatorCallback.keyguardDonePending(strongAuth, targetUserId);
            } else {
                viewMediatorCallback.keyguardDone(strongAuth, targetUserId);
            }
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityContainer.SecurityCallback
    public void reset() {
        this.mViewMediatorCallback.resetKeyguard();
    }

    @Override // com.android.keyguard.KeyguardSecurityContainer.SecurityCallback
    public void onCancelClicked() {
        this.mViewMediatorCallback.onCancelClicked();
    }

    public void resetSecurityContainer() {
        this.mSecurityContainer.reset();
    }

    @Override // com.android.keyguard.KeyguardSecurityContainer.SecurityCallback
    public void onSecurityModeChanged(KeyguardSecurityModel.SecurityMode securityMode, boolean needsInput) {
        ViewMediatorCallback viewMediatorCallback = this.mViewMediatorCallback;
        if (viewMediatorCallback != null) {
            viewMediatorCallback.setNeedsInput(needsInput);
        }
    }

    public CharSequence getAccessibilityTitleForCurrentMode() {
        return this.mSecurityContainer.getTitle();
    }

    @Override // com.android.keyguard.KeyguardSecurityContainer.SecurityCallback
    public void userActivity() {
        ViewMediatorCallback viewMediatorCallback = this.mViewMediatorCallback;
        if (viewMediatorCallback != null) {
            viewMediatorCallback.userActivity();
        }
    }

    public void onPause() {
        this.mSecurityContainer.showPrimarySecurityScreen(true);
        this.mSecurityContainer.onPause();
        clearFocus();
    }

    public void onResume() {
        this.mSecurityContainer.onResume(1);
        requestFocus();
    }

    public void startAppearAnimation() {
        this.mSecurityContainer.startAppearAnimation();
    }

    public void startDisappearAnimation(Runnable finishRunnable) {
        if (!this.mSecurityContainer.startDisappearAnimation(finishRunnable) && finishRunnable != null) {
            finishRunnable.run();
        }
    }

    public void cleanUp() {
        getSecurityContainer().onPause();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (interceptMediaKey(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean interceptMediaKey(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == 0) {
            if (keyCode != 24 && keyCode != 25) {
                if (keyCode != 79 && keyCode != 130) {
                    if (keyCode != 164) {
                        if (keyCode != 222) {
                            if (keyCode != 126 && keyCode != 127) {
                                switch (keyCode) {
                                }
                            }
                            if (this.mTelephonyManager == null) {
                                this.mTelephonyManager = (TelephonyManager) getContext().getSystemService("phone");
                            }
                            TelephonyManager telephonyManager = this.mTelephonyManager;
                            if (telephonyManager != null && telephonyManager.getCallState() != 0) {
                                return true;
                            }
                        }
                    }
                }
                handleMediaKeyEvent(event);
                return true;
            }
            return false;
        } else if (event.getAction() == 1) {
            if (keyCode != 79 && keyCode != 130 && keyCode != 222 && keyCode != 126 && keyCode != 127) {
                switch (keyCode) {
                }
            }
            handleMediaKeyEvent(event);
            return true;
        }
        return false;
    }

    private void handleMediaKeyEvent(KeyEvent keyEvent) {
        synchronized (this) {
            if (this.mAudioManager == null) {
                this.mAudioManager = (AudioManager) getContext().getSystemService(ListWidget.EXTRA_TYPE_AUDIO);
            }
        }
        this.mAudioManager.dispatchMediaKeyEvent(keyEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSystemUiVisibilityChanged(int visibility) {
        super.dispatchSystemUiVisibilityChanged(visibility);
        if (!(this.mContext instanceof Activity)) {
            setSystemUiVisibility(4194304);
        }
    }

    public boolean shouldEnableMenuKey() {
        Resources res = getResources();
        boolean configDisabled = res.getBoolean(R.bool.config_disableMenuKeyInLockScreen);
        boolean isTestHarness = ActivityManager.isRunningInTestHarness();
        boolean fileOverride = new File(ENABLE_MENU_KEY_FILE).exists();
        return !configDisabled || isTestHarness || fileOverride;
    }

    public void setViewMediatorCallback(ViewMediatorCallback viewMediatorCallback) {
        this.mViewMediatorCallback = viewMediatorCallback;
        this.mViewMediatorCallback.setNeedsInput(this.mSecurityContainer.needsInput());
    }

    public void setLockPatternUtils(LockPatternUtils utils) {
        this.mLockPatternUtils = utils;
        this.mSecurityContainer.setLockPatternUtils(utils);
    }

    public KeyguardSecurityModel.SecurityMode getSecurityMode() {
        return this.mSecurityContainer.getSecurityMode();
    }

    public KeyguardSecurityModel.SecurityMode getCurrentSecurityMode() {
        return this.mSecurityContainer.getCurrentSecurityMode();
    }
}
