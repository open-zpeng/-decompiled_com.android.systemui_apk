package com.xiaopeng.systemui.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserManager;
import android.util.Log;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class BootManager {
    private static final String TAG = BootManager.class.getSimpleName();
    private static Context mContext = SystemUIApplication.getContext();
    private static UserManager mUserManager = (UserManager) SystemUIApplication.getContext().getSystemService(UserManager.class);
    private static boolean unlocked = false;
    private List<Runnable> mBootTaskList;
    private Object mLock;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class BroadcastManagerHolder {
        private static final BootManager sInstance = new BootManager();

        private BroadcastManagerHolder() {
        }
    }

    private BootManager() {
        this.mLock = new Object();
    }

    public static BootManager getInstance() {
        return BroadcastManagerHolder.sInstance;
    }

    public void handleBroadcast(final Context context, final Intent intent) {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.utils.BootManager.1
            @Override // java.lang.Runnable
            public void run() {
                BootManager.this.dispatchBroadcast(context, intent);
            }
        });
    }

    public static boolean isBootComplete() {
        if (!unlocked) {
            unlocked = mUserManager.isUserUnlocked();
        }
        return unlocked;
    }

    public void addBootCompleteTask(Runnable runnable) {
        synchronized (this.mLock) {
            if (this.mBootTaskList == null) {
                this.mBootTaskList = new ArrayList();
            }
            this.mBootTaskList.add(runnable);
        }
    }

    public void init() {
        registerBootCompleteBroadcast();
    }

    private void registerBootCompleteBroadcast() {
        Log.d(TAG, "registerBootCompleteBroadcast");
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BOOT_COMPLETED");
        BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.utils.BootManager.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                BootManager.this.handleBroadcast(context, intent);
            }
        };
        mContext.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchBroadcast(Context context, Intent intent) {
        Log.d(TAG, "dispatchBroadcast, intent=" + intent);
        if (intent == null) {
            Log.w(TAG, "intent invalid");
            return;
        }
        String action = intent.getAction();
        char c = 65535;
        int hashCode = action.hashCode();
        if (hashCode != 798292259) {
            if (hashCode == 833559602 && action.equals("android.intent.action.USER_UNLOCKED")) {
                c = 1;
            }
        } else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            c = 0;
        }
        if (c != 0) {
            if (c == 1) {
                unlocked = true;
            }
        } else if (this.mBootTaskList != null) {
            synchronized (this.mLock) {
                for (Runnable runnable : this.mBootTaskList) {
                    runnable.run();
                }
            }
        }
    }
}
