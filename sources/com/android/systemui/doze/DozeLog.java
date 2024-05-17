package com.android.systemui.doze;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.TimeUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
/* loaded from: classes21.dex */
public class DozeLog {
    private static final boolean ENABLED = true;
    static final SimpleDateFormat FORMAT;
    public static final int PULSE_REASON_DOCKING = 6;
    public static final int PULSE_REASON_INTENT = 0;
    public static final int PULSE_REASON_NONE = -1;
    public static final int PULSE_REASON_NOTIFICATION = 1;
    public static final int PULSE_REASON_SENSOR_LONG_PRESS = 5;
    public static final int PULSE_REASON_SENSOR_SIGMOTION = 2;
    public static final int PULSE_REASON_SENSOR_WAKE_LOCK_SCREEN = 8;
    public static final int REASONS = 10;
    public static final int REASON_SENSOR_DOUBLE_TAP = 4;
    public static final int REASON_SENSOR_PICKUP = 3;
    public static final int REASON_SENSOR_TAP = 9;
    public static final int REASON_SENSOR_WAKE_UP = 7;
    private static final int SIZE;
    private static int sCount;
    private static SummaryStats sEmergencyCallStats;
    private static final KeyguardUpdateMonitorCallback sKeyguardCallback;
    private static String[] sMessages;
    private static SummaryStats sNotificationPulseStats;
    private static SummaryStats sPickupPulseNearVibrationStats;
    private static SummaryStats sPickupPulseNotNearVibrationStats;
    private static int sPosition;
    private static SummaryStats[][] sProxStats;
    private static boolean sPulsing;
    private static boolean sRegisterKeyguardCallback;
    private static SummaryStats sScreenOnNotPulsingStats;
    private static SummaryStats sScreenOnPulsingStats;
    private static long sSince;
    private static long[] sTimes;
    private static final String TAG = "DozeLog";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    static {
        SIZE = Build.IS_DEBUGGABLE ? 400 : 50;
        FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        sRegisterKeyguardCallback = true;
        sKeyguardCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.doze.DozeLog.1
            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onEmergencyCallAction() {
                DozeLog.traceEmergencyCall();
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onKeyguardBouncerChanged(boolean bouncer) {
                DozeLog.traceKeyguardBouncerChanged(bouncer);
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onStartedWakingUp() {
                DozeLog.traceScreenOn();
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onFinishedGoingToSleep(int why) {
                DozeLog.traceScreenOff(why);
            }

            @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
            public void onKeyguardVisibilityChanged(boolean showing) {
                DozeLog.traceKeyguard(showing);
            }
        };
    }

    public static void tracePickupWakeUp(Context context, boolean withinVibrationThreshold) {
        init(context);
        log("pickupWakeUp withinVibrationThreshold=" + withinVibrationThreshold);
        (withinVibrationThreshold ? sPickupPulseNearVibrationStats : sPickupPulseNotNearVibrationStats).append();
    }

    public static void tracePulseStart(int reason) {
        sPulsing = true;
        log("pulseStart reason=" + reasonToString(reason));
    }

    public static void tracePulseFinish() {
        sPulsing = false;
        log("pulseFinish");
    }

    public static void traceNotificationPulse(Context context) {
        init(context);
        log("notificationPulse");
        sNotificationPulseStats.append();
    }

    private static void init(Context context) {
        synchronized (DozeLog.class) {
            if (sMessages == null) {
                sTimes = new long[SIZE];
                sMessages = new String[SIZE];
                sSince = System.currentTimeMillis();
                sPickupPulseNearVibrationStats = new SummaryStats();
                sPickupPulseNotNearVibrationStats = new SummaryStats();
                sNotificationPulseStats = new SummaryStats();
                sScreenOnPulsingStats = new SummaryStats();
                sScreenOnNotPulsingStats = new SummaryStats();
                sEmergencyCallStats = new SummaryStats();
                sProxStats = (SummaryStats[][]) Array.newInstance(SummaryStats.class, 10, 2);
                for (int i = 0; i < 10; i++) {
                    sProxStats[i][0] = new SummaryStats();
                    sProxStats[i][1] = new SummaryStats();
                }
                log("init");
                if (sRegisterKeyguardCallback) {
                    KeyguardUpdateMonitor.getInstance(context).registerCallback(sKeyguardCallback);
                }
            }
        }
    }

    public static void traceDozing(Context context, boolean dozing) {
        sPulsing = false;
        init(context);
        log("dozing " + dozing);
    }

    public static void traceFling(boolean expand, boolean aboveThreshold, boolean thresholdNeeded, boolean screenOnFromTouch) {
        log("fling expand=" + expand + " aboveThreshold=" + aboveThreshold + " thresholdNeeded=" + thresholdNeeded + " screenOnFromTouch=" + screenOnFromTouch);
    }

    public static void traceEmergencyCall() {
        log("emergencyCall");
        sEmergencyCallStats.append();
    }

    public static void traceKeyguardBouncerChanged(boolean showing) {
        log("bouncer " + showing);
    }

    public static void traceScreenOn() {
        log("screenOn pulsing=" + sPulsing);
        (sPulsing ? sScreenOnPulsingStats : sScreenOnNotPulsingStats).append();
        sPulsing = false;
    }

    public static void traceScreenOff(int why) {
        log("screenOff why=" + why);
    }

    public static void traceMissedTick(String delay) {
        log("missedTick by=" + delay);
    }

    public static void traceTimeTickScheduled(long when, long triggerAt) {
        log("timeTickScheduled at=" + FORMAT.format(new Date(when)) + " triggerAt=" + FORMAT.format(new Date(triggerAt)));
    }

    public static void traceKeyguard(boolean showing) {
        log("keyguard " + showing);
        if (!showing) {
            sPulsing = false;
        }
    }

    public static void traceState(DozeMachine.State state) {
        log("state " + state);
    }

    public static void traceWakeDisplay(boolean wake) {
        log("wakeDisplay " + wake);
    }

    public static void traceProximityResult(Context context, boolean near, long millis, int reason) {
        init(context);
        log("proximityResult reason=" + reasonToString(reason) + " near=" + near + " millis=" + millis);
        sProxStats[reason][!near ? 1 : 0].append();
    }

    public static String reasonToString(int pulseReason) {
        switch (pulseReason) {
            case 0:
                return "intent";
            case 1:
                return "notification";
            case 2:
                return "sigmotion";
            case 3:
                return "pickup";
            case 4:
                return "doubletap";
            case 5:
                return "longpress";
            case 6:
                return "docking";
            case 7:
                return "wakeup";
            case 8:
                return "wakelockscreen";
            case 9:
                return "tap";
            default:
                throw new IllegalArgumentException("bad reason: " + pulseReason);
        }
    }

    public static void dump(PrintWriter pw) {
        synchronized (DozeLog.class) {
            if (sMessages == null) {
                return;
            }
            pw.println("  Doze log:");
            int start = ((sPosition - sCount) + SIZE) % SIZE;
            for (int i = 0; i < sCount; i++) {
                int j = (start + i) % SIZE;
                pw.print("    ");
                pw.print(FORMAT.format(new Date(sTimes[j])));
                pw.print(' ');
                pw.println(sMessages[j]);
            }
            pw.print("  Doze summary stats (for ");
            TimeUtils.formatDuration(System.currentTimeMillis() - sSince, pw);
            pw.println("):");
            sPickupPulseNearVibrationStats.dump(pw, "Pickup pulse (near vibration)");
            sPickupPulseNotNearVibrationStats.dump(pw, "Pickup pulse (not near vibration)");
            sNotificationPulseStats.dump(pw, "Notification pulse");
            sScreenOnPulsingStats.dump(pw, "Screen on (pulsing)");
            sScreenOnNotPulsingStats.dump(pw, "Screen on (not pulsing)");
            sEmergencyCallStats.dump(pw, "Emergency call");
            for (int i2 = 0; i2 < 10; i2++) {
                String reason = reasonToString(i2);
                sProxStats[i2][0].dump(pw, "Proximity near (" + reason + NavigationBarInflaterView.KEY_CODE_END);
                sProxStats[i2][1].dump(pw, "Proximity far (" + reason + NavigationBarInflaterView.KEY_CODE_END);
            }
        }
    }

    private static void log(String msg) {
        synchronized (DozeLog.class) {
            if (sMessages == null) {
                return;
            }
            sTimes[sPosition] = System.currentTimeMillis();
            sMessages[sPosition] = msg;
            sPosition = (sPosition + 1) % SIZE;
            sCount = Math.min(sCount + 1, SIZE);
            if (DEBUG) {
                Log.d(TAG, msg);
            }
        }
    }

    public static void tracePulseDropped(Context context, boolean pulsePending, DozeMachine.State state, boolean blocked) {
        init(context);
        log("pulseDropped pulsePending=" + pulsePending + " state=" + state + " blocked=" + blocked);
    }

    public static void tracePulseDropped(Context context, String why) {
        init(context);
        log("pulseDropped why=" + why);
    }

    public static void tracePulseTouchDisabledByProx(Context context, boolean disabled) {
        init(context);
        log("pulseTouchDisabledByProx " + disabled);
    }

    public static void setRegisterKeyguardCallback(boolean registerKeyguardCallback) {
        synchronized (DozeLog.class) {
            if (sRegisterKeyguardCallback != registerKeyguardCallback && sMessages != null) {
                throw new IllegalStateException("Cannot change setRegisterKeyguardCallback after init()");
            }
            sRegisterKeyguardCallback = registerKeyguardCallback;
        }
    }

    public static void traceSensor(Context context, int reason) {
        init(context);
        log("sensor type=" + reasonToString(reason));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class SummaryStats {
        private int mCount;

        private SummaryStats() {
        }

        public void append() {
            this.mCount++;
        }

        public void dump(PrintWriter pw, String type) {
            if (this.mCount == 0) {
                return;
            }
            pw.print("    ");
            pw.print(type);
            pw.print(": n=");
            pw.print(this.mCount);
            pw.print(" (");
            double perHr = (this.mCount / (System.currentTimeMillis() - DozeLog.sSince)) * 1000.0d * 60.0d * 60.0d;
            pw.print(perHr);
            pw.print("/hr)");
            pw.println();
        }
    }
}
