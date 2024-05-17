package com.android.systemui.doze;

import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Handler;
import android.util.Log;
import com.android.systemui.dock.DockManager;
import com.android.systemui.doze.DozeMachine;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class DozeDockHandler implements DozeMachine.Part {
    private static final boolean DEBUG = DozeService.DEBUG;
    private static final String TAG = "DozeDockHandler";
    private final AmbientDisplayConfiguration mConfig;
    private final DockManager mDockManager;
    private final DozeHost mDozeHost;
    private final Handler mHandler;
    private final DozeMachine mMachine;
    private boolean mPulsePending;
    private final DockEventListener mDockEventListener = new DockEventListener(this, null);
    private int mDockState = 0;

    public DozeDockHandler(Context context, DozeMachine machine, DozeHost dozeHost, AmbientDisplayConfiguration config, Handler handler, DockManager dockManager) {
        this.mMachine = machine;
        this.mDozeHost = dozeHost;
        this.mConfig = config;
        this.mHandler = handler;
        this.mDockManager = dockManager;
    }

    /* renamed from: com.android.systemui.doze.DozeDockHandler$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$doze$DozeMachine$State = new int[DozeMachine.State.values().length];

        static {
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.INITIALIZED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.DOZE_AOD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.DOZE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$android$systemui$doze$DozeMachine$State[DozeMachine.State.FINISH.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void transitionTo(DozeMachine.State oldState, final DozeMachine.State newState) {
        int i = AnonymousClass1.$SwitchMap$com$android$systemui$doze$DozeMachine$State[newState.ordinal()];
        if (i == 1) {
            this.mDockEventListener.register();
            return;
        }
        if (i != 2) {
            if (i != 3) {
                if (i == 4) {
                    this.mDockEventListener.unregister();
                    return;
                }
                return;
            }
        } else if (this.mDockState == 2) {
            this.mMachine.requestState(DozeMachine.State.DOZE);
            return;
        }
        if (this.mDockState == 1 && !this.mPulsePending) {
            this.mPulsePending = true;
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.doze.-$$Lambda$DozeDockHandler$G1vlJ8PUXQ7yuPGBUT3Rojg64gY
                @Override // java.lang.Runnable
                public final void run() {
                    DozeDockHandler.this.lambda$transitionTo$0$DozeDockHandler(newState);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: requestPulse */
    public void lambda$transitionTo$0$DozeDockHandler(DozeMachine.State dozeState) {
        if (!this.mDozeHost.isPulsingBlocked() && dozeState.canPulse()) {
            this.mMachine.requestPulse(6);
        }
        this.mPulsePending = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestPulseOutNow(DozeMachine.State dozeState) {
        if (dozeState == DozeMachine.State.DOZE_REQUEST_PULSE || dozeState == DozeMachine.State.DOZE_PULSING || dozeState == DozeMachine.State.DOZE_PULSING_BRIGHT) {
            int pulseReason = this.mMachine.getPulseReason();
            if (pulseReason == 6) {
                this.mDozeHost.stopPulsing();
            }
        }
    }

    private boolean isDocked() {
        int i = this.mDockState;
        return i == 1 || i == 2;
    }

    @Override // com.android.systemui.doze.DozeMachine.Part
    public void dump(PrintWriter pw) {
        pw.print(" DozeDockTriggers docking=");
        pw.println(isDocked());
    }

    /* loaded from: classes21.dex */
    private class DockEventListener implements DockManager.DockEventListener {
        private boolean mRegistered;

        private DockEventListener() {
        }

        /* synthetic */ DockEventListener(DozeDockHandler x0, AnonymousClass1 x1) {
            this();
        }

        @Override // com.android.systemui.dock.DockManager.DockEventListener
        public void onEvent(int event) {
            if (DozeDockHandler.DEBUG) {
                Log.d(DozeDockHandler.TAG, "dock event = " + event);
            }
            DozeMachine.State dozeState = DozeDockHandler.this.mMachine.getState();
            DozeDockHandler.this.mDockState = event;
            int i = DozeDockHandler.this.mDockState;
            if (i == 0) {
                if (dozeState != DozeMachine.State.DOZE || !DozeDockHandler.this.mConfig.alwaysOnEnabled(-2)) {
                    DozeDockHandler.this.requestPulseOutNow(dozeState);
                } else {
                    DozeDockHandler.this.mMachine.requestState(DozeMachine.State.DOZE_AOD);
                }
            } else if (i == 1) {
                DozeDockHandler.this.lambda$transitionTo$0$DozeDockHandler(dozeState);
            } else if (i == 2) {
                if (dozeState == DozeMachine.State.DOZE_AOD) {
                    DozeDockHandler.this.mMachine.requestState(DozeMachine.State.DOZE);
                } else {
                    DozeDockHandler.this.requestPulseOutNow(dozeState);
                }
            }
        }

        void register() {
            if (!this.mRegistered) {
                if (DozeDockHandler.this.mDockManager != null) {
                    DozeDockHandler.this.mDockManager.addListener(this);
                }
                this.mRegistered = true;
            }
        }

        void unregister() {
            if (this.mRegistered) {
                if (DozeDockHandler.this.mDockManager != null) {
                    DozeDockHandler.this.mDockManager.removeListener(this);
                }
                this.mRegistered = false;
            }
        }
    }
}
