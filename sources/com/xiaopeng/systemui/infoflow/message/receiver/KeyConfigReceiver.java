package com.xiaopeng.systemui.infoflow.message.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.xiaopeng.systemui.infoflow.message.KeyConfig;
/* loaded from: classes24.dex */
public class KeyConfigReceiver extends BroadcastReceiver {
    private static final String ACTION_KEY_CONFIG_CHANGED = "com.xiaopeng.systemui.infoflow.KEY_CONFIG_CHANGED";
    public static final String EXTRA_CONFIG_KEY = "extra_key_config";
    private static final String TAG = "KeyConfigReceiver";
    private Context mContext;

    public KeyConfigReceiver(Context context) {
        this.mContext = context;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_KEY_CONFIG_CHANGED.equals(action)) {
            int configValue = intent.getIntExtra(EXTRA_CONFIG_KEY, 0);
            KeyConfig.saveConfig(configValue);
        }
    }

    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_KEY_CONFIG_CHANGED);
        this.mContext.registerReceiver(this, filter);
    }
}
