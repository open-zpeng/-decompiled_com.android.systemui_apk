package com.android.keyguard;

import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.util.EmergencyAffordanceManager;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.util.EmergencyDialerConstants;
/* loaded from: classes19.dex */
public class EmergencyButton extends Button {
    private static final Intent INTENT_EMERGENCY_DIAL = new Intent().setAction(EmergencyDialerConstants.ACTION_DIAL).setPackage("com.android.phone").setFlags(343932928).putExtra(EmergencyDialerConstants.EXTRA_ENTRY_TYPE, 1);
    private static final String LOG_TAG = "EmergencyButton";
    private int mDownX;
    private int mDownY;
    private final EmergencyAffordanceManager mEmergencyAffordanceManager;
    private EmergencyButtonCallback mEmergencyButtonCallback;
    private final boolean mEnableEmergencyCallWhileSimLocked;
    KeyguardUpdateMonitorCallback mInfoCallback;
    private final boolean mIsVoiceCapable;
    private LockPatternUtils mLockPatternUtils;
    private boolean mLongPressWasDragged;
    private PowerManager mPowerManager;

    /* loaded from: classes19.dex */
    public interface EmergencyButtonCallback {
        void onEmergencyButtonClickedWhenInCall();
    }

    public EmergencyButton(Context context) {
        this(context, null);
    }

    public EmergencyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInfoCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.keyguard.EmergencyButton.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onSimStateChanged(int subId, int slotId, IccCardConstants.State simState) {
                EmergencyButton.this.updateEmergencyCallButton();
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onPhoneStateChanged(int phoneState) {
                EmergencyButton.this.updateEmergencyCallButton();
            }
        };
        this.mIsVoiceCapable = context.getResources().getBoolean(17891575);
        this.mEnableEmergencyCallWhileSimLocked = this.mContext.getResources().getBoolean(17891455);
        this.mEmergencyAffordanceManager = new EmergencyAffordanceManager(context);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyguardUpdateMonitor.getInstance(this.mContext).registerCallback(this.mInfoCallback);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KeyguardUpdateMonitor.getInstance(this.mContext).removeCallback(this.mInfoCallback);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        setOnClickListener(new View.OnClickListener() { // from class: com.android.keyguard.EmergencyButton.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                EmergencyButton.this.takeEmergencyCallAction();
            }
        });
        setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.keyguard.EmergencyButton.3
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                if (!EmergencyButton.this.mLongPressWasDragged && EmergencyButton.this.mEmergencyAffordanceManager.needsEmergencyAffordance()) {
                    EmergencyButton.this.mEmergencyAffordanceManager.performEmergencyCall();
                    return true;
                }
                return false;
            }
        });
        updateEmergencyCallButton();
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getActionMasked() == 0) {
            this.mDownX = x;
            this.mDownY = y;
            this.mLongPressWasDragged = false;
        } else {
            int xDiff = Math.abs(x - this.mDownX);
            int yDiff = Math.abs(y - this.mDownY);
            int touchSlop = ViewConfiguration.get(this.mContext).getScaledTouchSlop();
            if (Math.abs(yDiff) > touchSlop || Math.abs(xDiff) > touchSlop) {
                this.mLongPressWasDragged = true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean performLongClick() {
        return super.performLongClick();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateEmergencyCallButton();
    }

    public void takeEmergencyCallAction() {
        MetricsLogger.action(this.mContext, 200);
        this.mPowerManager.userActivity(SystemClock.uptimeMillis(), true);
        try {
            ActivityTaskManager.getService().stopSystemLockTaskMode();
        } catch (RemoteException e) {
            Slog.w(LOG_TAG, "Failed to stop app pinning");
        }
        if (isInCall()) {
            resumeCall();
            EmergencyButtonCallback emergencyButtonCallback = this.mEmergencyButtonCallback;
            if (emergencyButtonCallback != null) {
                emergencyButtonCallback.onEmergencyButtonClickedWhenInCall();
                return;
            }
            return;
        }
        KeyguardUpdateMonitor.getInstance(this.mContext).reportEmergencyCallAction(true);
        getContext().startActivityAsUser(INTENT_EMERGENCY_DIAL, ActivityOptions.makeCustomAnimation(getContext(), 0, 0).toBundle(), new UserHandle(KeyguardUpdateMonitor.getCurrentUser()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEmergencyCallButton() {
        int textId;
        boolean visible = false;
        if (this.mIsVoiceCapable) {
            if (isInCall()) {
                visible = true;
            } else {
                boolean simLocked = KeyguardUpdateMonitor.getInstance(this.mContext).isSimPinVoiceSecure();
                if (simLocked) {
                    visible = this.mEnableEmergencyCallWhileSimLocked;
                } else {
                    visible = this.mLockPatternUtils.isSecure(KeyguardUpdateMonitor.getCurrentUser());
                }
            }
        }
        if (visible) {
            setVisibility(0);
            if (isInCall()) {
                textId = 17040285;
            } else {
                textId = 17040258;
            }
            setText(textId);
            return;
        }
        setVisibility(8);
    }

    public void setCallback(EmergencyButtonCallback callback) {
        this.mEmergencyButtonCallback = callback;
    }

    private void resumeCall() {
        getTelecommManager().showInCallScreen(false);
    }

    private boolean isInCall() {
        return getTelecommManager().isInCall();
    }

    private TelecomManager getTelecommManager() {
        return (TelecomManager) this.mContext.getSystemService("telecom");
    }
}
