package com.android.systemui.statusbar.phone;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes21.dex */
public class NavigationPrototypeController extends ContentObserver implements ComponentCallbacks {
    static final int ACTION_ASSISTANT = 6;
    static final int ACTION_BACK = 3;
    static final int ACTION_DEFAULT = 0;
    static final int ACTION_NOTHING = 5;
    static final int ACTION_QUICKSCRUB = 2;
    static final int ACTION_QUICKSTEP = 1;
    static final int ACTION_QUICKSWITCH = 4;
    public static final String EDGE_SENSITIVITY_WIDTH_SETTING = "quickstepcontroller_edge_width_sensitivity";
    public static final String ENABLE_ASSISTANT_GESTURE = "ENABLE_ASSISTANT_GESTURE";
    private static final String HIDE_BACK_BUTTON_SETTING = "quickstepcontroller_hideback";
    private static final String HIDE_HOME_BUTTON_SETTING = "quickstepcontroller_hidehome";
    public static final String NAV_COLOR_ADAPT_ENABLE_SETTING = "navbar_color_adapt_enable";
    private static final String PROTOTYPE_ENABLED = "prototype_enabled";
    public static final String SHOW_HOME_HANDLE_SETTING = "quickstepcontroller_showhandle";
    private final String GESTURE_MATCH_SETTING;
    private int[] mActionMap;
    private final Context mContext;
    private OnPrototypeChangedListener mListener;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    @interface GestureAction {
    }

    /* loaded from: classes21.dex */
    public interface OnPrototypeChangedListener {
        void onAssistantGestureEnabled(boolean z);

        void onBackButtonVisibilityChanged(boolean z);

        void onColorAdaptChanged(boolean z);

        void onEdgeSensitivityChanged(int i, int i2);

        void onGestureRemap(int[] iArr);

        void onHomeButtonVisibilityChanged(boolean z);

        void onHomeHandleVisiblilityChanged(boolean z);
    }

    public NavigationPrototypeController(Context context) {
        super(new Handler());
        this.GESTURE_MATCH_SETTING = "quickstepcontroller_gesture_match_map";
        this.mActionMap = new int[6];
        this.mContext = context;
        updateSwipeLTRBackSetting();
    }

    public void setOnPrototypeChangedListener(OnPrototypeChangedListener listener) {
        this.mListener = listener;
    }

    public void register() {
        registerObserver(HIDE_BACK_BUTTON_SETTING);
        registerObserver(HIDE_HOME_BUTTON_SETTING);
        registerObserver("quickstepcontroller_gesture_match_map");
        registerObserver(NAV_COLOR_ADAPT_ENABLE_SETTING);
        registerObserver(SHOW_HOME_HANDLE_SETTING);
        registerObserver(ENABLE_ASSISTANT_GESTURE);
        this.mContext.registerComponentCallbacks(this);
    }

    public void unregister() {
        this.mContext.getContentResolver().unregisterContentObserver(this);
        this.mContext.unregisterComponentCallbacks(this);
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        if (!selfChange && this.mListener != null) {
            String path = uri.getPath();
            if (path.endsWith("quickstepcontroller_gesture_match_map")) {
                updateSwipeLTRBackSetting();
                this.mListener.onGestureRemap(this.mActionMap);
            } else if (path.endsWith(HIDE_BACK_BUTTON_SETTING)) {
                this.mListener.onBackButtonVisibilityChanged(!getGlobalBool(HIDE_BACK_BUTTON_SETTING, false));
            } else if (path.endsWith(HIDE_HOME_BUTTON_SETTING)) {
                this.mListener.onHomeButtonVisibilityChanged(!hideHomeButton());
            } else if (path.endsWith(NAV_COLOR_ADAPT_ENABLE_SETTING)) {
                this.mListener.onColorAdaptChanged(NavBarTintController.isEnabled(this.mContext, 2));
            } else if (path.endsWith(SHOW_HOME_HANDLE_SETTING)) {
                this.mListener.onHomeHandleVisiblilityChanged(showHomeHandle());
            } else if (path.endsWith(ENABLE_ASSISTANT_GESTURE)) {
                this.mListener.onAssistantGestureEnabled(isAssistantGestureEnabled());
            }
        }
    }

    public int getEdgeSensitivityWidth() {
        return this.mContext.getResources().getDimensionPixelSize(17105051);
    }

    public int getEdgeSensitivityHeight() {
        Point size = new Point();
        this.mContext.getDisplay().getRealSize(size);
        return size.y;
    }

    public boolean isEnabled() {
        return getGlobalBool(PROTOTYPE_ENABLED, false);
    }

    int[] getGestureActionMap() {
        return this.mActionMap;
    }

    boolean hideHomeButton() {
        return getGlobalBool(HIDE_HOME_BUTTON_SETTING, false);
    }

    boolean showHomeHandle() {
        return getGlobalBool(SHOW_HOME_HANDLE_SETTING, false);
    }

    boolean isAssistantGestureEnabled() {
        return getGlobalBool(ENABLE_ASSISTANT_GESTURE, false);
    }

    private void updateSwipeLTRBackSetting() {
        String value = Settings.Global.getString(this.mContext.getContentResolver(), "quickstepcontroller_gesture_match_map");
        if (value != null) {
            int i = 0;
            while (true) {
                int[] iArr = this.mActionMap;
                if (i < iArr.length) {
                    iArr[i] = Character.getNumericValue(value.charAt(i));
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private boolean getGlobalBool(String name, boolean defaultVal) {
        return Settings.Global.getInt(this.mContext.getContentResolver(), name, defaultVal ? 1 : 0) == 1;
    }

    private int getGlobalInt(String name, int defaultVal) {
        return Settings.Global.getInt(this.mContext.getContentResolver(), name, defaultVal);
    }

    private void registerObserver(String name) {
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(name), false, this);
    }

    private static int convertDpToPixel(float dp) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp);
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        OnPrototypeChangedListener onPrototypeChangedListener = this.mListener;
        if (onPrototypeChangedListener != null) {
            onPrototypeChangedListener.onEdgeSensitivityChanged(getEdgeSensitivityWidth(), getEdgeSensitivityHeight());
        }
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
    }
}
