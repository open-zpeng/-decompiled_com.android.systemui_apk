package com.android.systemui.statusbar.phone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.view.WindowManager;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.xiaopeng.systemui.helper.WindowHelper;
/* loaded from: classes21.dex */
public class SystemUIDialog extends AlertDialog {
    private final Context mContext;

    public SystemUIDialog(Context context) {
        this(context, R.style.Theme_SystemUI_Dialog);
    }

    public SystemUIDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        applyFlags(this);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.setTitle(getClass().getSimpleName());
        getWindow().setAttributes(attrs);
        registerDismissListener(this);
    }

    public void setShowForAllUsers(boolean show) {
        setShowForAllUsers(this, show);
    }

    public void setMessage(int resId) {
        setMessage(this.mContext.getString(resId));
    }

    public void setPositiveButton(int resId, DialogInterface.OnClickListener onClick) {
        setButton(-1, this.mContext.getString(resId), onClick);
    }

    public void setNegativeButton(int resId, DialogInterface.OnClickListener onClick) {
        setButton(-2, this.mContext.getString(resId), onClick);
    }

    public void setNeutralButton(int resId, DialogInterface.OnClickListener onClick) {
        setButton(-3, this.mContext.getString(resId), onClick);
    }

    public static void setShowForAllUsers(Dialog dialog, boolean show) {
        if (show) {
            dialog.getWindow().getAttributes().privateFlags |= 16;
            return;
        }
        dialog.getWindow().getAttributes().privateFlags &= -17;
    }

    public static void setWindowOnTop(Dialog dialog) {
        if (((KeyguardMonitor) Dependency.get(KeyguardMonitor.class)).isShowing()) {
            dialog.getWindow().setType(WindowHelper.TYPE_STATUS_BAR_PANEL);
        } else {
            dialog.getWindow().setType(2017);
        }
    }

    public static AlertDialog applyFlags(AlertDialog dialog) {
        dialog.getWindow().setType(WindowHelper.TYPE_STATUS_BAR_PANEL);
        dialog.getWindow().addFlags(655360);
        return dialog;
    }

    public static void registerDismissListener(Dialog dialog) {
        DismissReceiver dismissReceiver = new DismissReceiver(dialog);
        dismissReceiver.register();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class DismissReceiver extends BroadcastReceiver implements DialogInterface.OnDismissListener {
        private static final IntentFilter INTENT_FILTER = new IntentFilter();
        private final Dialog mDialog;
        private boolean mRegistered;

        static {
            INTENT_FILTER.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            INTENT_FILTER.addAction("android.intent.action.SCREEN_OFF");
        }

        DismissReceiver(Dialog dialog) {
            this.mDialog = dialog;
        }

        void register() {
            this.mDialog.getContext().registerReceiverAsUser(this, UserHandle.CURRENT, INTENT_FILTER, null, null);
            this.mRegistered = true;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            this.mDialog.dismiss();
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            if (this.mRegistered) {
                this.mDialog.getContext().unregisterReceiver(this);
                this.mRegistered = false;
            }
        }
    }
}
