package com.android.systemui.statusbar;

import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.internal.telephony.IccCardConstants;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.settingslib.WirelessUtils;
import com.android.systemui.DemoMode;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.tuner.TunerService;
import java.util.List;
/* loaded from: classes21.dex */
public class OperatorNameView extends TextView implements DemoMode, DarkIconDispatcher.DarkReceiver, NetworkController.SignalCallback, TunerService.Tunable {
    private static final String KEY_SHOW_OPERATOR_NAME = "show_operator_name";
    private final KeyguardUpdateMonitorCallback mCallback;
    private boolean mDemoMode;
    private KeyguardUpdateMonitor mKeyguardUpdateMonitor;

    public OperatorNameView(Context context) {
        this(context, null);
    }

    public OperatorNameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OperatorNameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.statusbar.OperatorNameView.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onRefreshCarrierInfo() {
                OperatorNameView.this.updateText();
            }
        };
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
        this.mKeyguardUpdateMonitor.registerCallback(this.mCallback);
        ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class)).addDarkReceiver(this);
        ((NetworkController) Dependency.get(NetworkController.class)).addCallback((NetworkController.SignalCallback) this);
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, KEY_SHOW_OPERATOR_NAME);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyguardUpdateMonitor.removeCallback(this.mCallback);
        ((DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class)).removeDarkReceiver(this);
        ((NetworkController) Dependency.get(NetworkController.class)).removeCallback((NetworkController.SignalCallback) this);
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver
    public void onDarkChanged(Rect area, float darkIntensity, int tint) {
        setTextColor(DarkIconDispatcher.getTint(area, this, tint));
    }

    @Override // com.android.systemui.statusbar.policy.NetworkController.SignalCallback
    public void setIsAirplaneMode(NetworkController.IconState icon) {
        update();
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        update();
    }

    @Override // com.android.systemui.DemoMode
    public void dispatchDemoCommand(String command, Bundle args) {
        if (!this.mDemoMode && command.equals("enter")) {
            this.mDemoMode = true;
        } else if (this.mDemoMode && command.equals(DemoMode.COMMAND_EXIT)) {
            this.mDemoMode = false;
            update();
        } else if (this.mDemoMode && command.equals(DemoMode.COMMAND_OPERATOR)) {
            setText(args.getString("name"));
        }
    }

    private void update() {
        boolean showOperatorName = ((TunerService) Dependency.get(TunerService.class)).getValue(KEY_SHOW_OPERATOR_NAME, 1) != 0;
        setVisibility(showOperatorName ? 0 : 8);
        boolean hasMobile = ConnectivityManager.from(this.mContext).isNetworkSupported(0);
        boolean airplaneMode = WirelessUtils.isAirplaneModeOn(this.mContext);
        if (!hasMobile || airplaneMode) {
            setText((CharSequence) null);
            setVisibility(8);
        } else if (!this.mDemoMode) {
            updateText();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateText() {
        ServiceState ss;
        CharSequence displayText = null;
        List<SubscriptionInfo> subs = this.mKeyguardUpdateMonitor.getFilteredSubscriptionInfo(false);
        int N = subs.size();
        int i = 0;
        while (true) {
            if (i >= N) {
                break;
            }
            int subId = subs.get(i).getSubscriptionId();
            IccCardConstants.State simState = this.mKeyguardUpdateMonitor.getSimState(subId);
            CharSequence carrierName = subs.get(i).getCarrierName();
            if (TextUtils.isEmpty(carrierName) || simState != IccCardConstants.State.READY || (ss = this.mKeyguardUpdateMonitor.getServiceState(subId)) == null || ss.getState() != 0) {
                i++;
            } else {
                displayText = carrierName;
                break;
            }
        }
        setText(displayText);
    }
}
