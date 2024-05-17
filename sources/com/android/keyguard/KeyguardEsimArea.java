package com.android.keyguard;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.euicc.EuiccManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.android.systemui.R;
import com.xiaopeng.systemui.controller.CarController;
/* loaded from: classes19.dex */
class KeyguardEsimArea extends Button implements View.OnClickListener {
    private static final String ACTION_DISABLE_ESIM = "com.android.keyguard.disable_esim";
    private static final String PERMISSION_SELF = "com.android.systemui.permission.SELF";
    private static final String TAG = "KeyguardEsimArea";
    private EuiccManager mEuiccManager;
    private BroadcastReceiver mReceiver;

    public KeyguardEsimArea(Context context) {
        this(context, null);
    }

    public KeyguardEsimArea(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyguardEsimArea(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 16974425);
    }

    public KeyguardEsimArea(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.keyguard.KeyguardEsimArea.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int resultCode;
                if (KeyguardEsimArea.ACTION_DISABLE_ESIM.equals(intent.getAction()) && (resultCode = getResultCode()) != 0) {
                    Log.e(KeyguardEsimArea.TAG, "Error disabling esim, result code = " + resultCode);
                    AlertDialog.Builder builder = new AlertDialog.Builder(KeyguardEsimArea.this.mContext).setMessage(R.string.error_disable_esim_msg).setTitle(R.string.error_disable_esim_title).setCancelable(false).setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().setType(CarController.TYPE_CAR_CLTC_DRIVE_DISTANCE);
                    alertDialog.show();
                }
            }
        };
        this.mEuiccManager = (EuiccManager) context.getSystemService("euicc");
        setOnClickListener(this);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter(ACTION_DISABLE_ESIM), "com.android.systemui.permission.SELF", null);
    }

    public static boolean isEsimLocked(Context context, int subId) {
        SubscriptionInfo sub;
        EuiccManager euiccManager = (EuiccManager) context.getSystemService("euicc");
        return euiccManager.isEnabled() && (sub = SubscriptionManager.from(context).getActiveSubscriptionInfo(subId)) != null && sub.isEmbedded();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        this.mContext.unregisterReceiver(this.mReceiver);
        super.onDetachedFromWindow();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        Intent intent = new Intent(ACTION_DISABLE_ESIM);
        intent.setPackage(this.mContext.getPackageName());
        PendingIntent callbackIntent = PendingIntent.getBroadcastAsUser(this.mContext, 0, intent, 134217728, UserHandle.SYSTEM);
        this.mEuiccManager.switchToSubscription(-1, callbackIntent);
    }
}
