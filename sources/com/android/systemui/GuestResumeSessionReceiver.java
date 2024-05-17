package com.android.systemui;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManagerGlobal;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.phone.SystemUIDialog;
/* loaded from: classes21.dex */
public class GuestResumeSessionReceiver extends BroadcastReceiver {
    private static final String SETTING_GUEST_HAS_LOGGED_IN = "systemui.guest_has_logged_in";
    private static final String TAG = "GuestResumeSessionReceiver";
    private Dialog mNewSessionDialog;

    public void register(Context context) {
        IntentFilter f = new IntentFilter("android.intent.action.USER_SWITCHED");
        context.registerReceiverAsUser(this, UserHandle.SYSTEM, f, null, null);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.intent.action.USER_SWITCHED".equals(action)) {
            cancelDialog();
            int userId = intent.getIntExtra("android.intent.extra.user_handle", -10000);
            if (userId == -10000) {
                Log.e(TAG, intent + " sent to " + TAG + " without EXTRA_USER_HANDLE");
                return;
            }
            try {
                UserInfo currentUser = ActivityManager.getService().getCurrentUser();
                if (!currentUser.isGuest()) {
                    return;
                }
                ContentResolver cr = context.getContentResolver();
                int notFirstLogin = Settings.System.getIntForUser(cr, SETTING_GUEST_HAS_LOGGED_IN, 0, userId);
                if (notFirstLogin != 0) {
                    this.mNewSessionDialog = new ResetSessionDialog(context, userId);
                    this.mNewSessionDialog.show();
                    return;
                }
                Settings.System.putIntForUser(cr, SETTING_GUEST_HAS_LOGGED_IN, 1, userId);
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void wipeGuestSession(Context context, int userId) {
        UserManager userManager = (UserManager) context.getSystemService("user");
        try {
            UserInfo currentUser = ActivityManager.getService().getCurrentUser();
            if (currentUser.id != userId) {
                Log.w(TAG, "User requesting to start a new session (" + userId + ") is not current user (" + currentUser.id + NavigationBarInflaterView.KEY_CODE_END);
            } else if (!currentUser.isGuest()) {
                Log.w(TAG, "User requesting to start a new session (" + userId + ") is not a guest");
            } else {
                boolean marked = userManager.markGuestForDeletion(currentUser.id);
                if (!marked) {
                    Log.w(TAG, "Couldn't mark the guest for deletion for user " + userId);
                    return;
                }
                UserInfo newGuest = userManager.createGuest(context, currentUser.name);
                try {
                    if (newGuest == null) {
                        Log.e(TAG, "Could not create new guest, switching back to system user");
                        ActivityManager.getService().switchUser(0);
                        userManager.removeUser(currentUser.id);
                        WindowManagerGlobal.getWindowManagerService().lockNow((Bundle) null);
                        return;
                    }
                    ActivityManager.getService().switchUser(newGuest.id);
                    userManager.removeUser(currentUser.id);
                } catch (RemoteException e) {
                    Log.e(TAG, "Couldn't wipe session because ActivityManager or WindowManager is dead");
                }
            }
        } catch (RemoteException e2) {
            Log.e(TAG, "Couldn't wipe session because ActivityManager is dead");
        }
    }

    private void cancelDialog() {
        Dialog dialog = this.mNewSessionDialog;
        if (dialog != null && dialog.isShowing()) {
            this.mNewSessionDialog.cancel();
            this.mNewSessionDialog = null;
        }
    }

    /* loaded from: classes21.dex */
    private static class ResetSessionDialog extends SystemUIDialog implements DialogInterface.OnClickListener {
        private static final int BUTTON_DONTWIPE = -1;
        private static final int BUTTON_WIPE = -2;
        private final int mUserId;

        public ResetSessionDialog(Context context, int userId) {
            super(context);
            setTitle(context.getString(R.string.guest_wipe_session_title));
            setMessage(context.getString(R.string.guest_wipe_session_message));
            setCanceledOnTouchOutside(false);
            setButton(-2, context.getString(R.string.guest_wipe_session_wipe), this);
            setButton(-1, context.getString(R.string.guest_wipe_session_dontwipe), this);
            this.mUserId = userId;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            if (which == -2) {
                GuestResumeSessionReceiver.wipeGuestSession(getContext(), this.mUserId);
                dismiss();
            } else if (which == -1) {
                cancel();
            }
        }
    }
}
