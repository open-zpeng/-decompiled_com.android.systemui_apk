package com.xiaopeng.libtheme;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import com.xiaopeng.libtheme.ThemeManager;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes23.dex */
public class ThemeController {
    private static final String KEY_DAYNIGHT_MODE = "ui_night_mode";
    private static final String KEY_THEME_STATE = "key_theme_type";
    private static final int MSG_STATE_NOTIFY = 1001;
    private static final int MSG_STATE_TIMEOUT = 1002;
    public static final int STATE_THEME_CHANGED = 2;
    public static final int STATE_THEME_PREPARE = 1;
    public static final int STATE_THEME_UNKNOWN = 0;
    private static final String TAG = "ThemeController";
    private static final int VIEW_DISABLE = 0;
    private static final int VIEW_ENABLED = 1;
    private static final int VIEW_UNKNOWN = -1;
    private Context mContext;
    private final ThemeObserver mThemeObserver;
    private static final String KEY_THEME_MODE = "key_theme_mode";
    public static final Uri URI_THEME_MODE = Settings.Secure.getUriFor(KEY_THEME_MODE);
    public static final Uri URI_THEME_STATE = Settings.Secure.getUriFor("key_theme_type");
    public static final Uri URI_DAYNIGHT_MODE = Settings.Secure.getUriFor("ui_night_mode");
    public static final long DEFAULT_STATE_DELAY = SystemProperties.getLong("persist.sys.theme.settings.delay", 3000);
    public static final long DEFAULT_STATE_TIMEOUT = SystemProperties.getLong("persist.sys.theme.settings.timeout", 3000);
    private static ThemeController sThemeController = null;
    private int mThemeMode = 0;
    private int mThemeState = 0;
    private int mDaynightMode = 0;
    private int mViewState = -1;
    private final Handler mHandler = new Handler() { // from class: com.xiaopeng.libtheme.ThemeController.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (i == 1001) {
                ThemeController.this.onStateChanged(msg.arg1 == 1);
            } else if (i == 1002) {
                ThemeController.this.handleStateEvent();
            }
        }
    };
    private ArrayList<OnThemeListener> mThemeListeners = new ArrayList<>();

    /* loaded from: classes23.dex */
    public interface OnThemeListener {
        void onStateChanged(boolean z);

        void onThemeChanged(boolean z, Uri uri);
    }

    public static ThemeController getInstance(Context context) {
        if (sThemeController == null) {
            synchronized (ThemeController.class) {
                if (sThemeController == null) {
                    sThemeController = new ThemeController(context);
                }
            }
        }
        return sThemeController;
    }

    private ThemeController(Context context) {
        this.mContext = context;
        this.mThemeObserver = new ThemeObserver(context, this.mHandler);
        this.mThemeObserver.registerThemeObserver();
        readThemeSettings();
    }

    public void register(OnThemeListener listener) {
        ThemeManager.Logger.log(TAG, "register listener=" + listener);
        this.mThemeListeners.add(listener);
    }

    public void unregister(OnThemeListener listener) {
        ThemeManager.Logger.log(TAG, "unregister listener=" + listener);
        if (this.mThemeListeners.contains(listener)) {
            this.mThemeListeners.remove(listener);
        }
    }

    public boolean isThemeWorking() {
        boolean isThemeWorking = ThemeManager.isThemeWorking(this.mContext);
        return isThemeWorking || this.mThemeState != 0;
    }

    private void setViewState(boolean disable) {
        this.mViewState = !disable ? 1 : 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStateEvent() {
        boolean auto = this.mDaynightMode == 0;
        boolean isThemeWorking = isThemeWorking();
        long delay = isThemeWorking ? 0L : auto ? DEFAULT_STATE_DELAY : 1000L;
        this.mHandler.removeMessages(1001);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1001, isThemeWorking ? 1 : 0, 0), delay);
        this.mHandler.removeMessages(1002);
        if (isThemeWorking) {
            this.mHandler.sendEmptyMessageDelayed(1002, DEFAULT_STATE_TIMEOUT);
        }
        StringBuffer buffer = new StringBuffer("");
        buffer.append("handleStateChanged");
        buffer.append(" auto=" + auto);
        buffer.append(" isThemeWorking=" + isThemeWorking);
        buffer.append(" disable=" + isThemeWorking);
        buffer.append(" delay=" + delay);
        buffer.append(" themeState=" + this.mThemeState);
        buffer.append(" daynightMode=" + this.mDaynightMode);
        ThemeManager.Logger.log(TAG, buffer.toString());
    }

    private void resetViewStateIfNeed(boolean selfChange, Uri uri) {
        ThemeManager.Logger.log(TAG, "resetViewStateIfNeed uri=" + uri);
        Uri URI = Settings.Secure.getUriFor("ui_night_mode");
        if (URI.equals(uri)) {
            this.mViewState = -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStateChanged(boolean disableView) {
        ThemeManager.Logger.log(TAG, "onStateChanged disableView=" + disableView);
        int state = disableView ^ 1;
        if (state == this.mViewState) {
            ThemeManager.Logger.log(TAG, "onStateChanged same state not to listeners");
            return;
        }
        setViewState(disableView);
        ArrayList<OnThemeListener> arrayList = this.mThemeListeners;
        if (arrayList != null) {
            Iterator<OnThemeListener> it = arrayList.iterator();
            while (it.hasNext()) {
                OnThemeListener listener = it.next();
                if (listener != null) {
                    listener.onStateChanged(disableView);
                }
            }
        }
    }

    private void onThemeChanged(boolean selfChange, Uri uri) {
        ArrayList<OnThemeListener> arrayList = this.mThemeListeners;
        if (arrayList != null) {
            Iterator<OnThemeListener> it = arrayList.iterator();
            while (it.hasNext()) {
                OnThemeListener listener = it.next();
                if (listener != null) {
                    listener.onThemeChanged(selfChange, uri);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void performStateChanged(boolean selfChange, Uri uri) {
        resetViewStateIfNeed(selfChange, uri);
        handleStateEvent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void performThemeChanged(boolean selfChange, Uri uri) {
        onThemeChanged(selfChange, uri);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readThemeSettings() {
        ContentResolver resolver = this.mContext.getContentResolver();
        this.mThemeMode = Settings.Secure.getInt(resolver, KEY_THEME_MODE, 0);
        this.mThemeState = Settings.Secure.getInt(resolver, "key_theme_type", 0);
        this.mDaynightMode = Settings.Secure.getInt(resolver, "ui_night_mode", 0);
        ThemeManager.Logger.log(TAG, "readThemeSettings themeState=" + this.mThemeState + " daynightMode=" + this.mDaynightMode);
    }

    /* loaded from: classes23.dex */
    private class ThemeObserver {
        private Context mThemeContext;
        private final ContentObserver mThemeObserver;

        public ThemeObserver(Context context, Handler handler) {
            this.mThemeContext = context;
            this.mThemeObserver = new ContentObserver(handler) { // from class: com.xiaopeng.libtheme.ThemeController.ThemeObserver.1
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
                    if (selfChange) {
                        return;
                    }
                    ThemeController.this.readThemeSettings();
                    ThemeController.this.performStateChanged(selfChange, uri);
                    ThemeController.this.performThemeChanged(selfChange, uri);
                }
            };
        }

        public void registerThemeObserver() {
            this.mThemeContext.getContentResolver().registerContentObserver(ThemeController.URI_THEME_MODE, true, this.mThemeObserver);
            this.mThemeContext.getContentResolver().registerContentObserver(ThemeController.URI_THEME_STATE, true, this.mThemeObserver);
            this.mThemeContext.getContentResolver().registerContentObserver(ThemeController.URI_DAYNIGHT_MODE, true, this.mThemeObserver);
        }

        public void unregisterThemeObserver() {
            this.mThemeContext.getContentResolver().unregisterContentObserver(this.mThemeObserver);
        }
    }
}
