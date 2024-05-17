package com.android.systemui.doze;

import android.app.AlarmManager;
import android.os.Handler;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.util.AlarmTimeout;
/* loaded from: classes21.dex */
public class DozePauser implements DozeMachine.Part {
    public static final String TAG = DozePauser.class.getSimpleName();
    private final DozeMachine mMachine;
    private final AlarmTimeout mPauseTimeout;
    private final AlwaysOnDisplayPolicy mPolicy;

    public DozePauser(Handler handler, DozeMachine machine, AlarmManager alarmManager, AlwaysOnDisplayPolicy policy) {
        this.mMachine = machine;
        this.mPauseTimeout = new AlarmTimeout(alarmManager, new AlarmManager.OnAlarmListener() { // from class: com.android.systemui.doze.-$$Lambda$DozePauser$RaYrBg9_HgEkLP8ozxXkVSg4K5c
            @Override // android.app.AlarmManager.OnAlarmListener
            public final void onAlarm() {
                DozePauser.this.onTimeout();
            }
        }, TAG, handler);
        this.mPolicy = policy;
    }

    /* renamed from: com.android.systemui.doze.DozePauser$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State = new int[DozeMachine.State.values().length];

        static {
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.DOZE_AOD_PAUSING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void transitionTo(DozeMachine.State oldState, DozeMachine.State newState) {
        if (AnonymousClass1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[newState.ordinal()] == 1) {
            this.mPauseTimeout.schedule(this.mPolicy.proxScreenOffDelayMs, 1);
        } else {
            this.mPauseTimeout.cancel();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeout() {
        this.mMachine.requestState(DozeMachine.State.DOZE_AOD_PAUSED);
    }
}
