package com.android.keyguard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.systemui.R;
import com.android.systemui.statusbar.policy.ConfigurationController;
import java.lang.ref.WeakReference;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes19.dex */
public class KeyguardMessageArea extends TextView implements SecurityMessageDisplay, ConfigurationController.ConfigurationListener {
    private static final long ANNOUNCEMENT_DELAY = 250;
    private static final Object ANNOUNCE_TOKEN = new Object();
    private static final int DEFAULT_COLOR = -1;
    private boolean mBouncerVisible;
    private final ConfigurationController mConfigurationController;
    private ColorStateList mDefaultColorState;
    private final Handler mHandler;
    private KeyguardUpdateMonitorCallback mInfoCallback;
    private CharSequence mMessage;
    private ColorStateList mNextMessageColorState;

    public KeyguardMessageArea(Context context) {
        super(context, null);
        this.mNextMessageColorState = ColorStateList.valueOf(-1);
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.keyguard.KeyguardMessageArea.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onFinishedGoingToSleep(int why) {
                KeyguardMessageArea.this.setSelected(false);
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onStartedWakingUp() {
                KeyguardMessageArea.this.setSelected(true);
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onKeyguardBouncerChanged(boolean bouncer) {
                KeyguardMessageArea.this.mBouncerVisible = bouncer;
                KeyguardMessageArea.this.update();
            }
        };
        throw new IllegalStateException("This constructor should never be invoked");
    }

    @Inject
    public KeyguardMessageArea(@Named("view_context") Context context, AttributeSet attrs, ConfigurationController configurationController) {
        this(context, attrs, KeyguardUpdateMonitor.getInstance(context), configurationController);
    }

    public KeyguardMessageArea(Context context, AttributeSet attrs, KeyguardUpdateMonitor monitor, ConfigurationController configurationController) {
        super(context, attrs);
        this.mNextMessageColorState = ColorStateList.valueOf(-1);
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.keyguard.KeyguardMessageArea.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onFinishedGoingToSleep(int why) {
                KeyguardMessageArea.this.setSelected(false);
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onStartedWakingUp() {
                KeyguardMessageArea.this.setSelected(true);
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onKeyguardBouncerChanged(boolean bouncer) {
                KeyguardMessageArea.this.mBouncerVisible = bouncer;
                KeyguardMessageArea.this.update();
            }
        };
        setLayerType(2, null);
        monitor.registerCallback(this.mInfoCallback);
        this.mHandler = new Handler(Looper.myLooper());
        this.mConfigurationController = configurationController;
        onThemeChanged();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mConfigurationController.addCallback(this);
        onThemeChanged();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mConfigurationController.removeCallback(this);
    }

    @Override // com.android.keyguard.SecurityMessageDisplay
    public void setNextMessageColor(ColorStateList colorState) {
        this.mNextMessageColorState = colorState;
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onThemeChanged() {
        TypedArray array = this.mContext.obtainStyledAttributes(new int[]{R.attr.wallpaperTextColor});
        ColorStateList newTextColors = ColorStateList.valueOf(array.getColor(0, -65536));
        array.recycle();
        this.mDefaultColorState = newTextColors;
        update();
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onDensityOrFontScaleChanged() {
        TypedArray array = this.mContext.obtainStyledAttributes(R.style.Keyguard_TextView, new int[]{16842901});
        setTextSize(0, array.getDimensionPixelSize(0, 0));
        array.recycle();
    }

    @Override // com.android.keyguard.SecurityMessageDisplay
    public void setMessage(CharSequence msg) {
        if (!TextUtils.isEmpty(msg)) {
            securityMessageChanged(msg);
        } else {
            clearMessage();
        }
    }

    @Override // com.android.keyguard.SecurityMessageDisplay
    public void setMessage(int resId) {
        CharSequence message = null;
        if (resId != 0) {
            message = getContext().getResources().getText(resId);
        }
        setMessage(message);
    }

    @Override // com.android.keyguard.SecurityMessageDisplay
    public void formatMessage(int resId, Object... formatArgs) {
        CharSequence message = null;
        if (resId != 0) {
            message = getContext().getString(resId, formatArgs);
        }
        setMessage(message);
    }

    public static KeyguardMessageArea findSecurityMessageDisplay(View v) {
        KeyguardMessageArea messageArea = (KeyguardMessageArea) v.findViewById(R.id.keyguard_message_area);
        if (messageArea == null) {
            messageArea = (KeyguardMessageArea) v.getRootView().findViewById(R.id.keyguard_message_area);
        }
        if (messageArea == null) {
            throw new RuntimeException("Can't find keyguard_message_area in " + v.getClass());
        }
        return messageArea;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        boolean shouldMarquee = KeyguardUpdateMonitor.getInstance(this.mContext).isDeviceInteractive();
        setSelected(shouldMarquee);
    }

    private void securityMessageChanged(CharSequence message) {
        this.mMessage = message;
        update();
        this.mHandler.removeCallbacksAndMessages(ANNOUNCE_TOKEN);
        this.mHandler.postAtTime(new AnnounceRunnable(this, getText()), ANNOUNCE_TOKEN, SystemClock.uptimeMillis() + ANNOUNCEMENT_DELAY);
    }

    private void clearMessage() {
        this.mMessage = null;
        update();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        CharSequence status = this.mMessage;
        setVisibility((TextUtils.isEmpty(status) || !this.mBouncerVisible) ? 4 : 0);
        setText(status);
        ColorStateList colorState = this.mDefaultColorState;
        if (this.mNextMessageColorState.getDefaultColor() != -1) {
            colorState = this.mNextMessageColorState;
            this.mNextMessageColorState = ColorStateList.valueOf(-1);
        }
        setTextColor(colorState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes19.dex */
    public static class AnnounceRunnable implements Runnable {
        private final WeakReference<View> mHost;
        private final CharSequence mTextToAnnounce;

        AnnounceRunnable(View host, CharSequence textToAnnounce) {
            this.mHost = new WeakReference<>(host);
            this.mTextToAnnounce = textToAnnounce;
        }

        @Override // java.lang.Runnable
        public void run() {
            View host = this.mHost.get();
            if (host != null) {
                host.announceForAccessibility(this.mTextToAnnounce);
            }
        }
    }
}
