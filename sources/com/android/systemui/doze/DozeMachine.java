package com.android.systemui.doze;

import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Trace;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.util.Assert;
import com.android.systemui.util.wakelock.WakeLock;
import java.io.PrintWriter;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class DozeMachine {
    static final boolean DEBUG = DozeService.DEBUG;
    private static final String REASON_CHANGE_STATE = "DozeMachine#requestState";
    private static final String REASON_HELD_FOR_STATE = "DozeMachine#heldForState";
    static final String TAG = "DozeMachine";
    private final BatteryController mBatteryController;
    private final AmbientDisplayConfiguration mConfig;
    private final Service mDozeService;
    private Part[] mParts;
    private int mPulseReason;
    private final WakeLock mWakeLock;
    private final WakefulnessLifecycle mWakefulnessLifecycle;
    private final ArrayList<State> mQueuedRequests = new ArrayList<>();
    private State mState = State.UNINITIALIZED;
    private boolean mWakeLockHeldForCurrentState = false;

    /* loaded from: classes21.dex */
    public enum State {
        UNINITIALIZED,
        INITIALIZED,
        DOZE,
        DOZE_AOD,
        DOZE_REQUEST_PULSE,
        DOZE_PULSING,
        DOZE_PULSING_BRIGHT,
        DOZE_PULSE_DONE,
        FINISH,
        DOZE_AOD_PAUSED,
        DOZE_AOD_PAUSING;

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean canPulse() {
            int i = AnonymousClass1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[ordinal()];
            return i == 1 || i == 2 || i == 3 || i == 4;
        }

        boolean staysAwake() {
            int i = AnonymousClass1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[ordinal()];
            if (i == 5 || i == 6 || i == 7) {
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int screenState(DozeParameters parameters) {
            switch (this) {
                case DOZE:
                case DOZE_AOD_PAUSED:
                    return 1;
                case DOZE_AOD:
                case DOZE_AOD_PAUSING:
                    return 4;
                case DOZE_REQUEST_PULSE:
                case UNINITIALIZED:
                case INITIALIZED:
                    return parameters.shouldControlScreenOff() ? 2 : 1;
                case DOZE_PULSING:
                case DOZE_PULSING_BRIGHT:
                    return 2;
                default:
                    return 0;
            }
        }
    }

    public DozeMachine(Service service, AmbientDisplayConfiguration config, WakeLock wakeLock, WakefulnessLifecycle wakefulnessLifecycle, BatteryController batteryController) {
        this.mDozeService = service;
        this.mConfig = config;
        this.mWakefulnessLifecycle = wakefulnessLifecycle;
        this.mWakeLock = wakeLock;
        this.mBatteryController = batteryController;
    }

    public void setParts(Part[] parts) {
        Preconditions.checkState(this.mParts == null);
        this.mParts = parts;
    }

    public void requestState(State requestedState) {
        Preconditions.checkArgument(requestedState != State.DOZE_REQUEST_PULSE);
        requestState(requestedState, -1);
    }

    public void requestPulse(int pulseReason) {
        Preconditions.checkState(!isExecutingTransition());
        requestState(State.DOZE_REQUEST_PULSE, pulseReason);
    }

    private void requestState(State requestedState, int pulseReason) {
        Assert.isMainThread();
        if (DEBUG) {
            Log.i(TAG, "request: current=" + this.mState + " req=" + requestedState, new Throwable("here"));
        }
        boolean runNow = !isExecutingTransition();
        this.mQueuedRequests.add(requestedState);
        if (runNow) {
            this.mWakeLock.acquire(REASON_CHANGE_STATE);
            for (int i = 0; i < this.mQueuedRequests.size(); i++) {
                transitionTo(this.mQueuedRequests.get(i), pulseReason);
            }
            this.mQueuedRequests.clear();
            this.mWakeLock.release(REASON_CHANGE_STATE);
        }
    }

    public State getState() {
        Assert.isMainThread();
        if (isExecutingTransition()) {
            throw new IllegalStateException("Cannot get state because there were pending transitions: " + this.mQueuedRequests.toString());
        }
        return this.mState;
    }

    public int getPulseReason() {
        Assert.isMainThread();
        boolean z = this.mState == State.DOZE_REQUEST_PULSE || this.mState == State.DOZE_PULSING || this.mState == State.DOZE_PULSING_BRIGHT || this.mState == State.DOZE_PULSE_DONE;
        Preconditions.checkState(z, "must be in pulsing state, but is " + this.mState);
        return this.mPulseReason;
    }

    public void wakeUp() {
        this.mDozeService.requestWakeUp();
    }

    public boolean isExecutingTransition() {
        return !this.mQueuedRequests.isEmpty();
    }

    private void transitionTo(State requestedState, int pulseReason) {
        State newState = transitionPolicy(requestedState);
        if (DEBUG) {
            Log.i(TAG, "transition: old=" + this.mState + " req=" + requestedState + " new=" + newState);
        }
        if (newState == this.mState) {
            return;
        }
        validateTransition(newState);
        State oldState = this.mState;
        this.mState = newState;
        DozeLog.traceState(newState);
        Trace.traceCounter(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, "doze_machine_state", newState.ordinal());
        updatePulseReason(newState, oldState, pulseReason);
        performTransitionOnComponents(oldState, newState);
        updateWakeLockState(newState);
        resolveIntermediateState(newState);
    }

    private void updatePulseReason(State newState, State oldState, int pulseReason) {
        if (newState == State.DOZE_REQUEST_PULSE) {
            this.mPulseReason = pulseReason;
        } else if (oldState == State.DOZE_PULSE_DONE) {
            this.mPulseReason = -1;
        }
    }

    private void performTransitionOnComponents(State oldState, State newState) {
        Part[] partArr;
        for (Part p : this.mParts) {
            p.transitionTo(oldState, newState);
        }
        if (AnonymousClass1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[newState.ordinal()] == 10) {
            this.mDozeService.finish();
        }
    }

    private void validateTransition(State newState) {
        try {
            int i = AnonymousClass1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[this.mState.ordinal()];
            boolean z = true;
            if (i == 8) {
                Preconditions.checkState(newState == State.INITIALIZED);
            } else if (i == 10) {
                Preconditions.checkState(newState == State.FINISH);
            }
            int i2 = AnonymousClass1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[newState.ordinal()];
            if (i2 == 6) {
                if (this.mState != State.DOZE_REQUEST_PULSE) {
                    z = false;
                }
                Preconditions.checkState(z);
            } else if (i2 == 11) {
                if (this.mState != State.DOZE_REQUEST_PULSE && this.mState != State.DOZE_PULSING && this.mState != State.DOZE_PULSING_BRIGHT) {
                    z = false;
                }
                Preconditions.checkState(z);
            } else if (i2 == 8) {
                throw new IllegalArgumentException("can't transition to UNINITIALIZED");
            } else {
                if (i2 == 9) {
                    if (this.mState != State.UNINITIALIZED) {
                        z = false;
                    }
                    Preconditions.checkState(z);
                }
            }
        } catch (RuntimeException e) {
            throw new IllegalStateException("Illegal Transition: " + this.mState + " -> " + newState, e);
        }
    }

    private State transitionPolicy(State requestedState) {
        if (this.mState == State.FINISH) {
            return State.FINISH;
        }
        if ((this.mState == State.DOZE_AOD_PAUSED || this.mState == State.DOZE_AOD_PAUSING || this.mState == State.DOZE_AOD || this.mState == State.DOZE) && requestedState == State.DOZE_PULSE_DONE) {
            Log.i(TAG, "Dropping pulse done because current state is already done: " + this.mState);
            return this.mState;
        } else if (requestedState == State.DOZE_AOD && this.mBatteryController.isAodPowerSave()) {
            return State.DOZE;
        } else {
            if (requestedState == State.DOZE_REQUEST_PULSE && !this.mState.canPulse()) {
                Log.i(TAG, "Dropping pulse request because current state can't pulse: " + this.mState);
                return this.mState;
            }
            return requestedState;
        }
    }

    private void updateWakeLockState(State newState) {
        boolean staysAwake = newState.staysAwake();
        if (this.mWakeLockHeldForCurrentState && !staysAwake) {
            this.mWakeLock.release(REASON_HELD_FOR_STATE);
            this.mWakeLockHeldForCurrentState = false;
        } else if (!this.mWakeLockHeldForCurrentState && staysAwake) {
            this.mWakeLock.acquire(REASON_HELD_FOR_STATE);
            this.mWakeLockHeldForCurrentState = true;
        }
    }

    private void resolveIntermediateState(State state) {
        State nextState;
        int i = AnonymousClass1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[state.ordinal()];
        if (i == 9 || i == 11) {
            int wakefulness = this.mWakefulnessLifecycle.getWakefulness();
            if (wakefulness == 2 || wakefulness == 1) {
                nextState = State.FINISH;
            } else if (this.mConfig.alwaysOnEnabled(-2)) {
                nextState = State.DOZE_AOD;
            } else {
                nextState = State.DOZE;
            }
            transitionTo(nextState, -1);
        }
    }

    public void dump(PrintWriter pw) {
        Part[] partArr;
        pw.print(" state=");
        pw.println(this.mState);
        pw.print(" wakeLockHeldForCurrentState=");
        pw.println(this.mWakeLockHeldForCurrentState);
        pw.print(" wakeLock=");
        pw.println(this.mWakeLock);
        pw.println("Parts:");
        for (Part p : this.mParts) {
            p.dump(pw);
        }
    }

    /* loaded from: classes21.dex */
    public interface Part {
        void transitionTo(State state, State state2);

        default void dump(PrintWriter pw) {
        }
    }

    /* loaded from: classes21.dex */
    public interface Service {
        void finish();

        void requestWakeUp();

        void setDozeScreenBrightness(int i);

        void setDozeScreenState(int i);

        /* loaded from: classes21.dex */
        public static class Delegate implements Service {
            private final Service mDelegate;

            public Delegate(Service delegate) {
                this.mDelegate = delegate;
            }

            @Override // com.android.systemui.doze.DozeMachine.Service
            public void finish() {
                this.mDelegate.finish();
            }

            @Override // com.android.systemui.doze.DozeMachine.Service
            public void setDozeScreenState(int state) {
                this.mDelegate.setDozeScreenState(state);
            }

            @Override // com.android.systemui.doze.DozeMachine.Service
            public void requestWakeUp() {
                this.mDelegate.requestWakeUp();
            }

            @Override // com.android.systemui.doze.DozeMachine.Service
            public void setDozeScreenBrightness(int brightness) {
                this.mDelegate.setDozeScreenBrightness(brightness);
            }
        }
    }
}
