package com.android.systemui.usb;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.widget.CheckBox;
import com.android.internal.app.IntentForwarderActivity;
import com.android.internal.app.ResolverActivity;
import com.android.systemui.R;
import com.xiaopeng.systemui.controller.DropmenuController;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes21.dex */
public class UsbResolverActivity extends ResolverActivity {
    public static final String EXTRA_RESOLVE_INFO = "rinfo";
    public static final String EXTRA_RESOLVE_INFOS = "rlist";
    public static final String TAG = "UsbResolverActivity";
    private UsbAccessory mAccessory;
    private UsbDevice mDevice;
    private UsbDisconnectedReceiver mDisconnectedReceiver;
    private ResolveInfo mForwardResolveInfo;
    private Intent mOtherProfileIntent;

    /* JADX WARN: Multi-variable type inference failed */
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Parcelable targetParcelable = intent.getParcelableExtra("android.intent.extra.INTENT");
        if (!(targetParcelable instanceof Intent)) {
            Log.w(TAG, "Target is not an intent: " + targetParcelable);
            finish();
            return;
        }
        Intent target = (Intent) targetParcelable;
        ArrayList<ResolveInfo> rList = new ArrayList<>(intent.getParcelableArrayListExtra(EXTRA_RESOLVE_INFOS));
        ArrayList<ResolveInfo> rListOtherProfile = new ArrayList<>();
        this.mForwardResolveInfo = null;
        Iterator<ResolveInfo> iterator = rList.iterator();
        while (iterator.hasNext()) {
            ResolveInfo ri = iterator.next();
            if (ri.getComponentInfo().name.equals(IntentForwarderActivity.FORWARD_INTENT_TO_MANAGED_PROFILE)) {
                this.mForwardResolveInfo = ri;
            } else if (UserHandle.getUserId(ri.activityInfo.applicationInfo.uid) != UserHandle.myUserId()) {
                iterator.remove();
                rListOtherProfile.add(ri);
            }
        }
        this.mDevice = (UsbDevice) target.getParcelableExtra("device");
        UsbDevice usbDevice = this.mDevice;
        if (usbDevice != null) {
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity) this, usbDevice);
        } else {
            this.mAccessory = (UsbAccessory) target.getParcelableExtra("accessory");
            UsbAccessory usbAccessory = this.mAccessory;
            if (usbAccessory == null) {
                Log.e(TAG, "no device or accessory");
                finish();
                return;
            }
            this.mDisconnectedReceiver = new UsbDisconnectedReceiver((Activity) this, usbAccessory);
        }
        if (this.mForwardResolveInfo != null) {
            if (rListOtherProfile.size() > 1) {
                this.mOtherProfileIntent = new Intent(intent);
                this.mOtherProfileIntent.putParcelableArrayListExtra(EXTRA_RESOLVE_INFOS, rListOtherProfile);
            } else {
                this.mOtherProfileIntent = new Intent((Context) this, (Class<?>) UsbConfirmActivity.class);
                this.mOtherProfileIntent.putExtra(EXTRA_RESOLVE_INFO, rListOtherProfile.get(0));
                UsbDevice usbDevice2 = this.mDevice;
                if (usbDevice2 != null) {
                    this.mOtherProfileIntent.putExtra("device", usbDevice2);
                }
                UsbAccessory usbAccessory2 = this.mAccessory;
                if (usbAccessory2 != null) {
                    this.mOtherProfileIntent.putExtra("accessory", usbAccessory2);
                }
            }
        }
        CharSequence title = getResources().getText(17039653);
        super.onCreate(savedInstanceState, target, title, (Intent[]) null, rList, true);
        CheckBox alwaysUse = (CheckBox) findViewById(16908821);
        if (alwaysUse != null) {
            if (this.mDevice == null) {
                alwaysUse.setText(R.string.always_use_accessory);
            } else {
                alwaysUse.setText(R.string.always_use_device);
            }
        }
    }

    protected void onDestroy() {
        UsbDisconnectedReceiver usbDisconnectedReceiver = this.mDisconnectedReceiver;
        if (usbDisconnectedReceiver != null) {
            unregisterReceiver(usbDisconnectedReceiver);
        }
        super.onDestroy();
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected boolean onTargetSelected(ResolverActivity.TargetInfo target, boolean alwaysCheck) {
        ResolveInfo ri = target.getResolveInfo();
        ResolveInfo resolveInfo = this.mForwardResolveInfo;
        if (ri == resolveInfo) {
            startActivityAsUser(this.mOtherProfileIntent, null, UserHandle.of(resolveInfo.targetUserId));
            return true;
        }
        try {
            IBinder b = ServiceManager.getService(DropmenuController.DROPMENU_USB);
            IUsbManager service = IUsbManager.Stub.asInterface(b);
            int uid = ri.activityInfo.applicationInfo.uid;
            int userId = UserHandle.myUserId();
            if (this.mDevice != null) {
                service.grantDevicePermission(this.mDevice, uid);
                if (alwaysCheck) {
                    service.setDevicePackage(this.mDevice, ri.activityInfo.packageName, userId);
                } else {
                    service.setDevicePackage(this.mDevice, (String) null, userId);
                }
            } else if (this.mAccessory != null) {
                service.grantAccessoryPermission(this.mAccessory, uid);
                if (alwaysCheck) {
                    service.setAccessoryPackage(this.mAccessory, ri.activityInfo.packageName, userId);
                } else {
                    service.setAccessoryPackage(this.mAccessory, (String) null, userId);
                }
            }
            try {
                target.startAsUser(this, (Bundle) null, UserHandle.of(userId));
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "startActivity failed", e);
            }
        } catch (RemoteException e2) {
            Log.e(TAG, "onIntentSelected failed", e2);
        }
        return true;
    }
}
