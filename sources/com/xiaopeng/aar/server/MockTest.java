package com.xiaopeng.aar.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.RestrictTo;
import com.xiaopeng.aar.Apps;
import com.xiaopeng.aar.server.MockTest;
import com.xiaopeng.aar.utils.LogUtils;
import com.xiaopeng.aar.utils.ThreadUtils;
/* JADX INFO: Access modifiers changed from: package-private */
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class MockTest {
    private static final String TAG = "Mock";
    private BroadcastReceiver mReceiver;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes22.dex */
    public static class SingletonHolder {
        private static final MockTest INSTANCE = new MockTest();

        private SingletonHolder() {
        }
    }

    public static MockTest get() {
        return SingletonHolder.INSTANCE;
    }

    private MockTest() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(Context context) {
        if (this.mReceiver == null) {
            this.mReceiver = new DemoReceiver();
            String appId = Apps.getAppId(context);
            String action = appId + "_MOCK";
            LogUtils.d(TAG, "init action : " + action);
            context.registerReceiver(this.mReceiver, new IntentFilter(action));
        }
    }

    void release(Context context) {
        if (this.mReceiver != null) {
            LogUtils.d(TAG, "release : ");
            context.unregisterReceiver(this.mReceiver);
            this.mReceiver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes22.dex */
    public class DemoReceiver extends BroadcastReceiver {
        private DemoReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            final String module = intent.getStringExtra("v1");
            final String msgId = intent.getStringExtra("v2");
            final String data = intent.getStringExtra("v3");
            final String type = intent.getStringExtra("v4");
            LogUtils.i(MockTest.TAG, "onReceive  module : " + module + ", msgId : " + msgId + ",data : " + data);
            ThreadUtils.MULTI.post(new Runnable() { // from class: com.xiaopeng.aar.server.-$$Lambda$MockTest$DemoReceiver$C6468fjcAIMUHSTs6l3nPGkZNsQ
                @Override // java.lang.Runnable
                public final void run() {
                    MockTest.DemoReceiver.lambda$onReceive$0(type, module, msgId, data);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onReceive$0(String type, String module, String msgId, String data) {
            if ("1".equals(type)) {
                ServerManager.get().send(true, module, msgId, data, null);
            } else {
                ServerManager.get().send(module, msgId, data, null);
            }
        }
    }
}
