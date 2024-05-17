package com.xiaopeng.libtheme;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.xiaopeng.libtheme.ThemeManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes23.dex */
public class ThemeViewModel {
    private HashMap<String, Integer> mAttributeSet;
    private OnCallback mCallback;
    private List<OnCallback> mCallbacks = new ArrayList();
    private int mUiMode;

    /* loaded from: classes23.dex */
    public interface OnCallback {
        void onThemeChanged();
    }

    private ThemeViewModel(Context context, HashMap<String, Integer> attributes) {
        this.mUiMode = context.getResources().getConfiguration().uiMode;
        this.mAttributeSet = attributes != null ? attributes : new HashMap<>();
    }

    public static ThemeViewModel create(Context context) {
        HashMap<String, Integer> map = new HashMap<>();
        return new ThemeViewModel(context, map);
    }

    public static ThemeViewModel create(Context context, AttributeSet attrs) {
        HashMap<String, Integer> map = ThemeManager.resolveAttribute(context, attrs, 0, 0, null);
        return new ThemeViewModel(context, map);
    }

    public static ThemeViewModel create(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        HashMap<String, Integer> map = ThemeManager.resolveAttribute(context, attrs, defStyleAttr, defStyleRes, null);
        return new ThemeViewModel(context, map);
    }

    public static ThemeViewModel create(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, Object extras) {
        HashMap<String, Integer> map = ThemeManager.resolveAttribute(context, attrs, defStyleAttr, defStyleRes, getExtra(extras));
        return new ThemeViewModel(context, map);
    }

    public static List<String> asList(String... value) {
        if (value != null) {
            return Arrays.asList(value);
        }
        return null;
    }

    public static HashMap<String, List<String>> asMaps(String key, String... value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            HashMap<String, List<String>> map = new HashMap<>();
            map.put(key, Arrays.asList(value));
            return map;
        }
        return null;
    }

    public static HashMap<String, List<String>> asMaps(String key, List<String> list) {
        if (!TextUtils.isEmpty(key) && list != null && !list.isEmpty()) {
            HashMap<String, List<String>> map = new HashMap<>();
            map.put(key, list);
            return map;
        }
        return null;
    }

    public static HashMap<String, List<String>> getExtra(Object extras) {
        if (extras != null) {
            try {
                if (extras instanceof List) {
                    return asMaps(ThemeManager.KEY_APPEND, (List) extras);
                }
                if (extras instanceof HashMap) {
                    return (HashMap) extras;
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void setCallback(OnCallback callback) {
        this.mCallback = callback;
    }

    public void addCallback(OnCallback callback) {
        List<OnCallback> list;
        if (callback != null && (list = this.mCallbacks) != null) {
            list.add(callback);
        }
    }

    public void removeCallback(OnCallback callback) {
        List<OnCallback> list;
        if (callback != null && (list = this.mCallbacks) != null && list.contains(callback)) {
            this.mCallbacks.remove(callback);
        }
    }

    public void onAttachedToWindow(View view) {
        Context context = view.getContext();
        int uiMode = ThemeManager.getUiMode(context);
        ThemeManager.Logger.log("ThemeViewModel onAttachedToWindow newMode=" + uiMode + " oldMode=" + this.mUiMode + " view=" + getViewInfo(view));
        if (isUiModeChanged(uiMode)) {
            updateThemeResource(view);
        }
        this.mUiMode = uiMode;
    }

    public void onDetachedFromWindow(View view) {
    }

    public void onWindowFocusChanged(View view, boolean hasWindowFocus) {
        if (hasWindowFocus) {
            Context context = view.getContext();
            int uiMode = ThemeManager.getUiMode(context);
            ThemeManager.Logger.log("ThemeViewModel onWindowFocusChanged newMode=" + uiMode + " oldMode=" + this.mUiMode + " view=" + getViewInfo(view));
            if (isUiModeChanged(uiMode)) {
                updateThemeResource(view);
            }
            this.mUiMode = uiMode;
        }
    }

    public void onWindowVisibilityChanged(View view, int visibility) {
        if (visibility == 0) {
            Context context = view.getContext();
            int uiMode = ThemeManager.getUiMode(context);
            ThemeManager.Logger.log("ThemeViewModel onWindowVisibilityChanged newMode=" + uiMode + " oldMode=" + this.mUiMode + " view=" + getViewInfo(view));
            if (isUiModeChanged(uiMode)) {
                updateThemeResource(view);
            }
            this.mUiMode = uiMode;
        }
    }

    public void onConfigurationChanged(View view, Configuration newConfig) {
        int uiMode = newConfig != null ? newConfig.uiMode : 0;
        boolean isThemeChanged = ThemeManager.isThemeChanged(newConfig);
        ThemeManager.Logger.log("ThemeViewModel onConfigurationChanged isThemeChanged=" + isThemeChanged + " uiMode=" + uiMode + " view=" + getViewInfo(view));
        if (isThemeChanged) {
            updateThemeResource(view);
            this.mUiMode = newConfig != null ? newConfig.uiMode : this.mUiMode;
        }
    }

    public void setTextColor(int resid) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.TEXT_COLOR, Integer.valueOf(resid));
    }

    public void setBackground(Drawable background) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.BACKGROUND, 0);
    }

    public void setBackgroundColor(int color) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.BACKGROUND, 0);
    }

    public void setBackgroundResource(int resid) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.BACKGROUND, Integer.valueOf(resid));
    }

    public void setBackgroundDrawable(Drawable background) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.BACKGROUND, 0);
    }

    public void setImageBitmap(Bitmap bm) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.SRC, 0);
    }

    public void setImageResource(int resid) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.SRC, Integer.valueOf(resid));
    }

    public void setImageDrawable(Drawable drawable) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.SRC, 0);
    }

    public void setThemeAttribute(String attr, int resid) {
        if (!TextUtils.isEmpty(attr) && this.mAttributeSet.containsKey(attr)) {
            this.mAttributeSet.put(attr, Integer.valueOf(resid));
        }
    }

    private boolean isUiModeChanged(int uiMode) {
        return uiMode != this.mUiMode;
    }

    private void updateThemeResource(View view) {
        ThemeManager.Logger.log("ThemeViewModel updateThemeResource view=" + getViewInfo(view) + " attr=" + getAttrInfo());
        ThemeManager.updateAttribute(view, this.mAttributeSet);
        OnCallback onCallback = this.mCallback;
        if (onCallback != null) {
            onCallback.onThemeChanged();
        }
        List<OnCallback> list = this.mCallbacks;
        if (list != null && list.size() > 0) {
            for (OnCallback callback : this.mCallbacks) {
                if (callback != null) {
                    callback.onThemeChanged();
                }
            }
        }
    }

    private String getAttrInfo() {
        try {
            if (this.mAttributeSet != null && this.mAttributeSet.keySet() != null) {
                return Arrays.toString(this.mAttributeSet.keySet().toArray());
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private String getViewInfo(View view) {
        return view != null ? view.toString() : "view null";
    }
}
