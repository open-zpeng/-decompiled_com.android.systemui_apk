package com.xiaopeng.systemui.statusbar;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.infoflow.message.define.CardExtra;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class NotificationListener extends NotificationListenerService {
    public static final int EVENT_POSTED = 1;
    public static final int EVENT_REMOVED = 2;
    private static final String PKG_DOWNLOAD_PROVIDER = "com.android.providers.downloads";
    private static final String TAG = "StatusNotificationListener";
    private NotificationCallback mCallback;
    private final Context mContext;

    /* loaded from: classes24.dex */
    public interface NotificationCallback {
        void onNotificationPosted(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap);

        void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap);
    }

    public NotificationListener(Context context) {
        this.mContext = context;
    }

    public void registerAsSystemService() {
        try {
            registerAsSystemService(this.mContext, new ComponentName(this.mContext.getPackageName(), getClass().getCanonicalName()), -1);
        } catch (RemoteException e) {
            Logger.e(TAG, "Unable to register notification listener");
        }
    }

    public void setCallback(NotificationCallback callback) {
        this.mCallback = callback;
    }

    @Override // android.service.notification.NotificationListenerService
    public StatusBarNotification[] getActiveNotifications() {
        StatusBarNotification[] activeNotifications = super.getActiveNotifications();
        return activeNotifications;
    }

    @Override // android.service.notification.NotificationListenerService
    public NotificationListenerService.RankingMap getCurrentRanking() {
        NotificationListenerService.RankingMap currentRanking = super.getCurrentRanking();
        return currentRanking;
    }

    @Override // android.service.notification.NotificationListenerService
    public void onListenerConnected() {
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationPosted(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        NotificationCallback notificationCallback;
        if (sbn != null) {
            Notification notification = sbn.getNotification();
            Logger.d(TAG, "onNotificationPosted sbn=" + sbn.toString());
            StringBuilder sb = new StringBuilder();
            sb.append("onNotificationPosted notification=");
            sb.append(notification != null ? notification.toString() : "null");
            Logger.d(TAG, sb.toString());
            if ((isDownloadNotification(sbn) || isOtaNotification(notification)) && (notificationCallback = this.mCallback) != null) {
                notificationCallback.onNotificationPosted(sbn, rankingMap);
            }
            if (!CarModelsManager.getFeature().isOsdReduceSelfUse() && isOsdNotification(notification)) {
                OsdController.OsdParams params = OsdController.getOsdParams(notification);
                OsdController.getInstance(this.mContext).showOsd(params);
            }
        }
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        NotificationCallback notificationCallback;
        if (sbn != null) {
            Notification notification = sbn.getNotification();
            if ((isDownloadNotification(sbn) || isOtaNotification(notification)) && (notificationCallback = this.mCallback) != null) {
                notificationCallback.onNotificationRemoved(sbn, rankingMap);
            }
        }
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRankingUpdate(NotificationListenerService.RankingMap rankingMap) {
        Logger.d(TAG, "onRankingUpdate");
    }

    public static boolean isOsdNotification(Notification notification) {
        if (notification != null && notification.hasDisplayFlag(2)) {
            return true;
        }
        return false;
    }

    public static boolean isOtaNotification(Notification notification) {
        if (isStatusBarNotification(notification) && notification.extras != null) {
            String subType = notification.extras.getString("android.subType", "");
            if ("ota".equals(subType)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public static boolean isPackageNotification(Notification notification) {
        if (notification != null && notification.displayFlag == 32) {
            try {
                int type = -1;
                String subtext = notification.extras.containsKey("android.subText") ? notification.extras.getString("android.subText") : "";
                if (notification.extras.containsKey(CardExtra.KEY_CARD_TYPE)) {
                    type = notification.extras.getInt(CardExtra.KEY_CARD_TYPE);
                }
                if (type == 8) {
                    if (!TextUtils.isEmpty(subtext)) {
                        return true;
                    }
                }
            } catch (Exception e) {
            }
            return true;
        }
        return false;
    }

    public static boolean isDownloadNotification(StatusBarNotification sbn) {
        Notification notification;
        if (sbn != null && !PKG_DOWNLOAD_PROVIDER.equals(sbn.getPackageName()) && (notification = sbn.getNotification()) != null && notification.hasDisplayFlag(64)) {
            return true;
        }
        return false;
    }

    public static boolean isStatusBarNotification(Notification notification) {
        if (notification != null && notification.hasDisplayFlag(128)) {
            return true;
        }
        return false;
    }

    private ComponentName getPackageFromNotification(Notification notification) {
        if (notification != null && notification.displayFlag == 32) {
            try {
                String subtext = "";
                int type = -1;
                if (notification.extras.containsKey("android.subText")) {
                    subtext = notification.extras.getString("android.subText");
                }
                if (notification.extras.containsKey(CardExtra.KEY_CARD_TYPE)) {
                    type = notification.extras.getInt(CardExtra.KEY_CARD_TYPE);
                }
                if (type == 8 && !TextUtils.isEmpty(subtext)) {
                    ComponentName cn = ComponentName.unflattenFromString(subtext);
                    if (cn != null) {
                        return cn;
                    }
                    return null;
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public boolean hasNotification(int flag) {
        List<StatusBarNotification> list = getNotification(flag);
        if (list == null) {
            return false;
        }
        Logger.d(TAG, "hasNotification : list size = " + list.size() + " flag = " + flag);
        return list.size() > 0;
    }

    public StatusBarNotification getTopNotification(int flag) {
        List<StatusBarNotification> list = getNotification(flag);
        if (list != null && list.size() > 0) {
            return list.get(list.size() - 1);
        }
        return null;
    }

    public List<StatusBarNotification> getNotification(int flag) {
        List<StatusBarNotification> list = new ArrayList<>();
        StatusBarNotification[] notifications = getActiveNotifications();
        if (notifications != null && notifications.length > 0) {
            for (StatusBarNotification sbn : notifications) {
                if (sbn != null && sbn.getNotification() != null && sbn.getNotification().hasDisplayFlag(flag) && !PKG_DOWNLOAD_PROVIDER.equals(sbn.getPackageName())) {
                    list.add(sbn);
                }
            }
        }
        return list;
    }

    private void printNotification(Notification n, String msg) {
        if (n != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(msg);
            buffer.append(" ");
            if (n.extras.containsKey("android.title")) {
                buffer.append("Title=");
                buffer.append(n.extras.getString("android.title"));
                buffer.append(" ");
            }
            if (n.extras.containsKey("android.text")) {
                buffer.append("Text=");
                buffer.append(n.extras.getString("android.text"));
                buffer.append(" ");
            }
            if (n.extras.containsKey("android.subText")) {
                buffer.append("SubText=");
                buffer.append(n.extras.getString("android.subText"));
                buffer.append(" ");
            }
            if (n.extras.containsKey(CardExtra.KEY_CARD_EXTRA_DATA)) {
                buffer.append("extraData=");
                buffer.append(n.extras.getString(CardExtra.KEY_CARD_EXTRA_DATA));
                buffer.append(" ");
            }
            Logger.d(TAG, buffer.toString());
        }
    }
}
