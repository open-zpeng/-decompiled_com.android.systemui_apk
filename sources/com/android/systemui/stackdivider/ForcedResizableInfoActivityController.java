package com.android.systemui.stackdivider;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.UserHandle;
import android.util.ArraySet;
import android.widget.Toast;
import com.android.systemui.R;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
/* loaded from: classes21.dex */
public class ForcedResizableInfoActivityController {
    private static final String SELF_PACKAGE_NAME = "com.android.systemui";
    private static final int TIMEOUT = 1000;
    private final Context mContext;
    private boolean mDividerDragging;
    private final Handler mHandler = new Handler();
    private final ArraySet<PendingTaskRecord> mPendingTasks = new ArraySet<>();
    private final ArraySet<String> mPackagesShownInSession = new ArraySet<>();
    private final Runnable mTimeoutRunnable = new Runnable() { // from class: com.android.systemui.stackdivider.ForcedResizableInfoActivityController.1
        @Override // java.lang.Runnable
        public void run() {
            ForcedResizableInfoActivityController.this.showPending();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class PendingTaskRecord {
        int reason;
        int taskId;

        PendingTaskRecord(int taskId, int reason) {
            this.taskId = taskId;
            this.reason = reason;
        }
    }

    public ForcedResizableInfoActivityController(Context context) {
        this.mContext = context;
        ActivityManagerWrapper.getInstance().registerTaskStackListener(new TaskStackChangeListener() { // from class: com.android.systemui.stackdivider.ForcedResizableInfoActivityController.2
            @Override // com.android.systemui.shared.system.TaskStackChangeListener
            public void onActivityForcedResizable(String packageName, int taskId, int reason) {
                ForcedResizableInfoActivityController.this.activityForcedResizable(packageName, taskId, reason);
            }

            @Override // com.android.systemui.shared.system.TaskStackChangeListener
            public void onActivityDismissingDockedStack() {
                ForcedResizableInfoActivityController.this.activityDismissingDockedStack();
            }

            @Override // com.android.systemui.shared.system.TaskStackChangeListener
            public void onActivityLaunchOnSecondaryDisplayFailed() {
                ForcedResizableInfoActivityController.this.activityLaunchOnSecondaryDisplayFailed();
            }
        });
    }

    public void notifyDockedStackExistsChanged(boolean exists) {
        if (!exists) {
            this.mPackagesShownInSession.clear();
        }
    }

    public void onAppTransitionFinished() {
        if (!this.mDividerDragging) {
            showPending();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDraggingStart() {
        this.mDividerDragging = true;
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDraggingEnd() {
        this.mDividerDragging = false;
        showPending();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void activityForcedResizable(String packageName, int taskId, int reason) {
        if (debounce(packageName)) {
            return;
        }
        this.mPendingTasks.add(new PendingTaskRecord(taskId, reason));
        postTimeout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void activityDismissingDockedStack() {
        Toast.makeText(this.mContext, R.string.dock_non_resizeble_failed_to_dock_text, 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void activityLaunchOnSecondaryDisplayFailed() {
        Toast.makeText(this.mContext, R.string.activity_launch_on_secondary_display_failed_text, 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPending() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        for (int i = this.mPendingTasks.size() - 1; i >= 0; i--) {
            PendingTaskRecord pendingRecord = this.mPendingTasks.valueAt(i);
            Intent intent = new Intent(this.mContext, ForcedResizableInfoActivity.class);
            ActivityOptions options = ActivityOptions.makeBasic();
            options.setLaunchTaskId(pendingRecord.taskId);
            options.setTaskOverlay(true, true);
            intent.putExtra(ForcedResizableInfoActivity.EXTRA_FORCED_RESIZEABLE_REASON, pendingRecord.reason);
            this.mContext.startActivityAsUser(intent, options.toBundle(), UserHandle.CURRENT);
        }
        this.mPendingTasks.clear();
    }

    private void postTimeout() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        this.mHandler.postDelayed(this.mTimeoutRunnable, 1000L);
    }

    private boolean debounce(String packageName) {
        if (packageName == null) {
            return false;
        }
        if ("com.android.systemui".equals(packageName)) {
            return true;
        }
        boolean debounce = this.mPackagesShownInSession.contains(packageName);
        this.mPackagesShownInSession.add(packageName);
        return debounce;
    }
}
