package com.android.systemui.keyguard;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class WorkLockActivity extends Activity {
    static final String EXTRA_TASK_DESCRIPTION = "com.android.systemui.keyguard.extra.TASK_DESCRIPTION";
    private static final int REQUEST_CODE_CONFIRM_CREDENTIALS = 1;
    private static final String TAG = "WorkLockActivity";
    private KeyguardManager mKgm;
    private final BroadcastReceiver mLockEventReceiver = new BroadcastReceiver() { // from class: com.android.systemui.keyguard.WorkLockActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int targetUserId = WorkLockActivity.this.getTargetUserId();
            int userId = intent.getIntExtra("android.intent.extra.user_handle", targetUserId);
            if (userId == targetUserId && !WorkLockActivity.this.getKeyguardManager().isDeviceLocked(targetUserId)) {
                WorkLockActivity.this.finish();
            }
        }
    };

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiverAsUser(this.mLockEventReceiver, UserHandle.ALL, new IntentFilter("android.intent.action.DEVICE_LOCKED_CHANGED"), null, null);
        if (!getKeyguardManager().isDeviceLocked(getTargetUserId())) {
            finish();
            return;
        }
        setOverlayWithDecorCaptionEnabled(true);
        View blankView = new View(this);
        blankView.setContentDescription(getString(R.string.accessibility_desc_work_lock));
        blankView.setBackgroundColor(getPrimaryColor());
        setContentView(blankView);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            showConfirmCredentialActivity();
        }
    }

    @Override // android.app.Activity
    public void onDestroy() {
        unregisterReceiver(this.mLockEventReceiver);
        super.onDestroy();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
    }

    @Override // android.app.Activity
    public void setTaskDescription(ActivityManager.TaskDescription taskDescription) {
    }

    private void showConfirmCredentialActivity() {
        Intent credential;
        if (isFinishing() || !getKeyguardManager().isDeviceLocked(getTargetUserId()) || (credential = getKeyguardManager().createConfirmDeviceCredentialIntent(null, null, getTargetUserId())) == null) {
            return;
        }
        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLaunchTaskId(getTaskId());
        PendingIntent target = PendingIntent.getActivity(this, -1, getIntent(), 1409286144, options.toBundle());
        if (target != null) {
            credential.putExtra("android.intent.extra.INTENT", target.getIntentSender());
        }
        startActivityForResult(credential, 1);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode != -1) {
            goToHomeScreen();
        }
    }

    private void goToHomeScreen() {
        Intent homeIntent = new Intent("android.intent.action.MAIN");
        homeIntent.addCategory("android.intent.category.HOME");
        homeIntent.setFlags(268435456);
        startActivity(homeIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public KeyguardManager getKeyguardManager() {
        if (this.mKgm == null) {
            this.mKgm = (KeyguardManager) getSystemService("keyguard");
        }
        return this.mKgm;
    }

    @VisibleForTesting
    final int getTargetUserId() {
        return getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
    }

    @VisibleForTesting
    final int getPrimaryColor() {
        ActivityManager.TaskDescription taskDescription = (ActivityManager.TaskDescription) getIntent().getExtra(EXTRA_TASK_DESCRIPTION);
        if (taskDescription != null && Color.alpha(taskDescription.getPrimaryColor()) == 255) {
            return taskDescription.getPrimaryColor();
        }
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService("device_policy");
        return devicePolicyManager.getOrganizationColorForUser(getTargetUserId());
    }
}
