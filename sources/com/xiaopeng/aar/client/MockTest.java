package com.xiaopeng.aar.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.RestrictTo;
import com.xiaopeng.aar.client.MockTest;
import com.xiaopeng.aar.utils.LogUtils;
import com.xiaopeng.aar.utils.ThreadUtils;
import com.xiaopeng.lib.apirouter.server.ApiPublisherProvider;
/* JADX INFO: Access modifiers changed from: package-private */
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class MockTest {
    private static final String TAG = "Mock";
    private BroadcastReceiver mReceiver;

    /* loaded from: classes22.dex */
    private static class SingletonHolder {
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
    public void init() {
        if (this.mReceiver == null) {
            LogUtils.d(TAG, "init : ");
            this.mReceiver = new DemoReceiver();
            if (ApiPublisherProvider.CONTEXT != null) {
                IntentFilter intentFilter = new IntentFilter("NAPA_MOCK");
                intentFilter.addAction("NAPA_MOCK_SUB");
                ApiPublisherProvider.CONTEXT.registerReceiver(this.mReceiver, intentFilter);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void release() {
        if (this.mReceiver != null) {
            LogUtils.i(TAG, "release : ");
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
            String action = intent.getAction();
            if ("NAPA_MOCK".equals(action)) {
                final String appId = intent.getStringExtra("v1");
                final String module = intent.getStringExtra("v2");
                final String method = intent.getStringExtra("v3");
                final String param = intent.getStringExtra("v4");
                LogUtils.i(MockTest.TAG, "onReceive  appId : " + appId + ", module : " + module + ",method : " + method + ",param : " + param);
                ThreadUtils.MULTI.post(new Runnable() { // from class: com.xiaopeng.aar.client.-$$Lambda$MockTest$DemoReceiver$mtD_5NFPv-4TsOEveWgdINkv3bE
                    @Override // java.lang.Runnable
                    public final void run() {
                        ApiManager.get().call(appId, module, method, param, null);
                    }
                });
            } else if ("NAPA_MOCK_SUB".equals(action)) {
                final String type = intent.getStringExtra("v1");
                final String appId2 = intent.getStringExtra("v2");
                final String module2 = intent.getStringExtra("v3");
                final String subscriber = intent.getStringExtra("v4");
                ThreadUtils.MULTI.post(new Runnable() { // from class: com.xiaopeng.aar.client.-$$Lambda$MockTest$DemoReceiver$UPLseABMno79x_b2A6nyGnSMMO8
                    @Override // java.lang.Runnable
                    public final void run() {
                        MockTest.DemoReceiver.lambda$onReceive$1(type, appId2, module2, subscriber);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onReceive$1(String type, String appId, String module, String subscriber) {
            if ("1".equals(type)) {
                ApiManager.get().subscribe(appId, module, subscriber);
            } else {
                ApiManager.get().unSubscribe(appId, module, subscriber);
            }
        }
    }
}
