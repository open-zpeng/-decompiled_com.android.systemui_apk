package com.xiaopeng.systemui.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.Logger;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class ThemeController {
    private static final String KEY_DAYNIGHT_MODE = "ui_night_mode";
    private static final String KEY_THEME_STATE = "key_theme_type";
    private static final String KEY_THEME_STYLE = "key_theme_style";
    private static final String PERSIST_THEME_CACHE = "persist.sys.theme.cache";
    private static final String PERSIST_THEME_ID = "persist.sys.theme.id";
    private static final String PERSIST_THEME_LOGGER = "persist.sys.theme.logger";
    private static final String PERSIST_THEME_STATE = "persist.sys.theme.state";
    private static final String PERSIST_THEME_STYLE = "persist.sys.theme.style";
    private static final int STATE_THEME_CHANGED = 2;
    private static final int STATE_THEME_PREPARE = 1;
    private static final int STATE_THEME_UNKNOWN = 0;
    private static final String TAG = "ThemeController";
    private static final int UI_MODE_AUTO = 0;
    private static final int UI_MODE_DAY = 1;
    private static final int UI_MODE_NIGHT = 2;
    private static final int UI_MODE_THEME_CLEAR = 63;
    private static final int UI_MODE_THEME_MASK = 192;
    private static final int UI_MODE_THEME_UNDEFINED = 0;
    private Context mContext;
    private final Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.controller.ThemeController.1
    };
    private ArrayList<OnThemeListener> mThemeListeners = new ArrayList<>();
    private final ThemeObserver mThemeObserver;
    public static final Uri URI_DAYNIGHT_MODE = Settings.Secure.getUriFor("ui_night_mode");
    public static final Uri URI_THEME_STATE = Settings.Secure.getUriFor("key_theme_type");
    private static final String KEY_THEME_MODE = "key_theme_mode";
    public static final Uri URI_THEME_MODE = Settings.Secure.getUriFor(KEY_THEME_MODE);
    private static ThemeController sThemeController = null;

    /* loaded from: classes24.dex */
    public interface OnThemeListener {
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
    }

    private void handleThemeChanged(boolean selfChange, Uri uri) {
    }

    public void registerThemeListener(OnThemeListener listener) {
        this.mThemeListeners.add(listener);
    }

    public void unregisterThemeListener(OnThemeListener listener) {
        if (this.mThemeListeners.contains(listener)) {
            this.mThemeListeners.remove(listener);
        }
    }

    public static boolean isDayMode(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration != null && (configuration.uiMode & 48) == 16;
    }

    public static boolean isNightMode(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration != null && (configuration.uiMode & 48) == 32;
    }

    public static boolean isThemeChanged(Configuration newConfig) {
        return ThemeManager.isThemeChanged(newConfig);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onThemeChanged(boolean selfChange, Uri uri) {
        Logger.d(TAG, "onThemeChanged uri=" + uri);
        handleThemeChanged(selfChange, uri);
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

    /* loaded from: classes24.dex */
    private class ThemeObserver {
        private Context mThemeContext;
        private final ContentObserver mThemeObserver;

        public ThemeObserver(Context context, Handler handler) {
            this.mThemeContext = context;
            this.mThemeObserver = new ContentObserver(handler) { // from class: com.xiaopeng.systemui.controller.ThemeController.ThemeObserver.1
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
                    if (selfChange) {
                        return;
                    }
                    ThemeController.this.onThemeChanged(selfChange, uri);
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
