package com.xiaopeng.libtheme;

import android.app.IUiModeManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes23.dex */
public class ThemeManager {
    public static final boolean DEBUG;
    public static final String KEY_APPEND = "extra_append";
    public static final String KEY_GLOBAL = "extra_global";
    public static final int MODE_NIGHT_AUTO = 0;
    public static final int MODE_NIGHT_NO = 1;
    public static final int MODE_NIGHT_YES = 2;
    private static final String TAG = "ThemeManager";
    public static final boolean THEME;
    public static final long THEME_ANIMATION_INTERVAL;
    public static final long THEME_TIMEOUT_DELAY;
    public static final int UI_MODE_NIGHT_MASK = 48;
    public static final int UI_MODE_NIGHT_NO = 16;
    public static final int UI_MODE_NIGHT_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_YES = 32;
    public static final int UI_MODE_THEME_CLEAR = 63;
    public static final int UI_MODE_THEME_MASK = 192;
    public static final int UI_MODE_THEME_UNDEFINED = 0;
    public static final int VERSION = 3;
    private Context mContext;
    private ThemeData mThemeData = new ThemeData();

    static {
        THEME = SystemProperties.getInt("persist.sys.theme.logger", 0) == 1;
        DEBUG = THEME;
        THEME_ANIMATION_INTERVAL = UiModeManager.THEME_ANIMATION_INTERVAL;
        THEME_TIMEOUT_DELAY = UiModeManager.THEME_TIMEOUT_DELAY;
    }

    private ThemeManager(Context context, View root, String xml, List<ThemeView> list) {
        this.mContext = context.getApplicationContext();
        ThemeData themeData = this.mThemeData;
        themeData.xml = xml;
        themeData.root = root;
        themeData.list = list;
    }

    public static ThemeManager create(Context context, View root, String xml, List<ThemeView> list) {
        return new ThemeManager(context, root, xml, list);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        ThemeWrapper.getInstance().onConfigurationChanged(this.mContext, this.mThemeData, newConfig);
    }

    public static boolean isThemeChanged(Configuration newConfig) {
        return (newConfig == null || (newConfig.uiMode & 192) == 0) ? false : true;
    }

    public static int getThemeMode(Context context) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        return uim.getThemeMode();
    }

    public static int getDayNightMode(Context context) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        return uim.getDayNightMode();
    }

    public static int getDayNightAutoMode(Context context) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        return uim.getDayNightAutoMode();
    }

    public static int getUiMode(Context context) {
        Context ctx = context != null ? context.getApplicationContext() : null;
        Configuration configuration = ctx != null ? ctx.getResources().getConfiguration() : null;
        if (configuration != null) {
            return configuration.uiMode;
        }
        return 0;
    }

    public static boolean isNightMode(Context context) {
        Context ctx = context != null ? context.getApplicationContext() : null;
        Configuration configuration = ctx != null ? ctx.getResources().getConfiguration() : null;
        return configuration != null && (configuration.uiMode & 48) == 32;
    }

    public static boolean isThemeWorking(Context context) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        return uim.isThemeWorking();
    }

    public static void applyThemeMode(Context context, int themeMode) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        uim.applyThemeMode(themeMode);
    }

    public static void applyDayNightMode(Context context, int daynightMode) {
        UiModeManager uim = (UiModeManager) context.getSystemService("uimode");
        uim.applyDayNightMode(daynightMode);
    }

    public static IUiModeManager getUiModeManager() {
        IBinder b = ServiceManager.getService("uimode");
        return IUiModeManager.Stub.asInterface(b);
    }

    public static void setWindowBackgroundResource(Configuration newConfig, Window window, int resId) {
        if (isThemeChanged(newConfig) && window != null) {
            try {
                window.setBackgroundDrawableResource(resId);
            } catch (Exception e) {
            }
        }
    }

    public static void setWindowBackgroundDrawable(Configuration newConfig, Window window, Drawable drawable) {
        if (isThemeChanged(newConfig) && window != null) {
            try {
                window.setBackgroundDrawable(drawable);
            } catch (Exception e) {
            }
        }
    }

    public static HashMap<String, Integer> resolveAttribute(Context context, android.util.AttributeSet attrs) {
        return ThemeWrapper.resolveAttribute(context, attrs, 0, 0, null);
    }

    public static HashMap<String, Integer> resolveAttribute(Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes, HashMap<String, List<String>> extras) {
        return ThemeWrapper.resolveAttribute(context, attrs, defStyleAttr, defStyleRes, extras);
    }

    public static void setAttributeValue(HashMap<String, Integer> map, String attr, int resId) {
        ThemeWrapper.setAttributeValue(map, attr, resId);
    }

    public static void updateAttribute(View view, HashMap<String, Integer> map) {
        ThemeWrapper.updateAttribute(view, map);
    }

    /* loaded from: classes23.dex */
    public static class Logger {
        public static void log(String msg) {
            if (ThemeManager.DEBUG) {
                Log.i(ThemeManager.TAG, msg);
            }
        }

        public static void log(String tag, String msg) {
            Log.i(tag, msg);
        }
    }

    /* loaded from: classes23.dex */
    public static class ViewBuilder {
        private List<ThemeView> list = new ArrayList();

        private ViewBuilder() {
            this.list.clear();
        }

        public static ViewBuilder create() {
            return new ViewBuilder();
        }

        public ViewBuilder add(int resId, String resAttr, int resValue) {
            return add(resId, resAttr, -1, resValue);
        }

        public ViewBuilder add(int resId, String resAttr, int resRoot, int resValue) {
            if (resId > 0) {
                try {
                    if (!TextUtils.isEmpty(resAttr) && resValue >= 0) {
                        ThemeView view = new ThemeView();
                        view.resId = resId;
                        view.resAttr = resAttr;
                        view.resRoot = resRoot;
                        view.resValue = Integer.valueOf(resValue);
                        this.list.add(view);
                    }
                } catch (Exception e) {
                }
            }
            return this;
        }

        public List<ThemeView> get() {
            return this.list;
        }
    }

    /* loaded from: classes23.dex */
    public static class ResourceType {
        public static final int ANIM = 0;
        public static final int ARRAY = 1;
        public static final int ATTR = 2;
        public static final int BOOL = 3;
        public static final int COLOR = 4;
        public static final int DIMEN = 5;
        public static final int DRAWABLE = 6;
        public static final int ID = 7;
        public static final int INTEGER = 8;
        public static final int LAYOUT = 9;
        public static final int MIPMAP = 10;
        public static final int STRING = 11;
        public static final int STYLE = 12;
        public static final int STYLEABLE = 13;
        public static final HashMap<String, Integer> sResourceType = new HashMap<>();

        static {
            sResourceType.clear();
            sResourceType.put("anim", 0);
            sResourceType.put("array", 1);
            sResourceType.put("attr", 2);
            sResourceType.put("bool", 3);
            sResourceType.put("color", 4);
            sResourceType.put("dimen", 5);
            sResourceType.put("drawable", 6);
            sResourceType.put("id", 7);
            sResourceType.put("integer", 8);
            sResourceType.put("layout", 9);
            sResourceType.put("mipmap", 10);
            sResourceType.put("string", 11);
            sResourceType.put(AttributeSet.STYLE, 12);
            sResourceType.put("styleable", 13);
        }

        public static int getType(String type) {
            if (TextUtils.isEmpty(type)) {
                return -1;
            }
            int ret = sResourceType.get(type).intValue();
            return ret;
        }
    }

    /* loaded from: classes23.dex */
    public static class AttributeSet {
        public static final String ALPHA = "alpha";
        public static final String BACKGROUND = "background";
        public static final String BUTTON = "button";
        public static final String DIVIDER = "divider";
        public static final String DRAWABLE_BOTTOM = "drawableBottom";
        public static final String DRAWABLE_END = "drawableEnd";
        public static final String DRAWABLE_LEFT = "drawableLeft";
        public static final String DRAWABLE_RIGHT = "drawableRight";
        public static final String DRAWABLE_START = "drawableStart";
        public static final String DRAWABLE_TOP = "drawableTop";
        public static final String FOREGROUND = "foreground";
        public static final String PROGRESS_DRAWABLE = "progressDrawable";
        public static final String SCROLLBAR_THUMB_VERTICAL = "scrollbarThumbVertical";
        public static final String SRC = "src";
        public static final String STYLE = "style";
        public static final String TEXT_COLOR = "textColor";
        public static final String TEXT_COLOR_HINT = "textColorHint";
        public static final String THEME = "theme";
        public static final String THUMB = "thumb";
        public static final HashMap<String, Integer> sAttributeMap = new HashMap<>();

        static {
            sAttributeMap.clear();
            sAttributeMap.put(STYLE, 0);
            sAttributeMap.put(THEME, 16842752);
            sAttributeMap.put(ALPHA, 16843551);
            sAttributeMap.put(FOREGROUND, 16843017);
            sAttributeMap.put(BACKGROUND, 16842964);
            sAttributeMap.put(SCROLLBAR_THUMB_VERTICAL, 16842853);
            sAttributeMap.put(SRC, 16843033);
            sAttributeMap.put(TEXT_COLOR, 16842904);
            sAttributeMap.put(TEXT_COLOR_HINT, 16842906);
            sAttributeMap.put(DRAWABLE_LEFT, 16843119);
            sAttributeMap.put(DRAWABLE_TOP, 16843117);
            sAttributeMap.put(DRAWABLE_RIGHT, 16843120);
            sAttributeMap.put(DRAWABLE_BOTTOM, 16843118);
            sAttributeMap.put(DRAWABLE_START, 16843666);
            sAttributeMap.put(DRAWABLE_END, 16843667);
            sAttributeMap.put(PROGRESS_DRAWABLE, 16843068);
            sAttributeMap.put(THUMB, 16843074);
            sAttributeMap.put(BUTTON, 16843015);
            sAttributeMap.put(DIVIDER, 16843049);
        }

        public static boolean hasAttribute(String attr) {
            if (!TextUtils.isEmpty(attr)) {
                return sAttributeMap.containsKey(attr);
            }
            return false;
        }

        public static boolean isThemeAttribute(String attr) {
            if (!TextUtils.isEmpty(attr)) {
                return THEME.equals(attr);
            }
            return false;
        }

        public static boolean isStyleAttribute(String attr) {
            if (!TextUtils.isEmpty(attr)) {
                return STYLE.equals(attr);
            }
            return false;
        }

        public static boolean supportTransition(String attr) {
            if (TextUtils.isEmpty(attr)) {
                return false;
            }
            return BACKGROUND.equals(attr) || SRC.equals(attr) || TEXT_COLOR.equals(attr);
        }
    }
}
