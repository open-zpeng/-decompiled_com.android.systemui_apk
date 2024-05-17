package com.xiaopeng.systemui.helper;

import android.os.SystemProperties;
import android.util.Log;
/* loaded from: classes24.dex */
public class TrafficStatusEventHelper {
    private static final String RAFFIC_STATUS_TYPE_KEY = "persist.sys.xp.4g.st";
    public static final int STATUS_TRAFFIC_CLOSE = 2;
    public static final int STATUS_TRAFFIC_INVAILD = -1;
    public static final int STATUS_TRAFFIC_NORMAL = 3;
    public static final int STATUS_TRAFFIC_OUT = 1;
    private static final String TAG = "TrafficStatusEventHelper";
    private static TrafficStatusEventHelper mInstance;
    public OnTrafficStatusListener mTrafficStatusListener;

    /* loaded from: classes24.dex */
    public interface OnTrafficStatusListener {
        void onTrafficStatusChanged(boolean z);
    }

    private TrafficStatusEventHelper() {
    }

    public static TrafficStatusEventHelper getInstance() {
        if (mInstance == null) {
            synchronized (TrafficStatusEventHelper.class) {
                if (mInstance == null) {
                    mInstance = new TrafficStatusEventHelper();
                }
            }
        }
        return mInstance;
    }

    public int getTrafficStatus() {
        int value = SystemProperties.getInt("persist.sys.xp.4g.st", -1);
        Log.i(TAG, "getTrafficStatus : " + value);
        return value;
    }

    public boolean isTrafficOut() {
        int status = getTrafficStatus();
        return status == 1;
    }

    public void setTrafficStatusListener(OnTrafficStatusListener listener) {
        this.mTrafficStatusListener = listener;
    }

    public void notifyTrafficStatusChangedEvent() {
        OnTrafficStatusListener onTrafficStatusListener = this.mTrafficStatusListener;
        if (onTrafficStatusListener != null) {
            onTrafficStatusListener.onTrafficStatusChanged(isTrafficOut());
        }
    }
}
