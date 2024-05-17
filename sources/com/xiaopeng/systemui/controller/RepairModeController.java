package com.xiaopeng.systemui.controller;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.xiaopeng.aftersales.manager.AfterSalesManager;
import com.xiaopeng.aftersales.manager.AuthModeListener;
import com.xiaopeng.aftersales.manager.RepairModeListener;
import com.xiaopeng.systemui.utils.Logger;
/* loaded from: classes24.dex */
public class RepairModeController {
    private static final String INTENT_QUIT_AUTH_MODE = "com.xiaopeng.systemui.intent.action.QUIT_AUTH_MODE";
    private static final String INTENT_QUIT_REPAIR_MODE = "com.xiaopeng.systemui.intent.action.QUIT_REPAIR_MODE";
    private static final String TAG = "RepairModeController";
    private AfterSalesManager mAfterSalesManager;
    private Context mContext;
    private OnAuthModeChangedListener mOnAuthModeChangedListener;
    private OnRepairModeChangeChangeListener mOnRepairModeChangeChangeListener;
    private ServiceConnection mAfterSalesServiceConnection = new ServiceConnection() { // from class: com.xiaopeng.systemui.controller.RepairModeController.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.i(RepairModeController.TAG, "AfterSales onServiceConnected, name: " + name + ", service: " + service);
            RepairModeController.this.mAfterSalesManager.registerRepairModeListener(RepairModeController.this.mRepairModeListener);
            RepairModeController.this.mAfterSalesManager.registerAuthModeListener(RepairModeController.this.mAuthModeListener);
            RepairModeController repairModeController = RepairModeController.this;
            repairModeController.onRepairModeChangedImpl(repairModeController.isInRepairMode(), 2);
            RepairModeController repairModeController2 = RepairModeController.this;
            repairModeController2.onAuthModeChangedImpl(repairModeController2.isInAuthMode(), 2);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Logger.i(RepairModeController.TAG, "AfterSales onServiceDisconnected, name: " + name);
            RepairModeController.this.mAfterSalesManager.unregisterRepairModeListener(RepairModeController.this.mRepairModeListener);
            RepairModeController.this.mAfterSalesManager.unregisterAuthModeListener(RepairModeController.this.mAuthModeListener);
        }
    };
    private RepairModeListener mRepairModeListener = new RepairModeListener() { // from class: com.xiaopeng.systemui.controller.RepairModeController.2
        public void onRepairModeChanged(boolean onOff, int switchResult) {
            Logger.d(RepairModeController.TAG, "onRepairModeChanged : onOff = " + onOff);
            RepairModeController.this.onRepairModeChangedImpl(onOff, switchResult);
        }
    };
    private AuthModeListener mAuthModeListener = new AuthModeListener() { // from class: com.xiaopeng.systemui.controller.RepairModeController.3
        public void onAuthModeChanged(boolean onOff, int switchResult) {
            Logger.d(RepairModeController.TAG, "onAuthModeChanged : onOff = " + onOff);
            RepairModeController.this.onAuthModeChangedImpl(onOff, switchResult);
        }
    };

    /* loaded from: classes24.dex */
    public interface OnAuthModeChangedListener {
        void onAuthModeChanged(boolean z, int i);
    }

    /* loaded from: classes24.dex */
    public interface OnRepairModeChangeChangeListener {
        void onRepairModeChanged(boolean z, int i);
    }

    public void changeRepairModeStatus(boolean status) {
        try {
            if (status) {
                this.mAfterSalesManager.enableRepairMode();
            } else {
                this.mAfterSalesManager.disableRepairMode();
            }
        } catch (Exception e) {
        }
    }

    public long getAuthEndTime() {
        return this.mAfterSalesManager.getAuthEndTime();
    }

    public boolean isInAuthMode() {
        try {
            return this.mAfterSalesManager.getAuthMode();
        } catch (Exception e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRepairModeChangedImpl(boolean onOff, int switchResult) {
        OnRepairModeChangeChangeListener onRepairModeChangeChangeListener = this.mOnRepairModeChangeChangeListener;
        if (onRepairModeChangeChangeListener != null) {
            onRepairModeChangeChangeListener.onRepairModeChanged(onOff, switchResult);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAuthModeChangedImpl(boolean onOff, int switchResult) {
        OnAuthModeChangedListener onAuthModeChangedListener = this.mOnAuthModeChangedListener;
        if (onAuthModeChangedListener != null) {
            onAuthModeChangedListener.onAuthModeChanged(onOff, switchResult);
        }
    }

    public RepairModeController(Context context) {
        Logger.d(TAG, TAG);
        this.mContext = context;
        this.mAfterSalesManager = AfterSalesManager.createAfterSalesManager(this.mContext, this.mAfterSalesServiceConnection);
        this.mAfterSalesManager.connect();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_QUIT_REPAIR_MODE);
        intentFilter.addAction(INTENT_QUIT_AUTH_MODE);
        context.registerReceiver(new BroadcastReceiver() { // from class: com.xiaopeng.systemui.controller.RepairModeController.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                char c;
                String action = intent.getAction();
                int hashCode = action.hashCode();
                if (hashCode != -1284987257) {
                    if (hashCode == 1876744322 && action.equals(RepairModeController.INTENT_QUIT_REPAIR_MODE)) {
                        c = 0;
                    }
                    c = 65535;
                } else {
                    if (action.equals(RepairModeController.INTENT_QUIT_AUTH_MODE)) {
                        c = 1;
                    }
                    c = 65535;
                }
                if (c == 0) {
                    RepairModeController.this.changeRepairModeStatus(false);
                } else if (c == 1) {
                    RepairModeController.this.mAfterSalesManager.disableAuthMode();
                }
            }
        }, intentFilter);
    }

    public void destroy() {
        Logger.d(TAG, "destroy");
        this.mAfterSalesManager.unregisterRepairModeListener(this.mRepairModeListener);
        this.mAfterSalesManager.unregisterAuthModeListener(this.mAuthModeListener);
    }

    public boolean isInRepairMode() {
        try {
            return this.mAfterSalesManager.getRepairMode();
        } catch (Exception e) {
            return false;
        }
    }

    public void setOnRepairModeChangeChangeListener(OnRepairModeChangeChangeListener listener) {
        this.mOnRepairModeChangeChangeListener = listener;
    }

    public void setOnAuthModeChangedListener(OnAuthModeChangedListener listener) {
        this.mOnAuthModeChangedListener = listener;
    }
}
