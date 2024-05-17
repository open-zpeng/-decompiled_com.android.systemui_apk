package com.xiaopeng.systemui.infoflow.message.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import com.xiaopeng.systemui.infoflow.message.contract.CardsContract;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class NotificationReadReceiver extends BroadcastReceiver {
    private static final String ACTION_NUMBER_CHANGED = "android.intent.action.NOTIFICATION_NUMBER_CHANGED";
    public static final String EXTRA_NUMBER_KEY = "android.intent.extra.NOTIFICATION_KEY";
    public static final String EXTRA_NUMBER_PACKAGENAME = "android.intent.extra.NOTIFICATION_PACKAGENAME";
    private static final String TAG = NotificationReadReceiver.class.getSimpleName();
    private CardsContract.Presenter mCardsPresenter;
    private Context mContext;

    public NotificationReadReceiver(Context context, CardsContract.Presenter presenter) {
        this.mContext = context;
        this.mCardsPresenter = presenter;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_NUMBER_CHANGED)) {
            String packageName = intent.getStringExtra(EXTRA_NUMBER_PACKAGENAME);
            String key = intent.getStringExtra(EXTRA_NUMBER_KEY);
            if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(key)) {
                String str = TAG;
                Logger.d(str, "remove key-" + key + " &packageName-" + packageName);
                this.mCardsPresenter.removeNotification(key);
            }
        }
    }

    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NUMBER_CHANGED);
        this.mContext.registerReceiver(this, filter);
    }
}
