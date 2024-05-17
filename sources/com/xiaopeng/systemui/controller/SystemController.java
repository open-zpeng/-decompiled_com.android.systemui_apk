package com.xiaopeng.systemui.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.checking.CarCheckHelper;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class SystemController {
    public static final String KEY_SYSTEM_MEDITATION_MODE = "key_system_meditation_mode";
    private static final int MSG_MEDITATION_ENTER = 1000;
    private static final int MSG_MEDITATION_EXIT = 1001;
    private static final String TAG = "SystemController";
    private Context mContext;
    private final SettingsObserver mSettingsObserver;
    public static final Uri URI_SYSTEM_MEDITATION_MODE = Settings.Secure.getUriFor("key_system_meditation_mode");
    public static final Uri URI_SYSTEM_TIME_FORMAT_MODE = Settings.System.getUriFor("time_12_24");
    public static final String KEY_SHOW_OOBE = "xp_oobe_show";
    public static final Uri URI_OOBE_SHOW_STATUS = Settings.Global.getUriFor(KEY_SHOW_OOBE);
    private static SystemController sSystemController = null;
    private List<OnTimeFormatChangedListener> mOnTimeFormatChangedListenerList = new ArrayList();
    private final Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.controller.SystemController.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (i == 1000) {
                SystemController.this.enterMeditationMode();
            } else if (i == 1001) {
                SystemController.this.exitMeditationMode();
            }
        }
    };

    /* loaded from: classes24.dex */
    public interface OnTimeFormatChangedListener {
        void onTimeFormatChanged();
    }

    public static SystemController getInstance(Context context) {
        if (sSystemController == null) {
            synchronized (BrightnessController.class) {
                if (sSystemController == null) {
                    sSystemController = new SystemController(context);
                }
            }
        }
        return sSystemController;
    }

    private SystemController(Context context) {
        this.mContext = context;
        this.mSettingsObserver = new SettingsObserver(context, this.mHandler);
        init();
    }

    public void addOnTimeFormatChangeListener(OnTimeFormatChangedListener listener) {
        this.mOnTimeFormatChangedListenerList.add(listener);
    }

    public void removeOnTimeFormatChangeListener(OnTimeFormatChangedListener listener) {
        this.mOnTimeFormatChangedListenerList.remove(listener);
    }

    public void init() {
        this.mSettingsObserver.registerTimeFormatObserver();
        this.mSettingsObserver.registerOOBEShowObserver();
    }

    private void handleMeditationMode(boolean enter) {
        if (enter) {
            this.mHandler.removeMessages(1001);
            this.mHandler.removeMessages(1000);
            this.mHandler.sendEmptyMessageDelayed(1000, 0L);
            return;
        }
        this.mHandler.removeMessages(1001);
        this.mHandler.sendEmptyMessageDelayed(1001, 100L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enterMeditationMode() {
        SystemProperties.set(BrightnessController.PROP_BRIGHTNESS_DARK_IGNORE, OOBEEvent.STRING_TRUE);
        BrightnessController.getInstance(this.mContext).updateBrightness();
        BrightnessController.getInstance(this.mContext).animateIcmBrightness(1, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void exitMeditationMode() {
        SystemProperties.set(BrightnessController.PROP_BRIGHTNESS_DARK_IGNORE, OOBEEvent.STRING_FALSE);
        BrightnessController.getInstance(this.mContext).updateBrightness();
        int brightness = BrightnessController.getInstance(this.mContext).getTargetIcmBrightness();
        BrightnessController.getInstance(this.mContext).animateIcmBrightness(brightness);
    }

    public boolean isMeditationMode() {
        return getInt(this.mContext, "key_system_meditation_mode", 0) == 1;
    }

    private void onMeditationChanged() {
        int value = getInt(this.mContext, "key_system_meditation_mode", 0);
        Logger.i(TAG, "onMeditationChanged value=" + value);
        handleMeditationMode(value == 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSettingsChanged(boolean selfChange, Uri uri) {
        Logger.i(TAG, "onSettingsChanged uri=" + uri);
        if (URI_SYSTEM_MEDITATION_MODE.equals(uri)) {
            onMeditationChanged();
        } else if (URI_SYSTEM_TIME_FORMAT_MODE.equals(uri)) {
            onTimeFormatModeChanged();
        } else if (URI_OOBE_SHOW_STATUS.equals(uri)) {
            onOOBEShowStatusChanged();
        }
    }

    private void onOOBEShowStatusChanged() {
        char c;
        String OOBEShowStatus = getGlobalString(this.mContext, KEY_SHOW_OOBE);
        int hashCode = OOBEShowStatus.hashCode();
        if (hashCode != 3569038) {
            if (hashCode == 97196323 && OOBEShowStatus.equals(OOBEEvent.STRING_FALSE)) {
                c = 1;
            }
            c = 65535;
        } else {
            if (OOBEShowStatus.equals(OOBEEvent.STRING_TRUE)) {
                c = 0;
            }
            c = 65535;
        }
        if (c == 0) {
            CarCheckHelper.enterOOBE();
        } else if (c == 1) {
            CarCheckHelper.exitOOBE();
        }
    }

    private void onTimeFormatModeChanged() {
        for (OnTimeFormatChangedListener listener : this.mOnTimeFormatChangedListenerList) {
            listener.onTimeFormatChanged();
        }
    }

    private static String getGlobalString(Context context, String key) {
        ContentResolver resolver = context.getContentResolver();
        return Settings.Global.getString(resolver, key);
    }

    private static int getInt(Context context, String key, int defaultValue) {
        ContentResolver resolver = context.getContentResolver();
        return Settings.Secure.getInt(resolver, key, defaultValue);
    }

    private static boolean putInt(Context context, String key, int value) {
        ContentResolver resolver = context.getContentResolver();
        return Settings.Secure.putInt(resolver, key, value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public class SettingsObserver {
        private Context mSettingsContext;
        private final ContentObserver mSettingsObserver;

        public SettingsObserver(Context context, Handler handler) {
            this.mSettingsContext = context;
            this.mSettingsObserver = new ContentObserver(handler) { // from class: com.xiaopeng.systemui.controller.SystemController.SettingsObserver.1
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
                    if (selfChange) {
                        return;
                    }
                    SystemController.this.onSettingsChanged(selfChange, uri);
                }
            };
        }

        public void registerThemeObserver() {
            this.mSettingsContext.getContentResolver().registerContentObserver(SystemController.URI_SYSTEM_MEDITATION_MODE, true, this.mSettingsObserver);
        }

        public void unregisterThemeObserver() {
            this.mSettingsContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        }

        public void registerTimeFormatObserver() {
            this.mSettingsContext.getContentResolver().registerContentObserver(SystemController.URI_SYSTEM_TIME_FORMAT_MODE, true, this.mSettingsObserver);
        }

        public void registerOOBEShowObserver() {
            this.mSettingsContext.getContentResolver().registerContentObserver(SystemController.URI_OOBE_SHOW_STATUS, true, this.mSettingsObserver);
        }
    }
}
