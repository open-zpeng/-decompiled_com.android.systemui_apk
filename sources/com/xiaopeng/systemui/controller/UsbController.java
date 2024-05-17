package com.xiaopeng.systemui.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Handler;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.xiaopeng.systemui.Logger;
import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes24.dex */
public class UsbController {
    private static final boolean DEBUG = false;
    private static final String TAG = "UsbController";
    public static final int TYPE_COMPUTER = 4;
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_GENERAL = 6;
    public static final int TYPE_MEDIA = 2;
    public static final int TYPE_MICROPHONE = 5;
    public static final int TYPE_PHONE = 3;
    public static final int TYPE_STORAGE = 1;
    private ContentObserverAdapter mContentObserverAdapter;
    private Context mContext;
    private OnUsbListener mUsbListener;
    private UsbManager mUsbManager;
    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.controller.UsbController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.d(UsbController.TAG, "onReceive UsbReceiver action=" + action);
            if (!TextUtils.isEmpty(action)) {
                if (action.equals("android.hardware.usb.action.USB_STATE")) {
                    boolean connected = intent.getBooleanExtra("connected", false);
                    Context context2 = UsbController.this.mContext;
                    UsbController.log(context2, "onReceive UsbReceiver connected=" + connected);
                    return;
                }
                if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                    UsbController.this.onUsbDeviceAttached(device);
                    Context context3 = UsbController.this.mContext;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onReceive UsbReceiver attached device=");
                    sb.append(device != null ? device.toString() : "null");
                    UsbController.log(context3, sb.toString());
                } else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                    UsbDevice device2 = (UsbDevice) intent.getParcelableExtra("device");
                    UsbController.this.onUsbDeviceDetached(device2);
                    Context context4 = UsbController.this.mContext;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("onReceive UsbReceiver detached device=");
                    sb2.append(device2 != null ? device2.toString() : "null");
                    UsbController.log(context4, sb2.toString());
                } else if (action.equals("android.hardware.usb.action.USB_ACCESSORY_ATTACHED")) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra("accessory");
                    Context context5 = UsbController.this.mContext;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("onReceive UsbReceiver attached accessory=");
                    sb3.append(accessory != null ? accessory.toString() : "null");
                    UsbController.log(context5, sb3.toString());
                } else if (action.equals("android.hardware.usb.action.USB_ACCESSORY_DETACHED")) {
                    UsbAccessory accessory2 = (UsbAccessory) intent.getParcelableExtra("accessory");
                    Context context6 = UsbController.this.mContext;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("onReceive UsbReceiver detached accessory=");
                    sb4.append(accessory2 != null ? accessory2.toString() : "null");
                    UsbController.log(context6, sb4.toString());
                }
            }
        }
    };
    private static final ArrayList<UsbDevice> sUsbDevices = new ArrayList<>();
    private static UsbController sUsbController = null;

    /* loaded from: classes24.dex */
    public interface OnUsbListener {
        void onUsbTypeChanged(int i);
    }

    public UsbController(Context context) {
        this.mContext = context;
    }

    public static UsbController getInstance(Context context) {
        if (sUsbController == null) {
            synchronized (UsbController.class) {
                if (sUsbController == null) {
                    sUsbController = new UsbController(context);
                }
            }
        }
        return sUsbController;
    }

    public void init() {
        this.mContentObserverAdapter = new ContentObserverAdapter();
    }

    public void setUsbListener(OnUsbListener listener) {
        this.mUsbListener = listener;
    }

    public int getCurrentUsbStatus() {
        ContentObserverAdapter contentObserverAdapter = this.mContentObserverAdapter;
        if (contentObserverAdapter != null) {
            return contentObserverAdapter.getUsbType();
        }
        return -1;
    }

    private int getLatestType() {
        int count = sUsbDevices.size();
        if (count > 0) {
            return getUsbDeviceType(sUsbDevices.get(count - 1));
        }
        return -1;
    }

    private void initUsb() {
        sUsbDevices.clear();
        HashMap<String, UsbDevice> map = this.mUsbManager.getDeviceList();
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                UsbDevice device = map.get(key);
                if (device != null && isValid(device)) {
                    sUsbDevices.add(device);
                }
            }
        }
    }

    private void register() {
        try {
            IntentFilter usbFilter = new IntentFilter();
            usbFilter.addAction("android.hardware.usb.action.USB_STATE");
            usbFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
            usbFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
            usbFilter.addAction("android.hardware.usb.action.USB_ACCESSORY_ATTACHED");
            usbFilter.addAction("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
            this.mContext.registerReceiver(this.mUsbReceiver, usbFilter);
        } catch (Exception e) {
        }
    }

    private void unregister() {
        try {
            this.mContext.unregisterReceiver(this.mUsbReceiver);
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUsbDeviceAttached(UsbDevice device) {
        if (device != null && isValid(device)) {
            sUsbDevices.add(device);
            OnUsbListener onUsbListener = this.mUsbListener;
            if (onUsbListener != null) {
                onUsbListener.onUsbTypeChanged(getUsbDeviceType(device));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUsbDeviceDetached(UsbDevice device) {
        OnUsbListener onUsbListener;
        if (device != null && sUsbDevices.contains(device) && isValid(device)) {
            sUsbDevices.remove(device);
            if (sUsbDevices.isEmpty() && (onUsbListener = this.mUsbListener) != null) {
                onUsbListener.onUsbTypeChanged(-1);
            }
        }
    }

    private static void printStorageInfo(Context context) {
        StorageManager storage = (StorageManager) context.getSystemService(StorageManager.class);
        List<DiskInfo> disks = storage.getDisks();
        if (disks != null) {
            Iterator<DiskInfo> it = disks.iterator();
            while (it.hasNext()) {
                DiskInfo disk = it.next();
                StringBuilder sb = new StringBuilder();
                sb.append("printStorageInfo disk=");
                sb.append(disk != null ? disk.toString() : "");
                Logger.d(TAG, sb.toString());
            }
        }
        List<VolumeRecord> records = storage.getVolumeRecords();
        if (records != null) {
            for (VolumeRecord record : records) {
                IndentingPrintWriter writer = new IndentingPrintWriter(new CharArrayWriter(), " ", 80);
                record.dump(writer);
                Logger.d(TAG, "printStorageInfo record=" + writer.toString());
            }
        }
        StorageVolume[] volumes = storage.getVolumeList();
        if (volumes != null) {
            int length = volumes.length;
            for (int i = 0; i < length; i++) {
                StorageVolume volume = volumes[i];
                StringBuilder sb2 = new StringBuilder();
                sb2.append("printStorageInfo volume=");
                sb2.append(volume != null ? volume.toString() : "");
                Logger.d(TAG, sb2.toString());
            }
        }
        List<VolumeInfo> infos = storage.getVolumes();
        if (infos != null) {
            for (VolumeInfo info : infos) {
                if (info != null) {
                    Logger.d(TAG, "printStorageInfo info=" + info.toString());
                }
            }
        }
    }

    /* loaded from: classes24.dex */
    private class ContentObserverAdapter {
        private static final String KEY_USB_TYPE = "xp_usb_status";
        private ContentObserver mContentObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.controller.UsbController.ContentObserverAdapter.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Logger.i(UsbController.TAG, "SettingProvider onChange, key = " + uri);
                if (uri.equals(Settings.System.getUriFor(ContentObserverAdapter.KEY_USB_TYPE)) && UsbController.this.mUsbListener != null) {
                    int type = Settings.System.getInt(UsbController.this.mContext.getContentResolver(), ContentObserverAdapter.KEY_USB_TYPE, -1);
                    UsbController.this.mUsbListener.onUsbTypeChanged(type);
                }
            }
        };

        public ContentObserverAdapter() {
            try {
                registerContentObserver(this.mContentObserver, KEY_USB_TYPE);
            } catch (Exception e) {
                Logger.i(UsbController.TAG, "ContentObserverAdapter() e =" + e);
            }
        }

        protected void registerContentObserver(ContentObserver observer, String key) {
            UsbController.this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(key), true, observer);
        }

        protected int getUsbType() {
            int type = Settings.System.getInt(UsbController.this.mContext.getContentResolver(), KEY_USB_TYPE, -1);
            Logger.i(UsbController.TAG, "ContentProvider get value, key:xp_usb_status, value: " + type);
            return type;
        }
    }

    private static int getUsbDeviceType(UsbDevice device) {
        int type;
        if (isMediaDevice(device)) {
            type = 2;
        } else if (isPhoneDevice(device)) {
            type = 3;
        } else if (isStorageDevice(device)) {
            type = 1;
        } else if (isMicrophoneDevice(device)) {
            type = 5;
        } else {
            type = 6;
        }
        Logger.d(TAG, "getUsbDeviceType type=" + type);
        return type;
    }

    private static boolean isMediaDevice(UsbDevice device) {
        boolean hasAudio = hasUsbClass(device, 1);
        boolean hasVideo = hasUsbClass(device, 14);
        boolean hasHid = hasUsbClass(device, 3);
        if ((hasAudio || hasVideo) && !hasHid) {
            return true;
        }
        return false;
    }

    private static boolean isPhoneDevice(UsbDevice device) {
        UsbInterface[] interfaces = getUsbInterfaces(device);
        if (interfaces == null) {
            return false;
        }
        int length = interfaces.length;
        for (int i = 0; i < length; i++) {
            UsbInterface ui = interfaces[i];
            String name = ui != null ? ui.getName() : "";
            Logger.d(TAG, "isPhoneDevice name=" + name);
            if (!TextUtils.isEmpty(name) && ("ADB Interface".equals(name) || "Apple USB Multiplexor".equals(name) || "PTP".equals(name) || "MTP".equals(name))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isStorageDevice(UsbDevice device) {
        boolean hasStorage = hasUsbClass(device, 8);
        if (hasStorage) {
            return true;
        }
        return false;
    }

    private static boolean isMicrophoneDevice(UsbDevice device) {
        boolean hasAudio = hasUsbClass(device, 1);
        boolean hasHid = hasUsbClass(device, 3);
        if (hasAudio && hasHid) {
            return true;
        }
        return false;
    }

    private static boolean isValid(UsbDevice device) {
        boolean valid = true;
        UsbInterface[] interfaces = getUsbInterfaces(device);
        if (interfaces != null) {
            int length = interfaces.length;
            for (int i = 0; i < length; i++) {
                UsbInterface ui = interfaces[i];
                int clazz = ui != null ? ui.getInterfaceClass() : -1;
                if (clazz == 239 || clazz == 11 || clazz == 10 || clazz == 13 || clazz == 224) {
                    valid = false;
                    break;
                }
            }
        }
        if (valid && device != null && device.getVendorId() == 2578) {
            return false;
        }
        return valid;
    }

    private static boolean hasUsbClass(UsbDevice device, int clazz) {
        int count = device != null ? device.getInterfaceCount() : 0;
        if (device == null || count <= 0) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            UsbInterface ui = device.getInterface(i);
            int classValue = ui != null ? ui.getInterfaceClass() : -1;
            if (classValue == clazz) {
                return true;
            }
        }
        return false;
    }

    private static int[] getUsbClass(UsbDevice device) {
        int count = device != null ? device.getInterfaceCount() : 0;
        if (device != null && count > 0) {
            int[] classes = new int[count];
            for (int i = 0; i < count; i++) {
                UsbInterface ui = device.getInterface(i);
                classes[i] = ui != null ? ui.getInterfaceClass() : -1;
            }
            return classes;
        }
        return null;
    }

    private static UsbInterface[] getUsbInterfaces(UsbDevice device) {
        int count;
        if (device != null && (count = device.getInterfaceCount()) > 0) {
            UsbInterface[] interfaces = new UsbInterface[count];
            for (int i = 0; i < count; i++) {
                interfaces[i] = device.getInterface(i);
            }
            return interfaces;
        }
        return null;
    }

    private static int getUsbType(int dClass) {
        if (dClass == 1 || dClass == 2 || dClass == 3 || dClass == 13 || dClass == 14 || dClass == 224 || dClass == 239 || dClass == 254) {
            return -1;
        }
        switch (dClass) {
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
                return -1;
        }
    }

    private static int getUsbCount() {
        return sUsbDevices.size();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(Context context, String text) {
        Logger.d(TAG, text);
    }
}
