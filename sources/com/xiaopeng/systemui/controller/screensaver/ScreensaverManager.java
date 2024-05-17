package com.xiaopeng.systemui.controller.screensaver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import com.android.systemui.R;
import com.xiaopeng.app.xpDialogInfo;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.controller.brightness.BrightnessCarManager;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.statusbar.MaskLayer.ScreensaverPresenter;
import com.xiaopeng.systemui.ui.widget.ScreensaverSurfaceView;
import com.xiaopeng.util.FeatureOption;
import com.xiaopeng.view.SharedDisplayListener;
import com.xiaopeng.view.WindowManagerFactory;
import com.xiaopeng.xui.app.XToast;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class ScreensaverManager {
    private static final String TAG = "ScreensaverManager";
    private Context mContext;
    private final PowerManager mPowerManager;
    private View mScreensaverView;
    private SettingsObserver mSettingsObserver;
    private WindowManagerFactory mWindowFactory;
    private WindowManager mWindowManager;
    private MediaPlayerHelper mediaPlayerHelper;
    private SurfaceHolder surfaceHolder;
    public static final boolean IS_CIRCULATION = FeatureOption.FO_SHARED_DISPLAY_ENABLED;
    private static boolean mIdle = false;
    private static int mScreensaverMode = 0;
    private static boolean mAcquire = false;
    private static boolean mStatus = true;
    private static ScreensaverManager mScreensaverManager = null;
    private static final boolean DEBUG = SystemProperties.getBoolean("persist.sys.sysui.screensaver.debug", false);
    private boolean mInitCompleted = false;
    private boolean mPlaying = false;
    private boolean mDialog = false;
    private final Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.controller.screensaver.ScreensaverManager.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (i == 1) {
                ScreensaverPresenter.getInstance().updateView(true);
                PowerManager powerManager = ScreensaverManager.this.mPowerManager;
                PowerManager unused = ScreensaverManager.this.mPowerManager;
                powerManager.setXpScreenIdle("xp_mt_psg", true);
                boolean unused2 = ScreensaverManager.mIdle = true;
            } else if (i == 2) {
                XToast.show(ScreensaverManager.this.mContext.getText(R.string.toast_media_play), 0, 1);
            } else if (i == 3) {
                ScreensaverManager.this.mediaCreate();
            } else if (i != 4) {
                if (i == 5) {
                    long time = SystemClock.uptimeMillis();
                    ScreensaverManager.this.mPowerManager.setXpScreenOff("xp_mt_psg", time);
                }
            } else {
                ScreensaverPresenter.getInstance().updateView(false);
                PowerManager powerManager2 = ScreensaverManager.this.mPowerManager;
                PowerManager unused3 = ScreensaverManager.this.mPowerManager;
                powerManager2.setXpScreenIdle("xp_mt_psg", false);
                boolean unused4 = ScreensaverManager.mIdle = false;
            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.controller.screensaver.ScreensaverManager.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (ScreensaverSettings.SCREEN_STATUS_CHANGE_ACTION.equals(intent.getAction())) {
                String deviceName = intent.getStringExtra("device");
                boolean status = intent.getBooleanExtra("status", true);
                Logger.d(ScreensaverManager.TAG, "onReceive " + deviceName + " " + status);
                if ("xp_mt_psg".equals(deviceName)) {
                    boolean unused = ScreensaverManager.mStatus = status;
                    if (ScreensaverManager.this.mWindowFactory != null) {
                        ScreensaverManager.this.mWindowFactory.setSharedEvent(120, 1, status ? "1" : "0");
                    }
                    if (status) {
                        ScreensaverManager.this.updateScreenState();
                        if (ScreensaverManager.DEBUG) {
                            Logger.d(ScreensaverManager.TAG, "screenOn updateScreenState");
                        }
                    }
                    if (ScreensaverManager.mScreensaverMode != 0) {
                        PowerManager powerManager = ScreensaverManager.this.mPowerManager;
                        PowerManager unused2 = ScreensaverManager.this.mPowerManager;
                        powerManager.setXpScreenIdle("xp_mt_psg", !status);
                        boolean unused3 = ScreensaverManager.mIdle = !status;
                    } else if (!status) {
                        ScreensaverManager.this.StopDoIdle();
                        if (ScreensaverManager.DEBUG) {
                            Logger.d(ScreensaverManager.TAG, "screenOn stop");
                        }
                    }
                }
            }
            if (ScreensaverSettings.ACTION_SCREENSAVER_EXIT.equals(intent.getAction())) {
                ScreensaverPresenter.getInstance().updateView(false);
                PowerManager powerManager2 = ScreensaverManager.this.mPowerManager;
                PowerManager unused4 = ScreensaverManager.this.mPowerManager;
                powerManager2.setXpScreenIdle("xp_mt_psg", false);
                boolean unused5 = ScreensaverManager.mIdle = false;
                ScreensaverManager.this.updateScreenState();
            }
            if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                Logger.d(ScreensaverManager.TAG, "onReceive ACTION_SCREEN_ON mAcquire=" + ScreensaverManager.mAcquire);
                boolean unused6 = ScreensaverManager.mStatus = true;
                ScreensaverManager.this.updateScreenState();
            }
            if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                Logger.d(ScreensaverManager.TAG, "onReceive ACTION_SCREEN_OFF mAcquire=" + ScreensaverManager.mAcquire);
                boolean unused7 = ScreensaverManager.mStatus = false;
                ScreensaverManager.this.StopDoIdle();
            }
            if (PackageHelper.ACTION_DIALOG_CHANGED.equals(intent.getAction())) {
                ScreensaverManager.this.onDialogChanged(intent);
            }
            if (ScreensaverSettings.SCREEN_IDLE_CHANGE_ACTION.equals(intent.getAction())) {
                String deviceName2 = intent.getStringExtra("device");
                boolean isIdle = intent.getBooleanExtra("isIdle", false);
                Logger.d(ScreensaverManager.TAG, "onReceive " + deviceName2 + " isIdle=" + isIdle);
                if ("xp_mt_psg".equals(deviceName2)) {
                    if (!isIdle) {
                        if (ScreensaverManager.mScreensaverMode == 1) {
                            if (ScreensaverManager.this.mPlaying) {
                                ScreensaverManager.this.stop();
                            }
                        } else if (ScreensaverManager.mScreensaverMode == 0) {
                            Message msg = ScreensaverManager.this.mHandler.obtainMessage(4);
                            ScreensaverManager.this.mHandler.sendMessage(msg);
                        } else {
                            long time = SystemClock.uptimeMillis();
                            ScreensaverManager.this.mPowerManager.setXpScreenOn("xp_mt_psg", time);
                        }
                        ScreensaverManager.this.updateScreenState();
                        Logger.d(ScreensaverManager.TAG, "setScreenIdle false");
                    }
                    if (isIdle && !ScreensaverManager.mAcquire) {
                        if (ScreensaverManager.mScreensaverMode != 1) {
                            if (ScreensaverManager.mScreensaverMode == 0) {
                                if (ScreensaverManager.this.isInDoIdle()) {
                                    ScreensaverManager.this.mHandler.removeMessages(1);
                                }
                                Message msg2 = ScreensaverManager.this.mHandler.obtainMessage(1);
                                ScreensaverManager.this.mHandler.sendMessage(msg2);
                            } else {
                                if (ScreensaverManager.this.isInDoIdle()) {
                                    ScreensaverManager.this.mHandler.removeMessages(5);
                                }
                                Message msg3 = ScreensaverManager.this.mHandler.obtainMessage(5);
                                ScreensaverManager.this.mHandler.sendMessage(msg3);
                            }
                        } else {
                            if (ScreensaverManager.this.isInDoIdle()) {
                                ScreensaverManager.this.mHandler.removeMessages(2);
                                ScreensaverManager.this.mHandler.removeMessages(3);
                            }
                            if (!ScreensaverManager.this.mPlaying) {
                                Message msg4 = ScreensaverManager.this.mHandler.obtainMessage(3);
                                ScreensaverManager.this.mHandler.sendMessage(msg4);
                            }
                        }
                        Logger.d(ScreensaverManager.TAG, "setScreenIdle true");
                    }
                }
            }
        }
    };

    public static ScreensaverManager get(Context context) {
        if (mScreensaverManager == null) {
            synchronized (ScreensaverManager.class) {
                if (mScreensaverManager == null) {
                    mScreensaverManager = new ScreensaverManager(context);
                }
            }
        }
        return mScreensaverManager;
    }

    private ScreensaverManager(Context context) {
        this.mContext = context;
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
    }

    public void init() {
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        this.mSettingsObserver.startObserving();
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        ScreensaverSettings.SCREEN_TIMEOUT = ScreensaverSettings.getInt(this.mContext, "passenger_screen_time", ScreensaverSettings.DEF_PASSENGER_SCREEN_TIME);
        ScreensaverSettings.SCREEN_EXTRA_TIMEOUT = ScreensaverSettings.getInt(this.mContext, ScreensaverSettings.KEY_PASSENGER_SCREEN_EXTRA_TIME, ScreensaverSettings.DEF_PASSENGER_SCREEN_EXTRA_TIME);
        ScreensaverSettings.SCREEN_OFF_TIMEOUT = ScreensaverSettings.SCREEN_TIMEOUT + ScreensaverSettings.SCREEN_EXTRA_TIMEOUT;
        ScreensaverSettings.TOAST_TIMEOUT = ScreensaverSettings.SCREEN_TIMEOUT - 5000;
        initScreensaver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initScreensaver() {
        boolean serviceConnected = BrightnessCarManager.get(this.mContext).isCarConnected();
        boolean bootCompleted = "1".equals(SystemProperties.get("sys.boot_completed"));
        Logger.i(TAG, "initScreensaver serviceConnected=" + serviceConnected + " bootCompleted=" + bootCompleted);
        if (serviceConnected && bootCompleted) {
            this.mInitCompleted = true;
            if (IS_CIRCULATION) {
                this.mWindowFactory = WindowManagerFactory.create(this.mContext);
                WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
                wm.registerSharedListener(new SharedDisplayListenerImpl());
                updateScreenState();
                this.mWindowFactory.setSharedEvent(120, 1, "1");
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ScreensaverSettings.SCREEN_STATUS_CHANGE_ACTION);
                intentFilter.addAction("android.intent.action.SCREEN_ON");
                intentFilter.addAction("android.intent.action.SCREEN_OFF");
                intentFilter.addAction(ScreensaverSettings.SCREEN_IDLE_CHANGE_ACTION);
                intentFilter.addAction(ScreensaverSettings.ACTION_SCREENSAVER_EXIT);
                intentFilter.addAction(PackageHelper.ACTION_DIALOG_CHANGED);
                this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
                return;
            }
            return;
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.controller.screensaver.ScreensaverManager.3
            @Override // java.lang.Runnable
            public void run() {
                ScreensaverManager.this.initScreensaver();
            }
        }, OsdController.TN.DURATION_TIMEOUT_SHORT);
    }

    public synchronized void onSystemGestureChanged() {
        if (IS_CIRCULATION) {
            if (isInDoIdle()) {
                try {
                    ContentResolver resolver = this.mContext.getContentResolver();
                    String property = Settings.Secure.getString(resolver, "key_system_gesture_event");
                    JSONObject jsonObject = new JSONObject(property);
                    int screenId = jsonObject.getInt("screenId");
                    if (screenId == 1) {
                        updateScreenState();
                        if (DEBUG) {
                            Logger.d(TAG, "KEY_SYSTEM_GESTURE_EVENT");
                        }
                    }
                } catch (Exception e) {
                    Logger.i(TAG, "onSystemGestureChanged new json object failed");
                }
            }
        }
    }

    public synchronized void onPassengerScreenTimeChanged() {
        ScreensaverSettings.SCREEN_TIMEOUT = ScreensaverSettings.getInt(this.mContext, "passenger_screen_time", ScreensaverSettings.DEF_PASSENGER_SCREEN_TIME);
        ScreensaverSettings.SCREEN_EXTRA_TIMEOUT = ScreensaverSettings.getInt(this.mContext, ScreensaverSettings.KEY_PASSENGER_SCREEN_EXTRA_TIME, ScreensaverSettings.DEF_PASSENGER_SCREEN_EXTRA_TIME);
        ScreensaverSettings.SCREEN_OFF_TIMEOUT = ScreensaverSettings.SCREEN_TIMEOUT + ScreensaverSettings.SCREEN_EXTRA_TIMEOUT;
        ScreensaverSettings.TOAST_TIMEOUT = ScreensaverSettings.SCREEN_TIMEOUT - 5000;
        Logger.d(TAG, "SCREEN_TIMEOUT changed=" + ScreensaverSettings.SCREEN_TIMEOUT + " SCREEN_EXTRA_TIMEOUT=" + ScreensaverSettings.SCREEN_EXTRA_TIMEOUT);
        StartDoIdle();
    }

    public synchronized void onIsScreensaverChanged() {
        mScreensaverMode = ScreensaverSettings.getInt(this.mContext, ScreensaverSettings.KEY_PASSENGER_SCREENSAVER, 0);
        Logger.d(TAG, "mScreensaverMode" + mScreensaverMode);
        StartDoIdle();
    }

    public synchronized void onSettingsChanged(boolean selfChange, Uri uri) {
        if (ScreensaverSettings.URI_SYSTEM_GESTURE_EVENT.equals(uri)) {
            onSystemGestureChanged();
        } else {
            if (!ScreensaverSettings.URI_PASSENGER_SCREEN_TIME.equals(uri) && !ScreensaverSettings.URI_PASSENGER_SCREEN_EXTRA_TIME.equals(uri)) {
                if (ScreensaverSettings.URI_PASSENGER_SCREENSAVER.equals(uri)) {
                    onIsScreensaverChanged();
                }
            }
            onPassengerScreenTimeChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public final class SharedDisplayListenerImpl extends SharedDisplayListener {
        private SharedDisplayListenerImpl() {
        }

        public void onActivityChanged(int screenId, String property) throws RemoteException {
            super.onActivityChanged(screenId, property);
            Logger.d(ScreensaverManager.TAG, "onActivityChanged screenId=" + screenId + " property=" + property);
            if (screenId == 1) {
                ScreensaverManager.this.updateScreenState();
            }
        }

        public void onPositionChanged(String packageName, int event, int from, int to) throws RemoteException {
            super.onPositionChanged(packageName, event, from, to);
            Logger.d(ScreensaverManager.TAG, "onPositionChanged packageName=" + packageName + " event=" + event);
            if (4 == event) {
                ScreensaverManager.this.StopDoIdle();
                ScreensaverManager.this.updateScreenState();
            }
        }

        public void onEventChanged(int event, String property) {
            if (!ScreensaverManager.IS_CIRCULATION) {
                return;
            }
            if (event == 200) {
                try {
                    JSONObject jsonObject = new JSONObject(property);
                    int screenId = jsonObject.getInt("screenId");
                    if (screenId == 1) {
                        ScreensaverManager.this.StopDoIdle();
                        boolean unused = ScreensaverManager.mAcquire = true;
                        if (ScreensaverManager.DEBUG) {
                            Logger.d(ScreensaverManager.TAG, "EVENT_WAKEUP_ACQUIRE");
                        }
                    }
                } catch (Exception e) {
                    Logger.i(ScreensaverManager.TAG, "EVENT_WAKEUP_ACQUIRE new json object failed");
                }
            } else if (event == 201) {
                try {
                    JSONObject jsonObject2 = new JSONObject(property);
                    int screenId2 = jsonObject2.getInt("screenId");
                    if (screenId2 == 1 && ScreensaverManager.mAcquire && ScreensaverManager.mStatus) {
                        ScreensaverManager.this.StartDoIdle();
                        boolean unused2 = ScreensaverManager.mAcquire = false;
                        if (ScreensaverManager.DEBUG) {
                            Logger.d(ScreensaverManager.TAG, "EVENT_WAKEUP_RELEASE");
                        }
                    }
                } catch (Exception e2) {
                    Logger.i(ScreensaverManager.TAG, "EVENT_WAKEUP_RELEASE new json object failed");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mediaCreate() {
        this.mScreensaverView = View.inflate(this.mContext, R.layout.layout_secondary_screensaver_window, null);
        WindowHelper.addSecondaryScreensaverWindow(this.mWindowManager, this.mScreensaverView);
        ScreensaverSurfaceView surfaceView = (ScreensaverSurfaceView) this.mScreensaverView.findViewById(R.id.screensaverSurfaceView);
        this.surfaceHolder = surfaceView.getHolder();
        this.surfaceHolder.setType(3);
        this.surfaceHolder.addCallback(new ScreensaverSurfaceView(this.mContext));
        this.mediaPlayerHelper = MediaPlayerHelper.getInstance(this.mContext);
    }

    public void play() {
        this.mediaPlayerHelper.setPlay(this.surfaceHolder, "/system/media/screensaver1.mp4");
        this.mPowerManager.setXpScreenIdle("xp_mt_psg", true);
        this.mPlaying = true;
        boolean screenIdle = this.mPowerManager.xpIsScreenIdle("xp_mt_psg");
        Logger.d(TAG, "start Play screenIdle=" + screenIdle);
    }

    public void stop() {
        if (this.mediaPlayerHelper.isPlaying()) {
            this.mediaPlayerHelper.stop();
            this.mPowerManager.setXpScreenIdle("xp_mt_psg", false);
            this.mPlaying = false;
            this.mWindowManager.removeView(this.mScreensaverView);
            boolean screenIdle = this.mPowerManager.xpIsScreenIdle("xp_mt_psg");
            Logger.d(TAG, "stop Play screenIdle=" + screenIdle);
        }
    }

    public void onDialogChanged(Intent intent) {
        Bundle extras = new Bundle();
        boolean z = true;
        extras.putInt("screenId", 1);
        xpDialogInfo dialogInfo = this.mWindowManager.getTopDialog(extras);
        if (dialogInfo == null) {
            this.mDialog = false;
            updateScreenState();
            return;
        }
        this.mDialog = dialogInfo.topVisible();
        Logger.d(TAG, "on Dialog Change mDialog= " + this.mDialog);
        if (intent.hasExtra(PackageHelper.EXTRA_TOPPING_DIALOG)) {
            if (!dialogInfo.visible || dialogInfo.dimAmount == 0.0f) {
                z = false;
            }
            boolean hasVisibleDialog = z;
            if (hasVisibleDialog) {
                StopDoIdle();
            }
        }
    }

    public void updateScreenState() {
        String secondary = this.mWindowFactory.getTopActivity(0, 1);
        if (DEBUG) {
            Logger.d(TAG, "updateScreenState " + secondary);
        }
        if (secondary == null || secondary.equals("")) {
            if (!mAcquire && mStatus) {
                StartDoIdle();
                return;
            }
            return;
        }
        ComponentName component = ComponentName.unflattenFromString(secondary);
        if (DEBUG) {
            Logger.d(TAG, "ComponentName " + component);
        }
        if (component != null) {
            String packageName = component.getPackageName();
            boolean isLauncher = PackageHelper.isHomePackage(this.mContext, packageName);
            if (isLauncher && !this.mDialog) {
                if (!mAcquire && mStatus) {
                    StartDoIdle();
                    return;
                }
                return;
            }
            StopDoIdle();
        }
    }

    public boolean isInDoIdle() {
        int i = mScreensaverMode;
        if (i == 1) {
            return this.mHandler.hasMessages(3) || this.mHandler.hasMessages(2);
        } else if (i == 0) {
            return this.mHandler.hasMessages(1) || this.mHandler.hasMessages(5);
        } else {
            return this.mHandler.hasMessages(5);
        }
    }

    public void StartDoIdle() {
        int i = mScreensaverMode;
        if (i == 1) {
            if (isInDoIdle()) {
                this.mHandler.removeMessages(2);
                this.mHandler.removeMessages(3);
            }
            if (!this.mPlaying) {
                Handler handler = this.mHandler;
                handler.sendMessageDelayed(handler.obtainMessage(2), ScreensaverSettings.TOAST_TIMEOUT);
                Handler handler2 = this.mHandler;
                handler2.sendMessageDelayed(handler2.obtainMessage(3), ScreensaverSettings.SCREEN_TIMEOUT);
            }
        } else if (i == 0) {
            if (!mIdle) {
                if (isInDoIdle()) {
                    this.mHandler.removeMessages(1);
                    this.mHandler.removeMessages(5);
                }
                Handler handler3 = this.mHandler;
                handler3.sendMessageDelayed(handler3.obtainMessage(1), ScreensaverSettings.SCREEN_TIMEOUT);
                Handler handler4 = this.mHandler;
                handler4.sendMessageDelayed(handler4.obtainMessage(5), ScreensaverSettings.SCREEN_OFF_TIMEOUT);
            }
        } else {
            if (isInDoIdle()) {
                this.mHandler.removeMessages(5);
            }
            if (!mIdle) {
                Message msg = this.mHandler.obtainMessage(5);
                this.mHandler.sendMessageDelayed(msg, ScreensaverSettings.SCREEN_TIMEOUT);
            }
        }
    }

    public void StopDoIdle() {
        int i = mScreensaverMode;
        if (i == 1) {
            if (this.mPlaying) {
                stop();
            }
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
        } else if (i != 0) {
            this.mHandler.removeMessages(5);
        } else {
            if (mIdle) {
                Message msg = this.mHandler.obtainMessage(4);
                this.mHandler.sendMessage(msg);
            }
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(5);
        }
    }

    /* loaded from: classes24.dex */
    private class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (selfChange) {
                return;
            }
            ScreensaverManager.this.onSettingsChanged(selfChange, uri);
        }

        public void startObserving() {
            ContentResolver resolver = ScreensaverManager.this.mContext.getContentResolver();
            resolver.unregisterContentObserver(this);
            resolver.registerContentObserver(ScreensaverSettings.URI_SYSTEM_GESTURE_EVENT, false, this, -1);
            resolver.registerContentObserver(ScreensaverSettings.URI_PASSENGER_SCREEN_TIME, false, this, -1);
            resolver.registerContentObserver(ScreensaverSettings.URI_PASSENGER_SCREEN_EXTRA_TIME, false, this, -1);
            resolver.registerContentObserver(ScreensaverSettings.URI_PASSENGER_SCREENSAVER, false, this, -1);
        }

        public void stopObserving() {
            ContentResolver resolver = ScreensaverManager.this.mContext.getContentResolver();
            resolver.unregisterContentObserver(this);
        }
    }
}
