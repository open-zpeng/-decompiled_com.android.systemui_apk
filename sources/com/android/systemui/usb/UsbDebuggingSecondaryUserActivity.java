package com.android.systemui.usb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class UsbDebuggingSecondaryUserActivity extends AlertActivity implements DialogInterface.OnClickListener {
    private UsbDisconnectedReceiver mDisconnectedReceiver;

    /* JADX WARN: Multi-variable type inference failed */
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (SystemProperties.getInt("service.adb.tcp.port", 0) == 0) {
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver(this);
        }
        AlertController.AlertParams ap = this.mAlertParams;
        ap.mTitle = getString(R.string.usb_debugging_secondary_user_title);
        ap.mMessage = getString(R.string.usb_debugging_secondary_user_message);
        ap.mPositiveButtonText = getString(17039370);
        ap.mPositiveButtonListener = this;
        setupAlert();
    }

    /* loaded from: classes21.dex */
    private class UsbDisconnectedReceiver extends BroadcastReceiver {
        private final Activity mActivity;

        public UsbDisconnectedReceiver(Activity activity) {
            this.mActivity = activity;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context content, Intent intent) {
            String action = intent.getAction();
            if ("android.hardware.usb.action.USB_STATE".equals(action)) {
                boolean connected = intent.getBooleanExtra("connected", false);
                if (!connected) {
                    this.mActivity.finish();
                }
            }
        }
    }

    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("android.hardware.usb.action.USB_STATE");
        registerReceiver(this.mDisconnectedReceiver, filter);
    }

    protected void onStop() {
        UsbDisconnectedReceiver usbDisconnectedReceiver = this.mDisconnectedReceiver;
        if (usbDisconnectedReceiver != null) {
            unregisterReceiver(usbDisconnectedReceiver);
        }
        super.onStop();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        finish();
    }
}
