package com.android.systemui.statusbar.notification;

import android.app.Notification;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import com.android.systemui.DejankUtils;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.phone.ShadeController;
/* loaded from: classes21.dex */
public final class NotificationClicker implements View.OnClickListener {
    private static final String TAG = "NotificationClicker";
    private final BubbleController mBubbleController;
    private final NotificationActivityStarter mNotificationActivityStarter;
    private final ShadeController mShadeController;

    public NotificationClicker(ShadeController shadeController, BubbleController bubbleController, NotificationActivityStarter notificationActivityStarter) {
        this.mShadeController = shadeController;
        this.mBubbleController = bubbleController;
        this.mNotificationActivityStarter = notificationActivityStarter;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (!(v instanceof ExpandableNotificationRow)) {
            Log.e(TAG, "NotificationClicker called on a view that is not a notification row.");
            return;
        }
        this.mShadeController.wakeUpIfDozing(SystemClock.uptimeMillis(), v, "NOTIFICATION_CLICK");
        final ExpandableNotificationRow row = (ExpandableNotificationRow) v;
        StatusBarNotification sbn = row.getStatusBarNotification();
        if (sbn == null) {
            Log.e(TAG, "NotificationClicker called on an unclickable notification,");
        } else if (isMenuVisible(row)) {
            row.animateTranslateNotification(0.0f);
        } else if (row.isChildInGroup() && isMenuVisible(row.getNotificationParent())) {
            row.getNotificationParent().animateTranslateNotification(0.0f);
        } else if (row.isSummaryWithChildren() && row.areChildrenExpanded()) {
        } else {
            row.setJustClicked(true);
            DejankUtils.postAfterTraversal(new Runnable() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$NotificationClicker$nu7GkRUU5fnNpaTPmGoBAs8FJoU
                @Override // java.lang.Runnable
                public final void run() {
                    ExpandableNotificationRow.this.setJustClicked(false);
                }
            });
            if (!row.getEntry().isBubble()) {
                this.mBubbleController.collapseStack();
            }
            this.mNotificationActivityStarter.onNotificationClicked(sbn, row);
        }
    }

    private boolean isMenuVisible(ExpandableNotificationRow row) {
        return row.getProvider() != null && row.getProvider().isMenuVisible();
    }

    public void register(ExpandableNotificationRow row, StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        if (notification.contentIntent != null || notification.fullScreenIntent != null || row.getEntry().isBubble()) {
            row.setOnClickListener(this);
        } else {
            row.setOnClickListener(null);
        }
    }
}
