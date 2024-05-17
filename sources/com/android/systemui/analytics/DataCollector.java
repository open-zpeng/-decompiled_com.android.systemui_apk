package com.android.systemui.analytics;

import android.content.Context;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.MotionEvent;
import android.widget.Toast;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.FalsingPlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.phone.nano.TouchAnalyticsProto;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/* loaded from: classes21.dex */
public class DataCollector implements SensorEventListener {
    private static final String ALLOW_REJECTED_TOUCH_REPORTS = "data_collector_allow_rejected_touch_reports";
    private static final String COLLECTOR_ENABLE = "data_collector_enable";
    private static final String COLLECT_BAD_TOUCHES = "data_collector_collect_bad_touches";
    public static final boolean DEBUG = false;
    private static final String DISABLE_UNLOCKING_FOR_FALSING_COLLECTION = "data_collector_disable_unlocking";
    private static final String TAG = "DataCollector";
    private static final long TIMEOUT_MILLIS = 11000;
    private static DataCollector sInstance = null;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private SensorLoggerSession mCurrentSession = null;
    private boolean mEnableCollector = false;
    private boolean mCollectBadTouches = false;
    private boolean mCornerSwiping = false;
    private boolean mTrackingStarted = false;
    private boolean mAllowReportRejectedTouch = false;
    private boolean mDisableUnlocking = false;
    private FalsingPlugin mFalsingPlugin = null;
    protected final ContentObserver mSettingsObserver = new ContentObserver(this.mHandler) { // from class: com.android.systemui.analytics.DataCollector.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            DataCollector.this.updateConfiguration();
        }
    };
    private final PluginListener mPluginListener = new PluginListener<FalsingPlugin>() { // from class: com.android.systemui.analytics.DataCollector.2
        @Override // com.android.systemui.plugins.PluginListener
        public void onPluginConnected(FalsingPlugin plugin, Context context) {
            DataCollector.this.mFalsingPlugin = plugin;
        }

        @Override // com.android.systemui.plugins.PluginListener
        public void onPluginDisconnected(FalsingPlugin plugin) {
            DataCollector.this.mFalsingPlugin = null;
        }
    };

    private DataCollector(Context context) {
        this.mContext = context;
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(COLLECTOR_ENABLE), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(COLLECT_BAD_TOUCHES), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(ALLOW_REJECTED_TOUCH_REPORTS), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(DISABLE_UNLOCKING_FOR_FALSING_COLLECTION), false, this.mSettingsObserver, -1);
        updateConfiguration();
        ((PluginManager) Dependency.get(PluginManager.class)).addPluginListener(this.mPluginListener, FalsingPlugin.class);
    }

    public static DataCollector getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataCollector(context);
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConfiguration() {
        boolean z = true;
        this.mEnableCollector = Build.IS_DEBUGGABLE && Settings.Secure.getInt(this.mContext.getContentResolver(), COLLECTOR_ENABLE, 0) != 0;
        this.mCollectBadTouches = this.mEnableCollector && Settings.Secure.getInt(this.mContext.getContentResolver(), COLLECT_BAD_TOUCHES, 0) != 0;
        this.mAllowReportRejectedTouch = Build.IS_DEBUGGABLE && Settings.Secure.getInt(this.mContext.getContentResolver(), ALLOW_REJECTED_TOUCH_REPORTS, 0) != 0;
        if (!this.mEnableCollector || !Build.IS_DEBUGGABLE || Settings.Secure.getInt(this.mContext.getContentResolver(), DISABLE_UNLOCKING_FOR_FALSING_COLLECTION, 0) == 0) {
            z = false;
        }
        this.mDisableUnlocking = z;
    }

    private boolean sessionEntrypoint() {
        if (isEnabled() && this.mCurrentSession == null) {
            onSessionStart();
            return true;
        }
        return false;
    }

    private void sessionExitpoint(int result) {
        if (this.mCurrentSession != null) {
            onSessionEnd(result);
        }
    }

    private void onSessionStart() {
        this.mCornerSwiping = false;
        this.mTrackingStarted = false;
        this.mCurrentSession = new SensorLoggerSession(System.currentTimeMillis(), System.nanoTime());
    }

    private void onSessionEnd(int result) {
        SensorLoggerSession session = this.mCurrentSession;
        this.mCurrentSession = null;
        if (this.mEnableCollector || this.mDisableUnlocking) {
            session.end(System.currentTimeMillis(), result);
            queueSession(session);
        }
    }

    public Uri reportRejectedTouch() {
        if (this.mCurrentSession == null) {
            Toast.makeText(this.mContext, "Generating rejected touch report failed: session timed out.", 1).show();
            return null;
        }
        SensorLoggerSession currentSession = this.mCurrentSession;
        currentSession.setType(4);
        currentSession.end(System.currentTimeMillis(), 1);
        TouchAnalyticsProto.Session proto = currentSession.toProto();
        byte[] b = TouchAnalyticsProto.Session.toByteArray(proto);
        File dir = new File(this.mContext.getExternalCacheDir(), "rejected_touch_reports");
        dir.mkdir();
        File touch = new File(dir, "rejected_touch_report_" + System.currentTimeMillis());
        try {
            new FileOutputStream(touch).write(b);
            return Uri.fromFile(touch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void queueSession(final SensorLoggerSession currentSession) {
        AsyncTask.execute(new Runnable() { // from class: com.android.systemui.analytics.DataCollector.3
            @Override // java.lang.Runnable
            public void run() {
                byte[] b = TouchAnalyticsProto.Session.toByteArray(currentSession.toProto());
                if (DataCollector.this.mFalsingPlugin != null) {
                    DataCollector.this.mFalsingPlugin.dataCollected(currentSession.getResult() == 1, b);
                    return;
                }
                String dir = DataCollector.this.mContext.getFilesDir().getAbsolutePath();
                if (currentSession.getResult() != 1) {
                    if (!DataCollector.this.mDisableUnlocking && !DataCollector.this.mCollectBadTouches) {
                        return;
                    }
                    dir = dir + "/bad_touches";
                } else if (!DataCollector.this.mDisableUnlocking) {
                    dir = dir + "/good_touches";
                }
                File file = new File(dir);
                file.mkdir();
                File touch = new File(file, "trace_" + System.currentTimeMillis());
                try {
                    new FileOutputStream(touch).write(b);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override // android.hardware.SensorEventListener
    public synchronized void onSensorChanged(SensorEvent event) {
        if (isEnabled() && this.mCurrentSession != null) {
            this.mCurrentSession.addSensorEvent(event, System.nanoTime());
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public boolean isEnabled() {
        return this.mEnableCollector || this.mAllowReportRejectedTouch || this.mDisableUnlocking;
    }

    public boolean isUnlockingDisabled() {
        return this.mDisableUnlocking;
    }

    public boolean isEnabledFull() {
        return this.mEnableCollector;
    }

    public void onScreenTurningOn() {
        if (sessionEntrypoint()) {
            addEvent(0);
        }
    }

    public void onScreenOnFromTouch() {
        if (sessionEntrypoint()) {
            addEvent(1);
        }
    }

    public void onScreenOff() {
        addEvent(2);
        sessionExitpoint(0);
    }

    public void onSucccessfulUnlock() {
        addEvent(3);
        sessionExitpoint(1);
    }

    public void onBouncerShown() {
        addEvent(4);
    }

    public void onBouncerHidden() {
        addEvent(5);
    }

    public void onQsDown() {
        addEvent(6);
    }

    public void setQsExpanded(boolean expanded) {
        if (expanded) {
            addEvent(7);
        } else {
            addEvent(8);
        }
    }

    public void onTrackingStarted() {
        this.mTrackingStarted = true;
        addEvent(9);
    }

    public void onTrackingStopped() {
        if (this.mTrackingStarted) {
            this.mTrackingStarted = false;
            addEvent(10);
        }
    }

    public void onNotificationActive() {
        addEvent(11);
    }

    public void onNotificationDoubleTap() {
        addEvent(13);
    }

    public void setNotificationExpanded() {
        addEvent(14);
    }

    public void onNotificatonStartDraggingDown() {
        addEvent(16);
    }

    public void onStartExpandingFromPulse() {
    }

    public void onExpansionFromPulseStopped() {
    }

    public void onNotificatonStopDraggingDown() {
        addEvent(17);
    }

    public void onNotificationDismissed() {
        addEvent(18);
    }

    public void onNotificatonStartDismissing() {
        addEvent(19);
    }

    public void onNotificatonStopDismissing() {
        addEvent(20);
    }

    public void onCameraOn() {
        addEvent(24);
    }

    public void onLeftAffordanceOn() {
        addEvent(25);
    }

    public void onAffordanceSwipingStarted(boolean rightCorner) {
        this.mCornerSwiping = true;
        if (rightCorner) {
            addEvent(21);
        } else {
            addEvent(22);
        }
    }

    public void onAffordanceSwipingAborted() {
        if (this.mCornerSwiping) {
            this.mCornerSwiping = false;
            addEvent(23);
        }
    }

    public void onUnlockHintStarted() {
        addEvent(26);
    }

    public void onCameraHintStarted() {
        addEvent(27);
    }

    public void onLeftAffordanceHintStarted() {
        addEvent(28);
    }

    public void onTouchEvent(MotionEvent event, int width, int height) {
        SensorLoggerSession sensorLoggerSession = this.mCurrentSession;
        if (sensorLoggerSession != null) {
            sensorLoggerSession.addMotionEvent(event);
            this.mCurrentSession.setTouchArea(width, height);
        }
    }

    private void addEvent(int eventType) {
        SensorLoggerSession sensorLoggerSession;
        if (isEnabled() && (sensorLoggerSession = this.mCurrentSession) != null) {
            sensorLoggerSession.addPhoneEvent(eventType, System.nanoTime());
        }
    }

    public boolean isReportingEnabled() {
        return this.mAllowReportRejectedTouch;
    }

    public void onFalsingSessionStarted() {
        sessionEntrypoint();
    }
}
