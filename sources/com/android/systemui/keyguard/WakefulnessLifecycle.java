package com.android.systemui.keyguard;

import android.os.Trace;
import android.support.v4.media.session.PlaybackStateCompat;
import com.android.systemui.Dumpable;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Consumer;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class WakefulnessLifecycle extends Lifecycle<Observer> implements Dumpable {
    public static final int WAKEFULNESS_ASLEEP = 0;
    public static final int WAKEFULNESS_AWAKE = 2;
    public static final int WAKEFULNESS_GOING_TO_SLEEP = 3;
    public static final int WAKEFULNESS_WAKING = 1;
    private int mWakefulness = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface Wakefulness {
    }

    public int getWakefulness() {
        return this.mWakefulness;
    }

    public void dispatchStartedWakingUp() {
        if (getWakefulness() == 1) {
            return;
        }
        setWakefulness(1);
        dispatch(new Consumer() { // from class: com.android.systemui.keyguard.-$$Lambda$TPhVA13qrDBGFKbgQpRNBPBvAqI
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WakefulnessLifecycle.Observer) obj).onStartedWakingUp();
            }
        });
    }

    public void dispatchFinishedWakingUp() {
        if (getWakefulness() == 2) {
            return;
        }
        setWakefulness(2);
        dispatch(new Consumer() { // from class: com.android.systemui.keyguard.-$$Lambda$v8UUYbN3IpgugNoVVCKp-k3ABDI
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WakefulnessLifecycle.Observer) obj).onFinishedWakingUp();
            }
        });
    }

    public void dispatchStartedGoingToSleep() {
        if (getWakefulness() == 3) {
            return;
        }
        setWakefulness(3);
        dispatch(new Consumer() { // from class: com.android.systemui.keyguard.-$$Lambda$ASgSeR7gTZT1Q2JGNWCU20EppLY
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WakefulnessLifecycle.Observer) obj).onStartedGoingToSleep();
            }
        });
    }

    public void dispatchFinishedGoingToSleep() {
        if (getWakefulness() == 0) {
            return;
        }
        setWakefulness(0);
        dispatch(new Consumer() { // from class: com.android.systemui.keyguard.-$$Lambda$AKoGNPXjF07Pzc3_fzdQTCHgk6E
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WakefulnessLifecycle.Observer) obj).onFinishedGoingToSleep();
            }
        });
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("WakefulnessLifecycle:");
        pw.println("  mWakefulness=" + this.mWakefulness);
    }

    private void setWakefulness(int wakefulness) {
        this.mWakefulness = wakefulness;
        Trace.traceCounter(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, "wakefulness", wakefulness);
    }

    /* loaded from: classes21.dex */
    public interface Observer {
        default void onStartedWakingUp() {
        }

        default void onFinishedWakingUp() {
        }

        default void onStartedGoingToSleep() {
        }

        default void onFinishedGoingToSleep() {
        }
    }
}
