package com.android.keyguard;

import android.content.Context;
import android.telephony.SubscriptionManager;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.widget.LockPatternUtils;
/* loaded from: classes19.dex */
public class KeyguardSecurityModel {
    private final Context mContext;
    private final boolean mIsPukScreenAvailable;
    private LockPatternUtils mLockPatternUtils;

    /* loaded from: classes19.dex */
    public enum SecurityMode {
        Invalid,
        None,
        Pattern,
        Password,
        PIN,
        SimPin,
        SimPuk
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public KeyguardSecurityModel(Context context) {
        this.mContext = context;
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mIsPukScreenAvailable = this.mContext.getResources().getBoolean(17891456);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLockPatternUtils(LockPatternUtils utils) {
        this.mLockPatternUtils = utils;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SecurityMode getSecurityMode(int userId) {
        KeyguardUpdateMonitor monitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        if (this.mIsPukScreenAvailable && SubscriptionManager.isValidSubscriptionId(monitor.getNextSubIdForState(IccCardConstants.State.PUK_REQUIRED))) {
            return SecurityMode.SimPuk;
        }
        if (SubscriptionManager.isValidSubscriptionId(monitor.getNextSubIdForState(IccCardConstants.State.PIN_REQUIRED))) {
            return SecurityMode.SimPin;
        }
        int security = this.mLockPatternUtils.getActivePasswordQuality(userId);
        if (security != 0) {
            if (security != 65536) {
                if (security == 131072 || security == 196608) {
                    return SecurityMode.PIN;
                }
                if (security == 262144 || security == 327680 || security == 393216 || security == 524288) {
                    return SecurityMode.Password;
                }
                throw new IllegalStateException("Unknown security quality:" + security);
            }
            return SecurityMode.Pattern;
        }
        return SecurityMode.None;
    }
}
