package com.android.systemui.usb;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.UserHandle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import com.android.systemui.SystemUI;
import com.android.systemui.util.NotificationChannels;
import java.util.List;
/* loaded from: classes21.dex */
public class StorageNotification extends SystemUI {
    private static final String ACTION_FINISH_WIZARD = "com.android.systemui.action.FINISH_WIZARD";
    private static final String ACTION_SNOOZE_VOLUME = "com.android.systemui.action.SNOOZE_VOLUME";
    private static final String TAG = "StorageNotification";
    private NotificationManager mNotificationManager;
    private StorageManager mStorageManager;
    private final SparseArray<MoveInfo> mMoves = new SparseArray<>();
    private final StorageEventListener mListener = new StorageEventListener() { // from class: com.android.systemui.usb.StorageNotification.1
        public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
            StorageNotification.this.onVolumeStateChangedInternal(vol);
        }

        public void onVolumeRecordChanged(VolumeRecord rec) {
            VolumeInfo vol = StorageNotification.this.mStorageManager.findVolumeByUuid(rec.getFsUuid());
            if (vol != null && vol.isMountedReadable()) {
                StorageNotification.this.onVolumeStateChangedInternal(vol);
            }
        }

        public void onVolumeForgotten(String fsUuid) {
            StorageNotification.this.mNotificationManager.cancelAsUser(fsUuid, 1397772886, UserHandle.ALL);
        }

        public void onDiskScanned(DiskInfo disk, int volumeCount) {
            StorageNotification.this.onDiskScannedInternal(disk, volumeCount);
        }

        public void onDiskDestroyed(DiskInfo disk) {
            StorageNotification.this.onDiskDestroyedInternal(disk);
        }
    };
    private final BroadcastReceiver mSnoozeReceiver = new BroadcastReceiver() { // from class: com.android.systemui.usb.StorageNotification.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String fsUuid = intent.getStringExtra("android.os.storage.extra.FS_UUID");
            StorageNotification.this.mStorageManager.setVolumeSnoozed(fsUuid, true);
        }
    };
    private final BroadcastReceiver mFinishReceiver = new BroadcastReceiver() { // from class: com.android.systemui.usb.StorageNotification.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            StorageNotification.this.mNotificationManager.cancelAsUser(null, 1397575510, UserHandle.ALL);
        }
    };
    private final PackageManager.MoveCallback mMoveCallback = new PackageManager.MoveCallback() { // from class: com.android.systemui.usb.StorageNotification.4
        public void onCreated(int moveId, Bundle extras) {
            MoveInfo move = new MoveInfo();
            move.moveId = moveId;
            move.extras = extras;
            if (extras != null) {
                move.packageName = extras.getString("android.intent.extra.PACKAGE_NAME");
                move.label = extras.getString("android.intent.extra.TITLE");
                move.volumeUuid = extras.getString("android.os.storage.extra.FS_UUID");
            }
            StorageNotification.this.mMoves.put(moveId, move);
        }

        public void onStatusChanged(int moveId, int status, long estMillis) {
            MoveInfo move = (MoveInfo) StorageNotification.this.mMoves.get(moveId);
            if (move == null) {
                Log.w(StorageNotification.TAG, "Ignoring unknown move " + moveId);
            } else if (PackageManager.isMoveStatusFinished(status)) {
                StorageNotification.this.onMoveFinished(move, status);
            } else {
                StorageNotification.this.onMoveProgress(move, status, estMillis);
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class MoveInfo {
        public Bundle extras;
        public String label;
        public int moveId;
        public String packageName;
        public String volumeUuid;

        private MoveInfo() {
        }
    }

    @Override // com.android.systemui.SystemUI
    public void start() {
        this.mNotificationManager = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        this.mStorageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        this.mStorageManager.registerListener(this.mListener);
        this.mContext.registerReceiver(this.mSnoozeReceiver, new IntentFilter(ACTION_SNOOZE_VOLUME), "android.permission.MOUNT_UNMOUNT_FILESYSTEMS", null);
        this.mContext.registerReceiver(this.mFinishReceiver, new IntentFilter(ACTION_FINISH_WIZARD), "android.permission.MOUNT_UNMOUNT_FILESYSTEMS", null);
        List<DiskInfo> disks = this.mStorageManager.getDisks();
        for (DiskInfo disk : disks) {
            onDiskScannedInternal(disk, disk.volumeCount);
        }
        List<VolumeInfo> vols = this.mStorageManager.getVolumes();
        for (VolumeInfo vol : vols) {
            onVolumeStateChangedInternal(vol);
        }
        this.mContext.getPackageManager().registerMoveCallback(this.mMoveCallback, new Handler());
        updateMissingPrivateVolumes();
    }

    private void updateMissingPrivateVolumes() {
        if (isTv()) {
            return;
        }
        List<VolumeRecord> recs = this.mStorageManager.getVolumeRecords();
        for (VolumeRecord rec : recs) {
            if (rec.getType() == 1) {
                String fsUuid = rec.getFsUuid();
                VolumeInfo info = this.mStorageManager.findVolumeByUuid(fsUuid);
                if ((info != null && info.isMountedWritable()) || rec.isSnoozed()) {
                    this.mNotificationManager.cancelAsUser(fsUuid, 1397772886, UserHandle.ALL);
                } else {
                    CharSequence title = this.mContext.getString(17039933, rec.getNickname());
                    CharSequence text = this.mContext.getString(17039932);
                    Notification.Builder builder = new Notification.Builder(this.mContext, NotificationChannels.STORAGE).setSmallIcon(17302796).setColor(this.mContext.getColor(17170460)).setContentTitle(title).setContentText(text).setContentIntent(buildForgetPendingIntent(rec)).setStyle(new Notification.BigTextStyle().bigText(text)).setVisibility(1).setLocalOnly(true).setCategory("sys").setDeleteIntent(buildSnoozeIntent(fsUuid)).extend(new Notification.TvExtender());
                    SystemUI.overrideNotificationAppName(this.mContext, builder, false);
                    this.mNotificationManager.notifyAsUser(fsUuid, 1397772886, builder.build(), UserHandle.ALL);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDiskScannedInternal(DiskInfo disk, int volumeCount) {
        if (volumeCount != 0 || disk.size <= 0) {
            this.mNotificationManager.cancelAsUser(disk.getId(), 1396986699, UserHandle.ALL);
            return;
        }
        CharSequence title = this.mContext.getString(17039963, disk.getDescription());
        CharSequence text = this.mContext.getString(17039962, disk.getDescription());
        Notification.Builder builder = new Notification.Builder(this.mContext, NotificationChannels.STORAGE).setSmallIcon(getSmallIcon(disk, 6)).setColor(this.mContext.getColor(17170460)).setContentTitle(title).setContentText(text).setContentIntent(buildInitPendingIntent(disk)).setStyle(new Notification.BigTextStyle().bigText(text)).setVisibility(1).setLocalOnly(true).setCategory("err").extend(new Notification.TvExtender());
        SystemUI.overrideNotificationAppName(this.mContext, builder, false);
        this.mNotificationManager.notifyAsUser(disk.getId(), 1396986699, builder.build(), UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDiskDestroyedInternal(DiskInfo disk) {
        this.mNotificationManager.cancelAsUser(disk.getId(), 1396986699, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onVolumeStateChangedInternal(VolumeInfo vol) {
        int type = vol.getType();
        if (type == 0) {
            onPublicVolumeStateChangedInternal(vol);
        } else if (type == 1) {
            onPrivateVolumeStateChangedInternal(vol);
        }
    }

    private void onPrivateVolumeStateChangedInternal(VolumeInfo vol) {
        Log.d(TAG, "Notifying about private volume: " + vol.toString());
        updateMissingPrivateVolumes();
    }

    private void onPublicVolumeStateChangedInternal(VolumeInfo vol) {
        Notification notif;
        Log.d(TAG, "Notifying about public volume: " + vol.toString());
        switch (vol.getState()) {
            case 0:
                notif = onVolumeUnmounted(vol);
                break;
            case 1:
                notif = onVolumeChecking(vol);
                break;
            case 2:
            case 3:
                notif = onVolumeMounted(vol);
                break;
            case 4:
                notif = onVolumeFormatting(vol);
                break;
            case 5:
                notif = onVolumeEjecting(vol);
                break;
            case 6:
                notif = onVolumeUnmountable(vol);
                break;
            case 7:
                notif = onVolumeRemoved(vol);
                break;
            case 8:
                notif = onVolumeBadRemoval(vol);
                break;
            default:
                notif = null;
                break;
        }
        if (notif != null) {
            this.mNotificationManager.notifyAsUser(vol.getId(), 1397773634, notif, UserHandle.of(vol.getMountUserId()));
        } else {
            this.mNotificationManager.cancelAsUser(vol.getId(), 1397773634, UserHandle.of(vol.getMountUserId()));
        }
    }

    private Notification onVolumeUnmounted(VolumeInfo vol) {
        return null;
    }

    private Notification onVolumeChecking(VolumeInfo vol) {
        DiskInfo disk = vol.getDisk();
        CharSequence title = this.mContext.getString(17039930, disk.getDescription());
        CharSequence text = this.mContext.getString(17039929, disk.getDescription());
        return buildNotificationBuilder(vol, title, text).setCategory("progress").setOngoing(true).build();
    }

    private Notification onVolumeMounted(VolumeInfo vol) {
        VolumeRecord rec = this.mStorageManager.findRecordByUuid(vol.getFsUuid());
        DiskInfo disk = vol.getDisk();
        if (rec.isSnoozed() && disk.isAdoptable()) {
            return null;
        }
        if (disk.isAdoptable() && !rec.isInited()) {
            CharSequence title = disk.getDescription();
            CharSequence text = this.mContext.getString(17039940, disk.getDescription());
            PendingIntent initIntent = buildInitPendingIntent(vol);
            return buildNotificationBuilder(vol, title, text).addAction(new Notification.Action(17302802, this.mContext.getString(17039931), initIntent)).addAction(new Notification.Action(17302408, this.mContext.getString(17039957), buildUnmountPendingIntent(vol))).setContentIntent(initIntent).setDeleteIntent(buildSnoozeIntent(vol.getFsUuid())).build();
        }
        CharSequence title2 = disk.getDescription();
        CharSequence text2 = this.mContext.getString(17039944, disk.getDescription());
        PendingIntent browseIntent = buildBrowsePendingIntent(vol);
        Notification.Builder builder = buildNotificationBuilder(vol, title2, text2).addAction(new Notification.Action(17302426, this.mContext.getString(17039928), browseIntent)).addAction(new Notification.Action(17302408, this.mContext.getString(17039957), buildUnmountPendingIntent(vol))).setContentIntent(browseIntent).setCategory("sys");
        if (disk.isAdoptable()) {
            builder.setDeleteIntent(buildSnoozeIntent(vol.getFsUuid()));
        }
        return builder.build();
    }

    private Notification onVolumeFormatting(VolumeInfo vol) {
        return null;
    }

    private Notification onVolumeEjecting(VolumeInfo vol) {
        DiskInfo disk = vol.getDisk();
        CharSequence title = this.mContext.getString(17039961, disk.getDescription());
        CharSequence text = this.mContext.getString(17039960, disk.getDescription());
        return buildNotificationBuilder(vol, title, text).setCategory("progress").setOngoing(true).build();
    }

    private Notification onVolumeUnmountable(VolumeInfo vol) {
        DiskInfo disk = vol.getDisk();
        CharSequence title = this.mContext.getString(17039959, disk.getDescription());
        CharSequence text = this.mContext.getString(17039958, disk.getDescription());
        return buildNotificationBuilder(vol, title, text).setContentIntent(buildInitPendingIntent(vol)).setCategory("err").build();
    }

    private Notification onVolumeRemoved(VolumeInfo vol) {
        if (!vol.isPrimary()) {
            return null;
        }
        DiskInfo disk = vol.getDisk();
        CharSequence title = this.mContext.getString(17039943, disk.getDescription());
        CharSequence text = this.mContext.getString(17039942, disk.getDescription());
        return buildNotificationBuilder(vol, title, text).setCategory("err").build();
    }

    private Notification onVolumeBadRemoval(VolumeInfo vol) {
        if (!vol.isPrimary()) {
            return null;
        }
        DiskInfo disk = vol.getDisk();
        CharSequence title = this.mContext.getString(17039927, disk.getDescription());
        CharSequence text = this.mContext.getString(17039926, disk.getDescription());
        return buildNotificationBuilder(vol, title, text).setCategory("err").build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMoveProgress(MoveInfo move, int status, long estMillis) {
        CharSequence title;
        CharSequence text;
        PendingIntent intent;
        if (!TextUtils.isEmpty(move.label)) {
            title = this.mContext.getString(17039936, move.label);
        } else {
            title = this.mContext.getString(17039939);
        }
        if (estMillis < 0) {
            text = null;
        } else {
            text = DateUtils.formatDuration(estMillis);
        }
        if (move.packageName != null) {
            intent = buildWizardMovePendingIntent(move);
        } else {
            intent = buildWizardMigratePendingIntent(move);
        }
        Notification.Builder builder = new Notification.Builder(this.mContext, NotificationChannels.STORAGE).setSmallIcon(17302796).setColor(this.mContext.getColor(17170460)).setContentTitle(title).setContentText(text).setContentIntent(intent).setStyle(new Notification.BigTextStyle().bigText(text)).setVisibility(1).setLocalOnly(true).setCategory("progress").setProgress(100, status, false).setOngoing(true);
        SystemUI.overrideNotificationAppName(this.mContext, builder, false);
        this.mNotificationManager.notifyAsUser(move.packageName, 1397575510, builder.build(), UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMoveFinished(MoveInfo move, int status) {
        CharSequence title;
        CharSequence text;
        PendingIntent intent;
        if (move.packageName != null) {
            this.mNotificationManager.cancelAsUser(move.packageName, 1397575510, UserHandle.ALL);
            return;
        }
        VolumeInfo privateVol = this.mContext.getPackageManager().getPrimaryStorageCurrentVolume();
        String descrip = this.mStorageManager.getBestVolumeDescription(privateVol);
        if (status == -100) {
            title = this.mContext.getString(17039938);
            text = this.mContext.getString(17039937, descrip);
        } else {
            title = this.mContext.getString(17039935);
            text = this.mContext.getString(17039934);
        }
        if (privateVol != null && privateVol.getDisk() != null) {
            intent = buildWizardReadyPendingIntent(privateVol.getDisk());
        } else if (privateVol != null) {
            intent = buildVolumeSettingsPendingIntent(privateVol);
        } else {
            intent = null;
        }
        Notification.Builder builder = new Notification.Builder(this.mContext, NotificationChannels.STORAGE).setSmallIcon(17302796).setColor(this.mContext.getColor(17170460)).setContentTitle(title).setContentText(text).setContentIntent(intent).setStyle(new Notification.BigTextStyle().bigText(text)).setVisibility(1).setLocalOnly(true).setCategory("sys").setAutoCancel(true);
        SystemUI.overrideNotificationAppName(this.mContext, builder, false);
        this.mNotificationManager.notifyAsUser(move.packageName, 1397575510, builder.build(), UserHandle.ALL);
    }

    private int getSmallIcon(DiskInfo disk, int state) {
        return disk.isSd() ? (state == 1 || state == 5) ? 17302796 : 17302796 : disk.isUsb() ? 17302837 : 17302796;
    }

    private Notification.Builder buildNotificationBuilder(VolumeInfo vol, CharSequence title, CharSequence text) {
        Notification.Builder builder = new Notification.Builder(this.mContext, NotificationChannels.STORAGE).setSmallIcon(getSmallIcon(vol.getDisk(), vol.getState())).setColor(this.mContext.getColor(17170460)).setContentTitle(title).setContentText(text).setStyle(new Notification.BigTextStyle().bigText(text)).setVisibility(1).setLocalOnly(true).extend(new Notification.TvExtender());
        overrideNotificationAppName(this.mContext, builder, false);
        return builder;
    }

    private PendingIntent buildInitPendingIntent(DiskInfo disk) {
        Intent intent = new Intent();
        if (isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("com.android.tv.settings.action.NEW_STORAGE");
        } else {
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardInit");
        }
        intent.putExtra("android.os.storage.extra.DISK_ID", disk.getId());
        int requestKey = disk.getId().hashCode();
        return PendingIntent.getActivityAsUser(this.mContext, requestKey, intent, 268435456, null, UserHandle.CURRENT);
    }

    private PendingIntent buildInitPendingIntent(VolumeInfo vol) {
        Intent intent = new Intent();
        if (isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("com.android.tv.settings.action.NEW_STORAGE");
        } else {
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardInit");
        }
        intent.putExtra("android.os.storage.extra.VOLUME_ID", vol.getId());
        int requestKey = vol.getId().hashCode();
        return PendingIntent.getActivityAsUser(this.mContext, requestKey, intent, 268435456, null, UserHandle.CURRENT);
    }

    private PendingIntent buildUnmountPendingIntent(VolumeInfo vol) {
        Intent intent = new Intent();
        if (isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("com.android.tv.settings.action.UNMOUNT_STORAGE");
            intent.putExtra("android.os.storage.extra.VOLUME_ID", vol.getId());
            int requestKey = vol.getId().hashCode();
            return PendingIntent.getActivityAsUser(this.mContext, requestKey, intent, 268435456, null, UserHandle.CURRENT);
        } else if (isAutomotive()) {
            intent.setClassName("com.android.car.settings", "com.android.car.settings.storage.StorageUnmountReceiver");
            intent.putExtra("android.os.storage.extra.VOLUME_ID", vol.getId());
            int requestKey2 = vol.getId().hashCode();
            return PendingIntent.getBroadcastAsUser(this.mContext, requestKey2, intent, 268435456, UserHandle.CURRENT);
        } else {
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageUnmountReceiver");
            intent.putExtra("android.os.storage.extra.VOLUME_ID", vol.getId());
            int requestKey3 = vol.getId().hashCode();
            return PendingIntent.getBroadcastAsUser(this.mContext, requestKey3, intent, 268435456, UserHandle.CURRENT);
        }
    }

    private PendingIntent buildBrowsePendingIntent(VolumeInfo vol) {
        StrictMode.VmPolicy oldPolicy = StrictMode.allowVmViolations();
        try {
            Intent intent = vol.buildBrowseIntentForUser(vol.getMountUserId());
            int requestKey = vol.getId().hashCode();
            return PendingIntent.getActivityAsUser(this.mContext, requestKey, intent, 268435456, null, UserHandle.CURRENT);
        } finally {
            StrictMode.setVmPolicy(oldPolicy);
        }
    }

    private PendingIntent buildVolumeSettingsPendingIntent(VolumeInfo vol) {
        Intent intent = new Intent();
        if (isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("android.settings.INTERNAL_STORAGE_SETTINGS");
        } else {
            int type = vol.getType();
            if (type == 0) {
                intent.setClassName("com.android.settings", "com.android.settings.Settings$PublicVolumeSettingsActivity");
            } else if (type == 1) {
                intent.setClassName("com.android.settings", "com.android.settings.Settings$PrivateVolumeSettingsActivity");
            } else {
                return null;
            }
        }
        intent.putExtra("android.os.storage.extra.VOLUME_ID", vol.getId());
        int requestKey = vol.getId().hashCode();
        return PendingIntent.getActivityAsUser(this.mContext, requestKey, intent, 268435456, null, UserHandle.CURRENT);
    }

    private PendingIntent buildSnoozeIntent(String fsUuid) {
        Intent intent = new Intent(ACTION_SNOOZE_VOLUME);
        intent.putExtra("android.os.storage.extra.FS_UUID", fsUuid);
        int requestKey = fsUuid.hashCode();
        return PendingIntent.getBroadcastAsUser(this.mContext, requestKey, intent, 268435456, UserHandle.CURRENT);
    }

    private PendingIntent buildForgetPendingIntent(VolumeRecord rec) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$PrivateVolumeForgetActivity");
        intent.putExtra("android.os.storage.extra.FS_UUID", rec.getFsUuid());
        int requestKey = rec.getFsUuid().hashCode();
        return PendingIntent.getActivityAsUser(this.mContext, requestKey, intent, 268435456, null, UserHandle.CURRENT);
    }

    private PendingIntent buildWizardMigratePendingIntent(MoveInfo move) {
        Intent intent = new Intent();
        if (isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("com.android.tv.settings.action.MIGRATE_STORAGE");
        } else {
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardMigrateProgress");
        }
        intent.putExtra("android.content.pm.extra.MOVE_ID", move.moveId);
        VolumeInfo vol = this.mStorageManager.findVolumeByQualifiedUuid(move.volumeUuid);
        if (vol != null) {
            intent.putExtra("android.os.storage.extra.VOLUME_ID", vol.getId());
        }
        return PendingIntent.getActivityAsUser(this.mContext, move.moveId, intent, 268435456, null, UserHandle.CURRENT);
    }

    private PendingIntent buildWizardMovePendingIntent(MoveInfo move) {
        Intent intent = new Intent();
        if (isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("com.android.tv.settings.action.MOVE_APP");
        } else {
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardMoveProgress");
        }
        intent.putExtra("android.content.pm.extra.MOVE_ID", move.moveId);
        return PendingIntent.getActivityAsUser(this.mContext, move.moveId, intent, 268435456, null, UserHandle.CURRENT);
    }

    private PendingIntent buildWizardReadyPendingIntent(DiskInfo disk) {
        Intent intent = new Intent();
        if (isTv()) {
            intent.setPackage("com.android.tv.settings");
            intent.setAction("android.settings.INTERNAL_STORAGE_SETTINGS");
        } else {
            intent.setClassName("com.android.settings", "com.android.settings.deviceinfo.StorageWizardReady");
        }
        intent.putExtra("android.os.storage.extra.DISK_ID", disk.getId());
        int requestKey = disk.getId().hashCode();
        return PendingIntent.getActivityAsUser(this.mContext, requestKey, intent, 268435456, null, UserHandle.CURRENT);
    }

    private boolean isAutomotive() {
        PackageManager packageManager = this.mContext.getPackageManager();
        return packageManager.hasSystemFeature("android.hardware.type.automotive");
    }

    private boolean isTv() {
        PackageManager packageManager = this.mContext.getPackageManager();
        return packageManager.hasSystemFeature("android.software.leanback");
    }
}
