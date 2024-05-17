package com.android.systemui.doze;

import android.os.Handler;
import android.util.Log;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.util.wakelock.SettableWakeLock;
import com.android.systemui.util.wakelock.WakeLock;
/* loaded from: classes21.dex */
public class DozeScreenState implements DozeMachine.Part {
    private static final boolean DEBUG = DozeService.DEBUG;
    private static final int ENTER_DOZE_DELAY = 4000;
    public static final int ENTER_DOZE_HIDE_WALLPAPER_DELAY = 2500;
    private static final String TAG = "DozeScreenState";
    private final DozeMachine.Service mDozeService;
    private final Handler mHandler;
    private final DozeParameters mParameters;
    private SettableWakeLock mWakeLock;
    private final Runnable mApplyPendingScreenState = new Runnable() { // from class: com.android.systemui.doze.-$$Lambda$DozeScreenState$eRrLSFQgxPfG2I_jJDfdCLwKzVE
        @Override // java.lang.Runnable
        public final void run() {
            DozeScreenState.this.applyPendingScreenState();
        }
    };
    private int mPendingScreenState = 0;

    public DozeScreenState(DozeMachine.Service service, Handler handler, DozeParameters parameters, WakeLock wakeLock) {
        this.mDozeService = service;
        this.mHandler = handler;
        this.mParameters = parameters;
        this.mWakeLock = new SettableWakeLock(wakeLock, TAG);
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void transitionTo(DozeMachine.State oldState, DozeMachine.State newState) {
        int screenState = newState.screenState(this.mParameters);
        boolean shouldDelayTransition = false;
        if (newState == DozeMachine.State.FINISH) {
            this.mPendingScreenState = 0;
            this.mHandler.removeCallbacks(this.mApplyPendingScreenState);
            applyScreenState(screenState);
            this.mWakeLock.setAcquired(false);
        } else if (screenState == 0) {
        } else {
            boolean messagePending = this.mHandler.hasCallbacks(this.mApplyPendingScreenState);
            boolean pulseEnding = oldState == DozeMachine.State.DOZE_PULSE_DONE && newState == DozeMachine.State.DOZE_AOD;
            boolean turningOn = (oldState == DozeMachine.State.DOZE_AOD_PAUSED || oldState == DozeMachine.State.DOZE) && newState == DozeMachine.State.DOZE_AOD;
            boolean justInitialized = oldState == DozeMachine.State.INITIALIZED;
            if (messagePending || justInitialized || pulseEnding || turningOn) {
                this.mPendingScreenState = screenState;
                if (newState == DozeMachine.State.DOZE_AOD && this.mParameters.shouldControlScreenOff() && !turningOn) {
                    shouldDelayTransition = true;
                }
                if (shouldDelayTransition) {
                    this.mWakeLock.setAcquired(true);
                }
                if (!messagePending) {
                    if (DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Display state changed to ");
                        sb.append(screenState);
                        sb.append(" delayed by ");
                        sb.append(shouldDelayTransition ? 4000 : 1);
                        Log.d(TAG, sb.toString());
                    }
                    if (shouldDelayTransition) {
                        this.mHandler.postDelayed(this.mApplyPendingScreenState, 4000L);
                        return;
                    } else {
                        this.mHandler.post(this.mApplyPendingScreenState);
                        return;
                    }
                } else if (DEBUG) {
                    Log.d(TAG, "Pending display state change to " + screenState);
                    return;
                } else {
                    return;
                }
            }
            applyScreenState(screenState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyPendingScreenState() {
        applyScreenState(this.mPendingScreenState);
        this.mPendingScreenState = 0;
    }

    private void applyScreenState(int screenState) {
        if (screenState != 0) {
            if (DEBUG) {
                Log.d(TAG, "setDozeScreenState(" + screenState + NavigationBarInflaterView.KEY_CODE_END);
            }
            this.mDozeService.setDozeScreenState(screenState);
            this.mPendingScreenState = 0;
            this.mWakeLock.setAcquired(false);
        }
    }
}
