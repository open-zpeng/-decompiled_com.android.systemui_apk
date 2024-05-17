package com.android.systemui.usb;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.systemui.R;
import com.xiaopeng.systemui.controller.DropmenuController;
/* loaded from: classes21.dex */
public class UsbPermissionActivity extends AlertActivity implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "UsbPermissionActivity";
    private UsbAccessory mAccessory;
    private CheckBox mAlwaysUse;
    private TextView mClearDefaultHint;
    private UsbDevice mDevice;
    private UsbDisconnectedReceiver mDisconnectedReceiver;
    private String mPackageName;
    private PendingIntent mPendingIntent;
    private boolean mPermissionGranted;
    private int mUid;

    /* JADX WARN: Multi-variable type inference failed */
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        this.mDevice = (UsbDevice) intent.getParcelableExtra("device");
        this.mAccessory = (UsbAccessory) intent.getParcelableExtra("accessory");
        this.mPendingIntent = (PendingIntent) intent.getParcelableExtra("android.intent.extra.INTENT");
        this.mUid = intent.getIntExtra("android.intent.extra.UID", -1);
        this.mPackageName = intent.getStringExtra("android.hardware.usb.extra.PACKAGE");
        boolean canBeDefault = intent.getBooleanExtra("android.hardware.usb.extra.CAN_BE_DEFAULT", false);
        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo aInfo = packageManager.getApplicationInfo(this.mPackageName, 0);
            String appName = aInfo.loadLabel(packageManager).toString();
            AlertController.AlertParams ap = this.mAlertParams;
            ap.mTitle = appName;
            if (this.mDevice == null) {
                ap.mMessage = getString(R.string.usb_accessory_permission_prompt, new Object[]{appName, this.mAccessory.getDescription()});
                this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity) this, this.mAccessory);
            } else {
                ap.mMessage = getString(R.string.usb_device_permission_prompt, new Object[]{appName, this.mDevice.getProductName()});
                this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity) this, this.mDevice);
            }
            ap.mPositiveButtonText = getString(17039370);
            ap.mNegativeButtonText = getString(17039360);
            ap.mPositiveButtonListener = this;
            ap.mNegativeButtonListener = this;
            if (canBeDefault && (this.mDevice != null || this.mAccessory != null)) {
                LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
                ap.mView = inflater.inflate(17367090, (ViewGroup) null);
                this.mAlwaysUse = (CheckBox) ap.mView.findViewById(16908821);
                if (this.mDevice == null) {
                    this.mAlwaysUse.setText(getString(R.string.always_use_accessory, new Object[]{appName, this.mAccessory.getDescription()}));
                } else {
                    this.mAlwaysUse.setText(getString(R.string.always_use_device, new Object[]{appName, this.mDevice.getProductName()}));
                }
                this.mAlwaysUse.setOnCheckedChangeListener(this);
                this.mClearDefaultHint = (TextView) ap.mView.findViewById(16908911);
                this.mClearDefaultHint.setVisibility(8);
            }
            setupAlert();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "unable to look up package name", e);
            finish();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onDestroy() {
        IBinder b = ServiceManager.getService(DropmenuController.DROPMENU_USB);
        IUsbManager service = IUsbManager.Stub.asInterface(b);
        Intent intent = new Intent();
        try {
            if (this.mDevice != null) {
                intent.putExtra("device", this.mDevice);
                if (this.mPermissionGranted) {
                    service.grantDevicePermission(this.mDevice, this.mUid);
                    if (this.mAlwaysUse != null && this.mAlwaysUse.isChecked()) {
                        int userId = UserHandle.getUserId(this.mUid);
                        service.setDevicePackage(this.mDevice, this.mPackageName, userId);
                    }
                }
            }
            if (this.mAccessory != null) {
                intent.putExtra("accessory", this.mAccessory);
                if (this.mPermissionGranted) {
                    service.grantAccessoryPermission(this.mAccessory, this.mUid);
                    if (this.mAlwaysUse != null && this.mAlwaysUse.isChecked()) {
                        int userId2 = UserHandle.getUserId(this.mUid);
                        service.setAccessoryPackage(this.mAccessory, this.mPackageName, userId2);
                    }
                }
            }
            intent.putExtra("permission", this.mPermissionGranted);
            this.mPendingIntent.send((Context) this, 0, intent);
        } catch (PendingIntent.CanceledException e) {
            Log.w(TAG, "PendingIntent was cancelled");
        } catch (RemoteException e2) {
            Log.e(TAG, "IUsbService connection failed", e2);
        }
        UsbDisconnectedReceiver usbDisconnectedReceiver = this.mDisconnectedReceiver;
        if (usbDisconnectedReceiver != null) {
            unregisterReceiver(usbDisconnectedReceiver);
        }
        super.onDestroy();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            this.mPermissionGranted = true;
        }
        finish();
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        TextView textView = this.mClearDefaultHint;
        if (textView == null) {
            return;
        }
        if (isChecked) {
            textView.setVisibility(0);
        } else {
            textView.setVisibility(8);
        }
    }
}
