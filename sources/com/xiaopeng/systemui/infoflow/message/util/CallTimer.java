package com.xiaopeng.systemui.infoflow.message.util;

import android.os.SystemClock;
import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.TimeUtils;
import java.util.Timer;
import java.util.TimerTask;
/* loaded from: classes24.dex */
public class CallTimer {
    private static final String TAG = CallTimer.class.getSimpleName();
    private long mCallingElapsedRealtime;
    private OnElapsedTimerListener mListener;
    private TimerTask mTimerTask;
    private long mBaseTime = SystemClock.elapsedRealtime();
    private final long TASK_PERIOD = 1000;
    private Timer mTimer = new Timer();

    /* loaded from: classes24.dex */
    public interface OnElapsedTimerListener {
        void onElapsedTimeString(String str);
    }

    public CallTimer(OnElapsedTimerListener listener) {
        this.mListener = listener;
    }

    public void startTimer(long elapsedRealtime) {
        Logger.d(TAG, "startTimer");
        this.mCallingElapsedRealtime = elapsedRealtime;
        this.mBaseTime = SystemClock.elapsedRealtime();
        this.mTimerTask = new TimerTask() { // from class: com.xiaopeng.systemui.infoflow.message.util.CallTimer.1
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                CallTimer.this.calculateElapsedTime();
            }
        };
        this.mTimer.scheduleAtFixedRate(this.mTimerTask, 0L, 1000L);
    }

    public void end() {
        Logger.d(TAG, DMWait.STATUS_END);
        TimerTask timerTask = this.mTimerTask;
        if (timerTask != null) {
            timerTask.cancel();
            this.mTimerTask = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void calculateElapsedTime() {
        long currentTime = SystemClock.elapsedRealtime();
        long elapsedTime = (currentTime - this.mBaseTime) + this.mCallingElapsedRealtime;
        String result = TimeUtils.longToString(elapsedTime);
        OnElapsedTimerListener onElapsedTimerListener = this.mListener;
        if (onElapsedTimerListener != null) {
            onElapsedTimerListener.onElapsedTimeString(result);
        }
    }
}
