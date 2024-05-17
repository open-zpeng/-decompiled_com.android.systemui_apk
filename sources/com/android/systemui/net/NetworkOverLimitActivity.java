package com.android.systemui.net;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.INetworkPolicyManager;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class NetworkOverLimitActivity extends Activity {
    private static final String TAG = "NetworkOverLimitActivity";

    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        final NetworkTemplate template = getIntent().getParcelableExtra("android.net.NETWORK_TEMPLATE");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getLimitedDialogTitleForTemplate(template));
        builder.setMessage(R.string.data_usage_disabled_dialog);
        builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        builder.setNegativeButton(R.string.data_usage_disabled_dialog_enable, new DialogInterface.OnClickListener() { // from class: com.android.systemui.net.NetworkOverLimitActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                NetworkOverLimitActivity.this.snoozePolicy(template);
            }
        });
        Dialog dialog = builder.create();
        dialog.getWindow().setType(2003);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.systemui.net.NetworkOverLimitActivity.2
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialog2) {
                NetworkOverLimitActivity.this.finish();
            }
        });
        dialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void snoozePolicy(NetworkTemplate template) {
        INetworkPolicyManager policyService = INetworkPolicyManager.Stub.asInterface(ServiceManager.getService("netpolicy"));
        try {
            policyService.snoozeLimit(template);
        } catch (RemoteException e) {
            Log.w(TAG, "problem snoozing network policy", e);
        }
    }

    private static int getLimitedDialogTitleForTemplate(NetworkTemplate template) {
        if (template.getMatchRule() == 1) {
            return R.string.data_usage_disabled_dialog_mobile_title;
        }
        return R.string.data_usage_disabled_dialog_title;
    }
}
