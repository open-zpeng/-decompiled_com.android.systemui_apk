package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.View;
import com.android.systemui.statusbar.notification.ImageTransformState;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
/* loaded from: classes21.dex */
public class NotificationBigPictureTemplateViewWrapper extends NotificationTemplateViewWrapper {
    /* JADX INFO: Access modifiers changed from: protected */
    public NotificationBigPictureTemplateViewWrapper(Context ctx, View view, ExpandableNotificationRow row) {
        super(ctx, view, row);
    }

    @Override // com.android.systemui.statusbar.notification.row.wrapper.NotificationTemplateViewWrapper, com.android.systemui.statusbar.notification.row.wrapper.NotificationHeaderViewWrapper, com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper
    public void onContentUpdated(ExpandableNotificationRow row) {
        super.onContentUpdated(row);
        updateImageTag(row.getStatusBarNotification());
    }

    private void updateImageTag(StatusBarNotification notification) {
        Bundle extras = notification.getNotification().extras;
        Icon overRiddenIcon = (Icon) extras.getParcelable("android.largeIcon.big");
        if (overRiddenIcon != null) {
            this.mPicture.setTag(ImageTransformState.ICON_TAG, overRiddenIcon);
        }
    }
}
