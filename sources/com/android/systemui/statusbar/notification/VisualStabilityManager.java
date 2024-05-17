package com.android.systemui.statusbar.notification;

import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import androidx.collection.ArraySet;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class VisualStabilityManager implements OnHeadsUpChangedListener, Dumpable {
    private static final long TEMPORARY_REORDERING_ALLOWED_DURATION = 1000;
    private final Handler mHandler;
    private boolean mIsTemporaryReorderingAllowed;
    private boolean mPanelExpanded;
    private NotificationPresenter mPresenter;
    private boolean mPulsing;
    private boolean mReorderingAllowed;
    private boolean mScreenOn;
    private long mTemporaryReorderingStart;
    private VisibilityLocationProvider mVisibilityLocationProvider;
    private final ArrayList<Callback> mCallbacks = new ArrayList<>();
    private ArraySet<View> mAllowedReorderViews = new ArraySet<>();
    private ArraySet<NotificationEntry> mLowPriorityReorderingViews = new ArraySet<>();
    private ArraySet<View> mAddedChildren = new ArraySet<>();
    private final Runnable mOnTemporaryReorderingExpired = new Runnable() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$VisualStabilityManager$6rf_6W4K3PrMdhwP_O1LDBveJ6k
        @Override // java.lang.Runnable
        public final void run() {
            VisualStabilityManager.this.lambda$new$0$VisualStabilityManager();
        }
    };

    /* loaded from: classes21.dex */
    public interface Callback {
        void onReorderingAllowed();
    }

    @Inject
    public VisualStabilityManager(NotificationEntryManager notificationEntryManager, @Named("main_handler") Handler handler) {
        this.mHandler = handler;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() { // from class: com.android.systemui.statusbar.notification.VisualStabilityManager.1
            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPreEntryUpdated(NotificationEntry entry) {
                boolean mAmbientStateHasChanged = entry.ambient != entry.getRow().isLowPriority();
                if (mAmbientStateHasChanged) {
                    VisualStabilityManager.this.mLowPriorityReorderingViews.add(entry);
                }
            }

            @Override // com.android.systemui.statusbar.notification.NotificationEntryListener
            public void onPostEntryUpdated(NotificationEntry entry) {
                VisualStabilityManager.this.mLowPriorityReorderingViews.remove(entry);
            }
        });
    }

    public void setUpWithPresenter(NotificationPresenter presenter) {
        this.mPresenter = presenter;
    }

    public void addReorderingAllowedCallback(Callback callback) {
        if (this.mCallbacks.contains(callback)) {
            return;
        }
        this.mCallbacks.add(callback);
    }

    public void setPanelExpanded(boolean expanded) {
        this.mPanelExpanded = expanded;
        updateReorderingAllowed();
    }

    public void setScreenOn(boolean screenOn) {
        this.mScreenOn = screenOn;
        updateReorderingAllowed();
    }

    public void setPulsing(boolean pulsing) {
        if (this.mPulsing == pulsing) {
            return;
        }
        this.mPulsing = pulsing;
        updateReorderingAllowed();
    }

    private void updateReorderingAllowed() {
        boolean changedToTrue = true;
        boolean reorderingAllowed = ((this.mScreenOn && this.mPanelExpanded && !this.mIsTemporaryReorderingAllowed) || this.mPulsing) ? false : true;
        if (!reorderingAllowed || this.mReorderingAllowed) {
            changedToTrue = false;
        }
        this.mReorderingAllowed = reorderingAllowed;
        if (changedToTrue) {
            notifyCallbacks();
        }
    }

    private void notifyCallbacks() {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            Callback callback = this.mCallbacks.get(i);
            callback.onReorderingAllowed();
        }
        this.mCallbacks.clear();
    }

    public boolean isReorderingAllowed() {
        return this.mReorderingAllowed;
    }

    public boolean canReorderNotification(ExpandableNotificationRow row) {
        if (this.mReorderingAllowed || this.mAddedChildren.contains(row) || this.mLowPriorityReorderingViews.contains(row.getEntry())) {
            return true;
        }
        return this.mAllowedReorderViews.contains(row) && !this.mVisibilityLocationProvider.isInVisibleLocation(row.getEntry());
    }

    public void setVisibilityLocationProvider(VisibilityLocationProvider visibilityLocationProvider) {
        this.mVisibilityLocationProvider = visibilityLocationProvider;
    }

    public void onReorderingFinished() {
        this.mAllowedReorderViews.clear();
        this.mAddedChildren.clear();
        this.mLowPriorityReorderingViews.clear();
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpStateChanged(NotificationEntry entry, boolean isHeadsUp) {
        if (isHeadsUp) {
            this.mAllowedReorderViews.add(entry.getRow());
        }
    }

    public void temporarilyAllowReordering() {
        this.mHandler.removeCallbacks(this.mOnTemporaryReorderingExpired);
        this.mHandler.postDelayed(this.mOnTemporaryReorderingExpired, 1000L);
        if (!this.mIsTemporaryReorderingAllowed) {
            this.mTemporaryReorderingStart = SystemClock.elapsedRealtime();
        }
        this.mIsTemporaryReorderingAllowed = true;
        updateReorderingAllowed();
    }

    public /* synthetic */ void lambda$new$0$VisualStabilityManager() {
        this.mIsTemporaryReorderingAllowed = false;
        updateReorderingAllowed();
    }

    public void notifyViewAddition(View view) {
        this.mAddedChildren.add(view);
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("VisualStabilityManager state:");
        pw.print("  mIsTemporaryReorderingAllowed=");
        pw.println(this.mIsTemporaryReorderingAllowed);
        pw.print("  mTemporaryReorderingStart=");
        pw.println(this.mTemporaryReorderingStart);
        long now = SystemClock.elapsedRealtime();
        pw.print("    Temporary reordering window has been open for ");
        pw.print(now - (this.mIsTemporaryReorderingAllowed ? this.mTemporaryReorderingStart : now));
        pw.println("ms");
        pw.println();
    }
}
