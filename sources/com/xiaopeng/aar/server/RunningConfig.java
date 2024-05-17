package com.xiaopeng.aar.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.RestrictTo;
import com.xiaopeng.aar.Apps;
import com.xiaopeng.aar.utils.LogUtils;
import com.xiaopeng.aar.utils.Utils;
/* JADX INFO: Access modifiers changed from: package-private */
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class RunningConfig {
    private static final String TAG = "RunCg";
    private BroadcastReceiver mReceiver;

    RunningConfig() {
    }

    /* loaded from: classes22.dex */
    private static class SingletonHolder {
        private static final RunningConfig INSTANCE = new RunningConfig();

        private SingletonHolder() {
        }
    }

    public static RunningConfig get() {
        return SingletonHolder.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(Context context) {
        if (this.mReceiver == null) {
            this.mReceiver = new Receiver();
            String appId = Apps.getAppId(context);
            String action = appId + "_CONFIG";
            LogUtils.d(TAG, "init action : " + action);
            IntentFilter intentFilter = new IntentFilter(action);
            context.registerReceiver(this.mReceiver, intentFilter);
        }
    }

    /* loaded from: classes22.dex */
    private class Receiver extends BroadcastReceiver {
        private Receiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int level;
            String v1 = intent.getStringExtra("v1");
            String v2 = intent.getStringExtra("v2");
            LogUtils.i(RunningConfig.TAG, String.format("onReceive  v1 : %s , v2 : %s ", v1, v2));
            if ("logLevel".equals(v1) && (level = Utils.parse(v2)) >= 3) {
                LogUtils.setLogLevel(level);
            }
        }
    }
}
