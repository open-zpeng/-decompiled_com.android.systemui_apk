package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.internal.telephony.IccCardConstants;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import java.util.List;
/* loaded from: classes21.dex */
public class EmergencyCryptkeeperText extends TextView {
    private final KeyguardUpdateMonitorCallback mCallback;
    private KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final BroadcastReceiver mReceiver;

    public EmergencyCryptkeeperText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.statusbar.policy.EmergencyCryptkeeperText.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onPhoneStateChanged(int phoneState) {
                EmergencyCryptkeeperText.this.update();
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onRefreshCarrierInfo() {
                EmergencyCryptkeeperText.this.update();
            }
        };
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.policy.EmergencyCryptkeeperText.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                    EmergencyCryptkeeperText.this.update();
                }
            }
        };
        setVisibility(8);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        this.mKeyguardUpdateMonitor.registerCallback(this.mCallback);
        getContext().registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.AIRPLANE_MODE"));
        update();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KeyguardUpdateMonitor keyguardUpdateMonitor = this.mKeyguardUpdateMonitor;
        if (keyguardUpdateMonitor != null) {
            keyguardUpdateMonitor.removeCallback(this.mCallback);
        }
        getContext().unregisterReceiver(this.mReceiver);
    }

    public void update() {
        int i = 0;
        boolean hasMobile = ConnectivityManager.from(this.mContext).isNetworkSupported(0);
        boolean airplaneMode = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
        if (!hasMobile || airplaneMode) {
            setText((CharSequence) null);
            setVisibility(8);
            return;
        }
        boolean allSimsMissing = true;
        CharSequence displayText = null;
        List<SubscriptionInfo> subs = this.mKeyguardUpdateMonitor.getFilteredSubscriptionInfo(false);
        int N = subs.size();
        for (int i2 = 0; i2 < N; i2++) {
            int subId = subs.get(i2).getSubscriptionId();
            IccCardConstants.State simState = this.mKeyguardUpdateMonitor.getSimState(subId);
            CharSequence carrierName = subs.get(i2).getCarrierName();
            if (simState.iccCardExist() && !TextUtils.isEmpty(carrierName)) {
                allSimsMissing = false;
                displayText = carrierName;
            }
        }
        if (allSimsMissing) {
            if (N != 0) {
                displayText = subs.get(0).getCarrierName();
            } else {
                displayText = getContext().getText(17039907);
                Intent i3 = getContext().registerReceiver(null, new IntentFilter("android.provider.Telephony.SPN_STRINGS_UPDATED"));
                if (i3 != null) {
                    displayText = i3.getStringExtra("plmn");
                }
            }
        }
        setText(displayText);
        if (TextUtils.isEmpty(displayText)) {
            i = 8;
        }
        setVisibility(i);
    }
}
