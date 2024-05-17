package com.android.systemui.keyguard;

import android.os.Handler;
import android.os.Message;
/* loaded from: classes21.dex */
public class KeyguardLifecyclesDispatcher {
    static final int FINISHED_GOING_TO_SLEEP = 7;
    static final int FINISHED_WAKING_UP = 5;
    static final int SCREEN_TURNED_OFF = 3;
    static final int SCREEN_TURNED_ON = 1;
    static final int SCREEN_TURNING_OFF = 2;
    static final int SCREEN_TURNING_ON = 0;
    static final int STARTED_GOING_TO_SLEEP = 6;
    static final int STARTED_WAKING_UP = 4;
    private Handler mHandler = new Handler() { // from class: com.android.systemui.keyguard.KeyguardLifecyclesDispatcher.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    KeyguardLifecyclesDispatcher.this.mScreenLifecycle.dispatchScreenTurningOn();
                    return;
                case 1:
                    KeyguardLifecyclesDispatcher.this.mScreenLifecycle.dispatchScreenTurnedOn();
                    return;
                case 2:
                    KeyguardLifecyclesDispatcher.this.mScreenLifecycle.dispatchScreenTurningOff();
                    return;
                case 3:
                    KeyguardLifecyclesDispatcher.this.mScreenLifecycle.dispatchScreenTurnedOff();
                    return;
                case 4:
                    KeyguardLifecyclesDispatcher.this.mWakefulnessLifecycle.dispatchStartedWakingUp();
                    return;
                case 5:
                    KeyguardLifecyclesDispatcher.this.mWakefulnessLifecycle.dispatchFinishedWakingUp();
                    return;
                case 6:
                    KeyguardLifecyclesDispatcher.this.mWakefulnessLifecycle.dispatchStartedGoingToSleep();
                    return;
                case 7:
                    KeyguardLifecyclesDispatcher.this.mWakefulnessLifecycle.dispatchFinishedGoingToSleep();
                    return;
                default:
                    throw new IllegalArgumentException("Unknown message: " + msg);
            }
        }
    };
    private final ScreenLifecycle mScreenLifecycle;
    private final WakefulnessLifecycle mWakefulnessLifecycle;

    public KeyguardLifecyclesDispatcher(ScreenLifecycle screenLifecycle, WakefulnessLifecycle wakefulnessLifecycle) {
        this.mScreenLifecycle = screenLifecycle;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatch(int what) {
        this.mHandler.obtainMessage(what).sendToTarget();
    }
}
