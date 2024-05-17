package com.xiaopeng.xui.widget.dialogview;

import android.os.Handler;
import android.os.Message;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes25.dex */
public class XCountDown extends Handler {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public XCountDown(CallBack callBack) {
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

    void start(int count) {
        start(null, count);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start(String tag, int count) {
        this.mTag = tag;
        stop();
        this.count = count;
        this.index = count;
        sendEmptyMessage(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stop() {
        removeMessages(0);
        CallBack callBack = this.mCallBack;
        if (callBack != null) {
            callBack.onCountDownStop(this.mTag);
        }
    }
}
