package com.xiaopeng.xui.utils;

import android.os.Handler;
import android.os.Message;
import androidx.annotation.RestrictTo;
@RestrictTo({RestrictTo.Scope.LIBRARY})
@Deprecated
/* loaded from: classes25.dex */
public class XCountDownHandler extends Handler {
    private int count;
    private int index;
    private CallBack mCallBack;
    private String mTag;

    /* loaded from: classes25.dex */
    public interface CallBack {
        void onCountDown(String str, int i, int i2);

        void onCountDownOver(String str);

        void onCountDownStop(String str);
    }

    public XCountDownHandler(CallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        CallBack callBack;
        CallBack callBack2 = this.mCallBack;
        if (callBack2 != null) {
            callBack2.onCountDown(this.mTag, this.count, this.index);
        }
        if (this.index == 0 && (callBack = this.mCallBack) != null) {
            callBack.onCountDownOver(this.mTag);
        }
        int i = this.index;
        if (i > 0) {
            this.index = i - 1;
            sendEmptyMessageDelayed(0, 1000L);
        }
    }

    public void start(int count) {
        start(null, count);
    }

    public void start(String tag, int count) {
        this.mTag = tag;
        stop();
        this.count = count;
        this.index = count;
        sendEmptyMessage(0);
    }

    public void stop() {
        removeMessages(0);
        CallBack callBack = this.mCallBack;
        if (callBack != null) {
            callBack.onCountDownStop(this.mTag);
        }
    }
}
