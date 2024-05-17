package com.xiaopeng.systemui.infoflow.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class TimePickHelper {
    private static final int MSG_NOTIFY_LISTENERS = 1;
    private static final String TAG = "TimePickHelper";
    private static final TimePickHelper sInstance = new TimePickHelper();
    private List<OnTimeChangedListener> mOnTimeChangedListeners = new ArrayList();
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.helper.TimePickHelper.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                TimePickHelper.this.notifyListeners();
            }
        }
    };

    /* loaded from: classes24.dex */
    public interface OnTimeChangedListener {
        void onTimeChanged();
    }

    private TimePickHelper() {
        registerTimeChangeReceiver();
    }

    public static final TimePickHelper instance() {
        return sInstance;
    }

    private void registerTimeChangeReceiver() {
        DateChangedBroadcast dateChangedBroadcast = new DateChangedBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        SystemUIApplication.getContext().registerReceiver(dateChangedBroadcast, filter);
    }

    public void addListener(OnTimeChangedListener listener) {
        if (!this.mOnTimeChangedListeners.contains(listener)) {
            this.mOnTimeChangedListeners.add(listener);
        }
    }

    public void removeListenter(OnTimeChangedListener listener) {
        if (this.mOnTimeChangedListeners.contains(listener)) {
            this.mOnTimeChangedListeners.remove(listener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyListeners() {
        for (OnTimeChangedListener listener : this.mOnTimeChangedListeners) {
            listener.onTimeChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public class DateChangedBroadcast extends BroadcastReceiver {
        private DateChangedBroadcast() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.helper.TimePickHelper.DateChangedBroadcast.1
                @Override // java.lang.Runnable
                public void run() {
                    TimePickHelper.this.mHandler.sendEmptyMessage(1);
                }
            });
        }
    }
}
