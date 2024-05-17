package com.xiaopeng.aar.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.RestrictTo;
import com.xiaopeng.aar.utils.LogUtils;
import com.xiaopeng.aar.utils.Utils;
import com.xiaopeng.lib.apirouter.server.ApiPublisherProvider;
/* JADX INFO: Access modifiers changed from: package-private */
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class RunningConfig {
    private static final String TAG = "RunCg";
    private BroadcastReceiver mReceiver;

    /* loaded from: classes22.dex */
    private static class SingletonHolder {
        private static final RunningConfig INSTANCE = new RunningConfig();

        private SingletonHolder() {
        }
    }

    public static RunningConfig get() {
        return SingletonHolder.INSTANCE;
    }

    private RunningConfig() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init() {
        if (this.mReceiver == null) {
            LogUtils.d(TAG, "init : ");
            this.mReceiver = new DemoReceiver();
            if (ApiPublisherProvider.CONTEXT != null) {
                ApiPublisherProvider.CONTEXT.registerReceiver(this.mReceiver, new IntentFilter("NAPA_CONFIG"));
            }
        }
    }

    void release() {
        if (this.mReceiver != null) {
            LogUtils.d(TAG, "release : ");
            if (ApiPublisherProvider.CONTEXT != null) {
                ApiPublisherProvider.CONTEXT.unregisterReceiver(this.mReceiver);
            }
            this.mReceiver = null;
        }
    }

    /* loaded from: classes22.dex */
    private class DemoReceiver extends BroadcastReceiver {
        private DemoReceiver() {
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
