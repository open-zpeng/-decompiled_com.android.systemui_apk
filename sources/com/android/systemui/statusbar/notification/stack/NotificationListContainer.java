package com.android.systemui.statusbar.notification.stack;

import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.VisibilityLocationProvider;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
/* loaded from: classes21.dex */
public interface NotificationListContainer extends ExpandableView.OnHeightChangedListener, VisibilityLocationProvider {
    void addContainerView(View view);

    void changeViewPosition(ExpandableView expandableView, int i);

    void cleanUpViewStateForEntry(NotificationEntry notificationEntry);

    void generateAddAnimation(ExpandableView expandableView, boolean z);

    void generateChildOrderChangedEvent();

    View getContainerChildAt(int i);

    int getContainerChildCount();

    NotificationSwipeActionHelper getSwipeActionHelper();

    ViewGroup getViewParentForNotification(NotificationEntry notificationEntry);

    boolean hasPulsingNotifications();

    void notifyGroupChildAdded(ExpandableView expandableView);

    void notifyGroupChildRemoved(ExpandableView expandableView, ViewGroup viewGroup);

    void removeContainerView(View view);

    void resetExposedMenuView(boolean z, boolean z2);

    void setChildLocationsChangedListener(NotificationLogger.OnChildLocationsChangedListener onChildLocationsChangedListener);

    void setChildTransferInProgress(boolean z);

    void setMaxDisplayedNotifications(int i);

    default void onNotificationViewUpdateFinished() {
    }

    default void applyExpandAnimationParams(ActivityLaunchAnimator.ExpandAnimationParameters params) {
    }

    default void setExpandingNotification(ExpandableNotificationRow row) {
    }

    default void bindRow(ExpandableNotificationRow row) {
    }

    default boolean containsView(View v) {
        return true;
    }

    default void setWillExpand(boolean willExpand) {
    }
}
