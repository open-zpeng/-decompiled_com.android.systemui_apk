package com.android.systemui.statusbar;

import android.app.Notification;
import android.os.RemoteException;
import android.util.ArraySet;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class SmartReplyController {
    private final IStatusBarService mBarService;
    private Callback mCallback;
    private final NotificationEntryManager mEntryManager;
    private Set<String> mSendingKeys = new ArraySet();

    /* loaded from: classes21.dex */
    public interface Callback {
        void onSmartReplySent(NotificationEntry notificationEntry, CharSequence charSequence);
    }

    @Inject
    public SmartReplyController(NotificationEntryManager entryManager, IStatusBarService statusBarService) {
        this.mBarService = statusBarService;
        this.mEntryManager = entryManager;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void smartReplySent(NotificationEntry entry, int replyIndex, CharSequence reply, int notificationLocation, boolean modifiedBeforeSending) {
        this.mCallback.onSmartReplySent(entry, reply);
        this.mSendingKeys.add(entry.key);
        try {
            this.mBarService.onNotificationSmartReplySent(entry.notification.getKey(), replyIndex, reply, notificationLocation, modifiedBeforeSending);
        } catch (RemoteException e) {
        }
    }

    public void smartActionClicked(NotificationEntry entry, int actionIndex, Notification.Action action, boolean generatedByAssistant) {
        int count = this.mEntryManager.getNotificationData().getActiveNotifications().size();
        int rank = this.mEntryManager.getNotificationData().getRank(entry.key);
        NotificationVisibility.NotificationLocation location = NotificationLogger.getNotificationLocation(entry);
        NotificationVisibility nv = NotificationVisibility.obtain(entry.key, rank, count, true, location);
        try {
            this.mBarService.onNotificationActionClick(entry.key, actionIndex, action, nv, generatedByAssistant);
        } catch (RemoteException e) {
        }
    }

    public boolean isSendingSmartReply(String key) {
        return this.mSendingKeys.contains(key);
    }

    public void smartSuggestionsAdded(NotificationEntry entry, int replyCount, int actionCount, boolean generatedByAssistant, boolean editBeforeSending) {
        try {
            this.mBarService.onNotificationSmartSuggestionsAdded(entry.notification.getKey(), replyCount, actionCount, generatedByAssistant, editBeforeSending);
        } catch (RemoteException e) {
        }
    }

    public void stopSending(NotificationEntry entry) {
        if (entry != null) {
            this.mSendingKeys.remove(entry.notification.getKey());
        }
    }
}
