package com.xiaopeng.systemui.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaopeng.systemui.utils.Logger;
/* loaded from: classes24.dex */
public class UpgradeController {
    private static final String TAG = "UpgradeController";
    private static final String UPGRADE_CANCEL_ACTION = "com.xiaopeng.ota.intent.action.ACTION_UPGRADE_CANCEL";
    private static final String UPGRADE_READY_ACTION = "com.xiaopeng.ota.intent.action.ACTION_UPGRADE_READY";
    private BroadcastReceiver mBroadcastReceiver;
    private Context mContext;
    private OnUpgradeStatusChangeListener mOnUpgradeStatusChangeListener;

    /* loaded from: classes24.dex */
    public interface OnUpgradeStatusChangeListener {
        void OnUpgradeStatusChange(boolean z);
    }

    public UpgradeController(Context context) {
        Logger.d(TAG, TAG);
        this.mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPGRADE_READY_ACTION);
        filter.addAction(UPGRADE_CANCEL_ACTION);
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.controller.UpgradeController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent == null) {
                    return;
                }
                Logger.d(UpgradeController.TAG, "onReceive : " + intent.getAction());
                String action = intent.getAction();
                char c = 65535;
                int hashCode = action.hashCode();
                if (hashCode != 141053818) {
                    if (hashCode == 1403982979 && action.equals(UpgradeController.UPGRADE_READY_ACTION)) {
                        c = 1;
                    }
                } else if (action.equals(UpgradeController.UPGRADE_CANCEL_ACTION)) {
                    c = 0;
                }
                if (c == 0) {
                    UpgradeController.this.mOnUpgradeStatusChangeListener.OnUpgradeStatusChange(false);
                } else if (c == 1) {
                    UpgradeController.this.mOnUpgradeStatusChangeListener.OnUpgradeStatusChange(true);
                }
            }
        };
        context.registerReceiver(this.mBroadcastReceiver, filter);
    }

    public void destroy() {
        Logger.d(TAG, "destroy");
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
    }

    public void setOnUpgradeStatusChangeListener(OnUpgradeStatusChangeListener listener) {
        this.mOnUpgradeStatusChangeListener = listener;
    }
}
