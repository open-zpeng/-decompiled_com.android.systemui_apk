package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.metrics.LogMaker;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.logging.NotificationCounters;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationBlockingHelperManager {
    private static final boolean DEBUG = false;
    private static final String TAG = "BlockingHelper";
    private ExpandableNotificationRow mBlockingHelperRow;
    private final Context mContext;
    private boolean mIsShadeExpanded;
    private MetricsLogger mMetricsLogger = new MetricsLogger();
    private Set<String> mNonBlockablePkgs = new HashSet();

    @Inject
    public NotificationBlockingHelperManager(Context context) {
        this.mContext = context;
        Collections.addAll(this.mNonBlockablePkgs, this.mContext.getResources().getStringArray(17236054));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean perhapsShowBlockingHelper(ExpandableNotificationRow row, NotificationMenuRowPlugin menuRow) {
        if (row.getEntry().userSentiment == -1 && this.mIsShadeExpanded && !row.getIsNonblockable() && ((!row.isChildInGroup() || row.isOnlyChildInGroup()) && row.getNumUniqueChannels() <= 1)) {
            dismissCurrentBlockingHelper();
            NotificationGutsManager manager = (NotificationGutsManager) Dependency.get(NotificationGutsManager.class);
            this.mBlockingHelperRow = row;
            this.mBlockingHelperRow.setBlockingHelperShowing(true);
            this.mMetricsLogger.write(getLogMaker().setSubtype(3));
            manager.openGuts(this.mBlockingHelperRow, 0, 0, menuRow.getLongpressMenuItem(this.mContext));
            ((MetricsLogger) Dependency.get(MetricsLogger.class)).count(NotificationCounters.BLOCKING_HELPER_SHOWN, 1);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean dismissCurrentBlockingHelper() {
        if (isBlockingHelperRowNull()) {
            return false;
        }
        if (!this.mBlockingHelperRow.isBlockingHelperShowing()) {
            Log.e(TAG, "Manager.dismissCurrentBlockingHelper: Non-null row is not showing a blocking helper");
        }
        this.mBlockingHelperRow.setBlockingHelperShowing(false);
        if (this.mBlockingHelperRow.isAttachedToWindow()) {
            ((NotificationEntryManager) Dependency.get(NotificationEntryManager.class)).updateNotifications();
        }
        this.mBlockingHelperRow = null;
        return true;
    }

    public void setNotificationShadeExpanded(float expandedHeight) {
        this.mIsShadeExpanded = expandedHeight > 0.0f;
    }

    public boolean isNonblockable(String packageName, String channelName) {
        return this.mNonBlockablePkgs.contains(packageName) || this.mNonBlockablePkgs.contains(makeChannelKey(packageName, channelName));
    }

    private LogMaker getLogMaker() {
        return this.mBlockingHelperRow.getStatusBarNotification().getLogMaker().setCategory(1621);
    }

    private String makeChannelKey(String pkg, String channel) {
        return pkg + NavigationBarInflaterView.KEY_IMAGE_DELIM + channel;
    }

    @VisibleForTesting
    boolean isBlockingHelperRowNull() {
        return this.mBlockingHelperRow == null;
    }

    @VisibleForTesting
    void setBlockingHelperRowForTest(ExpandableNotificationRow blockingHelperRowForTest) {
        this.mBlockingHelperRow = blockingHelperRowForTest;
    }

    @VisibleForTesting
    void setNonBlockablePkgs(String[] pkgsAndChannels) {
        this.mNonBlockablePkgs = new HashSet();
        Collections.addAll(this.mNonBlockablePkgs, pkgsAndChannels);
    }
}
