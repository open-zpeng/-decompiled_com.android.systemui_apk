package com.android.systemui.statusbar.phone;

import android.os.Handler;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.doze.DozeHost;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.phone.ScrimController;
/* loaded from: classes21.dex */
public class DozeScrimController implements StatusBarStateController.StateListener {
    private final DozeParameters mDozeParameters;
    private boolean mDozing;
    private boolean mFullyPulsing;
    private DozeHost.PulseCallback mPulseCallback;
    private int mPulseReason;
    private static final String TAG = "DozeScrimController";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private final Handler mHandler = new Handler();
    private final ScrimController.Callback mScrimCallback = new ScrimController.Callback() { // from class: com.android.systemui.statusbar.phone.DozeScrimController.1
        @Override // com.android.systemui.statusbar.phone.ScrimController.Callback
        public void onDisplayBlanked() {
            if (DozeScrimController.DEBUG) {
                Log.d(DozeScrimController.TAG, "Pulse in, mDozing=" + DozeScrimController.this.mDozing + " mPulseReason=" + DozeLog.reasonToString(DozeScrimController.this.mPulseReason));
            }
            if (DozeScrimController.this.mDozing) {
                DozeScrimController.this.pulseStarted();
            }
        }

        @Override // com.android.systemui.statusbar.phone.ScrimController.Callback
        public void onFinished() {
            if (DozeScrimController.DEBUG) {
                Log.d(DozeScrimController.TAG, "Pulse in finished, mDozing=" + DozeScrimController.this.mDozing);
            }
            if (DozeScrimController.this.mDozing) {
                if (DozeScrimController.this.mPulseReason != 1 && DozeScrimController.this.mPulseReason != 6) {
                    DozeScrimController.this.mHandler.postDelayed(DozeScrimController.this.mPulseOut, DozeScrimController.this.mDozeParameters.getPulseVisibleDuration());
                    DozeScrimController.this.mHandler.postDelayed(DozeScrimController.this.mPulseOutExtended, DozeScrimController.this.mDozeParameters.getPulseVisibleDurationExtended());
                }
                DozeScrimController.this.mFullyPulsing = true;
            }
        }

        @Override // com.android.systemui.statusbar.phone.ScrimController.Callback
        public void onCancelled() {
            DozeScrimController.this.pulseFinished();
        }

        @Override // com.android.systemui.statusbar.phone.ScrimController.Callback
        public boolean shouldTimeoutWallpaper() {
            return DozeScrimController.this.mPulseReason == 6;
        }
    };
    private final Runnable mPulseOutExtended = new Runnable() { // from class: com.android.systemui.statusbar.phone.DozeScrimController.2
        @Override // java.lang.Runnable
        public void run() {
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOut);
            DozeScrimController.this.mPulseOut.run();
        }
    };
    private final Runnable mPulseOut = new Runnable() { // from class: com.android.systemui.statusbar.phone.DozeScrimController.3
        @Override // java.lang.Runnable
        public void run() {
            DozeScrimController.this.mFullyPulsing = false;
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOut);
            DozeScrimController.this.mHandler.removeCallbacks(DozeScrimController.this.mPulseOutExtended);
            if (DozeScrimController.DEBUG) {
                Log.d(DozeScrimController.TAG, "Pulse out, mDozing=" + DozeScrimController.this.mDozing);
            }
            if (DozeScrimController.this.mDozing) {
                DozeScrimController.this.pulseFinished();
            }
        }
    };

    public DozeScrimController(DozeParameters dozeParameters) {
        this.mDozeParameters = dozeParameters;
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
    }

    @VisibleForTesting
    public void setDozing(boolean dozing) {
        if (this.mDozing == dozing) {
            return;
        }
        this.mDozing = dozing;
        if (!this.mDozing) {
            cancelPulsing();
        }
    }

    public void pulse(DozeHost.PulseCallback callback, int reason) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        if (!this.mDozing || this.mPulseCallback != null) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Pulse supressed. Dozing: ");
                sb.append(this.mDozeParameters);
                sb.append(" had callback? ");
                sb.append(this.mPulseCallback != null);
                Log.d(TAG, sb.toString());
            }
            callback.onPulseFinished();
            return;
        }
        this.mPulseCallback = callback;
        this.mPulseReason = reason;
    }

    public void pulseOutNow() {
        if (this.mPulseCallback != null && this.mFullyPulsing) {
            this.mPulseOut.run();
        }
    }

    public boolean isPulsing() {
        return this.mPulseCallback != null;
    }

    public boolean isDozing() {
        return this.mDozing;
    }

    public void extendPulse() {
        this.mHandler.removeCallbacks(this.mPulseOut);
    }

    public void cancelPendingPulseTimeout() {
        this.mHandler.removeCallbacks(this.mPulseOut);
        this.mHandler.removeCallbacks(this.mPulseOutExtended);
    }

    private void cancelPulsing() {
        if (this.mPulseCallback != null) {
            if (DEBUG) {
                Log.d(TAG, "Cancel pulsing");
            }
            this.mFullyPulsing = false;
            this.mHandler.removeCallbacks(this.mPulseOut);
            this.mHandler.removeCallbacks(this.mPulseOutExtended);
            pulseFinished();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pulseStarted() {
        DozeLog.tracePulseStart(this.mPulseReason);
        DozeHost.PulseCallback pulseCallback = this.mPulseCallback;
        if (pulseCallback != null) {
            pulseCallback.onPulseStarted();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pulseFinished() {
        DozeLog.tracePulseFinish();
        DozeHost.PulseCallback pulseCallback = this.mPulseCallback;
        if (pulseCallback != null) {
            pulseCallback.onPulseFinished();
            this.mPulseCallback = null;
        }
    }

    public ScrimController.Callback getScrimCallback() {
        return this.mScrimCallback;
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        setDozing(isDozing);
    }
}
