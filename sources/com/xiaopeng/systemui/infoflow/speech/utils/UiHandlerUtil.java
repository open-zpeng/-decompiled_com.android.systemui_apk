package com.xiaopeng.systemui.infoflow.speech.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/* loaded from: classes24.dex */
public class UiHandlerUtil extends Handler {
    private static final UiHandlerUtil sInstance = new UiHandlerUtil(Looper.getMainLooper());

    private UiHandlerUtil(Looper looper) {
        super(looper);
    }

    @NonNull
    public static UiHandlerUtil getInstance() {
        return sInstance;
    }

    @Override // android.os.Handler
    public boolean sendMessageAtTime(@Nullable Message msg, long uptimeMillis) {
        if (msg != null && msg.getCallback() != null) {
            UiHandlerRunnable runnable = new UiHandlerRunnable(msg.getCallback());
            Message newMsg = Message.obtain(msg.getTarget(), runnable);
            newMsg.what = msg.what;
            newMsg.arg1 = msg.arg1;
            newMsg.arg2 = msg.arg2;
            newMsg.obj = msg.obj;
            newMsg.replyTo = msg.replyTo;
            newMsg.setData(msg.getData());
            return super.sendMessageAtTime(newMsg, uptimeMillis);
        }
        return super.sendMessageAtTime(msg, uptimeMillis);
    }

    /* loaded from: classes24.dex */
    static class UiHandlerRunnable implements Runnable {
        private static final String TAG = "UiHandlerRunnable";
        private boolean isLoggable = false;
        private Runnable mRunnable;
        private long mStartTime;
        private String mTag;

        UiHandlerRunnable(Runnable runnable) {
            int i = 0;
            this.mTag = "unknown";
            if (this.isLoggable) {
                StackTraceElement[] elements = Thread.currentThread().getStackTrace();
                int length = elements.length;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    StackTraceElement element = elements[i];
                    String fileName = element.getFileName();
                    String declaringClass = element.getClassName();
                    if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(declaringClass) || "UiHandlerUtil.java".equals(fileName) || !declaringClass.startsWith("com.xiaopeng")) {
                        i++;
                    } else {
                        this.mTag = fileName + ":line:" + element.getLineNumber();
                        break;
                    }
                }
                this.mStartTime = System.currentTimeMillis();
            }
            this.mRunnable = runnable;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mRunnable != null) {
                TraceUtils.alwaysTraceBegin("UiHandlerRunnable:" + this.mTag);
                long currTime = System.currentTimeMillis();
                this.mRunnable.run();
                if (this.isLoggable) {
                    long runTime = System.currentTimeMillis() - currTime;
                    long waitTime = currTime - this.mStartTime;
                    if (runTime > 200 || waitTime > 200) {
                        Log.w(TAG, this.mTag + "  wait before execute Time:" + waitTime + "  runTime:" + runTime);
                    }
                }
                TraceUtils.alwaysTraceEnd();
            }
        }
    }
}
