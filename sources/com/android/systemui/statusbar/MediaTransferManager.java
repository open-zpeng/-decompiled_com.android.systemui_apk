package com.android.systemui.statusbar;

import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.util.FeatureFlagUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.android.settingslib.media.MediaOutputSliceConstants;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
/* loaded from: classes21.dex */
public class MediaTransferManager {
    private final Context mContext;
    private final View.OnClickListener mOnClickHandler = new View.OnClickListener() { // from class: com.android.systemui.statusbar.MediaTransferManager.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (handleMediaTransfer(view)) {
            }
        }

        private boolean handleMediaTransfer(View view) {
            if (view.findViewById(16909203) == null) {
                return false;
            }
            ViewParent parent = view.getParent();
            StatusBarNotification statusBarNotification = getNotificationForParent(parent);
            Intent intent = new Intent().setAction(MediaOutputSliceConstants.ACTION_MEDIA_OUTPUT).putExtra(MediaOutputSliceConstants.EXTRA_PACKAGE_NAME, statusBarNotification.getPackageName());
            MediaTransferManager.this.mActivityStarter.startActivity(intent, false, true, 268468224);
            return true;
        }

        private StatusBarNotification getNotificationForParent(ViewParent parent) {
            while (parent != null) {
                if (parent instanceof ExpandableNotificationRow) {
                    return ((ExpandableNotificationRow) parent).getStatusBarNotification();
                }
                parent = parent.getParent();
            }
            return null;
        }
    };
    private final ActivityStarter mActivityStarter = (ActivityStarter) Dependency.get(ActivityStarter.class);

    public MediaTransferManager(Context context) {
        this.mContext = context;
    }

    public void applyMediaTransferView(ViewGroup root, NotificationEntry entry) {
        View view;
        if (!FeatureFlagUtils.isEnabled(this.mContext, "settings_seamless_transfer") || (view = root.findViewById(16909203)) == null) {
            return;
        }
        view.setVisibility(0);
        view.setOnClickListener(this.mOnClickHandler);
    }
}
