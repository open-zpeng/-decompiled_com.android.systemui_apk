package com.android.systemui.usb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.debug.IAdbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class UsbDebuggingActivity extends AlertActivity implements DialogInterface.OnClickListener {
    private static final String TAG = "UsbDebuggingActivity";
    private CheckBox mAlwaysAllow;
    private UsbDisconnectedReceiver mDisconnectedReceiver;
    private String mKey;

    /* JADX WARN: Multi-variable type inference failed */
    public void onCreate(Bundle icicle) {
        Window window = getWindow();
        window.addSystemFlags(524288);
        window.setType(2008);
        super.onCreate(icicle);
        if (SystemProperties.getInt("service.adb.tcp.port", 0) == 0) {
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver(this);
        }
        Intent intent = getIntent();
        String fingerprints = intent.getStringExtra("fingerprints");
        this.mKey = intent.getStringExtra("key");
        if (fingerprints == null || this.mKey == null) {
            finish();
            return;
        }
        AlertController.AlertParams ap = this.mAlertParams;
        ap.mTitle = getString(R.string.usb_debugging_title);
        ap.mMessage = getString(R.string.usb_debugging_message, new Object[]{fingerprints});
        ap.mPositiveButtonText = getString(R.string.usb_debugging_allow);
        ap.mNegativeButtonText = getString(17039360);
        ap.mPositiveButtonListener = this;
        ap.mNegativeButtonListener = this;
        LayoutInflater inflater = LayoutInflater.from(ap.mContext);
        View checkbox = inflater.inflate(17367090, (ViewGroup) null);
        this.mAlwaysAllow = (CheckBox) checkbox.findViewById(16908821);
        this.mAlwaysAllow.setText(getString(R.string.usb_debugging_always));
        ap.mView = checkbox;
        setupAlert();
        View.OnTouchListener filterTouchListener = new View.OnTouchListener() { // from class: com.android.systemui.usb.-$$Lambda$UsbDebuggingActivity$XWt--qGCtWBJlTLnAvCSF7AuSg8
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return UsbDebuggingActivity.lambda$onCreate$0(view, motionEvent);
            }
        };
        this.mAlert.getButton(-1).setOnTouchListener(filterTouchListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$onCreate$0(View v, MotionEvent event) {
        if ((event.getFlags() & 1) == 0 && (event.getFlags() & 2) == 0) {
            return false;
        }
        if (event.getAction() == 1) {
            EventLog.writeEvent(1397638484, "62187985");
            Toast.makeText(v.getContext(), R.string.touch_filtered_warning, 0).show();
        }
        return true;
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
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
            if (!"android.hardware.usb.action.USB_STATE".equals(action)) {
                return;
            }
            boolean connected = intent.getBooleanExtra("connected", false);
            if (!connected) {
                this.mActivity.finish();
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
        boolean alwaysAllow = true;
        boolean allow = which == -1;
        if (!allow || !this.mAlwaysAllow.isChecked()) {
            alwaysAllow = false;
        }
        try {
            IBinder b = ServiceManager.getService("adb");
            IAdbManager service = IAdbManager.Stub.asInterface(b);
            if (allow) {
                service.allowDebugging(alwaysAllow, this.mKey);
            } else {
                service.denyDebugging();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to notify Usb service", e);
        }
        finish();
    }
}
