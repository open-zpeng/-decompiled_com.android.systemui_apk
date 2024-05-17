package com.android.systemui.util;

import android.app.AlarmManager;
import android.os.Handler;
import android.os.SystemClock;
/* loaded from: classes21.dex */
public class AlarmTimeout implements AlarmManager.OnAlarmListener {
    public static final int MODE_CRASH_IF_SCHEDULED = 0;
    public static final int MODE_IGNORE_IF_SCHEDULED = 1;
    public static final int MODE_RESCHEDULE_IF_SCHEDULED = 2;
    private final AlarmManager mAlarmManager;
    private final Handler mHandler;
    private final AlarmManager.OnAlarmListener mListener;
    private boolean mScheduled;
    private final String mTag;

    public AlarmTimeout(AlarmManager alarmManager, AlarmManager.OnAlarmListener listener, String tag, Handler handler) {
        this.mAlarmManager = alarmManager;
        this.mListener = listener;
        this.mTag = tag;
        this.mHandler = handler;
    }

    public boolean schedule(long timeout, int mode) {
        if (mode != 0) {
            if (mode != 1) {
                if (mode == 2) {
                    if (this.mScheduled) {
                        cancel();
                    }
                } else {
                    throw new IllegalArgumentException("Illegal mode: " + mode);
                }
            } else if (this.mScheduled) {
                return false;
            }
        } else if (this.mScheduled) {
            throw new IllegalStateException(this.mTag + " timeout is already scheduled");
        }
        this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + timeout, this.mTag, this, this.mHandler);
        this.mScheduled = true;
        return true;
    }

    public boolean isScheduled() {
        return this.mScheduled;
    }

    public void cancel() {
        if (this.mScheduled) {
            this.mAlarmManager.cancel(this);
            this.mScheduled = false;
        }
    }

    @Override // android.app.AlarmManager.OnAlarmListener
    public void onAlarm() {
        if (!this.mScheduled) {
            return;
        }
        this.mScheduled = false;
        this.mListener.onAlarm();
    }
}
