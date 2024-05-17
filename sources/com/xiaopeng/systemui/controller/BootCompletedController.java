package com.xiaopeng.systemui.controller;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.OsdController;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
/* loaded from: classes24.dex */
public class BootCompletedController {
    private static final String TAG = "BootCompleted";
    private boolean mBootCompleted;
    private final CopyOnWriteArraySet<CallBack> mCallBacks;
    private final Handler mHandler;

    /* loaded from: classes24.dex */
    public interface CallBack {
        void onBootCompleted();
    }

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final BootCompletedController sInstance = new BootCompletedController();

        private SingleHolder() {
        }
    }

    public static BootCompletedController get() {
        return SingleHolder.sInstance;
    }

    private BootCompletedController() {
        this.mBootCompleted = false;
        this.mCallBacks = new CopyOnWriteArraySet<>();
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public void init() {
        checkBootCompleted();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkBootCompleted() {
        if (this.mBootCompleted) {
            return;
        }
        boolean bootCompleted = "1".equals(SystemProperties.get("sys.boot_completed"));
        if (bootCompleted) {
            this.mBootCompleted = true;
            notifyBootCompleted();
            return;
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.controller.-$$Lambda$BootCompletedController$r9Q4Fylhfbf7G5dunJ9wcd_2KWo
            @Override // java.lang.Runnable
            public final void run() {
                BootCompletedController.this.checkBootCompleted();
            }
        }, OsdController.TN.DURATION_TIMEOUT_SHORT);
    }

    private void notifyBootCompleted() {
        if (this.mCallBacks.isEmpty()) {
            Logger.d(TAG, "notifyBootCompleted callback isEmpty  ");
            return;
        }
        long time = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        Iterator<CallBack> it = this.mCallBacks.iterator();
        while (it.hasNext()) {
            CallBack back = it.next();
            back.onBootCompleted();
            sb.append(back);
            sb.append(" time : ");
            sb.append(System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
        }
        Logger.d(TAG, "notifyBootCompleted : " + sb.toString());
        this.mCallBacks.clear();
    }

    public boolean isBootCompleted() {
        return this.mBootCompleted;
    }

    public void addOnceCallBack(CallBack callBack) {
        if (this.mBootCompleted) {
            Logger.d(TAG, "addOnceCallBack already BootCompleted : " + callBack);
            return;
        }
        this.mCallBacks.add(callBack);
    }
}
