package com.android.systemui.usb;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.IBinder;
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
public class UsbConfirmActivity extends AlertActivity implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "UsbConfirmActivity";
    private UsbAccessory mAccessory;
    private CheckBox mAlwaysUse;
    private TextView mClearDefaultHint;
    private UsbDevice mDevice;
    private UsbDisconnectedReceiver mDisconnectedReceiver;
    private boolean mPermissionGranted;
    private ResolveInfo mResolveInfo;

    /* JADX WARN: Multi-variable type inference failed */
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        this.mDevice = (UsbDevice) intent.getParcelableExtra("device");
        this.mAccessory = (UsbAccessory) intent.getParcelableExtra("accessory");
        this.mResolveInfo = (ResolveInfo) intent.getParcelableExtra(UsbResolverActivity.EXTRA_RESOLVE_INFO);
        PackageManager packageManager = getPackageManager();
        String appName = this.mResolveInfo.loadLabel(packageManager).toString();
        AlertController.AlertParams ap = this.mAlertParams;
        ap.mTitle = appName;
        if (this.mDevice == null) {
            ap.mMessage = getString(R.string.usb_accessory_confirm_prompt, new Object[]{appName, this.mAccessory.getDescription()});
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity) this, this.mAccessory);
        } else {
            ap.mMessage = getString(R.string.usb_device_confirm_prompt, new Object[]{appName, this.mDevice.getProductName()});
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity) this, this.mDevice);
        }
        ap.mPositiveButtonText = getString(17039370);
        ap.mNegativeButtonText = getString(17039360);
        ap.mPositiveButtonListener = this;
        ap.mNegativeButtonListener = this;
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
        setupAlert();
    }

    protected void onDestroy() {
        UsbDisconnectedReceiver usbDisconnectedReceiver = this.mDisconnectedReceiver;
        if (usbDisconnectedReceiver != null) {
            unregisterReceiver(usbDisconnectedReceiver);
        }
        super.onDestroy();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            try {
                IBinder b = ServiceManager.getService(DropmenuController.DROPMENU_USB);
                IUsbManager service = IUsbManager.Stub.asInterface(b);
                int uid = this.mResolveInfo.activityInfo.applicationInfo.uid;
                int userId = UserHandle.myUserId();
                boolean alwaysUse = this.mAlwaysUse.isChecked();
                Intent intent = null;
                if (this.mDevice != null) {
                    intent = new Intent("android.hardware.usb.action.USB_DEVICE_ATTACHED");
                    intent.putExtra("device", this.mDevice);
                    service.grantDevicePermission(this.mDevice, uid);
                    if (alwaysUse) {
                        service.setDevicePackage(this.mDevice, this.mResolveInfo.activityInfo.packageName, userId);
                    } else {
                        service.setDevicePackage(this.mDevice, (String) null, userId);
                    }
                } else if (this.mAccessory != null) {
                    intent = new Intent("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
                    intent.putExtra("accessory", this.mAccessory);
                    service.grantAccessoryPermission(this.mAccessory, uid);
                    if (alwaysUse) {
                        service.setAccessoryPackage(this.mAccessory, this.mResolveInfo.activityInfo.packageName, userId);
                    } else {
                        service.setAccessoryPackage(this.mAccessory, (String) null, userId);
                    }
                }
                intent.addFlags(268435456);
                intent.setComponent(new ComponentName(this.mResolveInfo.activityInfo.packageName, this.mResolveInfo.activityInfo.name));
                startActivityAsUser(intent, new UserHandle(userId));
            } catch (Exception e) {
                Log.e(TAG, "Unable to start activity", e);
            }
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
