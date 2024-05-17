package com.xiaopeng.systemui.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import com.xiaopeng.systemui.Logger;
/* loaded from: classes24.dex */
public class StorageController {
    private static final String TAG = "StorageController";
    private Context mContext;
    private InputManager mInputManager;
    private OnStorageCallback mOnStorageCallback;
    private StorageManager mStorageManager;
    private BroadcastReceiver mMediaReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.controller.StorageController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String path = intent.getData().getPath();
            Logger.d(StorageController.TAG, "onReceive action=" + action);
            if (!TextUtils.isEmpty(path)) {
                if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                    StorageController.this.updateStorageView();
                } else if (action.equals("android.intent.action.MEDIA_UNMOUNTED")) {
                    StorageController.this.updateStorageView();
                } else if (action.equals("android.intent.action.MEDIA_EJECT")) {
                    StorageController.this.updateStorageView();
                } else if (action.equals("android.intent.action.MEDIA_REMOVED")) {
                    StorageController.this.updateStorageView();
                } else {
                    action.equals("android.intent.action.MEDIA_CHECKING");
                }
            }
        }
    };
    private InputManager.InputDeviceListener mInputDeviceListener = new InputManager.InputDeviceListener() { // from class: com.xiaopeng.systemui.controller.StorageController.2
        @Override // android.hardware.input.InputManager.InputDeviceListener
        public void onInputDeviceAdded(int deviceId) {
            Logger.d(StorageController.TAG, "onInputDeviceAdded");
            StorageController.this.updateStorageView();
        }

        @Override // android.hardware.input.InputManager.InputDeviceListener
        public void onInputDeviceRemoved(int deviceId) {
            Logger.d(StorageController.TAG, "onInputDeviceRemoved");
            StorageController.this.updateStorageView();
        }

        @Override // android.hardware.input.InputManager.InputDeviceListener
        public void onInputDeviceChanged(int deviceId) {
            Logger.d(StorageController.TAG, "onInputDeviceChanged");
            StorageController.this.updateStorageView();
        }
    };
    private final StorageEventListener mStorageListener = new StorageEventListener() { // from class: com.xiaopeng.systemui.controller.StorageController.3
        public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
            Logger.d(StorageController.TAG, "onVolumeStateChanged");
            StorageController.this.updateStorageView();
        }

        public void onVolumeRecordChanged(VolumeRecord rec) {
            VolumeInfo vol = StorageController.this.mStorageManager.findVolumeByUuid(rec.getFsUuid());
            if (vol != null && vol.isMountedReadable()) {
                Logger.d(StorageController.TAG, "onVolumeRecordChanged");
                StorageController.this.updateStorageView();
            }
        }

        public void onVolumeForgotten(String fsUuid) {
            Logger.d(StorageController.TAG, "onVolumeForgotten");
        }

        public void onDiskScanned(DiskInfo disk, int volumeCount) {
            Logger.d(StorageController.TAG, "onDiskScanned");
            StorageController.this.updateStorageView();
        }

        public void onDiskDestroyed(DiskInfo disk) {
            Logger.d(StorageController.TAG, "onDiskDestroyed");
            StorageController.this.updateStorageView();
        }
    };

    /* loaded from: classes24.dex */
    public interface OnStorageCallback {
        void onStorageChanged(boolean z);
    }

    public StorageController(Context context) {
        this.mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_SHARED");
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        filter.addAction("android.intent.action.MEDIA_CHECKING");
        filter.addAction("android.intent.action.MEDIA_EJECT");
        filter.addAction("android.intent.action.MEDIA_REMOVED");
        context.registerReceiver(this.mMediaReceiver, filter);
        this.mInputManager = (InputManager) context.getSystemService("input");
        this.mInputManager.registerInputDeviceListener(this.mInputDeviceListener, null);
        this.mStorageManager = (StorageManager) context.getSystemService(StorageManager.class);
        this.mStorageManager.registerListener(this.mStorageListener);
        updateStorageView();
    }

    public void setCallback(OnStorageCallback callback) {
        this.mOnStorageCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStorageView() {
        boolean hasStorage = hasExternalStorage();
        OnStorageCallback onStorageCallback = this.mOnStorageCallback;
        if (onStorageCallback != null) {
            onStorageCallback.onStorageChanged(hasStorage);
        }
    }

    public boolean hasExternalStorage() {
        try {
            StorageVolume[] volumeList = this.mStorageManager.getVolumeList();
            if (volumeList == null || volumeList.length <= 0) {
                return false;
            }
            for (StorageVolume volume : volumeList) {
                if (volume != null) {
                    volume.getPath();
                    boolean mounted = "mounted".equals(volume.getState());
                    Logger.i(TAG, "hasExternalStorage volume=" + volume.dump());
                    if (!volume.isPrimary() && volume.isRemovable() && mounted) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            Logger.i(TAG, "hasExternalStorage e=" + e);
            return false;
        }
    }
}
