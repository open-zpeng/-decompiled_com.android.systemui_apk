package com.android.systemui.statusbar;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationListenerWithPlugins;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.ArrayList;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
@SuppressLint({"OverrideAbstract"})
/* loaded from: classes21.dex */
public class NotificationListener extends NotificationListenerWithPlugins {
    private static final String TAG = "NotificationListener";
    private final Context mContext;
    private final NotificationRemoteInputManager mRemoteInputManager = (NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class);
    private final NotificationEntryManager mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);
    private final NotificationGroupManager mGroupManager = (NotificationGroupManager) Dependency.get(NotificationGroupManager.class);
    private final ArrayList<NotificationSettingsListener> mSettingsListeners = new ArrayList<>();

    @Inject
    public NotificationListener(Context context) {
        this.mContext = context;
    }

    public void addNotificationSettingsListener(NotificationSettingsListener listener) {
        this.mSettingsListeners.add(listener);
    }

    @Override // android.service.notification.NotificationListenerService
    public void onListenerConnected() {
        onPluginConnected();
        final StatusBarNotification[] notifications = getActiveNotifications();
        if (notifications == null) {
            Log.w(TAG, "onListenerConnected unable to get active notifications.");
            return;
        }
        final NotificationListenerService.RankingMap currentRanking = getCurrentRanking();
        ((Handler) Dependency.get(Dependency.MAIN_HANDLER)).post(new Runnable() { // from class: com.android.systemui.statusbar.-$$Lambda$NotificationListener$IqvG8K3BFQSXJ_G1S_U_QONW3G4
            @Override // java.lang.Runnable
            public final void run() {
                NotificationListener.this.lambda$onListenerConnected$0$NotificationListener(notifications, currentRanking);
            }
        });
        NotificationManager noMan = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        onSilentStatusBarIconsVisibilityChanged(noMan.shouldHideSilentStatusBarIcons());
    }

    public /* synthetic */ void lambda$onListenerConnected$0$NotificationListener(StatusBarNotification[] notifications, NotificationListenerService.RankingMap currentRanking) {
        for (StatusBarNotification sbn : notifications) {
            this.mEntryManager.addNotification(sbn, currentRanking);
        }
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationPosted(final StatusBarNotification sbn, final NotificationListenerService.RankingMap rankingMap) {
        if (sbn != null && !onPluginNotificationPosted(sbn, rankingMap)) {
            ((Handler) Dependency.get(Dependency.MAIN_HANDLER)).post(new Runnable() { // from class: com.android.systemui.statusbar.-$$Lambda$NotificationListener$NvFmU0XrVPuc5pizHcri9I0apkw
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationListener.this.lambda$onNotificationPosted$1$NotificationListener(sbn, rankingMap);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onNotificationPosted$1$NotificationListener(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        RemoteInputController.processForRemoteInput(sbn.getNotification(), this.mContext);
        String key = sbn.getKey();
        boolean isUpdate = this.mEntryManager.getNotificationData().get(key) != null;
        if (!StatusBar.ENABLE_CHILD_NOTIFICATIONS && this.mGroupManager.isChildInGroupWithSummary(sbn)) {
            if (isUpdate) {
                this.mEntryManager.removeNotification(key, rankingMap, 0);
            } else {
                this.mEntryManager.getNotificationData().updateRanking(rankingMap);
            }
        } else if (isUpdate) {
            this.mEntryManager.updateNotification(sbn, rankingMap);
        } else {
            this.mEntryManager.addNotification(sbn, rankingMap);
        }
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRemoved(StatusBarNotification sbn, final NotificationListenerService.RankingMap rankingMap, final int reason) {
        if (sbn != null && !onPluginNotificationRemoved(sbn, rankingMap)) {
            final String key = sbn.getKey();
            ((Handler) Dependency.get(Dependency.MAIN_HANDLER)).post(new Runnable() { // from class: com.android.systemui.statusbar.-$$Lambda$NotificationListener$OjTZipKiRzOnJVuWnYuZIfR5DJ0
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationListener.this.lambda$onNotificationRemoved$2$NotificationListener(key, rankingMap, reason);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onNotificationRemoved$2$NotificationListener(String key, NotificationListenerService.RankingMap rankingMap, int reason) {
        this.mEntryManager.removeNotification(key, rankingMap, reason);
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        onNotificationRemoved(sbn, rankingMap, 0);
    }

    @Override // android.service.notification.NotificationListenerService
    public void onNotificationRankingUpdate(NotificationListenerService.RankingMap rankingMap) {
        if (rankingMap != null) {
            final NotificationListenerService.RankingMap r = onPluginRankingUpdate(rankingMap);
            ((Handler) Dependency.get(Dependency.MAIN_HANDLER)).post(new Runnable() { // from class: com.android.systemui.statusbar.-$$Lambda$NotificationListener$MPB4hTnfgfJz099PViVIkkbEBIE
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationListener.this.lambda$onNotificationRankingUpdate$3$NotificationListener(r);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onNotificationRankingUpdate$3$NotificationListener(NotificationListenerService.RankingMap r) {
        this.mEntryManager.updateNotificationRanking(r);
    }

    @Override // android.service.notification.NotificationListenerService
    public void onSilentStatusBarIconsVisibilityChanged(boolean hideSilentStatusIcons) {
        Iterator<NotificationSettingsListener> it = this.mSettingsListeners.iterator();
        while (it.hasNext()) {
            NotificationSettingsListener listener = it.next();
            listener.onStatusBarIconsBehaviorChanged(hideSilentStatusIcons);
        }
    }

    public void registerAsSystemService() {
        try {
            registerAsSystemService(this.mContext, new ComponentName(this.mContext.getPackageName(), getClass().getCanonicalName()), -1);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to register notification listener", e);
        }
    }

    /* loaded from: classes21.dex */
    public interface NotificationSettingsListener {
        default void onStatusBarIconsBehaviorChanged(boolean hideSilentStatusIcons) {
        }
    }
}
